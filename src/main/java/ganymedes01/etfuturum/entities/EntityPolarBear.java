package ganymedes01.etfuturum.entities;

import ganymedes01.etfuturum.Tags;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class EntityPolarBear extends EntityAnimal {

	private static final int STANDING_DATA_WATCHER_ID = 18;

	private float prevStandingAnimation;
	private float standingAnimation;
	private int warningSoundTicks;

	public EntityPolarBear(World world) {
		super(world);
		setSize(1.3F, 1.4F);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityPolarBear.AIMeleeAttack(1.25D, true));
		tasks.addTask(1, new EntityPolarBear.AIPanic());
		tasks.addTask(4, new EntityAIFollowParent(this, 1.25D));
		tasks.addTask(5, new EntityAIWander(this, 1.0D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(7, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityPolarBear.AIHurtByTarget());
		targetTasks.addTask(2, new EntityPolarBear.AIAttackPlayer());
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(STANDING_DATA_WATCHER_ID, (byte) 0);
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return new EntityPolarBear(worldObj);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (worldObj.isRemote) {
			prevStandingAnimation = standingAnimation;

			if (isStanding()) {
				standingAnimation = MathHelper.clamp_float(standingAnimation + 1.0F, 0.0F, 6.0F);
			} else {
				standingAnimation = MathHelper.clamp_float(standingAnimation - 1.0F, 0.0F, 6.0F);
			}
		}

		if (warningSoundTicks > 0) {
			--warningSoundTicks;
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		return !isChild() && entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) ((int) getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue()));
	}

	public boolean isStanding() {
		return dataWatcher.getWatchableObjectByte(STANDING_DATA_WATCHER_ID) != 0;
	}

	public void setStanding(boolean standing) {
		dataWatcher.updateObject(STANDING_DATA_WATCHER_ID, standing ? (byte) 1 : (byte) 0);
	}

	public float getStandingAnimationScale(float partialTicks) {
		return (prevStandingAnimation + (standingAnimation - prevStandingAnimation) * partialTicks) / 6.0F;
	}

	protected void playWarningSound() {
		if (warningSoundTicks <= 0) {
			playSound(Tags.MC_ASSET_VER + ":entity.polar_bear.warning", 1.0F, 1.0F);
			warningSoundTicks = 40;
		}
	}

	@Override
	protected String getLivingSound() {
		return isChild() ? Tags.MC_ASSET_VER + ":entity.polar_bear.ambient_baby" : Tags.MC_ASSET_VER + ":entity.polar_bear.ambient";
	}

	@Override
	protected String getHurtSound() {
		return Tags.MC_ASSET_VER + ":entity.polar_bear.hurt";
	}

	@Override
	protected String getDeathSound() {
		return Tags.MC_ASSET_VER + ":entity.polar_bear.death";
	}

	@Override
	protected void func_145780_a(int x, int y, int z, Block block) {
		playSound(Tags.MC_ASSET_VER + ":entity.polar_bear.step", 0.15F, 1.0F);
	}

	@Override
	protected void dropFewItems(boolean hitRecently, int fortune) {
		int fishMeta = rand.nextInt(4) == 0 ? 1 : 0;
		int count = rand.nextInt(3) + rand.nextInt(1 + fortune);

		for (int i = 0; i < count; ++i) {
			entityDropItem(new ItemStack(Items.fish, 1, fishMeta), 0.0F);
		}
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEFINED;
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData livingData) {
		if (livingData instanceof EntityPolarBear.GroupData) {
			if (((EntityPolarBear.GroupData) livingData).spawnedAdult) {
				setGrowingAge(-24000);
			}
		} else {
			EntityPolarBear.GroupData groupData = new EntityPolarBear.GroupData();
			groupData.spawnedAdult = true;
			livingData = groupData;
		}

		return livingData;
	}

	class AIAttackPlayer extends EntityAINearestAttackableTarget {

		public AIAttackPlayer() {
			super(EntityPolarBear.this, EntityPlayer.class, 20, true, true);
		}

		@Override
		public boolean shouldExecute() {
			if (EntityPolarBear.this.isChild()) {
				return false;
			}

			if (super.shouldExecute()) {
				List<EntityPolarBear> bears = worldObj.getEntitiesWithinAABB(EntityPolarBear.class, boundingBox.expand(8.0D, 4.0D, 8.0D));
				for (EntityPolarBear bear : bears) {
					if (bear.isChild()) {
						return true;
					}
				}
			}

			EntityPolarBear.this.setAttackTarget(null);
			return false;
		}

		@Override
		protected double getTargetDistance() {
			return super.getTargetDistance() * 0.5D;
		}
	}

	class AIHurtByTarget extends EntityAIHurtByTarget {

		public AIHurtByTarget() {
			super(EntityPolarBear.this, false);
		}

		@Override
		public void startExecuting() {
			if (EntityPolarBear.this.isChild()) {
				EntityLivingBase attacker = EntityPolarBear.this.getAITarget();
				if (attacker != null) {
					alertAdultPolarBears(attacker);
				}

				EntityPolarBear.this.setAttackTarget(null);
				EntityPolarBear.this.setRevengeTarget(null);
			} else {
				super.startExecuting();
			}
		}

		private void alertAdultPolarBears(EntityLivingBase attacker) {
			List<EntityPolarBear> bears = worldObj.getEntitiesWithinAABB(EntityPolarBear.class, boundingBox.expand(16.0D, 8.0D, 16.0D));
			for (EntityPolarBear bear : bears) {
				if (bear != EntityPolarBear.this && !bear.isChild()) {
					bear.setAttackTarget(attacker);
				}
			}
		}
	}

	class AIMeleeAttack extends EntityAIBase {

		private final double speedTowardsTarget;
		private final boolean longMemory;
		private int attackTick;
		private int delayCounter;
		private double targetX;
		private double targetY;
		private double targetZ;
		private PathEntity entityPathEntity;

		public AIMeleeAttack(double speed, boolean useLongMemory) {
			speedTowardsTarget = speed;
			longMemory = useLongMemory;
			setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase target = EntityPolarBear.this.getAttackTarget();
			if (target == null || !target.isEntityAlive() || EntityPolarBear.this.isChild()) {
				return false;
			}

			entityPathEntity = EntityPolarBear.this.getNavigator().getPathToEntityLiving(target);
			return entityPathEntity != null || EntityPolarBear.this.getDistanceSqToEntity(target) <= getAttackReachSqr(target);
		}

		@Override
		public boolean continueExecuting() {
			EntityLivingBase target = EntityPolarBear.this.getAttackTarget();
			if (target == null || !target.isEntityAlive() || EntityPolarBear.this.isChild()) {
				return false;
			}

			return longMemory || !EntityPolarBear.this.getNavigator().noPath();
		}

		@Override
		public void startExecuting() {
			EntityPolarBear.this.getNavigator().setPath(entityPathEntity, speedTowardsTarget);
			delayCounter = 0;
		}

		@Override
		public void resetTask() {
			EntityPolarBear.this.getNavigator().clearPathEntity();
			EntityPolarBear.this.setStanding(false);
		}

		@Override
		public void updateTask() {
			EntityLivingBase target = EntityPolarBear.this.getAttackTarget();
			if (target == null) {
				return;
			}

			EntityPolarBear.this.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
			double distance = EntityPolarBear.this.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
			--delayCounter;

			if ((longMemory || EntityPolarBear.this.getEntitySenses().canSee(target)) && delayCounter <= 0 && (targetX == 0.0D && targetY == 0.0D && targetZ == 0.0D || target.getDistanceSq(targetX, targetY, targetZ) >= 1.0D || EntityPolarBear.this.getRNG().nextFloat() < 0.05F)) {
				targetX = target.posX;
				targetY = target.boundingBox.minY;
				targetZ = target.posZ;
				delayCounter = 4 + EntityPolarBear.this.getRNG().nextInt(7);

				if (distance > 1024.0D) {
					delayCounter += 10;
				} else if (distance > 256.0D) {
					delayCounter += 5;
				}

				if (!EntityPolarBear.this.getNavigator().tryMoveToEntityLiving(target, speedTowardsTarget)) {
					delayCounter += 15;
				}
			}

			--attackTick;
			checkAndPerformAttack(target, distance);
		}

		private void checkAndPerformAttack(EntityLivingBase target, double distance) {
			double reach = getAttackReachSqr(target);

			if (distance <= reach && attackTick <= 0) {
				attackTick = 20;
				EntityPolarBear.this.swingItem();
				EntityPolarBear.this.attackEntityAsMob(target);
				EntityPolarBear.this.setStanding(false);
			} else if (distance <= reach * 2.0D) {
				if (attackTick <= 0) {
					EntityPolarBear.this.setStanding(false);
					attackTick = 20;
				}

				if (attackTick <= 10) {
					EntityPolarBear.this.setStanding(true);
					EntityPolarBear.this.playWarningSound();
				}
			} else {
				attackTick = 20;
				EntityPolarBear.this.setStanding(false);
			}
		}

		private double getAttackReachSqr(EntityLivingBase target) {
			return 4.0F + target.width;
		}
	}

	class AIPanic extends EntityAIPanic {

		public AIPanic() {
			super(EntityPolarBear.this, 2.0D);
		}

		@Override
		public boolean shouldExecute() {
			return (EntityPolarBear.this.isChild() || EntityPolarBear.this.isBurning()) && super.shouldExecute();
		}
	}

	static class GroupData implements IEntityLivingData {
		public boolean spawnedAdult;
	}
}
