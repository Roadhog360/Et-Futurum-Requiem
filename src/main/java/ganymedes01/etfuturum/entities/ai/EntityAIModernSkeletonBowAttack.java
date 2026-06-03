package ganymedes01.etfuturum.entities.ai;

import ganymedes01.etfuturum.entities.ISkeletonSwingingArms;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EntityAIModernSkeletonBowAttack extends EntityAIBase {

	private final EntityLiving entityHost;
	private final IRangedAttackMob rangedAttackEntityHost;
	private final double moveSpeedAmp;
	private final float maxAttackDistance;
	private int attackCooldown;
	private int attackTime = -1;
	private int seeTime;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTime = -1;
	private int bowUseTime;

	public EntityAIModernSkeletonBowAttack(IRangedAttackMob rangedAttackMob, double speedAmplifier, int delay, float maxDistance) {
		if (!(rangedAttackMob instanceof EntityLiving)) {
			throw new IllegalArgumentException("ModernSkeletonBowAttack requires a living ranged mob");
		}

		this.entityHost = (EntityLiving) rangedAttackMob;
		this.rangedAttackEntityHost = rangedAttackMob;
		this.moveSpeedAmp = speedAmplifier;
		this.attackCooldown = delay;
		this.maxAttackDistance = maxDistance * maxDistance;
		this.setMutexBits(3);
	}

	public void setAttackCooldown(int attackCooldown) {
		this.attackCooldown = attackCooldown;
	}

	@Override
	public boolean shouldExecute() {
		return this.entityHost.getAttackTarget() != null && this.isBowInMainhand();
	}

	@Override
	public boolean continueExecuting() {
		return (this.shouldExecute() || !this.entityHost.getNavigator().noPath()) && this.isBowInMainhand();
	}

	@Override
	public void startExecuting() {
		super.startExecuting();
		this.setSwingingArms(true);
		this.setBowUseTime(0);
	}

	@Override
	public void resetTask() {
		super.resetTask();
		this.setSwingingArms(false);
		this.seeTime = 0;
		this.attackTime = -1;
		this.strafingTime = -1;
		this.bowUseTime = 0;
		this.setBowUseTime(0);
	}

	@Override
	public void updateTask() {
		EntityLivingBase attackTarget = this.entityHost.getAttackTarget();

		if (attackTarget == null) {
			return;
		}

		double distance = this.entityHost.getDistanceSq(attackTarget.posX, attackTarget.boundingBox.minY, attackTarget.posZ);
		boolean canSee = this.entityHost.getEntitySenses().canSee(attackTarget);
		boolean hadSeenTarget = this.seeTime > 0;

		if (canSee != hadSeenTarget) {
			this.seeTime = 0;
		}

		if (canSee) {
			++this.seeTime;
		} else {
			--this.seeTime;
		}

		if (distance <= this.maxAttackDistance && this.seeTime >= 20) {
			this.entityHost.getNavigator().clearPathEntity();
			++this.strafingTime;
		} else {
			this.entityHost.getNavigator().tryMoveToEntityLiving(attackTarget, this.moveSpeedAmp);
			this.strafingTime = -1;
		}

		if (this.strafingTime >= 20) {
			if (this.entityHost.getRNG().nextFloat() < 0.3F) {
				this.strafingClockwise = !this.strafingClockwise;
			}

			if (this.entityHost.getRNG().nextFloat() < 0.3F) {
				this.strafingBackwards = !this.strafingBackwards;
			}

			this.strafingTime = 0;
		}

		if (this.strafingTime > -1) {
			if (distance > this.maxAttackDistance * 0.75F) {
				this.strafingBackwards = false;
			} else if (distance < this.maxAttackDistance * 0.25F) {
				this.strafingBackwards = true;
			}

			this.strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
			this.entityHost.faceEntity(attackTarget, 30.0F, 30.0F);
		} else {
			this.entityHost.getLookHelper().setLookPositionWithEntity(attackTarget, 30.0F, 30.0F);
		}

		if (this.bowUseTime > 0) {
			if (!canSee && this.seeTime < -60) {
				this.bowUseTime = 0;
				this.setBowUseTime(0);
			} else if (canSee) {
				++this.bowUseTime;
				this.setBowUseTime(this.bowUseTime);

				if (this.bowUseTime >= 20) {
					this.rangedAttackEntityHost.attackEntityWithRangedAttack(attackTarget, getArrowVelocity(this.bowUseTime));
					this.bowUseTime = 0;
					this.setBowUseTime(0);
					this.attackTime = this.attackCooldown;
				}
			}
		} else if (--this.attackTime <= 0 && this.seeTime >= -60) {
			this.bowUseTime = 1;
			this.setBowUseTime(this.bowUseTime);
		}
	}

	private boolean isBowInMainhand() {
		ItemStack heldItem = this.entityHost.getHeldItem();
		return heldItem != null && heldItem.getItem() == Items.bow;
	}

	private void strafe(float forward, float strafe) {
		EntityMoveHelper moveHelper = this.entityHost.getMoveHelper();

		if (moveHelper instanceof ExtendedEntityMoveHelper) {
			((ExtendedEntityMoveHelper) moveHelper).setStrafe(forward, strafe);
		}
	}

	private void setSwingingArms(boolean swingingArms) {
		if (this.entityHost instanceof ISkeletonSwingingArms) {
			((ISkeletonSwingingArms) this.entityHost).etfu$setSwingingArms(swingingArms);
		}
	}

	private void setBowUseTime(int bowUseTime) {
		if (this.entityHost instanceof ISkeletonSwingingArms) {
			((ISkeletonSwingingArms) this.entityHost).etfu$setBowUseTime(bowUseTime);
		}
	}

	private static float getArrowVelocity(int charge) {
		float velocity = (float) charge / 20.0F;
		velocity = (velocity * velocity + velocity * 2.0F) / 3.0F;
		return velocity > 1.0F ? 1.0F : velocity;
	}
}
