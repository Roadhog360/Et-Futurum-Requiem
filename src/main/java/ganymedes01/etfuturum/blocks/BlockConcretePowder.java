package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockConcretePowder extends BlockGenericSand implements IConfigurable {

	public BlockConcretePowder() {
		super(Material.sand, "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black");
		this.setHarvestLevel("shovel", 0);
		this.setStepSound(soundTypeSand);
		this.setHardness(.5F);
		this.setResistance(.5F);
		this.setBlockName(Utils.getUnlocalisedName("concrete_powder"));
		this.setBlockTextureName("concrete_powder");
		this.setCreativeTab(isEnabled() ? EtFuturum.creativeTabBlocks : null);
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
		this.setBlock(world, x, y, z);
	}
			
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
		this.setBlock(world, x, y, z);
	}
	
	private void setBlock(World world, int x, int y, int z)
	{
				boolean flag = false;

				if (flag || world.getBlock(x, y, z - 1).getMaterial() == Material.water)
				{
					flag = true;
				}

				if (flag || world.getBlock(x, y, z + 1).getMaterial() == Material.water)
				{
					flag = true;
				}

				if (flag || world.getBlock(x - 1, y, z).getMaterial() == Material.water)
				{
					flag = true;
				}

				if (flag || world.getBlock(x + 1, y, z).getMaterial() == Material.water)
				{
					flag = true;
				}

				if (flag || world.getBlock(x, y + 1, z).getMaterial() == Material.water)
				{
					flag = true;
				}

				if (flag)
				{
					int l = world.getBlockMetadata(x, y, z);
						world.setBlock(x, y, z, ModBlocks.concrete, l, 2);
						world.markBlockForUpdate(x, y, z);
					}
				if (world.getBlock(x, y, z) == ModBlocks.concrete_powder) {
					world.markBlockForUpdate(x, y, z);
				}
				
			}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		icons = new IIcon[types.length];
		for (int i = 0; i < types.length; i++)
			if ("".equals(types[i]))
				icons[i] = reg.registerIcon(getTextureName());
			else
				icons[i] = reg.registerIcon( types[i] + "_" + getTextureName());
	}
	
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return ConfigBlocksItems.enableConcretePowder;
	}

}
