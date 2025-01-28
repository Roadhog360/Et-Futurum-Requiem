package ganymedes01.etfuturum.client.sound;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class BlockSoundRegistryHelper {
	public static void registerSoundsDynamic(Block block, String namespace) {
		String blockID = namespace.split(":")[1].toLowerCase();

		Block.SoundType sound = getCustomStepSound(block, blockID);
		if (sound != null) {
			block.setStepSound(sound);
		}
	}

	public static Block.SoundType getCustomStepSound(Block block, String namespace) {
		if (block.stepSound == Block.soundTypePiston || block.stepSound == Block.soundTypeStone) {
			if (namespace.contains("nether") && namespace.contains("brick")) {
				return ModSounds.soundNetherBricks;
			} else if (namespace.contains("netherrack") || namespace.contains("hellfish")) {
				return ModSounds.soundNetherrack;
			} else if (block == Blocks.quartz_ore || (namespace.contains("nether") && (block instanceof BlockOre || namespace.contains("ore")))) {
				return ModSounds.soundNetherOre;
			} else if (namespace.contains("deepslate")) {
				return namespace.contains("brick") ? ModSounds.soundDeepslateBricks : ModSounds.soundDeepslate;
			} else if (block instanceof BlockNetherWart || (namespace.contains("nether") && namespace.contains("wart"))) {
				return ModSounds.soundCropWarts;
			} else if (namespace.contains("bone") || namespace.contains("ivory")) {
				return ModSounds.soundBoneBlock;
			}
		}

		if (block.stepSound == Block.soundTypeGrass) {
			if (block instanceof BlockVine) {
				return ModSounds.soundVines;
			}

			if (block instanceof BlockLilyPad) {
				return ModSounds.soundWetGrass;
			}
		}

		if (block instanceof BlockCrops || block instanceof BlockStem) {
			return ModSounds.soundCrops;
		}

		if (block.stepSound == Block.soundTypeSand && namespace.contains("soul") && namespace.contains("sand")) {
			return ModSounds.soundSoulSand;
		}

		if (block.stepSound == Block.soundTypeMetal && (namespace.contains("copper") || namespace.contains("tin"))) {
			return ModSounds.soundCopper;
		}

		if (block.getMaterial() == Material.iron && block instanceof BlockHopper) {
			return Block.soundTypeMetal;
		}

		return null;
	}

	public static void setupMultiBlockSoundRegistry() {
//		MultiBlockSoundRegistry.addBasic(Blocks.stone_slab, ModSounds.soundNetherBricks, 6, 14);
//		MultiBlockSoundRegistry.addBasic(Blocks.double_stone_slab, ModSounds.soundNetherBricks, 6, 14);
//
//		MultiBlockSoundRegistry.addBasic(ExternalContent.Blocks.TCON_MULTIBRICK.get(), ModSounds.soundNetherrack, 2);
//		MultiBlockSoundRegistry.addBasic(ExternalContent.Blocks.TCON_MULTIBRICK.get(), ModSounds.soundBoneBlock, 9);
//		MultiBlockSoundRegistry.addBasic(ExternalContent.Blocks.TCON_MULTIBRICK_FANCY.get(), ModSounds.soundNetherrack, 2);
//		MultiBlockSoundRegistry.addBasic(ExternalContent.Blocks.TCON_MULTIBRICK_FANCY.get(), ModSounds.soundBoneBlock, 9);

//		MultiBlockSoundRegistry.addBasic(ModBlocks.DEEPSLATE_BRICK_WALL.get(), ModSounds.soundDeepslateTiles, 1);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.DEEPSLATE_BRICKS.get(), ModSounds.soundDeepslateTiles, 2, 3);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.DEEPSLATE_BRICK_SLAB.get(), ModSounds.soundDeepslateTiles, 1, 9);
//
//		MultiBlockSoundRegistry.addBasic(ModBlocks.TUFF.get(), ModSounds.soundPolishedTuff, 1);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.TUFF.get(), ModSounds.soundTuffBricks, 2, 4);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.TUFF_WALL.get(), ModSounds.soundPolishedTuff, 1);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.TUFF_WALL.get(), ModSounds.soundTuffBricks, 2);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.TUFF_SLAB.get(), ModSounds.soundPolishedTuff, 1, 9);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.TUFF_SLAB.get(), ModSounds.soundTuffBricks, 2, 10);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.DOUBLE_TUFF_SLAB.get(), ModSounds.soundPolishedTuff, 1, 9);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.DOUBLE_TUFF_SLAB.get(), ModSounds.soundTuffBricks, 2, 10);
//
//		MultiBlockSoundRegistry.addBasic(ModBlocks.AMETHYST_CLUSTER_1.get(), ModSounds.soundAmethystBudSmall, 0, 1, 2, 3, 4, 5, 6);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.AMETHYST_CLUSTER_2.get(), ModSounds.soundAmethystBudLrg, 0, 1, 2, 3, 4, 5, 6);
//
//		MultiBlockSoundRegistry.addBasic(ModBlocks.SPONGE.get(), ModSounds.soundWetSponge, 1);
//		MultiBlockSoundRegistry.addBasic(Blocks.sponge, ModSounds.soundWetSponge, 1);
//
//		MultiBlockSoundRegistry.addBasic(ModBlocks.SAPLING.get(), ModSounds.soundCherrySapling, 1, 9);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.LEAVES.get(), ModSounds.soundCherryLeaves, 1, 5, 9, 13);
//
//		MultiBlockSoundRegistry.addBasic(ModBlocks.WOOD_PLANKS.get(), ModSounds.soundNetherWood, 0, 1);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.WOOD_PLANKS.get(), ModSounds.soundCherryWood, 3);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.WOOD_PLANKS.get(), ModSounds.soundBambooWood, 4);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.WOOD_FENCE.get(), ModSounds.soundNetherWood, 0, 1);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.WOOD_FENCE.get(), ModSounds.soundCherryWood, 3);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.WOOD_FENCE.get(), ModSounds.soundBambooWood, 4);
//
//		MultiBlockSoundRegistry.addBasic(ModBlocks.WOOD_SLAB.get(), ModSounds.soundNetherWood, 0, 1, 8, 9);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.WOOD_SLAB.get(), ModSounds.soundCherryWood, 3, 11);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.WOOD_SLAB.get(), ModSounds.soundBambooWood, 4, 12);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.DOUBLE_WOOD_SLAB.get(), ModSounds.soundNetherWood, 0, 1, 8, 9);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.DOUBLE_WOOD_SLAB.get(), ModSounds.soundCherryWood, 3, 11);
//		MultiBlockSoundRegistry.addBasic(ModBlocks.DOUBLE_WOOD_SLAB.get(), ModSounds.soundBambooWood, 4, 12);
//
//		MultiBlockSoundRegistry.addBasic(ModBlocks.PACKED_MUD.get(), ModSounds.soundMudBricks, 1);

//		if(ModsList.IRON_CHEST.isLoaded() && ModsList.IRON_CHEST.isVersionNewerOrEqual("6.0.78")) { // Version netherite chests were added in
//			MultiBlockSoundRegistry.addBasic(ExternalContent.Blocks.IRON_CHEST.get(), ModSounds.soundNetherite, 8);
//		}

//		if (ExternalContent.Blocks.TCON_METAL.get() != null) { Makes TCon copper tin make the respective noises
//			{
//				BasicMultiBlockSound mbs = new BasicMultiBlockSound() {
//					@Override
//					public float getPitch(World world, int x, int y, int z, float pitch, MultiBlockSoundRegistry.BlockSoundType type) {
//						if (type != MultiBlockSoundRegistry.BlockSoundType.WALK) {
//							return pitch * .67F;
//						}
//						return 1;
//					}
//				};
//				mbs.setTypes(ModSounds.soundCopper, 3);
//				mbs.setTypes(ModSounds.soundCopper, 5);
//				MultiBlockSoundRegistry.multiBlockSounds.put(ExternalContent.Blocks.TCON_METAL.get(), mbs);
//			}
//		}
	}
}
