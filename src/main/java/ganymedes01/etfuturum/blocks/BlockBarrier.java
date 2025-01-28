package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

public class BlockBarrier extends BaseEFRBlock implements IFloatingParticleBlock {

	/**
	 * Caches barrier particles spawned, so we don't just spam barrier particles
	 */
	protected static final Map<BlockPos, EntityFX> INVISIBLE_BLOCK_FX_MAP = new WeakHashMap<>();

	public BlockBarrier(String type) {
		super(Material.rock, type);
		setBlockUnbreakable();
		setResistance(2000000F);
		setLightOpacity(0);
		useNeighborBrightness = true;
		canBlockGrass = false;
	}

	public BlockBarrier() {
		this("barrier");
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		ItemStack heldItem = FMLClientHandler.instance().getClient().thePlayer.getHeldItem();

		if (player.capabilities.isCreativeMode && heldItem != null && heldItem.getItem() == Item.getItemFromBlock(this)) {
			BlockPos pos = new BlockPos(x, y, z);
			if (!INVISIBLE_BLOCK_FX_MAP.containsKey(pos) || INVISIBLE_BLOCK_FX_MAP.get(pos).isDead) {
				EntityFX particle = CustomParticles.spawnInvisibleBlockParticle(world, x + .5F, y + .5F, z + .5F);
				INVISIBLE_BLOCK_FX_MAP.put(pos, particle);
			}
		}
	}

	@Override
	public int getMobilityFlag() {
		return 2;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isNormalCube() {
		return true;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, int x, int y, int z, int side) {
		return true;
	}

	@Override
	public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		return false;
	}

	@Override
	public IIcon getParticleName(int meta) {
		return blockIcon;
	}
}
