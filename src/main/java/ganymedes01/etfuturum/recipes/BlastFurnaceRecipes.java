package ganymedes01.etfuturum.recipes;

import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.core.utils.ItemStackMap;
import ganymedes01.etfuturum.core.utils.ItemStackSet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

public class BlastFurnaceRecipes {
	private static final BlastFurnaceRecipes smeltingBase = new BlastFurnaceRecipes();
	private boolean reloadingCT;

	/**
	 * The list of smelting results.
	 */
	public final ItemStackMap<ItemStack> smeltingList = new ItemStackMap<ItemStack>();
	public final ItemStackMap<Float> experienceList = new ItemStackMap<Float>();
	public final ItemStackSet smeltingBlacklist = new ItemStackSet();


	public final ItemStackMap<ItemStack> smeltingListCache = new ItemStackMap<ItemStack>();
	public final ItemStackMap<Float> experienceListCache = new ItemStackMap<Float>();

	/**
	 * Used to call methods addSmelting and getSmeltingResult.
	 */
	public static BlastFurnaceRecipes smelting() {
		return smeltingBase;
	}

	public void clearLists() {
		smeltingListCache.clear();
		experienceListCache.clear();
	}

	public void setReloadingCT(boolean val) {
		reloadingCT = val;
	}

	/**
	 * Returns the smelting result of an item.
	 */
	public ItemStack getSmeltingResult(ItemStack input) {
		if (smeltingBlacklist.contains(input)) return null;

		if (!smeltingListCache.containsKey(input)) {
			if (!smeltingList.containsKey(input)) {
				ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(input);
				if (canAdd(input, result)) {
					if (!reloadingCT) smeltingListCache.put(input, result);
					return result;
				}
			}
			ItemStack CTResult = smeltingList.get(input);
			if (!reloadingCT) smeltingListCache.put(input, CTResult);
			return CTResult;
		}
		return smeltingListCache.get(input);
	}

	public float getSmeltingExperience(ItemStack result) {
		float ret = result.getItem().getSmeltingExperience(result);
		if (ret != -1) return ret;

		if (!experienceListCache.containsKey(result)) {
			if (!experienceList.containsKey(result)) {
				float exp = FurnaceRecipes.smelting().func_151398_b(result); // getSmeltingExperience
				if (!reloadingCT) experienceListCache.put(result, exp);
				return exp;
			}
			float expCT = experienceList.get(result);
			if (!reloadingCT) experienceListCache.put(result, expCT);
			return expCT;
		}
		return experienceListCache.get(result);
	}

	public void addRecipe(ItemStack input, ItemStack output, float exp) {
		smeltingList.put(input, output);
		experienceList.put(output, exp);
		smeltingBlacklist.remove(input);
	}

	public void removeRecipe(ItemStack input) {
		experienceList.remove(smeltingList.get(input));
		smeltingList.remove(input);
		smeltingBlacklist.add(input);
	}

	public boolean canAdd(ItemStack input, ItemStack result) {
		if (ConfigFunctions.enableAutoAddBlastFurnace) {

			if (input != null && result != null) {
				for (int id : OreDictionary.getOreIDs(input)) {
					String oreName = OreDictionary.getOreName(id);
					// It should not be possible for this to be null, but better safe than sorry...
					if (oreName == null) continue;
					// Any Type of Ores.
					if (oreName.startsWith("ore")) {
						return true;
					}
                    // Raw Ores
                    if(oreName.matches("^raw[A-Z].*")) {
                        return true;
                    }
					// Thaumcraft Ore Cluster Items.
					if (oreName.startsWith("cluster")) {
						return true;
					}
					// IndustrialCraft2 Crushed Ore Items.
					if (oreName.startsWith("crushed")) {
						return true;
					}
				}
				for (int id : OreDictionary.getOreIDs(result)) {
					String oreName = OreDictionary.getOreName(id);
					// It should not be possible for this to be null, but better safe than sorry...
					if (oreName == null) continue;
					// Outputs Ingots.
					if (oreName.startsWith("ingot")) {
						return true;
					}
					// Outputs GregTechs Quarter-of-Ingot Nuggets.
					if (oreName.startsWith("chunkGt")) {
						return true;
					}
					// Outputs normal Nuggets.
					if (oreName.startsWith("nugget")) {
						return true;
					}
					// Outputs Thaumcrafts Quicksilver Item, which weirdly enough is not registered as an Ingot, while still having Nuggets.
					if (oreName.equalsIgnoreCase("quicksilver")) {
						return true;
					}
				}
			}
		}
		return false;

//      if(ConfigFunctions.enableMeltGear) {
//          Item[][] crafts = new Item[][] {
//              {Items.golden_helmet, Items.golden_chestplate, Items.golden_leggings, Items.golden_boots, Items.golden_sword, Items.golden_pickaxe,
//                  Items.golden_axe, Items.golden_shovel, Items.golden_hoe, Items.golden_horse_armor,
//              Items.iron_helmet, Items.iron_chestplate, Items.iron_leggings, Items.iron_boots, Items.iron_sword, Items.iron_pickaxe,
//                  Items.iron_axe, Items.iron_shovel, Items.iron_hoe, Items.iron_horse_armor},
//              {Items.gold_nugget, ModItems.iron_nugget}};
//          for(int i = 0; i < crafts[0].length; i++) {
//              if(!ConfigBlocksItems.enableIronNugget && i > 9)
//                  break;
//              smeltingBase.addRecipe(crafts[0][i], new ItemStack(crafts[1][i > 9 ? 1 : 0]), .1F);
//          }
//      }
	}
}
