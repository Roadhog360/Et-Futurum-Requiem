package ganymedes01.etfuturum.api.mappings;

import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.RegistryMapping;

import java.util.Random;

/// Doesn't extend {@link RegistryMapping} because it doesn't need to, it's not really used in the same way anyways.
/// It's not used as a key, so it doesn't need the whole static interning system the RegistryMapping does.
public class RawOreDropMapping {

	private final Item dropItem;
	private final int meta;
	private final boolean dropsExtra;

	/**
	 * Used by raw ores to keep a registry of what block drops which raw ores.
	 * Args:
	 * ore: Item to drop
	 * meta: Meta
	 */
	public RawOreDropMapping(Item ore, int meta, boolean dropsExtra) {
		this.dropItem = ore;
		this.meta = meta;
		this.dropsExtra = dropsExtra;
	}

	public boolean getDropsExtra() {
		return dropsExtra;
	}

	public int getDropAmount(Random rand, int fortune) {
		return getDropsExtra() ? rand.nextInt(3 * (fortune + 1) - 1) + 2 : rand.nextInt(1 + fortune) + 1;
	}

	@Override
	public boolean equals(Object o) {
		return o == this || (o instanceof RawOreDropMapping mapping
				&& mapping.dropItem == dropItem
				&& (mapping.meta == meta || mapping.meta == OreDictionary.WILDCARD_VALUE || meta == OreDictionary.WILDCARD_VALUE)
				&& mapping.dropsExtra == dropsExtra);
	}

	public Item getObject() {
		return dropItem;
	}

	public int getMeta() {
		return meta;
	}
}