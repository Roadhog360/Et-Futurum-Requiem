package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class BlockNetherite extends Block {

	public BlockNetherite() {
		super(Material.iron);
		setHarvestLevel("pickaxe", 3);
		setHardness(50F);
		setResistance(1200F);
		setStepSound(ModSounds.soundNetherite);
		setBlockTextureName("netherite_block");
		setBlockName("netherite_block");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public boolean isBeaconBase(IBlockAccess world, int x, int y, int z, int bX, int bY, int bZ) {
		return true;
	}
}
