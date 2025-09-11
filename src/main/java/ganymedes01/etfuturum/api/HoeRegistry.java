package ganymedes01.etfuturum.api;

import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.hogtags.helpers.BlockTags;

import java.util.ArrayList;

@Deprecated
public class HoeRegistry {

	@Deprecated
	public static void addToHoeArray(Block block) {
		BlockTags.addTags(block, "minecraft:mineable/hoe");
	}

	@Deprecated
	public static boolean hoeArrayHas(Block block) {
		return BlockTags.hasTag(block, OreDictionary.WILDCARD_VALUE,"minecraft:mineable/hoe");
	}

	/// No longer works. TODO: Make it work maybe
	@Deprecated
	public static ArrayList<Block> getHoeArray() {
		return new ArrayList<>();
	}
}
