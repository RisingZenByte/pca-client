package pls.pca.client;

import net.fabricmc.api.ClientModInitializer;
import pls.pca.client.network.PcaClientProtocol;
import pls.pca.client.preview.InventoryPreviewSync;

public class PcaClientMod implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		PcaClientProtocol.initClientEvents();
		InventoryPreviewSync.init();
		ModInfo.LOGGER.info("PCA Client initialized");
	}
}
