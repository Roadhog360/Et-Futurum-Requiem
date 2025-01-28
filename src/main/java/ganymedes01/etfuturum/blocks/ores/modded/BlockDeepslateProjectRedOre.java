package ganymedes01.etfuturum.blocks.ores.modded;

import ganymedes01.etfuturum.blocks.ores.BaseSubtypesDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;
import net.minecraft.block.Block;

public class BlockDeepslateProjectRedOre extends BaseSubtypesDeepslateOre {
	public BlockDeepslateProjectRedOre() {
		super("deepslate_ruby_ore", "deepslate_sapphire_ore", "deepslate_peridot_ore", "deepslate_electrotine_ore");
	}

	@Override
	public String getTextureSubfolder(String name) {
		return "projectred";
	}

	@Override
	public Block getBase(int meta) {
		return ExternalContent.Blocks.PROJECT_RED_ORE.get();
	}

	@Override
	public int getBaseMeta(int meta) {
		if (meta == 3) {
			return 6;
		}
		return meta;
	}
}
