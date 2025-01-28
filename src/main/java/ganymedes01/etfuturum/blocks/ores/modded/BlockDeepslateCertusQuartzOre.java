package ganymedes01.etfuturum.blocks.ores.modded;

import ganymedes01.etfuturum.blocks.IEmissiveLayerBlock;
import ganymedes01.etfuturum.blocks.ores.BaseSubtypesDeepslateOre;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.compat.ExternalContent;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

public class BlockDeepslateCertusQuartzOre extends BaseSubtypesDeepslateOre implements IEmissiveLayerBlock {

	public BlockDeepslateCertusQuartzOre() {
		super("deepslate_certus_quartz_ore", "deepslate_charged_certus_quartz_ore");
	}

	@Override
	public String getTextureSubfolder(String name) {
		return "ae2";
	}

	@Override
	public Block getBase(int meta) {
		if (meta == 1) {
			return ExternalContent.Blocks.AE2_CHARGED_CERTUS_QUARTZ_ORE.get();
		}
		return ExternalContent.Blocks.AE2_CERTUS_QUARTZ_ORE.get();
	}

	@Override
	public IIcon getSecondLayerIcon(int side, int meta) {
		return getBase(meta).getIcon(side, getBaseMeta(meta));
	}

	//Not sure how to make this work rn
	@Override
	public int getEmissiveMinBrightness(int meta) {
		return 1;
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.EMISSIVE_DOUBLE_LAYER.getRenderId();
	}
}
