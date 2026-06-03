package ganymedes01.etfuturum.world.structure;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.io.*;
import java.util.*;

public class OceanMonument {

	private static final List<BiomeGenBase> validBiomes = Arrays.asList(BiomeGenBase.ocean, BiomeGenBase.deepOcean, BiomeGenBase.river, BiomeGenBase.frozenOcean, BiomeGenBase.frozenRiver);
	private static final Long2IntOpenHashMap map = new Long2IntOpenHashMap(26000);

	public static void makeMap() {
		try {
			InputStream is = EtFuturum.class.getResourceAsStream("/assets/ocean_monument.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String s;
			while ((s = br.readLine()) != null) {
				String[] data = s.split("-");
				data[0] = data[0].trim();
				data[0] = data[0].substring(1, data[0].length() - 1);

				data[1] = data[1].trim();

				String[] coords = data[0].split(",");

				final int x = Integer.parseInt(coords[0].trim());
				final int y = Integer.parseInt(coords[1].trim());
				final int z = Integer.parseInt(coords[2].trim());
				final long key = CoordinatePacker.pack(x, y, z);
				final int value = Integer.parseInt(data[1]);

				map.put(key, value);
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void buildTemple(World world, int x, int y, int z) {
		if (world.isRemote)
			return;

		for (Long2IntMap.Entry entry : map.long2IntEntrySet()) {
			final long pos = entry.getLongKey();
			final int value = entry.getIntValue();
			final int posX = CoordinatePacker.unpackX(pos);
			final int posY = CoordinatePacker.unpackY(pos);
			final int posZ = CoordinatePacker.unpackZ(pos);

			Block block = null;
			int meta = 0;
			switch (value) {
				case 0:
				case 1:
				case 2:
					block = ModBlocks.PRISMARINE_BLOCK.get();
					meta = value;
					break;
				case 3:
					block = ModBlocks.SEA_LANTERN.get();
					break;
				case 4:
					block = Blocks.gold_block;
					break;
				case 5:
					block = ConfigBlocksItems.enableSponge ? ModBlocks.SPONGE.get() : Blocks.sponge;
					meta = 1;
					break;
				case 6:
					block = Blocks.water;
					break;
			}

			if (block != null)
				world.setBlock(posX + x, posY + y, posZ + z, block, meta, 2);
		}

		for (int i = 0; i < 7; i++) {
			generatePillar(world, x + 5 * i + 4 * i, y, z, ModBlocks.PRISMARINE_BLOCK.get(), 1);
			generatePillar(world, x, y, z + 5 * i + 4 * i, ModBlocks.PRISMARINE_BLOCK.get(), 1);
			generatePillar(world, x + 54, y, z + 5 * i + 4 * i, ModBlocks.PRISMARINE_BLOCK.get(), 1);
			if (i != 3)
				generatePillar(world, x + 5 * i + 4 * i, y, z + 54, ModBlocks.PRISMARINE_BLOCK.get(), 1);
		}
	}

	private static void generatePillar(World world, int x, int y, int z, Block block, int meta) {
		for (int i = 1; i <= 5; i++)
			generatePillarSection(world, x, y - i, z, block, meta);
		y -= 5;

		for (; y >= 0; y--) {
			generatePillarSection(world, x, y, z, block, meta);
			for (int i = 0; i < 4; i++)
				for (int k = 0; k < 4; k++)
					if (world.getBlock(x + i, y, z).getMaterial() != Material.water && y > 3) {
						generatePillarSection(world, x, y - 1, z, block, meta);
						generatePillarSection(world, x, y - 2, z, block, meta);
						return;
					}
		}
	}

	private static void generatePillarSection(World world, int x, int y, int z, Block block, int meta) {
		for (int i = 0; i < 4; i++)
			for (int k = 0; k < 4; k++)
				if (world.getBlock(x + i, y, z).getBlockHardness(world, x + i, y, z + k) > 0)
					world.setBlock(x + i, y, z + k, block, meta, 2);
	}

	public static void generateFile(World world, int x, int y, int z, String path) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));

			for (int i = 0; i < 58; i++)
				for (int j = 0; j < 31 - 9; j++)
					for (int k = 0; k < 58; k++) {
						Block b = world.getBlock(x + i, y + j, z + k);
						int meta = world.getBlockMetadata(x + i, y + j, z + k);

						String s = "(" + i + ", " + j + ", " + k + ") - ";
						if (b == ModBlocks.PRISMARINE_BLOCK.get())
							s += meta;
						else if (b == ModBlocks.SEA_LANTERN.get())
							s += 3;
						else if (b == Blocks.gold_block)
							s += 4;
						else if (b == Blocks.sponge)
							s += 5;
						else if (b == Blocks.stained_glass)
							s += 6;
						else
							s = null;

						if (s != null) {
							bw.write(s);
							bw.newLine();
						}
					}

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean canSpawnAt(World worldObj, int chunkX, int chunkZ) {
		int spacing = 32;
		int separation = 5;
		int xx = chunkX;
		int zz = chunkZ;

		if (chunkX < 0)
			chunkX -= spacing - 1;

		if (chunkZ < 0)
			chunkZ -= spacing - 1;

		int i1 = chunkX / spacing;
		int j1 = chunkZ / spacing;
		Random random = worldObj.setRandomSeed(i1, j1, 10387313);
		i1 *= spacing;
		j1 *= spacing;
		i1 += (random.nextInt(spacing - separation) + random.nextInt(spacing - separation)) / 2;
		j1 += (random.nextInt(spacing - separation) + random.nextInt(spacing - separation)) / 2;

		if (xx == i1 && zz == j1) {
			if (worldObj.getWorldChunkManager().getBiomeGenAt(xx * 16 + 8, zz * 16 + 8) != BiomeGenBase.deepOcean)
				return false;
			return worldObj.getWorldChunkManager().areBiomesViable(xx * 16 + 8, zz * 16 + 8, 29, validBiomes);
		}

		return false;
	}
}