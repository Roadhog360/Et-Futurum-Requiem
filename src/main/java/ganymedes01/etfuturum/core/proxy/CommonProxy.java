package ganymedes01.etfuturum.core.proxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.client.gui.inventory.*;
import ganymedes01.etfuturum.configuration.configs.*;
import ganymedes01.etfuturum.core.handlers.EntityEventHandler;
import ganymedes01.etfuturum.core.handlers.SculkEventHandler;
import ganymedes01.etfuturum.core.handlers.ServerEventHandler;
import ganymedes01.etfuturum.core.handlers.WorldEventHandler;
import ganymedes01.etfuturum.core.utils.Logger;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.entities.*;
import ganymedes01.etfuturum.inventory.*;
import ganymedes01.etfuturum.lib.GUIIDs;
import ganymedes01.etfuturum.spectator.SpectatorMode;
import ganymedes01.etfuturum.tileentities.*;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonProxy implements IGuiHandler {

	public void registerEvents() {
		MinecraftForge.EVENT_BUS.register(EntityEventHandler.INSTANCE);

		FMLCommonHandler.instance().bus().register(ServerEventHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ServerEventHandler.INSTANCE);

		FMLCommonHandler.instance().bus().register(WorldEventHandler.INSTANCE);
		MinecraftForge.TERRAIN_GEN_BUS.register(WorldEventHandler.INSTANCE);

		if (ConfigMixins.enableSpectatorMode) {
			FMLCommonHandler.instance().bus().register(SpectatorMode.INSTANCE);
			MinecraftForge.EVENT_BUS.register(SpectatorMode.INSTANCE);
		}

		if (ModBlocks.SCULK_CATALYST.isEnabled()) {
			FMLCommonHandler.instance().bus().register(SculkEventHandler.INSTANCE);
			MinecraftForge.EVENT_BUS.register(SculkEventHandler.INSTANCE);
		}

		if (ModItems.DEBUGGING_TOOL.isEnabled()) {
			MinecraftForge.EVENT_BUS.register(ModItems.DEBUGGING_TOOL.get());
		}
	}

	public void registerEntities() {
		if (ModBlocks.BREWING_STAND.isEnabled()) {
			GameRegistry.registerTileEntity(TileEntityNewBrewingStand.class, Utils.getUnlocalisedName("brewing_stand"));
		}
		if (ModBlocks.BEACON.isEnabled()) {
			GameRegistry.registerTileEntity(TileEntityNewBeacon.class, Utils.getUnlocalisedName("beacon"));
		}
		if (ModBlocks.BARREL.isEnabled()) {
			GameRegistry.registerTileEntity(TileEntityBarrel.class, Utils.getUnlocalisedName("barrel"));
		}
		if (ModBlocks.SMOKER.isEnabled()) {
			GameRegistry.registerTileEntity(TileEntitySmoker.class, Utils.getUnlocalisedName("smoker"));
		}
		if (ModBlocks.BLAST_FURNACE.isEnabled()) {
			GameRegistry.registerTileEntity(TileEntityBlastFurnace.class, Utils.getUnlocalisedName("blast_furnace"));
		}
		if (ConfigBlocksItems.enableVanillaSigns) {
			GameRegistry.registerTileEntity(TileEntityWoodSign.class, Utils.getUnlocalisedName("sign"));
		}
		if (ModBlocks.SCULK_CATALYST.isEnabled()) {
			GameRegistry.registerTileEntity(TileEntitySculkCatalyst.class, Utils.getUnlocalisedName("sculk_catalyst"));
		}
		if (ModBlocks.END_GATEWAY.isEnabled()) {
			GameRegistry.registerTileEntity(TileEntityGateway.class, Utils.getUnlocalisedName("end_gateway"));
		}
		if (ModBlocks.SHULKER_BOX.isEnabled()) {
			GameRegistry.registerTileEntity(TileEntityShulkerBox.class, Utils.getUnlocalisedName("shulker_box"));
		}
		if (ModBlocks.POTION_CAULDRON.isEnabled()) {
			GameRegistry.registerTileEntity(TileEntityCauldronPotion.class, Utils.getUnlocalisedName("potion_cauldron"));
		}
		if (ModBlocks.BANNER.isEnabled()) {
			GameRegistry.registerTileEntity(TileEntityBanner.class, Utils.getUnlocalisedName("banner"));
		}
		if (ModBlocks.BEEHIVE.isEnabled() || ModBlocks.BEE_NEST.isEnabled()) {
			GameRegistry.registerTileEntity(TileEntityBeeHive.class, Utils.getUnlocalisedName("hive"));
		}

		if (ConfigEntities.enableRabbit) {
			ModEntityList.registerEntity(EntityRabbit.class, "rabbit", 3, EtFuturum.instance, 80, 3, true, 0x995F40, 0x734831);

			EntityRegistry.addSpawn(EntityRabbit.class, 5, 3, 3, EnumCreatureType.creature, BiomeDictionary.getBiomesForType(Type.SANDY));
			BiomeGenBase[] array = BiomeDictionary.getBiomesForType(Type.SNOWY);
			array = ArrayUtils.addAll(array, BiomeDictionary.getBiomesForType(Type.PLAINS));
			array = ArrayUtils.addAll(array, BiomeDictionary.getBiomesForType(Type.FOREST));
			for (BiomeGenBase biome : ArrayUtils.clone(array)) {
				if (biome.getClass().getName().toLowerCase().contains("divinerpg")) {
					ArrayUtils.removeElement(array, biome);
				}
			}
			EntityRegistry.addSpawn(EntityRabbit.class, 10, 3, 3, EnumCreatureType.creature, array);
		}

		if (ConfigBlocksItems.enableArmourStand) {
			ModEntityList.registerEntity(EntityArmourStand.class, "wooden_armorstand", 0, EtFuturum.instance, 64, 1, true);
		}

		if (ConfigEntities.enableEndermite) {
			ModEntityList.registerEntity(EntityEndermite.class, "endermite", 1, EtFuturum.instance, 64, 1, true, 0x161616, 0x6E6E6E);
		}

		if (ConfigBlocksItems.enableTippedArrows) {
			ModEntityList.registerEntity(EntityTippedArrow.class, "tipped_arrow", 2, EtFuturum.instance, 64, 20, true);
		}

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT && FMLClientHandler.instance().hasOptifine()) {
			if (!ConfigWorld.oldHuskSpawning) {
				Logger.warn("OptiFine detected, old husk spawn logic will be enabled since OptiFine is stupid and breaks the default behavior.");
				ConfigWorld.oldHuskSpawning = true;
			}
			if (!ConfigWorld.oldStraySpawning) {
				Logger.warn("OptiFine detected, old stray spawn logic will be enabled since OptiFine is stupid and breaks the default behavior.");
				ConfigWorld.oldStraySpawning = true;
			}
		}

		if (ConfigEntities.enableHusk) {
			ModEntityList.registerEntity(EntityHusk.class, "husk", 4, EtFuturum.instance, 80, 3, true, 0x777561, 0xE0D991);

			if (ConfigWorld.oldHuskSpawning) {
				List<BiomeGenBase> biomes = new ArrayList<>(Arrays.asList(BiomeDictionary.getBiomesForType(Type.SANDY)));
				biomes.retainAll(Arrays.asList(BiomeDictionary.getBiomesForType(Type.DRY)));
				biomes.retainAll(Arrays.asList(BiomeDictionary.getBiomesForType(Type.HOT)));

				BiomeGenBase[] biomeArray = (BiomeGenBase[]) ArrayUtils.addAll(new BiomeGenBase[]{}, biomes.toArray());
				//change spawn weights
				EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.monster, biomeArray);

				EntityRegistry.addSpawn(EntityZombie.class, 20, 4, 4, EnumCreatureType.monster, biomeArray);
				EntityRegistry.addSpawn(EntityHusk.class, 80, 4, 4, EnumCreatureType.monster, biomeArray);
			}
		}

		if (ConfigEntities.enableStray) {
			ModEntityList.registerEntity(EntityStray.class, "stray", 5, EtFuturum.instance, 80, 3, true, 0x617778, 0xE6EAEA);

			if (ConfigWorld.oldStraySpawning) {
				//change spawn weights
				EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.monster, BiomeDictionary.getBiomesForType(Type.SNOWY));

				EntityRegistry.addSpawn(EntitySkeleton.class, 20, 4, 4, EnumCreatureType.monster, BiomeDictionary.getBiomesForType(Type.SNOWY));
				EntityRegistry.addSpawn(EntityStray.class, 80, 4, 4, EnumCreatureType.monster, BiomeDictionary.getBiomesForType(Type.SNOWY));
			}
		}

		if (ConfigEntities.enableNetherEndermen) {
			EntityRegistry.addSpawn(EntityEnderman.class, 1, 4, 4, EnumCreatureType.monster, BiomeGenBase.hell);
//			if (ModBlocks.NYLIUM.isEnabled()) {
//				EntityEnderman.setCarriable(ModBlocks.NYLIUM.get(), true);
//			}
		}

		if (ConfigBlocksItems.enableLingeringPotions) {
			ModEntityList.registerEntity(EntityLingeringPotion.class, "lingering_potion", 6, EtFuturum.instance, 64, 10, true);
			ModEntityList.registerEntity(EntityLingeringEffect.class, "lingering_effect", 7, EtFuturum.instance, 64, 1, true);
		}

		if (ConfigEntities.enableVillagerZombies)
			ModEntityList.registerEntity(EntityZombieVillager.class, "villager_zombie", 8, EtFuturum.instance, 80, 3, true, 0x00AFAF, 0x799C65);

		if (ConfigEntities.enableDragonRespawn) {
			ModEntityList.registerEntity(EntityPlacedEndCrystal.class, "end_crystal", 9, EtFuturum.instance, 256, Integer.MAX_VALUE, false);
			ModEntityList.registerEntity(EntityRespawnedDragon.class, "ender_dragon", 10, EtFuturum.instance, 160, 3, true);
		}

		if (ConfigEntities.enableShearableSnowGolems)
			ModEntityList.registerEntity(EntityNewSnowGolem.class, "snow_golem", 11, EtFuturum.instance, 80, 3, true);

		if (ConfigEntities.enableBrownMooshroom)
			ModEntityList.registerEntity(EntityBrownMooshroom.class, "brown_mooshroom", 12, EtFuturum.instance, 80, 3, true);

		ModEntityList.registerEntity(EntityItemUninflammable.class, "fireproof_item", 15, EtFuturum.instance, 64, 1, true);

		if (ConfigEntities.enableShulker) {
			ModEntityList.registerEntity(EntityShulker.class, "shulker", 16, EtFuturum.instance, 80, 1, false, 0x946794, 0x4D3852);
			ModEntityList.registerEntity(EntityShulkerBullet.class, "shulker_candy", 17, EtFuturum.instance, 64, 1, true);

			if (ConfigTweaks.shulkersSpawnAnywhere) {
				EntityRegistry.addSpawn(EntityShulker.class, 1, 1, 2, EnumCreatureType.monster, BiomeDictionary.getBiomesForType(Type.END));
			}
		}

