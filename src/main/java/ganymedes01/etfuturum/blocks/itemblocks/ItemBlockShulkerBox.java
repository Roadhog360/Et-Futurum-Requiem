package ganymedes01.etfuturum.blocks.itemblocks;

import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.dispenser.DispenserBehaviourShulkerBox;
import ganymedes01.etfuturum.recipes.ModRecipes;
import ganymedes01.etfuturum.tileentities.TileEntityShulkerBox;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.List;

public class ItemBlockShulkerBox extends ItemBlock {

	public ItemBlockShulkerBox(Block p_i45328_1_) {
		super(p_i45328_1_);
		if (ConfigBlocksItems.enableShulkerBoxes) {
			BlockDispenser.dispenseBehaviorRegistry.putObject(this, new DispenserBehaviourShulkerBox());
		}
		this.setMaxStackSize(1);
	}

	@Override
	public int getMetadata(int i) {
		return field_150939_a/*blockInstance*/.damageDropped(i);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int type = stack.hasTagCompound() ? stack.getTagCompound().getByte("Type") : 0;
		int color = stack.hasTagCompound() ? stack.getTagCompound().getByte("Color") : 0;

		String string = field_150939_a/*blockInstance*/.getUnlocalizedName().substring(15);
		if (type > 0 && type - 1 < TileEntityShulkerBox.tiers.length) {
			string = TileEntityShulkerBox.tiers[type - 1] + "_" + string;
		}
		if (color > 0) {
			String dye = ModRecipes.dye_names[stack.getTagCompound().getByte("Color") - 1 % ModRecipes.dye_names.length];
			string = dye + "_" + string;
		}
		return "tile." + string;
	}

	@Override
	public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
		Block block = p_77648_3_.getBlock(p_77648_4_, p_77648_5_, p_77648_6_);
		int meta = p_77648_3_.getBlockMetadata(p_77648_4_, p_77648_5_, p_77648_6_);
		if (block == Blocks.cauldron && meta > 0 && p_77648_1_.hasTagCompound() && p_77648_1_.getTagCompound().hasKey("Color")) {
			p_77648_1_.getTagCompound().removeTag("Color");
			if (p_77648_1_.getTagCompound().hasNoTags()) {
				p_77648_1_.setTagCompound(null);
			}
			p_77648_3_.setBlockMetadataWithNotify(p_77648_4_, p_77648_5_, p_77648_6_, meta - 1, 2);
			return true;
		}
		return super.onItemUse(p_77648_1_, p_77648_2_, p_77648_3_, p_77648_4_, p_77648_5_, p_77648_6_, p_77648_7_, p_77648_8_, p_77648_9_, p_77648_10_);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		if (!world.setBlock(x, y, z, field_150939_a/*blockInstance*/, metadata, 3)) {
			return false;
		}

		if (world.getBlock(x, y, z) == field_150939_a) { // blockInstance
			field_150939_a/*blockInstance*/.onBlockPlacedBy(world, x, y, z, player, stack);
			field_150939_a/*blockInstance*/.onPostBlockPlaced(world, x, y, z, metadata);
		}

		TileEntityShulkerBox box = (TileEntityShulkerBox) world.getTileEntity(x, y, z);
		box.facing = (byte) side;
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> lore, boolean f3h) {
		if (ConfigFunctions.shulkerBoxTooltipLines > 0 && stack.getTagCompound() != null && stack.getTagCompound().hasKey("Items")) {
			NBTTagList tag = stack.getTagCompound().getTagList("Items", 10);
			int items = 0;
			for (int i = 0; i < tag.tagCount(); ++i) {
				NBTTagCompound nbttagcompound1 = tag.getCompoundTagAt(i);
				ItemStack containerStack = ItemStack.loadItemStackFromNBT(nbttagcompound1);
				if (containerStack != null && containerStack.getItem() != null) {
					items++;
					if (lore.size() <= ConfigFunctions.shulkerBoxTooltipLines) {
						try {
							lore.add(containerStack.getDisplayName() + " x" + containerStack.stackSize);
						} catch (Exception e) {
							lore.add("missingno x" + containerStack.stackSize);
						}
					}
				}
			}
			if (items > ConfigFunctions.shulkerBoxTooltipLines) {
				lore.add("\u00a7oAnd " + (items - ConfigFunctions.shulkerBoxTooltipLines) + " more...");
			}
		}
	}
}
