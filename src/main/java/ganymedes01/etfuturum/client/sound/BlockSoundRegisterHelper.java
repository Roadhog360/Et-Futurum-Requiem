package ganymedes01.etfuturum.client.sound;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class BlockSoundRegisterHelper {
	public static void registerSoundsDynamic(Block block, String namespace) {
		String blockID = namespace.split(":")[1].toLowerCase();

		Block.SoundType sound = getCustomStepSound(block, blockID);
		if (sound != null) {
			block.setStepSound(sound);
		}
	}

	private static Block.SoundType getCustomStepSound(Block block, String namespace) {
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

}
