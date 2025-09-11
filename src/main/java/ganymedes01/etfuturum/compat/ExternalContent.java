package ganymedes01.etfuturum.compat;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class ExternalContent {

	public enum Blocks {
		GREGTECH_MACHINE("Gregtech", "gt.blockmachines"),

		CFB_CAMPFIRE("campfirebackport", "campfire"),
		CFB_CAMPFIRE_BASE("campfirebackport", "campfire_base"),
		CFB_SOUL_CAMPFIRE("campfirebackport", "soul_campfire"),
		CFB_SOUL_CAMPFIRE_BASE("campfirebackport", "soul_campfire_base"),
		CFB_FOXFIRE_CAMPFIRE("campfirebackport", "foxfire_campfire"),
		CFB_FOXFIRE_CAMPFIRE_BASE("campfirebackport", "foxfire_campfire_base"),
		CFB_SHADOW_CAMPFIRE("campfirebackport", "shadow_campfire"),
		CFB_SHADOW_CAMPFIRE_BASE("campfirebackport", "shadow_campfire_base"),

		BAMBOO_CAMPFIRE("BambooMod", "campfire"),

		ECRU_LEAVES_FIRE("mod_ecru_MapleTree", "ecru_BlockFallenLeavesFire"),

		TCON_GRAVEL_ORE("TConstruct", "GravelOre"),
		TCON_MULTIBRICK("TConstruct", "decoration.multibrick"),
		TCON_MULTIBRICK_FANCY("TConstruct", "decoration.multibrickfancy"),
		TCON_METAL("TConstruct", "MetalBlock"),
		TCON_SMELTERY("TConstruct", "Smeltery"),
		TCON_SEARED_BLOCK("TConstruct", "SearedBlock"),

		NATURA_HEAT_SAND("Natura", "heatsand"),
		NATURA_TAINTED_SOIL("Natura", "soil.tainted"),

		NETHERLICIOUS_NETHER_GRAVEL("netherlicious", "Nether_Gravel"),
		NETHERLICIOUS_SOUL_SOIL("netherlicious", "SoulSoil"),
		NETHERLICIOUS_BONE_BLOCK("netherlicious", "BoneBlock"),

		ENDERLICIOUS_END_ROCK("enderlicious", "EndRock"),
		ENDERLICIOUS_SAND("enderlicious", "EndSand"),

		HEE_END_STONE("HardcoreEnderExpansion", "end_stone_terrain"),

		AE2_CERTUS_QUARTZ_ORE("appliedenergistics2", "tile.OreQuartz"),
		AE2_CHARGED_CERTUS_QUARTZ_ORE("appliedenergistics2", "tile.OreQuartzCharged"),

		IRON_CHEST("IronChest", "BlockIronChest"),

		UTD_WHITE_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_white"),
		UTD_ORANGE_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_orange"),
		UTD_MAGENTA_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_magenta"),
		UTD_LIGHT_BLUE_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_light_blue"),
		UTD_YELLOW_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_yellow"),
		UTD_LIME_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_lime"),
		UTD_PINK_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_PINK"),
		UTD_GRAY_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_gray"),
		UTD_LIGHT_GRAY_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_light_gray"),
		UTD_CYAN_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_cyan"),
		UTD_PURPLE_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_purple"),
		UTD_BLUE_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_blue"),
		UTD_BROWN_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_brown"),
		UTD_GREEN_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_green"),
		UTD_RED_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_red"),
		UTD_BLACK_GLAZED_TERRACOTTA("uptodate", "glazed_terracotta_black"),

		VN_GLAZED_TERRACOTTA_1("VillageNames", "glazedTerracotta"),
		VN_GLAZED_TERRACOTTA_2("VillageNames", "glazedTerracotta2"),
		VN_GLAZED_TERRACOTTA_3("VillageNames", "glazedTerracotta3"),
		VN_GLAZED_TERRACOTTA_4("VillageNames", "glazedTerracotta4"),

		ARS_MAGICA_2_ORE("arsmagica2", "vinteumOre"),

		THAUMCRAFT_ORE("Thaumcraft", "blockCustomOre"),
		THAUMCRAFT_AIRY("Thaumcraft", "blockAiry"),
		THAUMCRAFT_TABLE("Thaumcraft", "blockTable"),

		BOP_GEM_ORE("BiomesOPlenty", "gemOre"),

		DRACONIUM_ORE("DraconicEvolution", "draconiumOre"),

		PROJECT_RED_ORE("ProjRed|Exploration", "projectred.exploration.ore"),

		BR_YELLORITE_ORE("BigReactors", "YelloriteOre"),

		BP_TESLATITE_ORE("bluepower", "teslatite_ore"),
		BP_RUBY_ORE("bluepower", "ruby_ore"),
		BP_SAPPHIRE_ORE("bluepower", "sapphire_ore"),
		BP_AMETHYST_ORE("bluepower", "amethyst_ore"),
		/*BP_MALACHITE_ORE("bluepower", "malachite_ore"), Unused malachite texture in files? I'll have this set up here in case a fork or something uses it*/

		FISK_TUTRIDIUM_ORE("fiskheroes", "tutridium_ore"),
		FISK_TUTRIDIUM_SPECKLED_STONE("fiskheroes", "tutridium_stone"),
		FISK_VIBRANIUM_ORE("fiskheroes", "vibranium_ore"),
		FISK_DWARF_STAR_ORE("fiskheroes", "dwarf_star_ore"),
		FISK_OLIVINE_ORE("fiskheroes", "olivine_ore"),
		FISK_ETERNIUM_ORE("fiskheroes", "eternium_ore"),
		FISK_ETERNIUM_INFUSED_STONE("fiskheroes", "eternium_stone"),

		FISK_NEXUS_BRICKS("fiskheroes", "nexus_bricks"),

		DBC_WARENAI_ORE("jinryuudragonblockc", "tile.BlockOreWrenai"),
		DBC_JJAY_ORE("jinryuudragonblockc", "tile.JJayore"),
		DBC_DLOG_ORE("jinryuudragonblockc", "tile.Dlogore"),
		DBC_LEHNORI_ORE("jinryuudragonblockc", "tile.Lehnoriore"),

		SIMPLEORES_ADAMANTIUM_ORE("simpleores", "adamantium_ore"),

		SIMPLEORES_ADAMANTIUM_BLOCK("simpleores", "adamantium_block"),

		DQ_ROCKBOMB_ORE("DQMIIINext", "BlockOreBakudanisi"),
		DQ_BRIGHTEN_ORE("DQMIIINext", "BlockOreHikarinoisi"),
		DQ_LUCIDA_ORE("DQMIIINext", "BlockOreHosinokakera"),
		DQ_RESURROCK_ORE("DQMIIINext", "BlockOreInotinoisi"),
		DQ_MIRRORSTONE_ORE("DQMIIINext", "BlockOreKagaminoisi"),
		DQ_ICE_CRYSTAL_ORE("DQMIIINext", "BlockOreKoorinokessyou"),
		DQ_MINIMEDAL_ORE("DQMIIINext", "BlockOreLittlemedal"),
		DQ_DENSINIUM_ORE("DQMIIINext", "BlockOreMetaru"),
		DQ_GLASS_FRIT_ORE("DQMIIINext", "BlockOreMigakizuna"),
		DQ_MYTHRIL_ORE("DQMIIINext", "BlockOreMisuriru"),
		DQ_LUNAR_DIAMOND_ORE("DQMIIINext", "BlockOreMoon"),
		DQ_PLATINUM_ORE("DQMIIINext", "BlockOrePuratina"),
		DQ_CORUNDUM_ORE("DQMIIINext", "BlockOreRubi"),
		DQ_SUNSTONE_ORE("DQMIIINext", "BlockOreTaiyounoisi"),
		DQ_ALLOYED_IRON_ORE("DQMIIINext", "BlockOreTekkouseki"),
		DQ_CHRONOCRYSTAL_ORE("DQMIIINext", "BlockOreTokinosuisyou"),
		DQ_VOLCANIC_ORE("DQMIIINext", "BlockOreYougansekinokakera"),
		;

		private Block block;
		private final Supplier<Block> blockSupplier;

		Blocks(Supplier<Block> blockSupplier) {
			this.blockSupplier = blockSupplier;
		}

		Blocks(String modID, String blockID) {
			this(() -> GameRegistry.findBlock(modID, blockID));
		}

		public Block get() {
			if (block == null) {
				block = blockSupplier.get();
			}
			return block;
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

		public boolean isEnabled() {
			return get() != null;
		}
	}

	public enum Items {
		EXTRAUTILS_WATERING_CAN("ExtraUtilities", "watering_can"),

		SIMPLEORES_ADAMANTIUM_INGOT("simpleores", "adamantium_ingot"),

		BLUEPOWER_CIRCUIT_PLATE("bluepower", "stone_tile"),
		PROJECTRED_CIRCUIT_PLATE("ProjRed|Core", "projectred.core.part"),

		THAUMCRAFT_EARTHMOVER_SHOVEL("Thaumcraft", "ItemShovelElemental"),
		BOTANIA_MANASTEEL_SHOVEL("Botania", "manasteelShovel"),
		;

		private Item item;
		private final Supplier<Item> blockSupplier;

		Items(Supplier<Item> blockSupplier) {
			this.blockSupplier = blockSupplier;
		}

		Items(String modID, String itemID) {
			this(() -> GameRegistry.findItem(modID, itemID));
		}

		public Item get() {
			if (item == null) {
				item = blockSupplier.get();
			}
			return item;
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

		public boolean isEnabled() {
			return get() != null;
		}
	}
}