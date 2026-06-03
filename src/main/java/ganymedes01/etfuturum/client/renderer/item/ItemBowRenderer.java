package ganymedes01.etfuturum.client.renderer.item;

import ganymedes01.etfuturum.client.OpenGLHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class ItemBowRenderer implements IItemRenderer {

	private final RenderItem renderItem = new RenderItem();

	@Override
	public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (player == null)
			return false;

		ItemStack usingItem = player.getItemInUse();
		return type == ItemRenderType.INVENTORY && usingItem != null && usingItem == stack;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		ResourceLocation resource = textureManager.getResourceLocation(stack.getItemSpriteNumber());

		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (player == null)
			return;

		ItemStack usingItem = player.getItemInUse();
		int useRemaining = player.getItemInUseCount();

		ItemBow bow = (ItemBow) stack.getItem();

		IIcon icon = bow.getIcon(stack, 0);
		if (usingItem != null && usingItem == stack) {
			int charge = stack.getMaxItemUseDuration() - useRemaining;
			if (charge >= 18)
				icon = bow.getItemIconForUseDuration(2);
			else if (charge > 13)
				icon = bow.getItemIconForUseDuration(1);
			else if (charge > 0)
				icon = bow.getItemIconForUseDuration(0);
		}
		if (icon == null)
			icon = ((TextureMap) textureManager.getTexture(resource)).getAtlasSprite("missingno");

		OpenGLHelper.pushMatrix();
		textureManager.bindTexture(resource);

		OpenGLHelper.colour(bow.getColorFromItemStack(stack, 0));

		OpenGLHelper.disableLighting();
		OpenGLHelper.enableAlpha();
		OpenGLHelper.enableBlend();
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);

		renderItem.renderIcon(0, 0, icon, 16, 16);

		OpenGLHelper.enableLighting();
		OpenGLHelper.disableAlpha();
		OpenGLHelper.disableBlend();

		if (stack.hasEffect(0))
			renderItem.renderEffect(textureManager, 0, 0);

		OpenGLHelper.popMatrix();
	}
}
