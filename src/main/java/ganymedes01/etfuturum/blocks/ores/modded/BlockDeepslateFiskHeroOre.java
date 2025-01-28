package ganymedes01.etfuturum.blocks.ores.modded;

import ganymedes01.etfuturum.blocks.IEmissiveLayerBlock;
import ganymedes01.etfuturum.blocks.ores.BaseSubtypesDeepslateOre;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.compat.ExternalContent;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

public class BlockDeepslateFiskHeroOre extends BaseSubtypesDeepslateOre implements IEmissiveLayerBlock {

	public BlockDeepslateFiskHeroOre() {
		super("deepslate_tutridium_ore", "tutridium_deepslate", "deepslate_vibranium_ore", "deepslate_dwarf_star_ore", "deepslate_olivine_ore", "deepslate_eternium_ore", "eternium_deepslate");
	}

	@Override
	public String getTextureSubfolder(String name) {
		return "fiskheroes";
	}

	@Override
	public Block getBase(int meta) {
		return switch (meta) {
			case 1 -> ExternalContent.Blocks.FISK_TUTRIDIUM_SPECKLED_STONE.get();
			case 2 -> ExternalContent.Blocks.FISK_VIBRANIUM_ORE.get();
			case 3 -> ExternalContent.Blocks.FISK_DWARF_STAR_ORE.get();
			case 4 -> ExternalContent.Blocks.FISK_OLIVINE_ORE.get();
			case 5 -> ExternalContent.Blocks.FISK_ETERNIUM_ORE.get();
			case 6 -> ExternalContent.Blocks.FISK_ETERNIUM_INFUSED_STONE.get();
			default -> ExternalContent.Blocks.FISK_TUTRIDIUM_ORE.get();
		};
	}

	@Override
	public IIcon getSecondLayerIcon(int side, int meta) {
		return ExternalContent.Blocks.FISK_VIBRANIUM_ORE.get().getIcon(0, 0);
	}

	@Override
	public int getEmissiveMinBrightness(int meta) {
		return 15;
	}

	@Override
	public boolean isMetaNormalBlock(int meta) {
		return meta != 2;
	}

	@Override
	public boolean isSecondLayerAbove(int meta) {
		return true;
	}

	@Override
	public boolean itemBlockGlows(int meta) {
		return true;
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.EMISSIVE_DOUBLE_LAYER.getRenderId();
	}
}
