package ganymedes01.etfuturum.world.generate.decorate;


import ganymedes01.etfuturum.blocks.BlockCaveVines;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

import static net.minecraft.world.EnumSkyBlock.Sky;
public class WorldGenCaveVines extends WorldGenerator {

    private final Block caveVines;

    public WorldGenCaveVines(Block caveVines) {
        this.caveVines = caveVines;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        if (world.isAirBlock(x, y, z) && world.getSavedLightValue(Sky, x, y, z) <= 3 && world.getBlock(x, y + 1, z).isSideSolid(world, x, y, z, ForgeDirection.DOWN)) {

            world.setBlock(x, y, z, caveVines);

            for (int i = 1; i < rand.nextInt(6) + 1; i++)
            {
                if (world.isAirBlock(x, y - i, z) && world.getBlock(x, y - i + 1, z) instanceof BlockCaveVines vine)
                {
                    vine.growVine(world, x, y - i + 1, z, false);
                }
            }
            return true;
        }
        return false;
    }
}
