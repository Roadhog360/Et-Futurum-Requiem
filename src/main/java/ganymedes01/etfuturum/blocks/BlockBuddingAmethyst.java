package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import roadhog360.hogutils.api.utils.GenericUtils;

import java.util.Random;

public class BlockBuddingAmethyst extends BlockAmethystBlock {

	public BlockBuddingAmethyst() {
		setHardness(1.5F);
		setResistance(1.5F);
		setBlockTextureName("budding_amethyst");
		setBlockName("budding_amethyst");
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setTickRandomly(true);
	}

	@Override
	public int getMobilityFlag() {
		return ConfigWorld.buddingAmethystMode == 0 ? 1 : 0;
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		return ConfigWorld.buddingAmethystMode != 0;
	}

	@Override
	public int quantityDropped(Random random) {
		return ConfigWorld.buddingAmethystMode == 2 ? 1 : 0;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return ConfigWorld.buddingAmethystMode == 2 ? super.getItemDropped(meta, random, fortune) : null;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (rand.nextInt(5) == 0) {
			EnumFacing facing = EnumFacing.getFront(rand.nextInt(GenericUtils.Constants.ENUM_FACING_VALUES.length));
			Block block = world.getBlock(x + facing.getFrontOffsetX(), y + facing.getFrontOffsetY(), z + facing.getFrontOffsetZ());
			int meta = world.getBlockMetadata(x + facing.getFrontOffsetX(), y + facing.getFrontOffsetY(), z + facing.getFrontOffsetZ());

			if (block instanceof BlockAmethystCluster && meta % 6 == facing.ordinal()) {
				if (meta < 6) {
					world.setBlockMetadataWithNotify(x + facing.getFrontOffsetX(), y + facing.getFrontOffsetY(), z + facing.getFrontOffsetZ(), meta + 6, 3);
				} else if (block == ModBlocks.AMETHYST_CLUSTER_1.get()) {
					world.setBlock(x + facing.getFrontOffsetX(), y + facing.getFrontOffsetY(), z + facing.getFrontOffsetZ(), ModBlocks.AMETHYST_CLUSTER_2.get(), meta - 6, 3);
				}
			} else if (canGrowIn(block)) {
				world.setBlock(x + facing.getFrontOffsetX(), y + facing.getFrontOffsetY(), z + facing.getFrontOffsetZ(), ModBlocks.AMETHYST_CLUSTER_1.get(), facing.ordinal(), 3);
			}
		}
	}

	private boolean canGrowIn(Block state) {
		return state.getMaterial() == Material.air || state.getMaterial() == Material.water;
	}
}
