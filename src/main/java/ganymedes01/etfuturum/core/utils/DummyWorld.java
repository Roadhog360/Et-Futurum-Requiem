package ganymedes01.etfuturum.core.utils;

import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import roadhog360.hogutils.api.RegistryMapping;
import roadhog360.hogutils.api.utils.FastRandom;

import java.io.File;
import java.util.Map;

/**
 * Pulled from GregTech6 with permission from Greg, with modification to support fake block setting
 *
 * @author roadhog360 GregoriusT
 */
public class DummyWorld extends World {
	public static class GT_IteratorRandom extends FastRandom {
		public int mIterationStep = Integer.MAX_VALUE;

		@Override
		public int nextInt(int aParameter) {
			if (mIterationStep == 0 || mIterationStep > aParameter) {
				mIterationStep = aParameter;
			}
			return --mIterationStep;
		}
	}

	public static final DummyWorld GLOBAL_DUMMY_WORLD = new DummyWorld();
	public GT_IteratorRandom mRandom = new GT_IteratorRandom();
	private final Map<BlockPos, RegistryMapping<Block>> FAKE_WORLD_DATA = new Object2ObjectArrayMap<>(); //Stores setblock data for getblock
	private static final RegistryMapping<Block> AIR = RegistryMapping.of(Blocks.air, 0);

	DummyWorld(ISaveHandler par1iSaveHandler, String par2Str, WorldProvider par3WorldProvider, WorldSettings par4WorldSettings, Profiler par5Profiler) {
		super(par1iSaveHandler, par2Str, par4WorldSettings, par3WorldProvider, par5Profiler);
		rand = mRandom;
	}

	public DummyWorld() {
		this(
				new ISaveHandler() {
					@Override
					public void saveWorldInfoWithPlayer(WorldInfo var1, NBTTagCompound var2) {/*Do nothing*/}

					@Override
					public void saveWorldInfo(WorldInfo var1) {/*Do nothing*/}

					@Override
					public WorldInfo loadWorldInfo() {
						return null;
					}

					@Override
					public IPlayerFileData getSaveHandler() {
						return null;
					}

					@Override
					public File getMapFileFromName(String var1) {
						return null;
					}

					@Override
					public IChunkLoader getChunkLoader(WorldProvider var1) {
						return null;
					}

					@Override
					public void flush() {/*Do nothing*/}

					@Override
					public void checkSessionLock() {/*Do nothing*/}

					@Override
					public String getWorldDirectoryName() {
						return null;
					}

					@Override
					public File getWorldDirectory() {
						return null;
					}
				},
				"DUMMY_DIMENSION",
				new WorldProvider() {
					@Override
					public String getDimensionName() {
						return "DUMMY_DIMENSION";
					}
				},
				new WorldSettings(new WorldInfo(new NBTTagCompound())),
				new Profiler()
		);
	}

	@Override
	protected IChunkProvider createChunkProvider() {
		return null;
	}

	@Override
	public Entity getEntityByID(int aEntityID) {
		return null;
	}

	@Override
	public boolean setBlockMetadataWithNotify(int aX, int aY, int aZ, int aMeta, int flags) {
		BlockPos pos = new BlockPos(aX, aY, aZ);
		if (FAKE_WORLD_DATA.containsKey(pos)) {
			RegistryMapping<Block> block = FAKE_WORLD_DATA.get(pos);
			setBlock(aX, aY, aZ, block.getObject(), aMeta, 0);
			return true;
		}
		return false;
	}

	@Override
	public boolean setBlockToAir(int aX, int aY, int aZ) {
		FAKE_WORLD_DATA.remove(new BlockPos(aX, aY, aZ));
		return true;
	}

	@Override
	public boolean setBlock(int x, int y, int z, Block block) {
		return this.setBlock(x, y, z, block, 0, 0);
	}

	@Override
	public boolean setBlock(int aX, int aY, int aZ, Block aBlock, int aMeta, int aFlags) {
		BlockPos pos = new BlockPos(aX, aY, aZ);
		if (aBlock == Blocks.air) {
			FAKE_WORLD_DATA.remove(pos);
		} else {
			FAKE_WORLD_DATA.put(pos, RegistryMapping.of(aBlock, aMeta));
		}
		return true;
	}

	@Override
	public float getSunBrightnessFactor(float p_72967_1_) {
		return 1.0F;
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(int aX, int aZ) {
		if (aX >= 16 && aZ >= 16 && aX < 32 && aZ < 32) return BiomeGenBase.plains;
		return BiomeGenBase.ocean;
	}

	@Override
	public int getFullBlockLightValue(int aX, int aY, int aZ) {
		return 10;
	}

	@Override
	public Block getBlock(int aX, int aY, int aZ) {
		return FAKE_WORLD_DATA.getOrDefault(new BlockPos(aX, aY, aZ), AIR).getObject();
	}

	@Override
	public int getBlockMetadata(int aX, int aY, int aZ) {
		return FAKE_WORLD_DATA.getOrDefault(new BlockPos(aX, aY, aZ), AIR).getMeta();
	}

	@Override
	public boolean canBlockSeeTheSky(int aX, int aY, int aZ) {
		if (aX >= 16 && aZ >= 16 && aX < 32 && aZ < 32) return aY > 64;
		return true;
	}

	/**
	 * MCP name: {@code getRenderDistanceChunks}
	 */
	@Override
	protected int func_152379_p() {
		return 0;
	}

	public void clearBlocksCache() {
		FAKE_WORLD_DATA.clear();
	}
}
