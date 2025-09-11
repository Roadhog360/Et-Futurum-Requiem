package ganymedes01.etfuturum.blocks.ores;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.blocks.BaseSubtypesBlock;
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

public abstract class BaseSubtypesDeepslateOre extends BaseSubtypesBlock implements IInitAction {

	public BaseSubtypesDeepslateOre(String... types) {
		super(Material.rock, types);
		setBlockSound(ModSounds.soundDeepslate);
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return checkDrop(new ItemStack(getBase(meta).getItemDropped(getBaseMeta(meta), rand, fortune), 1, fortune), meta).getItem();
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> list = getBase(metadata).getDrops(world, x, y, z, getBaseMeta(metadata), fortune);
		list.forEach(itemStack -> this.checkDrop(itemStack, metadata));
		return list;
	}

	//	@Override //TODO: Figure out how to capture meta in case a mod overrides these. Although it's unlikely this is needed so it's "FINE" for now
//	public int quantityDropped(Random random) {
//		return getBase().quantityDropped(random);
//	}
//
//	@Override
//	public int quantityDroppedWithBonus(int i, Random random) {
//		return getBase().quantityDroppedWithBonus(i, random);
//	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return getBase(meta).quantityDropped(getBaseMeta(meta), fortune, random);
	}

	/**
	 * Determines the damage on the item the block drops. Used in cloth and wood.
	 */
	@Override
	public int damageDropped(int meta) {
		return getBase(meta).damageDropped(getBaseMeta(meta));
	}

	@Override
	public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
		return getBase(metadata).getExpDrop(world, getBaseMeta(metadata), fortune);
	}

	@Override
	public MapColor getMapColor(int meta) {
		return getBase(meta).getMapColor(getBaseMeta(meta));
	}

	/**
	 * Called when a player hits the block. Args: world, x, y, z, player
	 */
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		getBase(world.getBlockMetadata(x, y, z)).onBlockClicked(world, x, y, z, player);
	}

	/**
	 * A randomly called display update to be able to add particles or other items for display
	 */
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		getBase(world.getBlockMetadata(x, y, z)).randomDisplayTick(world, x, y, z, random);
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
	public void velocityToAddToEntity(World world, int x, int y, int z, Entity entityIn, Vec3 velocity) {
		getBase(world.getBlockMetadata(x, y, z)).velocityToAddToEntity(world, x, y, z, entityIn, velocity);
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return getBase(world.getBlockMetadata(x, y, z)).getLightValue();
	}

	/**
	 * How bright to render this block based on the light its receiving. Args: iBlockAccess, x, y, z
	 */
	@Override
	public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z) {
		return getBase(world.getBlockMetadata(x, y, z)).getMixedBrightnessForBlock(world, x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block blockBroken, int meta) {
		getBase(meta).breakBlock(world, x, y, z, blockBroken, getBaseMeta(meta));
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
	public void updateTick(World world, int x, int y, int z, Random random) {
		getBase(world.getBlockMetadata(x, y, z)).updateTick(world, x, y, z, random);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		return getBase(world.getBlockMetadata(x, y, z)).onBlockActivated(world, x, y, z, player, 0, 0.0F, 0.0F, 0.0F);
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

	/**
	 * Used to replace the base ore with the deepslate version in drop lists.
	 * If it drops the base ore block, replace it with the base deepslate block (eg iron ore drop is swapped for deepslate iron ore drop)
	 */
	protected ItemStack checkDrop(ItemStack drop, int meta) {
		Block droppedBlock = Block.getBlockFromItem(drop.getItem());
		if (droppedBlock == getBase(meta)) {
			Item thisAsItem = Item.getItemFromBlock(this);
			if (thisAsItem != null) {
				drop.func_150996_a(thisAsItem); // setItem
				drop.itemDamage = meta;
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
	public boolean canHarvestBlock(EntityPlayer player, int meta) {
		return getBase(meta).canHarvestBlock(player, getBaseMeta(meta));
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		return getBase(metadata).canSilkHarvest(world, player, x, y, z, getBaseMeta(metadata));
	}

	public abstract Block getBase(int meta);

	public int getBaseMeta(int meta) {
		return 0;
	}

	@Override
	public String getNameDomain() {
		return super.getNameDomain() + (getTextureSubfolder().isEmpty() ? "" : (super.getNameDomain().isEmpty() ? "" : ".") + getTextureSubfolder());
	}

	@Override
	public String getTextureDomain() {
		return Tags.MOD_ID;
	}

	@Override
	public int getDamageValue(World worldIn, int x, int y, int z) {
		return worldIn.getBlockMetadata(x, y, z);
	}

	public static final List<BaseSubtypesDeepslateOre> loaded = Lists.newLinkedList();

	@Override
	public void postInitAction() {
		loaded.add(this);
	}

	@Override
	public void onLoadAction() {
		DummyWorld world = DummyWorld.getGlobalInstance();
		for (int i = 0; i < getTypes().length; i++) {
			Block block = getBase(i);
			//See BlockGeneralModdedDeepslateOre for a comment on why we do this cursed stuff
			world.setBlock(0, 0, 0, block, getBaseMeta(i), 0);
			try {
				if (block.getHarvestTool(getBaseMeta(i)) != null) {
					setHarvestLevel("pickaxe", block.getHarvestLevel(getBaseMeta(i)), i);
				}
				setHardnessValues(ConfigFunctions.useStoneHardnessForDeepslate ? block.getBlockHardness(world, 0, 0, 0) : block.getBlockHardness(world, 0, 0, 0) * 1.5F, i);
				setResistanceValues(block.getExplosionResistance(null, world, 0, 0, 0, 0, 0, 0), i);
			} catch (Exception e) {
				setHarvestLevel("pickaxe", 1, i);
				setHardnessValues(ConfigFunctions.useStoneHardnessForDeepslate ? Blocks.iron_ore.blockHardness : Blocks.iron_ore.blockHardness * 1.5F, i);
				setResistanceValues(Blocks.iron_ore.blockResistance, i);
			}
		}
		world.clearFakeData();
	}
}