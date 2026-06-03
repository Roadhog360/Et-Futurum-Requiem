package ganymedes01.etfuturum.mixins.early.spectator;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class MixinEntity {
	@Shadow
	public boolean noClip;

	@ModifyReturnValue(method = "isEntityInsideOpaqueBlock", at = @At("RETURN"))
	private boolean ignoreBlockIfSpectator(boolean original) {
		if (this.noClip && original) {
			if(SpectatorUtils.isSpectator((Entity) (Object) this)) {
				return false;
			}
		}
		return original;
	}

	@ModifyReturnValue(method = "canAttackWithItem", at = @At(value = "RETURN"))
	public boolean canAttackWithItem(boolean original)
	{
		if(original) {
			return !SpectatorUtils.isSpectator((Entity) (Object) this);
		}
		return false;
	}

	@ModifyReturnValue(method = "handleWaterMovement", at = @At(value = "RETURN"))
	public boolean handleWaterMovement(boolean original) {
		if(original) {
			return !SpectatorUtils.isSpectator((Entity) (Object) this);
		}
		return false;
	}
}
