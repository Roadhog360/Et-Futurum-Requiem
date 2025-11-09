package ganymedes01.etfuturum.api;

import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.api.mappings.RawOreDropMapping;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.items.rawore.modded.ItemGeneralModdedRawOre;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import roadhog360.hogutils.api.utils.RecipeHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * The registry Et Futurum's block breaking handler uses to find out if we should drop
 * a raw ore item. When broken, that blocks OreDictionary tags are checked and if it
 * matches a tag that is mapped here, it will drop the respective item you mapped.
 *
 * @author roadhog360
 * @apiNote Usage example: {addOre("oreIron", ModItems.raw_ore, 1);} would cause any ore with the tag
 * "oreIron" to drop Et Futurum's raw ore item, with a meta value of 1, which is the raw iron item.
 */
public class RawOreRegistry {

	public static final Map<String, RawOreDropMapping> rawOreRegistry = new HashMap<>();

	/**
	 * What item should blocks that have this OreDict tag drop?
	 * Assumes a meta value of 0 for the specified drop.
	 *
	 * @param oreDict
	 * @param ore
	 */
	public static void addOre(String oreDict, Item ore) {
		addOre(oreDict, ore, 0);
	}


	/**
	 * What item should blocks that have this OreDict tag drop?
	 *
	 * @param oreDict
	 * @param ore
	 */
	public static void addOre(String oreDict, Item ore, int meta) {
		if (RecipeHelper.validateItems(ore)) {
			RawOreDropMapping mapping = new RawOreDropMapping(ore, meta);
			if (ArrayUtils.contains(ConfigFunctions.extraDropRawOres, oreDict.replace("oreNether", "ore"))) {
				mapping.setDropsExtra(true);
			}
			addOre(oreDict, mapping);
		}
	}

	private static void addOre(String oreDict, RawOreDropMapping map) {
		rawOreRegistry.put(oreDict, map);
	}

	/**
	 * @param oreDict
	 * @return Returns true if this oreDictionary tag has a raw ore drop assigned to it.
	 * Otherwise it returns false
	 */
	public static boolean hasOre(String oreDict) {
		return rawOreRegistry.containsKey(oreDict);
	}

	/**
	 * Gets the raw ore this should drop.
	 *
	 * @param oreDict
	 * @return As the class RawOreDropMapping, which
	 * contains the instance of the item the specified OreDictionary tag should drop,
	 * the meta value it should drop as and a boolean which determines if it should drop
	 * extra instead of just one per block, which is used by copper, as well as how much
	 * it will drop, inputting a Random instance and the fortune level.
	 */
	public static RawOreDropMapping getOre(String oreDict) {
		return rawOreRegistry.get(oreDict);
	}


	/**
	 * @return The entire raw ore mapping, where an OreDictionary tag is the key.
	 * The key's return value is of the class RawOreDropMapping, which contains
	 * the instance of the item the specified OreDictionary tag should drop,
	 * the meta value it should drop as and a boolean which determines if it should drop
	 * extra instead of just one per block, which is used by copper, as well as how much
	 * it will drop, inputting a Random instance and the fortune level.
	 * <p>
	 * Do not use this to add or get items from the map this way,
	 * in case the key changes.
	 */
	public static Map<String, RawOreDropMapping> getOreMap() {
		return Collections.unmodifiableMap(rawOreRegistry);
	}

	public static void init() {
		if (ModItems.RAW_ORE.isEnabled()) {
			if (ConfigBlocksItems.enableCopper || OreDictionary.doesOreNameExist("ingotCopper")) {
				addOre("oreCopper", ModItems.RAW_ORE.get());
				if(ModsList.NETHERORES.isLoaded())
					addOre("oreNetherCopper", ModItems.RAW_ORE.get());
			}
			addOre("oreIron", ModItems.RAW_ORE.get(), 1);
			addOre("oreGold", ModItems.RAW_ORE.get(), 2);
			if(ModsList.NETHERORES.isLoaded()) {
				addOre("oreNetherIron", ModItems.RAW_ORE.get(), 1);
				addOre("oreNetherGold", ModItems.RAW_ORE.get(), 2);
			}
		}
		if (Utils.enableModdedRawOres()) {
			for (ItemGeneralModdedRawOre oreItem : ItemGeneralModdedRawOre.loaded) {
				for (int i = 0; i < oreItem.ores.length; i++) {
					String type = oreItem.ores[i];
					for (int j = 0; j < 1; j++) {
						if (Utils.listGeneralModdedRawOre(type)) { //If it's mythril, we'll run this once more, changing the spelling to mithril to account for both tags.
							addOre(type.replace("ingot", "ore"), ModItems.MODDED_RAW_ORE.get(), i);
							if(ModsList.NETHERORES.isLoaded()) {
								addOre(type.replace("ingot", "oreNether"), ModItems.MODDED_RAW_ORE.get(), i);
							}
						}
						if (type.endsWith("Mythril")) {
							type = type.replace("Mythril", "Mithril"); //Redoes it once more for mithril spelling
							j = -1;
						}
					}
				}
			}
		}
	}
}
