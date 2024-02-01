package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
@ThreadSafeISBRH(perThread = false)
public class BlockDoorRenderer extends BlockModelBase {

	public BlockDoorRenderer(int modelID) {
		super(modelID);
		set2DInventory();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);

		if ((meta & 8) != 0) {
			if (world.getBlock(x, y - 1, z) != block) {
				return false;
			}
		} else if (world.getBlock(x, y + 1, z) != block) {
			return false;
		}

		if ((meta & 8) != 0) {
			renderer.uvRotateTop = meta % 6;
		}
		boolean flag = renderer.renderStandardBlock(block, x, y, z);
		renderer.uvRotateTop = 0;
		return flag;
	}
}
