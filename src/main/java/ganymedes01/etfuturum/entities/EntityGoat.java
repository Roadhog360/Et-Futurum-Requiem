package ganymedes01.etfuturum.entities;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.Tags;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import roadhog360.hogutils.api.hogtags.helpers.BlockTags;

import java.util.List;

public class EntityGoat extends EntityAnimal {

	private static final int GOAT_FLAGS = 18;
	private static final int SCREAMING_FLAG = 1;
	private static final int LEFT_HORN_FLAG = 2;
	private static final int RIGHT_HORN_FLAG = 4;
	private static final byte LOWER_HEAD_EVENT = 58;
	private static final byte RAISE_HEAD_EVENT = 59;
	private static final int RAM_MIN_DISTANCE = 4;
	private static final int RAM_MAX_DISTANCE = 7;
	private static final int RAM_PREPARE_TICKS = 20;

	private boolean loweringHead;
	private int lowerHeadTick;
	private boolean wasChild;
	private int longJumpCooldown;
	private int ramCooldown;

	public EntityGoat(World world) {
		super(world);
		setSize(0.9F, 1.3F);
		getNavigator().setAvoidsWater(true);
		longJumpCooldown = nextLongJumpCooldown();
		ramCooldown = nextRamCooldown();

		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityGoat.AIRamTarget());
		tasks.addTask(2, new EntityGoat.AILongJump());
		tasks.addTask(3, new EntityAIPanic(this, 2.0D));
		tasks.addTask(4, new EntityAIMate(this, 1.0D));
		tasks.addTask(5, new EntityAITempt(this, 1.25D, Items.wheat, false));
		tasks.addTask(6, new EntityAIFollowParent(this, 1.25D));
		tasks.addTask(7, new EntityAIWander(this, 1.0D));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(9, new EntityAILookIdle(this));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(GOAT_FLAGS, (byte) (LEFT_HORN_FLAG | RIGHT_HORN_FLAG));
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2D);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2.0D);
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData livingData) {
		livingData = super.onSpawnWithEgg(livingData);
		setScreamingGoat(rand.nextDouble() < 0.02D);
		updateHornsForAge(true);

		if (!isChild() && rand.nextFloat() < 0.1F) {
			if (rand.nextBoolean()) {
				setLeftHorn(false);
			} else {
				setRightHorn(false);
			}
		}

		wasChild = isChild();
		return livingData;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable mate) {
		EntityGoat goat = new EntityGoat(worldObj);
		EntityAgeable parent = rand.nextBoolean() ? this : mate;
		goat.setScreamingGoat(parent instanceof EntityGoat && ((EntityGoat) parent).isScreamingGoat() || rand.nextDouble() < 0.02D);
		goat.removeHorns();
		return goat;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack != null && stack.getItem() == Items.wheat;
	}

	@Override
	public boolean interact(EntityPlayer player) {
		ItemStack stack = player.inventory.getCurrentItem();
		if (stack != null && stack.getItem() == Items.bucket && !isChild()) {
			playSound(getMilkingSound(), 1.0F, 1.0F);
			if (!player.capabilities.isCreativeMode) {
				--stack.stackSize;
				ItemStack milk = new ItemStack(Items.milk_bucket);
				if (stack.stackSize <= 0) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, milk);
				} else if (!player.inventory.addItemStackToInventory(milk)) {
					player.dropPlayerItemWithRandomChoice(milk, false);
				}
			}
			return true;
		}

		boolean eating = isBreedingItem(stack);
		boolean result = super.interact(player);
		if (result && eating) {
			playSound(getEatSound(), 1.0F, 0.8F + rand.nextFloat() * 0.4F);
		}
		return result;
	}

	@Override
	public void onLivingUpdate() {
		if (loweringHead) {
			++lowerHeadTick;
		} else {
			lowerHeadTick -= 2;
		}
		lowerHeadTick = MathHelper.clamp_int(lowerHeadTick, 0, 20);

		if (!worldObj.isRemote) {
			if (longJumpCooldown > 0) {
				--longJumpCooldown;
			}
			if (ramCooldown > 0) {
				--ramCooldown;
			}
			if (wasChild != isChild()) {
				updateHornsForAge(true);
				wasChild = isChild();
			}
		}

		super.onLivingUpdate();
	}

	@Override
	public void handleHealthUpdate(byte id) {
		if (id == LOWER_HEAD_EVENT) {
			loweringHead = true;
		} else if (id == RAISE_HEAD_EVENT) {
			loweringHead = false;
		} else {
			super.handleHealthUpdate(id);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("IsScreamingGoat", isScreamingGoat());
		nbt.setBoolean("HasLeftHorn", hasLeftHorn());
		nbt.setBoolean("HasRightHorn", hasRightHorn());
		nbt.setInteger("LongJumpCooldown", longJumpCooldown);
		nbt.setInteger("RamCooldown", ramCooldown);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setScreamingGoat(nbt.getBoolean("IsScreamingGoat"));
		setLeftHorn(nbt.getBoolean("HasLeftHorn"));
		setRightHorn(nbt.getBoolean("HasRightHorn"));
		longJumpCooldown = nbt.hasKey("LongJumpCooldown") ? nbt.getInteger("LongJumpCooldown") : nextLongJumpCooldown();
		ramCooldown = nbt.hasKey("RamCooldown") ? nbt.getInteger("RamCooldown") : nextRamCooldown();
		wasChild = isChild();
		setAttackDamageForAge();
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		return entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
	}

	@Override
	protected void fall(float distance) {
		super.fall(Math.max(0.0F, distance - 10.0F));
	}

	@Override
	protected String getLivingSound() {
		return isScreamingGoat() ? Tags.MC_ASSET_VER + ":entity.goat.screaming.ambient" : Tags.MC_ASSET_VER + ":entity.goat.ambient";
	}

	@Override
	protected String getHurtSound() {
		return isScreamingGoat() ? Tags.MC_ASSET_VER + ":entity.goat.screaming.hurt" : Tags.MC_ASSET_VER + ":entity.goat.hurt";
	}

	@Override
	protected String getDeathSound() {
		return isScreamingGoat() ? Tags.MC_ASSET_VER + ":entity.goat.screaming.death" : Tags.MC_ASSET_VER + ":entity.goat.death";
	}

	@Override
	protected void func_145780_a(int x, int y, int z, Block block) {
		playSound(Tags.MC_ASSET_VER + ":entity.goat.step", 0.15F, 1.0F);
	}

	@Override
	protected void dropFewItems(boolean hitRecently, int fortune) {
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEFINED;
	}

	@Override
	public void setRotationYawHead(float yaw) {
		float diff = MathHelper.wrapAngleTo180_float(yaw - renderYawOffset);
		diff = MathHelper.clamp_float(diff, -15.0F, 15.0F);
		super.setRotationYawHead(renderYawOffset + diff);
	}

	@Override
	public boolean getCanSpawnHere() {
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(boundingBox.minY);
		int z = MathHelper.floor_double(posZ);
		Block block = worldObj.getBlock(x, y - 1, z);
		int meta = worldObj.getBlockMetadata(x, y - 1, z);
		return worldObj.getBlockLightValue(x, y, z) > 8
				&& isGoatSpawnableOn(block, meta)
				&& worldObj.checkNoEntityCollision(boundingBox)
				&& worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty()
				&& !worldObj.isAnyLiquid(boundingBox);
	}

	private String getEatSound() {
		return isScreamingGoat() ? Tags.MC_ASSET_VER + ":entity.goat.screaming.eat" : Tags.MC_ASSET_VER + ":entity.goat.eat";
	}

	private String getMilkingSound() {
		return isScreamingGoat() ? Tags.MC_ASSET_VER + ":entity.goat.screaming.milk" : Tags.MC_ASSET_VER + ":entity.goat.milk";
	}

	private String getLongJumpSound() {
		return isScreamingGoat() ? Tags.MC_ASSET_VER + ":entity.goat.screaming.long_jump" : Tags.MC_ASSET_VER + ":entity.goat.long_jump";
	}

	private String getPrepareRamSound() {
		return isScreamingGoat() ? Tags.MC_ASSET_VER + ":entity.goat.screaming.prepare_ram" : Tags.MC_ASSET_VER + ":entity.goat.prepare_ram";
	}

	private String getRamImpactSound() {
		return isScreamingGoat() ? Tags.MC_ASSET_VER + ":entity.goat.screaming.ram_impact" : Tags.MC_ASSET_VER + ":entity.goat.ram_impact";
	}

	public boolean isScreamingGoat() {
		return getGoatFlag(SCREAMING_FLAG);
	}

	public void setScreamingGoat(boolean screaming) {
		setGoatFlag(SCREAMING_FLAG, screaming);
	}

	public boolean hasLeftHorn() {
		return getGoatFlag(LEFT_HORN_FLAG);
	}

	private void setLeftHorn(boolean hasHorn) {
		setGoatFlag(LEFT_HORN_FLAG, hasHorn);
	}

	public boolean hasRightHorn() {
		return getGoatFlag(RIGHT_HORN_FLAG);
	}

	private void setRightHorn(boolean hasHorn) {
		setGoatFlag(RIGHT_HORN_FLAG, hasHorn);
	}

	public void addHorns() {
		setLeftHorn(true);
		setRightHorn(true);
	}

	public void removeHorns() {
		setLeftHorn(false);
		setRightHorn(false);
	}

	public boolean dropHorn() {
		boolean left = hasLeftHorn();
		boolean right = hasRightHorn();
		if (!left && !right) {
			return false;
		}

		if (!left) {
			setRightHorn(false);
		} else if (!right) {
			setLeftHorn(false);
		} else if (rand.nextBoolean()) {
			setLeftHorn(false);
		} else {
			setRightHorn(false);
		}

		EntityItem item = new EntityItem(worldObj, posX, posY + 0.5D, posZ, createHorn());
		item.motionX = MathHelper.getRandomDoubleInRange(rand, -0.2D, 0.2D);
		item.motionY = MathHelper.getRandomDoubleInRange(rand, 0.3D, 0.7D);
		item.motionZ = MathHelper.getRandomDoubleInRange(rand, -0.2D, 0.2D);
		worldObj.spawnEntityInWorld(item);
		return true;
	}

	public ItemStack createHorn() {
		int groupOffset = isScreamingGoat() ? 4 : 0;
		int variant = (getUniqueID().hashCode() & Integer.MAX_VALUE) % 4;
		return ModItems.GOAT_HORN.newItemStack(1, groupOffset + variant);
	}

	public float getRammingXHeadRot() {
		return (float) lowerHeadTick / 20.0F * 30.0F * (float) Math.PI / 180.0F;
	}

	private boolean getGoatFlag(int flag) {
		return (dataWatcher.getWatchableObjectByte(GOAT_FLAGS) & flag) != 0;
	}

	private void setGoatFlag(int flag, boolean value) {
		byte flags = dataWatcher.getWatchableObjectByte(GOAT_FLAGS);
		if (value) {
			flags = (byte) (flags | flag);
		} else {
			flags = (byte) (flags & ~flag);
		}
		dataWatcher.updateObject(GOAT_FLAGS, flags);
	}

	private void setLoweringHead(boolean lowering) {
		if (loweringHead != lowering) {
			loweringHead = lowering;
			worldObj.setEntityState(this, lowering ? LOWER_HEAD_EVENT : RAISE_HEAD_EVENT);
		}
	}

	private void updateHornsForAge(boolean resetAdultHorns) {
		setAttackDamageForAge();
		if (isChild()) {
			removeHorns();
		} else if (resetAdultHorns) {
			addHorns();
		}
	}

	private void setAttackDamageForAge() {
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(isChild() ? 1.0D : 2.0D);
	}

	private int nextLongJumpCooldown() {
		return 600 + rand.nextInt(601);
	}

	private int nextRamCooldown() {
		return isScreamingGoat() ? 100 + rand.nextInt(201) : 600 + rand.nextInt(5401);
	}

	private int nextFailedRamCooldown() {
		return isScreamingGoat() ? 100 : 600;
	}

	private static boolean isGoatSpawnableOn(Block block, int meta) {
		return BlockTags.hasTag(block, meta, "minecraft:goats_spawnable_on")
				|| block == Blocks.grass || block == Blocks.stone || block == Blocks.snow || block == Blocks.snow_layer || block == Blocks.packed_ice || block == Blocks.gravel;
	}

	private static boolean snapsGoatHorn(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		return BlockTags.hasTag(block, meta, "minecraft:snaps_goat_horn")
				|| block == Blocks.log || block == Blocks.log2 || block == Blocks.stone || block == Blocks.packed_ice
				|| block == Blocks.iron_ore || block == Blocks.coal_ore || block == Blocks.emerald_ore || block == ModBlocks.COPPER_ORE.get();
	}

	private class AILongJump extends EntityAIBase {

		private int targetX;
		private int targetY;
		private int targetZ;
		private int prepareTicks;
		private boolean jumped;

		private AILongJump() {
			setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			if (longJumpCooldown > 0 || !onGround || isInWater() || getAttackTarget() != null || isInLove()) {
				return false;
			}
			return chooseJumpTarget();
		}

		@Override
		public boolean continueExecuting() {
			return !jumped && prepareTicks < 45 || jumped && !onGround;
		}

		@Override
		public void startExecuting() {
			prepareTicks = 0;
			jumped = false;
			getNavigator().clearPathEntity();
		}

		@Override
		public void resetTask() {
			longJumpCooldown = nextLongJumpCooldown();
		}

		@Override
		public void updateTask() {
			getLookHelper().setLookPosition(targetX + 0.5D, targetY + 0.5D, targetZ + 0.5D, 30.0F, 30.0F);
			if (++prepareTicks >= 40 && !jumped) {
				double dx = targetX + 0.5D - posX;
				double dz = targetZ + 0.5D - posZ;
				double horizontal = MathHelper.sqrt_double(dx * dx + dz * dz);
				if (horizontal > 0.001D) {
					double dy = targetY - posY;
					motionX = dx / horizontal * Math.min(1.2D, 0.45D + horizontal * 0.08D);
					motionZ = dz / horizontal * Math.min(1.2D, 0.45D + horizontal * 0.08D);
					motionY = MathHelper.clamp_double(0.55D + dy * 0.12D, 0.35D, 1.0D);
					isAirBorne = true;
					playSound(getLongJumpSound(), 1.0F, 1.0F);
				}
				jumped = true;
			}
		}

		private boolean chooseJumpTarget() {
			int originX = MathHelper.floor_double(posX);
			int originY = MathHelper.floor_double(boundingBox.minY);
			int originZ = MathHelper.floor_double(posZ);

			for (int i = 0; i < 20; ++i) {
				int x = originX + rand.nextInt(11) - 5;
				int y = originY + rand.nextInt(11) - 5;
				int z = originZ + rand.nextInt(11) - 5;
				if ((x != originX || z != originZ) && isValidLanding(x, y, z)) {
					targetX = x;
					targetY = y;
					targetZ = z;
					return true;
				}
			}

			longJumpCooldown = nextLongJumpCooldown() / 2;
			return false;
		}

		private boolean isValidLanding(int x, int y, int z) {
			Block below = worldObj.getBlock(x, y - 1, z);
			return below.isSideSolid(worldObj, x, y - 1, z, ForgeDirection.UP)
					&& worldObj.isAirBlock(x, y, z)
					&& worldObj.isAirBlock(x, y + 1, z)
					&& worldObj.getBlock(x, y, z).getMaterial() != Material.water;
		}
	}

	private class AIRamTarget extends EntityAIBase {

		private EntityLivingBase target;
		private int startX;
		private int startY;
		private int startZ;
		private int targetX;
		private int targetY;
		private int targetZ;
		private double ramTargetX;
		private double ramTargetZ;
		private int prepareTicks;
		private int timeout;
		private boolean charging;
		private double chargeDirX;
		private double chargeDirZ;

		private AIRamTarget() {
			setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			if (ramCooldown > 0 || isInLove()) {
				return false;
			}
			if (findRamCandidate()) {
				return true;
			}
			ramCooldown = nextFailedRamCooldown();
			return false;
		}

		@Override
		public boolean continueExecuting() {
			return target != null && target.isEntityAlive() && timeout < (charging ? 200 : 160);
		}

		@Override
		public void startExecuting() {
			prepareTicks = 0;
			timeout = 0;
			charging = false;
			setLoweringHead(false);
			getNavigator().tryMoveToXYZ(startX + 0.5D, startY, startZ + 0.5D, 1.25D);
		}

		@Override
		public void resetTask() {
			if (target != null) {
				finishRam(false);
			}
		}

		@Override
		public void updateTask() {
			++timeout;
			if (target == null) {
				return;
			}

			getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
			if (!charging) {
				if (hasTargetMovedBlock()) {
					if (!chooseStartFor(target)) {
						finishRam(true);
						return;
					}
					prepareTicks = 0;
					setLoweringHead(false);
					getNavigator().tryMoveToXYZ(startX + 0.5D, startY, startZ + 0.5D, 1.25D);
					return;
				}

				if (getDistanceSq(startX + 0.5D, startY, startZ + 0.5D) <= 1.0D) {
					getNavigator().clearPathEntity();
					setLoweringHead(true);
					if (++prepareTicks >= RAM_PREPARE_TICKS) {
						beginCharge();
					}
				} else if (getNavigator().noPath()) {
					finishRam(true);
				}
				return;
			}

			getNavigator().clearPathEntity();
			faceChargeDirection();
			motionX += chargeDirX * 0.12D;
			motionZ += chargeDirZ * 0.12D;
			limitHorizontalChargeSpeed();

			if (getDistanceSqToEntity(target) <= 2.25D || boundingBox.expand(0.2D, 0.0D, 0.2D).intersectsWith(target.boundingBox)) {
				hitTarget(target);
			} else if (hasRammedHornBreakingBlock()) {
				playSound(getRamImpactSound(), 1.0F, 1.0F);
				if (dropHorn()) {
					playSound(Tags.MC_ASSET_VER + ":entity.goat.horn_break", 1.0F, 1.0F);
				}
				finishRam(false);
			} else if (hasPassedTarget()) {
				finishRam(false);
			}
		}

		private void beginCharge() {
			charging = true;
			playSound(getPrepareRamSound(), 1.0F, getSoundPitch());
			double dx = ramTargetX - posX;
			double dz = ramTargetZ - posZ;
			double length = MathHelper.sqrt_double(dx * dx + dz * dz);
			if (length < 0.001D) {
				finishRam(true);
				return;
			}
			chargeDirX = dx / length;
			chargeDirZ = dz / length;
			getNavigator().clearPathEntity();
			faceChargeDirection();
			motionX = chargeDirX * 0.65D;
			motionZ = chargeDirZ * 0.65D;
		}

		private void faceChargeDirection() {
			float yaw = (float) (Math.atan2(chargeDirZ, chargeDirX) * 180.0D / Math.PI) - 90.0F;
			rotationYaw = yaw;
			renderYawOffset = yaw;
			rotationYawHead = yaw;
		}

		private void limitHorizontalChargeSpeed() {
			double speed = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			if (speed > 0.9D) {
				motionX = motionX / speed * 0.9D;
				motionZ = motionZ / speed * 0.9D;
			}
		}

		private boolean hasPassedTarget() {
			double dx = ramTargetX - posX;
			double dz = ramTargetZ - posZ;
			return dx * chargeDirX + dz * chargeDirZ < -0.5D;
		}

		private boolean hasTargetMovedBlock() {
			return MathHelper.floor_double(target.posX) != targetX
					|| MathHelper.floor_double(target.boundingBox.minY) != targetY
					|| MathHelper.floor_double(target.posZ) != targetZ;
		}

		private void hitTarget(EntityLivingBase living) {
			living.attackEntityFrom(DamageSource.causeMobDamage(EntityGoat.this), isChild() ? 1.0F : 2.0F);
			double force = isChild() ? 0.5D : 1.25D;
			living.addVelocity(chargeDirX * force, 0.35D, chargeDirZ * force);
			playSound(getRamImpactSound(), 1.0F, 1.0F);
			finishRam(false);
		}

		private void finishRam(boolean failed) {
			setLoweringHead(false);
			getNavigator().clearPathEntity();
			ramCooldown = failed ? nextFailedRamCooldown() : nextRamCooldown();
			target = null;
		}

		private boolean findRamCandidate() {
			EntityLivingBase nearest = null;
			double nearestDistance = Double.MAX_VALUE;
			List<EntityLivingBase> list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, boundingBox.expand(10.0D, 4.0D, 10.0D));
			for (EntityLivingBase living : list) {
				if (!canRam(living)) {
					continue;
				}
				double distance = getDistanceSqToEntity(living);
				if (distance < nearestDistance && chooseStartFor(living)) {
					nearest = living;
					nearestDistance = distance;
				}
			}

			target = nearest;
			return target != null;
		}

		private boolean canRam(EntityLivingBase living) {
			if (living == EntityGoat.this || living instanceof EntityGoat || living instanceof EntityArmourStand || !living.isEntityAlive()) {
				return false;
			}
			if (living instanceof EntityPlayer && ((EntityPlayer) living).capabilities.isCreativeMode) {
				return false;
			}
			return getEntitySenses().canSee(living);
		}

		private boolean chooseStartFor(EntityLivingBase living) {
			int tx = MathHelper.floor_double(living.posX);
			int ty = MathHelper.floor_double(living.boundingBox.minY);
			int tz = MathHelper.floor_double(living.posZ);
			int bestX = 0;
			int bestY = 0;
			int bestZ = 0;
			double bestDistance = Double.MAX_VALUE;
			int[][] dirs = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

			for (int[] dir : dirs) {
				int sx = tx;
				int sz = tz;
				int lastValidX = tx;
				int lastValidZ = tz;
				int distance = 0;
				for (int i = 0; i < RAM_MAX_DISTANCE; ++i) {
					sx += dir[0];
					sz += dir[1];
					if (!isWalkable(sx, ty, sz)) {
						break;
					}
					lastValidX = sx;
					lastValidZ = sz;
					++distance;
				}

				if (distance >= RAM_MIN_DISTANCE) {
					double distanceToStart = getDistanceSq(lastValidX + 0.5D, ty, lastValidZ + 0.5D);
					PathEntity path = getNavigator().getPathToXYZ(lastValidX + 0.5D, ty, lastValidZ + 0.5D);
					if (path != null && distanceToStart < bestDistance) {
						bestX = lastValidX;
						bestY = ty;
						bestZ = lastValidZ;
						bestDistance = distanceToStart;
					}
				}
			}

			if (bestDistance < Double.MAX_VALUE) {
				startX = bestX;
				startY = bestY;
				startZ = bestZ;
				targetX = tx;
				targetY = ty;
				targetZ = tz;
				setRamTargetEdge();
				return true;
			}
			return false;
		}

		private void setRamTargetEdge() {
			ramTargetX = targetX + 0.5D + 0.5D * Math.signum(targetX - startX);
			ramTargetZ = targetZ + 0.5D + 0.5D * Math.signum(targetZ - startZ);
		}

		private boolean isWalkable(int x, int y, int z) {
			return worldObj.getBlock(x, y - 1, z).isSideSolid(worldObj, x, y - 1, z, ForgeDirection.UP)
					&& worldObj.isAirBlock(x, y, z)
					&& worldObj.isAirBlock(x, y + 1, z);
		}

		private boolean hasRammedHornBreakingBlock() {
			double horizontal = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			double dirX = horizontal < 0.001D ? chargeDirX : motionX / horizontal;
			double dirZ = horizontal < 0.001D ? chargeDirZ : motionZ / horizontal;
			int x = MathHelper.floor_double(posX + dirX * 0.8D);
			int y = MathHelper.floor_double(boundingBox.minY + 0.5D);
			int z = MathHelper.floor_double(posZ + dirZ * 0.8D);
			return snapsGoatHorn(worldObj, x, y, z) || snapsGoatHorn(worldObj, x, y + 1, z);
		}
	}
}
