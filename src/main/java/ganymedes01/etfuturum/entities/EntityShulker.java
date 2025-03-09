package ganymedes01.etfuturum.entities;

import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.configuration.configs.ConfigTweaks;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.recipes.ModRecipes;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public class EntityShulker extends EntityGolem implements IMob {

	private static final int ATTACHED_FACE = 12;
	private static final int PEEK_TICK = 13;
	private static final int ATTACHED_X = 14;
	private static final int ATTACHED_Y = 15;
	private static final int ATTACHED_Z = 16;
	private static final int COLOR = 17;
	private float currentPeekAmount0;
	private float currentPeekAmount;
	private BlockPos currentAttachmentPosition;
	private int clientSideTeleportInterpolation;

	public EntityShulker(World p_i1686_1_) {
		super(p_i1686_1_);
		this.tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(4, new EntityShulker.AIAttack());
		this.tasks.addTask(7, new EntityShulker.AIPeek());
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, new EntityShulker.AIAttackNearest(this));
		this.targetTasks.addTask(3, new EntityShulker.AIDefenseAttack(this));
		this.setSize(1, 1);
		persistenceRequired = true;
	}

	@Override
	protected boolean canDespawn() {
		return !isNoDespawnRequired();
	}

	@Override
	protected float func_110146_f(float p_110146_1_, float p_110146_2_) {
		return 180;
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData p_110161_1_) {
		this.renderYawOffset = 180.0F;
		this.prevRenderYawOffset = 180.0F;
		this.rotationYaw = 180.0F;
		this.prevRotationYaw = 180.0F;
		this.rotationYawHead = 180.0F;
		this.prevRotationYawHead = 180.0F;
		return super.onSpawnWithEgg(p_110161_1_);
	}

	@Override
	public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
		super.setLocationAndAngles(x, y, z, yaw, pitch);
		if (getAge() == 0) {
			int xPos = MathHelper.floor_double(posX);
			int yPos = MathHelper.floor_double(posY);
			int zPos = MathHelper.floor_double(posZ);
			this.setAttachmentPos(new BlockPos(xPos, yPos, zPos));
			for (EnumFacing enumfacing1 : Utils.ENUM_FACING_VALUES) {
				if (this.worldObj.isBlockNormalCubeDefault(xPos + enumfacing1.getFrontOffsetX(), yPos + enumfacing1.getFrontOffsetY(), zPos + enumfacing1.getFrontOffsetZ(), false)) {
					this.getDataWatcher().updateObject(ATTACHED_FACE, (byte) enumfacing1.ordinal());
					break;
				}
			}
		}
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataWatcher().addObject(ATTACHED_FACE, (byte) 0);
		this.getDataWatcher().addObject(PEEK_TICK, (byte) 0);
		this.getDataWatcher().addObject(ATTACHED_X, 0);
		this.getDataWatcher().addObject(ATTACHED_Y, -1);
		this.getDataWatcher().addObject(ATTACHED_Z, 0);
		this.getDataWatcher().addObject(COLOR, (byte) 16);
	}

	public void setColor(byte color) {
		getDataWatcher().updateObject(COLOR, (byte) Math.min(Math.max(color, 0), 16));
	}

	public byte getColor() {
		return getDataWatcher().getWatchableObjectByte(COLOR);
	}

	@Override
	protected String getLivingSound() {
		return Reference.MCAssetVer + ":entity.shulker.ambient";
	}

	@Override
	protected String getHurtSound() {
		return Reference.MCAssetVer + ":entity.shulker.hurt" + (isClosed() ? "_closed" : "");
	}

	@Override
	protected String getDeathSound() {
		return Reference.MCAssetVer + ":entity.shulker.death";
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void playLivingSound() {
		if (!this.isClosed()) {
			super.playLivingSound();
		}
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
	}

//    protected EntityBodyHelper createBodyHelper()
//    { //TODO: Check what the fuck this is supposed to do
//        return new EntityShulker.BodyHelper(this);
//    }

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.getDataWatcher().updateObject(ATTACHED_FACE, (byte) EnumFacing.getFront(compound.getByte("AttachFace")).ordinal());
		this.getDataWatcher().updateObject(PEEK_TICK, compound.getByte("Peek"));

		this.setAttachmentPos(new BlockPos(compound.getInteger("APX"), compound.getInteger("APY"), compound.getInteger("APZ")));
		this.setColor(compound.getByte("Color"));
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setByte("AttachFace", this.getDataWatcher().getWatchableObjectByte(ATTACHED_FACE));
		compound.setByte("Peek", this.getDataWatcher().getWatchableObjectByte(PEEK_TICK));

		compound.setInteger("APX", this.getDataWatcher().getWatchableObjectInt(ATTACHED_X));
		compound.setInteger("APY", this.getDataWatcher().getWatchableObjectInt(ATTACHED_Y));
		compound.setInteger("APZ", this.getDataWatcher().getWatchableObjectInt(ATTACHED_Z));
		compound.setByte("Color", getDataWatcher().getWatchableObjectByte(COLOR));
	}

	public BlockPos getAttachmentPos() {
		return new BlockPos(getDataWatcher().getWatchableObjectInt(ATTACHED_X), getDataWatcher().getWatchableObjectInt(ATTACHED_Y), getDataWatcher().getWatchableObjectInt(ATTACHED_Z));
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		super.onUpdate();
		BlockPos blockpos = getAttachmentPos();

		if (getColor() != 16 && worldObj.getBlock(blockpos.getX(), blockpos.getY(), blockpos.getZ()).getMaterial() == Material.water) {
			setColor((byte) 16);
		}

		if (worldObj.difficultySetting == EnumDifficulty.PEACEFUL && this.getAttackTarget() instanceof EntityPlayer) {
			this.setAttackTarget(null);
		}
		if (blockpos.getY() == -1 && !this.worldObj.isRemote) {
			blockpos = new BlockPos(this);
			this.getDataWatcher().updateObject(ATTACHED_X, blockpos.getX());
			this.getDataWatcher().updateObject(ATTACHED_Y, blockpos.getY());
			this.getDataWatcher().updateObject(ATTACHED_Z, blockpos.getZ());
		}

		if (this.isRiding()) {
			blockpos = null;
			float f = this.ridingEntity.rotationYaw;
			this.rotationYaw = f;
			this.renderYawOffset = f;
			this.prevRenderYawOffset = f;
			this.clientSideTeleportInterpolation = 0;
		} else if (!this.worldObj.isRemote) {
			Block iblockstate = this.worldObj.getBlock(blockpos.getX(), blockpos.getY(), blockpos.getZ());

			if (iblockstate != Blocks.air) {
//                if (iblockstate == Blocks.piston_extension)
//                {
//                    EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(BlockPistonBase.FACING);
//                    blockpos = blockpos.offset(enumfacing);
//                    this.setAttachmentPos(blockpos);
//                }
//                else if (iblockstate == Blocks.piston_head)
//                {
//                    EnumFacing enumfacing3 = (EnumFacing)iblockstate.getValue(BlockPistonExtension.FACING);
//                    blockpos = blockpos.offset(enumfacing3);
//                    this.setAttachmentPos(blockpos);
//                }
				if (iblockstate == Blocks.piston_extension) {
					EnumFacing enumfacing = EnumFacing.getFront(worldObj.getBlockMetadata(blockpos.getX(), blockpos.getY(), blockpos.getZ()));
					blockpos = blockpos.offset(enumfacing);
					this.setAttachmentPos(blockpos);
				} else if (iblockstate == Blocks.piston_head) {
					EnumFacing enumfacing3 = EnumFacing.getFront(worldObj.getBlockMetadata(blockpos.getX(), blockpos.getY(), blockpos.getZ()));
					blockpos = blockpos.offset(enumfacing3);
					this.setAttachmentPos(blockpos);
				} else {
					this.tryTeleportToNewPosition();
				}
			}

			BlockPos blockpos1 = blockpos.offset(this.getAttachmentFacing());

			if (!this.worldObj.isBlockNormalCubeDefault(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ(), false)) {
				boolean flag = false;


				for (EnumFacing enumfacing1 : Utils.ENUM_FACING_VALUES) {
					if (this.worldObj.isBlockNormalCubeDefault(blockpos.getX() + enumfacing1.getFrontOffsetX(), blockpos.getY() + enumfacing1.getFrontOffsetY(),
							blockpos.getZ() + enumfacing1.getFrontOffsetZ(), false)) {
						this.getDataWatcher().updateObject(ATTACHED_FACE, (byte) enumfacing1.ordinal());
						flag = true;
						break;
					}
				}

				if (!flag) {
					this.tryTeleportToNewPosition();
				}
			}

			int ordinal = getAttachmentFacing().ordinal();
			int opposite = ordinal = ordinal % 2 == 0 ? ordinal + 1 : ordinal - 1;
			BlockPos blockpos2 = blockpos.offset(EnumFacing.getFront(opposite));

			if (this.worldObj.isBlockNormalCubeDefault(blockpos2.getX(), blockpos2.getY(), blockpos2.getZ(), false)) {
				this.tryTeleportToNewPosition();
			}
		}

		float f1 = (float) this.getPeekTick() * 0.01F;
		this.currentPeekAmount0 = this.currentPeekAmount;

		if (this.currentPeekAmount > f1) {
			this.currentPeekAmount = MathHelper.clamp_float(this.currentPeekAmount - 0.05F, f1, 1.0F);
		} else if (this.currentPeekAmount < f1) {
			this.currentPeekAmount = MathHelper.clamp_float(this.currentPeekAmount + 0.05F, 0.0F, f1);
		}

		if (blockpos != null) {
			if (this.worldObj.isRemote) {
				if (this.clientSideTeleportInterpolation > 0 && this.currentAttachmentPosition != null) {
					--this.clientSideTeleportInterpolation;
				} else {
					this.currentAttachmentPosition = blockpos;
				}
			}

			this.lastTickPosX = this.prevPosX = this.posX = (double) blockpos.getX() + 0.5D;
			this.lastTickPosY = this.prevPosY = this.posY = blockpos.getY();
			this.lastTickPosZ = this.prevPosZ = this.posZ = (double) blockpos.getZ() + 0.5D;
			double d3 = 0.5D - (double) MathHelper.sin((0.5F + this.currentPeekAmount) * (float) Math.PI) * 0.5D;
			double d4 = 0.5D - (double) MathHelper.sin((0.5F + this.currentPeekAmount0) * (float) Math.PI) * 0.5D;
			double d5 = d3 - d4;
			double d0 = 0.0D;
			double d1 = 0.0D;
			double d2 = 0.0D;
			EnumFacing enumfacing2 = this.getAttachmentFacing();

			switch (enumfacing2) {
				case DOWN:
				default:
					this.boundingBox.setBounds(this.posX - 0.5D, this.posY, this.posZ - 0.5D, this.posX + 0.5D, this.posY + 1.0D + d3, this.posZ + 0.5D);
					d1 = d5;
					break;

				case UP:
					this.boundingBox.setBounds(this.posX - 0.5D, this.posY - d3, this.posZ - 0.5D, this.posX + 0.5D, this.posY + 1.0D, this.posZ + 0.5D);
					d1 = -d5;
					break;

				case NORTH:
					this.boundingBox.setBounds(this.posX - 0.5D, this.posY, this.posZ - 0.5D, this.posX + 0.5D, this.posY + 1.0D, this.posZ + 0.5D + d3);
					d2 = d5;
					break;

				case SOUTH:
					this.boundingBox.setBounds(this.posX - 0.5D, this.posY, this.posZ - 0.5D - d3, this.posX + 0.5D, this.posY + 1.0D, this.posZ + 0.5D);
					d2 = -d5;
					break;

				case WEST:
					this.boundingBox.setBounds(this.posX - 0.5D, this.posY, this.posZ - 0.5D, this.posX + 0.5D + d3, this.posY + 1.0D, this.posZ + 0.5D);
					d0 = d5;
					break;
				case EAST:
					this.boundingBox.setBounds(this.posX - 0.5D - d3, this.posY, this.posZ - 0.5D, this.posX + 0.5D, this.posY + 1.0D, this.posZ + 0.5D);
					d0 = -d5;
			}

			if (d5 > 0.0D) {
				List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);

				if (!list.isEmpty()) {
					for (Entity entity : list) {
						if (!(entity instanceof EntityShulker) && !entity.noClip) {
							entity.moveEntity(d0, d1, d2);
						}
					}
				}
			}
		}
	}

	@Override
	public void setPosition(double x, double y, double z) {
		super.setPosition(x, y, z);

		if (this.getDataWatcher() != null) {
			int xpos = MathHelper.floor_double(x);
			int ypos = MathHelper.floor_double(y);
			int zpos = MathHelper.floor_double(z);
			if (this.getDataWatcher().getWatchableObjectInt(ATTACHED_X) != xpos || this.getDataWatcher().getWatchableObjectInt(ATTACHED_Y) != ypos
					|| this.getDataWatcher().getWatchableObjectInt(ATTACHED_Z) != zpos) {
				this.setAttachmentPos(new BlockPos(xpos, ypos, zpos));
				if (!this.isRiding())
					this.getDataWatcher().updateObject(PEEK_TICK, (byte) 0);
				this.isAirBorne = true;
			}
		}
	}

	protected boolean tryTeleportToNewPosition() {
		if (this.isAIEnabled() && this.isEntityAlive()) {
			for (int i = 0; i < 5; ++i) {
				int newx = MathHelper.floor_double(this.posX) + 8 - this.rand.nextInt(17);
				int newy = MathHelper.floor_double(this.posY) + 8 - this.rand.nextInt(17);
				int newz = MathHelper.floor_double(this.posZ) + 8 - this.rand.nextInt(17);

				if (newy > 0 && this.worldObj.isAirBlock(newx, newy, newz)
						&& /* this.worldObj.isInsideBorder(this.worldObj.getWorldBorder(), this) && */this.worldObj.getCollidingBoundingBoxes(this, AxisAlignedBB.getBoundingBox(newx, newy, newz, newx + 1, newy + 1, newz + 1)).isEmpty()) {
					boolean flag = false;

					for (EnumFacing enumfacing : Utils.ENUM_FACING_VALUES) {
						if (this.worldObj.isBlockNormalCubeDefault(newx + enumfacing.getFrontOffsetX(), newy + enumfacing.getFrontOffsetY(),
								newz + enumfacing.getFrontOffsetZ(), false)) {
							this.getDataWatcher().updateObject(ATTACHED_FACE, (byte) enumfacing.ordinal());
							flag = true;
							break;
						}
					}

					if (flag) {
						this.playSound(Reference.MCAssetVer + ":entity.shulker.teleport", 1.0F, 1.0F);
						this.setAttachmentPos(new BlockPos(newx, newy, newz));
						this.getDataWatcher().updateObject(PEEK_TICK, (byte) 0);
						this.setAttackTarget(null);
						return true;
					}
				}
			}

			return false;
		}
		return true;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevRenderYawOffset = 180.0F;
		this.renderYawOffset = 180.0F;
		this.rotationYaw = 180.0F;
	}

	@Override
	public void func_145781_i(int key) {
		if ((key == ATTACHED_X || key == ATTACHED_Y || key == ATTACHED_Z) && this.worldObj.isRemote && !this.isRiding()) {
			BlockPos blockpos = this.getAttachmentPos();

			if (blockpos.getY() != -1) {
				if (this.currentAttachmentPosition == null) {
					this.currentAttachmentPosition = blockpos;
				} else if (this.ticksExisted > 0) {
					this.clientSideTeleportInterpolation = 6;
				}

				this.lastTickPosX = this.prevPosX = this.posX = (double) blockpos.getX() + 0.5D;
				this.lastTickPosY = this.prevPosY = this.posY = blockpos.getY();
				this.lastTickPosZ = this.prevPosZ = this.posZ = (double) blockpos.getZ() + 0.5D;
			}
		}

		super.func_145781_i(key);
	}

	/**
	 * Called when the entity is attacked.
	 */
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isClosed()) {
			Entity entity = source.getSourceOfDamage();

			if (entity instanceof EntityArrow) {
				return false;
			}
		}

		if (super.attackEntityFrom(source, amount)) {
			if ((double) this.getHealth() < (double) this.getMaxHealth() * 0.5D && this.rand.nextInt(4) == 0) {
				this.tryTeleportToNewPosition();
			} else if (source.getSourceOfDamage() instanceof EntityShulkerBullet) {
				int x = MathHelper.floor_double(posX);
				int y = MathHelper.floor_double(posY);
				int z = MathHelper.floor_double(posZ);
				boolean prevClosed = isClosed();
				boolean teleported = this.tryTeleportToNewPosition();
				if (isEntityAlive() && teleported && !prevClosed) { //1.17 reproduction mechanic remade by hand
					int i = worldObj.selectEntitiesWithinAABB(EntityShulker.class, this.boundingBox.expand(8, 8, 8), IEntitySelector.selectAnything).size();
					float f = (float) (i - 1) / 5.0F;
					if (!(this.worldObj.rand.nextFloat() < f)) {
						EntityShulker newShulker = new EntityShulker(worldObj);
						newShulker.setPosition(x, y, z);
						newShulker.setColor(getColor());
						worldObj.spawnEntityInWorld(newShulker);
					}
				}
			}

			return true;
		}
		return false;
	}

	public boolean isClosed() {
		return this.getPeekTick() == 0;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return null;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return isEntityAlive() ? boundingBox : null;
	}

	public EnumFacing getAttachmentFacing() {
		return EnumFacing.getFront(this.getDataWatcher().getWatchableObjectByte(ATTACHED_FACE));
	}

	public void setAttachmentPos(BlockPos pos) {
		this.getDataWatcher().updateObject(ATTACHED_X, pos.getX());
		this.getDataWatcher().updateObject(ATTACHED_Y, pos.getY());
		this.getDataWatcher().updateObject(ATTACHED_Z, pos.getZ());
	}

	public int getPeekTick() {
		return this.getDataWatcher().getWatchableObjectByte(PEEK_TICK);
	}


	public void updateArmorModifier(int p_184691_1_) {
		if (!this.worldObj.isRemote) {
//          this.getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(COVERED_ARMOR_BONUS_MODIFIER);

			if (p_184691_1_ == 0) {
//              this.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier(COVERED_ARMOR_BONUS_MODIFIER);
				this.playSound(Reference.MCAssetVer + ":entity.shulker.close", 1.0F, 1.0F);
			} else {
				this.playSound(Reference.MCAssetVer + ":entity.shulker.open", 1.0F, 1.0F);
			}
		}

		this.getDataWatcher().updateObject(PEEK_TICK, (byte) p_184691_1_);
	}

	@Override
	public int getTotalArmorValue() {
		return isClosed() ? 20 : super.getTotalArmorValue();
	}

	public float getClientPeekAmount(float p_184688_1_) {
		return this.currentPeekAmount0 + (this.currentPeekAmount - this.currentPeekAmount0) * p_184688_1_;
	}

	public int getClientTeleportInterp() {
		return this.clientSideTeleportInterpolation;
	}

	public BlockPos getOldAttachPos() {
		return this.currentAttachmentPosition;
	}

	@Override
	public float getEyeHeight() {
		return 0.5F;
	}

	/**
	 * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
	 * use in wolves.
	 */
	@Override
	public int getVerticalFaceSpeed() {
		return 180;
	}

