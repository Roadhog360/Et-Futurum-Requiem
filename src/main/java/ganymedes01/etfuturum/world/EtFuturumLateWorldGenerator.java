package ganymedes01.etfuturum.world;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class EtFuturumLateWorldGenerator extends EtFuturumWorldGenerator {

	public static final EtFuturumLateWorldGenerator INSTANCE = new EtFuturumLateWorldGenerator();

	protected EtFuturumLateWorldGenerator() {
		super();
	}

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		// Currently not used
	}
}
