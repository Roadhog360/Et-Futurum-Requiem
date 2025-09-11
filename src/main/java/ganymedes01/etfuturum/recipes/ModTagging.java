package ganymedes01.etfuturum.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.api.crops.IBeeGrowable;
import ganymedes01.etfuturum.blocks.BlockBerryBush;
import ganymedes01.etfuturum.blocks.IDegradable;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.items.ItemNewBoat;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.hogtags.helpers.BlockTags;
import roadhog360.hogutils.api.hogtags.helpers.ItemTags;
import roadhog360.hogutils.api.utils.GenericUtils;
import roadhog360.hogutils.api.utils.RecipeHelper;

import java.util.Map;

public class ModTagging {

	public static void registerBlockTagsDynamic(Block block) {
		// HOE MINING DYNAMIC TAGS
		if (block instanceof BlockLeaves || block instanceof BlockHay || block instanceof BlockSponge || block instanceof BlockNetherWart) {
			BlockTags.addTags(block, "minecraft:mineable/hoe");
		}

		// BEE FLOWER DYNAMIC TAGS
		if (block instanceof BlockFlower) {
			BlockTags.addTags(block, "minecraft:bee_attractive");
			if(Item.getItemFromBlock(block) != null) {
				ItemTags.addTags(Item.getItemFromBlock(block), "minecraft:bee_food");
			}
		}
		if (block instanceof BlockCrops || block instanceof BlockStem || block instanceof BlockBerryBush || block instanceof IBeeGrowable) {
			BlockTags.addTags(block, OreDictionary.WILDCARD_VALUE, "minecraft:bee_growables");
			//TODO: Add cave vines as a pollinatable crop, when they get added
		}

		if (ModSounds.soundAmethystBlock == block.stepSound) {
			BlockTags.addTags(block, "minecraft:crystal_sound_blocks");
		}
	}

	public static void registerItemTagsDynamic(Item item) {
	}

	public static void registerEarlyHogTags() {
		ItemTags.addTags(Items.blaze_powder, "minecraft:brewing_fuel");
		ItemTags.addTags(Items.dye, 4, Tags.MOD_ID + ":enchantment_fuel");

		BlockTags.addInheritors("minecraft:campfires", Tags.MOD_ID + ":bee_hive_fumigator");
	}

	@SuppressWarnings("unchecked")
	static void registerHogTags() {
		BlockTags.addTags(ModBlocks.SHROOMLIGHT.get(), "minecraft:mineable/hoe");
		BlockTags.addTags(ModBlocks.SPONGE.get(), "minecraft:mineable/hoe");
		BlockTags.addTags(ModBlocks.TARGET.get(), "minecraft:mineable/hoe");
		BlockTags.addTags(ModBlocks.NETHER_WART.get(), "minecraft:mineable/hoe");
		BlockTags.addTags(ModBlocks.SCULK.get(), "minecraft:mineable/hoe");
		BlockTags.addTags(ModBlocks.SCULK_CATALYST.get(), "minecraft:mineable/hoe");
		BlockTags.addTags(ModBlocks.TARGET.get(), "minecraft:mineable/hoe");

		BlockTags.addTags(ModBlocks.DEEPSLATE.get(), "minecraft:deepslate_ore_replaceables");
		BlockTags.addTags(ModBlocks.TUFF.get(), 0, "minecraft:deepslate_ore_replaceables");

		BlockTags.addTagsByID("Gregtech", "gt.blockmachines", "etfuturum:spectators_cannot_interact");
		BlockTags.addTagsByID("TConstruct", "SearedBlock", "etfuturum:spectators_cannot_interact");
		BlockTags.addTagsByID("TConstruct", "Smeltery", "etfuturum:spectators_cannot_interact");
//		BlockTags.addTagsByID("Thaumcraft", "blockTable", "etfuturum:spectators_cannot_interact"); //Found no interaction faults, keeping as a comment for note-taking purposes

		doBeeTags();
		doPistonTags();
	}

