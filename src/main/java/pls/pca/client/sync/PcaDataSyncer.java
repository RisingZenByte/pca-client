package pls.pca.client.sync;

import fi.dy.masa.malilib.interfaces.IDataSyncer;
import fi.dy.masa.malilib.util.data_syncer.EntityDataCache;
import fi.dy.masa.malilib.util.data_syncer.EntityDataRequestTracker;
import fi.dy.masa.tweakeroo.data.EntityDataManager;
import fi.dy.masa.tweakeroo.renderer.InventoryOverlayHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import pls.pca.client.network.PcaClientProtocol;

/**
 * Bridges PCA server push updates into MaLiLib/Tweakeroo's inventory overlay cache,
 * so preview UI refreshes as soon as the server sends container changes.
 */
public final class PcaDataSyncer implements IDataSyncer
{
	private static final PcaDataSyncer INSTANCE = new PcaDataSyncer();

	private static final long CACHE_TIMEOUT_MS = 60_000L;
	/** Fallback poll interval if a push is missed; normal updates come from server push. */
	private static final long REFRESH_TIME_MS = 5_000L;

	private final EntityDataCache cache = new EntityDataCache("pca-client", CACHE_TIMEOUT_MS);
	private final EntityDataRequestTracker requestTracker = new EntityDataRequestTracker();

	private PcaDataSyncer()
	{
	}

	public static PcaDataSyncer getInstance()
	{
		return INSTANCE;
	}

	public static void onProtocolEnabled()
	{
		InventoryOverlayHandler.getInstance().setDataSyncer(INSTANCE);
	}

	public static void onProtocolDisabled()
	{
		PcaClientProtocol.cancelSyncBlockEntity();
		PcaClientProtocol.cancelSyncEntity();
		INSTANCE.clearAll();
		InventoryOverlayHandler.getInstance().setDataSyncer(EntityDataManager.getInstance());
	}

	@Override
	public EntityDataCache getCache()
	{
		return cache;
	}

	@Override
	public EntityDataRequestTracker getRequestTracker()
	{
		return requestTracker;
	}

	@Override
	public boolean isEnabled()
	{
		return PcaClientProtocol.enabled;
	}

	@Override
	public boolean isBackupEnabled()
	{
		return false;
	}

	@Override
	public long getRefreshTime()
	{
		return REFRESH_TIME_MS;
	}

	@Override
	public long getCacheTimeout()
	{
		return CACHE_TIMEOUT_MS;
	}

	@Override
	public boolean loadContainerBlockEntities()
	{
		return true;
	}

	@Override
	public boolean requestBlockEntityFromLocalServer(Minecraft mc, Level world, BlockPos pos)
	{
		if (!isEnabled() || mc.hasSingleplayerServer())
		{
			return false;
		}

		PcaClientProtocol.syncBlockEntity(pos);
		return true;
	}

	@Override
	public boolean requestEntityFromLocalServer(Minecraft mc, Level world, int entityId)
	{
		if (!isEnabled() || mc.hasSingleplayerServer())
		{
			return false;
		}

		PcaClientProtocol.syncEntity(entityId);
		return true;
	}
}
