package ganymedes01.etfuturum;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigEntities;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import ganymedes01.etfuturum.entities.EntityNewBoat;
import ganymedes01.etfuturum.items.*;
import ganymedes01.etfuturum.items.ItemWoodSign;
import ganymedes01.etfuturum.items.equipment.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum ModItems {
	MUTTON_RAW(ConfigBlocksItems.enableMutton, new ItemMuttonRaw()),
	MUTTON_COOKED(ConfigBlocksItems.enableMutton, new ItemMuttonCooked()),
	PRISMARINE_SHARD(ConfigBlocksItems.enablePrismarine, new ItemPrismarineShard()),
	PRISMARINE_CRYSTALS(ConfigBlocksItems.enablePrismarine, new ItemPrismarineCrystals()),
	WOODEN_ARMORSTAND(ConfigBlocksItems.enableArmourStand, new ItemArmorStand()),
	RABBIT_RAW(ConfigEntities.enableRabbit, new ItemRabbitRaw()),
	RABBIT_COOKED(ConfigEntities.enableRabbit, new ItemRabbitCooked()),
	RABBIT_FOOT(ConfigEntities.enableRabbit, new ItemRabbitFoot()),
	RABBIT_HIDE(ConfigEntities.enableRabbit, new ItemRabbitHide()),
	RABBIT_STEW(ConfigEntities.enableRabbit, new ItemRabbitStew()),
	BEETROOT(ConfigBlocksItems.enableBeetroot, new ItemBeetroot()),
	BEETROOT_SEEDS(ConfigBlocksItems.enableBeetroot, new ItemBeetrootSeeds()),
	BEETROOT_SOUP(ConfigBlocksItems.enableBeetroot, new ItemBeetrootSoup()),
	CHORUS_FRUIT(ConfigBlocksItems.enableChorusFruit, new ItemChorusFruit()),
	CHORUS_FRUIT_POPPED(ConfigBlocksItems.enableChorusFruit, new ItemPoppedChorusFruit()),
	TIPPED_ARROW(ConfigBlocksItems.enableTippedArrows, new ItemArrowTipped()),
	LINGERING_POTION(ConfigBlocksItems.enableLingeringPotions, new ItemLingeringPotion()),
	DRAGON_BREATH(ConfigBlocksItems.enableLingeringPotions, new ItemDragonBreath()),
	ELYTRA(ConfigMixins.enableElytra, new ItemArmorElytra()),
	END_CRYSTAL(ConfigEntities.enableDragonRespawn, new ItemEndCrystal()),
	NUGGET_IRON(ConfigBlocksItems.enableIronNugget, new ItemNuggetIron()),
	RAW_ORE(ConfigBlocksItems.enableRawOres, new ItemRawOre(false)),
	//modded_raw_ore(true, new ItemRawOre(true)),
	NETHERITE_SCRAP(ConfigBlocksItems.enableNetherite, new ItemNetherite(0)),
	NETHERITE_INGOT(ConfigBlocksItems.enableNetherite, new ItemNetherite(1)),
	NETHERITE_HELMET(ConfigBlocksItems.enableNetherite, new ItemEFRArmour(ModMaterials.NETHERITE_ARMOUR, 0, ConfigBlocksItems.netheriteHelmetDurability)),
	NETHERITE_CHESTPLATE(ConfigBlocksItems.enableNetherite, new ItemEFRArmour(ModMaterials.NETHERITE_ARMOUR, 1, ConfigBlocksItems.netheriteChestplateDurability)),
	NETHERITE_LEGGINGS(ConfigBlocksItems.enableNetherite, new ItemEFRArmour(ModMaterials.NETHERITE_ARMOUR, 2, ConfigBlocksItems.netheriteLeggingsDurability)),
	NETHERITE_BOOTS(ConfigBlocksItems.enableNetherite, new ItemEFRArmour(ModMaterials.NETHERITE_ARMOUR, 3, ConfigBlocksItems.netheriteBootsDurability)),
	NETHERITE_PICKAXE(ConfigBlocksItems.enableNetherite, new ItemEFRPickaxe(ModMaterials.NETHERITE_TOOL, ConfigBlocksItems.netheritePickaxeDurability)),
	NETHERITE_SPADE(ConfigBlocksItems.enableNetherite, new ItemEFRSpade(ModMaterials.NETHERITE_TOOL, ConfigBlocksItems.netheriteSpadeDurability)),
	NETHERITE_AXE(ConfigBlocksItems.enableNetherite, new ItemEFRAxe(ModMaterials.NETHERITE_TOOL, ConfigBlocksItems.netheriteAxeDurability)),
	NETHERITE_HOE(ConfigBlocksItems.enableNetherite, new ItemEFRHoe(ModMaterials.NETHERITE_TOOL, ConfigBlocksItems.netheriteHoeDurability)),
	NETHERITE_SWORD(ConfigBlocksItems.enableNetherite, new ItemEFRSword(ModMaterials.NETHERITE_TOOL, ConfigBlocksItems.netheriteSwordDurability)),
	SHIELD(true, new ItemEFRShield()),
	TOTEM_OF_UNDYING(ConfigBlocksItems.enableTotemUndying, new ItemTotemUndying()),
	DYE(ConfigBlocksItems.enableNewDyes, new ItemNewDye()),
	COPPER_INGOT(ConfigBlocksItems.enableCopper, new ItemCopperIngot()),
	SUSPICIOUS_STEW(ConfigBlocksItems.enableSuspiciousStew, new ItemSuspiciousStew()),
	SWEET_BERRIES(ConfigBlocksItems.enableSweetBerryBushes, new ItemSweetBerries()),
	SHULKER_SHELL(ConfigBlocksItems.enableShulkerBoxes, new ItemShulkerShell()),
	PIGSTEP_RECORD(ConfigBlocksItems.enablePigstep, new ItemEtFuturumRecord("pigstep")),
	OTHERSIDE_RECORD(ConfigBlocksItems.enableOtherside, new ItemEtFuturumRecord("otherside")),
	AMETHYST_SHARD(ConfigBlocksItems.enableAmethyst, new ItemAmethystShard()),
	SHULKER_BOX_UPGRADE(EtFuturum.hasIronChest && ConfigModCompat.shulkerBoxesIronChest, new ItemShulkerBoxUpgrade()),

	ITEM_SIGN_SPRUCE(ConfigBlocksItems.enableSigns, new ItemWoodSign(1)),
	ITEM_SIGN_BIRCH(ConfigBlocksItems.enableSigns, new ItemWoodSign(2)),
	ITEM_SIGN_JUNGLE(ConfigBlocksItems.enableSigns, new ItemWoodSign(3)),
	ITEM_SIGN_ACACIA(ConfigBlocksItems.enableSigns, new ItemWoodSign(4)),
	ITEM_SIGN_DARK_OAK(ConfigBlocksItems.enableSigns, new ItemWoodSign(5)),

	OAK_BOAT(ConfigBlocksItems.enableNewBoats && !ConfigBlocksItems.replaceOldBoats, new ItemNewBoat(EntityNewBoat.Type.OAK, false)),
	OAK_CHEST_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat(EntityNewBoat.Type.OAK, true)),
	SPRUCE_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat(EntityNewBoat.Type.SPRUCE, false)),
	SPRUCE_CHEST_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat(EntityNewBoat.Type.SPRUCE, true)),
	BIRCH_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat(EntityNewBoat.Type.BIRCH, false)),
	BIRCH_CHEST_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat(EntityNewBoat.Type.BIRCH, true)),
	JUNGLE_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat(EntityNewBoat.Type.JUNGLE, false)),
	JUNGLE_CHEST_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat(EntityNewBoat.Type.JUNGLE, true)),
	ACACIA_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat(EntityNewBoat.Type.ACACIA, false)),
	ACACIA_CHEST_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat(EntityNewBoat.Type.ACACIA, true)),
	DARK_OAK_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat(EntityNewBoat.Type.DARK_OAK, false)),
	DARK_OAK_CHEST_BOAT(ConfigBlocksItems.enableNewBoats, new ItemNewBoat(EntityNewBoat.Type.DARK_OAK, true));

	public static final ModItems[] CHEST_BOATS = new ModItems[] {OAK_BOAT, SPRUCE_CHEST_BOAT, BIRCH_CHEST_BOAT, JUNGLE_CHEST_BOAT, ACACIA_CHEST_BOAT, DARK_OAK_CHEST_BOAT};
	public static final ModItems[] BOATS = new ModItems[] {OAK_BOAT, SPRUCE_BOAT, BIRCH_BOAT, JUNGLE_BOAT, ACACIA_BOAT, DARK_OAK_BOAT};
	public static final ModItems[] ITEM_SIGNS = new ModItems[] {ITEM_SIGN_SPRUCE, ITEM_SIGN_BIRCH, ITEM_SIGN_JUNGLE, ITEM_SIGN_ACACIA, ITEM_SIGN_DARK_OAK};

	/*
	 * Stand-in static final fields because some mods incorrectly referenced my code directly.
	 * They should be using GameRegistry.findItem but it is what it is I guess.
	 */

	//D-Mod
	@Deprecated public static final Item sweet_berries = SWEET_BERRIES.get();

	public static void init() {
		for(ModItems item : values()) {
			if(item.isEnabled()) { //Honestly what do you think it's doing lmfao
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
