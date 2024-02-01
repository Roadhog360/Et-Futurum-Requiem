package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class BlockGlazedTerracottaRenderer extends BlockModelBase {

	public BlockGlazedTerracottaRenderer(int modelID) {
		super(modelID);
	}

	protected void renderStandardInventoryCube(Block block, int meta, int modelID, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		renderer.uvRotateTop = 1;
		renderer.uvRotateNorth = 2;
		//renderer.uvRotateEast = 3;
		renderer.uvRotateSouth = 1;
		renderer.uvRotateWest = 3;
		renderer.uvRotateBottom = 1;

		super.renderStandardInventoryCube(block, meta, modelID, renderer, minX, minY, minZ, maxX, maxY, maxZ);

		renderer.uvRotateTop = 0;
		renderer.uvRotateNorth = 0;
		//renderer.uvRotateEast = 0;
		renderer.uvRotateSouth = 0;
		renderer.uvRotateWest = 0;
		renderer.uvRotateBottom = 0;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		int orient = world.getBlockMetadata(x, y, z); // Direction block is facing: 0=N, 1=E, 2=S, 3=W

		renderer.uvRotateTop = orient > 1 ? 5 - orient : orient; // Rotate top side (NO NEED FOR MIRROR)
		renderer.uvRotateNorth = orient > 0 ? (orient % 3) + 1 : 0; // Rotate west-facing side (NO NEED FOR MIRROR)
		renderer.uvRotateEast = orient < 2 ? 1 - orient : orient; // Rotate north-facing side (N&S orientations are flipped)
		int uvRotateSouthAndBottom = orient > 1 ? 2 * orient - 4 : 3 - 2 * orient;
		renderer.uvRotateSouth = uvRotateSouthAndBottom; // Rotate east-facing side (E&W orientations are flipped)
		renderer.uvRotateWest = orient > 1 ? 3 - orient : orient + 2; // Rotate south-facing side (NO NEED FOR MIRROR)
		renderer.uvRotateBottom = uvRotateSouthAndBottom; // (ALL orientations are rotated)

		boolean flag = renderer.renderStandardBlock(block, x, y, z);

		// Must reset the rotation or it will mess up all rotating blocks around
		renderer.uvRotateTop = 0;
		renderer.uvRotateNorth = 0;
		renderer.uvRotateEast = 0;
		renderer.uvRotateSouth = 0;
		renderer.uvRotateWest = 0;
		renderer.uvRotateBottom = 0;

		return flag;

	}
}
