package ganymedes01.etfuturum.mixins.late.backlytra.thaumcraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import ganymedes01.etfuturum.elytra.IElytraPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import thaumcraft.common.lib.events.EventHandlerEntity;

@Mixin(EventHandlerEntity.class)
public abstract class MixinEventHandlerEntity {

	@WrapOperation(method = "updateSpeed", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerCapabilities;isFlying:Z"))
	private boolean checkElytra(PlayerCapabilities instance, Operation<Boolean> original, @Local(argsOnly = true) EntityPlayer player) {
		if (player instanceof IElytraPlayer elytraPlayer && elytraPlayer.etfu$isElytraFlying()) {
			return true; // Condition is inverted; this really returns false.
		}
		return original.call(instance);
	}
}
