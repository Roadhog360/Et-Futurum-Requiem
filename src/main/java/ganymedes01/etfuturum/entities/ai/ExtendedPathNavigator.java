package ganymedes01.etfuturum.entities.ai;

import ganymedes01.etfuturum.entities.INoGravityEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class ExtendedPathNavigator extends PathNavigate {

	private long lastTimeUpdated;
	protected boolean tryUpdatePath = true;
	private final PathFinder pathFinder;

	public ExtendedPathNavigator(EntityLiving p_i1671_1_, World p_i1671_2_) {
		super(p_i1671_1_, p_i1671_2_);
		this.pathFinder = this.getPathFinder();
	}

	protected abstract PathFinder getPathFinder();

	public void updatePath() {
		if (this.worldObj.getTotalWorldTime() - this.lastTimeUpdated > 20L) {
			if (this.getPath() != null) {
				int x = getPath().getFinalPathPoint().xCoord;
				int y = getPath().getFinalPathPoint().yCoord;
				int z = getPath().getFinalPathPoint().zCoord;
				this.currentPath = this.getPathToXYZ(x, y, z);
				this.lastTimeUpdated = this.worldObj.getTotalWorldTime();
				this.tryUpdatePath = false;
			}
		} else {
			this.tryUpdatePath = true;
		}
	}

	@Override
	public boolean tryMoveToXYZ(double x, double y, double z, double speedIn) {
		PathEntity pathentity = this.getPathToXYZ(MathHelper.floor_double(x), (int) y, MathHelper.floor_double(z));
		return pathentity != null && this.setPath(pathentity, speedIn);
	}

	@Override
	public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
		PathEntity pathentity = this.getPathToEntityLiving(entityIn);
		return pathentity != null && this.setPath(pathentity, speedIn);
	}

	@Override
	public PathEntity getPathToXYZ(double p_75488_1_, double p_75488_3_, double p_75488_5_) {
		return !this.canNavigate() ? null : getEntityPathToXYZ(MathHelper.floor_double(p_75488_1_), (int) p_75488_3_, MathHelper.floor_double(p_75488_5_), this.getPathSearchRange(), this.canPassOpenWoodenDoors, this.getCanBreakDoors(), this.getAvoidsWater(), this.canSwim);
	}

	@Override
	public PathEntity getPathToEntityLiving(Entity p_75494_1_) {
		return !this.canNavigate() ? null : getPathEntityToEntity(p_75494_1_, this.getPathSearchRange(), this.canPassOpenWoodenDoors, this.getCanBreakDoors(), this.getAvoidsWater(), this.canSwim);
	}

	public PathEntity getEntityPathToXYZ(double toPosX, double toPosY, double toPosZ, float searchRange, boolean canBreakDoors, boolean canEnterDoors, boolean avoidsWater, boolean canSwim, double xOffset, double yOffset, double zOffset) {
		worldObj.theProfiler.startSection("pathfind");
//		int l = MathHelper.floor_double(theEntity.posX + xOffset);
//		int i1 = MathHelper.floor_double(theEntity.posY + yOffset);
//		int j1 = MathHelper.floor_double(theEntity.posZ + zOffset);
//		int k1 = (int) (searchRange);
//		int l1 = l - k1;
//		int i2 = i1 - k1;
//		int j2 = j1 - k1;
//		int k2 = l + k1;
//		int l2 = i1 + k1;
//		int i3 = j1 + k1;
//      ChunkCache chunkcache = new ChunkCache(worldObj, l1, i2, j2, k2, l2, i3, 0);
		PathEntity pathentity = pathFinder.createEntityPathTo(theEntity, toPosX, toPosY, toPosZ, searchRange);
		worldObj.theProfiler.endSection();
		return pathentity;
	}

	public PathEntity getEntityPathToXYZ(int toPosX, int toPosY, int toPosZ, float searchRange, boolean canBreakDoors, boolean canEnterDoors, boolean avoidsWater, boolean canSwim) {
		return getEntityPathToXYZ((double) toPosX + 0.5D, (double) toPosY + 0.5D, (double) toPosZ + 0.5D, searchRange + 8.0F, canBreakDoors, canEnterDoors, avoidsWater, canSwim, 0.0D, 0.0D, 0.0D);
	}

	public PathEntity getPathEntityToEntity(Entity entityIn, float searchRange, boolean canBreakDoors, boolean canEnterDoors, boolean avoidsWater, boolean canSwim) {
		return getEntityPathToXYZ(entityIn.posX, entityIn.posY, entityIn.posZ, searchRange + 16.0F, canBreakDoors, canEnterDoors, avoidsWater, canSwim, 0.0D, 1.0D, 0.0D);
	}

	@Override
	public boolean canNavigate() {
		return theEntity instanceof INoGravityEntity || super.canNavigate();
	}
}
