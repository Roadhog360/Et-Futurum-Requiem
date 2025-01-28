package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.configuration.configs.ConfigTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStonecutter extends Block {

	private IIcon sideIcon;
	private IIcon bottomIcon;
	public IIcon sawIcon;

	public BlockStonecutter() {
		super(Material.rock);
		this.setHarvestLevel("pickaxe", 0);
		this.setStepSound(soundTypeStone);
		this.setHardness(3.5F);
		this.setResistance(3.5F);
		this.setBlockName("stonecutter");
		this.setBlockTextureName("stonecutter");
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
		this.setLightOpacity(0);
		this.useNeighborBrightness = true;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? this.blockIcon : side == 0 ? bottomIcon : this.sideIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.blockIcon = reg.registerIcon(this.getTextureName() + "_top");
		this.sideIcon = reg.registerIcon(this.getTextureName() + "_side");
		this.bottomIcon = reg.registerIcon(this.getTextureName() + "_bottom");
		this.sawIcon = reg.registerIcon(this.getTextureName() + "_saw");
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
	public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
		Block block = worldIn.getBlock(x, y, z);
		if (block instanceof BlockStonecutter && side > 1) {
			return false;
		}
		return super.shouldSideBeRendered(worldIn, x, y, z, side);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.STONECUTTER.getRenderId();
	}

	//NON-VANILLA TWEAK CODE

	public static final DamageSource STONECUTTER = (new DamageSource("stonecutter")).setFireDamage();

	@Override
	public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
		if (!worldIn.isRemote && ConfigTweaks.stonecutterSawHurts) {
			entityIn.attackEntityFrom(STONECUTTER, 1);
		}
		super.onEntityCollidedWithBlock(worldIn, x, y, z, entityIn);
	}

}