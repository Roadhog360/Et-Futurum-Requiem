package ganymedes01.etfuturum.api;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DripOperationRegistry {

    private static final List<IDripOperation> OPERATIONS = Lists.newArrayList();

    /** Registers a custom drip operation. Operations run in registration order. */
    public static void register(@NotNull IDripOperation operation) {
        OPERATIONS.add(operation);
    }

    /**
     * Iterates registered operations and stops at the first that returns true.
     *
     * Assumption: called server-side only, from BlockPointedDripstone.updateTick.
     */
    @ApiStatus.Internal
    public static void runOperations(World world,
                                     int tipX, int tipY, int tipZ,
                                     int targetX, int targetY, int targetZ,
                                     @NotNull Fluid fluid) {
        for (IDripOperation op : OPERATIONS) {
            if (op.apply(world, tipX, tipY, tipZ, targetX, targetY, targetZ, fluid)) {
                return;
            }
        }
    }

    /** Registers the built-in drip operations. Called from {@code EtFuturum.postInit}. */
    public static void init() {
        if (!ConfigBlocksItems.enableDripstone) return;

        if (ConfigBlocksItems.enableLavaCauldrons) {
            register(new LavaCauldronFillOperation());
        }

        register(new WaterCauldronFillOperation());
    }

    // Built-in operations — registered in init(), available for reference/extension

    /** Fills an empty water cauldron one level when water drips. 45/256 chance. */
    static final class WaterCauldronFillOperation implements IDripOperation {

        @Override
        public boolean apply(World world,
                             int tipX, int tipY, int tipZ,
                             int targetX, int targetY, int targetZ,
                             @NotNull Fluid fluid) {
            if (fluid != FluidRegistry.WATER) return false;
            if (world.getBlock(targetX, targetY, targetZ) != Blocks.cauldron) return false;

            int meta = world.getBlockMetadata(targetX, targetY, targetZ);
            if (meta >= 3) return false;

            if (world.rand.nextInt(256) >= 45) return false;

            world.setBlockMetadataWithNotify(targetX, targetY, targetZ, meta + 1, 3);
            return true;
        }
    }

    /** Fills an empty cauldron with lava when lava drips. 15/256 chance. */
    static final class LavaCauldronFillOperation implements IDripOperation {

        @Override
        public boolean apply(World world,
                             int tipX, int tipY, int tipZ,
                             int targetX, int targetY, int targetZ,
                             @NotNull Fluid fluid) {
            if (fluid != FluidRegistry.LAVA) return false;
            if (world.getBlock(targetX, targetY, targetZ) != Blocks.cauldron) return false;
            if (world.getBlockMetadata(targetX, targetY, targetZ) != 0) return false;

            if (world.rand.nextInt(256) >= 15) return false;

            world.setBlock(targetX, targetY, targetZ, ModBlocks.LAVA_CAULDRON.get(), 3, 3);
            return true;
        }
    }
}
