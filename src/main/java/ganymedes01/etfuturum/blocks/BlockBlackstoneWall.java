package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;

public class BlockBlackstoneWall extends BaseEFRWall {
	public BlockBlackstoneWall() {
		super(Material.rock, "blackstone", "polished_blackstone", "polished_blackstone_bricks");
		setResistance(6.0F);
		setHardness(1.5F);
		setHardnessValues(2.0F, 1);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta == 0) {
			ModBlocks.BLACKSTONE.get().getIcon(side, 0);
		}
		return super.getIcon(side, meta);
	}
}