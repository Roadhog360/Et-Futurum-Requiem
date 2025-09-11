package ganymedes01.etfuturum.mixins.early.soulfire;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import ganymedes01.etfuturum.ducks.ISoulFireInfo;
import net.minecraft.block.BlockFire;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks {
	@Shadow
	public IBlockAccess blockAccess;

	@WrapOperation(method = "renderBlockFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockFire;getFireIcon(I)Lnet/minecraft/util/IIcon;"))
	private IIcon getFireIcon(BlockFire instance, int p_149840_1_, Operation<IIcon> original,
							  @Local(ordinal = 0, argsOnly = true) int x, @Local(ordinal = 1, argsOnly = true) int y, @Local(ordinal = 2, argsOnly = true) int z) {
		if (instance == Blocks.fire && instance instanceof ISoulFireInfo) { //Only apply to vanilla fires
			if (((ISoulFireInfo) instance).efr$isSoulFire(blockAccess, x, y, z)) {
				return ((ISoulFireInfo) instance).getSoulFireIcon(p_149840_1_);
			}
		}
		return original.call(instance, p_149840_1_);
	}
}
