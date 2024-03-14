package ganymedes01.etfuturum.compat;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.function.Supplier;

public class ExternalContent {

	public enum Blocks {
		CFB_CAMPFIRE(() -> GameRegistry.findBlock("campfirebackport", "campfire")),
		CFB_SOUL_CAMPFIRE(() -> GameRegistry.findBlock("campfirebackport", "soul_campfire")),
		CFB_CAMPFIRE_BASE(() -> GameRegistry.findBlock("campfirebackport", "campfire_base")),
		CFB_SOUL_CAMPFIRE_BASE(() -> GameRegistry.findBlock("campfirebackport", "soul_campfire_base")),

		TCON_GRAVEL_ORE(() -> GameRegistry.findBlock("TConstruct", "GravelOre")),

		NATURA_HEAT_SAND(() -> GameRegistry.findBlock("Natura", "heatsand")),
		NATURA_TAINTED_SOIL(() -> GameRegistry.findBlock("Natura", "soil.tainted")),

		NETHERLICIOUS_NETHER_GRAVEL(() -> GameRegistry.findBlock("netherlicious", "Nether_Gravel")),
		NETHERLICIOUS_SOUL_SOIL(() -> GameRegistry.findBlock("netherlicious", "SoulSoil")),
		NETHERLICIOUS_BONE_BLOCK(() -> GameRegistry.findBlock("netherlicious", "BoneBlock")),

		ENDERLICIOUS_END_ROCK(() -> GameRegistry.findBlock("enderlicious", "EndRock")),
		ENDERLICIOUS_SAND(() -> GameRegistry.findBlock("enderlicious", "EndSand")),

		HEE_END_STONE(() -> GameRegistry.findBlock("HardcoreEnderExpansion", "end_stone_terrain")),

		AE2_CERTUS_QUARTZ_ORE(() -> GameRegistry.findBlock("appliedenergistics2", "tile.OreQuartz")),
		AE2_CHARGED_CERTUS_QUARTZ_ORE(() -> GameRegistry.findBlock("appliedenergistics2", "tile.OreQuartzCharged")),

		ARS_MAGICA_2_ORE(() -> GameRegistry.findBlock("arsmagica2", "vinteumOre")),

		THAUMCRAFT_ORE(() -> GameRegistry.findBlock("Thaumcraft", "blockCustomOre")),

		BOP_GEM_ORE(() -> GameRegistry.findBlock("BiomesOPlenty", "gemOre")),

		DRACONIUM_ORE(() -> GameRegistry.findBlock("DraconicEvolution", "draconiumOre")),

		PROJECT_RED_ORE(() -> GameRegistry.findBlock("ProjRed|Exploration", "projectred.exploration.ore")),
		
		BR_YELLORITE_ORE(() -> GameRegistry.findBlock("BigReactors", "YelloriteOre")),

		BP_TESLATITE_ORE(() -> GameRegistry.findBlock("bluepower", "teslatite_ore")),
		BP_RUBY_ORE(() -> GameRegistry.findBlock("bluepower", "ruby_ore")),
		BP_SAPPHIRE_ORE(() -> GameRegistry.findBlock("bluepower", "sapphire_ore")),
		BP_AMETHYST_ORE(() -> GameRegistry.findBlock("bluepower", "amethyst_ore")),
		/*BP_MALACHITE_ORE(() -> GameRegistry.findBlock("bluepower", "malachite_ore")), Unused malachite texture in files? I'll have this set up here in case a fork or something uses it*/;

		private Block block;
		private final Supplier<Block> blockSupplier;

		Blocks(Supplier<Block> blockSupplier) {
			this.blockSupplier = blockSupplier;
		}

		public Block get() {
			if (block == null) {
				block = blockSupplier.get();
			}
			return block;
		}
	}

	public enum Items {
		EXTRAUTILS_WATERING_CAN(() -> GameRegistry.findItem("ExtraUtilities", "watering_can"));

		private Item item;
		private final Supplier<Item> blockSupplier;

		Items(Supplier<Item> blockSupplier) {
			this.blockSupplier = blockSupplier;
		}

		public Item get() {
			if (item == null) {
				item = blockSupplier.get();
			}
			return item;
		}
	}
}
