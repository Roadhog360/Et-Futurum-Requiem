package ganymedes01.etfuturum.client.renderer.tileentity;

import ganymedes01.etfuturum.client.model.ModelShulker;
import ganymedes01.etfuturum.core.utils.EtFuturumResources;
import ganymedes01.etfuturum.tileentities.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class TileEntityShulkerBoxRenderer extends TileEntityClearChestItemRenderer {
	private final ModelShulker modelShulker;

	public TileEntityShulkerBoxRenderer(ModelShulker p_i47216_1_) {
		super(key -> {
			if(key instanceof TileEntityShulkerBox box && box.type.isClear()) {
				return box.getTopItemStacks();
			}
			return null;
		});
		this.modelShulker = p_i47216_1_;
	}

	public void renderTileEntityAt(TileEntityShulkerBox te, double x, double y, double z, float partialTicks, int destroyStage) {
		ForgeDirection enumfacing = ForgeDirection.UP;

		if (te.hasWorldObj()) {
			int facing = te.facing;
			enumfacing = ForgeDirection.VALID_DIRECTIONS[facing];
		}

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

//        GlStateManager.enableDepth();
//        GlStateManager.depthFunc(515);
//        GlStateManager.depthMask(true);
//        GlStateManager.disableCull();
		boolean disableCull = te.getAnimationStatus() != TileEntityShulkerBox.AnimationStatus.CLOSED || te.type.isClear();
		if(disableCull) {
			GL11.glDisable(GL11.GL_CULL_FACE);
		}
		GL11.glEnable(GL11.GL_ALPHA_TEST); // Needed because the texture has transparent pixels

		if (destroyStage >= 0) {
			this.bindTexture(EtFuturumResources.DESTROY_STAGES[destroyStage]);
			GL11.glMatrixMode(GL11.GL_TEXTURE);
//            GL11.glPushMatrix();
			GL11.glScalef(4.0F, 4.0F, 1.0F);
			GL11.glTranslatef(0.0625F, 0.0625F, 0.0625F);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
		} else {
			this.bindTexture(te.type.getBoxTextures()[te.color % te.type.getBoxTextures().length]);
		}

		GL11.glPushMatrix();
//        GlStateManager.enableRescaleNormal();

		if (destroyStage < 0) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.0F, 1.0F, 0.0F);
		GL11.glScalef(0.9995F, 0.9995F, 0.9995F);
		GL11.glTranslatef(0.0F, -1.0F, 0.0F);

		switch (enumfacing) {
			case DOWN:
				GL11.glTranslatef(0.0F, 2.0F, 0.0F);
				GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

			case UP:
			default:
				break;

			case NORTH:
				GL11.glTranslatef(0.0F, 1.0F, 1.0F);
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
				break;

			case SOUTH:
				GL11.glTranslatef(0.0F, 1.0F, -1.0F);
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				break;

			case WEST:
				GL11.glTranslatef(-1.0F, 1.0F, 0.0F);
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				break;

			case EAST:
				GL11.glTranslatef(1.0F, 1.0F, 0.0F);
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
		}

		this.modelShulker.base.render(0.0625F);
		GL11.glTranslatef(0.0F, -te.getProgress(partialTicks) * 0.5F, 0.0F);
		GL11.glRotatef(270.0F * te.getProgress(partialTicks), 0.0F, 1.0F, 0.0F);
		this.modelShulker.lid.render(0.0625F);
//        GlStateManager.enableCull();
		if(disableCull) {
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
//        GlStateManager.disableRescaleNormal();
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (destroyStage >= 0) {
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
		}
		if (te.type.isClear()) {
			super.renderTileEntityAt(te, x, y, z, destroyStage);
		}

		GL11.glPopAttrib();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
		this.renderTileEntityAt((TileEntityShulkerBox) te, x, y, z, partialTicks, -1);
	}

}
