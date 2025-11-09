package ganymedes01.etfuturum.compat;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;

public class CompatThaumcraft {

	public static void doAspects() {
		if (ModsList.FORBIDDEN_MAGIC.isLoaded()) {
			CompatForbiddenMagic.doDarkAspects();
		} else {
			for (int i = 0; i < ModBlocks.BEDS.length; i++) {
				ThaumcraftApi.registerObjectTag(ModBlocks.BEDS[i].newItemStack(), new AspectList());
			}
			ThaumcraftApi.registerObjectTag(ModBlocks.OBSERVER.newItemStack(), new AspectList().add(Aspect.MECHANISM, 4).add(Aspect.EARTH, 4).add(Aspect.ENTROPY, 4).add(Aspect.SENSES, 2));

			ThaumcraftApi.registerObjectTag(ModBlocks.RED_NETHERBRICK.newItemStack(1, 0), new AspectList(new ItemStack(Blocks.nether_brick)));
			ThaumcraftApi.registerObjectTag(ModBlocks.RED_NETHERBRICK.newItemStack(1, 1), new AspectList(new ItemStack(Blocks.nether_brick)).add(Aspect.ENTROPY, 1));
			ThaumcraftApi.registerObjectTag(ModBlocks.RED_NETHERBRICK.newItemStack(1, 2), new AspectList(new ItemStack(Blocks.nether_brick)).add(Aspect.MAGIC, 1));

			ThaumcraftApi.registerObjectTag(ModBlocks.BLACKSTONE.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.EARTH, 1).add(Aspect.DARKNESS, 1));
			ThaumcraftApi.registerObjectTag(ModBlocks.BLACKSTONE.newItemStack(1, 4), new AspectList().add(Aspect.EARTH, 1).add(Aspect.DARKNESS, 1).add(Aspect.BEAST, 1));
			ThaumcraftApi.registerObjectTag(ModBlocks.GILDED_BLACKSTONE.newItemStack(), new AspectList().add(Aspect.GREED, 1).add(Aspect.DARKNESS, 1));

			ThaumcraftApi.registerObjectTag(ModItems.NETHERITE_INGOT.newItemStack(), new AspectList().add(Aspect.METAL, 8).add(Aspect.GREED, 4).add(Aspect.ARMOR, 2));
			ThaumcraftApi.registerObjectTag(ModBlocks.ANCIENT_DEBRIS.newItemStack(), new AspectList().add(Aspect.METAL, 4).add(Aspect.ARMOR, 2).add(Aspect.EARTH, 1));
			ThaumcraftApi.registerObjectTag(ModItems.NETHERITE_SCRAP.newItemStack(), new AspectList().add(Aspect.METAL, 4).add(Aspect.ARMOR, 2).add(Aspect.FIRE, 1));

			ThaumcraftApi.registerObjectTag(ModBlocks.NETHER_GOLD_ORE.newItemStack(), new AspectList(new ItemStack(Blocks.gold_ore)));
		}

		// Entities
		// ThaumcraftApi.registerEntityTag("etfuturum.entityname", new AspectList(), new ThaumcraftApi.EntityTagsNBT[0] );
		ThaumcraftApi.registerEntityTag("etfuturum.rabbit", new AspectList().add(Aspect.BEAST, 1).add(Aspect.EARTH, 1).add(Aspect.MOTION, 1));
		ThaumcraftApi.registerEntityTag("etfuturum.bee", new AspectList().add(Aspect.BEAST, 1).add(Aspect.FLIGHT, 1).add(Aspect.POISON, 1).add(Aspect.WEAPON, 1));

		ThaumcraftApi.registerEntityTag("etfuturum.endermite", new AspectList().add(Aspect.BEAST, 1).add(Aspect.ELDRITCH, 1).add(Aspect.TRAVEL, 1));
		ThaumcraftApi.registerEntityTag("etfuturum.snow_golem", new AspectList().add(Aspect.COLD, 3).add(Aspect.WATER, 1));

