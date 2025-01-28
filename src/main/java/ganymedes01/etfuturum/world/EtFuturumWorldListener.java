package ganymedes01.etfuturum.world;

import com.google.common.collect.Maps;
import com.gtnewhorizon.gtnhlib.client.renderer.util.DirectionUtil;
import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockBubbleColumn;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import roadhog360.hogutils.api.utils.GenericUtils;

import java.util.Map;

public class EtFuturumWorldListener implements IWorldAccess {

	private final World world;
	private static final Map<Block, Block> replacements = Maps.newHashMap();
	public static final Map<Block, Block> bubbleColumnMap = Maps.newHashMap();

	public EtFuturumWorldListener(World theWorld) {
		world = theWorld;
		if (ModBlocks.BREWING_STAND.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				replacements.put(Blocks.brewing_stand, ModBlocks.BREWING_STAND.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				replacements.put(ModBlocks.BREWING_STAND.get(), Blocks.brewing_stand);
			}
		}


		if (ModBlocks.BEACON.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				replacements.put(Blocks.beacon, ModBlocks.BEACON.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				replacements.put(ModBlocks.BEACON.get(), Blocks.beacon);
			}
		}

		if (ModBlocks.ENCHANTMENT_TABLE.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				replacements.put(Blocks.enchanting_table, ModBlocks.ENCHANTMENT_TABLE.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				replacements.put(ModBlocks.ENCHANTMENT_TABLE.get(), Blocks.enchanting_table);
			}
		}

		if (ModBlocks.ANVIL.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				replacements.put(Blocks.anvil, ModBlocks.ANVIL.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				replacements.put(ModBlocks.ANVIL.get(), Blocks.anvil);
			}
		}

		if (ModBlocks.SPONGE.isEnabled()) {
			if (ConfigWorld.tileReplacementMode == 0) {
				replacements.put(Blocks.sponge, ModBlocks.SPONGE.get());
			} else if (ConfigWorld.tileReplacementMode == 1) {
				replacements.put(ModBlocks.SPONGE.get(), Blocks.sponge);
			}
		}

		if (ModBlocks.DAYLIGHT_DETECTOR.isEnabled()) {
			if (ConfigWorld.tileReplacementMode != -1) {
				replacements.put(ModBlocks.DAYLIGHT_DETECTOR.get(), Blocks.daylight_detector);
			}
		}

		if (ModsList.BACK_IN_SLIME.isLoaded() && ConfigMixins.betterPistons) {
			replacements.put(GameRegistry.findBlock("bis", "SlimePistonBase"), Blocks.piston);
			replacements.put(GameRegistry.findBlock("bis", "StickySlimePistonBase"), Blocks.sticky_piston);
			replacements.put(GameRegistry.findBlock("bis", "SlimePistonHead"), Blocks.piston_head);
			if (ModBlocks.SLIME.isEnabled()) {
				replacements.put(GameRegistry.findBlock("bis", "SlimeBlock"), ModBlocks.SLIME.get());
			}
		}
	}

