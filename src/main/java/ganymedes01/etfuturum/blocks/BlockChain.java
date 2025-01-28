package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockChain extends BaseEFRBlock {

	public BlockChain() {
		super(Material.iron);
		setNames("chain");
		setResistance(6F);
		setHardness(5F);
		setStepSound(ModSounds.soundChain);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		switch (world.getBlockMetadata(x, y, z)) {
			case 0:
			default:
				return AxisAlignedBB.getBoundingBox(x + 0.40625D, y, z + 0.40625D, x + 0.59375D, y + 1.0D, z + 0.59375D);
			case 1:
				return AxisAlignedBB.getBoundingBox(x, y + 0.40625D, z + 0.40625D, x + 1.0D, y + 0.59375D, z + 0.59375D);
			case 2:
				return AxisAlignedBB.getBoundingBox(x + 0.40625D, y + 0.40625D, z, x + 0.59375D, y + 0.59375D, z + 1.0D);
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		switch (world.getBlockMetadata(x, y, z)) {
			case 1:
				setBlockBounds(0, 0.40625F, 0.40625F, 1.0F, 0.59375F, 0.59375F);
				break;
			case 2:
				setBlockBounds(0.40625F, 0.40625F, 0, 0.59375F, 0.59375F, 1.0F);
				break;
			case 0:
			default:
				setBlockBounds(0.40625F, 0, 0.40625F, 0.59375F, 1.0F, 0.59375F);
				break;
		}
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
		switch (side) {
			case 2:
			case 3:
				return 2;
			case 4:
			case 5:
				return 1;
			case 0:
			case 1:
			default:
				return 0;
		}
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isNormalCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public String getItemIconName() {
		return "chain";
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.CHAIN.getRenderId();
	}
}
