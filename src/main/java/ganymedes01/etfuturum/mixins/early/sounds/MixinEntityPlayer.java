package ganymedes01.etfuturum.mixins.early.sounds;

import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.blocks.BlockBerryBush;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {

	@Override
	@Shadow
	protected abstract String getDeathSound();

	@Override
	@Shadow
	protected abstract String getHurtSound();

	public MixinEntityPlayer(World p_i1594_1_) {
		super(p_i1594_1_);
	}

	@Unique
	private DamageSource etfuturum$lastDamageSource;

	@Inject(method = "damageEntity", at = @At("HEAD"))
	public void captureLastDamageSource(DamageSource source, float amount, CallbackInfo ci) {
		etfuturum$lastDamageSource = source;
	}

	@Unique
	private String etfuturum$getUniqueHurtSound() {
		if (etfuturum$lastDamageSource != null) {
			if (etfuturum$lastDamageSource.isFireDamage()) {
				return Tags.MC_ASSET_VER + ":entity.player.hurt_on_fire";
			}
			if (etfuturum$lastDamageSource == DamageSource.drown) {
				return Tags.MC_ASSET_VER + ":entity.player.hurt_drown";
			}
			if (etfuturum$lastDamageSource == BlockBerryBush.SWEET_BERRY_BUSH) {
				return Tags.MC_ASSET_VER + ":entity.player.hurt_sweet_berry_bush";
			}
		}
		return null;
	}

	@Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
	protected void getHurtSound(CallbackInfoReturnable<String> cir) {
		if (etfuturum$getUniqueHurtSound() != null) {
			cir.setReturnValue(etfuturum$getUniqueHurtSound());
		}
	}

	@Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
	protected void getDeathSound(CallbackInfoReturnable<String> cir) {
		if (etfuturum$getUniqueHurtSound() != null) {
			cir.setReturnValue(etfuturum$getUniqueHurtSound());
		}
	}

	@Inject(method = "playSound", at = @At("HEAD"), cancellable = true)
	public void overrideDamageSound(String name, float volume, float pitch, CallbackInfo ci) {
		if (etfuturum$getUniqueHurtSound() != null && (name.equals(getHurtSound()) || name.equals(getDeathSound()))) {
			this.worldObj.playSoundAtEntity(this, name, volume, pitch);
			etfuturum$lastDamageSource = null;
			ci.cancel();
		}
	}
}