	private static void doBeeTags() {
		ItemTags.addTags(Item.getItemFromBlock(Blocks.double_plant), 0, "minecraft:bee_food");
		ItemTags.addTags(Item.getItemFromBlock(Blocks.double_plant), 1, "minecraft:bee_food");
		ItemTags.addTags(Item.getItemFromBlock(Blocks.double_plant), 4, "minecraft:bee_food");
		ItemTags.addTags(Item.getItemFromBlock(Blocks.double_plant), 5, "minecraft:bee_food");
		BlockTags.addTags(Blocks.double_plant, 0, "minecraft:bee_attractive");
		BlockTags.addTags(Blocks.double_plant, 1, "minecraft:bee_attractive");
		BlockTags.addTags(Blocks.double_plant, 4, "minecraft:bee_attractive");
		BlockTags.addTags(Blocks.double_plant, 5, "minecraft:bee_attractive");

		ItemTags.addTags(ModBlocks.AZALEA.newItemStack(1, 1), "minecraft:bee_food");
		BlockTags.addTags(ModBlocks.AZALEA.get(), 1, "minecraft:bee_attractive");
		BlockTags.addTags(ModBlocks.AZALEA.get(), 9, "minecraft:bee_attractive");

		ItemTags.addTags(ModBlocks.AZALEA_LEAVES.newItemStack(1, 1), "minecraft:bee_food");
		BlockTags.addTags(ModBlocks.AZALEA_LEAVES.get(), 1, "minecraft:bee_attractive");
		BlockTags.addTags(ModBlocks.AZALEA_LEAVES.get(), 5, "minecraft:bee_attractive");
		BlockTags.addTags(ModBlocks.AZALEA_LEAVES.get(), 9, "minecraft:bee_attractive");
		BlockTags.addTags(ModBlocks.AZALEA_LEAVES.get(), 13, "minecraft:bee_attractive");

		//Mangrove propagule
		ItemTags.addTags(ModBlocks.SAPLING.newItemStack(), "minecraft:bee_food");
		BlockTags.addTags(ModBlocks.SAPLING.get(), 0, "minecraft:bee_attractive");
		BlockTags.addTags(ModBlocks.SAPLING.get(), 8, "minecraft:bee_attractive");

		//Cherry leaves
		ItemTags.addTags(ModBlocks.LEAVES.newItemStack(1, 1), "minecraft:bee_food");
		BlockTags.addTags(ModBlocks.LEAVES.get(), 1, "minecraft:bee_attractive");
		BlockTags.addTags(ModBlocks.LEAVES.get(), 5, "minecraft:bee_attractive");
		BlockTags.addTags(ModBlocks.LEAVES.get(), 9, "minecraft:bee_attractive");
		BlockTags.addTags(ModBlocks.LEAVES.get(), 13, "minecraft:bee_attractive");

		ItemTags.addTags(ModBlocks.PINK_PETALS.newItemStack(), "minecraft:bee_food");
		BlockTags.addTags(ModBlocks.PINK_PETALS.get(), "minecraft:bee_attractive");

		ItemTags.addTags(ModBlocks.CHORUS_FLOWER.newItemStack(), "minecraft:bee_food");
		BlockTags.addTags(ModBlocks.CHORUS_FLOWER.get(), "minecraft:bee_attractive");

		BlockTags.addTagsByID("campfirebackport", "campfire", "minecraft:campfires");
		BlockTags.addTagsByID("campfirebackport", "soul_campfire", "minecraft:campfires");
		BlockTags.addTagsByID("campfirebackport", "foxfire_campfire", "minecraft:campfires");
		BlockTags.addTagsByID("campfirebackport", "shadow_campfire", "minecraft:campfires");
		BlockTags.addTagsByID("BambooMod", "campfire", "minecraft:campfires");
		BlockTags.addTagsByID("mod_ecru_MapleTree", "ecru_BlockFallenLeavesFire", Tags.MOD_ID + ":bee_hive_fumigator");
		BlockTags.addTagsByID("Thaumcraft", "blockAiry", Tags.MOD_ID + ":bee_hive_fumigator");
		//TODO: And spore blossoms, when added
	}

