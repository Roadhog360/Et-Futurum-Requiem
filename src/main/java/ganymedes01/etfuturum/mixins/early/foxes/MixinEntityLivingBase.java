package ganymedes01.etfuturum.mixins.early.foxes;

import ganymedes01.etfuturum.entities.EntityFox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {

    DamageSource lastDamageSource;

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void preOnDeath(DamageSource src, CallbackInfo ci) {
        lastDamageSource = src;
    }

    @ModifyVariable(method = "onDeath", at = @At("STORE"), name = "i", ordinal = 0)
    public int lootingModifier(int value) {
        Entity entity = lastDamageSource.getEntity();
        if (entity instanceof EntityFox fox) {
            value = fox.getLootingLevel();
        }
        return value;
    }
}
