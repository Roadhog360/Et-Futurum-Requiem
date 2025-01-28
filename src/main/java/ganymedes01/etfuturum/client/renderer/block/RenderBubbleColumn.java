package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import ganymedes01.etfuturum.blocks.BlockBubbleColumn;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;

@ThreadSafeISBRH(perThread = false)
public class RenderBubbleColumn extends RendererBaseOld {
	public RenderBubbleColumn() {
		set2DInventory();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (ForgeHooksClient.getWorldRenderPass() == 1) {
			return renderer.renderBlockLiquid(Blocks.water, x, y, z);
		} else {
			boolean liquidOnTop = world.getBlock(x, y + 1, z).getMaterial().isLiquid();
			double topHeight = liquidOnTop ? 1 : 0.90625;
			renderer.setRenderBounds(0.0625, 0, 0.0625, 0.9375, topHeight, 0.9375);
			renderFaceXNeg(renderer, block, x, y, z);
			renderFaceXPos(renderer, block, x, y, z);
			renderFaceZNeg(renderer, block, x, y, z);
			renderFaceZPos(renderer, block, x, y, z);
			((BlockBubbleColumn) block).renderingInner.set(true);
			renderer.setRenderBounds(0.375, 0, 0.375, 0.625, topHeight, 0.625);
			renderFaceXNeg(renderer, block, x, y, z);
			renderFaceXPos(renderer, block, x, y, z);
			renderFaceZNeg(renderer, block, x, y, z);
			renderFaceZPos(renderer, block, x, y, z);
			((BlockBubbleColumn) block).renderingInner.set(false);
			if (!liquidOnTop) {
				renderer.setRenderBounds(0, 1, 0, 1, topHeight, 1);
				renderFaceYPos(renderer, block, x, y, z);
			}
		}
		return true;
	}
}
