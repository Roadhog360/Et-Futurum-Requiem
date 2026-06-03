package ganymedes01.etfuturum.mixins.early.observer;

import ganymedes01.etfuturum.blocks.BlockObserver;
import ganymedes01.etfuturum.blocks.IBlockObserver;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinWorld {
	@Shadow
	public boolean isRemote;

	@Shadow
	public abstract boolean blockExists(int p_72899_1_, int p_72899_2_, int p_72899_3_);

	@Shadow
	public abstract Block getBlock(int p_147439_1_, int p_147439_2_, int p_147439_3_);

	private int observedX, observedY, observedZ;

	private void etfu$notifySurroundingObservers(int x, int y, int z, Block block, int sideExcept) {
		observedX = x;
		observedY = y;
		observedZ = z;
		/* blockExists checks added to prevent MC-123860 */
		if (sideExcept != 4 && blockExists(x - 1, y, z)) {
			this.etfu$notifyObserver(x - 1, y, z, block);
		}

		if (sideExcept != 5 && blockExists(x + 1, y, z)) {
			this.etfu$notifyObserver(x + 1, y, z, block);
		}

		if (sideExcept != 0) {
			this.etfu$notifyObserver(x, y - 1, z, block);
		}

		if (sideExcept != 1) {
			this.etfu$notifyObserver(x, y + 1, z, block);
		}

		if (sideExcept != 2 && blockExists(x, y, z - 1)) {
			this.etfu$notifyObserver(x, y, z - 1, block);
		}

		if (sideExcept != 3 && blockExists(x, y, z + 1)) {
			this.etfu$notifyObserver(x, y, z + 1, block);
		}
	}

	private void etfu$notifyObserver(int x, int y, int z, Block otherBlock) {
		Block observer = getBlock(x, y, z);
		if (observer instanceof IBlockObserver) {
			((IBlockObserver) observer).observedNeighborChange((World) (Object) this, x, y, z, otherBlock, observedX, observedY, observedZ);
		}
	}

	private static boolean etfu$shouldObserverNotify(int flag) {
		return ((flag & 1) != 0 || (flag & 16) == 0);
	}

	@Inject(method = "markAndNotifyBlock", at = @At("TAIL"), remap = false)
	private void notifyObserverOnSkippedNeighborUpdate(int x, int y, int z, Chunk chunk, Block oldBlock, Block newBlock, int flag, CallbackInfo ci) {
		/* Ensure that observers are also notified */
		if (BlockObserver.areNotificationsEnabled() && !this.isRemote && etfu$shouldObserverNotify(flag)) {
			etfu$notifySurroundingObservers(x, y, z, newBlock, -1);
		}
	}

	@Inject(method = "setBlockMetadataWithNotify", at = @At("RETURN"))
	private void notifyObserverOnSkippedNeighborUpdate(int x, int y, int z, int meta, int flag, CallbackInfoReturnable<Boolean> cir) {
		/* Ensure that observers are also notified */
		if (BlockObserver.areNotificationsEnabled() && !this.isRemote && cir.getReturnValue() && etfu$shouldObserverNotify(flag)) {
			etfu$notifySurroundingObservers(x, y, z, getBlock(x, y, z), -1);
		}
	}


}
