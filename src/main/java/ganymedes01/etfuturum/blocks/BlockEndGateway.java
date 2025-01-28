package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.tileentities.TileEntityGateway;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockEndGateway extends BlockContainer {

	public BlockEndGateway() {
		super(Material.portal);
		this.setLightLevel(1);
		this.setResistance(3600000);
		this.setBlockUnbreakable();
		this.setBlockName("end_gateway");
		this.setCreativeTab(EtFuturum.creativeTabBlocks);

		Blocks.end_portal.setBlockName("end_portal");
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(Blocks.end_portal, 1, 0));
		list.add(new ItemStack(itemIn, 1, 0));
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
		return null;
	}

	public boolean canUpdate() {
		return true;
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		return !(entity instanceof EntityDragon) && !(entity instanceof EntityWither);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityGateway();
	}

	@Override
	public String getItemIconName() {
		return "end_gateway";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.blockIcon = Blocks.obsidian.getIcon(0, 0);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		EnumFacing facing = EnumFacing.getFront(side);
		Block block = world.getBlock(x + facing.getFrontOffsetX(), y + facing.getFrontOffsetY(), z + facing.getFrontOffsetZ());
		return !block.isOpaqueCube() && block != this;
	}

	@Override
	public void randomDisplayTick(World worldIn, int x, int y, int z, Random rand) {

		TileEntity tileentity = worldIn.getTileEntity(x, y, z);

		if (tileentity instanceof TileEntityGateway) {
			int i = ((TileEntityGateway) tileentity).getParticleAmount();

			for (int j = 0; j < i; ++j) {
				double d0 = (float) x + rand.nextFloat();
				double d1 = (float) y + rand.nextFloat();
				double d2 = (float) z + rand.nextFloat();
				double d3 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
				double d4 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
				double d5 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
				int k = rand.nextInt(2) * 2 - 1;

				if (rand.nextBoolean()) {
					d2 = (double) z + 0.5D + 0.25D * (double) k;
					d5 = rand.nextFloat() * 2.0F * (float) k;
				} else {
					d0 = (double) x + 0.5D + 0.25D * (double) k;
					d3 = rand.nextFloat() * 2.0F * (float) k;
				}

				worldIn.spawnParticle("portal", d0, d1, d2, d3, d4, d5);
			}
		}
	}
}
