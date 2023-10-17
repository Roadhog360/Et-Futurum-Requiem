package ganymedes01.etfuturum.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Modded raw ore class that doesn't add the raw ores to the Creative menu if there would be no ore for them to base off of.
 */
public class ItemModdedRawOre extends BaseSubtypesItem {

	public static final String[] names = new String[]{"raw_aluminum", "raw_tin", "raw_silver", "raw_lead", "raw_nickel", "raw_platinum", "raw_mythril", "raw_uranium"};
	public static final String[] ores = new String[names.length];

	static {
		for (int i = 0; i < names.length; i++) {
			ores[i] = "ingot" + StringUtils.capitalize(names[i].replaceFirst("^raw_", ""));
		}
	}

	public ItemModdedRawOre() {
		super(names);
		setNames("modded_raw_ore");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tabs, List list) {
		for (int i = 0; i < ores.length; i++) {
			if (!OreDictionary.getOres(ores[i]).isEmpty()) {
				list.add(new ItemStack(item, 1, i));
			} else if (ores[i].equals("ingotMythril") && !OreDictionary.getOres("ingotMithril").isEmpty()) {
				//If Mythril is empty but Mithril isn't, we'll add the raw ore anyways since it's Mythril and Mithril
				list.add(new ItemStack(item, 1, i));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		icons = new IIcon[types.length];
		for (int i = 0; i < types.length; i++) {
			icons[i] = reg.registerIcon(Reference.MOD_ID + ":" + types[i]);
		}
	}
}
