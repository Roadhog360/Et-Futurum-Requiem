package ganymedes01.etfuturum.blocks.ores.modded;

import ganymedes01.etfuturum.blocks.ores.BaseSubtypesDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;
import net.minecraft.block.Block;

public class BlockDeepslateDragonQuestOre extends BaseSubtypesDeepslateOre {
	private static final String[] ores = new String[]{
			"deepslate_bakudanisi_ore", "deepslate_hikarinoisi_ore", "deepslate_hosinokakera_ore", "deepslate_inotinoisi_ore", "deepslate_kagaminoisi_ore", "deepslate_koorinokessyou_ore",
			"deepslate_littlemedal_ore", "deepslate_metaru_ore", "deepslate_migakizuna_ore", /*"deepslate_misuriru_ore",*/ "deepslate_moon_ore",/* "deepslate_puratina_ore", */"deepslate_rubi_ore",
			"deepslate_taiyounoisi_ore", "deepslate_tekkouseki_ore", "deepslate_tokinosuisyou_ore", "deepslate_yougansekinokakera_ore"};

	public BlockDeepslateDragonQuestOre() {
		super(ores);
	}

	@Override
	public String getTextureSubfolder(String name) {
		return "dragonquest";
	}

	@Override
	public Block getBase(int meta) {
		return switch (meta) {
			case 1 -> ExternalContent.Blocks.DQ_BRIGHTEN_ORE.get();
			case 2 -> ExternalContent.Blocks.DQ_LUCIDA_ORE.get();
			case 3 -> ExternalContent.Blocks.DQ_RESURROCK_ORE.get();
			case 4 -> ExternalContent.Blocks.DQ_MIRRORSTONE_ORE.get();
			case 5 -> ExternalContent.Blocks.DQ_ICE_CRYSTAL_ORE.get();
			case 6 -> ExternalContent.Blocks.DQ_MINIMEDAL_ORE.get();
			case 7 -> ExternalContent.Blocks.DQ_DENSINIUM_ORE.get();
			case 8 -> ExternalContent.Blocks.DQ_GLASS_FRIT_ORE.get();
			case 9 -> ExternalContent.Blocks.DQ_LUNAR_DIAMOND_ORE.get();
			case 10 -> ExternalContent.Blocks.DQ_CORUNDUM_ORE.get();
			case 11 -> ExternalContent.Blocks.DQ_SUNSTONE_ORE.get();
			case 12 -> ExternalContent.Blocks.DQ_ALLOYED_IRON_ORE.get();
			case 13 -> ExternalContent.Blocks.DQ_CHRONOCRYSTAL_ORE.get();
			case 14 -> ExternalContent.Blocks.DQ_VOLCANIC_ORE.get();
			default -> ExternalContent.Blocks.DQ_ROCKBOMB_ORE.get();
		};
	}
}
