package ganymedes01.etfuturum.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.compat.CompatIronChests;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class ItemCustomICUpgrade extends BaseSubtypesItem {
	private final String[][] upgrades;
	private final String textureDomain;

	public ItemCustomICUpgrade(boolean wooden, String baseName, String domain, String... types) {
		super(types);
		textureDomain = domain;
		upgrades = new String[types.length][2];
		for(int i = 0; i < types.length; i++) {
			if(!wooden) {
				types[i] = types[i].replace("wood", "vanilla");
			}
			upgrades[i][0] = types[i].split("_")[0].toUpperCase();
			upgrades[i][1] = types[i].split("_")[1].toUpperCase();
			types[i] = baseName + "_" + types[i] + "_upgrade";
		}
		setMaxStackSize(1);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
		for(int i = 0; i < types.length; i++) {
			var stack = new ItemStack(item, 1, i);
			if(CompatIronChests.upgradeExists(upgrades[i][0], upgrades[i][1])) {
				list.add(stack);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		icons = new IIcon[types.length];
		for (int i = 0; i < types.length; i++) {
			icons[i] = reg.registerIcon((getTextureDomain().isEmpty() ? "" : getTextureDomain() + ":") + types[i].replace(types[i].split("_")[0] + "_", ""));
		}
	}

	@Override
	public String getTextureDomain() {
		return textureDomain;
	}

	public String getSource(int meta) {
		if(meta < 0 || meta >= types.length) {
			return null;
		}
		return upgrades[meta][0];
	}

	public String getTarget(int meta) {
		if(meta < 0 || meta >= types.length) {
			return null;
		}
		return upgrades[meta][1];
	}
}
