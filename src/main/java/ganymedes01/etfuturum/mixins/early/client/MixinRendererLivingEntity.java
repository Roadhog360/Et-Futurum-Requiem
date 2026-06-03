package ganymedes01.etfuturum.mixins.early.client;

import ganymedes01.etfuturum.client.renderer.tileentity.TileEntityFancySkullRenderer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RendererLivingEntity.class)
public class MixinRendererLivingEntity {
    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At("HEAD"))
    private void etfuturum$storeEntity(EntityLivingBase entity, double x, double y, double z, float yaw, float partialTicks, CallbackInfo ci) {
        TileEntityFancySkullRenderer.currentRenderEntity = entity;
        TileEntityFancySkullRenderer.currentPartialTicks = partialTicks;
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At("RETURN"))
    private void etfuturum$clearEntity(EntityLivingBase entity, double x, double y, double z, float yaw, float partialTicks, CallbackInfo ci) {
        TileEntityFancySkullRenderer.currentRenderEntity = null;
        TileEntityFancySkullRenderer.currentPartialTicks = 0.0F;
    }
}
