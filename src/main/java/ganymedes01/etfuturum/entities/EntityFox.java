package ganymedes01.etfuturum.entities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.blocks.BlockBerryBush;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigEntities;
import ganymedes01.etfuturum.core.handlers.ServerEventHandler;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.core.utils.helpers.WeightedRandomItem;
import ganymedes01.etfuturum.entities.ai.EntityAICustomAvoidEntity;
import ganymedes01.etfuturum.entities.ai.EntityAICustomNearestAttackableTarget;
import ganymedes01.etfuturum.entities.ai.EntityAIFleeSunExtended;
import ganymedes01.etfuturum.entities.ai.ExtendedEntityLookHelper;
import ganymedes01.etfuturum.entities.ai.TargetPredicate;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.spectator.SpectatorMode;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityFox extends EntityAnimal {

    private static final int OWNER = 18;
    private static final int OTHER_TRUSTED = 19;
    private static final int TYPE = 20;
    private static final int FOX_FLAGS = 21;

    private static final IEntitySelector PICKABLE_DROP_FILTER =
            entity -> entity instanceof EntityItem item && item.delayBeforeCanPickup <= 0 && item.isEntityAlive();
    private static final Predicate<EntityLivingBase> CHICKEN_AND_RABBIT_FILTER =
            entity -> entity instanceof EntityChicken || entity instanceof EntityRabbit;
    private static final Predicate<EntityLivingBase> NOTICEABLE_PLAYER_FILTER =
            entity -> !(entity instanceof EntityPlayer player)
                    || (!player.isSneaking() && !player.capabilities.isCreativeMode && !SpectatorMode.isSpectator(player));
    private static final Predicate<EntityLivingBase> JUST_ATTACKED_SOMETHING_FILTER =
            entity -> entity instanceof EntityLiving;

    private EntityAIBase followChickenAndRabbitTask;
    // private EntityAIBase followBabyTurtleTask;
    // private EntityAIBase followFishTask;

    private float headRollProgress;
    private float lastHeadRollProgress;
    private float extraRollingHeight;
    private float lastExtraRollingHeight;
    private int eatingTime;
    private EntityLivingBase friend;
    private boolean followOwner;

    public EntityFox(World world) {
        super(world);
        this.lookHelper = new EntityFox.FoxLookHelper();
        this.moveHelper = new EntityFox.FoxMoveHelper();
        this.setSize(0.6F, 0.7F);
        this.setCanPickUpLoot(true);
        initTasks();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(OWNER, "");
        this.dataWatcher.addObject(OTHER_TRUSTED, "");
        this.dataWatcher.addObject(TYPE, (byte) 0);
        this.dataWatcher.addObject(FOX_FLAGS, (byte) 0);
    }

    protected void initTasks() {
        this.followChickenAndRabbitTask = new EntityAICustomNearestAttackableTarget(this, EntityLiving.class, 10, true, false,
                entity -> entity instanceof EntityChicken || entity instanceof EntityRabbit);
        // todo when turtles are added
        // this.followBabyTurtleTask = new EntityAICustomNearestAttackableTarget(this, EntityTurtle.class, 10, false, false,
        //         TurtleEntity.BABY_TURTLE_ON_LAND_FILTER);
        // todo when fish entities are added
        // this.followFishTask = new EntityAICustomNearestAttackableTarget(this, EntityFish.class, 20, false, false,
        //         entity -> entity instanceof EntitySchoolingFish);

        this.tasks.addTask(0, new EntityFox.AISwim());
        this.tasks.addTask(1, new EntityFox.AIStopWandering());
        this.tasks.addTask(2, new EntityFox.AIEscapeWhenNotAggressive(2.2D));

        this.tasks.addTask(3, new EntityAICustomAvoidEntity(this, EntityPlayer.class, 16.0F, 1.6D, 1.4D,
                livingEntity -> NOTICEABLE_PLAYER_FILTER.test(livingEntity) && !this.canTrust(livingEntity.getUniqueID()) && !this.isAggressive()));
        this.tasks.addTask(3, new EntityAICustomAvoidEntity(this, EntityWolf.class, 8.0F, 1.6D, 1.4D,
                livingEntity -> !((EntityWolf) livingEntity).isTamed() && !this.isAggressive()));
        // todo when polar bears are added
        // this.tasks.addTask(3, new EntityAICustomAvoidEntity(this, EntityPolarBear.class, 8.0F, 1.6D, 1.4D,
        //         livingEntity -> !this.isAggressive()));

        this.tasks.addTask(4, new EntityFox.AIMoveToHunt());
        this.tasks.addTask(5, new EntityFox.AIJumpChase());
        this.tasks.addTask(5, new EntityFox.AIMate(1.0D));
        this.tasks.addTask(5, new EntityFox.AIAvoidDaylight(1.25D));
        this.tasks.addTask(6, new EntityFox.AIAttack(1.2D, true));
        this.tasks.addTask(6, new EntityFox.AIDelayedCalmDown());
        this.tasks.addTask(7, new EntityFox.AIFollowParent(this, 1.25D));
        // TODO (hint: this.worldObj.villageCollectionObj.findNearestVillage)
        // this.tasks.addTask(8, new EntityFox.GoToVillageGoal(32, 200));
        if (ConfigBlocksItems.enableSweetBerryBushes) {
            this.tasks.addTask(9, new EntityFox.AIEatSweetBerries(1.2D, 12, 2));
        }
        this.tasks.addTask(9, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(10, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(10, new EntityFox.AIPickupItem());
        this.tasks.addTask(11, new EntityFox.AILookAtEntity(this, EntityPlayer.class, 24.0F));
        this.tasks.addTask(12, new EntityFox.AISitDownAndLookAround());

        this.targetTasks.addTask(3, new EntityFox.AIDefendFriend(EntityLiving.class, false, false,
                entity -> entity != null && JUST_ATTACKED_SOMETHING_FILTER.test(entity) && !this.canTrust(entity.getUniqueID())));
    }

    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote && this.isEntityAlive()) {
            ++this.eatingTime;
            ItemStack itemStack = this.getHeldItem();
            if (this.canEat(itemStack)) {
                int eatDelay = 560;
                if (this.eatingTime > eatDelay + 40) {
                    ItemStack itemStack2 = this.eatFood(this.worldObj, itemStack);
                    if (itemStack2 != null && itemStack2.getItem() != null && itemStack2.stackSize > 0) {
                        this.setCurrentItemOrArmor(0, itemStack2);
                    } else {
                        this.setCurrentItemOrArmor(0, null);
                    }

                    this.eatingTime = 0;
                } else if (this.eatingTime > eatDelay && this.rand.nextFloat() < 0.1F) {
                    this.playSound(this.getEatSound(), 1.0F, 1.0F);
                    this.worldObj.setEntityState(this, (byte) 45);
                }
            }

            EntityLivingBase livingEntity = this.getAttackTarget();
            if (livingEntity == null || !livingEntity.isEntityAlive()) {
                this.setCrouching(false);
                this.setRollingHead(false);
            }
        }

        if (this.getPetOwner() != null && this.getDistanceSqToEntity(this.getPetOwner()) > 64f * 64f) {
            followOwner = false;
        }

        if (this.isPlayerSleeping() || this.isDead) {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
        }

        super.onLivingUpdate();

        if (!this.worldObj.isRemote
                && this.canPickUpLoot()
                && this.isEntityAlive()
                && !this.dead
                && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
            List<EntityItem> list = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(1.0D, 0.0D, 1.0D));
            for (EntityItem item : list) {
                if (!item.isDead && item.getEntityItem() != null && item.delayBeforeCanPickup <= 0) {
                    this.loot(item);
                }
            }
        }

        if (this.isAggressive() && this.rand.nextFloat() < 0.05F) {
            this.playSound(Reference.MCAssetVer + ":entity.fox.aggro", 1.0F, 1.0F);
        }
    }

    private boolean canEat(ItemStack stack) {
        return stack != null
                && stack.getItem() instanceof ItemFood
                && this.getAttackTarget() == null
                && this.onGround
                && !this.isPlayerSleeping();
    }

    protected void initEquipment() {
        if (this.rand.nextFloat() < 0.2F) {
            List<WeightedRandomItem<Item>> items = getSpawnItems();
            if (!items.isEmpty()) {
                WeightedRandomItem<Item> choice = (WeightedRandomItem<Item>) WeightedRandom.getRandomItem(getRNG(), items);
                this.setCurrentItemOrArmor(0, new ItemStack(choice.data));
            }
        }
    }

    private static List<WeightedRandomItem<Item>> spawnItems;

    private static List<WeightedRandomItem<Item>> getSpawnItems() {
        if (spawnItems == null) {
            // Chances:
            // 5% emerald
            // 10% rabbits foot (if rabbits enabled)
            // 10% rabbit hide (if rabbits enabled)
            // 15% egg (20% if rabbits disabled)
            // 20% wheat (25% if rabbits disabled)
            // 20% leather (25% if rabbits disabled)
            // 20% feather (25% if rabbits disabled)
            spawnItems = new ArrayList<>();

            int extra = 5;
            if (ConfigEntities.enableRabbit) {
                extra = 0;
                spawnItems.add(new WeightedRandomItem<>(10, ModItems.RABBIT_FOOT.get()));
                spawnItems.add(new WeightedRandomItem<>(10, ModItems.RABBIT_HIDE.get()));
            }

            spawnItems.add(new WeightedRandomItem<>(5, Items.emerald));
            spawnItems.add(new WeightedRandomItem<>(15 + extra, Items.egg));
            spawnItems.add(new WeightedRandomItem<>(20 + extra, Items.wheat));
            spawnItems.add(new WeightedRandomItem<>(20 + extra, Items.leather));
            spawnItems.add(new WeightedRandomItem<>(20 + extra, Items.feather));
        }
        return spawnItems;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte status) {
        if (status == 45) {
            ItemStack itemStack = this.getHeldItem();
            if (itemStack != null && itemStack.getItem() != null && itemStack.stackSize > 0) {
                for (int i = 0; i < 8; ++i) {
                    Vec3 vec3d = (Vec3.createVectorHelper(((double) this.rand.nextFloat() - 0.5D) * 0.1D,
                            Math.random() * 0.1D + 0.1D, 0.0D));
                    vec3d.rotateAroundX(-this.rotationPitch * 0.017453292F);
                    vec3d.rotateAroundY(-this.rotationYaw * 0.017453292F);
                    Vec3 rv = getRotationVector(this.rotationPitch, this.rotationYaw);
                    this.worldObj.spawnParticle(getItemStackParticleName(itemStack),
                            this.posX + rv.xCoord / 2.0D, this.posY,
                            this.posZ + rv.zCoord / 2.0D, vec3d.xCoord, vec3d.yCoord + 0.05D, vec3d.zCoord);
                }
            }
        } else {
            super.handleHealthUpdate(status);
        }
    }

    private static String getItemStackParticleName(ItemStack stack) {
        String s = "iconcrack_" + Item.getIdFromItem(stack.getItem());
        if (stack.getHasSubtypes()) {
            s = s + "_" + stack.getItemDamage();
        }
        return s;
    }

    private static Vec3 getRotationVector(float pitch, float yaw) {
        float f = (float) (pitch * Math.PI / 180.0F);
        float g = (float) (-yaw * Math.PI / 180.0F);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return Vec3.createVectorHelper(i * j, -k, h * j);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2D);
    }

    @Override
    public EntityFox createChild(EntityAgeable passiveEntity) {
        EntityFox EntityFox = new EntityFox(this.worldObj);
        EntityFox.setType(this.rand.nextBoolean() ? this.getFoxType() : ((EntityFox) passiveEntity).getFoxType());
        return EntityFox;
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityData) {
        entityData = super.onSpawnWithEgg(entityData);
        EntityFox.Type type = EntityFox.Type.fromBiome(worldObj.getBiomeGenForCoords((int) this.posX, (int) this.posZ));
        boolean bl = false;
        if (entityData instanceof EntityFox.FoxData) {
            type = ((EntityFox.FoxData) entityData).type;
            if (((EntityFox.FoxData) entityData).getSpawnedCount() >= 2) {
                bl = true;
            }
        } else {
            entityData = new EntityFox.FoxData(type);
        }

        this.setType(type);
        if (bl) {
            this.setGrowingAge(-24000);
        }

        if (worldObj instanceof WorldServer) {
            this.addTypeSpecificTasks();
        }

        this.initEquipment();

        EntityFox.FoxData foxData = (EntityFox.FoxData) entityData;
        if (foxData.canSpawnBaby() && foxData.getSpawnedCount() > 0 && rand.nextFloat() <= foxData.getBabyChance()) {
            this.setGrowingAge(-24000);
        }
        foxData.countSpawned();
        return entityData;
    }

    private void addTypeSpecificTasks() {
        if (this.getFoxType() == EntityFox.Type.RED) {
            this.targetTasks.addTask(4, this.followChickenAndRabbitTask);
            // this.targetTasks.addTask(4, this.followBabyTurtleGoal);
            // this.targetTasks.addTask(6, this.followFishGoal);
        } else {
            // this.targetTasks.addTask(4, this.followFishGoal);
            this.targetTasks.addTask(6, this.followChickenAndRabbitTask);
            // this.targetTasks.addTask(6, this.followBabyTurtleGoal);
        }
    }

    public void eat(EntityPlayer player, ItemStack stack) {
        if (this.isBreedingItem(stack)) {
            this.playSound(this.getEatSound(), 1.0F, 1.0F);
        }

        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;

            if (stack.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
        }
    }

    @Override
    public float getEyeHeight() {
        return this.isChild() ? this.height * 0.85F : 0.4F;
    }

    public EntityFox.Type getFoxType() {
        return EntityFox.Type.fromId(dataWatcher.getWatchableObjectByte(TYPE));
    }

    private void setType(EntityFox.Type type) {
        this.dataWatcher.updateObject(TYPE, (byte) type.getId());
    }

    private List<UUID> getTrustedUuids() {
        List<UUID> list = new ArrayList<>();
        String owner = this.dataWatcher.getWatchableObjectString(OWNER);
        list.add(owner == null || owner.isEmpty() ? null : UUID.fromString(owner));
        String otherTrusted = this.dataWatcher.getWatchableObjectString(OTHER_TRUSTED);
        list.add(otherTrusted == null || otherTrusted.isEmpty() ? null : UUID.fromString(otherTrusted));
        return list;
    }

    private void addTrustedUuid(UUID uuid) {
        if (!this.dataWatcher.getWatchableObjectString(OWNER).isEmpty()) {
            this.dataWatcher.updateObject(OTHER_TRUSTED, uuid.toString());
        } else {
            this.dataWatcher.updateObject(OWNER, uuid.toString());
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        List<UUID> list = this.getTrustedUuids();
        NBTTagList listTag = new NBTTagList();

        for (UUID uUID : list) {
            if (uUID != null) {
                listTag.appendTag(new NBTTagString(uUID.toString()));
            }
        }

        tag.setTag("Trusted", listTag);
        tag.setBoolean("Sleeping", this.isPlayerSleeping());
        tag.setString("Type", this.getFoxType().getKey());
        tag.setBoolean("Sitting", this.isSitting());
        tag.setBoolean("Crouching", this.isInSneakingPose());
        if (this.followOwner) {
            tag.setBoolean("FollowOwner", true);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        NBTTagList listTag = tag.getTagList("Trusted", Constants.NBT.TAG_STRING);

        for (int i = 0; i < listTag.tagCount(); ++i) {
            this.addTrustedUuid(UUID.fromString(listTag.getStringTagAt(i)));
        }

        this.setSleeping(tag.getBoolean("Sleeping"));
        this.setType(EntityFox.Type.byName(tag.getString("Type")));
        this.setSitting(false);//tag.getBoolean("Sitting")); // disabled to avoid bugs
        this.setCrouching(tag.getBoolean("Crouching"));
        if (this.worldObj instanceof WorldServer) {
            this.addTypeSpecificTasks();
        }
        if (tag.hasKey("FollowOwner")) {
            this.followOwner = tag.getBoolean("FollowOwner");
        }
    }

    public boolean isSitting() {
        return this.getFoxFlag(1);
    }

    public void setSitting(boolean sitting) {
        this.setFoxFlag(1, sitting);
    }

    public boolean isWalking() {
        return this.getFoxFlag(64);
    }

    private void setWalking(boolean walking) {
        this.setFoxFlag(64, walking);
    }

    private boolean isAggressive() {
        return this.getFoxFlag(128);
    }

    private void setAggressive(boolean aggressive) {
        this.setFoxFlag(128, aggressive);
    }

    @Override
    public boolean isPlayerSleeping() {
        return this.getFoxFlag(32);
    }

    private void setSleeping(boolean sleeping) {
        this.setFoxFlag(32, sleeping);
    }

    private void setFoxFlag(int mask, boolean value) {
        if (value) {
            this.dataWatcher.updateObject(FOX_FLAGS, (byte) (this.dataWatcher.getWatchableObjectByte(FOX_FLAGS) | mask));
        } else {
            this.dataWatcher.updateObject(FOX_FLAGS, (byte) (this.dataWatcher.getWatchableObjectByte(FOX_FLAGS) & ~mask));
        }
    }

    private boolean getFoxFlag(int bitmask) {
        return (this.dataWatcher.getWatchableObjectByte(FOX_FLAGS) & bitmask) != 0;
    }

    public boolean canPickupItem(ItemStack stack) {
        Item item = stack.getItem();
        ItemStack itemStack = this.getEquipmentInSlot(0);
        return itemStack == null || this.eatingTime > 0 && item instanceof ItemFood && !(itemStack.getItem() instanceof ItemFood);
    }

    private void spit(ItemStack stack) {
        if (stack != null && !this.worldObj.isRemote) {
            Vec3 rv = getRotationVector(this.rotationPitch, this.rotationYaw);
            EntityItem EntityItem = new EntityItem(
                    this.worldObj,
                    this.posX + rv.xCoord,
                    this.posY + 1.0D,
                    this.posZ + rv.zCoord,
                    stack);
            EntityItem.delayBeforeCanPickup = 40;
            EntityItem.func_145799_b(getUniqueID().toString()); // is this OK?
            this.playSound(Reference.MCAssetVer + ":entity.fox.spit", 1.0F, 1.0F);
            this.worldObj.spawnEntityInWorld(EntityItem);
        }
    }

    private void dropItem(ItemStack stack) {
        EntityItem EntityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, stack);
        this.worldObj.spawnEntityInWorld(EntityItem);
    }

    protected void loot(EntityItem item) {
        ItemStack itemStack = item.getEntityItem();
        if (this.canPickupItem(itemStack)) {
            EntityPlayer dropper = ServerEventHandler.droppedEntityItems.getIfPresent(item);

            if (isBreedingItem(itemStack) && dropper != null && dropper.getUniqueID().equals(getTrustedUuids().get(0))) {
                followOwner = true;
            }

            int i = itemStack.stackSize;
            if (i > 1) {
                this.dropItem(itemStack.splitStack(i - 1));
            }

            this.spit(this.getEquipmentInSlot(0));
            this.setCurrentItemOrArmor(0, itemStack.splitStack(1));
            this.equipmentDropChances[0] = 2.0F;
            this.onItemPickup(item, itemStack.stackSize);
            item.setDead();
            this.eatingTime = 0;
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.worldObj.isRemote) {
            boolean bl = this.inWater;
            if (bl || this.getAttackTarget() != null || this.worldObj.isThundering()) {
                this.stopSleeping();
            }

            if (bl || this.isPlayerSleeping()) {
                this.setSitting(false);
            }

            if (this.isWalking() && this.worldObj.rand.nextFloat() < 0.2F) {
                this.worldObj.playAuxSFX(2001,
                        MathHelper.floor_double(posX),
                        MathHelper.floor_double(posY),
                        MathHelper.floor_double(posZ),
                        worldObj.getBlockMetadata(MathHelper.floor_double(posX),
                                MathHelper.floor_double(posY),
                                MathHelper.floor_double(posZ)));
            }
        }

        this.lastHeadRollProgress = this.headRollProgress;
        if (this.isRollingHead()) {
            this.headRollProgress += (1.0F - this.headRollProgress) * 0.4F;
        } else {
            this.headRollProgress += (0.0F - this.headRollProgress) * 0.4F;
        }

        this.lastExtraRollingHeight = this.extraRollingHeight;
        if (this.isInSneakingPose()) {
            this.extraRollingHeight += 0.2F;
            if (this.extraRollingHeight > 3.0F) {
                this.extraRollingHeight = 3.0F;
            }
        } else {
            this.extraRollingHeight = 0.0F;
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack != null && getBreedingItems().contains(stack.getItem());
    }

    private static List<Item> breedingItems;

    private static List<Item> getBreedingItems() {
        if (breedingItems == null) {
            breedingItems = new ArrayList<>();
            if (ConfigBlocksItems.enableSweetBerryBushes) {
                breedingItems.add(ModItems.SWEET_BERRIES.get());
            }
            // todo when glow berries are added
            // if (ConfigBlocksItems.enableGlowBerries) {
            //     breedingItems.add(ModItems.GLOW_BERRIES.get());
            // }
            if (breedingItems.isEmpty()) {
                // Fallback if no vanilla option is enabled via config
                breedingItems.add(Items.wheat);
            }
        }
        return breedingItems;
    }

    public boolean isChasing() {
        return this.getFoxFlag(16);
    }

    public void setChasing(boolean chasing) {
        this.setFoxFlag(16, chasing);
    }

    public boolean isFullyCrouched() {
        return this.extraRollingHeight == 3.0F;
    }

    public void setCrouching(boolean crouching) {
        this.setFoxFlag(4, crouching);
    }

    public boolean isInSneakingPose() {
        return this.getFoxFlag(4);
    }

    public void setRollingHead(boolean rollingHead) {
        this.setFoxFlag(8, rollingHead);
    }

    public boolean isRollingHead() {
        return this.getFoxFlag(8);
    }

    @SideOnly(Side.CLIENT)
    public float getHeadRoll(float tickDelta) {
        return (this.lastHeadRollProgress + tickDelta * (this.headRollProgress - this.lastHeadRollProgress)) * 0.11F * (float) Math.PI;
    }

    @SideOnly(Side.CLIENT)
    public float getBodyRotationHeightOffset(float tickDelta) {
        return this.lastExtraRollingHeight + tickDelta * (this.extraRollingHeight - this.lastExtraRollingHeight);
    }

    @Override
    public void setAttackTarget(EntityLivingBase target) {
        if (this.isAggressive() && target == null) {
            this.setAggressive(false);
        }

        super.setAttackTarget(target);
    }

    public float computeFallDistance(float fallDistance) {
        return fallDistance - 2f;
    }

    private void stopSleeping() {
        this.setSleeping(false);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    private void stopActions() {
        this.setRollingHead(false);
        this.setCrouching(false);
        this.setSitting(false);
        this.setSleeping(false);
        this.setAggressive(false);
        this.setWalking(false);
    }

    private boolean wantsToPickupItem() {
        return !this.isPlayerSleeping() && !this.isSitting() && !this.isWalking();
    }

    @Override
    public void playLivingSound() {
        String soundEvent = this.getLivingSound();
        if (soundEvent.equals(Reference.MCAssetVer + ":entity.fox.screech")) {
            this.playSound(soundEvent, 2.0F, this.getSoundPitch());
        } else {
            super.playLivingSound();
        }
    }

    @Override
    protected String getLivingSound() {
        if (this.isPlayerSleeping()) {
            return Reference.MCAssetVer + ":entity.fox.sleep";
        } else {
            if (!this.worldObj.isDaytime() && this.rand.nextFloat() < 0.1F) {
                List<EntityPlayer> list = this.worldObj.getEntitiesWithinAABB(
                        EntityPlayer.class,
                        this.boundingBox.expand(16.0D, 16.0D, 16.0D));
                if (list.isEmpty()) {
                    return Reference.MCAssetVer + ":entity.fox.screech";
                }
            }
            return Reference.MCAssetVer + ":entity.fox.ambient";
        }
    }

    @Override
    protected String getHurtSound() {
        return Reference.MCAssetVer + ":entity.fox.hurt";
    }

    @Override
    protected String getDeathSound() {
        return Reference.MCAssetVer + ":entity.fox.death";
    }

    protected String getEatSound() {
        return Reference.MCAssetVer + ":entity.fox.eat";
    }

    private boolean canTrust(UUID uuid) {
        return this.getTrustedUuids().contains(uuid);
    }

    @Override
    public void onDeath(DamageSource source) {
        if (!this.worldObj.isRemote) {
            ItemStack itemStack = this.getEquipmentInSlot(0);
            if (itemStack != null) {
                this.entityDropItem(itemStack, 0.0F);
                this.setCurrentItemOrArmor(0, null);
            }
        }

        super.onDeath(source);
    }

    public static boolean canJumpChase(EntityFox fox, EntityLivingBase chasedEntity) {
        double d = chasedEntity.posZ - fox.posZ;
        double e = chasedEntity.posX - fox.posX;
        double f = d / e;

        for (int j = 0; j < 6; ++j) {
            double g = f == 0.0D ? 0.0D : d * (double) ((float) j / 6.0F);
            double h = f == 0.0D ? e * (double) ((float) j / 6.0F) : g / f;

            for (int k = 1; k < 4; ++k) {
                if (!fox.worldObj.getBlock(
                        MathHelper.floor_double(fox.posX + h),
                        MathHelper.floor_double(fox.posY + (double) k),
                        MathHelper.floor_double(fox.posZ + g)).getMaterial().isReplaceable()) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        float f = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        int i = 0;

        if (target instanceof EntityLivingBase) {
            f += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)target);
            i += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)target);
        }

        int j = EnchantmentHelper.getFireAspectModifier(this);

        if (j > 0) {
            target.setFire(j * 4);
        }

        boolean result = target.attackEntityFrom(DamageSource.causeMobDamage(this), f);

        if (result) {
            if (i > 0) {
                target.addVelocity(
                        -MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F, 0.1D,
                        MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F);
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            this.dealDamage(this, target);
            this.onAttacking(target);
            this.playSound(Reference.MCAssetVer + ":entity.fox.bite", 1.0F, 1.0F);
        }

        return result;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (source == BlockBerryBush.SWEET_BERRY_BUSH) {
            return false;
        }
        return super.attackEntityFrom(source, damage);
    }

    public EntityLivingBase getPetOwner() {
        UUID uUID = EntityFox.this.getTrustedUuids().get(0);

        if (uUID != null && EntityFox.this.worldObj instanceof WorldServer) {
            // Assuming owner is a player
            return EntityFox.this.worldObj.func_152378_a(uUID); // getPlayerByUuid
        }
        return null;
    }

    public int getLootingLevel() {
        int lootingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.looting.effectId, this.getHeldItem());
        return Math.max(0, lootingLevel);
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack itemStack = player.inventory.getCurrentItem();
        if (this.isBreedingItem(itemStack)) {
            int i = this.getGrowingAge();
            if (!this.worldObj.isRemote && i == 0 && this.canEat()) {
                this.eat(player, itemStack);
                this.func_146082_f(player);
                return true;
            }
        }
        return super.interact(player);
    }

    public boolean canEat() {
        return !this.isInLove();
    }

    public ItemStack eatFood(World world, ItemStack stack) {
        if (stack.getItem() instanceof ItemFood food) {
            world.playSound(
                    this.posX,
                    this.posY,
                    this.posZ,
                    this.getEatSound(),
                    1.0F,
                    1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4F,
                    false);

            if (!world.isRemote && food.potionId > 0 && world.rand.nextFloat() < food.potionEffectProbability) {
                this.addPotionEffect(new PotionEffect(food.potionId, food.potionDuration * 20, food.potionAmplifier));
            }
            stack.stackSize -= 1;
        }
        return stack;
    }

    public int getLookPitchSpeed() {
        return 40;
    }

    public int getBodyYawSpeed() {
        return 75;
    }

    public void dealDamage(EntityLivingBase attacker, Entity target) {
        if (target instanceof EntityLivingBase) {
            EnchantmentHelper.func_151384_a((EntityLivingBase)target, attacker);
        }
        EnchantmentHelper.func_151385_b(attacker, target);
    }

    public void onAttacking(Entity target) {
        this.setLastAttacker(target instanceof EntityLivingBase ? target : null);
    }

    private class AILookAtEntity extends EntityAIWatchClosest {

        public AILookAtEntity(EntityLiving fox, Class<? extends Entity> targetType, float range) {
            super(fox, targetType, range);
        }

        @Override
        public boolean shouldExecute() {
            return super.shouldExecute() && !EntityFox.this.isWalking() && !EntityFox.this.isRollingHead();
        }

        @Override
        public boolean continueExecuting() {
            return super.continueExecuting() && !EntityFox.this.isWalking() && !EntityFox.this.isRollingHead();
        }
    }

    private static class AIFollowParent extends EntityAIFollowParent {

        private final EntityFox fox;

        public AIFollowParent(EntityFox fox, double speed) {
            super(fox, speed);
            this.fox = fox;
        }

        @Override
        public boolean shouldExecute() {
            return !this.fox.isAggressive() && super.shouldExecute();
        }

        @Override
        public boolean continueExecuting() {
            return !this.fox.isAggressive() && super.continueExecuting();
        }

        @Override
        public void startExecuting() {
            this.fox.stopActions();
            super.startExecuting();
        }
    }

    private class FoxLookHelper extends ExtendedEntityLookHelper {

        public FoxLookHelper() {
            super(EntityFox.this);
        }

        @Override
        public void onUpdateLook() {
            if (!EntityFox.this.isPlayerSleeping()) {
                super.onUpdateLook();
            }
        }

        @Override
        protected boolean shouldStayHorizontal() {
            return !EntityFox.this.isChasing()
                    && !EntityFox.this.isInSneakingPose()
                    && !EntityFox.this.isRollingHead()
                    && !EntityFox.this.isWalking();
        }
    }

    private class AIJumpChase extends EntityAIBase {

        public AIJumpChase() {
            this.setMutexBits(5);
        }

        @Override
        public boolean shouldExecute() {
            if (!EntityFox.this.isFullyCrouched()) {
                return false;
            } else {
                EntityLivingBase livingEntity = EntityFox.this.getAttackTarget();
                if (livingEntity != null && livingEntity.isEntityAlive()) {
                    boolean bl = EntityFox.canJumpChase(EntityFox.this, livingEntity);
                    if (!bl) {
                        EntityFox.this.getNavigator().tryMoveToEntityLiving(livingEntity, 0);
                        EntityFox.this.setCrouching(false);
                        EntityFox.this.setRollingHead(false);
                    }
                    return bl;
                } else {
                    return false;
                }
            }
        }

        @Override
        public boolean continueExecuting() {
            EntityLivingBase livingEntity = EntityFox.this.getAttackTarget();
            if (livingEntity != null && livingEntity.isEntityAlive()) {
                double d = EntityFox.this.motionY;
                return (d * d >= 0.05D
                        || Math.abs(EntityFox.this.rotationPitch) >= 15.0F
                        || !EntityFox.this.onGround)
                        && !EntityFox.this.isWalking();
            } else {
                return false;
            }
        }

        @Override
        public boolean isInterruptible() {
            return false;
        }

        @Override
        public void startExecuting() {
            EntityFox.this.setJumping(true);
            EntityFox.this.setChasing(true);
            EntityFox.this.setRollingHead(false);
            EntityLivingBase livingEntity = EntityFox.this.getAttackTarget();
            EntityFox.this.getLookHelper().setLookPositionWithEntity(livingEntity, 60.0F, 30.0F);
            Vec3 vec3d = Vec3.createVectorHelper(
                    livingEntity.posX - EntityFox.this.posX,
                    livingEntity.posY - EntityFox.this.posY,
                    livingEntity.posZ - EntityFox.this.posZ).normalize();
            EntityFox.this.motionX += vec3d.xCoord * 0.8D;
            EntityFox.this.motionY += 0.9D;
            EntityFox.this.motionZ += vec3d.zCoord * 0.8D;
            EntityFox.this.getNavigator().clearPathEntity();
        }

        @Override
        public void resetTask() {
            EntityFox.this.setCrouching(false);
            EntityFox.this.extraRollingHeight = 0.0F;
            EntityFox.this.lastExtraRollingHeight = 0.0F;
            EntityFox.this.setRollingHead(false);
            EntityFox.this.setChasing(false);
        }

        @Override
        public void updateTask() {
            EntityLivingBase livingEntity = EntityFox.this.getAttackTarget();
            if (livingEntity != null) {
                EntityFox.this.getLookHelper().setLookPositionWithEntity(livingEntity, 60.0F, 30.0F);
            }

            if (!EntityFox.this.isWalking()) {
                Vec3 vec3d = Vec3.createVectorHelper(EntityFox.this.motionX, EntityFox.this.motionY, EntityFox.this.motionZ);
                if (vec3d.yCoord * vec3d.yCoord < 0.03D && EntityFox.this.rotationPitch != 0.0F) {
                    EntityFox.this.rotationPitch = lerpAngle(EntityFox.this.rotationPitch, 0.0F, 0.2F);
                } else {
                    double d = Math.sqrt(vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord);
                    double e = Math.signum(-vec3d.yCoord) * Math.acos(d / vec3d.lengthVector()) * 57.2957763671875D;
                    EntityFox.this.rotationPitch = (float) e;
                }
            }

            if (livingEntity != null && EntityFox.this.getDistanceToEntity(livingEntity) <= 2.0F) {
                EntityFox.this.attackEntityAsMob(livingEntity);
            } else if (EntityFox.this.rotationPitch > 0.0F
                    && EntityFox.this.onGround
                    && (float) EntityFox.this.motionY != 0.0F
                    && EntityFox.this.worldObj.getBlock(
                            MathHelper.floor_double(posX),
                            MathHelper.floor_double(posY),
                            MathHelper.floor_double(posZ)) == Blocks.snow_layer) {
                EntityFox.this.rotationPitch = 60.0F;
                EntityFox.this.setAttackTarget(null);
                EntityFox.this.setWalking(true);
            }
        }

        private static float lerpAngle(float start, float end, float delta) {
            float f;
            for (f = end - start; f < -180.0F; f += 360.0F);
            while (f >= 180.0F) f -= 360.0F;
            return start + delta * f;
        }
    }

    private class AISwim extends EntityAISwimming {

        public AISwim() {
            super(EntityFox.this);
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
            EntityFox.this.stopActions();
        }

        @Override
        public boolean shouldExecute() {
            return super.shouldExecute();
        }
    }

    /*
    private class GoToVillageGoal extends net.minecraft.entity.ai.goal.GoToVillageGoal {
        public GoToVillageGoal(int unused, int searchRange) {
            super(EntityFox.this, searchRange);
        }

        public void start() {
            EntityFox.this.stopActions();
            super.start();
        }

        public boolean canStart() {
            return super.canStart() && this.canGoToVillage();
        }

        public boolean shouldContinue() {
            return super.shouldContinue() && this.canGoToVillage();
        }

        private boolean canGoToVillage() {
            return !EntityFox.this.isPlayerSleeping()
                    && !EntityFox.this.isSitting()
                    && !EntityFox.this.isAggressive()
                    && EntityFox.this.getAttackTarget() == null;
        }
    }*/

    private class AIEscapeWhenNotAggressive extends EntityAIPanic {

        private int startTime;
        private static final int MAX_DURATION = 7 * 20;

        public AIEscapeWhenNotAggressive(double speed) {
            super(EntityFox.this, speed);
        }

        @Override
        public boolean shouldExecute() {
            return !EntityFox.this.isAggressive() && super.shouldExecute();
        }

        @Override
        public void startExecuting() {
            startTime = EntityFox.this.ticksExisted;
            super.startExecuting();
        }

        @Override
        public boolean continueExecuting() {
            return super.continueExecuting() && !isTimedOut();
        }

        @Override
        public void resetTask() {
            if (isTimedOut()) {
                EntityFox.this.getNavigator().clearPathEntity();
            }
            super.resetTask();
        }

        private boolean isTimedOut() {
            return EntityFox.this.ticksExisted >= startTime + MAX_DURATION;
        }
    }

    private class AIStopWandering extends EntityAIBase {

        int timer;

        public AIStopWandering() {
            setMutexBits(7);
        }

        @Override
        public boolean shouldExecute() {
            return EntityFox.this.isWalking();
        }

        @Override
        public boolean continueExecuting() {
            return this.shouldExecute() && this.timer > 0;
        }

        @Override
        public void startExecuting() {
            this.timer = 40;
        }

        @Override
        public void resetTask() {
            EntityFox.this.setWalking(false);
        }

        @Override
        public void updateTask() {
            --this.timer;
        }
    }

    public static class FoxData implements IEntityLivingData {

        public final EntityFox.Type type;
        private int spawnCount;
        private final boolean babyAllowed;
        private final float babyChance;

        public FoxData(EntityFox.Type type) {
            this.type = type;
            this.babyAllowed = false;
            this.babyChance = 0.05F;
        }

        public int getSpawnedCount() {
            return this.spawnCount;
        }

        public void countSpawned() {
            ++this.spawnCount;
        }

        public boolean canSpawnBaby() {
            return this.babyAllowed;
        }

        public float getBabyChance() {
            return this.babyChance;
        }
    }

    private class AIEatSweetBerries extends EntityAIBase {

        public final double speed;
        protected int cooldown;
        protected int tryingTime;
        private int safeWaitingTime;
        protected BlockPos targetPos;
        private boolean reached;
        private final int range;
        private final int maxYDifference;
        protected int lowestY;
        protected int timer;

        public AIEatSweetBerries(double speed, int range, int maxYDifference) {
            this.targetPos = BlockPos.ORIGIN;
            this.speed = speed;
            this.range = range;
            this.lowestY = 0;
            this.maxYDifference = maxYDifference;
            this.setMutexBits(5);
        }

        @Override
        public boolean shouldExecute() {
            if (EntityFox.this.isPlayerSleeping()) return false;

            if (this.cooldown > 0) {
                --this.cooldown;
                return false;
            } else {
                this.cooldown = this.getInterval(EntityFox.this);
                return this.findTargetPos();
            }
        }

        private int getInterval(EntityCreature mob) {
            return 200 + mob.getRNG().nextInt(200);
        }

        @Override
        public boolean continueExecuting() {
            return this.tryingTime >= -this.safeWaitingTime
                    && this.tryingTime <= 1200
                    && this.isTargetPos(EntityFox.this.worldObj, this.targetPos);
        }

        @Override
        public void startExecuting() {
            this.timer = 0;
            EntityFox.this.setSitting(false);
            this.startMovingToTarget();
            this.tryingTime = 0;
            this.safeWaitingTime = EntityFox.this.getRNG().nextInt(EntityFox.this.getRNG().nextInt(1200) + 1200) + 1200;
        }

        private void startMovingToTarget() {
            EntityFox.this.getNavigator().tryMoveToXYZ(
                    (double) ((float) this.targetPos.getX()) + 0.5D,
                    this.targetPos.getY() + 1,
                    (double) ((float) this.targetPos.getZ()) + 0.5D,
                    this.speed);
        }

        private double getDesiredSquaredDistanceToTarget() {
            return 2.0D;
        }

        private BlockPos getTargetPos() {
            return this.targetPos.up();
        }

        @Override
        public void updateTask() {
            if (this.hasReached()) {
                if (this.timer >= 40) {
                    this.eatSweetBerry();
                } else {
                    ++this.timer;
                }
            } else if (!this.hasReached() && EntityFox.this.rand.nextFloat() < 0.05F) {
                EntityFox.this.playSound(Reference.MCAssetVer + ":entity.fox.sniff", 1.0F, 1.0F);
            }

            BlockPos blockPos = this.getTargetPos();
            if (EntityFox.this.getDistanceSq(blockPos.getX(), blockPos.getY(), blockPos.getZ()) >= Math.pow(this.getDesiredSquaredDistanceToTarget(), 2)) {
                this.reached = false;
                ++this.tryingTime;
                if (this.shouldResetPath()) {
                    EntityFox.this.getNavigator().tryMoveToXYZ(
                            (double) ((float) blockPos.getX()) + 0.5D,
                            blockPos.getY(),
                            (double) ((float) blockPos.getZ()) + 0.5D,
                            this.speed);
                }
            } else {
                this.reached = true;
                --this.tryingTime;
            }
        }

        private boolean shouldResetPath() {
            return this.tryingTime % 100 == 0;
        }

        private boolean hasReached() {
            return this.reached;
        }

        protected boolean findTargetPos() {
            int mobX = MathHelper.floor_double(EntityFox.this.posX);
            int mobY = MathHelper.floor_double(EntityFox.this.posY);
            int mobZ = MathHelper.floor_double(EntityFox.this.posZ);
            int bx, by, bz;

            for(int k = this.lowestY; k <= this.maxYDifference; k = k > 0 ? -k : 1 - k) {
                for(int l = 0; l < this.range; ++l) {
                    for(int m = 0; m <= l; m = m > 0 ? -m : 1 - m) {
                        for(int n = m < l && m > -l ? l : 0; n <= l; n = n > 0 ? -n : 1 - n) {
                            bx = mobX + m;
                            by = mobY + k - 1;
                            bz = mobZ + n;
                            if (EntityFox.this.isWithinHomeDistance(bx, by, bz)
                                    && this.isTargetPos(EntityFox.this.worldObj, bx, by, bz)) {
                                this.targetPos = new BlockPos(bx, by, bz);
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }

        private boolean isTargetPos(World world, BlockPos pos) {
            return this.isTargetPos(world, pos.getX(), pos.getY(), pos.getZ());
        }

        private boolean isTargetPos(World world, int bx, int by, int bz) {
            Block block = world.getBlock(bx, by, bz);
            if (block instanceof BlockBerryBush) {
                return world.getBlockMetadata(bx, by, bz) >= 2;
            }
            return false;
        }

        private void eatSweetBerry() {
            if (EntityFox.this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                int x = this.targetPos.getX();
                int y = this.targetPos.getY();
                int z = this.targetPos.getZ();
                Block block = EntityFox.this.worldObj.getBlock(x, y, z);

                if (block instanceof BlockBerryBush) {
                    int i = worldObj.getBlockMetadata(x, y, z);
                    int j = 1 + EntityFox.this.worldObj.rand.nextInt(2) + (i == 3 ? 1 : 0);
                    ItemStack itemStack = EntityFox.this.getHeldItem();
                    if (itemStack == null) {
                        EntityFox.this.setCurrentItemOrArmor(0, ModItems.SWEET_BERRIES.newItemStack());
                        --j;
                    }

                    if (j > 0 && !EntityFox.this.worldObj.isRemote) {
                        float f = 0.7F;
                        double d0 = (double) (EntityFox.this.worldObj.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                        double d1 = (double) (EntityFox.this.worldObj.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                        double d2 = (double) (EntityFox.this.worldObj.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                        EntityItem entityitem = new EntityItem(
                                EntityFox.this.worldObj,
                                (double) x + d0,
                                (double) y + d1,
                                (double) z + d2,
                                ModItems.SWEET_BERRIES.newItemStack(j));
                        entityitem.delayBeforeCanPickup = 10;
                        EntityFox.this.worldObj.spawnEntityInWorld(entityitem);
                    }

                    EntityFox.this.playSound(Reference.MCAssetVer + ":block.sweet_berry_bush.pick_berries", 1.0F, 1.0F);
                    EntityFox.this.worldObj.setBlockMetadataWithNotify(x, y, z, 1, 2);
                }
            }
        }
    }

    private class AISitDownAndLookAround extends EntityFox.AICalmDown {

        private double lookX;
        private double lookZ;
        private int timer;
        private int counter;

        public AISitDownAndLookAround() {
            super(null);
            this.setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            return isIdle() && EntityFox.this.rand.nextFloat() < 0.02F;
        }

        private boolean isIdle() {
            return EntityFox.this.getEntityToAttack() == null
                    && !EntityFox.this.isPlayerSleeping()
                    && EntityFox.this.getAttackTarget() == null
                    && EntityFox.this.getNavigator().noPath()
                    && !EntityFox.this.isChasing()
                    && !EntityFox.this.isInSneakingPose()
                    && EntityFox.this.getHealth() > EntityFox.this.getMaxHealth() / 4f
                    && !this.canNotCalmDown();
        }

        @Override
        public boolean continueExecuting() {
            return this.counter > 0 && isIdle();
        }

        @Override
        public void startExecuting() {
            this.chooseNewAngle();
            this.counter = 2 + EntityFox.this.rand.nextInt(3);
            EntityFox.this.setSitting(true);
            EntityFox.this.getNavigator().clearPathEntity();
        }

        @Override
        public void resetTask() {
            EntityFox.this.setSitting(false);
        }

        @Override
        public void updateTask() {
            --this.timer;
            if (this.timer <= 0) {
                --this.counter;
                this.chooseNewAngle();
            }

            EntityFox.this.getLookHelper().setLookPosition(
                    EntityFox.this.posX + this.lookX,
                    EntityFox.this.posY + EntityFox.this.getEyeHeight(),
                    EntityFox.this.posZ + this.lookZ,
                    (float) EntityFox.this.getBodyYawSpeed(),
                    (float) EntityFox.this.getLookPitchSpeed());
        }

        private void chooseNewAngle() {
            double d = Math.PI * 2.0D * EntityFox.this.rand.nextDouble();
            this.lookX = Math.cos(d);
            this.lookZ = Math.sin(d);
            this.timer = 80 + EntityFox.this.rand.nextInt(20);
        }
    }

    private class AIDelayedCalmDown extends EntityFox.AICalmDown {

        private int timer;

        public AIDelayedCalmDown() {
            super(null);
            this.timer = EntityFox.this.rand.nextInt(140);
            this.setMutexBits(7);
        }

        @Override
        public boolean shouldExecute() {
            if (EntityFox.this.moveStrafing == 0.0F && EntityFox.this.onGround && EntityFox.this.moveForward == 0.0F) {
                return this.canCalmDown() || EntityFox.this.isPlayerSleeping();
            } else {
                return false;
            }
        }

        @Override
        public boolean continueExecuting() {
            return this.canCalmDown();
        }

        private boolean canCalmDown() {
            if (this.timer > 0) {
                --this.timer;
                return false;
            } else {
                return EntityFox.this.worldObj.isDaytime() && this.isAtFavoredLocation() && !this.canNotCalmDown();
            }
        }

        @Override
        public void resetTask() {
            this.timer = EntityFox.this.rand.nextInt(140);
            EntityFox.this.stopActions();
        }

        @Override
        public void startExecuting() {
            EntityFox.this.setSitting(false);
            EntityFox.this.setCrouching(false);
            EntityFox.this.setRollingHead(false);
            EntityFox.this.setJumping(false);
            EntityFox.this.setSleeping(true);
            EntityFox.this.getNavigator().clearPathEntity();
            EntityFox.this.getMoveHelper().setMoveTo(EntityFox.this.posX, EntityFox.this.posY, EntityFox.this.posZ, 0.0D);
        }
    }

    private abstract class AICalmDown extends EntityAIBase {

        private final TargetPredicate WORRIABLE_ENTITY_PREDICATE;

        private AICalmDown() {
            this.WORRIABLE_ENTITY_PREDICATE = (new TargetPredicate())
                    .setBaseMaxDistance(12.0D)
                    .includeHidden()
                    .setPredicate(EntityFox.this.new WorriableEntityFilter());
        }

        protected boolean isAtFavoredLocation() {
            BlockPos blockPos = new BlockPos(EntityFox.this.posX, EntityFox.this.boundingBox.maxY, EntityFox.this.posZ);
            return !EntityFox.this.worldObj.canBlockSeeTheSky(blockPos.getX(), blockPos.getY(), blockPos.getZ()) &&
                    EntityFox.this.getBlockPathWeight(blockPos.getX(), blockPos.getY(), blockPos.getZ()) >= 0.0F;
        }

        protected boolean canNotCalmDown() {
            List<EntityLivingBase> list = EntityFox.this.worldObj.selectEntitiesWithinAABB(
                    EntityLivingBase.class,
                    EntityFox.this.boundingBox.expand(12.0D, 6.0D, 12.0D),
                    null);

            List<EntityLivingBase> list2 = new ArrayList<>();
            for (EntityLivingBase entityLivingBase : list) {
                if (this.WORRIABLE_ENTITY_PREDICATE.test(EntityFox.this, entityLivingBase)) {
                    list2.add(entityLivingBase);
                }
            }

            return !list2.isEmpty();
        }

        // $FF: synthetic method
        AICalmDown(Object arg) {
            this();
        }
    }

    private class WorriableEntityFilter implements Predicate<EntityLivingBase> {
        public boolean test(EntityLivingBase livingEntity) {
            if (livingEntity instanceof EntityFox) {
                return false;
            } else if (!(livingEntity instanceof EntityChicken) && !(livingEntity instanceof EntityRabbit) && !(livingEntity instanceof EntityMob)) {
                if (livingEntity instanceof EntityTameable) {
                    return !((EntityTameable) livingEntity).isTamed();
                } else if (livingEntity instanceof EntityPlayer player && (SpectatorMode.isSpectator(player) || player.capabilities.isCreativeMode)) {
                    return false;
                } else if (EntityFox.this.canTrust(livingEntity.getUniqueID())) {
                    return false;
                } else {
                    return !livingEntity.isPlayerSleeping() && !livingEntity.isSneaking();
                }
            } else {
                return true;
            }
        }
    }

    private class AIAvoidDaylight extends EntityAIFleeSunExtended {
        private int timer = 100;

        public AIAvoidDaylight(double speed) {
            super(EntityFox.this, speed);
        }

        @Override
        public boolean shouldExecute() {
            if (!EntityFox.this.isPlayerSleeping() && EntityFox.this.getAttackTarget() == null) {
                if (EntityFox.this.worldObj.isThundering()) {
                    return EntityFox.this.worldObj.canBlockSeeTheSky(
                            MathHelper.floor_double(posX),
                            MathHelper.floor_double(posY),
                            MathHelper.floor_double(posZ))
                            // todo when points of interest are added
                            /*&& !EntityFox.this.worldObj.isNearOccupiedPointOfInterest(blockPos)*/
                            && this.targetShadedPos();
                } else if (this.timer > 0) {
                    --this.timer;
                    return false;
                } else {
                    this.timer = 100;
                    return EntityFox.this.worldObj.isDaytime() && EntityFox.this.worldObj.canBlockSeeTheSky(
                            MathHelper.floor_double(posX),
                            MathHelper.floor_double(posY),
                            MathHelper.floor_double(posZ))
                            /*&& !EntityFox.this.worldObj.isNearOccupiedPointOfInterest(blockPos)*/
                            && this.targetShadedPos();
                }
            } else {
                return false;
            }
        }

        @Override
        public void startExecuting() {
            EntityFox.this.stopActions();
            super.startExecuting();
        }
    }

    private class AIDefendFriend extends EntityAICustomNearestAttackableTarget {

        private EntityLivingBase offender;
        private int lastAttackedTime;
        TargetPredicate targetPredicate;

        public AIDefendFriend(Class<? extends Entity> targetEntityClass, boolean checkVisibility,
                              boolean checkCanNavigate, Predicate<EntityLivingBase> targetPredicate) {
            super(EntityFox.this, targetEntityClass, 10, checkVisibility, checkCanNavigate,
                    entity -> entity instanceof EntityLivingBase livingBase && targetPredicate.test(livingBase));
            this.targetPredicate = new TargetPredicate()
                    .setBaseMaxDistance(this.getTargetDistance())
                    .setPredicate(targetPredicate);

            if (!checkVisibility) {
                // Rationale for this: tall grass blocks the view of foxes. since they are so short,
                // this greatly limits their helpfulness in areas with tall grass.
                this.targetPredicate.includeHidden();
            }
        }

        @Override
        public boolean shouldExecute() {
            if (this.targetChance <= 0 || EntityFox.this.getRNG().nextInt(this.targetChance) == 0) {
                for (UUID uUID : EntityFox.this.getTrustedUuids()) {
                    if (uUID != null && EntityFox.this.worldObj instanceof WorldServer) {
                        EntityPlayer player = EntityFox.this.worldObj.func_152378_a(uUID); // getPlayerByUuid
                        if (player != null) {
                            EntityFox.this.friend = player;
                            this.offender = player.getAITarget();
                            int i = player.func_142015_aE();
                            return i != this.lastAttackedTime && this.canTrack(this.offender, this.targetPredicate);
                        }
                    }
                }
            }
            return false;
        }

        private boolean canTrack(EntityLivingBase target, TargetPredicate targetPredicate) {
            if (target == null) {
                return false;
            } else if (!targetPredicate.test(EntityFox.this, target)) {
                return false;
            } else if (!EntityFox.this.isWithinHomeDistance(
                    MathHelper.floor_double(target.posX),
                    MathHelper.floor_double(target.posY),
                    MathHelper.floor_double(target.posZ))) {
                return false;
            } else {
                if (this.nearbyOnly) {
                    if (--this.targetSearchDelay <= 0) {
                        this.targetSearchStatus = 0;
                    }

                    if (this.targetSearchStatus == 0) {
                        this.targetSearchStatus = this.canEasilyReach(target) ? 1 : 2;
                    }

                    return this.targetSearchStatus != 2;
                }
                return true;
            }
        }

        @Override
        public void startExecuting() {
            this.targetEntity = this.offender;
            if (EntityFox.this.friend != null) {
                this.lastAttackedTime = EntityFox.this.friend.getLastAttackerTime();
            }

            EntityFox.this.playSound(Reference.MCAssetVer + ":entity.fox.aggro", 1.0F, 1.0F);
            EntityFox.this.setAggressive(true);
            EntityFox.this.stopSleeping();
            super.startExecuting();
        }

        @Override
        public void resetTask() {
            EntityFox.this.friend = null;
            super.resetTask();
        }
    }

    private class AIMate extends EntityAIMate {

        public AIMate(double chance) {
            super(EntityFox.this, chance);
        }

        @Override
        public void startExecuting() {
            EntityFox.this.stopActions();
            ((EntityFox) this.targetMate).stopActions();
            super.startExecuting();
        }

        @Override
        protected void spawnBaby() {
            EntityFox babyFox = EntityFox.this.createChild(this.targetMate);
            if (babyFox != null) {
                EntityPlayer serverPlayerEntity = EntityFox.this.func_146083_cb();
                EntityPlayer serverPlayerEntity2 = this.targetMate.func_146083_cb();
                EntityPlayer serverPlayerEntity3 = serverPlayerEntity;
                if (serverPlayerEntity != null) {
                    babyFox.addTrustedUuid(serverPlayerEntity.getUniqueID());
                } else {
                    serverPlayerEntity3 = serverPlayerEntity2;
                }

                if (serverPlayerEntity2 != null && serverPlayerEntity != serverPlayerEntity2) {
                    babyFox.addTrustedUuid(serverPlayerEntity2.getUniqueID());
                }

                if (serverPlayerEntity3 != null) {
                    serverPlayerEntity3.triggerAchievement(StatList.field_151186_x);
                }

                EntityFox.this.setGrowingAge(6000);
                this.targetMate.setGrowingAge(6000);
                EntityFox.this.resetInLove();
                this.targetMate.resetInLove();
                babyFox.setGrowingAge(-24000);
                babyFox.setLocationAndAngles(EntityFox.this.posX, EntityFox.this.posY, EntityFox.this.posZ, 0.0F, 0.0F);
                EntityFox.this.worldObj.spawnEntityInWorld(babyFox);
                EntityFox.this.worldObj.setEntityState(EntityFox.this, (byte) 18);
                if (EntityFox.this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
                    EntityFox.this.worldObj.spawnEntityInWorld(new EntityXPOrb(
                            EntityFox.this.worldObj,
                            EntityFox.this.posX,
                            EntityFox.this.posY,
                            EntityFox.this.posZ,
                            EntityFox.this.rand.nextInt(7) + 1));
                }
            }
        }
    }

    private class AIAttack extends EntityAIAttackOnCollide {

        public AIAttack(double speed, boolean pauseWhenIdle) {
            super(EntityFox.this, speed, pauseWhenIdle);
        }

        @Override
        public void startExecuting() {
            EntityFox.this.setRollingHead(false);
            super.startExecuting();
        }

        @Override
        public void updateTask() {
            this.longMemory = true;
            super.updateTask();
        }

        @Override
        public boolean shouldExecute() {
            return !EntityFox.this.isSitting()
                    && !EntityFox.this.isPlayerSleeping()
                    && !EntityFox.this.isInSneakingPose()
                    && !EntityFox.this.isWalking()
                    && super.shouldExecute();
        }
    }

    private class AIMoveToHunt extends EntityAIBase {
        public AIMoveToHunt() {
            this.setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            if (EntityFox.this.isPlayerSleeping()) {
                return false;
            } else {
                EntityLivingBase livingEntity = EntityFox.this.getAttackTarget();
                return livingEntity != null
                        && livingEntity.isEntityAlive()
                        && EntityFox.CHICKEN_AND_RABBIT_FILTER.test(livingEntity)
                        && EntityFox.this.getDistanceSqToEntity(livingEntity) > 36.0D
                        && !EntityFox.this.isInSneakingPose()
                        && !EntityFox.this.isRollingHead()
                        && !EntityFox.this.isJumping;
            }
        }

        @Override
        public void startExecuting() {
            EntityFox.this.setSitting(false);
            EntityFox.this.setWalking(false);
        }

        @Override
        public void resetTask() {
            EntityLivingBase livingEntity = EntityFox.this.getAttackTarget();
            if (livingEntity != null && EntityFox.canJumpChase(EntityFox.this, livingEntity)) {
                EntityFox.this.setRollingHead(true);
                EntityFox.this.setCrouching(true);
                EntityFox.this.getNavigator().clearPathEntity();
                EntityFox.this.getLookHelper().setLookPositionWithEntity(
                        livingEntity,
                        (float) EntityFox.this.getBodyYawSpeed(),
                        (float) EntityFox.this.getLookPitchSpeed());
            } else {
                EntityFox.this.setRollingHead(false);
                EntityFox.this.setCrouching(false);
            }

        }

        @Override
        public void updateTask() {
            EntityLivingBase livingEntity = EntityFox.this.getAttackTarget();
            EntityFox.this.getLookHelper().setLookPositionWithEntity(
                    livingEntity,
                    (float) EntityFox.this.getBodyYawSpeed(),
                    (float) EntityFox.this.getLookPitchSpeed());
            if (EntityFox.this.getDistanceSqToEntity(livingEntity) <= 36.0D) {
                EntityFox.this.setRollingHead(true);
                EntityFox.this.setCrouching(true);
                EntityFox.this.getNavigator().clearPathEntity();
            } else {
                EntityFox.this.getNavigator().tryMoveToEntityLiving(livingEntity, 1.5D);
            }

        }
    }

    private class FoxMoveHelper extends EntityMoveHelper {

        public FoxMoveHelper() {
            super(EntityFox.this);
        }

        @Override
        public void onUpdateMoveHelper() {
            if (EntityFox.this.wantsToPickupItem()) {
                super.onUpdateMoveHelper();
            }
        }
    }

    private class AIPickupItem extends EntityAIBase {

        public AIPickupItem() {
            this.setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            if (EntityFox.this.getHeldItem() != null) {
                return false;
            } else if ((EntityFox.this.getAttackTarget() == null && EntityFox.this.getAITarget() == null)) {
                if (!EntityFox.this.wantsToPickupItem()) {
                    return false;
                } else if (EntityFox.this.rand.nextInt(10) != 0) {
                    return false;
                } else {
                    return canPickUpNearbyItemStack();
                }
            } else {
                return false;
            }
        }

        @Override
        public void updateTask() {
            moveToNearbyItemStack(true);
        }

        @Override
        public void startExecuting() {
            moveToNearbyItemStack(false);
        }

        protected double getFetchSpeed() {
            return 1.2D;
        }

        protected List<EntityItem> getNearbyItemStacks() {
            return EntityFox.this.worldObj.selectEntitiesWithinAABB(
                    EntityItem.class,
                    EntityFox.this.boundingBox.expand(8.0D, 8.0D, 8.0D),
                    EntityFox.PICKABLE_DROP_FILTER);
        }

        protected void moveToNearbyItemStack(boolean onlyIfNotHoldingItem) {
            List<EntityItem> list = getNearbyItemStacks();
            if (!list.isEmpty()) {
                EntityItem ei = list.get(0);
                if (!onlyIfNotHoldingItem || EntityFox.this.getHeldItem() == null) {
                    EntityFox.this.getNavigator().tryMoveToEntityLiving(ei, getFetchSpeed());
                }
            }
        }

        protected boolean canPickUpNearbyItemStack() {
            List<EntityItem> nearbyItemStacks = getNearbyItemStacks();
            return !nearbyItemStacks.isEmpty() && EntityFox.this.canPickupItem(nearbyItemStacks.get(0).getEntityItem());
        }
    }

    public enum Type {

        RED(0, "red"),
        SNOW(1, "snow");

        private final int id;
        private final String key;

        Type(int id, String key) {
            this.id = id;
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }

        public int getId() {
            return this.id;
        }

        public static EntityFox.Type byName(String name) {
            if (SNOW.key.equals(name)) {
                return SNOW;
            }
            return RED;
        }

        public static EntityFox.Type fromId(int id) {
            if (id == SNOW.id) return SNOW;
            return RED;
        }

        public static EntityFox.Type fromBiome(BiomeGenBase bgb) {
            return bgb != null && BiomeDictionary.isBiomeOfType(bgb, BiomeDictionary.Type.SNOWY) ? SNOW : RED;
        }
    }
}
