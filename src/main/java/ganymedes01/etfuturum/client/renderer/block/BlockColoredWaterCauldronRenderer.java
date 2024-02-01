package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockPotionCauldron;
import ganymedes01.etfuturum.tileentities.TileEntityCauldronColoredWater;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class BlockColoredWaterCauldronRenderer extends BlockCauldronBaseRenderer {

	public BlockColoredWaterCauldronRenderer(int modelID) {
		super(modelID);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
		final Tessellator tessellator = Tessellator.instance;

		TileEntityCauldronColoredWater tile = (TileEntityCauldronColoredWater) world.getTileEntity(x, y, z);
		int color = tile.getWaterColor();
		float r = (float) (color >> 16 & 255) / 255.0F;
		float g = (float) (color >> 8 & 255) / 255.0F;
		float b = (float) (color & 255) / 255.0F;
		tessellator.setColorOpaque_F(r, g, b);
		IIcon iicon = ((BlockPotionCauldron) ModBlocks.POTION_CAULDRON.get()).grayscaleWaterIcon();
		renderer.renderFaceYPos(block, x, (double) y - 1.0F + BlockCauldron.getRenderLiquidLevel(renderer.blockAccess.getBlockMetadata(x, y, z) + 1), z, iicon);

		return true;
	}

}
