package ganymedes01.etfuturum.mixins.early.isLadderFix;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
    private boolean etfuturum$expandClimbCondition1(boolean original) 
    {
        return original || this.isJumping;
    }

}
