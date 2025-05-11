package ganymedes01.etfuturum.entities;

import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityNewBoatSeat extends Entity {

	private EntityNewBoat boat;
	private UUID boatUUID;

	public EntityNewBoatSeat(World worldIn) {
		super(worldIn);
		this.setSize(0, 0);
		this.width = 0;
		this.height = 0;
		this.setInvisible(true);
		this.noClip = true;
	}

	public EntityNewBoatSeat(World worldIn, EntityNewBoat boat) {
		this(worldIn);
		setBoat(boat);
	}

	@Override
	public void entityInit() {
		dataWatcher.addObject(17, boat == null ? 0 : boat.getEntityId()); //Attached boat ID
	}

	public void setBoat(EntityNewBoat newBoat) {
		boat = newBoat;
		if (newBoat != null) {
			dataWatcher.updateObject(17, newBoat.getEntityId());
			this.boatUUID = newBoat.getPersistentID();
		}
	}

	@Override
	public boolean isEntityInvulnerable() {
		return true;
	}

	public void sitEntity(Entity entity) {
		entity.mountEntity(this);
	}

	@Override
	public void onUpdate() {

		if (isDead || !worldObj.getChunkFromBlockCoords((int) posX, (int) posZ).isChunkLoaded || !ConfigBlocksItems.newBoatPassengerSeat) {
			return;
		}

		if (ridingEntity != null) {
			ridingEntity.mountEntity(null);
			ridingEntity = null;
		}

		if (riddenByEntity != null && !riddenByEntity.isEntityAlive()) {
			riddenByEntity = null;
		}

		int boatID = dataWatcher.getWatchableObjectInt(17);

		if (boat == null && !worldObj.isRemote) {
			if (boatID == 0 && boatUUID != null) {
				for (Entity entity : worldObj.loadedEntityList) {
					if (entity == null || entity.getPersistentID() == null)
						continue;
					if (entity.getPersistentID().equals(boatUUID) && entity instanceof EntityNewBoat && !entity.isDead) {
						if (riddenByEntity instanceof EntityPlayer) {
							riddenByEntity.mountEntity(null);
						}
						setBoat((EntityNewBoat) entity);
						break;
					}
				}
			}
		}

		if (boatID > 0 && boat == null) {
			Entity entity = worldObj.getEntityByID(boatID);
			if (entity instanceof EntityNewBoat && !entity.isDead) {
				setBoat((EntityNewBoat) entity);
			}
		}

		if (worldObj.isRemote) {
			boolean flag = false;
			if (boat != null && boat.getSeat() == null) {
				flag = true;
			} else if (boat != null && boat.getSeat() != null && boat.getSeat().getEntityId() != boatID && boat.getSeat().riddenByEntity == null) {
				flag = true;
			}
			if (flag) {
				boat.setSeat(this);
			}
		}

		if (boat == null || boat.isDead) {
			kill();
			return;
		}

		copyLocationAndAnglesFrom(boat);
	}

    private void mountToBoat(Entity entity) {
        boat.sitEntity(entity);
    }

	@Override
	protected void kill() {
		if (riddenByEntity != null) {
			if (riddenByEntity instanceof EntityLivingBase) {
				((EntityLivingBase) riddenByEntity).dismountEntity(this);
			}
			riddenByEntity.mountEntity(null);
		}
		isDead = true;
		boat = null;
	}

	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements) {
		if (boat != null) {
			copyLocationAndAnglesFrom(boat);
			prevRotationYaw = boat.prevRotationYaw;
			prevRotationPitch = boat.prevRotationPitch;
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		boatUUID = new UUID(nbt.getLong("boatUUIDMost"), nbt.getLong("boatUUIDLeast"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setLong("boatUUIDLeast", boatUUID.getLeastSignificantBits());
		nbt.setLong("boatUUIDMost", boatUUID.getMostSignificantBits());
	}

	/**
	 * The boat this entity is following.
	 */
	public EntityNewBoat getBoat() {
		return boat;
	}

	@Override
	public boolean writeToNBTOptional(NBTTagCompound tagCompund) {
		if (this.ridingEntity != null) {
			ridingEntity.mountEntity(null);
			ridingEntity = null;
		}
		//This entity does not save on disk, we save it through the boat instead.
		return false;
	}

	@Override
	public void updateRiderPosition() {
		if (boat != null && riddenByEntity != null) {
			copyLocationAndAnglesFrom(boat);
			boat.updatePassenger(riddenByEntity);
		}
	}

	/*
	 * ====================DUMMY START====================
	 *
	 *  Any code beyond this point has been canceled out with
	 *  empty methods for performance reasons. This is to
	 *  ensure that code that doesn't matter isn't running.
	 *
	 * ===================================================
	 */


	@Deprecated
	@Override
	public void onEntityUpdate() {

	}

	@Deprecated
	@Override
	public void moveEntity(double x, double y, double z) {

	}

	@Deprecated
	@Override
	public void updateRidden() {

	}

	@Deprecated
	@Override
	public boolean isBurning() {
		return false;
	}

	@Deprecated
	@Override
	public boolean isRiding() {
		return false;

	}

	@Deprecated
	@Override
	public boolean isInvisibleToPlayer(EntityPlayer player) {
		return true;
	}

	@Deprecated
	@Override
	public boolean isInvisible() {
		return true;
	}

	@Deprecated
	@Override
	public void setInWeb() {

	}

	@Deprecated
	@Override
	public boolean hitByEntity(Entity entityIn) {
		return true;
	}

	@Deprecated
	@Override
	public AxisAlignedBB getBoundingBox() {
		return null;
	}

	@Deprecated
	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return null;
	}

	@Deprecated
	@Override
	public void applyEntityCollision(Entity entityIn) {

	}

	@Deprecated
	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Deprecated
	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Deprecated
	@Override
	public boolean canBePushed() {
		return false;
	}

	@Deprecated
	@Override
	public boolean shouldRenderInPass(int pass) {
		return false;
	}

}
