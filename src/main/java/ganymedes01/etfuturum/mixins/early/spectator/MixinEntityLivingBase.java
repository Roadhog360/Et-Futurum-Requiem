package ganymedes01.etfuturum.mixins.early.spectator;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {
    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @ModifyReturnValue(method = "canBePushed", at = @At(value = "RETURN"))
    public boolean spectatorCannotBePushed(boolean original) {
        return !SpectatorUtils.isSpectator(this) && original;
    }

    @ModifyReturnValue(method = "canBeCollidedWith", at = @At(value = "RETURN"))
    public boolean spectatorCannotBeCollidedWith(boolean original) {
        return !SpectatorUtils.isSpectator(this) && original;
    }

    @ModifyReturnValue(method = "canBeCollidedWith", at = @At(value = "RETURN"))
    protected boolean spectatorCannotCollideWith(boolean original) {
        return !SpectatorUtils.isSpectator(this) && original;
    }

    @ModifyReturnValue(method = "isOnLadder", at = @At(value = "RETURN"))
    public boolean spectatorNotOnLadder(boolean original) {
        return !SpectatorUtils.isSpectator(this) && original;
    }
}
