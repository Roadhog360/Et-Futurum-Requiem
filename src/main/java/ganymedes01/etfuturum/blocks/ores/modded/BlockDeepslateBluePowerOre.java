package ganymedes01.etfuturum.blocks.ores.modded;

import ganymedes01.etfuturum.blocks.ores.BaseSubtypesDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;
import net.minecraft.block.Block;

public class BlockDeepslateBluePowerOre extends BaseSubtypesDeepslateOre {
	public BlockDeepslateBluePowerOre() {
		super("deepslate_teslatite_ore", "deepslate_ruby_ore", "deepslate_sapphire_ore", "deepslate_amethyst_ore");
	}

	@Override
	public String getTextureSubfolder(String name) {
		return "bluepower";
	}

	@Override
	public Block getBase(int meta) {
		switch (meta) {
			case 1:
				return ExternalContent.Blocks.BP_RUBY_ORE.get();
			case 2:
				return ExternalContent.Blocks.BP_SAPPHIRE_ORE.get();
			case 3:
				return ExternalContent.Blocks.BP_AMETHYST_ORE.get();
			/*case 4: return ExternalContent.Blocks.BP_MALACHITE_ORE.get();*/
			default:
				return ExternalContent.Blocks.BP_TESLATITE_ORE.get();
		}
	}
}
