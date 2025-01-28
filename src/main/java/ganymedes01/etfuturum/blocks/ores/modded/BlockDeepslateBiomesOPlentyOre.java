package ganymedes01.etfuturum.blocks.ores.modded;

import ganymedes01.etfuturum.blocks.ores.BaseSubtypesDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;
import net.minecraft.block.Block;

public class BlockDeepslateBiomesOPlentyOre extends BaseSubtypesDeepslateOre {
	public BlockDeepslateBiomesOPlentyOre() {
		super("deepslate_ruby_ore", "deepslate_peridot_ore", "deepslate_topaz_ore", "deepslate_tanzanite_ore", "deepslate_malachite_ore", "deepslate_sapphire_ore", "deepslate_amber_ore");
	}

	@Override
	public String getTextureSubfolder(String name) {
		return "bop";
	}

	@Override
	public Block getBase(int meta) {
		return ExternalContent.Blocks.BOP_GEM_ORE.get();
	}

	@Override
	public int getBaseMeta(int meta) {
		return switch (meta) {
			case 0 -> 2;
			case 1 -> 4;
			case 2 -> 6;
			case 3 -> 8;
			case 4 -> 10;
			case 5 -> 12;
			case 6 -> 14;
			default -> 0;
		};
	}
}
