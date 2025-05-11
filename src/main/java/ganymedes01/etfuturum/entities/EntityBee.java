package ganymedes01.etfuturum.entities;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.api.BeePlantRegistry;
import ganymedes01.etfuturum.blocks.BlockBeeHive;
import ganymedes01.etfuturum.blocks.BlockMagma;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.core.utils.EntityVectorUtils;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.entities.ai.FlyMoveHelper;
import ganymedes01.etfuturum.entities.ai.FlyingPathNavigator;
import ganymedes01.etfuturum.entities.attributes.EtFuturumEntityAttributes;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.tileentities.TileEntityBeeHive;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * Credit to thedarkcolour for permission for thedarkcolour to see how FutureMC bees work in 1.12 to see what makes bees work without 1.15 mechanics present, and what part of 1.15 mechanics need to be changed or cut.
 * FutureMC was only referenced; no code was actually used from it; It is ARR but visible source.
 */
public class EntityBee extends EntityAnimal implements INoGravityEntity {
	private static final int DATA_FLAGS_ID = 13; //byte
	private static final int ANGER_TIME = 14; //int
	private static final int NO_GRAVITY = 15; //byte (as boolean)
	private UUID lastHurtBy;
	private float rollAmount;
	private float rollAmountO;
	private int timeSinceSting;
	private int ticksWithoutNectarSinceExitingHive;
	private int stayOutOfHiveCountdown;
	private int numCropsGrownSincePollination;
	private int remainingCooldownBeforeLocatingNewHive = 0;
	private int remainingCooldownBeforeLocatingNewFlower = 0;
	private BlockPos savedFlowerPos = null;
	private BlockPos hivePos = null;
	private EntityBee.PollinateGoal pollinateGoal;
	private EntityBee.FindBeehiveGoal findBeehiveGoal;
	private EntityBee.FindFlowerGoal findFlowerGoal;
	private int underWaterTicks;

	private float moveVertical;

