package ganymedes01.etfuturum.configuration.configs;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.ConfigBase;

import java.io.File;
import java.util.List;

public class ConfigModCompat extends ConfigBase {

	public static int elytraBaublesExpandedCompat;
	public static boolean baublesMending;
	public static boolean shulkerBoxesIronChest;
	public static boolean shulkerBoxesIronChestUpgradeItem;
	public static boolean barrelIronChest;
	public static boolean barrelIronChestUpgradeItem;
	public static boolean shulkerUpgradeReturnsShell;

	public static boolean moddedRawOres;
	public static List<String> moddedRawOresBlacklist;
	public static boolean moddedDeepslateOres;
	public static List<String> moddedDeepslateOresBlacklist;

	public static boolean disableBaseBountifulStonesOnly;
	public static boolean disableCopperOreAndIngotOnly;

	public static short soulFireColor;

	static final String catMisc = "misc";
	static final String catRPLE = "RPLE";

	public ConfigModCompat(File file) {
		super(file);

		getCategory(catMisc).setComment("""
				When modded deepslate or raw ore support is enabled, OreDictionary tags will be given to the needed Lord of the Rings ores since they don't have any tags for some reason.\
				LoTR Ores that have no raw/deepslate modded alternative will not be tagged.""");
		getCategory(catRPLE).setComment("""
				RPLE color values for things that don't use normal block/meta states and thus can't be in the RPLE config.
				I would have put these in the RPLE config folder but venn got mad at me and told me not to. :(""");

		configCats.add(getCategory(catMisc));
		configCats.add(getCategory(catRPLE));
	}


	//TODO: Move Iron Chest checks here
	@Override
	protected void syncConfigOptions() {
		shulkerBoxesIronChest = getBoolean("shulkerBoxesIronChest", catMisc, true, "If Iron Chests is installed, allow Iron Shulker boxes to be crafted. This option does nothing if Iron Chests is not installed.");
		shulkerBoxesIronChestUpgradeItem = getBoolean("shulkerBoxesIronChestUpgradeItem", catMisc, false, "If the above is enabled, also enable an item to upgrade the Shulker boxes with. If this is disabled, the default chest upgrade can be used instead.");
		barrelIronChest = getBoolean("barrelIronChest", catMisc, true, "If Iron Chests is installed, allow Iron Barrels to be crafted. This option does nothing if Iron Chests is not installed.");
		barrelIronChestUpgradeItem = getBoolean("barrelIronChestUpgradeItem", catMisc, false, "If the above is enabled, also enable an item to upgrade the barrels with. If this is disabled, the default chest upgrade can be used instead.");
		shulkerUpgradeReturnsShell = getBoolean("shulkerUpgradeReturnsShell", catMisc, true, """
						When the shulker box upgrades are used in a recipe, a shulker shell will be returned.
						This exists because the default recipe for the upgrades is a shulker shell and a regular chest upgrade.
						You are able to uncraft the upgrade back to a regular one, so the shell is returned to avoid accidental loss.""");

		elytraBaublesExpandedCompat = getInt("elytraBaublesExpandedCompat", catMisc, 1, 0, 2, """
				Adds compat for Baubles Expanded. Does nothing for standard baubles, this REQUIRES Baubles Expanded! It uses the new "wings" slot added by the expanded version. https://modrinth.com/mod/baubles-expanded\
				When enabled, this allows the player to equip an elytra with a chestplate, by placing the elytra in a "wings" slot instead of the chestplate slot. Note that the player can only equip one elytra at a time.\
				0 = No compat, do not allow the elytra to be placed in a wings slot.\
				1 = Elytra will be placeable in a wings slot. Will enable the slot, if it isn't there.\
				2 = The elytra can ONLY go in the wings slots, not the chestplate slot.""");
		baublesMending = getBoolean("baublesMending", catMisc, true, "If Baubles (or Baubles Expanded) is installed, allows the Mending enchantment to repair items that are worn in bauble slots when experience is collected.\nThis option does nothing if Baubles (or BE) is not installed or if the Mending enchantment is disabled.");
		soulFireColor = (short) getInt("soulFireColor", catRPLE, 0x49A, 0x000, 0xFFF, """
						The color of soul fire. Needs to be a separate option because it's a mixin for fire and not a meta state.
						Does not have any effect on the color of soul lanterns or soul torches. Check the RPLE colors config for those.""");

		moddedRawOres = getBoolean("moddedRawOres", catMisc, true, "Raw ores for modded metals. Adds a set of \"general\" raw ores for common metals like \"oreAluminium\", \"oreTin\", etc.");
		moddedRawOresBlacklist = Lists.newArrayList(getStringList("moddedRawOresBlacklist", catMisc, new String[0], """
				List of modded raw ores to disable. Add a ModID or ore dictionary tag. For example adding "oreTin" disables modded raw tin, and adding "SimpleOres" would disable raw adamantium ore. CaSe-SeNsItIvE!\
				Each entry is separated by a new line. This only disables raw ores added from Et Futurum's end and will not affect raw ores from other mods."""));
		moddedDeepslateOres = getBoolean("moddedDeepslateOres", catMisc, true, "Deepslate ores for modded ores. Adds a set of \"general\" deepslate ores for common metals like \"oreAluminium\", \"oreTin\", etc, as well as explicit support for numerous mods.");
		moddedDeepslateOresBlacklist = Lists.newArrayList(getStringList("moddedDeepslateOresBlacklist", catMisc, new String[0], """
				List of modded deepslate ores to disable. Add a ModID or ore dictionary tag. For example adding "oreTin" disables deepslate tin, and adding "SimpleOres" would disable deepslate adamantium ore. CaSe-SeNsItIvE!\
				Each entry is separated by a new line. This only disables deepslate ores added from Et Futurum's end and will not affect deepslate ores from other mods."""));

		disableBaseBountifulStonesOnly = getBoolean("disableBaseBountifulStonesOnly", catMisc, false, "Disables just the andesite, granite, and diorite full blocks, but leaves their stairs and slabs, etc enabled." +
				"\nUseful if you have mods like Chisel or Botania which feature these same stones but not the stairs and other variants.");
		disableCopperOreAndIngotOnly = getBoolean("disableCopperOreAndIngotOnly", catMisc, false, "Disables copper ingots and ores, but leaves the blocks and other stuff." +
				"\nUseful if you prefer another mod's copper, but want to use the oxidizing copper building blocks.");
	}

	@Override
	protected void onConstructingValues() {
		if(!ModsList.IRON_CHEST.isLoaded()) {
			shulkerBoxesIronChest = barrelIronChest = shulkerBoxesIronChestUpgradeItem = barrelIronChestUpgradeItem = false;
		}
	}
}
