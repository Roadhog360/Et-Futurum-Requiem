package ganymedes01.etfuturum.blocks.itemblocks;

import ganymedes01.etfuturum.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemBlockAmethystCluster extends ItemBlock {

	private static final String[] item_names = new String[]{"small_amethyst_bud", "medium_amethyst_bud", "large_amethyst_bud", "amethyst_cluster"};
	private final boolean secondCluster = field_150939_a/*blockInstance*/ == ModBlocks.AMETHYST_CLUSTER_2.get();

	public ItemBlockAmethystCluster(Block p_i45328_1_) {
		super(p_i45328_1_);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int p_77647_1_) {
		return p_77647_1_ < 6 ? 0 : 6;
	}

	@Override
	public IIcon getIconFromDamage(int p_77617_1_) {
		return field_150939_a/*blockInstance*/.getIcon(0, p_77617_1_);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int damage = stack.getItemDamage() < 6 ? 0 : 1;
		if (secondCluster) {
			damage += 2;
		}
		return "tile." + item_names[damage];
	}
}
