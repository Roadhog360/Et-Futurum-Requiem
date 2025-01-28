package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import java.util.List;

public class BlockNetherwart extends BaseEFRBlock {

	public BlockNetherwart() {
		super(Material.grass, "nether_wart_block", "warped_wart_block");
		setHardness(1F);
		setResistance(5F);
		setStepSound(ModSounds.soundWartBlock);
		setNames("wart_block");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		if (ConfigBlocksItems.enableNetherwartBlock) {
			list.add(new ItemStack(item, 1, 0));
		}
		if (ConfigExperiments.enableWarpedBlocks) {
			list.add(new ItemStack(item, 1, 1));
		}
	}

	@Override
	public boolean isLeaves(IBlockAccess world, int x, int y, int z) {
		return true;
	}
}
