package ganymedes01.etfuturum.compat;

import com.falsepattern.rple.api.common.colorizer.RPLEBlockColorRegistry;
import com.falsepattern.rple.api.common.event.BlockColorRegistrationEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.recipes.ModRecipes;
import net.minecraft.block.Block;

import static com.falsepattern.rple.api.common.color.DefaultColor.*;

public class CompatRPLEEventHandler {

	private static final CompatRPLEEventHandler instance = new CompatRPLEEventHandler();

	public static void registerRPLECompat() {
		FMLCommonHandler.instance().bus().register(instance);
	}

	@SubscribeEvent
	public void kolorsLol(BlockColorRegistrationEvent event) {
		RPLEBlockColorRegistry registry = event.registry();

		colorize(registry, ModBlocks.SEA_LANTERN.get(), 0xAFF);
		colorize(registry, ModBlocks.END_ROD.get(), 0xFAF);
		colorize(registry, ModBlocks.MAGMA.get(), 0x510);
		colorize(registry, ModBlocks.BEACON.get(), 0xCCC);
		colorize(registry, ModBlocks.DEEPSLATE_LIT_REDSTONE_ORE.get(), 0x511);
		colorize(registry, ModBlocks.LANTERN.get(), 0xFC5);
		colorize(registry, ModBlocks.SOUL_LANTERN.get(), 0x49A);
		colorize(registry, ModBlocks.SOUL_TORCH.get(), 0x49A);
		colorize(registry, ModBlocks.SHROOMLIGHT.get(), 0xFC8);
		colorize(registry, ModBlocks.LIT_BLAST_FURNACE.get(), 0xDCA);
		colorize(registry, ModBlocks.LIT_SMOKER.get(), 0xDCA);

		registerAmethystColor(registry, 0, 0x101);
		registerAmethystColor(registry, 1, 0x212);
		registerAmethystColor(registry, 2, 0x324);
		registerAmethystColor(registry, 3, 0x435);

		registerBulbColor(registry, 0, 0xFC8);
		registerBulbColor(registry, 1, 0xCA7);
		registerBulbColor(registry, 2, 0x863);
		registerBulbColor(registry, 3, 0x443);

		transparency(registry, ModBlocks.SLIME.get(), LIME.rgb16());
		transparency(registry, ModBlocks.HONEY_BLOCK.get(), 0xFB7);
	}

	private void registerAmethystColor(RPLEBlockColorRegistry registry, int clusterSize, int color) {
		Block cluster = clusterSize < 2 ? ModBlocks.AMETHYST_CLUSTER_1.get() : ModBlocks.AMETHYST_CLUSTER_2.get();
		boolean secondHalf = clusterSize % 2 == 0;
		for(int i = secondHalf ? 0 : 6; i < (secondHalf ? 6 : 12); i++) {
			colorize(registry, cluster, i, color);
		}
	}

	private void registerBulbColor(RPLEBlockColorRegistry registry, int level, int color) {
		colorize(registry, ModBlocks.COPPER_BULB.get(), level + 4, color);
		colorize(registry, ModBlocks.COPPER_BULB.get(), level + 12, color);
		colorize(registry, ModBlocks.POWERED_COPPER_BULB.get(), level + 4, color);
		colorize(registry, ModBlocks.POWERED_COPPER_BULB.get(), level + 12, color);
	}
	
	private void colorize(RPLEBlockColorRegistry registry, Block block, int rgb16) {
		if(ModRecipes.validateItems(block)) {
			registry.colorizeBlock(block).brightness(rgb16).apply();
		}
	}

	private void colorize(RPLEBlockColorRegistry registry, Block block, int meta, int rgb16) {
		if(ModRecipes.validateItems(block)) {
			registry.colorizeBlock(block, meta).brightness(rgb16).apply();
		}
	}

	private void transparency(RPLEBlockColorRegistry registry, Block block, int rgb16) {
		if(ModRecipes.validateItems(block)) {
			registry.colorizeBlock(block).translucency(rgb16).apply();
		}
	}

	private void transparency(RPLEBlockColorRegistry registry, Block block, int meta, int rgb16) {
		if(ModRecipes.validateItems(block)) {
			registry.colorizeBlock(block, meta).translucency(rgb16).apply();
		}
	}
}
