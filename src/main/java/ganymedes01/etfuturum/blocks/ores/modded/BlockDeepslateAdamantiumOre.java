package ganymedes01.etfuturum.blocks.ores.modded;

import ganymedes01.etfuturum.blocks.ores.BaseDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;
import net.minecraft.block.Block;

public class BlockDeepslateAdamantiumOre extends BaseDeepslateOre {

	public BlockDeepslateAdamantiumOre() {
		setNames("deepslate_adamantium_ore");
	}

	@Override
	public String getTextureSubfolder(String name) {
		return "simpleores";
	}

	@Override
	public Block getBase() {
		return ExternalContent.Blocks.SIMPLEORES_ADAMANTIUM_ORE.get();
	}
}
