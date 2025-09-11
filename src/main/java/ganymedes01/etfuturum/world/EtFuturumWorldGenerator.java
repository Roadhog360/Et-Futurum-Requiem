package ganymedes01.etfuturum.world;

import cpw.mods.fml.common.IWorldGenerator;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.blocks.BlockChorusFlower;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.world.end.dimension.WorldProviderEFREnd;
import ganymedes01.etfuturum.world.generate.WorldGenMinableCustom;
import ganymedes01.etfuturum.world.generate.decorate.WorldGenBamboo;
import ganymedes01.etfuturum.world.generate.decorate.WorldGenCherryTrees;
import ganymedes01.etfuturum.world.generate.decorate.WorldGenPinkPetals;
import ganymedes01.etfuturum.world.generate.feature.WorldGenFossil;
import ganymedes01.etfuturum.world.generate.feature.WorldGenGeode;
import ganymedes01.etfuturum.world.structure.OceanMonument;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.ArrayUtils;
import roadhog360.hogutils.api.hogtags.helpers.BiomeTags;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class EtFuturumWorldGenerator implements IWorldGenerator {

	public static final EtFuturumWorldGenerator INSTANCE = new EtFuturumWorldGenerator();

	protected final List<WorldGenMinable> stoneGen = new LinkedList<>();

	protected final WorldGenMinable copperGen = new WorldGenMinable(ModBlocks.COPPER_ORE.get(), ConfigWorld.maxCopperPerCluster);

	protected final WorldGenMinable magmaGen = new WorldGenMinable(ModBlocks.MAGMA.get(), ConfigWorld.maxMagmaPerCluster, Blocks.netherrack);
	protected final WorldGenMinable netherGoldGen = new WorldGenMinable(ModBlocks.NETHER_GOLD_ORE.get(), ConfigWorld.maxNetherGoldPerCluster, Blocks.netherrack);
	protected final WorldGenMinable debrisGen = new WorldGenMinableCustom(ModBlocks.ANCIENT_DEBRIS.get(), ConfigWorld.debrisMax, Blocks.netherrack);
	protected final WorldGenMinable smallDebrisGen = new WorldGenMinableCustom(ModBlocks.ANCIENT_DEBRIS.get(), ConfigWorld.smallDebrisMax, Blocks.netherrack);
	protected final WorldGenMinable mesaGoldGen = new WorldGenMinable(Blocks.gold_ore, 8);

	protected WorldGenerator amethystGen;
	protected WorldGenerator fossilGen;
	protected WorldGenerator berryBushGen;
	protected WorldGenerator cornflowerGen;
	protected WorldGenerator lilyValleyGen;
	protected WorldGenerator pinkPetalsGen;
	protected WorldGenerator bambooGen;
	protected WorldGenerator mudGen;

	//trees
	protected WorldGenAbstractTree cherryTreeGen;

	protected EtFuturumWorldGenerator() {
		stoneGen.add(new WorldGenMinableCustom(ModBlocks.STONE.get(), 1, ConfigWorld.maxStonesPerCluster, Blocks.stone));
		stoneGen.add(new WorldGenMinableCustom(ModBlocks.STONE.get(), 3, ConfigWorld.maxStonesPerCluster, Blocks.stone));
		stoneGen.add(new WorldGenMinableCustom(ModBlocks.STONE.get(), 5, ConfigWorld.maxStonesPerCluster, Blocks.stone));
	}

	public void postInit() {
		if (ConfigWorld.enableAmethystGeodes && ModBlocks.AMETHYST_BLOCK.isEnabled() && ModBlocks.AMETHYST_CLUSTER_1.isEnabled() && ModBlocks.AMETHYST_CLUSTER_2.isEnabled()
				&& ModBlocks.BUDDING_AMETHYST.isEnabled() && ConfigWorld.amethystOuterBlock != null && ConfigWorld.amethystMiddleBlock != null) {
			amethystGen = new WorldGenGeode(ConfigWorld.amethystOuterBlock, ConfigWorld.amethystMiddleBlock);
		}

		if (ConfigWorld.enableFossils && ConfigWorld.fossilBlock != null) {
			//Add biomes that are only hot, AND dry AND sandy, and add all swamps.
			BiomeGenBase[] fossilBiomesArray = BiomeDictionary.getBiomesForType(Type.SANDY);
			for (BiomeGenBase biome : fossilBiomesArray) {
				if (!ArrayUtils.contains(BiomeDictionary.getBiomesForType(Type.HOT), biome) || !ArrayUtils.contains(BiomeDictionary.getBiomesForType(Type.DRY), biome)) {
					fossilBiomesArray = ArrayUtils.removeElement(fossilBiomesArray, biome);
					break;
				}
			}
			fossilBiomesArray = ArrayUtils.addAll(fossilBiomesArray, BiomeDictionary.getBiomesForType(Type.SWAMP));
			fossilBiomesArray = Utils.excludeBiomesFromTypes(fossilBiomesArray, Type.NETHER, Type.END);
			for(BiomeGenBase biome : fossilBiomesArray) {
				BiomeTags.addTags(biome, Tags.MOD_ID + ":has_feature/fossil");
			}
			fossilGen = new WorldGenFossil();
		}

		if (ModBlocks.SWEET_BERRY_BUSH.isEnabled()) {
			for(BiomeGenBase biome : Utils.excludeBiomesFromTypesWithDefaults(BiomeDictionary.getBiomesForType(Type.CONIFEROUS))) {
				BiomeTags.addTags(biome, Tags.MOD_ID + ":has_decorator/sweet_berry_bushes");
			}
			berryBushGen = new WorldGenFlowers(ModBlocks.SWEET_BERRY_BUSH.get());
			((WorldGenFlowers) berryBushGen).func_150550_a(ModBlocks.SWEET_BERRY_BUSH.get(), 3);
		}

		if (ModBlocks.LILY_OF_THE_VALLEY.isEnabled()) {
			BiomeGenBase[] lilyValleyBiomeArray = BiomeDictionary.getBiomesForType(Type.FOREST);
			lilyValleyBiomeArray = Utils.excludeBiomesFromTypes(lilyValleyBiomeArray, Type.JUNGLE, Type.DRY, Type.HOT, Type.SNOWY, Type.COLD);
			for(BiomeGenBase biome : lilyValleyBiomeArray) {
				BiomeTags.addTags(biome, Tags.MOD_ID + ":has_decorator/lilies_of_the_valley");
			}
			lilyValleyGen = new WorldGenFlowers(ModBlocks.LILY_OF_THE_VALLEY.get());
		}

		if (ModBlocks.CORNFLOWER.isEnabled()) {
			BiomeGenBase[] cornflowerBiomeArray = BiomeDictionary.getBiomesForType(Type.PLAINS);
			cornflowerBiomeArray = Utils.excludeBiomesFromTypes(cornflowerBiomeArray, Type.SAVANNA, Type.SNOWY, Type.SAVANNA);
			cornflowerBiomeArray = ArrayUtils.add(cornflowerBiomeArray, BiomeGenBase.getBiome(BiomeGenBase.forest.biomeID + 128));
			for(BiomeGenBase biome : cornflowerBiomeArray) {
				BiomeTags.addTags(biome, Tags.MOD_ID + ":has_decorator/cornflowers");
			}
			cornflowerGen = new WorldGenFlowers(ModBlocks.CORNFLOWER.get());
			for (BiomeGenBase biome : cornflowerBiomeArray) {
				// Affects bone meal only, not actual worldgen.
				biome.addFlower(ModBlocks.CORNFLOWER.get(), 0, 5);
			}
		}

		if (ModBlocks.BAMBOO.isEnabled() && ConfigWorld.bambooWorldgen) {
			bambooGen = new WorldGenBamboo(ModBlocks.BAMBOO.get());
			for(BiomeGenBase biome : Utils.excludeBiomesFromTypesWithDefaults(BiomeDictionary.getBiomesForType(Type.JUNGLE))) {
				String type = ArrayUtils.contains(BiomeDictionary.getTypesForBiome(biome), Type.SPARSE) ? "sparse_bamboo" : "dense_bamboo";
				BiomeTags.addTags(biome, Tags.MOD_ID + ":has_decorator/" + type);
			}
		}

		{
			BiomeGenBase[] cherryBiomeArray = BiomeDictionary.getBiomesForType(Type.MOUNTAIN);
			cherryBiomeArray = Utils.excludeBiomesFromTypesWithDefaults(cherryBiomeArray, Type.SNOWY, Type.HOT, Type.SANDY, Type.MESA, Type.SPARSE, Type.JUNGLE);
			if (ModBlocks.CHERRY_LOG.isEnabled() && ModBlocks.LEAVES.isEnabled()) {
				for(BiomeGenBase biome : cherryBiomeArray) {
					BiomeTags.addTags(biome, Tags.MOD_ID + ":has_decorator/cherry_tree");
				}
				cherryTreeGen = new WorldGenCherryTrees(false);
			}

			if (ModBlocks.PINK_PETALS.isEnabled()) {
				pinkPetalsGen = new WorldGenPinkPetals(ModBlocks.PINK_PETALS.get());
				for (BiomeGenBase biome : cherryBiomeArray) {
					// Affects bone meal only, not actual worldgen.
					biome.addFlower(ModBlocks.PINK_PETALS.get(), 0, 1);
					biome.addFlower(ModBlocks.PINK_PETALS.get(), 4, 1);
					biome.addFlower(ModBlocks.PINK_PETALS.get(), 8, 1);
					biome.addFlower(ModBlocks.PINK_PETALS.get(), 12, 1);
				}
			}
		}

		if (ModBlocks.MUD.isEnabled()) {
			mudGen = new WorldGenClay(4);
			((WorldGenClay) mudGen).field_150546_a/*block*/ = ModBlocks.MUD.get();
			for(BiomeGenBase biome : BiomeDictionary.getBiomesForType(Type.SWAMP)) {
				BiomeTags.addTags(biome, Tags.MOD_ID + ":has_decorator/mud_blob");
			}
		}
	}


	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (!isFlatWorld(chunkGenerator) || world.getWorldInfo().getGeneratorOptions().contains("decoration")) {
			int x;
			int z;

			if (amethystGen != null && ArrayUtils.contains(ConfigWorld.amethystDimensionBlacklist, world.provider.dimensionId) == ConfigWorld.amethystDimensionBlacklistAsWhitelist) {
				x = (chunkX << 4) + rand.nextInt(16) + 8;
				z = (chunkZ << 4) + rand.nextInt(16) + 8;
				if (ConfigWorld.amethystRarity == 1 || rand.nextInt(ConfigWorld.amethystRarity) == 0) {
					amethystGen.generate(world, rand, x, MathHelper.getRandomIntegerInRange(rand, 5, ConfigWorld.amethystMaxY - 5), z);
				}
			}

			if (ModBlocks.COPPER_ORE.isEnabled()) {
				generateOre(copperGen, world, rand, chunkX, chunkZ, 8, 4, 80);
			}

			if (ConfigWorld.enableExtraMesaGold) {
				if (ArrayUtils.contains(BiomeDictionary.getTypesForBiome(world.getBiomeGenForCoords(chunkX << 4, chunkZ << 4)), Type.MESA)) {
					generateOre(mesaGoldGen, world, rand, chunkX, chunkZ, 20, 32, 80);
				}
			}

			if (lilyValleyGen != null) {
				x = (chunkX << 4) + rand.nextInt(16) + 8;
				z = (chunkZ << 4) + rand.nextInt(16) + 8;
				if (world.getHeightValue(x, z) > 0 && BiomeTags.hasTag(world.getBiomeGenForCoords(x, z), Tags.MOD_ID + ":has_decorator/lilies_of_the_valley")) {
					lilyValleyGen.generate(world, rand, x, nextHeightInt(rand, world.getHeightValue(x, z) * 2), z);
				}
			}

			if (cornflowerGen != null) {
				x = (chunkX << 4) + rand.nextInt(16) + 8;
				z = (chunkZ << 4) + rand.nextInt(16) + 8;
				if (world.getHeightValue(x, z) > 0 && BiomeTags.hasTag((world.getBiomeGenForCoords(x, z)), Tags.MOD_ID + ":has_decorator/cornflowers")) {
					cornflowerGen.generate(world, rand, x, nextHeightInt(rand, world.getHeightValue(x, z) * 2), z);
				}
			}

			if (berryBushGen != null) {
				x = (chunkX << 4) + rand.nextInt(16) + 8;
				z = (chunkZ << 4) + rand.nextInt(16) + 8;
				if (world.getHeightValue(x, z) > 0 && BiomeTags.hasTag((world.getBiomeGenForCoords(x, z)), Tags.MOD_ID + ":has_decorator/sweet_berry_bushes")) {
					berryBushGen.generate(world, rand, x, nextHeightInt(rand, world.getHeightValue(x, z) * 2), z);
				}
			}

			if (bambooGen != null) {
				x = (chunkX << 4) + rand.nextInt(16) + 8;
				z = (chunkZ << 4) + rand.nextInt(16) + 8;
				int y = world.getHeightValue(x, z);
				// null = no bamboo, neither tag present
				BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
				Boolean dense = BiomeTags.hasTag(biome, Tags.MOD_ID + ":has_decorator/dense_bamboo")
						? BiomeTags.hasTag(biome, Tags.MOD_ID + ":has_decorator/sparse_bamboo") : null;
				if (y > 0 && dense != null) {
					int count = dense && rand.nextInt(16) == 0 ? 256 : 16;
					for (int i = 0; i < count; i++) {
						int xoff = x + rand.nextInt(10) - rand.nextInt(10);
						int yoff = y + rand.nextInt(4) - rand.nextInt(4);
						int zoff = z + rand.nextInt(10) - rand.nextInt(10);
						bambooGen.generate(world, rand, xoff, yoff, zoff);
					}
				}
			}

			if (cherryTreeGen != null && ConfigWorld.cherryTreeRarity > 0) {
				x = (chunkX << 4) + rand.nextInt(16) + 8;
				z = (chunkZ << 4) + rand.nextInt(16) + 8;
				if(BiomeTags.hasTag(world.getBiomeGenForCoords(x, z), Tags.MOD_ID + ":has_decorator/cherry_tree")) {
					int y = world.getHeightValue(x, z);
					Block block = world.getBlock(x, y - 1, z);
					if (y > 0 && block.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, (IPlantable) ModBlocks.SAPLING.get())) {
						if (rand.nextInt(ConfigWorld.cherryTreeRarity) == 0) {
							if (cherryTreeGen.generate(world, rand, x, y, z)) {
								cherryTreeGen.func_150524_b(world, rand, x, y, z);
								if (pinkPetalsGen != null) {
									pinkPetalsGen.generate(world, rand, x, y, z);
								}
							}
						}
					}
				}
			}

			if (mudGen != null) {
				x = (chunkX << 4) + rand.nextInt(16) + 8;
				z = (chunkZ << 4) + rand.nextInt(16) + 8;
				int y = world.getHeightValue(x, z);
				if (y > 0 && BiomeTags.hasTag((world.getBiomeGenForCoords(x, z)), Tags.MOD_ID + ":has_decorator/mud_blob")) {
					mudGen.generate(world, rand, x, world.getTopSolidOrLiquidBlock(x, z), z);
				}
			}

			if (fossilGen != null && rand.nextInt(64) == 0 && ArrayUtils.contains(ConfigWorld.fossilDimensionBlacklist, world.provider.dimensionId) == ConfigWorld.fossilDimensionBlacklistAsWhitelist) {
				x = (chunkX << 4) + rand.nextInt(16) + 8;
				z = (chunkZ << 4) + rand.nextInt(16) + 8;
				if (BiomeTags.hasTag((world.getBiomeGenForCoords(x, z)), Tags.MOD_ID + ":has_feature/fossil")) {
					fossilGen.generate(world, rand, x, MathHelper.getRandomIntegerInRange(rand, 40, 49), z);
				}
			}

			if (ConfigWorld.enableOceanMonuments && ModBlocks.PRISMARINE_BLOCK.isEnabled() && ModBlocks.SEA_LANTERN.isEnabled()
					&& !(world.provider instanceof WorldProviderEnd) && !(world.provider instanceof WorldProviderHell)) {
				if (OceanMonument.canSpawnAt(world, chunkX, chunkZ)) {
					x = (chunkX << 4) + rand.nextInt(16) + 8;
					z = (chunkZ << 4) + rand.nextInt(16) + 8;
					int y;
					for (y = world.getActualHeight(); y > 0; y--)
						if (!world.isAirBlock(x, y, z))
							break;
					int monumentCeiling = y - (1 + rand.nextInt(3));
					OceanMonument.buildTemple(world, x, monumentCeiling - 22, z);
					return;
				}
			}
		}

		if (world.provider instanceof WorldProviderHell) {
			if (ModBlocks.MAGMA.isEnabled()) {
				this.generateOre(magmaGen, world, rand, chunkX, chunkZ, 4, 23, 37);
			}

//          if(ConfigurationHandler.enableBlackstone)
//              this.generateOre(ModBlocks.blackstone, 0, world, rand, chunkX, chunkZ, 1, ConfigurationHandler.maxBlackstonePerCluster, 2, 5, 28, Blocks.netherrack);

			if (ModBlocks.NETHER_GOLD_ORE.isEnabled()) {
				this.generateOre(netherGoldGen, world, rand, chunkX, chunkZ, 10, 10, 117);
			}

			if (ModBlocks.ANCIENT_DEBRIS.isEnabled()) {
				this.generateOre(debrisGen, world, rand, chunkX, chunkZ, 1, 8, 22);
				this.generateOre(smallDebrisGen, world, rand, chunkX, chunkZ, 1, 8, 119);
			}
		}

		if (ModBlocks.CHORUS_PLANT.isEnabled() && ModBlocks.CHORUS_FLOWER.isEnabled() && !(world.provider instanceof WorldProviderEFREnd) && world.provider instanceof WorldProviderEnd) {
			int x = (chunkX << 4) + rand.nextInt(16) + 8;
			int y = world.getActualHeight();
			int z = (chunkZ << 4) + rand.nextInt(16) + 8;
			for (; y > 0; y--) {
				if (!world.getBlock(x, y, z).isAir(world, x, y, z)) {
					if (BlockChorusFlower.canPlantStay(world, x, y + 1, z)) {
						BlockChorusFlower.generatePlant(world, x, y + 1, z, rand, 8);
						break;
					}
				}
			}
		}
	}

	public void generateSingleOre(Block block, int meta, World world, Random random, int chunkX, int chunkZ, float chance, int minY, int maxY, Block generateIn) {
		if (maxY <= 0 || minY < 0 || maxY < minY || chance <= 0)
			return;

		for (int i = 0; i < (chance < 1 ? 1 : chance); i++) {
			if (chance > 1 || random.nextFloat() < chance) {
				int xRand = (chunkX << 4) + random.nextInt(16);
				int yRand = MathHelper.getRandomIntegerInRange(random, minY, maxY);
				int zRand = (chunkZ << 4) + random.nextInt(16);
				if (world.getBlock(xRand, yRand, zRand).isReplaceableOreGen(world, xRand, yRand, zRand, generateIn))
					world.setBlock(xRand, yRand, zRand, block, meta, 3);
			}
		}
	}

	public void generateOre(WorldGenMinable gen, World world, Random random, int chunkX, int chunkZ, float chance, int minY, int maxY) {
		if (maxY <= 0 || minY < 0 || maxY < minY || gen.numberOfBlocks <= 0 || chance <= 0)
			return;

		for (int i = 0; i < (chance < 1 ? 1 : (int) chance); i++) {
			if (chance >= 1 || random.nextFloat() < chance) {
				int xRand = (chunkX << 4) + random.nextInt(16);
				int yRand = MathHelper.getRandomIntegerInRange(random, minY, maxY);
				int zRand = (chunkZ << 4) + random.nextInt(16);

				gen.generate(world, random, xRand, yRand, zRand);
			}
		}
	}

	protected int nextHeightInt(Random rand, int i) {
		if (i <= 1)
			return 1;
		return rand.nextInt(i);
	}

	public static boolean isFlatWorld(IChunkProvider chunkProvider) {
		return chunkProvider instanceof ChunkProviderFlat && !chunkProvider.getClass().getName().equals("com.rwtema.extrautils.worldgen.Underdark.ChunkProviderUnderdark");
	}
}