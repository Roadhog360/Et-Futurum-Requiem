package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class BlockMangroveRootsRenderer extends BlockModelBase {
	public BlockMangroveRootsRenderer(int renderID) {
		super(renderID);
	}

	@Override
	protected void renderInventoryModel(Block block, int meta, int modelId, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		final Tessellator tessellator = Tessellator.instance;
		super.renderInventoryModel(block, meta, modelId, renderer, minX, minY, minZ, maxX, maxY, maxZ);
		boolean prev = renderer.renderFromInside;
		renderer.renderFromInside = true;
		super.renderInventoryModel(block, meta, modelId, renderer, minX, minY, minZ, maxX, maxY, maxZ);
		renderer.renderFromInside = prev;

		renderer.setRenderBounds(0.5, 0, 0, 0.5, 1, 1);
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));

		renderer.setRenderBounds(0, 0, 0.5, 1, 1, 0.5);
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (super.renderWorldBlock(world, x, y, z, block, modelId, renderer)) {
			boolean prev = renderer.renderAllFaces;
			renderer.renderAllFaces = true;
			drawStraightCrossedSquares(renderer, block, x, y, z);
			renderer.renderAllFaces = prev;
			return true;
		}
		return false;
	}
}
