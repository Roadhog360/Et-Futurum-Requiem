package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.material.Material;

public class BlockBasaltRemove extends BaseEFRPillar {

	public BlockBasaltRemove() {
		super(Material.rock, "basalt", "polished_basalt");
		setHardness(1.25F);
		setResistance(4.2F);
		setNames("basalt");
		setStepSound(ModSounds.soundBasalt);
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}
}
