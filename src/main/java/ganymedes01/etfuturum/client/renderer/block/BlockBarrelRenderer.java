package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class BlockBarrelRenderer extends BlockModelBase {

	public BlockBarrelRenderer(int modelID) {
		super(modelID);
	}

	protected void renderStandardInventoryCube(Block block, int meta, int modelID, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		super.renderStandardInventoryCube(block, 1, modelID, renderer, minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		int l = BlockPistonBase.getPistonOrientation(renderer.blockAccess.getBlockMetadata(x, y, z));

		switch (l) {
			case 0:
				renderer.uvRotateEast = 3;
				renderer.uvRotateWest = 3;
				renderer.uvRotateSouth = 3;
				renderer.uvRotateNorth = 3;
				break;
			case 1:
				break;
			case 2:
				renderer.uvRotateSouth = 1;
				renderer.uvRotateNorth = 2;
				renderer.uvRotateEast = 3;
				break;
			case 3:
				renderer.uvRotateSouth = 2;
				renderer.uvRotateNorth = 1;
				renderer.uvRotateWest = 3;
				renderer.uvRotateTop = 3;
				renderer.uvRotateBottom = 3;
				break;
			case 4:
				renderer.uvRotateEast = 1;
				renderer.uvRotateWest = 2;
				renderer.uvRotateNorth = 3;
				renderer.uvRotateTop = 2;
				renderer.uvRotateBottom = 1;
				break;
			case 5:
				renderer.uvRotateEast = 2;
				renderer.uvRotateWest = 1;
				renderer.uvRotateSouth = 3;
				renderer.uvRotateTop = 1;
				renderer.uvRotateBottom = 2;
				break;
		}

		boolean flag = renderer.renderStandardBlock(block, x, y, z);

		renderer.uvRotateEast = 0;
		renderer.uvRotateWest = 0;
		renderer.uvRotateSouth = 0;
		renderer.uvRotateNorth = 0;
		renderer.uvRotateTop = 0;
		renderer.uvRotateBottom = 0;

		return flag;
	}

}
