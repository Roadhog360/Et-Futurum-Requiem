package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockCopperBulb extends BlockCopper {

	protected final boolean powered;

	public BlockCopperBulb(boolean powered) {
		super(powered ? new String[]{
				"copper_bulb_powered", "exposed_copper_bulb_powered", "weathered_copper_bulb_powered", "oxidized_copper_bulb_powered",
				"copper_bulb_lit_powered", "exposed_copper_bulb_lit_powered", "weathered_copper_bulb_lit_powered", "oxidized_copper_bulb_lit_powered",
				"waxed_copper_bulb_powered", "waxed_exposed_copper_bulb_powered", "waxed_weathered_copper_bulb_powered", "waxed_oxidized_copper_bulb_powered",
				"waxed_copper_bulb_lit_powered", "waxed_exposed_copper_bulb_lit_powered", "waxed_weathered_copper_bulb_lit_powered", "waxed_oxidized_copper_bulb_lit"
		} : new String[]{
				"copper_bulb", "exposed_copper_bulb", "weathered_copper_bulb", "oxidized_copper_bulb",
				"copper_bulb_lit", "exposed_copper_bulb_lit", "weathered_copper_bulb_lit", "oxidized_copper_bulb_lit",
				"waxed_copper_bulb", "waxed_exposed_copper_bulb", "waxed_weathered_copper_bulb", "waxed_oxidized_copper_bulb",
				"waxed_copper_bulb_lit", "waxed_exposed_copper_bulb_lit", "waxed_weathered_copper_bulb_lit", "waxed_oxidized_copper_bulb_lit"
		});
		this.powered = powered;
		setStepSound(ModSounds.soundCopperBulb);
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		String type = getTypes().get((stack.getItemDamage() % 4) + (stack.getItemDamage() > 7 ? 8 : 0));
		return type == null ? getUnlocalizedName() : type;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(item, 1));
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 3));
		list.add(new ItemStack(item, 1, 8));
		list.add(new ItemStack(item, 1, 9));
		list.add(new ItemStack(item, 1, 10));
		list.add(new ItemStack(item, 1, 11));
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return switch (world.getBlockMetadata(x, y, z) % 8) {
			case 7 -> 4;
			case 6 -> 8;
			case 5 -> 12;
			case 4 -> 15;
			default -> 0;
		};
	}

	@Override
	public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
		return switch (worldIn.getBlockMetadata(x, y, z) % 8) {
			case 4, 5, 6, 7 -> 15;
			default -> 0;
		};
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		if (!world.isRemote) {
			boolean poweredByRedstone = world.isBlockIndirectlyGettingPowered(x, y, z);
			if ((!powered && poweredByRedstone) || (powered && !poweredByRedstone)) {
				switchBulbState(world, x, y, z);
			}
		}
	}

	/**
	 * Ticks the block if it's been scheduled
	 * Use this if I introduce the 1 tick delay
	 */
//	public void updateTick(World world, int x, int y, int z, Random random)
//	{
//		if (!world.isRemote)
//		{
//			switchBulbState(world, x, y, z);
//		}
//	}
	private void switchBulbState(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		boolean lit = isLit(world, x, y, z, meta);
		if (!powered) {
			world.playSoundEffect(x, y, z, Tags.MC_ASSET_VER + ":block.copper_bulb.turn_" + (lit ? "off" : "on"), 1, 1);
			if (lit) {
				meta -= 4;
			} else {
				meta += 4;
			}
		}
		world.setBlock(x, y, z, getOppositeState(), meta, 3);
	}

	protected boolean isLit(World world, int x, int y, int z, int meta) {
		return meta % 8 > 3;
	}

	protected Block getOppositeState() {
		return powered ? ModBlocks.COPPER_BULB.get() : ModBlocks.POWERED_COPPER_BULB.get();
	}

	@Override
	public int damageDropped(int meta) {
		return (meta % 4) + (meta > 7 ? 8 : 0);
	}

	@Override
	public int getDamageValue(World worldIn, int x, int y, int z) {
		return damageDropped(worldIn.getBlockMetadata(x, y, z));
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return ModBlocks.COPPER_BULB.getItem();
	}

	@Override
	public Item getItem(World worldIn, int x, int y, int z) {
		return ModBlocks.COPPER_BULB.getItem();
	}
}