//    @Override
//    public int getHorizontalFaceSpeed()
//    {
//        return 180;
//    }

	@Override
	public void applyEntityCollision(Entity entityIn) {
	}

	@Override
	public float getCollisionBorderSize() {
		return 0.0F;
	}

	public boolean isAttachedToBlock() {
		return this.currentAttachmentPosition != null && this.getAttachmentPos().getY() != -1;
	}

	@Override
	protected Item getDropItem() {
		return ModItems.SHULKER_SHELL.isEnabled() ? ModItems.SHULKER_SHELL.get() : null;
	}


	@Override
	public void setRevengeTarget(EntityLivingBase p_70604_1_) {
		if (worldObj.difficultySetting != EnumDifficulty.PEACEFUL) {
			super.setRevengeTarget(p_70604_1_);
		}
	}

	@Override
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
		Item item = this.getDropItem();

		float chance = rand.nextFloat();

		chance += (float) (p_70628_2_ * 0.0625);

		if (item != null && chance > 0.5) {
			this.dropItem(item, 1);
		}
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return ModEntityList.getEggFromEntity(this);
	}

	@Override
	public void travelToDimension(int dimensionId) {
		super.travelToDimension(dimensionId);
		this.setAttachmentPos(new BlockPos(this));
	}

	@Override
	public void dismountEntity(Entity p_110145_1_) {
		super.dismountEntity(p_110145_1_);
		int y = (int) posY;
		if (worldObj.isAirBlock(MathHelper.floor_double(posX), y - 1, MathHelper.floor_double(posZ))) {
			y -= 1;
		}
		this.setAttachmentPos(new BlockPos(posX, y, posZ));
	}

	@Override
	public boolean interact(EntityPlayer p_70085_1_) {
		ItemStack stack = p_70085_1_.getCurrentEquippedItem();
		if (ConfigTweaks.dyableShulkers && stack != null) {
			if (stack.getItem() instanceof ItemBucket && ((ItemBucket) stack.getItem()).isFull == Blocks.flowing_water && getColor() != 16) {
				setColor((byte) 16);
				return true;
			}
			for (int oreID : OreDictionary.getOreIDs(stack)) {
				byte color = (byte) (~ArrayUtils.indexOf(ModRecipes.ore_dyes, OreDictionary.getOreName(oreID)) & 15);
				if (ArrayUtils.contains(ModRecipes.ore_dyes, OreDictionary.getOreName(oreID)) && getColor() != color) {
					setColor(color);
					if (!p_70085_1_.capabilities.isCreativeMode)
						--stack.stackSize;
					return true;
				}
			}
		}
		return super.interact(p_70085_1_);
	}

	class AIAttack extends EntityAIBase {
		private int attackTime;

		public AIAttack() {
			this.setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = EntityShulker.this.getAttackTarget();
			return entitylivingbase != null && entitylivingbase.isEntityAlive() && EntityShulker.this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL;
		}

		@Override
		public void startExecuting() {
			this.attackTime = 20;
			EntityShulker.this.updateArmorModifier(100);
		}

		@Override
		public void resetTask() {
			EntityShulker.this.updateArmorModifier(0);
		}

		@Override
		public void updateTask() {
			if (EntityShulker.this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL) {
				--this.attackTime;
				EntityLivingBase entitylivingbase = EntityShulker.this.getAttackTarget();
				EntityShulker.this.getLookHelper().setLookPositionWithEntity(entitylivingbase, 180.0F, 180.0F);
				double d0 = EntityShulker.this.getDistanceSqToEntity(entitylivingbase);

				if (d0 < 400.0D) {
					if (this.attackTime <= 0) {
						this.attackTime = 20 + EntityShulker.this.rand.nextInt(10) * 20 / 2;
						EntityShulkerBullet entityshulkerbullet = new EntityShulkerBullet(EntityShulker.this.worldObj, EntityShulker.this, entitylivingbase, EntityShulker.this.getAttachmentFacing());
						EntityShulker.this.worldObj.spawnEntityInWorld(entityshulkerbullet);
						EntityShulker.this.playSound(Reference.MCAssetVer + ":entity.shulker.shoot", 1, 1);
					}
				} else {
					EntityShulker.this.setAttackTarget(null);
				}

				super.updateTask();
			}
		}
	}

	class AIAttackNearest extends EntityAINearestAttackableTarget {
		public AIAttackNearest(EntityShulker shulker) {
			//TODO: Added arg 0
			super(shulker, EntityPlayer.class, 0, true);
		}

		@Override
		public boolean shouldExecute() {
			return EntityShulker.this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL && super.shouldExecute();
		}

		protected AxisAlignedBB getTargetableArea(double targetDistance) {
			EnumFacing enumfacing = ((EntityShulker) this.taskOwner).getAttachmentFacing();
			return enumfacing.getFrontOffsetX() != 0 ? this.taskOwner.boundingBox.expand(4.0D, targetDistance, targetDistance) : (enumfacing.getFrontOffsetZ() != 0 ? this.taskOwner.boundingBox.expand(targetDistance, targetDistance, 4.0D) : this.taskOwner.boundingBox.expand(targetDistance, 4.0D, targetDistance));
		}
	}

	static class AIDefenseAttack extends EntityAINearestAttackableTarget {
		public AIDefenseAttack(EntityShulker shulker) {
			super(shulker, EntityLivingBase.class, 10, true);
//            super(shulker, EntityLivingBase.class, 10, true, false, new Predicate<EntityLivingBase>()
//            {
//                public boolean apply(@Nullable EntityLivingBase p_apply_1_)
//                {
//                    return p_apply_1_ instanceof IMob;
//                }
//            });
		}

		@Override
		public boolean shouldExecute() {
			return this.taskOwner.getTeam() != null && super.shouldExecute();
		}

		protected AxisAlignedBB getTargetableArea(double targetDistance) {
			EnumFacing enumfacing = ((EntityShulker) this.taskOwner).getAttachmentFacing();
			return enumfacing.getFrontOffsetX() != 0 ? this.taskOwner.boundingBox.expand(4.0D, targetDistance, targetDistance) : (enumfacing.getFrontOffsetZ() != 0 ? this.taskOwner.boundingBox.expand(targetDistance, targetDistance, 4.0D) : this.taskOwner.boundingBox.expand(targetDistance, 4.0D, targetDistance));
		}
	}

	class AIPeek extends EntityAIBase {
		private int peekTime;

		public AIPeek() {
		}

		@Override
		public boolean shouldExecute() {
			return EntityShulker.this.getAttackTarget() == null && EntityShulker.this.rand.nextInt(40) == 0;
		}

		@Override
		public boolean continueExecuting() {
			return EntityShulker.this.getAttackTarget() == null && this.peekTime > 0;
		}

		@Override
		public void startExecuting() {
			this.peekTime = 20 * (1 + EntityShulker.this.rand.nextInt(3));
			EntityShulker.this.updateArmorModifier(30);
		}

		@Override
		public void resetTask() {
			if (EntityShulker.this.getAttackTarget() == null) {
				EntityShulker.this.updateArmorModifier(0);
			}
		}

		@Override
		public void updateTask() {
			--this.peekTime;
		}
	}
}
