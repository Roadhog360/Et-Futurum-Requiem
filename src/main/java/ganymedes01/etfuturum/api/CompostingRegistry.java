package ganymedes01.etfuturum.api;

import com.google.common.collect.ImmutableList;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.blocks.BlockComposter;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.core.utils.ItemStackMap;
import ganymedes01.etfuturum.core.utils.Logger;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.ApiStatus;
import roadhog360.hogutils.api.utils.RecipeHelper;

import java.util.List;

public class CompostingRegistry {

	private static final ItemStackMap<Integer> COMPOSTING_REGISTRY = new ItemStackMap<>();

	@ApiStatus.AvailableSince("2.4.4")
	public static void init() {
		if (!ConfigFunctions.enableAutoAddComposter) {
			return;
		}
		registerCompostable(ImmutableList.of(
				new ItemStack(Blocks.tallgrass, 1, 1),
				new ItemStack(Blocks.leaves, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Items.melon_seeds),
				new ItemStack(Items.pumpkin_seeds),
				new ItemStack(Items.wheat_seeds),
				"listAllSeeds",
				"treeSapling",
				"treeLeaves",
				ModItems.BEETROOT_SEEDS.newItemStack(),
				ModItems.SWEET_BERRIES.newItemStack(),
				ModBlocks.MOSS_CARPET.newItemStack(),
				ModBlocks.PINK_PETALS.newItemStack(),
				ModBlocks.MANGROVE_ROOTS.newItemStack(),
				ModBlocks.AZALEA_LEAVES.newItemStack(1, 0)
		), 30);

		remove(ModBlocks.AZALEA.newItemStack(1, OreDictionary.WILDCARD_VALUE));
		remove(ModBlocks.AZALEA_LEAVES.newItemStack(1, OreDictionary.WILDCARD_VALUE));

		registerCompostable(ImmutableList.of(
				new ItemStack(Blocks.cactus),
				new ItemStack(Items.melon),
				new ItemStack(Items.reeds),
				new ItemStack(Blocks.double_plant, 1, 2),
				new ItemStack(Blocks.vine),
				ModBlocks.NETHER_ROOTS.newItemStack(1, OreDictionary.WILDCARD_VALUE),
				ModBlocks.NETHER_SPROUTS.newItemStack(1, OreDictionary.WILDCARD_VALUE),
				ModBlocks.TWISTING_VINES.newItemStack(),
				ModBlocks.WEEPING_VINES.newItemStack(),
				ModBlocks.AZALEA_LEAVES.newItemStack(1, 1)
		), 50);

		registerCompostable(ImmutableList.of(
				new ItemStack(Items.apple),
				new ItemStack(ModItems.BEETROOT.get()),
				"cropCarrot",
				new ItemStack(Items.dye, 1, 3),
				new ItemStack(Blocks.tallgrass, 1, 2),
				new ItemStack(Blocks.double_plant, 1, 0),
				new ItemStack(Blocks.double_plant, 1, 1),
				new ItemStack(Blocks.double_plant, 1, 3),
				new ItemStack(Blocks.double_plant, 1, 4),
				new ItemStack(Blocks.double_plant, 1, 5),
				new ItemStack(Blocks.red_flower, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.yellow_flower, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.waterlily),
				new ItemStack(Blocks.melon_block),
				new ItemStack(Blocks.brown_mushroom),
				new ItemStack(Blocks.red_mushroom),
				new ItemStack(Items.nether_wart),
				new ItemStack(Blocks.pumpkin),
				"cropPotato",
				"cropWheat",
				ModBlocks.ROSE.newItemStack(),
				ModBlocks.CORNFLOWER.newItemStack(),
				ModBlocks.LILY_OF_THE_VALLEY.newItemStack(),
				ModBlocks.WITHER_ROSE.newItemStack(),
				ModBlocks.NETHER_FUNGUS.newItemStack(1, OreDictionary.WILDCARD_VALUE),
				ModBlocks.SHROOMLIGHT.newItemStack(),
				ModBlocks.AZALEA.newItemStack(1, 0),
				ModBlocks.MOSS_BLOCK.newItemStack()
		), 65);

		registerCompostable(ImmutableList.of(
				new ItemStack(Items.baked_potato),
				new ItemStack(Items.bread),
				new ItemStack(Items.cookie),
				new ItemStack(Blocks.hay_block),
				new ItemStack(Blocks.red_mushroom_block, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.brown_mushroom_block, 1, OreDictionary.WILDCARD_VALUE),
				ModBlocks.RED_MUSHROOM.newItemStack(1, OreDictionary.WILDCARD_VALUE),
				ModBlocks.BROWN_MUSHROOM.newItemStack(1, OreDictionary.WILDCARD_VALUE),
				ModBlocks.AZALEA.newItemStack(1, 1)
		), 85);

		registerCompostable(ImmutableList.of(
				new ItemStack(Blocks.cake),
				new ItemStack(Items.cake),
				new ItemStack(Items.pumpkin_pie)
		), 100);
	}

