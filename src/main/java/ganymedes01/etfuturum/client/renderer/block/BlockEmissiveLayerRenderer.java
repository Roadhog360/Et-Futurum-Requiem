package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import ganymedes01.etfuturum.blocks.IEmissiveLayerBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class BlockEmissiveLayerRenderer extends BlockModelBase {

	/**
	 * If the emissive layer should render above or below the main icon
	 */
	private final boolean emissiveLayerAbove;

	public BlockEmissiveLayerRenderer(int modelID, boolean above) {
		super(modelID);
		emissiveLayerAbove = above;
	}

	@Override
	protected void renderInventoryModel(Block block, int meta, int modelId, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		if (!(block instanceof IEmissiveLayerBlock)) {
			throw new IllegalArgumentException("Block using emissive layer renderer must implement IEmissiveLayerBlock!");
		}

		if (((IEmissiveLayerBlock) block).isMetaNormalBlock(meta)) {
			super.renderInventoryModel(block, meta, modelId, renderer, minX, minY, minZ, maxX, maxY, maxZ);
			return;
		}

		renderStandardInventoryCubeEmissive(block, meta, modelId, renderer, !emissiveLayerAbove, minX, minY, minZ, maxX, maxY, maxZ);
		renderStandardInventoryCubeEmissive(block, meta, modelId, renderer, emissiveLayerAbove, minX, minY, minZ, maxX, maxY, maxZ);
	}


	protected void renderStandardInventoryCubeEmissive(Block block, int meta, int modelId, RenderBlocks renderer, boolean emissive,
													   double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		final Tessellator tessellator = Tessellator.instance;
		if (emissive) {
			int m = ((IEmissiveLayerBlock) block).getEmissiveLayerColor(meta);
			float f = (float) (m >> 16 & 255) / 255.0F;
			float f1 = (float) (m >> 8 & 255) / 255.0F;
			float f2 = (float) (m & 255) / 255.0F;

			tessellator.setColorOpaque_F(f, f1, f2);
		}
		renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, getIconOrEmissiveLayerIcon(block, renderer, 0, meta, emissive));
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, getIconOrEmissiveLayerIcon(block, renderer, 1, meta, emissive));
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, getIconOrEmissiveLayerIcon(block, renderer, 2, meta, emissive));
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, getIconOrEmissiveLayerIcon(block, renderer, 3, meta, emissive));
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, getIconOrEmissiveLayerIcon(block, renderer, 4, meta, emissive));
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, getIconOrEmissiveLayerIcon(block, renderer, 5, meta, emissive));
		tessellator.setColorOpaque_F(1, 1, 1);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (((IEmissiveLayerBlock) block).isMetaNormalBlock(world.getBlockMetadata(x, y, z))) {
			return super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
		}

		return renderStandardWorldCubeWithEmissiveness(world, x, y, z, block, modelId, renderer, !emissiveLayerAbove,
				block.getBlockBoundsMinX(), block.getBlockBoundsMinY(), block.getBlockBoundsMinZ(),
				block.getBlockBoundsMaxX(), block.getBlockBoundsMaxY(), block.getBlockBoundsMaxZ()) &&

				renderStandardWorldCubeWithEmissiveness(world, x, y, z, block, modelId, renderer, emissiveLayerAbove,
						block.getBlockBoundsMinX(), block.getBlockBoundsMinY(), block.getBlockBoundsMinZ(),
						block.getBlockBoundsMaxX(), block.getBlockBoundsMaxY(), block.getBlockBoundsMaxZ());
	}

	protected boolean renderStandardWorldCubeWithEmissiveness(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer,
															  boolean emissive, double minX, double minY, double minZ, double maxF, double maxY, double maxZ) {
		if (!(block instanceof IEmissiveLayerBlock)) {
			throw new IllegalArgumentException("Block using emissive layer renderer must implement IEmissiveLayerBlock!");
		}

		if (!emissive) {
			return super.renderStandardWorldCube(world, x, y, z, block, modelId, renderer, minX, minY, minZ, maxF, maxY, maxZ);
		}

		renderer.setRenderBounds(minX, minY, minZ, maxF, maxY, maxZ);
		int meta = world.getBlockMetadata(x, y, z);
		renderFaceYNeg(renderer, block, x, y, z, ((IEmissiveLayerBlock) block).getEmissiveMinBrightness(world, x, y, z), getIconOrEmissiveLayerIcon(block, renderer, 0, meta, true), meta);
		renderFaceYPos(renderer, block, x, y, z, ((IEmissiveLayerBlock) block).getEmissiveMinBrightness(world, x, y, z), getIconOrEmissiveLayerIcon(block, renderer, 1, meta, true), meta);
		renderFaceZNeg(renderer, block, x, y, z, ((IEmissiveLayerBlock) block).getEmissiveMinBrightness(world, x, y, z), getIconOrEmissiveLayerIcon(block, renderer, 2, meta, true), meta);
		renderFaceZPos(renderer, block, x, y, z, ((IEmissiveLayerBlock) block).getEmissiveMinBrightness(world, x, y, z), getIconOrEmissiveLayerIcon(block, renderer, 3, meta, true), meta);
		renderFaceXNeg(renderer, block, x, y, z, ((IEmissiveLayerBlock) block).getEmissiveMinBrightness(world, x, y, z), getIconOrEmissiveLayerIcon(block, renderer, 4, meta, true), meta);
		renderFaceXPos(renderer, block, x, y, z, ((IEmissiveLayerBlock) block).getEmissiveMinBrightness(world, x, y, z), getIconOrEmissiveLayerIcon(block, renderer, 5, meta, true), meta);
		return true;
	}

	/**
	 * Renders the YNeg face with proper shading like renderStandardBlock.
	 */
	private void renderFaceYNeg(RenderBlocks renderer, Block block, double dx, double dy, double dz, int minBrightness, IIcon icon, int meta) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.enableAO = false;

		int x = MathHelper.floor_double(dx);
		int y = MathHelper.floor_double(dy);
		int z = MathHelper.floor_double(dz);

		int m = ((IEmissiveLayerBlock) block).getEmissiveLayerColor(meta);
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

		float f6 = ((IEmissiveLayerBlock) block).doesEmissiveLayerHaveDirShading(meta) ? 0.5F : 1.0F;
		float f7 = f6 * f;
		float f8 = f6 * f1;
		float f9 = f6 * f2;

		int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
		if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y - 1, z, 0)) {
			tessellator.setBrightness(floorMixedBrightness(minBrightness, renderer.renderMinY > 0.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x, MathHelper.floor_double(y - 1), z)));
			tessellator.setColorOpaque_F(f7, f8, f9);
			renderer.renderFaceYNeg(block, dx, dy, dz, icon);
		}
	}

	/**
	 * Renders the YPos face with proper shading like renderStandardBlock.
	 */
	private void renderFaceYPos(RenderBlocks renderer, Block block, double dx, double dy, double dz, int minBrightness, IIcon icon, int meta) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.enableAO = false;

		int x = MathHelper.floor_double(dx);
		int y = MathHelper.floor_double(dy);
		int z = MathHelper.floor_double(dz);

		int m = ((IEmissiveLayerBlock) block).getEmissiveLayerColor(meta);
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
			tessellator.setBrightness(floorMixedBrightness(minBrightness, renderer.renderMaxY < 1.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z)));
			tessellator.setColorOpaque_F(f7, f8, f9);
			renderer.renderFaceYPos(block, dx, dy, dz, icon);
		}
	}

	/**
	 * Renders the ZNeg face with proper shading like renderStandardBlock.
	 */
	private void renderFaceZNeg(RenderBlocks renderer, Block block, double dx, double dy, double dz, int minBrightness, IIcon icon, int meta) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.enableAO = false;

		int x = MathHelper.floor_double(dx);
		int y = MathHelper.floor_double(dy);
		int z = MathHelper.floor_double(dz);

		int m = ((IEmissiveLayerBlock) block).getEmissiveLayerColor(meta);
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

		float f6 = ((IEmissiveLayerBlock) block).doesEmissiveLayerHaveDirShading(meta) ? 0.8F : 1.0F;
		float f7 = f6 * f;
		float f8 = f6 * f1;
		float f9 = f6 * f2;

		int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);

		if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y, z - 1, 2)) {
			tessellator.setBrightness(floorMixedBrightness(minBrightness, renderer.renderMinZ > 0.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1)));
			tessellator.setColorOpaque_F(f7, f8, f9);
			renderer.renderFaceZNeg(block, dx, dy, dz, icon);
		}
	}

	/**
	 * Renders the ZPos face with proper shading like renderStandardBlock.
	 */
	private void renderFaceZPos(RenderBlocks renderer, Block block, double dx, double dy, double dz, int minBrightness, IIcon icon, int meta) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.enableAO = false;

		int x = MathHelper.floor_double(dx);
		int y = MathHelper.floor_double(dy);
		int z = MathHelper.floor_double(dz);

		int m = ((IEmissiveLayerBlock) block).getEmissiveLayerColor(meta);
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

		float f6 = ((IEmissiveLayerBlock) block).doesEmissiveLayerHaveDirShading(meta) ? 0.8F : 1.0F;
		float f7 = f6 * f;
		float f8 = f6 * f1;
		float f9 = f6 * f2;

		int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);

		if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y, z + 1, 3)) {
			tessellator.setBrightness(floorMixedBrightness(minBrightness, renderer.renderMaxZ < 1.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1)));
			tessellator.setColorOpaque_F(f7, f8, f9);
			renderer.renderFaceZPos(block, dx, dy, dz, icon);
		}
	}

	/**
	 * Renders the XNeg face with proper shading like renderStandardBlock.
	 */
	private void renderFaceXNeg(RenderBlocks renderer, Block block, double dx, double dy, double dz, int minBrightness, IIcon icon, int meta) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.enableAO = false;

		int x = MathHelper.floor_double(dx);
		int y = MathHelper.floor_double(dy);
		int z = MathHelper.floor_double(dz);

		int m = ((IEmissiveLayerBlock) block).getEmissiveLayerColor(meta);
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

		float f6 = ((IEmissiveLayerBlock) block).doesEmissiveLayerHaveDirShading(meta) ? 0.6F : 1.0F;
		float f7 = f6 * f;
		float f8 = f6 * f1;
		float f9 = f6 * f2;

		int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);

		if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x - 1, y, z, 4)) {
			tessellator.setBrightness(floorMixedBrightness(minBrightness, renderer.renderMinX > 0.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z)));
			tessellator.setColorOpaque_F(f7, f8, f9);
			renderer.renderFaceXNeg(block, dx, dy, dz, icon);
		}
	}

	/**
	 * Renders the XPos face with proper shading like renderStandardBlock.
	 */
	private void renderFaceXPos(RenderBlocks renderer, Block block, double dx, double dy, double dz, int minBrightness, IIcon icon, int meta) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.enableAO = false;

		int x = MathHelper.floor_double(dx);
		int y = MathHelper.floor_double(dy);
		int z = MathHelper.floor_double(dz);

		int m = ((IEmissiveLayerBlock) block).getEmissiveLayerColor(meta);
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

		float f6 = ((IEmissiveLayerBlock) block).doesEmissiveLayerHaveDirShading(meta) ? 0.6F : 1.0F;
		float f7 = f6 * f;
		float f8 = f6 * f1;
		float f9 = f6 * f2;

		int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);

		if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x + 1, y, z, 5)) {
			tessellator.setBrightness(floorMixedBrightness(minBrightness, renderer.renderMaxX < 1.0D ? l : block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z)));
			tessellator.setColorOpaque_F(f7, f8, f9);
			renderer.renderFaceXPos(block, dx, dy, dz, icon);
		}
	}

	private int floorMixedBrightness(int minBrightness, int brightness) {
		//Max value is 240 because 15 (max light value) * 16 = 240
		int sky = MathHelper.clamp_int(((brightness >>> 16) & 0xFF), minBrightness * 16, 240);
		int block = MathHelper.clamp_int(brightness & 0xFF, minBrightness * 16, 240);

		return ((sky & 0xFF) << 16) | (block & 0xFF);
	}

	private IIcon getIconOrEmissiveLayerIcon(Block block, RenderBlocks renderer, int side, int meta, boolean emissive) {
		return emissive ? ((IEmissiveLayerBlock) block).getEmissiveLayerIcon(side, meta) : renderer.getBlockIconFromSideAndMetadata(block, 1, meta);
	}
}
