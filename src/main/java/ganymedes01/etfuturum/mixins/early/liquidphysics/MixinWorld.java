package ganymedes01.etfuturum.mixins.early.liquidphysics;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(World.class)
public class MixinWorld {

    @ModifyConstant(method = "handleMaterialAcceleration", constant = @Constant(doubleValue = 0.014D, ordinal = 0))
    private double speedupItemsInLiquids(double in, AxisAlignedBB boundingBox, Material liquid, Entity entity) {
        if (entity instanceof EntityItem) {
            return in * 2.5;
        } else {
            return in * 1.25;
        }
    }
}