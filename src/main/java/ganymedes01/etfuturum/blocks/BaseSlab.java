package ganymedes01.etfuturum.blocks;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class BaseSlab extends BlockSlab implements ISubBlocksBlock {

	private IIcon[] icons;
	public final String[] types;
	private final BaseSlab singleSlab;
	private BaseSlab doubleSlab;
	/**
	 * Used to know the previous slab registered, so the slab knows what the double slab version is without passing it as a constructor argument every time
	 * We store the previous slab we registered, so it can be passed to the double slab that registers after it.
	 */
	private static BaseSlab previousSlab;
	private Block mapColorBase;

	public BaseSlab(boolean isDouble, Material material, String... names) {
		super(isDouble, material);
		if (names.length > 8) {
			throw new IllegalArgumentException("Slabs can't have more than 8 subtypes! Tried to register a slab with " + names.length);
		}
		types = names;
		useNeighborBrightness = !isDouble;
		opaque = isDouble;
		if (isDouble) {
			doubleSlab = this;
			previousSlab.doubleSlab = this;
			singleSlab = previousSlab;
			previousSlab = null;
		} else {
			singleSlab = previousSlab = this;
			setCreativeTab(EtFuturum.creativeTabBlocks);
		}
	}

	public BaseSlab setUnlocalizedNameWithPrefix(String name) {
		setBlockName(Utils.getUnlocalisedName(name));
		return this;
	}

	public BaseSlab setNames(String name) {
		setUnlocalizedNameWithPrefix(name);
		setBlockTextureName(name);
		return this;
	}

	public BaseSlab setToolClass(String toolClass, int level) {
		for (int m = 0; m < 8; m++) {
			setHarvestLevel(toolClass, level, m);
		}
		return this;
	}

	public BaseSlab setToolClass(String toolClass, int level, int meta) {
		setHarvestLevel(toolClass, level, meta);
		if (meta < 8) {
			setHarvestLevel(toolClass, level, meta + 8);
		}
		return this;
	}

	public BaseSlab setMapColorBaseBlock(Block block) {
		mapColorBase = block;
		return this;
	}

	public BaseSlab setBlockSound(SoundType type) {
		Utils.setBlockSound(this, type);
		return this;
	}

	@Override
	public MapColor getMapColor(int meta) {
		return mapColorBase == null ? super.getMapColor(meta % 8) : mapColorBase.getMapColor(meta % 8);
	}

	public BaseSlab getDoubleSlab() {
		return doubleSlab;
	}

	public BaseSlab getSingleSlab() {
		return singleSlab;
	}

	@Override
	public IIcon[] getIcons() {
		return icons;
	}

	protected void setIcons(IIcon[] icons) {
		this.icons = icons;
	}

	@Override
	public String[] getTypes() {
		return types;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		setIcons(new IIcon[getTypes().length]);
		for (int i = 0; i < getIcons().length; i++) {
			getIcons()[i] = "".equals(getTypes()[i]) ? reg.registerIcon(getTextureName()) : reg.registerIcon((getTextureDomain().isEmpty() ? "" : getTextureDomain() + ":") + getTypes()[i]);
		}
		blockIcon = getIcons()[0];
	}

	@Override
	public String getNameFor(ItemStack stack) {
		return func_150002_b(stack.getItemDamage()); // getFullSlabName
	}

	/**
	 * Returns the slab block name with the type associated with it
	 * <p>
	 * MCP name: {@code getFullSlabName}
	 */
	@Override
	public String func_150002_b(int meta) {
		String type = getTypes()[Math.max(0, (meta % 8) % getTypes().length)];
		type = ("".equals(type) ? getUnlocalizedName().replace("tile.", "").replace("etfuturum.", "") : type);
		return type.replace("bricks", "brick").replace("tiles", "tile") + "_slab";
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		if (!field_150004_a) { // isFullBlock
			for (int i = 0; i < types.length; i++) {
				list.add(new ItemStack(itemIn, 1, i));
			}
		}
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return icons[(meta % 8) % icons.length];
	}

	@Override
	public Item getItem(World worldIn, int x, int y, int z) {
		return Item.getItemFromBlock(singleSlab);
	}

	/*
	 * Vanilla slabs don't override this so how the hell do they make double slabs drop the single variant?
	 * If I could figure it out I likely won't need to store the single slab...
	 */
	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return Item.getItemFromBlock(singleSlab);
	}

	// Block.harvestBlock calls this for silk touch drops. Double slabs must yield 2 single slabs,
	// not the double slab item (which has no inventory representation).
	@Override
	protected ItemStack createStackedBlock(int meta) {
		if (field_150004_a) { // isDouble
			return new ItemStack(Item.getItemFromBlock(singleSlab), 2, meta % 8);
		}
		return super.createStackedBlock(meta);
	}

	private final Map<Integer, Float> hardnesses = Maps.newHashMap();
	private final Map<Integer, Float> resistances = Maps.newHashMap();

	@Override
	public float getBlockHardness(World worldIn, int x, int y, int z) {
		return hardnesses.getOrDefault(worldIn.getBlockMetadata(x, y, z), super.getBlockHardness(worldIn, x, y, z));
	}

	public BaseSlab setHardnessValues(float hardness, int... metas) {
		if (metas.length == 0) {
			setHardness(hardness);
		} else for (int meta : metas) {
			hardnesses.put(meta + 8, hardness);
			hardnesses.put(meta, hardness);
		}
		return this;
	}

	@Override
	public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		Float resistance = resistances.get(world.getBlockMetadata(x, y, z));
		if (resistance != null) {
			return resistance / 5.0F;
		}
		return super.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
	}

	public BaseSlab setResistanceValues(float resistance, int... metas) {
		if (metas.length == 0) {
			setResistance(resistance);
		} else for (int meta : metas) {
			resistances.put(meta + 8, resistance);
			resistances.put(meta, resistance);
		}
		return this;
	}
}