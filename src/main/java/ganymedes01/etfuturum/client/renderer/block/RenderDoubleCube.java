package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class RenderDoubleCube extends RendererBaseOld {

	private final float innerSizeMax;
	private final float innerSizeMin;

	public RenderDoubleCube(int secondSize) {
		innerSizeMax = secondSize * 0.0625F;
		innerSizeMin = (16 - secondSize) * 0.0625F;
	}

	@Override
	protected void renderStandardInventoryCube(Block block, int meta, int modelID, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		super.renderStandardInventoryCube(block, meta, modelID, renderer, innerSizeMin, innerSizeMin, innerSizeMin, innerSizeMax, innerSizeMax, innerSizeMax);
		super.renderStandardInventoryCube(block, meta, modelID, renderer, minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(innerSizeMin, innerSizeMin, innerSizeMin, innerSizeMax, innerSizeMax, innerSizeMax);
		renderer.renderAllFaces = true;
		boolean flag = renderer.renderStandardBlock(block, x, y, z);
		renderer.renderAllFaces = false;
		return flag;
	}
}
