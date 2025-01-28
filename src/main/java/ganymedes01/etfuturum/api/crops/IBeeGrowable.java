package ganymedes01.etfuturum.api.crops;

import ganymedes01.etfuturum.entities.EntityBee;
import net.minecraft.world.World;

public interface IBeeGrowable {
	/// Does the pollination logic. Typically advancing the crop by 1 stage.
	void pollinateCrop(World world, int x, int y, int z, EntityBee bee);

	/// If true, the bee invokes {@link #pollinateCrop} on this block.
	boolean canPollinate(World world, int x, int y, int z, EntityBee bee);
}
