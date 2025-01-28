package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

import static net.minecraftforge.common.util.ForgeDirection.UP;

public class BlockMagma extends BaseEFRBlock {

	public BlockMagma() {
		super(Material.rock);
		setHardness(0.5F);
		setResistance(0.5F);
		setLightLevel(0.2F);
		setNames("magma");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	public static final DamageSource HOT_FLOOR = (new DamageSource("hotFloor")).setFireDamage();

	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
		return side == UP;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		Block block1 = world.getBlock(x, y + 1, z);
		Block block2 = world.getBlock(x, y + 2, z);

		if ((block1 == Blocks.water || block1 == Blocks.flowing_water) && block2.isAir(world, x, y + 2, z)) {
			world.spawnParticle("explode", x + 0.5D, y + 1.0D, z + 0.5D, 0.0D, 0.0D, 0.0D);
		} else if ((block1 == Blocks.water || block1 == Blocks.flowing_water) &&
				(block2 == Blocks.water || block2 == Blocks.flowing_water)) {
			world.spawnParticle("bubble", x + 0.5D, y + 1.1D, z + 0.5D, 0.0D, 1.0D, 0.0D);
		}
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess worldIn, int x, int y, int z) {
		return true;
	}
}
