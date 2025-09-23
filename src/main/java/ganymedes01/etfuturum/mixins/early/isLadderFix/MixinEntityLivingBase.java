package ganymedes01.etfuturum.mixins.early.isLadderFix;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import ganymedes01.etfuturum.blocks.IClimbableWithoutWall;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity
{
    @Shadow
    protected boolean isJumping;
    
    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Definition(id = "isCollidedHorizontally", field = "Lnet/minecraft/entity/EntityLivingBase;isCollidedHorizontally:Z")
    @Expression("this.isCollidedHorizontally")
    @ModifyExpressionValue(method = "moveEntityWithHeading", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 2))
    private boolean etfuturum$expandClimbCondition1(boolean original, @Share("isOnSpecialClimableCheck") LocalBooleanRef conditionChecked) 
    {
        conditionChecked.set(this.isJumping && isOnSpecialClimbable());
        return original || conditionChecked.get();
    }

    @Definition(id = "isOnLadder", method = "Lnet/minecraft/entity/EntityLivingBase;isOnLadder()Z")
    @Expression("this.isOnLadder()")
    @ModifyExpressionValue(method = "moveEntityWithHeading", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
    private boolean etfuturum$expandClimbCondition2(boolean original, @Share("isOnSpecialClimableCheck") LocalBooleanRef conditionChecked)
    {
        return original || conditionChecked.get();
    }
    
    @Unique
    private boolean isOnSpecialClimbable() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        Block block = this.worldObj.getBlock(x, y, z);
        return block instanceof IClimbableWithoutWall;
    }
}
