package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;

public class BlockBlackstoneSlab extends BaseEFRSlab {
	public BlockBlackstoneSlab(boolean isDouble) {
		super(isDouble, Material.rock, "blackstone", "polished_blackstone", "polished_blackstone_bricks");
		setHardness(2.0F);
		setResistance(6.0F);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta % 8 == 0 && side < 2) {
			return ModBlocks.BLACKSTONE.get().getIcon(1, 0);
		}
		return super.getIcon(side, meta);
	}
}
