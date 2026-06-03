package ganymedes01.etfuturum.core.utils.structurenbt;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Map;

public class EFRBlockStateConverter extends BlockStateConverter {

	public static final BlockStateConverter INSTANCE = new EFRBlockStateConverter();

	private static final String[] DYE_COLORS = {"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"};

	@Override
	public BlockStateContainer createBlockStateContainer(String blockName, Block block, Map<String, String> blockStates, ForgeDirection dir) {
		BlockStateContainer container = super.createBlockStateContainer(blockName, block, blockStates, dir);
		if (container.getType() == BlockStateContainer.BlockStateType.BLOCK_ENTITY && blockName.contains("banner")) {
			net.minecraft.nbt.NBTTagCompound nbt = new net.minecraft.nbt.NBTTagCompound();
			int color = 0;
			for (int i = 0; i < DYE_COLORS.length; i++) {
				if (blockName.contains(DYE_COLORS[i])) {
					color = i;
					break;
				}
			}
			nbt.setInteger("Base", color);
			nbt.setBoolean("IsStanding", !blockName.endsWith("_wall_banner") && !blockName.equals("minecraft:wall_banner"));
			container.setCompound(nbt);
		}
		return container;
	}

	@Override
	public int getMetaFromState(String blockName, Map<String, String> blockStates, ForgeDirection dir) {
		if (blockName.equals("minecraft:bone_block") && ConfigWorld.fossilBlock != null) {
			if (ConfigWorld.fossilBlock.get() == Blocks.quartz_block && ConfigWorld.fossilBlock.getMeta() == 2) {
				return super.getMetaFromState("minecraft:quartz_pillar", blockStates, dir);
			}
		}
		//TODO: Beetroot should be age * 2
		return super.getMetaFromState(blockName, blockStates, dir);
	}

	@Override
	public int getMetaFromStateWithSubtypeAdditions(String blockName, Map<String, String> blockStates, ForgeDirection dir) {
		int meta = getMetaFromState(blockName, blockStates, dir);
		String truncatedName = blockName.substring(blockName.indexOf(":") + 1);
		switch (truncatedName) {
			case "bone_block":
				if (ConfigWorld.fossilBlock != null) {
					if (ConfigWorld.fossilBlock.get() == Blocks.quartz_block && ConfigWorld.fossilBlock.getMeta() == 2) {
						return meta;
					}
					return meta + ConfigWorld.fossilBlock.getMeta();
				}
				break;
			default:
				break;
		}
		return super.getMetaFromStateWithSubtypeAdditions(blockName, blockStates, dir);
	}

	@Override
	public Block getBlockFromNamespace(String blockName, Map<String, String> blockStates) {
		String truncatedName = blockName.substring(blockName.indexOf(":") + 1);
		String nameToFind = truncatedName;

		// Removed explicit mapping to Blocks.air for dragon heads

		switch (truncatedName) {
			case "stone":
				return Blocks.stone;
			case "bone_block":
				if (ConfigWorld.fossilBlock != null) {
					return ConfigWorld.fossilBlock.get();
				}
				break;
			case "end_stone_bricks":
				nameToFind = "end_bricks";
				break;
			case "white_banner":
			case "orange_banner":
			case "magenta_banner":
			case "light_blue_banner":
			case "yellow_banner":
			case "lime_banner":
			case "pink_banner":
			case "gray_banner":
			case "light_gray_banner":
			case "cyan_banner":
			case "purple_banner":
			case "blue_banner":
			case "brown_banner":
			case "green_banner":
			case "red_banner":
			case "black_banner":
			case "white_wall_banner":
			case "orange_wall_banner":
			case "magenta_wall_banner":
			case "light_blue_wall_banner":
			case "yellow_wall_banner":
			case "lime_wall_banner":
			case "pink_wall_banner":
			case "gray_wall_banner":
			case "light_gray_wall_banner":
			case "cyan_wall_banner":
			case "purple_wall_banner":
			case "blue_wall_banner":
			case "brown_wall_banner":
			case "green_wall_banner":
			case "red_wall_banner":
			case "black_wall_banner":
				nameToFind = "banner";
		}
		Block efrBlock = GameRegistry.findBlock("etfuturum", nameToFind);
		if (efrBlock != null) {
			return efrBlock;
		}
		return super.getBlockFromNamespace(blockName, blockStates);
	}
}