		ThaumcraftApi.registerEntityTag("etfuturum.end_crystal", new AspectList().add(Aspect.ELDRITCH, 3).add(Aspect.MAGIC, 3).add(Aspect.HEAL, 3));
		ThaumcraftApi.registerEntityTag("etfuturum.ender_dragon", new AspectList().add(Aspect.ELDRITCH, 20).add(Aspect.BEAST, 20).add(Aspect.ENTROPY, 20));
		ThaumcraftApi.registerEntityTag("etfuturum.shulker", new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.ARMOR, 3).add(Aspect.FLIGHT, 3));
		ThaumcraftApi.registerEntityTag("etfuturum.shulker_candy", new AspectList().add(Aspect.WEAPON, 3).add(Aspect.FLIGHT, 2));

		ThaumcraftApi.registerEntityTag("etfuturum.villager_zombie", new AspectList().add(Aspect.UNDEAD, 2).add(Aspect.MAN, 1).add(Aspect.EARTH, 1));

		ThaumcraftApi.registerEntityTag("etfuturum.husk", new AspectList().add(Aspect.UNDEAD, 3).add(Aspect.MAN, 2).add(Aspect.FIRE, 2));
		ThaumcraftApi.registerEntityTag("etfuturum.stray", new AspectList().add(Aspect.UNDEAD, 3).add(Aspect.MAN, 2).add(Aspect.COLD, 2));

		ThaumcraftApi.registerEntityTag("etfuturum.wooden_armorstand", new AspectList().add(Aspect.TREE, 4).add(Aspect.ARMOR, 1));
		ThaumcraftApi.registerEntityTag("etfuturum.new_boat", new AspectList().add(Aspect.WATER, 4).add(Aspect.TRAVEL, 4).add(Aspect.TREE, 3));
		ThaumcraftApi.registerEntityTag("etfuturum.chest_boat", new AspectList().add(Aspect.WATER, 4).add(Aspect.TRAVEL, 4).add(Aspect.TREE, 6).add(Aspect.VOID, 4));


		// Items
		// ThaumcraftApi.registerObjectTag(ModItems.ITEMNAME.newItemStack(), new AspectList() );
		ThaumcraftApi.registerObjectTag(ModItems.BEETROOT.newItemStack(), new AspectList().add(Aspect.CROP, 1).add(Aspect.HUNGER, 1).add(Aspect.GREED, 1));
		ThaumcraftApi.registerObjectTag(ModItems.BEETROOT_SEEDS.newItemStack(), new AspectList().add(Aspect.PLANT, 1));
		ThaumcraftApi.registerObjectTag(ModItems.CHORUS_FRUIT.newItemStack(), new AspectList().add(Aspect.ELDRITCH, 1).add(Aspect.SENSES, 1).add(Aspect.PLANT, 1));
		ThaumcraftApi.registerObjectTag(ModItems.CHORUS_FRUIT_POPPED.newItemStack(), new AspectList().add(Aspect.ELDRITCH, 1).add(Aspect.SENSES, 1).add(Aspect.PLANT, 1).add(Aspect.FIRE, 1));
		ThaumcraftApi.registerObjectTag(ModItems.DRAGON_BREATH.newItemStack(), new AspectList().add(Aspect.DARKNESS, 2).add(Aspect.AIR, 2).add(Aspect.FIRE, 2).add(Aspect.MAGIC, 2));
		ThaumcraftApi.registerObjectTag(ModItems.END_CRYSTAL.newItemStack(), new AspectList().add(Aspect.MAGIC, 3).add(Aspect.ELDRITCH, 3).add(Aspect.HEAL, 3)); // Matches entity
		ThaumcraftApi.registerObjectTag(ModItems.SHULKER_SHELL.newItemStack(), new AspectList().add(Aspect.ARMOR, 2).add(Aspect.ELDRITCH, 2));
		ThaumcraftApi.registerObjectTag(ModItems.ELYTRA.newItemStack(), new AspectList().add(Aspect.FLIGHT, 4).add(Aspect.MOTION, 3).add(Aspect.TRAVEL, 3));
		ThaumcraftApi.registerObjectTag(ModItems.PRISMARINE_CRYSTALS.newItemStack(), new AspectList().add(Aspect.WATER, 1).add(Aspect.CRYSTAL, 1).add(Aspect.LIGHT, 1));
		ThaumcraftApi.registerObjectTag(ModItems.PRISMARINE_SHARD.newItemStack(), new AspectList().add(Aspect.WATER, 1).add(Aspect.EARTH, 1));
		ThaumcraftApi.registerObjectTag(ModItems.TOTEM_OF_UNDYING.newItemStack(), new AspectList().add(Aspect.HEAL, 5).add(Aspect.ARMOR, 5).add(Aspect.LIFE, 5).add(Aspect.UNDEAD, 5));

		ThaumcraftApi.registerObjectTag(ModItems.MUTTON_RAW.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.FLESH, 3).add(Aspect.LIFE, 1).add(Aspect.BEAST, 1));
		ThaumcraftApi.registerObjectTag(ModItems.MUTTON_COOKED.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.FLESH, 3).add(Aspect.HUNGER, 3).add(Aspect.CRAFT, 1));
		ThaumcraftApi.registerObjectTag(ModItems.RABBIT_RAW.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.FLESH, 3).add(Aspect.LIFE, 1).add(Aspect.BEAST, 1));
		ThaumcraftApi.registerObjectTag(ModItems.RABBIT_COOKED.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.FLESH, 3).add(Aspect.HUNGER, 3).add(Aspect.CRAFT, 1));
		ThaumcraftApi.registerObjectTag(ModItems.RABBIT_HIDE.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.BEAST, 1).add(Aspect.ARMOR, 1));
		ThaumcraftApi.registerObjectTag(ModItems.RABBIT_FOOT.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.BEAST, 1).add(Aspect.ARMOR, 1).add(Aspect.MOTION, 2));

		ThaumcraftApi.registerObjectTag(ModItems.TIPPED_ARROW.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.WEAPON, 1).add(Aspect.MAGIC, 2));

		ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.PIGSTEP_RECORD.get()), new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.GREED, 4).add(Aspect.FIRE, 2).add(Aspect.BEAST, 2));
		ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.OTHERSIDE_RECORD.get()), new AspectList().add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.GREED, 4).add(Aspect.ELDRITCH, 4));

		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.TRAP, 1).add(Aspect.WATER, 1).add(Aspect.MAGIC, 2));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 0), new AspectList().add(Aspect.CRYSTAL, 1).add(Aspect.WATER, 1));
		// Regen
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8193), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.HEAL, 3));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8225), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.HEAL, 6).add(Aspect.MAGIC, 2));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8257), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.HEAL, 3));
		// Speed
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8194), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.MOTION, 3));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8226), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.MOTION, 6).add(Aspect.MAGIC, 2));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8258), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.MOTION, 3));
		// Fire Res
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8227), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.FIRE, 2).add(Aspect.ARMOR, 1));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8259), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.FIRE, 2).add(Aspect.ARMOR, 1));
		// Poison
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8196), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.POISON, 3));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8228), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.POISON, 6).add(Aspect.MAGIC, 2));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8260), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.POISON, 3));
		// Healing
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8229), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.HEAL, 3));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8261), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.HEAL, 3));
		// Night Vision
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8230), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.SENSES, 3));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8262), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.SENSES, 3));
		// Weakness
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8232), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.DEATH, 3));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8264), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.DEATH, 3));
		// Strength
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8201), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.WEAPON, 3));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8233), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.WEAPON, 6).add(Aspect.MAGIC, 2));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8264), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.WEAPON, 3));
		// Slowness
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8234), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.TRAP, 3));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8266), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.TRAP, 3));
		// Leaping
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8235), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.FLIGHT, 3));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8267), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.FLIGHT, 3));
		// Damage
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8236), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.DEATH, 3));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8268), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.DEATH, 3));
		// Water Breathing
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8237), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.AIR, 3));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8269), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.AIR, 3));
		// Invis
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8238), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.SENSES, 3));
		ThaumcraftApi.registerObjectTag(ModItems.LINGERING_POTION.newItemStack(1, 8270), new AspectList(ModItems.LINGERING_POTION.newItemStack(1, OreDictionary.WILDCARD_VALUE)).add(Aspect.SENSES, 3));

		ThaumcraftApi.registerObjectTag(ModItems.DYE.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Items.dye)));
		ThaumcraftApi.registerObjectTag(ModItems.NUGGET_IRON.newItemStack(), new AspectList().add(Aspect.METAL, 1));
		ThaumcraftApi.registerObjectTag(ModItems.AMETHYST_SHARD.newItemStack(), new AspectList().add(Aspect.CRYSTAL, 1).add(Aspect.SENSES, 1).add(Aspect.GREED, 1));
		ThaumcraftApi.registerObjectTag(ModItems.WOODEN_ARMORSTAND.newItemStack(), new AspectList().add(Aspect.TREE, 4).add(Aspect.ARMOR, 1));

		ThaumcraftApi.registerObjectTag(ModItems.BAMBOO.newItemStack(), new AspectList().add(Aspect.PLANT, 1).add(Aspect.TREE, 1).add(Aspect.AIR, 1));
		ThaumcraftApi.registerObjectTag(ModItems.SWEET_BERRIES.newItemStack(), new AspectList().add(Aspect.PLANT, 1).add(Aspect.HUNGER, 1));
		ThaumcraftApi.registerObjectTag(ModItems.SUSPICIOUS_STEW.newItemStack(), new AspectList(new ItemStack(Items.mushroom_stew)));

		ThaumcraftApi.registerObjectTag(ModItems.NETHERITE_HELMET.newItemStack(), new AspectList(new ItemStack(Items.diamond_helmet)).add(new AspectList(ModItems.NETHERITE_INGOT.newItemStack())));
		ThaumcraftApi.registerObjectTag(ModItems.NETHERITE_CHESTPLATE.newItemStack(), new AspectList(new ItemStack(Items.diamond_chestplate)).add(new AspectList(ModItems.NETHERITE_INGOT.newItemStack())));
		ThaumcraftApi.registerObjectTag(ModItems.NETHERITE_LEGGINGS.newItemStack(), new AspectList(new ItemStack(Items.diamond_leggings)).add(new AspectList(ModItems.NETHERITE_INGOT.newItemStack())));
		ThaumcraftApi.registerObjectTag(ModItems.NETHERITE_BOOTS.newItemStack(), new AspectList(new ItemStack(Items.diamond_boots)).add(new AspectList(ModItems.NETHERITE_INGOT.newItemStack())));

		ThaumcraftApi.registerObjectTag(ModItems.NETHERITE_SWORD.newItemStack(), new AspectList(new ItemStack(Items.diamond_sword)).add(new AspectList(ModItems.NETHERITE_INGOT.newItemStack())));
		ThaumcraftApi.registerObjectTag(ModItems.NETHERITE_SPADE.newItemStack(), new AspectList(new ItemStack(Items.diamond_shovel)).add(new AspectList(ModItems.NETHERITE_INGOT.newItemStack())));
		ThaumcraftApi.registerObjectTag(ModItems.NETHERITE_PICKAXE.newItemStack(), new AspectList(new ItemStack(Items.diamond_pickaxe)).add(new AspectList(ModItems.NETHERITE_INGOT.newItemStack())));
		ThaumcraftApi.registerObjectTag(ModItems.NETHERITE_AXE.newItemStack(), new AspectList(new ItemStack(Items.diamond_axe)).add(new AspectList(ModItems.NETHERITE_INGOT.newItemStack())));
		ThaumcraftApi.registerObjectTag(ModItems.NETHERITE_HOE.newItemStack(), new AspectList(new ItemStack(Items.diamond_hoe)).add(new AspectList(ModItems.NETHERITE_INGOT.newItemStack())));

		ThaumcraftApi.registerObjectTag(ModItems.COPPER_INGOT.newItemStack(1, 0), new AspectList().add(Aspect.METAL, 3).add(Aspect.EXCHANGE, 1));
		ThaumcraftApi.registerObjectTag(ModItems.RAW_ORE.newItemStack(1, 0), new AspectList().add(Aspect.METAL, 2).add(Aspect.ENTROPY, 1).add(Aspect.EXCHANGE, 1));
		ThaumcraftApi.registerObjectTag(ModItems.RAW_ORE.newItemStack(1, 1), new AspectList().add(Aspect.METAL, 3).add(Aspect.ENTROPY, 1));
		ThaumcraftApi.registerObjectTag(ModItems.RAW_ORE.newItemStack(1, 2), new AspectList().add(Aspect.METAL, 2).add(Aspect.ENTROPY, 1).add(Aspect.GREED, 1));

		ThaumcraftApi.registerObjectTag(ModItems.HONEY_BOTTLE.newItemStack(), new AspectList().add(Aspect.HUNGER, 2).add(Aspect.TRAP, 1).add(Aspect.HEAL, 1));
		ThaumcraftApi.registerObjectTag(ModItems.HONEYCOMB.newItemStack(), new AspectList().add(Aspect.ORDER, 2).add(Aspect.HUNGER, 1));

		// Blocks
		// ThaumcraftApi.registerObjectTag(ModBlocks.BLOCKNAME.newItemStack(), new AspectList() );
		ThaumcraftApi.registerObjectTag(ModBlocks.BEACON.newItemStack(), new AspectList(new ItemStack(Blocks.beacon)));
		ThaumcraftApi.registerObjectTag(ModBlocks.BEETROOTS.newItemStack(), new AspectList().add(Aspect.PLANT, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.BREWING_STAND.newItemStack(), new AspectList(new ItemStack(Blocks.brewing_stand)));
		ThaumcraftApi.registerObjectTag(ModBlocks.CHORUS_FLOWER.newItemStack(), new AspectList().add(Aspect.ELDRITCH, 1).add(Aspect.SENSES, 1).add(Aspect.PLANT, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.CHORUS_PLANT.newItemStack(), new AspectList().add(Aspect.ELDRITCH, 1).add(Aspect.PLANT, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.DAYLIGHT_DETECTOR.newItemStack(), new AspectList(new ItemStack(Blocks.daylight_detector)));
		ThaumcraftApi.registerObjectTag(ModBlocks.ENCHANTMENT_TABLE.newItemStack(), new AspectList(new ItemStack(Blocks.enchanting_table)));ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.ANVIL.get()), new AspectList(new ItemStack(Blocks.anvil)));
		ThaumcraftApi.registerObjectTag(ModBlocks.IRON_TRAPDOOR.newItemStack(), new AspectList().add(Aspect.METAL, 12).add(Aspect.MECHANISM, 2).add(Aspect.MOTION, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.BARREL.newItemStack(), new AspectList(new ItemStack(Blocks.chest)));
		ThaumcraftApi.registerObjectTag(ModBlocks.COMPOSTER.newItemStack(), new AspectList().add(Aspect.TREE, 5).add(Aspect.EXCHANGE, 2));
		ThaumcraftApi.registerObjectTag(ModBlocks.SMITHING_TABLE.newItemStack(), new AspectList().add(Aspect.CRAFT, 4).add(Aspect.METAL, 2).add(Aspect.TREE, 4));
		ThaumcraftApi.registerObjectTag(ModBlocks.BANNER.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.CLOTH, 6).add(Aspect.CRAFT, 4).add(Aspect.SENSES, 2));
		ThaumcraftApi.registerObjectTag(ModBlocks.PURPUR_BLOCK.newItemStack(), new AspectList().add(Aspect.ELDRITCH, 1).add(Aspect.SENSES, 1).add(Aspect.EARTH, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.PURPUR_PILLAR.newItemStack(), new AspectList().add(Aspect.ELDRITCH, 1).add(Aspect.SENSES, 1).add(Aspect.EARTH, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.END_ROD.newItemStack(), new AspectList().add(Aspect.LIGHT, 2).add(Aspect.FIRE, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.GRASS_PATH.newItemStack(), new AspectList(new ItemStack(Blocks.dirt)).add(Aspect.ORDER, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.FROSTED_ICE.newItemStack(), new AspectList(new ItemStack(Blocks.ice)).add(Aspect.ENTROPY, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.BEE_NEST.newItemStack(), new AspectList().add(Aspect.BEAST, 1).add(Aspect.FLIGHT, 1).add(Aspect.ORDER, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.BEEHIVE.newItemStack(), new AspectList().add(Aspect.BEAST, 2).add(Aspect.FLIGHT, 2).add(Aspect.CRAFT, 2));
		ThaumcraftApi.registerObjectTag(ModBlocks.TARGET.newItemStack(), new AspectList().add(Aspect.CROP, 9).add(Aspect.MECHANISM, 4).add(Aspect.WEAPON, 2));

		ThaumcraftApi.registerObjectTag(ModBlocks.LOG_STRIPPED.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Blocks.log)));
		ThaumcraftApi.registerObjectTag(ModBlocks.LOG2_STRIPPED.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Blocks.log2)));
		ThaumcraftApi.registerObjectTag(ModBlocks.CHERRY_LOG.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Blocks.log)));
		ThaumcraftApi.registerObjectTag(ModBlocks.BAMBOO_BLOCK.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Blocks.log)));
		ThaumcraftApi.registerObjectTag(ModBlocks.WOOD_PLANKS.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Blocks.planks)));
		ThaumcraftApi.registerObjectTag(ModBlocks.SAPLING.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Blocks.sapling)));
		ThaumcraftApi.registerObjectTag(ModBlocks.LEAVES.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Blocks.leaves)));
		ThaumcraftApi.registerObjectTag(ModBlocks.PINK_PETALS.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Blocks.tallgrass)));

		ThaumcraftApi.registerObjectTag(ModBlocks.MAGMA.newItemStack(), new AspectList().add(Aspect.FIRE, 2).add(Aspect.EARTH, 1));

		ThaumcraftApi.registerObjectTag(ModBlocks.LANTERN.newItemStack(),new AspectList().add(Aspect.METAL, 6).add(Aspect.LIGHT, 2));
		ThaumcraftApi.registerObjectTag(ModBlocks.SOUL_LANTERN.newItemStack(), new AspectList(new ItemStack(ModBlocks.LANTERN.get())).add(Aspect.SOUL, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.SOUL_TORCH.newItemStack(), new AspectList(new ItemStack(Blocks.torch)).add(Aspect.SOUL, 1));

		ThaumcraftApi.registerObjectTag(ModBlocks.ROSE.newItemStack(), new AspectList(new ItemStack(Blocks.red_flower)));
		ThaumcraftApi.registerObjectTag(ModBlocks.CORNFLOWER.newItemStack(), new AspectList(new ItemStack(Blocks.red_flower)));
		ThaumcraftApi.registerObjectTag(ModBlocks.LILY_OF_THE_VALLEY.newItemStack(), new AspectList(new ItemStack(Blocks.red_flower)).add(Aspect.POISON, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.WITHER_ROSE.newItemStack(1, 0), new AspectList().add(Aspect.PLANT, 1).add(Aspect.POISON, 1).add(Aspect.DEATH, 1).add(Aspect.SENSES, 1));

		ThaumcraftApi.registerObjectTag(ModBlocks.BROWN_MUSHROOM.newItemStack(), new AspectList(new ItemStack(Blocks.brown_mushroom)));
		ThaumcraftApi.registerObjectTag(ModBlocks.RED_MUSHROOM.newItemStack(), new AspectList(new ItemStack(Blocks.red_mushroom)));

		ThaumcraftApi.registerObjectTag(ModBlocks.RED_SANDSTONE.newItemStack(1, 0), new AspectList(new ItemStack(Blocks.sandstone)));
		ThaumcraftApi.registerObjectTag(ModBlocks.RED_SANDSTONE.newItemStack(1, 1), new AspectList(new ItemStack(Blocks.sandstone,1,1)) );
		ThaumcraftApi.registerObjectTag(ModBlocks.RED_SANDSTONE.newItemStack( 1, 2), new AspectList(new ItemStack(Blocks.sandstone,1,2)) );
		ThaumcraftApi.registerObjectTag(ModBlocks.SMOOTH_SANDSTONE.newItemStack(), new AspectList(new ItemStack(Blocks.sandstone, 1, 2)));
		ThaumcraftApi.registerObjectTag(ModBlocks.SMOOTH_RED_SANDSTONE.newItemStack(), new AspectList(new ItemStack(Blocks.sandstone, 1, 2)));

		ThaumcraftApi.registerObjectTag(ModBlocks.SPONGE.newItemStack(1, 0), new AspectList().add(Aspect.EARTH, 1).add(Aspect.TRAP, 1).add(Aspect.VOID, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.SPONGE.newItemStack(1, 1), new AspectList().add(Aspect.EARTH, 1).add(Aspect.TRAP, 1).add(Aspect.WATER, 1));

		ThaumcraftApi.registerObjectTag(ModBlocks.STONE.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Blocks.stone)));
		ThaumcraftApi.registerObjectTag(ModBlocks.DEEPSLATE.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Blocks.stone)));
		ThaumcraftApi.registerObjectTag(ModBlocks.COBBLED_DEEPSLATE.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Blocks.cobblestone)));
		ThaumcraftApi.registerObjectTag(ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Blocks.stonebrick)));
		ThaumcraftApi.registerObjectTag(ModBlocks.DEEPSLATE_BRICKS.newItemStack(1, 4), new AspectList(new ItemStack(Blocks.stonebrick, 1, 3)));
		ThaumcraftApi.registerObjectTag(ModBlocks.TUFF.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Blocks.stonebrick)).add(Aspect.FIRE, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.TUFF.newItemStack(1, 4), new AspectList(new ItemStack(Blocks.stonebrick, 1, 3)).add(Aspect.FIRE, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.CALCITE.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.EARTH, 1).add(Aspect.CRYSTAL, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.BASALT.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.EARTH, 1).add(Aspect.FIRE, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.SMOOTH_BASALT.newItemStack(), new AspectList(ModBlocks.BASALT.newItemStack()));
		ThaumcraftApi.registerObjectTag(ModBlocks.SMOOTH_STONE.newItemStack(), new AspectList(new ItemStack(Blocks.stone)));
		ThaumcraftApi.registerObjectTag(ModBlocks.SMOOTH_QUARTZ.newItemStack(), new AspectList(new ItemStack(Blocks.quartz_block)));
		ThaumcraftApi.registerObjectTag(ModBlocks.COARSE_DIRT.newItemStack(), new AspectList(new ItemStack(Blocks.dirt)));
		ThaumcraftApi.registerObjectTag(ModBlocks.OLD_GRAVEL.newItemStack(), new AspectList(new ItemStack(Blocks.gravel)));
		ThaumcraftApi.registerObjectTag(ModBlocks.MUD.newItemStack(), new AspectList(new ItemStack(Blocks.dirt)).add(Aspect.WATER, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.PACKED_MUD.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(new ItemStack(Blocks.dirt)).add(Aspect.ORDER, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.SOUL_SOIL.newItemStack(), new AspectList(new ItemStack(Blocks.soul_sand)));
		ThaumcraftApi.registerObjectTag(ModBlocks.TINTED_GLASS.newItemStack(), new AspectList(new ItemStack(Blocks.glass)).add(Aspect.DARKNESS, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.CRYING_OBSIDIAN.newItemStack(), new AspectList(new ItemStack(Blocks.obsidian)).add(Aspect.WATER, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.END_BRICKS.newItemStack(), new AspectList(new ItemStack(Blocks.end_stone)));

		// Copies
		for (int i = 0; i < ModBlocks.TRAPDOORS.length; i++) {
			ThaumcraftApi.registerObjectTag(ModBlocks.TRAPDOORS[i].newItemStack(), new AspectList(new ItemStack(Blocks.trapdoor)));
		}

		for (int i = 0; i < ModBlocks.FENCE_GATES.length; i++) {
			ThaumcraftApi.registerObjectTag(ModBlocks.FENCE_GATES[i].newItemStack(), new AspectList(new ItemStack(Blocks.fence_gate)));
		}

		for (int i = 0; i < ModBlocks.DOORS.length; i++) {
			ThaumcraftApi.registerObjectTag(ModBlocks.DOORS[i].newItemStack(), new AspectList(new ItemStack(Items.wooden_door)));
		}

		for (int i = 0; i < ModBlocks.BUTTONS.length; i++) {
			ThaumcraftApi.registerObjectTag(ModBlocks.BUTTONS[i].newItemStack(), new AspectList(new ItemStack(Blocks.wooden_button)));
		}
		ThaumcraftApi.registerObjectTag(ModBlocks.POLISHED_BLACKSTONE_BUTTON.newItemStack(), new AspectList(new ItemStack(Blocks.stone_button)));

		for (int i = 0; i < ModBlocks.PRESSURE_PLATES.length; i++) {
			ThaumcraftApi.registerObjectTag(ModBlocks.PRESSURE_PLATES[i].newItemStack(), new AspectList(new ItemStack(Blocks.wooden_pressure_plate)));
		}
		ThaumcraftApi.registerObjectTag(ModBlocks.POLISHED_BLACKSTONE_PRESSURE_PLATE.newItemStack(), new AspectList(new ItemStack(Blocks.stone_pressure_plate)));

		for (int i = 0; i < ModItems.BOATS.length; i++) {
			ThaumcraftApi.registerObjectTag(ModItems.BOATS[i].newItemStack(), new AspectList(new ItemStack(Items.boat)));
		}

		for (int i = 0; i < ModItems.CHEST_BOATS.length; i++) {
			ThaumcraftApi.registerObjectTag(ModItems.CHEST_BOATS[i].newItemStack(), new AspectList(new ItemStack(Items.boat)).add(Aspect.VOID, 4).add(Aspect.TREE, 3));
		}

		for (int i = 0; i < ModBlocks.TERRACOTTA.length; i++) {
			ThaumcraftApi.registerObjectTag(ModBlocks.TERRACOTTA[i].newItemStack(), new AspectList(new ItemStack(Blocks.stained_hardened_clay)));
		}

		for (ModBlocks door : new ModBlocks[]{ModBlocks.COPPER_DOOR, ModBlocks.EXPOSED_COPPER_DOOR, ModBlocks.WEATHERED_COPPER_DOOR, ModBlocks.OXIDIZED_COPPER_DOOR, ModBlocks.WAXED_COPPER_DOOR, ModBlocks.WAXED_EXPOSED_COPPER_DOOR, ModBlocks.WAXED_WEATHERED_COPPER_DOOR, ModBlocks.WAXED_OXIDIZED_COPPER_DOOR}) {
			ThaumcraftApi.registerObjectTag(door.newItemStack(), new AspectList().add(Aspect.METAL, 4).add(Aspect.EXCHANGE, 1).add(Aspect.MECHANISM, 1).add(Aspect.MOTION, 1));
		}

		for (ModBlocks trapdoor : new ModBlocks[]{ModBlocks.COPPER_TRAPDOOR, ModBlocks.EXPOSED_COPPER_TRAPDOOR, ModBlocks.WEATHERED_COPPER_TRAPDOOR, ModBlocks.OXIDIZED_COPPER_TRAPDOOR, ModBlocks.WAXED_COPPER_TRAPDOOR, ModBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR, ModBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR, ModBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR}) {
			ThaumcraftApi.registerObjectTag(trapdoor.newItemStack(), new AspectList().add(Aspect.METAL, 4).add(Aspect.EXCHANGE, 1).add(Aspect.MECHANISM, 1).add(Aspect.MOTION, 1));
		}

		ThaumcraftApi.registerObjectTag(ModBlocks.CONCRETE_POWDER.newItemStack(8, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.EARTH, 3).add(Aspect.ENTROPY, 2));
		ThaumcraftApi.registerObjectTag(ModBlocks.CONCRETE.newItemStack(8, OreDictionary.WILDCARD_VALUE), new AspectList(ModBlocks.CONCRETE_POWDER.newItemStack(8, 0)).add(Aspect.WATER, 1).add(Aspect.ORDER, 1));

		ThaumcraftApi.registerObjectTag(ModBlocks.COPPER_BLOCK.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.METAL, 20).add(Aspect.EXCHANGE, 6));
		ThaumcraftApi.registerObjectTag(ModBlocks.CHISELED_COPPER.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(ModBlocks.COPPER_BLOCK.newItemStack()));
		ThaumcraftApi.registerObjectTag(ModBlocks.COPPER_GRATE.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(ModBlocks.COPPER_BLOCK.newItemStack()));
		ThaumcraftApi.registerObjectTag(ModBlocks.COPPER_BULB.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList(ModBlocks.COPPER_BLOCK.newItemStack()).add(Aspect.LIGHT, 4).add(Aspect.MECHANISM, 3));

		ThaumcraftApi.registerObjectTag(ModBlocks.BUDDING_AMETHYST.newItemStack(), new AspectList().add(Aspect.CRYSTAL, 4).add(Aspect.SENSES, 4).add(Aspect.GREED, 4));
		ThaumcraftApi.registerObjectTag(ModBlocks.AMETHYST_CLUSTER_1.newItemStack(1, 0), new AspectList().add(Aspect.CRYSTAL, 1).add(Aspect.SENSES, 1).add(Aspect.GREED, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.AMETHYST_CLUSTER_1.newItemStack(1, 6), new AspectList().add(Aspect.CRYSTAL, 2).add(Aspect.SENSES, 2).add(Aspect.GREED, 2));
		ThaumcraftApi.registerObjectTag(ModBlocks.AMETHYST_CLUSTER_2.newItemStack(1, 0), new AspectList().add(Aspect.CRYSTAL, 2).add(Aspect.SENSES, 2).add(Aspect.GREED, 2));
		ThaumcraftApi.registerObjectTag(ModBlocks.AMETHYST_CLUSTER_2.newItemStack(1, 6), new AspectList().add(Aspect.CRYSTAL, 3).add(Aspect.SENSES, 3).add(Aspect.GREED, 3));

		// Ores
		ThaumcraftApi.registerObjectTag(ModBlocks.DEEPSLATE_COAL_ORE.newItemStack(), new AspectList(new ItemStack(Blocks.coal_ore)));
		ThaumcraftApi.registerObjectTag(ModBlocks.DEEPSLATE_IRON_ORE.newItemStack(), new AspectList(new ItemStack(Blocks.iron_ore)));
		ThaumcraftApi.registerObjectTag(ModBlocks.DEEPSLATE_GOLD_ORE.newItemStack(), new AspectList(new ItemStack(Blocks.gold_ore)));
		ThaumcraftApi.registerObjectTag(ModBlocks.DEEPSLATE_REDSTONE_ORE.newItemStack(), new AspectList(new ItemStack(Blocks.redstone_ore)));
		ThaumcraftApi.registerObjectTag(ModBlocks.DEEPSLATE_LAPIS_ORE.newItemStack(), new AspectList(new ItemStack(Blocks.lapis_ore)));
		ThaumcraftApi.registerObjectTag(ModBlocks.DEEPSLATE_DIAMOND_ORE.newItemStack(), new AspectList(new ItemStack(Blocks.diamond_ore)));
		ThaumcraftApi.registerObjectTag(ModBlocks.DEEPSLATE_EMERALD_ORE.newItemStack(), new AspectList(new ItemStack(Blocks.emerald_ore)));
		ThaumcraftApi.registerObjectTag(ModBlocks.COPPER_ORE.newItemStack(), new AspectList().add(Aspect.METAL, 2).add(Aspect.EARTH, 1).add(Aspect.EXCHANGE, 1));
		ThaumcraftApi.registerObjectTag(ModBlocks.DEEPSLATE_COPPER_ORE.newItemStack(), new AspectList(new ItemStack(ModBlocks.COPPER_ORE.get())));
		for (int i = 0; i < 8; i++) {
			ThaumcraftApi.registerObjectTag(ModBlocks.DEEPSLATE_THAUMCRAFT_ORE.newItemStack(1, i), new AspectList(new ItemStack(ConfigBlocks.blockCustomOre, 1, i)));
		}
	}

	public static void doRecipes() {
		// TODO is there supposed to be Thaumcraft related Recipes? If so, what would they be?
	}

	public static int getHasteEnchID() {
		return Config.enchHaste.effectId;
	}

}
