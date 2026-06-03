package ganymedes01.etfuturum.mixins.early.goats.client;

import ganymedes01.etfuturum.client.renderer.item.ItemGoatHornRenderer;
import ganymedes01.etfuturum.items.ItemGoatHorn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

	@Shadow
	private Minecraft mc;
	@Shadow
	private ItemStack itemToRender;
	@Shadow
	private float equippedProgress;
	@Shadow
	private float prevEquippedProgress;

	@Inject(method = "renderItemInFirstPerson", at = @At("HEAD"), cancellable = true)
	private void renderActiveGoatHorn(float partialTicks, CallbackInfo ci) {
		EntityClientPlayerMP player = mc.thePlayer;
		ItemStack stack = itemToRender;
		if (!isUsingRenderedGoatHorn(player, stack)) {
			return;
		}

		renderGoatHornInFirstPerson(player, stack, partialTicks);
		ci.cancel();
	}

	private boolean isUsingRenderedGoatHorn(EntityClientPlayerMP player, ItemStack stack) {
		if (player == null || !ItemGoatHorn.isGoatHorn(stack) || player.getItemInUseCount() <= 0) {
			return false;
		}

		return ItemGoatHorn.isGoatHorn(player.getItemInUse());
	}

	private void renderGoatHornInFirstPerson(EntityClientPlayerMP player, ItemStack stack, float partialTicks) {
		float equipped = prevEquippedProgress + (equippedProgress - prevEquippedProgress) * partialTicks;
		float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;

		GL11.glPushMatrix();
		GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();

		EntityPlayerSP playerSP = (EntityPlayerSP) player;
		float armPitch = playerSP.prevRenderArmPitch + (playerSP.renderArmPitch - playerSP.prevRenderArmPitch) * partialTicks;
		float armYaw = playerSP.prevRenderArmYaw + (playerSP.renderArmYaw - playerSP.prevRenderArmYaw) * partialTicks;
		GL11.glRotatef((player.rotationPitch - armPitch) * 0.1F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef((player.rotationYaw - armYaw) * 0.1F, 0.0F, 1.0F, 0.0F);

		int light = mc.theWorld.getLightBrightnessForSkyBlocks(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ), 0);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) (light % 65536), (float) (light / 65536));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GL11.glPushMatrix();
		GL11.glTranslatef(0.56F, -0.52F - (1.0F - equipped) * 0.6F, -0.72F);
		GL11.glTranslatef(-0.2785682F, 0.18344387F, 0.15731531F);
		GL11.glRotatef(-13.935F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-35.3F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(9.785F, 0.0F, 0.0F, 1.0F);
		GL11.glScalef(0.4F, 0.4F, 0.4F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		ItemGoatHornRenderer.renderIcon(stack);
		GL11.glPopMatrix();

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
	}
}