	/**
	 * Register an item to be compostable. The percent value is for how likely it is to successfully fill a layer.
	 * Values greater than 100 mean the composter fill more than one layer.
	 * In that case, the hundredth part of the number is how many layers will be filled, the last two digits = the % chance to fill an additional layer.
	 * The composter can hold 6 layers so the max value is 600.
	 * <p>
	 * A fill chance of 50 is a 50% chance to fill one layer.
	 * A fill chance of 100 is a 100% chance to fill one layer.
	 * A fill chance of 150 will fill one layer and then has a 50% chance of filling another layer.
	 * A fill chance of 200 is a 100% chance to fill two layers.
	 */
	@ApiStatus.AvailableSince("2.4.4")
	public static void registerCompostable(Object itemObj, int percent) {
		if (percent <= 0 || percent > BlockComposter.FULL_META * 100) {
			throw new IllegalArgumentException("Tried to add a composter entry with percent value " + percent + " which is not allowed, should be above 0 and equal to or below" + (BlockComposter.FULL_META * 100) + "!");
		}

		if (RecipeHelper.validateItems(itemObj)) {
			if (itemObj instanceof ItemStack) {
				COMPOSTING_REGISTRY.put(((ItemStack) itemObj).copy(), percent);
			} else if (itemObj instanceof String) {
//            OreDictionary.getOres((String) itemObj).forEach(itemStack -> COMPOSTING_REGISTRY.put(itemStack.copy(), percent));//For some reason this line does nothing
				for (ItemStack oreStack : OreDictionary.getOres((String) itemObj)) { //This should be the same as the forEach above but for some reason the above just does nothing?
					COMPOSTING_REGISTRY.put(oreStack.copy(), percent);
				}
			} else if (itemObj instanceof Item) {
				COMPOSTING_REGISTRY.put(new ItemStack((Item) itemObj, 1, OreDictionary.WILDCARD_VALUE), percent);
			} else if (itemObj instanceof Block && Item.getItemFromBlock((Block) itemObj) != null) {
				COMPOSTING_REGISTRY.put(new ItemStack(Item.getItemFromBlock((Block) itemObj), 1, OreDictionary.WILDCARD_VALUE), percent);
			} else {
				throw new IllegalArgumentException("Tried to add " + itemObj + " as a compostable, which is not an Itemstack, item, block or string.");
			}
		}
	}

	@ApiStatus.AvailableSince("2.4.4")
	public static void registerCompostable(List<Object> list, int percent) {
		list.forEach(o -> registerCompostable(o, percent));
	}

	@ApiStatus.AvailableSince("2.4.4")
	public static ItemStackMap<Integer> getComposts() {
		return COMPOSTING_REGISTRY;
	}

	@ApiStatus.AvailableSince("2.4.4")
	public static void remove(String stackOreDict) {
		for (ItemStack stack : OreDictionary.getOres(stackOreDict)) {
			remove(stack);
		}
	}

	@ApiStatus.AvailableSince("2.4.4")
	public static void remove(ItemStack stack) {
		COMPOSTING_REGISTRY.remove(stack);
	}

	@ApiStatus.AvailableSince("2.4.4")
	public static boolean isCompostable(String stackOreDict) {
		for (ItemStack stack : OreDictionary.getOres(stackOreDict)) {
			if (isCompostable(stack)) return true;
		}
		return false;
	}

	@ApiStatus.AvailableSince("2.4.4")
	public static boolean isCompostable(ItemStack stack) {
		return getCompostChance(stack) > 0;
	}

	@ApiStatus.AvailableSince("2.4.4")
	public static int getCompostChance(ItemStack stack) {
		Integer percent = COMPOSTING_REGISTRY.get(stack);
		if (percent != null) {
			return percent;
		}

		return 0;
	}

	/**
	 * Print all entries in composting registry. Used for debugging purposes.
	 */
	@ApiStatus.AvailableSince("2.4.4")
	public void printCompostables() {
		getComposts().forEach((key, value) -> Logger.info("Composter entry: " + key.getItem().delegate.name() + " Meta: " + (key.getItemDamage() == OreDictionary.WILDCARD_VALUE ? "any" : key.getItemDamage())));
	}

}
