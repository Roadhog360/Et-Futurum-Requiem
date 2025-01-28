package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.item.ItemSoup;

public class ItemBeetrootSoup extends ItemSoup {

	public ItemBeetrootSoup() {
		super(6);
		setTextureName("beetroot_soup");
		setUnlocalizedName("beetroot_soup");
		setCreativeTab(EtFuturum.creativeTabItems);
	}
}