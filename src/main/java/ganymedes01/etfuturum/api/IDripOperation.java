package ganymedes01.etfuturum.api;

import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import org.jetbrains.annotations.NotNull;

/**
 * Server-side action triggered when a stalactite drips toward a target block.
 * Register implementations via {@link DripOperationRegistry#register}.
 *
 * <p>Operations run in registration order. The first to return {@code true}
 * consumes the event and skips remaining operations for that tick.
 */
public interface IDripOperation {

    /**
     * @param world   the server world (never remote)
     * @param tipX    X of the stalactite tip block
     * @param tipY    Y of the stalactite tip block
     * @param tipZ    Z of the stalactite tip block
     * @param targetX X of the target block below the stalactite
     * @param targetY Y of the target block below the stalactite
     * @param targetZ Z of the target block below the stalactite
     * @param fluid   the source fluid above the stalactite base
     * @return {@code true} to consume the event and skip remaining operations
     */
    boolean apply(World world,
                  int tipX, int tipY, int tipZ,
                  int targetX, int targetY, int targetZ,
                  @NotNull Fluid fluid);
}
