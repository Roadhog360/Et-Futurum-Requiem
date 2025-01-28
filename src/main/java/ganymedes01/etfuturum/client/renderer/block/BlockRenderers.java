package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizon.gtnhlib.client.model.isbrh.RenderJSON4Rot;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class BlockRenderers {
	public static final ISimpleBlockRenderingHandler SLIME_BLOCK = new RenderDoubleCube(13);
	public static final ISimpleBlockRenderingHandler HONEY_BLOCK = new RenderDoubleCube(15);
	public static final ISimpleBlockRenderingHandler END_ROD = new RenderEndRod();
//	public static final ISimpleBlockRenderingHandler LIGHTNING_ROD =
//			new RenderJSONBasic("models/item/lightning_rod", "models/block/lightning_rod");
	public static final ISimpleBlockRenderingHandler LIGHTNING_ROD = new RenderEndRod();//Just a random class I picked as a placeholder
//	public static final ISimpleBlockRenderingHandler CHORUS_FLOWER = new RenderChorusFlower();
	public static final ISimpleBlockRenderingHandler CHORUS_FLOWER = new RenderChorusFlowerNewTest();
	public static final ISimpleBlockRenderingHandler CHORUS_PLANT = new RenderChorusPlant();
	public static final ISimpleBlockRenderingHandler LANTERN = new RenderLantern();
	public static final ISimpleBlockRenderingHandler BARREL = new RenderBarrel();
	public static final ISimpleBlockRenderingHandler GLAZED_TERRACOTTA = new RenderGlazedTerracotta();
	public static final ISimpleBlockRenderingHandler LAVA_CAULDRON = new RenderCauldronLava();
	public static final ISimpleBlockRenderingHandler STONECUTTER = new RenderStonecutter();
	public static final ISimpleBlockRenderingHandler COMPOSTER = new RenderComposter();
	public static final ISimpleBlockRenderingHandler LOOM = new RenderLoom();
	public static final ISimpleBlockRenderingHandler AMETHYST_CLUSTER = new RenderAmethystCluster();
	public static final ISimpleBlockRenderingHandler POINTED_DRIPSTONE = new RenderPointedDripstone();
	public static final ISimpleBlockRenderingHandler OBSERVER = new RenderObserver();
	public static final ISimpleBlockRenderingHandler CHAIN = new RenderChain();
	public static final ISimpleBlockRenderingHandler AZALEA = new RenderAzalea();
	public static final ISimpleBlockRenderingHandler MANGROVE_ROOTS = new RenderMangroveRoots();
	public static final ISimpleBlockRenderingHandler BAMBOO = new RenderBamboo();
	public static final ISimpleBlockRenderingHandler BUBBLE_COLUMN = new RenderBubbleColumn();
	public static final ISimpleBlockRenderingHandler FLOWER_BED = new RenderFlowerBed();
	public static final ISimpleBlockRenderingHandler LECTERN = new RenderJSON4Rot("models/item/lectern", "models/block/lectern");

	public static final ISimpleBlockRenderingHandler COLOR_CAULDRON = new RenderCauldronGenericColor();
	public static final ISimpleBlockRenderingHandler EMISSIVE_DOUBLE_LAYER = new RenderEmissiveDoubleLayer();

	public static void registerRenderers() {
		RenderingRegistry.registerBlockHandler(SLIME_BLOCK);
		RenderingRegistry.registerBlockHandler(HONEY_BLOCK);
		RenderingRegistry.registerBlockHandler(END_ROD);
		RenderingRegistry.registerBlockHandler(LIGHTNING_ROD);
		RenderingRegistry.registerBlockHandler(CHORUS_FLOWER);
		RenderingRegistry.registerBlockHandler(CHORUS_PLANT);
		RenderingRegistry.registerBlockHandler(LANTERN);
		RenderingRegistry.registerBlockHandler(BARREL);
		RenderingRegistry.registerBlockHandler(GLAZED_TERRACOTTA);
		RenderingRegistry.registerBlockHandler(LAVA_CAULDRON);
		RenderingRegistry.registerBlockHandler(STONECUTTER);
		RenderingRegistry.registerBlockHandler(COMPOSTER);
		RenderingRegistry.registerBlockHandler(LOOM);
		RenderingRegistry.registerBlockHandler(AMETHYST_CLUSTER);
		RenderingRegistry.registerBlockHandler(POINTED_DRIPSTONE);
		RenderingRegistry.registerBlockHandler(OBSERVER);
		RenderingRegistry.registerBlockHandler(CHAIN);
		RenderingRegistry.registerBlockHandler(AZALEA);
		RenderingRegistry.registerBlockHandler(MANGROVE_ROOTS);
		RenderingRegistry.registerBlockHandler(BAMBOO);
		RenderingRegistry.registerBlockHandler(BUBBLE_COLUMN);
		RenderingRegistry.registerBlockHandler(FLOWER_BED);
		RenderingRegistry.registerBlockHandler(LECTERN);

		RenderingRegistry.registerBlockHandler(EMISSIVE_DOUBLE_LAYER);
		RenderingRegistry.registerBlockHandler(COLOR_CAULDRON);

		RenderingRegistry.registerBlockHandler(new RenderBlankOverlay());
	}
}
