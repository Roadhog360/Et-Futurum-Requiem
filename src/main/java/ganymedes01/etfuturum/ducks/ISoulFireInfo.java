package ganymedes01.etfuturum.ducks;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.compat.ExternalContent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public interface ISoulFireInfo {

	default boolean efr$isSoulFire(IBlockAccess world, int x, int y, int z) {
		Block block = world.getBlock(x, y - 1, z);
		return block == Blocks.soul_sand || block == ModBlocks.SOUL_SOIL.get() || block == ExternalContent.Blocks.NETHERLICIOUS_SOUL_SOIL.get();
	}

	IIcon getSoulFireIcon(int type);
}
