package ganymedes01.etfuturum.mixins.early.liquidphysics;

import ganymedes01.etfuturum.core.utils.Logger;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BlockLiquid.class)
public abstract class MixinBlockLiquid {

    @ModifyVariable(method = "velocityToAddToEntity", at = @At("STORE"), ordinal = 1, name = "vec31")
    public Vec3 changeEntitySpeedByFluidLevel(Vec3 liquidVelocityChange, World worldIn, int x, int y, int z, Entity entityIn, Vec3 velocity) {
        double power = worldIn.getBlockMetadata(x, y, z);
        /* Liquid levels go from 0 to 8, where 8 is flowing down
         * This changes nothing for liquids flowing down, but for levels between 0 and 7 it does:
         * Inverse linear mapping from [0,7] to [0.5,2] and precomputing the constant of (2-0.5) / (7-0)
         */

        //if ( !worldIn.isRemote ) Logger.debug(String.format("Liquid at x: %d y: %d z: %d has velo %s", x, y, z, liquidVelocityChange));
        double minSpeed = 0.4;
        double maxSpeed = 1.0;
//      power = power == 8 ? 1 : maxSpeed - power * ((maxSpeed - minSpeed) / 7);
        power = power == 8 ? 1 : maxSpeed - (power / 7) * (maxSpeed - minSpeed);
        //if ( !worldIn.isRemote ) Logger.debug(String.format("Liquid at x: %d y: %d z: %d has power %f", x, y, z, power));

        liquidVelocityChange.xCoord *= power;
        liquidVelocityChange.zCoord *= power;
        //if ( !worldIn.isRemote ) Logger.debug(String.format("Returning liquid velo %s", liquidVelocityChange));
        return liquidVelocityChange;
    }

}
