package ganymedes01.etfuturum.blocks.ores;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.mappings.RegistryMapping;
import ganymedes01.etfuturum.blocks.BaseBlock;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.core.utils.DummyWorld;
import ganymedes01.etfuturum.core.utils.IInitAction;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class BaseDeepslateOre extends BaseBlock implements IInitAction {
	public BaseDeepslateOre() {
		super(Material.rock);
		setBlockSound(ModSounds.soundDeepslate);
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return checkDrop(new ItemStack(getBase().getItemDropped(meta, rand, fortune), 1, fortune)).getItem();
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> list = getBase().getDrops(world, x, y, z, metadata, fortune);
		list.forEach(this::checkDrop);
		return list;
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	@Override
	public int quantityDropped(Random p_149745_1_) {
		return getBase().quantityDropped(p_149745_1_);
	}

	@Override
	public int quantityDroppedWithBonus(int i, Random p_149745_1_) {
		return getBase().quantityDroppedWithBonus(i, p_149745_1_);
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return getBase().quantityDropped(meta, fortune, random);
	}

	/**
	 * Determines the damage on the item the block drops. Used in cloth and wood.
	 */
	@Override
	public int damageDropped(int p_149692_1_) {
		return getBase().damageDropped(p_149692_1_);
	}

	@Override
	public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
		return getBase().getExpDrop(world, metadata, fortune);
	}

	/**
	 * Called when a player hits the block. Args: world, x, y, z, player
	 */
	@Override
	public void onBlockClicked(World p_149699_1_, int p_149699_2_, int p_149699_3_, int p_149699_4_, EntityPlayer p_149699_5_) {
		getBase().onBlockClicked(p_149699_1_, p_149699_2_, p_149699_3_, p_149699_4_, p_149699_5_);
	}

	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_) {
		getBase().randomDisplayTick(p_149734_1_, p_149734_2_, p_149734_3_, p_149734_4_, p_149734_5_);
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
	 */
	@Override
	public void onBlockDestroyedByPlayer(World p_149664_1_, int p_149664_2_, int p_149664_3_, int p_149664_4_, int p_149664_5_) {
		getBase().onBlockDestroyedByPlayer(p_149664_1_, p_149664_2_, p_149664_3_, p_149664_4_, p_149664_5_);
	}

	@Override
	public int getLightValue() {
		return getBase().getLightValue();
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return getBase().getLightValue();
	}

	/**
	 * Can add to the passed in vector for a movement vector to be applied to the entity. Args: x, y, z, entity, vec3d
	 */
	@Override
	public void velocityToAddToEntity(World p_149640_1_, int p_149640_2_, int p_149640_3_, int p_149640_4_, Entity p_149640_5_, Vec3 p_149640_6_) {
		getBase().velocityToAddToEntity(p_149640_1_, p_149640_2_, p_149640_3_, p_149640_4_, p_149640_5_, p_149640_6_);
	}

	/**
	 * How bright to render this block based on the light its receiving. Args: iBlockAccess, x, y, z
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public int getMixedBrightnessForBlock(IBlockAccess p_149677_1_, int p_149677_2_, int p_149677_3_, int p_149677_4_) {
		return getBase().getMixedBrightnessForBlock(p_149677_1_, p_149677_2_, p_149677_3_, p_149677_4_);
	}

	/**
	 * Returns whether this block is collideable based on the arguments passed in
	 */
	@Override
	public boolean canCollideCheck(int p_149678_1_, boolean p_149678_2_) {
		return getBase().canCollideCheck(p_149678_1_, p_149678_2_);
	}

	@Override
	public void breakBlock(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_) {
		getBase().breakBlock(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
	}

	@Override
	public MapColor getMapColor(int p_149728_1_) {
		return getBase().getMapColor(p_149728_1_);
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	@Override
	public boolean canPlaceBlockAt(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_) {
		return getBase().canPlaceBlockAt(p_149742_1_, p_149742_2_, p_149742_3_, p_149742_4_);
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_) {
		getBase().onBlockAdded(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
	}

	/**
	 * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityWalking(World p_149724_1_, int p_149724_2_, int p_149724_3_, int p_149724_4_, Entity p_149724_5_) {
		getBase().onEntityWalking(p_149724_1_, p_149724_2_, p_149724_3_, p_149724_4_, p_149724_5_);
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_) {
		getBase().updateTick(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_, p_149674_5_);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
		return getBase().onBlockActivated(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_, p_149727_5_, 0, 0.0F, 0.0F, 0.0F);
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	@Override
	public void onBlockDestroyedByExplosion(World p_149723_1_, int p_149723_2_, int p_149723_3_, int p_149723_4_, Explosion p_149723_5_) {
		getBase().onBlockDestroyedByExplosion(p_149723_1_, p_149723_2_, p_149723_3_, p_149723_4_, p_149723_5_);
	}

	@Override
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
		return getBase().addDestroyEffects(world, x, y, z, meta, effectRenderer);
	}

	@Override
	protected void dropBlockAsItem(World p_149642_1_, int p_149642_2_, int p_149642_3_, int p_149642_4_, ItemStack p_149642_5_) {
		super.dropBlockAsItem(p_149642_1_, p_149642_2_, p_149642_3_, p_149642_4_, p_149642_5_);
	}

	/**
	 * Used to replace the base ore with the deepslate version in drop lists.
	 * If it drops the base ore block, replace it with the base deepslate block (eg iron ore drop is swapped for deepslate iron ore drop)
	 */
	protected ItemStack checkDrop(ItemStack drop) {
		Block droppedBlock = Block.getBlockFromItem(drop.getItem());
		if (droppedBlock == getBase()) {
			Item thisAsItem = Item.getItemFromBlock(this);
			if (thisAsItem != null) {
				drop.func_150996_a(thisAsItem);
				drop.itemDamage = 0;
				return drop;
			}
		} else if (droppedBlock == Blocks.stone) {
			drop.func_150996_a(ModBlocks.DEEPSLATE.getItem());
		} else if (droppedBlock == Blocks.cobblestone) {
			drop.func_150996_a(ModBlocks.COBBLED_DEEPSLATE.getItem());
		}
		return drop;
	}

	@Override
	public String getNameDomain() {
		return super.getNameDomain() + (getTextureSubfolder().isEmpty() ? "" : (super.getNameDomain().isEmpty() ? "" : ".") + getTextureSubfolder());
	}

	@Override
	public String getTextureDomain() {
		return Reference.MOD_ID;
	}

	public abstract Block getBase();

	public int getBaseMeta() {
		return 0;
	}

	public static final List<BaseDeepslateOre> loaded = Lists.newLinkedList();

	@Override
	public void postInitAction() {
		if (!getBase().getClass().getName().contains("net.minecraft.block") && getBase() != ModBlocks.COPPER_ORE.get()) {
			loaded.add(this);
		}
	}

	@Override
	public void onLoadAction() {
		DummyWorld world = DummyWorld.GLOBAL_DUMMY_WORLD;
		Block block = getBase();
		//See BlockGeneralModdedDeepslateOre for a comment on why we do this cursed stuff
		world.setBlock(0, 0, 0, block, getBaseMeta(), 0);
		try {
			if (block.getHarvestTool(getBaseMeta()) != null) {
				setHarvestLevel("pickaxe", block.getHarvestLevel(getBaseMeta()));
			}
			blockHardness = ConfigFunctions.useStoneHardnessForDeepslate ? block.getBlockHardness(world, 0, 0, 0) : block.getBlockHardness(world, 0, 0, 0) * 1.5F;
			blockResistance = block.getExplosionResistance(null, world, 0, 0, 0, 0, 0, 0) * 5; //Because the game divides it by 5 for some reason
		} catch (Exception e) {
			setHarvestLevel("pickaxe", 1);
			blockHardness = ConfigFunctions.useStoneHardnessForDeepslate ? Blocks.iron_ore.blockHardness : Blocks.iron_ore.blockHardness * 1.5F;
			blockResistance = Blocks.iron_ore.blockResistance;
		}
		world.clearBlocksCache();
	}
}
