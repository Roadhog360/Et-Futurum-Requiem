package ganymedes01.etfuturum.mixins.early.blockinventories;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import ganymedes01.etfuturum.api.inventory.FakeTileEntityProvider;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TileEntityHopper.class)
public class MixinTileEntityHopper {

	@WrapOperation(method = "func_145893_b", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTileEntity(III)Lnet/minecraft/tileentity/TileEntity;", ordinal = 0))
	private static TileEntity getFakeTileEntity(World world, int x, int y, int z, Operation<TileEntity> original) {
		Block block = world.getBlock(x, y, z);
		if (block instanceof FakeTileEntityProvider) {
			return ((FakeTileEntityProvider) block).getFakeTileEntity(world, x, y, z);
		}
		return original.call(world, x, y, z);
	}
}
