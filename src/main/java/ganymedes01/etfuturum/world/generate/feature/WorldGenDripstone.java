package ganymedes01.etfuturum.world.generate.feature;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.noise.NoiseSampler;
import com.gtnewhorizon.gtnhlib.noise.NormalizedSampler;
import com.gtnewhorizon.gtnhlib.noise.OctavesSampler;
import com.gtnewhorizon.gtnhlib.noise.ScaledSampler;
import com.gtnewhorizon.gtnhlib.util.StdLCG;
import cpw.mods.fml.common.IWorldGenerator;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockPointedDripstone;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;

public class WorldGenDripstone implements IWorldGenerator {

    /// We don't want to generate dripstone too close to the surface
    private static final int MAX_HEIGHT = 16;
    private static final double NOISE_THRESHOLD = 0.6;
    private static final double DRIPSTONE_THRESHOLD = 0.65;

    private final StdLCG rand = new StdLCG();

    @Override
    public void generate(
        Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider
    ) {
        rand.setSeed(world.getSeed());
        NoiseSampler humidity = new ScaledSampler(new NormalizedSampler(new OctavesSampler(rand, 6)), 0.02);

        int wX = chunkX << 4;
        int wZ = chunkZ << 4;

        BlockPointedDripstone pointed = (BlockPointedDripstone) ModBlocks.POINTED_DRIPSTONE.get();

        for (int rZ = 0; rZ < 16; rZ++) {
            for (int rX = 0; rX < 16; rX++) {
                int x = wX + rX + 8;
                int z = wZ + rZ + 8;

                BiomeGenBase biome = world.getBiomeGenForCoords(x, z);

                boolean validBiome = biome.rootHeight >= 0.5 || biome.temperature >= 0.5 && biome.temperature <= 1.5 && biome.rainfall >= 0.5;

                // We only want to generate in mountainy or humid biomes
                if (!validBiome) continue;

                int start = (ConfigWorld.deepslateGenerationMode != -1 ? ConfigWorld.deepslateMaxY : 10) + random.nextInt(8) - 4;

                for (int y = start; y < world.getHeightValue(x, z) - MAX_HEIGHT; y++) {
                    if (humidity.sample(x, y, z) < NOISE_THRESHOLD) continue;

                    if (world.getBlock(x, y, z) != Blocks.stone) continue;

                    int surroundingAir = 0;

                    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                        if (world.isAirBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) {
                            surroundingAir |= dir.flag;
                        }
                    }

                    if (surroundingAir == 0) continue;

                    world.setBlock(x, y, z, ModBlocks.DRIPSTONE_BLOCK.get(), 0, 2);

                    if ((surroundingAir & ForgeDirection.UP.flag) != 0) {
                        int height = (int) ((humidity.sample(x, y + 1, z) - NOISE_THRESHOLD) / 0.05);

                        for (int y2 = 0; y2 < height; y2++) {
                            world.setBlock(x, y + 1 + y2, z, pointed, pointed.up.getMetaPrimitive(true, 0), 2);
                        }

                        for (int y2 = 0; y2 < height; y2++) {
                            pointed.onNeighborBlockChange(world, x, y + 1 + y2, z, pointed);
                        }
                    }

                    if ((surroundingAir & ForgeDirection.DOWN.flag) != 0) {
                        int height = (int) ((humidity.sample(x, y - 1, z) - NOISE_THRESHOLD) / 0.05);

                        for (int y2 = 0; y2 < height; y2++) {
                            world.setBlock(x, y - 1 - y2, z, pointed, pointed.up.getMetaPrimitive(false, 0), 2);
                        }

                        for (int y2 = 0; y2 < height; y2++) {
                            pointed.onNeighborBlockChange(world, x, y - 1 - y2, z, pointed);
                        }
                    }
                }
            }
        }
    }
}
