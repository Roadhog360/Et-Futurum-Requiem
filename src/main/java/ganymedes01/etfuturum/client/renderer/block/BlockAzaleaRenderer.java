package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
@ThreadSafeISBRH(perThread = false)
public class BlockAzaleaRenderer extends BlockModelBase {

	public BlockAzaleaRenderer(int modelID) {
		super(modelID);
	}

	protected void renderInventoryModel(Block block, int meta, int modelId, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		final Tessellator tessellator = Tessellator.instance;
		//We have to render each side manually because the bottom side gets rendered even though we set shouldSideBeRendered to false on side 0 (bottom)
		renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
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
		renderer.drawCrossedSquares(block.getBlockTextureFromSide(0), 0, 0, 0, 1.0F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		//We don't need to cull the bottom (0) face because we do that in the block class
		boolean prev = renderer.renderFromInside;
		renderer.renderFromInside = true;
		renderStandardWorldCube(world, x, y, z, block, modelId, renderer, 0, 0, 0, 1, 1, 1);
		renderer.renderFromInside = prev;
		renderer.drawCrossedSquares(block.getBlockTextureFromSide(0), x, y, z, 1.0F);
		return renderStandardWorldCube(world, x, y, z, block, modelId, renderer, 0, 0, 0, 1, 1, 1);
	}

}
