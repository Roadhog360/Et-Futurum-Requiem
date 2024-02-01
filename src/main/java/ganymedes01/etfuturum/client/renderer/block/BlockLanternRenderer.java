package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class BlockLanternRenderer extends BlockModelBase {

	public BlockLanternRenderer(int modelID) {
		super(modelID);
		set2DInventory();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		int i = world.getBlockMetadata(x, y, z);
		float yoffset = i % 2 == 0 ? 0 : 0.0625F;
		renderer.setRenderBounds(0, .4375F, 0, .375F, .875F, .375F);
		renderFaceXNeg(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
		renderFaceXPos(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
		renderFaceZNeg(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
		renderFaceZPos(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);

		renderer.uvRotateTop = 1;
		renderer.uvRotateBottom = 2;
		renderer.setRenderBounds(.0625F, 0, 0, .4375F, .4375F, .375F);
		renderFaceYPos(renderer, block, x, y, z, .25F, yoffset, .3125F);
		renderFaceYNeg(renderer, block, x, y, z, .25F, yoffset, .3125F);
		renderer.setRenderBounds(.125F, .5625F, .0625F, .375F, .5625F, .3125F);
		renderFaceYPos(renderer, block, x, y, z, .25F, yoffset, .3125F);
		renderer.uvRotateBottom = 0;
		renderer.uvRotateTop = 0;

		renderer.setRenderBounds(.0625F, .875F, .0625F, .3125F, 1, .3125F);
		renderFaceXNeg(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
		renderFaceXPos(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
		renderFaceZNeg(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
		renderFaceZPos(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);

		IIcon iicon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : renderer.getBlockIcon(block);
		//Lantern chain
		if (world.getBlockMetadata(x, y, z) == 0) {
			//If meta is 0, there are two crossed chain links on the top of the lantern
			renderRawDoubleSidedFace(renderer, block, x, y, z, 0.4375D, 0.5625D, 0.5625D, 0.6875D, 0.4375D, 0.5625D, 11, 10, 14, 12, iicon);
			renderRawDoubleSidedFace(renderer, block, x, y, z, 0.4375D, 0.5625D, 0.5625D, 0.6875D, 0.5625D, 0.4375D, 11, 10, 14, 12, iicon);
		} else {
			//If meta is not 0, there's only one chain link on the lantern itself, followed by two links attached above
			renderRawDoubleSidedFace(renderer, block, x, y, z, 0.4375D, 0.5625D, 0.625D, 0.75D, 0.4375D, 0.5625D, 11, 10, 14, 12, iicon);
			renderRawDoubleSidedFace(renderer, block, x, y, z, 0.5625D, 0.4375D, 0.6875D, 0.9375D, 0.4375D, 0.5625D, 11, 1, 14, 5, iicon);
			renderRawDoubleSidedFace(renderer, block, x, y, z, 0.4375D, 0.5625D, 0.875D, 1D, 0.4375D, 0.5625D, 11, 6, 14, 8, iicon);
		}

		return true;
	}

}
