package ganymedes01.etfuturum.compat.cthandlers;

import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.api.EnchantingFuelRegistry;
import ganymedes01.etfuturum.compat.CompatCraftTweaker;
import ganymedes01.etfuturum.core.utils.ItemStackSet;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import roadhog360.hogutils.api.hogtags.HogTagsHelper;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

import static ganymedes01.etfuturum.compat.CompatCraftTweaker.getInternal;

@ZenClass("mods.etfuturum.enchantingFuel")
public class CTEnchantingFuels {

	@ZenMethod
	public static void remove(IIngredient fuel) {

		MineTweakerAPI.logWarning("Registering/Removing enchantment fuels is no longer supported this way.");
		MineTweakerAPI.logWarning("Please use HogTags instead!");
		Object internal = getInternal(fuel);

		if (((internal instanceof String && EnchantingFuelRegistry.isFuel((String) internal))
				|| (internal instanceof ItemStack && HogTagsHelper.ItemTags.hasAnyTag(((ItemStack) internal).getItem(), ((ItemStack) internal).getItemDamage(), Tags.MOD_ID + ":enchantment_fuel")))) {
//			final List<ItemTagMapping> toRemove = new ObjectArrayList<>();
//			for (ItemTagMapping stack : HogTagsHelper.ItemTags.getInTag(Tags.MOD_ID + ":enchantment_fuel")) {
//				if (fuel.matches(new MCItemStack(stack.newItemStack()))) {
//					toRemove.add(stack);
//				}
//			}
//			MineTweakerAPI.apply(new RemoveAction(toRemove));
		} else {
			MineTweakerAPI.logWarning("No enchanting fuels for " + fuel);
		}
	}

	@ZenMethod
	public static void addFuel(IIngredient fuel) {
		MineTweakerAPI.logWarning("Registering/Removing enchantment fuels is no longer supported this way.");
		MineTweakerAPI.logWarning("Please use HogTags instead!");

		List<IItemStack> items = fuel.getItems();
		if (items == null) {
			MineTweakerAPI.logError("Cannot turn " + fuel + " into a enchanting fuel");
			return;
		}

		final ItemStack[] toAdd = CompatCraftTweaker.getItemStacks(items);
		MineTweakerAPI.apply(new AddAction(fuel, toAdd));
	}

	// ######################
	// ### Action classes ###
	// ######################

	private static class RemoveAction implements IUndoableAction {

		private final ItemStackSet items;

		public RemoveAction(ItemStackSet items) {
			this.items = items;
		}

		@Override
		public void apply() {
			for (ItemStack item : items.keySet()) {
				HogTagsHelper.ItemTags.removeTags(item.getItem(), item.getItemDamage(), ":enchantment_fuel");
			}
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			for (ItemStack entry : items.keySet()) {
				HogTagsHelper.ItemTags.addTags(entry.getItem(), entry.getItemDamage(), ":enchantment_fuel");
			}
		}

		@Override
		public String describe() {
			return "Removing " + items.size() + " enchanting fuels";
		}

		@Override
		public String describeUndo() {
			return "Restoring " + items.size() + " enchanting fuels";
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}

	private static class AddAction implements IUndoableAction {

		private final IIngredient ingredient;
		private final ItemStack[] fuels;

		public AddAction(IIngredient ingredient, ItemStack[] fuels) {
			this.ingredient = ingredient;
			this.fuels = fuels;
		}

		@Override
		public void apply() {
			for (ItemStack inputStack : fuels) {
				EnchantingFuelRegistry.registerFuel(inputStack);
			}
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			for (ItemStack inputStack : fuels) {
				EnchantingFuelRegistry.remove(inputStack);
			}
		}

		@Override
		public String describe() {
			return "Adding enchanting fuel for " + ingredient;
		}

		@Override
		public String describeUndo() {
			return "Removing enchanting fuel for " + ingredient;
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}
}
