package ganymedes01.etfuturum.world.end.gen;

import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.world.end.dimension.ChunkProviderEFREnd;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.Random;

/**
 * End city structure generator for the outer End islands.
 * Uses vanilla MapGenStructure for chunk boundary safety, bounding box clipping,
 * and persistence.
 */
public class MapGenEndCity extends MapGenStructure {

	// Vanilla End city spacing parameters
	private static final int SPACING = 20;
	private static final int SEPARATION = 11;
	private static final int SALT = 10387313;

	private final ChunkProviderEFREnd chunkProvider;

	public MapGenEndCity(ChunkProviderEFREnd chunkProvider) {
		this.chunkProvider = chunkProvider;
	}

	@Override
	public String func_143025_a() {
		return "EndCity";
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
		return isValidCityAt(this.chunkProvider, chunkX, chunkZ, this.worldObj.getSeed());
	}

	/**
	 * Determines if an End city should spawn at the given chunk coordinates.
	 * Checks spacing grid, island presence, and ground height.
	 */
	public static boolean isValidCityAt(ChunkProviderEFREnd chunkProvider, int chunkX, int chunkZ, long worldSeed) {
		int cx = chunkX;
		int cz = chunkZ;

		if (chunkX < 0) chunkX -= SPACING - 1;
		if (chunkZ < 0) chunkZ -= SPACING - 1;

		int gridX = chunkX / SPACING;
		int gridZ = chunkZ / SPACING;

		Random random = new Random();
		random.setSeed((long) gridX * 341873128712L + (long) gridZ * 132897987541L + worldSeed + SALT);

		gridX *= SPACING;
		gridZ *= SPACING;

		gridX += (random.nextInt(SPACING - SEPARATION) + random.nextInt(SPACING - SEPARATION)) / 2;
		gridZ += (random.nextInt(SPACING - SEPARATION) + random.nextInt(SPACING - SEPARATION)) / 2;

		if (cx != gridX || cz != gridZ) return false;

		// Check if this is on a valid outer island
		if (!chunkProvider.isIslandChunk(cx, cz)) return false;

		// Predict the rotation that the start will use to calculate the height
		Random structRand = new Random(
				(long) cx * 341873128712L + (long) cz * 132897987541L + worldSeed + SALT);
		int rotation = structRand.nextInt(4);

		int groundHeight = getGroundHeight(chunkProvider, cx, cz, rotation);
		return groundHeight >= 60;
	}

	@Override
	protected StructureStart getStructureStart(int chunkX, int chunkZ) {
		return new Start(this.worldObj, chunkProvider, this.rand, chunkX, chunkZ);
	}

	/**
	 * Approximate terrain height to avoid recursive chunk loading during generation.
	 * Evaluates exactly 4 corners matching the first room footprint like vanilla 1.9+.
	 */
	public static int getGroundHeight(ChunkProviderEFREnd chunkProvider, int chunkX, int chunkZ, int rotation) {
		Block[] blocks = new Block[32768];
		chunkProvider.setBlocksInChunk(chunkX, chunkZ, blocks);
		
		int i = 5;
		int j = 5;

		if (rotation == 1) { // CLOCKWISE_90
			i = -5;
		} else if (rotation == 2) { // CLOCKWISE_180
			i = -5;
			j = -5;
		} else if (rotation == 3) { // COUNTERCLOCKWISE_90
			j = -5;
		}

		int k = findGroundBlockIdx(blocks, 7, 7);
		int l = findGroundBlockIdx(blocks, 7, 7 + j);
		int i1 = findGroundBlockIdx(blocks, 7 + i, 7);
		int j1 = findGroundBlockIdx(blocks, 7 + i, 7 + j);
		
		return Math.min(Math.min(k, l), Math.min(i1, j1));
	}

	private static boolean isValidEndCitySupportBlock(Block b) {
		if (b == null || b.getMaterial() == net.minecraft.block.material.Material.air) return false;
		if (b == net.minecraft.init.Blocks.end_stone) return true;
		// Generally ignore non-solid blocks, liquids, and replaceable elements
		if (b.getMaterial().isLiquid() || b.getMaterial().isReplaceable()) return false;
		if (!b.getMaterial().isSolid()) return false;
		return true;
	}

	private static int findGroundBlockIdx(Block[] blocks, int x, int z) {
		for (int y = 127; y > 0; y--) {
			Block b = blocks[(x * 16 + z) * 128 + y];
			if (isValidEndCitySupportBlock(b)) {
				return y;
			}
		}
		return 0;
	}

	public static class Start extends StructureStart {
		
		private boolean isCreated;

		public Start() {}

		public Start(World world, ChunkProviderEFREnd provider, Random rand, int chunkX, int chunkZ) {
			super(chunkX, chunkZ);

			// Determine rotation (based on world seed + position for determinism)
			Random structRand = new Random(
					(long) chunkX * 341873128712L + (long) chunkZ * 132897987541L + world.getSeed() + SALT);
			int rotation = structRand.nextInt(4);

			int groundHeight = getGroundHeight(provider, chunkX, chunkZ, rotation);
			if (groundHeight < 60) {
				// Fallback safety (should be caught by canSpawnStructureAtCoords)
				this.isCreated = false;
				this.boundingBox = new net.minecraft.world.gen.structure.StructureBoundingBox();
				return;
			}

			int x = chunkX * 16 + 8;
			int z = chunkZ * 16 + 8;

			BlockPos startPos = new BlockPos(x, groundHeight, z);

			EndCityPieces.startHouseTower(startPos, rotation, this.components, structRand);

			this.updateBoundingBox();
			this.isCreated = true;
		}

		@Override
		public boolean isSizeableStructure() {
			return this.isCreated;
		}
	}
}
