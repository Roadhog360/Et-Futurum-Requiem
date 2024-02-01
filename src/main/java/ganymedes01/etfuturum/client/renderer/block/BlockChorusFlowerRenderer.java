package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
@ThreadSafeISBRH(perThread = false)
public class BlockChorusFlowerRenderer extends BlockModelBase {

	public BlockChorusFlowerRenderer(int modelId) {
		super(modelId);
	}

	@Override
	protected void renderInventoryModel(Block block, int meta, int modelId, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		renderStandardInventoryCube(block, meta, modelId, renderer, .125D, .875D, .125D, .875D, 1, .875D);
		renderStandardInventoryCube(block, meta, modelId, renderer, 0, .125D, .125D, .125D, .875D, .875D);
		renderStandardInventoryCube(block, meta, modelId, renderer, .125D, .125D, 0, .875D, .875D, .125D);
		renderStandardInventoryCube(block, meta, modelId, renderer, .125D, .125D, .875D, .875D, .875D, 1);
		renderStandardInventoryCube(block, meta, modelId, renderer, .875D, .125D, .125D, 1, .875D, .875D);
		renderStandardInventoryCube(block, meta, modelId, renderer, .125D, 0, .125D, .875D, .875D, .875D);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		renderer.renderAllFaces = true;
		renderStandardWorldCube(world, x, y, z, block, modelId, renderer, .125D, .875D, .125D, .875D, 1, .875D);
		renderStandardWorldCube(world, x, y, z, block, modelId, renderer, 0, .125D, .125D, .125D, .875D, .875D);
		renderStandardWorldCube(world, x, y, z, block, modelId, renderer, .125D, .125D, 0, .875D, .875D, .125D);
		renderStandardWorldCube(world, x, y, z, block, modelId, renderer, .125D, .125D, .875D, .875D, .875D, 1);
		renderStandardWorldCube(world, x, y, z, block, modelId, renderer, .875D, .125D, .125D, 1, .875D, .875D);
		renderStandardWorldCube(world, x, y, z, block, modelId, renderer, .125D, 0, .125D, .875D, .875D, .875D);
		renderer.renderAllFaces = false;
		return true;
	}
}