	private static void doPistonTags() {
		BlockTags.addTags(ModBlocks.SLIME.get(), Tags.MOD_ID + ":piston_slime_blocks");
		BlockTags.addTags(ModBlocks.HONEY_BLOCK.get(), Tags.MOD_ID + ":piston_honey_blocks");
		for (ModBlocks mb : ModBlocks.TERRACOTTA) {
			if (mb.isEnabled()) {
				BlockTags.addTags(mb.get(), Tags.MOD_ID + ":piston_slick_blocks");
			}
		}

		//Begin mod blocks
		BlockTags.addTagsByID("VillageNames", "glazedTerracotta", Tags.MOD_ID + ":piston_slick_blocks");
		BlockTags.addTagsByID("VillageNames", "glazedTerracotta2", Tags.MOD_ID + ":piston_slick_blocks");
		BlockTags.addTagsByID("VillageNames", "glazedTerracotta3", Tags.MOD_ID + ":piston_slick_blocks");
		BlockTags.addTagsByID("VillageNames", "glazedTerracotta4", Tags.MOD_ID + ":piston_slick_blocks");

		for (String color : GenericUtils.Constants.MODERN_COLORS_SNAKE_CASE) {
			BlockTags.addTagsByID("uptodate", "glazed_terracotta_" + color, Tags.MOD_ID + ":piston_slick_blocks");
		}

		BlockTags.addTagsByID("TConstruct", "slime.gel",Tags.MOD_ID + ":piston_slime_blocks");
		BlockTags.addTagsByID("TConstruct", "GlueBlock", Tags.MOD_ID + ":piston_honey_blocks");

		BlockTags.addTagsByID("MineFactoryReloaded", "pinkslime.block", Tags.MOD_ID + ":piston_slime_blocks");

		BlockTags.addTagsByID("BiomesOPlenty", "honeyBlock", Tags.MOD_ID + ":piston_honey_blocks");
	}

