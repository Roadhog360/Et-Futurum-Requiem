package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockComposter;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class BlockComposterRenderer extends BlockModelBase {

	public BlockComposterRenderer(int modelID) {
		super(modelID);
	}

	protected void renderStandardInventoryCube(Block block, int meta, int modelID, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		final Tessellator tessellator = Tessellator.instance;
		renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));

		renderer.setRenderBounds(0.875D, 0.125D, 0.125D, 0.125D, 1, 0.875D);
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceXPos(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceZPos(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));

		renderer.setRenderBounds(0, 0, 0, 1, 0.125D, 1);
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0, 0, 0, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		renderer.renderStandardBlock(block, x, y, z);

		if (block.shouldSideBeRendered(renderer.blockAccess, x, y + 1, z, 1)) {
			boolean prevRenderFaces = renderer.renderAllFaces;
			renderer.renderAllFaces = true;
			renderer.setRenderBounds(0.875D, 0.125D, 0.125D, 0.125D, 1, 0.875D);
			this.renderFaceZNeg(renderer, block, x, y, z);
			this.renderFaceXPos(renderer, block, x, y, z);
			this.renderFaceZPos(renderer, block, x, y, z);
			this.renderFaceXNeg(renderer, block, x, y, z);
			renderer.renderAllFaces = prevRenderFaces;

			int meta = Math.min(world.getBlockMetadata(x, y, z), 9);
			if (!renderer.hasOverrideBlockTexture()) {
				if (meta == 0) {
					renderer.setOverrideBlockTexture(ModBlocks.COMPOSTER.get().getIcon(0, 0));
				} else if (meta == BlockComposter.HARVESTABLE_META) {
					renderer.setOverrideBlockTexture(((BlockComposter) block).fullCompostIcon);
				} else {
					renderer.setOverrideBlockTexture(((BlockComposter) block).compostIcon);
				}

				double offset = 0.125D * Math.min(meta, BlockComposter.FULL_META);
				if (meta > 0) {
					offset += 0.0625D;
				}

				renderer.setRenderBounds(0, 0, 0, 1, offset, 1);
				this.renderFaceYPos(renderer, block, x, y, z);
			}

			renderer.clearOverrideBlockTexture();
		}

		return true;
	}
}
