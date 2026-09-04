package ganymedes01.etfuturum.client.particle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CustomDripFX extends EntityFX {
	private final String dripSound;
	private final boolean splashes;
	private boolean dripSoundPlayed;
	private int bobTimer;

	public CustomDripFX(World worldIn, double p_i1203_2_, double p_i1203_4_, double p_i1203_6_, String dripSound, int color, boolean splashes) {
		super(worldIn, p_i1203_2_, p_i1203_4_, p_i1203_6_, 0, 0, 0);
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionX = this.motionY = this.motionZ = 0.0D;
		particleAlpha = (color >> 24 & 0xff) / 255F;
		particleRed = (color >> 16 & 0xff) / 255F;
		particleGreen = (color >> 8 & 0xff) / 255F;
		particleBlue = (color & 0xff) / 255F;
		this.dripSound = dripSound;
		this.splashes = splashes;

		this.setParticleTextureIndex(113);
		this.setSize(0.01F, 0.01F);
		this.setPosition(this.posX, this.posY, this.posZ); // re-center bbox; setSize shrink doesn't call setPosition
		this.particleGravity = 0.06F;
		this.bobTimer = 40;
		this.particleMaxAge = (int) (64.0D / (Math.random() * 0.8D + 0.2D));
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.motionY -= this.particleGravity;

		if (this.bobTimer-- > 0) {
			this.motionX *= 0.02D;
			this.motionY *= 0.02D;
			this.motionZ *= 0.02D;
			this.setParticleTextureIndex(113);
		} else {
			this.setParticleTextureIndex(112);
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if (this.particleMaxAge-- <= 0) {
			this.setDead();
		}

		if (this.onGround) {
			if (dripSound != null && !dripSoundPlayed) {
				worldObj.playSound(posX, posY, posZ, dripSound, 1, 1, false);
				dripSoundPlayed = true;
			}
			if (splashes) {
				this.setDead();
				this.worldObj.spawnParticle("splash", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
			} else {
				this.setParticleTextureIndex(114);
			}

			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
		}

		int bX = MathHelper.floor_double(this.posX);
		int bY = MathHelper.floor_double(this.posY);
		int bZ = MathHelper.floor_double(this.posZ);

		Block block = this.worldObj.getBlock(bX, bY, bZ);
		int meta = this.worldObj.getBlockMetadata(bX, bY, bZ);

		Material material = block.getMaterial();

		if (material.isLiquid()) {
			double d0 = (float) (bY + 1) - BlockLiquid.getLiquidHeightPercent(meta);

			if (this.posY < d0) {
				this.setDead();
			}
		} else if (material.isSolid()) {
			block.setBlockBoundsBasedOnState(worldObj, bX, bY, bZ);

			AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(worldObj, bX, bY, bZ);

			if (aabb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ))) {
				this.setDead();
			}
		}
	}
}
