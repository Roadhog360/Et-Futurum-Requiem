package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.GrayscaleWaterResourcePack;
import ganymedes01.etfuturum.client.GrayscaleWaterResourcePack.GrayscaleType;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import ganymedes01.etfuturum.tileentities.TileEntityCauldronColoredWater;
import ganymedes01.etfuturum.tileentities.TileEntityCauldronPotion;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockPotionCauldron extends BlockCauldronTileEntity {

	public BlockPotionCauldron() {
		super(Material.iron);
		this.setStepSound(Blocks.cauldron.stepSound);
		this.setHardness(2);
		this.setResistance(2);
		this.setBlockName("potion_cauldron");
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		if (random.nextInt(30) == 0) {
			int color = ((TileEntityCauldronColoredWater) world.getTileEntity(x, y, z)).getWaterColor();
			float r = (float) (color >> 16 & 255) / 255.0F;
			float g = (float) (color >> 8 & 255) / 255.0F;
			float b = (float) (color & 255) / 255.0F;

			//Slightly decrease the bounds in which the particles can spawn so they don't go through the inside of the cauldron walls
			float min = 0.1875F;
			float max = 0.8125F;
			double d0 = x + (min + random.nextFloat() * (max - min));
			double d1 = z + (min + random.nextFloat() * (max - min));
			world.spawnParticle("mobSpell", d0, y + BlockCauldron.getRenderLiquidLevel(world.getBlockMetadata(x, y, z) + 1), d1, r, g, b);
		}
	}


	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float subX, float subY, float subZ) {

		final ItemStack stack = entityPlayer.getHeldItem();
		if (stack != null) {
			final Item item = stack.getItem();
			final TileEntityCauldronPotion potionCauldron = (TileEntityCauldronPotion) world.getTileEntity(x, y, z);
			if (item == Items.potionitem) {
				boolean shouldFill = true;
				if (potionCauldron.potion == null) {
					potionCauldron.potion = stack;
					potionCauldron.potion.stackSize = 1;
					potionCauldron.potion.func_135074_t();
					shouldFill = false;
				}

				boolean flag = false;
				final int meta = world.getBlockMetadata(x, y, z);
				final ItemStack bottle = new ItemStack(Items.glass_bottle);
				final List<PotionEffect> effects = ((ItemPotion) item).getEffects(stack);
				if (effects == null || !effects.equals(((ItemPotion) potionCauldron.potion.getItem()).getEffects(potionCauldron.potion))) {
					EnumCauldronFillAction.EVAPORATE.getAction(world, x, y, z, false);//TODO make this evaporate with buckets too
					world.setBlock(x, y, z, Blocks.cauldron, 0, 3);
					flag = true;
				} else if (meta < 2) {
					EnumCauldronFillAction.CHANGE_LEVEL.getAction(world, x, y, z, true);
					if (shouldFill) {
						world.setBlockMetadataWithNotify(x, y, z, meta + 1, 3);
					}
					flag = true;
				}
				if (flag && !entityPlayer.capabilities.isCreativeMode) {
					if (stack.stackSize <= 1) {
						entityPlayer.setCurrentItemOrArmor(0, bottle);
					} else {
						entityPlayer.inventory.decrStackSize(entityPlayer.inventory.currentItem, 1);
						if (!entityPlayer.inventory.addItemStackToInventory(bottle)) {
							entityPlayer.dropPlayerItemWithRandomChoice(bottle, false);
						}
					}
				}
				return flag;
			} else if (item == Items.glass_bottle) {
				final ItemStack newPotion = potionCauldron.potion.copy();
				if (stack.stackSize <= 1 && !entityPlayer.capabilities.isCreativeMode) {
					entityPlayer.setCurrentItemOrArmor(0, newPotion);
				} else {
					if (!entityPlayer.capabilities.isCreativeMode) {
						entityPlayer.inventory.decrStackSize(entityPlayer.inventory.currentItem, 1);
					}
					if (!entityPlayer.inventory.addItemStackToInventory(newPotion)) {
						entityPlayer.dropPlayerItemWithRandomChoice(newPotion, false);
					}
				}
				EnumCauldronFillAction.CHANGE_LEVEL.getAction(world, x, y, z, false);
				if (world.getBlockMetadata(x, y, z) <= 0) {
					world.setBlock(x, y, z, Blocks.cauldron, 0, 3);
				} else {
					world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) - 1, 3);
				}
				return true;
			} else if (item == Items.arrow && ModItems.TIPPED_ARROW.isEnabled()) {
				final int meta = world.getBlockMetadata(x, y, z);
				final int tipmax = meta == 0 ? 16 : meta == 1 ? 32 : 64;

				ItemStack tippedArrow = ModItems.TIPPED_ARROW.newItemStack(Math.min(stack.stackSize, tipmax), potionCauldron.potion.getItemDamage());
				final int setMeta = tippedArrow.stackSize >= tipmax ? -1 : meta - (tippedArrow.stackSize < 32 ? 1 : 2);

				if (!Items.potionitem.getEffects(potionCauldron.potion).isEmpty() && potionCauldron.potion.hasTagCompound() && potionCauldron.potion.getTagCompound().hasKey("CustomPotionEffects", 9)) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setTag("CustomPotionEffects", potionCauldron.potion.getTagCompound().getTagList("CustomPotionEffects", 10).copy());
					tippedArrow.setTagCompound(tag);
				}

				if (!entityPlayer.capabilities.isCreativeMode && tippedArrow.stackSize == stack.stackSize) {
					entityPlayer.setCurrentItemOrArmor(0, tippedArrow);
				} else {
					if (!entityPlayer.capabilities.isCreativeMode) {
						entityPlayer.inventory.decrStackSize(entityPlayer.inventory.currentItem, tipmax);
					}
					if (!entityPlayer.inventory.addItemStackToInventory(tippedArrow)) {
						entityPlayer.dropPlayerItemWithRandomChoice(tippedArrow, false);
					}
				}


				if (setMeta < 0) {
					world.setBlock(x, y, z, Blocks.cauldron, 0, 3);
				} else {
					world.setBlockMetadataWithNotify(x, y, z, setMeta, 3);
				}

				float liquidLevel = y + getRenderLiquidLevel(world.getBlockMetadata(x, y, z) + 1);
				world.playSoundEffect(x + 0.5D, liquidLevel, z + 0.5D, "game.player.swim.splash", 1, 1);
				return true;
			}
		}
		return false;
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.COLOR_CAULDRON.getRenderId();
	}

	public IIcon grayscaleWaterIcon() {
		return blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		blockIcon = reg.registerIcon(GrayscaleWaterResourcePack.createGrayscaleName("water_still", GrayscaleType.TINT_INVERSE));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCauldronPotion();
	}

	static float getRenderLiquidLevel(int meta) {
		int j = MathHelper.clamp_int(meta, 0, 3);
		return (float) (6 + 3 * j) / 16.0F;
	}

	public enum EnumCauldronFillAction {
		CHANGE_LEVEL {
			@Override
			public void getAction(World world, int x, int y, int z, boolean filling) {
				int color = ((TileEntityCauldronColoredWater) world.getTileEntity(x, y, z)).getWaterColor();
				float r = (float) (color >> 16 & 255) / 255.0F;
				float g = (float) (color >> 8 & 255) / 255.0F;
				float b = (float) (color & 255) / 255.0F;
				float liquidLevel = y + getRenderLiquidLevel(world.getBlockMetadata(x, y, z) + 1);
				if (ConfigSounds.fluidInteract) {
					world.playSoundEffect(x + 0.5D, liquidLevel, z + 0.5D, Tags.MC_ASSET_VER + ":item.bottle." + (filling ? "fill" : "empty"), 1, 1);
				}
				for (int i = 0; i < world.rand.nextInt(4) + 4; i++) {
					world.spawnParticle("mobSpell", this.getParticleXYCoord(x, world.rand), liquidLevel, this.getParticleXYCoord(z, world.rand), r, g, b);
				}
			}
		},
		EVAPORATE {
			@Override
			public void getAction(World world, int x, int y, int z, boolean filling) {
				float min = 0.25F;
				float max = getRenderLiquidLevel(world.getBlockMetadata(x, y, z) + 1);
				float liquidLevel = y + (min + world.rand.nextFloat() * (max - min));
				world.playSound(x + 0.5D, liquidLevel, z + 0.5D, "random.fizz", 0.3F, (world.rand.nextFloat() * 0.6F) + 0.4F, false);
				for (int i = 0; i < world.rand.nextInt(4) + 4; i++) {
					world.spawnParticle("explode", this.getParticleXYCoord(x, world.rand), liquidLevel, this.getParticleXYCoord(z, world.rand), 0, 0, 0);
				}
			}
		};

		public abstract void getAction(World world, int x, int y, int z, boolean filling);

		protected float getParticleXYCoord(int coordinate, Random rand) {
			float min = 0.1875F;
			float max = 0.8125F;
			return coordinate + (min + rand.nextFloat() * (max - min));
		}
	}

}
