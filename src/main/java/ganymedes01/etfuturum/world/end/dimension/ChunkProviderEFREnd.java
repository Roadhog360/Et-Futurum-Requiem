package ganymedes01.etfuturum.world.end.dimension;

import cpw.mods.fml.common.eventhandler.Event.Result;
import ganymedes01.etfuturum.blocks.BlockChorusFlower;
import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import ganymedes01.etfuturum.world.end.gen.MapGenEndCity;
import ganymedes01.etfuturum.world.end.gen.WorldGenEndIsland;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import java.util.List;
import java.util.Random;

public class ChunkProviderEFREnd implements IChunkProvider {
	/**
	 * RNG.
	 */
	private final Random rand;
	protected static final Block END_STONE = Blocks.end_stone;
	protected static final Block AIR = Blocks.air;
	private final NoiseGeneratorOctaves lperlinNoise1;
	private final NoiseGeneratorOctaves lperlinNoise2;
	private final NoiseGeneratorOctaves perlinNoise1;

	/**
	 * A NoiseGeneratorOctaves used in generating terrain
	 */
	public NoiseGeneratorOctaves noiseGen5;

	/**
	 * A NoiseGeneratorOctaves used in generating terrain
	 */
	public NoiseGeneratorOctaves noiseGen6;

	/**
	 * Reference to the World object.
	 */
	private final World worldObj;

	/**
	 * are map structures going to be generated (e.g. strongholds)
	 */
	private final boolean mapFeaturesEnabled;
	    private final MapGenEndCity endCityGen;
	private final NoiseGeneratorSimplex islandNoise;
	private double[] buffer;

	/**
	 * The biomes that are used to generate the chunk
	 */
	private BiomeGenBase[] biomesForGeneration;
	double[] pnr;
	double[] ar;
	double[] br;
	private final WorldGenEndIsland endIslands = new WorldGenEndIsland();

	public ChunkProviderEFREnd(World worldObjIn, long seed) {
		this.worldObj = worldObjIn;
		this.mapFeaturesEnabled = true; //come back to
		this.rand = new Random(seed);
		this.lperlinNoise1 = new NoiseGeneratorOctaves(this.rand, 16);
		this.lperlinNoise2 = new NoiseGeneratorOctaves(this.rand, 16);
		this.perlinNoise1 = new NoiseGeneratorOctaves(this.rand, 8);
		this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
		this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
		this.islandNoise = new NoiseGeneratorSimplex(this.rand);
		this.endCityGen = (ConfigExperiments.endDimensionProvider && ConfigExperiments.enableEndCities) ? new MapGenEndCity(this) : null;
	}

	public Chunk provideChunk(int x, int z) {
		Block[] ablock = new Block[32768];
		byte[] meta = new byte[ablock.length];
		this.rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
		this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, x * 16, z * 16, 16, 16);
		this.setBlocksInChunk(x, z, ablock);
		this.buildSurfaces(x, z, ablock, biomesForGeneration, meta);

		if (this.mapFeaturesEnabled && this.endCityGen != null) {
			this.endCityGen.func_151539_a(this, this.worldObj, x, z, ablock);
		}

		Chunk chunk = new Chunk(this.worldObj, ablock, meta, x, z);
		byte[] abyte = chunk.getBiomeArray();

		for (int i = 0; i < abyte.length; ++i) {
			abyte[i] = (byte) this.biomesForGeneration[i].biomeID;
		}

