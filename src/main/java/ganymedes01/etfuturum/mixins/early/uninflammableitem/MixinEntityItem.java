package ganymedes01.etfuturum.mixins.early.uninflammableitem;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
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

	@Inject(method = "onUpdate", at = @At("HEAD"))
	private void floatLava(CallbackInfo ci) {
//		if (!worldObj.isRemote && isImmuneToFire) Logger.debug("item motions: x: " + motionX + " y: " + motionY + " z: " + motionZ);
		// This is a rough check if the item is in lava, if we wanted something based on the actual hitbox, see World.handleMaterialAcceleration()
		if (isImmuneToFire && isLava(this.posX, this.posY, this.posZ, this.worldObj, false)) {
			if (this.motionY > -0.15D && Math.max(Math.abs(this.motionX), Math.abs(this.motionZ)) < 0.015) {

				boolean aboveBlockIsSource = isLava(this.posX, this.posY + 1, this.posZ, this.worldObj, true);
				// We set the target height into the next (higher) block if it is also source-lava, otherwise we use the liquid level of the current block
				float liquidHeight = aboveBlockIsSource ? 1.1f : 0.9f - BlockLiquid.getLiquidHeightPercent(this.worldObj.getBlockMetadata(
        		    MathHelper.floor_double(this.posX),
					MathHelper.floor_double(this.posY),
        		    MathHelper.floor_double(this.posZ)
				));
				double surfaceY = Math.floor(this.posY) + liquidHeight;

				if (this.posY < surfaceY) {
					this.motionY = 0.05D;
					this.motionX *= 0.4D;
					this.motionZ *= 0.4D;
				} else {
					/* This is an alternate variant that simulates stronger bobbing on lava, needs  - 0.3D at the surface check
					if ((this.ticksExisted - (!this.worldObj.isRemote ? 0 : 10)) % 100 < 50) {
						this.motionY = 0.045D;
					} else {
						this.motionY = 0.032D;
					}*/
					// Once we reach the target height, float
					// Using the exact gravity constant here, to prevent resync later on
					this.motionY = 0.03999999910593033D;
				}
			}
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

	private boolean isLava(double posX, double posY, double posZ, World worldObj, boolean checkSource) {
		int[] pos = new int[] {MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)};
		Block block = worldObj.getBlock(pos[0], pos[1], pos[2]);

		return block.getMaterial().isLiquid() &&
				block.getMaterial() == Material.lava &&
				(!checkSource || worldObj.getBlockMetadata(pos[0], pos[1], pos[2]) == 0);
	}
}