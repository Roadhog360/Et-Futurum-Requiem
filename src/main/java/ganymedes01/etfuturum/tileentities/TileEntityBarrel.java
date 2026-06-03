package ganymedes01.etfuturum.tileentities;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.blocks.BlockBarrel;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.inventory.ContainerChestGeneric;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import java.util.*;

public class TileEntityBarrel extends TileEntity implements IInventory {
	private int ticksSinceSync;
	private float soundTimer;
	private String customName;
	public ItemStack[] chestContents;
	public int numPlayersUsing;
	public BarrelType type;
	public boolean upgrading = false;
	//TODO: Fish barrel Easter Egg

	public TileEntityBarrel(){
		this(BarrelType.WOOD);
	}

	public TileEntityBarrel(BarrelType type) {
		this.type = type;
		this.chestContents = new ItemStack[type.size];
	}

	@Override
	public int getSizeInventory() {
		return type.getSize();
	}

	public int getRowSize() {
		return type.getRowSize();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("Type")){ // compat with barrels placed before iron barrels were added
			this.type = ConfigModCompat.barrelIronChest ? BarrelType.VALUES[compound.getByte("Type")] : BarrelType.WOOD;
		} else {
			this.type = BarrelType.WOOD;
		}
		if (this.chestContents == null || this.chestContents.length != this.getSizeInventory()) {
			this.chestContents = new ItemStack[this.getSizeInventory()];
		}


		Utils.loadItemStacksFromNBT(compound.getTagList("Items", 10), this.chestContents);

		if (compound.hasKey("CustomName", 8)) {
			this.customName = compound.getString("CustomName");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		compound.setByte("Type", (byte) type.ordinal());

		compound.setTag("Items", Utils.writeItemStacksToNBT(this.chestContents));
		
		if (this.hasCustomInventoryName()) {
			compound.setString("CustomName", this.customName);
		}
	}

	@Override
	public ItemStack getStackInSlot(int slotIn) {
		return this.chestContents[slotIn];
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
	 * new stack.
	 */
	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (this.chestContents[index] != null) {
			ItemStack itemstack;

			if (this.chestContents[index].stackSize <= count) {
				itemstack = this.chestContents[index];
				this.chestContents[index] = null;
				this.markDirty();
				return itemstack;
			}
			itemstack = this.chestContents[index].splitStack(count);

			if (this.chestContents[index].stackSize == 0) {
				this.chestContents[index] = null;
			}

			this.markDirty();
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
		if (this.chestContents[index] != null) {
			ItemStack itemstack = this.chestContents[index];
			this.chestContents[index] = null;
			return itemstack;
		}
		return null;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.chestContents[index] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	@Override
	public void updateEntity() {
		++this.ticksSinceSync;
		float f;

		if (!this.worldObj.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200 == 0) {
			this.numPlayersUsing = 0;
			f = 5.0F;
			List<EntityPlayer> list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(this.xCoord - f, this.yCoord - f, this.zCoord - f, this.xCoord + 1 + f, this.yCoord + 1 + f, this.zCoord + 1 + f));
			Iterator<EntityPlayer> iterator = list.iterator();

			while (iterator.hasNext()) {
				EntityPlayer entityplayer = iterator.next();

				if (entityplayer.openContainer instanceof ContainerChestGeneric) {
					++this.numPlayersUsing;
				}
			}
		}

		f = 0.1F;
		double d2;

		if (this.numPlayersUsing > 0 && this.soundTimer <= 0.0F && !worldObj.isRemote) {

			double d1 = this.xCoord + 0.5D;
			d2 = this.zCoord + 0.5D;

			this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord) % 8 + 8, 2);
			this.worldObj.playSoundEffect(d1, this.yCoord + 0.5D, d2, Tags.MC_ASSET_VER + ":block.barrel.open", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);

		}

