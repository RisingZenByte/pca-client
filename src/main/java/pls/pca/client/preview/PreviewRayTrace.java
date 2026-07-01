package pls.pca.client.preview;

import fi.dy.masa.malilib.util.game.RayTraceUtils;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public final class PreviewRayTrace
{
	private PreviewRayTrace()
	{
	}

	public static HitResult trace(Minecraft mc, Level level)
	{
		Entity camera = getViewEntity(mc);
		if (camera == null)
		{
			return null;
		}

		return RayTraceUtils.getRayTraceFromEntity(level, camera, ClipContext.Fluid.NONE);
	}

	public static @Nullable Entity getViewEntity(Minecraft mc)
	{
		if (mc.player == null)
		{
			return null;
		}

		if (FeatureToggle.TWEAK_FREE_CAMERA.getBooleanValue())
		{
			CameraEntity camera = CameraEntity.getCamera();
			if (camera != null)
			{
				return camera;
			}
		}

		return mc.player;
	}
}
