package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import ganymedes01.etfuturum.blocks.BlockNetherRoots;
import ganymedes01.etfuturum.blocks.BlockNetherSprouts;
import ganymedes01.etfuturum.lib.RenderIDs;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

/**
 * Basically the default crossed squares renderer (renderID 1) doesn't call getIcon(IBlockAccess world, int x, int y, int z, int side)
 * This means that we can't have world-sensitive rendering icons with it, so we do this.
 */
@ThreadSafeISBRH(perThread = false)
public class BlockExtendedCrossedSquaresRenderer extends BlockModelBase {
	public BlockExtendedCrossedSquaresRenderer() {
		super(RenderIDs.EXTENDED_CROSSED_SQUARES);
		set2DInventory();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		final Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		int l = block.colorMultiplier(world, x, y, z);
		float f = (float) (l >> 16 & 255) / 255.0F;
		float f1 = (float) (l >> 8 & 255) / 255.0F;
		float f2 = (float) (l & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable) {
			float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
			float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
			float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
			f = f3;
			f1 = f4;
			f2 = f5;
		}

		tessellator.setColorOpaque_F(f, f1, f2);
		double d1 = x;
		double d0 = z;
		long i1;

		if (block instanceof BlockNetherSprouts || block instanceof BlockNetherRoots) {
			i1 = (x * 3129871L) ^ (long) z * 116129781L ^ (long) y;
			i1 = i1 * i1 * 42317861L + i1 * 11L;
			d1 += ((double) ((float) (i1 >> 16 & 15L) / 15.0F) - 0.5D) * 0.3D;
			d0 += ((double) ((float) (i1 >> 24 & 15L) / 15.0F) - 0.5D) * 0.3D;
		}

		IIcon iicon = block.getIcon(world, x, y, z, 2);
		renderer.drawCrossedSquares(iicon, d1, y, d0, 1.0F);
		return true;
	}

}
