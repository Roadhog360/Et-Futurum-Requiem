package ganymedes01.etfuturum.compat;

import ganymedes01.etfuturum.api.RawOreRegistry;
import ganymedes01.etfuturum.api.mappings.RawOreDropMapping;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.library.crafting.Smeltery;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CompatTinkersConstruct {

	public static void postInit() {
		regsiterRawOresToSmeltery();
	}

	private static void regsiterRawOresToSmeltery() {
		Map<String, RawOreDropMapping> oreMap = RawOreRegistry.getOreMap();

		for (Entry<String, RawOreDropMapping> kvp : oreMap.entrySet()) {
			String oreDictName = kvp.getKey();
			RawOreDropMapping mapping = kvp.getValue();
			ItemStack melting = new ItemStack(mapping.get(), 1, mapping.getMeta());
			List<ItemStack> allOres = OreDictionary.getOres(oreDictName);
			List<ItemStack> rawOreBlocks = OreDictionary.getOres(oreDictName.replace("ore", "blockRaw"));
			ItemStack oreBlock = null;
			ItemStack rawOreBlock = null;

			// Find an item stack that is a block.
			for (ItemStack ore : rawOreBlocks) {
				if (ore.getItem() instanceof ItemBlock itemBlock) {
					Block block = Block.getBlockFromItem(itemBlock);

					if (block != null && block != Blocks.air) {
						rawOreBlock = ore;
						break;
					}
				}
			}

			// Find an item stack that is a block.
			for (ItemStack ore : allOres) {
				if (ore.getItem() instanceof ItemBlock itemBlock) {
					Block block = Block.getBlockFromItem(itemBlock);

					if (block != null && block != Blocks.air) {
						oreBlock = ore;
						if (rawOreBlock == null) {
							rawOreBlock = oreBlock;
						}
						break;
					}
				}
			}

			if (oreBlock != null) {
				// Collect some infos for the existing block
				FluidStack liquid = Smeltery.getSmelteryResult(oreBlock);
				int temperature = Smeltery.getLiquifyTemperature(oreBlock);
				Block renderBlock = Block.getBlockFromItem(rawOreBlock.getItem());
				int renderBlockMeta = rawOreBlock.getItemDamage();

				// Add the melting recipe
				if (liquid != null) {
					Smeltery.addMelting(melting, renderBlock, renderBlockMeta, temperature, liquid);
				}
			}
		}
	}
}