//      {
//          ModEntityList.registerEntity(EntityFallingDripstone.class, "falling_dripstone", 18, EtFuturum.instance, 64, 1, true);
//      }

		if (ConfigBlocksItems.enableNewBoats) {
			ModEntityList.registerEntity(EntityNewBoat.class, "new_boat", 13, EtFuturum.instance, 64, 1, true);
			ModEntityList.registerEntity(EntityNewBoatWithChest.class, "chest_boat", 19, EtFuturum.instance, 64, 1, true);
			ModEntityList.registerEntity(EntityNewBoatSeat.class, "new_boat_seat", 14, EtFuturum.instance, 64, 1, false);
		}

		if (ConfigMixins.enableElytra) {
			ModEntityList.registerEntity(EntityBoostingFireworkRocket.class, "boosting_firework_rocket", 20, EtFuturum.instance, 64, 1, true);
		}

		if (ConfigEntities.enableBees) {
			ModEntityList.registerEntity(EntityBee.class, "bee", 21, EtFuturum.instance, 80, 1, true, 0xEDC343, 0x43241B);
		}

		if (ConfigEntities.enableFoxes) {
			ModEntityList.registerEntity(EntityFox.class, "fox", 22, EtFuturum.instance, 64, 1, true, 0xD5B69F, 0xCC6920);
			EntityRegistry.addSpawn(EntityFox.class, 8, 2, 4, EnumCreatureType.creature, BiomeDictionary.getBiomesForType(Type.CONIFEROUS));
		}

		//make magmas slightly more common, hopefully.
		EntityRegistry.removeSpawn(EntityMagmaCube.class, EnumCreatureType.monster, BiomeGenBase.hell);
		EntityRegistry.addSpawn(EntityMagmaCube.class, 2, 4, 4, EnumCreatureType.monster, BiomeGenBase.hell);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return switch (ID) {
			case GUIIDs.ENCHANTING_TABLE -> new ContainerEnchantment(player.inventory, world, x, y, z);
			case GUIIDs.ANVIL -> new ContainerAnvil(player, world, x, y, z);
			case GUIIDs.BREWING_STAND ->
					new ContainerNewBrewingStand(player.inventory, (TileEntityNewBrewingStand) world.getTileEntity(x, y, z));
			case GUIIDs.BARREL -> new ContainerChest(player.inventory, (TileEntityBarrel) world.getTileEntity(x, y, z));
			case GUIIDs.SMOKER ->
					new ContainerSmoker(player.inventory, (TileEntitySmoker) world.getTileEntity(x, y, z));
			case GUIIDs.BLAST_FURNACE ->
					new ContainerBlastFurnace(player.inventory, (TileEntityBlastFurnace) world.getTileEntity(x, y, z));
			case GUIIDs.SHULKER_BOX ->
					new ContainerChestGeneric(player.inventory, (TileEntityShulkerBox) world.getTileEntity(x, y, z), ((TileEntityShulkerBox) world.getTileEntity(x, y, z)).getRowSize(), ((TileEntityShulkerBox) world.getTileEntity(x, y, z)).getSizeInventory() != 27);
			case GUIIDs.SMITHING_TABLE -> new ContainerSmithingTable(player.inventory, world);
			default -> null;
		};
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return switch (ID) {
			case GUIIDs.ENCHANTING_TABLE -> new GuiEnchantment(player.inventory, world, null);
			case GUIIDs.ANVIL -> new GuiAnvil(player, world, x, y, z);
			case GUIIDs.BREWING_STAND ->
					new GuiNewBrewingStand(player.inventory, (TileEntityNewBrewingStand) world.getTileEntity(x, y, z));
			case GUIIDs.BARREL -> new GuiChest(player.inventory, (TileEntityBarrel) world.getTileEntity(x, y, z));
			case GUIIDs.SMOKER -> new GuiSmoker(player.inventory, (TileEntitySmoker) world.getTileEntity(x, y, z));
			case GUIIDs.BLAST_FURNACE ->
					new GuiBlastFurnace(player.inventory, (TileEntityBlastFurnace) world.getTileEntity(x, y, z));
			case GUIIDs.SHULKER_BOX ->
					new GuiShulkerBox(player.inventory, (TileEntityShulkerBox) world.getTileEntity(x, y, z));
			case GUIIDs.SMITHING_TABLE -> new GuiSmithingTable(new ContainerSmithingTable(player.inventory, world));
			default -> null;
		};
	}

	public void registerRenderers() {
	}
}