	public EntityBee(World worldIn) {
		super(worldIn);
		setSize(0.7F, 0.7F);

		navigator = new FlyingPathNavigator(this, worldIn) {
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
								Block block = this.worldObj.getBlock(i2, p_75483_2_ - 1, j2);
								if (block.isAir(worldObj, i2, p_75483_2_ - 1, j2)) {
									return true;
								}
							}
						}
					}
				}
				return false;
			}

			@Override
			public void onUpdateNavigation() {
				if (!EntityBee.this.pollinateGoal.isRunning()) {
					super.onUpdateNavigation();
				}
			}
		};
		lookHelper = new BeeLookController(this);
		moveHelper = new FlyMoveHelper(this, 10, true);
		addTasks();
		getNavigator().setBreakDoors(false);
		getNavigator().setEnterDoors(true);
		getNavigator().setCanSwim(false);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataWatcher().addObject(DATA_FLAGS_ID, (byte) 0);
		this.getDataWatcher().addObject(ANGER_TIME, 0);
		this.getDataWatcher().addObject(NO_GRAVITY, (byte) 1);
	}

	@Override
	public float getBlockPathWeight(int x, int y, int z) {
		Block block = worldObj.getBlock(x, y, z);
		if (block.getMaterial() == Material.air) {
			return 10.0F;
		}
		if (block.getMaterial().isLiquid() || block instanceof BlockFire || block instanceof BlockCocoa || block instanceof BlockFence || block instanceof BlockWall || block instanceof BlockMagma) {
			return -1F;
		}
		return 0.0F;
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	protected void addTasks() {
		tasks.addTask(0, new EntityBee.StingGoal(this, EntityLivingBase.class, 1.4D, true));
		tasks.addTask(1, new EntityBee.EnterBeehiveGoal());
		tasks.addTask(2, new EntityAIMate(this, 1.0D));
		tasks.addTask(3, new TemptBeeWithFlowerGoal(this, 1.25D, false));
		this.pollinateGoal = new EntityBee.PollinateGoal();
		tasks.addTask(4, this.pollinateGoal);
		tasks.addTask(5, new EntityAIFollowParent(this, 1.25D));
		tasks.addTask(5, new EntityBee.UpdateBeehiveGoal());
		this.findBeehiveGoal = new EntityBee.FindBeehiveGoal();
		tasks.addTask(5, this.findBeehiveGoal);
		this.findFlowerGoal = new EntityBee.FindFlowerGoal();
		tasks.addTask(6, this.findFlowerGoal);
		tasks.addTask(7, new EntityBee.FindPollinationTargetGoal());
		tasks.addTask(8, new EntityBee.WanderGoal());
		tasks.addTask(9, new EntityAISwimming(this));
		targetTasks.addTask(1, (new EntityBee.AngerGoal(this))/*.setCallsForHelp(new Class[0])*/);
		targetTasks.addTask(2, new EntityBee.AttackPlayerGoal(this));
		getNavigator().setAvoidsWater(true);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("HasNoGravity", this.hasNoGravity());
		if (this.hasHive()) {
			compound.setTag("HivePos", BlockPos.writeToNBT(this.getHivePos()));
		}

		if (this.hasFlower()) {
			compound.setTag("FlowerPos", BlockPos.writeToNBT(this.getFlowerPos()));
		}

		compound.setBoolean("HasNectar", this.hasNectar());
		compound.setBoolean("HasStung", this.hasStung());
		compound.setInteger("TicksSincePollination", this.ticksWithoutNectarSinceExitingHive);
		compound.setInteger("CannotEnterHiveTicks", this.stayOutOfHiveCountdown);
		compound.setInteger("CropsGrownSincePollination", this.numCropsGrownSincePollination);
		compound.setInteger("Anger", this.getAnger());
		if (this.lastHurtBy != null) {
			compound.setString("HurtBy", this.lastHurtBy.toString());
		} else {
			compound.setString("HurtBy", "");
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		this.hivePos = null;
		if (compound.hasKey("HivePos")) {
			this.hivePos = BlockPos.readFromNBT(compound.getCompoundTag("HivePos"));
		}

		this.savedFlowerPos = null;
		if (compound.hasKey("FlowerPos")) {
			setFlowerPos(BlockPos.readFromNBT(compound.getCompoundTag("FlowerPos")));
		}

		super.readEntityFromNBT(compound);
		setNoGravity(compound.getBoolean("HasNoGravity"));
		this.setHasNectar(compound.getBoolean("HasNectar"));
		this.setHasStung(compound.getBoolean("HasStung"));
		this.setAnger(compound.getInteger("Anger"));
		this.ticksWithoutNectarSinceExitingHive = compound.getInteger("TicksSincePollination");
		this.stayOutOfHiveCountdown = compound.getInteger("CannotEnterHiveTicks");
		this.numCropsGrownSincePollination = compound.getInteger("CropsGrownSincePollination");
		String s = compound.getString("HurtBy");
		if (!s.isEmpty()) {
			this.lastHurtBy = UUID.fromString(s);
			EntityPlayer playerentity = null;
			for (EntityPlayer player : worldObj.playerEntities) {
				if (lastHurtBy.equals(player.getUniqueID())) {
					playerentity = player;
					break;
				}
			}
			this.setRevengeTarget(playerentity);
			if (playerentity != null) {
				this.attackingPlayer = playerentity;
				this.recentlyHit = this.numTicksToChaseTarget;
			}
		}
	}

	@Override
	public void moveEntityWithHeading(float moveForward, float moveStrafing) {
		double d0;
		if (this.isInWater()) {
			d0 = this.posY;
			this.moveFlying(moveForward, moveStrafing, this.isAIEnabled() ? 0.04F : 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.800000011920929D;
			this.motionY *= 0.800000011920929D;
			this.motionZ *= 0.800000011920929D;
			if (!hasNoGravity()) {
				this.motionY -= 0.02D;
			}

			if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + d0, this.motionZ)) {
				this.motionY = 0.30000001192092896D;
			}
		} else if (this.handleLavaMovement()) {
			d0 = this.posY;
			this.moveFlying(moveForward, moveStrafing, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
			if (!hasNoGravity()) {
				this.motionY -= 0.02D;
			}

			if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + d0, this.motionZ)) {
				this.motionY = 0.30000001192092896D;
			}
		} else {
			float f2 = 0.91F;

			if (this.onGround) {
				f2 = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
			}

			float f3 = 0.16277136F / (f2 * f2 * f2);
			float f4;

			if (this.onGround) {
				f4 = this.getAIMoveSpeed() * f3;
			} else {
				f4 = this.jumpMovementFactor;
			}

			this.moveFlying(moveForward, moveStrafing, f4);
			f2 = 0.91F;

			if (this.onGround) {
				f2 = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
			}

			if (this.isOnLadder()) {
				float f5 = 0.15F;
				this.motionX = MathHelper.clamp_double(this.motionX, -f5, f5);
				this.motionZ = MathHelper.clamp_double(this.motionZ, -f5, f5);

				this.fallDistance = 0.0F;

				if (this.motionY < -0.15D) {
					this.motionY = -0.15D;
				}
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);

			if (this.isCollidedHorizontally && this.isOnLadder()) {
				this.motionY = 0.2D;
			}

			if (this.worldObj.isRemote && (!this.worldObj.blockExists((int) this.posX, 0, (int) this.posZ) || !this.worldObj.getChunkFromBlockCoords((int) this.posX, (int) this.posZ).isChunkLoaded)) {
				if (this.posY > 0.0D) {
					this.motionY = -0.1D;
				} else {
					this.motionY = 0.0D;
				}
			} else if (!hasNoGravity()) {
				this.motionY -= 0.08D;
			}

			this.motionY *= moveVertical != 0 ? 0.98D : 0.5D;
			this.motionX *= f2;
			this.motionZ *= f2;
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		d0 = this.posX - this.prevPosX;
		double d1 = this.posZ - this.prevPosZ;
		float f6 = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;

		if (f6 > 1.0F) {
			f6 = 1.0F;
		}

		this.limbSwingAmount += (f6 - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}

	@Override
	public void setMoveVertical(float moveVertical) {
		this.moveVertical = moveVertical;
	}

	@Override
	public void moveFlying(float moveForward, float moveStrafing, float varFloat) {
		if (moveVertical != 0) {
			float f3 = moveForward * moveForward + moveVertical * moveVertical + moveStrafing * moveStrafing;

			if (f3 >= 1.0E-4F) {
				f3 = MathHelper.sqrt_float(f3);

				if (f3 < 1.0F) {
					f3 = 1.0F;
				}

				f3 = varFloat / f3;
				moveForward *= f3;
				moveVertical *= f3;
				moveStrafing *= f3;
				float f4 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F);
				float f5 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
				this.motionX += moveForward * f5 - moveStrafing * f4;
				this.motionY += moveVertical;
				this.motionZ += moveStrafing * f5 + moveForward * f4;
			}
		} else {
			super.moveFlying(moveForward, moveStrafing, varFloat);
		}
	}

	@Override
	public void knockBack(Entity p_70653_1_, float p_70653_2_, double p_70653_3_, double p_70653_5_) {
		if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue()) {
			this.isAirBorne = true;
			float f1 = MathHelper.sqrt_double(p_70653_3_ * p_70653_3_ + p_70653_5_ * p_70653_5_);
			float f2 = 0.4F;
			this.motionX /= 2.0D;
			this.motionZ /= 2.0D;
			this.motionX -= p_70653_3_ / (double) f1 * (double) f2;
			this.motionZ -= p_70653_5_ / (double) f1 * (double) f2;

			if (!hasNoGravity()) {
				this.motionY /= 2.0D;
				this.motionY += f2;
				if (this.motionY > 0.4000000059604645D) {
					this.motionY = 0.4000000059604645D;
				}
			} else {
				motionY += .1D;
			}
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag = entityIn.attackEntityFrom(new EntityDamageSource("sting", this), (float) ((int) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue()));
		if (flag) {
//          this.applyEnchantments(this, entityIn);
			if (entityIn instanceof EntityLivingBase) {
//              ((EntityLiving)entityIn).setBeeStingCount(((EntityLiving)entityIn).getBeeStingCount() + 1);
				int i = 0;
				if (this.worldObj.difficultySetting == EnumDifficulty.NORMAL) {
					i = 10;
				} else if (this.worldObj.difficultySetting == EnumDifficulty.HARD) {
					i = 18;
				}

				if (i > 0) {
					((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(Potion.poison.id, i * 20, 0));
				}
			}

			this.setHasStung(true);
			this.setAttackTarget(null);
			this.playSound(Reference.MCAssetVer + ":entity.bee.sting", 1.0F, 1.0F);
		}

		return flag;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (worldObj.isRemote && this.hasNectar() && this.getCropsGrownSincePollination() < 10 && this.rand.nextFloat() < 0.05F) {
			for (int i = 0; i < this.rand.nextInt(2) + 1; ++i) {
				addParticle(this.worldObj, this.posX - (double) 0.3F, this.posX + (double) 0.3F, this.posZ - (double) 0.3F, posZ + (double) 0.3F, posY + this.height * 0.5F);
			}
		}
		updateBodyPitch();
	}

	private void addParticle(World worldIn, double p_226397_2_, double p_226397_4_, double p_226397_6_, double p_226397_8_, double posY) {
		CustomParticles.spawnBeeNectarParticle(worldIn, Utils.lerp(worldIn.rand.nextDouble(), p_226397_2_, p_226397_4_), posY, Utils.lerp(worldIn.rand.nextDouble(), p_226397_6_, p_226397_8_));
	}

	private boolean isBreedingFlower(Block block, int meta) {
		return BeePlantRegistry.isFlower(block, meta);
	}

	//TODO: Maybe fix [https://bugs.mojang.com/browse/MC-168267](MC-168267) and evaluate the contents of flower pots?
	//Both flowers. For breeding the ItemBlock will have different metas, this is an issue for double plants so we handle them separately.
	public static boolean isValidFlower(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (block instanceof BlockDoublePlant && meta > 8) {
			return BeePlantRegistry.isFlower(world.getBlock(x, y - 1, z), world.getBlockMetadata(x, y - 1, z));
		}
		return BeePlantRegistry.isFlower(block, meta);
	}

	public BlockPos getFlowerPos() {
		return this.savedFlowerPos;
	}

	public boolean hasFlower() {
		return this.savedFlowerPos != null;
	}

	public void setFlowerPos(BlockPos pos) {
		this.savedFlowerPos = pos;
	}

	private boolean failedPollinatingTooLong() {
		return this.ticksWithoutNectarSinceExitingHive > 3600;
	}

	private boolean canEnterHive() {
		if (this.stayOutOfHiveCountdown <= 0 && !this.pollinateGoal.isRunning() && !this.hasStung()) {
			boolean flag = this.failedPollinatingTooLong() || this.worldObj.isRaining() || !this.worldObj.isDaytime() || this.hasNectar();
			return flag && !this.isHiveNearFire();
		} else {
			return false;
		}
	}

	public void setStayOutOfHiveCountdown(int p_226450_1_) {
		this.stayOutOfHiveCountdown = p_226450_1_;
	}

	public float getBodyPitch(float p_226455_1_) {
		return Utils.lerp(p_226455_1_, this.rollAmountO, this.rollAmount);
	}

	private void updateBodyPitch() {
		this.rollAmountO = this.rollAmount;
		if (this.isNearTarget()) {
			this.rollAmount = Math.min(1.0F, this.rollAmount + 0.2F);
		} else {
			this.rollAmount = Math.max(0.0F, this.rollAmount - 0.24F);
		}
	}

	@Override
	public void setRevengeTarget(EntityLivingBase livingBase) {
		super.setRevengeTarget(livingBase);
		if (livingBase != null) {
			this.lastHurtBy = livingBase.getUniqueID();
		}

	}

	@Override
	protected void updateAITick() {
		if (this.isInWater()) {
			++this.underWaterTicks;
		} else {
			this.underWaterTicks = 0;
		}

		if (this.underWaterTicks > 20) {
			this.attackEntityFrom(DamageSource.drown, 1.0F);
		}

		if (hasStung()) {
			++this.timeSinceSting;
			if (this.timeSinceSting % 5 == 0 && this.rand.nextInt(MathHelper.clamp_int(1200 - this.timeSinceSting, 1, 1200)) == 0) {
				this.attackEntityFrom(DamageSource.generic, this.getHealth()); //Kills the bee after it's been a while when it stings someone
			}
		}

		if (this.isAngry()) {
			int i = this.getAnger();
			this.setAnger(i - 1);
			if (i == 0 && this.getAttackTarget() != null) {
				this.setBeeAttacker(this.getAttackTarget());
			}
		}

		if (!this.hasNectar()) {
			++this.ticksWithoutNectarSinceExitingHive;
		}
	}

	public void resetTicksWithoutNectar() {
		this.ticksWithoutNectarSinceExitingHive = 0;
	}

	private boolean isHiveNearFire() {
		if (this.hivePos == null) {
			return false;
		} else {
			TileEntity tileentity = hivePos.getTileEntity(worldObj);
			return tileentity instanceof TileEntityBeeHive && ((TileEntityBeeHive) tileentity).isNearFire();
		}
	}

	public boolean isAngry() {
		return this.getAnger() > 0;
	}

	private int getAnger() {
		return this.getDataWatcher().getWatchableObjectInt(ANGER_TIME);
	}

	private void setAnger(int angerTime) {
		this.getDataWatcher().updateObject(ANGER_TIME, angerTime);
	}

	private boolean doesHiveHaveSpace(BlockPos pos) {
		TileEntity tileentity = pos.getTileEntity(worldObj);
		if (tileentity instanceof TileEntityBeeHive) {
			return !((TileEntityBeeHive) tileentity).isFullOfBees();
		} else {
			return false;
		}
	}

	public boolean hasHive() {
		return this.hivePos != null;
	}

	public BlockPos getHivePos() {
		return this.hivePos;
	}

//  protected void sendDebugPackets() {
//      super.sendDebugPackets();
//      DebugPacketSender.func_229749_a_(this);
//  }

	private int getCropsGrownSincePollination() {
		return this.numCropsGrownSincePollination;
	}

	private void resetCropCounter() {
		this.numCropsGrownSincePollination = 0;
	}

	private void addCropCounter() {
		++this.numCropsGrownSincePollination;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!this.worldObj.isRemote) {
			if (this.stayOutOfHiveCountdown > 0) {
				--this.stayOutOfHiveCountdown;
			}

			if (this.remainingCooldownBeforeLocatingNewHive > 0) {
				--this.remainingCooldownBeforeLocatingNewHive;
			}

			if (this.remainingCooldownBeforeLocatingNewFlower > 0) {
				--this.remainingCooldownBeforeLocatingNewFlower;
			}
		}

	}

	private boolean isHiveValid() {
		if (!this.hasHive()) {
			return false;
		} else {
			return hivePos.getTileEntity(worldObj) instanceof TileEntityBeeHive;
		}
	}

	public boolean hasNectar() {
		return this.getBeeFlag(8);
	}

	private void setHasNectar(boolean p_226447_1_) {
		if (p_226447_1_) {
			this.resetTicksWithoutNectar();
		}

		this.setBeeFlag(8, p_226447_1_);
	}

	public boolean hasStung() {
		return this.getBeeFlag(4);
	}

	private void setHasStung(boolean p_226449_1_) {
		this.setBeeFlag(4, p_226449_1_);
	}

	private boolean isNearTarget() {
		return this.getBeeFlag(2);
	}

    private void setNearTarget(boolean p_226452_1_) {
        this.setBeeFlag(2, p_226452_1_);
    }

	private boolean isTooFar(BlockPos pos) {
		return !this.isWithinDistance(pos, 48);
	}

	private void setBeeFlag(int flagId, boolean p_226404_2_) {
		if (p_226404_2_) {
			this.getDataWatcher().updateObject(DATA_FLAGS_ID, (byte) (this.getDataWatcher().getWatchableObjectByte(DATA_FLAGS_ID) | flagId));
		} else {
			this.getDataWatcher().updateObject(DATA_FLAGS_ID, (byte) (this.getDataWatcher().getWatchableObjectByte(DATA_FLAGS_ID) & ~flagId));
		}

	}

	private boolean getBeeFlag(int flagId) {
		return (this.getDataWatcher().getWatchableObjectByte(DATA_FLAGS_ID) & flagId) != 0;
	}

	public boolean isPollinating() {
		return getBeeFlag(0x1);
	}

	public void setPollinating(boolean isPollinating) {
		setBeeFlag(0x1, isPollinating);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttributeMap().registerAttribute(EtFuturumEntityAttributes.flyingSpeed);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
		getEntityAttribute(EtFuturumEntityAttributes.flyingSpeed).setBaseValue(0.6000000238418579D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2.0D);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return isBreedingFlower(Block.getBlockFromItem(stack.getItem()), stack.getItemDamage());
	}

	/**
	 * MCP name: {@code playStepSound}
	 */
	@Override
	protected void func_145780_a(final int x, final int y, final int z, final Block blockIn) {
	}

	@Override
	public int getTalkInterval() {
		return 0;
	}

	@Override
	public void playLivingSound() {
	}

	@Override
	protected String getHurtSound() {
		return Reference.MCAssetVer + ":entity.bee.hurt";
	}

	@Override
	protected String getDeathSound() {
		return Reference.MCAssetVer + ":entity.bee.death";
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public EntityBee createChild(EntityAgeable entity) {
		return new EntityBee(entity.worldObj);
	}

	@Override
	public float getEyeHeight() {
		return this.height * 0.5F;
	}

	@Override
	protected void fall(float distance) {
	}

	@Override
	protected void updateFallState(double distanceFallenThisTick, boolean isOnGround) {
	}

	public void onHoneyDelivered() {
		this.setHasNectar(false);
		this.resetCropCounter();
	}

	public boolean setBeeAttacker(Entity attacker) {
		this.setAnger(400 + this.rand.nextInt(400));
		if (attacker instanceof EntityLivingBase) {
			this.setRevengeTarget((EntityLivingBase) attacker);
		}

		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable()) {
			return false;
		} else {
			Entity entity = source.getEntity();
			if (!this.worldObj.isRemote && entity instanceof EntityLivingBase && (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).capabilities.isCreativeMode) && canEntityBeSeen(entity)) {
				this.pollinateGoal.cancel();
				List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(32.0D, 32.0D, 32.0D));
				for (Entity entity1 : list) {
					if (entity1 instanceof EntityBee) {
						((EntityBee) entity1).setBeeAttacker(entity);
					}
				}
			}

			return super.attackEntityFrom(source, amount);
		}
	}

	/**
	 * Get this Entity's EnumCreatureAttribute
	 */
	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ARTHROPOD;
	}

