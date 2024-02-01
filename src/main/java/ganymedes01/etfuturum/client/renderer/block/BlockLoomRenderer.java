package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class BlockLoomRenderer extends BlockModelBase {

	public BlockLoomRenderer(int modelID) {
		super(modelID);
	}

	protected void renderStandardInventoryCube(Block block, int meta, int modelID, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		renderer.uvRotateTop = 1;
		super.renderStandardInventoryCube(block, 3, modelID, renderer, minX, minY, minZ, maxX, maxY, maxZ);
		renderer.uvRotateTop = 0;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
									RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 1) {
			meta = 3;
		} else if (meta == 3) {
			meta = 1;
		}
		renderer.uvRotateTop = meta;
		renderer.renderStandardBlock(block, x, y, z);
		renderer.uvRotateTop = 0;
		return true;
	}

}
