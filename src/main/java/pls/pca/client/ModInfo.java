package pls.pca.client;

import net.minecraft.resources.Identifier;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public final class ModInfo
{
	public static final String MOD_ID = "pca-client";
	public static final String PROTOCOL_NAMESPACE = "pca";
	public static final Logger LOGGER = LogUtils.getLogger();

	private ModInfo()
	{
	}

	public static Identifier id(String path)
	{
		return Identifier.fromNamespaceAndPath(PROTOCOL_NAMESPACE, path);
	}
}
