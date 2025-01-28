package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockConcretePowder extends BaseEFRSand {

	public BlockConcretePowder() {
		super("white_concrete_powder", "orange_concrete_powder", "magenta_concrete_powder", "light_blue_concrete_powder", "yellow_concrete_powder",
				"lime_concrete_powder", "pink_concrete_powder", "gray_concrete_powder", "light_gray_concrete_powder", "cyan_concrete_powder", "purple_concrete_powder",
				"blue_concrete_powder", "brown_concrete_powder", "green_concrete_powder", "red_concrete_powder", "black_concrete_powder");
		this.setHarvestLevel("shovel", 0);
		this.setHardness(.5F);
		this.setResistance(.5F);
		setNames("concrete_powder");
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		if (!this.setBlock(world, x, y, z)) {
			super.onBlockAdded(world, x, y, z);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (!this.setBlock(world, x, y, z)) {
			super.onNeighborBlockChange(world, x, y, z, block);
		}
	}

	private boolean setBlock(World world, int x, int y, int z) {
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (dir != ForgeDirection.DOWN && world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ).getMaterial() == Material.water) {
				world.setBlock(x, y, z, ModBlocks.CONCRETE.get(), world.getBlockMetadata(x, y, z), 3);
				return true;
			}
		}
		return false;
	}

	@Override
	public MapColor getMapColor(int meta) {
		return MapColor.getMapColorForBlockColored(meta);
	}

}
