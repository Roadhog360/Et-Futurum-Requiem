package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import ganymedes01.etfuturum.core.utils.IInitAction;

public class ItemShulkerBoxUpgrade extends ItemCustomICUpgrade implements IInitAction {

	public ItemShulkerBoxUpgrade() {
		super(false, "shulker", "ironshulkerbox",
				"vanilla_iron",
				"vanilla_copper",
				"iron_gold",
				"gold_diamond",
				"diamond_obsidian",
				"diamond_crystal",
				"copper_iron",
				"copper_silver",
				"silver_gold"
		);
		setNames("shulker_box_upgrade");
		if(ModItems.SHULKER_SHELL.isEnabled() && ConfigModCompat.shulkerUpgradeReturnsShell) {
			setContainerItem(ModItems.SHULKER_SHELL.get());
		}
	}
}
