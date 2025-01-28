package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.blocks.BlockWoodSign;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import ganymedes01.etfuturum.network.WoodSignOpenMessage;
import ganymedes01.etfuturum.tileentities.TileEntityWoodSign;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemWoodSign extends Item {

	private final BlockWoodSign sign;

	public ItemWoodSign(BlockWoodSign sign) {
		this.sign = sign;
		setUnlocalizedName(sign.type + "_sign");
		setTextureName(sign.type + "_sign");
		setCreativeTab(EtFuturum.creativeTabBlocks);
		this.maxStackSize = Items.sign.getItemStackLimit(new ItemStack(Items.sign));
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {

		if (p_77648_7_ == 0) {
			return false;
		} else if (!world.getBlock(x, y, z).getMaterial().isSolid()) {
			return false;
		} else {
			if (p_77648_7_ == 1) {
				++y;
			}

			if (p_77648_7_ == 2) {
				--z;
			}

			if (p_77648_7_ == 3) {
				++z;
			}

			if (p_77648_7_ == 4) {
				--x;
			}

			if (p_77648_7_ == 5) {
				++x;
			}

			if (!player.canPlayerEdit(x, y, z, p_77648_7_, itemStack)) {
				return false;
			} else if (!Blocks.standing_sign.canPlaceBlockAt(world, x, y, z)) {
				return false;
			} else if (world.isRemote) {
				return true;
			} else {
				Block block;
				if (p_77648_7_ == 1) {
					int i1 = MathHelper.floor_double((player.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5D) & 15;
					block = sign;
					world.setBlock(x, y, z, block, i1, 3);
				} else {
					block = sign.getWallSign();
					world.setBlock(x, y, z, block, p_77648_7_, 3);
				}

				//Disable the sound for continuity, so it doesn't play when the event-based player would not
				if (ConfigSounds.fixSilentPlacing)
					world.playSoundEffect((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F, block.stepSound.func_150496_b()/*getPlaceSound*/, (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);

				--itemStack.stackSize;
				TileEntityWoodSign tileentitysign = (TileEntityWoodSign) world.getTileEntity(x, y, z);

				if (tileentitysign != null) {
					if (player instanceof EntityPlayerMP) {
						tileentitysign.func_145912_a(player);
						//This is needed because the game crashes whenever we open the sign normally for some reason, so we have to use a packet.
						//I'd like to get rid of this packet eventually because it feels wrong
						EtFuturum.networkWrapper.sendTo(new WoodSignOpenMessage(tileentitysign, Block.getIdFromBlock(block)), (EntityPlayerMP) player);
					}
				}
				return true;
			}
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return getUnlocalizedName();
	}

	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName().replaceFirst("item", "tile");
	}

	/**
	 * Used by these legacy sign items to indicate to the creative tab what sign block they represent.
	 * This is so they can be sorted by block ID instead of item ID (not always on the bottom)
	 *
	 * @return
	 */
	public BlockWoodSign getSignBlock() {
		return sign;
	}
}
