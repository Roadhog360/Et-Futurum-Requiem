package ganymedes01.etfuturum.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.api.DeepslateOreRegistry;
import ganymedes01.etfuturum.api.RawOreRegistry;
import ganymedes01.etfuturum.blocks.BaseSlab;
import ganymedes01.etfuturum.blocks.IDegradable;
import ganymedes01.etfuturum.blocks.ores.BaseDeepslateOre;
import ganymedes01.etfuturum.blocks.ores.BaseSubtypesDeepslateOre;
import ganymedes01.etfuturum.blocks.ores.modded.BlockGeneralModdedDeepslateOre;
import ganymedes01.etfuturum.blocks.rawore.modded.BlockGeneralModdedRawOre;
import ganymedes01.etfuturum.compat.ExternalContent;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.*;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.items.ItemNewBoat;
import ganymedes01.etfuturum.items.ItemSuspiciousStew;
import ganymedes01.etfuturum.items.rawore.modded.ItemGeneralModdedRawOre;
import ganymedes01.etfuturum.lib.EnumColor;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.recipes.crafting.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeFireworks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ModRecipes {

	public static final String[] ore_dyes = new String[]{"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"};
	public static final String[] dye_names = new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"};
	public static final String[] woodTypes = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"};
	public static final String[] modernWoodTypes = new String[]{"crimson", "warped", "mangrove", "cherry"};
	private static final boolean[] modernWoodTypesEnabled = new boolean[5];

	public static void init() {
		if (ConfigBlocksItems.enableBanners) {
			RecipeSorter.register(Reference.MOD_ID + ".RecipeDuplicatePattern", RecipeDuplicatePattern.class, Category.SHAPELESS, "after:minecraft:shapeless");
			RecipeSorter.register(Reference.MOD_ID + ".RecipeAddPattern", RecipeAddPattern.class, Category.SHAPED, "after:minecraft:shaped");
		}
		RecipeSorter.register(Reference.MOD_ID + ":shaped", ShapedEtFuturumRecipe.class, RecipeSorter.Category.SHAPED, "before:minecraft:shaped");
		RecipeSorter.register(Reference.MOD_ID + ":shapeless", ShapelessEtFuturumRecipe.class, RecipeSorter.Category.SHAPELESS, "before:minecraft:shapeless");

		modernWoodTypesEnabled[0] = ConfigExperiments.enableCrimsonBlocks;
		modernWoodTypesEnabled[1] = ConfigExperiments.enableWarpedBlocks;
		modernWoodTypesEnabled[2] = ConfigExperiments.enableMangroveBlocks;
		modernWoodTypesEnabled[3] = ConfigBlocksItems.enableCherryBlocks;
		modernWoodTypesEnabled[4] = ConfigBlocksItems.enableBambooBlocks;

		registerOreDictionary();
		registerRecipes();
		tweakRecipes();
		registerLoot();
	}

	private static void registerLoot() {
		if (ModItems.BEETROOT_SEEDS.isEnabled()) {
			ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(ModItems.BEETROOT_SEEDS.newItemStack(), 1, 2, 5));
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(ModItems.BEETROOT_SEEDS.newItemStack(), 1, 2, 5));
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(ModItems.BEETROOT_SEEDS.newItemStack(), 1, 2, 5));
			ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CORRIDOR, new WeightedRandomChestContent(ModItems.BEETROOT_SEEDS.newItemStack(), 1, 2, 5));
			ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_LIBRARY, new WeightedRandomChestContent(ModItems.BEETROOT_SEEDS.newItemStack(), 1, 2, 5));
			ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CROSSING, new WeightedRandomChestContent(ModItems.BEETROOT_SEEDS.newItemStack(), 1, 2, 5));
			ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(ModItems.BEETROOT_SEEDS.newItemStack(), 1, 2, 5));
		}
	}

	@SuppressWarnings("unchecked")
	private static void tweakRecipes() {
		if (ConfigBlocksItems.enableExtraVanillaSlabs && !ModsList.GTNH.isLoaded()) {
			removeFirstRecipeFor(Blocks.stone_slab, 0);
		}

		if (ConfigBlocksItems.enableVanillaDoors) {
			Items.wooden_door.setMaxStackSize(64);
			Items.iron_door.setMaxStackSize(64);
		}

		if (ConfigFunctions.enableDoorRecipeBuffs && !ModsList.GTNH.isLoaded()) {
			removeFirstRecipeFor(Items.wooden_door);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.wooden_door, 3), "xx", "xx", "xx", 'x', "plankWood"));
			removeFirstRecipeFor(Items.iron_door);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.iron_door, 3), "xx", "xx", "xx", 'x', "ingotIron"));
		}

		if (ConfigBlocksItems.enableVanillaFences && !ModsList.GTNH.isLoaded()) {
			removeFirstRecipeFor(Blocks.fence);
			removeFirstRecipeFor(Blocks.nether_brick_fence);
		}

		if (ConfigBlocksItems.replaceOldBoats && ConfigBlocksItems.enableNewBoats) {
			if (!ModsList.GTNH.isLoaded()) {
				removeFirstRecipeFor(Items.boat);
			}
			Items.boat.setTextureName("oak_boat");
		}

		if (ConfigFunctions.enableExtraBurnableBlocks) {
			Blocks.fire.setFireInfo(Blocks.fence_gate, 5, 20);
			Blocks.fire.setFireInfo(Blocks.fence, 5, 20);
			Blocks.fire.setFireInfo(Blocks.deadbush, 60, 100);
		}

		if (ModItems.RABBIT_FOOT.isEnabled()) {
			PotionHelper.potionRequirements.put(Potion.jump.getId(), "0 & 1 & !2 & 3");
			PotionHelper.potionAmplifiers.put(Potion.jump.getId(), "5");
			Potion.jump.liquidColor = 0x22FF4C;
		}

		if (ConfigFunctions.enableLangReplacements) {
			Blocks.wooden_button.setBlockName(Utils.getUnlocalisedName("oak_button"));
			Blocks.wooden_pressure_plate.setBlockName(Utils.getUnlocalisedName("oak_pressure_plate"));
		}

		if (ConfigBlocksItems.replaceOldBoats) {
			Items.boat.setUnlocalizedName(Utils.getUnlocalisedName("oak_boat"));
		}

		if (ConfigFunctions.fireworkRecipeFixes) {
			for (int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); i++) {
				Object recipe = CraftingManager.getInstance().getRecipeList().get(i);
				if (recipe != null && recipe.getClass() == RecipeFireworks.class) {
					CraftingManager.getInstance().getRecipeList().remove(i);
					CraftingManager.getInstance().getRecipeList().add(i, new RecipeFixedFireworks());
					break;
				}
			}
		}
	}

	private static void registerOreDictionary() {
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
			registerOre("bedWood", bed.get());
		}

		registerOre("treeLeaves", ModBlocks.LEAVES.newItemStack(1, OreDictionary.WILDCARD_VALUE));
		registerOre("treeLeaves", ModBlocks.AZALEA_LEAVES.newItemStack(1, OreDictionary.WILDCARD_VALUE));
		registerOre("treeSapling", ModBlocks.AZALEA.newItemStack(1, OreDictionary.WILDCARD_VALUE));

		registerOre("soulSoil", ModBlocks.SOUL_SOIL.get());

		registerOre("nuggetIron", ModItems.NUGGET_IRON.get());

		registerOre("foodMuttonraw", ModItems.MUTTON_RAW.get());
		registerOre("listAllmeatraw", ModItems.MUTTON_RAW.get());
		registerOre("listAllmuttonraw", ModItems.MUTTON_RAW.get());

		registerOre("foodMuttoncooked", ModItems.MUTTON_COOKED.get());
		registerOre("listAllmeatcooked", ModItems.MUTTON_COOKED.get());
		registerOre("listAllmuttoncooked", ModItems.MUTTON_COOKED.get());

		registerOre("shardPrismarine", ModItems.PRISMARINE_SHARD.get());
		registerOre("crystalPrismarine", ModItems.PRISMARINE_CRYSTALS.get());
		registerOre("blockPrismarine", ModBlocks.PRISMARINE_BLOCK.newItemStack(1, OreDictionary.WILDCARD_VALUE));

		for (ModBlocks door : ModBlocks.DOORS) {
			registerOre("doorWood", door.get());
		}

		for (ModBlocks trapdoor : ModBlocks.TRAPDOORS) {
			registerOre("trapdoorWood", trapdoor.get());
		}

		for (ModBlocks fence : ModBlocks.FENCES) {
			registerOre("fenceWood", fence.get());
		}

		for (ModBlocks fenceGate : ModBlocks.FENCE_GATES) {
			registerOre("fenceGateWood", fenceGate.get());
		}

		for (ModBlocks button : ModBlocks.BUTTONS) {
			registerOre("buttonWood", button.get());
		}

		for (ModBlocks pressurePlate : ModBlocks.PRESSURE_PLATES) {
			registerOre("pressurePlateWood", pressurePlate.get());
		}

		registerOre("signWood", Items.sign);
		registerOre("signWood", ModItems.ITEM_SIGN_SPRUCE.get());
		registerOre("signWood", ModItems.ITEM_SIGN_BIRCH.get());
		registerOre("signWood", ModItems.ITEM_SIGN_JUNGLE.get());
		registerOre("signWood", ModItems.ITEM_SIGN_ACACIA.get());
		registerOre("signWood", ModItems.ITEM_SIGN_DARK_OAK.get());

		registerOre("signWood", ModBlocks.CRIMSON_SIGN.get());
		registerOre("signWood", ModBlocks.WARPED_SIGN.get());
		registerOre("signWood", ModBlocks.MANGROVE_SIGN.get());
		registerOre("signWood", ModBlocks.CHERRY_SIGN.get());
		registerOre("signWood", ModBlocks.BAMBOO_SIGN.get());

		if (ConfigExperiments.enableMangroveBlocks) {
			registerOre("treeSapling", ModBlocks.SAPLING.newItemStack(1, 0));
		}
		if (ConfigBlocksItems.enableCherryBlocks) {
			registerOre("treeSapling", ModBlocks.SAPLING.newItemStack(1, 1));
		}

		for (int i = 0; i < modernWoodTypesEnabled.length; i++) {
			if (modernWoodTypesEnabled[i]) {
				registerOre("plankWood", ModBlocks.WOOD_PLANKS.newItemStack(1, i));
				registerOre("slabWood", ModBlocks.WOOD_SLAB.newItemStack(1, i));
				registerOre("fenceWood", ModBlocks.WOOD_FENCE.newItemStack(1, i));
			}
		}

		registerOre("stairWood", ModBlocks.CRIMSON_STAIRS.get());
		registerOre("stairWood", ModBlocks.WARPED_STAIRS.get());
		registerOre("stairWood", ModBlocks.MANGROVE_STAIRS.get());
		registerOre("stairWood", ModBlocks.CHERRY_STAIRS.get());
		registerOre("stairWood", ModBlocks.BAMBOO_STAIRS.get());

		// Log / Bark Variations
		for (int i = 0; i < 4; i++) {
			registerOre("logWood", ModBlocks.LOG_STRIPPED.newItemStack(1, i));
			registerOre("logWood", ModBlocks.LOG2_STRIPPED.newItemStack(1, i));
			registerOre("logWood", ModBlocks.WOOD_STRIPPED.newItemStack(1, i));
			registerOre("logWood", ModBlocks.WOOD2_STRIPPED.newItemStack(1, i));
			registerOre("logWood", ModBlocks.BARK.newItemStack(1, i));
			registerOre("logWood", ModBlocks.BARK2.newItemStack(1, i));

			if (i == 0
					|| (i == 1 && ConfigBlocksItems.enableStrippedLogs)
					|| (i == 2 && ConfigBlocksItems.enableBarkLogs)
					|| (i == 3 && ConfigBlocksItems.enableStrippedLogs && ConfigBlocksItems.enableBarkLogs)) {
				registerOre("logWood", ModBlocks.CRIMSON_STEM.newItemStack(1, i));
				registerOre("logWood", ModBlocks.WARPED_STEM.newItemStack(1, i));
				registerOre("logWood", ModBlocks.MANGROVE_LOG.newItemStack(1, i));
				registerOre("logWood", ModBlocks.CHERRY_LOG.newItemStack(1, i));
			}
		}

		registerOre("stoneGranite", ModBlocks.STONE.newItemStack(1, 1));
		registerOre("stoneDiorite", ModBlocks.STONE.newItemStack(1, 3));
		registerOre("stoneAndesite", ModBlocks.STONE.newItemStack(1, 5));
		registerOre("stoneGranitePolished", ModBlocks.STONE.newItemStack(1, 2));
		registerOre("stoneDioritePolished", ModBlocks.STONE.newItemStack(1, 4));
		registerOre("stoneAndesitePolished", ModBlocks.STONE.newItemStack(1, 6));

		registerOre("blockSlime", ModBlocks.SLIME.newItemStack());

		registerOre("trapdoorIron", ModBlocks.IRON_TRAPDOOR.get());

		registerOre("cropBeetroot", ModItems.BEETROOT.get());
		registerOre("listAllseed", ModItems.BEETROOT_SEEDS.get());
		registerOre("seedBeetroot", ModItems.BEETROOT_SEEDS.get());

		registerOre("foodRabbitraw", ModItems.RABBIT_RAW.newItemStack());
		registerOre("listAllmeatraw", ModItems.RABBIT_RAW.newItemStack());
		registerOre("listAllrabbitraw", ModItems.RABBIT_RAW.newItemStack());

		registerOre("foodRabbitcooked", ModItems.RABBIT_COOKED.newItemStack());
		registerOre("listAllmeatcooked", ModItems.RABBIT_COOKED.newItemStack());
		registerOre("listAllrabbitcooked", ModItems.RABBIT_COOKED.newItemStack());

		registerOre("brickEndStone", ModBlocks.END_BRICKS.get());

		registerOre("ingotNetherite", ModItems.NETHERITE_INGOT.get());
		registerOre("scrapDebris", ModItems.NETHERITE_SCRAP.get());
		registerOre("oreDebris", ModBlocks.ANCIENT_DEBRIS.get());
		registerOre("blockNetherite", ModBlocks.NETHERITE_BLOCK.get());

		registerOre("dye", ModItems.DYE.newItemStack(1, OreDictionary.WILDCARD_VALUE));
		registerOre("dyeWhite", ModItems.DYE.newItemStack());
		registerOre("dyeBlue", ModItems.DYE.newItemStack(1, 1));
		registerOre("dyeBrown", ModItems.DYE.newItemStack(1, 2));
		registerOre("dyeBlack", ModItems.DYE.newItemStack(1, 3));

		registerOre("oreCopper", ModBlocks.COPPER_ORE.newItemStack());
		registerOre("ingotCopper", ModItems.COPPER_INGOT.newItemStack());
		registerOre("blockCopper", ModBlocks.COPPER_BLOCK.newItemStack());
		registerOre("blockCopperCut", ModBlocks.COPPER_BLOCK.newItemStack(1, 4));

		registerOre("cobblestone", ModBlocks.COBBLED_DEEPSLATE.newItemStack());

		registerOre("record", ModItems.PIGSTEP_RECORD.get());
		registerOre("record", ModItems.OTHERSIDE_RECORD.get());

		registerOre("gemAmethyst", ModItems.AMETHYST_SHARD.get());
		registerOre("blockGlassTinted", ModBlocks.TINTED_GLASS.newItemStack());

		registerOre("cobblestone", ModBlocks.BLACKSTONE.get());
		registerOre("buttonStone", ModBlocks.POLISHED_BLACKSTONE_BUTTON.get());
		registerOre("pressurePlateStone", ModBlocks.POLISHED_BLACKSTONE_PRESSURE_PLATE.get());

		for (Map.Entry<String, ItemNewBoat.BoatInfo> entry : ItemNewBoat.BOAT_INFO.entrySet()) {
			String key = entry.getKey();
			if (key == null) continue;
			if (key.endsWith("_chest")) {
				registerOre("boatChestWood", entry.getValue().getBoatItem());
			} else {
				registerOre("boatWood", entry.getValue().getBoatItem());
			}
		}

		for (String waxString : IDegradable.waxStrings) {
			registerOre(waxString, ModItems.HONEYCOMB.get());
		}

		if (ConfigBlocksItems.enableColourfulBeacons && ModsList.TINKERS_CONSTRUCT.isLoaded()) { //TCon lacks proper tagging for their glass; let's add the right tags so beacons can see it
			Block glassBlock = GameRegistry.findBlock("TConstruct", "GlassBlock.StainedClear");
			Block glassPane = GameRegistry.findBlock("TConstruct", "GlassPaneClearStained");
			OreDictionary.registerOre("blockGlass", new ItemStack(glassBlock, 1, OreDictionary.WILDCARD_VALUE));
			OreDictionary.registerOre("paneGlass", new ItemStack(glassPane, 1, OreDictionary.WILDCARD_VALUE));
			for (int i = 0; i < ore_dyes.length; i++) {
				String capitalizedColor = ore_dyes[15 - i].substring(3);
				OreDictionary.registerOre("blockGlass" + capitalizedColor, new ItemStack(glassBlock, 1, i));
				OreDictionary.registerOre("paneGlass" + capitalizedColor, new ItemStack(glassPane, 1, i));
			}
		}

		registerOre("rawCopper", ModItems.RAW_ORE.newItemStack());
		registerOre("blockRawCopper", ModBlocks.RAW_ORE_BLOCK.newItemStack());
		registerOre("rawIron", ModItems.RAW_ORE.newItemStack(1, 1));
		registerOre("blockRawIron", ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 1));
		registerOre("rawGold", ModItems.RAW_ORE.newItemStack(1, 2));
		registerOre("blockRawGold", ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 2));

		if (ConfigFunctions.registerRawItemAsOre) {
			registerOre("oreCopper", ModItems.RAW_ORE.newItemStack()); //Todo: the registration of copper raw ore should be conditional because it is a configurable meta value, I may make the raw ore class itself provide enabled metas.
			registerOre("oreIron", ModItems.RAW_ORE.newItemStack(1, 1));
			registerOre("oreGold", ModItems.RAW_ORE.newItemStack(1, 2));
		}
	}

	private static void registerRecipes() {
		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.OLD_GRAVEL.newItemStack(4), "xy", "yx", 'x', ModBlocks.COARSE_DIRT.get(), 'y', Blocks.gravel);
		}

		if (ConfigFunctions.enableStoneBrickRecipes && !ModsList.GTNH.isLoaded()) {
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.mossy_cobblestone), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.vine));
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.stonebrick, 1, 1), new ItemStack(Blocks.stonebrick), new ItemStack(Blocks.vine));
			GameRegistry.addShapedRecipe(new ItemStack(Blocks.stonebrick, 1, 3), "x", "x", 'x', new ItemStack(Blocks.stone_slab, 1, 5));
			GameRegistry.addSmelting(new ItemStack(Blocks.stonebrick), new ItemStack(Blocks.stonebrick, 1, 2), 0.0F);
		}

		if (!ModsList.GTNH.isLoaded()) {
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.mossy_cobblestone), new ItemStack(Blocks.cobblestone), ModBlocks.MOSS_BLOCK.newItemStack());
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.stonebrick, 1, 1), new ItemStack(Blocks.stonebrick), ModBlocks.MOSS_BLOCK.newItemStack());
		}

		if (ConfigFunctions.enableRecipeForTotem) {
			addShapedRecipe(ModItems.TOTEM_OF_UNDYING.newItemStack(), "EBE", "GBG", " G ", 'E', "gemEmerald", 'G', "ingotGold", 'B', "blockGold");
		}

		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.SLIME.newItemStack(), "xxx", "xxx", "xxx", 'x', new ItemStack(Items.slime_ball));
			addShapelessRecipe(new ItemStack(Items.slime_ball, 9), ModBlocks.SLIME.get());

			addShapedRecipe(ModBlocks.COARSE_DIRT.newItemStack(4), "xy", "yx", 'x', new ItemStack(Blocks.dirt), 'y', new ItemStack(Blocks.gravel));
		}

		addSmelting(ModItems.MUTTON_RAW.get(), ModItems.MUTTON_COOKED.newItemStack(), 0.35F);

		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(new ItemStack(Items.iron_ingot), "xxx", "xxx", "xxx", 'x', "nuggetIron");
			addShapelessRecipe(ModItems.NUGGET_IRON.newItemStack(9), "ingotIron");
		}

		// Granite
		addShapelessRecipe(ModBlocks.STONE.newItemStack(2, 1), "gemQuartz", "stoneDiorite");
		addShapedRecipe(ModBlocks.STONE.newItemStack(4, 2), "xx", "xx", 'x', "stoneGranite");
		// Diorite
		addShapedRecipe(ModBlocks.STONE.newItemStack(2, 3), "xy", "yx", 'x', new ItemStack(Blocks.cobblestone), 'y', "gemQuartz");
		addShapedRecipe(ModBlocks.STONE.newItemStack(4, 4), "xx", "xx", 'x', "stoneDiorite");
		// Andesite
		addShapelessRecipe(ModBlocks.STONE.newItemStack(2, 5), new ItemStack(Blocks.cobblestone), "stoneDiorite");
		addShapedRecipe(ModBlocks.STONE.newItemStack(4, 6), "xx", "xx", 'x', "stoneAndesite");
		Block[] stone_stairs = new Block[]{ModBlocks.GRANITE_STAIRS.get(), ModBlocks.POLISHED_GRANITE_STAIRS.get(), ModBlocks.DIORITE_STAIRS.get(), ModBlocks.POLISHED_DIORITE_STAIRS.get(), ModBlocks.ANDESITE_STAIRS.get(), ModBlocks.POLISHED_ANDESITE_STAIRS.get()};
		for (int i = 0; i < stone_stairs.length; i++) { //TODO: Rewrite this, this seems needlessly convoluted
			String dictName = "stone" + StringUtils.capitalize(((BaseSlab) ModBlocks.STONE_SLAB_2.get()).types[(i / 2) * 2]) + (i % 2 == 1 ? "Polished" : "");
			if (!ModsList.GTNH.isLoaded()) {
				addShapedRecipe(ModBlocks.STONE_SLAB_2.newItemStack(6, i), "xxx", 'x', dictName);
			}
			addShapedRecipe(new ItemStack(stone_stairs[i], 4), "x  ", "xx ", "xxx", 'x', dictName);
			if (i % 2 == 1) {
				addShapedRecipe(ModBlocks.STONE_WALL_2.newItemStack(6, i == 5 ? 2 : i == 3 ? 1 : 0), "xxx", "xxx", 'x', ModBlocks.STONE.newItemStack(1, i));
			}
		}

		addShapedRecipe(ModBlocks.PRISMARINE_BLOCK.newItemStack(), "xx", "xx", 'x', "shardPrismarine");
		addShapedRecipe(ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 1), "xxx", "xxx", "xxx", 'x', "shardPrismarine");
		addShapedRecipe(ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 2), "xxx", "xyx", "xxx", 'x', "shardPrismarine", 'y', "dyeBlack");
		addShapedRecipe(ModBlocks.SEA_LANTERN.newItemStack(), "xyx", "yyy", "xyx", 'x', "shardPrismarine", 'y', "crystalPrismarine");

		addShapedRecipe(ModBlocks.PRISMARINE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack());
		addShapedRecipe(ModBlocks.PRISMARINE_STAIRS_BRICK.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.PRISMARINE_STAIRS_DARK.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.PRISMARINE_WALL.newItemStack(6), "xxx", "xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack());

		for (int i = 0; i < 3; i++) {
			addShapedRecipe(ModBlocks.PRISMARINE_SLAB.newItemStack(6, i), "xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack(1, i));
		}

		if (!ModsList.BOTANIA.isLoaded()) {
			addShapedRecipe(ModItems.PRISMARINE_SHARD.newItemStack(4), "xy", "zx", 'x', "gemQuartz", 'y', "dyeBlue", 'z', "dyeGreen");
			addShapedRecipe(ModItems.PRISMARINE_CRYSTALS.newItemStack(4), "xy", "yx", 'x', "gemQuartz", 'y', "dustGlowstone");
		}

		if (!ModsList.GTNH.isLoaded()) {
			Block[] metaBlocks = new Block[]{Blocks.stone, Blocks.mossy_cobblestone, Blocks.stonebrick, Blocks.sandstone};
			for (int i = 0; i < metaBlocks.length; i++) {
				addShapedRecipe(ModBlocks.STONE_SLAB.newItemStack(6, i), "xxx", 'x', new ItemStack(metaBlocks[i], 1, i != 0 ? i - 1 : i));
			}
			addShapedRecipe(new ItemStack(Blocks.stone_slab, 6, 0), "xxx", 'x', ModBlocks.SMOOTH_STONE.newItemStack());
		}

		addShapedRecipe(ModBlocks.STONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', new ItemStack(Blocks.stone, 1, 0));
		addShapedRecipe(ModBlocks.MOSSY_COBBLESTONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', new ItemStack(Blocks.mossy_cobblestone, 1, 0));
		addShapedRecipe(ModBlocks.MOSSY_STONE_BRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', new ItemStack(Blocks.stonebrick, 1, 1));

		if (!ModsList.GTNH.isLoaded()) {
			//Bark to planks
			addShapedRecipe(new ItemStack(Blocks.planks, 4), "x", 'x', ModBlocks.BARK.newItemStack(1));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 1), "x", 'x', ModBlocks.BARK.newItemStack(1, 1));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 2), "x", 'x', ModBlocks.BARK.newItemStack(1, 2));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 3), "x", 'x', ModBlocks.BARK.newItemStack(1, 3));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 4), "x", 'x', ModBlocks.BARK2.newItemStack(1));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 5), "x", 'x', ModBlocks.BARK2.newItemStack(1, 1));

			//Stripped log to planks
			addShapedRecipe(new ItemStack(Blocks.planks, 4), "x", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 1), "x", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1, 1));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 2), "x", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1, 2));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 3), "x", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1, 3));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 4), "x", 'x', ModBlocks.LOG2_STRIPPED.newItemStack(1));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 5), "x", 'x', ModBlocks.LOG2_STRIPPED.newItemStack(1, 1));

			//Stripped bark to planks
			addShapedRecipe(new ItemStack(Blocks.planks, 4), "x", 'x', ModBlocks.WOOD_STRIPPED.newItemStack(1));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 1), "x", 'x', ModBlocks.WOOD_STRIPPED.newItemStack(1, 1));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 2), "x", 'x', ModBlocks.WOOD_STRIPPED.newItemStack(1, 2));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 3), "x", 'x', ModBlocks.WOOD_STRIPPED.newItemStack(1, 3));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 4), "x", 'x', ModBlocks.WOOD2_STRIPPED.newItemStack(1));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, 5), "x", 'x', ModBlocks.WOOD2_STRIPPED.newItemStack(1, 1));

			for (int i = 0; i < 4; i++) {
				addSmelting(ModBlocks.LOG_STRIPPED.newItemStack(1, i), new ItemStack(Items.coal, 1, 1), 0.15F);
				addSmelting(ModBlocks.LOG2_STRIPPED.newItemStack(1, i), new ItemStack(Items.coal, 1, 1), 0.15F);
				addSmelting(ModBlocks.WOOD_STRIPPED.newItemStack(1, i), new ItemStack(Items.coal, 1, 1), 0.15F);
				addSmelting(ModBlocks.WOOD2_STRIPPED.newItemStack(1, i), new ItemStack(Items.coal, 1, 1), 0.15F);
				addSmelting(ModBlocks.BARK.newItemStack(1, i), new ItemStack(Items.coal, 1, 1), 0.15F);
				addSmelting(ModBlocks.BARK2.newItemStack(1, i), new ItemStack(Items.coal, 1, 1), 0.15F);

				addSmelting(ModBlocks.MANGROVE_LOG.newItemStack(1, i), new ItemStack(Items.coal, 1, 1), 0.15F);
				addSmelting(ModBlocks.CHERRY_LOG.newItemStack(1, i), new ItemStack(Items.coal, 1, 1), 0.15F);
			}
		}

		//Logs to bark
		addShapedRecipe(ModBlocks.BARK.newItemStack(3), "xx", "xx", 'x', new ItemStack(Blocks.log, 1));
		addShapedRecipe(ModBlocks.BARK.newItemStack(3, 1), "xx", "xx", 'x', new ItemStack(Blocks.log, 1, 1));
		addShapedRecipe(ModBlocks.BARK.newItemStack(3, 2), "xx", "xx", 'x', new ItemStack(Blocks.log, 1, 2));
		addShapedRecipe(ModBlocks.BARK.newItemStack(3, 3), "xx", "xx", 'x', new ItemStack(Blocks.log, 1, 3));
		addShapedRecipe(ModBlocks.BARK2.newItemStack(3), "xx", "xx", 'x', new ItemStack(Blocks.log2, 1));
		addShapedRecipe(ModBlocks.BARK2.newItemStack(3, 1), "xx", "xx", 'x', new ItemStack(Blocks.log2, 1, 1));

		//Stripped logs to stripped bark
		addShapedRecipe(ModBlocks.WOOD_STRIPPED.newItemStack(3), "xx", "xx", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1));
		addShapedRecipe(ModBlocks.WOOD_STRIPPED.newItemStack(3, 1), "xx", "xx", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.WOOD_STRIPPED.newItemStack(3, 2), "xx", "xx", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.WOOD_STRIPPED.newItemStack(3, 3), "xx", "xx", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1, 3));
		addShapedRecipe(ModBlocks.WOOD2_STRIPPED.newItemStack(3), "xx", "xx", 'x', ModBlocks.LOG2_STRIPPED.newItemStack(1));
		addShapedRecipe(ModBlocks.WOOD2_STRIPPED.newItemStack(3, 1), "xx", "xx", 'x', ModBlocks.LOG2_STRIPPED.newItemStack(1, 1));

		if (!ModsList.GTNH.isLoaded()) {
			//New logs, bark, stripped log and stripped bark to planks
			addShapedRecipe(ModBlocks.WOOD_PLANKS.newItemStack(4), "x", 'x', ModBlocks.CRIMSON_STEM.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			addShapedRecipe(ModBlocks.WOOD_PLANKS.newItemStack(4, 1), "x", 'x', ModBlocks.WARPED_STEM.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			addShapedRecipe(ModBlocks.WOOD_PLANKS.newItemStack(4, 2), "x", 'x', ModBlocks.MANGROVE_LOG.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			addShapedRecipe(ModBlocks.WOOD_PLANKS.newItemStack(4, 3), "x", 'x', ModBlocks.CHERRY_LOG.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			addShapedRecipe(ModBlocks.WOOD_PLANKS.newItemStack(2, 4), "x", 'x', ModBlocks.BAMBOO_BLOCK.newItemStack(1, OreDictionary.WILDCARD_VALUE));
		}

		if (ConfigBlocksItems.enableStrippedLogs && ConfigBlocksItems.enableBarkLogs) {
			//New stripped logs to new stripped bark
			addShapedRecipe(ModBlocks.CRIMSON_STEM.newItemStack(3, 3), "xx", "xx", 'x', ModBlocks.CRIMSON_STEM.newItemStack(2, 2));
			addShapedRecipe(ModBlocks.WARPED_STEM.newItemStack(3, 3), "xx", "xx", 'x', ModBlocks.WARPED_STEM.newItemStack(2, 2));
			addShapedRecipe(ModBlocks.MANGROVE_LOG.newItemStack(3, 3), "xx", "xx", 'x', ModBlocks.MANGROVE_LOG.newItemStack(2, 2));
			addShapedRecipe(ModBlocks.CHERRY_LOG.newItemStack(3, 3), "xx", "xx", 'x', ModBlocks.CHERRY_LOG.newItemStack(2, 2));
		}

		if (ConfigBlocksItems.enableBarkLogs) {
			//New logs to new bark
			addShapedRecipe(ModBlocks.CRIMSON_STEM.newItemStack(3, 1), "xx", "xx", 'x', ModBlocks.CRIMSON_STEM.newItemStack(1));
			addShapedRecipe(ModBlocks.WARPED_STEM.newItemStack(3, 1), "xx", "xx", 'x', ModBlocks.WARPED_STEM.newItemStack(1));
			addShapedRecipe(ModBlocks.MANGROVE_LOG.newItemStack(3, 1), "xx", "xx", 'x', ModBlocks.MANGROVE_LOG.newItemStack(1));
			addShapedRecipe(ModBlocks.CHERRY_LOG.newItemStack(3, 1), "xx", "xx", 'x', ModBlocks.CHERRY_LOG.newItemStack(1));
		}

		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.FENCE_SPRUCE.newItemStack(3), "xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 1), 'y', "stickWood");
			addShapedRecipe(ModBlocks.FENCE_BIRCH.newItemStack(3), "xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 2), 'y', "stickWood");
			addShapedRecipe(ModBlocks.FENCE_JUNGLE.newItemStack(3), "xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 3), 'y', "stickWood");
			addShapedRecipe(ModBlocks.FENCE_ACACIA.newItemStack(3), "xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 4), 'y', "stickWood");
			addShapedRecipe(ModBlocks.FENCE_DARK_OAK.newItemStack(3), "xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 5), 'y', "stickWood");

			if (ConfigExperiments.enableCrimsonBlocks) {
				addShapedRecipe(ModBlocks.WOOD_FENCE.newItemStack(3), "xyx", "xyx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1), 'y', "stickWood");
				addShapedRecipe(ModBlocks.WOOD_SLAB.newItemStack(6, 0), "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 0));
			}
			if (ConfigExperiments.enableWarpedBlocks) {
				addShapedRecipe(ModBlocks.WOOD_FENCE.newItemStack(3, 1), "xyx", "xyx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1), 'y', "stickWood");
				addShapedRecipe(ModBlocks.WOOD_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1));
			}
			if (ConfigExperiments.enableMangroveBlocks) {
				addShapedRecipe(ModBlocks.WOOD_FENCE.newItemStack(3, 2), "xyx", "xyx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2), 'y', "stickWood");
				addShapedRecipe(ModBlocks.WOOD_SLAB.newItemStack(6, 2), "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2));
			}
			if (ConfigBlocksItems.enableCherryBlocks) {
				addShapedRecipe(ModBlocks.WOOD_FENCE.newItemStack(3, 3), "xyx", "xyx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3), 'y', "stickWood");
				addShapedRecipe(ModBlocks.WOOD_SLAB.newItemStack(6, 3), "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3));
			}
			if (ConfigBlocksItems.enableBambooBlocks) {
				addShapedRecipe(ModBlocks.WOOD_FENCE.newItemStack(3, 4), "xyx", "xyx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4), 'y', "stickWood");
				addShapedRecipe(ModBlocks.WOOD_SLAB.newItemStack(6, 4), "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4));
			}
		}

		addShapedRecipe(ModBlocks.CRIMSON_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.WARPED_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.MANGROVE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.CHERRY_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3));
		addShapedRecipe(ModBlocks.BAMBOO_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4));

		addShapedRecipe(ModBlocks.BAMBOO_BLOCK.newItemStack(), "bbb", "bbb", "bbb", 'b', ModItems.BAMBOO.newItemStack());
		addShapedRecipe(new ItemStack(Items.stick), "b", "b", 'b', ModItems.BAMBOO.newItemStack());

		// Bamboo Mosaic
		addShapedRecipe(ModBlocks.BAMBOO_MOSAIC.newItemStack(1, 0), "x", "x", 'x', ModBlocks.WOOD_SLAB.newItemStack(1, 4));
		addShapedRecipe(ModBlocks.BAMBOO_MOSAIC_SLAB.newItemStack(6, 5), "xxx", 'x', ModBlocks.BAMBOO_MOSAIC.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.BAMBOO_MOSAIC_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.BAMBOO_MOSAIC.newItemStack(1, 0));

		addShapedRecipe(ModBlocks.FENCE_GATE_SPRUCE.newItemStack(), "yxy", "yxy", 'x', new ItemStack(Blocks.planks, 1, 1), 'y', "stickWood");
		addShapedRecipe(ModBlocks.FENCE_GATE_BIRCH.newItemStack(), "yxy", "yxy", 'x', new ItemStack(Blocks.planks, 1, 2), 'y', "stickWood");
		addShapedRecipe(ModBlocks.FENCE_GATE_JUNGLE.newItemStack(), "yxy", "yxy", 'x', new ItemStack(Blocks.planks, 1, 3), 'y', "stickWood");
		addShapedRecipe(ModBlocks.FENCE_GATE_ACACIA.newItemStack(), "yxy", "yxy", 'x', new ItemStack(Blocks.planks, 1, 4), 'y', "stickWood");
		addShapedRecipe(ModBlocks.FENCE_GATE_DARK_OAK.newItemStack(), "yxy", "yxy", 'x', new ItemStack(Blocks.planks, 1, 5), 'y', "stickWood");

		addShapedRecipe(ModBlocks.CRIMSON_FENCE_GATE.newItemStack(), "yxy", "yxy", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1), 'y', "stickWood");
		addShapedRecipe(ModBlocks.WARPED_FENCE_GATE.newItemStack(), "yxy", "yxy", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1), 'y', "stickWood");
		addShapedRecipe(ModBlocks.MANGROVE_FENCE_GATE.newItemStack(), "yxy", "yxy", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2), 'y', "stickWood");
		addShapedRecipe(ModBlocks.CHERRY_FENCE_GATE.newItemStack(), "yxy", "yxy", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3), 'y', "stickWood");
		addShapedRecipe(ModBlocks.BAMBOO_FENCE_GATE.newItemStack(), "yxy", "yxy", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4), 'y', "stickWood");

		int output = ConfigFunctions.enableDoorRecipeBuffs ? 3 : 1;
		addShapedRecipe(ModBlocks.DOOR_SPRUCE.newItemStack(output), "xx", "xx", "xx", 'x', new ItemStack(Blocks.planks, 1, 1));
		addShapedRecipe(ModBlocks.DOOR_BIRCH.newItemStack(output), "xx", "xx", "xx", 'x', new ItemStack(Blocks.planks, 1, 2));
		addShapedRecipe(ModBlocks.DOOR_JUNGLE.newItemStack(output), "xx", "xx", "xx", 'x', new ItemStack(Blocks.planks, 1, 3));
		addShapedRecipe(ModBlocks.DOOR_ACACIA.newItemStack(output), "xx", "xx", "xx", 'x', new ItemStack(Blocks.planks, 1, 4));
		addShapedRecipe(ModBlocks.DOOR_DARK_OAK.newItemStack(output), "xx", "xx", "xx", 'x', new ItemStack(Blocks.planks, 1, 5));

		addShapedRecipe(ModBlocks.CRIMSON_DOOR.newItemStack(output), "xx", "xx", "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1));
		addShapedRecipe(ModBlocks.WARPED_DOOR.newItemStack(output), "xx", "xx", "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.MANGROVE_DOOR.newItemStack(output), "xx", "xx", "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.CHERRY_DOOR.newItemStack(output), "xx", "xx", "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3));
		addShapedRecipe(ModBlocks.BAMBOO_DOOR.newItemStack(output), "xx", "xx", "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4));

		addShapedRecipe(ModBlocks.TRAPDOOR_SPRUCE.newItemStack(2), "xxx", "xxx", 'x', new ItemStack(Blocks.planks, 1, 1));
		addShapedRecipe(ModBlocks.TRAPDOOR_BIRCH.newItemStack(2), "xxx", "xxx", 'x', new ItemStack(Blocks.planks, 1, 2));
		addShapedRecipe(ModBlocks.TRAPDOOR_JUNGLE.newItemStack(2), "xxx", "xxx", 'x', new ItemStack(Blocks.planks, 1, 3));
		addShapedRecipe(ModBlocks.TRAPDOOR_ACACIA.newItemStack(2), "xxx", "xxx", 'x', new ItemStack(Blocks.planks, 1, 4));
		addShapedRecipe(ModBlocks.TRAPDOOR_DARK_OAK.newItemStack(2), "xxx", "xxx", 'x', new ItemStack(Blocks.planks, 1, 5));

		addShapedRecipe(ModBlocks.CRIMSON_TRAPDOOR.newItemStack(2), "xxx", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1));
		addShapedRecipe(ModBlocks.WARPED_TRAPDOOR.newItemStack(2), "xxx", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.MANGROVE_TRAPDOOR.newItemStack(2), "xxx", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.CHERRY_TRAPDOOR.newItemStack(2), "xxx", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3));
		addShapedRecipe(ModBlocks.BAMBOO_TRAPDOOR.newItemStack(2), "xxx", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4));

		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.IRON_TRAPDOOR.newItemStack(), "xx", "xx", 'x', "ingotIron");

			addShapedRecipe(ModBlocks.BUTTON_SPRUCE.newItemStack(), "x", 'x', new ItemStack(Blocks.planks, 1, 1));
			addShapedRecipe(ModBlocks.BUTTON_BIRCH.newItemStack(), "x", 'x', new ItemStack(Blocks.planks, 1, 2));
			addShapedRecipe(ModBlocks.BUTTON_JUNGLE.newItemStack(), "x", 'x', new ItemStack(Blocks.planks, 1, 3));
			addShapedRecipe(ModBlocks.BUTTON_ACACIA.newItemStack(), "x", 'x', new ItemStack(Blocks.planks, 1, 4));
			addShapedRecipe(ModBlocks.BUTTON_DARK_OAK.newItemStack(), "x", 'x', new ItemStack(Blocks.planks, 1, 5));

			addShapedRecipe(ModBlocks.CRIMSON_BUTTON.newItemStack(), "x", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1));
			addShapedRecipe(ModBlocks.WARPED_BUTTON.newItemStack(), "x", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1));
			addShapedRecipe(ModBlocks.MANGROVE_BUTTON.newItemStack(), "x", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2));
			addShapedRecipe(ModBlocks.CHERRY_BUTTON.newItemStack(), "x", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3));
			addShapedRecipe(ModBlocks.BAMBOO_BUTTON.newItemStack(), "x", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4));

			addShapedRecipe(ModBlocks.PRESSURE_PLATE_SPRUCE.newItemStack(), "xx", 'x', new ItemStack(Blocks.planks, 1, 1));
			addShapedRecipe(ModBlocks.PRESSURE_PLATE_BIRCH.newItemStack(), "xx", 'x', new ItemStack(Blocks.planks, 1, 2));
			addShapedRecipe(ModBlocks.PRESSURE_PLATE_JUNGLE.newItemStack(), "xx", 'x', new ItemStack(Blocks.planks, 1, 3));
			addShapedRecipe(ModBlocks.PRESSURE_PLATE_ACACIA.newItemStack(), "xx", 'x', new ItemStack(Blocks.planks, 1, 4));
			addShapedRecipe(ModBlocks.PRESSURE_PLATE_DARK_OAK.newItemStack(), "xx", 'x', new ItemStack(Blocks.planks, 1, 5));

			addShapedRecipe(ModBlocks.CRIMSON_PRESSURE_PLATE.newItemStack(), "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1));
			addShapedRecipe(ModBlocks.WARPED_PRESSURE_PLATE.newItemStack(), "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1));
			addShapedRecipe(ModBlocks.MANGROVE_PRESSURE_PLATE.newItemStack(), "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2));
			addShapedRecipe(ModBlocks.CHERRY_PRESSURE_PLATE.newItemStack(), "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3));
			addShapedRecipe(ModBlocks.BAMBOO_PRESSURE_PLATE.newItemStack(), "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4));

			addShapedRecipe(ModItems.ITEM_SIGN_SPRUCE.newItemStack(3), "xxx", "xxx", " y ", 'x', new ItemStack(Blocks.planks, 1, 1), 'y', "stickWood");
			addShapedRecipe(ModItems.ITEM_SIGN_BIRCH.newItemStack(3), "xxx", "xxx", " y ", 'x', new ItemStack(Blocks.planks, 1, 2), 'y', "stickWood");
			addShapedRecipe(ModItems.ITEM_SIGN_JUNGLE.newItemStack(3), "xxx", "xxx", " y ", 'x', new ItemStack(Blocks.planks, 1, 3), 'y', "stickWood");
			addShapedRecipe(ModItems.ITEM_SIGN_ACACIA.newItemStack(3), "xxx", "xxx", " y ", 'x', new ItemStack(Blocks.planks, 1, 4), 'y', "stickWood");
			addShapedRecipe(ModItems.ITEM_SIGN_DARK_OAK.newItemStack(3), "xxx", "xxx", " y ", 'x', new ItemStack(Blocks.planks, 1, 5), 'y', "stickWood");

			addShapedRecipe(ModBlocks.CRIMSON_SIGN.newItemStack(3), "xxx", "xxx", " y ", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1), 'y', "stickWood");
			addShapedRecipe(ModBlocks.WARPED_SIGN.newItemStack(3), "xxx", "xxx", " y ", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1), 'y', "stickWood");
			addShapedRecipe(ModBlocks.MANGROVE_SIGN.newItemStack(3), "xxx", "xxx", " y ", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2), 'y', "stickWood");
			addShapedRecipe(ModBlocks.CHERRY_SIGN.newItemStack(3), "xxx", "xxx", " y ", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3), 'y', "stickWood");
			addShapedRecipe(ModBlocks.BAMBOO_SIGN.newItemStack(3), "xxx", "xxx", " y ", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4), 'y', "stickWood");
		}

		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.RED_SANDSTONE.newItemStack(), "xx", "xx", 'x', new ItemStack(Blocks.sand, 1, 1));
			addShapedRecipe(ModBlocks.RED_SANDSTONE_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.RED_SANDSTONE.newItemStack(1, 2));
			addShapedRecipe(ModBlocks.RED_SANDSTONE_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.RED_SANDSTONE.newItemStack(1, OreDictionary.WILDCARD_VALUE));
		}
		addShapedRecipe(ModBlocks.RED_SANDSTONE.newItemStack(1, 1), "x", "x", 'x', ModBlocks.RED_SANDSTONE_SLAB.newItemStack());
		addShapedRecipe(ModBlocks.RED_SANDSTONE.newItemStack(4, 2), "xx", "xx", 'x', ModBlocks.RED_SANDSTONE.newItemStack());
		addShapedRecipe(ModBlocks.RED_SANDSTONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.RED_SANDSTONE.get());
		addShapedRecipe(ModBlocks.RED_SANDSTONE_WALL.newItemStack(6), "xxx", "xxx", 'x', ModBlocks.RED_SANDSTONE.newItemStack());

		if (ConfigBlocksItems.enableVanillaFences && !ModsList.GTNH.isLoaded()) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fence, 3), "xyx", "xyx", 'x', "plankWood", 'y', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.nether_brick_fence, 6), "xyx", "xyx", 'x', Blocks.nether_brick, 'y', "ingotBrickNether"));
		}

		for (EnumColor colour : EnumColor.VALUES) {
			addShapedRecipe(ModBlocks.BANNER.newItemStack(1, colour.getDamage()), "xxx", "xxx", " y ", 'x', new ItemStack(Blocks.wool, 1, colour.getDamage()), 'y', "stickWood");
		}
		GameRegistry.addRecipe(new RecipeDuplicatePattern());
		GameRegistry.addRecipe(new RecipeAddPattern());

		addShapedRecipe(ModItems.WOODEN_ARMORSTAND.newItemStack(), "xxx", " x ", "xyx", 'x', "stickWood", 'y', new ItemStack(Blocks.stone_slab));

		addShapedRecipe(ModItems.RABBIT_STEW.newItemStack(), " R ", "CPM", " B ", 'R', ModItems.RABBIT_COOKED.newItemStack(), 'C', Items.carrot, 'P', Items.baked_potato, 'M', Blocks.brown_mushroom, 'B', "bowlWood");
		addShapedRecipe(ModItems.RABBIT_STEW.newItemStack(), " R ", "CPD", " B ", 'R', ModItems.RABBIT_COOKED.newItemStack(), 'C', Items.carrot, 'P', Items.baked_potato, 'D', Blocks.red_mushroom, 'B', "bowlWood");
		addSmelting(ModItems.RABBIT_RAW.get(), ModItems.RABBIT_COOKED.newItemStack(), 0.35F);
		addShapedRecipe(new ItemStack(Items.leather), "xx", "xx", 'x', ModItems.RABBIT_HIDE.get());

		addSmelting(ModBlocks.SPONGE.newItemStack(1, 1), ConfigWorld.tileReplacementMode == -1 ? ModBlocks.SPONGE.newItemStack() : new ItemStack(Blocks.sponge), 0.15F);

		addShapedRecipe(ModItems.BEETROOT_SOUP.newItemStack(), "xxx", "xxx", " y ", 'x', "cropBeetroot", 'y', "bowlWood");
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.dye, 1, 1), "cropBeetroot"));

		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.END_BRICK_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.END_BRICKS.get());
		}
		addShapedRecipe(ModBlocks.END_BRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.END_BRICKS.get());
		addShapedRecipe(ModBlocks.END_BRICK_WALL.newItemStack(6), "xxx", "xxx", 'x', ModBlocks.END_BRICKS.newItemStack());

		addShapedRecipe(ModBlocks.PURPUR_BLOCK.newItemStack(4), "xx", "xx", 'x', ModItems.CHORUS_FRUIT_POPPED.get());
		addShapedRecipe(ModBlocks.PURPUR_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.PURPUR_BLOCK.get());
		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.PURPUR_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.PURPUR_BLOCK.get());
		}
		addShapedRecipe(ModBlocks.PURPUR_PILLAR.newItemStack(), "x", "x", 'x', ModBlocks.PURPUR_SLAB.get());
		addShapedRecipe(ModBlocks.END_BRICKS.newItemStack(4), "xx", "xx", 'x', Blocks.end_stone);

		addSmelting(ModItems.CHORUS_FRUIT.newItemStack(), ModItems.CHORUS_FRUIT_POPPED.newItemStack(), 0.0F);
		addShapedRecipe(ModBlocks.END_ROD.newItemStack(), "x", "y", 'x', Items.blaze_rod, 'y', ModItems.CHORUS_FRUIT_POPPED.get());

		addShapelessRecipe(ModItems.DRAGON_BREATH.newItemStack(), new ItemStack(Items.potionitem, 1, 8195), ModItems.CHORUS_FRUIT.get(), ModItems.CHORUS_FRUIT.get());

		addShapedRecipe(ModItems.END_CRYSTAL.newItemStack(), "xxx", "xyx", "xzx", 'x', "blockGlassColorless", 'y', Items.ender_eye, 'z', Items.ghast_tear);

		addShapelessRecipe(new ItemStack(Items.dye, 1, 1), ModBlocks.ROSE.get());
		addShapedRecipe(new ItemStack(Blocks.double_plant, 1, 4), "xx", "xx", "xx", 'x', ModBlocks.ROSE.newItemStack());
		addShapedRecipe(ModBlocks.ROSE.newItemStack(12), "xx", 'x', new ItemStack(Blocks.double_plant, 1, 4));

		if (ModItems.TIPPED_ARROW.isEnabled() && ModItems.LINGERING_POTION.isEnabled()) {
			RecipeSorter.register(Reference.MOD_ID + ".RecipeTippedArrow", RecipeTippedArrow.class, Category.SHAPED, "after:minecraft:shaped");
			GameRegistry.addRecipe(new RecipeTippedArrow(ModItems.TIPPED_ARROW.newItemStack(), "xxx", "xyx", "xxx", 'x', Items.arrow, 'y', ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)));
		}

		if (!ModsList.GTNH.isLoaded()) {
			for (int i = 0; i < ModBlocks.BEDS.length; i++) {
				int j = i == 14 ? 15 : i;
				addShapedRecipe(ModBlocks.BEDS[i].newItemStack(1), "###", "XXX", '#', new ItemStack(Blocks.wool, 1, j), 'X', "plankWood");
				if (i > 0) {
					addShapelessRecipe(ModBlocks.BEDS[i].newItemStack(1), ModBlocks.BEDS[0].get(), ore_dyes[~j & 15]);
				}
			}
			addShapelessRecipe(new ItemStack(Items.bed, 1), ModBlocks.BEDS[0].newItemStack(), ore_dyes[1]);

			addShapedRecipe(ModBlocks.MAGMA.newItemStack(), "xx", "xx", 'x', new ItemStack(Items.magma_cream));
		}

		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.RED_NETHERBRICK.newItemStack(), "xi", "ix", 'x', Items.nether_wart, 'i', "ingotBrickNether");
			addShapedRecipe(ModBlocks.RED_NETHERBRICK.newItemStack(1, 2), "x", "x", 'x', new ItemStack(Blocks.stone_slab, 1, 6));
			addSmelting(new ItemStack(Blocks.nether_brick, 1, 1), ModBlocks.RED_NETHERBRICK.newItemStack(), .1F);
			addShapedRecipe(ModBlocks.RED_NETHERBRICK_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.RED_NETHERBRICK.newItemStack());
		}
		addShapedRecipe(ModBlocks.RED_NETHERBRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.RED_NETHERBRICK.newItemStack());
		addShapedRecipe(ModBlocks.RED_NETHER_BRICK_WALL.newItemStack(6), "xxx", "xxx", 'x', ModBlocks.RED_NETHERBRICK.newItemStack());

		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.NETHER_WART.newItemStack(), "xxx", "xxx", "xxx", 'x', Items.nether_wart);
			addShapedRecipe(ModBlocks.BONE.newItemStack(), "xxx", "xxx", "xxx", 'x', new ItemStack(Items.dye, 1, 15));
		}
		addShapelessRecipe(new ItemStack(Items.dye, 9, 15), ModBlocks.BONE.newItemStack());

		if (!ModsList.GTNH.isLoaded()) {
			for (int i = 0; i < ore_dyes.length; i++) {
				int dye = ~i & 15;
				addShapelessRecipe(ModBlocks.CONCRETE_POWDER.newItemStack(8, i),
						ore_dyes[dye], new ItemStack(Blocks.sand, 1, 0), new ItemStack(Blocks.sand, 1, 0), new ItemStack(Blocks.sand, 1, 0),
						new ItemStack(Blocks.sand, 1, 0), Blocks.gravel, Blocks.gravel, Blocks.gravel, Blocks.gravel);
				addSmelting(new ItemStack(Blocks.stained_hardened_clay, 1, i), ModBlocks.TERRACOTTA[i].newItemStack(), 0.1F);
			}
		}

		if (!ModsList.GTNH.isLoaded()) {
			if (!OreDictionary.getOres("nuggetIron").isEmpty()) {
				addShapedRecipe(ModBlocks.LANTERN.newItemStack(), "xxx", "xix", "xxx", 'x', "nuggetIron", 'i', Blocks.torch);
				addShapedRecipe(ModBlocks.SOUL_LANTERN.newItemStack(), "xxx", "xix", "xxx", 'x', "nuggetIron", 'i', ModBlocks.SOUL_TORCH.get());
			} else {
				addShapedRecipe(ModBlocks.LANTERN.newItemStack(), "i", "x", 'x', "ingotIron", 'i', Blocks.torch);
				addShapedRecipe(ModBlocks.SOUL_LANTERN.newItemStack(), "i", "x", 'x', "ingotIron", 'i', ModBlocks.SOUL_TORCH.get());
			}
		}
		addShapedRecipe(ModBlocks.SOUL_TORCH.newItemStack(4), "i", "x", "s", 'x', "stickWood", 'i', "coal", 's', "soulSand");
		addShapedRecipe(ModBlocks.SOUL_TORCH.newItemStack(4), "i", "x", "s", 'x', "stickWood", 'i', "coal", 's', "soulSoil");
		addShapedRecipe(ModBlocks.SOUL_TORCH.newItemStack(4), "i", "x", "s", 'x', "stickWood", 'i', "charcoal", 's', "soulSand");
		addShapedRecipe(ModBlocks.SOUL_TORCH.newItemStack(4), "i", "x", "s", 'x', "stickWood", 'i', "charcoal", 's', "soulSoil");

		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.BARREL.newItemStack(), "xsx", "x x", "xsx", 'x', "plankWood", 's', "slabWood");

			addShapedRecipe(ModBlocks.BLUE_ICE.newItemStack(), "xxx", "xxx", "xxx", 'x', Blocks.packed_ice);
		}

		addShapedRecipe(ModBlocks.SMOKER.newItemStack(), " l ", "lxl", " l ", 'x', Blocks.furnace, 'l', "logWood");

		addShapedRecipe(ModBlocks.BLAST_FURNACE.newItemStack(), "iii", "ixi", "sss", 'x', Blocks.furnace, 'i', "ingotIron", 's', ConfigBlocksItems.enableSmoothStone ? ModBlocks.SMOOTH_STONE.get() : Blocks.stone);

		addSmelting(ModBlocks.ANCIENT_DEBRIS.get(), ModItems.NETHERITE_SCRAP.newItemStack(), 2F);
		addShapelessRecipe(ModItems.NETHERITE_INGOT.newItemStack(), ModItems.NETHERITE_SCRAP.newItemStack(), ModItems.NETHERITE_SCRAP.newItemStack(),
				ModItems.NETHERITE_SCRAP.newItemStack(), ModItems.NETHERITE_SCRAP.newItemStack(), "ingotGold", "ingotGold", "ingotGold", "ingotGold");
		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.NETHERITE_BLOCK.newItemStack(), "xxx", "xxx", "xxx", 'x', ModItems.NETHERITE_INGOT.get());
		}
		addShapedRecipe(ModItems.NETHERITE_INGOT.newItemStack(9), "x", 'x', ModBlocks.NETHERITE_BLOCK.get());

		addSmelting(ModBlocks.NETHER_GOLD_ORE.newItemStack(), new ItemStack(Items.gold_ingot), .1F);

		if (!ModsList.GTNH.isLoaded()) {
			addSmelting(new ItemStack(Blocks.stone), ModBlocks.SMOOTH_STONE.newItemStack(), .1F);

			if (ModBlocks.SMOOTH_STONE.isEnabled()) {
				ItemStack stoneTile = ExternalContent.Items.BLUEPOWER_CIRCUIT_PLATE.isEnabled() ? ExternalContent.Items.BLUEPOWER_CIRCUIT_PLATE.newItemStack(2)
						: ExternalContent.Items.PROJECTRED_CIRCUIT_PLATE.isEnabled() ? ExternalContent.Items.PROJECTRED_CIRCUIT_PLATE.newItemStack(2)
						: null;
				if (stoneTile != null) {
					removeFurnaceRecipeFor(new ItemStack(Blocks.stone), stoneTile);
					addShapedRecipe(stoneTile, "x", 'x', ModBlocks.SMOOTH_STONE.newItemStack());
				}
			}

			addSmelting(new ItemStack(Blocks.sandstone, 1, 0), ModBlocks.SMOOTH_SANDSTONE.newItemStack(), .1F);
			addShapedRecipe(ModBlocks.SMOOTH_SANDSTONE_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.SMOOTH_SANDSTONE.newItemStack());
			addSmelting(ModBlocks.RED_SANDSTONE.newItemStack(), ModBlocks.SMOOTH_RED_SANDSTONE.newItemStack(), .1F);
			addShapedRecipe(ModBlocks.SMOOTH_RED_SANDSTONE_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.SMOOTH_RED_SANDSTONE.newItemStack());
		}
		addShapedRecipe(ModBlocks.SMOOTH_SANDSTONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.SMOOTH_SANDSTONE.newItemStack());
		addShapedRecipe(ModBlocks.SMOOTH_RED_SANDSTONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.SMOOTH_RED_SANDSTONE.newItemStack());

		if (!ModsList.GTNH.isLoaded()) {
			addSmelting(new ItemStack(Blocks.quartz_block, 1, 0), ModBlocks.SMOOTH_QUARTZ.newItemStack(), .1F);
			addShapedRecipe(ModBlocks.SMOOTH_QUARTZ_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.SMOOTH_QUARTZ.newItemStack());
		}
		addShapedRecipe(ModBlocks.SMOOTH_QUARTZ_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.SMOOTH_QUARTZ.newItemStack());

		addShapedRecipe(ModBlocks.QUARTZ_BRICKS.newItemStack(4), "xx", "xx", 'x', new ItemStack(Blocks.quartz_block, 1, 0));

		addShapelessRecipe(ModItems.DYE.newItemStack(), ModBlocks.LILY_OF_THE_VALLEY.newItemStack());
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 1), ModBlocks.CORNFLOWER.newItemStack());
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 3), ModBlocks.WITHER_ROSE.newItemStack());
		addShapelessRecipe(ModItems.DYE.newItemStack(), new ItemStack(Items.dye, 1, 15));
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 1), new ItemStack(Items.dye, 1, 4));
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 2), new ItemStack(Items.dye, 1, 3));
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 3), new ItemStack(Items.dye, 1, 0));

		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1), "xxx", "xxx", "xxx", 'x', ModItems.COPPER_INGOT.newItemStack());
			addShapedRecipe(ModItems.COPPER_INGOT.newItemStack(9), "x", 'x', ModBlocks.COPPER_BLOCK.newItemStack());
			addShapedRecipe(ModItems.COPPER_INGOT.newItemStack(9), "x", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 8));
		}

		addSmelting(ModBlocks.COPPER_ORE.newItemStack(), ModItems.COPPER_INGOT.newItemStack(), .7F);

		//Copper block to cut copper block
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 4), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack());
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 5), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 6), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 7), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 3));

		//Waxed copper block to waxed cut copper block
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 12), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 8));
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 13), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 9));
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 14), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 10));
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 15), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 11));

		//Copper block to copper grate
		addShapedRecipe(ModBlocks.COPPER_GRATE.newItemStack(4), " x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack());
		addShapedRecipe(ModBlocks.COPPER_GRATE.newItemStack(4, 1), " x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.COPPER_GRATE.newItemStack(4, 2), " x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.COPPER_GRATE.newItemStack(4, 3), " x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 3));

		//Waxed copper block to waxed copper grate
		addShapedRecipe(ModBlocks.COPPER_GRATE.newItemStack(4, 4), " x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 8));
		addShapedRecipe(ModBlocks.COPPER_GRATE.newItemStack(4, 5), " x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 9));
		addShapedRecipe(ModBlocks.COPPER_GRATE.newItemStack(4, 6), " x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 10));
		addShapedRecipe(ModBlocks.COPPER_GRATE.newItemStack(4, 7), " x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 11));

		//Copper block to copper grate
		addShapedRecipe(ModBlocks.COPPER_BULB.newItemStack(4), " x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(), 'b', Items.blaze_rod, 'r', "dustRedstone");
		addShapedRecipe(ModBlocks.COPPER_BULB.newItemStack(4, 1), " x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 1), 'b', Items.blaze_rod, 'r', "dustRedstone");
		addShapedRecipe(ModBlocks.COPPER_BULB.newItemStack(4, 2), " x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 2), 'b', Items.blaze_rod, 'r', "dustRedstone");
		addShapedRecipe(ModBlocks.COPPER_BULB.newItemStack(4, 3), " x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 3), 'b', Items.blaze_rod, 'r', "dustRedstone");

		//Waxed copper block to waxed copper grate
		addShapedRecipe(ModBlocks.COPPER_BULB.newItemStack(4, 8), " x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 8), 'b', Items.blaze_rod, 'r', "dustRedstone");
		addShapedRecipe(ModBlocks.COPPER_BULB.newItemStack(4, 9), " x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 9), 'b', Items.blaze_rod, 'r', "dustRedstone");
		addShapedRecipe(ModBlocks.COPPER_BULB.newItemStack(4, 10), " x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 10), 'b', Items.blaze_rod, 'r', "dustRedstone");
		addShapedRecipe(ModBlocks.COPPER_BULB.newItemStack(4, 11), " x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 11), 'b', Items.blaze_rod, 'r', "dustRedstone");

		//Copper block to copper grate
		addShapedRecipe(ModBlocks.CHISELED_COPPER.newItemStack(), "x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack());
		addShapedRecipe(ModBlocks.CHISELED_COPPER.newItemStack(1, 1), "x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.CHISELED_COPPER.newItemStack(1, 2), "x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.CHISELED_COPPER.newItemStack(1, 3), "x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 3));

		//Waxed copper block to waxed copper grate
		addShapedRecipe(ModBlocks.CHISELED_COPPER.newItemStack(1, 4), "x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 4));
		addShapedRecipe(ModBlocks.CHISELED_COPPER.newItemStack(1, 5), "x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 5));
		addShapedRecipe(ModBlocks.CHISELED_COPPER.newItemStack(1, 6), "x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 6));
		addShapedRecipe(ModBlocks.CHISELED_COPPER.newItemStack(1, 7), "x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 7));

		//Cut copper to cut copper slab
		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 4));
			addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 5));
			addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 2), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 6));
			addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 3), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 7));

			//Waxed cut copper to waxed cut copper slab
			addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 4), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 12));
			addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 5), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 13));
			addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 6), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 14));
			addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 7), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 15));
		}

		//Cut copper to cut copper stairs
		addShapedRecipe(ModBlocks.CUT_COPPER_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 4));
		addShapedRecipe(ModBlocks.EXPOSED_CUT_COPPER_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 5));
		addShapedRecipe(ModBlocks.WEATHERED_CUT_COPPER_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 6));
		addShapedRecipe(ModBlocks.OXIDIZED_CUT_COPPER_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 7));

		//Waxed cut copper to waxed cut copper stairs
		addShapedRecipe(ModBlocks.WAXED_CUT_COPPER_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 12));
		addShapedRecipe(ModBlocks.WAXED_EXPOSED_CUT_COPPER_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 13));
		addShapedRecipe(ModBlocks.WAXED_WEATHERED_CUT_COPPER_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 14));
		addShapedRecipe(ModBlocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 15));

		//Copper door/trapdoor
		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.COPPER_DOOR.newItemStack(3), "xx", "xx", "xx", 'x', ModItems.COPPER_INGOT.newItemStack());
			addShapedRecipe(ModBlocks.COPPER_TRAPDOOR.newItemStack(2), "xxX", "xxX", 'x', ModItems.COPPER_INGOT.newItemStack());
		}


		boolean slimeball = true;
		for (int k = 0; k <= IDegradable.waxStrings.length; k++) {
			String waxString;
			if (k == IDegradable.waxStrings.length) {
				if (!slimeball) {
					break;
				}
				waxString = "slimeball";
			} else {
				waxString = IDegradable.waxStrings[k];
			}

			if (OreDictionary.doesOreNameExist(waxString)) {
				slimeball = false;
				//Copper block to waxed copper block
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 8), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1));
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 9), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 1));
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 10), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 2));
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 11), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 3));

				//Cut copper block to waxed cut copper blocks
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 12), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 4));
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 13), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 5));
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 14), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 6));
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 15), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 7));

				//Copper grate to waxed copper grate
				addShapelessRecipe(ModBlocks.COPPER_GRATE.newItemStack(1, 4), waxString, ModBlocks.COPPER_GRATE.newItemStack(1));
				addShapelessRecipe(ModBlocks.COPPER_GRATE.newItemStack(1, 5), waxString, ModBlocks.COPPER_GRATE.newItemStack(1, 1));
				addShapelessRecipe(ModBlocks.COPPER_GRATE.newItemStack(1, 6), waxString, ModBlocks.COPPER_GRATE.newItemStack(1, 2));
				addShapelessRecipe(ModBlocks.COPPER_GRATE.newItemStack(1, 7), waxString, ModBlocks.COPPER_GRATE.newItemStack(1, 3));

				//Copper grate to waxed copper grate
				addShapelessRecipe(ModBlocks.CHISELED_COPPER.newItemStack(1, 4), waxString, ModBlocks.CHISELED_COPPER.newItemStack(1));
				addShapelessRecipe(ModBlocks.CHISELED_COPPER.newItemStack(1, 5), waxString, ModBlocks.CHISELED_COPPER.newItemStack(1, 1));
				addShapelessRecipe(ModBlocks.CHISELED_COPPER.newItemStack(1, 6), waxString, ModBlocks.CHISELED_COPPER.newItemStack(1, 2));
				addShapelessRecipe(ModBlocks.CHISELED_COPPER.newItemStack(1, 7), waxString, ModBlocks.CHISELED_COPPER.newItemStack(1, 3));

				//Cut copper block to waxed cut copper blocks
				addShapelessRecipe(ModBlocks.COPPER_BULB.newItemStack(1, 12), waxString, ModBlocks.COPPER_BULB.newItemStack(1, 4));
				addShapelessRecipe(ModBlocks.COPPER_BULB.newItemStack(1, 13), waxString, ModBlocks.COPPER_BULB.newItemStack(1, 5));
				addShapelessRecipe(ModBlocks.COPPER_BULB.newItemStack(1, 14), waxString, ModBlocks.COPPER_BULB.newItemStack(1, 6));
				addShapelessRecipe(ModBlocks.COPPER_BULB.newItemStack(1, 15), waxString, ModBlocks.COPPER_BULB.newItemStack(1, 7));

				//Cut copper slabs to waxed cut copper slabs
				addShapelessRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 4), waxString, ModBlocks.CUT_COPPER_SLAB.newItemStack(1));
				addShapelessRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 5), waxString, ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 1));
				addShapelessRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 6), waxString, ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 2));
				addShapelessRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 7), waxString, ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 3));

				//Cut copper stairs to waxed cut copper stairs
				addShapelessRecipe(ModBlocks.WAXED_CUT_COPPER_STAIRS.newItemStack(), waxString, ModBlocks.CUT_COPPER_STAIRS.newItemStack());
				addShapelessRecipe(ModBlocks.WAXED_EXPOSED_CUT_COPPER_STAIRS.newItemStack(), waxString, ModBlocks.EXPOSED_CUT_COPPER_STAIRS.newItemStack());
				addShapelessRecipe(ModBlocks.WAXED_WEATHERED_CUT_COPPER_STAIRS.newItemStack(), waxString, ModBlocks.WEATHERED_CUT_COPPER_STAIRS.newItemStack());
				addShapelessRecipe(ModBlocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS.newItemStack(), waxString, ModBlocks.OXIDIZED_CUT_COPPER_STAIRS.newItemStack());

				//Copper door to waxed copper door
				addShapelessRecipe(ModBlocks.WAXED_COPPER_DOOR.newItemStack(), waxString, ModBlocks.COPPER_DOOR.newItemStack());
				addShapelessRecipe(ModBlocks.WAXED_EXPOSED_COPPER_DOOR.newItemStack(), waxString, ModBlocks.EXPOSED_COPPER_DOOR.newItemStack());
				addShapelessRecipe(ModBlocks.WAXED_WEATHERED_COPPER_DOOR.newItemStack(), waxString, ModBlocks.WEATHERED_COPPER_DOOR.newItemStack());
				addShapelessRecipe(ModBlocks.WAXED_OXIDIZED_COPPER_DOOR.newItemStack(), waxString, ModBlocks.OXIDIZED_COPPER_DOOR.newItemStack());

				//Copper trapdoor to waxed cut copper trapdoor
				addShapelessRecipe(ModBlocks.WAXED_COPPER_TRAPDOOR.newItemStack(), waxString, ModBlocks.COPPER_TRAPDOOR.newItemStack());
				addShapelessRecipe(ModBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR.newItemStack(), waxString, ModBlocks.EXPOSED_COPPER_TRAPDOOR.newItemStack());
				addShapelessRecipe(ModBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR.newItemStack(), waxString, ModBlocks.WEATHERED_COPPER_TRAPDOOR.newItemStack());
				addShapelessRecipe(ModBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR.newItemStack(), waxString, ModBlocks.OXIDIZED_COPPER_TRAPDOOR.newItemStack());
			}
		}

		addSmelting(ModBlocks.COBBLED_DEEPSLATE.get(), ModBlocks.DEEPSLATE.newItemStack(), 0.1F);
		addSmelting(ModBlocks.DEEPSLATE_BRICKS.newItemStack(), ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 1), 0.1F);
		addSmelting(ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2), ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 3), 0.1F);

		addShapedRecipe(ModBlocks.POLISHED_DEEPSLATE.newItemStack(4), "xx", "xx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICKS.newItemStack(4), "xx", "xx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICKS.newItemStack(4, 2), "xx", "xx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 4), "x", "x", 'x', ModBlocks.DEEPSLATE_SLAB.newItemStack());

		addShapedRecipe(ModBlocks.TUFF.newItemStack(4, 1), "xx", "xx", 'x', ModBlocks.TUFF.newItemStack());
		addShapedRecipe(ModBlocks.TUFF.newItemStack(4, 2), "xx", "xx", 'x', ModBlocks.TUFF.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.TUFF.newItemStack(1, 3), "x", "x", 'x', ModBlocks.TUFF_SLAB.newItemStack());
		addShapedRecipe(ModBlocks.TUFF.newItemStack(1, 4), "x", "x", 'x', ModBlocks.TUFF_SLAB.newItemStack(1, 2));

		addShapedRecipe(ModBlocks.COBBLED_DEEPSLATE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack());
		addShapedRecipe(ModBlocks.POLISHED_DEEPSLATE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_TILE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2));
		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.DEEPSLATE_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack());
			addShapedRecipe(ModBlocks.DEEPSLATE_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack());
			addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack());
			addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2));
		}
		addShapedRecipe(ModBlocks.DEEPSLATE_WALL.newItemStack(6), "xxx", "xxx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_WALL.newItemStack(6, 1), "xxx", "xxx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_WALL.newItemStack(6), "xxx", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_WALL.newItemStack(6, 1), "xxx", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2));

		addShapedRecipe(ModBlocks.TUFF_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.TUFF.newItemStack());
		addShapedRecipe(ModBlocks.POLISHED_TUFF_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.TUFF.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.TUFF_BRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.TUFF.newItemStack(1, 2));
		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.TUFF_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.TUFF.newItemStack());
			addShapedRecipe(ModBlocks.TUFF_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.TUFF.newItemStack(1, 1));
			addShapedRecipe(ModBlocks.TUFF_SLAB.newItemStack(6, 2), "xxx", 'x', ModBlocks.TUFF.newItemStack(1, 2));
		}
		addShapedRecipe(ModBlocks.TUFF_WALL.newItemStack(6), "xxx", "xxx", 'x', ModBlocks.TUFF.newItemStack());
		addShapedRecipe(ModBlocks.TUFF_WALL.newItemStack(6, 1), "xxx", "xxx", 'x', ModBlocks.TUFF.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.TUFF_WALL.newItemStack(6, 2), "xxx", "xxx", 'x', ModBlocks.TUFF.newItemStack(1, 2));


		// Mud Recipes
		addShapelessRecipe(ModBlocks.MUDDY_MANGROVE_ROOTS.newItemStack(1), ModBlocks.MUD.newItemStack(), ModBlocks.MANGROVE_ROOTS.newItemStack());

		// VANILLA
		addShapelessRecipe(ModBlocks.PACKED_MUD.newItemStack(1, 0), ModBlocks.MUD.newItemStack(1), new ItemStack(Items.wheat, 1));
		addShapedRecipe(ModBlocks.PACKED_MUD.newItemStack(4, 1), "xx", "xx", 'x', ModBlocks.PACKED_MUD.newItemStack(1, 0));

		addShapedRecipe(ModBlocks.MUD_BRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.PACKED_MUD.newItemStack(1, 1));
		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.MUD_BRICK_SLAB.newItemStack(6, 0), "xxx", 'x', ModBlocks.PACKED_MUD.newItemStack(1, 1));
		}
		addShapedRecipe(ModBlocks.MUD_BRICK_WALL.newItemStack(6, 0), "xxx", "xxx", 'x', ModBlocks.PACKED_MUD.newItemStack(1, 1));

		addShapedRecipe(ModBlocks.MOSS_BLOCK.newItemStack(1, 0), "xxx", "xyx", "xxx", 'x', new ItemStack(Blocks.vine, 1), 'y', new ItemStack(Blocks.dirt, 1));

		for (int i = 0; i < getStewFlowers().size(); i++) {
			ItemStack stew = ModItems.SUSPICIOUS_STEW.newItemStack();

			PotionEffect effect = EtFuturum.getSuspiciousStewEffect(getStewFlowers().get(i));

			stew.stackTagCompound = new NBTTagCompound();
			NBTTagList effectsList = new NBTTagList();
			stew.stackTagCompound.setTag(ItemSuspiciousStew.effectsList, effectsList);
			NBTTagCompound potionEffect = new NBTTagCompound();
			potionEffect.setByte(ItemSuspiciousStew.stewEffect, (byte) effect.getPotionID());
			potionEffect.setInteger(ItemSuspiciousStew.stewEffectDuration, effect.getDuration());
			effectsList.appendTag(potionEffect);
			addShapelessRecipe(stew, Blocks.red_mushroom, Blocks.brown_mushroom, Items.bowl, getStewFlowers().get(i));
		}

		if (!ModsList.GTNH.isLoaded()) {
			for (Map.Entry<String, ItemNewBoat.BoatInfo> entry : ItemNewBoat.BOAT_INFO.entrySet()) {
				String key = entry.getKey();
				if (key == null) continue;
				boolean isOak = entry.getKey().equals("minecraft:oak");
				ItemStack boat = (isOak && ConfigBlocksItems.replaceOldBoats ? new ItemStack(Items.boat) : entry.getValue().getBoatItem());
				if (key.endsWith("_chest")) {
					ItemStack inputBoat = ItemNewBoat.BOAT_INFO.get(key.substring(0, key.indexOf("_chest"))).getBoatItem();
					addShapelessRecipe(boat, "chestWood", inputBoat);
				} else {
					if (isOak) { //We're using the plankWood tag for this, so it needs to be in the vanilla sorter
						GameRegistry.addRecipe(new ShapedOreRecipe(boat, (ConfigBlocksItems.replaceOldBoats ? "x x" : "xyx"), "xxx", 'x', "plankWood", 'y', new ItemStack(Items.wooden_shovel, 1)));
					} else { //Not a tagged recipe, we sort this before vanilla recipes, so it takes precedent over them
						addShapedRecipe(boat, (ConfigBlocksItems.replaceOldBoats ? "x x" : "xyx"), "xxx", 'x', entry.getValue().getPlank(), 'y', new ItemStack(Items.wooden_shovel, 1));
					}
				}
			}
			if (!ConfigBlocksItems.replaceOldBoats) {
				addShapelessRecipe(new ItemStack(Items.boat), "boatWood");
				addShapelessRecipe(ModItems.BOATS[0].newItemStack(), Items.wooden_shovel, Items.boat);
			}

			addShapedRecipe(ModBlocks.SHULKER_BOX.newItemStack(), "x", "c", "x", 'x', ModItems.SHULKER_SHELL.newItemStack(), 'c', new ItemStack(Blocks.chest));
		}
		if (ModBlocks.SHULKER_BOX.isEnabled()) {
			for (int i = ore_dyes.length - 1; i >= 0; i--) {//Dyed box recipes
				ItemStack shulker = ModBlocks.SHULKER_BOX.newItemStack();
				shulker.setTagCompound(new NBTTagCompound());
				shulker.getTagCompound().setByte("Color", (byte) (16 - i));
				GameRegistry.addRecipe(new RecipeDyedShulkerBox(shulker, new Object[]{ModBlocks.SHULKER_BOX.get(), ore_dyes[i]}));
			}
		}
		addShapedRecipe(ModItems.SHULKER_BOX_UPGRADE.newItemStack(), "XXX", "XYX", "XXX", 'X', "ingotIron", 'Y', ModItems.SHULKER_SHELL.get());
		addShapedRecipe(ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 1), "XXX", "XYX", "XXX", 'X', "ingotCopper", 'Y', ModItems.SHULKER_SHELL.get());
		addShapedRecipe(ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 2), "XYX", "XXX", "XXX", 'X', "ingotGold", 'Y', "ingotIron");
		addShapedRecipe(ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 3), "GXG", "GYG", "GXG", 'X', "gemDiamond", 'Y', "ingotGold", 'G', "blockGlassColorless");
		addShapedRecipe(ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 4), "XYX", "XXX", "XXX", 'X', Blocks.obsidian, 'Y', "blockGlassColorless");
		addShapedRecipe(ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 5), "XYX", "XXX", "XXX", 'X', "blockGlassColorless", 'Y', Blocks.obsidian);
		addShapedRecipe(ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 6), "GGG", "XYX", "XGX", 'X', "ingotIron", 'Y', "ingotCopper", 'G', "blockGlassColorless");
		addShapedRecipe(ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 7), "XYX", "XXX", "XXX", 'X', "ingotSilver", 'Y', "ingotCopper");
		addShapedRecipe(ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 8), "XYX", "GGG", "XGX", 'X', "ingotGold", 'Y', "ingotSilver", 'G', "blockGlassColorless");

		if (ConfigWorld.tileReplacementMode == -1) {
			//We keep the original enabled checks inside of the booleans and use the original addShapedRecipe function because we need to check it anyways for recipe removal
			if (ModBlocks.ANVIL.isEnabled()) {
				GameRegistry.addShapedRecipe(ModBlocks.ANVIL.newItemStack(), "BBB", " I ", "III", 'I', new ItemStack(Items.iron_ingot), 'B', new ItemStack(Blocks.iron_block));
				removeFirstRecipeFor(Blocks.anvil);
			}

			if (ModBlocks.BREWING_STAND.isEnabled()) {
				GameRegistry.addShapedRecipe(ModBlocks.BREWING_STAND.newItemStack(), " B ", "CCC", 'C', new ItemStack(Blocks.cobblestone), 'B', new ItemStack(Items.blaze_rod));
				removeFirstRecipeFor(Blocks.brewing_stand);
			}

			if (ModBlocks.BEACON.isEnabled()) {
				GameRegistry.addShapedRecipe(ModBlocks.BEACON.newItemStack(), "GGG", "GNG", "OOO", 'G', new ItemStack(Blocks.glass), 'N', new ItemStack(Items.nether_star), 'O', new ItemStack(Blocks.obsidian));
				removeFirstRecipeFor(Blocks.beacon);
			}

			if (ModBlocks.ENCHANTMENT_TABLE.isEnabled()) {
				GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.ENCHANTMENT_TABLE.newItemStack(), " B ", "D#D", "###", '#', Blocks.obsidian, 'B', Items.book, 'D', "gemDiamond"));
				removeFirstRecipeFor(Blocks.enchanting_table);
			}

			if (ModBlocks.SPONGE.isEnabled()) {
				addShapelessRecipe(ModBlocks.SPONGE.newItemStack(), Blocks.sponge);
				addShapelessRecipe(new ItemStack(Blocks.sponge), ModBlocks.SPONGE.get()); //For recipes that want the vanilla sponge you can convert it
			}
		}

		addShapedRecipe(ModBlocks.STONE_WALL.newItemStack(6), "BBB", "BBB", 'B', new ItemStack(Blocks.stonebrick, 1, 0));
		addShapedRecipe(ModBlocks.STONE_WALL.newItemStack(6, 1), "BBB", "BBB", 'B', new ItemStack(Blocks.stonebrick, 1, 1));
		addShapedRecipe(ModBlocks.STONE_WALL.newItemStack(6, 2), "BBB", "BBB", 'B', new ItemStack(Blocks.sandstone, 1, 0));
		addShapedRecipe(ModBlocks.STONE_WALL.newItemStack(6, 3), "BBB", "BBB", 'B', new ItemStack(Blocks.brick_block, 1, 0));

		//TODO Nether brick wall should be individually toggleable because of Netherlicious
		addShapedRecipe(ModBlocks.NETHER_BRICK_WALL.newItemStack(6), "BBB", "BBB", 'B', new ItemStack(Blocks.nether_brick));

		addShapedRecipe(ModBlocks.SMITHING_TABLE.newItemStack(), "II", "PP", "PP", 'P', "plankWood", 'I', "ingotIron");

		addShapedRecipe(ModBlocks.FLETCHING_TABLE.newItemStack(), "FF", "PP", "PP", 'P', "plankWood", 'F', new ItemStack(Items.flint, 1, 0));

		addShapedRecipe(ModBlocks.STONECUTTER.newItemStack(), " I ", "SSS", 'S', "stone", 'I', "ingotIron");

		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.COMPOSTER.newItemStack(), "S S", "S S", "SSS", 'S', "slabWood");
		}

		addShapedRecipe(ModBlocks.CARTOGRAPHY_TABLE.newItemStack(), "pp", "PP", "PP", 'P', "plankWood", 'p', new ItemStack(Items.paper, 1, 0));

		addShapedRecipe(ModBlocks.LOOM.newItemStack(), "SS", "PP", 'P', "plankWood", 'S', new ItemStack(Items.string, 1, 0));

		addShapedRecipe(ModBlocks.AMETHYST_BLOCK.newItemStack(), "AA", "AA", 'A', "gemAmethyst");
		addShapedRecipe(ModBlocks.TINTED_GLASS.newItemStack(2, 0), " A ", "AGA", " A ", 'A', "gemAmethyst", 'G', "blockGlassColorless");

		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.TARGET.newItemStack(), " R ", "RHR", " R ", 'R', "dustRedstone", 'H', Blocks.hay_block);

			addShapedRecipe(ModBlocks.OBSERVER.newItemStack(), "CCC", "RRQ", "CCC", 'R', "dustRedstone", 'C', "cobblestone", 'Q', "gemQuartz");
		}

		addShapedRecipe(ModBlocks.HONEY_BLOCK.newItemStack(), "HH", "HH", 'H', ModItems.HONEY_BOTTLE.get());
		addShapedRecipe(ModBlocks.HONEYCOMB_BLOCK.newItemStack(), "HH", "HH", 'H', "materialHoneycomb");
		addShapedRecipe(ModBlocks.BEEHIVE.newItemStack(), "WWW", "HHH", "WWW", 'W', "plankWood", 'H', "materialHoneycomb");

		addShapedRecipe(ModBlocks.CHAIN.newItemStack(), "N", "G", "N", 'N', "nuggetIron", 'G', "ingotIron");

		addShapedRecipe(ModBlocks.BLACKSTONE.newItemStack(4, 1), "xx", "xx", 'x', ModBlocks.BLACKSTONE.newItemStack());
		addShapedRecipe(ModBlocks.BLACKSTONE.newItemStack(4, 2), "xx", "xx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.BLACKSTONE.newItemStack(1, 4), "x", "x", 'x', ModBlocks.BLACKSTONE_SLAB.newItemStack(1, 1));

		addSmelting(ModBlocks.BLACKSTONE.newItemStack(1, 2), ModBlocks.BLACKSTONE.newItemStack(1, 3), 0.1F);

		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.BLACKSTONE_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack());
			addShapedRecipe(ModBlocks.BLACKSTONE_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1));
			addShapedRecipe(ModBlocks.BLACKSTONE_SLAB.newItemStack(6, 2), "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 2));
		}

		addShapedRecipe(ModBlocks.BLACKSTONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack());
		addShapedRecipe(ModBlocks.POLISHED_BLACKSTONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.POLISHED_BLACKSTONE_BRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 2));

		addShapedRecipe(ModBlocks.BLACKSTONE_WALL.newItemStack(6), "xxx", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack());
		addShapedRecipe(ModBlocks.BLACKSTONE_WALL.newItemStack(6, 1), "xxx", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.BLACKSTONE_WALL.newItemStack(6, 2), "xxx", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 2));

		if (!ModsList.GTNH.isLoaded()) {
			addShapedRecipe(ModBlocks.POLISHED_BLACKSTONE_BUTTON.newItemStack(1), "x", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1));
			addShapedRecipe(ModBlocks.POLISHED_BLACKSTONE_PRESSURE_PLATE.newItemStack(1), "xx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1));
		}

		addShapedRecipe(ModBlocks.BASALT.newItemStack(4, 1), "xx", "xx", 'x', ModBlocks.BASALT.newItemStack());
		addSmelting(ModBlocks.BASALT.newItemStack(), ModBlocks.SMOOTH_BASALT.newItemStack(), 0.1F);

		if (ModsList.LOTR.isLoaded()) {//LoTR ores and ingots stupidly lack dictionary entries; let's add them so the code below can find them.
			if (ConfigModCompat.moddedRawOres) {
				registerOre("oreCopper", GameRegistry.findBlock("lotr", "tile.oreCopper"));
				registerOre("ingotCopper", GameRegistry.findItem("lotr", "item.copper"));

				registerOre("oreTin", GameRegistry.findBlock("lotr", "tile.oreTin"));
				registerOre("ingotTin", GameRegistry.findItem("lotr", "item.tin"));
				registerOre("oreSilver", GameRegistry.findBlock("lotr", "tile.oreSilver"));
				registerOre("ingotSilver", GameRegistry.findItem("lotr", "item.silver"));
				registerOre("oreMithril", GameRegistry.findBlock("lotr", "tile.oreMithril"));
				registerOre("ingotMithril", GameRegistry.findItem("lotr", "item.mithril"));
			}
		}

		if (ModsList.BIG_REACTORS.isLoaded()) {
			if (ConfigModCompat.moddedRawOres) {
				registerOre("oreUranium", ExternalContent.Blocks.BR_YELLORITE_ORE.get());
			}
		}

		addShapelessRecipe(new ItemStack(Items.dye, 1, 9), ModBlocks.PINK_PETALS.get());

		if (!ConfigExperiments.netherDimensionProvider) { //A way to get soul soil without new Nether
			addShapelessRecipe(ModBlocks.SOUL_SOIL.newItemStack(5), Blocks.dirt, Blocks.soul_sand, Blocks.soul_sand, Blocks.soul_sand, Blocks.soul_sand);
		}

		if (ConfigModCompat.moddedDeepslateOres) { //OreDict-based registration is only used when mod support is enabled
			DeepslateOreRegistry.addOreByOreDict("oreCoal", ModBlocks.DEEPSLATE_COAL_ORE.get());
			DeepslateOreRegistry.addOreByOreDict("oreLapis", ModBlocks.DEEPSLATE_LAPIS_ORE.get());
			DeepslateOreRegistry.addOreByOreDict("oreDiamond", ModBlocks.DEEPSLATE_DIAMOND_ORE.get());
			DeepslateOreRegistry.addOreByOreDict("oreEmerald", ModBlocks.DEEPSLATE_EMERALD_ORE.get());
			DeepslateOreRegistry.addOreByOreDict("oreCopper", ModBlocks.DEEPSLATE_COPPER_ORE.get());
			DeepslateOreRegistry.addOreByOreDict("oreIron", ModBlocks.DEEPSLATE_IRON_ORE.get());
			DeepslateOreRegistry.addOreByOreDict("oreGold", ModBlocks.DEEPSLATE_GOLD_ORE.get());
			DeepslateOreRegistry.addOreByOreDict("oreRedstone", ModBlocks.DEEPSLATE_REDSTONE_ORE.get());
		}

		addTagsAndDeepslate("Coal", ModBlocks.DEEPSLATE_COAL_ORE.newItemStack());
		addTagsAndDeepslate("Lapis", ModBlocks.DEEPSLATE_LAPIS_ORE.newItemStack());
		addTagsAndDeepslate("Diamond", ModBlocks.DEEPSLATE_DIAMOND_ORE.newItemStack());
		addTagsAndDeepslate("Emerald", ModBlocks.DEEPSLATE_EMERALD_ORE.newItemStack());
		addTagsAndDeepslate("Copper", ModBlocks.DEEPSLATE_COPPER_ORE.newItemStack());
		addTagsAndDeepslate("Iron", ModBlocks.DEEPSLATE_IRON_ORE.newItemStack());
		addTagsAndDeepslate("Gold", ModBlocks.DEEPSLATE_GOLD_ORE.newItemStack());
		addTagsAndDeepslate("Redstone", ModBlocks.DEEPSLATE_REDSTONE_ORE.newItemStack());

		registerGeneralDeepslateOres();
		registerModSupportDeepslateOres();

		ItemStack result;
		if (ModItems.COPPER_INGOT.isEnabled()) {
			result = ModItems.COPPER_INGOT.newItemStack();
		} else {
			result = Utils.getFirstStackFromTag("ingotCopper");
		}

		addShapedRecipe(ModBlocks.RAW_ORE_BLOCK.newItemStack(), "xxx", "xxx", "xxx", 'x', ModItems.RAW_ORE.newItemStack());
		addShapedRecipe(ModItems.RAW_ORE.newItemStack(9), "x", 'x', ModBlocks.RAW_ORE_BLOCK.newItemStack());
		if (result != null) {
			addSmelting(ModItems.RAW_ORE.newItemStack(), result, 0.7F);
		}

		addShapedRecipe(ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 1), "xxx", "xxx", "xxx", 'x', ModItems.RAW_ORE.newItemStack(1, 1));
		addShapedRecipe(ModItems.RAW_ORE.newItemStack(9, 1), "x", 'x', ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 1));
		addSmelting(ModItems.RAW_ORE.newItemStack(1, 1), new ItemStack(Items.iron_ingot, 1, 0), 0.7F);

		addShapedRecipe(ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 2), "xxx", "xxx", "xxx", 'x', ModItems.RAW_ORE.newItemStack(1, 2));
		addShapedRecipe(ModItems.RAW_ORE.newItemStack(9, 2), "x", 'x', ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 2));
		addSmelting(ModItems.RAW_ORE.newItemStack(1, 2), new ItemStack(Items.gold_ingot, 1, 0), 0.7F);

		registerGeneralRawOres();
		registerModSupportRawOres();
	}

	public static void registerGeneralDeepslateOres() {
		//Insert alternate Mythril spelling to list. Yes I know "mithril" is technically the primary spelling but "mythril" is used by most mods, so "mithril" is secondary to it here.
		for (BlockGeneralModdedDeepslateOre block : BlockGeneralModdedDeepslateOre.loaded) { //Future-proofing in case I add more than one
			for (int i = 0; i < block.ores.length; i++) {
				String type = block.ores[i];
				//This can run post-load, so don't register multiple tags if the deepslate ore already got one.
				//We have to do this since it is impossible to remove OreDictionary entries.
				//Well it's probably *technically* possible but I don't want to do it, PR an OD remover if you need EFR to do it.
				//For now just restart your game to clear entries that would no longer get a tag.
				for (int j = 0; j < 1; j++) { //If it's mythril, we'll run this once more, changing the spelling to mithril to account for both tags.
					if (Utils.listGeneralModdedDeepslateOre(type)) { //Make sure an ore is present.
//						registerOre(type, ModBlocks.MODDED_DEEPSLATE_ORE.newItemStack(1, i));
						DeepslateOreRegistry.addOreByOreDict(type, ModBlocks.MODDED_DEEPSLATE_ORE.get(), i);
						registerOre(type, ModBlocks.MODDED_DEEPSLATE_ORE.newItemStack(1, i));
						registerOre(type.replace("ore", "oreDeepslate"), ModBlocks.MODDED_DEEPSLATE_ORE.newItemStack(1, i));
					}
					//We put this outside of the if statement so additional tags added with CT get added.
					if (type.endsWith("Mythril")) {
						type = type.replace("Mythril", "Mithril"); //Redoes it once more for mithril spelling
						j = -1;
					}
				}
			}
		}
	}

	public static void unregisterGeneralRawOres() {
		Pair<List<ItemGeneralModdedRawOre>, List<BlockGeneralModdedRawOre>> pair = Pair.of(ItemGeneralModdedRawOre.loaded, BlockGeneralModdedRawOre.loaded);
		if (pair.getLeft().isEmpty()) {
			return;
		}
		for (int j = 0; j < pair.getLeft().size(); j++) {
			ItemGeneralModdedRawOre oreItem = pair.getLeft().get(j);
			BlockGeneralModdedRawOre oreBlock = pair.getRight().get(j);
			for (int i = 0; i < oreItem.ores.length; i++) {
				removeAllEFRRecipesFor(oreItem, i);
				removeAllEFRRecipesFor(oreBlock, i);
				ItemStack stack = new ItemStack(oreItem, 1, i);
				Iterator<ItemStack> iterator = FurnaceRecipes.smelting().getSmeltingList().keySet().iterator();
				while (iterator.hasNext()) {
					ItemStack smeltingInput = iterator.next();
					if (stack.getItem() == smeltingInput.getItem() && stack.getItemDamage() == smeltingInput.getItemDamage()) {
						iterator.remove();
					}
				}
			}
		}
	}

	public static void registerGeneralRawOres() {
		//Future-proofing in case I add more of these. Also will have only the meta-combined versions when enabled for the eventual NEID/EID support
		Pair<List<ItemGeneralModdedRawOre>, List<BlockGeneralModdedRawOre>> pair = Pair.of(ItemGeneralModdedRawOre.loaded, BlockGeneralModdedRawOre.loaded);
		if (pair.getLeft().size() != pair.getRight().size()) {
			throw new RuntimeException("Modded raw ore block count does not match modded raw ore item count!");
		}
		if (pair.getLeft().isEmpty()) {
			return;
		}
		for (int k = 0; k < pair.getLeft().size(); k++) {
			ItemGeneralModdedRawOre oreItem = pair.getLeft().get(k);
			BlockGeneralModdedRawOre oreBlock = pair.getRight().get(k);
			//Insert alternate Mythril spelling to list. Yes I know "mithril" is technically the primary spelling but "mythril" is used by most mods, so "mithril" is secondary to it here.
			for (int i = 0; i < oreItem.ores.length; i++) {
				String type = oreItem.ores[i];
				//This can run post-load, so don't register multiple tags if the raw ore already got one.
				//We have to do this since it is impossible to remove OreDictionary entries.
				//Well it's probably *technically* possible but I don't want to do it, PR an OD remover if you need EFR to do it.
				//For now just restart your game to clear entries that would no longer get a tag.
				boolean registeredRecipe = false;
				for (int j = 0; j < 1; j++) { //If it's mythril, we'll run this once more, changing the spelling to mithril to account for both tags.
					if (Utils.listGeneralModdedRawOre(type)) { //Make sure an ingot AND ore is present
						registerOre(type.replace("ingot", "raw"), new ItemStack(oreItem, 1, i));
						registerOre(type.replace("ingot", "blockRaw"), new ItemStack(oreBlock, 1, i));
						if (ConfigFunctions.registerRawItemAsOre) {
							registerOre(type.replace("ingot", "ore"), new ItemStack(oreItem, 1, i));
						}
						if (!registeredRecipe) {
							addShapedRecipe(new ItemStack(oreBlock, 1, i), "xxx", "xxx", "xxx", 'x', new ItemStack(oreItem, 1, i));
							addShapedRecipe(new ItemStack(oreItem, 9, i), "x", 'x', new ItemStack(oreBlock, 1, i));
							registeredRecipe = true;
						}
						addSmelting(new ItemStack(oreItem, 1, i), Utils.getFirstStackFromTag(type), 0.7F);
					}
					if (type.endsWith("Mythril")) {
						type = type.replace("Mythril", "Mithril"); //Redoes it once more for mithril spelling
						j = -1;
					}
				}
			}
		}
	}

	private static void registerModSupportDeepslateOres() {
		if (Utils.enableModdedDeepslateOres()) {
			for (BaseDeepslateOre ore : BaseDeepslateOre.loaded) {
				ItemStack baseStack = new ItemStack(ore.getBase(), 1, ore.getBaseMeta());
				ItemStack stack = new ItemStack(ore);
				for (String tag : EtFuturum.getOreStrings(baseStack)) {
					if (tag.startsWith("ore")) {
						addTagsAndDeepslate(tag.replace("ore", ""), stack);
					} else {
						registerOre(tag, stack);
					}
				}
				if (validateItems(ore)) {
					DeepslateOreRegistry.addOre(ore.getBase(), ore.getBaseMeta(), ore, 0);
				}
			}
			for (BaseSubtypesDeepslateOre ore : BaseSubtypesDeepslateOre.loaded) {
				for (int i = 0; i < ore.getTypes().length; i++) {
					ItemStack baseStack = new ItemStack(ore.getBase(i), 1, ore.getBaseMeta(i));
					ItemStack stack = new ItemStack(ore, 1, i);
					for (String tag : EtFuturum.getOreStrings(baseStack)) {
						if (tag.startsWith("ore")) {
							addTagsAndDeepslate(tag.replace("ore", ""), stack);
						} else {
							registerOre(tag, stack);
						}
					}
					if (validateItems(ore)) {
						DeepslateOreRegistry.addOre(ore.getBase(i), ore.getBaseMeta(i), ore, i);
					}
				}
			}
		}
	}

	private static void registerModSupportRawOres() {
		if (ModItems.RAW_ADAMANTIUM.isEnabled() && ModBlocks.RAW_ADAMANTIUM_BLOCK.isEnabled()) {
			addTagsAndRawOre("Adamantium", ModItems.RAW_ADAMANTIUM.newItemStack());
			addTagsAndRawOre("Adamantite", ModItems.RAW_ADAMANTIUM.newItemStack());
			addTagsAndRawOre("Adamantine", ModItems.RAW_ADAMANTIUM.newItemStack());
			registerOre("blockRawAdamantium", ModBlocks.RAW_ADAMANTIUM_BLOCK.newItemStack());
			registerOre("blockRawAdamantite", ModBlocks.RAW_ADAMANTIUM_BLOCK.newItemStack());
			registerOre("blockRawAdamantine", ModBlocks.RAW_ADAMANTIUM_BLOCK.newItemStack());
			addShapedRecipe(ModBlocks.RAW_ADAMANTIUM_BLOCK.newItemStack(), "xxx", "xxx", "xxx", 'x', ModItems.RAW_ADAMANTIUM.newItemStack());
			addShapedRecipe(ModItems.RAW_ADAMANTIUM.newItemStack(9), "x", 'x', ModBlocks.RAW_ADAMANTIUM_BLOCK.newItemStack());
			addSmelting(ModItems.RAW_ADAMANTIUM.newItemStack(), ExternalContent.Items.SIMPLEORES_ADAMANTIUM_INGOT.newItemStack(),
					ExternalContent.Items.SIMPLEORES_ADAMANTIUM_INGOT.get().getSmeltingExperience(ExternalContent.Items.SIMPLEORES_ADAMANTIUM_INGOT.newItemStack()));
			RawOreRegistry.addOre("oreAdamantium", ModItems.RAW_ADAMANTIUM.get(), 0);
		}
	}

	private static void addTagsAndDeepslate(String tagNoPrefix, ItemStack stack) {
		registerOre("ore" + StringUtils.capitalize(tagNoPrefix), stack);
		registerOre("oreDeepslate" + StringUtils.capitalize(tagNoPrefix), stack);
	}

	private static void addTagsAndRawOre(String tagNoPrefix, ItemStack stack) {
		if (ConfigFunctions.registerRawItemAsOre) {
			registerOre("ore" + StringUtils.capitalize(tagNoPrefix), stack);
		}
		registerOre("raw" + StringUtils.capitalize(tagNoPrefix), stack);
	}

	private static List<ItemStack> getStewFlowers() {
		List<ItemStack> list = new ArrayList<>();

		list.add(new ItemStack(Blocks.red_flower, 1, 2));
		list.add(new ItemStack(Blocks.red_flower, 1, 3));
		list.add(new ItemStack(Blocks.red_flower, 1, 1));
		list.add(new ItemStack(Blocks.yellow_flower, 1));
		list.add(new ItemStack(Blocks.red_flower, 1, 8));
		list.add(new ItemStack(Blocks.red_flower, 1, 0));
		list.add(new ItemStack(Blocks.red_flower, 1, 4));
		list.add(new ItemStack(Blocks.red_flower, 1, 5));
		list.add(new ItemStack(Blocks.red_flower, 1, 6));
		list.add(new ItemStack(Blocks.red_flower, 1, 7));

		list.add(ModBlocks.LILY_OF_THE_VALLEY.newItemStack());
		list.add(ModBlocks.CORNFLOWER.newItemStack());
		list.add(ModBlocks.WITHER_ROSE.newItemStack());

		return list;
	}

	private static void removeFurnaceRecipeFor(ItemStack input, ItemStack output) {
		for (Map.Entry<ItemStack, ItemStack> set : FurnaceRecipes.smelting().getSmeltingList().entrySet()) {
			ItemStack setInput = set.getKey();
			ItemStack setOutput = set.getValue();
			int wildcard = OreDictionary.WILDCARD_VALUE;
			if (input.getItem() == setInput.getItem() && (input.getItemDamage() == wildcard || setInput.getItemDamage() == wildcard || (input.getItemDamage() == setInput.getItemDamage()))) {
				if (output.getItem() == setOutput.getItem() && (output.getItemDamage() == wildcard || setOutput.getItemDamage() == wildcard || (output.getItemDamage() == setOutput.getItemDamage()))) {
					FurnaceRecipes.smelting().getSmeltingList().remove(setInput, setOutput);
					return;
				}
			}
		}
	}

	private static void registerOre(String oreName, ItemStack ore) {
		if (validateItems(ore)) {
			OreDictionary.registerOre(oreName, ore);
		}
	}

	private static void registerOre(String oreName, Item ore) {
		if (validateItems(ore)) {
			OreDictionary.registerOre(oreName, ore);
		}
	}

	private static void registerOre(String oreName, Block ore) {
		if (validateItems(ore)) {
			OreDictionary.registerOre(oreName, ore);
		}
	}

	private static void addSmelting(Item input, ItemStack output, float exp) {
		if (validateItems(input) && validateItems(output)) {
			GameRegistry.addSmelting(input, output, exp);
		}
	}

	private static void addSmelting(Block input, ItemStack output, float exp) {
		if (validateItems(input) && validateItems(output)) {
			GameRegistry.addSmelting(input, output, exp);
		}
	}

	private static void addSmelting(ItemStack input, ItemStack output, float exp) {
		if (validateItems(input) && validateItems(output)) {
			GameRegistry.addSmelting(input, output, exp);
		}
	}

	private static void addShapedRecipe(ItemStack output, Object... objects) {
		if (validateItems(output) && validateItems(objects)) {
			GameRegistry.addRecipe(new ShapedEtFuturumRecipe(output, objects));
		}
	}

	private static void addShapelessRecipe(ItemStack output, Object... objects) {
		if (validateItems(output) && validateItems(objects)) {
			GameRegistry.addRecipe(new ShapelessEtFuturumRecipe(output, objects));
		}
	}

	public static boolean validateItems(Object... objects) {
		for (Object object : objects) {
			if (object == null || object == Blocks.air) return false;
			if (object instanceof String) continue;

			if (object instanceof ItemStack) {
				if (((ItemStack) object).getItem() == null || Item.itemRegistry.getNameForObject(((ItemStack) object).getItem()) == null) {
					return false;
				}
			}
			if (object instanceof Item) {
				if (Item.itemRegistry.getNameForObject(object) == null) {
					return false;
				}
			}
			if (object instanceof Block) {
				if (Block.blockRegistry.getNameForObject(object) == null) {
					return false;
				}
			}
		}
		return true;
	}

	private static void removeFirstRecipeFor(Block block) {
		removeFirstRecipeFor(Item.getItemFromBlock(block));
	}

	private static void removeFirstRecipeFor(Block block, int meta) {
		removeFirstRecipeFor(Item.getItemFromBlock(block), meta);
	}

	private static void removeFirstRecipeFor(Item item) {
		removeFirstRecipeFor(item, OreDictionary.WILDCARD_VALUE);
	}

	private static void removeFirstRecipeFor(Item item, int meta) {
		Iterator<IRecipe> iterator = CraftingManager.getInstance().getRecipeList().iterator();
		while (iterator.hasNext()) {
			IRecipe recipe = iterator.next();
			if (recipe != null) {
				ItemStack stack = recipe.getRecipeOutput();
				if (stack != null && stack.getItem() != null && stack.getItem() == item && (meta == OreDictionary.WILDCARD_VALUE || meta == stack.getItemDamage())) {
					iterator.remove();
					return;
				}
			}
		}
	}

	private static void removeAllEFRRecipesFor(Block block) {
		removeAllEFRRecipesFor(Item.getItemFromBlock(block));
	}

	private static void removeAllEFRRecipesFor(Block block, int meta) {
		removeAllEFRRecipesFor(Item.getItemFromBlock(block), meta);
	}

	private static void removeAllEFRRecipesFor(Item item) {
		removeAllEFRRecipesFor(item, OreDictionary.WILDCARD_VALUE);
	}

	private static void removeAllEFRRecipesFor(Item item, int meta) {
		Iterator<IRecipe> iterator = CraftingManager.getInstance().getRecipeList().iterator();
		while (iterator.hasNext()) {
			IRecipe recipe = iterator.next();
			if ((recipe instanceof ShapedEtFuturumRecipe || recipe instanceof ShapelessEtFuturumRecipe)) {
				ItemStack stack = recipe.getRecipeOutput();
				if (stack != null && stack.getItem() != null && stack.getItem() == item && (meta == OreDictionary.WILDCARD_VALUE || meta == stack.getItemDamage())) {
					iterator.remove();
				}
			}
		}
	}
}