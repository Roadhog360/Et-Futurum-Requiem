package ganymedes01.etfuturum.client.renderer.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.client.LayeredColorMaskTexture;
import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.client.model.ModelBanner;
import ganymedes01.etfuturum.lib.EnumColor;
import ganymedes01.etfuturum.tileentities.TileEntityBanner;
import ganymedes01.etfuturum.tileentities.TileEntityBanner.EnumBannerPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class TileEntityBannerRenderer extends TileEntitySpecialRenderer {

	private static final Map<String, TimedBannerTexture> CANVAS_TEXTURES = Maps.newHashMap();
	private static final ResourceLocation BASE_TEXTURE = new ResourceLocation("textures/entity/banner_base.png");
	private final ModelBanner bannerModel = new ModelBanner();

	private ResourceLocation getTexture(TileEntityBanner banner) {
		String s = banner.func_175116_e();

		if (s.isEmpty())
			return null;
		TimedBannerTexture texture = CANVAS_TEXTURES.get(s);
		if (texture == null) {
			if (CANVAS_TEXTURES.size() >= 256) {
				long i = System.currentTimeMillis();
				Iterator<String> iterator = CANVAS_TEXTURES.keySet().iterator();

				while (iterator.hasNext()) {
					String s1 = iterator.next();
					TimedBannerTexture texture1 = CANVAS_TEXTURES.get(s1);

					if (i - texture1.time > 60000L) {
						Minecraft.getMinecraft().getTextureManager().deleteTexture(texture1.texture);
						iterator.remove();
					}
				}

				if (CANVAS_TEXTURES.size() >= 256)
					return null;
			}

			List<EnumBannerPattern> list1 = banner.getPatternList();
			List<EnumColor> list = banner.getColorList();
			ArrayList<String> arraylist = Lists.newArrayList();
			Iterator<EnumBannerPattern> patters = list1.iterator();

			while (patters.hasNext())
				arraylist.add("textures/entity/banner/" + patters.next().getPatternName() + ".png");

			texture = new TimedBannerTexture();
			texture.texture = new ResourceLocation(s);
			Minecraft.getMinecraft().getTextureManager().loadTexture(texture.texture, new LayeredColorMaskTexture(BASE_TEXTURE, arraylist, list));
			CANVAS_TEXTURES.put(s, texture);
		}

		texture.time = System.currentTimeMillis();
		return texture.texture;
	}

	@SideOnly(Side.CLIENT)
	static class TimedBannerTexture {

		public long time;
		public ResourceLocation texture;

		TimedBannerTexture() {
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
		TileEntityBanner banner = (TileEntityBanner) tile;

		int meta = tile.getWorldObj() != null ? banner.getBlockMetadata() : 0;

		OpenGLHelper.pushMatrix();
		float f1 = 0.6666667F;

		if (banner.isStanding) {
			OpenGLHelper.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
			OpenGLHelper.rotate(-(meta * 360 / 16.0F), 0.0F, 1.0F, 0.0F);
			bannerModel.bannerStand.showModel = true;
		} else {
			float f3 = 0.0F;
			if (meta == 2)
				f3 = 180.0F;
			if (meta == 4)
				f3 = 90.0F;
			if (meta == 5)
				f3 = -90.0F;

			OpenGLHelper.translate((float) x + 0.5F, (float) y - 0.25F * f1, (float) z + 0.5F);
			OpenGLHelper.rotate(-f3, 0.0F, 1.0F, 0.0F);
			OpenGLHelper.translate(0.0F, -0.3125F, -0.4375F);
			bannerModel.bannerStand.showModel = false;
		}

		long worldTime = banner.getWorldObj() != null ? banner.getWorldObj().getTotalWorldTime() : 0;
		// 100 is one full revolution, so we can just mod by 100 without affecting the result
		int animationProgress100 = (((banner.xCoord % 100) * 7 + (banner.yCoord % 100) * 9 + (banner.zCoord % 100) * 13) + (int)(worldTime % 100)) % 100;
		float f3 = (float) animationProgress100 + partialTicks;
		bannerModel.bannerSlate.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(f3 * 0.01F * 2F * (float) Math.PI)) * (float) Math.PI;
		OpenGLHelper.enableRescaleNormal();
		ResourceLocation resourcelocation = getTexture(banner);

		if (resourcelocation != null) {
			bindTexture(resourcelocation);
			OpenGLHelper.pushMatrix();
			OpenGLHelper.scale(f1, -f1, -f1);
			bannerModel.renderAll();
			OpenGLHelper.popMatrix();
		}

		OpenGLHelper.colour(1.0F, 1.0F, 1.0F);
		OpenGLHelper.popMatrix();
	}
}