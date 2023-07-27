package ganymedes01.etfuturum.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.blocks.BlockStoneSlab2;
import ganymedes01.etfuturum.blocks.IDegradable;
import ganymedes01.etfuturum.configuration.configs.*;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.entities.EntityNewBoat;
import ganymedes01.etfuturum.items.ItemSuspiciousStew;
import ganymedes01.etfuturum.lib.EnumColour;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.recipes.crafting.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ModRecipes {

	public static final String[] ore_dyes = new String[]{"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"};
	public static final String[] dye_names = new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"};
	public static final String[] woodTypes = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"/*, "crimson", "warped"*/};

	public static void init() {
		if (ConfigBlocksItems.enableBanners) {
			RecipeSorter.register(Reference.MOD_ID + ".RecipeDuplicatePattern", RecipeDuplicatePattern.class, Category.SHAPELESS, "after:minecraft:shapeless");
			RecipeSorter.register(Reference.MOD_ID + ".RecipeAddPattern", RecipeAddPattern.class, Category.SHAPED, "after:minecraft:shaped");
		}
		RecipeSorter.register("etfuturum:shaped", ShapedEtFuturumRecipe.class, RecipeSorter.Category.SHAPED, "before:minecraft:shaped");
		RecipeSorter.register("etfuturum:shapeless", ShapelessEtFuturumRecipe.class, RecipeSorter.Category.SHAPELESS, "before:minecraft:shapeless");

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

		if (ConfigBlocksItems.enableFences) {
			removeFirstRecipeFor(Blocks.fence);
			removeFirstRecipeFor(Blocks.nether_brick_fence);
		}

		if (ConfigBlocksItems.replaceOldBoats && ConfigBlocksItems.enableNewBoats) {
			removeFirstRecipeFor(Items.boat);
			Items.boat.setTextureName("minecraft:oak_boat");
			Items.boat.setUnlocalizedName(Utils.getUnlocalisedName("oak_boat"));
		}

		if (ConfigFunctions.enableExtraBurnableBlocks) {
			Blocks.fire.setFireInfo(Blocks.fence_gate, 5, 20);
			Blocks.fire.setFireInfo(Blocks.fence, 5, 20);
			Blocks.fire.setFireInfo(Blocks.deadbush, 60, 100);
		}

		if (ConfigBlocksItems.enableDyedBeds) {
			Items.bed.setUnlocalizedName(Utils.getUnlocalisedName("red_bed"));
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
		OreDictionary.registerOre("doorIron", new ItemStack(Items.iron_door));
		OreDictionary.registerOre("buttonStone", new ItemStack(Blocks.stone_button));
		OreDictionary.registerOre("pressurePlateStone", new ItemStack(Blocks.stone_pressure_plate));
		OreDictionary.registerOre("pressurePlateIron", new ItemStack(Blocks.heavy_weighted_pressure_plate));
		OreDictionary.registerOre("pressurePlateGold", new ItemStack(Blocks.light_weighted_pressure_plate));
		OreDictionary.registerOre("bowlWood", new ItemStack(Items.bowl));

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

		for (int i = 0; i < 4; i++) {
			registerOre("logWood", ModBlocks.LOG_STRIPPED.newItemStack(1, i));
			registerOre("logWood", ModBlocks.LOG2_STRIPPED.newItemStack(1, i));
			registerOre("logWood", ModBlocks.WOOD_STRIPPED.newItemStack(1, i));
			registerOre("logWood", ModBlocks.WOOD2_STRIPPED.newItemStack(1, i));
			registerOre("logWood", ModBlocks.BARK.newItemStack(1, i));
			registerOre("logWood", ModBlocks.BARK2.newItemStack(1, i));
		}

		registerOre("dye", ModItems.DYE.newItemStack(1, OreDictionary.WILDCARD_VALUE));
		registerOre("dyeWhite", ModItems.DYE.newItemStack(1, 0));
		registerOre("dyeBlue", ModItems.DYE.newItemStack(1, 1));
		registerOre("dyeBrown", ModItems.DYE.newItemStack(1, 2));
		registerOre("dyeBlack", ModItems.DYE.newItemStack(1, 3));

		registerOre("oreCopper", ModBlocks.COPPER_ORE.newItemStack(1, 0));
		registerOre("ingotCopper", ModItems.COPPER_INGOT.newItemStack(1, 0));
		registerOre("blockCopper", ModBlocks.COPPER_BLOCK.newItemStack(1, 0));
		registerOre("blockCopperCut", ModBlocks.COPPER_BLOCK.newItemStack(1, 4));

		registerOre("cobblestone", ModBlocks.COBBLED_DEEPSLATE.newItemStack(1, 0));

		registerOre("rawCopper", ModItems.RAW_ORE.newItemStack(1, 0));
		registerOre("blockRawCopper", ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 0));
		registerOre("rawIron", ModItems.RAW_ORE.newItemStack(1, 1));
		registerOre("blockRawIron", ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 1));
		registerOre("rawGold", ModItems.RAW_ORE.newItemStack(1, 2));
		registerOre("blockRawGold", ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 2));

		registerOre("oreCopper", ModItems.RAW_ORE.newItemStack(1, 0)); //Todo: the registration of copper raw ore should be conditional because it is a configurable meta value, I may make the raw ore class itself provide enabled metas.
		registerOre("oreIron", ModItems.RAW_ORE.newItemStack(1, 1));
		registerOre("oreGold", ModItems.RAW_ORE.newItemStack(1, 2));

		registerOre("record", ModItems.PIGSTEP_RECORD.get());
		registerOre("record", ModItems.OTHERSIDE_RECORD.get());

		registerOre("gemAmethyst", ModItems.AMETHYST_SHARD.get());
		registerOre("blockGlassTinted", ModBlocks.TINTED_GLASS.newItemStack(1, 0));
	}

	private static void registerRecipes() {

		addShapedRecipe(ModBlocks.OLD_GRAVEL.newItemStack(4), "xy", "yx", 'x', ModBlocks.COARSE_DIRT.get(), 'y', Blocks.gravel);

		if (ConfigFunctions.enableStoneBrickRecipes) {
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.mossy_cobblestone), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.vine));
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.stonebrick, 1, 1), new ItemStack(Blocks.stonebrick), new ItemStack(Blocks.vine));
			GameRegistry.addShapedRecipe(new ItemStack(Blocks.stonebrick, 1, 3), "x", "x", 'x', new ItemStack(Blocks.stone_slab, 1, 5));
			GameRegistry.addSmelting(new ItemStack(Blocks.stonebrick), new ItemStack(Blocks.stonebrick, 1, 2), 0.0F);
		}

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
			String dictName = "stone" + StringUtils.capitalize(((BlockStoneSlab2) ModBlocks.STONE_SLAB_2.get()).metaBlocks[(i / 2) * 2]) + (i % 2 == 1 ? "Polished" : "");
			addShapedRecipe(ModBlocks.STONE_SLAB_2.newItemStack(6, i), "xxx", 'x', dictName);
			addShapedRecipe(new ItemStack(stone_stairs[i], 4), "x  ", "xx ", "xxx", 'x', dictName);
			if (i % 2 == 1) {
				addShapedRecipe(new ItemStack(ModBlocks.STONE_WALL_2.get(), 6, i == 5 ? 2 : i == 3 ? 1 : 0), "xxx", "xxx", 'x', ModBlocks.STONE.newItemStack(1, i));
			}
		}

		addShapedRecipe(ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 0), "xx", "xx", 'x', "shardPrismarine");
		addShapedRecipe(ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 1), "xxx", "xxx", "xxx", 'x', "shardPrismarine");
		addShapedRecipe(ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 2), "xxx", "xyx", "xxx", 'x', "shardPrismarine", 'y', "dyeBlack");
		addShapedRecipe(ModBlocks.SEA_LANTERN.newItemStack(), "xyx", "yyy", "xyx", 'x', "shardPrismarine", 'y', "crystalPrismarine");

		addShapedRecipe(ModBlocks.PRISMARINE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.PRISMARINE_STAIRS_BRICK.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 1));
		addShapedRecipe(ModBlocks.PRISMARINE_STAIRS_DARK.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.PRISMARINE_WALL.newItemStack(6, 0), "xxx", "xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 0));

		for (int i = 0; i < 3; i++) {
			addShapedRecipe(ModBlocks.PRISMARINE_SLAB.newItemStack(6, i), "xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack(1, i));
		}

		if (!EtFuturum.hasBotania) {
			addShapedRecipe(ModItems.PRISMARINE_SHARD.newItemStack(4), "xy", "zx", 'x', "gemQuartz", 'y', "dyeBlue", 'z', "dyeGreen");
			addShapedRecipe(ModItems.PRISMARINE_CRYSTALS.newItemStack(4), "xy", "yx", 'x', "gemQuartz", 'y', "dustGlowstone");
		}

		Block[] metaBlocks = new Block[]{Blocks.stone, Blocks.mossy_cobblestone, Blocks.stonebrick, Blocks.sandstone};
		for (int i = 0; i < metaBlocks.length; i++) {
			addShapedRecipe(ModBlocks.DOUBLE_STONE_SLAB.newItemStack(6, i), "xxx", 'x', new ItemStack(metaBlocks[i], 1, i != 0 ? i - 1 : i));
		}
		addShapedRecipe(new ItemStack(Blocks.stone_slab, 6, 0), "xxx", 'x', ModBlocks.SMOOTH_STONE.newItemStack(1, 0));


		addShapedRecipe(ModBlocks.STONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', new ItemStack(Blocks.stone, 1, 0));
		addShapedRecipe(ModBlocks.MOSSY_COBBLESTONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', new ItemStack(Blocks.mossy_cobblestone, 1, 0));
		addShapedRecipe(ModBlocks.MOSSY_STONE_BRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', new ItemStack(Blocks.stonebrick, 1, 1));

		int output = ConfigFunctions.enableDoorRecipeBuffs ? 3 : 1;
		for (int i = 0; i < ModBlocks.DOORS.length; i++) {
			addShapedRecipe(ModBlocks.DOORS[i].newItemStack(output), "xx", "xx", "xx", 'x', new ItemStack(Blocks.planks, 1, i + 1));
		}
		if (ConfigFunctions.enableDoorRecipeBuffs) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.wooden_door, 3), "xx", "xx", "xx", 'x', "plankWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.iron_door, 3), "xx", "xx", "xx", 'x', "ingotIron"));
		}

		addShapedRecipe(ModBlocks.IRON_TRAPDOOR.newItemStack(), "xx", "xx", 'x', "ingotIron");
		for (int i = 0; i < ModBlocks.TRAPDOORS.length; i++) {
			ItemStack planks = /* i < 5 ? */new ItemStack(Blocks.planks, 1, i + 1)/* : new ItemStack(ModBlocks.nether_planks, 1, i - 5) */;
			addShapedRecipe(ModBlocks.TRAPDOORS[i].newItemStack(2), "xxx", "xxx", 'x', planks);
		}

		for (int i = 0; i < ModItems.ITEM_SIGNS.length; i++) {
			ItemStack planks = /* i < 5 ? */new ItemStack(Blocks.planks, 1, i + 1)/* : new ItemStack(ModBlocks.nether_planks, 1, i - 5)*/;
			addShapedRecipe(ModItems.ITEM_SIGNS[i].newItemStack(3), "xxx", "xxx", " y ", 'x', planks, 'y', "stickWood");
		}

		addShapedRecipe(ModBlocks.RED_SANDSTONE.newItemStack(), "xx", "xx", 'x', new ItemStack(Blocks.sand, 1, 1));
		addShapedRecipe(ModBlocks.RED_SANDSTONE.newItemStack(1, 1), "x", "x", 'x', ModBlocks.RED_SANDSTONE_SLAB.newItemStack());
		addShapedRecipe(ModBlocks.RED_SANDSTONE.newItemStack(4, 2), "xx", "xx", 'x', ModBlocks.RED_SANDSTONE.newItemStack());
		addShapedRecipe(ModBlocks.RED_SANDSTONE_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.RED_SANDSTONE.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.RED_SANDSTONE_SLAB.newItemStack(6, 0), "xxx", 'x', ModBlocks.RED_SANDSTONE.newItemStack(1, OreDictionary.WILDCARD_VALUE));
		addShapedRecipe(ModBlocks.RED_SANDSTONE_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.RED_SANDSTONE.get());
		addShapedRecipe(ModBlocks.RED_SANDSTONE_WALL.newItemStack(6, 0), "xxx", "xxx", 'x', ModBlocks.RED_SANDSTONE.newItemStack(1, 0));

		if (ConfigBlocksItems.enableFences) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fence, 3), "xyx", "xyx", 'x', "plankWood", 'y', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.nether_brick_fence, 6), "xyx", "xyx", 'x', Blocks.nether_brick, 'y', "ingotBrickNether"));
		}

		for (int i = 0; i < ModBlocks.FENCES.length; i++) {
			addShapedRecipe(ModBlocks.FENCES[i].newItemStack(3), "xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, i + 1), 'y', "stickWood");
		}

		for (int i = 0; i < ModBlocks.FENCE_GATES.length; i++) {
			addShapedRecipe(new ItemStack(ModBlocks.FENCE_GATES[i].get()), "yxy", "yxy", 'x', new ItemStack(Blocks.planks, 1, i + 1), 'y', "stickWood");
		}

		for (EnumColour colour : EnumColour.values()) {
			addShapedRecipe(ModBlocks.BANNER.newItemStack(1, colour.getDamage()), "xxx", "xxx", " y ", 'x', new ItemStack(Blocks.wool, 1, colour.getDamage()), 'y', "stickWood");
		}
		GameRegistry.addRecipe(new RecipeDuplicatePattern());
		GameRegistry.addRecipe(new RecipeAddPattern());

		addShapedRecipe(ModItems.WOODEN_ARMORSTAND.newItemStack(), "xxx", " x ", "xyx", 'x', "stickWood", 'y', new ItemStack(Blocks.stone_slab));

		addShapedRecipe(ModItems.RABBIT_STEW.newItemStack(), " R ", "CPM", " B ", 'R', ModItems.RABBIT_COOKED.newItemStack(), 'C', Items.carrot, 'P', Items.baked_potato, 'M', Blocks.brown_mushroom, 'B', "bowlWood");
		addShapedRecipe(ModItems.RABBIT_STEW.newItemStack(), " R ", "CPD", " B ", 'R', ModItems.RABBIT_COOKED.newItemStack(), 'C', Items.carrot, 'P', Items.baked_potato, 'D', Blocks.red_mushroom, 'B', "bowlWood");
		addSmelting(ModItems.RABBIT_RAW.get(), ModItems.RABBIT_COOKED.newItemStack(), 0.35F);
		addShapedRecipe(new ItemStack(Items.leather), "xx", "xx", 'x', ModItems.RABBIT_HIDE.get());

		addShapelessRecipe(ModBlocks.SPONGE.newItemStack(), Blocks.sponge);
		addShapelessRecipe(new ItemStack(Blocks.sponge), ModBlocks.SPONGE.get()); //For recipes that want the vanilla sponge you can convert it
		addSmelting(ModBlocks.SPONGE.newItemStack(1, 1), ModBlocks.SPONGE.newItemStack(), 0.15F);

		addShapedRecipe(ModItems.BEETROOT_SOUP.newItemStack(), "xxx", "xxx", " y ", 'x', "cropBeetroot", 'y', "bowlWood");
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.dye, 1, 1), "cropBeetroot"));

		addShapedRecipe(ModBlocks.END_BRICK_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.END_BRICKS.get());
		addShapedRecipe(ModBlocks.END_BRICK_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.END_BRICKS.get());
		addShapedRecipe(ModBlocks.END_BRICK_WALL.newItemStack(6, 0), "xxx", "xxx", 'x', ModBlocks.END_BRICKS.newItemStack(1, 0));

		addShapedRecipe(ModBlocks.PURPUR_BLOCK.newItemStack(4), "xx", "xx", 'x', ModItems.CHORUS_FRUIT_POPPED.get());
		addShapedRecipe(ModBlocks.PURPUR_STAIRS.newItemStack(4), "x  ", "xx ", "xxx", 'x', ModBlocks.PURPUR_BLOCK.get());
		addShapedRecipe(ModBlocks.PURPUR_SLAB.newItemStack(6), "xxx", 'x', ModBlocks.PURPUR_BLOCK.get());
		addShapedRecipe(ModBlocks.PURPUR_PILLAR.newItemStack(), "x", "x", 'x', ModBlocks.PURPUR_SLAB.get());
		addShapedRecipe(ModBlocks.END_BRICKS.newItemStack(4), "xx", "xx", 'x', Blocks.end_stone);

		addSmelting(ModItems.CHORUS_FRUIT.newItemStack(), ModItems.CHORUS_FRUIT_POPPED.newItemStack(), 0.0F);
		addShapedRecipe(ModBlocks.END_ROD.newItemStack(), "x", "y", 'x', Items.blaze_rod, 'y', ModItems.CHORUS_FRUIT_POPPED.get());

		addShapelessRecipe(ModBlocks.CRYING_OBSIDIAN.newItemStack(), Blocks.obsidian, "gemLapis");

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
		addShapelessRecipe(new ItemStack(Items.bed, 1), new ItemStack(ModBlocks.BEDS[0].get()), ore_dyes[1]);

		addShapedRecipe(ModBlocks.MAGMA.newItemStack(), "xx", "xx", 'x', new ItemStack(Items.magma_cream));

		addShapedRecipe(ModBlocks.RED_NETHERBRICK.newItemStack(1, 0), "xi", "ix", 'x', Items.nether_wart, 'i', "ingotBrickNether");
		addShapedRecipe(ModBlocks.RED_NETHERBRICK.newItemStack(1, 2), "x", "x", 'x', new ItemStack(Blocks.stone_slab, 1, 6));
		addSmelting(new ItemStack(Blocks.nether_brick, 1, 1), ModBlocks.RED_NETHERBRICK.newItemStack(), .1F);
		addShapedRecipe(ModBlocks.RED_NETHERBRICK_STAIRS.newItemStack(4, 0), "x  ", "xx ", "xxx", 'x', ModBlocks.RED_NETHERBRICK.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.RED_NETHERBRICK_SLAB.newItemStack(6, 0), "xxx", 'x', ModBlocks.RED_NETHERBRICK.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.RED_NETHER_BRICK_WALL.newItemStack(6, 0), "xxx", "xxx", 'x', ModBlocks.RED_NETHERBRICK.newItemStack(1, 0));

		addShapedRecipe(ModBlocks.NETHER_WART.newItemStack(), "xxx", "xxx", "xxx", 'x', Items.nether_wart);
		addShapelessRecipe(new ItemStack(Items.nether_wart, 9), ModBlocks.NETHER_WART.get());

		addShapedRecipe(ModBlocks.BONE.newItemStack(), "xxx", "xxx", "xxx", 'x', new ItemStack(Items.dye, 1, 15));
		addShapelessRecipe(new ItemStack(Items.dye, 9, 15), ModBlocks.BONE.newItemStack());

		for (int i = 0; i < ore_dyes.length; i++) {
			int dye = ~i & 15;
			addShapelessRecipe(ModBlocks.CONCRETE_POWDER.newItemStack(8, i),
					ore_dyes[dye], new ItemStack(Blocks.sand, 1, 0), new ItemStack(Blocks.sand, 1, 0), new ItemStack(Blocks.sand, 1, 0),
					new ItemStack(Blocks.sand, 1, 0), Blocks.gravel, Blocks.gravel, Blocks.gravel, Blocks.gravel);
			addSmelting(new ItemStack(Blocks.stained_hardened_clay, 1, i), ModBlocks.TERRACOTTA[i].newItemStack(), 0.1F);
		}

		for (int i = 0; i < ModBlocks.BUTTONS.length; i++)
			addShapedRecipe(ModBlocks.BUTTONS[i].newItemStack(1), "x", 'x', new ItemStack(Blocks.planks, 1, i + 1));

		for (int i = 0; i < ModBlocks.PRESSURE_PLATES.length; i++)
			addShapedRecipe(ModBlocks.PRESSURE_PLATES[i].newItemStack(1), "xx", 'x', new ItemStack(Blocks.planks, 1, i + 1));

		for (int i = 0; i < 6; i++) {//TODO: Make this less complicated, I should just register these individually or make a more readable iterator...
			Block slog = i >= 4 ? ModBlocks.LOG2_STRIPPED.get() : ModBlocks.LOG_STRIPPED.get();
			Block log = i >= 4 ? Blocks.log2 : Blocks.log;
			Block sbark = i >= 4 ? ModBlocks.WOOD2_STRIPPED.get() : ModBlocks.WOOD_STRIPPED.get();
			Block bark = i >= 4 ? ModBlocks.BARK2.get() : ModBlocks.BARK.get();

			addShapedRecipe(new ItemStack(Blocks.planks, 4, i), "x", 'x', new ItemStack(slog, 1, i % 4));
			addShapedRecipe(new ItemStack(Blocks.planks, 4, i), "x", 'x', new ItemStack(sbark, 1, i % 4));

			addShapedRecipe(new ItemStack(Blocks.planks, 4, i), "x", 'x', new ItemStack(bark, 1, i % 4));
			addShapedRecipe(new ItemStack(bark, 3, i % 4), "xx", "xx", 'x', new ItemStack(log, 1, i % 4));
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
		if (EtFuturum.hasBluePower) {
			Item stoneTile = GameRegistry.findItem("bluepower", "stone_tile");
			if (stoneTile != null) {
				addShapedRecipe(new ItemStack(stoneTile, 4), "xx", 'x', ModBlocks.SMOOTH_STONE.newItemStack());
			}
		}

		addSmelting(new ItemStack(Blocks.sandstone, 1, 0), ModBlocks.SMOOTH_SANDSTONE.newItemStack(), .1F);
		addShapedRecipe(ModBlocks.SMOOTH_SANDSTONE_STAIRS.newItemStack(4, 0), "x  ", "xx ", "xxx", 'x', ModBlocks.SMOOTH_SANDSTONE.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.SMOOTH_SANDSTONE_SLAB.newItemStack(6, 0), "xxx", 'x', ModBlocks.SMOOTH_SANDSTONE.newItemStack(1, 0));
		addSmelting(ModBlocks.RED_SANDSTONE.newItemStack(1, 0), ModBlocks.SMOOTH_RED_SANDSTONE.newItemStack(), .1F);
		addShapedRecipe(ModBlocks.SMOOTH_RED_SANDSTONE_STAIRS.newItemStack(4, 0), "x  ", "xx ", "xxx", 'x', ModBlocks.SMOOTH_RED_SANDSTONE.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.SMOOTH_RED_SANDSTONE_SLAB.newItemStack(6, 0), "xxx", 'x', ModBlocks.SMOOTH_RED_SANDSTONE.newItemStack(1, 0));


		addSmelting(new ItemStack(Blocks.quartz_block, 1, 0), ModBlocks.SMOOTH_QUARTZ.newItemStack(), .1F);
		addShapedRecipe(ModBlocks.SMOOTH_QUARTZ_STAIRS.newItemStack(4, 0), "x  ", "xx ", "xxx", 'x', ModBlocks.SMOOTH_QUARTZ.newItemStack(1, 0));

		addShapedRecipe(ModBlocks.QUARTZ_BRICKS.newItemStack(4, 0), "xx", "xx", 'x', new ItemStack(Blocks.quartz_block, 1, 0));

		addShapelessRecipe(ModItems.DYE.newItemStack(1, 0), ModBlocks.LILY_OF_THE_VALLEY.newItemStack(1, 0));
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 1), ModBlocks.CORNFLOWER.newItemStack(1, 0));
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 3), ModBlocks.WITHER_ROSE.newItemStack(1, 0));
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 0), new ItemStack(Items.dye, 1, 15));
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 1), new ItemStack(Items.dye, 1, 4));
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 2), new ItemStack(Items.dye, 1, 3));
		addShapelessRecipe(ModItems.DYE.newItemStack(1, 3), new ItemStack(Items.dye, 1, 0));

		Block[] copper_stairs = new Block[]{ModBlocks.CUT_COPPER_STAIRS.get(), ModBlocks.EXPOSED_CUT_COPPER_STAIRS.get(), ModBlocks.WEATHERED_CUT_COPPER_STAIRS.get(), ModBlocks.OXIDIZED_CUT_COPPER_STAIRS.get(), ModBlocks.WAXED_CUT_COPPER_STAIRS.get(), ModBlocks.WAXED_EXPOSED_CUT_COPPER_STAIRS.get(), ModBlocks.WAXED_WEATHERED_CUT_COPPER_STAIRS.get(), ModBlocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS.get()};
		addShapedRecipe(ModBlocks.COPPER_BLOCK.newItemStack(1), "xxx", "xxx", "xxx", 'x', ModItems.COPPER_INGOT.newItemStack(1, 0));
		addShapedRecipe(ModItems.COPPER_INGOT.newItemStack(9), "x", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 0));
		addShapedRecipe(ModItems.COPPER_INGOT.newItemStack(9), "x", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 8));

		addSmelting(ModBlocks.COPPER_ORE.newItemStack(), ModItems.COPPER_INGOT.newItemStack(), .7F);
		for (int i = 0; i <= 7; i++) {//TODO: Maybe make this not a loop as well like the other ones I put a TODO on, although this loop adds a ton of recipes
			int j = i;
			if (i > 3)
				j = i + 4;
			if (j != 11) {
				addShapedRecipe(new ItemStack(ModBlocks.COPPER_BLOCK.get(), 4, j + 4), "xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, j));
			}
			if (i != 7) {
				addShapedRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(6, i), "xxx", 'x', new ItemStack(ModBlocks.COPPER_BLOCK.get(), 1, j + 4));
				addShapedRecipe(new ItemStack(copper_stairs[i], 4), "x  ", "xx ", "xxx", 'x', new ItemStack(ModBlocks.COPPER_BLOCK.get(), 1, j + 4));
			}

			boolean slimeball = true;
			for(int k = 0; k <= IDegradable.waxStrings.length; k++) {
				String waxString;
				if(k == IDegradable.waxStrings.length) {
					if(!slimeball) {
						break;
					}
					waxString = "slimeball";
				} else {
					waxString = IDegradable.waxStrings[k];
				}

				if(OreDictionary.doesOreNameExist(waxString)) {
					slimeball = false;
					addShapelessRecipe(new ItemStack(ModBlocks.COPPER_BLOCK.get(), 1, i + 8), waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, i));
					if (i > 3) {
						addShapelessRecipe(ModBlocks.CUT_COPPER_SLAB.newItemStack(1, i), waxString, new ItemStack(ModBlocks.CUT_COPPER_SLAB.get(), 1, i - 4));
						addShapelessRecipe(new ItemStack(copper_stairs[i], 1), waxString, new ItemStack(copper_stairs[i - 4], 1));
					}
				}
			}
		}

		addSmelting(ModBlocks.COBBLED_DEEPSLATE.get(), ModBlocks.DEEPSLATE.newItemStack(), 0.1F);
		addSmelting(ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 0), ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 1), 0.1F);
		addSmelting(ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2), ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 3), 0.1F);

		addShapedRecipe(ModBlocks.POLISHED_DEEPSLATE.newItemStack(4, 0), "xx", "xx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICKS.newItemStack(4, 0), "xx", "xx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICKS.newItemStack(4, 2), "xx", "xx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 4), "x", "x", 'x', ModBlocks.DEEPSLATE_SLAB.newItemStack(1, 0));

		addShapedRecipe(ModBlocks.COBBLED_DEEPSLATE_STAIRS.newItemStack(4, 0), "x  ", "xx ", "xxx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.POLISHED_DEEPSLATE_STAIRS.newItemStack(4, 0), "x  ", "xx ", "xxx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_STAIRS.newItemStack(4, 0), "x  ", "xx ", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.DEEPSLATE_TILE_STAIRS.newItemStack(4, 0), "x  ", "xx ", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.DEEPSLATE_SLAB.newItemStack(6, 0), "xxx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.DEEPSLATE_SLAB.newItemStack(6, 1), "xxx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_SLAB.newItemStack(4, 0), "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_SLAB.newItemStack(4, 1), "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2));
		addShapedRecipe(ModBlocks.DEEPSLATE_WALL.newItemStack(6, 0), "xxx", "xxx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.DEEPSLATE_WALL.newItemStack(6, 1), "xxx", "xxx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_WALL.newItemStack(6, 0), "xxx", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.DEEPSLATE_BRICK_WALL.newItemStack(6, 1), "xxx", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2));

		Item result = null;
		if (ModItems.COPPER_INGOT.isEnabled()) {
			result = ModItems.COPPER_INGOT.get();
		} else if (!OreDictionary.getOres("ingotCopper").isEmpty()) {
			result = OreDictionary.getOres("ingotCopper").get(0).getItem();
		}

		if (result != null) {
			addSmelting(ModItems.RAW_ORE.newItemStack(1, 0), new ItemStack(result, 1, 0), 0.7F);
		}
		addShapedRecipe(ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 0), "xxx", "xxx", "xxx", 'x', ModItems.RAW_ORE.newItemStack(1, 0));
		addShapedRecipe(ModItems.RAW_ORE.newItemStack(9, 0), "x", 'x', ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 0));
		addShapedRecipe(ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 1), "xxx", "xxx", "xxx", 'x', ModItems.RAW_ORE.newItemStack(1, 1));
		addShapedRecipe(ModItems.RAW_ORE.newItemStack(9, 1), "x", 'x', ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 1));
		addSmelting(ModItems.RAW_ORE.newItemStack(1, 1), new ItemStack(Items.iron_ingot, 1, 0), 0.7F);
		addShapedRecipe(ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 2), "xxx", "xxx", "xxx", 'x', ModItems.RAW_ORE.newItemStack(1, 2));
		addShapedRecipe(ModItems.RAW_ORE.newItemStack(9, 2), "x", 'x', ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 2));
		addSmelting(ModItems.RAW_ORE.newItemStack(1, 2), new ItemStack(Items.gold_ingot, 1, 0), 0.7F);

		for (int i = 0; i < getStewFlowers().size(); i++) {
			ItemStack stew = ModItems.SUSPICIOUS_STEW.newItemStack(1, 0);

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

		//for (int i = EntityNewBoat.Type.values().length - 1; i >= 0; i--) {
			//addShapedRecipe(new ItemStack(i == 0 && ConfigBlocksItems.replaceOldBoats ? Items.boat : ModItems.BOATS[i].get(), 1),
			//		(ConfigBlocksItems.replaceOldBoats ? "x x" : "xyx"), "xxx", 'x', i == 0 ? "plankWood" : new ItemStack(Blocks.planks, 1, i), 'y', new ItemStack(Items.wooden_shovel, 1));
		//}
		//if (!ConfigBlocksItems.replaceOldBoats) {
		//	addShapelessRecipe(new ItemStack(Items.boat), ModItems.BOATS[0]);
		//	addShapelessRecipe(new ItemStack(ModItems.BOATS[0].get()), Items.wooden_shovel, Items.boat);
		//}
		//for (int i = EntityNewBoat.Type.values().length - 1; i >= 0; i--) {
		//	addShapedRecipe(new ItemStack(ModItems.CHEST_BOATS[i].get()), "c", "b", 'b', i == 0 && ConfigBlocksItems.replaceOldBoats ? Items.boat : ModItems.BOATS[i].get(), 'c', Blocks.chest);
		//}

		addShapedRecipe(ModBlocks.SHULKER_BOX.newItemStack(), "x", "c", "x", 'x', ModItems.SHULKER_SHELL.newItemStack(), 'c', new ItemStack(Blocks.chest));
		for (int i = ore_dyes.length - 1; i >= 0; i--) {//Dyed box recipes
			ItemStack shulker = ModBlocks.SHULKER_BOX.newItemStack();
			shulker.setTagCompound(new NBTTagCompound());
			shulker.getTagCompound().setByte("Color", (byte) (16 - i));
			GameRegistry.addRecipe(new RecipeDyedShulkerBox(shulker, new Object[]{ModBlocks.SHULKER_BOX.get(), ore_dyes[i]}));
		}
		addShapedRecipe(ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 0), "XXX", "XYX", "XXX", 'X', "ingotIron", 'Y', ModItems.SHULKER_SHELL.get());
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

		}

		addShapedRecipe(ModBlocks.STONE_WALL.newItemStack(6, 0), "BBB", "BBB", 'B', new ItemStack(Blocks.stonebrick, 1, 0));
		addShapedRecipe(ModBlocks.STONE_WALL.newItemStack(6, 1), "BBB", "BBB", 'B', new ItemStack(Blocks.stonebrick, 1, 1));
		addShapedRecipe(ModBlocks.STONE_WALL.newItemStack(6, 2), "BBB", "BBB", 'B', new ItemStack(Blocks.sandstone, 1, 0));
		addShapedRecipe(ModBlocks.STONE_WALL.newItemStack(6, 3), "BBB", "BBB", 'B', new ItemStack(Blocks.brick_block, 1, 0));

		//TODO Nether brick wall should be individually toggleable because of Netherlicious
		addShapedRecipe(ModBlocks.NETHER_BRICK_WALL.newItemStack(6), "BBB", "BBB", 'B', new ItemStack(Blocks.nether_brick));

		addShapedRecipe(ModBlocks.SMITHING_TABLE.newItemStack(1, 0), "II", "PP", "PP", 'P', "plankWood", 'I', "ingotIron");

		addShapedRecipe(ModBlocks.FLETCHING_TABLE.newItemStack(1, 0), "FF", "PP", "PP", 'P', "plankWood", 'F', new ItemStack(Items.flint, 1, 0));

		addShapedRecipe(ModBlocks.STONECUTTER.newItemStack(1, 0), " I ", "SSS", 'S', "stone", 'I', "ingotIron");

		addShapedRecipe(ModBlocks.COMPOSTER.newItemStack(1, 0), "S S", "S S", "SSS", 'S', "slabWood");

		addShapedRecipe(ModBlocks.CARTOGRAPHY_TABLE.newItemStack(1, 0), "pp", "PP", "PP", 'P', "plankWood", 'p', new ItemStack(Items.paper, 1, 0));

		addShapedRecipe(ModBlocks.LOOM.newItemStack(1, 0), "SS", "PP", 'P', "plankWood", 'S', new ItemStack(Items.string, 1, 0));

		addShapedRecipe(ModBlocks.AMETHYST_BLOCK.newItemStack(1, 0), "AA", "AA", 'A', "gemAmethyst");
		addShapedRecipe(ModBlocks.TINTED_GLASS.newItemStack(2, 0), " A ", "AGA", " A ", 'A', "gemAmethyst", 'G', "blockGlassColorless");

		addShapedRecipe(ModBlocks.TARGET.newItemStack(1, 0), " R ", "RHR", " R ", 'R', "dustRedstone", 'H', Blocks.hay_block);

		addShapedRecipe(ModBlocks.OBSERVER.newItemStack(1, 0), "CCC", "RRQ", "CCC", 'R', "dustRedstone", 'C', "cobblestone", 'Q', "gemQuartz");
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

			list.add(ModBlocks.LILY_OF_THE_VALLEY.newItemStack(1, 0));
			list.add(ModBlocks.CORNFLOWER.newItemStack(1, 0));
			list.add(ModBlocks.WITHER_ROSE.newItemStack(1, 0));

		return list;
	}

	private static void registerOre(String oreName, ItemStack ore) {
		if(validateItems(ore)) {
			OreDictionary.registerOre(oreName, ore);
		}
	}

	private static void registerOre(String oreName, Item ore) {
		if(validateItems(ore)) {
			OreDictionary.registerOre(oreName, ore);
		}
	}

	private static void registerOre(String oreName, Block ore) {
		if(validateItems(ore)) {
			OreDictionary.registerOre(oreName, ore);
		}
	}

	private static void addSmelting(Item input, ItemStack output, float exp) {
		if(validateItems(input) && validateItems(output)) {
			GameRegistry.addSmelting(input, output, exp);
		}
	}
	
	private static void addSmelting(Block input, ItemStack output, float exp) {
		if(validateItems(input) && validateItems(output)) {
			GameRegistry.addSmelting(input, output, exp);
		}
	}

	private static void addSmelting(ItemStack input, ItemStack output, float exp) {
		if(validateItems(input) && validateItems(output)) {
			GameRegistry.addSmelting(input, output, exp);
		}
	}

	private static void addShapedRecipe(ItemStack output, Object... objects) {
		if(validateItems(output) && validateItems(objects)) {
			GameRegistry.addRecipe(new ShapedEtFuturumRecipe(output, objects));
		}
	}

	private static void addShapelessRecipe(ItemStack output, Object... objects) {
		if(validateItems(output) && validateItems(objects)) {
			GameRegistry.addRecipe(new ShapelessEtFuturumRecipe(output, objects));
		}
	}

	private static boolean validateItems(Object... objects) {
		for (Object object : objects) {
			if (object instanceof ItemStack) {
				if (Item.itemRegistry.getNameForObject(((ItemStack) object).getItem()) == null) {
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
		for (Object recipe : CraftingManager.getInstance().getRecipeList())
			if (recipe != null) {
				ItemStack stack = ((IRecipe) recipe).getRecipeOutput();
				if (stack != null && stack.getItem() == item && (meta == -1 || meta == stack.getItemDamage())) {
					CraftingManager.getInstance().getRecipeList().remove(recipe);
					return;
				}
			}
	}

}
