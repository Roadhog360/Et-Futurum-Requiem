package ganymedes01.etfuturum.api;

import ganymedes01.etfuturum.core.utils.ItemStackMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.hogtags.helpers.ItemTags;

import java.util.List;

@Deprecated
public class BrewingFuelRegistry {

	@Deprecated
	public static void registerFuel(Object itemObj, int count) {
		if (count <= 0) {
			throw new IllegalArgumentException("Tried to add a brewing fuel with " + count + " cycles??? It must be able to at least brew 1 set of potions...");
		}

		if (itemObj instanceof ItemStack stack && stack.getItem() != null) {
			ItemTags.addTags(stack.getItem(), stack.getItemDamage(), "minecraft:brewing_fuel");
		} else if (itemObj instanceof String) {
			for (ItemStack oreStack : OreDictionary.getOres((String) itemObj)) {
				if(oreStack.getItem() != null) {
					ItemTags.addTags(oreStack.getItem(), oreStack.getItemDamage(), "minecraft:brewing_fuel");
				}
			}
		} else if (itemObj instanceof Item item) {
			ItemTags.addTags(item, "minecraft:brewing_fuel");
		} else if (itemObj instanceof Block block && Item.getItemFromBlock(block) != null) {
			ItemTags.addTags(Item.getItemFromBlock(block), "minecraft:brewing_fuel");
		} else {
			throw new IllegalArgumentException("Tried to add " + itemObj + " as a brewing fuel, which is not an Itemstack, item, block or string.");
		}
	}

	@Deprecated
	public static void registerFuel(List<Object> list, int count) {
		list.forEach(o -> registerFuel(o, count));
	}

	@Deprecated
	public static void remove(String fuelOreDict) {
		for (ItemStack stack : OreDictionary.getOres(fuelOreDict)) {
			remove(stack);
		}
	}

	@Deprecated
	public static void remove(ItemStack fuel) {
		ItemTags.removeTags(fuel.getItem(), fuel.getItemDamage(), "minecraft:brewing_fuel");
	}

	@Deprecated
	public static boolean isFuel(String fuelOreDict) {
		for (ItemStack stack : OreDictionary.getOres(fuelOreDict)) {
			if (isFuel(stack)) return true;
		}
		return false;
	}

	@Deprecated
	public static boolean isFuel(ItemStack fuel) {
		return getBrewingAmount(fuel) > 0;
	}

	/// I'll let this slide as it can now be used as a helper function to get the custom fuel level tag
	public static int getBrewingAmount(ItemStack fuel) {
		int count = 0;
		if(ItemTags.hasTag(fuel.getItem(), fuel.getItemDamage(), "minecraft:brewing_fuel")) {
			count = 20;
			//TODO: Do we want to support custom fuel counts with tagging, or just remove that entirely?
		}

		return count;
	}

	/// TODO: This should populate an ItemStackMap with the current tags contents
	@Deprecated
	public static ItemStackMap<Integer> getFuels() {
		return new ItemStackMap<>();
	}

	/// TODO: This needs to print the tags stuff instead
	@Deprecated
	public void printFuels() {
	}
}