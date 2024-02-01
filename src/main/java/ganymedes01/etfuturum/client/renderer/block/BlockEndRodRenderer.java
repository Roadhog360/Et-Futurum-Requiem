package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.client.OpenGLHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
@ThreadSafeISBRH(perThread = false)
public class BlockEndRodRenderer extends BlockModelBase {

	public BlockEndRodRenderer(int modelID) {
		super(modelID);
	}

	@Override
	public void renderInventoryBlock(Block block, int meta, int modelID, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		OpenGLHelper.translate(-0.5F, -0.5F, -0.5F);

		double x = 0.4375D;
		double y = 0.0D;
		double z = 0.4375D;
		renderer.setRenderBounds(0, 0.0625D, 0, 0.125D, 1, 0.125D);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
		tessellator.draw();

		x = 0.25D;
		z = 0.25D;

		renderer.setRenderBounds(0.125D, 0, 0.125D, 0.375D, 0.0625D, 0.375D);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));

		y = -.5625D;

		renderer.setRenderBounds(0.125D, 0.5625, 0.125D, 0.375D, 0.625D, 0.375D);
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, x, y, z, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
		tessellator.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z) % 6;

		double x1 = 0;
		double y1 = 0;
		double z1 = 0;

		if (meta == 0 || meta == 1) {
			x1 = .4375D;
			y1 = meta == 0 ? -.0625D : 0;
			z1 = .4375D;
			renderer.setRenderBounds(0D, 0.0625D, 0D, 0.125D, 1D, 0.125D);
		} else if (meta == 2 || meta == 3) {
			x1 = -.4375D;
			y1 = .4375D;
			z1 = meta == 2 ? -.0625D : 0;
			renderer.uvRotateTop = 3;
			renderer.uvRotateBottom = 3;
			renderer.uvRotateEast = 2;
			renderer.uvRotateWest = 1;
			renderer.uvRotateNorth = 1;
			renderer.uvRotateSouth = 2;
			renderer.setRenderBounds(0.875D, 0, 0.0625D, 1, 0.125D, 1);
		} else if (meta == 4 || meta == 5) {
			x1 = meta == 4 ? -0.0625D : 0;
			y1 = .4375D;
			z1 = .4375D;
			renderer.uvRotateTop = 1;
			renderer.uvRotateBottom = 2;
			renderer.uvRotateEast = 2;
			renderer.uvRotateWest = 1;
			renderer.uvRotateNorth = 0;
			renderer.uvRotateSouth = 0;
			renderer.setRenderBounds(0.0625D, 0, 0, 1D, 0.125D, 0.125D);
		}

		if (meta == 0 || meta == 1) {
			renderFaceZNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceZPos(renderer, block, x, y, z, x1, y1, z1);
			renderFaceXNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceXPos(renderer, block, x, y, z, x1, y1, z1);
		} else if (meta == 2 || meta == 3) {
			renderFaceYNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceYPos(renderer, block, x, y, z, x1, y1, z1);
			renderFaceXNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceXPos(renderer, block, x, y, z, x1, y1, z1);
		} else if (meta == 4 || meta == 5) {
			renderFaceYNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceYPos(renderer, block, x, y, z, x1, y1, z1);
			renderFaceZNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceZPos(renderer, block, x, y, z, x1, y1, z1);
		}

		if (meta == 0 || meta == 1) {
			x1 = .3125D;
			y1 = meta == 0 ? -.0625D : 0;
			z1 = .4375D;
			renderer.setRenderBounds(.125D, 0.0625D, 0D, .25, 1D, .125);
		} else if (meta == 2 || meta == 3) {
			x1 = -.4375D;
			y1 = .3125D;
			z1 = meta == 2 ? -.0625D : 0;
			renderer.setRenderBounds(0.875D, 0.125D, 0.0625D, 1, 0.25D, 1);
		} else if (meta == 4 || meta == 5) {
			x1 = meta == 4 ? -0.0625D : 0;
			y1 = -.4375D;
			z1 = .3125D;
			renderer.setRenderBounds(0.0625D, 0.875D, 0.125D, 1D, 1, 0.25D);
		}

		if (meta == 0 || meta == 1) {
			renderFaceYNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceYPos(renderer, block, x, y, z, x1, y1, z1);
		} else if (meta == 2 || meta == 3) {
			renderFaceZNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceZPos(renderer, block, x, y, z, x1, y1, z1);
		} else if (meta == 4 || meta == 5) {
			renderFaceXNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceXPos(renderer, block, x, y, z, x1, y1, z1);
		}

		x1 = 0;
		y1 = 0;
		z1 = 0;
		renderer.uvRotateTop = 0;
		renderer.uvRotateBottom = 0;
		renderer.uvRotateEast = 0;
		renderer.uvRotateWest = 0;
		renderer.uvRotateNorth = 0;
		renderer.uvRotateSouth = 0;

		if (meta == 0 || meta == 1) {
			x1 = .25;
			y1 = meta == 0 ? .9375 : 0;
			z1 = .25;
			renderer.setRenderBounds(0.125D, 0, 0.125D, 0.375D, 0.0625D, 0.375D);
		} else if (meta == 2 || meta == 3) {
			x1 = .25;
			y1 = -.25;
			z1 = meta == 2 ? .9375 : 0;
			renderer.setRenderBounds(0.125D, 0.625D, 0, .375, .875, .0625);
		} else if (meta == 4 || meta == 5) {
			x1 = meta == 4 ? 0 : -.9375;
			y1 = -.25;
			z1 = .25;
			renderer.uvRotateNorth = 2;
			renderer.uvRotateSouth = 1;
			renderer.setRenderBounds(0.9375, 0.625D, 0.125D, 1, .875, .375);
		}

		renderer.renderAllFaces = true;
		if (meta == 0 || meta == 1) {
			renderFaceYNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceYPos(renderer, block, x, y, z, x1, y1, z1);
		} else if (meta == 2 || meta == 3) {
			renderFaceZNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceZPos(renderer, block, x, y, z, x1, y1, z1);
		} else if (meta == 4 || meta == 5) {
			renderFaceXNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceXPos(renderer, block, x, y, z, x1, y1, z1);
		}
		renderer.renderAllFaces = false;

		x1 = 0;
		y1 = 0;
		z1 = 0;
		renderer.uvRotateTop = 0;
		renderer.uvRotateBottom = 0;
		renderer.uvRotateEast = 0;
		renderer.uvRotateWest = 0;
		renderer.uvRotateNorth = 0;
		renderer.uvRotateSouth = 0;

		if (meta == 0 || meta == 1) {
			x1 = .25;
			y1 = meta == 0 ? .375 : -.5625;
			z1 = .25;
			renderer.setRenderBounds(0.125D, 0.5625, 0.125D, 0.375D, 0.625D, 0.375D);
		} else if (meta == 2 || meta == 3) {
			x1 = -.25;
			y1 = .25;
			z1 = meta == 2 ? .375 : -.5625;
			renderer.uvRotateTop = 3;
			renderer.uvRotateBottom = 3;
			renderer.uvRotateNorth = 1;
			renderer.uvRotateSouth = 2;
			renderer.setRenderBounds(0.625, .125, .5625, .875, .375, .625);
		} else if (meta == 4 || meta == 5) {
			x1 = meta == 4 ? .375 : -.5625;
			y1 = .25;
			z1 = .25;
			renderer.uvRotateTop = 1;
			renderer.uvRotateBottom = 2;
			renderer.uvRotateEast = 2;
			renderer.uvRotateWest = 1;
			renderer.setRenderBounds(0.5625, 0.125, 0.125, 0.625, 0.375, 0.375);
		}

		if (meta == 0 || meta == 1) {
			renderFaceZNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceZPos(renderer, block, x, y, z, x1, y1, z1);
			renderFaceXNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceXPos(renderer, block, x, y, z, x1, y1, z1);
		} else if (meta == 2 || meta == 3) {
			renderFaceYNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceYPos(renderer, block, x, y, z, x1, y1, z1);
			renderFaceXNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceXPos(renderer, block, x, y, z, x1, y1, z1);
		} else if (meta == 4 || meta == 5) {
			renderFaceYNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceYPos(renderer, block, x, y, z, x1, y1, z1);
			renderFaceZNeg(renderer, block, x, y, z, x1, y1, z1);
			renderFaceZPos(renderer, block, x, y, z, x1, y1, z1);
		}


		renderer.uvRotateBottom = 0;
		renderer.uvRotateTop = 0;
		renderer.uvRotateEast = 0;
		renderer.uvRotateWest = 0;
		renderer.uvRotateNorth = 0;
		renderer.uvRotateSouth = 0;

		return true;
	}
}
