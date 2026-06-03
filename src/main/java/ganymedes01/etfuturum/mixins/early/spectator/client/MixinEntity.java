package ganymedes01.etfuturum.mixins.early.spectator.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import ganymedes01.etfuturum.api.spectator.ISpectatorInfo;
import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity implements ISpectatorInfo {
    @Inject(method = "setAngles", at = @At("HEAD"))
    private void spectatorCameraLock(float yaw, float pitch, CallbackInfo ci,
                                     @Local(argsOnly = true, ordinal = 0) LocalFloatRef yawAssignable,
                                     @Local(argsOnly = true, ordinal = 1) LocalFloatRef pitchAssignable) {
        if((Object) this instanceof Entity entity) {
            if(SpectatorUtils.isSpectator(entity) && SpectatorUtils.getSpectatingEntity(entity) != null) {
                yawAssignable.set(0);
                pitchAssignable.set(0);
            }
        }
    }
}
