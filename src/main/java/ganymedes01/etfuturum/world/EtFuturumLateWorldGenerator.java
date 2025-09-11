package ganymedes01.etfuturum.world;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

public class EtFuturumLateWorldGenerator extends EtFuturumWorldGenerator {

	public static final EtFuturumLateWorldGenerator INSTANCE = new EtFuturumLateWorldGenerator();

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		if (!isFlatWorld(chunkGenerator) || world.getWorldInfo().getGeneratorOptions().contains("decoration")) {
			if (ModBlocks.STONE.isEnabled() && ConfigWorld.maxStonesPerCluster > 0) {
				for (WorldGenMinable stoneGenerator : stoneGen) {
					for (int i = 0; i < 10; i++) {
						generateOre(stoneGenerator, world, rand, chunkX, chunkZ, 1, 0, 80);
					}
				}
			}
		}
	}

}
