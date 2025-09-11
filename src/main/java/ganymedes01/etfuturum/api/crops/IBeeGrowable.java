package ganymedes01.etfuturum.api.crops;

import net.minecraft.block.IGrowable;
import net.minecraft.world.World;

import java.util.Random;

/// Specify special behaviors bees should have when growing crops. Typically they'll use the {@link IGrowable} interface; implementing this overrides what bees will do to your crops.
/// Blocks can implement this interface only, and do not also have to implement {@link IGrowable}.
public interface IBeeGrowable {
	/// Can a bee interact with this crop?
	boolean canBeeGrow(World world, Random rand, int x, int y, int z);
	/// What happens to this block if the bee does grow it? (Is {@link IBeeGrowable#canBeeGrow} true?)
	boolean onBeeGrow(World world, Random rand, int x, int y, int z);
}
