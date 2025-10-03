package ganymedes01.etfuturum.tileentities;

import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.blocks.BlockSmoker;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.recipes.SmokerRecipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;

/**
 * @author Mojang
 * Code borrowed due to issues directly extending the class.
 */
public class TileEntitySmoker extends TileEntityFurnace implements ISidedInventory {
	private static final int[] slotsTop = new int[]{0};
	private static final int[] slotsBottom = new int[]{2, 1};
	private static final int[] slotsSides = new int[]{1};
	/**
	 * The ItemStacks that hold the items currently being used in the furnace
	 */
	private ItemStack[] furnaceItemStacks = new ItemStack[3];
	/**
	 * The number of ticks that the furnace will keep burning
	 */
	public int furnaceBurnTime;
	/**
	 * The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for
	 */
	public int currentItemBurnTime;
	/**
	 * The number of ticks that the current item has been cooking for
	 */
	public int furnaceCookTime;

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		return this.furnaceItemStacks.length;
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int slotIn) {
		return this.furnaceItemStacks[slotIn];
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
	 * new stack.
	 */
	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (this.furnaceItemStacks[index] != null) {
			ItemStack itemstack;

			if (this.furnaceItemStacks[index].stackSize <= count) {
				itemstack = this.furnaceItemStacks[index];
				this.furnaceItemStacks[index] = null;
				return itemstack;
			}
			itemstack = this.furnaceItemStacks[index].splitStack(count);

			if (this.furnaceItemStacks[index].stackSize == 0) {
				this.furnaceItemStacks[index] = null;
			}

			return itemstack;
		}
		return null;
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
	 * like when you close a workbench GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		if (this.furnaceItemStacks[index] != null) {
			ItemStack itemstack = this.furnaceItemStacks[index];
			this.furnaceItemStacks[index] = null;
			return itemstack;
		}
		return null;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.furnaceItemStacks[index] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}
	}

	/**
	 * Returns the name of the inventory
	 */
	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.field_145958_o : "container." + Tags.MOD_ID + ".smoker";
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];
		Utils.loadItemStacksFromNBT(nbttaglist, this.furnaceItemStacks);

		this.furnaceBurnTime = compound.getShort("BurnTime");
		this.furnaceCookTime = compound.getShort("CookTime");
		this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);

		if (compound.hasKey("CustomName", 8)) {
			this.field_145958_o = compound.getString("CustomName");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setShort("BurnTime", (short) this.furnaceBurnTime);
		compound.setShort("CookTime", (short) this.furnaceCookTime);

		compound.setTag("Items", Utils.writeItemStacksToNBT(this.furnaceItemStacks));

		if (this.hasCustomInventoryName()) {
			compound.setString("CustomName", this.field_145958_o);
		}
	}

	/**
	 * Returns the maximum stack size for a inventory slot.
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how close the current item is to being completely
	 * cooked
	 */
	public int getCookProgressScaled(int p_145953_1_) {
		return this.furnaceCookTime * p_145953_1_ / 100;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
	 * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
	 */
	public int getBurnTimeRemainingScaled(int p_145955_1_) {
		if (this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = 150;
		}

		return this.furnaceBurnTime * p_145955_1_ / this.currentItemBurnTime;
	}

	/**
	 * Furnace isBurning
	 */
	public boolean isBurning() {
		return this.furnaceBurnTime > 0;
	}

	@Override
	public void updateEntity() {
		boolean flag = this.furnaceBurnTime > 0;
		boolean flag1 = false;

		if (this.furnaceBurnTime > 0) {
			--this.furnaceBurnTime;
		}

		if (!this.worldObj.isRemote) {
			if (this.furnaceBurnTime != 0 || this.furnaceItemStacks[1] != null && this.furnaceItemStacks[0] != null) {
				if (this.furnaceBurnTime == 0 && this.canSmelt()) {
					this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);

					if (this.furnaceBurnTime > 0) {
						flag1 = true;

						if (this.furnaceItemStacks[1] != null) {
							--this.furnaceItemStacks[1].stackSize;

							if (this.furnaceItemStacks[1].stackSize == 0) {
								this.furnaceItemStacks[1] = furnaceItemStacks[1].getItem().getContainerItem(furnaceItemStacks[1]);
							}
						}
					}
				}

				if (this.isBurning() && this.canSmelt()) {
					++this.furnaceCookTime;

					if (this.furnaceCookTime == 100) {
						this.furnaceCookTime = 0;
						this.smeltItem();
						flag1 = true;
					}
				} else {
					this.furnaceCookTime = 0;
				}
			}

			if (flag != this.furnaceBurnTime > 0) {
				flag1 = true;
				BlockSmoker.updateFurnaceBlockState(this.furnaceBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			}
		}

		if (flag1) {
			this.markDirty();
		}
	}

	/**
	 * Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, etc.
	 */
	private boolean canSmelt() {
		if (this.furnaceItemStacks[0] == null) {
			return false;
		}
		ItemStack itemstack = SmokerRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0]);
		if (itemstack == null) return false;
		if (this.furnaceItemStacks[2] == null) return true;
		if (!this.furnaceItemStacks[2].isItemEqual(itemstack)) return false;
		int result = furnaceItemStacks[2].stackSize + itemstack.stackSize;
		return result <= getInventoryStackLimit() && result <= this.furnaceItemStacks[2].getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
	}

	/**
	 * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
	 */
	public void smeltItem() {
		if (this.canSmelt()) {
			ItemStack itemstack = SmokerRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0]);

			if (this.furnaceItemStacks[2] == null) {
				this.furnaceItemStacks[2] = itemstack.copy();
			} else if (this.furnaceItemStacks[2].getItem() == itemstack.getItem()) {
				this.furnaceItemStacks[2].stackSize += itemstack.stackSize; // Forge BugFix: Results may have multiple items
			}

			--this.furnaceItemStacks[0].stackSize;

			if (this.furnaceItemStacks[0].stackSize <= 0) {
				this.furnaceItemStacks[0] = null;
			}
		}
	}

	/**
	 * Returns the number of ticks that the supplied fuel item will keep the furnace burning, or 0 if the item isn't
	 * fuel
	 */
	public static int getItemBurnTime(ItemStack p_145952_0_) {
		return TileEntityFurnace.getItemBurnTime(p_145952_0_) / 2;
	}

	public static boolean isItemFuel(ItemStack p_145954_0_) {
		return getItemBurnTime(p_145954_0_) > 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return index != 2 && (index != 1 || isItemFuel(stack));
	}

	/**
	 * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
	 * block.
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return p_94128_1_ == 0 ? slotsBottom : (p_94128_1_ == 1 ? slotsTop : slotsSides);
	}

	/**
	 * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item,
	 * side
	 */
	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
		return this.isItemValidForSlot(p_102007_1_, p_102007_2_);
	}

	/**
	 * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item,
	 * side
	 */
	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
		if (p_102008_2_ != null)
			return p_102008_3_ != 0 || p_102008_1_ != 1 || p_102008_2_.getItem() == Items.bucket;

		return false;
	}
}
