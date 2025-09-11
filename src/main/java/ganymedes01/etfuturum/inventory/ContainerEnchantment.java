package ganymedes01.etfuturum.inventory;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.storage.EtFuturumPlayer;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import roadhog360.hogutils.api.hogtags.helpers.ItemTags;

import java.util.List;
import java.util.Random;

public class ContainerEnchantment extends Container {

	/**
	 * SlotEnchantmentTable object with ItemStack to be enchanted
	 */
	public IInventory tableInventory;

	/**
	 * current world (for bookshelf counting)
	 */
	private final World world;
	private final Random rand;
	public int enchantmentSeed;

	private final int posX;
	private final int posY;
	private final int posZ;

	/**
	 * 3-member array storing the enchantment levels of each slot
	 */
	public int[] enchantLevels;
	public int[] enchantmentIds;

	public final boolean noFuel;

	public ContainerEnchantment(InventoryPlayer inventory, World world, int x, int y, int z) {
		tableInventory = new InventoryBasic("Enchant", true, 2) {

			@Override
			public void markDirty() {
				super.markDirty();
				ContainerEnchantment.this.onCraftMatrixChanged(this);
			}
		};
		posX = x;
		posY = y;
		posZ = z;
		rand = new Random();
		enchantLevels = new int[3];
		enchantmentIds = new int[]{-1, -1, -1};
		this.world = world;
		enchantmentSeed = getEnchantSeed(inventory.player);
		noFuel = ItemTags.getInTag(Tags.MOD_ID + ":enchantment_fuel").isEmpty();
		addSlotToContainer(new Slot(tableInventory, 0, noFuel ? 25 : 15, 47) {

			@Override
			public int getSlotStackLimit() {
				return 1;
			}
		});
		if (!noFuel) {
			addSlotToContainer(new Slot(tableInventory, 1, 35, 47) {
				@Override
				public boolean isItemValid(ItemStack stack) {
					return ItemTags.hasTag(stack.getItem(), stack.getItemDamage(), Tags.MOD_ID + ":enchantment_fuel");
				}
			});
		}
		int var4;

		for (var4 = 0; var4 < 3; ++var4) {
			for (int var5 = 0; var5 < 9; ++var5) {
				addSlotToContainer(new Slot(inventory, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
			}
		}

		for (var4 = 0; var4 < 9; ++var4) {
			addSlotToContainer(new Slot(inventory, var4, 8 + var4 * 18, 142));
		}
	}

	private static void setEnchantSeed(EntityPlayer player, int seed) {
		EtFuturumPlayer storage = EtFuturumPlayer.get(player);
		storage.setEnchantmentSeed(seed);
	}

	private static int getEnchantSeed(EntityPlayer player) {
		EtFuturumPlayer storage = EtFuturumPlayer.get(player);
		return storage.getEnchantmentSeed();
	}

	private static void chargeForEnchant(EntityPlayer player, Random rand, int amount) {
		player.addExperienceLevel(-amount);

		setEnchantSeed(player, rand.nextInt());
	}

	@Override
	public void addCraftingToCrafters(ICrafting p_75132_1_) {
		super.addCraftingToCrafters(p_75132_1_);
		p_75132_1_.sendProgressBarUpdate(this, 0, enchantLevels[0]);
		p_75132_1_.sendProgressBarUpdate(this, 1, enchantLevels[1]);
		p_75132_1_.sendProgressBarUpdate(this, 2, enchantLevels[2]);
		p_75132_1_.sendProgressBarUpdate(this, 3, enchantmentSeed & -16);
		p_75132_1_.sendProgressBarUpdate(this, 4, enchantmentIds[0]);
		p_75132_1_.sendProgressBarUpdate(this, 5, enchantmentIds[1]);
		p_75132_1_.sendProgressBarUpdate(this, 6, enchantmentIds[2]);
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (Object crafter : crafters) {
			ICrafting var2 = (ICrafting) crafter;
			var2.sendProgressBarUpdate(this, 0, enchantLevels[0]);
			var2.sendProgressBarUpdate(this, 1, enchantLevels[1]);
			var2.sendProgressBarUpdate(this, 2, enchantLevels[2]);
			var2.sendProgressBarUpdate(this, 3, enchantmentSeed & -16);
			var2.sendProgressBarUpdate(this, 4, enchantmentIds[0]);
			var2.sendProgressBarUpdate(this, 5, enchantmentIds[1]);
			var2.sendProgressBarUpdate(this, 6, enchantmentIds[2]);
		}
	}

	@Override
	public void updateProgressBar(int p_75137_1_, int p_75137_2_) {
		if (p_75137_1_ >= 0 && p_75137_1_ <= 2)
			enchantLevels[p_75137_1_] = p_75137_2_;
		else if (p_75137_1_ == 3)
			enchantmentSeed = p_75137_2_;
		else if (p_75137_1_ >= 4 && p_75137_1_ <= 6)
			enchantmentIds[p_75137_1_ - 4] = p_75137_2_;
		else
			super.updateProgressBar(p_75137_1_, p_75137_2_);
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	@Override
	public void onCraftMatrixChanged(IInventory p_75130_1_) {
		if (p_75130_1_ == tableInventory) {
			ItemStack var2 = p_75130_1_.getStackInSlot(0);
			int power;

			if (var2 != null && var2.isItemEnchantable()) {
				if (!world.isRemote) {
					power = 0;
					int j;

					for (j = -1; j <= 1; ++j)
						for (int k = -1; k <= 1; ++k)
							if ((j != 0 || k != 0) && world.isAirBlock(posX + k, posY, posZ + j) && world.isAirBlock(posX + k, posY + 1, posZ + j)) {
								power += (int) ForgeHooks.getEnchantPower(world, posX + k * 2, posY, posZ + j * 2);
								power += (int) ForgeHooks.getEnchantPower(world, posX + k * 2, posY + 1, posZ + j * 2);

								if (k != 0 && j != 0) {
									power += (int) ForgeHooks.getEnchantPower(world, posX + k * 2, posY, posZ + j);
									power += (int) ForgeHooks.getEnchantPower(world, posX + k * 2, posY + 1, posZ + j);
									power += (int) ForgeHooks.getEnchantPower(world, posX + k, posY, posZ + j * 2);
									power += (int) ForgeHooks.getEnchantPower(world, posX + k, posY + 1, posZ + j * 2);
								}
							}

					rand.setSeed(enchantmentSeed);

					for (j = 0; j < 3; ++j) {
						enchantLevels[j] = EnchantmentHelper.calcItemStackEnchantability(rand, j, power, var2);
						enchantmentIds[j] = -1;

						if (enchantLevels[j] < j + 1)
							enchantLevels[j] = 0;
					}

					for (j = 0; j < 3; ++j)
						if (enchantLevels[j] > 0) {
							List<EnchantmentData> var7 = getEnchantmentList(var2, j, enchantLevels[j]);

							if (var7 != null && !var7.isEmpty()) {
								EnchantmentData var6 = var7.get(rand.nextInt(var7.size()));
								enchantmentIds[j] = var6.enchantmentobj.effectId | var6.enchantmentLevel << 8;
							}
						}

					detectAndSendChanges();
				}
			} else
				for (power = 0; power < 3; ++power) {
					enchantLevels[power] = 0;
					enchantmentIds[power] = -1;
				}
		}
	}

	/**
	 * enchants the item on the table using the specified slot; also deducts XP from player
	 */
	@Override
	public boolean enchantItem(EntityPlayer player, int id) {
		ItemStack slot0 = tableInventory.getStackInSlot(0);
		ItemStack slot1 = tableInventory.getStackInSlot(1);
		int var5 = id + 1;

		if ((slot1 == null || slot1.stackSize < var5) && !player.capabilities.isCreativeMode) {
			return false;
		} else if (enchantLevels[id] > 0 && slot0 != null && (player.experienceLevel >= var5 && player.experienceLevel >= enchantLevels[id] || player.capabilities.isCreativeMode)) {
			if (!world.isRemote) {
				List<EnchantmentData> var6 = getEnchantmentList(slot0, id, enchantLevels[id]);
				boolean var7 = slot0.getItem() == Items.book;

				if (var6 != null) {
					chargeForEnchant(player, rand, var5);
					world.playSoundEffect(posX + 0.5F, posY + 0.5F, posZ + 0.5F, Tags.MC_ASSET_VER + ":block.enchantment_table.use", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);

					if (var7)
						slot0.func_150996_a(Items.enchanted_book); // setItem

					for (EnchantmentData var9 : var6) {
						if (var7)
							Items.enchanted_book.addEnchantment(slot0, var9);
						else
							slot0.addEnchantment(var9.enchantmentobj, var9.enchantmentLevel);
					}

					if (!player.capabilities.isCreativeMode && slot1 != null) {
						slot1.stackSize -= var5;

						if (slot1.stackSize <= 0) {
							ItemStack containerItem = slot1.getItem().getContainerItem(slot1);
							tableInventory.setInventorySlotContents(1, containerItem != null && (!slot1.getItem().doesContainerItemLeaveCraftingGrid(slot1) || !player.inventory.addItemStackToInventory(containerItem)) ? containerItem : null);
						}
					}

					tableInventory.markDirty();
					enchantmentSeed = rand.nextInt();
					onCraftMatrixChanged(tableInventory);
				}
			}

			return true;
		} else
			return false;
	}

	private List<EnchantmentData> getEnchantmentList(ItemStack stack, int seed, int level) {
		rand.setSeed(enchantmentSeed + seed);
		List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(rand, stack, level);

		if (stack.getItem() == Items.book && list != null && list.size() > 1)
			list.remove(rand.nextInt(list.size()));

		return list;
	}

	public int getLapisAmount() {
		ItemStack var1 = tableInventory.getStackInSlot(1);
		return var1 == null ? (noFuel ? Integer.MAX_VALUE : 0) : var1.stackSize;
	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);

		if (!world.isRemote)
			for (int i = 0; i < tableInventory.getSizeInventory(); i++) {
				ItemStack stack = tableInventory.getStackInSlotOnClosing(i);
				if (stack != null)
					player.dropPlayerItemWithRandomChoice(stack, false);
			}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return world.getBlock(posX, posY, posZ) == ModBlocks.ENCHANTMENT_TABLE.get() && player.getDistanceSq(posX + 0.5D, posY + 0.5D, posZ + 0.5D) <= 64.0D;
	}

	/**
	 * Take a stack from the specified inventory slot.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack var3 = null;
		Slot var4 = (Slot) inventorySlots.get(index);

		if (var4 != null && var4.getHasStack()) {
			ItemStack var5 = var4.getStack();
			var3 = var5.copy();

			if (index == 0) {
				if (!mergeItemStack(var5, 2, noFuel ? 37 : 38, true))
					return null;
			} else if (!noFuel && index == 1) {
				if (!mergeItemStack(var5, 2, 38, true))
					return null;
			} else if (!noFuel && ItemTags.hasTag(var5.getItem(), var5.getItemDamage(), Tags.MOD_ID + ":enchantment_fuel")) {
				if (!mergeItemStack(var5, 1, 2, true))
					return null;
			} else {
				if (((Slot) inventorySlots.get(0)).getHasStack() || !((Slot) inventorySlots.get(0)).isItemValid(var5))
					return null;

				if (var5.hasTagCompound() && var5.stackSize == 1) {
					((Slot) inventorySlots.get(0)).putStack(var5.copy());
					var5.stackSize = 0;
				} else if (var5.stackSize >= 1) {
					ItemStack newStack = var5.copy();
					var5.stackSize = 1;
					((Slot) inventorySlots.get(0)).putStack(newStack);
					--var5.stackSize;
				}
			}

			if (var5.stackSize == 0)
				var4.putStack(null);
			else
				var4.onSlotChanged();

			if (var5.stackSize == var3.stackSize)
				return null;

			var4.onPickupFromSlot(playerIn, var5);
		}

		return var3;
	}
}