package ganymedes01.etfuturum.mixins.early.items;

import com.mojang.authlib.GameProfile;
import ganymedes01.etfuturum.client.renderer.tileentity.TileEntityFancySkullRenderer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.lwjgl.opengl.GL11;

@Mixin(RenderBiped.class)
public class MixinRenderBiped {

    @Redirect(method = "renderEquippedItems(Lnet/minecraft/entity/EntityLiving;F)V", 
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/tileentity/TileEntitySkullRenderer;func_152674_a(FFFIFILcom/mojang/authlib/GameProfile;)V"))
    private void etfuturum$redirectSkullRender(TileEntitySkullRenderer instance, float x, float y, float z, int direction, float rotation, int meta, GameProfile profile) {
        if (meta == 5) {
            TileEntityFancySkullRenderer.instance.renderWornDragonHead(x, y, z, direction, rotation, profile);
        } else {
            instance.func_152674_a(x, y, z, direction, rotation, meta, profile);
        }
    }
}