//  protected void handleFluidJump(Tag<Fluid> fluidTag) {
//      this.setMotion(this.getMotion().add(0.0D, 0.01D, 0.0D));
//  }

	private boolean isWithinDistance(BlockPos pos, int distance) {
		return Utils.getVec3FromEntity(this, 1.0F).squareDistanceTo(pos.getX(), pos.getY(), pos.getZ()) <= distance * distance;
	}

	@Override
	public void setNoGravity(boolean noGravity) {
		getDataWatcher().updateObject(NO_GRAVITY, noGravity ? (byte) 1 : (byte) 0);
	}

	@Override
	public boolean hasNoGravity() {
		return getDataWatcher().getWatchableObjectByte(NO_GRAVITY) == 1;
	}

	class AngerGoal extends EntityAIHurtByTarget {
		AngerGoal(EntityBee beeIn) {
			super(beeIn, true);
		}

		@Override
		protected boolean isSuitableTarget(EntityLivingBase targetIn, boolean p_75296_2_) {
			if (targetIn != null && this.taskOwner.canEntityBeSeen(targetIn) && (!(targetIn instanceof EntityPlayer) || !((EntityPlayer) targetIn).capabilities.isCreativeMode) && ((EntityBee) taskOwner).setBeeAttacker(targetIn)) {
				taskOwner.setAttackTarget(targetIn);
				return true;
			}
			return false;
		}
	}

	static class AttackPlayerGoal extends EntityAINearestAttackableTarget {
		AttackPlayerGoal(EntityBee beeIn) {
			super(beeIn, EntityPlayer.class, 10, true);
		}

		@Override
		public boolean shouldExecute() {
			return this.canSting() && super.shouldExecute();
		}

		@Override
		public boolean continueExecuting() {
			boolean flag = this.canSting();
			if (flag && taskOwner.getAttackTarget() != null) {
				return super.continueExecuting();
			} else {
				targetEntity = null;
				return false;
			}
		}

		private boolean canSting() {
			EntityBee beeentity = (EntityBee) this.taskOwner;
			return beeentity.isAngry() && !beeentity.hasStung();
		}
	}

	class BeeLookController extends EntityLookHelper {
		BeeLookController(EntityBee beeIn) {
			super(beeIn);
		}

		@Override
		public void onUpdateLook() {
			if (!isAngry()) {
				super.onUpdateLook();
			}
		}
	}

	private void startMovingTo(BlockPos pos) {
		Vec3 vec3d = pos.newVec3();
		int i = 0;
		BlockPos blockpos = new BlockPos(this);
		int j = (int) vec3d.yCoord - blockpos.getY();
		if (j > 2) {
			i = 4;
		} else if (j < -2) {
			i = -4;
		}

		int k = 6;
		int l = 8;
		int i1 = blockpos.manhattanDistance(pos);
		if (i1 < 15) {
			k = i1 / 2;
			l = i1 / 2;
		}

		Vec3 vec3d1 = EntityVectorUtils.func_226344_b_(this, k, l, i, vec3d, (float) Math.PI / 10F);
		if (vec3d1 != null) {
//          this.navigator.setRangeMultiplier(0.5F);
			this.getNavigator().tryMoveToXYZ(vec3d1.xCoord, vec3d1.yCoord, vec3d1.zCoord, 1.0D);
		}
	}

	private List<BlockPos> getBlocksInRange(Predicate<BlockPos> predicate, int range, boolean shuffle) {
		final BlockPos beePos = new BlockPos(EntityBee.this);
		final List<BlockPos> posList = Lists.newArrayList();

		for (int x1 = -range; x1 <= range; x1++) {
			for (int y1 = -range; y1 <= range; y1++) {
				for (int z1 = -range; z1 <= range; z1++) {
					BlockPos pos = beePos.add(x1, y1, z1);
					if (predicate.test(pos)) {
						posList.add(pos);
					}
				}
			}
		}
		if (shuffle) {
			Collections.shuffle(posList);
		} else {
			posList.sort(Comparator.comparingDouble(pos -> pos.getSquaredDistance(beePos)));
		}
		return posList;
	}

	class EnterBeehiveGoal extends EntityBee.PassiveGoal {
		private EnterBeehiveGoal() {
		}

		@Override
		public boolean canBeeStart() {
			if (EntityBee.this.hasHive() && EntityBee.this.canEnterHive() && EntityBee.this.hivePos.isWithinDistance(new BlockPos(EntityBee.this), 2.0D)) {
				TileEntity tileentity = getHivePos().getTileEntity(worldObj);
				if (tileentity instanceof TileEntityBeeHive beehivetileentity) {
					if (!beehivetileentity.isFullOfBees()) {
						return true;
					}

					EntityBee.this.hivePos = null;
				}
			}

			return false;
		}

		@Override
		public boolean canBeeContinue() {
			return false;
		}

		@Override
		public void startExecuting() {
			TileEntity tileentity = getHivePos().getTileEntity(worldObj);
			if (tileentity instanceof TileEntityBeeHive beehivetileentity) {
				beehivetileentity.tryEnterHive(EntityBee.this, EntityBee.this.hasNectar());
			}

		}
	}

	public class FindBeehiveGoal extends EntityBee.PassiveGoal {
		private int ticks = EntityBee.this.worldObj.rand.nextInt(10);
		private final List<BlockPos> possibleHives = Lists.newArrayList();
		private PathEntity path = null;

		FindBeehiveGoal() {
			setMutexBits(0);
		}

		@Override
		public boolean canBeeStart() {
			return EntityBee.this.hivePos != null && !EntityBee.this.hasHome() && EntityBee.this.canEnterHive() && !this.isCloseEnough(EntityBee.this.hivePos) && getHivePos().getBlock(worldObj) instanceof BlockBeeHive;
		}

		@Override
		public boolean canBeeContinue() {
			return this.canBeeStart();
		}

		@Override
		public void startExecuting() {
			this.ticks = 0;
			super.startExecuting();
		}

		@Override
		public void resetTask() {
			this.ticks = 0;
			EntityBee.this.getNavigator().clearPathEntity();
//          EntityBee.this.getNavigator().resetRangeMultiplier();
		}

		@Override
		public void updateTask() {
			if (EntityBee.this.hivePos != null) {
				++this.ticks;
				if (this.ticks > 600) {
					this.makeChosenHivePossibleHive();
				} else if (getNavigator().noPath()) {
					if (!EntityBee.this.isWithinDistance(EntityBee.this.hivePos, 16)) {
						if (EntityBee.this.isTooFar(EntityBee.this.hivePos)) {
							this.reset();
						} else {
							EntityBee.this.startMovingTo(EntityBee.this.hivePos);
						}
					} else {
						boolean flag = this.startMovingToFar(EntityBee.this.hivePos);
						if (!flag) {
							this.makeChosenHivePossibleHive();
						} else if (this.path != null && EntityBee.this.getNavigator().getPath().isSamePath(this.path)) {
							this.reset();
						} else {
							this.path = EntityBee.this.getNavigator().getPath();
						}

					}
				}
			}
		}

		private boolean startMovingToFar(BlockPos pos) {
//          EntityBee.this.getNavigator().setRangeMultiplier(10.0F);
			EntityBee.this.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.0D);
			return EntityBee.this.getNavigator().getPath() != null /*&& EntityBee.this.getNavigator().getPath().reachesTarget()*/;
		}

		private boolean isPossibleHive(BlockPos pos) {
			return this.possibleHives.contains(pos);
		}

		private void addPossibleHives(BlockPos pos) {
			this.possibleHives.add(pos);

			while (this.possibleHives.size() > 3) {
				this.possibleHives.remove(0);
			}

		}

		private void clearPossibleHives() {
			this.possibleHives.clear();
		}

		private void makeChosenHivePossibleHive() {
			if (EntityBee.this.hivePos != null) {
				this.addPossibleHives(EntityBee.this.hivePos);
			}

			this.reset();
		}

		private void reset() {
			EntityBee.this.hivePos = null;
			EntityBee.this.remainingCooldownBeforeLocatingNewHive = 200;
		}

		private boolean isCloseEnough(BlockPos pos) {
			if (EntityBee.this.isWithinDistance(pos, 2)) {
				return true;
			} else {
				PathEntity path = EntityBee.this.getNavigator().getPath();
				return path != null && path.isDestinationSame(Vec3.createVectorHelper(pos.getX(), pos.getY(), pos.getZ()))/* && path.reachesTarget() */ && path.isFinished();
			}
		}
	}

	public class FindFlowerGoal extends EntityBee.PassiveGoal {
		private int ticks = EntityBee.this.worldObj.rand.nextInt(10);

		FindFlowerGoal() {
			setMutexBits(0);
		}

		@Override
		public boolean canBeeStart() {
			return EntityBee.this.savedFlowerPos != null && !EntityBee.this.hasHome() && this.shouldMoveToFlower() && isValidFlower(worldObj, savedFlowerPos.getX(), savedFlowerPos.getY(), savedFlowerPos.getZ())
					&& !EntityBee.this.isWithinDistance(EntityBee.this.savedFlowerPos, 2);
		}

		@Override
		public boolean canBeeContinue() {
			return this.canBeeStart();
		}

		@Override
		public void startExecuting() {
			this.ticks = 0;
			super.startExecuting();
		}

		@Override
		public void resetTask() {
			this.ticks = 0;
			EntityBee.this.getNavigator().clearPathEntity();
//          EntityBee.this.getNavigator().resetRangeMultiplier();
		}

		@Override
		public void updateTask() {
			if (EntityBee.this.savedFlowerPos != null) {
				++this.ticks;
				if (this.ticks > 600) {
					EntityBee.this.savedFlowerPos = null;
				} else if (getNavigator().noPath()) {
					if (EntityBee.this.isTooFar(EntityBee.this.savedFlowerPos)) {
						EntityBee.this.savedFlowerPos = null;
					} else {
						EntityBee.this.startMovingTo(EntityBee.this.savedFlowerPos);
					}
				}
			}
		}

		private boolean shouldMoveToFlower() {
			return EntityBee.this.ticksWithoutNectarSinceExitingHive > 2400;
		}
	}

	class FindPollinationTargetGoal extends EntityBee.PassiveGoal {
		private FindPollinationTargetGoal() {
		}

		@Override
		public boolean canBeeStart() {
			if (EntityBee.this.getCropsGrownSincePollination() >= 10) {
				return false;
			} else if (EntityBee.this.rand.nextFloat() < 0.3F) {
				return false;
			} else {
				return EntityBee.this.hasNectar() && EntityBee.this.isHiveValid();
			}
		}

		@Override
		public boolean canBeeContinue() {
			return this.canBeeStart();
		}

		@Override
		public void updateTask() {
			if (EntityBee.this.rand.nextInt(30) == 0) {
				for (int i = 1; i <= 2; ++i) {
					int x = (int) posX;
					int y = (int) posY - i;
					int z = (int) posZ;
					Block block = worldObj.getBlock(x, y, z);
					if (BeePlantRegistry.isCrop(block) && ((IGrowable) block).func_149851_a/*canFertilize*/(worldObj, x, y, z, false) && ((IGrowable) block).func_149852_a/*shouldFertilize*/(worldObj, worldObj.rand, x, y, z)) {
						//BlockCrops, BlockStem and BlockBerryBush should use the next meta for growth stage. We can change this later if incrementing the meta doesn't work with mod crops.
						//For now we'll just increment instead of using the IGrowable grow event, since that often adds several growth stages.
						worldObj.setBlockMetadataWithNotify(x, y, z, worldObj.getBlockMetadata(x, y, z) + 1, 2);
						addCropCounter();
					}
				}
			}
		}
	}

	abstract class PassiveGoal extends EntityAIBase {
		private PassiveGoal() {
		}

		public abstract boolean canBeeStart();

		public abstract boolean canBeeContinue();

		@Override
		public boolean shouldExecute() {
			return this.canBeeStart() && !EntityBee.this.isAngry();
		}

		@Override
		public boolean continueExecuting() {
			return this.canBeeContinue() && !EntityBee.this.isAngry();
		}
	}

	class PollinateGoal extends EntityBee.PassiveGoal {
		private int pollinationTicks = 0;
		private int lastPollinationTick = 0;
		private boolean running;
		private Vec3 nextTarget;
		private int ticks = 0;

		PollinateGoal() {
			this.setMutexBits(0);
		}

		@Override
		public boolean canBeeStart() {
			if (EntityBee.this.remainingCooldownBeforeLocatingNewFlower > 0) {
				return false;
			} else if (EntityBee.this.hasNectar()) {
				return false;
			} else if (EntityBee.this.worldObj.isRaining()) {
				return false;
			} else if (EntityBee.this.rand.nextFloat() < 0.7F) {
				return false;
			} else {
				Optional<BlockPos> optional = this.getFlower();
				if (optional.isPresent()) {
					EntityBee.this.savedFlowerPos = optional.get();
					EntityBee.this.getNavigator().tryMoveToXYZ((double) EntityBee.this.savedFlowerPos.getX() + 0.5D, (double) EntityBee.this.savedFlowerPos.getY() + 0.5D, (double) EntityBee.this.savedFlowerPos.getZ() + 0.5D, 1.2F);
					return true;
				} else {
					return false;
				}
			}
		}

		@Override
		public boolean canBeeContinue() {
			if (!this.running) {
				return false;
			} else if (!EntityBee.this.hasFlower()) {
				return false;
			} else if (EntityBee.this.worldObj.isRaining()) {
				return false;
			} else if (this.completedPollination()) {
				return EntityBee.this.rand.nextFloat() < 0.2F;
			} else if (EntityBee.this.ticksExisted % 20 == 0 && !isValidFlower(worldObj, savedFlowerPos.getX(), savedFlowerPos.getY(), savedFlowerPos.getZ())) {
				EntityBee.this.savedFlowerPos = null;
				return false;
			} else {
				return true;
			}
		}

		private boolean completedPollination() {
			return this.pollinationTicks > 400;
		}

		private boolean isRunning() {
			return this.running;
		}

		private void cancel() {
			this.running = false;
		}

		@Override
		public void startExecuting() {
			this.pollinationTicks = 0;
			this.ticks = 0;
			this.lastPollinationTick = 0;
			this.running = true;
			EntityBee.this.resetTicksWithoutNectar();
		}

		@Override
		public void resetTask() {
			if (this.completedPollination()) {
				EntityBee.this.setHasNectar(true);
			}

			this.running = false;
			EntityBee.this.getNavigator().clearPathEntity();
			EntityBee.this.remainingCooldownBeforeLocatingNewFlower = 200;
		}

		@Override
		public void updateTask() {
			BlockPos beePos = new BlockPos(EntityBee.this);
			++this.ticks;
			if (this.ticks > 600) {
				EntityBee.this.savedFlowerPos = null;
			} else {
				Vec3 vec3d = EntityBee.this.savedFlowerPos.add(0.5D, 0.6F, 0.5D).newVec3();
				if (vec3d.squareDistanceTo(posX, posY, posZ) > 1.0D) {
					this.nextTarget = vec3d;
					this.moveToNextTarget();
				} else {
					if (this.nextTarget == null) {
						this.nextTarget = vec3d;
					}

					boolean flag = beePos.getSquaredDistance(this.nextTarget) <= 0.1D;
					boolean flag1 = true;
					if (!flag && this.ticks > 600) {
						EntityBee.this.savedFlowerPos = null;
					} else {
						if (flag) {
							boolean flag2 = EntityBee.this.rand.nextInt(100) == 0;
							if (flag2) {
								this.nextTarget = Vec3.createVectorHelper(vec3d.xCoord + (double) this.getRandomOffset(), vec3d.yCoord, vec3d.zCoord + (double) this.getRandomOffset());
								EntityBee.this.getNavigator().clearPathEntity();
							} else {
								flag1 = false;
							}
						}

						if (flag1) {
							this.moveToNextTarget();
						}

						++this.pollinationTicks;
						if (EntityBee.this.rand.nextFloat() < 0.05F && this.pollinationTicks > this.lastPollinationTick + 60) {
							this.lastPollinationTick = this.pollinationTicks;
							EntityBee.this.playSound(Reference.MCAssetVer + ":entity.bee.pollinate", 1.0F, 1.0F);
						}
					}
				}
			}
		}

		private void moveToNextTarget() {
			EntityBee.this.getMoveHelper().setMoveTo(this.nextTarget.xCoord, this.nextTarget.yCoord, this.nextTarget.zCoord, 0.35F);
		}

		private float getRandomOffset() {
			return (EntityBee.this.rand.nextFloat() * 2.0F - 1.0F) * 0.33333334F;
		}

		private Optional<BlockPos> getFlower() {
			return this.findFlower(5);
		}

		private Optional<BlockPos> findFlower(int distance) {
			List<BlockPos> list = getBlocksInRange(pos -> isValidFlower(worldObj, pos.getX(), pos.getY(), pos.getZ()), distance, true);
			return list.isEmpty() || list.get(0) == null ? Optional.empty() : Optional.of(list.get(0));
		}
	}

	class StingGoal extends EntityAIAttackOnCollide {
		StingGoal(EntityCreature creatureIn, Class<? extends Entity> classIn, double speedIn, boolean useLongMemory) {
			super(creatureIn, classIn, speedIn, useLongMemory);
		}

		@Override
		public boolean shouldExecute() {
			return super.shouldExecute() && EntityBee.this.isAngry() && !EntityBee.this.hasStung();
		}

		@Override
		public boolean continueExecuting() {
			return super.continueExecuting() && EntityBee.this.isAngry() && !EntityBee.this.hasStung();
		}
	}

	class UpdateBeehiveGoal extends EntityBee.PassiveGoal {
		private UpdateBeehiveGoal() {
		}

		@Override
		public boolean canBeeStart() {
			return EntityBee.this.remainingCooldownBeforeLocatingNewHive == 0 && !EntityBee.this.hasHive() && EntityBee.this.canEnterHive();
		}

		@Override
		public boolean canBeeContinue() {
			return false;
		}

		@Override
		public void startExecuting() {
			EntityBee.this.remainingCooldownBeforeLocatingNewHive = 200;
			List<BlockPos> list = getBlocksInRange(EntityBee.this::doesHiveHaveSpace, 20, false);
			if (!list.isEmpty()) {
				for (BlockPos blockpos : list) {
					if (!EntityBee.this.findBeehiveGoal.isPossibleHive(blockpos)) {
						EntityBee.this.hivePos = blockpos;
						return;
					}
				}

				EntityBee.this.findBeehiveGoal.clearPossibleHives();
				EntityBee.this.hivePos = list.get(0);
			}
		}
	}

	class WanderGoal extends EntityAIBase {
		WanderGoal() {
			this.setMutexBits(0);
		}

		@Override
		public boolean shouldExecute() {
			return EntityBee.this.getNavigator().noPath() && EntityBee.this.rand.nextInt(10) == 0;
		}

		@Override
		public boolean continueExecuting() {
			return !getNavigator().noPath();
		}

		@Override
		public void startExecuting() {
			Vec3 vec3d = this.getRandomLocation();
			if (vec3d != null) {
				EntityBee.this.getNavigator().setPath(EntityBee.this.getNavigator().getPathToXYZ(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord), 1.0D);
			}
		}

		@Nullable
		private Vec3 getRandomLocation() {
			Vec3 vec3d;
			if (isHiveValid() && !isWithinDistance(hivePos, 40)) {
				Vec3 vec3d1 = hivePos.newVec3();
				vec3d = vec3d1.subtract(Utils.getVec3FromEntity(EntityBee.this, 1.0F)).normalize();
			} else {
				vec3d = getLook(0.0F);
			}

			Vec3 vec3d2 = EntityVectorUtils.findAirTarget(EntityBee.this, 8, 7, vec3d, ((float) Math.PI / 2F), 2, 1);
			return vec3d2 != null ? vec3d2 : EntityVectorUtils.findGroundTarget(EntityBee.this, 8, 4, -2, vec3d, (float) Math.PI / 2F);
		}
	}

	class TemptBeeWithFlowerGoal extends EntityAITempt {

		public TemptBeeWithFlowerGoal(EntityCreature p_i45316_1_, double p_i45316_2_, boolean p_i45316_5_) {
			super(p_i45316_1_, p_i45316_2_, null, p_i45316_5_);
		}

		@Override
		public boolean shouldExecute() {
			if (this.delayTemptCounter > 0) {
				--this.delayTemptCounter;
				return false;
			} else {
				this.temptingPlayer = this.temptedEntity.worldObj.getClosestPlayerToEntity(this.temptedEntity, getEntityAttribute(EtFuturumEntityAttributes.flyingSpeed).getAttributeValue());

				if (this.temptingPlayer == null) {
					return false;
				} else {
					ItemStack itemstack = this.temptingPlayer.getCurrentEquippedItem();
					return itemstack != null && isBreedingFlower(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());
				}
			}
		}
	}
}
