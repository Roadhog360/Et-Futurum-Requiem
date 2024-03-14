package ganymedes01.etfuturum.recipes;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.api.DeepslateOreRegistry;
import ganymedes01.etfuturum.blocks.BaseSlab;
import ganymedes01.etfuturum.blocks.IDegradable;
import ganymedes01.etfuturum.blocks.ores.modded.BlockModdedDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.entities.EntityNewBoat;
import ganymedes01.etfuturum.items.ItemModdedRawOre;
import ganymedes01.etfuturum.items.ItemSuspiciousStew;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

		modernWoodTypesEnabled[0] = ConfigBlocksItems.enableCrimsonBlocks;
		modernWoodTypesEnabled[1] = ConfigBlocksItems.enableWarpedBlocks;
		modernWoodTypesEnabled[2] = ConfigBlocksItems.enableMangroveBlocks;
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

	private static void tweakRecipes() {
		if (ConfigBlocksItems.enableExtraVanillaSlabs) {
			removeFirstRecipeFor(Blocks.stone_slab, 0);
		}

		if (ConfigBlocksItems.enableDoors) {
			Items.wooden_door.setMaxStackSize(64);
			Items.iron_door.setMaxStackSize(64);
		}

		if (ConfigFunctions.enableDoorRecipeBuffs) {
			removeFirstRecipeFor(Items.wooden_door);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.wooden_door, 3), "xx", "xx", "xx", 'x', "plankWood"));
			removeFirstRecipeFor(Items.iron_door);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.iron_door, 3), "xx", "xx", "xx", 'x', "ingotIron"));
		}

		if (ConfigBlocksItems.enableFences) {
			removeFirstRecipeFor(Blocks.fence);
			removeFirstRecipeFor(Blocks.nether_brick_fence);
		}

		if (ConfigBlocksItems.replaceOldBoats && ConfigBlocksItems.enableNewBoats) {
			removeFirstRecipeFor(Items.boat);
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
		registerOre("stairWood", ModBlocks.BAMBOO_MOSAIC_STAIRS.get());

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

		addShapedRecipe(ModBlocks.OLD_GRAVEL.newItemStack(4), "xy", "yx", 'x', ModBlocks.COARSE_DIRT.get(), 'y', Blocks.gravel);

		if (ConfigFunctions.enableStoneBrickRecipes) {
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.mossy_cobblestone), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.vine));
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.stonebrick, 1, 1), new ItemStack(Blocks.stonebrick), new ItemStack(Blocks.vine));
			GameRegistry.addShapedRecipe(new ItemStack(Blocks.stonebrick, 1, 3), "x", "x", 'x', new ItemStack(Blocks.stone_slab, 1, 5));
			GameRegistry.addSmelting(new ItemStack(Blocks.stonebrick), new ItemStack(Blocks.stonebrick, 1, 2), 0.0F);
		}

		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.mossy_cobblestone), new ItemStack(Blocks.cobblestone), ModBlocks.MOSS_BLOCK.newItemStack());
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.stonebrick, 1, 1), new ItemStack(Blocks.stonebrick), ModBlocks.MOSS_BLOCK.newItemStack());

		if (ConfigFunctions.enableRecipeForTotem) {
			addShapedRecipe(ModItems.TOTEM_OF_UNDYING.newItemStack(), "EBE", "GBG", " G ", 'E', "gemEmerald", 'G', "ingotGold", 'B', "blockGold");
		}

		addShapedRecipe(ModBlocks.SLIME.newItemStack(), "xxx", "xxx", "xxx", 'x', new ItemStack(Items.slime_ball));
		addShapelessRecipe(new ItemStack(Items.slime_ball, 9), ModBlocks.SLIME.get());

		addShapedRecipe(ModBlocks.COARSE_DIRT.newItemStack(4), "xy", "yx", 'x', new ItemStack(Blocks.dirt), 'y', new ItemStack(Blocks.gravel));

		addSmelting(ModItems.MUTTON_RAW.get(), ModItems.MUTTON_COOKED.newItemStack(), 0.35F);

		addShapedRecipe(new ItemStack(Items.iron_ingot), "xxx", "xxx", "xxx", 'x', "nuggetIron");
		addShapelessRecipe(ModItems.NUGGET_IRON.newItemStack(9), "ingotIron");

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
			addShapedRecipe(ModBlocks.STONE_SLAB_2.newItemStack(6, i), "xxx", 'x', dictName);
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

		Block[] metaBlocks = new Block[]{Blocks.stone, Blocks.mossy_cobblestone, Blocks.stonebrick, Blocks.sandstone};
		for (int i = 0; i < metaBlocks.length; i++) {
			addShapedRecipe(ModBlocks.STONE_SLAB.newItemStack(6, i), "xxx", 'x', new ItemStack(metaBlocks[i], 1, i != 0 ? i - 1 : i));
		}
		addShapedRecipe(new ItemStack(Blocks.stone_slab, 6, 0), "xxx", 'x', ModBlocks.SMOOTH_STONE.newItemStack());

		addShapedRecipe(ModBlocks.STONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', new ItemStack(Blocks.stone, 1, 0));
		addShapedRecipe(ModBlocks.MOSSY_COBBLESTONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', new ItemStack(Blocks.mossy_cobblestone, 1, 0));
		addShapedRecipe(ModBlocks.MOSSY_STONE_BRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', new ItemStack(Blocks.stonebrick, 1, 1));

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

		//New logs, bark, stripped log and stripped bark to planks
		addShapedRecipe(ModBlocks.WOOD_PLANKS.newItemStack(4), "x", 'x', ModBlocks.CRIMSON_STEM.newItemStack(1, OreDictionary.WILDCARD_VALUE));
		addShapedRecipe(ModBlocks.WOOD_PLANKS.newItemStack(4, 1), "x", 'x', ModBlocks.WARPED_STEM.newItemStack(1, OreDictionary.WILDCARD_VALUE));
		addShapedRecipe(ModBlocks.WOOD_PLANKS.newItemStack(4, 2), "x", 'x', ModBlocks.MANGROVE_LOG.newItemStack(1, OreDictionary.WILDCARD_VALUE));
		addShapedRecipe(ModBlocks.WOOD_PLANKS.newItemStack(4, 3), "x", 'x', ModBlocks.CHERRY_LOG.newItemStack(1, OreDictionary.WILDCARD_VALUE));
		addShapedRecipe(ModBlocks.WOOD_PLANKS.newItemStack(2, 4), "x", 'x', ModBlocks.BAMBOO_BLOCK.newItemStack(1, OreDictionary.WILDCARD_VALUE));

		if (ConfigBlocksItems.enableStrippedLogs && ConfigBlocksItems.enableBarkLogs) {
			//New stripped logs to new stripped bark
			addShapedRecipe(ModBlocks.CRIMSON_STEM.newItemStack(3, 3), "xx", "xx", 'x', ModBlocks.CRIMSON_STEM.newItemStack(2, 2));
			addShapedRecipe(ModBlocks.WARPED_STEM.newItemStack(3, 3), "xx", "xx", 'x', ModBlocks.WARPED_STEM.newItemStack(2, 2));
			addShapedRecipe(ModBlocks.MANGROVE_LOG.newItemStack(3, 3), "xx", "xx", 'x', ModBlocks.MANGROVE_LOG.newItemStack(2, 3));
			addShapedRecipe(ModBlocks.CHERRY_LOG.newItemStack(3, 3), "xx", "xx", 'x', ModBlocks.CHERRY_LOG.newItemStack(2, 4));
		}

		if (ConfigBlocksItems.enableBarkLogs) {
			//New logs to new bark
			addShapedRecipe(ModBlocks.CRIMSON_STEM.newItemStack(3, 1), "xx", "xx", 'x', ModBlocks.CRIMSON_STEM.newItemStack(1));
			addShapedRecipe(ModBlocks.WARPED_STEM.newItemStack(3, 1), "xx", "xx", 'x', ModBlocks.WARPED_STEM.newItemStack(1));
			addShapedRecipe(ModBlocks.MANGROVE_LOG.newItemStack(3, 1), "xx", "xx", 'x', ModBlocks.MANGROVE_LOG.newItemStack(1));
			addShapedRecipe(ModBlocks.CHERRY_LOG.newItemStack(3, 1), "xx", "xx", 'x', ModBlocks.CHERRY_LOG.newItemStack(1));
		}

		addShapedRecipe(ModBlocks.FENCE_SPRUCE.newItemStack(3), "xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 1), 'y', "stickWood");
		addShapedRecipe(ModBlocks.FENCE_BIRCH.newItemStack(3), "xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 2), 'y', "stickWood");
		addShapedRecipe(ModBlocks.FENCE_JUNGLE.newItemStack(3), "xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 3), 'y', "stickWood");
		addShapedRecipe(ModBlocks.FENCE_ACACIA.newItemStack(3), "xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 4), 'y', "stickWood");
		addShapedRecipe(ModBlocks.FENCE_DARK_OAK.newItemStack(3), "xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 5), 'y', "stickWood");

		if (ConfigBlocksItems.enableCrimsonBlocks) {
			addShapedRecipe(ModBlocks.WOOD_FENCE.newItemStack(3), "xyx", "xyx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1), 'y', "stickWood");
			addShapedRecipe(ModBlocks.WOOD_SLAB.newItemStack(6, 0), "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 0));
		}
		if (ConfigBlocksItems.enableWarpedBlocks) {
			addShapedRecipe(ModBlocks.WOOD_FENCE.newItemStack(3, 1), "xyx", "xyx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1), 'y', "stickWood");
			addShapedRecipe(ModBlocks.WOOD_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1));
		}
		if (ConfigBlocksItems.enableMangroveBlocks) {
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

		addShapedRecipe(ModBlocks.RED_SANDSTONE.newItemStack(), "xx", "xx", 'x', new ItemStack(Blocks.sand, 1, 1));
		addShapedRecipe(ModBlocks.RED_SANDSTONE.newItemStack(1, 1), "x", "x", 'x', ModBlocks.RED_SANDSTONE_SLAB.newItemStack());
		addShapedRecipe(ModBlocks.RED_SANDSTONE.newItemStack(4, 2), "xx", "xx", 'x', ModBlocks.RED_SANDSTONE.newItemStack());
		addShapedRecipe(ModBlocks.RED_SANDSTONE_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.RED_SANDSTONE.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.RED_SANDSTONE_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.RED_SANDSTONE.newItemStack(1, OreDictionary.WILDCARD_VALUE));
		addShapedRecipe(ModBlocks.RED_SANDSTONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.RED_SANDSTONE.get());
		addShapedRecipe(ModBlocks.RED_SANDSTONE_WALL.newItemStack(6), "xxx", "xxx", 'x', ModBlocks.RED_SANDSTONE.newItemStack());

		if (ConfigBlocksItems.enableFences) {
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

		addShapedRecipe(ModBlocks.END_BRICK_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.END_BRICKS.get());
		addShapedRecipe(ModBlocks.END_BRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.END_BRICKS.get());
		addShapedRecipe(ModBlocks.END_BRICK_WALL.newItemStack(6), "xxx", "xxx", 'x', ModBlocks.END_BRICKS.newItemStack());

		addShapedRecipe(ModBlocks.PURPUR_BLOCK.newItemStack(4), "xx", "xx", 'x', ModItems.CHORUS_FRUIT_POPPED.get());
		addShapedRecipe(ModBlocks.PURPUR_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.PURPUR_BLOCK.get());
		addShapedRecipe(ModBlocks.PURPUR_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.PURPUR_BLOCK.get());
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

		for (int i = 0; i < ModBlocks.BEDS.length; i++) {
			int j = i == 14 ? 15 : i;
			addShapedRecipe(ModBlocks.BEDS[i].newItemStack(1), "###", "XXX", '#', new ItemStack(Blocks.wool, 1, j), 'X', "plankWood");
			if (i > 0) {
				addShapelessRecipe(ModBlocks.BEDS[i].newItemStack(1), ModBlocks.BEDS[0].get(), ore_dyes[~j & 15]);
			}
		}
		addShapelessRecipe(new ItemStack(Items.bed, 1), ModBlocks.BEDS[0].newItemStack(), ore_dyes[1]);

		addShapedRecipe(ModBlocks.MAGMA.newItemStack(), "xx", "xx", 'x', new ItemStack(Items.magma_cream));

		addShapedRecipe(ModBlocks.RED_NETHERBRICK.newItemStack(), "xi", "ix", 'x', Items.nether_wart, 'i', "ingotBrickNether");
		addShapedRecipe(ModBlocks.RED_NETHERBRICK.newItemStack(1, 2), "x", "x", 'x', new ItemStack(Blocks.stone_slab, 1, 6));
		addSmelting(new ItemStack(Blocks.nether_brick, 1, 1), ModBlocks.RED_NETHERBRICK.newItemStack(), .1F);
		addShapedRecipe(ModBlocks.RED_NETHERBRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.RED_NETHERBRICK.newItemStack());
		addShapedRecipe(ModBlocks.RED_NETHERBRICK_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.RED_NETHERBRICK.newItemStack());
		addShapedRecipe(ModBlocks.RED_NETHER_BRICK_WALL.newItemStack(6), "xxx", "xxx", 'x', ModBlocks.RED_NETHERBRICK.newItemStack());

		addShapedRecipe(ModBlocks.NETHER_WART.newItemStack(), "xxx", "xxx", "xxx", 'x', Items.nether_wart);

		addShapedRecipe(ModBlocks.BONE.newItemStack(), "xxx", "xxx", "xxx", 'x', new ItemStack(Items.dye, 1, 15));
		addShapelessRecipe(new ItemStack(Items.dye, 9, 15), ModBlocks.BONE.newItemStack());

		for (int i = 0; i < ore_dyes.length; i++) {
			int dye = ~i & 15;
			addShapelessRecipe(ModBlocks.CONCRETE_POWDER.newItemStack(8, i),
					ore_dyes[dye], new ItemStack(Blocks.sand, 1, 0), new ItemStack(Blocks.sand, 1, 0), new ItemStack(Blocks.sand, 1, 0),
					new ItemStack(Blocks.sand, 1, 0), Blocks.gravel, Blocks.gravel, Blocks.gravel, Blocks.gravel);
			addSmelting(new ItemStack(Blocks.stained_hardened_clay, 1, i), ModBlocks.TERRACOTTA[i].newItemStack(), 0.1F);
		}

		if (!OreDictionary.getOres("nuggetIron").isEmpty()) {
			addShapedRecipe(ModBlocks.LANTERN.newItemStack(), "xxx", "xix", "xxx", 'x', "nuggetIron", 'i', Blocks.torch);
		} else {
			addShapedRecipe(ModBlocks.LANTERN.newItemStack(), "i", "x", 'x', "ingotIron", 'i', Blocks.torch);
		}

		addShapedRecipe(ModBlocks.BARREL.newItemStack(), "xsx", "x x", "xsx", 'x', "plankWood", 's', "slabWood");

		addShapedRecipe(ModBlocks.BLUE_ICE.newItemStack(), "xxx", "xxx", "xxx", 'x', Blocks.packed_ice);

		addShapedRecipe(ModBlocks.SMOKER.newItemStack(), " l ", "lxl", " l ", 'x', Blocks.furnace, 'l', "logWood");

		addShapedRecipe(ModBlocks.BLAST_FURNACE.newItemStack(), "iii", "ixi", "sss", 'x', Blocks.furnace, 'i', "ingotIron", 's', ConfigBlocksItems.enableSmoothStone ? ModBlocks.SMOOTH_STONE.get() : Blocks.stone);

		addSmelting(ModBlocks.ANCIENT_DEBRIS.get(), ModItems.NETHERITE_SCRAP.newItemStack(), 2F);
		addShapelessRecipe(ModItems.NETHERITE_INGOT.newItemStack(), ModItems.NETHERITE_SCRAP.newItemStack(), ModItems.NETHERITE_SCRAP.newItemStack(),
				ModItems.NETHERITE_SCRAP.newItemStack(), ModItems.NETHERITE_SCRAP.newItemStack(), "ingotGold", "ingotGold", "ingotGold", "ingotGold");
		addShapedRecipe(ModBlocks.NETHERITE_BLOCK.newItemStack(), "xxx", "xxx", "xxx", 'x', ModItems.NETHERITE_INGOT.get());
		addShapedRecipe(ModItems.NETHERITE_INGOT.newItemStack(9), "x", 'x', ModBlocks.NETHERITE_BLOCK.get());

		addSmelting(ModBlocks.NETHER_GOLD_ORE.newItemStack(), new ItemStack(Items.gold_ingot), .1F);

		addSmelting(new ItemStack(Blocks.stone), ModBlocks.SMOOTH_STONE.newItemStack(), .1F);
		if (ModsList.BLUEPOWER.isLoaded()) {
			Item stoneTile = GameRegistry.findItem("bluepower", "stone_tile");
			if (stoneTile != null) {
				addShapedRecipe(new ItemStack(stoneTile, 4), "xx", 'x', ModBlocks.SMOOTH_STONE.newItemStack());
			}
		}

		addSmelting(new ItemStack(Blocks.sandstone, 1, 0), ModBlocks.SMOOTH_SANDSTONE.newItemStack(), .1F);
		addShapedRecipe(ModBlocks.SMOOTH_SANDSTONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.SMOOTH_SANDSTONE.newItemStack());
		addShapedRecipe(ModBlocks.SMOOTH_SANDSTONE_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.SMOOTH_SANDSTONE.newItemStack());
		addSmelting(ModBlocks.RED_SANDSTONE.newItemStack(), ModBlocks.SMOOTH_RED_SANDSTONE.newItemStack(), .1F);
		addShapedRecipe(ModBlocks.SMOOTH_RED_SANDSTONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.SMOOTH_RED_SANDSTONE.newItemStack());
		addShapedRecipe(ModBlocks.SMOOTH_RED_SANDSTONE_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.SMOOTH_RED_SANDSTONE.newItemStack());


		addSmelting(new ItemStack(Blocks.quartz_block, 1, 0), ModBlocks.SMOOTH_QUARTZ.newItemStack(), .1F);
		addShapedRecipe(ModBlocks.SMOOTH_QUARTZ_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.SMOOTH_QUARTZ.newItemStack());
		addShapedRecipe(ModBlocks.SMOOTH_QUARTZ_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.SMOOTH_QUARTZ.newItemStack());

		addShapedRecipe(ModBlocks.QUARTZ_BRICKS.newItemStack(4), "xx", "xx", 'x', new ItemStack(Blocks.quartz_block, 1, 0));

		addShapelessRecipe(ModItems.DYE.newItemStack(), ModBlocks.LILY_OF_THE_VALLEY.newItemStack());
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 1), ModBlocks.CORNFLOWER.newItemStack());
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 3), ModBlocks.WITHER_ROSE.newItemStack());
		addShapelessRecipe(ModItems.DYE.newItemStack(), new ItemStack(Items.dye, 1, 15));
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 1), new ItemStack(Items.dye, 1, 4));
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 2), new ItemStack(Items.dye, 1, 3));
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 3), new ItemStack(Items.dye, 1, 0));

		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1), "xxx", "xxx", "xxx", 'x', ModItems.COPPER_INGOT.newItemStack());
		addShapedRecipe(ModItems.COPPER_INGOT.newItemStack(9), "x", 'x', ModBlocks.COPPER_BLOCK.newItemStack());
		addShapedRecipe(ModItems.COPPER_INGOT.newItemStack(9), "x", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 8));

		addSmelting(ModBlocks.COPPER_ORE.newItemStack(), ModItems.COPPER_INGOT.newItemStack(), .7F);

		//Copper block to cut copper block
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 4), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1));
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 5), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 6), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 7), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 3));

		//Waxed copper block to waxed cut copper block
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 12), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 8));
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 13), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 9));
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 14), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 10));
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(4, 15), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 11));

		//Cut copper to cut copper slab
		addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 4));
		addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 5));
		addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 2), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 6));
		addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 3), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 7));

		//Waxed cut copper to waxed cut copper slab
		addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 4), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 12));
		addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 5), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 13));
		addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 6), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 14));
		addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 7), "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 15));

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
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 8), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1));
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 9), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 1));
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 10), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 2));
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 11), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 3));
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 12), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 4));
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 13), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 5));
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 14), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 6));
				addShapelessRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1, 15), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 7));

				addShapelessRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 4), waxString, ModBlocks.CUT_COPPER_SLAB.newItemStack(1));
				addShapelessRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 5), waxString, ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 1));
				addShapelessRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 6), waxString, ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 2));
				addShapelessRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 7), waxString, ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 3));

				addShapelessRecipe(ModBlocks.WAXED_CUT_COPPER_STAIRS.newItemStack(), waxString, ModBlocks.CUT_COPPER_STAIRS.newItemStack());
				addShapelessRecipe(ModBlocks.WAXED_EXPOSED_CUT_COPPER_STAIRS.newItemStack(), waxString, ModBlocks.EXPOSED_CUT_COPPER_STAIRS.newItemStack());
				addShapelessRecipe(ModBlocks.WAXED_WEATHERED_CUT_COPPER_STAIRS.newItemStack(), waxString, ModBlocks.WEATHERED_CUT_COPPER_STAIRS.newItemStack());
				addShapelessRecipe(ModBlocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS.newItemStack(), waxString, ModBlocks.OXIDIZED_CUT_COPPER_STAIRS.newItemStack());
			}
		}

		addSmelting(ModBlocks.COBBLED_DEEPSLATE.get(), ModBlocks.DEEPSLATE.newItemStack(), 0.1F);
		addSmelting(ModBlocks.DEEPSLATE_BRICKS.newItemStack(), ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 1), 0.1F);
		addSmelting(ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2), ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 3), 0.1F);

		addShapedRecipe(ModBlocks.POLISHED_DEEPSLATE.newItemStack(4), "xx", "xx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICKS.newItemStack(4), "xx", "xx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICKS.newItemStack(4, 2), "xx", "xx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 4), "x", "x", 'x', ModBlocks.DEEPSLATE_SLAB.newItemStack());

		addShapedRecipe(ModBlocks.COBBLED_DEEPSLATE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack());
		addShapedRecipe(ModBlocks.POLISHED_DEEPSLATE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_TILE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.DEEPSLATE_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.DEEPSLATE_WALL.newItemStack(6), "xxx", "xxx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_WALL.newItemStack(6, 1), "xxx", "xxx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_WALL.newItemStack(6), "xxx", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack());
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_WALL.newItemStack(6, 1), "xxx", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2));

		// Mud Recipes
		addShapelessRecipe(ModBlocks.MUDDY_MANGROVE_ROOTS.newItemStack(1), ModBlocks.MUD.newItemStack(), ModBlocks.MANGROVE_ROOTS.newItemStack());

		// VANILLA
		addShapelessRecipe(ModBlocks.PACKED_MUD.newItemStack(1, 0), ModBlocks.MUD.newItemStack(1), new ItemStack(Items.wheat, 1));
		addShapedRecipe(ModBlocks.PACKED_MUD.newItemStack(4, 1), "xx", "xx", 'x', ModBlocks.PACKED_MUD.newItemStack(1, 0));

		addShapedRecipe(ModBlocks.MUD_BRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.PACKED_MUD.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.MUD_BRICK_SLAB.newItemStack(6, 0), "xxx", 'x', ModBlocks.PACKED_MUD.newItemStack(1, 1));
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

		for (int i = EntityNewBoat.Type.VALUES.length - 1; i >= 0; i--) {
			addShapedRecipe(new ItemStack(i == 0 && ConfigBlocksItems.replaceOldBoats ? Items.boat : ModItems.BOATS[i].get(), 1),
					(ConfigBlocksItems.replaceOldBoats ? "x x" : "xyx"), "xxx", 'x', i == 0 ? "plankWood" : new ItemStack(Blocks.planks, 1, i), 'y', new ItemStack(Items.wooden_shovel, 1));
		}
		if (!ConfigBlocksItems.replaceOldBoats) {
			addShapelessRecipe(new ItemStack(Items.boat), ModItems.BOATS[0].get());
			addShapelessRecipe(ModItems.BOATS[0].newItemStack(), Items.wooden_shovel, Items.boat);
		}
		for (int i = EntityNewBoat.Type.VALUES.length - 1; i >= 0; i--) {
			addShapedRecipe(ModItems.CHEST_BOATS[i].newItemStack(), "c", "b", 'b', i == 0 && ConfigBlocksItems.replaceOldBoats ? Items.boat : ModItems.BOATS[i].get(), 'c', Blocks.chest);
		}

		addShapedRecipe(ModBlocks.SHULKER_BOX.newItemStack(), "x", "c", "x", 'x', ModItems.SHULKER_SHELL.newItemStack(), 'c', new ItemStack(Blocks.chest));
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

		addShapedRecipe(ModBlocks.COMPOSTER.newItemStack(), "S S", "S S", "SSS", 'S', "slabWood");

		addShapedRecipe(ModBlocks.CARTOGRAPHY_TABLE.newItemStack(), "pp", "PP", "PP", 'P', "plankWood", 'p', new ItemStack(Items.paper, 1, 0));

		addShapedRecipe(ModBlocks.LOOM.newItemStack(), "SS", "PP", 'P', "plankWood", 'S', new ItemStack(Items.string, 1, 0));

		addShapedRecipe(ModBlocks.AMETHYST_BLOCK.newItemStack(), "AA", "AA", 'A', "gemAmethyst");
		addShapedRecipe(ModBlocks.TINTED_GLASS.newItemStack(2, 0), " A ", "AGA", " A ", 'A', "gemAmethyst", 'G', "blockGlassColorless");

		addShapedRecipe(ModBlocks.TARGET.newItemStack(), " R ", "RHR", " R ", 'R', "dustRedstone", 'H', Blocks.hay_block);

		addShapedRecipe(ModBlocks.OBSERVER.newItemStack(), "CCC", "RRQ", "CCC", 'R', "dustRedstone", 'C', "cobblestone", 'Q', "gemQuartz");

		addShapedRecipe(ModBlocks.HONEY_BLOCK.newItemStack(), "HH", "HH", 'H', ModItems.HONEY_BOTTLE.get());
		addShapedRecipe(ModBlocks.HONEYCOMB_BLOCK.newItemStack(), "HH", "HH", 'H', "materialHoneycomb");
		addShapedRecipe(ModBlocks.BEEHIVE.newItemStack(), "WWW", "HHH", "WWW", 'W', "plankWood", 'H', "materialHoneycomb");

		addShapedRecipe(ModBlocks.CHAIN.newItemStack(), "N", "G", "N", 'N', "nuggetIron", 'G', "ingotIron");

		addShapedRecipe(ModBlocks.BLACKSTONE.newItemStack(4, 1), "xx", "xx", 'x', ModBlocks.BLACKSTONE.newItemStack());
		addShapedRecipe(ModBlocks.BLACKSTONE.newItemStack(4, 2), "xx", "xx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.BLACKSTONE.newItemStack(1, 4), "x", "x", 'x', ModBlocks.BLACKSTONE_SLAB.newItemStack(1, 1));

		addSmelting(ModBlocks.BLACKSTONE.newItemStack(1, 2), ModBlocks.BLACKSTONE.newItemStack(1, 3), 0.1F);

		addShapedRecipe(ModBlocks.BLACKSTONE_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack());
		addShapedRecipe(ModBlocks.BLACKSTONE_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.BLACKSTONE_SLAB.newItemStack(6, 2), "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 2));

		addShapedRecipe(ModBlocks.BLACKSTONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack());
		addShapedRecipe(ModBlocks.POLISHED_BLACKSTONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.POLISHED_BLACKSTONE_BRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 2));

		addShapedRecipe(ModBlocks.BLACKSTONE_WALL.newItemStack(6), "xxx", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack());
		addShapedRecipe(ModBlocks.BLACKSTONE_WALL.newItemStack(6, 1), "xxx", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.BLACKSTONE_WALL.newItemStack(6, 2), "xxx", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 2));

		addShapedRecipe(ModBlocks.POLISHED_BLACKSTONE_BUTTON.newItemStack(1), "x", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.POLISHED_BLACKSTONE_PRESSURE_PLATE.newItemStack(1), "xx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1));

		addShapedRecipe(ModBlocks.BASALT.newItemStack(4, 1), "xx", "xx", 'x', ModBlocks.BASALT.newItemStack());
		addSmelting(ModBlocks.BASALT.newItemStack(), ModBlocks.SMOOTH_BASALT.newItemStack(), 0.1F);

		if (Loader.isModLoaded("lotr")) {//LoTR ores and ingots stupidly lack dictionary entries; let's add them so the code below can find them.
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

		registerModdedDeepslateOres();

		ItemStack result = null;
		if (ModItems.COPPER_INGOT.isEnabled()) {
			result = ModItems.COPPER_INGOT.newItemStack();
		} else if (!OreDictionary.getOres("ingotCopper").isEmpty()) {
			result = OreDictionary.getOres("ingotCopper").get(0);
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

		registerModdedRawOres();
	}

	public static void registerModdedDeepslateOres() {
		if (ModBlocks.DEEPSLATE_COPPER_ORE.isEnabled()) {
			DeepslateOreRegistry.addOreByOreDict("oreCopper", ModBlocks.DEEPSLATE_COPPER_ORE.get());
			registerOre("oreCopper", ModBlocks.DEEPSLATE_COPPER_ORE.newItemStack());
			registerOre("oreDeepslateCopper", ModBlocks.DEEPSLATE_COPPER_ORE.newItemStack());
		}
		if (ModBlocks.DEEPSLATE_IRON_ORE.isEnabled()) {
			DeepslateOreRegistry.addOreByOreDict("oreIron", ModBlocks.DEEPSLATE_IRON_ORE.get());
			registerOre("oreIron", ModBlocks.DEEPSLATE_IRON_ORE.newItemStack());
			registerOre("oreDeepslateIron", ModBlocks.DEEPSLATE_IRON_ORE.newItemStack());
		}
		if (ModBlocks.DEEPSLATE_GOLD_ORE.isEnabled()) {
			DeepslateOreRegistry.addOreByOreDict("oreGold", ModBlocks.DEEPSLATE_GOLD_ORE.get());
			registerOre("oreGold", ModBlocks.DEEPSLATE_GOLD_ORE.newItemStack());
			registerOre("oreDeepslateGold", ModBlocks.DEEPSLATE_GOLD_ORE.newItemStack());
		}

		if (ModBlocks.MODDED_DEEPSLATE_ORE.isEnabled() && ConfigBlocksItems.enableDeepslateOres) {
			//Insert alternate Mythril spelling to list. Yes I know "mithril" is technically the primary spelling but "mythril" is used by most mods, so "mithril" is secondary to it here.
			for (int i = 0; i < BlockModdedDeepslateOre.ores.length; i++) {
				String type = BlockModdedDeepslateOre.ores[i];
				//This can run post-load, so don't register multiple tags if the deepslate ore already got one.
				//We have to do this since it is impossible to remove OreDictionary entries.
				//Well it's probably *technically* possible but I don't want to do it, PR an OD remover if you need EFR to do it.
				//For now just restart your game to clear entries that would no longer get a tag.
				for (int j = 0; j < 1; j++) { //If it's mythril, we'll run this once more, changing the spelling to mithril to account for both tags.
					if (!OreDictionary.getOres(type).isEmpty()) { //Make sure an ore is present.
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
			if (ModBlocks.DEEPSLATE_CERTUS_QUARTZ_ORE.isEnabled()) {
				registerOre("oreCertus", ModBlocks.DEEPSLATE_CERTUS_QUARTZ_ORE.newItemStack());
				registerOre("oreDeepslateCertus", ModBlocks.DEEPSLATE_CERTUS_QUARTZ_ORE.newItemStack());
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.AE2_CERTUS_QUARTZ_ORE.get(), ModBlocks.DEEPSLATE_CERTUS_QUARTZ_ORE.get());
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.AE2_CHARGED_CERTUS_QUARTZ_ORE.get(), 0, ModBlocks.DEEPSLATE_CERTUS_QUARTZ_ORE.get(), 1);
			}
			if (ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.isEnabled()) {
				registerOre("oreCinnabar", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack());
				registerOre("oreDeepslateCinnabar", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack());
				registerOre("oreInfusedAir", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, 1));
				registerOre("oreDeepslateInfusedAir", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, 1));
				registerOre("oreInfusedFire", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, 2));
				registerOre("oreDeepslateInfusedFire", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, 2));
				registerOre("oreInfusedWater", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, 3));
				registerOre("oreDeepslateInfusedWater", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, 3));
				registerOre("oreInfusedEarth", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, 4));
				registerOre("oreDeepslateInfusedEarth", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, 4));
				registerOre("oreInfusedOrder", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, 5));
				registerOre("oreDeepslateInfusedOrder", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, 5));
				registerOre("oreInfusedEntropy", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, 6));
				registerOre("oreDeepslateInfusedEntropy", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, 6));
				registerOre("oreAmber", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, 7));
				registerOre("oreDeepslateAmber", ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, 7));
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.THAUMCRAFT_ORE.get(), ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.get());
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.THAUMCRAFT_ORE.get(), 1, ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.get(), 1);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.THAUMCRAFT_ORE.get(), 2, ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.get(), 2);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.THAUMCRAFT_ORE.get(), 3, ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.get(), 3);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.THAUMCRAFT_ORE.get(), 4, ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.get(), 4);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.THAUMCRAFT_ORE.get(), 5, ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.get(), 5);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.THAUMCRAFT_ORE.get(), 6, ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.get(), 6);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.THAUMCRAFT_ORE.get(), 7, ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.get(), 7);
			}
			if (ModBlocks.DEEPSLATE_PROJRED_ORE.isEnabled()) {
				registerOre("oreDeepslateRuby", ModBlocks.DEEPSLATE_PROJRED_ORE.newItemStack());
				registerOre("oreRuby", ModBlocks.DEEPSLATE_PROJRED_ORE.newItemStack());
				registerOre("oreDeepslateSapphire", ModBlocks.DEEPSLATE_PROJRED_ORE.newItemStack(1, 1));
				registerOre("oreSapphire", ModBlocks.DEEPSLATE_PROJRED_ORE.newItemStack(1, 1));
				registerOre("oreDeepslatePeridot", ModBlocks.DEEPSLATE_PROJRED_ORE.newItemStack(1, 2));
				registerOre("orePeridot", ModBlocks.DEEPSLATE_PROJRED_ORE.newItemStack(1, 2));
				registerOre("oreDeepslateElectrotine", ModBlocks.DEEPSLATE_PROJRED_ORE.newItemStack(1, 3));
				registerOre("oreElectrotine", ModBlocks.DEEPSLATE_PROJRED_ORE.newItemStack(1, 3));
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.PROJECT_RED_ORE.get(), ModBlocks.DEEPSLATE_PROJRED_ORE.get());
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.PROJECT_RED_ORE.get(), 1, ModBlocks.DEEPSLATE_PROJRED_ORE.get(), 1);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.PROJECT_RED_ORE.get(), 2, ModBlocks.DEEPSLATE_PROJRED_ORE.get(), 2);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.PROJECT_RED_ORE.get(), 6, ModBlocks.DEEPSLATE_PROJRED_ORE.get(), 3);
			}
			if (ModBlocks.DEEPSLATE_BLUEPOWER_ORE.isEnabled()) {
				registerOre("oreDeepslateTeslatite", ModBlocks.DEEPSLATE_BLUEPOWER_ORE.newItemStack());
				registerOre("oreTeslatite", ModBlocks.DEEPSLATE_BLUEPOWER_ORE.newItemStack());
				registerOre("oreDeepslateRuby", ModBlocks.DEEPSLATE_BLUEPOWER_ORE.newItemStack(1, 1));
				registerOre("oreRuby", ModBlocks.DEEPSLATE_BLUEPOWER_ORE.newItemStack(1, 1));
				registerOre("oreDeepslateSapphire", ModBlocks.DEEPSLATE_BLUEPOWER_ORE.newItemStack(1, 2));
				registerOre("oreSapphire", ModBlocks.DEEPSLATE_BLUEPOWER_ORE.newItemStack(1, 2));
				registerOre("oreDeepslateAmethyst", ModBlocks.DEEPSLATE_BLUEPOWER_ORE.newItemStack(1, 3));
				registerOre("oreAmethyst", ModBlocks.DEEPSLATE_BLUEPOWER_ORE.newItemStack(1, 3));
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.BP_TESLATITE_ORE.get(), ModBlocks.DEEPSLATE_BLUEPOWER_ORE.get());
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.BP_RUBY_ORE.get(), 1, ModBlocks.DEEPSLATE_BLUEPOWER_ORE.get(), 1);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.BP_SAPPHIRE_ORE.get(), 2, ModBlocks.DEEPSLATE_BLUEPOWER_ORE.get(), 2);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.BP_AMETHYST_ORE.get(), 3, ModBlocks.DEEPSLATE_BLUEPOWER_ORE.get(), 3);
			}
			if (ModBlocks.DEEPSLATE_BOP_ORE.isEnabled()) {
				registerOre("oreDeepslateRuby", ModBlocks.DEEPSLATE_BOP_ORE.newItemStack());
				registerOre("oreRuby", ModBlocks.DEEPSLATE_BOP_ORE.newItemStack());
				registerOre("oreDeepslatePeridot", ModBlocks.DEEPSLATE_BOP_ORE.newItemStack(1, 1));
				registerOre("orePeridot", ModBlocks.DEEPSLATE_BOP_ORE.newItemStack(1, 1));
				registerOre("oreDeepslateTopaz", ModBlocks.DEEPSLATE_BOP_ORE.newItemStack(1, 2));
				registerOre("oreTopaz", ModBlocks.DEEPSLATE_BOP_ORE.newItemStack(1, 2));
				registerOre("oreDeepslateTanzanite", ModBlocks.DEEPSLATE_BOP_ORE.newItemStack(1, 3));
				registerOre("oreTanzanite", ModBlocks.DEEPSLATE_BOP_ORE.newItemStack(1, 3));
				registerOre("oreDeepslateMalachite", ModBlocks.DEEPSLATE_BOP_ORE.newItemStack(1, 4));
				registerOre("oreMalachite", ModBlocks.DEEPSLATE_BOP_ORE.newItemStack(1, 4));
				registerOre("oreDeepslateSapphire", ModBlocks.DEEPSLATE_BOP_ORE.newItemStack(1, 5));
				registerOre("oreSapphire", ModBlocks.DEEPSLATE_BOP_ORE.newItemStack(1, 5));
				registerOre("oreDeepslateAmber", ModBlocks.DEEPSLATE_BOP_ORE.newItemStack(1, 6));
				registerOre("oreAmber", ModBlocks.DEEPSLATE_BOP_ORE.newItemStack(1, 6));
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.BOP_GEM_ORE.get(), 2, ModBlocks.DEEPSLATE_BOP_ORE.get(), 0);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.BOP_GEM_ORE.get(), 4, ModBlocks.DEEPSLATE_BOP_ORE.get(), 1);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.BOP_GEM_ORE.get(), 6, ModBlocks.DEEPSLATE_BOP_ORE.get(), 2);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.BOP_GEM_ORE.get(), 8, ModBlocks.DEEPSLATE_BOP_ORE.get(), 3);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.BOP_GEM_ORE.get(), 10, ModBlocks.DEEPSLATE_BOP_ORE.get(), 4);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.BOP_GEM_ORE.get(), 12, ModBlocks.DEEPSLATE_BOP_ORE.get(), 5);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.BOP_GEM_ORE.get(), 14, ModBlocks.DEEPSLATE_BOP_ORE.get(), 6);
			}
			if (ModBlocks.DEEPSLATE_DRACONIUM_ORE.isEnabled()) {
				registerOre("oreDeepslateDraconium", ModBlocks.DEEPSLATE_DRACONIUM_ORE.newItemStack());
				registerOre("oreDraconium", ModBlocks.DEEPSLATE_DRACONIUM_ORE.newItemStack());
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.DRACONIUM_ORE.get(), ModBlocks.DEEPSLATE_DRACONIUM_ORE.get());
			}
			if (ModBlocks.DEEPSLATE_AM2_ORE.isEnabled()) {
				registerOre("oreDeepslateVinteum", ModBlocks.DEEPSLATE_AM2_ORE.newItemStack());
				registerOre("oreVinteum", ModBlocks.DEEPSLATE_AM2_ORE.newItemStack());
				registerOre("oreDeepslateChimerite", ModBlocks.DEEPSLATE_AM2_ORE.newItemStack(1, 1));
				registerOre("oreChimerite", ModBlocks.DEEPSLATE_AM2_ORE.newItemStack(1, 1));
				registerOre("oreDeepslateBlueTopaz", ModBlocks.DEEPSLATE_AM2_ORE.newItemStack(1, 2));
				registerOre("oreBlueTopaz", ModBlocks.DEEPSLATE_AM2_ORE.newItemStack(1, 2));
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.ARS_MAGICA_2_ORE.get(), ModBlocks.DEEPSLATE_AM2_ORE.get());
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.ARS_MAGICA_2_ORE.get(), 1, ModBlocks.DEEPSLATE_AM2_ORE.get(), 1);
				DeepslateOreRegistry.addOre(ExternalContent.Blocks.ARS_MAGICA_2_ORE.get(), 2, ModBlocks.DEEPSLATE_AM2_ORE.get(), 2);
			}
		}
	}

	public static void unregisterModdedRawOres() {
		for (int i = 0; i < ItemModdedRawOre.ores.length; i++) {
			removeFirstRecipeFor(ModItems.MODDED_RAW_ORE.get(), i);
			removeFirstRecipeFor(ModBlocks.MODDED_RAW_ORE_BLOCK.get(), i);
			ItemStack stack = ModItems.MODDED_RAW_ORE.newItemStack(1, i);
			Iterator<ItemStack> iterator = (Iterator<ItemStack>) FurnaceRecipes.smelting().getSmeltingList().keySet().iterator();
			while (iterator.hasNext()) {
				ItemStack smeltingInput = iterator.next();
				if (stack.getItem() == smeltingInput.getItem() && stack.getItemDamage() == smeltingInput.getItemDamage()) {
					iterator.remove();
				}
			}
		}
	}

	public static void registerModdedRawOres() {
		//Insert alternate Mythril spelling to list. Yes I know "mithril" is technically the primary spelling but "mythril" is used by most mods, so "mithril" is secondary to it here.
		for (int i = 0; i < ItemModdedRawOre.ores.length; i++) {
			String type = ItemModdedRawOre.ores[i];
			//This can run post-load, so don't register multiple tags if the raw ore already got one.
			//We have to do this since it is impossible to remove OreDictionary entries.
			//Well it's probably *technically* possible but I don't want to do it, PR an OD remover if you need EFR to do it.
			//For now just restart your game to clear entries that would no longer get a tag.
			for (int j = 0; j < 1; j++) { //If it's mythril, we'll run this once more, changing the spelling to mithril to account for both tags.
				if (!OreDictionary.getOres(type).isEmpty() && !OreDictionary.getOres(type.replace("ingot", "ore")).isEmpty()) { //Make sure an ingot AND ore is present
					registerOre(type.replace("ingot", "raw"), ModItems.MODDED_RAW_ORE.newItemStack(1, i));
					registerOre(type.replace("ingot", "blockRaw"), ModBlocks.MODDED_RAW_ORE_BLOCK.newItemStack(1, i));
					if (ConfigFunctions.registerRawItemAsOre) {
						registerOre(type.replace("ingot", "ore"), ModItems.MODDED_RAW_ORE.newItemStack(1, i));
					}
				}
				if (type.endsWith("Mythril")) {
					type = type.replace("Mythril", "Mithril"); //Redoes it once more for mithril spelling
					j = -1;
				}
			}
		}

		for (int i = 0; i < ItemModdedRawOre.ores.length; i++) {
			if (!OreDictionary.getOres(ItemModdedRawOre.ores[i]).isEmpty()) {
				addShapedRecipe(ModBlocks.MODDED_RAW_ORE_BLOCK.newItemStack(1, i), "xxx", "xxx", "xxx", 'x', ModItems.MODDED_RAW_ORE.newItemStack(1, i));
				addShapedRecipe(ModItems.MODDED_RAW_ORE.newItemStack(9, i), "x", 'x', ModBlocks.MODDED_RAW_ORE_BLOCK.newItemStack(1, i));
				addSmelting(ModItems.MODDED_RAW_ORE.newItemStack(1, i), OreDictionary.getOres(ItemModdedRawOre.ores[i]).get(0), 0.7F);
				if (ItemModdedRawOre.ores[i].endsWith("Mythril") && !OreDictionary.getOres("oreMithril").isEmpty()) {
					addSmelting(ModItems.MODDED_RAW_ORE.newItemStack(1, i), OreDictionary.getOres("oreMithril").get(0), 0.7F);
				}
			}
		}
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
		removeFirstRecipeFor(item, -1);
	}

	private static void removeFirstRecipeFor(Item item, int meta) {
		for (Object recipe : CraftingManager.getInstance().getRecipeList()) {
			if (recipe != null) {
				ItemStack stack = ((IRecipe) recipe).getRecipeOutput();
				if (stack != null && stack.getItem() == item && (meta == -1 || meta == stack.getItemDamage())) {
					CraftingManager.getInstance().getRecipeList().remove(recipe);
					return;
				}
			}
		}
	}
}