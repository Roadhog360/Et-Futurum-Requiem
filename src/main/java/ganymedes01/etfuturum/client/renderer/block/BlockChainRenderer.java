package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class BlockChainRenderer extends BlockModelBase {
	public BlockChainRenderer(int modelID) {
		super(modelID);
		set2DInventory();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		IIcon iicon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : renderer.getBlockIcon(block);
		switch (world.getBlockMetadata(x, y, z)) {
			case 1:
				renderRawDoubleSidedFace(renderer, block, x, y, z, 0, 1, 0.4375D, 0.5625D, 0.5625D, 0.4375D, 0, 0, 3, 16, iicon, 1);
				renderRawDoubleSidedFace(renderer, block, x, y, z, 0, 1, 0.4375D, 0.5625D, 0.4375D, 0.5625D, 3, 0, 6, 16, iicon, 1);
				break;
			case 2:
				renderRawDoubleSidedFace(renderer, block, x, y, z, 0.4375D, 0.5625D, 0.5625D, 0.4375D, 0, 1, 0, 0, 3, 16, iicon, 2);
				renderRawDoubleSidedFace(renderer, block, x, y, z, 0.4375D, 0.5625D, 0.4375D, 0.5625D, 0, 1, 3, 0, 6, 16, iicon, 2);
				break;
			case 0:
			default:
				renderRawDoubleSidedFace(renderer, block, x, y, z, 0.4375D, 0.5625D, 0, 1, 0.5625D, 0.4375D, 0, 0, 3, 16, iicon);
				renderRawDoubleSidedFace(renderer, block, x, y, z, 0.4375D, 0.5625D, 0, 1, 0.4375D, 0.5625D, 3, 0, 6, 16, iicon);
				break;
		}
		return true;
	}
}
