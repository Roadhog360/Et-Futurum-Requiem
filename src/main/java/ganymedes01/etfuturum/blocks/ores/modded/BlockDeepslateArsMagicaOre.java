package ganymedes01.etfuturum.blocks.ores.modded;

import ganymedes01.etfuturum.blocks.ores.BaseSubtypesDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;
import net.minecraft.block.Block;

public class BlockDeepslateArsMagicaOre extends BaseSubtypesDeepslateOre {
	public BlockDeepslateArsMagicaOre() {
		super("deepslate_vinteum_ore", "deepslate_chimerite_ore", "deepslate_blue_topaz_ore");
	}

	@Override
	public String getTextureSubfolder(String name) {
		return "am2";
	}

	@Override
	public Block getBase(int meta) {
		return ExternalContent.Blocks.ARS_MAGICA_2_ORE.get();
	}

	@Override
	public int getBaseMeta(int meta) {
		return meta;
	}
}
