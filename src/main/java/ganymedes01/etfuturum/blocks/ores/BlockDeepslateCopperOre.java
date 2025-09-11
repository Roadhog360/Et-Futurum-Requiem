package ganymedes01.etfuturum.blocks.ores;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.core.utils.IInitAction;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.world.DummyWorld;

public class BlockDeepslateCopperOre extends BlockDeepslateOre implements IInitAction {

	public BlockDeepslateCopperOre() {
		super(ModBlocks.COPPER_ORE.get());
	}

	@Override
	public void onLoadAction() {
		if (!ModBlocks.COPPER_ORE.isEnabled() && !OreDictionary.getOres("oreCopper").isEmpty()) {
			//If EFR copper ore isn't enabled, find another mod copper to pull stats from. Fallback to EFR copper stats if nothing is available.
			DummyWorld world = DummyWorld.getGlobalInstance();
			ItemStack stack = Utils.getFirstNonDeepslateBlockFromTag("oreCopper", ModBlocks.COPPER_ORE.newItemStack());
			try {
				Block block = Block.getBlockFromItem(stack.getItem());
				//See BlockGeneralModdedDeepslateOre for a comment on why we do this cursed stuff
				world.setBlock(0, 0, 0, block, stack.getItemDamage(), 0);

				if (block.getHarvestTool(stack.getItemDamage()) != null) {
					setHarvestLevel("pickaxe", block.getHarvestLevel(stack.getItemDamage()));
				}
				blockHardness = ConfigFunctions.useStoneHardnessForDeepslate ? block.getBlockHardness(world, 0, 0, 0) : block.getBlockHardness(world, 0, 0, 0) * 1.5F;
				blockResistance = block.getExplosionResistance(null, world, 0, 0, 0, 0, 0, 0); //Because the game divides it by 5 for some reason
			} catch (Exception ignored) {
				setHarvestLevel("pickaxe", 1);

				blockHardness = ConfigFunctions.useStoneHardnessForDeepslate ? ModBlocks.COPPER_ORE.get().blockHardness : ModBlocks.COPPER_ORE.get().blockHardness * 1.5F;
				blockResistance = ModBlocks.COPPER_ORE.get().blockResistance;
			}
			world.clearFakeData();
		}
	}
}
