package ganymedes01.etfuturum.entities.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ExtendedEntityMoveHelper extends EntityMoveHelper {
	protected float moveForward;
	protected float moveStrafe;
	public ExtendedEntityMoveHelper.Action action = ExtendedEntityMoveHelper.Action.WAIT;

	public ExtendedEntityMoveHelper(EntityLiving entitylivingIn) {
		super(entitylivingIn);
		this.entity = entitylivingIn;
	}

	@Override
	public boolean isUpdating() {
		return this.action == ExtendedEntityMoveHelper.Action.MOVE_TO;
	}

	@Override
	public double getSpeed() {
		return this.speed;
	}

	/**
	 * Sets the speed and location to move to
	 */
	@Override
	public void setMoveTo(double x, double y, double z, double speedIn) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.speed = speedIn;
		this.action = ExtendedEntityMoveHelper.Action.MOVE_TO;
	}

	public void setStrafe(float forward, float strafe) {
		this.action = ExtendedEntityMoveHelper.Action.STRAFE;
		this.moveForward = forward;
		this.moveStrafe = strafe;
		this.speed = 0.25D;
	}

	@Override
	public void onUpdateMoveHelper() {
		if (this.action == ExtendedEntityMoveHelper.Action.STRAFE) {
			float f = (float) this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
			float f1 = (float) this.speed * f;
			float f2 = this.moveForward;
			float f3 = this.moveStrafe;
			float f4 = MathHelper.sqrt_float(f2 * f2 + f3 * f3);

			if (f4 < 1.0F) {
				f4 = 1.0F;
			}

			f4 = f1 / f4;
			f2 = f2 * f4;
			f3 = f3 * f4;
			float f5 = MathHelper.sin(this.entity.rotationYaw * 0.017453292F);
			float f6 = MathHelper.cos(this.entity.rotationYaw * 0.017453292F);
			float f7 = f2 * f6 - f3 * f5;
			float f8 = f3 * f6 + f2 * f5;
			PathNavigate pathnavigate = this.entity.getNavigator();

			if (pathnavigate != null) {
				int width = MathHelper.ceiling_float_int(entity.width);
				Vec3 entityOrigin = pathnavigate.getEntityPosition();
				if (!pathnavigate.isSafeToStandAt(MathHelper.floor_double(entity.posX + f7), MathHelper.floor_double(entity.boundingBox.minY + 0.5D), MathHelper.floor_double(entity.posZ + f8),
						width, MathHelper.ceiling_float_int(entity.height), width, entityOrigin, f7, f8)) {
					this.moveForward = 1.0F;
					this.moveStrafe = 0.0F;
					f1 = f;
				}
			}

			this.entity.setAIMoveSpeed(f1);
			this.entity.setMoveForward(this.moveForward);
			this.entity.moveStrafing = this.moveStrafe;
			this.action = ExtendedEntityMoveHelper.Action.WAIT;
		} else if (this.action == ExtendedEntityMoveHelper.Action.MOVE_TO) {
			this.entity.moveStrafing = 0.0F;
			this.action = ExtendedEntityMoveHelper.Action.WAIT;
			double d0 = this.posX - this.entity.posX;
			double d1 = this.posZ - this.entity.posZ;
			double d2 = this.posY - this.entity.posY;
			double d3 = d0 * d0 + d2 * d2 + d1 * d1;

			if (d3 < 2.500000277905201E-7D) {
				this.entity.setMoveForward(0.0F);
				return;
			}

			float f9 = (float) (Math.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
			this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f9, 90.0F);
			this.entity.setAIMoveSpeed((float) (this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));

			if (d2 > (double) this.entity.stepHeight && d0 * d0 + d1 * d1 < (double) Math.max(1.0F, this.entity.width)) {
				this.entity.getJumpHelper().setJumping();
				this.action = ExtendedEntityMoveHelper.Action.JUMPING;
			}
		} else if (this.action == ExtendedEntityMoveHelper.Action.JUMPING) {
			this.entity.moveStrafing = 0.0F;
			this.entity.setAIMoveSpeed((float) (this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));

			if (this.entity.onGround) {
				this.action = ExtendedEntityMoveHelper.Action.WAIT;
			}
		} else {
			this.entity.setMoveForward(0.0F);
			this.entity.moveStrafing = 0.0F;
		}
	}

	protected float limitAngle(float p_75639_1_, float p_75639_2_, float p_75639_3_) {
		float f = MathHelper.wrapAngleTo180_float(p_75639_2_ - p_75639_1_);

		if (f > p_75639_3_) {
			f = p_75639_3_;
		}

		if (f < -p_75639_3_) {
			f = -p_75639_3_;
		}

		float f1 = p_75639_1_ + f;

		if (f1 < 0.0F) {
			f1 += 360.0F;
		} else if (f1 > 360.0F) {
			f1 -= 360.0F;
		}

		return f1;
	}

	public double getX() {
		return this.posX;
	}

	public double getY() {
		return this.posY;
	}

	public double getZ() {
		return this.posZ;
	}

	public enum Action {
		WAIT,
		MOVE_TO,
		STRAFE,
		JUMPING
	}
}
