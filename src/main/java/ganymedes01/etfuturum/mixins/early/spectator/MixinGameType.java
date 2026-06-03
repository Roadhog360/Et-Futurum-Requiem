package ganymedes01.etfuturum.mixins.early.spectator;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSettings.GameType.class)
public abstract class MixinGameType {
	@Unique
	private boolean etfuturum$isSpectator() {
		return (Object) this == SpectatorUtils.SPECTATOR_GAMETYPE;
	}

	@Inject(method = "configurePlayerCapabilities", at = @At("HEAD"), cancellable = true)
	public void configureSpecCaps(PlayerCapabilities caps, CallbackInfo ci) {
		if (etfuturum$isSpectator()) {
			ci.cancel();
			caps.allowFlying = true;
			caps.isCreativeMode = false;
			caps.disableDamage = true;
			caps.allowEdit = false;
			caps.isFlying = true;
		}
	}

	@Shadow
	public abstract String getName();

	@ModifyReturnValue(method = "isAdventure", at = @At(value = "RETURN"))
	private boolean isAdventureOrSpectator(boolean original) {
		return original || etfuturum$isSpectator();
	}
}
