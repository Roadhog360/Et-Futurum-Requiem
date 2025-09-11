package ganymedes01.etfuturum.inventory;

import ganymedes01.etfuturum.tileentities.TileEntityNewBrewingStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import roadhog360.hogutils.api.hogtags.helpers.ItemTags;

public class ContainerNewBrewingStand extends Container {

	private final TileEntityNewBrewingStand tile;
	private final Slot ingredientSlot;
	private int prevBrewTime, prevFuel, prevCurrentFuel;

	public ContainerNewBrewingStand(InventoryPlayer playerInvt, TileEntityNewBrewingStand tile) {
		this.tile = tile;
		addSlotToContainer(new PotionSlot(tile, 0, 56, 51));
		addSlotToContainer(new PotionSlot(tile, 1, 79, 58));
		addSlotToContainer(new PotionSlot(tile, 2, 102, 51));
		ingredientSlot = addSlotToContainer(new IngredientSlot(tile, 3, 79, 17));
		addSlotToContainer(new BlazePowderSlot(tile, 4, 17, 17));

		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(playerInvt, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for (int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(playerInvt, i, 8 + i * 18, 142));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (ICrafting crafter : crafters) {
			if (prevBrewTime != tile.getBrewTime())
				crafter.sendProgressBarUpdate(this, 0, tile.getBrewTime());
			if (prevFuel != tile.getFuel())
				crafter.sendProgressBarUpdate(this, 1, tile.getFuel());
			if (prevCurrentFuel != tile.getCurrentFuel())
				crafter.sendProgressBarUpdate(this, 2, tile.getCurrentFuel());
		}
		prevBrewTime = tile.getBrewTime();
		prevFuel = tile.getFuel();
		prevCurrentFuel = tile.getCurrentFuel();
	}

	@Override
	public void updateProgressBar(int id, int value) {
		if (id == 0)
			tile.func_145938_d(value); // setBrewTime
		else if (id == 1)
			tile.setFuel(value);
		else if (id == 2)
			tile.setCurrentFuel(value);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tile.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotIndex > 2 && slotIndex != 3 && slotIndex != 4) {
				if (((Slot) inventorySlots.get(4)).isItemValid(itemstack)) {
					if (!this.mergeItemStack(itemstack1, 4, 5, false)) {
						return null;
					}
				} else if (!this.ingredientSlot.getHasStack() && this.ingredientSlot.isItemValid(itemstack1)) {
					{
						if (!this.mergeItemStack(itemstack1, 3, 4, false)) {
							return null;
						}
					}
				} else if (PotionSlot.canHoldPotion(itemstack)) {
					if (!this.mergeItemStack(itemstack1, 0, 3, false)) {
						return null;
					}
				} else if (slotIndex < 32) {
					if (!this.mergeItemStack(itemstack1, 32, 41, false)) {
						return null;
					}
				} else if (slotIndex < 41) {
					if (!this.mergeItemStack(itemstack1, 5, 32, false)) {
						return null;
					}
				} else if (!this.mergeItemStack(itemstack1, 5, 41, false)) {
					return null;
				}
			} else {
				if (!this.mergeItemStack(itemstack1, 5, 41, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}

	class BlazePowderSlot extends Slot {

		public BlazePowderSlot(IInventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return ItemTags.hasTag(stack.getItem(), stack.getItemDamage(), "minecraft:brewing_fuel");
		}
	}

	class IngredientSlot extends Slot {

		public IngredientSlot(IInventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return stack != null && stack.getItem().isPotionIngredient(stack);
		}
	}

	static class PotionSlot extends Slot {

		public PotionSlot(IInventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return canHoldPotion(stack);
		}

		@Override
		public int getSlotStackLimit() {
			return 1;
		}

		@Override
		public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
			if (stack.getItem() instanceof ItemPotion && stack.getItemDamage() > 0)
				player.addStat(AchievementList.potion, 1);

			super.onPickupFromSlot(player, stack);
		}

		public static boolean canHoldPotion(ItemStack stack) {
			return stack != null && (stack.getItem() instanceof ItemPotion || stack.getItem() == Items.glass_bottle);
		}
	}
}