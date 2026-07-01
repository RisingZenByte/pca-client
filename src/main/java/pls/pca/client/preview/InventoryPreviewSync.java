package pls.pca.client.preview;

import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
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

		ClientTickEvents.END_CLIENT_TICK.register(InventoryPreviewSync::onClientTick);
	}

	private static void onClientTick(Minecraft mc)
	{
		if (!PcaClientProtocol.enabled || mc.hasSingleplayerServer() || mc.level == null || PreviewRayTrace.getViewEntity(mc) == null)
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

		HitResult hitResult = PreviewRayTrace.trace(mc, mc.level);

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

			if (PreviewContainerFilters.isBlockContainerSupported(container))
			{
				PcaClientProtocol.syncBlockEntity(pos);
				PcaClientProtocol.cancelSyncEntity();
			}
		}
		else if (hitResult.getType() == HitResult.Type.ENTITY)
		{
			Entity entity = ((EntityHitResult) hitResult).getEntity();

			if (PreviewContainerFilters.isEntitySupported(entity))
			{
				PcaClientProtocol.syncEntity(entity.getId());
				PcaClientProtocol.cancelSyncBlockEntity();
			}
		}

		lastPreviewHeld = true;
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
