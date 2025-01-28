package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class RenderCauldronLava extends RenderCauldronBase {

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
		IIcon lava = BlockLiquid.getLiquidIcon("lava_still");
		renderer.renderFaceYPos(block, x, y - 1.0F + (0.9375F), z, lava);
		return true;
	}

}
