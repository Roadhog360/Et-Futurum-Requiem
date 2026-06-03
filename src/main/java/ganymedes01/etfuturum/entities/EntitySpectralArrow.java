package ganymedes01.etfuturum.entities;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import ganymedes01.etfuturum.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySpectralArrow extends EntityArrow implements IEntityAdditionalSpawnData {

	private int duration = 200;

	public EntitySpectralArrow(World world) {
		super(world);
	}

	public EntitySpectralArrow(World world, EntityLivingBase shooter, float velocity) {
		super(world, shooter, velocity);
	}

	public EntitySpectralArrow(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (worldObj.isRemote && !inGround) {
			worldObj.spawnParticle("mobSpell",
					posX + rand.nextGaussian() * 0.12D,
					posY + rand.nextGaussian() * 0.12D,
					posZ + rand.nextGaussian() * 0.12D,
					0.9D, 0.9D, 0.4D);
		}
	}

	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int increments) {
		super.setPositionAndRotation2(x, y, z, yaw, pitch, increments);
		wrapPreviousRotationToCurrent();
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(shootingEntity == null ? -1 : shootingEntity.getEntityId());
		buffer.writeDouble(motionX);
		buffer.writeDouble(motionY);
		buffer.writeDouble(motionZ);
		buffer.writeFloat(rotationYaw);
		buffer.writeFloat(rotationPitch);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		int shooterId = buffer.readInt();
		Entity shooter = shooterId >= 0 ? worldObj.getEntityByID(shooterId) : null;
		shootingEntity = shooter;

		double syncedMotionX = buffer.readDouble();
		double syncedMotionY = buffer.readDouble();
		double syncedMotionZ = buffer.readDouble();
		float syncedYaw = buffer.readFloat();
		float syncedPitch = buffer.readFloat();

		setVelocity(syncedMotionX, syncedMotionY, syncedMotionZ);
		resetRotationFromMotion(syncedYaw, syncedPitch);
	}

	private void resetRotationFromMotion(float fallbackYaw, float fallbackPitch) {
		double horizontalMotion = motionX * motionX + motionZ * motionZ;
		double totalMotion = horizontalMotion + motionY * motionY;

		if (totalMotion > 1.0E-7D) {
			float horizontalDistance = MathHelper.sqrt_double(horizontalMotion);
			rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
			rotationPitch = (float) (Math.atan2(motionY, horizontalDistance) * 180.0D / Math.PI);
		} else {
			rotationYaw = fallbackYaw;
			rotationPitch = fallbackPitch;
		}

		prevRotationYaw = rotationYaw;
		prevRotationPitch = rotationPitch;
	}

	private void wrapPreviousRotationToCurrent() {
		while (rotationPitch - prevRotationPitch < -180.0F) {
			prevRotationPitch -= 360.0F;
		}

		while (rotationPitch - prevRotationPitch >= 180.0F) {
			prevRotationPitch += 360.0F;
		}

		while (rotationYaw - prevRotationYaw < -180.0F) {
			prevRotationYaw -= 360.0F;
		}

		while (rotationYaw - prevRotationYaw >= 180.0F) {
			prevRotationYaw += 360.0F;
		}
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("Duration")) {
			duration = compound.getInteger("Duration");
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("Duration", duration);
	}

	@Override
	public void onCollideWithPlayer(net.minecraft.entity.player.EntityPlayer player) {
		if (!worldObj.isRemote && inGround && arrowShake <= 0) {
			boolean flag = canBePickedUp == 1 || canBePickedUp == 2 && player.capabilities.isCreativeMode;

			ItemStack stack = ModItems.SPECTRAL_ARROW.newItemStack();

			if (canBePickedUp == 1 && !player.inventory.addItemStackToInventory(stack))
				flag = false;

			if (flag) {
				playSound("random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				player.onItemPickup(this, 1);
				setDead();
			}
		}
	}
}
