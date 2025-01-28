package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockPackedMud extends BaseEFRBlock {

	public BlockPackedMud() {
		super(Material.rock, "packed_mud", "mud_bricks");
		setHardness(1);
		setResistance(3);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setStepSound(ModSounds.soundPackedMud);
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		if (world.getBlockMetadata(x, y, z) == 1) {
			return 1.5F;
		}
		return blockHardness;
	}
}
