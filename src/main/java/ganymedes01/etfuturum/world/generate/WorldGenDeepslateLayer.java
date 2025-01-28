package ganymedes01.etfuturum.world.generate;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.world.EtFuturumWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

public class WorldGenDeepslateLayer extends WorldGenerator {
	@Override
	public boolean generate(World p_76484_1_, Random p_76484_2_, int p_76484_3_, int p_76484_4_, int p_76484_5_) {
		return generate(p_76484_1_, p_76484_3_ >> 4, p_76484_5_ >> 4);
	}

	public boolean generate(World world, int chunkX, int chunkZ) {
		doDeepslateGen(world.getChunkFromChunkCoords(chunkX, chunkZ));
		return true;
	}

	public boolean doesChunkSupportLayerDeepslate(IChunkProvider provider, int dimId) {
		if (ModBlocks.DEEPSLATE.isEnabled()) {
			if (ConfigWorld.deepslateGenerationMode == 0 && ConfigWorld.deepslateMaxY > 0) {
				return !EtFuturumWorldGenerator.isFlatWorld(provider) && ArrayUtils.contains(ConfigWorld.deepslateLayerDimensionBlacklist, dimId) == ConfigWorld.deepslateLayerDimensionBlacklistAsWhitelist;
			}
		}
		return false;
	}

	private void doDeepslateGen(Chunk chunk) {
		boolean shouldDimBeJustDeepslate = ArrayUtils.contains(ConfigWorld.replaceAllStoneWithDeepslateDimensionWhitelist, chunk.worldObj.provider.dimensionId);
		int replaceUnderY = (shouldDimBeJustDeepslate ? chunk.worldObj.getActualHeight() : ConfigWorld.deepslateMaxY);
		for (int y = 0; y <= Math.min(replaceUnderY, chunk.worldObj.getHeight()); y++) {
			ExtendedBlockStorage array = chunk.getBlockStorageArray()[y >> 4];
			if (array == null) continue;

			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					if(y >= chunk.getHeightValue(x, z)) continue;

					if (ConfigWorld.deepslateMaxY >= 255 || shouldDimBeJustDeepslate || y < ConfigWorld.deepslateMaxY - 4 || y <= ConfigWorld.deepslateMaxY - chunk.worldObj.rand.nextInt(4)) {
						EtFuturumWorldGenerator.changeToDeepslate(chunk, x, y, z);
					}
				}
			}
		}
	}
}
