package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import ganymedes01.etfuturum.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@ThreadSafeISBRH(perThread = false)
public class BlockTrapDoorRenderer extends BlockModelBase {

	public BlockTrapDoorRenderer(int modelID) {
		super(modelID);
	}

	@Override
	protected void renderStandardInventoryCube(Block block, int meta, int modelID, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		GL11.glTranslatef(0, 0.40625F, 0);
		renderer.uvRotateEast = 3;
		renderer.uvRotateWest = 3;
		renderer.uvRotateSouth = 3;
		renderer.uvRotateNorth = 3;
		super.renderStandardInventoryCube(block, meta, modelID, renderer, 0.0D, 0.0D, 0.0D, 1.0D, .1875D, 1.0F);
		renderer.uvRotateEast = 0;
		renderer.uvRotateWest = 0;
		renderer.uvRotateSouth = 0;
		renderer.uvRotateNorth = 0;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (block == ModBlocks.TRAPDOOR_DARK_OAK.get()) {
			return renderer.renderStandardBlock(block, x, y, z);
		}

		int l = renderer.blockAccess.getBlockMetadata(x, y, z);
		if (l <= 3) {
			renderer.uvRotateEast = 3;
			renderer.uvRotateWest = 3;
			renderer.uvRotateSouth = 3;
			renderer.uvRotateNorth = 3;
		} else if (l == 5 || l == 13) {
			renderer.uvRotateSouth = 1;
			renderer.uvRotateNorth = 2;
			if (l == 5) {
				renderer.uvRotateEast = 3;
				renderer.uvRotateWest = 3;
			}
		} else if (l == 4 || l == 12) {
			renderer.uvRotateSouth = 2;
			renderer.uvRotateNorth = 1;
			if (l == 4) {
				renderer.uvRotateEast = 3;
				renderer.uvRotateWest = 3;
			}
		} else if (l == 7 || l == 15) {
			renderer.uvRotateEast = 1;
			renderer.uvRotateWest = 2;
			if (l == 7) {
				renderer.uvRotateSouth = 3;
				renderer.uvRotateNorth = 3;
			}
		} else if (l == 6 || l == 14) {
			renderer.uvRotateEast = 2;
			renderer.uvRotateWest = 1;
			if (l == 6) {
				renderer.uvRotateSouth = 3;
				renderer.uvRotateNorth = 3;
			}
		}

		// 1 = west, 2 = east, 3 = north

		if (l == 0 || l == 4 || l == 8 || l == 12) {
			renderer.uvRotateTop = 3;
			renderer.uvRotateBottom = 3;
		} else if (l == 2 || l == 6 || l == 10 || l == 14) {
			renderer.uvRotateTop = 1;
			renderer.uvRotateBottom = 2;
		} else if (l == 3 || l == 7 || l == 11 || l == 15) {
			renderer.uvRotateTop = 2;
			renderer.uvRotateBottom = 1;
		}

		boolean flag = renderer.renderStandardBlock(block, x, y, z);
		renderer.uvRotateTop = 0;
		renderer.uvRotateBottom = 0;
		renderer.uvRotateEast = 0;
		renderer.uvRotateWest = 0;
		renderer.uvRotateSouth = 0;
		renderer.uvRotateNorth = 0;
		return flag;
	}
}
