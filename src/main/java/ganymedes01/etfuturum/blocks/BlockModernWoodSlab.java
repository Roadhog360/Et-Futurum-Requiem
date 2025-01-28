package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class BlockModernWoodSlab extends BaseEFRSlab {
	final BlockModernWoodPlanks basePlanks;

	public BlockModernWoodSlab(boolean isDouble) {
		super(isDouble, Material.wood, "crimson", "warped", "mangrove", "cherry", "bamboo");
		basePlanks = (BlockModernWoodPlanks) ModBlocks.WOOD_PLANKS.get();
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		ModBlocks.WOOD_PLANKS.get().getSubBlocks(itemIn, tab, list);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
	}

	@Override
	public boolean isMetadataEnabled(int meta) {
		return basePlanks.isMetadataEnabled(meta & 7);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return basePlanks.getIcon(side, meta % 8);
	}
}
