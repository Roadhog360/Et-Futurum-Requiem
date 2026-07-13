package ganymedes01.etfuturum.mixins.late.spectator;

import ganymedes01.etfuturum.spectator.SpectatorMode;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xonin.backhand.client.ClientEventHandler;

@Mixin(value = ClientEventHandler.class, remap = false)
public class MixinClientEventHandlerBackhand {
	@Inject(method = "render3rdPersonOffhand", at = @At("HEAD"), cancellable = true)
	private static void skipOffhandItemForSpectator(RenderPlayerEvent.Specials.Post event, CallbackInfo ci) {
		if (SpectatorMode.isSpectatorForRender(event.entityPlayer)) {
			ci.cancel();
		}
	}
}