		if (this.numPlayersUsing == 0 && this.soundTimer > 0.0F || this.numPlayersUsing > 0 && this.soundTimer < 1.0F) {
			float f1 = 0.5F;

			if (this.numPlayersUsing > 0) {
				this.soundTimer += f1;
			} else {
				this.soundTimer -= f1;
			}

			if (this.soundTimer > 10) {
				this.soundTimer = 10;
			}

			if (this.soundTimer < f1 && worldObj.getBlockMetadata(xCoord, yCoord, zCoord) > 7 && !worldObj.isRemote) {
				this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord) % 8, 2);
				this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, Tags.MC_ASSET_VER + ":block.barrel.close", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.soundTimer < 0) {
				this.soundTimer = 0;
			}
		}
	}

	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : "container." + Tags.MOD_ID + ".barrel";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.customName != null && !this.customName.isEmpty();
	}

	public void setCustomName(String p_145976_1_) {
		this.customName = p_145976_1_;
	}

	@Override
	public boolean receiveClientEvent(int id, int type) {
		if (id == 1) {
			this.numPlayersUsing = type;
			return true;
		}
		return super.receiveClientEvent(id, type);
	}

	@Override
	public void openInventory() {
		if (this.numPlayersUsing < 0) {
			this.numPlayersUsing = 0;
		}

		++this.numPlayersUsing;
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numPlayersUsing);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
	}

	@Override
	public void closeInventory() {
		if (this.getBlockType() instanceof BlockBarrel) {
			--this.numPlayersUsing;
			this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numPlayersUsing);
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
		}
	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	/// Needed so the TESR is not bound to other barrel types
	public static class ClearTE extends TileEntityBarrel {
		private ItemStack[] topStacks;
		private ItemStack[] prevContents;

		public ClearTE() {
			this(BarrelType.WOOD);
		}

		public ClearTE(BarrelType type) {
			super(type);
			topStacks = new ItemStack[8];
		}

		public ItemStack[] getTopItemStacks() {
			return topStacks;
		}

		@Override
		public void updateEntity() {
			super.updateEntity();
			if(isContainerDirty(false)) {
				sortTopStacks();
			}
		}

		@Override
		public void closeInventory() {
			super.closeInventory();
			if(isContainerDirty(true)) {
				sortTopStacks();
			}
		}

		public boolean isContainerDirty(boolean checkImmediately) {
			if(prevContents == null || prevContents.length != chestContents.length) {
				return true;
			} else if(checkImmediately || (numPlayersUsing > 0 && worldObj.getWorldTime() % 5 == 0)) {
				for (int i = 0; i < this.chestContents.length; i++) {
					if (!ItemStack.areItemStacksEqual(chestContents[i], prevContents[i])) {
						return true;
					}
				}
			}
			return false;
		}

		public void sortTopStacks() {
			if ((worldObj == null || worldObj.isRemote)) {
				return;
			}
			prevContents = chestContents.clone();
			Arrays.fill(topStacks, null);
			ItemStack[] tempCopy = new ItemStack[getSizeInventory()];
			ItemStack[] contents = chestContents;
			int compressedIdx = 0;
			mainLoop:
			for (int i = 0; i < getSizeInventory(); i++) {
				if (contents[i] != null) {
					for (int j = 0; j < compressedIdx; j++) {
						if (tempCopy[j].isItemEqual(contents[i])) {
							if ((tempCopy[j].stackSize += contents[i].stackSize) > 96) {
								tempCopy[j].stackSize = 96;
							}
							continue mainLoop;
						}
					}
					tempCopy[compressedIdx++] = contents[i].copy();
				}
			}
			Arrays.sort(tempCopy, (o1, o2) -> {
				if (o1 == null) {
					return 1;
				} else if (o2 == null) {
					return -1;
				} else {
					return o2.stackSize - o1.stackSize;
				}
			});
			int p = 0;
			for (ItemStack itemStack : tempCopy) {
				if (itemStack != null && itemStack.stackSize > 0) {
					topStacks[p++] = itemStack;
					if (p == topStacks.length) {
						break;
					}
				}
			}
			for (int i = p; i < topStacks.length; i++) {
				topStacks[i] = null;
			}
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public Packet getDescriptionPacket() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("Display", Utils.writeItemStacksToNBT(topStacks));
			return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
		}

		@Override
		public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
			Arrays.fill(topStacks, null);
			Utils.loadItemStacksFromNBT(pkt.func_148857_g().getTagList("Display", 10), topStacks);
		}
	}

	public enum BarrelType {
		WOOD(27, 9,  184, 168, false, null, Block.soundTypeWood),
		IRON(54, 9,  184, 202, false, "ironcontainer", Block.soundTypeMetal),
		GOLD(81, 9,  184, 256, false, "goldcontainer", Block.soundTypeMetal),
		DIAMOND(108, 12,  238, 256, false, "diamondcontainer", Block.soundTypeMetal),
		CRYSTAL(108, 12,  238, 256, true, "diamondcontainer", ModSounds.soundAmethystBlock),
		COPPER(45, 9,  184, 184, false, "coppercontainer", ModSounds.soundCopper),
		SILVER(72, 9,  184, 238, false, "silvercontainer", Block.soundTypeMetal),
		STEEL(72, 9, 184, 238, false, "silvercontainer", Block.soundTypeMetal),
		OBSIDIAN(108, 12, 238, 256, false, "diamondcontainer", Block.soundTypeStone),
		DARKSTEEL(135, 15, 292, 256, false, "netheritecontainer", Block.soundTypeMetal),
		NETHERITE(135, 15, 292, 256, false, "netheritecontainer", ModSounds.soundNetherite);

		public static final BarrelType[] VALUES = values();

		private final int size;
		private final int rowSize;
		private final int xSize;
		private final int ySize;
		private final boolean clear;
		private final String guiTextureName;
		private final Block.SoundType sound;
		public static final Map<String, BarrelType> map;
		static {
			Object2ObjectLinkedOpenHashMap<String, BarrelType> temp = new Object2ObjectLinkedOpenHashMap<>();
			for(BarrelType type : values()) {
				temp.put(type.name(), type);
			}
			map = Collections.unmodifiableMap(temp);
		}

		BarrelType(int size, int rowSize, int xSize, int ySize, boolean clear, String guiTextureName, Block.SoundType sound) {
			this.size = size;
			this.rowSize = rowSize;
			this.xSize = xSize;
			this.ySize = ySize;
			this.clear = clear;
			this.guiTextureName = guiTextureName;
			this.sound = sound;
		}

        public Block getBlock() {
            return switch (this) {
                case WOOD -> ModBlocks.BARREL.get();
                case IRON -> ModBlocks.IRON_BARREL.get();
                case GOLD -> ModBlocks.GOLD_BARREL.get();
                case DIAMOND -> ModBlocks.DIAMOND_BARREL.get();
				case CRYSTAL -> ModBlocks.CRYSTAL_BARREL.get();
				case COPPER -> ModBlocks.COPPER_BARREL.get();
                case SILVER -> ModBlocks.SILVER_BARREL.get();
				case STEEL -> ModBlocks.STEEL_BARREL.get();
                case OBSIDIAN -> ModBlocks.OBSIDIAN_BARREL.get();
				case DARKSTEEL -> ModBlocks.DARKSTEEL_BARREL.get();
                case NETHERITE -> ModBlocks.NETHERITE_BARREL.get();
            };
        }

		public static BarrelType fromBlock(Block block) {
			if(block == ModBlocks.IRON_BARREL.get()) {
				return IRON;
			}
			if(block == ModBlocks.DIAMOND_BARREL.get()) {
				return DIAMOND;
			}
			if(block == ModBlocks.CRYSTAL_BARREL.get()) {
				return CRYSTAL;
			}
			if(block == ModBlocks.COPPER_BARREL.get()) {
				return COPPER;
			}
			if(block == ModBlocks.SILVER_BARREL.get()) {
				return SILVER;
			}
			if(block == ModBlocks.STEEL_BARREL.get()) {
				return STEEL;
			}
			if(block == ModBlocks.OBSIDIAN_BARREL.get()) {
				return GOLD;
			}
			if(block == ModBlocks.DARKSTEEL_BARREL.get()) {
				return DARKSTEEL;
			}
			if(block == ModBlocks.NETHERITE_BARREL.get()) {
				return NETHERITE;
			}
			return WOOD;
		}

        public int getSize() {
			return size;
		}

		public int getRowSize() {
			return rowSize;
		}

		public int getXSize() {
			return xSize;
		}

		public int getYSize() {
			return ySize;
		}

		public boolean isClear() {
			return clear;
		}

		public String getGuiTextureName() {
			return guiTextureName;
		}

		public Block.SoundType getSound() {
			return sound;
		}
	}

}
