package ganymedes01.etfuturum.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.client.model.ModelHead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class TileEntityFancySkullRenderer extends TileEntitySpecialRenderer {

	private static final ResourceLocation skeleton_texture = new ResourceLocation("textures/entity/skeleton/skeleton.png");
	private static final ResourceLocation wither_skeleton_texture = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
	private static final ResourceLocation zombie_texture = new ResourceLocation("textures/entity/zombie/zombie.png");
	private static final ResourceLocation creeper_texture = new ResourceLocation("textures/entity/creeper/creeper.png");
	private static final ResourceLocation dragon_texture = new ResourceLocation("textures/entity/enderdragon/dragon.png");

	public static TileEntityFancySkullRenderer instance;

	private final ModelHead model1 = new ModelHead(32);
	private final ModelHead model2 = new ModelHead(64);
	private final ganymedes01.etfuturum.client.model.ModelDragonHead modelDragon = new ganymedes01.etfuturum.client.model.ModelDragonHead(0.0F);

	@Override
	public void func_147497_a(TileEntityRendererDispatcher dispatcher) {
		super.func_147497_a(dispatcher);
		instance = this;
	}

	public static net.minecraft.entity.EntityLivingBase currentRenderEntity;
	public static float currentPartialTicks;

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTick) {
		TileEntitySkull skull = (TileEntitySkull) tile;
		float animateTicks = 0.0F;
		if (skull.func_145904_a() == 5 && skull instanceof ganymedes01.etfuturum.client.renderer.tileentity.IDragonHeadAnimator) {
			animateTicks = ((ganymedes01.etfuturum.client.renderer.tileentity.IDragonHeadAnimator) skull).getAnimationProgress(partialTick);
		}
		renderSkull((float) x, (float) y, (float) z, tile.getBlockMetadata() & 7, skull.func_145906_b() * 360 / 16.0F, skull.func_145904_a(), skull.func_152108_a(), animateTicks);
	}

	public void renderSkull(float x, float y, float z, int meta, float rotation, int type, GameProfile profile) {
		renderSkull(x, y, z, meta, rotation, type, profile, 0.0F);
	}

	public void renderSkull(float x, float y, float z, int meta, float rotation, int type, GameProfile profile, float animateTicks) {
		ModelHead model = model1;

		switch (type) {
			case 0:
			default:
				bindTexture(skeleton_texture);
				break;
			case 1:
				bindTexture(wither_skeleton_texture);
				break;
			case 2:
				bindTexture(zombie_texture);
				model = model2;
				break;
			case 3:
				ResourceLocation texture = AbstractClientPlayer.locationStevePng;
				if (profile != null) {
					Minecraft minecraft = Minecraft.getMinecraft();
					Map<Type, MinecraftProfileTexture> map = minecraft.func_152342_ad().func_152788_a(profile);
					if (map.containsKey(Type.SKIN))
						texture = minecraft.func_152342_ad().func_152792_a(map.get(Type.SKIN), Type.SKIN);
				}
				bindTexture(texture);
				break;
			case 4:
				bindTexture(creeper_texture);
				break;
			case 5:
				bindTexture(dragon_texture);
				break;
		}

		OpenGLHelper.pushMatrix();
		OpenGLHelper.disableCull();

		if (meta != 1)
			switch (meta) {
				case 2:
					OpenGLHelper.translate(x + 0.5F, y + 0.25F, z + 0.74F);
					break;
				case 3:
					OpenGLHelper.translate(x + 0.5F, y + 0.25F, z + 0.26F);
					rotation = 180.0F;
					break;
				case 4:
					OpenGLHelper.translate(x + 0.74F, y + 0.25F, z + 0.5F);
					rotation = 270.0F;
					break;
				case 5:
				default:
					OpenGLHelper.translate(x + 0.26F, y + 0.25F, z + 0.5F);
					rotation = 90.0F;
			}
		else
			GL11.glTranslatef(x + 0.5F, y, z + 0.5F);

		OpenGLHelper.enableRescaleNormal();
		OpenGLHelper.scale(-1.0F, -1.0F, 1.0F);
		OpenGLHelper.enableAlpha();
		
		if (type == 5) {
			modelDragon.render(0.0F, rotation, animateTicks);
		} else {
			model.render(rotation);
		}
		
		OpenGLHelper.popMatrix();
	}

	public void renderWornDragonHead(float x, float y, float z, int direction, float rotation, GameProfile profile) {
		bindTexture(dragon_texture);
		
		OpenGLHelper.pushMatrix();
		OpenGLHelper.disableCull();

		// In 1.7.10 RenderBiped scales the skull by 1.0625F.
		// Vanilla 1.10 scales dragon head by 1.1875F. 
		// We invert the 1.0625F scale and apply 1.1875F scale so it matches vanilla size perfectly.
		float unscale = 1.0F / 1.0625F;
		OpenGLHelper.scale(unscale, unscale, unscale);
		OpenGLHelper.scale(1.1875F, -1.1875F, -1.1875F);
		
		// The original code applied translation if it was a zombie villager, but we can't easily detect that here,
		// and most mod packs don't have zombie villagers wearing hats, so we leave it standard.

		float limbSwing = 0.0F;
		if (currentRenderEntity != null) {
			// Interpolate limb swing.
			// limbSwing = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks)
			limbSwing = currentRenderEntity.limbSwing - currentRenderEntity.limbSwingAmount * (1.0F - currentPartialTicks);
		}

		OpenGLHelper.enableRescaleNormal();
		OpenGLHelper.enableAlpha();
		
		// Call render with the limb swing for jaw bobbing
		modelDragon.render(0.0F, 0.0F, limbSwing);
		
		OpenGLHelper.popMatrix();
	}
}