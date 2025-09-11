package ganymedes01.etfuturum.blocks.rawore;

import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.blocks.BaseSubtypesBlock;
import ganymedes01.etfuturum.core.utils.IInitAction;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.world.DummyWorld;

public class BlockRawOre extends BaseSubtypesBlock implements IInitAction {
	public BlockRawOre() {
		super(Material.rock, "raw_copper_block", "raw_iron_block", "raw_gold_block");
		setNames("raw_ore_block");
		setToolClass("pickaxe", 1, 0);
		setToolClass("pickaxe", 1, 1);
		setToolClass("pickaxe", 2, 2);
		setHardness(5);
		setResistance(6);
	}

	@Override
	public void onLoadAction() {
		if (!ModItems.COPPER_INGOT.isEnabled() && !OreDictionary.getOres("blockCopper").isEmpty()) {
			//If EFR copper ore isn't enabled, find another mod copper to pull stats from. Fallback to EFR copper stats if nothing is available.
			ItemStack stack = Utils.getFirstBlockFromTag("blockCopper", null);
			DummyWorld world = DummyWorld.getGlobalInstance();
			try {
				Block block = Block.getBlockFromItem(stack.getItem());
				//See BlockGeneralModdedDeepslateOre for a comment on why we do this cursed stuff
				world.setBlock(0, 0, 0, block, stack.getItemDamage(), 0);
				setHarvestLevel("pickaxe", block.getHarvestLevel(stack.getItemDamage()), 0);

				blockHardness = block.getBlockHardness(world, 0, 0, 0);
				blockResistance = block.getExplosionResistance(null, world, 0, 0, 0, 0, 0, 0) * 5; //Because the game divides it by 5 for some reason
			} catch (Exception ignored) {
				setHarvestLevel("pickaxe", 1, 0);
				blockHardness = 5;
				blockResistance = 6;
			}
			world.clearFakeData();
		}
	}
}
