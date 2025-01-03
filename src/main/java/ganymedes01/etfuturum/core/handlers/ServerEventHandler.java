package ganymedes01.etfuturum.core.handlers;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModEnchantments;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.api.HoeRegistry;
import ganymedes01.etfuturum.api.RawOreRegistry;
import ganymedes01.etfuturum.api.StrippedLogRegistry;
import ganymedes01.etfuturum.api.mappings.RawOreDropMapping;
import ganymedes01.etfuturum.api.mappings.RegistryMapping;
import ganymedes01.etfuturum.blocks.BlockHoney;
import ganymedes01.etfuturum.blocks.BlockMagma;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.compat.ExternalContent;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.*;
import ganymedes01.etfuturum.core.utils.ItemStackMap;
import ganymedes01.etfuturum.core.utils.ItemStackSet;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.elytra.IElytraEntityTrackerEntry;
import ganymedes01.etfuturum.elytra.IElytraPlayer;
import ganymedes01.etfuturum.entities.*;
import ganymedes01.etfuturum.entities.ai.EntityAIOpenCustomDoor;
import ganymedes01.etfuturum.gamerule.DoWeatherCycle;
import ganymedes01.etfuturum.gamerule.PlayersSleepingPercentage;
import ganymedes01.etfuturum.gamerule.RandomTickSpeed;
import ganymedes01.etfuturum.items.ItemArrowTipped;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.network.AttackYawMessage;
import ganymedes01.etfuturum.network.BlackHeartParticlesMessage;
import ganymedes01.etfuturum.recipes.ModRecipes;
import ganymedes01.etfuturum.spectator.SpectatorMode;
import ganymedes01.etfuturum.storage.EtFuturumPlayer;
import ganymedes01.etfuturum.tileentities.TileEntityGateway;
import ganymedes01.etfuturum.world.EtFuturumWorldListener;
import ganymedes01.etfuturum.world.nether.biome.utils.NetherBiomeManager;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.*;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.FuelBurnTimeEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("deprecation")
public class ServerEventHandler {

	public static final ServerEventHandler INSTANCE = new ServerEventHandler();
	public static HashSet<EntityPlayerMP> playersClosedContainers = new HashSet<>();
	private static final Map<EntityPlayer, List<ItemStack>> armorTracker = new WeakHashMap<>();
	private static final Set<EntityFallingBlock> fallingConcreteBlocks = new HashSet<>();

	private ServerEventHandler() {
	}

	@SubscribeEvent
	public void onPlayerPickXP(PlayerPickupXpEvent event) {
		ModEnchantments.onPlayerPickupXP(event);
	}

	@SubscribeEvent
	public void livingJump(LivingEvent.LivingJumpEvent event) {
		int x = MathHelper.floor_double(event.entity.posX);
		int y = MathHelper.floor_double(event.entity.posY - 0.20000000298023224D - event.entity.yOffset);
		int z = MathHelper.floor_double(event.entity.posZ);
		if (event.entity.worldObj.getBlock(x, y, z) instanceof BlockHoney) {
			event.entity.motionY *= .5D;
		}
	}

	@SubscribeEvent
	public void livingUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if (entity.worldObj == null) return;

		ModEnchantments.onLivingUpdate(entity);

		double x = entity.posX;
		double y = entity.posY;
		double z = entity.posZ;
		if (ConfigBlocksItems.enableMagmaBlock) {
			if (!entity.isImmuneToFire() && !entity.isSneaking() && entity.onGround && entity.worldObj.getBlock(MathHelper.floor_double(x), (int) (y - .45D), MathHelper.floor_double(z)) == ModBlocks.MAGMA.get()) {
				if (ConfigEnchantsPotions.enableFrostWalker && EnchantmentHelper.getEnchantmentLevel(ConfigEnchantsPotions.frostWalkerID, entity.getEquipmentInSlot(1)) == 0) {
					entity.attackEntityFrom(BlockMagma.HOT_FLOOR, 1);
				}
			}
		}

		if (ConfigMixins.stepHeightFix && entity.stepHeight == .5F) {
			entity.stepHeight = .6F;
		}

		if (ConfigMixins.enableElytra && entity instanceof IElytraPlayer) {
			((IElytraPlayer) entity).tickElytra();
		}

		if (EntitySquid.class.equals(entity.getClass())) {
			EntitySquid sq = (EntitySquid) entity;

			if (ConfigEntities.enableSquidInk && "Nelly".equals(sq.getCustomNameTag())) {
				Vec3 a = sq.getLookVec().normalize();
				sq.fallDistance = 0.0f;
				sq.addVelocity(a.xCoord * 0.08, 0.08, a.zCoord * 0.08);
				sq.motionY = 0.08;
				sq.velocityChanged = true;
			}
		}


