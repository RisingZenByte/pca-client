package pls.pca.client.preview;

import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.game.RayTraceUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import pls.pca.client.network.PcaClientProtocol;
import org.jetbrains.annotations.Nullable;

public final class InventoryPreviewSync
{
	private static boolean lastPreviewHeld = false;

	private InventoryPreviewSync()
	{
	}

	public static void init()
	{
		if (!FabricLoader.getInstance().isModLoaded("tweakeroo"))
		{
			return;
		}

		ClientTickEvents.END_CLIENT_TICK.register(client -> onClientTick(client));
	}

	private static void onClientTick(Minecraft mc)
	{
		if (!PcaClientProtocol.enabled || mc.hasSingleplayerServer() || mc.level == null || mc.player == null)
		{
			lastPreviewHeld = false;
			return;
		}

		if (!FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue())
		{
			return;
		}

		boolean previewHeld = Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld();
		boolean stateChanged = previewHeld != lastPreviewHeld;

		if (!previewHeld)
		{
			if (lastPreviewHeld)
			{
				PcaClientProtocol.cancelSyncBlockEntity();
				PcaClientProtocol.cancelSyncEntity();
			}
			lastPreviewHeld = false;
			return;
		}

		if (stateChanged)
		{
			PcaClientProtocol.cancelSyncBlockEntity();
			PcaClientProtocol.cancelSyncEntity();
		}

		HitResult hitResult = RayTraceUtils.getRayTraceFromEntity(mc.level, mc.player, ClipContext.Fluid.NONE);

		if (hitResult == null || hitResult.getType() == HitResult.Type.MISS)
		{
			PcaClientProtocol.cancelSyncBlockEntity();
			PcaClientProtocol.cancelSyncEntity();
			lastPreviewHeld = true;
			return;
		}

		if (hitResult.getType() == HitResult.Type.BLOCK)
		{
			BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hitResult).getBlockPos();
			Object container = resolveBlockContainer(mc.level, pos);

			if (shouldSyncBlockContainer(container))
			{
				PcaClientProtocol.syncBlockEntity(pos);
				PcaClientProtocol.cancelSyncEntity();
			}
		}
		else if (hitResult.getType() == HitResult.Type.ENTITY)
		{
			Entity entity = ((EntityHitResult) hitResult).getEntity();

			if (shouldSyncEntity(entity))
			{
				PcaClientProtocol.syncEntity(entity.getId());
				PcaClientProtocol.cancelSyncBlockEntity();
			}
		}

		lastPreviewHeld = true;
	}

	private static boolean shouldSyncBlockContainer(@Nullable Object container)
	{
		return container instanceof AbstractFurnaceBlockEntity
				|| container instanceof DispenserBlockEntity
				|| container instanceof HopperBlockEntity
				|| container instanceof ShulkerBoxBlockEntity
				|| container instanceof BarrelBlockEntity
				|| container instanceof BrewingStandBlockEntity
				|| container instanceof ChestBlockEntity
				|| container instanceof CompoundContainer
				|| container instanceof ComparatorBlockEntity
				|| container instanceof BeehiveBlockEntity;
	}

	private static boolean shouldSyncEntity(Entity entity)
	{
		return entity instanceof Container
				|| entity instanceof AbstractVillager
				|| entity instanceof AbstractHorse
				|| entity instanceof Player;
	}

	private static @Nullable Object resolveBlockContainer(Level level, BlockPos pos)
	{
		Container inventory = InventoryUtils.getInventory(level, pos);
		if (inventory != null)
		{
			return inventory;
		}

		return level.getBlockEntity(pos);
	}
}
