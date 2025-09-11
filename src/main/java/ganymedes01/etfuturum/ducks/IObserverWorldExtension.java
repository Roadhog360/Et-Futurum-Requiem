package ganymedes01.etfuturum.ducks;

import net.minecraft.block.Block;

public interface IObserverWorldExtension {
	boolean efr$hasScheduledUpdate(int x, int y, int z, Block block);
}