		if (ConfigSounds.armorEquip && !entity.worldObj.isRemote && entity instanceof EntityPlayer player && !(entity instanceof FakePlayer)) {

			if (!SpectatorMode.isSpectator(player)) {
				if (!armorTracker.containsKey(player)) {
					// Items currently on the player
					ItemStack playerBoots = player.getEquipmentInSlot(1);
					ItemStack playerLeggings = player.getEquipmentInSlot(2);
					ItemStack playerChestplate = player.getEquipmentInSlot(3);
					ItemStack playerHelmet = player.getEquipmentInSlot(4);
					armorTracker.put(player, Arrays.asList(playerBoots, playerLeggings, playerChestplate, playerHelmet));
				} else {
					List<ItemStack> armorList = armorTracker.get(player);

					String itemEquippedSound = "";
					ItemStack storedArmor;
					ItemStack currentArmor;
					for (int i = 0; i < 4; i++) {
						storedArmor = armorList.get(i);
						currentArmor = player.getEquipmentInSlot(i + 1);
						if (currentArmor != null && (storedArmor == null || (!currentArmor.getItem().equals(storedArmor.getItem()) ||
								!((currentArmor.stackTagCompound != null || storedArmor.stackTagCompound == null) && (currentArmor.stackTagCompound == null || currentArmor.stackTagCompound.equals(storedArmor.stackTagCompound)))))) {
							// Equipment is in the slot and either the NBT thinks there's not an item already there, or that the item is different in some way that's not its durability.
							if (player.inventory.isItemValidForSlot(i, currentArmor)) {
								String armorString = currentArmor.getUnlocalizedName().toLowerCase();
								if (EtFuturum.stringListContainsPhrase(ConfigSounds.newArmorEquipCustomRulesNone, armorString)) {
									continue;
								} else if (armorString.contains("chain") || EtFuturum.stringListContainsPhrase(ConfigSounds.newArmorEquipCustomRulesChain, armorString)) {
									itemEquippedSound = "item.armor.equip_chain";
								} else if (armorString.contains("diamond") || EtFuturum.stringListContainsPhrase(ConfigSounds.newArmorEquipCustomRulesDiamond, armorString)) {
									itemEquippedSound = "item.armor.equip_diamond";
								} else if (armorString.contains("gold") || EtFuturum.stringListContainsPhrase(ConfigSounds.newArmorEquipCustomRulesGold, armorString)) {
									itemEquippedSound = "item.armor.equip_gold";
								} else if (armorString.contains("iron") || EtFuturum.stringListContainsPhrase(ConfigSounds.newArmorEquipCustomRulesIron, armorString)) {
									itemEquippedSound = "item.armor.equip_iron";
								} else if (armorString.contains("leather") || EtFuturum.stringListContainsPhrase(ConfigSounds.newArmorEquipCustomRulesLeather, armorString)) {
									itemEquippedSound = "item.armor.equip_leather";
								} else if (armorString.contains("netherite") || EtFuturum.stringListContainsPhrase(ConfigSounds.newArmorEquipCustomRulesNetherite, armorString)) {
									itemEquippedSound = "item.armor.equip_netherite";
								} else if (armorString.contains("elytra") || EtFuturum.stringListContainsPhrase(ConfigSounds.newArmorEquipCustomRulesElytra, armorString)) {
									itemEquippedSound = "item.armor.equip_elytra";
								} else if (EtFuturum.stringListContainsPhrase(ConfigSounds.newArmorEquipCustomRulesTurtle, armorString)) {
									itemEquippedSound = "item.armor.equip_turtle";
								} else if (currentArmor.getItem() instanceof ItemArmor || EtFuturum.stringListContainsPhrase(ConfigSounds.newArmorEquipCustomRulesGeneric, armorString)) {
									itemEquippedSound = "item.armor.equip_generic";//Something not assigned a sound should be silent if it's not gear, and the user didn't specify that sound.
								}
							}
						}
						armorList.set(i, currentArmor);
						if (!itemEquippedSound.equals("")) { //We picked a sound, stop iterating.
							break;
						}
					}
					// Play a sound if one of the equipment pieces changed
					if (!itemEquippedSound.equals("")) {
						player.worldObj.playSoundAtEntity(player, Reference.MCAssetVer + ":" + itemEquippedSound, 1, 1);
					}
				}
			} else {
				armorTracker.remove(player);
			}
		}
	}

	@SubscribeEvent
	public void onAttackEntityEvent(AttackEntityEvent event) { //Fires when a player presses the attack button on an entity
		if (!event.target.worldObj.isRemote) {
			// --- Left-click an item frame --- //
			if (ConfigSounds.paintingItemFramePlacing && event.target instanceof EntityItemFrame itemframe) {
				if (itemframe.getDisplayedItem() != null) {
					event.target.playSound(Reference.MCAssetVer + ":entity.item_frame.remove_item", 1.0F, 1.0F);
				} else {
					event.target.playSound(Reference.MCAssetVer + ":entity.item_frame.break", 1.0F, 1.0F);
				}
			} else if (ConfigSounds.paintingItemFramePlacing && event.target instanceof EntityPainting) { // --- Break a painting --- //
				event.target.playSound(Reference.MCAssetVer + ":entity.painting.break", 1.0F, 1.0F);
			} else if (ConfigSounds.leashSounds && event.target instanceof EntityLeashKnot) { // --- Break a lead knot --- //
				event.target.playSound(Reference.MCAssetVer + ":entity.leash_knot.break", 1.0F, 1.0F);
			}
		}
	}


	@SubscribeEvent
	public void entityAdded(EntityJoinWorldEvent event) {
		if (event.world.isRemote) return;

		Chunk chunk = event.world.getChunkFromChunkCoords(MathHelper.floor_double(event.entity.posX) >> 4, MathHelper.floor_double(event.entity.posZ) >> 4);

		String sound = "";
		if (ConfigSounds.paintingItemFramePlacing && event.entity instanceof EntityItemFrame) {
			sound = "item_frame";
		} else if (ConfigSounds.paintingItemFramePlacing && event.entity instanceof EntityPainting) {
			sound = "painting";
		} else if (ConfigSounds.leashSounds && event.entity instanceof EntityLeashKnot) {
			sound = "leash_knot";
		}
		if (!sound.equals("")) {
			event.world.playSoundAtEntity(event.entity, Reference.MCAssetVer + ":entity." + sound + ".place", 1.0F, 1.0F);
			return;
		}


		if (ConfigBlocksItems.enableNewBoats && ConfigBlocksItems.replaceOldBoats) {
			if (event.entity.getClass() == EntityBoat.class) {
				EntityNewBoat boat = new EntityNewBoat(event.world);
				event.entity.rotationYaw += 90;
				replaceEntity(event.entity, boat, event.world, chunk);
				boat.setBoatType("minecraft", "oak");
				event.setCanceled(true);
				return;
			}
		}

		if (ConfigEntities.enableVillagerZombies && event.entity.getClass() == EntityZombie.class && ((EntityZombie) event.entity).isVillager()) {
			replaceEntity(event.entity, new EntityZombieVillager(event.world), event.world, chunk);
			event.setCanceled(true);
			return;
		}

		if (ConfigEntities.enableShearableSnowGolems && event.entity.getClass() == EntitySnowman.class) {
			Entity entity = new EntityNewSnowGolem(event.world);
			replaceEntity(event.entity, entity, event.world, chunk);
			entity.getDataWatcher().updateObject(12, (byte) 1);
			event.setCanceled(true);
		}
	}

	private final Set<Chunk> loadedChunks = Collections.newSetFromMap(new WeakHashMap<>());

	@SubscribeEvent
	public void chunkLoad(ChunkEvent.Load event) {
		loadedChunks.add(event.getChunk());
	}

	@SubscribeEvent
	public void chunkUnload(ChunkEvent.Unload event) {
		loadedChunks.remove(event.getChunk());
	}

	private void replaceEntity(Entity oldEntity, Entity newEntity, World world, Chunk chunk) {
		newEntity.copyDataFrom(oldEntity, true);
		if (loadedChunks.contains(chunk)) { // Use this list because somehow chunk.isChunkLoaded is always true here...
			// World#addLoadedEntities has already run for the chunk, we don't have to worry about conflicting with it
			world.spawnEntityInWorld(newEntity);
		} else {
			// don't add to tracker, because World#addLoadedEntities will also do it
			chunk.addEntity(newEntity);
		}
		oldEntity.setDead();
	}

	@SubscribeEvent
	public void livingAttack(LivingAttackEvent event) {
		if (event.source == DamageSource.wither && event.entityLiving instanceof EntitySkeleton && ((EntitySkeleton) event.entityLiving).getSkeletonType() == 1) {
			event.setCanceled(true);
			return;
		}

		if (event.source instanceof EntityDamageSourceIndirect dmgSrc) {
			if (dmgSrc.getSourceOfDamage() instanceof EntityTippedArrow tippedArrow) {
				if (!tippedArrow.worldObj.isRemote && dmgSrc.getEntity() instanceof EntityLivingBase) {

					List<PotionEffect> list = ((ItemArrowTipped) ModItems.TIPPED_ARROW.get()).getEffects(tippedArrow.getArrow());
					Iterator<PotionEffect> iterator1 = list.iterator();

					while (iterator1.hasNext()) {
						PotionEffect potioneffect = iterator1.next();
						int i = potioneffect.getPotionID();

						if (Potion.potionTypes[i].isInstant()) {
							Potion.potionTypes[i].affectEntity((EntityLivingBase) dmgSrc.getEntity(), event.entityLiving, potioneffect.getAmplifier(), 1D);
						} else {
							event.entityLiving.addPotionEffect(new PotionEffect(i, potioneffect.getDuration(), potioneffect.getAmplifier()));
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void arrowNock(ArrowNockEvent event) {
		if (event.result == null)
			return;
		IInventory invt = event.entityPlayer.inventory;
		for (int i = 0; i < invt.getSizeInventory(); i++) {
			ItemStack stack = invt.getStackInSlot(i);
			if (stack == null || stack.stackSize <= 0)
				continue;
			if (stack.getItem() == Items.arrow)
				return;
			if (stack.getItem() == ModItems.TIPPED_ARROW.get()) {
				event.setCanceled(true);
				event.entityPlayer.setItemInUse(event.result, event.result.getItem().getMaxItemUseDuration(event.result));
				return;
			}
		}
	}

	@SubscribeEvent
	public void arrowLoose(ArrowLooseEvent event) {
		if (event.bow == null)
			return;

		IInventory invt = event.entityPlayer.inventory;
		for (int i = 0; i < invt.getSizeInventory(); i++) {
			ItemStack arrow = invt.getStackInSlot(i);
			if (arrow != null && arrow.stackSize > 0 && arrow.getItem() == ModItems.TIPPED_ARROW.get()) {
				float charge = event.charge / 20.0F;
				charge = (charge * charge + charge * 2.0F) / 3.0F;

				if (charge < 0.1D)
					return;
				if (charge > 1.0F)
					charge = 1.0F;

				EntityTippedArrow arrowEntity = new EntityTippedArrow(event.entityPlayer.worldObj, event.entityPlayer, charge * 2.0F);
				arrowEntity.setArrow(arrow);

				if (charge == 1.0F)
					arrowEntity.setIsCritical(true);

				int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, event.bow);
				if (power > 0)
					arrowEntity.setDamage(arrowEntity.getDamage() + power * 0.5D + 0.5D);

				int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, event.bow);
				if (punch > 0)
					arrowEntity.setKnockbackStrength(punch);

				if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, event.bow) > 0)
					arrowEntity.setFire(100);

				event.bow.damageItem(1, event.entityPlayer);
				event.entityPlayer.worldObj.playSoundAtEntity(event.entityPlayer, "random.bow", 1.0F, 1.0F / (event.entityPlayer.worldObj.rand.nextFloat() * 0.4F + 1.2F) + charge * 0.5F);

				if (!event.entityPlayer.capabilities.isCreativeMode && --arrow.stackSize <= 0)
					event.entityPlayer.inventory.setInventorySlotContents(i, null);

				if (!event.entityPlayer.worldObj.isRemote)
					event.entityPlayer.worldObj.spawnEntityInWorld(arrowEntity);
				event.setCanceled(true);
				return;
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoadFromFileEvent(PlayerEvent.LoadFromFile event) {
		if (!ConfigBlocksItems.enableEnchantingTable) return;
		Path file = event.getPlayerFile(Reference.MOD_ID).toPath();

		if (Files.exists(file)) {
			EtFuturumPlayer storage = EtFuturumPlayer.get(event.entityPlayer);

			try {
				try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
					int seed = Integer.parseInt(reader.readLine());
					storage.setEnchantmentSeed(seed);
				} catch (NumberFormatException ignore) {
					// NO-OP
				} finally {
					Files.delete(file);
				}
			} catch (IOException ignore) {
				// NO-OP
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void harvestEvent(BlockEvent.HarvestDropsEvent event) {
		if (ConfigFunctions.enableSilkTouchingMushrooms && event.isSilkTouching)
			if (event.block == Blocks.brown_mushroom_block) {
				event.drops.clear();
				event.drops.add(new ItemStack(ModBlocks.BROWN_MUSHROOM.get()));
			} else if (event.block == Blocks.red_mushroom_block) {
				event.drops.clear();
				event.drops.add(new ItemStack(ModBlocks.RED_MUSHROOM.get()));
			}

		if (ConfigFunctions.enableSticksFromDeadBushes) {
			if (event.block == Blocks.deadbush) {
				boolean isShears = event.harvester != null && event.harvester.getCurrentEquippedItem() != null && event.harvester.getCurrentEquippedItem().getItem() instanceof ItemShears;
				if (event.harvester == null || event.harvester.getCurrentEquippedItem() == null || !isShears) {
					event.drops.add(new ItemStack(Items.stick, event.world.rand.nextInt(3)));
				}
			}
		}

		if (ConfigBlocksItems.enableRawOres && !event.isSilkTouching) {
			//Looks at the list of drops, and replaces all drops with its respective raw ore
			//For example all oreIron in the drops turn into Raw Iron
			for (int i = 0; i < event.drops.size(); i++) {
				ItemStack stack = event.drops.get(i);
				for (String oreName : EtFuturum.getOreStrings(stack)) {
					//For some reason this list is always empty for items which were added during the event being fired
					RawOreDropMapping mapping = RawOreRegistry.getOreMap().get(oreName);
					if (mapping != null && stack != null && !EtFuturum.dictTagsStartWith(stack, "raw")) {
						event.drops.set(i, new ItemStack(mapping.getObject(), mapping.getDropAmount(event.world.rand, event.fortuneLevel), mapping.getMeta()));
						break;
					}
				}
			}
		}

		if (ConfigFunctions.enableShearableCobwebs) {
			if (event.block == Blocks.web && event.harvester != null) {
				ItemStack stack = event.harvester.getCurrentEquippedItem();
				if (stack != null && stack.getItem() instanceof ItemShears) {
					event.drops.clear();
					event.drops.add(new ItemStack(Blocks.web));
				}
			}
		}
	}

	@SubscribeEvent
	public void onBlockBroken(BlockEvent.BreakEvent event) {
		if (ConfigFunctions.enableHoeMining && event.block.getBlockHardness(event.world, event.x, event.y, event.z) != 0.0D) {
			ItemStack itemstack = event.getPlayer().getHeldItem();
			if (itemstack != null && itemstack.getItem() instanceof ItemHoe) {
				itemstack.damageItem(1, event.getPlayer());
			}
		}
	}

	@SubscribeEvent
	public void breakSpeedEvent(PlayerEvent.BreakSpeed event) {
		boolean flag = false;
		float toolSpeed = 0;
		float speedModifier = 0;
		if (ConfigFunctions.enableHoeMining && HoeRegistry.hoeArrayHas(event.block)) {
			ItemStack stack = event.entityPlayer.getHeldItem();
			if (stack != null && stack.getItem() instanceof ItemHoe) {
				try {
					Item hoe = stack.getItem();
					toolSpeed = getHoeSpeed(hoe);
					speedModifier = this.speedModifier(event.entityPlayer, event.block, event.metadata, toolSpeed);
					flag = true;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}

		}
		if (flag)
			event.newSpeed = event.originalSpeed + toolSpeed + speedModifier;
	}

	/**
	 * Return 0 if the input is not a tool.
	 * Gets private tool speed value from tool material.
	 * Now cleaned up and uses Access Transformers for better performance!
	 *
	 * @param item
	 */
	public float getHoeSpeed(Item item) {
		float returnValue = 0;
		try {
			if (item instanceof ItemHoe || item instanceof ItemTool) {
				Item.ToolMaterial theToolMaterial;
				if (item instanceof ItemTool) {
					theToolMaterial = ((ItemTool) item).toolMaterial;
				} else {
					theToolMaterial = ((ItemHoe) item).theToolMaterial;
				}
				returnValue = theToolMaterial.getEfficiencyOnProperMaterial();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	public float speedModifier(EntityPlayer entity, Block block, int meta, float digSpeed) {
		float moddedDigSpeed = 1;

		int i = EnchantmentHelper.getEfficiencyModifier(entity);
		ItemStack itemstack = entity.inventory.getCurrentItem();

		if (i > 0 && itemstack != null) {
			float f1 = i * i + 1;

			//boolean canHarvest = ForgeHooks.canToolHarvestBlock(block, meta, itemstack); // TODO Do you even care if it is harvestable? Tbh not sure of what to slash the speed by if it isn't
			moddedDigSpeed += f1;
		}

		if (entity.isPotionActive(Potion.digSpeed)) {
			moddedDigSpeed *= 1.0F + (entity.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
		}

		if (entity.isPotionActive(Potion.digSlowdown)) {
			moddedDigSpeed *= 1.0F - (entity.getActivePotionEffect(Potion.digSlowdown).getAmplifier() + 1) * 0.2F;
		}

		if (entity.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(entity)) {
			moddedDigSpeed /= 5.0F;
		}

		if (!entity.onGround) {
			moddedDigSpeed /= 5.0F;
		}

		return moddedDigSpeed - 1 < 0 ? 0 : moddedDigSpeed - 1;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void captureLastSideHit(PlayerInteractEvent event) {
		//I need this because onPlaceBlock doesn't tell us what side we hit.
		//Using a MovingObjectPosition doesn't work either because that event fires AFTER the trapdoor is placed, so the MOP hits the trapdoor.
		if (ConfigFunctions.enableFloatingTrapDoors && event.action == Action.RIGHT_CLICK_BLOCK && !SpectatorMode.isSpectator(event.entityPlayer)) {
			sideHit.set(event.face);
		}
	}

	private final ThreadLocal<Integer> sideHit = new ThreadLocal<>();

	@SubscribeEvent
	public void onPlaceBlock(BlockEvent.PlaceEvent event) {
		if (ConfigFunctions.enableFloatingTrapDoors && event.placedBlock instanceof BlockTrapDoor && (sideHit.get() == 0 || sideHit.get() == 1)) {
			int l = (MathHelper.floor_double(event.player.rotationYaw * 4.0F / 360.0F + 0.5D) + 1) & 3;
			if (l == 0) {
				l = 2;
			} else if (l == 3) {
				l = 1;
			} else if (l == 1) {
				l = 0;
			} else {
				l = 3;
			}
			if (sideHit.get() == 0) {
				l += 8;
			}
			event.world.setBlockMetadataWithNotify(event.x, event.y, event.z, l, 2);
		}
	}

	@SubscribeEvent
	public void onPlayerInteractNonVanilla(PlayerInteractEvent event) {
		if (event.action == Action.RIGHT_CLICK_AIR) {
			EntityPlayer player = event.entityPlayer;
			ItemStack heldStack = player.getHeldItem();
			World world = event.world;
			//Firework boosting
			if (ConfigMixins.enableElytra && heldStack != null && heldStack.getItem() == Items.fireworks && ((IElytraPlayer) player).etfu$isElytraFlying()) {
				player.swingItem();
				if (!world.isRemote) {
					EntityBoostingFireworkRocket entityfireworkrocket = new EntityBoostingFireworkRocket(world, heldStack, player);
					world.spawnEntityInWorld(entityfireworkrocket);

					if (!player.capabilities.isCreativeMode) {
						--heldStack.stackSize;
					}
					event.useItem = Result.ALLOW;
				}
			}
		}
	}

	@SubscribeEvent
	public void onBoneMeal(BonemealEvent event) {
		if (ConfigSounds.bonemealing && event.block instanceof IGrowable && !event.world.isRemote &&
				((IGrowable) event.block).func_149851_a/*canFertilize*/(event.world, event.x, event.y, event.z, false)) { //Last arg should always be false because it typically checks for isRemote
			event.world.playSoundEffect(event.x + .5F, event.y + .5F, event.z + .5F, Reference.MCAssetVer + ":item.bone_meal.use", 1, 1);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		EntityPlayer player = event.entityPlayer;
		if ((event.action == Action.RIGHT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_AIR) && !SpectatorMode.isSpectator(player)) {
			if (player != null) {
				final ItemStack heldStack = player.getHeldItem();
				final World world = event.world;
				final int x = event.x;
				final int y = event.y;
				final int z = event.z;
				final Block oldBlock = world.getBlock(x, y, z);
				final int meta = world.getBlockMetadata(x, y, z);
				final int side = event.face;
				if (player.canPlayerEdit(x, y, z, side, heldStack)) {
					if (event.getResult() == event.useItem) {
						//Eye of Ender place sounds
						if (ConfigSounds.endPortalFillSounds && heldStack != null && !world.isRemote && heldStack.getItem() == Items.ender_eye && oldBlock == Blocks.end_portal_frame && !BlockEndPortalFrame.isEnderEyeInserted(meta)) {
							world.playSoundEffect(x + .5F, y + .5F, z + .5F, Reference.MCAssetVer + ":block.end_portal_frame.fill", 1, 1);
							int j1 = meta & 3;
							int j2 = 0;
							int k1 = 0;
							boolean flag1 = false;
							boolean flag = true;
							int k2 = Direction.rotateRight[j1];
							int l1;
							int i2;
							int l2;

							for (l1 = -2; l1 <= 2; ++l1) {
								l2 = x + Direction.offsetX[k2] * l1;
								i2 = z + Direction.offsetZ[k2] * l1;

								if (world.getBlock(l2, y, i2) == Blocks.end_portal_frame) {
									if (!BlockEndPortalFrame.isEnderEyeInserted(world.getBlockMetadata(l2, y, i2))) {
										if (l2 != x || i2 != z) { //We add this so it doesn't care for the block clicked at, as the eye won't be there quite yet.
											flag = false;
											break;
										}
									}

									k1 = l1;

									if (!flag1) {
										j2 = l1;
										flag1 = true;
									}
								}
							}
							if (flag && k1 == j2 + 2) {
								for (l1 = j2; l1 <= k1; ++l1) {
									l2 = x + Direction.offsetX[k2] * l1;
									i2 = z + Direction.offsetZ[k2] * l1;
									l2 += Direction.offsetX[j1] * 4;
									i2 += Direction.offsetZ[j1] * 4;

									if (world.getBlock(l2, y, i2) != Blocks.end_portal_frame || (!BlockEndPortalFrame.isEnderEyeInserted(world.getBlockMetadata(l2, y, i2)))) {
										if (l2 != x || i2 != z) { //We add this so it doesn't care for the block clicked at, as the eye won't be there quite yet.
											flag = false;
											break;
										}
									}
								}

								int i3;

								for (l1 = j2 - 1; l1 <= k1 + 1; l1 += 4) {
									for (l2 = 1; l2 <= 3; ++l2) {
										i2 = x + Direction.offsetX[k2] * l1;
										i3 = z + Direction.offsetZ[k2] * l1;
										i2 += Direction.offsetX[j1] * l2;
										i3 += Direction.offsetZ[j1] * l2;

										if (world.getBlock(i2, y, i3) != Blocks.end_portal_frame || (!BlockEndPortalFrame.isEnderEyeInserted(world.getBlockMetadata(i2, y, i3)))) {
											if (i2 != x || i3 != z) { //We add this so it doesn't care for the block clicked at, as the eye won't be there quite yet.
												flag = false;
												break;
											}
										}
									}
								}
								if (flag) {
									for (WorldServer worldserver : FMLCommonHandler.instance().getMinecraftServerInstance().worldServers) {
										for (Object playerobj : worldserver.playerEntities) {
											if (playerobj instanceof EntityPlayerMP playermp) {
												playermp.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(Reference.MCAssetVer + ":block.end_portal.spawn",
														playermp.posX, playermp.lastTickPosY, playermp.posZ, 1F, 1F));
											}
										}
									}
								}
							}
						}

						// --- For blocks with no place sound (reeds, redstone, cake, beds etc) --- //
						if (ConfigSounds.fixSilentPlacing && heldStack != null && !world.isRemote) {
							Block block = null;
							Item item = heldStack.getItem();

							if (item == Items.redstone) {
								block = Blocks.redstone_wire;
							} else if (item == Items.sign) {
								block = side < 2 ? Blocks.standing_sign : Blocks.wall_sign;
							} else if (item == Items.wooden_door && side == 1) {
								block = Blocks.wooden_door;
							} else if (item == Items.iron_door && side == 1) {
								block = Blocks.iron_door;
							} else if (item == Items.bed && side == 1) {//Because these only place if you click the top of a block, not the side of an adjacent one.
								block = Blocks.bed;
							}

							if (block != null) {
								int xMutable = x;
								int yMutable = y;
								int zMutable = z;

								//Only the redstone wire item replaces snow, the other ones don't replace anything no matter what
								if (block != Blocks.redstone_wire || !world.getBlock(xMutable, yMutable, zMutable).isReplaceable(world, xMutable, yMutable, zMutable)) {
									switch (event.face) {
										case 0:
											--yMutable;
											break;
										case 1:
											++yMutable;
											break;
										case 2:
											--zMutable;
											break;
										case 3:
											++zMutable;
											break;
										case 4:
											--xMutable;
											break;
										case 5:
											++xMutable;
											break;
									}

									if (!world.isAirBlock(xMutable, yMutable, zMutable)) {
										// Can't put block here because it's not air
										return;
									}
								}

								//Beds place a little weird, make sure we've got the right conditions
								if (block == Blocks.bed) {
									int i1 = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
									byte b0 = 0;
									byte b1 = 0;

									switch (i1) {
										case 0:
											++b1;
											break;
										case 1:
											--b0;
											break;
										case 2:
											--b1;
											break;
										case 3:
											++b0;
											break;
									}

									if (!player.canPlayerEdit(xMutable + b0, yMutable, zMutable + b1, side, heldStack) || !block.canPlaceBlockAt(world, xMutable + b0, yMutable, zMutable + b1) ||
											!world.isAirBlock(xMutable, yMutable, zMutable) || !world.isAirBlock(xMutable + b0, yMutable, zMutable + b1) ||
											!World.doesBlockHaveSolidTopSurface(world, xMutable, yMutable - 1, zMutable) || !World.doesBlockHaveSolidTopSurface(world, xMutable + b0, yMutable - 1, zMutable + b1)) {
										return;
									}
								}

								if (!player.canPlayerEdit(xMutable, yMutable, zMutable, side, heldStack)) {
									// Can't put it here because player is disallowed
									return;
								}
								if (block.canPlaceBlockAt(world, xMutable, yMutable, zMutable)) {
									// Here is where item would be consumed and block would be set
									// Block is successfully placed
									world.playSoundEffect(xMutable + 0.5, yMutable + 0.5, zMutable + 0.5, block.stepSound.func_150496_b()/*getPlaceSound*/, (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
									return;
								}
							}
						}


						//Seeds/Wart placing sounds
						if (ConfigSounds.seedPlanting && side == 1 && heldStack != null && heldStack.getItem() instanceof IPlantable && player.canPlayerEdit(x, y + 1, z, side, heldStack)) {
							/*
							 * This code was adapted from AstroTibs' ASMC.
							 * Used with permission!
							 */
							if (world.getBlock(x, y, z).canSustainPlant(world, x, y, z, ForgeDirection.UP, (IPlantable) heldStack.getItem()) && world.isAirBlock(x, y + 1, z)) {
								// Mundane seeds
								if (oldBlock instanceof BlockFarmland) {
									world.playSoundEffect(x + 0.5, y + 1F, z + 0.5, ModSounds.soundCrops.func_150496_b()/*getPlaceSound*/, ModSounds.soundCrops.getVolume(), ModSounds.soundCrops.getPitch());
									return;
								}
								// Nether wart
								else if (oldBlock instanceof BlockSoulSand) {
									world.playSoundEffect(x + 0.5, y + 1F, z + 0.5, ModSounds.soundCropWarts.func_150496_b()/*getPlaceSound*/, ModSounds.soundCropWarts.getVolume(), ModSounds.soundCropWarts.getPitch());
									return;
								}
							}
						}

						//Lava cauldron filling and cauldron filling noises
						if (heldStack != null && canUse(player, world, x, y, z) && oldBlock == Blocks.cauldron) {
							Item item = heldStack.getItem();
							if (ConfigBlocksItems.enableLavaCauldrons && item instanceof ItemBucket && ((ItemBucket) item).isFull == Blocks.flowing_lava && meta == 0) {
								event.setResult(Result.DENY);
								player.swingItem();
								world.setBlock(x, y, z, ModBlocks.LAVA_CAULDRON.get());
								if (ConfigSounds.fluidInteract) {
									world.playSoundEffect(x, y, z, Reference.MCAssetVer + ":item.bucket.empty_lava", 1, 1);
								}
								if (!player.capabilities.isCreativeMode) {
									if (heldStack.stackSize <= 1) {
										player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(item.getContainerItem()));
									} else {
										--heldStack.stackSize;
									}
								}
								return;
							} else if (ConfigSounds.fluidInteract) {
								String container = "";
								String fillOrEmpty = "";
								if (item instanceof ItemBucket && ((ItemBucket) item).isFull == Blocks.flowing_water && meta < 3) {
									container = "bucket";
									fillOrEmpty = "empty";
								} else if (item == Items.glass_bottle || (item == Items.potionitem && heldStack.getItemDamage() == 0 && !heldStack.hasTagCompound())) {
									container = "bottle";
									fillOrEmpty = /* meta < 3 && item == Items.potionitem ? "empty" : */ item == Items.glass_bottle && meta > 0 ? "fill" : "";
								}//TODO add taking from cauldrons and evaporation, and filling a cauldron with regular potion bottles
								if (!container.equals("") && !fillOrEmpty.equals("")) {
									world.playSoundEffect(x, y, z, Reference.MCAssetVer + ":item." + container + "." + fillOrEmpty, 1, 1);
									return;
								}
							}
						}

						// --- Bottle fill sounds --- //
						if (ConfigSounds.fluidInteract && !world.isRemote && heldStack != null && heldStack.getItem() == Items.glass_bottle && event.action == Action.RIGHT_CLICK_AIR) {
							MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, player, true);

							if (movingobjectposition != null && movingobjectposition.typeOfHit == MovingObjectType.BLOCK) {
								int i = movingobjectposition.blockX;
								int j = movingobjectposition.blockY;
								int k = movingobjectposition.blockZ;

								if (!world.canMineBlock(player, i, j, k)) {
									return;
								}
								if (!player.canPlayerEdit(i, j, k, movingobjectposition.sideHit, heldStack)) {
									return;
								}

								boolean isWater = world.getBlock(i, j, k).getMaterial() == Material.water;

								if (isWater) {
									world.playSoundAtEntity(player, Reference.MCAssetVer + ":item.bottle.fill", 1.0F, 1.0F);
									return;
								}
							}
						}

						// --- Lilypad sounds --- //
						if (ConfigSounds.newBlockSounds && heldStack != null && Block.getBlockFromItem(heldStack.getItem()) instanceof BlockLilyPad) {
							Block block = Block.getBlockFromItem(heldStack.getItem());
							MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, player, true);
							if (movingobjectposition == null) {
								return;
							} else {
								if (movingobjectposition.typeOfHit == MovingObjectType.BLOCK) {
									int i = movingobjectposition.blockX;
									int j = movingobjectposition.blockY;
									int k = movingobjectposition.blockZ;

									if (!world.canMineBlock(player, i + 1, j, k)) {
										return;
									}
									if (!player.canPlayerEdit(i, j + 1, k, movingobjectposition.sideHit, heldStack)) {
										return;
									}

									if (block.canBlockStay(world, i, j + 1, k) && event.action == Action.RIGHT_CLICK_AIR) {
										world.playSoundEffect(i + 0.5F, j + 0.5F, k + 0.5F, Reference.MCAssetVer + ":block.lily_pad.place", 1.0F, 1.0F);
										return;
									}
								}
							}
						}

						if (ModBlocks.DAYLIGHT_DETECTOR_INVERTED.isEnabled() && oldBlock == Blocks.daylight_detector && canUse(player, world, x, y, z)) {
							player.swingItem();
							world.setBlock(x, y, z, ModBlocks.DAYLIGHT_DETECTOR_INVERTED.get(), 15 - meta, 2);
							if (!world.isRemote) {
								event.setResult(Result.DENY);
								event.setCanceled(true);
							}
							world.notifyBlockChange(x, y, z, ModBlocks.DAYLIGHT_DETECTOR_INVERTED.get());
						}

						if (heldStack != null && heldStack.getItem() == Items.potionitem && Utils.hasPotionEffect(heldStack)) {
							if (ModBlocks.POTION_CAULDRON.isEnabled() && oldBlock == Blocks.cauldron && meta == 0 && !ItemPotion.isSplash(heldStack.getItemDamage())) {
								world.setBlock(x, y, z, ModBlocks.POTION_CAULDRON.get());
								//If we don't cancel the use event, the new block is used, so the use code is in the block class.
							}
						}

						if (heldStack != null) {
							doMudConversion(world, x, y, z, player, oldBlock, meta, heldStack);
						}

						if (ConfigFunctions.mobSpawnerEgging && oldBlock == Blocks.mob_spawner && heldStack != null && heldStack.getItem() == Items.spawn_egg) {
							TileEntityMobSpawner tileEntityMobSpawner = (TileEntityMobSpawner) world.getTileEntity(x, y, z);
							String entityName = EntityList.getStringFromID(heldStack.getItemDamage());
							if (!entityName.equals(tileEntityMobSpawner.func_145881_a().getEntityNameToSpawn())) {
								tileEntityMobSpawner.func_145881_a().setEntityName(entityName);
								player.swingItem();
								if (!world.isRemote) {
									event.setResult(Result.ALLOW);
									event.setCanceled(true);
								}
								world.markBlockForUpdate(x, y, z);
								if (!player.capabilities.isCreativeMode) {
									heldStack.stackSize--;
								}
								return;
							}
						}

						//Grass pathing/Log Stripping
						//This is nested into the same function since they use similar checks
						if (heldStack != null) {

							//Check if tool is a broken TiC tool
							boolean toolIsBroken = false;
							if (heldStack.hasTagCompound() && heldStack.getTagCompound().hasKey("InfiTool")) {
								toolIsBroken = heldStack.getTagCompound().getCompoundTag("InfiTool").getBoolean("Broken");
							}

							if (!toolIsBroken) {
								Set<String> toolClasses = heldStack.getItem().getToolClasses(heldStack);
								if (toolClasses != null
										//TODO dirty solution, make this a list, maybe a HogUtils tag `#etfuturum:no_till_shovels`?
										&& heldStack.getItem() != ExternalContent.Items.BOTANIA_MANASTEEL_SHOVEL.get()
										&& heldStack.getItem() != ExternalContent.Items.THAUMCRAFT_EARTHMOVER_SHOVEL.get()) {
									if (ConfigBlocksItems.enableGrassPath && toolClasses.contains("shovel") && !world.getBlock(x, y + 1, z).getMaterial().isSolid() && (oldBlock == Blocks.grass || oldBlock == Blocks.dirt || oldBlock == Blocks.mycelium)) {
										player.swingItem();
										if (!world.isRemote) {
											world.setBlock(x, y, z, ModBlocks.GRASS_PATH.get());
											heldStack.damageItem(1, player);
											world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, Reference.MCAssetVer + ":item.shovel.flatten", 1.0F, 1.0F);
										}
									} else if (ConfigBlocksItems.enableStrippedLogs && toolClasses.contains("axe")) {
										RegistryMapping<Block> newBlock = StrippedLogRegistry.getLog(oldBlock, world.getBlockMetadata(x, y, z) % 4);
										if (newBlock != null) {
											player.swingItem();
											if (!world.isRemote) {
												world.setBlock(x, y, z, newBlock.getObject(), newBlock.getMeta() + ((meta / 4) * 4), 2);
												heldStack.damageItem(1, player);
												world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, Reference.MCAssetVer + ":item.axe.strip", 1.0F, 0.8F);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean doMudConversion(World world, int x, int y, int z, EntityPlayer player, Block oldBlock, int meta, ItemStack heldStack) {
		boolean isValid = heldStack.getItem() == Items.potionitem && heldStack.getItemDamage() == 0 && Items.potionitem.getEffects(heldStack) == null;
		if (!isValid && ModsList.EXTRA_UTILITIES.isLoaded()) {
			int itemDamage = heldStack.getItemDamage();
			isValid = heldStack.getItem() == ExternalContent.Items.EXTRAUTILS_WATERING_CAN.get() && (itemDamage == 0 || itemDamage == 3);
		}
		if (ModBlocks.MUD.isEnabled() && isValid && ((oldBlock == Blocks.dirt && meta != 2) || oldBlock == ModBlocks.COARSE_DIRT.get())) { //TODO: Rooted dirt
			world.setBlock(x, y, z, ModBlocks.MUD.get());
			world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, Reference.MCAssetVer + ":item.bottle.empty", 1.0F, 1.0F);
			player.swingItem();
			if (world.isRemote) {
				double d0 = 0.0625D;
				for (int l = 0; l < 10; ++l) {
					double d1 = x + world.rand.nextFloat();
					double d2 = y + world.rand.nextFloat();
					double d3 = z + world.rand.nextFloat();

					if (l == 0 && !world.getBlock(x, y + 1, z).isOpaqueCube()) {
						d2 = y + 1 + d0;
					}

					if (l == 1 && !world.getBlock(x, y - 1, z).isOpaqueCube()) {
						d2 = y - d0;
					}

					if (l == 2 && !world.getBlock(x, y, z + 1).isOpaqueCube()) {
						d3 = z + 1 + d0;
					}

					if (l == 3 && !world.getBlock(x, y, z - 1).isOpaqueCube()) {
						d3 = z - d0;
					}

					if (l == 4 && !world.getBlock(x + 1, y, z).isOpaqueCube()) {
						d1 = x + 1 + d0;
					}

					if (l == 5 && !world.getBlock(x - 1, y, z).isOpaqueCube()) {
						d1 = x - d0;
					}

					if (d1 < x || d1 > x + 1 || d2 < 0.0D || d2 > y + 1 || d3 < z || d3 > z + 1) {
						world.spawnParticle("splash", d1, d2, d3, 0, 0, 0);
					}
				}
			}
			if (!player.capabilities.isCreativeMode && heldStack.getItem() == Items.potionitem) {
				final ItemStack bottle = new ItemStack(Items.glass_bottle);
				if (heldStack.stackSize <= 1) {
					player.setCurrentItemOrArmor(0, bottle);
				} else {
					player.inventory.decrStackSize(player.inventory.currentItem, 1);
					if (!player.inventory.addItemStackToInventory(bottle)) {
						player.dropPlayerItemWithRandomChoice(bottle, false);
					}
				}
			}
			return true;
		}
		return false;
	}

	@SubscribeEvent
	public void onFillBucketEvent(FillBucketEvent event) {
		if (ConfigSounds.fluidInteract && event.current.getItem() instanceof ItemBucket) {//In case some weirdo mod fires this event but doesn't use ItemBucket
			Block isFull = ((ItemBucket) event.current.getItem()).isFull;
			MovingObjectPosition target = event.target;
			int x = target.blockX;
			int y = target.blockY;
			int z = target.blockZ;

			if (!event.world.canMineBlock(event.entityPlayer, x, y, z)) {
				return;
			}

			if (isFull == Blocks.air) {
				if (!event.entityPlayer.canPlayerEdit(x, y, z, target.sideHit, event.current)) {
					return;
				}
			}

			if (!event.world.isRemote) { //We assume lava sounds if it's not water because most modded liquids are molten metals
				if (isFull.getMaterial() == Material.water) {// --- Pour water --- //
					event.world.playSoundEffect(target.blockX + 0.5, target.blockY + 0.5, target.blockZ + 0.5, Reference.MCAssetVer + ":item.bucket.empty", 1.0F, 1.0F);
				} else if (isFull != Blocks.air) {// --- Pour something else --- //
					event.world.playSoundEffect(target.blockX + 0.5, target.blockY + 0.5, target.blockZ + 0.5, Reference.MCAssetVer + ":item.bucket.empty_lava", 1.0F, 1.0F);
				} else {
					if (event.world.getBlock(target.blockX, target.blockY, target.blockZ).getMaterial() == Material.water) {// --- Fill with water --- //
						event.world.playSoundEffect(target.blockX + 0.5, target.blockY + 0.5, target.blockZ + 0.5, Reference.MCAssetVer + ":item.bucket.fill", 1.0F, 1.0F);
					} else if (event.world.getBlock(target.blockX, target.blockY, target.blockZ).getMaterial().isLiquid()) {// --- Fill with something else --- //
						event.world.playSoundEffect(target.blockX + 0.5, target.blockY + 0.5, target.blockZ + 0.5, Reference.MCAssetVer + ":item.bucket.fill_lava", 1.0F, 1.0F);
					}
				}
			}
		}
	}


	public static boolean canUse(EntityPlayer player, World world, int x, int y, int z) {
		return !player.isSneaking() || player.getHeldItem() == null || player.getHeldItem().getItem().doesSneakBypassUse(world, x, y, z, player);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void dropEvent(LivingDropsEvent event) {
		if (event.entityLiving.worldObj.isRemote)
			return;

		if (ConfigFunctions.enableSkullDrop && event.entityLiving.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
			dropHead(event.entityLiving, event.source, event.lootingLevel, event.drops);
		}

		Random rand = event.entityLiving.worldObj.rand;
		if (ModItems.MUTTON_RAW.isEnabled() && ModItems.MUTTON_COOKED.isEnabled()
				&& event.entityLiving.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot") && event.entityLiving instanceof EntitySheep) {
			int amount = rand.nextInt(3) + 1 + rand.nextInt(1 + event.lootingLevel);
			for (int i = 0; i < amount; i++)
				if (event.entityLiving.isBurning()) {
					addDrop(new ItemStack(ModItems.MUTTON_COOKED.get()), event.entityLiving, event.drops);
				} else {
					addDrop(new ItemStack(ModItems.MUTTON_RAW.get()), event.entityLiving, event.drops);
				}
		}

		if (ModBlocks.WITHER_ROSE.isEnabled() && event.entity instanceof EntityLivingBase && event.source.getEntity() instanceof EntityWither) {
			World world = event.entity.worldObj;
			Entity entity = event.entity;
			if (world.getGameRules().getGameRuleBooleanValue("mobGriefing") && ModBlocks.WITHER_ROSE.get().canPlaceBlockAt(world, (int) entity.posX, (int) entity.posY, (int) entity.posZ)) {
				world.setBlock((int) entity.posX, (int) entity.posY, (int) entity.posZ, ModBlocks.WITHER_ROSE.get());
			} else {
				addDrop(ModBlocks.WITHER_ROSE.newItemStack(1, 0), event.entityLiving, event.drops);
			}
		}
	}

	private void dropHead(EntityLivingBase entity, DamageSource source, int looting, List<EntityItem> drops) {
		if (isPoweredCreeper(source)) {
			int meta = getHeadMetadata(entity);
			if (meta >= 0)
				addDrop(new ItemStack(Items.skull, 1, meta), entity, drops);
		}
	}

	private void addDrop(ItemStack stack, EntityLivingBase entity, List<EntityItem> list) {
		if (stack.stackSize <= 0)
			return;

		EntityItem entityItem = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, stack);
		entityItem.delayBeforeCanPickup = 10;
		list.add(entityItem);
	}

	private boolean isPoweredCreeper(DamageSource source) {
		if (source.isExplosion() && source instanceof EntityDamageSource) {
			Entity entity = source.getEntity();
			if (entity instanceof EntityCreeper)
				return ((EntityCreeper) entity).getPowered();
		}

		return false;
	}

	private int getHeadMetadata(EntityLivingBase entity) {
		if (entity.getClass() == EntityZombie.class)
			return 2;
		else if (entity.getClass() == EntitySkeleton.class && ((EntitySkeleton) entity).getSkeletonType() == 0)
			return 0;
		else if (entity.getClass() == EntityCreeper.class)
			return 4;

		return -1;
	}

	@SubscribeEvent
	public void teleportEvent(EnderTeleportEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if (entity instanceof EntityPlayerMP) {
			if (ConfigEntities.enableEndermite) {
				if (entity.getRNG().nextFloat() < 0.05F && entity.worldObj.getGameRules().getGameRuleBooleanValue("doMobSpawning")) {
					EntityEndermite entityendermite = new EntityEndermite(entity.worldObj);
					entityendermite.setLocationAndAngles(event.targetX, event.targetY, event.targetZ, entity.rotationYaw, entity.rotationPitch);
					entity.worldObj.spawnEntityInWorld(entityendermite);
					entityendermite.setSpawnedByPlayer(true);
					entityendermite.aggroEndermen(64);
				}
			}

			ForgeDirection[] horizontal = {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST};

			for (ForgeDirection enumfacing : horizontal) {
				TileEntity tile = event.entity.worldObj.getTileEntity(MathHelper.floor_double(event.targetX + enumfacing.offsetX), MathHelper.floor_double(event.targetY), MathHelper.floor_double(event.targetZ + enumfacing.offsetZ));
				if (!event.entity.worldObj.isRemote && tile instanceof TileEntityGateway && !((TileEntityGateway) tile).isCoolingDown()) {
					((TileEntityGateway) tile).teleportEntity(event.entity);
					tile.markDirty();
					event.setCanceled(true);
					break;
				}
			}
		}
	}

	@SubscribeEvent
	public void naturalSpawnEvent(SpecialSpawn event) {
		if (!(event.entity instanceof EntityLiving)) {
			return;
		}
		int x = MathHelper.floor_double(event.x);
		int y = MathHelper.floor_double(event.y);
		int z = MathHelper.floor_double(event.z);
		World world = event.world;
		EntityLivingBase entity = event.entityLiving;
		if (event.entityLiving instanceof EntityShulker shulker) {
			shulker.persistenceRequired = false;
			if (ConfigTweaks.spawnAnywhereShulkerColors) {
				for (EnumFacing facing : Utils.ENUM_FACING_VALUES) {
					int offX = x + facing.getFrontOffsetX();
					int offY = y + facing.getFrontOffsetY();
					int offZ = z + facing.getFrontOffsetZ();

					Block block = world.getBlock(offX, offY, offZ);
					byte color = -1;
					int meta = world.getBlockMetadata(offX, offY, offZ);

					if (facing == EnumFacing.DOWN && block == ExternalContent.Blocks.HEE_END_STONE.get()) {
						color = (byte) (meta == 2 ? 10 : meta == 1 ? 1 : 14);
					} else if (block == ExternalContent.Blocks.ENDERLICIOUS_SAND.get()) {
						color = (byte) (meta == 1 ? 15 : 0);
					} else if (block == ExternalContent.Blocks.HEE_END_STONE.get()) {
						color = (byte) (meta % 4 == 1 ? 13 : -1);
					}

					if (color > -1) {
						shulker.setColor(color);
						break;
					}
				}
			}
		} else if (ConfigEntities.enableStray && !ConfigWorld.oldStraySpawning && EntityList.getEntityID(entity) == 51 /*Skeleton ID*/ && world.rand.nextFloat() < .80F && world.canBlockSeeTheSky(x, y + 1, z)) {
			Chunk chunk = world.getChunkFromBlockCoords(x, z);
			BiomeDictionary.Type[] biomeTags = BiomeDictionary.getTypesForBiome(chunk.getBiomeGenForWorldCoords(x & 15, z & 15, world.getWorldChunkManager()));
			if (ArrayUtils.contains(biomeTags, BiomeDictionary.Type.SNOWY)) {
				EntityStray stray = new EntityStray(world);
				replaceEntity(entity, stray, world, chunk);
				stray.onSpawnWithEgg(null);
				event.setCanceled(true);
				event.setResult(Result.DENY);
			}
		} else if (ConfigEntities.enableHusk && !ConfigWorld.oldHuskSpawning && EntityList.getEntityID(entity) == 54 /*Zombie ID*/ && world.rand.nextFloat() < .80F && world.canBlockSeeTheSky(x, y + 1, z)) {
			Chunk chunk = world.getChunkFromBlockCoords(x, z);
			BiomeDictionary.Type[] biomeTags = BiomeDictionary.getTypesForBiome(chunk.getBiomeGenForWorldCoords(x & 15, z & 15, world.getWorldChunkManager()));
			if (ArrayUtils.contains(biomeTags, BiomeDictionary.Type.HOT) && ArrayUtils.contains(biomeTags, BiomeDictionary.Type.DRY) && ArrayUtils.contains(biomeTags, BiomeDictionary.Type.SANDY)) {
				EntityHusk husk = new EntityHusk(world);
				replaceEntity(entity, husk, world, chunk);
				husk.onSpawnWithEgg(null);
				event.setCanceled(true);
				event.setResult(Result.DENY);
			}
		}
	}

	private Method addRandomArmorMethod;
	private Method enchantEquipmentMethod;
	private final Map<Class<? extends IChunkProvider>, Field[]> chunkProviderFieldsCache = new WeakHashMap<>();

	@SubscribeEvent
	public void naturalSpawnEvent(LivingPackSizeEvent event) {
		//Handles logic for preventing wither skeletons from appearing in soul sand valleys unless they're in a Nether fortress.
		int x = MathHelper.floor_double(event.entityLiving.posX);
		int y = MathHelper.floor_double(event.entityLiving.posY);
		int z = MathHelper.floor_double(event.entityLiving.posZ);
		World world = event.entityLiving.worldObj;
		if (ConfigWorld.soulSandValleyID != -1 && EntityList.getEntityID(event.entity) == 51 /*Skeleton ID*/ && ((EntitySkeleton) event.entity).getSkeletonType() == 1
				&& !world.isRemote && world.provider instanceof WorldProviderHell && world.getBiomeGenForCoords(x, z) == NetherBiomeManager.soulSandValley) {
			if (addRandomArmorMethod == null) {
				addRandomArmorMethod = ReflectionHelper.findMethod(EntityLiving.class, null, new String[]{"func_82164_bB", "addRandomArmor"});
			}
			if (enchantEquipmentMethod == null) {
				enchantEquipmentMethod = ReflectionHelper.findMethod(EntityLiving.class, null, new String[]{"func_82162_bC", "enchantEquipment"});
			}

			if (addRandomArmorMethod != null && enchantEquipmentMethod != null) {
				List<MapGenNetherBridge> list = Lists.newArrayList();
				try {
					IChunkProvider provider;
					if (world.getChunkProvider() instanceof ChunkProviderServer) {
						provider = ((ChunkProviderServer) world.getChunkProvider()).currentChunkProvider;
					} else {
						provider = world.getChunkProvider();
					}
					//Netherlicious and myself override the world provider instead of extending ChunkProviderHell
					//This should cover any world provider, for any WorldProviderHell dimension. Then we search for all Nether Fortress instances in that class.
					//This is so no matter what a custom Nether override or modded dimension that uses WorldProviderHell names a fortress field, we can detect its Nether fortress.
					//Is it overkill? Honestly yes, but I like to account for edgecases. There are also mods that build custom dimensions like Mystcraft or RFTools, and MultiWorld.
					Field[] fields;
					if (chunkProviderFieldsCache.containsKey(provider.getClass())) {
						fields = chunkProviderFieldsCache.get(provider.getClass());
					} else {
						fields = provider.getClass().getFields();
						chunkProviderFieldsCache.put(provider.getClass(), fields);
					}

					for (Field field : fields) {
						Object o = field.get(provider);
						if (o instanceof MapGenNetherBridge) {
							list.add((MapGenNetherBridge) o);
						}
					}
				} catch (IllegalAccessException ignored) {
					return;
				}
				for (MapGenNetherBridge bridge : list) {
					if (bridge.hasStructureAt(x, y, z)) {
						return;
					}
				}
				((EntitySkeleton) event.entity).setSkeletonType(0);
				event.entity.setCurrentItemOrArmor(0, null);
				event.entity.setCurrentItemOrArmor(1, null);
				event.entity.setCurrentItemOrArmor(2, null);
				event.entity.setCurrentItemOrArmor(3, null);
				event.entity.setCurrentItemOrArmor(4, null);
				try {
					addRandomArmorMethod.invoke(event.entity);
					enchantEquipmentMethod.invoke(event.entity);
				} catch (Exception ignored) {
				}
//				((EntitySkeleton)event.entity).addRandomArmor();
//				((EntitySkeleton)event.entity).enchantEquipment(); //Grrr, ATs refuse to apply to these for some reason, keeps throwing damn errors
			}
		}
	}

	@SubscribeEvent
	/**
	 * NOTE: This event for some reason uses event.setResult(Result.DENY) to cancel instead of event.setCanceled... Why does the "result" even exist?
	 */
	public void spawnEvent(LivingSpawnEvent.CheckSpawn event) {
		if (event.getResult() != Result.DENY) {
			World world = event.world;
			int x = MathHelper.floor_double(event.x);
			int y = MathHelper.floor_double(event.y);
			int z = MathHelper.floor_double(event.z);

			if (event.world.provider instanceof WorldProviderHell) {
				if (world.getBlock(x, y - 1, z) == ModBlocks.NETHER_WART.get() && world.getBlockMetadata(x, y - 1, z) == 0) {
					if (!(event.entity instanceof EntityFlying)) {
						event.setResult(Result.DENY);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void spawnEvent(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPig pig) {
			if (ModItems.BEETROOT.isEnabled()) {
				pig.tasks.addTask(4, new EntityAITempt(pig, 1.2, ModItems.BEETROOT.get(), false));
			}
		} else if (event.entity instanceof EntityChicken chicken) {
			if (ModItems.BEETROOT.isEnabled()) {
				chicken.tasks.addTask(3, new EntityAITempt(chicken, 1.0D, ModItems.BEETROOT_SEEDS.get(), false));
			}
		} else if (ConfigEntities.enableRabbit && event.entity instanceof EntityWolf wolf) {
			wolf.targetTasks.addTask(4, new EntityAITargetNonTamed(wolf, EntityRabbit.class, 200, false));
		} else if (ConfigEntities.enableEndermite && event.entity instanceof EntityEnderman enderman) {
			enderman.targetTasks.addTask(3, new EntityAINearestAttackableTarget(enderman, EntityEndermite.class, 100, false));
		} else if (event.entity instanceof EntityVillager villager) {
			for (Object obj : villager.tasks.taskEntries) {
				EntityAITaskEntry entry = (EntityAITaskEntry) obj;
				if (entry.action instanceof EntityAIOpenDoor) {
					villager.tasks.removeTask(entry.action);
					villager.tasks.addTask(entry.priority, new EntityAIOpenCustomDoor(villager, true));
					break;
				}
			}
		} else if (!event.world.isRemote && ModBlocks.CONCRETE_POWDER.isEnabled()
				&& event.entity instanceof EntityFallingBlock && ((EntityFallingBlock) event.entity).field_145811_e/*blockObj*/ == ModBlocks.CONCRETE_POWDER.get()) {
			fallingConcreteBlocks.add((EntityFallingBlock) event.entity);
		}
	}

	@SubscribeEvent
	public void interactEntityEvent(EntityInteractEvent event) {
		ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
		World world = event.entityPlayer.worldObj;

		Entity target = event.target;
		if (target instanceof EntityAnimal && stack != null) {
			EntityAnimal animal = (EntityAnimal) event.target;
			if (!animal.isChild()) {
				if (animal instanceof EntityPig) {
					if (stack.getItem() == ModItems.BEETROOT.get() && ConfigBlocksItems.enableBeetroot)
						setAnimalInLove(animal, event.entityPlayer, stack);
				} else if (animal instanceof EntityChicken)
					if (stack.getItem() == ModItems.BEETROOT_SEEDS.get() && ConfigBlocksItems.enableBeetroot)
						setAnimalInLove(animal, event.entityPlayer, stack);
			} else if (ConfigEntities.enableBabyGrowthBoost && isFoodItem(animal, stack)) {
				feedBaby(animal, event.entityPlayer, stack);
			}

			//Sheep dying with modded dyes
			if (animal instanceof EntitySheep sheep && stack.getItem() != Items.dye && !animal.worldObj.isRemote) {
				for (int oreID : OreDictionary.getOreIDs(stack)) {
					int fleeceColour = ~ArrayUtils.indexOf(ModRecipes.ore_dyes, OreDictionary.getOreName(oreID)) & 15;
					if (ArrayUtils.contains(ModRecipes.ore_dyes, OreDictionary.getOreName(oreID)) && sheep.getFleeceColor() != fleeceColour
							&& !sheep.getSheared()) {
						sheep.setFleeceColor(fleeceColour);
						if (!event.entityPlayer.capabilities.isCreativeMode)
							--stack.stackSize;
						break;
					}
				}
			}
		}

		// --- Milk Mooshroom --- //
		if (target instanceof EntityCow && stack != null && ConfigSounds.horseEatCowMilk) {
			if (target instanceof EntityMooshroom && stack.getItem() == Items.bowl) {
				world.playSoundEffect(target.posX, target.posY, target.posZ, Reference.MCAssetVer + ":entity.mooshroom.milk", 1.0F, 0.9F + (world.rand.nextInt(3) - 1) * 0.1F);
			} else if (stack.getItem() == Items.bucket) {
				world.playSoundEffect(target.posX, target.posY, target.posZ, Reference.MCAssetVer + ":entity.cow.milk", 1.0F, 1.0F);
			}
			return;
		}


		if (ConfigSounds.paintingItemFramePlacing && target instanceof EntityItemFrame itemframe) { // --- Add/Rotate within Item Frame --- //

			if (stack != null && itemframe.getDisplayedItem() == null) // This frame is empty and is getting an item placed inside.
			{
				world.playSoundEffect(target.posX, target.posY, target.posZ, Reference.MCAssetVer + ":entity.item_frame.add_item", 1.0F, 1.0F);
			} else if (itemframe.getDisplayedItem() != null) // There is an item in there, it's going to be rotated.
			{
				world.playSoundEffect(target.posX, target.posY, target.posZ, Reference.MCAssetVer + ":entity.item_frame.rotate_item", 1.0F, 1.0F);
			}
			return;
		}

		if (ConfigSounds.leashSounds && target instanceof EntityLeashKnot) { // --- Remove a Lead Knot --- //
			world.playSoundEffect(target.posX + 0.5, target.posY + 0.5, target.posZ + 0.5, Reference.MCAssetVer + ":entity.leash_knot.break", 1.0F, 1.0F);
		}
	}

	private void setAnimalInLove(EntityAnimal animal, EntityPlayer player, ItemStack stack) {
		if (!animal.isInLove()) {
			animal.func_146082_f(player); // setInLove
			if (!player.capabilities.isCreativeMode)
				if (--stack.stackSize <= 0)
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
		}
	}

	private void feedBaby(EntityAnimal animal, EntityPlayer player, ItemStack stack) {
		int currentAge = animal.getGrowingAge();
		int age = (int) (-currentAge * 0.1F);
		animal.setGrowingAge(currentAge + age);
		player.swingItem();

		Random itemRand = animal.worldObj.rand;
		for (int i = 0; i < 3; i++) {
			double d0 = itemRand.nextGaussian() * 0.02D;
			double d1 = itemRand.nextGaussian() * 0.02D;
			double d2 = itemRand.nextGaussian() * 0.02D;
			animal.worldObj.spawnParticle("happyVillager", animal.posX + itemRand.nextFloat() * 0.5, animal.posY + 0.5 + itemRand.nextFloat() * 0.5, animal.posZ + itemRand.nextFloat() * 0.5, d0, d1, d2);
		}

		if (!player.capabilities.isCreativeMode)
			if (--stack.stackSize <= 0)
				player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
	}

	private boolean isFoodItem(EntityAnimal animal, ItemStack food) {
		if (animal.isBreedingItem(food))
			return true;
		if (animal instanceof EntityPig && food.getItem() == ModItems.BEETROOT.get() && ConfigBlocksItems.enableBeetroot)
			return true;
		return animal instanceof EntityChicken && food.getItem() == ModItems.BEETROOT_SEEDS.get() && ConfigBlocksItems.enableBeetroot;
	}

	private float applyArmorCalculations(EntityLivingBase entity, DamageSource p_70655_1_, float p_70655_2_) {
		if (!p_70655_1_.isUnblockable()) {
			int i = 25 - entity.getTotalArmorValue();
			float f1 = p_70655_2_ * i;
			p_70655_2_ = f1 / 25.0F;
		}

		return p_70655_2_;
	}

	private float applyPotionDamageCalculations(EntityLivingBase entity, DamageSource p_70672_1_, float p_70672_2_) {
		if (p_70672_1_.isDamageAbsolute()) {
			return p_70672_2_;
		}
//      if (entity instanceof EntityZombie)
//      {
//          //par2 = par2; // Forge: Noop Warning
//      }

		int i;
		int j;
		float f1;

		if (entity.isPotionActive(Potion.resistance) && p_70672_1_ != DamageSource.outOfWorld) {
			i = (entity.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
			j = 25 - i;
			f1 = p_70672_2_ * j;
			p_70672_2_ = f1 / 25.0F;
		}

		if (p_70672_2_ <= 0.0F) {
			return 0.0F;
		}
		i = EnchantmentHelper.getEnchantmentModifierDamage(entity.getLastActiveItems(), p_70672_1_);

		if (i > 20) {
			i = 20;
		}

		if (i > 0) {
			j = 25 - i;
			f1 = p_70672_2_ * j;
			p_70672_2_ = f1 / 25.0F;
		}

		return p_70672_2_;
	}

	@SubscribeEvent
	public void entityStruckByLightning(EntityStruckByLightningEvent event) {
		if (ConfigEntities.enableVillagerTurnsIntoWitch && event.entity instanceof EntityVillager villager) {
			if (!villager.worldObj.isRemote) {
				EntityWitch witch = new EntityWitch(villager.worldObj);
				witch.copyLocationAndAnglesFrom(villager);
				witch.onSpawnWithEgg(null);
				villager.worldObj.spawnEntityInWorld(witch);
				villager.setDead();
			}
		} else if (ConfigEntities.enableBrownMooshroom && event.entity.ticksExisted > 40 && event.entity.getClass() == EntityMooshroom.class) {
			EntityMooshroom mooshroom = (EntityMooshroom) event.entity;
			if (!mooshroom.worldObj.isRemote) {
				EntityBrownMooshroom brownmooshroom = new EntityBrownMooshroom(mooshroom.worldObj);
				brownmooshroom.copyLocationAndAnglesFrom(mooshroom);
				brownmooshroom.onSpawnWithEgg(null);
				mooshroom.worldObj.spawnEntityInWorld(brownmooshroom);
				brownmooshroom.playSound(Reference.MCAssetVer + ":entity.mooshroom.convert", 1, 1);
				mooshroom.setDead();
			}
		} else if (ConfigEntities.enableBrownMooshroom && event.entity.ticksExisted > 40 && event.entity.getClass() == EntityBrownMooshroom.class) {
			EntityBrownMooshroom brownmooshroom = (EntityBrownMooshroom) event.entity;
			if (!brownmooshroom.worldObj.isRemote) {
				EntityMooshroom mooshroom = new EntityMooshroom(brownmooshroom.worldObj);
				mooshroom.copyLocationAndAnglesFrom(brownmooshroom);
				mooshroom.onSpawnWithEgg(null);
				brownmooshroom.worldObj.spawnEntityInWorld(mooshroom);
				mooshroom.playSound(Reference.MCAssetVer + ":entity.mooshroom.convert", 1, 1);
				brownmooshroom.setDead();
			}
		}
	}

	@SubscribeEvent
	public void livingHurtEvent(LivingHurtEvent event) {
		EntityLivingBase targetEntity = event.entityLiving;
		if (targetEntity == null) return;
		if (ConfigFunctions.enableHayBaleFalls
				&& targetEntity.worldObj.getBlock(MathHelper.floor_double(targetEntity.posX), MathHelper.floor_double(targetEntity.posY - 0.20000000298023224D - targetEntity.yOffset), MathHelper.floor_double(targetEntity.posZ)) == Blocks.hay_block
				&& event.source == DamageSource.fall) {
			event.ammount *= ((float) ConfigFunctions.hayBaleReducePercent / (float) 100);
		}

		// --- Attack a living entity --- //
		if (ConfigSounds.combatSounds && event.source.damageType.equals("player")) {
			EntityPlayer playerSource = (EntityPlayer) event.source.getEntity();
			World world = playerSource.worldObj;
			double x = targetEntity.posX;
			double y = targetEntity.posY;
			double z = targetEntity.posZ;

			if (targetEntity.canAttackWithItem()) {
				if (!targetEntity.hitByEntity(playerSource)) {
					float attackDamage = (float) playerSource.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
					float enchantmentDamage = 0.0F;

					enchantmentDamage = EnchantmentHelper.getEnchantmentModifierLiving(playerSource, targetEntity);

					if (attackDamage > 0.0F || enchantmentDamage > 0.0F) {
						boolean isStrongAttack = playerSource.getHeldItem() != null && event.ammount >= ConfigSounds.combatSoundStrongThreshold;

						// Knockback degree
						//Unused variable
//                      int i = EnchantmentHelper.getKnockbackModifier(playerSource, (EntityLivingBase) targetEntity);

						if (playerSource.isSprinting()) {
							// --- Knockback attack sound --- //
							world.playSoundEffect(x, y, z, Reference.MCAssetVer + ":entity.player.attack.knockback", 1, 1);
//                          ++i;
						}

						boolean isCriticalHit = targetEntity instanceof EntityLivingBase && playerSource.fallDistance > 0.0F && !playerSource.onGround &&
								!playerSource.isOnLadder() && !playerSource.isInWater() && !playerSource.isPotionActive(Potion.blindness) && playerSource.ridingEntity == null;
						// Removed sprinting check because you can crit while sprinting in 1.7

//                      if (isCriticalHit) {
//                          attackDamage *= 1.5F;//Unused assignment
//                      }

//                      attackDamage += enchantmentDamage;//Unused assignment

						boolean targetTakesDamage = !targetEntity.isEntityInvulnerable() && event.ammount > 0F;//targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(playerSource), f);

						if (targetTakesDamage) {
							if (isCriticalHit) {
								world.playSoundEffect(x, y, z, Reference.MCAssetVer + ":entity.player.attack.crit", 1, 1);
							}

							if (!isCriticalHit) {// flag in 1.12 is playerSource.getCooledAttackStrength(0.5F) > 0.9F
								if (isStrongAttack) {
									// --- Strong attack sound --- //
									world.playSoundEffect(x, y, z, Reference.MCAssetVer + ":entity.player.attack.strong", 1, 1);
								} else {
									// --- Weak attack sound --- //
									world.playSoundEffect(x, y, z, Reference.MCAssetVer + ":entity.player.attack.weak", 1, 1);
								}
							}
						} else {
							// --- Damageless attack sound --- //
							world.playSoundEffect(x, y, z, Reference.MCAssetVer + ":entity.player.attack.nodamage", 1, 1);
						}
					}
				}
			}
		}

		if (ConfigBlocksItems.enableTotemUndying) {
			handleTotemCheck(targetEntity, event);
		}

		if (EntitySquid.class.equals(targetEntity.getClass())) {
			World w = targetEntity.worldObj;
			Random r = w.rand;
			boolean inkSound = ConfigEntities.enableSquidInk || ConfigTweaks.squidsBlindPlayers;
			doInk:
			if (inkSound && r.nextDouble() < 0.15 && w instanceof WorldServer serverWorld && targetEntity.isInWater()) {
				AxisAlignedBB eBox = targetEntity.boundingBox;
				double cx = eBox.maxX - 0.5 * (eBox.maxX - eBox.minX);
				double cy = eBox.maxY - 0.5 * (eBox.maxY - eBox.minY);
				double cz = eBox.maxZ - 0.5 * (eBox.maxZ - eBox.minZ);

				AxisAlignedBB box = AxisAlignedBB.getBoundingBox(-1.5, -1.5, -1.5, 1.5, 1.5, 1.5).offset(cx, cy, cz);
				List<EntityLivingBase> around = w.getEntitiesWithinAABB(EntityLivingBase.class, box);

				if (around.isEmpty()) {
					break doInk;
				}

				EntityLivingBase target = around.get(r.nextInt(around.size()));
				if (target != null && target != targetEntity) {
					if (ConfigEntities.enableSquidInk) {
						serverWorld.func_147487_a("largesmoke", cx, cy, cz, 5, 0.0, 0.0, 0.0, 0.08);
					}
					if (ConfigMixins.newMobSounds) {
						float pitch = (r.nextFloat() - r.nextFloat()) * 0.2F + (target.isChild() ? 1.5F : 1.0F);
						w.playSoundAtEntity(target, Reference.MCAssetVer + ":entity.squid.squirt", 0.4F, pitch);
					}
					if (target.isInWater() && ConfigTweaks.squidsBlindPlayers) {
						PotionEffect activeEff = target.getActivePotionEffect(Potion.blindness);
						int time = 20 + r.nextInt(300);
						if (activeEff != null) {
							if (activeEff.getAmplifier() > 0) {
								break doInk;
							}
							time += activeEff.getDuration();
						}
						target.addPotionEffect(new PotionEffect(Potion.blindness.id, time));
					}
				}
			}
		}

		// If the attacker is a player spawn the hearts aligned and facing it
		if (event.source instanceof EntityDamageSource) {
			if (ConfigWorld.enableDmgIndicator) {
				int amount = MathHelper.floor_float(Math.min(event.entityLiving.getHealth(), event.ammount) / 2F);
				amount = (int) applyArmorCalculations(event.entityLiving, event.source, amount);
				amount = (int) applyPotionDamageCalculations(event.entityLiving, event.source, amount);
				amount = Math.min(amount, 25);
				if (amount > 0) {
					EntityDamageSource src = (EntityDamageSource) event.source;
					Entity attacker = src.getSourceOfDamage();
					if (attacker instanceof EntityPlayer player && !(attacker instanceof FakePlayer)) {
						Vec3 look = player.getLookVec();
						look.rotateAroundY((float) Math.PI / 2);
						for (int i = 0; i < amount; i++) {
							double x = event.entityLiving.posX - (0.35D * look.xCoord / 2 * look.xCoord) + (i / 3);
							double y = event.entityLiving.posY + (event.entityLiving.height * 0.75D);
							double z = event.entityLiving.posZ - (0.35D * look.zCoord / 2 * look.zCoord) + (i / 3);
							EtFuturum.networkWrapper.sendToAllAround(new BlackHeartParticlesMessage(x, y, z), new TargetPoint(player.worldObj.provider.dimensionId, x, y, z, 64));
						}
					}
				}
			}
			if (ConfigSounds.thornsSounds && event.source.getDamageType().equals("thorns")) {
				event.entity.worldObj.playSoundAtEntity(event.entity, Reference.MCAssetVer + ":enchant.thorns.hit", 1, 1);
			}
		}
	}

	public void handleTotemCheck(final EntityLivingBase entity, final LivingHurtEvent event) {
		if (entity.getHealth() > Math.round(event.ammount)) {
			return;
		}
		if (entity.getHeldItem() == null || entity.getHeldItem().getItem() != ModItems.TOTEM_OF_UNDYING.get()) {
			return;
		}

		if (entity instanceof EntityLiving || entity instanceof EntityPlayer) {
			//this.spawnTotemParticles(player);
			entity.worldObj.playSoundEffect(entity.posX + 0.5, entity.posY + 0.5, entity.posZ + 0.5, Reference.MCAssetVer + ":item.totem.use", 1.0f, entity.worldObj.rand.nextFloat() * 0.1f + 0.9f);

			entity.clearActivePotions();
			float healpercent = (float) ConfigFunctions.totemHealPercent / 100;
			float sethp = entity.getMaxHealth() * healpercent;
			entity.setHealth(Math.max(sethp, .5F));
			event.ammount = 0;
			entity.addPotionEffect(new PotionEffect(Potion.regeneration.id, 900, 1));
			entity.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 800, 1));
			entity.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, 100, 1)); // absorption
			//TODO: Make it respect a stack size

			if (entity instanceof EntityLiving) {
				entity.setCurrentItemOrArmor(0, null);
			}
			if (entity instanceof EntityPlayer) {
				((EntityPlayer) entity).addChatMessage(new ChatComponentText(StatCollector.translateToLocal("util.totemBreak")));
				((EntityPlayer) entity).destroyCurrentEquippedItem();
			}
		}
	}

	@SubscribeEvent
	public void onDrops(BlockEvent.HarvestDropsEvent event) {
		if (event.block == Blocks.double_stone_slab) {
			if (ModBlocks.SMOOTH_STONE.isEnabled() && event.blockMetadata == 8) {
				event.drops.clear();
				event.drops.add(ModBlocks.SMOOTH_STONE.newItemStack(1));
			} else if (ModBlocks.SMOOTH_SANDSTONE.isEnabled() && event.blockMetadata == 9) {
				event.drops.clear();
				event.drops.add(ModBlocks.SMOOTH_SANDSTONE.newItemStack(1));
			} else if (ModBlocks.SMOOTH_QUARTZ.isEnabled() && event.blockMetadata == 15) {
				event.drops.clear();
				event.drops.add(ModBlocks.SMOOTH_QUARTZ.newItemStack(1));
			}
		}
	}

	@SubscribeEvent
	public void loadWorldEvent(WorldEvent.Load event) {
		event.world.addWorldAccess(new EtFuturumWorldListener(event.world));

		if (ConfigMixins.enableDoWeatherCycle) {
			DoWeatherCycle.registerGamerule(event.world);
		}

		if (ConfigMixins.enablePlayersSleepingPecentageGamerule) {
			PlayersSleepingPercentage.registerGamerule(event.world);
		}

		if (ConfigMixins.enableRandomTickSpeed) {
			RandomTickSpeed.registerGamerule(event.world);
		}
	}

	@SubscribeEvent
	public void onItemToss(ItemTossEvent event) {
		if (ConfigMixins.avoidDroppingItemsWhenClosing && event.player instanceof EntityPlayerMP && playersClosedContainers.contains(event.player)) {
			if (event.player.inventory.addItemStackToInventory(event.entityItem.getEntityItem())) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onTickEnd(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END)
			playersClosedContainers.clear();
	}

	private final Map<EntityPlayer, MutableFloat> lastAttackedAtYaw = new WeakHashMap<>();

	@SubscribeEvent
	public void postPlayerTick(TickEvent.PlayerTickEvent e) {
		if (e.phase == TickEvent.Phase.END) {
			if (ConfigFunctions.enableAttackedAtYawFix && !e.player.worldObj.isRemote && e.player instanceof EntityPlayerMP) {
				if (!lastAttackedAtYaw.containsKey(e.player)) {
					lastAttackedAtYaw.put(e.player, new MutableFloat(e.player.attackedAtYaw));
				}
				if (Math.abs(lastAttackedAtYaw.get(e.player).floatValue() - e.player.attackedAtYaw) > 0.05F) {
					EtFuturum.networkWrapper.sendTo(new AttackYawMessage(e.player.attackedAtYaw), (EntityPlayerMP) e.player);
					lastAttackedAtYaw.get(e.player).setValue(e.player.attackedAtYaw);
				}
			}
			if (ConfigMixins.enableElytra) {
				boolean isElytraFlying = ((IElytraPlayer) e.player).etfu$isElytraFlying();
				if (e.player instanceof EntityPlayerMP && isElytraFlying) {
					((EntityPlayerMP) e.player).playerNetServerHandler.floatingTickCount = 0;
				}
				if (isElytraFlying != ((IElytraPlayer) e.player).etfu$lastElytraFlying()) {
					float f = 0.6f;
					float f1 = isElytraFlying ? 0.6f : 1.8f;

					if (f != e.player.width || f1 != e.player.height) {
						// Always reset hitbox. For future reference, Backlytra used world.func_147461_a to check that there was nothing in the bounding box.
						float f2 = e.player.width;
						e.player.width = f;
						e.player.height = f1;
						e.player.boundingBox.setBounds(e.player.boundingBox.minX, e.player.boundingBox.minY, e.player.boundingBox.minZ, e.player.boundingBox.minX + e.player.width, e.player.boundingBox.minY + e.player.height, e.player.boundingBox.minZ + e.player.width);

						if (e.player.width > f2 && !e.player.worldObj.isRemote) {
							e.player.moveEntity(f2 - e.player.width, 0.0D, f2 - e.player.width);
						}
					}
					((IElytraPlayer) e.player).etfu$setLastElytraFlying(isElytraFlying);
				}
			}
		}
	}

	@SubscribeEvent
	public void onExplosion(ExplosionEvent kaboom) {
		if (ConfigBlocksItems.enableLingeringPotions && !kaboom.world.isRemote && kaboom.explosion.exploder instanceof EntityCreeper creeper) {
			Collection<PotionEffect> collection = creeper.getActivePotionEffects();
			if (!collection.isEmpty()) {
				ItemStack potion = new ItemStack(ModItems.LINGERING_POTION.get());
				NBTTagCompound compound1 = new NBTTagCompound();
				NBTTagList potionList = new NBTTagList();

				for (PotionEffect potioneffect : collection) {
					NBTTagCompound compound2 = new NBTTagCompound();
					potioneffect.writeCustomPotionEffectToNBT(compound2);
					potionList.appendTag(compound2);
				}

				compound1.setTag("CustomPotionEffects", potionList);
				potion.setTagCompound(compound1);

				EntityLingeringEffect entityareaeffectcloud = new EntityLingeringEffect(kaboom.world, potion, creeper);
				entityareaeffectcloud.setPosition(creeper.posX, creeper.posY, creeper.posZ);
				entityareaeffectcloud.setColorOverride(PotionHelper.calcPotionLiquidColor(collection));
//              entityareaeffectcloud.setRadius(2.5F);
//              entityareaeffectcloud.setRadiusOnUse(-0.5F);
//              entityareaeffectcloud.setWaitTime(10);
//              entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
//              entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());

				kaboom.world.spawnEntityInWorld(entityareaeffectcloud);
			}
		}
	}

	@SubscribeEvent
	public void onPreWorldTick(TickEvent.WorldTickEvent e) {
		if (e.phase == TickEvent.Phase.START && e.side == Side.SERVER) {
			if (ModBlocks.CONCRETE_POWDER.isEnabled() && ModBlocks.CONCRETE.isEnabled()) {
				doConcreteTracking();
			}
			if (ConfigMixins.enableDoWeatherCycle) {
				DoWeatherCycle.INSTANCE.isWorldTickInProgress = true;
				DoWeatherCycle.INSTANCE.isCommandInProgress = false;
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unchecked")
	public void onPostWorldTick(TickEvent.WorldTickEvent e) {
		if (ConfigMixins.enableElytra && e.phase == TickEvent.Phase.END && e.world instanceof WorldServer ws) {
			for (EntityTrackerEntry ete : (Set<EntityTrackerEntry>) ws.getEntityTracker().trackedEntities) {
				if (ete != null && ete.myEntity instanceof IElytraPlayer elb) {
					boolean flying = elb.etfu$isElytraFlying();
					if (!flying && ((IElytraEntityTrackerEntry) ete).etfu$getWasSendingVelUpdates()) {
						ete.sendVelocityUpdates = false;
					} else if (flying) {
						if (!ete.sendVelocityUpdates) {
							((IElytraEntityTrackerEntry) ete).etfu$setWasSendingVelUpdates(true);
						}
						ete.sendVelocityUpdates = true;
					}
				}
			}
		}

		if (ConfigMixins.enableDoWeatherCycle && e.phase == TickEvent.Phase.END && e.side == Side.SERVER) {
			DoWeatherCycle.INSTANCE.isWorldTickInProgress = false;
		}
	}

	private void doConcreteTracking() {
		for (Iterator<EntityFallingBlock> iterator = fallingConcreteBlocks.iterator(); iterator.hasNext(); ) {
			EntityFallingBlock block = iterator.next();
			if (!block.isDead) {
				int i = MathHelper.floor_double(block.posX);
				int j = MathHelper.floor_double(block.posY);
				int k = MathHelper.floor_double(block.posZ);

				for (int jOff = 0; jOff <= (block.motionY < -1.0 ? 1 : 0); jOff++) { // If it's moving downward faster than a threshold speed: 1 in this case
					if (block.worldObj.getBlock(i, j - jOff, k).getMaterial() == Material.water) {
						block.worldObj.setBlock(i, j - jOff, k, ModBlocks.CONCRETE.get(), block.field_145814_a, 3); // metadata
						block.setDead();
						iterator.remove();
					}
				}
			} else {
				iterator.remove();
			}
		}
	}

	private final ItemStackSet noBurnItems = new ItemStackSet();
	private final ItemStackMap<Integer> burnTimeRemappings = new ItemStackMap<>();

	@SubscribeEvent
	public void fuelBurnTime(FuelBurnTimeEvent e) {
		if (e.fuel == null || e.fuel.getItem() == null || Item.itemRegistry.getNameForObject(e.fuel.getItem()) == null)
			return;

		initFurnaceModifiers();

		if (noBurnItems.contains(e.fuel)) {
			e.burnTime = 0;
			e.setResult(Result.DENY);
			return;
		}

		Integer time = burnTimeRemappings.get(e.fuel);
		if (time != null) {
			e.burnTime = time;
			e.setResult(Result.ALLOW);
		}
	}

	private final AtomicBoolean initNoBurnItems = new AtomicBoolean(false);
	private final AtomicBoolean initBurnTimeRemappings = new AtomicBoolean(false);

	private void initFurnaceModifiers() {
		if (!initNoBurnItems.getAndSet(true)) {
			noBurnItems.add(ModBlocks.WOOD_PLANKS.newItemStack(1, 0));
			noBurnItems.add(ModBlocks.WOOD_PLANKS.newItemStack(1, 1));
			noBurnItems.add(ModBlocks.WOOD_FENCE.newItemStack(1, 0));
			noBurnItems.add(ModBlocks.WOOD_FENCE.newItemStack(1, 1));

			noBurnItems.add(ModBlocks.WOOD_SLAB.newItemStack(1, 0));
			noBurnItems.add(ModBlocks.WOOD_SLAB.newItemStack(1, 1));
			noBurnItems.add(ModBlocks.WOOD_SLAB.newItemStack(1, 8));
			noBurnItems.add(ModBlocks.WOOD_SLAB.newItemStack(1, 9));

			noBurnItems.add(ModBlocks.DOUBLE_WOOD_SLAB.newItemStack(1, 0));
			noBurnItems.add(ModBlocks.DOUBLE_WOOD_SLAB.newItemStack(1, 1));
			noBurnItems.add(ModBlocks.DOUBLE_WOOD_SLAB.newItemStack(1, 8));
			noBurnItems.add(ModBlocks.DOUBLE_WOOD_SLAB.newItemStack(1, 9));

			noBurnItems.add(ModBlocks.CRIMSON_STEM.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.WARPED_STEM.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.CRIMSON_STAIRS.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.WARPED_STAIRS.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.CRIMSON_FENCE_GATE.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.WARPED_FENCE_GATE.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.CRIMSON_BUTTON.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.WARPED_BUTTON.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.CRIMSON_PRESSURE_PLATE.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.WARPED_PRESSURE_PLATE.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.CRIMSON_DOOR.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.WARPED_DOOR.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.CRIMSON_TRAPDOOR.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.WARPED_TRAPDOOR.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.CRIMSON_SIGN.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			noBurnItems.add(ModBlocks.WARPED_SIGN.newItemStack(1, OreDictionary.WILDCARD_VALUE));

			for (ModBlocks bed : ModBlocks.BEDS) {
				if (bed.isEnabled()) {
					noBurnItems.add(bed.newItemStack());
				}
			}
		}

		if (!initBurnTimeRemappings.getAndSet(true)) {
			burnTimeRemappings.put(ModItems.BAMBOO.newItemStack(1, OreDictionary.WILDCARD_VALUE), 50);
		}
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e) {
		if (ConfigMixins.enableElytra)
			e.world.getGameRules().addGameRule("disableElytraMovementCheck", "false");
	}

	static MovingObjectPosition getMovingObjectPositionFromPlayer(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
		float f = 1.0F;
		float f1 = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) * f;
		float f2 = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) * f;
		double d0 = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX) * (double) f;
		double d1 = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) * (double) f + (double) (worldIn.isRemote ? playerIn.getEyeHeight() - playerIn.getDefaultEyeHeight() : playerIn.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
		double d2 = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ) * (double) f;
		Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = 5.0D;
		if (playerIn instanceof EntityPlayerMP) {
			d3 = ((EntityPlayerMP) playerIn).theItemInWorldManager.getBlockReachDistance();
		}
		Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
		return worldIn.func_147447_a/*rayTraceBlocks*/(vec3, vec31, useLiquids, !useLiquids, false);
	}


	// UNUSED FUNCTIONS
	//private boolean playerHasItem(final EntityPlayer player, final ItemStack ist, final boolean checkEnabled) {
	//  for (int slot = 0; slot < player.inventory.mainInventory.length; ++slot) {
	//      if (player.inventory.mainInventory[slot] != null && player.inventory.mainInventory[slot].isItemEqual(ist)) {
	//          return true;
	//      }
	//  }
	//  return false;
	//}
	// UNUSED FUNCTIONS
	//private void decreaseItemByOne(final EntityPlayer player, final Item item) {
	//  for (int slot = 0; slot < player.inventory.mainInventory.length; ++slot) {
	//      if (player.inventory.mainInventory[slot] != null) {
	//          if (player.inventory.mainInventory[slot].getItem() == item) {
	//              player.inventory.decrStackSize(slot, 1);
	//              return;
	//          }
	//      }
	//  }
	//}
}
