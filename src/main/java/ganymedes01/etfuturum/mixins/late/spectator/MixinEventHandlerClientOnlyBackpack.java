package ganymedes01.etfuturum.mixins.late.spectator;

import de.eydamos.backpack.handler.EventHandlerClientOnly;
import ganymedes01.etfuturum.spectator.SpectatorMode;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EventHandlerClientOnly.class, remap = false)
public class MixinEventHandlerClientOnlyBackpack {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void skipBackpackForSpectator(RenderPlayerEvent.Specials.Pre event, CallbackInfo ci) {
		if (SpectatorMode.isSpectatorForRender(event.entityPlayer)) {
			ci.cancel();
		}
	}
}
