package ganymedes01.etfuturum.mixins.early.uninflammableitem;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = EntityItem.class, priority = 1001)
public abstract class MixinEntityItem extends Entity {

	@Shadow
	public abstract ItemStack getEntityItem();

	public MixinEntityItem(World worldIn) {
		super(worldIn);
	}

	private void makeImmuneToFire(ItemStack stack) {
		if (stack != null) {
			List<String> tags = EtFuturum.getOreStrings(stack);
			if (getEntityItem().getUnlocalizedName().contains("netherite")
					|| tags.contains("oreDebris") || tags.contains("scrapDebris")
					|| tags.contains("ingotNetherite") || tags.contains("blockNetherite")) {
				this.isImmuneToFire = true;
				this.fireResistance = Integer.MAX_VALUE;
			}
		}
	}

	@Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onUpdate()V"))
	private void applyFireImmunity(CallbackInfo ci) {
		if (ticksExisted == 1) {
			makeImmuneToFire(getEntityItem());
		}
	}

	@WrapOperation(method = "onUpdate",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
	private Block noFizzBounce(World instance, int x, int y, int z, Operation<Block> original) { //Returns AIR so the check for lava is false; we do this to remove the fizzing and bouncing that happens when items get incinerated
		return isImmuneToFire ? Blocks.air : original.call(instance, x, y, z);
	}

	@Override
	public boolean isBurning() {
		return !isImmuneToFire && super.isBurning();
	}

	@Inject(method = "attackEntityFrom", at = @At(value = "HEAD"), cancellable = true)
	public void attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (isImmuneToFire && source.isFireDamage()) {
			cir.setReturnValue(false);
		}
	}
}