package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Random;

public class BlockBerryBush extends BlockBush implements IGrowable {

	private IIcon[] icons;

	public BlockBerryBush() {
		super(Material.vine);
		setStepSound(ModSounds.soundBerryBush);
		setBlockName("sweet_berry_bush");
		setBlockTextureName("sweet_berry_bush");
		setCreativeTab(null);
		setTickRandomly(true);
	}

	public static final DamageSource SWEET_BERRY_BUSH = (new DamageSource("sweetBerryBush"));

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.getBlockMetadata(x, y, z) >= 3)
			return;
		int i = world.getBlockMetadata(x, y, z);
		if (i < 3 && world.rand.nextInt(5) == 0 && world.getBlockLightValue(x, y, z) >= 9) {
			world.setBlockMetadataWithNotify(x, y, z, i + 1, 2);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (entity instanceof EntityLivingBase) {
			entity.motionX *= 0.3405D;
			entity.motionY *= 0.3405D;
			entity.motionZ *= 0.3405D;
			entity.fallDistance = 0;
			if (!world.isRemote && world.getBlockMetadata(x, y, z) > 0 && (entity.lastTickPosX != entity.posX || entity.lastTickPosZ != entity.posZ)) {
				double d0 = Math.abs(entity.posX - entity.lastTickPosX);
				double d1 = Math.abs(entity.posZ - entity.lastTickPosZ);
				if (d0 >= 0.003F || d1 >= 0.003F) {
					entity.attackEntityFrom(SWEET_BERRY_BUSH, 1.0F);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister icon) {
		icons = new IIcon[4];
		for (int i = 0; i < icons.length; i++) {
			icons[i] = icon.registerIcon(getTextureName() + "_stage" + i);
		}
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta > 3)
			return icons[0];
		return icons[meta];
	}

	@Override
	public Item getItem(World worldIn, int x, int y, int z) {
		return ModItems.SWEET_BERRIES.get();
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
		if (worldIn.getBlockMetadata(x, y, z) == 0) {
			this.setBlockBounds(0.1875F, 0.0F, 0.1875F, 0.8125F, 0.5F, 0.8125F);
		} else {
			this.setBlockBounds(0.0625F, 0.0F, 0.0625F, .9375F, 1.0F, 0.9375F);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		int i = world.getBlockMetadata(x, y, z);
		if (i > 1 && (i != 2 || player.getHeldItem() == null || player.getHeldItem().getItem() != Items.dye || player.getHeldItem().getItemDamage() != 15)) {
			//We check for if the plant is in the first berried stage (meta 2, the plant has berries on meta 2 and 3) and if we're holding bone meal.
			//This is so we grow the berries to the final meta, 3 instead of picking them if the meta is 2.
			if (!world.isRemote && !world.restoringBlockSnapshots) {
				ItemStack stack = new ItemStack(ModItems.SWEET_BERRIES.get(), 1 + world.rand.nextInt(i));
				float f = 0.7F;
				double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, stack);
				entityitem.delayBeforeCanPickup = 10;
				world.spawnEntityInWorld(entityitem);
			}
			world.playSound(x, y, z, Tags.MC_ASSET_VER + ":block.sweet_berry_bush.pick_berries", 1.0F, 0.8F + world.rand.nextFloat() * 0.4F, false);
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);
			return true;
		}
		return false;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		int count = metadata - world.rand.nextInt(2);
		for (int i = 0; i < count && metadata > 1; i++) {
			Item item = getItemDropped(metadata, world.rand, fortune);
			if (item != null) {
				ret.add(new ItemStack(item, 1, damageDropped(metadata)));
			}
		}
		return ret;
	}


	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return ModItems.SWEET_BERRIES.get();
	}

	@Override
	public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return true;
	}

	@Override
	public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return 100;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return 60;
	}

	/**
	 * MCP name: {@code canFertilize}
	 */
	@Override
	public boolean func_149851_a(World worldIn, int x, int y, int z, boolean isClient) {
		return worldIn.getBlockMetadata(x, y, z) < 3;
	}

	/**
	 * MCP name: {@code shouldFertilize}
	 */
	@Override
	public boolean func_149852_a(World worldIn, Random random, int x, int y, int z) {
		return true;
	}

	/**
	 * MCP name: {@code fertilize}
	 */
	@Override
	public void func_149853_b(World world, Random rand, int x, int y, int z) {
		int i = world.getBlockMetadata(x, y, z);
		if (i < 3) {
			world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 1, 2);
		}
	}
}
