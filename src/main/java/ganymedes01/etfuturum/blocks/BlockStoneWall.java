package ganymedes01.etfuturum.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class BlockStoneWall extends BaseEFRWall {
	public BlockStoneWall() {
		super(Material.rock, "stone_bricks", "mossy_stone_bricks", "sandstone", "brick");
		setHardnessValues(Blocks.stonebrick.blockHardness, 0, 1);
		setResistanceValues(Blocks.stonebrick.blockResistance, 0, 1);
		setHardnessValues(Blocks.sandstone.blockHardness, 2);
		setResistanceValues(Blocks.sandstone.blockResistance, 2);
		setHardnessValues(Blocks.brick_block.blockHardness, 3);
		setResistanceValues(Blocks.brick_block.blockResistance, 3);
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		getIcons().clear();
		getIcons().put(0, Blocks.stonebrick.getIcon(0, 0));
		getIcons().put(1, Blocks.stonebrick.getIcon(0, 1));
		getIcons().put(2, Blocks.sandstone.getIcon(2, 0));
		getIcons().put(3, Blocks.brick_block.getIcon(0, 0));
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if ((meta % 8) == 2) {
			return Blocks.sandstone.getIcon(side, 0);
		}
		return super.getIcon(side, meta);
	}
}