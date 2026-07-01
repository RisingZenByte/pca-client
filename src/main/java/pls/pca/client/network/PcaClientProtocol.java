package pls.pca.client.network;

import io.netty.buffer.Unpooled;
import me.fallenbreath.fanetlib.api.event.FanetlibClientEvents;
import me.fallenbreath.fanetlib.api.packet.FanetlibPackets;
import me.fallenbreath.fanetlib.api.packet.PacketCodec;
import me.fallenbreath.fanetlib.api.packet.PacketHandlerC2S;
import me.fallenbreath.fanetlib.api.packet.PacketHandlerS2C;
import me.fallenbreath.fanetlib.api.packet.PacketId;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import pls.pca.client.ModInfo;
import pls.pca.client.sync.PcaDataSyncer;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public final class PcaClientProtocol
{
	public static volatile boolean enabled = false;

	private static final Identifier ENABLE = id("enable_pca_sync_protocol");
	private static final Identifier DISABLE = id("disable_pca_sync_protocol");
	private static final Identifier UPDATE_ENTITY = id("update_entity");
	private static final Identifier UPDATE_BLOCK_ENTITY = id("update_block_entity");
	public static final Identifier SYNC_BLOCK_ENTITY = id("sync_block_entity");
	public static final Identifier SYNC_ENTITY = id("sync_entity");
	public static final Identifier CANCEL_SYNC_BLOCK_ENTITY = id("cancel_sync_block_entity");
	public static final Identifier CANCEL_SYNC_ENTITY = id("cancel_sync_entity");

	private static final AtomicBoolean registered = new AtomicBoolean();
	private static @Nullable BlockPos lastBlockPos = null;
	private static int lastEntityId = -1;

	private PcaClientProtocol()
	{
	}

	public static void registerPackets()
	{
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT)
		{
			return;
		}
		if (!registered.compareAndSet(false, true))
		{
			return;
		}

		PacketCodec<FriendlyByteBuf> codec = PacketCodec.of(
				(p, buf) -> buf.writeBytes(p.copy()),
				buf -> {
					FriendlyByteBuf copy = new FriendlyByteBuf(Unpooled.buffer());
					copy.writeBytes(buf);
					return copy;
				}
		);

		FanetlibPackets.registerC2S(PacketId.of(SYNC_BLOCK_ENTITY), codec, PacketHandlerC2S.dummy());
		FanetlibPackets.registerC2S(PacketId.of(SYNC_ENTITY), codec, PacketHandlerC2S.dummy());
		FanetlibPackets.registerC2S(PacketId.of(CANCEL_SYNC_BLOCK_ENTITY), codec, PacketHandlerC2S.dummy());
		FanetlibPackets.registerC2S(PacketId.of(CANCEL_SYNC_ENTITY), codec, PacketHandlerC2S.dummy());

		FanetlibPackets.registerS2C(PacketId.of(ENABLE), codec, PcaClientProtocol::handleEnable);
		FanetlibPackets.registerS2C(PacketId.of(DISABLE), codec, PcaClientProtocol::handleDisable);
		FanetlibPackets.registerS2C(PacketId.of(UPDATE_ENTITY), codec, PcaClientProtocol::handleUpdateEntity);
		FanetlibPackets.registerS2C(PacketId.of(UPDATE_BLOCK_ENTITY), codec, PcaClientProtocol::handleUpdateBlockEntity);
	}

	public static void initClientEvents()
	{
		FanetlibClientEvents.registerDisconnectListener(client -> {
			ModInfo.LOGGER.debug("PCA client disconnect");
			if (enabled)
			{
				enabled = false;
				PcaDataSyncer.onProtocolDisabled();
			}
			lastBlockPos = null;
			lastEntityId = -1;
		});
	}

	private static Identifier id(String path)
	{
		return ModInfo.id(path);
	}

	private static void handleEnable(FriendlyByteBuf buf, PacketHandlerS2C.Context ctx)
	{
		if (!ctx.getClient().hasSingleplayerServer())
		{
			ModInfo.LOGGER.info("PCA sync protocol enabled by server");
			enabled = true;
			PcaDataSyncer.onProtocolEnabled();
		}
	}

	private static void handleDisable(FriendlyByteBuf buf, PacketHandlerS2C.Context ctx)
	{
		if (!ctx.getClient().hasSingleplayerServer())
		{
			ModInfo.LOGGER.info("PCA sync protocol disabled by server");
			enabled = false;
			PcaDataSyncer.onProtocolDisabled();
		}
	}

	private static void handleUpdateBlockEntity(FriendlyByteBuf buf, PacketHandlerS2C.Context ctx)
	{
		Minecraft mc = ctx.getClient();
		Level level = mc.level;

		if (level == null)
		{
			return;
		}

		Identifier dimension = buf.readIdentifier();
		BlockPos pos = buf.readBlockPos();
		CompoundTag tag = buf.readNbt();

		if (tag == null || !level.dimension().identifier().equals(dimension))
		{
			return;
		}

		ModInfo.LOGGER.debug("Applied PCA block entity update at {}", pos);
		PcaDataSyncer.getInstance().handleBlockEntityData(pos, tag);
	}

	private static void handleUpdateEntity(FriendlyByteBuf buf, PacketHandlerS2C.Context ctx)
	{
		Minecraft mc = ctx.getClient();
		Level level = mc.level;

		if (level == null)
		{
			return;
		}

		Identifier dimension = buf.readIdentifier();
		int entityId = buf.readInt();
		CompoundTag tag = buf.readNbt();

		if (tag == null || !level.dimension().identifier().equals(dimension))
		{
			return;
		}

		if (level.getEntity(entityId) == null)
		{
			return;
		}

		ModInfo.LOGGER.debug("Applied PCA entity update for id {}", entityId);
		PcaDataSyncer.getInstance().handleEntityData(entityId, tag);
	}

	public static void syncBlockEntity(BlockPos pos)
	{
		if (lastBlockPos != null && lastBlockPos.equals(pos))
		{
			return;
		}

		lastBlockPos = pos;
		lastEntityId = -1;
		ModInfo.LOGGER.debug("Request PCA sync for block {}", pos);
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeBlockPos(pos);
		sendToServer(SYNC_BLOCK_ENTITY, buf);
	}

	public static void syncEntity(int entityId)
	{
		if (lastEntityId == entityId)
		{
			return;
		}

		lastEntityId = entityId;
		lastBlockPos = null;
		ModInfo.LOGGER.debug("Request PCA sync for entity {}", entityId);
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeInt(entityId);
		sendToServer(SYNC_ENTITY, buf);
	}

	public static void cancelSyncBlockEntity()
	{
		if (lastBlockPos == null)
		{
			return;
		}

		lastBlockPos = null;
		ModInfo.LOGGER.debug("Cancel PCA block entity watch");
		sendToServer(CANCEL_SYNC_BLOCK_ENTITY, new FriendlyByteBuf(Unpooled.buffer()));
	}

	public static void cancelSyncEntity()
	{
		if (lastEntityId == -1)
		{
			return;
		}

		lastEntityId = -1;
		ModInfo.LOGGER.debug("Cancel PCA entity watch");
		sendToServer(CANCEL_SYNC_ENTITY, new FriendlyByteBuf(Unpooled.buffer()));
	}

	private static void sendToServer(Identifier id, FriendlyByteBuf buf)
	{
		Minecraft mc = Minecraft.getInstance();
		if (mc.getConnection() != null)
		{
			mc.getConnection().send(FanetlibPackets.createC2S(PacketId.of(id), buf));
		}
	}
}