		chunk.generateSkylightMap();
		return chunk;
	}

	/**
	 * Generates a bare-bones chunk of nothing but stone or ocean blocks, formed, but featureless.
	 */
	public void setBlocksInChunk(int x, int z, Block[] blocks) {
		int i = 2;
		int j = i + 1;
		int k = 33;
		int l = i + 1;
		this.buffer = this.getHeights(this.buffer, x * i, 0, z * i, j, k, l);

		for (int i1 = 0; i1 < i; ++i1) {
			for (int j1 = 0; j1 < i; ++j1) {
				for (int k1 = 0; k1 < 32; ++k1) {
					double d0 = 0.25D;
					double d1 = this.buffer[((i1) * l + j1) * k + k1];
					double d2 = this.buffer[((i1) * l + j1 + 1) * k + k1];
					double d3 = this.buffer[((i1 + 1) * l + j1) * k + k1];
					double d4 = this.buffer[((i1 + 1) * l + j1 + 1) * k + k1];
					double d5 = (this.buffer[((i1) * l + j1) * k + k1 + 1] - d1) * d0;
					double d6 = (this.buffer[((i1) * l + j1 + 1) * k + k1 + 1] - d2) * d0;
					double d7 = (this.buffer[((i1 + 1) * l + j1) * k + k1 + 1] - d3) * d0;
					double d8 = (this.buffer[((i1 + 1) * l + j1 + 1) * k + k1 + 1] - d4) * d0;

					for (int l1 = 0; l1 < 4; ++l1) {
						double d9 = 0.125D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;

						for (int i2 = 0; i2 < 8; ++i2) {
							int j3 = i2 + i1 * 8 << 11 | j1 * 8 << 7 | k1 * 4 + l1;
							double d14 = 0.125D;
							double d15 = d10;
							double d16 = (d11 - d10) * d14;

							for (int j2 = 0; j2 < 8; ++j2) {
								Block iblockstate = AIR;

								if (d15 > 0.0D) {
									iblockstate = END_STONE;
								}

								blocks[j3] = iblockstate;
								j3 += 128;
								d15 += d16;
							}

							d10 += d12;
							d11 += d13;
						}

						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
			}
		}
	}

	public void buildSurfaces(int x, int z, Block[] blocks, BiomeGenBase[] biomes, byte[] meta) {
		ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, x, z, blocks, meta, biomes, this.worldObj);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getResult() == Result.DENY) return;

		for (int i = 0; i < 16; ++i) {
			for (int j = 0; j < 16; ++j) {
				int k = 1;
				int l = -1;
				Block iblockstate = END_STONE;
				Block iblockstate1 = END_STONE;

				for (int i1 = 127; i1 >= 0; --i1) {
					int k1 = (j * 16 + i) * 128 + i1;
					Block iblockstate2 = blocks[k1];

					if (iblockstate2 == null || iblockstate2.getMaterial() == Material.air) {
						l = -1;
					} else if (iblockstate2 == Blocks.stone) {
						if (l == -1) {
							if (k <= 0) {
								iblockstate = AIR;
								iblockstate1 = END_STONE;
							}

							l = k;

							if (i1 >= 0) {
								blocks[k1] = iblockstate;
							} else {
								blocks[k1] = iblockstate1;
							}
						} else if (l > 0) {
							--l;
							blocks[k1] = iblockstate1;
						}
					}
				}
			}
		}
	}

	private float getIslandHeightValue(int p_185960_1_, int p_185960_2_, int p_185960_3_, int p_185960_4_) {
		float f = (float) (p_185960_1_ * 2 + p_185960_3_);
		float f1 = (float) (p_185960_2_ * 2 + p_185960_4_);
		float f2 = 100.0F - MathHelper.sqrt_float(f * f + f1 * f1) * 8.0F;

		if (f2 > 80.0F) {
			f2 = 80.0F;
		}

		if (f2 < -100.0F) {
			f2 = -100.0F;
		}

		for (int i = -12; i <= 12; ++i) {
			for (int j = -12; j <= 12; ++j) {
				long k = p_185960_1_ + i;
				long l = p_185960_2_ + j;

				if (k * k + l * l > 4096L && this.islandNoise.func_151605_a((double) k, (double) l) < -0.8999999761581421D) {
					float f3 = (MathHelper.abs((float) k) * 3439.0F + MathHelper.abs((float) l) * 147.0F) % 13.0F + 9.0F;
					f = (float) (p_185960_3_ - i * 2);
					f1 = (float) (p_185960_4_ - j * 2);
					float f4 = 100.0F - MathHelper.sqrt_float(f * f + f1 * f1) * f3;

					if (f4 > 80.0F) {
						f4 = 80.0F;
					}

					if (f4 < -100.0F) {
						f4 = -100.0F;
					}

					if (f4 > f2) {
						f2 = f4;
					}
				}
			}
		}

		return f2;
	}

	public boolean isIslandChunk(int p_185961_1_, int p_185961_2_) {
		return (long) p_185961_1_ * (long) p_185961_1_ + (long) p_185961_2_ * (long) p_185961_2_ > 4096L && this.getIslandHeightValue(p_185961_1_, p_185961_2_, 1, 1) >= 0.0F;
	}

	private double[] getHeights(double[] p_185963_1_, int p_185963_2_, int p_185963_3_, int p_185963_4_, int p_185963_5_, int p_185963_6_, int p_185963_7_) {
		if (p_185963_1_ == null) {
			p_185963_1_ = new double[p_185963_5_ * p_185963_6_ * p_185963_7_];
		}

		double d0 = 684.412D;
		double d1 = 684.412D;
		d0 = d0 * 2.0D;
		this.pnr = this.perlinNoise1.generateNoiseOctaves(this.pnr, p_185963_2_, p_185963_3_, p_185963_4_, p_185963_5_, p_185963_6_, p_185963_7_, d0 / 80.0D, d1 / 160.0D, d0 / 80.0D);
		this.ar = this.lperlinNoise1.generateNoiseOctaves(this.ar, p_185963_2_, p_185963_3_, p_185963_4_, p_185963_5_, p_185963_6_, p_185963_7_, d0, d1, d0);
		this.br = this.lperlinNoise2.generateNoiseOctaves(this.br, p_185963_2_, p_185963_3_, p_185963_4_, p_185963_5_, p_185963_6_, p_185963_7_, d0, d1, d0);
		int i = p_185963_2_ / 2;
		int j = p_185963_4_ / 2;
		int k = 0;

		for (int l = 0; l < p_185963_5_; ++l) {
			for (int i1 = 0; i1 < p_185963_7_; ++i1) {
				float f = this.getIslandHeightValue(i, j, l, i1);

				for (int j1 = 0; j1 < p_185963_6_; ++j1) {
					double d2 = 0.0D;
					double d3 = this.ar[k] / 512.0D;
					double d4 = this.br[k] / 512.0D;
					double d5 = (this.pnr[k] / 10.0D + 1.0D) / 2.0D;

					if (d5 < 0.0D) {
						d2 = d3;
					} else if (d5 > 1.0D) {
						d2 = d4;
					} else {
						d2 = d3 + (d4 - d3) * d5;
					}

					d2 = d2 - 8.0D;
					d2 = d2 + (double) f;
					int k1 = 2;

					if (j1 > p_185963_6_ / 2 - k1) {
						double d6 = (float) (j1 - (p_185963_6_ / 2 - k1)) / 64.0F;
						d6 = MathHelper.clamp_double(d6, 0.0D, 1.0D);
						d2 = d2 * (1.0D - d6) + -3000.0D * d6;
					}

					k1 = 8;

					if (j1 < k1) {
						double d7 = (float) (k1 - j1) / ((float) k1 - 1.0F);
						d2 = d2 * (1.0D - d7) + -30.0D * d7;
					}

					p_185963_1_[k] = d2;
					++k;
				}
			}
		}

		return p_185963_1_;
	}

	/**
	 * Checks to see if a chunk exists at x, y
	 */
	public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
		return true;
	}

	/**
	 * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
	 * Return true if all chunks have been saved.
	 */
	public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_) {
		return true;
	}

	/**
	 * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
	 * unimplemented.
	 */
	public void saveExtraData() {
	}

	/**
	 * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
	 */
	public boolean unloadQueuedChunks() {
		return false;
	}

	/**
	 * Returns if the IChunkProvider supports saving.
	 */
	public boolean canSave() {
		return true;
	}

	/**
	 * Converts the instance data to a readable string.
	 */
	public String makeString() {
		return "RandomLevelSource";
	}

	/**
	 * Returns a list of creatures of the specified type that can spawn at the given location.
	 */
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, int x, int p_73155_3_, int z) {
		return this.worldObj.getBiomeGenForCoords(x, z).getSpawnableList(creatureType);
	}

	/**
	 * MCP name: {@code findClosestStructure}
	 */
	public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_) {
		return null;
	}

	public int getLoadedChunkCount() {
		return 0;
	}

	public void recreateStructures(int p_82695_1_, int p_82695_2_) {
	}

	@Override
	public Chunk loadChunk(int x, int z) {
		return this.provideChunk(x, z);
	}

	@Override
	public void populate(IChunkProvider chunkProvider, int x, int z) {
		BlockFalling.fallInstantly = true;

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider, worldObj, rand, x, z, false));

		int xPos = x * 16;
		int yPos = 0;
		int zPos = z * 16;

		if (this.mapFeaturesEnabled && this.endCityGen != null) {
			this.endCityGen.generateStructuresInChunk(this.worldObj, this.rand, x, z);
		}

		this.worldObj.getBiomeGenForCoords(xPos + 16, zPos + 16).decorate(this.worldObj, rand, xPos, zPos);
		long i = (long) x * (long) x + (long) z * (long) z;

		if (i > 4096L) {
			float f = this.getIslandHeightValue(x, z, 1, 1);


			if (f < -20.0F && this.rand.nextInt(14) == 0) {
				this.endIslands.generate(this.worldObj, this.rand, xPos + (this.rand.nextInt(16) + 8), yPos + (55 + this.rand.nextInt(16)), zPos + (this.rand.nextInt(16) + 8));

				if (this.rand.nextInt(4) == 0) {
					this.endIslands.generate(this.worldObj, this.rand, xPos + (this.rand.nextInt(16) + 8), yPos + (55 + this.rand.nextInt(16)), zPos + (this.rand.nextInt(16) + 8));
				}
			}

			if (this.getIslandHeightValue(x, z, 1, 1) > 40.0F) {
				int j = this.rand.nextInt(5);

				for (int k = 0; k < j; ++k) {
					int l = this.rand.nextInt(16) + 8;
					int i1 = this.rand.nextInt(16) + 8;
					int j1 = worldObj.getTopSolidOrLiquidBlock(xPos + l, zPos + i1);
					if (j1 > 0) {
						int k1 = j1;

						if (this.worldObj.isAirBlock(xPos + l, yPos + k1, zPos + i1) && this.worldObj.getBlock(xPos + l, yPos + k1 - 1, zPos + i1) == Blocks.end_stone) {
							BlockChorusFlower.generatePlant(this.worldObj, xPos + l, yPos + k1, zPos + i1, this.rand, 8);
						}
					}
				}
			}
		}

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(chunkProvider, worldObj, rand, x, z, false));

		BlockFalling.fallInstantly = false;

	}
}
