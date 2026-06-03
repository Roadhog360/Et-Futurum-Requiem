package ganymedes01.etfuturum.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class FlyingPathNavigator extends ExtendedPathNavigator {

	public FlyingPathNavigator(EntityLiving p_i1671_1_, World p_i1671_2_) {
		super(p_i1671_1_, p_i1671_2_);
	}

	@Override
	protected PathFinder getPathFinder() {
		return new FlyingPathFinder(worldObj, this.canPassOpenWoodenDoors, this.getCanBreakDoors(), this.getAvoidsWater(), this.canSwim);
	}

	/**
	 * If on ground or swimming and can swim
	 */
	@Override
	public boolean canNavigate() {
		return canSwim && this.isInLiquid() || !this.theEntity.isRiding();
	}

	@Override
	public Vec3 getEntityPosition() {
		return Vec3.createVectorHelper(this.theEntity.posX, this.theEntity.posY, this.theEntity.posZ);
	}

	@Override
	public void onUpdateNavigation() {
		++this.totalTicks;

		if (tryUpdatePath) {
			this.updatePath();
		}

		if (!this.noPath()) {
			if (this.canNavigate()) {
				this.pathFollow();
			} else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
				Vec3 Vec3 = this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex());

				if (MathHelper.floor_double(this.theEntity.posX) == MathHelper.floor_double(Vec3.xCoord) && MathHelper.floor_double(this.theEntity.posY) == MathHelper.floor_double(Vec3.yCoord) && MathHelper.floor_double(this.theEntity.posZ) == MathHelper.floor_double(Vec3.zCoord)) {
					this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
				}
			}

			if (!this.noPath()) {
				Vec3 Vec31 = this.currentPath.getPosition(this.theEntity);
				this.theEntity.getMoveHelper().setMoveTo(Vec31.xCoord, Vec31.yCoord, Vec31.zCoord, this.speed);
			}
		}
	}

	/**
	 * Checks if the specified entity can safely walk to the specified location.
	 */
	@Override
	public boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
		int i = MathHelper.floor_double(posVec31.xCoord);
		int j = MathHelper.floor_double(posVec31.yCoord);
		int k = MathHelper.floor_double(posVec31.zCoord);
		double d0 = posVec32.xCoord - posVec31.xCoord;
		double d1 = posVec32.yCoord - posVec31.yCoord;
		double d2 = posVec32.zCoord - posVec31.zCoord;
		double d3 = d0 * d0 + d1 * d1 + d2 * d2;

		if (d3 < 1.0E-8D) {
			return false;
		} else {
			double d4 = 1.0D / Math.sqrt(d3);
			d0 = d0 * d4;
			d1 = d1 * d4;
			d2 = d2 * d4;
			double d5 = 1.0D / Math.abs(d0);
			double d6 = 1.0D / Math.abs(d1);
			double d7 = 1.0D / Math.abs(d2);
			double d8 = (double) i - posVec31.xCoord;
			double d9 = (double) j - posVec31.yCoord;
			double d10 = (double) k - posVec31.zCoord;

			if (d0 >= 0.0D) {
				++d8;
			}

			if (d1 >= 0.0D) {
				++d9;
			}

			if (d2 >= 0.0D) {
				++d10;
			}

			d8 = d8 / d0;
			d9 = d9 / d1;
			d10 = d10 / d2;
			int l = d0 < 0.0D ? -1 : 1;
			int i1 = d1 < 0.0D ? -1 : 1;
			int j1 = d2 < 0.0D ? -1 : 1;
			int k1 = MathHelper.floor_double(posVec32.xCoord);
			int l1 = MathHelper.floor_double(posVec32.yCoord);
			int i2 = MathHelper.floor_double(posVec32.zCoord);
			int j2 = k1 - i;
			int k2 = l1 - j;
			int l2 = i2 - k;

			while (j2 * l > 0 || k2 * i1 > 0 || l2 * j1 > 0) {
				if (d8 < d10 && d8 <= d9) {
					d8 += d5;
					i += l;
					j2 = k1 - i;
				} else if (d9 < d8 && d9 <= d10) {
					d9 += d6;
					j += i1;
					k2 = l1 - j;
				} else {
					d10 += d7;
					k += j1;
					l2 = i2 - k;
				}
			}

			return true;
		}
	}

	@Override
	public boolean isSafeToStandAt(int p_75483_1_, int p_75483_2_, int p_75483_3_, int p_75483_4_, int p_75483_5_, int p_75483_6_, Vec3 p_75483_7_, double p_75483_8_, double p_75483_10_) {
		int k1 = p_75483_1_ - p_75483_4_ / 2;
		int l1 = p_75483_3_ - p_75483_6_ / 2;

		if (this.isPositionClear(k1, p_75483_2_, l1, p_75483_4_, p_75483_5_, p_75483_6_, p_75483_7_, p_75483_8_, p_75483_10_)) {
			for (int i2 = k1; i2 < k1 + p_75483_4_; ++i2) {
				for (int j2 = l1; j2 < l1 + p_75483_6_; ++j2) {
					double d2 = (double) i2 + 0.5D - p_75483_7_.xCoord;
					double d3 = (double) j2 + 0.5D - p_75483_7_.zCoord;

					if (d2 * p_75483_8_ + d3 * p_75483_10_ >= 0.0D) {
						Block block = this.worldObj.getBlock(i2, p_75483_2_, j2);
						if (block.isSideSolid(worldObj, i2, p_75483_2_ - 1, j2, ForgeDirection.UP)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
