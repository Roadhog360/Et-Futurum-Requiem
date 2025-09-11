package ganymedes01.etfuturum.mixins.early.soulfire;

import ganymedes01.etfuturum.ducks.ISoulFireInfo;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public abstract class MixinEntity {

	@Shadow
	public abstract boolean attackEntityFrom(DamageSource source, float amount);

	@Shadow
	public World worldObj;

	@Shadow
	@Final
	public AxisAlignedBB boundingBox;

	@ModifyArg(method = "dealFireDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z"), index = 1)
	private float dealSoulFireDamage(float amount) {
		float damage = amount;
		if (isInSoulFire(boundingBox.contract(0.001D, 0.001D, 0.001D))) {
			damage *= 2;
		}
		return damage;
	}

	public boolean isInSoulFire(AxisAlignedBB p_147470_1_) {
		int i = MathHelper.floor_double(p_147470_1_.minX);
		int j = MathHelper.floor_double(p_147470_1_.maxX + 1.0D);
		int k = MathHelper.floor_double(p_147470_1_.minY);
		int l = MathHelper.floor_double(p_147470_1_.maxY + 1.0D);
		int i1 = MathHelper.floor_double(p_147470_1_.minZ);
		int j1 = MathHelper.floor_double(p_147470_1_.maxZ + 1.0D);

		if (worldObj.checkChunksExist(i, k, i1, j, l, j1)) {
			for (int k1 = i; k1 < j; ++k1) {
				for (int l1 = k; l1 < l; ++l1) {
					for (int i2 = i1; i2 < j1; ++i2) {
						Block block = worldObj.getBlock(k1, l1, i2);

						if (block instanceof ISoulFireInfo && ((ISoulFireInfo) block).efr$isSoulFire(worldObj, k1, l1, i2)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}
}
