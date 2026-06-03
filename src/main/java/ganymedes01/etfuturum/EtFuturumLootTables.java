package ganymedes01.etfuturum;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import roadhog360.hogutils.api.utils.RecipeHelper;

public class EtFuturumLootTables {

	public static final ChestGenHooks COMPOSTER_LOOT = ChestGenHooks.getInfo("composting");
	public static final ChestGenHooks END_CITY_TREASURE = ChestGenHooks.getInfo("endCityTreasure");
	public static final ChestGenHooks END_CITY_ELYTRA = ChestGenHooks.getInfo("endCityElytra");

	public static void init() {
		COMPOSTER_LOOT.setMin(1);
		COMPOSTER_LOOT.setMax(1);
		addLoot(COMPOSTER_LOOT, new ItemStack(Items.dye, 1, 15), 1, 1, 10);
		//For some reason it is min count, max count and weight, yes weight is the last arg not the first....

		END_CITY_TREASURE.setMin(2);
		END_CITY_TREASURE.setMax(6);

		// Basic treasures
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.diamond), 2, 7, 5);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.iron_ingot), 4, 8, 10);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.gold_ingot), 2, 7, 15);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.emerald), 2, 6, 2);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.wheat_seeds), 1, 10, 5); // Beetroot seeds substitute

		// Equipment
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.saddle), 1, 1, 3);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.iron_horse_armor), 1, 1, 1);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.golden_horse_armor), 1, 1, 1);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.diamond_horse_armor), 1, 1, 1);

		// Diamond gear (vanilla enchants these, but we add them unenchanted for simplicity)
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.diamond_sword), 1, 1, 3);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.diamond_pickaxe), 1, 1, 3);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.diamond_shovel), 1, 1, 3);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.diamond_helmet), 1, 1, 3);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.diamond_chestplate), 1, 1, 3);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.diamond_leggings), 1, 1, 3);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.diamond_boots), 1, 1, 3);

		// Iron gear
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.iron_sword), 1, 1, 3);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.iron_pickaxe), 1, 1, 3);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.iron_shovel), 1, 1, 3);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.iron_helmet), 1, 1, 3);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.iron_chestplate), 1, 1, 3);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.iron_leggings), 1, 1, 3);
		addLoot(END_CITY_ELYTRA, new ItemStack(Items.iron_boots), 1, 1, 3);

		END_CITY_ELYTRA.setMin(1);
		END_CITY_ELYTRA.setMax(1);
		// Loot table will be empty if elytra is disabled; add your own loot in that case.
		addLoot(END_CITY_ELYTRA,ModItems.ELYTRA.newItemStack(), 1, 1, 1);
	}
	
	private static void addLoot(ChestGenHooks table, ItemStack stack, int minCount, int maxCount, int weight) {
		if(RecipeHelper.validateItems(stack)) {
			table.addItem(new WeightedRandomChestContent(stack, minCount, maxCount, weight));
		}
	}
}
