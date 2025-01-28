package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.material.Material;

public class BlockBambooMosaicRemove extends BaseEFRBlock {
	public BlockBambooMosaicRemove() {
		super(Material.wood);
		setHardness(2.0F);
		setResistance(5.0F);
		setNames("bamboo_mosaic");
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setStepSound(ModSounds.soundBambooWood);
	}
}
