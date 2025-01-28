package ganymedes01.etfuturum.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockBlackstone extends BaseEFRBlock {
	private IIcon blackstoneTop;

	public BlockBlackstone() {
		super(Material.rock, "blackstone", "polished_blackstone", "polished_blackstone_bricks", "cracked_polished_blackstone_bricks", "chiseled_polished_blackstone");
		setResistance(6.0F);
		setHardness(1.5F);
		setHardnessValues(2.0F, 1);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta == 0 && side < 2) {
			return blackstoneTop;
		}
		return super.getIcon(side, meta);
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);
		blackstoneTop = reg.registerIcon("blackstone_top");
	}

	@Override
	public boolean isReplaceableOreGen(World world, int x, int y, int z, Block target) {
		return super.isReplaceableOreGen(world, x, y, z, target) || target == Blocks.netherrack;
	}
}