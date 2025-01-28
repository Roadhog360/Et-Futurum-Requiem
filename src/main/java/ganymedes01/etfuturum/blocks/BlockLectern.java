package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockLectern extends BaseEFRBlock {
	public BlockLectern() {
		super(Material.wood);
		setBlockName("lectern");
		setBlockTextureName("lectern_sides");
		setHardness(2.5F);
		setResistance(2.5F);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
		worldIn.setBlockMetadataWithNotify(x, y, z, switch (MathHelper.floor_double((double) (placer.rotationYaw / 90.0F) + 0.5D) & 3) {
			case 1 -> 3;
			case 2 -> 1;
			case 3 -> 2;
			default -> 0;
		}, 2);
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.LECTERN.getRenderId();
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		reg.registerIcon("lectern_base");
		blockIcon = reg.registerIcon("lectern_sides");
		reg.registerIcon("lectern_top");
		reg.registerIcon("lectern_front");
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