	@Override
	public void markBlockForUpdate(int x, int y, int z) {

		if (replacements.isEmpty() || !world.blockExists(x, y, z) /*|| !world.getChunkFromBlockCoords(x, z).isChunkLoaded*/)
			return;

		handleBasaltFromLava(x, y, z);
		for (ForgeDirection dir : DirectionUtil.ALL_DIRECTIONS) {
			handleBubbleColumnCreation(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
		}

		Block replacement;
		TileEntity tile;

		if ((replacement = replacements.get(world.getBlock(x, y, z))) == null)
			return;

		tile = world.getTileEntity(x, y, z);
		NBTTagCompound nbt = new NBTTagCompound();
		if (tile != null) {
			tile.writeToNBT(nbt);
			if (tile instanceof IInventory invt) {
				for (int j = 0; j < invt.getSizeInventory(); j++) {
					invt.setInventorySlotContents(j, null);
				}
			}
		}

		int replacementMeta = world.getBlockMetadata(x, y, z);

		world.setBlock(x, y, z, replacement, replacementMeta, 2);
		TileEntity newTile = world.getTileEntity(x, y, z);
		if (newTile != null) {
			newTile.readFromNBT(nbt);
		}

	}

	private void handleBasaltFromLava(int x, int y, int z) { //Don't support Netherlicious blocks, this can be handled on their end
		if (ModBlocks.BASALT.isEnabled()) {
			if (world.getBlock(x, y, z).getMaterial() == Material.lava) {
				Block soulsand = ModBlocks.SOUL_SOIL.isEnabled() ? ModBlocks.SOUL_SOIL.get() : Blocks.soul_sand;
				if (world.getBlock(x, y - 1, z) == soulsand) {
					Block ice = ModBlocks.BLUE_ICE.isEnabled() ? ModBlocks.BLUE_ICE.get() : Blocks.packed_ice;
					for (EnumFacing facing : GenericUtils.Constants.ENUM_FACING_VALUES) {
						if (facing == EnumFacing.DOWN) continue;
						if (world.getBlock(x + facing.getFrontOffsetX(), y + facing.getFrontOffsetY(), z + facing.getFrontOffsetZ()) == ice) {
							world.playSoundEffect((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
							for (int l = 0; l < 8; ++l) {
								world.spawnParticle("largesmoke", (double) x + Math.random(), (double) y + 1.2D, (double) z + Math.random(), 0.0D, 0.0D, 0.0D);
							}
							world.setBlock(x, y, z, ModBlocks.BASALT.get());
							return;
						}
					}
				}
			}
		}
	}

	/**
	 * This is only needed to create the initial bubble column above the magma or soul sand or custom column above another block.
	 * The column itself handles the creation of the remainder of the column, as well as destroying itself.
	 */
	private void handleBubbleColumnCreation(int x, int y, int z) {
		if (!bubbleColumnMap.isEmpty() && world.blockExists(x, y, z)) {
			if (BlockBubbleColumn.isFullVanillaWater(world.getBlock(x, y + 1, z), world.getBlockMetadata(x, y + 1, z))) {
				Block block = bubbleColumnMap.get(world.getBlock(x, y, z));
				if (block != null) {
					world.setBlock(x, y + 1, z, block, 0, 3);
				}
			}
		}
	}

	@Override
	public void markBlockForRenderUpdate(int p_147588_1_, int p_147588_2_, int p_147588_3_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void markBlockRangeForRenderUpdate(int p_147585_1_, int p_147585_2_, int p_147585_3_, int p_147585_4_,
											  int p_147585_5_, int p_147585_6_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playSound(String soundName, double x, double y, double z, float volume,
						  float pitch) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playSoundToNearExcept(EntityPlayer p_85102_1_, String p_85102_2_, double p_85102_3_, double p_85102_5_,
									  double p_85102_7_, float p_85102_9_, float p_85102_10_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void spawnParticle(String p_72708_1_, double p_72708_2_, double p_72708_4_, double p_72708_6_,
							  double p_72708_8_, double p_72708_10_, double p_72708_12_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEntityCreate(Entity p_72703_1_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEntityDestroy(Entity p_72709_1_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playRecord(String p_72702_1_, int p_72702_2_, int p_72702_3_, int p_72702_4_) {
		// TODO Auto-generated method stub
	}

	@Override
	public void broadcastSound(int p_82746_1_, int p_82746_2_, int p_82746_3_, int p_82746_4_, int p_82746_5_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playAuxSFX(EntityPlayer p_72706_1_, int p_72706_2_, int p_72706_3_, int p_72706_4_, int p_72706_5_,
						   int p_72706_6_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroyBlockPartially(int p_147587_1_, int p_147587_2_, int p_147587_3_, int p_147587_4_,
									  int p_147587_5_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStaticEntitiesChanged() {
		// TODO Auto-generated method stub

	}

}
