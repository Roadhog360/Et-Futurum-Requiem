package ganymedes01.etfuturum.world;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.DeepslateOreRegistry;
import ganymedes01.etfuturum.configuration.configs.ConfigTweaks;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.world.generate.WorldGenDeepslateLayerBlob;
import ganymedes01.etfuturum.world.generate.feature.WorldGenDeepslateLayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

public class EtFuturumEarlyWorldGenerator extends EtFuturumWorldGenerator {

	private WorldGenDeepslateLayer deepslateLayer;

	public static final EtFuturumEarlyWorldGenerator INSTANCE = new EtFuturumEarlyWorldGenerator();

	protected final WorldGenMinable deepslateBlobGen = new WorldGenDeepslateLayerBlob(ConfigWorld.maxDeepslatePerCluster, false);
	protected final WorldGenMinable tuffGen = new WorldGenDeepslateLayerBlob(ConfigWorld.maxTuffPerCluster, true);

	public EtFuturumEarlyWorldGenerator() {
		if (ModBlocks.DEEPSLATE.isEnabled()) {
			if (ConfigWorld.deepslateReplacesStones && ModBlocks.STONE.isEnabled()) {
				DeepslateOreRegistry.addOre(ModBlocks.STONE.get(), ModBlocks.DEEPSLATE.get());
			}
			if (ConfigWorld.deepslateReplacesDirt) {
				DeepslateOreRegistry.addOre(Blocks.dirt, ModBlocks.DEEPSLATE.get());
			}
			if (ConfigTweaks.deepslateReplacesCobblestone) {
				DeepslateOreRegistry.addOre(Blocks.cobblestone, ModBlocks.COBBLED_DEEPSLATE.get());
			}
			if(ConfigWorld.deepslateGenerationMode == 0) {
				deepslateLayer = new WorldGenDeepslateLayer(ModBlocks.DEEPSLATE.get(), 0);
			}
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		boolean flatWorld = EtFuturumWorldGenerator.isFlatWorld(chunkGenerator);
		if (!flatWorld || world.getWorldInfo().getGeneratorOptions().contains("decoration")) {
			if (world.provider instanceof WorldProviderSurface) {
				if (ModBlocks.DEEPSLATE.isEnabled() && ConfigWorld.deepslateGenerationMode == 1 && deepslateBlobGen != null) {
					generateOre(deepslateBlobGen, world, random, chunkX, chunkZ, 1, 6, ConfigWorld.deepslateMaxY);
				}
				if (ModBlocks.TUFF.isEnabled() && tuffGen != null) {
					generateOre(tuffGen, world, random, chunkX, chunkZ, 1, 6, ConfigWorld.deepslateMaxY);
				}
			}
			if (!flatWorld && deepslateLayer != null) {
				deepslateLayer.generate(world, random, chunkX << 4, 0, chunkZ << 4, DeepslateOreRegistry.getDeepslateHeight(world));
			}
		}
	}
}
