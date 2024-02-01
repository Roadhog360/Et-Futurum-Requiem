package ganymedes01.etfuturum.client.renderer.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import ganymedes01.etfuturum.client.OpenGLHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public abstract class BlockModelBase implements ISimpleBlockRenderingHandler {

	private final int modelID;
	private boolean inventory3D = true;

	protected BlockModelBase(int modelID) {
		this.modelID = modelID;
	}

	@Override
	public void renderInventoryBlock(Block block, int meta, int modelID, RenderBlocks renderer) {
		final Tessellator tessellator = Tessellator.instance;
		if (block.getRenderBlockPass() == 1) {
			OpenGLHelper.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			OpenGLHelper.enableBlend();
		}
		OpenGLHelper.translate(-0.5F, -0.5F, -0.5F);

		tessellator.startDrawingQuads();
		renderInventoryModel(block, meta, modelID, renderer, block.getBlockBoundsMinX(), block.getBlockBoundsMinY(), block.getBlockBoundsMinZ(), block.getBlockBoundsMaxX(), block.getBlockBoundsMaxY(), block.getBlockBoundsMaxZ());
		tessellator.draw();

		OpenGLHelper.translate(0.5F, 0.5F, 0.5F);
		OpenGLHelper.disableBlend();
	}

	protected void renderInventoryModel(Block block, int meta, int modelId, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		renderStandardInventoryCube(block, meta, modelID, renderer, block.getBlockBoundsMinX(), block.getBlockBoundsMinY(), block.getBlockBoundsMinZ(), block.getBlockBoundsMaxX(), block.getBlockBoundsMaxY(), block.getBlockBoundsMaxZ());
	}

	protected void renderStandardInventoryCube(Block block, int meta, int modelId, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		return renderStandardWorldCube(world, x, y, z, block, modelId, renderer, block.getBlockBoundsMinX(), block.getBlockBoundsMinY(), block.getBlockBoundsMinZ(), block.getBlockBoundsMaxX(), block.getBlockBoundsMaxY(), block.getBlockBoundsMaxZ());
	}

	protected boolean renderStandardWorldCube(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer, double minX, double minY, double minZ, double maxF, double maxY, double maxZ) {
		renderer.setRenderBounds(minX, minY, minZ, maxF, maxY, maxZ);
		return renderer.renderStandardBlock(block, x, y, z);
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return inventory3D;
	}

	@Override
	public int getRenderId() {
		return modelID;
	}

	public BlockModelBase set2DInventory() {
		inventory3D = false;
		return this;
	}

	public void drawStraightCrossedSquares(RenderBlocks renderer, Block block, double dx, double dy, double dz) {
		drawStraightCrossedSquares(renderer, block, dx, dy, dz, 0, 0, 0);
	}

	public void drawStraightCrossedSquares(RenderBlocks renderer, Block block, double dx, double dy, double dz, double offx, double offy, double offz) {
		double minX = renderer.renderMinX;
		double minY = renderer.renderMinY;
		double minZ = renderer.renderMinZ;
		double maxX = renderer.renderMaxX;
		double maxY = renderer.renderMaxY;
		double maxZ = renderer.renderMaxZ;

		renderer.setRenderBounds(0.5, 0, 0, 0.5, 1, 1);
		renderFaceXNeg(renderer, block, dx + offx, dy + offy, dz + offz);
		renderFaceXPos(renderer, block, dx + offx, dy + offy, dz + offz);

		renderer.setRenderBounds(0, 0, 0.5, 1, 1, 0.5);
		renderFaceZNeg(renderer, block, dx + offx, dy + offy, dz + offz);
		renderFaceZPos(renderer, block, dx + offx, dy + offy, dz + offz);

		renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public void renderRawFace(RenderBlocks renderer, Block block, double x, double y, double z,
							  double startX, double endX, double startY, double endY, double startZ, double endZ,
							  double startU, double startV, double endU, double endV, IIcon iicon,
							  float r, float g, float b) {
		renderRawFace(renderer, block, x, y, z, startX, endX, startY, endY, startZ, endZ, startU, startV, endU, endV, iicon, 0, r, g, b);
	}

	public void renderRawFace(RenderBlocks renderer, Block block, double x, double y, double z,
							  double startX, double endX, double startY, double endY, double startZ, double endZ,
							  double startU, double startV, double endU, double endV, IIcon iicon) {
		renderRawFace(renderer, block, x, y, z, startX, endX, startY, endY, startZ, endZ, startU, startV, endU, endV, iicon, 0);
	}

	public void renderRawFace(RenderBlocks renderer, Block block, double x, double y, double z,
							  double startX, double endX, double startY, double endY, double startZ, double endZ,
							  double startU, double startV, double endU, double endV, IIcon iicon, int rotate) {
		renderRawFace(renderer, block, x, y, z, startX, endX, startY, endY, startZ, endZ, startU, startV, endU, endV, iicon, rotate, 1, 1, 1);
	}

	public void renderRawFace(RenderBlocks renderer, Block block, double x, double y, double z,
							  double startX, double endX, double startY, double endY, double startZ, double endZ,
							  double startU, double startV, double endU, double endV, IIcon iicon, int rotate,
							  float r, float g, float b) {
		final Tessellator tessellator = Tessellator.instance;
		double startUInterpolated;
		double startVInterpolated;
		double endUInterpolated;
		double endVInterpolated;
		double startUInterpolated2;
		double startVInterpolated2;
		double endUInterpolated2;
		double endVInterpolated2;

		double endX1 = endX;
		double startX2 = startX;
		double endX2 = endX;
		double endZ1 = endZ;
		double startZ2 = startZ;
		double endZ2 = endZ;

		switch (rotate) {
			case 1:
				endUInterpolated2 = startUInterpolated = iicon.getInterpolatedU(startU);
				startVInterpolated2 = startVInterpolated = iicon.getInterpolatedV(startV);
				startUInterpolated2 = endUInterpolated = iicon.getInterpolatedU(endU);
				endVInterpolated2 = endVInterpolated = iicon.getInterpolatedV(endV);
				endZ2 = startZ;
				endZ1 = endZ;
				startZ2 = endZ;
				break;
			case 2:
				startUInterpolated2 = endUInterpolated = iicon.getInterpolatedU(startU);
				endVInterpolated2 = endVInterpolated = iicon.getInterpolatedV(startV);
				endUInterpolated2 = startUInterpolated = iicon.getInterpolatedU(endU);
				startVInterpolated2 = startVInterpolated = iicon.getInterpolatedV(endV);
				endX2 = startX;
				endX1 = endX;
				startX2 = endX;
				break;
			case 3:
				endUInterpolated2 = endUInterpolated = iicon.getInterpolatedU(endU);
				startVInterpolated2 = endVInterpolated = iicon.getInterpolatedV(endV);
				startUInterpolated2 = startUInterpolated = iicon.getInterpolatedU(startU);
				endVInterpolated2 = startVInterpolated = iicon.getInterpolatedV(startV);
				break;
			case 0:
			default:
				endUInterpolated2 = endUInterpolated = iicon.getInterpolatedU(startU);
				startVInterpolated2 = endVInterpolated = iicon.getInterpolatedV(startV);
				startUInterpolated2 = startUInterpolated = iicon.getInterpolatedU(endU);
				endVInterpolated2 = startVInterpolated = iicon.getInterpolatedV(endV);
				break;
		}

		tessellator.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess, MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z)));
		tessellator.setColorOpaque_F(r, g, b);
		tessellator.addVertexWithUV(x + startX, y + startY, z + startZ, startUInterpolated, startVInterpolated);
		tessellator.addVertexWithUV(x + startX2, y + endY, z + startZ2, startUInterpolated2, startVInterpolated2);
		tessellator.addVertexWithUV(x + endX1, y + endY, z + endZ1, endUInterpolated, endVInterpolated);
		tessellator.addVertexWithUV(x + endX2, y + startY, z + endZ2, endUInterpolated2, endVInterpolated2);
	}


	public void renderRawDoubleSidedFace(RenderBlocks renderer, Block block, double x, double y, double z,
										 double startX, double endX, double startY, double endY, double startZ, double endZ, double startU, double startV,
										 double endU, double endV, IIcon iicon, int rotate,
										 float r, float g, float b) {
		if (rotate == 1) {
			renderRawFace(renderer, block, x, y, z, startX, endX, endY, startY, startZ, endZ, startU, startV, endU, endV, iicon, 1, r, g, b);
			renderRawFace(renderer, block, x, y, z, endX, startX, endY, startY, startZ, endZ, endU, endV, startU, startV, iicon, 1);
		} else if (rotate == 2) {
			renderRawFace(renderer, block, x, y, z, startX, endX, endY, startY, startZ, endZ, startU, startV, endU, endV, iicon, 2, r, g, b);
			renderRawFace(renderer, block, x, y, z, startX, endX, endY, startY, endZ, startZ, endU, endV, startU, startV, iicon, 2, r, g, b);
		} else {
			renderRawFace(renderer, block, x, y, z, startX, endX, startY, endY, startZ, endZ, startU, startV, endU, endV, iicon, rotate, r, g, b);
			renderRawFace(renderer, block, x, y, z, endX, startX, startY, endY, endZ, startZ, startU, startV, endU, endV, iicon, rotate, r, g, b);
		}
	}

	public void renderRawDoubleSidedFace(RenderBlocks renderer, Block block, double x, double y, double z,
										 double startX, double endX, double startY, double endY, double startZ, double endZ, double startU, double startV,
										 double endU, double endV, IIcon iicon, int rotate) {
		renderRawDoubleSidedFace(renderer, block, x, y, z, startX, endX, startY, endY, startZ, endZ, startU, startV, endU, endV, iicon, rotate, 1, 1, 1);
	}

	public void renderRawDoubleSidedFace(RenderBlocks renderer, Block block, double x, double y, double z,
										 double startX, double endX, double startY, double endY, double startZ, double endZ,
										 double startU, double startV, double endU, double endV, IIcon iicon,
										 float r, float g, float b) {
		renderRawDoubleSidedFace(renderer, block, x, y, z, startX, endX, startY, endY, startZ, endZ, startU, startV, endU, endV, iicon, 0, r, g, b);
	}

	public void renderRawDoubleSidedFace(RenderBlocks renderer, Block block, double x, double y, double z, double startX, double endX, double startY, double endY, double startZ, double endZ, double startU, double startV, double endU, double endV, IIcon iicon) {
		renderRawDoubleSidedFace(renderer, block, x, y, z, startX, endX, startY, endY, startZ, endZ, startU, startV, endU, endV, iicon, 0);
	}

	/**
	 * Renders the YNeg face with proper shading like renderStandardBlock.
	 */
	public void renderFaceYNeg(RenderBlocks renderer, Block block, double dx, double dy, double dz) {
		this.renderFaceYNeg(renderer, block, dx, dy, dz, 0, 0, 0);
	}

	/**
	 * Renders the YNeg face with proper shading like renderStandardBlock.
	 */
	public void renderFaceYNeg(RenderBlocks renderer, Block block, double dx, double dy, double dz, double offx, double offy, double offz) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.enableAO = false;

		int x = MathHelper.floor_double(dx);
		int y = MathHelper.floor_double(dy);
		int z = MathHelper.floor_double(dz);

		float f3 = 0.5F;
		int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
		if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y - 1, z, 0)) {
			tessellator.setBrightness(renderer.renderMinY + Math.abs(offy) > 0.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x, MathHelper.floor_double(y - 1), z));
			tessellator.setColorOpaque_F(f3, f3, f3);
			renderer.renderFaceYNeg(block, dx + offx, dy + offy, dz + offz, renderer.getBlockIcon(block, renderer.blockAccess, x, y, z, 0));
		}
	}

	/**
	 * Renders the YPos face with proper shading like renderStandardBlock.
	 */
	public void renderFaceYPos(RenderBlocks renderer, Block block, double dx, double dy, double dz) {
		this.renderFaceYPos(renderer, block, dx, dy, dz, 0, 0, 0);
	}

	/**
	 * Renders the YPos face with proper shading like renderStandardBlock.
	 */
	public void renderFaceYPos(RenderBlocks renderer, Block block, double dx, double dy, double dz, double offx, double offy, double offz) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.enableAO = false;

		int x = MathHelper.floor_double(dx);
		int y = MathHelper.floor_double(dy);
		int z = MathHelper.floor_double(dz);

		int m = block.colorMultiplier(renderer.blockAccess, x, y, z);
		float f = (float) (m >> 16 & 255) / 255.0F;
		float f1 = (float) (m >> 8 & 255) / 255.0F;
		float f2 = (float) (m & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable) {
			float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
			float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
			float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
			f = f3;
			f1 = f4;
			f2 = f5;
		}

		float f4 = 1.0F;
		float f7 = f4 * f;
		float f8 = f4 * f1;
		float f9 = f4 * f2;
		int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);

		if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y + 1, z, 1)) {
			tessellator.setBrightness(renderer.renderMaxY - Math.abs(offy) < 1.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z));
			tessellator.setColorOpaque_F(f7, f8, f9);
			renderer.renderFaceYPos(block, dx + offx, dy + offy, dz + offz, renderer.getBlockIcon(block, renderer.blockAccess, x, y, z, 1));
		}
	}

	/**
	 * Renders the ZNeg face with proper shading like renderStandardBlock.
	 */
	public void renderFaceZNeg(RenderBlocks renderer, Block block, double dx, double dy, double dz) {
		this.renderFaceZNeg(renderer, block, dx, dy, dz, 0, 0, 0);
	}

	/**
	 * Renders the ZNeg face with proper shading like renderStandardBlock.
	 */
	public void renderFaceZNeg(RenderBlocks renderer, Block block, double dx, double dy, double dz, double offx, double offy, double offz) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.enableAO = false;

		int x = MathHelper.floor_double(dx);
		int y = MathHelper.floor_double(dy);
		int z = MathHelper.floor_double(dz);

		float f5 = 0.8F;
		int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);

		if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y, z - 1, 2)) {
			tessellator.setBrightness(renderer.renderMinZ + Math.abs(offz) > 0.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1));
			tessellator.setColorOpaque_F(f5, f5, f5);
			renderer.renderFaceZNeg(block, dx + offx, dy + offy, dz + offz, renderer.getBlockIcon(block, renderer.blockAccess, x, y, z, 2));
		}
	}

	/**
	 * Renders the ZPos face with proper shading like renderStandardBlock.
	 */
	public void renderFaceZPos(RenderBlocks renderer, Block block, double dx, double dy, double dz) {
		this.renderFaceZPos(renderer, block, dx, dy, dz, 0, 0, 0);
	}

	/**
	 * Renders the ZPos face with proper shading like renderStandardBlock.
	 */
	public void renderFaceZPos(RenderBlocks renderer, Block block, double dx, double dy, double dz, double offx, double offy, double offz) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.enableAO = false;

		int x = MathHelper.floor_double(dx);
		int y = MathHelper.floor_double(dy);
		int z = MathHelper.floor_double(dz);

		float f5 = 0.8F;
		int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);

		if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y, z + 1, 3)) {
			tessellator.setBrightness(renderer.renderMaxZ - Math.abs(offz) < 1.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1));
			tessellator.setColorOpaque_F(f5, f5, f5);
			renderer.renderFaceZPos(block, dx + offx, dy + offy, dz + offz, renderer.getBlockIcon(block, renderer.blockAccess, x, y, z, 3));
		}
	}

	/**
	 * Renders the XNeg face with proper shading like renderStandardBlock.
	 */
	public void renderFaceXNeg(RenderBlocks renderer, Block block, double dx, double dy, double dz) {
		this.renderFaceXNeg(renderer, block, dx, dy, dz, 0, 0, 0);
	}

	/**
	 * Renders the XNeg face with proper shading like renderStandardBlock.
	 */
	public void renderFaceXNeg(RenderBlocks renderer, Block block, double dx, double dy, double dz, double offx, double offy, double offz) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.enableAO = false;

		int x = MathHelper.floor_double(dx);
		int y = MathHelper.floor_double(dy);
		int z = MathHelper.floor_double(dz);

		float f6 = 0.6F;

		int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);

		if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x - 1, y, z, 4)) {
			tessellator.setBrightness(renderer.renderMinX + Math.abs(offx) > 0.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z));
			tessellator.setColorOpaque_F(f6, f6, f6);
			renderer.renderFaceXNeg(block, dx + offx, dy + offy, dz + offz, renderer.getBlockIcon(block, renderer.blockAccess, x, y, z, 4));
		}
	}

	/**
	 * Renders the XPos face with proper shading like renderStandardBlock.
	 */
	public void renderFaceXPos(RenderBlocks renderer, Block block, double dx, double dy, double dz) {
		this.renderFaceXPos(renderer, block, dx, dy, dz, 0, 0, 0);
	}

	/**
	 * Renders the XPos face with proper shading like renderStandardBlock.
	 */
	public void renderFaceXPos(RenderBlocks renderer, Block block, double dx, double dy, double dz, double offx, double offy, double offz) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.enableAO = false;

		int x = MathHelper.floor_double(dx);
		int y = MathHelper.floor_double(dy);
		int z = MathHelper.floor_double(dz);

		float f6 = 0.6F;

		int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);

		if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x + 1, y, z, 5)) {
			tessellator.setBrightness(renderer.renderMaxX - Math.abs(offx) < 1.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z));
			tessellator.setColorOpaque_F(f6, f6, f6);
			renderer.renderFaceXPos(block, dx + offx, dy + offy, dz + offz, renderer.getBlockIcon(block, renderer.blockAccess, x, y, z, 5));
		}
	}
}
