package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import net.minecraft.block.material.Material;

public class BlockNewNetherBrick extends BaseEFRBlock {

	public BlockNewNetherBrick() {
		super(Material.rock, "red_nether_bricks", "cracked_nether_bricks", "chiseled_nether_bricks");
		this.setResistance(6);
		this.setHardness(2);
		setStepSound(ConfigSounds.newBlockSounds ? ModSounds.soundNetherBricks : soundTypePiston);
		this.setHarvestLevel("pickaxe", 0);
		this.setBlockTextureName("nether_bricks");
		setBlockName("red_netherbrick");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}
}
