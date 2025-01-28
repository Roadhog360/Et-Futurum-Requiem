package ganymedes01.etfuturum.blocks.rawore.modded;

import ganymedes01.etfuturum.blocks.rawore.BaseRawOreBlock;
import ganymedes01.etfuturum.compat.ExternalContent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockRawAdamantium extends BaseRawOreBlock {
	public BlockRawAdamantium() {
		super(Material.rock);
		setNames("raw_adamantium_block");
	}

	@Override
	public String getTextureSubfolder(String name) {
		return "simpleores";
	}

	@Override
	protected Block getBase() {
		return ExternalContent.Blocks.SIMPLEORES_ADAMANTIUM_BLOCK.get();
	}
}
