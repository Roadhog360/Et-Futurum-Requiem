package ganymedes01.etfuturum;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.blocks.BlockWoodSign;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.*;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.items.*;
import ganymedes01.etfuturum.items.equipment.*;
import ganymedes01.etfuturum.items.rawore.modded.BaseRawOre;
import ganymedes01.etfuturum.items.rawore.modded.ItemGeneralModdedRawOre;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum ModItems {
	MUTTON_RAW(ConfigBlocksItems.enableMutton, new BaseFood(2, 0.3F, true).setNames("mutton")),
	MUTTON_COOKED(ConfigBlocksItems.enableMutton, new BaseFood(6, 0.8F, true).setNames("cooked_mutton")),
	PRISMARINE_SHARD(ConfigBlocksItems.enablePrismarine, new BaseItem("prismarine_shard")),
	PRISMARINE_CRYSTALS(ConfigBlocksItems.enablePrismarine, new BaseItem("prismarine_crystals")),
	WOODEN_ARMORSTAND(ConfigBlocksItems.enableArmourStand, new ItemArmorStand()),
	RABBIT_RAW(ConfigEntities.enableRabbit, new BaseFood(3, 0.3F, true).setNames("rabbit")),
	RABBIT_COOKED(ConfigEntities.enableRabbit, new BaseFood(5, 0.6F, true).setNames("cooked_rabbit")),
	RABBIT_FOOT(ConfigEntities.enableRabbit, new BaseItem("rabbit_foot").setPotionEffect("+0+1-2+3&4-4+13")),
	RABBIT_HIDE(ConfigEntities.enableRabbit, new BaseItem("rabbit_hide")),
	RABBIT_STEW(ConfigEntities.enableRabbit, new ItemRabbitStew()),
	BEETROOT(ConfigBlocksItems.enableBeetroot, new BaseFood(1, 0.6F, false).setNames("beetroot")),
	BEETROOT_SEEDS(ConfigBlocksItems.enableBeetroot, new ItemBeetrootSeeds()),
	BEETROOT_SOUP(ConfigBlocksItems.enableBeetroot, new ItemBeetrootSoup()),
	CHORUS_FRUIT(ConfigBlocksItems.enableChorusFruit, new ItemChorusFruit()),
	CHORUS_FRUIT_POPPED(ConfigBlocksItems.enableChorusFruit, new BaseItem("popped_chorus_fruit")),
	TIPPED_ARROW(ConfigBlocksItems.enableTippedArrows, new ItemArrowTipped()),
	LINGERING_POTION(ConfigBlocksItems.enableLingeringPotions, new ItemLingeringPotion()),
	DRAGON_BREATH(ConfigBlocksItems.enableLingeringPotions, new BaseItem("dragon_breath").setContainerItem(Items.glass_bottle).setPotionEffect("-14+13")),
	ELYTRA(ConfigMixins.enableElytra, new ItemArmorElytra()),
	END_CRYSTAL(ConfigEntities.enableDragonRespawn, new ItemEndCrystal()),
	NUGGET_IRON(ConfigBlocksItems.enableIronNugget, new BaseItem("iron_nugget")),
	RAW_ORE(ConfigBlocksItems.enableRawOres, new BaseSubtypesItem("raw_copper", "raw_iron", "raw_gold").setNames("raw_ore")),
	//modded_raw_ore(true, new ItemRawOre(true)),
	NETHERITE_SCRAP(ConfigBlocksItems.enableNetherite, new BaseItem("netherite_scrap")),
	NETHERITE_INGOT(ConfigBlocksItems.enableNetherite, new ItemNetheriteIngot()),
	NETHERITE_HELMET(ConfigBlocksItems.enableNetherite, new ItemEFRArmour(ModMaterials.NETHERITE_ARMOUR, 0, ConfigBlocksItems.netheriteHelmetDurability)),
	NETHERITE_CHESTPLATE(ConfigBlocksItems.enableNetherite, new ItemEFRArmour(ModMaterials.NETHERITE_ARMOUR, 1, ConfigBlocksItems.netheriteChestplateDurability)),
	NETHERITE_LEGGINGS(ConfigBlocksItems.enableNetherite, new ItemEFRArmour(ModMaterials.NETHERITE_ARMOUR, 2, ConfigBlocksItems.netheriteLeggingsDurability)),
	NETHERITE_BOOTS(ConfigBlocksItems.enableNetherite, new ItemEFRArmour(ModMaterials.NETHERITE_ARMOUR, 3, ConfigBlocksItems.netheriteBootsDurability)),
	NETHERITE_PICKAXE(ConfigBlocksItems.enableNetherite, new ItemEFRPickaxe(ModMaterials.NETHERITE_TOOL, ConfigBlocksItems.netheritePickaxeDurability)),
	NETHERITE_SPADE(ConfigBlocksItems.enableNetherite, new ItemEFRSpade(ModMaterials.NETHERITE_TOOL, ConfigBlocksItems.netheriteSpadeDurability)),
	NETHERITE_AXE(ConfigBlocksItems.enableNetherite, new ItemEFRAxe(ModMaterials.NETHERITE_TOOL, ConfigBlocksItems.netheriteAxeDurability)),
	NETHERITE_HOE(ConfigBlocksItems.enableNetherite, new ItemEFRHoe(ModMaterials.NETHERITE_TOOL, ConfigBlocksItems.netheriteHoeDurability)),
	NETHERITE_SWORD(ConfigBlocksItems.enableNetherite, new ItemEFRSword(ModMaterials.NETHERITE_TOOL, ConfigBlocksItems.netheriteSwordDurability)),
	TOTEM_OF_UNDYING(ConfigBlocksItems.enableTotemUndying, new BaseItem("totem_of_undying").setMaxStackSize(1)),
	DYE(ConfigBlocksItems.enableNewDyes, new BaseSubtypesItem("white_dye", "blue_dye", "brown_dye", "black_dye").setNames("dye")),
	COPPER_INGOT(ConfigBlocksItems.enableCopper && !ConfigModCompat.disableCopperOreAndIngotOnly, new BaseItem("copper_ingot")),
	SUSPICIOUS_STEW(ConfigBlocksItems.enableSuspiciousStew, new ItemSuspiciousStew()),
	SWEET_BERRIES(ConfigBlocksItems.enableSweetBerryBushes, new ItemSweetBerries()),
	SHULKER_SHELL(ConfigBlocksItems.enableShulkerBoxes, new BaseItem("shulker_shell")),
	PIGSTEP_RECORD(ConfigBlocksItems.enablePigstep, new ItemEtFuturumRecord("pigstep")),
	OTHERSIDE_RECORD(ConfigBlocksItems.enableOtherside, new ItemEtFuturumRecord("otherside")),
	AMETHYST_SHARD(ConfigBlocksItems.enableAmethyst, new BaseItem("amethyst_shard")),
	SHULKER_BOX_UPGRADE(ModsList.IRON_CHEST.isLoaded() && ConfigModCompat.shulkerBoxesIronChest, new ItemShulkerBoxUpgrade()),
	BARREL_UPGRADE(ModsList.IRON_CHEST.isLoaded() && ConfigModCompat.barrelIronChest, new ItemBarrelUpgrade()),
	HONEYCOMB(ConfigBlocksItems.enableHoney, new BaseItem("honeycomb")),
	HONEY_BOTTLE(ConfigBlocksItems.enableHoney, new ItemHoneyBottle()),
	BAMBOO(ConfigBlocksItems.enableBambooBlocks, new ItemBamboo()),

	OAK_BOAT(ConfigBlocksItems.enableNewBoats && !ConfigBlocksItems.replaceOldBoats, new ItemNewBoat("minecraft", "oak", () -> Item.getItemFromBlock(Blocks.planks), 0, false, false)),
	OAK_CHEST_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat("minecraft", "oak", () -> Item.getItemFromBlock(Blocks.planks), 0, true, false)),
	SPRUCE_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat("minecraft", "spruce", () -> Item.getItemFromBlock(Blocks.planks), 1, false, false)),
	SPRUCE_CHEST_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat("minecraft", "spruce", () -> Item.getItemFromBlock(Blocks.planks), 1, true, false)),
	BIRCH_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat("minecraft", "birch", () -> Item.getItemFromBlock(Blocks.planks), 2, false, false)),
	BIRCH_CHEST_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat("minecraft", "birch", () -> Item.getItemFromBlock(Blocks.planks), 2, true, false)),
	JUNGLE_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat("minecraft", "jungle", () -> Item.getItemFromBlock(Blocks.planks), 3, false, false)),
	JUNGLE_CHEST_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat("minecraft", "jungle", () -> Item.getItemFromBlock(Blocks.planks), 3, true, false)),
	ACACIA_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat("minecraft", "acacia", () -> Item.getItemFromBlock(Blocks.planks), 4, false, false)),
	ACACIA_CHEST_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat("minecraft", "acacia", () -> Item.getItemFromBlock(Blocks.planks), 4, true, false)),
	DARK_OAK_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat("minecraft", "dark_oak", () -> Item.getItemFromBlock(Blocks.planks), 5, false, false)),
	DARK_OAK_CHEST_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat("minecraft", "dark_oak", () -> Item.getItemFromBlock(Blocks.planks), 5, true, false)),
	MANGROVE_OAK_BOAT(ConfigBlocksItems.enableNewBoats && ConfigExperiments.enableMangroveBlocks, new ItemNewBoat("minecraft", "mangrove", ModBlocks.WOOD_PLANKS::getItem, 2, false, false)),
	MANGROVE_CHEST_BOAT(ConfigBlocksItems.enableNewBoats && ConfigExperiments.enableMangroveBlocks, new ItemNewBoat("minecraft", "mangrove", ModBlocks.WOOD_PLANKS::getItem, 2, true, false)),
	CHERRY_BOAT(ConfigBlocksItems.enableNewBoats && ConfigBlocksItems.enableCherryBlocks, new ItemNewBoat("minecraft", "cherry", ModBlocks.WOOD_PLANKS::getItem, 3, false, false)),
	CHERRY_CHEST_BOAT(ConfigBlocksItems.enableNewBoats && ConfigBlocksItems.enableCherryBlocks, new ItemNewBoat("minecraft", "cherry", ModBlocks.WOOD_PLANKS::getItem, 3, true, false)),
	BAMBOO_RAFT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat("minecraft", "bamboo", ModBlocks.WOOD_PLANKS::getItem, 4, false, true)),
	BAMBOO_CHEST_RAFT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat("minecraft", "bamboo", ModBlocks.WOOD_PLANKS::getItem, 4, true, true)),

	//legacy sign items -- new signs use their ItemBlock as the sign item instead
	ITEM_SIGN_SPRUCE(ConfigBlocksItems.enableVanillaSigns, new ItemWoodSign((BlockWoodSign) ModBlocks.SIGN_SPRUCE.get())),
	ITEM_SIGN_BIRCH(ConfigBlocksItems.enableVanillaSigns, new ItemWoodSign((BlockWoodSign) ModBlocks.SIGN_BIRCH.get())),
	ITEM_SIGN_JUNGLE(ConfigBlocksItems.enableVanillaSigns, new ItemWoodSign((BlockWoodSign) ModBlocks.SIGN_JUNGLE.get())),
	ITEM_SIGN_ACACIA(ConfigBlocksItems.enableVanillaSigns, new ItemWoodSign((BlockWoodSign) ModBlocks.SIGN_ACACIA.get())),
	ITEM_SIGN_DARK_OAK(ConfigBlocksItems.enableVanillaSigns, new ItemWoodSign((BlockWoodSign) ModBlocks.SIGN_DARK_OAK.get())),

	//Mod Support
	MODDED_RAW_ORE(Utils.enableModdedRawOres(), new ItemGeneralModdedRawOre("raw_aluminum", "raw_tin", "raw_silver", "raw_lead", "raw_nickel", "raw_platinum", "raw_mythril",
			"raw_uranium", "raw_thorium", "raw_tungsten", "raw_titanium", "raw_zinc", "raw_magnesium", "raw_boron")),
	RAW_ADAMANTIUM(Utils.enableModdedRawOres(ModsList.SIMPLEORES), new BaseRawOre("simpleores", "adamantium")),

	//Debug Item
	DEBUGGING_TOOL(Reference.DEV_ENVIRONMENT, new DebugTestItem());

	public static final ModItems[] CHEST_BOATS = new ModItems[]{OAK_CHEST_BOAT, SPRUCE_CHEST_BOAT, BIRCH_CHEST_BOAT, JUNGLE_CHEST_BOAT, ACACIA_CHEST_BOAT, DARK_OAK_CHEST_BOAT};
	public static final ModItems[] BOATS = new ModItems[]{OAK_BOAT, SPRUCE_BOAT, BIRCH_BOAT, JUNGLE_BOAT, ACACIA_BOAT, DARK_OAK_BOAT};
	public static final ModItems[] OLD_SIGN_ITEMS = new ModItems[]{ITEM_SIGN_SPRUCE, ITEM_SIGN_BIRCH, ITEM_SIGN_JUNGLE, ITEM_SIGN_ACACIA, ITEM_SIGN_DARK_OAK};

	/*
	 * Stand-in static final fields because some mods incorrectly referenced my code directly.
	 * They should be using GameRegistry.findItem but it is what it is I guess.
	 */

	//D-Mod
	@Deprecated
	public static final Item sweet_berries = SWEET_BERRIES.get();

	public static final ModItems[] VALUES = values();

	public static void init() {
		for (ModItems item : VALUES) {
			if (item.isEnabled()) { //Honestly what do you think it's doing lmfao
				GameRegistry.registerItem(item.get(), item.name().toLowerCase());
			}
		}
	}

	final private boolean isEnabled;
	final private Item theItem;

	ModItems(boolean enabled, Item item) {
		isEnabled = enabled;
		theItem = item;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public Item get() {
		return theItem;
	}

	public ItemStack newItemStack() {
		return newItemStack(1);
	}

	public ItemStack newItemStack(int count) {
		return newItemStack(count, 0);
	}

	public ItemStack newItemStack(int count, int meta) {
		return new ItemStack(this.get(), count, meta);
	}
}