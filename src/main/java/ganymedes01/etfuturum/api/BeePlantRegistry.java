package ganymedes01.etfuturum.api;

import ganymedes01.etfuturum.api.crops.IBeeGrowable;
import ganymedes01.etfuturum.core.utils.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.hogtags.HogTagsHelper;

@Deprecated
public class BeePlantRegistry {
	/// Deprecated; Use HogUtils tagging
	///
	/// Tags: `minecraft:bee_food` (item) and `minecraft:bee_attractive` (block)
	@Deprecated
	public static void addFlower(Block block, int meta) {
		HogTagsHelper.BlockTags.addTags(block, meta, "minecraft:bee_attractive");
		Item item = Item.getItemFromBlock(block);
		if(item != null) {
			HogTagsHelper.ItemTags.addTags(item, meta, "minecraft:bee_food");
		}
	}

	/// Deprecated; Use HogUtils tagging
	@Deprecated
	public static void addCrop(Block block) {
		if (!(block instanceof IGrowable) || !(block instanceof IBeeGrowable)) {
			Logger.warn("Bee crops can only be instance of IGrowable or IBeeGrowable; this entry will do nothing!");
		}
		HogTagsHelper.BlockTags.addTags(block, OreDictionary.WILDCARD_VALUE, "minecraft:bee_growables");
	}

	/// Deprecated; Use HogUtils tagging
	///
	/// Tags: `minecraft:bee_food` (item) and `minecraft:bee_attractive` (block)
	@Deprecated
	public static void removeFlower(Block block, int meta) {
		HogTagsHelper.BlockTags.removeTags(block, meta, "minecraft:bee_attractive");
		Item item = Item.getItemFromBlock(block);
		if(item != null) {
			HogTagsHelper.ItemTags.removeTags(item, meta, "minecraft:bee_food");
		}
	}

	/// Deprecated; Use HogUtils tagging
	@Deprecated
	public static void removeCrop(Block block) {
		HogTagsHelper.BlockTags.removeTags(block, OreDictionary.WILDCARD_VALUE, "minecraft:bee_growables");
	}

	/// Deprecated; Use HogUtils tagging
	/// Returns `TRUE` if this is either a bee food OR bee flower.
	/// This is because the previous registry did not have separate lists for bee breedables, and pollinateable flowers.
	///
	/// Tags: `minecraft:bee_food` (item) and `minecraft:bee_attractive` (block)
	@Deprecated
	public static boolean isFlower(Block block, int meta) {
		Item item = Item.getItemFromBlock(block);
		if(item != null && HogTagsHelper.ItemTags.getTags(item, meta).contains("minecraft:bee_food")) {
			return true;
		}
		return HogTagsHelper.BlockTags.getTags(block, meta).contains("minecraft:bee_attractive");
	}

	/// Deprecated; Use HogUtils tagging
	@Deprecated
	public static boolean isCrop(Block block) {
		return HogTagsHelper.BlockTags.getTags(block, OreDictionary.WILDCARD_VALUE).contains("minecraft:bee_growables");
	}

	@Deprecated
	public static void init() {
	}
}
