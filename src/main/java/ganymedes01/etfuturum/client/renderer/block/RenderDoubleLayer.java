package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import ganymedes01.etfuturum.blocks.IDoubleLayerBlock;
import ganymedes01.etfuturum.blocks.IEmissiveLayerBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class RenderDoubleLayer extends RendererBaseOld {

	public RenderDoubleLayer(int modelID) {
		super(modelID);
	}

	@Override
	protected void renderInventoryModel(Block block, int meta, int modelId, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		if (!(block instanceof IDoubleLayerBlock)) {
			throw new IllegalArgumentException("Block using double layer renderer must implement IDoubleLayerBlock!");
		}

		if (((IDoubleLayerBlock) block).isMetaNormalBlock(meta)) {
			super.renderInventoryModel(block, meta, modelId, renderer, minX, minY, minZ, maxX, maxY, maxZ);
			return;
		}

		boolean secondLayerAbove = ((IEmissiveLayerBlock) block).isSecondLayerAbove(meta);

		renderStandardInventoryCubeOrSecondLayer(block, meta, modelId, renderer, !secondLayerAbove, minX, minY, minZ, maxX, maxY, maxZ);
		renderStandardInventoryCubeOrSecondLayer(block, meta, modelId, renderer, secondLayerAbove, minX, minY, minZ, maxX, maxY, maxZ);
	}


	protected void renderStandardInventoryCubeOrSecondLayer(Block block, int meta, int modelId, RenderBlocks renderer, boolean secondLayer,
															double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, getIconOrSecondLayerIcon(block, renderer, 0, meta, secondLayer));
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, getIconOrSecondLayerIcon(block, renderer, 1, meta, secondLayer));
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, getIconOrSecondLayerIcon(block, renderer, 2, meta, secondLayer));
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, getIconOrSecondLayerIcon(block, renderer, 3, meta, secondLayer));
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, getIconOrSecondLayerIcon(block, renderer, 4, meta, secondLayer));
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, getIconOrSecondLayerIcon(block, renderer, 5, meta, secondLayer));
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (((IEmissiveLayerBlock) block).isMetaNormalBlock(world.getBlockMetadata(x, y, z))) {
			return super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
		}

		boolean emissiveLayerAbove = ((IEmissiveLayerBlock) block).isSecondLayerAbove(world.getBlockMetadata(x, y, z));

		return renderStandardWorldCubeWithEmissiveness(world, x, y, z, block, modelId, renderer, !emissiveLayerAbove,
				block.getBlockBoundsMinX(), block.getBlockBoundsMinY(), block.getBlockBoundsMinZ(),
				block.getBlockBoundsMaxX(), block.getBlockBoundsMaxY(), block.getBlockBoundsMaxZ()) &&

				renderStandardWorldCubeWithEmissiveness(world, x, y, z, block, modelId, renderer, emissiveLayerAbove,
						block.getBlockBoundsMinX(), block.getBlockBoundsMinY(), block.getBlockBoundsMinZ(),
						block.getBlockBoundsMaxX(), block.getBlockBoundsMaxY(), block.getBlockBoundsMaxZ());
	}

	protected boolean renderStandardWorldCubeWithEmissiveness(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer,
															  boolean secondLayer, double minX, double minY, double minZ, double maxF, double maxY, double maxZ) {
		if (!(block instanceof IEmissiveLayerBlock)) {
			throw new IllegalArgumentException("Block using emissive layer renderer must implement IEmissiveLayerBlock!");
		}

		if (!secondLayer) {
			return super.renderStandardWorldCube(world, x, y, z, block, modelId, renderer, minX, minY, minZ, maxF, maxY, maxZ);
		}

		renderer.setRenderBounds(minX, minY, minZ, maxF, maxY, maxZ);
		int meta = world.getBlockMetadata(x, y, z);
		renderFaceYNeg(renderer, block, x, y, z, 0, 0, 0, getIconOrSecondLayerIcon(block, renderer, 0, meta, true));
		renderFaceYPos(renderer, block, x, y, z, 0, 0, 0, getIconOrSecondLayerIcon(block, renderer, 1, meta, true));
		renderFaceZNeg(renderer, block, x, y, z, 0, 0, 0, getIconOrSecondLayerIcon(block, renderer, 2, meta, true));
		renderFaceZPos(renderer, block, x, y, z, 0, 0, 0, getIconOrSecondLayerIcon(block, renderer, 3, meta, true));
		renderFaceXNeg(renderer, block, x, y, z, 0, 0, 0, getIconOrSecondLayerIcon(block, renderer, 4, meta, true));
		renderFaceXPos(renderer, block, x, y, z, 0, 0, 0, getIconOrSecondLayerIcon(block, renderer, 5, meta, true));
		return true;
	}

	private IIcon getIconOrSecondLayerIcon(Block block, RenderBlocks renderer, int side, int meta, boolean secondLayer) {
		return secondLayer ? ((IEmissiveLayerBlock) block).getSecondLayerIcon(side, meta) : renderer.getBlockIconFromSideAndMetadata(block, side, meta);
	}
}
