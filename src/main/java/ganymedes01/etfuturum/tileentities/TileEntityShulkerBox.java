package ganymedes01.etfuturum.tileentities;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.blocks.BlockShulkerBox;
import ganymedes01.etfuturum.blocks.itemblocks.ItemBlockShulkerBox;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import ganymedes01.etfuturum.core.utils.EtFuturumResources;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.inventory.ContainerChestGeneric;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TileEntityShulkerBox extends TileEntity implements IInventory {
	private int ticksSinceSync;
	private String customName;
	public ItemStack[] chestContents;
	public int numPlayersUsing;
	protected boolean brokenInCreative = false;
	public byte color = 0;
	public byte facing = 0;
	public ShulkerBoxType type;

	public TileEntityShulkerBox() {
		this.animationStatus = TileEntityShulkerBox.AnimationStatus.CLOSED;
		this.topStacks = new ItemStack[8];
		type = ShulkerBoxType.VANILLA;
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
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		this.type = ConfigModCompat.shulkerBoxesIronChest ? ShulkerBoxType.VALUES[nbt.getByte("Type")] : ShulkerBoxType.VANILLA;

		if (this.chestContents == null || this.chestContents.length != this.getSizeInventory()) {
			this.chestContents = new ItemStack[this.getSizeInventory()];
		}

		Utils.loadItemStacksFromNBT(nbt.getTagList("Items", 10), this.chestContents);

		if (type.isClear()) {
			Arrays.fill(topStacks, null);
			Utils.loadItemStacksFromNBT(nbt.getTagList("Display", 10), this.topStacks);
		}

		if (ConfigBlocksItems.enableDyedShulkerBoxes) {
			color = nbt.getByte("Color");
		}

		if (nbt.hasKey("Facing")) {
			facing = nbt.getByte("Facing");
		}

		if (nbt.hasKey("CustomName", 8)) {
			this.customName = nbt.getString("CustomName");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setByte("Type", (byte) type.ordinal());

		if (chestContents != null) {
			nbt.setTag("Items", Utils.writeItemStacksToNBT(this.chestContents));
		}

		nbt.setByte("Color", color);
		nbt.setByte("Facing", facing);

		if (this.hasCustomInventoryName()) {
			nbt.setString("CustomName", this.customName);
		}
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
		this.updateAnimation();

		if (this.animationStatus == TileEntityShulkerBox.AnimationStatus.OPENING || this.animationStatus == TileEntityShulkerBox.AnimationStatus.CLOSING) {
			this.moveCollidedEntities();
		}

		++this.ticksSinceSync;
		float f;

		if (!this.worldObj.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200 == 0) {
			this.numPlayersUsing = 0;
			f = 5.0F;
			List<EntityPlayer> list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(this.xCoord - f, this.yCoord - f, this.zCoord - f, this.xCoord + 1 + f, this.yCoord + 1 + f, this.zCoord + 1 + f));

			for (EntityPlayer entityplayer : list) {
				if (entityplayer.openContainer instanceof ContainerChestGeneric) {
					++this.numPlayersUsing;
				}
			}
		}
		if(type.isClear() && isContainerDirty(false)) {
			sortTopStacks();
		}
	}

	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : "container." + Tags.MOD_ID + ".shulker_box";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.customName != null && this.customName.length() > 0;
	}

	public void setCustomName(String p_145976_1_) {
		this.customName = p_145976_1_;
	}

	@Override
	public boolean receiveClientEvent(int id, int type) {
		if (id == 1) {
			this.numPlayersUsing = type;

			if (type == 0) {
				this.animationStatus = TileEntityShulkerBox.AnimationStatus.CLOSING;
			}

			if (type == 1) {
				this.animationStatus = TileEntityShulkerBox.AnimationStatus.OPENING;
			}

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

		if (this.numPlayersUsing == 1) {
			this.worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, Tags.MC_ASSET_VER + ":block.shulker_box.open", 1, 1);
		}
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numPlayersUsing);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
	}

	@Override
	public void closeInventory() {
		if (this.getBlockType() instanceof BlockShulkerBox) {
			--this.numPlayersUsing;
			if (this.numPlayersUsing <= 0) {
				this.worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, Tags.MC_ASSET_VER + ":block.shulker_box.close", 1, 1);
			}
			this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numPlayersUsing);
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
		}
		if(type.isClear() && isContainerDirty(true)) {
			sortTopStacks();
		}
	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return !(stack.getItem() instanceof ItemBlockShulkerBox || ConfigFunctions.shulkerBans.contains(stack.getItem()));
	}

	/**
	 * invalidates a tile entity
	 */
	@Override
	public void invalidate() {
		super.invalidate();
		this.updateContainingBlockInfo();
	}

	public boolean onBlockBreak(EntityPlayer player) {
		brokenInCreative = player.capabilities.isCreativeMode;
		return true;
	}

	public ItemStack createItemStack(EntityPlayer player) {
		boolean empty = true;
		for (ItemStack chestContent : chestContents) {
			if (chestContent != null) {
				empty = false;
				break;
			}
		}
		// Don't drop an empty Shulker Box in creative.
		if ((!empty || player == null || !player.capabilities.isCreativeMode) && worldObj.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
			return writeToStack(ModBlocks.SHULKER_BOX.newItemStack());
		}
		return null;
	}

	public ItemStack writeToStack(ItemStack stack) {
		NBTTagList nbttaglist = new NBTTagList();

		for (int l = 0; l < this.chestContents.length; ++l) {
			if (this.chestContents[l] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) l);
				this.chestContents[l].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		stack.setTagCompound(new NBTTagCompound());
		if (nbttaglist.tagCount() > 0)
			stack.getTagCompound().setTag("Items", nbttaglist);

		byte typeOrd = (byte) type.ordinal();
		if (typeOrd > 0)
			stack.getTagCompound().setByte("Type", typeOrd);

		if (color > 0)
			stack.getTagCompound().setByte("Color", color);

		if (this.hasCustomInventoryName()) {
			stack.setStackDisplayName(this.getInventoryName());
		}

		return stack;
	}

	private float progress;
	private float progressOld;
	private TileEntityShulkerBox.AnimationStatus animationStatus;

	public float getProgress(float p_190585_1_) {
		return this.progressOld + (this.progress - this.progressOld) * p_190585_1_;
	}

	protected void updateAnimation() {
		this.progressOld = this.progress;

		switch (this.animationStatus) {
			case CLOSED:
				this.progress = 0.0F;
				break;

			case OPENING:
				this.progress += 0.1F;

				if (this.progress >= 1.0F) {
					this.moveCollidedEntities();
					this.animationStatus = TileEntityShulkerBox.AnimationStatus.OPENED;
					this.progress = 1.0F;
				}

				break;

			case CLOSING:
				this.progress -= 0.1F;

				if (this.progress <= 0.0F) {
					this.animationStatus = TileEntityShulkerBox.AnimationStatus.CLOSED;
					this.progress = 0.0F;
				}

				break;

			case OPENED:
				this.progress = 1.0F;
		}
	}

	public TileEntityShulkerBox.AnimationStatus getAnimationStatus() {
		return this.animationStatus;
	}

	public AxisAlignedBB getBoundingBox(int facing) {
		return this.getBoundingBox(EnumFacing.getFront(facing));
	}

	public AxisAlignedBB getBoundingBox(EnumFacing p_190587_1_) {
		return AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D).addCoord(0.5F * this.getProgress(1.0F) * (float) p_190587_1_.getFrontOffsetX(), 0.5F * this.getProgress(1.0F) * (float) p_190587_1_.getFrontOffsetY(), 0.5F * this.getProgress(1.0F) * (float) p_190587_1_.getFrontOffsetZ());
	}

	private AxisAlignedBB getTopBoundingBox(EnumFacing p_190588_1_) {
		int ordinal = p_190588_1_.ordinal();
		int opposite = ordinal = ordinal % 2 == 0 ? ordinal + 1 : ordinal - 1;
		EnumFacing enumfacing = EnumFacing.getFront(opposite);
		return contract(this.getBoundingBox(p_190588_1_), enumfacing.getFrontOffsetX(), enumfacing.getFrontOffsetY(), enumfacing.getFrontOffsetZ());
	}

	private void moveCollidedEntities() {
		if (getBlockType() instanceof BlockShulkerBox) {
			EnumFacing enumfacing = EnumFacing.getFront(facing);
			AxisAlignedBB axisalignedbb = this.getTopBoundingBox(enumfacing).offset(xCoord, yCoord, zCoord);
			List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb);

			if (!list.isEmpty()) {
				for (Entity value : list) {
					Entity entity = value;

					if (entity.canBePushed()) {
						double d0 = 0.0D;
						double d1 = 0.0D;
						double d2 = 0.0D;
						AxisAlignedBB axisalignedbb1 = entity.boundingBox;

						if (enumfacing.getFrontOffsetX() != 0) {
							if (enumfacing.getFrontOffsetX() > 0) {
								d0 = axisalignedbb.maxX - axisalignedbb1.minX;
							} else {
								d0 = axisalignedbb1.maxX - axisalignedbb.minX;
							}

							d0 = d0 + 0.01D;
						} else if (enumfacing.getFrontOffsetY() != 0) {
							if (enumfacing.getFrontOffsetY() > 0) {
								d1 = axisalignedbb.maxY - axisalignedbb1.minY;
							} else {
								d1 = axisalignedbb1.maxY - axisalignedbb.minY;
							}

							d1 = d1 + 0.01D;
						} else if (enumfacing.getFrontOffsetZ() != 0) {
							if (enumfacing.getFrontOffsetZ() > 0) {
								d2 = axisalignedbb.maxZ - axisalignedbb1.minZ;
							} else {
								d2 = axisalignedbb1.maxZ - axisalignedbb.minZ;
							}

							d2 = d2 + 0.01D;
						}

						entity.moveEntity(d0 * (double) enumfacing.getFrontOffsetX(), d1 * (double) enumfacing.getFrontOffsetY(), d2 * (double) enumfacing.getFrontOffsetZ());
					}
				}
			}
		}
	}

	
	/**
	 * Creates a new {@link AxisAlignedBB} that has been contracted by the given amount, with positive changes decreasing max values and negative changes increasing min values.
	 * <br/>
	 * If the amount to contract by is larger than the length of a side, then the side will wrap (still creating a valid AABB - see last sample).
	 * 
	 * <h3>Samples:</h3>
	 * <table>
	 * <tr><th>Input</th><th>Result</th></tr>
	 * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 4, 4, 4).contract(2, 2, 2)</code></pre></td><td><pre><samp>box[0.0, 0.0, 0.0 -> 2.0, 2.0, 2.0]</samp></pre></td></tr>
	 * <tr><td><pre><code>new AxisAlignedBB(0, 0, 0, 4, 4, 4).contract(-2, -2, -2)</code></pre></td><td><pre><samp>box[2.0, 2.0, 2.0 -> 4.0, 4.0, 4.0]</samp></pre></td></tr>
	 * <tr><td><pre><code>new AxisAlignedBB(5, 5, 5, 7, 7, 7).contract(0, 1, -1)</code></pre></td><td><pre><samp>box[5.0, 5.0, 6.0 -> 7.0, 6.0, 7.0]</samp></pre></td></tr>
	 * <tr><td><pre><code>new AxisAlignedBB(-2, -2, -2, 2, 2, 2).contract(4, -4, 0)</code></pre></td><td><pre><samp>box[-8.0, 2.0, -2.0 -> -2.0, 8.0, 2.0]</samp></pre></td></tr>
	 * </table>
	 *
	 * 
	 * @return A new modified bounding box.
	 */
	public AxisAlignedBB contract(AxisAlignedBB bb, double x, double y, double z) {
		double d0 = bb.minX;
		double d1 = bb.minY;
		double d2 = bb.minZ;
		double d3 = bb.maxX;
		double d4 = bb.maxY;
		double d5 = bb.maxZ;

		if (x < 0.0D) {
			d0 -= x;
		} else if (x > 0.0D) {
			d3 -= x;
		}

		if (y < 0.0D) {
			d1 -= y;
		} else if (y > 0.0D) {
			d4 -= y;
		}

		if (z < 0.0D) {
			d2 -= z;
		} else if (z > 0.0D) {
			d5 -= z;
		}

		return AxisAlignedBB.getBoundingBox(d0, d1, d2, d3, d4, d5);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();

		super.writeToNBT(nbt);

		nbt.setByte("Type", (byte) this.type.ordinal());

		if(type.isClear()) {
			nbt.setTag("Display", Utils.writeItemStacksToNBT(topStacks));
		}

		nbt.setByte("Color", this.color);
		nbt.setByte("Facing", this.facing);

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g()); // getNbtCompound
	}

	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
		return oldBlock != newBlock || oldMeta != newMeta;
	}

	@Override
	public int getBlockMetadata() {
		if (this.blockMetadata == -1) {
			if (ConfigModCompat.shulkerBoxesIronChest) {
				this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
			} else {
				blockMetadata = 0;
			}
		}

		return this.blockMetadata;
	}

	public enum AnimationStatus {
		CLOSED,
		OPENING,
		OPENED,
		CLOSING
	}

	public enum ShulkerBoxType {
		VANILLA(27, 9, false, 184, 166, null, EtFuturumResources.SHULKERS),
		IRON(54, 9, false, 184, 202, "ironcontainer", EtFuturumResources.IRON_SHULKERS),
		GOLD(81, 9, false, 184, 256, "goldcontainer", EtFuturumResources.GOLD_SHULKERS),
		DIAMOND(108, 12, false, 238, 256, "diamondcontainer", EtFuturumResources.DIAMOND_SHULKERS),
		COPPER(45, 9, false, 184, 184, "coppercontainer", EtFuturumResources.COPPER_SHULKERS),
		SILVER(72, 9, false, 184, 238, "silvercontainer", EtFuturumResources.SILVER_SHULKERS),
		CRYSTAL(108, 12, true, 238, 256, "diamondcontainer", EtFuturumResources.CRYSTAL_SHULKERS),
		OBSIDIAN(108, 12, false, 238, 256, "diamondcontainer", EtFuturumResources.OBSIDIAN_SHULKERS);

		public static final ShulkerBoxType[] VALUES = values();

		private final int size;
		private final int rowSize;
		private final boolean isClear;
		private final int xSize;
		private final int ySize;
		private final String guiTextureName;
		private final ResourceLocation[] boxTextures;
		public static final Map<String, ShulkerBoxType> map;
		static {
			Object2ObjectLinkedOpenHashMap<String, ShulkerBoxType> temp = new Object2ObjectLinkedOpenHashMap<>();
			for(ShulkerBoxType type : values()) {
				temp.put(type.name(), type);
			}
			map = Collections.unmodifiableMap(temp);
		}

		ShulkerBoxType(int size, int rowSize, boolean isClear, int xSize, int ySize, String guiTextureName, ResourceLocation[] boxTextures) {
			this.size = size;
			this.rowSize = rowSize;
			this.isClear = isClear;
			this.xSize = xSize;
			this.ySize = ySize;
			this.guiTextureName = guiTextureName;
			this.boxTextures = boxTextures;
		}

		public int getSize() {
			return size;
		}

		public int getRowSize() {
			return rowSize;
		}

		public boolean isClear() {
			return isClear;
		}

		public int getXSize() {
			return xSize;
		}

		public int getYSize() {
			return ySize;
		}

		@Nullable
		public String getGuiTextureName() {
			return guiTextureName;
		}

		public ResourceLocation[] getBoxTextures() {
			return boxTextures;
		}
	}

	/*
	 * IRON CHESTS CODE START
	 *
	 * ALL CREDIT TO IRON CHEST 1.7.10 AUTHOR
	 *
	 * Iron Shulker Boxes were ported by eye and not by copying code
	 *
	 * However, this code copies crystal chest display code
	 * from ICs 1.7.10 for accuracy's sake
	 *
	 * Some code was also copied to from the chest upgrades
	 * to ensure upgrades function exactly the same way.
	 */

	private ItemStack[] topStacks;
	private ItemStack[] prevContents;

	@Override
	public ItemStack getStackInSlot(int slotIn) {
		return this.chestContents[slotIn];
	}

	public ItemStack[] getTopItemStacks() {
		return topStacks;
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
}