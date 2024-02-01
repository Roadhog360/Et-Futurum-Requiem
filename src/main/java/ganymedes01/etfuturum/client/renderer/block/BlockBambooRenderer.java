package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import ganymedes01.etfuturum.blocks.BlockBamboo;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class BlockBambooRenderer extends BlockModelBase {
	public BlockBambooRenderer(int modelID) {
		super(modelID);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		BlockBamboo bamboo = ((BlockBamboo) block);
		int meta = world.getBlockMetadata(x, y, z);

		Vec3 offsets = BlockBamboo.getOffset(x, z);
		int age = BlockBamboo.getStalkSize(meta);
		float stemSize = age == 0 ? 0.125F : 0.1875F;
		float sizeOffset = age == 0 ? 0.0625F : 0.09375F;
		float offsetX = (float) (offsets.xCoord - sizeOffset);
		float offsetZ = (float) (offsets.zCoord - sizeOffset);

		//Stalk top
		renderer.setRenderBounds(1 - stemSize, 1, 0, 1, 1, stemSize);
		renderFaceYPos(renderer, block, x, y, z, stemSize - (1 - offsetX), 0, offsetZ);

		int textureIndex = (int) Math.abs(Utils.hashPos(x, (int) (y * 2654435761L), z) % 4);
		float textureOffset = 0.0625F * (textureIndex * 3);
		float textureOffsetX = offsetX - textureOffset;
		float textureOffsetZ = offsetZ - textureOffset;
		//Stalk stem
		renderer.setRenderBounds(textureOffset, 0, textureOffset, stemSize + textureOffset, 1, stemSize + textureOffset);
		renderFaceXPos(renderer, block, x, y, z, textureOffsetX, 0, textureOffsetZ);
		renderFaceZPos(renderer, block, x, y, z, textureOffsetX, 0, textureOffsetZ);
		renderFaceXNeg(renderer, block, x, y, z, textureOffsetX, 0, textureOffsetZ);
		renderFaceZNeg(renderer, block, x, y, z, textureOffsetX, 0, textureOffsetZ);

		int leavesSize = BlockBamboo.getLeavesSize(meta);
		if (leavesSize > 0 && leavesSize < 3) {
			if (!renderer.hasOverrideBlockTexture()) {
				renderer.setOverrideBlockTexture(bamboo.leaves[leavesSize]);
			}
			drawStraightCrossedSquares(renderer, block, x, y, z, offsetX - 0.5F + sizeOffset, 0, offsetZ - 0.5F + sizeOffset);
			renderer.setOverrideBlockTexture(null);
		}

		//Debug code: Marks age 1 bamboo with stone and unused metas with a barrier icon
//		if(BlockBamboo.getStage(meta) == 1) {
//			renderer.setRenderBounds(0, 0, 0, 1, 0.003, 1);
//			renderer.renderStandardBlock(Blocks.stone, x, y, z);
//		}
//		if(leavesSize == 3) {
//			tessellator.setColorOpaque_F(1, 1, 1);
//			tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
//			renderer.drawCrossedSquares(ModBlocks.BARRIER.get().getIcon(0, 0), x, y, z, 1);
//		}
		return true;
	}

}
