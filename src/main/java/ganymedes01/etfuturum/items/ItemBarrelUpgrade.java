package ganymedes01.etfuturum.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemBarrelUpgrade extends BaseSubtypesItem {

	public ItemBarrelUpgrade() {
		super(
				"vanilla_iron_upgrade",
				"vanilla_copper_upgrade",
				"iron_gold_upgrade",
				"gold_diamond_upgrade",
				"steel_gold_upgrade",
				"diamond_obsidian_upgrade",
				"obsidian_netherite_upgrade",
				"obsidian_darksteel_upgrade",
				"copper_iron_upgrade",
				"copper_silver_upgrade",
				"silver_gold_upgrade"
		);
		setNames("barrel_upgrade");
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.barrel_" + types[Math.max(Math.min(stack.getItemDamage(), types.length - 1), 0)];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		icons = new IIcon[types.length];
		for (int i = 0; i < types.length; i++) {
			icons[i] = reg.registerIcon("metalbarrels:" + types[i]);
		}
	}

}
