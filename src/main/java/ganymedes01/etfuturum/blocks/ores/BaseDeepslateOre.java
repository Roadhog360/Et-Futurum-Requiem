package ganymedes01.etfuturum.blocks.ores;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.blocks.BaseBlock;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.core.utils.IInitAction;
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
import roadhog360.hogutils.api.world.DummyWorld;

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
	public int quantityDropped(Random random) {
		return getBase().quantityDropped(random);
	}

	@Override
	public int quantityDroppedWithBonus(int i, Random random) {
		return getBase().quantityDroppedWithBonus(i, random);
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return getBase().quantityDropped(meta, fortune, random);
	}

	/**
	 * Determines the damage on the item the block drops. Used in cloth and wood.
	 */
	@Override
	public int damageDropped(int meta) {
		return getBase().damageDropped(meta);
	}

	@Override
	public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
		return getBase().getExpDrop(world, metadata, fortune);
	}

	/**
	 * Called when a player hits the block. Args: world, x, y, z, player
	 */
	@Override
	public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player) {
		getBase().onBlockClicked(worldIn, x, y, z, player);
	}

	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
	@Override
	public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
		getBase().randomDisplayTick(worldIn, x, y, z, random);
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
	 */
	@Override
	public void onBlockDestroyedByPlayer(World worldIn, int x, int y, int z, int meta) {
		getBase().onBlockDestroyedByPlayer(worldIn, x, y, z, meta);
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
	public void velocityToAddToEntity(World worldIn, int x, int y, int z, Entity entityIn, Vec3 velocity) {
		getBase().velocityToAddToEntity(worldIn, x, y, z, entityIn, velocity);
	}

	/**
	 * How bright to render this block based on the light its receiving. Args: iBlockAccess, x, y, z
	 */
	@Override
	public int getMixedBrightnessForBlock(IBlockAccess worldIn, int x, int y, int z) {
		return getBase().getMixedBrightnessForBlock(worldIn, x, y, z);
	}

	/**
	 * Returns whether this block is collideable based on the arguments passed in
	 */
	@Override
	public boolean canCollideCheck(int meta, boolean includeLiquid) {
		return getBase().canCollideCheck(meta, includeLiquid);
	}

	@Override
	public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
		getBase().breakBlock(worldIn, x, y, z, blockBroken, meta);
	}

	@Override
	public MapColor getMapColor(int meta) {
		return getBase().getMapColor(meta);
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	@Override
	public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
		return getBase().canPlaceBlockAt(worldIn, x, y, z);
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World worldIn, int x, int y, int z) {
		getBase().onBlockAdded(worldIn, x, y, z);
	}

	/**
	 * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityWalking(World worldIn, int x, int y, int z, Entity entityIn) {
		getBase().onEntityWalking(worldIn, x, y, z, entityIn);
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World worldIn, int x, int y, int z, Random random) {
		getBase().updateTick(worldIn, x, y, z, random);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		return getBase().onBlockActivated(worldIn, x, y, z, player, 0, 0.0F, 0.0F, 0.0F);
	}

	/**
	 * Called upon the block being destroyed by an explosion
	 */
	@Override
	public void onBlockDestroyedByExplosion(World worldIn, int x, int y, int z, Explosion explosionIn) {
		getBase().onBlockDestroyedByExplosion(worldIn, x, y, z, explosionIn);
	}

	@Override
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
		return getBase().addDestroyEffects(world, x, y, z, meta, effectRenderer);
	}

	@Override
	protected void dropBlockAsItem(World worldIn, int x, int y, int z, ItemStack itemIn) {
		super.dropBlockAsItem(worldIn, x, y, z, itemIn);
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
				drop.func_150996_a(thisAsItem); // setItem
				drop.itemDamage = 0;
				return drop;
			}
		} else if (droppedBlock == Blocks.stone) {
			drop.func_150996_a(ModBlocks.DEEPSLATE.getItem()); // setItem
		} else if (droppedBlock == Blocks.cobblestone) {
			drop.func_150996_a(ModBlocks.COBBLED_DEEPSLATE.getItem()); // setItem
		}
		return drop;
	}

	@Override
	public String getNameDomain() {
		return super.getNameDomain() + (getTextureSubfolder().isEmpty() ? "" : (super.getNameDomain().isEmpty() ? "" : ".") + getTextureSubfolder());
	}

	@Override
	public String getTextureDomain() {
		return Tags.MOD_ID;
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
		DummyWorld world = DummyWorld.getGlobalInstance();
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
		world.clearFakeData();
	}
}
