package ganymedes01.etfuturum.dispenser;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.tileentities.TileEntityShulkerBox;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;

public class DispenserBehaviourShulkerBox extends BehaviorDefaultDispenseItem {

	@Override
	protected ItemStack dispenseStack(IBlockSource coords, ItemStack stack) {
		EnumFacing enumfacing = BlockDispenser.func_149937_b(coords.getBlockMetadata()); // getFacingDirection
		int x = coords.getXInt() + enumfacing.getFrontOffsetX();
		int y = coords.getYInt() + enumfacing.getFrontOffsetY();
		int z = coords.getZInt() + enumfacing.getFrontOffsetZ();
		if (coords.getWorld().getBlock(x, y, z).isReplaceable(coords.getWorld(), x, y, z)) {
			boolean successful = coords.getWorld().setBlock(x, y, z, ModBlocks.SHULKER_BOX.get());
			if (!successful) return super.dispenseStack(coords, stack);
			stack.stackSize--;
			TileEntityShulkerBox box = (TileEntityShulkerBox) coords.getWorld().getTileEntity(x, y, z);
			box.facing = enumfacing != EnumFacing.UP && coords.getWorld().getBlock(x, y - 1, z) == Blocks.air ? (byte) enumfacing.ordinal() : 1;
			if (stack.hasTagCompound()) {
				box.type = TileEntityShulkerBox.ShulkerBoxType.VALUES[stack.getTagCompound().getByte("Type")];

				NBTTagList nbttaglist = stack.getTagCompound().getTagList("Items", 10);
				box.chestContents = new ItemStack[box.getSizeInventory()];
				Utils.loadItemStacksFromNBT(nbttaglist, box.chestContents);

				box.color = stack.getTagCompound().getByte("Color");

				if (stack.hasDisplayName()) {
					box.setCustomName(stack.getDisplayName()); // setCustomName
				}
			}
			return stack;
		}
		return super.dispenseStack(coords, stack);
	}
}
