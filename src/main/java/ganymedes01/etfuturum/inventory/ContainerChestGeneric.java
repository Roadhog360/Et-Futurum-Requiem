package ganymedes01.etfuturum.inventory;

import ganymedes01.etfuturum.spectator.SpectatorMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * ContainerChest modified to use a custom row size.
 */
public class ContainerChestGeneric extends Container {
	private final IInventory chestInventory;
	private final int rowSize;

	public ContainerChestGeneric(IInventory p_i1806_1_, IInventory p_i1806_2_) {
		this(p_i1806_1_, p_i1806_2_, 9, false);
	}

	public ContainerChestGeneric(IInventory p_i1806_1_, IInventory p_i1806_2_, int rowSize, boolean slim) {
		this.chestInventory = p_i1806_2_;
		this.rowSize = rowSize = Math.min(rowSize, p_i1806_2_.getSizeInventory());
		if (p_i1806_2_.getSizeInventory() % rowSize != 0) {
			throw new IllegalArgumentException("Non-rectangular inventory. " + p_i1806_2_.getSizeInventory() + " slots, " + rowSize + " columns.");
		}

		int numRows = (int) Math.ceil(p_i1806_2_.getSizeInventory() / (float) rowSize);
		if (!(p_i1806_1_ instanceof InventoryPlayer) || !SpectatorMode.isSpectator(((InventoryPlayer) p_i1806_1_).player)) {
			p_i1806_2_.openInventory();
		}
		int j;
		int k;

		int padL = slim ? 12 : 8; // padding on left and right
		int padT = slim ? 8 : 18; // padding on top
		int sep1H = slim ? 4 : 13; // height of separator
		int sep2H = 4; // height of separator

		int slotStartXOff = 0;

		for (j = 0; j < numRows; ++j) {
			for (k = 0; k < rowSize; ++k) {
				if (k + j * rowSize < chestInventory.getSizeInventory()) {
					this.addSlotToContainer(constructSlot(p_i1806_2_, k + j * rowSize, slotStartXOff + padL + k * 18, padT + j * 18));
				}
			}
		}

		int invSlotStartXOff = (rowSize * 18 - 9 * 18) / 2;
		if(!slim) {
			// Needed for vanilla inventories so the slots aren't off by one pixel
			padT--;
		}

		for (j = 0; j < 3; ++j) {
			for (k = 0; k < 9; ++k) {
				this.addSlotToContainer(new Slot(p_i1806_1_, k + j * 9 + 9, invSlotStartXOff + padL + k * 18, padT + numRows * 18 + sep1H + j * 18));
			}
		}

		for (j = 0; j < 9; ++j) {
			this.addSlotToContainer(new Slot(p_i1806_1_, j, invSlotStartXOff + padL + j * 18, padT + numRows * 18 + sep1H + 3 * 18 + sep2H));
		}
	}

	protected Slot constructSlot(IInventory inventory, int slotIndex, int displayX, int displayY) {
		return new SlotCustom(inventory, slotIndex, displayX, displayY);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.chestInventory.isUseableByPlayer(player);
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < this.chestInventory.getSizeInventory()) {
				if (!this.mergeItemStack(itemstack1, this.chestInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, this.chestInventory.getSizeInventory(), false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	@Override
	protected boolean mergeItemStack(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_) {
		int k = p_75135_2_;

		if (p_75135_4_) {
			k = p_75135_3_ - 1;
		}

		while (!p_75135_4_ && k < p_75135_3_ || p_75135_4_ && k >= p_75135_2_) {
			Slot slot = (Slot) this.inventorySlots.get(k);

			if (!slot.isItemValid(p_75135_1_)) {
				return false;
			}

			if (p_75135_4_) {
				--k;
			} else {
				++k;
			}
		}

		return super.mergeItemStack(p_75135_1_, p_75135_2_, p_75135_3_, p_75135_4_);
	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer p_75134_1_) {
		if (!SpectatorMode.isSpectator(p_75134_1_)) {
			super.onContainerClosed(p_75134_1_);
			this.chestInventory.closeInventory();
		}
	}

	/**
	 * Return this chest container's <s>lower</s> UPPER chest inventory.
	 */
	public IInventory getLowerChestInventory() {
		return this.chestInventory;
	}

	public int getRowSize() {
		return this.rowSize;
	}

	public static class SlotCustom extends Slot {

		public SlotCustom(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
			super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return inventory.isItemValidForSlot(this.getSlotIndex(), stack);
		}

	}
}