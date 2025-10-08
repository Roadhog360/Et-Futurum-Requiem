package ganymedes01.etfuturum.compat;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.versioning.ComparableVersion;

public enum ModsList {
	ENDERLICIOUS("enderlicious"),
	IRON_CHEST("IronChest"),
	NETHERLICIOUS("netherlicious"),
	AETHER_LEGACY("aether_legacy"),
	WAILA("Waila"),
	THAUMCRAFT("Thaumcraft"),
	BLUEPOWER("bluepower"),
	PROJECT_RED_EXPLORATION("ProjRed|Exploration"),
	NETHERITEPLUS("netheriteplus"),
	BOTANIA("Botania"),
	HARDCORE_ENDER_EXPANSION("HardcoreEnderExpansion"),
	INDUSTRIAL_CRAFT_2("IC2"),
	SKINPORT("skinport"),
	EARS("ears"),
	BAUBLES("Baubles"),
	BAUBLES_EXPANDED("Baubles|Expanded"),
	MINETWEAKER_3("MineTweaker3"),
	TINKERS_CONSTRUCT("TConstruct"),
	NATURA("Natura"),
	CAMPFIRE_BACKPORT("campfirebackport"),
	BACK_IN_SLIME("bis"),
	MINEFACTORY_RELOADED("MineFactoryReloaded"),
	FOAMFIX("foamfix"),
	MOREPLAYERMODELS("moreplayermodels"),
	DRAGON_BLOCK_C("jinryuudragonblockc"),
	BIOMES_O_PLENTY("BiomesOPlenty"),
	EXTRA_UTILITIES("ExtraUtilities"),
	APPLIED_ENERGISTICS_2("appliedenergistics2"),
	ARS_MAGICA_2("arsmagica2"),
	MULTIPART("McMultipart"),
	DRACONIC_EVOLUTION("DraconicEvolution"),
	BIG_REACTORS("BigReactors"),
	FISKS_SUPERHEROES("fiskheroes"),
	SIMPLEORES("simpleores"),
	DRAGON_QUEST("DQMIIINext"),
	LOTR("lotr"),
    BATTLEGEAR_2("battlegear2"),

	NOTFINE("notfine"),
	NOT_ENOUGH_IDS("neid"),
	ENDLESS_IDS("endlessids"),
	ENDLESS_IDS_BLOCKITEM("endlessids_blockitem"),
	ENDLESS_IDS_DATAWATCHER("endlessids_datawatcher"),
	ENDLESS_IDS_BIOME("endlessids_biome"),
	ENDLESS_IDS_POTION("endlessids_potion"),
	ENDLESS_IDS_ENCHANTMENT("endlessids_enchantment"),
	MC_PATCHER_FORGE("mcpatcherforge"),

	RPLE("rple"),

	GTNH("dreamcraft"),
	;

	private final String modID;
	private Boolean isLoaded;
	private String version;

	ModsList(String modID) {
		this.modID = modID;
	}

	public boolean isLoaded() {
		if (isLoaded == null) {
			isLoaded = Loader.isModLoaded(modID);
		}
		return isLoaded;
	}

	public String modID() {
		return modID;
	}

	public String getVersion() {
		if (isLoaded()) {
			if (version == null) {
				version = Loader.instance().getIndexedModList().get(modID).getProcessedVersion().getVersionString();
			}
		} else {
			throw new RuntimeException("Cannot get version for mod that is not loaded!");
		}
		return version;
	}

	public int compareVersion(String compareTo) {
		String cleanedString = compareTo.replaceAll("§.", ""); //Remove folor code values
		return new ComparableVersion(getVersion()).compareTo(new ComparableVersion(cleanedString));
	}

	public boolean isVersionNewer(String compareTo) {
		return compareVersion(compareTo) > 0;
	}

	public boolean isVersionNewerOrEqual(String compareTo) {
		return compareVersion(compareTo) >= 0;
	}

	public boolean isVersionEqual(String compareTo) {
		return compareVersion(compareTo) == 0;
	}

	public boolean isVersionOlderOrEqual(String compareTo) {
		return compareVersion(compareTo) <= 0;
	}

	public boolean isVersionOlder(String compareTo) {
		return compareVersion(compareTo) < 0;
	}
}