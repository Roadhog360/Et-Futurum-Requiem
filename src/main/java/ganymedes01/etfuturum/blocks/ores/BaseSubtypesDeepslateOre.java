package ganymedes01.etfuturum.blocks.ores;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.blocks.BaseSubtypesBlock;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public abstract class BaseSubtypesDeepslateOre extends BaseSubtypesBlock {
	public BaseSubtypesDeepslateOre(String... types) {
		super(Material.rock, types);
		setBlockSound(ModSounds.soundDeepslate);
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return checkDrop(getBase(p_149650_1_).getItemDropped(getBaseMeta(p_149650_1_), p_149650_2_, p_149650_3_), p_149650_1_);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> list = getBase(metadata).getDrops(world, x, y, z, getBaseMeta(metadata), fortune);
		list.forEach(this::checkDrop);
		return list;
	}

	//	@Override //TODO: Figure out how to capture meta in case a mod overrides these. Although it's unlikely this is needed so it's "FINE" for now
//	public int quantityDropped(Random p_149745_1_) {
//		return getBase().quantityDropped(p_149745_1_);
//	}
//
//	@Override
//	public int quantityDroppedWithBonus(int i, Random p_149745_1_) {
//		return getBase().quantityDroppedWithBonus(i, p_149745_1_);
//	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return getBase(meta).quantityDropped(getBaseMeta(meta), fortune, random);
	}

	/**
	 * Determines the damage on the item the block drops. Used in cloth and wood.
	 */
	@Override
	public int damageDropped(int p_149692_1_) {
		return getBase(p_149692_1_).damageDropped(getBaseMeta(p_149692_1_));
	}

	@Override
	public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
		return getBase(metadata).getExpDrop(world, getBaseMeta(metadata), fortune);
	}

	@Override
	public MapColor getMapColor(int p_149728_1_) {
		return getBase(p_149728_1_).getMapColor(getBaseMeta(p_149728_1_));
	}

	/**
	 * Called when a player hits the block. Args: world, x, y, z, player
	 */
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer p_149699_5_) {
		getBase(world.getBlockMetadata(x, y, z)).onBlockClicked(world, x, y, z, p_149699_5_);
	}

	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random p_149734_5_) {
		getBase(world.getBlockMetadata(x, y, z)).randomDisplayTick(world, x, y, z, p_149734_5_);
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
	 */
	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metamaybe) {
		//TODO, Assuming the last value is meta, make sure it's not side
		getBase(metamaybe).onBlockDestroyedByPlayer(world, x, y, z, getBaseMeta(metamaybe));
	}

	/**
	 * Can add to the passed in vector for a movement vector to be applied to the entity. Args: x, y, z, entity, vec3d
	 */
	@Override
	public void velocityToAddToEntity(World world, int x, int y, int z, Entity p_149640_5_, Vec3 p_149640_6_) {
		getBase(world.getBlockMetadata(x, y, z)).velocityToAddToEntity(world, x, y, z, p_149640_5_, p_149640_6_);
	}

	/**
	 * How bright to render this block based on the light its receiving. Args: iBlockAccess, x, y, z
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z) {
		return getBase(world.getBlockMetadata(x, y, z)).getMixedBrightnessForBlock(world, x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int meta) {
		getBase(meta).breakBlock(world, x, y, z, p_149749_5_, getBaseMeta(meta));
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 * TODO: Find a way to capture metadata then uncomment this
	 */
//	@Override
//	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
//		return getBase(0).canPlaceBlockAt(world, x, y, z);
//	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		getBase(world.getBlockMetadata(x, y, z)).onBlockAdded(world, x, y, z);
	}

	/**
	 * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
		getBase(world.getBlockMetadata(x, y, z)).onEntityWalking(world, x, y, z, entity);
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World world, int x, int y, int z, Random p_149674_5_) {
		getBase(world.getBlockMetadata(x, y, z)).updateTick(world, x, y, z, p_149674_5_);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
		return getBase(world.getBlockMetadata(x, y, z)).onBlockActivated(world, x, y, z, p_149727_5_, 0, 0.0F, 0.0F, 0.0F);
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
		getBase(world.getBlockMetadata(x, y, z)).onBlockDestroyedByExplosion(world, x, y, z, explosion);
	}

	@Override
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
		return getBase(meta).addDestroyEffects(world, x, y, z, getBaseMeta(meta), effectRenderer);
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		return getBase(world.getBlockMetadata(x, y, z)).getBlockHardness(world, x, y, z) * 1.5F;
	}

	@Override
	public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		return getBase(world.getBlockMetadata(x, y, z)).getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
	}

	/**
	 * Used to replace the base ore with the deepslate version in drop lists.
	 * If it drops the base ore block, replace it with the base deepslate block (eg iron ore drop is swapped for deepslate iron ore drop)
	 */
	protected ItemStack checkDrop(ItemStack drop) {
		drop.func_150996_a(checkDrop(drop.getItem(), drop.getItemDamage()));
		return drop;
	}

	protected Item checkDrop(Item drop, int meta) {
		Block droppedBlock = Block.getBlockFromItem(drop);
		if (droppedBlock == getBase(meta)) {
			Item thisAsItem = Item.getItemFromBlock(this);
			if (thisAsItem != null) {
				return thisAsItem;
			}
		}
		return drop;
	}

	protected abstract Block getBase(int meta);

	protected int getBaseMeta(int meta) {
		return 0;
	}

	@Override
	public String getNameDomain() {
		return super.getNameDomain() + (getTextureSubfolder().isEmpty() ? "" : (super.getNameDomain().isEmpty() ? "" : ".") + getTextureSubfolder());
	}

	@Override
	public String getTextureDomain() {
		return Reference.MOD_ID;
	}

	@Override
	public int getDamageValue(World p_149643_1_, int p_149643_2_, int p_149643_3_, int p_149643_4_) {
		return p_149643_1_.getBlockMetadata(p_149643_2_, p_149643_3_, p_149643_4_);
	}
}
