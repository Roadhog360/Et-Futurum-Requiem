package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockLoom extends Block {

	private IIcon topIcon;
	private IIcon sideIcon;
	private IIcon bottomIcon;

	public BlockLoom() {
		super(Material.wood);
		this.setStepSound(soundTypeWood);
		this.setHardness(2.5F);
		this.setHarvestLevel("axe", 0);
		this.setResistance(2.5F);
		this.setBlockName("loom");
		this.setBlockTextureName("loom");
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
		int ordinal = MathHelper.floor_double((double) (placer.rotationYaw / 90.0F) + 0.5D) & 3;
		switch (ordinal) {
			case 1:
				ordinal = 3;
				break;
			case 2:
				ordinal = 1;
				break;
			case 3:
				ordinal = 2;
				break;
		}

		worldIn.setBlockMetadataWithNotify(x, y, z, ordinal, 2);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? this.topIcon : (side == 0 ? this.bottomIcon : (side != meta + 2 ? this.sideIcon : this.blockIcon));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.blockIcon = reg.registerIcon(this.getTextureName() + "_front");
		this.topIcon = reg.registerIcon(this.getTextureName() + "_top");
		this.sideIcon = reg.registerIcon(this.getTextureName() + "_side");
		this.bottomIcon = reg.registerIcon(this.getTextureName() + "_bottom");
	}

	@Override
	public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return true;
	}

	@Override
	public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return 5;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return 20;
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.LOOM.getRenderId();
	}

}