package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import ganymedes01.etfuturum.blocks.BlockStonecutter;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class BlockStonecutterRenderer extends BlockModelBase {

	public BlockStonecutterRenderer(int modelID) {
		super(modelID);
	}

	protected void renderStandardInventoryCube(Block block, int meta, int modelID, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		super.renderStandardInventoryCube(block, meta, modelID, renderer, minX, minY, minZ, maxX, maxY, maxZ);
		renderer.setRenderBounds(0.0F, 0, 0.5F, 1.0F, 0.4375F, 0.5F);
		renderer.renderFaceZNeg(block, 0, .5625D, 0, ((BlockStonecutter) block).sawIcon);
		renderer.renderFaceZPos(block, 0, .5625D, 0, ((BlockStonecutter) block).sawIcon);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		int metadata = world.getBlockMetadata(x, y, z) % 4;

		switch (metadata) {
			case 1:
				renderer.uvRotateTop = 3;
				break;
			case 2:
				renderer.uvRotateTop = 2;
				break;
			case 3:
				renderer.uvRotateTop = 1;
				break;
			case 0:
			default:
		}

		renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, .5625F, 1.0F);
		renderer.renderStandardBlock(block, x, y, z);

		if (!renderer.hasOverrideBlockTexture()) {
			renderer.setOverrideBlockTexture(((BlockStonecutter) block).sawIcon);
		}
		renderer.setRenderAllFaces(true);

		if (metadata < 2) {
			renderer.setRenderBounds(0F, 0.0F, 0.5F, 1F, 0.4375F, 0.5F);
//          renderer.renderStandardBlock(block, x, y + 1, z);
			if (metadata == 1) {
				renderer.flipTexture = true;
			}
			renderFaceZNeg(renderer, block, x, y, z, 0, .5625D, 0);
			renderer.flipTexture = metadata == 0;
			renderFaceZPos(renderer, block, x, y, z, 0, .5625D, 0);
		} else {
			renderer.setRenderBounds(0.5F, 0.0F, 0F, 0.5F, 0.4375F, 1F);
			if (metadata == 3) {
				renderer.flipTexture = true;
			}
			renderFaceXNeg(renderer, block, x, y, z, 0, .5625D, 0);
			renderer.flipTexture = metadata == 2;
			renderFaceXPos(renderer, block, x, y, z, 0, .5625D, 0);
		}

		renderer.setRenderAllFaces(false);
		renderer.clearOverrideBlockTexture();
		renderer.flipTexture = false;
		renderer.uvRotateTop = 0;

		return true;
	}

}
