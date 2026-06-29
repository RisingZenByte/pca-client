package pls.pca.client.mixin;

import me.fallenbreath.fanetlib.api.packet.FanetlibPacketRegistrationCenter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pls.pca.client.network.PcaClientProtocol;

@Mixin(FanetlibPacketRegistrationCenter.class)
public abstract class FanetlibPacketRegistrationCenterMixin
{
	@Inject(method = "common", at = @At("HEAD"), remap = false)
	private static void registerPcaClientPackets(CallbackInfo ci)
	{
		PcaClientProtocol.registerPackets();
	}
}
