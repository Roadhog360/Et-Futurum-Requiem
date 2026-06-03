package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class BlockBasalt extends BlockRotatedPillar implements ISubBlocksBlock {

	protected IIcon[] sideIcons;
	protected IIcon[] topIcons;
	private final String[] types = new String[]{"basalt", "polished_basalt"};

	public BlockBasalt() {
		super(Material.rock);
		setHardness(1.25F);
		setResistance(4.2F);
		setBlockName(Utils.getUnlocalisedName("basalt"));
		setBlockTextureName("basalt");
		setHarvestLevel("pickaxe",0);
		Utils.setBlockSound(this, ModSounds.soundBasalt);
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(itemIn, 1, 0));
		list.add(new ItemStack(itemIn, 1, 1));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		sideIcons = new IIcon[2];
		topIcons = new IIcon[2];

		for (int i = 0; i < getTypes().length; ++i) {
			sideIcons[i] = reg.registerIcon(getTypes()[i] + "_side");
			topIcons[i] = reg.registerIcon(getTypes()[i] + "_top");
		}
	}

	@Override
	protected IIcon getSideIcon(int p_150163_1_) {
		return this.sideIcons[p_150163_1_ % this.sideIcons.length];
	}

	@Override
	protected IIcon getTopIcon(int p_150161_1_) {
		return this.topIcons[p_150161_1_ % this.topIcons.length];
	}

	@Override
	public IIcon[] getIcons() {
		return sideIcons;
	}

	@Override
	public String[] getTypes() {
		return types;
	}

	@Override
	public String getNameFor(ItemStack stack) {
		return getTypes()[stack.getItemDamage() % getTypes().length];
	}
}
