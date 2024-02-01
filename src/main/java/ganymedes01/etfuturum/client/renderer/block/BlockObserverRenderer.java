package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class BlockObserverRenderer extends BlockModelBase {

	public BlockObserverRenderer(int modelID) {
		super(modelID);
	}

	@Override
	protected void renderStandardInventoryCube(Block block, int meta, int modelID, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		setRotation(5, renderer);
		super.renderStandardInventoryCube(block, 5, modelID, renderer, minX, minY, minZ, maxX, maxY, maxZ);
		resetRotation(renderer);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);
		setRotation(meta, renderer);
		boolean flag = renderer.renderStandardBlock(block, x, y, z);
		resetRotation(renderer);
		return flag;
	}

	private void setRotation(int meta, RenderBlocks renderer) {
		int orientation = BlockPistonBase.getPistonOrientation(meta);
		switch (orientation) {
			case 2:
				renderer.uvRotateTop = 3;
				renderer.uvRotateBottom = 3;
				break;
			case 5:
				renderer.uvRotateTop = 2;
				renderer.uvRotateBottom = 1;
				break;
			case 4:
				renderer.uvRotateTop = 1;
				renderer.uvRotateBottom = 2;
				break;
			case 1:
				renderer.uvRotateNorth = 1;
				renderer.uvRotateSouth = 2;
				renderer.uvRotateWest = 3;
				renderer.uvRotateEast = 3;
				break;
			case 0:
				renderer.uvRotateNorth = 1;
				renderer.uvRotateSouth = 2;
				break;
		}
	}

	private void resetRotation(RenderBlocks renderer) {
		renderer.flipTexture = false;
		renderer.uvRotateNorth = 0; //west
		renderer.uvRotateSouth = 0; //east
		renderer.uvRotateWest = 0; //south
		renderer.uvRotateEast = 0; //north
		renderer.uvRotateTop = 0;
		renderer.uvRotateBottom = 0;
	}
}
