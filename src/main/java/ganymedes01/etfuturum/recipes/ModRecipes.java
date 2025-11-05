package ganymedes01.etfuturum.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.Tags;
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
import ganymedes01.etfuturum.recipes.crafting.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
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
import roadhog360.hogutils.api.utils.RecipeHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ModRecipes {

	public static final String[] ore_dyes = new String[]{"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"};
	public static final String[] dye_names = new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"};
	public static final String[] woodTypes = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "dark_oak"};
	public static final String[] modernWoodTypes = new String[]{"crimson", "warped", "mangrove", "cherry"};
	static final boolean[] modernWoodTypesEnabled = new boolean[5];

	public static void init() {
		if (ConfigBlocksItems.enableBanners) {
			RecipeSorter.register(Tags.MOD_ID + ".RecipeDuplicatePattern", RecipeDuplicatePattern.class, Category.SHAPELESS, "after:minecraft:shapeless");
			RecipeSorter.register(Tags.MOD_ID + ".RecipeAddPattern", RecipeAddPattern.class, Category.SHAPED, "after:minecraft:shaped");
		}

		modernWoodTypesEnabled[0] = ConfigExperiments.enableCrimsonBlocks;
		modernWoodTypesEnabled[1] = ConfigExperiments.enableWarpedBlocks;
		modernWoodTypesEnabled[2] = ConfigExperiments.enableMangroveBlocks;
		modernWoodTypesEnabled[3] = ConfigBlocksItems.enableCherryBlocks;
		modernWoodTypesEnabled[4] = ConfigBlocksItems.enableBambooBlocks;

		ModTagging.registerOreDictionary();
		ModTagging.registerHogTags();
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
			RecipeHelper.removeFirstRecipeWithOutput(Blocks.stone_slab, 0, false);
		}

		if (ConfigBlocksItems.enableVanillaDoors) {
			Items.wooden_door.setMaxStackSize(64);
			Items.iron_door.setMaxStackSize(64);
		}

		if (ConfigFunctions.enableDoorRecipeBuffs && !ModsList.GTNH.isLoaded()) {
			RecipeHelper.removeFirstRecipeWithOutput(Items.wooden_door, 0, false);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.wooden_door, 3), "xx", "xx", "xx", 'x', "plankWood"));
			RecipeHelper.removeFirstRecipeWithOutput(Items.iron_door, 0, false);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.iron_door, 3), "xx", "xx", "xx", 'x', "ingotIron"));
		}

		if (ConfigBlocksItems.enableVanillaFences && !ModsList.GTNH.isLoaded()) {
			RecipeHelper.removeFirstRecipeWithOutput(Blocks.fence, 0, false);
			RecipeHelper.removeFirstRecipeWithOutput(Blocks.nether_brick_fence, 0, false);
		}

		if (ConfigBlocksItems.replaceOldBoats && ConfigBlocksItems.enableNewBoats) {
			if (!ModsList.GTNH.isLoaded()) {
				RecipeHelper.removeFirstRecipeWithOutput(Items.boat, 0, false);
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

	private static void registerRecipes() {
		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output = ModBlocks.OLD_GRAVEL.newItemStack(4);
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, "xy", "yx", 'x', ModBlocks.COARSE_DIRT.get(), 'y', Blocks.gravel);
		}

		if (ConfigFunctions.enableStoneBrickRecipes && !ModsList.GTNH.isLoaded()) {
			RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, new ItemStack(Blocks.mossy_cobblestone), new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.vine));
			RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, new ItemStack(Blocks.stonebrick, 1, 1), new ItemStack(Blocks.stonebrick), new ItemStack(Blocks.vine));
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, new ItemStack(Blocks.stonebrick, 1, 3), "x", "x", 'x', new ItemStack(Blocks.stone_slab, 1, 5));
			RecipeHelper.addSmelting(new ItemStack(Blocks.stonebrick), new ItemStack(Blocks.stonebrick, 1, 2), 0.0F);
		}

		if (!ModsList.GTNH.isLoaded()) {
			RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, new ItemStack(Blocks.mossy_cobblestone), new ItemStack(Blocks.cobblestone), ModBlocks.MOSS_BLOCK.newItemStack());
			RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, new ItemStack(Blocks.stonebrick, 1, 1), new ItemStack(Blocks.stonebrick), ModBlocks.MOSS_BLOCK.newItemStack());
		}

		if (ConfigFunctions.enableRecipeForTotem) {
			ItemStack output = ModItems.TOTEM_OF_UNDYING.newItemStack();
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, "EBE", "GBG", " G ", 'E', "gemEmerald", 'G', "ingotGold", 'B', "blockGold");
		}

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output1 = ModBlocks.SLIME.newItemStack();
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, "xxx", "xxx", "xxx", 'x', new ItemStack(Items.slime_ball));
			RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, new ItemStack(Items.slime_ball, 9), ModBlocks.SLIME.get());

			ItemStack output = ModBlocks.COARSE_DIRT.newItemStack(4);
			Object[] objects = new Object[]{"xy", "yx", 'x', new ItemStack(Blocks.dirt), 'y', new ItemStack(Blocks.gravel)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, objects);
		}

		ItemStack output2 = ModItems.MUTTON_COOKED.newItemStack();
		RecipeHelper.addSmelting(ModItems.MUTTON_RAW.get(), output2, 0.35F);

		if (!ModsList.GTNH.isLoaded()) {
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, new ItemStack(Items.iron_ingot), "xxx", "xxx", "xxx", 'x', "nuggetIron");
			ItemStack output = ModItems.NUGGET_IRON.newItemStack(9);
			RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output, "ingotIron");
		}

		// Granite
		ItemStack output228 = ModBlocks.STONE.newItemStack(2, 1);
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output228, "gemQuartz", "stoneDiorite");
		ItemStack output215 = ModBlocks.STONE.newItemStack(4, 2);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output215, "xx", "xx", 'x', "stoneGranite");
		// Diorite
		ItemStack output214 = ModBlocks.STONE.newItemStack(2, 3);
		Object[] objects162 = new Object[]{"xy", "yx", 'x', new ItemStack(Blocks.cobblestone), 'y', "gemQuartz"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output214, objects162);
		ItemStack output213 = ModBlocks.STONE.newItemStack(4, 4);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output213, "xx", "xx", 'x', "stoneDiorite");
		// Andesite
		ItemStack output227 = ModBlocks.STONE.newItemStack(2, 5);
		Object[] objects170 = new Object[]{new ItemStack(Blocks.cobblestone), "stoneDiorite"};
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output227, objects170);
		ItemStack output212 = ModBlocks.STONE.newItemStack(4, 6);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output212, "xx", "xx", 'x', "stoneAndesite");
		Block[] stone_stairs = new Block[]{ModBlocks.GRANITE_STAIRS.get(), ModBlocks.POLISHED_GRANITE_STAIRS.get(), ModBlocks.DIORITE_STAIRS.get(), ModBlocks.POLISHED_DIORITE_STAIRS.get(), ModBlocks.ANDESITE_STAIRS.get(), ModBlocks.POLISHED_ANDESITE_STAIRS.get()};
		for (int i = 0; i < stone_stairs.length; i++) { //TODO: Rewrite this, this seems needlessly convoluted
			String dictName = "stone" + StringUtils.capitalize(((BaseSlab) ModBlocks.STONE_SLAB_2.get()).types[(i / 2) * 2]) + (i % 2 == 1 ? "Polished" : "");
			if (!ModsList.GTNH.isLoaded()) {
				ItemStack output = ModBlocks.STONE_SLAB_2.newItemStack(6, i);
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, "xxx", 'x', dictName);
			}
			ItemStack output1 = new ItemStack(stone_stairs[i], 4);
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, "x  ", "xx ", "xxx", 'x', dictName);
			if (i % 2 == 1) {
				ItemStack output = ModBlocks.STONE_WALL_2.newItemStack(6, i == 5 ? 2 : i == 3 ? 1 : 0);
				Object[] objects = new Object[]{"xxx", "xxx", 'x', ModBlocks.STONE.newItemStack(1, i)};
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, objects);
			}
		}

		ItemStack output211 = ModBlocks.PRISMARINE_BLOCK.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output211, "xx", "xx", 'x', "shardPrismarine");
		ItemStack output210 = ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 1);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output210, "xxx", "xxx", "xxx", 'x', "shardPrismarine");
		ItemStack output209 = ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 2);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output209, "xxx", "xyx", "xxx", 'x', "shardPrismarine", 'y', "dyeBlack");
		ItemStack output208 = ModBlocks.SEA_LANTERN.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output208, "xyx", "yyy", "xyx", 'x', "shardPrismarine", 'y', "crystalPrismarine");

		ItemStack output207 = ModBlocks.PRISMARINE_STAIRS.newItemStack(4);
		Object[] objects161 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output207, objects161);
		ItemStack output206 = ModBlocks.PRISMARINE_STAIRS_BRICK.newItemStack(4);
		Object[] objects160 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output206, objects160);
		ItemStack output205 = ModBlocks.PRISMARINE_STAIRS_DARK.newItemStack(4);
		Object[] objects159 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output205, objects159);
		ItemStack output204 = ModBlocks.PRISMARINE_WALL.newItemStack(6);
		Object[] objects158 = new Object[]{"xxx", "xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output204, objects158);

		for (int i = 0; i < 3; i++) {
			ItemStack output = ModBlocks.PRISMARINE_SLAB.newItemStack(6, i);
			Object[] objects = new Object[]{"xxx", 'x', ModBlocks.PRISMARINE_BLOCK.newItemStack(1, i)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, objects);
		}

		if (!ModsList.BOTANIA.isLoaded()) {
			ItemStack output1 = ModItems.PRISMARINE_SHARD.newItemStack(4);
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, "xy", "zx", 'x', "gemQuartz", 'y', "dyeBlue", 'z', "dyeGreen");
			ItemStack output = ModItems.PRISMARINE_CRYSTALS.newItemStack(4);
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, "xy", "yx", 'x', "gemQuartz", 'y', "dustGlowstone");
		}

		if (!ModsList.GTNH.isLoaded()) {
			Block[] metaBlocks = new Block[]{Blocks.stone, Blocks.mossy_cobblestone, Blocks.stonebrick, Blocks.sandstone};
			for (int i = 0; i < metaBlocks.length; i++) {
				ItemStack output = ModBlocks.STONE_SLAB.newItemStack(6, i);
				Object[] objects = new Object[]{"xxx", 'x', new ItemStack(metaBlocks[i], 1, i != 0 ? i - 1 : i)};
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, objects);
			}
			ItemStack output = new ItemStack(Blocks.stone_slab, 6, 0);
			Object[] objects = new Object[]{"xxx", 'x', ModBlocks.SMOOTH_STONE.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, objects);
		}

		ItemStack output203 = ModBlocks.STONE_STAIRS.newItemStack(4);
		Object[] objects157 = new Object[]{"x  ", "xx ", "xxx", 'x', new ItemStack(Blocks.stone, 1, 0)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output203, objects157);
		ItemStack output202 = ModBlocks.MOSSY_COBBLESTONE_STAIRS.newItemStack(4);
		Object[] objects156 = new Object[]{"x  ", "xx ", "xxx", 'x', new ItemStack(Blocks.mossy_cobblestone, 1, 0)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output202, objects156);
		ItemStack output201 = ModBlocks.MOSSY_STONE_BRICK_STAIRS.newItemStack(4);
		Object[] objects155 = new Object[]{"x  ", "xx ", "xxx", 'x', new ItemStack(Blocks.stonebrick, 1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output201, objects155);

		if (!ModsList.GTNH.isLoaded()) {
			//Bark to planks
			ItemStack output18 = new ItemStack(Blocks.planks, 4);
			Object[] objects17 = new Object[]{"x", 'x', ModBlocks.BARK.newItemStack(1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output18, objects17);
			ItemStack output17 = new ItemStack(Blocks.planks, 4, 1);
			Object[] objects16 = new Object[]{"x", 'x', ModBlocks.BARK.newItemStack(1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output17, objects16);
			ItemStack output16 = new ItemStack(Blocks.planks, 4, 2);
			Object[] objects15 = new Object[]{"x", 'x', ModBlocks.BARK.newItemStack(1, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output16, objects15);
			ItemStack output15 = new ItemStack(Blocks.planks, 4, 3);
			Object[] objects14 = new Object[]{"x", 'x', ModBlocks.BARK.newItemStack(1, 3)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output15, objects14);
			ItemStack output14 = new ItemStack(Blocks.planks, 4, 4);
			Object[] objects13 = new Object[]{"x", 'x', ModBlocks.BARK2.newItemStack(1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output14, objects13);
			ItemStack output13 = new ItemStack(Blocks.planks, 4, 5);
			Object[] objects12 = new Object[]{"x", 'x', ModBlocks.BARK2.newItemStack(1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output13, objects12);

			//Stripped log to planks
			ItemStack output12 = new ItemStack(Blocks.planks, 4);
			Object[] objects11 = new Object[]{"x", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output12, objects11);
			ItemStack output11 = new ItemStack(Blocks.planks, 4, 1);
			Object[] objects10 = new Object[]{"x", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output11, objects10);
			ItemStack output10 = new ItemStack(Blocks.planks, 4, 2);
			Object[] objects9 = new Object[]{"x", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output10, objects9);
			ItemStack output9 = new ItemStack(Blocks.planks, 4, 3);
			Object[] objects8 = new Object[]{"x", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1, 3)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output9, objects8);
			ItemStack output8 = new ItemStack(Blocks.planks, 4, 4);
			Object[] objects7 = new Object[]{"x", 'x', ModBlocks.LOG2_STRIPPED.newItemStack(1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output8, objects7);
			ItemStack output7 = new ItemStack(Blocks.planks, 4, 5);
			Object[] objects6 = new Object[]{"x", 'x', ModBlocks.LOG2_STRIPPED.newItemStack(1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output7, objects6);

			//Stripped bark to planks
			ItemStack output6 = new ItemStack(Blocks.planks, 4);
			Object[] objects5 = new Object[]{"x", 'x', ModBlocks.WOOD_STRIPPED.newItemStack(1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output6, objects5);
			ItemStack output5 = new ItemStack(Blocks.planks, 4, 1);
			Object[] objects4 = new Object[]{"x", 'x', ModBlocks.WOOD_STRIPPED.newItemStack(1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects4);
			ItemStack output4 = new ItemStack(Blocks.planks, 4, 2);
			Object[] objects3 = new Object[]{"x", 'x', ModBlocks.WOOD_STRIPPED.newItemStack(1, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output4, objects3);
			ItemStack output3 = new ItemStack(Blocks.planks, 4, 3);
			Object[] objects2 = new Object[]{"x", 'x', ModBlocks.WOOD_STRIPPED.newItemStack(1, 3)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, objects2);
			ItemStack output1 = new ItemStack(Blocks.planks, 4, 4);
			Object[] objects1 = new Object[]{"x", 'x', ModBlocks.WOOD2_STRIPPED.newItemStack(1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, objects1);
			ItemStack output = new ItemStack(Blocks.planks, 4, 5);
			Object[] objects = new Object[]{"x", 'x', ModBlocks.WOOD2_STRIPPED.newItemStack(1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, objects);

			for (int i = 0; i < 4; i++) {
				ItemStack input7 = ModBlocks.LOG_STRIPPED.newItemStack(1, i);
				RecipeHelper.addSmelting(input7, new ItemStack(Items.coal, 1, 1), 0.15F);
				ItemStack input6 = ModBlocks.LOG2_STRIPPED.newItemStack(1, i);
				RecipeHelper.addSmelting(input6, new ItemStack(Items.coal, 1, 1), 0.15F);
				ItemStack input5 = ModBlocks.WOOD_STRIPPED.newItemStack(1, i);
				RecipeHelper.addSmelting(input5, new ItemStack(Items.coal, 1, 1), 0.15F);
				ItemStack input4 = ModBlocks.WOOD2_STRIPPED.newItemStack(1, i);
				RecipeHelper.addSmelting(input4, new ItemStack(Items.coal, 1, 1), 0.15F);
				ItemStack input3 = ModBlocks.BARK.newItemStack(1, i);
				RecipeHelper.addSmelting(input3, new ItemStack(Items.coal, 1, 1), 0.15F);
				ItemStack input2 = ModBlocks.BARK2.newItemStack(1, i);
				RecipeHelper.addSmelting(input2, new ItemStack(Items.coal, 1, 1), 0.15F);

				ItemStack input1 = ModBlocks.MANGROVE_LOG.newItemStack(1, i);
				RecipeHelper.addSmelting(input1, new ItemStack(Items.coal, 1, 1), 0.15F);
				ItemStack input = ModBlocks.CHERRY_LOG.newItemStack(1, i);
				RecipeHelper.addSmelting(input, new ItemStack(Items.coal, 1, 1), 0.15F);
			}
		}

		//Logs to bark
		ItemStack output200 = ModBlocks.BARK.newItemStack(3);
		Object[] objects154 = new Object[]{"xx", "xx", 'x', new ItemStack(Blocks.log, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output200, objects154);
		ItemStack output199 = ModBlocks.BARK.newItemStack(3, 1);
		Object[] objects153 = new Object[]{"xx", "xx", 'x', new ItemStack(Blocks.log, 1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output199, objects153);
		ItemStack output198 = ModBlocks.BARK.newItemStack(3, 2);
		Object[] objects152 = new Object[]{"xx", "xx", 'x', new ItemStack(Blocks.log, 1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output198, objects152);
		ItemStack output197 = ModBlocks.BARK.newItemStack(3, 3);
		Object[] objects151 = new Object[]{"xx", "xx", 'x', new ItemStack(Blocks.log, 1, 3)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output197, objects151);
		ItemStack output196 = ModBlocks.BARK2.newItemStack(3);
		Object[] objects150 = new Object[]{"xx", "xx", 'x', new ItemStack(Blocks.log2, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output196, objects150);
		ItemStack output195 = ModBlocks.BARK2.newItemStack(3, 1);
		Object[] objects149 = new Object[]{"xx", "xx", 'x', new ItemStack(Blocks.log2, 1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output195, objects149);

		//Stripped logs to stripped bark
		ItemStack output194 = ModBlocks.WOOD_STRIPPED.newItemStack(3);
		Object[] objects148 = new Object[]{"xx", "xx", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output194, objects148);
		ItemStack output193 = ModBlocks.WOOD_STRIPPED.newItemStack(3, 1);
		Object[] objects147 = new Object[]{"xx", "xx", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output193, objects147);
		ItemStack output192 = ModBlocks.WOOD_STRIPPED.newItemStack(3, 2);
		Object[] objects146 = new Object[]{"xx", "xx", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output192, objects146);
		ItemStack output191 = ModBlocks.WOOD_STRIPPED.newItemStack(3, 3);
		Object[] objects145 = new Object[]{"xx", "xx", 'x', ModBlocks.LOG_STRIPPED.newItemStack(1, 3)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output191, objects145);
		ItemStack output190 = ModBlocks.WOOD2_STRIPPED.newItemStack(3);
		Object[] objects144 = new Object[]{"xx", "xx", 'x', ModBlocks.LOG2_STRIPPED.newItemStack(1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output190, objects144);
		ItemStack output189 = ModBlocks.WOOD2_STRIPPED.newItemStack(3, 1);
		Object[] objects143 = new Object[]{"xx", "xx", 'x', ModBlocks.LOG2_STRIPPED.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output189, objects143);

		if (!ModsList.GTNH.isLoaded()) {
			//New logs, bark, stripped log and stripped bark to planks
			ItemStack output5 = ModBlocks.WOOD_PLANKS.newItemStack(4);
			Object[] objects4 = new Object[]{"x", 'x', ModBlocks.CRIMSON_STEM.newItemStack(1, OreDictionary.WILDCARD_VALUE)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects4);
			ItemStack output4 = ModBlocks.WOOD_PLANKS.newItemStack(4, 1);
			Object[] objects3 = new Object[]{"x", 'x', ModBlocks.WARPED_STEM.newItemStack(1, OreDictionary.WILDCARD_VALUE)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output4, objects3);
			ItemStack output3 = ModBlocks.WOOD_PLANKS.newItemStack(4, 2);
			Object[] objects2 = new Object[]{"x", 'x', ModBlocks.MANGROVE_LOG.newItemStack(1, OreDictionary.WILDCARD_VALUE)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, objects2);
			ItemStack output1 = ModBlocks.WOOD_PLANKS.newItemStack(4, 3);
			Object[] objects1 = new Object[]{"x", 'x', ModBlocks.CHERRY_LOG.newItemStack(1, OreDictionary.WILDCARD_VALUE)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, objects1);
			ItemStack output = ModBlocks.WOOD_PLANKS.newItemStack(2, 4);
			Object[] objects = new Object[]{"x", 'x', ModBlocks.BAMBOO_BLOCK.newItemStack(1, OreDictionary.WILDCARD_VALUE)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, objects);
		}

		if (ConfigBlocksItems.enableStrippedLogs && ConfigBlocksItems.enableBarkLogs) {
			//New stripped logs to new stripped bark
			ItemStack output4 = ModBlocks.CRIMSON_STEM.newItemStack(3, 3);
			Object[] objects3 = new Object[]{"xx", "xx", 'x', ModBlocks.CRIMSON_STEM.newItemStack(2, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output4, objects3);
			ItemStack output3 = ModBlocks.WARPED_STEM.newItemStack(3, 3);
			Object[] objects2 = new Object[]{"xx", "xx", 'x', ModBlocks.WARPED_STEM.newItemStack(2, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, objects2);
			ItemStack output1 = ModBlocks.MANGROVE_LOG.newItemStack(3, 3);
			Object[] objects1 = new Object[]{"xx", "xx", 'x', ModBlocks.MANGROVE_LOG.newItemStack(2, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, objects1);
			ItemStack output = ModBlocks.CHERRY_LOG.newItemStack(3, 3);
			Object[] objects = new Object[]{"xx", "xx", 'x', ModBlocks.CHERRY_LOG.newItemStack(2, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, objects);
		}

		if (ConfigBlocksItems.enableBarkLogs) {
			//New logs to new bark
			ItemStack output4 = ModBlocks.CRIMSON_STEM.newItemStack(3, 1);
			Object[] objects3 = new Object[]{"xx", "xx", 'x', ModBlocks.CRIMSON_STEM.newItemStack(1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output4, objects3);
			ItemStack output3 = ModBlocks.WARPED_STEM.newItemStack(3, 1);
			Object[] objects2 = new Object[]{"xx", "xx", 'x', ModBlocks.WARPED_STEM.newItemStack(1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, objects2);
			ItemStack output1 = ModBlocks.MANGROVE_LOG.newItemStack(3, 1);
			Object[] objects1 = new Object[]{"xx", "xx", 'x', ModBlocks.MANGROVE_LOG.newItemStack(1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, objects1);
			ItemStack output = ModBlocks.CHERRY_LOG.newItemStack(3, 1);
			Object[] objects = new Object[]{"xx", "xx", 'x', ModBlocks.CHERRY_LOG.newItemStack(1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, objects);
		}

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output7 = ModBlocks.FENCE_SPRUCE.newItemStack(3);
			Object[] objects6 = new Object[]{"xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 1), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output7, objects6);
			ItemStack output6 = ModBlocks.FENCE_BIRCH.newItemStack(3);
			Object[] objects5 = new Object[]{"xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 2), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output6, objects5);
			ItemStack output5 = ModBlocks.FENCE_JUNGLE.newItemStack(3);
			Object[] objects4 = new Object[]{"xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 3), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects4);
			ItemStack output4 = ModBlocks.FENCE_ACACIA.newItemStack(3);
			Object[] objects3 = new Object[]{"xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 4), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output4, objects3);
			ItemStack output3 = ModBlocks.FENCE_DARK_OAK.newItemStack(3);
			Object[] objects2 = new Object[]{"xyx", "xyx", 'x', new ItemStack(Blocks.planks, 1, 5), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, objects2);

			if (ConfigExperiments.enableCrimsonBlocks) {
				ItemStack output1 = ModBlocks.WOOD_FENCE.newItemStack(3);
				Object[] objects1 = new Object[]{"xyx", "xyx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1), 'y', "stickWood"};
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, objects1);
				ItemStack output = ModBlocks.WOOD_SLAB.newItemStack(6, 0);
				Object[] objects = new Object[]{"xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 0)};
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, objects);
			}
			if (ConfigExperiments.enableWarpedBlocks) {
				ItemStack output1 = ModBlocks.WOOD_FENCE.newItemStack(3, 1);
				Object[] objects1 = new Object[]{"xyx", "xyx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1), 'y', "stickWood"};
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, objects1);
				ItemStack output = ModBlocks.WOOD_SLAB.newItemStack(6, 1);
				Object[] objects = new Object[]{"xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1)};
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, objects);
			}
			if (ConfigExperiments.enableMangroveBlocks) {
				ItemStack output1 = ModBlocks.WOOD_FENCE.newItemStack(3, 2);
				Object[] objects1 = new Object[]{"xyx", "xyx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2), 'y', "stickWood"};
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, objects1);
				ItemStack output = ModBlocks.WOOD_SLAB.newItemStack(6, 2);
				Object[] objects = new Object[]{"xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2)};
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, objects);
			}
			if (ConfigBlocksItems.enableCherryBlocks) {
				ItemStack output1 = ModBlocks.WOOD_FENCE.newItemStack(3, 3);
				Object[] objects1 = new Object[]{"xyx", "xyx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3), 'y', "stickWood"};
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, objects1);
				ItemStack output = ModBlocks.WOOD_SLAB.newItemStack(6, 3);
				Object[] objects = new Object[]{"xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3)};
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, objects);
			}
			if (ConfigBlocksItems.enableBambooBlocks) {
				ItemStack output1 = ModBlocks.WOOD_FENCE.newItemStack(3, 4);
				Object[] objects1 = new Object[]{"xyx", "xyx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4), 'y', "stickWood"};
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, objects1);
				ItemStack output = ModBlocks.WOOD_SLAB.newItemStack(6, 4);
				Object[] objects = new Object[]{"xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4)};
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, objects);
			}
		}

		ItemStack output188 = ModBlocks.CRIMSON_STAIRS.newItemStack(4);
		Object[] objects142 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 0)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output188, objects142);
		ItemStack output187 = ModBlocks.WARPED_STAIRS.newItemStack(4);
		Object[] objects141 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output187, objects141);
		ItemStack output186 = ModBlocks.MANGROVE_STAIRS.newItemStack(4);
		Object[] objects140 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output186, objects140);
		ItemStack output185 = ModBlocks.CHERRY_STAIRS.newItemStack(4);
		Object[] objects139 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output185, objects139);
		ItemStack output184 = ModBlocks.BAMBOO_STAIRS.newItemStack(4);
		Object[] objects138 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output184, objects138);

		ItemStack output183 = ModBlocks.BAMBOO_BLOCK.newItemStack();
		Object[] objects137 = new Object[]{"bbb", "bbb", "bbb", 'b', ModItems.BAMBOO.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output183, objects137);
		Object[] objects136 = new Object[]{"b", "b", 'b', ModItems.BAMBOO.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, new ItemStack(Items.stick), objects136);

		// Bamboo Mosaic
		ItemStack output182 = ModBlocks.BAMBOO_MOSAIC.newItemStack(1, 0);
		Object[] objects135 = new Object[]{"x", "x", 'x', ModBlocks.WOOD_SLAB.newItemStack(1, 4)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output182, objects135);
		ItemStack output181 = ModBlocks.BAMBOO_MOSAIC_SLAB.newItemStack(6, 5);
		Object[] objects134 = new Object[]{"xxx", 'x', ModBlocks.BAMBOO_MOSAIC.newItemStack(1, 0)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output181, objects134);
		ItemStack output180 = ModBlocks.BAMBOO_MOSAIC_STAIRS.newItemStack(4);
		Object[] objects133 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.BAMBOO_MOSAIC.newItemStack(1, 0)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output180, objects133);

		ItemStack output179 = ModBlocks.FENCE_GATE_SPRUCE.newItemStack();
		Object[] objects132 = new Object[]{"yxy", "yxy", 'x', new ItemStack(Blocks.planks, 1, 1), 'y', "stickWood"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output179, objects132);
		ItemStack output178 = ModBlocks.FENCE_GATE_BIRCH.newItemStack();
		Object[] objects131 = new Object[]{"yxy", "yxy", 'x', new ItemStack(Blocks.planks, 1, 2), 'y', "stickWood"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output178, objects131);
		ItemStack output177 = ModBlocks.FENCE_GATE_JUNGLE.newItemStack();
		Object[] objects130 = new Object[]{"yxy", "yxy", 'x', new ItemStack(Blocks.planks, 1, 3), 'y', "stickWood"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output177, objects130);
		ItemStack output176 = ModBlocks.FENCE_GATE_ACACIA.newItemStack();
		Object[] objects129 = new Object[]{"yxy", "yxy", 'x', new ItemStack(Blocks.planks, 1, 4), 'y', "stickWood"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output176, objects129);
		ItemStack output175 = ModBlocks.FENCE_GATE_DARK_OAK.newItemStack();
		Object[] objects128 = new Object[]{"yxy", "yxy", 'x', new ItemStack(Blocks.planks, 1, 5), 'y', "stickWood"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output175, objects128);

		ItemStack output174 = ModBlocks.CRIMSON_FENCE_GATE.newItemStack();
		Object[] objects127 = new Object[]{"yxy", "yxy", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1), 'y', "stickWood"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output174, objects127);
		ItemStack output173 = ModBlocks.WARPED_FENCE_GATE.newItemStack();
		Object[] objects126 = new Object[]{"yxy", "yxy", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1), 'y', "stickWood"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output173, objects126);
		ItemStack output172 = ModBlocks.MANGROVE_FENCE_GATE.newItemStack();
		Object[] objects125 = new Object[]{"yxy", "yxy", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2), 'y', "stickWood"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output172, objects125);
		ItemStack output171 = ModBlocks.CHERRY_FENCE_GATE.newItemStack();
		Object[] objects124 = new Object[]{"yxy", "yxy", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3), 'y', "stickWood"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output171, objects124);
		ItemStack output170 = ModBlocks.BAMBOO_FENCE_GATE.newItemStack();
		Object[] objects123 = new Object[]{"yxy", "yxy", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4), 'y', "stickWood"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output170, objects123);

		int output = ConfigFunctions.enableDoorRecipeBuffs ? 3 : 1;
		ItemStack output169 = ModBlocks.DOOR_SPRUCE.newItemStack(output);
		Object[] objects122 = new Object[]{"xx", "xx", "xx", 'x', new ItemStack(Blocks.planks, 1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output169, objects122);
		ItemStack output168 = ModBlocks.DOOR_BIRCH.newItemStack(output);
		Object[] objects121 = new Object[]{"xx", "xx", "xx", 'x', new ItemStack(Blocks.planks, 1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output168, objects121);
		ItemStack output167 = ModBlocks.DOOR_JUNGLE.newItemStack(output);
		Object[] objects120 = new Object[]{"xx", "xx", "xx", 'x', new ItemStack(Blocks.planks, 1, 3)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output167, objects120);
		ItemStack output166 = ModBlocks.DOOR_ACACIA.newItemStack(output);
		Object[] objects119 = new Object[]{"xx", "xx", "xx", 'x', new ItemStack(Blocks.planks, 1, 4)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output166, objects119);
		ItemStack output165 = ModBlocks.DOOR_DARK_OAK.newItemStack(output);
		Object[] objects118 = new Object[]{"xx", "xx", "xx", 'x', new ItemStack(Blocks.planks, 1, 5)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output165, objects118);

		ItemStack output164 = ModBlocks.CRIMSON_DOOR.newItemStack(output);
		Object[] objects117 = new Object[]{"xx", "xx", "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output164, objects117);
		ItemStack output163 = ModBlocks.WARPED_DOOR.newItemStack(output);
		Object[] objects116 = new Object[]{"xx", "xx", "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output163, objects116);
		ItemStack output162 = ModBlocks.MANGROVE_DOOR.newItemStack(output);
		Object[] objects115 = new Object[]{"xx", "xx", "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output162, objects115);
		ItemStack output161 = ModBlocks.CHERRY_DOOR.newItemStack(output);
		Object[] objects114 = new Object[]{"xx", "xx", "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output161, objects114);
		ItemStack output160 = ModBlocks.BAMBOO_DOOR.newItemStack(output);
		Object[] objects113 = new Object[]{"xx", "xx", "xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output160, objects113);

		ItemStack output159 = ModBlocks.TRAPDOOR_SPRUCE.newItemStack(2);
		Object[] objects112 = new Object[]{"xxx", "xxx", 'x', new ItemStack(Blocks.planks, 1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output159, objects112);
		ItemStack output158 = ModBlocks.TRAPDOOR_BIRCH.newItemStack(2);
		Object[] objects111 = new Object[]{"xxx", "xxx", 'x', new ItemStack(Blocks.planks, 1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output158, objects111);
		ItemStack output157 = ModBlocks.TRAPDOOR_JUNGLE.newItemStack(2);
		Object[] objects110 = new Object[]{"xxx", "xxx", 'x', new ItemStack(Blocks.planks, 1, 3)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output157, objects110);
		ItemStack output156 = ModBlocks.TRAPDOOR_ACACIA.newItemStack(2);
		Object[] objects109 = new Object[]{"xxx", "xxx", 'x', new ItemStack(Blocks.planks, 1, 4)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output156, objects109);
		ItemStack output155 = ModBlocks.TRAPDOOR_DARK_OAK.newItemStack(2);
		Object[] objects108 = new Object[]{"xxx", "xxx", 'x', new ItemStack(Blocks.planks, 1, 5)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output155, objects108);

		ItemStack output154 = ModBlocks.CRIMSON_TRAPDOOR.newItemStack(2);
		Object[] objects107 = new Object[]{"xxx", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output154, objects107);
		ItemStack output153 = ModBlocks.WARPED_TRAPDOOR.newItemStack(2);
		Object[] objects106 = new Object[]{"xxx", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output153, objects106);
		ItemStack output152 = ModBlocks.MANGROVE_TRAPDOOR.newItemStack(2);
		Object[] objects105 = new Object[]{"xxx", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output152, objects105);
		ItemStack output151 = ModBlocks.CHERRY_TRAPDOOR.newItemStack(2);
		Object[] objects104 = new Object[]{"xxx", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output151, objects104);
		ItemStack output150 = ModBlocks.BAMBOO_TRAPDOOR.newItemStack(2);
		Object[] objects103 = new Object[]{"xxx", "xxx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output150, objects103);

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output32 = ModBlocks.IRON_TRAPDOOR.newItemStack();
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output32, "xx", "xx", 'x', "ingotIron");

			ItemStack output31 = ModBlocks.BUTTON_SPRUCE.newItemStack();
			Object[] objects29 = new Object[]{"x", 'x', new ItemStack(Blocks.planks, 1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output31, objects29);
			ItemStack output30 = ModBlocks.BUTTON_BIRCH.newItemStack();
			Object[] objects28 = new Object[]{"x", 'x', new ItemStack(Blocks.planks, 1, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output30, objects28);
			ItemStack output29 = ModBlocks.BUTTON_JUNGLE.newItemStack();
			Object[] objects27 = new Object[]{"x", 'x', new ItemStack(Blocks.planks, 1, 3)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output29, objects27);
			ItemStack output28 = ModBlocks.BUTTON_ACACIA.newItemStack();
			Object[] objects26 = new Object[]{"x", 'x', new ItemStack(Blocks.planks, 1, 4)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output28, objects26);
			ItemStack output27 = ModBlocks.BUTTON_DARK_OAK.newItemStack();
			Object[] objects25 = new Object[]{"x", 'x', new ItemStack(Blocks.planks, 1, 5)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output27, objects25);

			ItemStack output26 = ModBlocks.CRIMSON_BUTTON.newItemStack();
			Object[] objects24 = new Object[]{"x", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output26, objects24);
			ItemStack output25 = ModBlocks.WARPED_BUTTON.newItemStack();
			Object[] objects23 = new Object[]{"x", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output25, objects23);
			ItemStack output24 = ModBlocks.MANGROVE_BUTTON.newItemStack();
			Object[] objects22 = new Object[]{"x", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output24, objects22);
			ItemStack output23 = ModBlocks.CHERRY_BUTTON.newItemStack();
			Object[] objects21 = new Object[]{"x", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output23, objects21);
			ItemStack output22 = ModBlocks.BAMBOO_BUTTON.newItemStack();
			Object[] objects20 = new Object[]{"x", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output22, objects20);

			ItemStack output21 = ModBlocks.PRESSURE_PLATE_SPRUCE.newItemStack();
			Object[] objects19 = new Object[]{"xx", 'x', new ItemStack(Blocks.planks, 1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output21, objects19);
			ItemStack output20 = ModBlocks.PRESSURE_PLATE_BIRCH.newItemStack();
			Object[] objects18 = new Object[]{"xx", 'x', new ItemStack(Blocks.planks, 1, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output20, objects18);
			ItemStack output19 = ModBlocks.PRESSURE_PLATE_JUNGLE.newItemStack();
			Object[] objects17 = new Object[]{"xx", 'x', new ItemStack(Blocks.planks, 1, 3)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output19, objects17);
			ItemStack output18 = ModBlocks.PRESSURE_PLATE_ACACIA.newItemStack();
			Object[] objects16 = new Object[]{"xx", 'x', new ItemStack(Blocks.planks, 1, 4)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output18, objects16);
			ItemStack output17 = ModBlocks.PRESSURE_PLATE_DARK_OAK.newItemStack();
			Object[] objects15 = new Object[]{"xx", 'x', new ItemStack(Blocks.planks, 1, 5)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output17, objects15);

			ItemStack output16 = ModBlocks.CRIMSON_PRESSURE_PLATE.newItemStack();
			Object[] objects14 = new Object[]{"xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output16, objects14);
			ItemStack output15 = ModBlocks.WARPED_PRESSURE_PLATE.newItemStack();
			Object[] objects13 = new Object[]{"xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output15, objects13);
			ItemStack output14 = ModBlocks.MANGROVE_PRESSURE_PLATE.newItemStack();
			Object[] objects12 = new Object[]{"xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output14, objects12);
			ItemStack output13 = ModBlocks.CHERRY_PRESSURE_PLATE.newItemStack();
			Object[] objects11 = new Object[]{"xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output13, objects11);
			ItemStack output12 = ModBlocks.BAMBOO_PRESSURE_PLATE.newItemStack();
			Object[] objects10 = new Object[]{"xx", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output12, objects10);

			ItemStack output11 = ModItems.ITEM_SIGN_SPRUCE.newItemStack(3);
			Object[] objects9 = new Object[]{"xxx", "xxx", " y ", 'x', new ItemStack(Blocks.planks, 1, 1), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output11, objects9);
			ItemStack output10 = ModItems.ITEM_SIGN_BIRCH.newItemStack(3);
			Object[] objects8 = new Object[]{"xxx", "xxx", " y ", 'x', new ItemStack(Blocks.planks, 1, 2), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output10, objects8);
			ItemStack output9 = ModItems.ITEM_SIGN_JUNGLE.newItemStack(3);
			Object[] objects7 = new Object[]{"xxx", "xxx", " y ", 'x', new ItemStack(Blocks.planks, 1, 3), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output9, objects7);
			ItemStack output8 = ModItems.ITEM_SIGN_ACACIA.newItemStack(3);
			Object[] objects6 = new Object[]{"xxx", "xxx", " y ", 'x', new ItemStack(Blocks.planks, 1, 4), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output8, objects6);
			ItemStack output7 = ModItems.ITEM_SIGN_DARK_OAK.newItemStack(3);
			Object[] objects5 = new Object[]{"xxx", "xxx", " y ", 'x', new ItemStack(Blocks.planks, 1, 5), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output7, objects5);

			ItemStack output6 = ModBlocks.CRIMSON_SIGN.newItemStack(3);
			Object[] objects4 = new Object[]{"xxx", "xxx", " y ", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output6, objects4);
			ItemStack output5 = ModBlocks.WARPED_SIGN.newItemStack(3);
			Object[] objects3 = new Object[]{"xxx", "xxx", " y ", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 1), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects3);
			ItemStack output4 = ModBlocks.MANGROVE_SIGN.newItemStack(3);
			Object[] objects2 = new Object[]{"xxx", "xxx", " y ", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 2), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output4, objects2);
			ItemStack output3 = ModBlocks.CHERRY_SIGN.newItemStack(3);
			Object[] objects1 = new Object[]{"xxx", "xxx", " y ", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 3), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, objects1);
			ItemStack output1 = ModBlocks.BAMBOO_SIGN.newItemStack(3);
			Object[] objects = new Object[]{"xxx", "xxx", " y ", 'x', ModBlocks.WOOD_PLANKS.newItemStack(1, 4), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, objects);
		}

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output4 = ModBlocks.RED_SANDSTONE.newItemStack();
			Object[] objects2 = new Object[]{"xx", "xx", 'x', new ItemStack(Blocks.sand, 1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output4, objects2);
			ItemStack output3 = ModBlocks.RED_SANDSTONE_SLAB.newItemStack(6, 1);
			Object[] objects1 = new Object[]{"xxx", 'x', ModBlocks.RED_SANDSTONE.newItemStack(1, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, objects1);
			ItemStack output1 = ModBlocks.RED_SANDSTONE_SLAB.newItemStack(6);
			Object[] objects = new Object[]{"xxx", 'x', ModBlocks.RED_SANDSTONE.newItemStack(1, OreDictionary.WILDCARD_VALUE)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, objects);
		}
		ItemStack output149 = ModBlocks.RED_SANDSTONE.newItemStack(1, 1);
		Object[] objects102 = new Object[]{"x", "x", 'x', ModBlocks.RED_SANDSTONE_SLAB.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output149, objects102);
		ItemStack output148 = ModBlocks.RED_SANDSTONE.newItemStack(4, 2);
		Object[] objects101 = new Object[]{"xx", "xx", 'x', ModBlocks.RED_SANDSTONE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output148, objects101);
		ItemStack output147 = ModBlocks.RED_SANDSTONE_STAIRS.newItemStack(4);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output147, "x  ", "xx ", "xxx", 'x', ModBlocks.RED_SANDSTONE.get());
		ItemStack output146 = ModBlocks.RED_SANDSTONE_WALL.newItemStack(6);
		Object[] objects100 = new Object[]{"xxx", "xxx", 'x', ModBlocks.RED_SANDSTONE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output146, objects100);

		if (ConfigBlocksItems.enableVanillaFences && !ModsList.GTNH.isLoaded()) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fence, 3), "xyx", "xyx", 'x', "plankWood", 'y', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.nether_brick_fence, 6), "xyx", "xyx", 'x', Blocks.nether_brick, 'y', "ingotBrickNether"));
		}

		for (EnumColor colour : EnumColor.VALUES) {
			ItemStack output1 = ModBlocks.BANNER.newItemStack(1, colour.getDamage());
			Object[] objects = new Object[]{"xxx", "xxx", " y ", 'x', new ItemStack(Blocks.wool, 1, colour.getDamage()), 'y', "stickWood"};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, objects);
		}
		GameRegistry.addRecipe(new RecipeDuplicatePattern());
		GameRegistry.addRecipe(new RecipeAddPattern());

		ItemStack output145 = ModItems.WOODEN_ARMORSTAND.newItemStack();
		Object[] objects99 = new Object[]{"xxx", " x ", "xyx", 'x', "stickWood", 'y', new ItemStack(Blocks.stone_slab)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output145, objects99);

		ItemStack output144 = ModItems.RABBIT_STEW.newItemStack();
		Object[] objects98 = new Object[]{" R ", "CPM", " B ", 'R', ModItems.RABBIT_COOKED.newItemStack(), 'C', Items.carrot, 'P', Items.baked_potato, 'M', Blocks.brown_mushroom, 'B', "bowlWood"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output144, objects98);
		ItemStack output143 = ModItems.RABBIT_STEW.newItemStack();
		Object[] objects97 = new Object[]{" R ", "CPD", " B ", 'R', ModItems.RABBIT_COOKED.newItemStack(), 'C', Items.carrot, 'P', Items.baked_potato, 'D', Blocks.red_mushroom, 'B', "bowlWood"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output143, objects97);
		ItemStack output1 = ModItems.RABBIT_COOKED.newItemStack();
		RecipeHelper.addSmelting(ModItems.RABBIT_RAW.get(), output1, 0.35F);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, new ItemStack(Items.leather), "xx", "xx", 'x', ModItems.RABBIT_HIDE.get());

		ItemStack input9 = ModBlocks.SPONGE.newItemStack(1, 1);
		ItemStack output11 = ConfigWorld.tileReplacementMode == -1 ? ModBlocks.SPONGE.newItemStack() : new ItemStack(Blocks.sponge);
		RecipeHelper.addSmelting(input9, output11, 0.15F);

		ItemStack output142 = ModItems.BEETROOT_SOUP.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output142, "xxx", "xxx", " y ", 'x', "cropBeetroot", 'y', "bowlWood");
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.dye, 1, 1), "cropBeetroot"));

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output3 = ModBlocks.END_BRICK_SLAB.newItemStack(6);
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, "xxx", 'x', ModBlocks.END_BRICKS.get());
		}
		ItemStack output141 = ModBlocks.END_BRICK_STAIRS.newItemStack(4);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output141, "x  ", "xx ", "xxx", 'x', ModBlocks.END_BRICKS.get());
		ItemStack output140 = ModBlocks.END_BRICK_WALL.newItemStack(6);
		Object[] objects96 = new Object[]{"xxx", "xxx", 'x', ModBlocks.END_BRICKS.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output140, objects96);

		ItemStack output139 = ModBlocks.PURPUR_BLOCK.newItemStack(4);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output139, "xx", "xx", 'x', ModItems.CHORUS_FRUIT_POPPED.get());
		ItemStack output138 = ModBlocks.PURPUR_STAIRS.newItemStack(4);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output138, "x  ", "xx ", "xxx", 'x', ModBlocks.PURPUR_BLOCK.get());
		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output3 = ModBlocks.PURPUR_SLAB.newItemStack(6);
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, "xxx", 'x', ModBlocks.PURPUR_BLOCK.get());
		}
		ItemStack output137 = ModBlocks.PURPUR_PILLAR.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output137, "x", "x", 'x', ModBlocks.PURPUR_SLAB.get());
		ItemStack output136 = ModBlocks.END_BRICKS.newItemStack(4);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output136, "xx", "xx", 'x', Blocks.end_stone);

		ItemStack input8 = ModItems.CHORUS_FRUIT.newItemStack();
		ItemStack output10 = ModItems.CHORUS_FRUIT_POPPED.newItemStack();
		RecipeHelper.addSmelting(input8, output10, 0.0F);
		ItemStack output135 = ModBlocks.END_ROD.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output135, "x", "y", 'x', Items.blaze_rod, 'y', ModItems.CHORUS_FRUIT_POPPED.get());

		ItemStack output226 = ModItems.DRAGON_BREATH.newItemStack();
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output226, new ItemStack(Items.potionitem, 1, 8195), ModItems.CHORUS_FRUIT.get(), ModItems.CHORUS_FRUIT.get());

		ItemStack output134 = ModItems.END_CRYSTAL.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output134, "xxx", "xyx", "xzx", 'x', "blockGlassColorless", 'y', Items.ender_eye, 'z', Items.ghast_tear);

		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, new ItemStack(Items.dye, 1, 1), ModBlocks.ROSE.get());
		ItemStack output133 = new ItemStack(Blocks.double_plant, 1, 4);
		Object[] objects95 = new Object[]{"xx", "xx", "xx", 'x', ModBlocks.ROSE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output133, objects95);
		ItemStack output132 = ModBlocks.ROSE.newItemStack(12);
		Object[] objects94 = new Object[]{"xx", 'x', new ItemStack(Blocks.double_plant, 1, 4)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output132, objects94);

		if (ModItems.TIPPED_ARROW.isEnabled() && ModItems.LINGERING_POTION.isEnabled()) {
			RecipeSorter.register(Tags.MOD_ID + ".RecipeTippedArrow", RecipeTippedArrow.class, Category.SHAPED, "after:minecraft:shaped");
			GameRegistry.addRecipe(new RecipeTippedArrow(ModItems.TIPPED_ARROW.newItemStack(), "xxx", "xyx", "xxx", 'x', Items.arrow, 'y', ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)));
		}

		if (!ModsList.GTNH.isLoaded()) {
			for (int i = 0; i < ModBlocks.BEDS.length; i++) {
				int j = i == 14 ? 15 : i;
				ItemStack output3 = ModBlocks.BEDS[i].newItemStack(1);
				Object[] objects = new Object[]{"###", "XXX", '#', new ItemStack(Blocks.wool, 1, j), 'X', "plankWood"};
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, objects);
				if (i > 0) {
					ItemStack output4 = ModBlocks.BEDS[i].newItemStack(1);
					Object[] objects1 = new Object[]{ModBlocks.BEDS[0].get(), ore_dyes[~j & 15]};
					RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output4, objects1);
				}
			}
			Object[] objects = new Object[]{ModBlocks.BEDS[0].newItemStack(), ore_dyes[1]};
			RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, new ItemStack(Items.bed, 1), objects);

			ItemStack output3 = ModBlocks.MAGMA.newItemStack();
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, "xx", "xx", 'x', new ItemStack(Items.magma_cream));
		}

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output6 = ModBlocks.RED_NETHERBRICK.newItemStack();
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output6, "xi", "ix", 'x', Items.nether_wart, 'i', "ingotBrickNether");
			ItemStack output5 = ModBlocks.RED_NETHERBRICK.newItemStack(1, 2);
			Object[] objects1 = new Object[]{"x", "x", 'x', new ItemStack(Blocks.stone_slab, 1, 6)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects1);
			ItemStack input = new ItemStack(Blocks.nether_brick, 1, 1);
			ItemStack output3 = ModBlocks.RED_NETHERBRICK.newItemStack();
			RecipeHelper.addSmelting(input, output3, .1F);
			ItemStack output4 = ModBlocks.RED_NETHERBRICK_SLAB.newItemStack(6);
			Object[] objects = new Object[]{"xxx", 'x', ModBlocks.RED_NETHERBRICK.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output4, objects);
		}
		ItemStack output131 = ModBlocks.RED_NETHERBRICK_STAIRS.newItemStack(4);
		Object[] objects93 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.RED_NETHERBRICK.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output131, objects93);
		ItemStack output130 = ModBlocks.RED_NETHER_BRICK_WALL.newItemStack(6);
		Object[] objects92 = new Object[]{"xxx", "xxx", 'x', ModBlocks.RED_NETHERBRICK.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output130, objects92);

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output4 = ModBlocks.NETHER_WART.newItemStack();
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output4, "xxx", "xxx", "xxx", 'x', Items.nether_wart);
			ItemStack output3 = ModBlocks.BONE.newItemStack();
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, "xxx", "xxx", "xxx", 'x', new ItemStack(Items.dye, 1, 15));
		}
		Object[] objects169 = new Object[]{ModBlocks.BONE.newItemStack()};
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, new ItemStack(Items.dye, 9, 15), objects169);

		if (!ModsList.GTNH.isLoaded()) {
			for (int i = 0; i < ore_dyes.length; i++) {
				int dye = ~i & 15;
				ItemStack output4 = ModBlocks.CONCRETE_POWDER.newItemStack(8, i);
				Object[] objects = new Object[]{ore_dyes[dye], new ItemStack(Blocks.sand, 1, 0), new ItemStack(Blocks.sand, 1, 0), new ItemStack(Blocks.sand, 1, 0), new ItemStack(Blocks.sand, 1, 0), Blocks.gravel, Blocks.gravel, Blocks.gravel, Blocks.gravel};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output4, objects);
				ItemStack input = new ItemStack(Blocks.stained_hardened_clay, 1, i);
				ItemStack output3 = ModBlocks.TERRACOTTA[i].newItemStack();
				RecipeHelper.addSmelting(input, output3, 0.1F);
			}
		}

		if (!ModsList.GTNH.isLoaded()) {
			if (!OreDictionary.getOres("nuggetIron").isEmpty()) {
				ItemStack output4 = ModBlocks.LANTERN.newItemStack();
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output4, "xxx", "xix", "xxx", 'x', "nuggetIron", 'i', Blocks.torch);
				ItemStack output3 = ModBlocks.SOUL_LANTERN.newItemStack();
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, "xxx", "xix", "xxx", 'x', "nuggetIron", 'i', ModBlocks.SOUL_TORCH.get());
			} else {
				ItemStack output4 = ModBlocks.LANTERN.newItemStack();
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output4, "i", "x", 'x', "ingotIron", 'i', Blocks.torch);
				ItemStack output3 = ModBlocks.SOUL_LANTERN.newItemStack();
				RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, "i", "x", 'x', "ingotIron", 'i', ModBlocks.SOUL_TORCH.get());
			}
		}
		ItemStack output129 = ModBlocks.SOUL_TORCH.newItemStack(4);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output129, "i", "x", "s", 'x', "stickWood", 'i', "coal", 's', "soulSand");
		ItemStack output128 = ModBlocks.SOUL_TORCH.newItemStack(4);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output128, "i", "x", "s", 'x', "stickWood", 'i', "coal", 's', "soulSoil");
		ItemStack output127 = ModBlocks.SOUL_TORCH.newItemStack(4);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output127, "i", "x", "s", 'x', "stickWood", 'i', "charcoal", 's', "soulSand");
		ItemStack output126 = ModBlocks.SOUL_TORCH.newItemStack(4);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output126, "i", "x", "s", 'x', "stickWood", 'i', "charcoal", 's', "soulSoil");

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output4 = ModBlocks.BARREL.newItemStack();
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output4, "xsx", "x x", "xsx", 'x', "plankWood", 's', "slabWood");

			ItemStack output3 = ModBlocks.BLUE_ICE.newItemStack();
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, "xxx", "xxx", "xxx", 'x', Blocks.packed_ice);
		}

		ItemStack output125 = ModBlocks.SMOKER.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output125, " l ", "lxl", " l ", 'x', Blocks.furnace, 'l', "logWood");

		ItemStack output124 = ModBlocks.BLAST_FURNACE.newItemStack();
		Object[] objects91 = new Object[]{"iii", "ixi", "sss", 'x', Blocks.furnace, 'i', "ingotIron", 's', ConfigBlocksItems.enableSmoothStone ? ModBlocks.SMOOTH_STONE.get() : Blocks.stone};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output124, objects91);

		ItemStack output4 = ModItems.NETHERITE_SCRAP.newItemStack();
		RecipeHelper.addSmelting(ModBlocks.ANCIENT_DEBRIS.get(), output4, 2F);
		ItemStack output225 = ModItems.NETHERITE_INGOT.newItemStack();
		Object[] objects168 = new Object[]{ModItems.NETHERITE_SCRAP.newItemStack(), ModItems.NETHERITE_SCRAP.newItemStack(), ModItems.NETHERITE_SCRAP.newItemStack(), ModItems.NETHERITE_SCRAP.newItemStack(), "ingotGold", "ingotGold", "ingotGold", "ingotGold"};
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output225, objects168);
		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output3 = ModBlocks.NETHERITE_BLOCK.newItemStack();
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, "xxx", "xxx", "xxx", 'x', ModItems.NETHERITE_INGOT.get());
		}
		ItemStack output123 = ModItems.NETHERITE_INGOT.newItemStack(9);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output123, "x", 'x', ModBlocks.NETHERITE_BLOCK.get());

		ItemStack input7 = ModBlocks.NETHER_GOLD_ORE.newItemStack();
		RecipeHelper.addSmelting(input7, new ItemStack(Items.gold_ingot), .1F);

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack input2 = new ItemStack(Blocks.stone);
			ItemStack output6 = ModBlocks.SMOOTH_STONE.newItemStack();
			RecipeHelper.addSmelting(input2, output6, .1F);

			if (ModBlocks.SMOOTH_STONE.isEnabled()) {
				ItemStack stoneTile = ExternalContent.Items.BLUEPOWER_CIRCUIT_PLATE.isEnabled() ? ExternalContent.Items.BLUEPOWER_CIRCUIT_PLATE.newItemStack(2)
						: ExternalContent.Items.PROJECTRED_CIRCUIT_PLATE.isEnabled() ? ExternalContent.Items.PROJECTRED_CIRCUIT_PLATE.newItemStack(2)
						: null;
				if (stoneTile != null) {
					removeFurnaceRecipeFor(new ItemStack(Blocks.stone), stoneTile);
					Object[] objects = new Object[]{"x", 'x', ModBlocks.SMOOTH_STONE.newItemStack()};
					RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, stoneTile, objects);
				}
			}

			ItemStack input1 = new ItemStack(Blocks.sandstone, 1, 0);
			ItemStack output5 = ModBlocks.SMOOTH_SANDSTONE.newItemStack();
			RecipeHelper.addSmelting(input1, output5, .1F);
			ItemStack output8 = ModBlocks.SMOOTH_SANDSTONE_SLAB.newItemStack(6);
			Object[] objects1 = new Object[]{"xxx", 'x', ModBlocks.SMOOTH_SANDSTONE.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output8, objects1);
			ItemStack input = ModBlocks.RED_SANDSTONE.newItemStack();
			ItemStack output3 = ModBlocks.SMOOTH_RED_SANDSTONE.newItemStack();
			RecipeHelper.addSmelting(input, output3, .1F);
			ItemStack output7 = ModBlocks.SMOOTH_RED_SANDSTONE_SLAB.newItemStack(6);
			Object[] objects = new Object[]{"xxx", 'x', ModBlocks.SMOOTH_RED_SANDSTONE.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output7, objects);
		}
		ItemStack output122 = ModBlocks.SMOOTH_SANDSTONE_STAIRS.newItemStack(4);
		Object[] objects90 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.SMOOTH_SANDSTONE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output122, objects90);
		ItemStack output121 = ModBlocks.SMOOTH_RED_SANDSTONE_STAIRS.newItemStack(4);
		Object[] objects89 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.SMOOTH_RED_SANDSTONE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output121, objects89);

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack input = new ItemStack(Blocks.quartz_block, 1, 0);
			ItemStack output3 = ModBlocks.SMOOTH_QUARTZ.newItemStack();
			RecipeHelper.addSmelting(input, output3, .1F);
			ItemStack output5 = ModBlocks.SMOOTH_QUARTZ_SLAB.newItemStack(6);
			Object[] objects = new Object[]{"xxx", 'x', ModBlocks.SMOOTH_QUARTZ.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects);
		}
		ItemStack output120 = ModBlocks.SMOOTH_QUARTZ_STAIRS.newItemStack(4);
		Object[] objects88 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.SMOOTH_QUARTZ.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output120, objects88);

		ItemStack output119 = ModBlocks.QUARTZ_BRICKS.newItemStack(4);
		Object[] objects87 = new Object[]{"xx", "xx", 'x', new ItemStack(Blocks.quartz_block, 1, 0)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output119, objects87);

		ItemStack output224 = ModItems.DYE.newItemStack();
		Object[] objects167 = new Object[]{ModBlocks.LILY_OF_THE_VALLEY.newItemStack()};
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output224, objects167);
		ItemStack output223 = ModItems.DYE.newItemStack(1, 1);
		Object[] objects166 = new Object[]{ModBlocks.CORNFLOWER.newItemStack()};
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output223, objects166);
		ItemStack output222 = ModItems.DYE.newItemStack(1, 3);
		Object[] objects165 = new Object[]{ModBlocks.WITHER_ROSE.newItemStack()};
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output222, objects165);
		ItemStack output221 = ModItems.DYE.newItemStack();
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output221, new ItemStack(Items.dye, 1, 15));
		ItemStack output220 = ModItems.DYE.newItemStack(1, 1);
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output220, new ItemStack(Items.dye, 1, 4));
		ItemStack output219 = ModItems.DYE.newItemStack(1, 2);
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output219, new ItemStack(Items.dye, 1, 3));
		ItemStack output218 = ModItems.DYE.newItemStack(1, 3);
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output218, new ItemStack(Items.dye, 1, 0));

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output6 = ModBlocks.COPPER_BLOCK.newItemStack(1);
			Object[] objects2 = new Object[]{"xxx", "xxx", "xxx", 'x', ModItems.COPPER_INGOT.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output6, objects2);
			ItemStack output5 = ModItems.COPPER_INGOT.newItemStack(9);
			Object[] objects1 = new Object[]{"x", 'x', ModBlocks.COPPER_BLOCK.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects1);
			ItemStack output3 = ModItems.COPPER_INGOT.newItemStack(9);
			Object[] objects = new Object[]{"x", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 8)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, objects);
		}

		ItemStack input6 = ModBlocks.COPPER_ORE.newItemStack();
		ItemStack output9 = ModItems.COPPER_INGOT.newItemStack();
		RecipeHelper.addSmelting(input6, output9, .7F);

		//Copper block to cut copper block
		ItemStack output118 = ModBlocks.COPPER_BLOCK.newItemStack(4, 4);
		Object[] objects86 = new Object[]{"xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output118, objects86);
		ItemStack output117 = ModBlocks.COPPER_BLOCK.newItemStack(4, 5);
		Object[] objects85 = new Object[]{"xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output117, objects85);
		ItemStack output116 = ModBlocks.COPPER_BLOCK.newItemStack(4, 6);
		Object[] objects84 = new Object[]{"xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output116, objects84);
		ItemStack output115 = ModBlocks.COPPER_BLOCK.newItemStack(4, 7);
		Object[] objects83 = new Object[]{"xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 3)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output115, objects83);

		//Waxed copper block to waxed cut copper block
		ItemStack output114 = ModBlocks.COPPER_BLOCK.newItemStack(4, 12);
		Object[] objects82 = new Object[]{"xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 8)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output114, objects82);
		ItemStack output113 = ModBlocks.COPPER_BLOCK.newItemStack(4, 13);
		Object[] objects81 = new Object[]{"xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 9)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output113, objects81);
		ItemStack output112 = ModBlocks.COPPER_BLOCK.newItemStack(4, 14);
		Object[] objects80 = new Object[]{"xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 10)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output112, objects80);
		ItemStack output111 = ModBlocks.COPPER_BLOCK.newItemStack(4, 15);
		Object[] objects79 = new Object[]{"xx", "xx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 11)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output111, objects79);

		//Copper block to copper grate
		ItemStack output110 = ModBlocks.COPPER_GRATE.newItemStack(4);
		Object[] objects78 = new Object[]{" x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output110, objects78);
		ItemStack output109 = ModBlocks.COPPER_GRATE.newItemStack(4, 1);
		Object[] objects77 = new Object[]{" x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output109, objects77);
		ItemStack output108 = ModBlocks.COPPER_GRATE.newItemStack(4, 2);
		Object[] objects76 = new Object[]{" x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output108, objects76);
		ItemStack output107 = ModBlocks.COPPER_GRATE.newItemStack(4, 3);
		Object[] objects75 = new Object[]{" x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 3)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output107, objects75);

		//Waxed copper block to waxed copper grate
		ItemStack output106 = ModBlocks.COPPER_GRATE.newItemStack(4, 4);
		Object[] objects74 = new Object[]{" x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 8)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output106, objects74);
		ItemStack output105 = ModBlocks.COPPER_GRATE.newItemStack(4, 5);
		Object[] objects73 = new Object[]{" x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 9)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output105, objects73);
		ItemStack output104 = ModBlocks.COPPER_GRATE.newItemStack(4, 6);
		Object[] objects72 = new Object[]{" x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 10)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output104, objects72);
		ItemStack output103 = ModBlocks.COPPER_GRATE.newItemStack(4, 7);
		Object[] objects71 = new Object[]{" x ", "x x", " x ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 11)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output103, objects71);

		//Copper block to copper grate
		ItemStack output102 = ModBlocks.COPPER_BULB.newItemStack(4);
		Object[] objects70 = new Object[]{" x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(), 'b', Items.blaze_rod, 'r', "dustRedstone"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output102, objects70);
		ItemStack output101 = ModBlocks.COPPER_BULB.newItemStack(4, 1);
		Object[] objects69 = new Object[]{" x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 1), 'b', Items.blaze_rod, 'r', "dustRedstone"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output101, objects69);
		ItemStack output100 = ModBlocks.COPPER_BULB.newItemStack(4, 2);
		Object[] objects68 = new Object[]{" x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 2), 'b', Items.blaze_rod, 'r', "dustRedstone"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output100, objects68);
		ItemStack output99 = ModBlocks.COPPER_BULB.newItemStack(4, 3);
		Object[] objects67 = new Object[]{" x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 3), 'b', Items.blaze_rod, 'r', "dustRedstone"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output99, objects67);

		//Waxed copper block to waxed copper grate
		ItemStack output98 = ModBlocks.COPPER_BULB.newItemStack(4, 8);
		Object[] objects66 = new Object[]{" x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 8), 'b', Items.blaze_rod, 'r', "dustRedstone"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output98, objects66);
		ItemStack output97 = ModBlocks.COPPER_BULB.newItemStack(4, 9);
		Object[] objects65 = new Object[]{" x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 9), 'b', Items.blaze_rod, 'r', "dustRedstone"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output97, objects65);
		ItemStack output96 = ModBlocks.COPPER_BULB.newItemStack(4, 10);
		Object[] objects64 = new Object[]{" x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 10), 'b', Items.blaze_rod, 'r', "dustRedstone"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output96, objects64);
		ItemStack output95 = ModBlocks.COPPER_BULB.newItemStack(4, 11);
		Object[] objects63 = new Object[]{" x ", "xbx", " r ", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 11), 'b', Items.blaze_rod, 'r', "dustRedstone"};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output95, objects63);

		//Copper block to copper grate
		ItemStack output94 = ModBlocks.CHISELED_COPPER.newItemStack();
		Object[] objects62 = new Object[]{"x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output94, objects62);
		ItemStack output93 = ModBlocks.CHISELED_COPPER.newItemStack(1, 1);
		Object[] objects61 = new Object[]{"x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output93, objects61);
		ItemStack output92 = ModBlocks.CHISELED_COPPER.newItemStack(1, 2);
		Object[] objects60 = new Object[]{"x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output92, objects60);
		ItemStack output91 = ModBlocks.CHISELED_COPPER.newItemStack(1, 3);
		Object[] objects59 = new Object[]{"x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 3)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output91, objects59);

		//Waxed copper block to waxed copper grate
		ItemStack output90 = ModBlocks.CHISELED_COPPER.newItemStack(1, 4);
		Object[] objects58 = new Object[]{"x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 4)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output90, objects58);
		ItemStack output89 = ModBlocks.CHISELED_COPPER.newItemStack(1, 5);
		Object[] objects57 = new Object[]{"x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 5)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output89, objects57);
		ItemStack output88 = ModBlocks.CHISELED_COPPER.newItemStack(1, 6);
		Object[] objects56 = new Object[]{"x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 6)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output88, objects56);
		ItemStack output87 = ModBlocks.CHISELED_COPPER.newItemStack(1, 7);
		Object[] objects55 = new Object[]{"x", "x", 'x', ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 7)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output87, objects55);

		//Cut copper to cut copper slab
		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output14 = ModBlocks.CUT_COPPER_SLAB.newItemStack(6);
			Object[] objects7 = new Object[]{"xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 4)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output14, objects7);
			ItemStack output13 = ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 1);
			Object[] objects6 = new Object[]{"xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 5)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output13, objects6);
			ItemStack output12 = ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 2);
			Object[] objects5 = new Object[]{"xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 6)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output12, objects5);
			ItemStack output8 = ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 3);
			Object[] objects4 = new Object[]{"xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 7)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output8, objects4);

			//Waxed cut copper to waxed cut copper slab
			ItemStack output7 = ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 4);
			Object[] objects3 = new Object[]{"xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 12)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output7, objects3);
			ItemStack output6 = ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 5);
			Object[] objects2 = new Object[]{"xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 13)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output6, objects2);
			ItemStack output5 = ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 6);
			Object[] objects1 = new Object[]{"xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 14)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects1);
			ItemStack output3 = ModBlocks.CUT_COPPER_SLAB.newItemStack(6, 7);
			Object[] objects = new Object[]{"xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 15)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, objects);
		}

		//Cut copper to cut copper stairs
		ItemStack output86 = ModBlocks.CUT_COPPER_STAIRS.newItemStack(4);
		Object[] objects54 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 4)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output86, objects54);
		ItemStack output85 = ModBlocks.EXPOSED_CUT_COPPER_STAIRS.newItemStack(4);
		Object[] objects53 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 5)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output85, objects53);
		ItemStack output84 = ModBlocks.WEATHERED_CUT_COPPER_STAIRS.newItemStack(4);
		Object[] objects52 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 6)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output84, objects52);
		ItemStack output83 = ModBlocks.OXIDIZED_CUT_COPPER_STAIRS.newItemStack(4);
		Object[] objects51 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 7)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output83, objects51);

		//Waxed cut copper to waxed cut copper stairs
		ItemStack output82 = ModBlocks.WAXED_CUT_COPPER_STAIRS.newItemStack(4);
		Object[] objects50 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 12)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output82, objects50);
		ItemStack output81 = ModBlocks.WAXED_EXPOSED_CUT_COPPER_STAIRS.newItemStack(4);
		Object[] objects49 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 13)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output81, objects49);
		ItemStack output80 = ModBlocks.WAXED_WEATHERED_CUT_COPPER_STAIRS.newItemStack(4);
		Object[] objects48 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 14)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output80, objects48);
		ItemStack output79 = ModBlocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS.newItemStack(4);
		Object[] objects47 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.COPPER_BLOCK.newItemStack(1, 15)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output79, objects47);

		//Copper door/trapdoor
		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output5 = ModBlocks.COPPER_DOOR.newItemStack(3);
			Object[] objects1 = new Object[]{"xx", "xx", "xx", 'x', ModItems.COPPER_INGOT.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects1);
			ItemStack output3 = ModBlocks.COPPER_TRAPDOOR.newItemStack(2);
			Object[] objects = new Object[]{"xx", "xx", 'x', ModItems.COPPER_INGOT.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output3, objects);
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
				ItemStack output42 = ModBlocks.COPPER_BLOCK.newItemStack(1, 8);
				Object[] objects35 = new Object[]{waxString, ModBlocks.COPPER_BLOCK.newItemStack(1)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output42, objects35);
				ItemStack output41 = ModBlocks.COPPER_BLOCK.newItemStack(1, 9);
				Object[] objects34 = new Object[]{waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 1)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output41, objects34);
				ItemStack output40 = ModBlocks.COPPER_BLOCK.newItemStack(1, 10);
				Object[] objects33 = new Object[]{waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 2)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output40, objects33);
				ItemStack output39 = ModBlocks.COPPER_BLOCK.newItemStack(1, 11);
				Object[] objects32 = new Object[]{waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 3)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output39, objects32);

				//Cut copper block to waxed cut copper blocks
				ItemStack output38 = ModBlocks.COPPER_BLOCK.newItemStack(1, 12);
				Object[] objects31 = new Object[]{waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 4)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output38, objects31);
				ItemStack output37 = ModBlocks.COPPER_BLOCK.newItemStack(1, 13);
				Object[] objects30 = new Object[]{waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 5)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output37, objects30);
				ItemStack output36 = ModBlocks.COPPER_BLOCK.newItemStack(1, 14);
				Object[] objects29 = new Object[]{waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 6)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output36, objects29);
				ItemStack output35 = ModBlocks.COPPER_BLOCK.newItemStack(1, 15);
				Object[] objects28 = new Object[]{waxString, ModBlocks.COPPER_BLOCK.newItemStack(1, 7)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output35, objects28);

				//Copper grate to waxed copper grate
				ItemStack output34 = ModBlocks.COPPER_GRATE.newItemStack(1, 4);
				Object[] objects27 = new Object[]{waxString, ModBlocks.COPPER_GRATE.newItemStack(1)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output34, objects27);
				ItemStack output33 = ModBlocks.COPPER_GRATE.newItemStack(1, 5);
				Object[] objects26 = new Object[]{waxString, ModBlocks.COPPER_GRATE.newItemStack(1, 1)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output33, objects26);
				ItemStack output32 = ModBlocks.COPPER_GRATE.newItemStack(1, 6);
				Object[] objects25 = new Object[]{waxString, ModBlocks.COPPER_GRATE.newItemStack(1, 2)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output32, objects25);
				ItemStack output31 = ModBlocks.COPPER_GRATE.newItemStack(1, 7);
				Object[] objects24 = new Object[]{waxString, ModBlocks.COPPER_GRATE.newItemStack(1, 3)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output31, objects24);

				//Copper grate to waxed copper grate
				ItemStack output30 = ModBlocks.CHISELED_COPPER.newItemStack(1, 4);
				Object[] objects23 = new Object[]{waxString, ModBlocks.CHISELED_COPPER.newItemStack(1)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output30, objects23);
				ItemStack output29 = ModBlocks.CHISELED_COPPER.newItemStack(1, 5);
				Object[] objects22 = new Object[]{waxString, ModBlocks.CHISELED_COPPER.newItemStack(1, 1)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output29, objects22);
				ItemStack output28 = ModBlocks.CHISELED_COPPER.newItemStack(1, 6);
				Object[] objects21 = new Object[]{waxString, ModBlocks.CHISELED_COPPER.newItemStack(1, 2)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output28, objects21);
				ItemStack output27 = ModBlocks.CHISELED_COPPER.newItemStack(1, 7);
				Object[] objects20 = new Object[]{waxString, ModBlocks.CHISELED_COPPER.newItemStack(1, 3)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output27, objects20);

				//Cut copper block to waxed cut copper blocks
				ItemStack output26 = ModBlocks.COPPER_BULB.newItemStack(1, 12);
				Object[] objects19 = new Object[]{waxString, ModBlocks.COPPER_BULB.newItemStack(1, 4)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output26, objects19);
				ItemStack output25 = ModBlocks.COPPER_BULB.newItemStack(1, 13);
				Object[] objects18 = new Object[]{waxString, ModBlocks.COPPER_BULB.newItemStack(1, 5)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output25, objects18);
				ItemStack output24 = ModBlocks.COPPER_BULB.newItemStack(1, 14);
				Object[] objects17 = new Object[]{waxString, ModBlocks.COPPER_BULB.newItemStack(1, 6)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output24, objects17);
				ItemStack output23 = ModBlocks.COPPER_BULB.newItemStack(1, 15);
				Object[] objects16 = new Object[]{waxString, ModBlocks.COPPER_BULB.newItemStack(1, 7)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output23, objects16);

				//Cut copper slabs to waxed cut copper slabs
				ItemStack output22 = ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 4);
				Object[] objects15 = new Object[]{waxString, ModBlocks.CUT_COPPER_SLAB.newItemStack(1)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output22, objects15);
				ItemStack output21 = ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 5);
				Object[] objects14 = new Object[]{waxString, ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 1)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output21, objects14);
				ItemStack output20 = ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 6);
				Object[] objects13 = new Object[]{waxString, ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 2)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output20, objects13);
				ItemStack output19 = ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 7);
				Object[] objects12 = new Object[]{waxString, ModBlocks.CUT_COPPER_SLAB.newItemStack(1, 3)};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output19, objects12);

				//Cut copper stairs to waxed cut copper stairs
				ItemStack output18 = ModBlocks.WAXED_CUT_COPPER_STAIRS.newItemStack();
				Object[] objects11 = new Object[]{waxString, ModBlocks.CUT_COPPER_STAIRS.newItemStack()};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output18, objects11);
				ItemStack output17 = ModBlocks.WAXED_EXPOSED_CUT_COPPER_STAIRS.newItemStack();
				Object[] objects10 = new Object[]{waxString, ModBlocks.EXPOSED_CUT_COPPER_STAIRS.newItemStack()};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output17, objects10);
				ItemStack output16 = ModBlocks.WAXED_WEATHERED_CUT_COPPER_STAIRS.newItemStack();
				Object[] objects9 = new Object[]{waxString, ModBlocks.WEATHERED_CUT_COPPER_STAIRS.newItemStack()};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output16, objects9);
				ItemStack output15 = ModBlocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS.newItemStack();
				Object[] objects8 = new Object[]{waxString, ModBlocks.OXIDIZED_CUT_COPPER_STAIRS.newItemStack()};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output15, objects8);

				//Copper door to waxed copper door
				ItemStack output14 = ModBlocks.WAXED_COPPER_DOOR.newItemStack();
				Object[] objects7 = new Object[]{waxString, ModBlocks.COPPER_DOOR.newItemStack()};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output14, objects7);
				ItemStack output13 = ModBlocks.WAXED_EXPOSED_COPPER_DOOR.newItemStack();
				Object[] objects6 = new Object[]{waxString, ModBlocks.EXPOSED_COPPER_DOOR.newItemStack()};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output13, objects6);
				ItemStack output12 = ModBlocks.WAXED_WEATHERED_COPPER_DOOR.newItemStack();
				Object[] objects5 = new Object[]{waxString, ModBlocks.WEATHERED_COPPER_DOOR.newItemStack()};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output12, objects5);
				ItemStack output8 = ModBlocks.WAXED_OXIDIZED_COPPER_DOOR.newItemStack();
				Object[] objects4 = new Object[]{waxString, ModBlocks.OXIDIZED_COPPER_DOOR.newItemStack()};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output8, objects4);

				//Copper trapdoor to waxed cut copper trapdoor
				ItemStack output7 = ModBlocks.WAXED_COPPER_TRAPDOOR.newItemStack();
				Object[] objects3 = new Object[]{waxString, ModBlocks.COPPER_TRAPDOOR.newItemStack()};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output7, objects3);
				ItemStack output6 = ModBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR.newItemStack();
				Object[] objects2 = new Object[]{waxString, ModBlocks.EXPOSED_COPPER_TRAPDOOR.newItemStack()};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output6, objects2);
				ItemStack output5 = ModBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR.newItemStack();
				Object[] objects1 = new Object[]{waxString, ModBlocks.WEATHERED_COPPER_TRAPDOOR.newItemStack()};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output5, objects1);
				ItemStack output3 = ModBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR.newItemStack();
				Object[] objects = new Object[]{waxString, ModBlocks.OXIDIZED_COPPER_TRAPDOOR.newItemStack()};
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output3, objects);
			}
		}

		ItemStack output3 = ModBlocks.DEEPSLATE.newItemStack();
		RecipeHelper.addSmelting(ModBlocks.COBBLED_DEEPSLATE.get(), output3, 0.1F);
		ItemStack input5 = ModBlocks.DEEPSLATE_BRICKS.newItemStack();
		ItemStack output8 = ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 1);
		RecipeHelper.addSmelting(input5, output8, 0.1F);
		ItemStack input4 = ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2);
		ItemStack output7 = ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 3);
		RecipeHelper.addSmelting(input4, output7, 0.1F);

		ItemStack output78 = ModBlocks.POLISHED_DEEPSLATE.newItemStack(4);
		Object[] objects46 = new Object[]{"xx", "xx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output78, objects46);
		ItemStack output77 = ModBlocks.DEEPSLATE_BRICKS.newItemStack(4);
		Object[] objects45 = new Object[]{"xx", "xx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output77, objects45);
		ItemStack output76 = ModBlocks.DEEPSLATE_BRICKS.newItemStack(4, 2);
		Object[] objects44 = new Object[]{"xx", "xx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output76, objects44);
		ItemStack output75 = ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 4);
		Object[] objects43 = new Object[]{"x", "x", 'x', ModBlocks.DEEPSLATE_SLAB.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output75, objects43);

		ItemStack output74 = ModBlocks.TUFF.newItemStack(4, 1);
		Object[] objects42 = new Object[]{"xx", "xx", 'x', ModBlocks.TUFF.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output74, objects42);
		ItemStack output73 = ModBlocks.TUFF.newItemStack(4, 2);
		Object[] objects41 = new Object[]{"xx", "xx", 'x', ModBlocks.TUFF.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output73, objects41);
		ItemStack output72 = ModBlocks.TUFF.newItemStack(1, 3);
		Object[] objects40 = new Object[]{"x", "x", 'x', ModBlocks.TUFF_SLAB.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output72, objects40);
		ItemStack output71 = ModBlocks.TUFF.newItemStack(1, 4);
		Object[] objects39 = new Object[]{"x", "x", 'x', ModBlocks.TUFF_SLAB.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output71, objects39);

		ItemStack output70 = ModBlocks.COBBLED_DEEPSLATE_STAIRS.newItemStack(4);
		Object[] objects38 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output70, objects38);
		ItemStack output69 = ModBlocks.POLISHED_DEEPSLATE_STAIRS.newItemStack(4);
		Object[] objects37 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output69, objects37);
		ItemStack output68 = ModBlocks.DEEPSLATE_BRICK_STAIRS.newItemStack(4);
		Object[] objects36 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output68, objects36);
		ItemStack output67 = ModBlocks.DEEPSLATE_TILE_STAIRS.newItemStack(4);
		Object[] objects35 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output67, objects35);
		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output13 = ModBlocks.DEEPSLATE_SLAB.newItemStack(6);
			Object[] objects3 = new Object[]{"xxx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output13, objects3);
			ItemStack output12 = ModBlocks.DEEPSLATE_SLAB.newItemStack(6, 1);
			Object[] objects2 = new Object[]{"xxx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output12, objects2);
			ItemStack output6 = ModBlocks.DEEPSLATE_BRICK_SLAB.newItemStack(6);
			Object[] objects1 = new Object[]{"xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output6, objects1);
			ItemStack output5 = ModBlocks.DEEPSLATE_BRICK_SLAB.newItemStack(6, 1);
			Object[] objects = new Object[]{"xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects);
		}
		ItemStack output66 = ModBlocks.DEEPSLATE_WALL.newItemStack(6);
		Object[] objects34 = new Object[]{"xxx", "xxx", 'x', ModBlocks.COBBLED_DEEPSLATE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output66, objects34);
		ItemStack output65 = ModBlocks.DEEPSLATE_WALL.newItemStack(6, 1);
		Object[] objects33 = new Object[]{"xxx", "xxx", 'x', ModBlocks.POLISHED_DEEPSLATE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output65, objects33);
		ItemStack output64 = ModBlocks.DEEPSLATE_BRICK_WALL.newItemStack(6);
		Object[] objects32 = new Object[]{"xxx", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output64, objects32);
		ItemStack output63 = ModBlocks.DEEPSLATE_BRICK_WALL.newItemStack(6, 1);
		Object[] objects31 = new Object[]{"xxx", "xxx", 'x', ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output63, objects31);

		ItemStack output62 = ModBlocks.TUFF_STAIRS.newItemStack(4);
		Object[] objects30 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.TUFF.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output62, objects30);
		ItemStack output61 = ModBlocks.POLISHED_TUFF_STAIRS.newItemStack(4);
		Object[] objects29 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.TUFF.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output61, objects29);
		ItemStack output60 = ModBlocks.TUFF_BRICK_STAIRS.newItemStack(4);
		Object[] objects28 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.TUFF.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output60, objects28);
		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output12 = ModBlocks.TUFF_SLAB.newItemStack(6);
			Object[] objects2 = new Object[]{"xxx", 'x', ModBlocks.TUFF.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output12, objects2);
			ItemStack output6 = ModBlocks.TUFF_SLAB.newItemStack(6, 1);
			Object[] objects1 = new Object[]{"xxx", 'x', ModBlocks.TUFF.newItemStack(1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output6, objects1);
			ItemStack output5 = ModBlocks.TUFF_SLAB.newItemStack(6, 2);
			Object[] objects = new Object[]{"xxx", 'x', ModBlocks.TUFF.newItemStack(1, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects);
		}
		ItemStack output59 = ModBlocks.TUFF_WALL.newItemStack(6);
		Object[] objects27 = new Object[]{"xxx", "xxx", 'x', ModBlocks.TUFF.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output59, objects27);
		ItemStack output58 = ModBlocks.TUFF_WALL.newItemStack(6, 1);
		Object[] objects26 = new Object[]{"xxx", "xxx", 'x', ModBlocks.TUFF.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output58, objects26);
		ItemStack output57 = ModBlocks.TUFF_WALL.newItemStack(6, 2);
		Object[] objects25 = new Object[]{"xxx", "xxx", 'x', ModBlocks.TUFF.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output57, objects25);


		// Mud Recipes
		ItemStack output217 = ModBlocks.MUDDY_MANGROVE_ROOTS.newItemStack(1);
		Object[] objects164 = new Object[]{ModBlocks.MUD.newItemStack(), ModBlocks.MANGROVE_ROOTS.newItemStack()};
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output217, objects164);

		// VANILLA
		ItemStack output216 = ModBlocks.PACKED_MUD.newItemStack(1, 0);
		Object[] objects163 = new Object[]{ModBlocks.MUD.newItemStack(1), new ItemStack(Items.wheat, 1)};
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output216, objects163);
		ItemStack output56 = ModBlocks.PACKED_MUD.newItemStack(4, 1);
		Object[] objects24 = new Object[]{"xx", "xx", 'x', ModBlocks.PACKED_MUD.newItemStack(1, 0)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output56, objects24);

		ItemStack output55 = ModBlocks.MUD_BRICK_STAIRS.newItemStack(4);
		Object[] objects23 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.PACKED_MUD.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output55, objects23);
		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output5 = ModBlocks.MUD_BRICK_SLAB.newItemStack(6, 0);
			Object[] objects = new Object[]{"xxx", 'x', ModBlocks.PACKED_MUD.newItemStack(1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects);
		}
		ItemStack output54 = ModBlocks.MUD_BRICK_WALL.newItemStack(6, 0);
		Object[] objects22 = new Object[]{"xxx", "xxx", 'x', ModBlocks.PACKED_MUD.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output54, objects22);

		ItemStack output53 = ModBlocks.MOSS_BLOCK.newItemStack(1, 0);
		Object[] objects21 = new Object[]{"xxx", "xyx", "xxx", 'x', new ItemStack(Blocks.vine, 1), 'y', new ItemStack(Blocks.dirt, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output53, objects21);

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
			RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, stew, Blocks.red_mushroom, Blocks.brown_mushroom, Items.bowl, getStewFlowers().get(i));
		}

		if (!ModsList.GTNH.isLoaded()) {
			for (Map.Entry<String, ItemNewBoat.BoatInfo> entry : ItemNewBoat.BOAT_INFO.entrySet()) {
				String key = entry.getKey();
				if (key == null) continue;
				boolean isOak = entry.getKey().equals("minecraft:oak");
				ItemStack boat = (isOak && ConfigBlocksItems.replaceOldBoats ? new ItemStack(Items.boat) : entry.getValue().getBoatItem());
				if (key.endsWith("_chest")) {
					ItemStack inputBoat = ItemNewBoat.BOAT_INFO.get(key.substring(0, key.indexOf("_chest"))).getBoatItem();
					RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, boat, "chestWood", inputBoat);
				} else {
					if (isOak) { //We're using the plankWood tag for this, so it needs to be in the vanilla sorter
						GameRegistry.addRecipe(new ShapedOreRecipe(boat, (ConfigBlocksItems.replaceOldBoats ? "x x" : "xyx"), "xxx", 'x', "plankWood", 'y', new ItemStack(Items.wooden_shovel, 1)));
					} else { //Not a tagged recipe, we sort this before vanilla recipes, so it takes precedent over them
						Object[] objects = new Object[]{(ConfigBlocksItems.replaceOldBoats ? "x x" : "xyx"), "xxx", 'x', entry.getValue().getPlank(), 'y', new ItemStack(Items.wooden_shovel, 1)};
						RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, boat, objects);
					}
				}
			}
			if (!ConfigBlocksItems.replaceOldBoats) {
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, new ItemStack(Items.boat), "boatWood");
				ItemStack output5 = ModItems.BOATS[0].newItemStack();
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output5, Items.wooden_shovel, Items.boat);
			}

			ItemStack output5 = ModBlocks.SHULKER_BOX.newItemStack();
			Object[] objects = new Object[]{"x", "c", "x", 'x', ModItems.SHULKER_SHELL.newItemStack(), 'c', new ItemStack(Blocks.chest)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects);
		}
		if (ModBlocks.SHULKER_BOX.isEnabled()) {
			for (int i = ore_dyes.length - 1; i >= 0; i--) {//Dyed box recipes
				ItemStack shulker = ModBlocks.SHULKER_BOX.newItemStack();
				shulker.setTagCompound(new NBTTagCompound());
				shulker.getTagCompound().setByte("Color", (byte) (16 - i));
				GameRegistry.addRecipe(new RecipeDyedShulkerBox(shulker, new Object[]{ModBlocks.SHULKER_BOX.get(), ore_dyes[i]}));
			}
		}
		ItemStack output52 = ModItems.SHULKER_BOX_UPGRADE.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output52, "XXX", "XYX", "XXX", 'X', "ingotIron", 'Y', ModItems.SHULKER_SHELL.get());
		ItemStack output51 = ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 1);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output51, "XXX", "XYX", "XXX", 'X', "ingotCopper", 'Y', ModItems.SHULKER_SHELL.get());
		ItemStack output50 = ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 2);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output50, "XYX", "XXX", "XXX", 'X', "ingotGold", 'Y', "ingotIron");
		ItemStack output49 = ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 3);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output49, "GXG", "GYG", "GXG", 'X', "gemDiamond", 'Y', "ingotGold", 'G', "blockGlassColorless");
		ItemStack output48 = ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 4);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output48, "XYX", "XXX", "XXX", 'X', Blocks.obsidian, 'Y', "blockGlassColorless");
		ItemStack output47 = ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 5);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output47, "XYX", "XXX", "XXX", 'X', "blockGlassColorless", 'Y', Blocks.obsidian);
		ItemStack output46 = ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 6);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output46, "GGG", "XYX", "XGX", 'X', "ingotIron", 'Y', "ingotCopper", 'G', "blockGlassColorless");
		ItemStack output45 = ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 7);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output45, "XYX", "XXX", "XXX", 'X', "ingotSilver", 'Y', "ingotCopper");
		ItemStack output44 = ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, 8);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output44, "XYX", "GGG", "XGX", 'X', "ingotGold", 'Y', "ingotSilver", 'G', "blockGlassColorless");

		if (ConfigWorld.tileReplacementMode == -1) {
			//We keep the original enabled checks inside of the booleans and use the original addShapedRecipe function because we need to check it anyways for recipe removal
			if (ModBlocks.ANVIL.isEnabled()) {
				GameRegistry.addShapedRecipe(ModBlocks.ANVIL.newItemStack(), "BBB", " I ", "III", 'I', new ItemStack(Items.iron_ingot), 'B', new ItemStack(Blocks.iron_block));
				RecipeHelper.removeFirstRecipeWithOutput(Blocks.anvil, 0, false);
			}

			if (ModBlocks.BREWING_STAND.isEnabled()) {
				GameRegistry.addShapedRecipe(ModBlocks.BREWING_STAND.newItemStack(), " B ", "CCC", 'C', new ItemStack(Blocks.cobblestone), 'B', new ItemStack(Items.blaze_rod));
				RecipeHelper.removeFirstRecipeWithOutput(Blocks.brewing_stand, 0, false);
			}

			if (ModBlocks.BEACON.isEnabled()) {
				GameRegistry.addShapedRecipe(ModBlocks.BEACON.newItemStack(), "GGG", "GNG", "OOO", 'G', new ItemStack(Blocks.glass), 'N', new ItemStack(Items.nether_star), 'O', new ItemStack(Blocks.obsidian));
				RecipeHelper.removeFirstRecipeWithOutput(Blocks.beacon, 0, false);
			}

			if (ModBlocks.ENCHANTMENT_TABLE.isEnabled()) {
				GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.ENCHANTMENT_TABLE.newItemStack(), " B ", "D#D", "###", '#', Blocks.obsidian, 'B', Items.book, 'D', "gemDiamond"));
				RecipeHelper.removeFirstRecipeWithOutput(Blocks.enchanting_table, 0, false);
			}

			if (ModBlocks.SPONGE.isEnabled()) {
				ItemStack output6 = ModBlocks.SPONGE.newItemStack();
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output6, Blocks.sponge);
				//For recipes that want the vanilla sponge you can convert it
				ItemStack output5 = new ItemStack(Blocks.sponge);
				RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output5, ModBlocks.SPONGE.get());
			}
		}

		ItemStack output43 = ModBlocks.STONE_WALL.newItemStack(6);
		Object[] objects20 = new Object[]{"BBB", "BBB", 'B', new ItemStack(Blocks.stonebrick, 1, 0)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output43, objects20);
		ItemStack output42 = ModBlocks.STONE_WALL.newItemStack(6, 1);
		Object[] objects19 = new Object[]{"BBB", "BBB", 'B', new ItemStack(Blocks.stonebrick, 1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output42, objects19);
		ItemStack output41 = ModBlocks.STONE_WALL.newItemStack(6, 2);
		Object[] objects18 = new Object[]{"BBB", "BBB", 'B', new ItemStack(Blocks.sandstone, 1, 0)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output41, objects18);
		ItemStack output40 = ModBlocks.STONE_WALL.newItemStack(6, 3);
		Object[] objects17 = new Object[]{"BBB", "BBB", 'B', new ItemStack(Blocks.brick_block, 1, 0)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output40, objects17);

		//TODO Nether brick wall should be individually toggleable because of Netherlicious
		ItemStack output39 = ModBlocks.NETHER_BRICK_WALL.newItemStack(6);
		Object[] objects16 = new Object[]{"BBB", "BBB", 'B', new ItemStack(Blocks.nether_brick)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output39, objects16);

		ItemStack output38 = ModBlocks.SMITHING_TABLE.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output38, "II", "PP", "PP", 'P', "plankWood", 'I', "ingotIron");

		ItemStack output37 = ModBlocks.FLETCHING_TABLE.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output37, "FF", "PP", "PP", 'P', "plankWood", 'F', new ItemStack(Items.flint, 1, 0));

		ItemStack output36 = ModBlocks.STONECUTTER.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output36, " I ", "SSS", 'S', "stone", 'I', "ingotIron");

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output5 = ModBlocks.COMPOSTER.newItemStack();
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, "S S", "S S", "SSS", 'S', "slabWood");
		}

		ItemStack output35 = ModBlocks.CARTOGRAPHY_TABLE.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output35, "pp", "PP", "PP", 'P', "plankWood", 'p', new ItemStack(Items.paper, 1, 0));

		ItemStack output34 = ModBlocks.LOOM.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output34, "SS", "PP", 'P', "plankWood", 'S', new ItemStack(Items.string, 1, 0));

		ItemStack output33 = ModBlocks.AMETHYST_BLOCK.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output33, "AA", "AA", 'A', "gemAmethyst");
		ItemStack output32 = ModBlocks.TINTED_GLASS.newItemStack(2, 0);
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output32, " A ", "AGA", " A ", 'A', "gemAmethyst", 'G', "blockGlassColorless");

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output6 = ModBlocks.TARGET.newItemStack();
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output6, " R ", "RHR", " R ", 'R', "dustRedstone", 'H', Blocks.hay_block);

			ItemStack output5 = ModBlocks.OBSERVER.newItemStack();
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, "CCC", "RRQ", "CCC", 'R', "dustRedstone", 'C', "cobblestone", 'Q', "gemQuartz");
		}

		ItemStack output31 = ModBlocks.HONEY_BLOCK.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output31, "HH", "HH", 'H', ModItems.HONEY_BOTTLE.get());
		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, ModItems.HONEY_BOTTLE.newItemStack(4), ModBlocks.HONEY_BLOCK.get(), Items.glass_bottle, Items.glass_bottle, Items.glass_bottle, Items.glass_bottle);
		ItemStack output30 = ModBlocks.HONEYCOMB_BLOCK.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output30, "HH", "HH", 'H', "materialHoneycomb");
		ItemStack output29 = ModBlocks.BEEHIVE.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output29, "WWW", "HHH", "WWW", 'W', "plankWood", 'H', "materialHoneycomb");

		ItemStack output28 = ModBlocks.CHAIN.newItemStack();
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output28, "N", "G", "N", 'N', "nuggetIron", 'G', "ingotIron");

		ItemStack output27 = ModBlocks.BLACKSTONE.newItemStack(4, 1);
		Object[] objects15 = new Object[]{"xx", "xx", 'x', ModBlocks.BLACKSTONE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output27, objects15);
		ItemStack output26 = ModBlocks.BLACKSTONE.newItemStack(4, 2);
		Object[] objects14 = new Object[]{"xx", "xx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output26, objects14);
		ItemStack output25 = ModBlocks.BLACKSTONE.newItemStack(1, 4);
		Object[] objects13 = new Object[]{"x", "x", 'x', ModBlocks.BLACKSTONE_SLAB.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output25, objects13);

		ItemStack input3 = ModBlocks.BLACKSTONE.newItemStack(1, 2);
		ItemStack output6 = ModBlocks.BLACKSTONE.newItemStack(1, 3);
		RecipeHelper.addSmelting(input3, output6, 0.1F);

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output13 = ModBlocks.BLACKSTONE_SLAB.newItemStack(6);
			Object[] objects2 = new Object[]{"xxx", 'x', ModBlocks.BLACKSTONE.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output13, objects2);
			ItemStack output12 = ModBlocks.BLACKSTONE_SLAB.newItemStack(6, 1);
			Object[] objects1 = new Object[]{"xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output12, objects1);
			ItemStack output5 = ModBlocks.BLACKSTONE_SLAB.newItemStack(6, 2);
			Object[] objects = new Object[]{"xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 2)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects);
		}

		ItemStack output24 = ModBlocks.BLACKSTONE_STAIRS.newItemStack(4);
		Object[] objects12 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output24, objects12);
		ItemStack output23 = ModBlocks.POLISHED_BLACKSTONE_STAIRS.newItemStack(4);
		Object[] objects11 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output23, objects11);
		ItemStack output22 = ModBlocks.POLISHED_BLACKSTONE_BRICK_STAIRS.newItemStack(4);
		Object[] objects10 = new Object[]{"x  ", "xx ", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output22, objects10);

		ItemStack output21 = ModBlocks.BLACKSTONE_WALL.newItemStack(6);
		Object[] objects9 = new Object[]{"xxx", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output21, objects9);
		ItemStack output20 = ModBlocks.BLACKSTONE_WALL.newItemStack(6, 1);
		Object[] objects8 = new Object[]{"xxx", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output20, objects8);
		ItemStack output19 = ModBlocks.BLACKSTONE_WALL.newItemStack(6, 2);
		Object[] objects7 = new Object[]{"xxx", "xxx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output19, objects7);

		if (!ModsList.GTNH.isLoaded()) {
			ItemStack output12 = ModBlocks.POLISHED_BLACKSTONE_BUTTON.newItemStack(1);
			Object[] objects1 = new Object[]{"x", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output12, objects1);
			ItemStack output5 = ModBlocks.POLISHED_BLACKSTONE_PRESSURE_PLATE.newItemStack(1);
			Object[] objects = new Object[]{"xx", 'x', ModBlocks.BLACKSTONE.newItemStack(1, 1)};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output5, objects);
		}

		ItemStack output18 = ModBlocks.BASALT.newItemStack(4, 1);
		Object[] objects6 = new Object[]{"xx", "xx", 'x', ModBlocks.BASALT.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output18, objects6);
		ItemStack input2 = ModBlocks.BASALT.newItemStack();
		ItemStack output5 = ModBlocks.SMOOTH_BASALT.newItemStack();
		RecipeHelper.addSmelting(input2, output5, 0.1F);

		if (ModsList.LOTR.isLoaded()) {//LoTR ores and ingots stupidly lack dictionary entries; let's add them so the code below can find them.
			if (ConfigModCompat.moddedRawOres) {
				Block ore7 = GameRegistry.findBlock("lotr", "tile.oreCopper");
				RecipeHelper.registerOre("oreCopper", ore7);
				Item ore3 = GameRegistry.findItem("lotr", "item.copper");
				RecipeHelper.registerOre("ingotCopper", ore3);

				Block ore6 = GameRegistry.findBlock("lotr", "tile.oreTin");
				RecipeHelper.registerOre("oreTin", ore6);
				Item ore2 = GameRegistry.findItem("lotr", "item.tin");
				RecipeHelper.registerOre("ingotTin", ore2);
				Block ore5 = GameRegistry.findBlock("lotr", "tile.oreSilver");
				RecipeHelper.registerOre("oreSilver", ore5);
				Item ore1 = GameRegistry.findItem("lotr", "item.silver");
				RecipeHelper.registerOre("ingotSilver", ore1);
				Block ore4 = GameRegistry.findBlock("lotr", "tile.oreMithril");
				RecipeHelper.registerOre("oreMithril", ore4);
				Item ore = GameRegistry.findItem("lotr", "item.mithril");
				RecipeHelper.registerOre("ingotMithril", ore);
			}
		}

		if (ModsList.BIG_REACTORS.isLoaded()) {
			if (ConfigModCompat.moddedRawOres) {
				Block ore = ExternalContent.Blocks.BR_YELLORITE_ORE.get();
				RecipeHelper.registerOre("oreUranium", ore);
			}
		}

		RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, new ItemStack(Items.dye, 1, 9), ModBlocks.PINK_PETALS.get());

		if (!ConfigExperiments.netherDimensionProvider) { //A way to get soul soil without new Nether
			ItemStack output12 = ModBlocks.SOUL_SOIL.newItemStack(5);
			RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.HIGH, output12, Blocks.dirt, Blocks.soul_sand, Blocks.soul_sand, Blocks.soul_sand, Blocks.soul_sand);
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

		ItemStack output17 = ModBlocks.RAW_ORE_BLOCK.newItemStack();
		Object[] objects5 = new Object[]{"xxx", "xxx", "xxx", 'x', ModItems.RAW_ORE.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output17, objects5);
		ItemStack output16 = ModItems.RAW_ORE.newItemStack(9);
		Object[] objects4 = new Object[]{"x", 'x', ModBlocks.RAW_ORE_BLOCK.newItemStack()};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output16, objects4);
		if (result != null) {
			ItemStack input = ModItems.RAW_ORE.newItemStack();
			RecipeHelper.addSmelting(input, result, 0.7F);
		}

		ItemStack output15 = ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 1);
		Object[] objects3 = new Object[]{"xxx", "xxx", "xxx", 'x', ModItems.RAW_ORE.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output15, objects3);
		ItemStack output14 = ModItems.RAW_ORE.newItemStack(9, 1);
		Object[] objects2 = new Object[]{"x", 'x', ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 1)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output14, objects2);
		ItemStack input1 = ModItems.RAW_ORE.newItemStack(1, 1);
		RecipeHelper.addSmelting(input1, new ItemStack(Items.iron_ingot, 1, 0), 0.7F);

		ItemStack output13 = ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 2);
		Object[] objects1 = new Object[]{"xxx", "xxx", "xxx", 'x', ModItems.RAW_ORE.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output13, objects1);
		ItemStack output12 = ModItems.RAW_ORE.newItemStack(9, 2);
		Object[] objects = new Object[]{"x", 'x', ModBlocks.RAW_ORE_BLOCK.newItemStack(1, 2)};
		RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output12, objects);
		ItemStack input = ModItems.RAW_ORE.newItemStack(1, 2);
		RecipeHelper.addSmelting(input, new ItemStack(Items.gold_ingot, 1, 0), 0.7F);

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
						ItemStack ore1 = ModBlocks.MODDED_DEEPSLATE_ORE.newItemStack(1, i);
						RecipeHelper.registerOre(type, ore1);
						ItemStack ore = ModBlocks.MODDED_DEEPSLATE_ORE.newItemStack(1, i);
						RecipeHelper.registerOre(type.replace("ore", "oreDeepslate"), ore);
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
				RecipeHelper.removeAllRecipesWithOutput(oreItem, i, false);
				RecipeHelper.removeAllRecipesWithOutput(oreBlock, i, false);
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
						RecipeHelper.registerOre(type.replace("ingot", "raw"), new ItemStack(oreItem, 1, i));
						ItemStack ore = new ItemStack(oreBlock, 1, i);
						RecipeHelper.registerOre(type.replace("ingot", "blockRaw"), ore);
						if (ConfigFunctions.registerRawItemAsOre) {
							RecipeHelper.registerOre(type.replace("ingot", "ore"), new ItemStack(oreItem, 1, i));
						}
						if (!registeredRecipe) {
							ItemStack output = new ItemStack(oreBlock, 1, i);
							RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output, "xxx", "xxx", "xxx", 'x', new ItemStack(oreItem, 1, i));
							Object[] objects = new Object[]{"x", 'x', new ItemStack(oreBlock, 1, i)};
							RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, new ItemStack(oreItem, 9, i), objects);
							registeredRecipe = true;
						}
						ItemStack output = Utils.getFirstStackFromTag(type);
						RecipeHelper.addSmelting(new ItemStack(oreItem, 1, i), output, 0.7F);
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
						RecipeHelper.registerOre(tag, stack);
					}
				}
				if (RecipeHelper.validateItems(ore)) {
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
							RecipeHelper.registerOre(tag, stack);
						}
					}
					if (RecipeHelper.validateItems(ore)) {
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
			ItemStack ore2 = ModBlocks.RAW_ADAMANTIUM_BLOCK.newItemStack();
			RecipeHelper.registerOre("blockRawAdamantium", ore2);
			ItemStack ore1 = ModBlocks.RAW_ADAMANTIUM_BLOCK.newItemStack();
			RecipeHelper.registerOre("blockRawAdamantite", ore1);
			ItemStack ore = ModBlocks.RAW_ADAMANTIUM_BLOCK.newItemStack();
			RecipeHelper.registerOre("blockRawAdamantine", ore);
			ItemStack output2 = ModBlocks.RAW_ADAMANTIUM_BLOCK.newItemStack();
			Object[] objects1 = new Object[]{"xxx", "xxx", "xxx", 'x', ModItems.RAW_ADAMANTIUM.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output2, objects1);
			ItemStack output1 = ModItems.RAW_ADAMANTIUM.newItemStack(9);
			Object[] objects = new Object[]{"x", 'x', ModBlocks.RAW_ADAMANTIUM_BLOCK.newItemStack()};
			RecipeHelper.addShapedRecipe(RecipeHelper.Priority.HIGH, output1, objects);
			ItemStack input = ModItems.RAW_ADAMANTIUM.newItemStack();
			ItemStack output = ExternalContent.Items.SIMPLEORES_ADAMANTIUM_INGOT.newItemStack();
			float exp = ExternalContent.Items.SIMPLEORES_ADAMANTIUM_INGOT.get().getSmeltingExperience(ExternalContent.Items.SIMPLEORES_ADAMANTIUM_INGOT.newItemStack());
			RecipeHelper.addSmelting(input, output, exp);
			RawOreRegistry.addOre("oreAdamantium", ModItems.RAW_ADAMANTIUM.get(), 0);
		}
	}

	private static void addTagsAndDeepslate(String tagNoPrefix, ItemStack stack) {
		String oreName1 = "ore" + StringUtils.capitalize(tagNoPrefix);
		RecipeHelper.registerOre(oreName1, stack);
		String oreName = "oreDeepslate" + StringUtils.capitalize(tagNoPrefix);
		RecipeHelper.registerOre(oreName, stack);
	}

	private static void addTagsAndRawOre(String tagNoPrefix, ItemStack stack) {
		if (ConfigFunctions.registerRawItemAsOre) {
			String oreName = "ore" + StringUtils.capitalize(tagNoPrefix);
			RecipeHelper.registerOre(oreName, stack);
		}
		String oreName = "raw" + StringUtils.capitalize(tagNoPrefix);
		RecipeHelper.registerOre(oreName, stack);
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
}