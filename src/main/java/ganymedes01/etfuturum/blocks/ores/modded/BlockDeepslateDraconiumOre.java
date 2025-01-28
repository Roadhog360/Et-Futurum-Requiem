package ganymedes01.etfuturum.blocks.ores.modded;

import ganymedes01.etfuturum.blocks.ores.BaseDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;
import net.minecraft.block.Block;

public class BlockDeepslateDraconiumOre extends BaseDeepslateOre {
	public BlockDeepslateDraconiumOre() {
		super();
		setNames("deepslate_draconium_ore");
	}

	@Override
	public String getTextureSubfolder(String name) {
		return "draconic";
	}

	@Override
	public Block getBase() {
		return ExternalContent.Blocks.DRACONIUM_ORE.get();
	}
}
