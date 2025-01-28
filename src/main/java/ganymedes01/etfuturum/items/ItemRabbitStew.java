package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.item.ItemSoup;

public class ItemRabbitStew extends ItemSoup {

	public ItemRabbitStew() {
		super(10);
		setTextureName("rabbit_stew");
		setUnlocalizedName("rabbit_stew");
		setCreativeTab(EtFuturum.creativeTabItems);
	}
}