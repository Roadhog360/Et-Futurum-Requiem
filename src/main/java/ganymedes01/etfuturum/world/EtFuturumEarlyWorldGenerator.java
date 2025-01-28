package ganymedes01.etfuturum.world;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.api.DeepslateOreRegistry;
import ganymedes01.etfuturum.configuration.configs.ConfigTweaks;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.world.generate.WorldGenDeepslateLayer;
import ganymedes01.etfuturum.world.generate.WorldGenDeepslateLayerBlob;
import ganymedes01.etfuturum.world.generate.WorldGenMinableCustom;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.feature.WorldGenMinable;
import roadhog360.hogutils.api.RegistryMapping;
import roadhog360.hogutils.api.hogtags.HogTagsHelper;
import roadhog360.hogutils.api.world.SetBlockSniper;

import javax.annotation.Nullable;
import java.util.Random;

public class EtFuturumEarlyWorldGenerator extends EtFuturumWorldGenerator {

	public static final EtFuturumEarlyWorldGenerator INSTANCE = new EtFuturumEarlyWorldGenerator();

	protected final WorldGenDeepslateLayer deepslateGenLayer = new WorldGenDeepslateLayer();
	protected final WorldGenMinable deepslateGen = new WorldGenDeepslateLayerBlob(ConfigWorld.maxDeepslatePerCluster, false);
	protected final WorldGenMinable tuffGen = new WorldGenDeepslateLayerBlob(ConfigWorld.maxTuffPerCluster, true);

	protected final WorldGenMinable dioriteGen = new WorldGenMinableCustom(ModBlocks.STONE.get(), 1, ConfigWorld.maxStonesPerCluster, Blocks.stone);
	protected final WorldGenMinable graniteGen = new WorldGenMinableCustom(ModBlocks.STONE.get(), 3, ConfigWorld.maxStonesPerCluster, Blocks.stone);
	protected final WorldGenMinable andesiteGen = new WorldGenMinableCustom(ModBlocks.STONE.get(), 5, ConfigWorld.maxStonesPerCluster, Blocks.stone);

	protected EtFuturumEarlyWorldGenerator() {
		super();
		if (ModBlocks.DEEPSLATE.isEnabled()) {
			if (ConfigWorld.deepslateReplacesDirt) {
				DeepslateOreRegistry.addOre(Blocks.dirt, ModBlocks.DEEPSLATE.get());
			}
			if (ConfigTweaks.deepslateReplacesCobblestone) {
				DeepslateOreRegistry.addOre(Blocks.cobblestone, ModBlocks.COBBLED_DEEPSLATE.get());
			}
		}

		SetBlockSniper.registerSniper(new SetBlockSniper.Sniper() {
			private final ThreadLocal<Integer> lastMeta = ThreadLocal.withInitial(() -> 0);

			@Nullable
			@Override
			public Block replaceBlock(Chunk chunk, int x, int y, int z, Block block, int meta) {
				if(chunk.inhabitedTime <= 0) { //Is this really needed? The goal is to make this so it only runs in relatively "fresh" chunks.
					ExtendedBlockStorage array = chunk.getBlockStorageArray()[y >> 4];
					if (array != null) {
						Block prevBlock = array.getBlockByExtId(x, y & 15, z);
						int prevMeta = array.getExtBlockMetadata(x, y & 15, z);
						if (HogTagsHelper.BlockTags.hasAnyTag(prevBlock, prevMeta, Tags.MOD_ID + ":deepslate_ore_base")) {
							RegistryMapping<Block> mapping = DeepslateOreRegistry.getOre(block, meta);
							if (mapping != null) {
								lastMeta.set(mapping.getMeta());
								return mapping.getObject();
							}
						}
					}
				}
				return null;
			}

			@Override
			public int replaceMeta(Chunk chunk, int i, int i1, int i2, Block block, int i3) {
				int meta = lastMeta.get();
				lastMeta.remove();
				return meta;
			}
		});
	}

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (supportsStandardWorldgen(chunkGenerator, world)) {
			if (ModBlocks.DEEPSLATE.isEnabled()) {
				if(deepslateGenLayer.doesChunkSupportLayerDeepslate(chunkGenerator, world.provider.dimensionId)) {
					deepslateGenLayer.generate(world, chunkX, chunkZ);
				} else {
					generateOre(deepslateGen, world, rand, chunkX, chunkZ, 1, 6, ConfigWorld.deepslateMaxY);
				}
			}
			if (ModBlocks.TUFF.isEnabled()) {
				generateOre(tuffGen, world, rand, chunkX, chunkZ, 1, 6, ConfigWorld.deepslateMaxY);
			}
			if (ModBlocks.STONE.isEnabled() && ConfigWorld.maxStonesPerCluster > 0) {
				generateOre(dioriteGen, world, rand, chunkX, chunkZ, 5, 0, 80);
				generateOre(graniteGen, world, rand, chunkX, chunkZ, 5, 0, 80);
				generateOre(andesiteGen, world, rand, chunkX, chunkZ, 5, 0, 80);
			}
		}
	}
}
