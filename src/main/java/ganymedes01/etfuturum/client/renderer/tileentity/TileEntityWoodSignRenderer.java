package ganymedes01.etfuturum.client.renderer.tileentity;

import ganymedes01.etfuturum.blocks.BlockWoodSign;
import ganymedes01.etfuturum.tileentities.TileEntityWoodSign;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityWoodSignRenderer extends TileEntitySpecialRenderer {

	/**
	 * The ModelSign instance for use in this renderer
	 */
	private final ModelSign model = new ModelSign();

	/*
	 * The following code is derived from Mojang's original sign code as I had no other choice as there was
	 * no way for me to add in my texture without copying most of the code to add my own code to this class.
	 * Extending the block class was not enough, since I had to copy this code anyways.
	 * I, in no way, take credit for the majority of this code. I only altered it to work with my blocks and
	 * textures.
	 */
	@Override
	public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_) {
		if (!(p_147500_1_.getBlockType() instanceof BlockWoodSign block))
			return;
		GL11.glPushMatrix();
		float f1 = 0.6666667F;
		float f3;

		if (block.standing) {
			GL11.glTranslatef((float) p_147500_2_ + 0.5F, (float) p_147500_4_ + 0.75F * f1, (float) p_147500_6_ + 0.5F);
			float f2 = p_147500_1_.getBlockMetadata() * 360 / 16.0F;
			GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
			this.model.signStick.showModel = true;
		} else {
			int j = p_147500_1_.getBlockMetadata();
			f3 = 0.0F;

			if (j == 2) {
				f3 = 180.0F;
			}

			if (j == 4) {
				f3 = 90.0F;
			}

			if (j == 5) {
				f3 = -90.0F;
			}

			GL11.glTranslatef((float) p_147500_2_ + 0.5F, (float) p_147500_4_ + 0.75F * f1, (float) p_147500_6_ + 0.5F);
			GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
			this.model.signStick.showModel = false;
		}
		bindTexture(new ResourceLocation(block.signTexture + ".png"));
		GL11.glPushMatrix();
		GL11.glScalef(f1, -f1, -f1);
		this.model.renderSign();
		GL11.glPopMatrix();
		FontRenderer fontrenderer = this.func_147498_b();
		f3 = 0.016666668F * f1;
		GL11.glTranslatef(0.0F, 0.5F * f1, 0.07F * f1);
		GL11.glScalef(f3, -f3, f3);
		GL11.glNormal3f(0.0F, 0.0F, -1.0F * f3);
		GL11.glDepthMask(false);
		byte b0 = 0;
		TileEntityWoodSign sign = (TileEntityWoodSign) p_147500_1_;
		for (int i = 0; i < sign.signText.length; ++i) {
			String colour = "";
//          if (signType(sign)) {
//              colour = "\u00A7f";
//          }
			String s = colour + sign.signText[i];

			if (i == sign.lineBeingEdited) {
				s = "> " + s + " <";
				fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, i * 10 - sign.signText.length * 5, b0);
			} else {
				fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, i * 10 - sign.signText.length * 5, b0);
			}
		}

		GL11.glDepthMask(true);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}
}