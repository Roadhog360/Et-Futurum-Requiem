package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSeeds;

public class ItemBeetrootSeeds extends ItemSeeds {

	public ItemBeetrootSeeds() {
		super(ModBlocks.BEETROOTS.get(), Blocks.farmland);
		setTextureName("beetroot_seeds");
		setUnlocalizedName("beetroot_seeds");
		setCreativeTab(EtFuturum.creativeTabItems);
	}
}