	static void registerOreDictionary() {
		OreDictionary.registerOre("chestWood", new ItemStack(Blocks.chest));
		OreDictionary.registerOre("bookshelfWood", new ItemStack(Blocks.bookshelf));
		OreDictionary.registerOre("doorWood", new ItemStack(Items.wooden_door));
		OreDictionary.registerOre("trapdoorWood", Blocks.trapdoor);
		OreDictionary.registerOre("fenceWood", new ItemStack(Blocks.fence));
		OreDictionary.registerOre("fenceGateWood", new ItemStack(Blocks.fence_gate));
		OreDictionary.registerOre("buttonWood", new ItemStack(Blocks.wooden_button));
		OreDictionary.registerOre("pressurePlateWood", new ItemStack(Blocks.wooden_pressure_plate));
		OreDictionary.registerOre("bedWood", new ItemStack(Items.bed));
		OreDictionary.registerOre("doorIron", new ItemStack(Items.iron_door));
		OreDictionary.registerOre("buttonStone", new ItemStack(Blocks.stone_button));
		OreDictionary.registerOre("pressurePlateStone", new ItemStack(Blocks.stone_pressure_plate));
		OreDictionary.registerOre("pressurePlateIron", new ItemStack(Blocks.heavy_weighted_pressure_plate));
		OreDictionary.registerOre("pressurePlateGold", new ItemStack(Blocks.light_weighted_pressure_plate));

		OreDictionary.registerOre("bowlWood", new ItemStack(Items.bowl));
		OreDictionary.registerOre("soulSand", Blocks.soul_sand);

		for (ModBlocks bed : ModBlocks.BEDS) {
			RecipeHelper.registerOre("bedWood", bed.get());
		}

		ItemStack ore36 = ModBlocks.LEAVES.newItemStack(1, OreDictionary.WILDCARD_VALUE);
		RecipeHelper.registerOre("treeLeaves", ore36);
		ItemStack ore35 = ModBlocks.AZALEA_LEAVES.newItemStack(1, OreDictionary.WILDCARD_VALUE);
		RecipeHelper.registerOre("treeLeaves", ore35);
		ItemStack ore34 = ModBlocks.AZALEA.newItemStack(1, OreDictionary.WILDCARD_VALUE);
		RecipeHelper.registerOre("treeSapling", ore34);

		RecipeHelper.registerOre("soulSoil", ModBlocks.SOUL_SOIL.get());

		RecipeHelper.registerOre("nuggetIron", ModItems.NUGGET_IRON.get());

		RecipeHelper.registerOre("foodMuttonraw", ModItems.MUTTON_RAW.get());
		RecipeHelper.registerOre("listAllmeatraw", ModItems.MUTTON_RAW.get());
		RecipeHelper.registerOre("listAllmuttonraw", ModItems.MUTTON_RAW.get());

		RecipeHelper.registerOre("foodMuttoncooked", ModItems.MUTTON_COOKED.get());
		RecipeHelper.registerOre("listAllmeatcooked", ModItems.MUTTON_COOKED.get());
		RecipeHelper.registerOre("listAllmuttoncooked", ModItems.MUTTON_COOKED.get());

		RecipeHelper.registerOre("shardPrismarine", ModItems.PRISMARINE_SHARD.get());
		RecipeHelper.registerOre("crystalPrismarine", ModItems.PRISMARINE_CRYSTALS.get());
		ItemStack ore33 = ModBlocks.PRISMARINE_BLOCK.newItemStack(1, OreDictionary.WILDCARD_VALUE);
		RecipeHelper.registerOre("blockPrismarine", ore33);

		for (ModBlocks door : ModBlocks.DOORS) {
			RecipeHelper.registerOre("doorWood", door.get());
		}

		for (ModBlocks trapdoor : ModBlocks.TRAPDOORS) {
			RecipeHelper.registerOre("trapdoorWood", trapdoor.get());
		}

		for (ModBlocks fence : ModBlocks.FENCES) {
			RecipeHelper.registerOre("fenceWood", fence.get());
		}

		for (ModBlocks fenceGate : ModBlocks.FENCE_GATES) {
			RecipeHelper.registerOre("fenceGateWood", fenceGate.get());
		}

		for (ModBlocks button : ModBlocks.BUTTONS) {
			RecipeHelper.registerOre("buttonWood", button.get());
		}

		for (ModBlocks pressurePlate : ModBlocks.PRESSURE_PLATES) {
			RecipeHelper.registerOre("pressurePlateWood", pressurePlate.get());
		}

		RecipeHelper.registerOre("signWood", Items.sign);
		RecipeHelper.registerOre("signWood", ModItems.ITEM_SIGN_SPRUCE.get());
		RecipeHelper.registerOre("signWood", ModItems.ITEM_SIGN_BIRCH.get());
		RecipeHelper.registerOre("signWood", ModItems.ITEM_SIGN_JUNGLE.get());
		RecipeHelper.registerOre("signWood", ModItems.ITEM_SIGN_ACACIA.get());
		RecipeHelper.registerOre("signWood", ModItems.ITEM_SIGN_DARK_OAK.get());

		RecipeHelper.registerOre("signWood", ModBlocks.CRIMSON_SIGN.get());
		RecipeHelper.registerOre("signWood", ModBlocks.WARPED_SIGN.get());
		RecipeHelper.registerOre("signWood", ModBlocks.MANGROVE_SIGN.get());
		RecipeHelper.registerOre("signWood", ModBlocks.CHERRY_SIGN.get());
		RecipeHelper.registerOre("signWood", ModBlocks.BAMBOO_SIGN.get());

		if (ConfigExperiments.enableMangroveBlocks) {
			ItemStack ore = ModBlocks.SAPLING.newItemStack(1, 0);
			RecipeHelper.registerOre("treeSapling", ore);
		}
		if (ConfigBlocksItems.enableCherryBlocks) {
			ItemStack ore = ModBlocks.SAPLING.newItemStack(1, 1);
			RecipeHelper.registerOre("treeSapling", ore);
		}

		for (int i = 0; i < ModRecipes.modernWoodTypesEnabled.length; i++) {
			if (ModRecipes.modernWoodTypesEnabled[i]) {
				ItemStack ore2 = ModBlocks.WOOD_PLANKS.newItemStack(1, i);
				RecipeHelper.registerOre("plankWood", ore2);
				ItemStack ore1 = ModBlocks.WOOD_SLAB.newItemStack(1, i);
				RecipeHelper.registerOre("slabWood", ore1);
				ItemStack ore = ModBlocks.WOOD_FENCE.newItemStack(1, i);
				RecipeHelper.registerOre("fenceWood", ore);
			}
		}

		RecipeHelper.registerOre("stairWood", ModBlocks.CRIMSON_STAIRS.get());
		RecipeHelper.registerOre("stairWood", ModBlocks.WARPED_STAIRS.get());
		RecipeHelper.registerOre("stairWood", ModBlocks.MANGROVE_STAIRS.get());
		RecipeHelper.registerOre("stairWood", ModBlocks.CHERRY_STAIRS.get());
		RecipeHelper.registerOre("stairWood", ModBlocks.BAMBOO_STAIRS.get());

		// Log / Bark Variations
		for (int i = 0; i < 4; i++) {
			ItemStack ore9 = ModBlocks.LOG_STRIPPED.newItemStack(1, i);
			RecipeHelper.registerOre("logWood", ore9);
			ItemStack ore8 = ModBlocks.LOG2_STRIPPED.newItemStack(1, i);
			RecipeHelper.registerOre("logWood", ore8);
			ItemStack ore7 = ModBlocks.WOOD_STRIPPED.newItemStack(1, i);
			RecipeHelper.registerOre("logWood", ore7);
			ItemStack ore6 = ModBlocks.WOOD2_STRIPPED.newItemStack(1, i);
			RecipeHelper.registerOre("logWood", ore6);
			ItemStack ore5 = ModBlocks.BARK.newItemStack(1, i);
			RecipeHelper.registerOre("logWood", ore5);
			ItemStack ore4 = ModBlocks.BARK2.newItemStack(1, i);
			RecipeHelper.registerOre("logWood", ore4);

			if (i == 0
					|| (i == 1 && ConfigBlocksItems.enableStrippedLogs)
					|| (i == 2 && ConfigBlocksItems.enableBarkLogs)
					|| (i == 3 && ConfigBlocksItems.enableStrippedLogs && ConfigBlocksItems.enableBarkLogs)) {
				ItemStack ore3 = ModBlocks.CRIMSON_STEM.newItemStack(1, i);
				RecipeHelper.registerOre("logWood", ore3);
				ItemStack ore2 = ModBlocks.WARPED_STEM.newItemStack(1, i);
				RecipeHelper.registerOre("logWood", ore2);
				ItemStack ore1 = ModBlocks.MANGROVE_LOG.newItemStack(1, i);
				RecipeHelper.registerOre("logWood", ore1);
				ItemStack ore = ModBlocks.CHERRY_LOG.newItemStack(1, i);
				RecipeHelper.registerOre("logWood", ore);
			}
		}

		ItemStack ore32 = ModBlocks.STONE.newItemStack(1, 1);
		RecipeHelper.registerOre("stoneGranite", ore32);
		ItemStack ore31 = ModBlocks.STONE.newItemStack(1, 3);
		RecipeHelper.registerOre("stoneDiorite", ore31);
		ItemStack ore30 = ModBlocks.STONE.newItemStack(1, 5);
		RecipeHelper.registerOre("stoneAndesite", ore30);
		ItemStack ore29 = ModBlocks.STONE.newItemStack(1, 2);
		RecipeHelper.registerOre("stoneGranitePolished", ore29);
		ItemStack ore28 = ModBlocks.STONE.newItemStack(1, 4);
		RecipeHelper.registerOre("stoneDioritePolished", ore28);
		ItemStack ore27 = ModBlocks.STONE.newItemStack(1, 6);
		RecipeHelper.registerOre("stoneAndesitePolished", ore27);

		ItemStack ore26 = ModBlocks.SLIME.newItemStack();
		RecipeHelper.registerOre("blockSlime", ore26);

		RecipeHelper.registerOre("trapdoorIron", ModBlocks.IRON_TRAPDOOR.get());

		RecipeHelper.registerOre("cropBeetroot", ModItems.BEETROOT.get());
		RecipeHelper.registerOre("listAllseed", ModItems.BEETROOT_SEEDS.get());
		RecipeHelper.registerOre("seedBeetroot", ModItems.BEETROOT_SEEDS.get());

		ItemStack ore25 = ModItems.RABBIT_RAW.newItemStack();
		RecipeHelper.registerOre("foodRabbitraw", ore25);
		ItemStack ore24 = ModItems.RABBIT_RAW.newItemStack();
		RecipeHelper.registerOre("listAllmeatraw", ore24);
		ItemStack ore23 = ModItems.RABBIT_RAW.newItemStack();
		RecipeHelper.registerOre("listAllrabbitraw", ore23);

		ItemStack ore22 = ModItems.RABBIT_COOKED.newItemStack();
		RecipeHelper.registerOre("foodRabbitcooked", ore22);
		ItemStack ore21 = ModItems.RABBIT_COOKED.newItemStack();
		RecipeHelper.registerOre("listAllmeatcooked", ore21);
		ItemStack ore20 = ModItems.RABBIT_COOKED.newItemStack();
		RecipeHelper.registerOre("listAllrabbitcooked", ore20);

		RecipeHelper.registerOre("brickEndStone", ModBlocks.END_BRICKS.get());

		RecipeHelper.registerOre("ingotNetherite", ModItems.NETHERITE_INGOT.get());
		RecipeHelper.registerOre("scrapDebris", ModItems.NETHERITE_SCRAP.get());
		RecipeHelper.registerOre("oreDebris", ModBlocks.ANCIENT_DEBRIS.get());
		RecipeHelper.registerOre("blockNetherite", ModBlocks.NETHERITE_BLOCK.get());

		ItemStack ore19 = ModItems.DYE.newItemStack(1, OreDictionary.WILDCARD_VALUE);
		RecipeHelper.registerOre("dye", ore19);
		ItemStack ore18 = ModItems.DYE.newItemStack();
		RecipeHelper.registerOre("dyeWhite", ore18);
		ItemStack ore17 = ModItems.DYE.newItemStack(1, 1);
		RecipeHelper.registerOre("dyeBlue", ore17);
		ItemStack ore16 = ModItems.DYE.newItemStack(1, 2);
		RecipeHelper.registerOre("dyeBrown", ore16);
		ItemStack ore15 = ModItems.DYE.newItemStack(1, 3);
		RecipeHelper.registerOre("dyeBlack", ore15);

		ItemStack ore14 = ModBlocks.COPPER_ORE.newItemStack();
		RecipeHelper.registerOre("oreCopper", ore14);
		ItemStack ore13 = ModItems.COPPER_INGOT.newItemStack();
		RecipeHelper.registerOre("ingotCopper", ore13);
		ItemStack ore12 = ModBlocks.COPPER_BLOCK.newItemStack();
		RecipeHelper.registerOre("blockCopper", ore12);
		ItemStack ore11 = ModBlocks.COPPER_BLOCK.newItemStack(1, 4);
		RecipeHelper.registerOre("blockCopperCut", ore11);

		ItemStack ore10 = ModBlocks.COBBLED_DEEPSLATE.newItemStack();
		RecipeHelper.registerOre("cobblestone", ore10);

		RecipeHelper.registerOre("record", ModItems.PIGSTEP_RECORD.get());
		RecipeHelper.registerOre("record", ModItems.OTHERSIDE_RECORD.get());

		RecipeHelper.registerOre("gemAmethyst", ModItems.AMETHYST_SHARD.get());
		ItemStack ore9 = ModBlocks.TINTED_GLASS.newItemStack();
		RecipeHelper.registerOre("blockGlassTinted", ore9);

		RecipeHelper.registerOre("cobblestone", ModBlocks.BLACKSTONE.get());
		RecipeHelper.registerOre("buttonStone", ModBlocks.POLISHED_BLACKSTONE_BUTTON.get());
		RecipeHelper.registerOre("pressurePlateStone", ModBlocks.POLISHED_BLACKSTONE_PRESSURE_PLATE.get());

		for (Map.Entry<String, ItemNewBoat.BoatInfo> entry : ItemNewBoat.BOAT_INFO.entrySet()) {
			String key = entry.getKey();
			if (key == null) continue;
			if (key.endsWith("_chest")) {
				ItemStack ore = entry.getValue().getBoatItem();
				RecipeHelper.registerOre("boatChestWood", ore);
			} else {
				ItemStack ore = entry.getValue().getBoatItem();
				RecipeHelper.registerOre("boatWood", ore);
			}
		}

		for (String waxString : IDegradable.waxStrings) {
			RecipeHelper.registerOre(waxString, ModItems.HONEYCOMB.get());
		}

		if (ConfigBlocksItems.enableColourfulBeacons && ModsList.TINKERS_CONSTRUCT.isLoaded()) { //TCon lacks proper tagging for their glass; let's add the right tags so beacons can see it
			Block glassBlock = GameRegistry.findBlock("TConstruct", "GlassBlock.StainedClear");
			Block glassPane = GameRegistry.findBlock("TConstruct", "GlassPaneClearStained");
			OreDictionary.registerOre("blockGlass", new ItemStack(glassBlock, 1, OreDictionary.WILDCARD_VALUE));
			OreDictionary.registerOre("paneGlass", new ItemStack(glassPane, 1, OreDictionary.WILDCARD_VALUE));
			for (int i = 0; i < ModRecipes.ore_dyes.length; i++) {
				String capitalizedColor = ModRecipes.ore_dyes[15 - i].substring(3);
				OreDictionary.registerOre("blockGlass" + capitalizedColor, new ItemStack(glassBlock, 1, i));
				OreDictionary.registerOre("paneGlass" + capitalizedColor, new ItemStack(glassPane, 1, i));
			}
		}

		ItemStack ore8 = ModItems.RAW_ORE.newItemStack();
		RecipeHelper.registerOre("rawCopper", ore8);
		ItemStack ore7 = ModBlocks.RAW_ORE_BLOCK.newItemStack();
		RecipeHelper.registerOre("blockRawCopper", ore7);
		ItemStack ore6 = ModItems.RAW_ORE.newItemStack(1, 1);
		RecipeHelper.registerOre("rawIron", ore6);
		ItemStack ore5 = ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 1);
		RecipeHelper.registerOre("blockRawIron", ore5);
		ItemStack ore4 = ModItems.RAW_ORE.newItemStack(1, 2);
		RecipeHelper.registerOre("rawGold", ore4);
		ItemStack ore3 = ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 2);
		RecipeHelper.registerOre("blockRawGold", ore3);

		if (ConfigFunctions.registerRawItemAsOre) {
			//Todo: the registration of copper raw ore should be conditional because it is a configurable meta value, I may make the raw ore class itself provide enabled metas.
			ItemStack ore2 = ModItems.RAW_ORE.newItemStack();
			RecipeHelper.registerOre("oreCopper", ore2);
			ItemStack ore1 = ModItems.RAW_ORE.newItemStack(1, 1);
			RecipeHelper.registerOre("oreIron", ore1);
			ItemStack ore = ModItems.RAW_ORE.newItemStack(1, 2);
			RecipeHelper.registerOre("oreGold", ore);
		}
	}
}
