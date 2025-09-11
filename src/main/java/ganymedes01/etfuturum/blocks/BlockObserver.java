package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.ducks.IObserverWorldExtension;
import ganymedes01.etfuturum.lib.RenderIDs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Random;

public class BlockObserver extends Block implements IBlockObserver {

	private IIcon observerFront, observerBack, observerTop, observerBackLit;

	private static final ThreadLocal<MutableInt> timesDisabled = ThreadLocal.withInitial(MutableInt::new);

	public BlockObserver() {
		super(Material.rock);
		this.setHardness(3F);
		this.setHarvestLevel("pickaxe", 0);
		this.setResistance(3F);
		this.setBlockName(Utils.getUnlocalisedName("observer"));
		this.setBlockTextureName("observer");
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		int k = BlockPistonBase.getPistonOrientation(meta);
		boolean powered = (meta & 0x8) != 0;
		if (side == k)
			return this.observerFront;
		else if (side == Facing.oppositeSide[k])
			return powered ? this.observerBackLit : this.observerBack;
		else {
			int topSide;
			switch (k) {
				case 2:
				case 3:
				case 4:
				case 5:
					topSide = 1;
					break;
				default:
				case 0:
				case 1:
					topSide = 2;
					break;
			}
			return (side == topSide || side == Facing.oppositeSide[topSide]) ? this.observerTop : this.blockIcon;
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
		int l = BlockPistonBase.determineOrientation(worldIn, x, y, z, placer);
		worldIn.setBlockMetadataWithNotify(x, y, z, Facing.oppositeSide[l], 2);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.blockIcon = reg.registerIcon("observer_side");
		this.observerFront = reg.registerIcon("observer_front");
		this.observerTop = reg.registerIcon("observer_top");
		this.observerBack = reg.registerIcon("observer_back");
		this.observerBackLit = reg.registerIcon("observer_back_lit");
	}

	@Override
	public int getRenderType() {
		return RenderIDs.OBSERVER;
	}

	@Override
	public void observedNeighborChange(World world, int observerX, int observerY, int observerZ, Block changedBlock, int changedX, int changedY, int changedZ) {
		if (world.isRemote)
			return;
		int myMeta = world.getBlockMetadata(observerX, observerY, observerZ);
		int facing = BlockPistonBase.getPistonOrientation(myMeta);
		int observedX = observerX + Facing.offsetsXForSide[facing];
		int observedY = observerY + Facing.offsetsYForSide[facing];
		int observedZ = observerZ + Facing.offsetsZForSide[facing];
		if (observedX == changedX && observedY == changedY && observedZ == changedZ) {
			if ((myMeta & 8) == 0 && !((IObserverWorldExtension) world).efr$hasScheduledUpdate(observerX, observerY, observerZ, this)) {
				world.scheduleBlockUpdate(observerX, observerY, observerZ, this, 2);
			}
		}
	}

	protected void updateNeighborsInFront(World worldIn, int x, int y, int z) {
		int facing = BlockPistonBase.getPistonOrientation(worldIn.getBlockMetadata(x, y, z));
		int opposite = Facing.oppositeSide[facing];
		int newX = x + Facing.offsetsXForSide[opposite];
		int newY = y + Facing.offsetsYForSide[opposite];
		int newZ = z + Facing.offsetsZForSide[opposite];
		worldIn.notifyBlockOfNeighborChange(newX, newY, newZ, this);
		worldIn.notifyBlocksOfNeighborChange(newX, newY, newZ, this, facing);
	}


	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int meta = world.getBlockMetadata(x, y, z);
		if ((meta & 8) != 0) {
			world.setBlockMetadataWithNotify(x, y, z, meta & 7, 2);
		} else {
			world.setBlockMetadataWithNotify(x, y, z, meta | 8, 2);
			world.scheduleBlockUpdate(x, y, z, this, 2);
		}
		updateNeighborsInFront(world, x, y, z);
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side) {
		return isProvidingWeakPower(worldIn, x, y, z, side);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side) {
		int metadata = worldIn.getBlockMetadata(x, y, z);
		if ((metadata & 8) == 0)
			return 0;
		return (BlockPistonBase.getPistonOrientation(metadata) == side) ? 15 : 0;
	}

	@Override
	public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	/**
	 * <p>Disable notification of observers about chunk updates on the current thread. Observer notifications add
	 * some overhead to chunk updates, so they should be disabled if it's reasonable to assume that no observers will
	 * see them (e.g. during world gen).</p>
	 *
	 * <p>Calling this method will increase the disablification stack by one level. Each call should be followed by a
	 * {@link BlockObserver#enableNotifications()} call to restore the stack.</p>
	 */
	public static void disableNotifications() {
		timesDisabled.get().increment();
	}

	public static void enableNotifications() {
		MutableInt timesDisabledMut = timesDisabled.get();
		int timesDisabledInt = timesDisabledMut.intValue();
		timesDisabledInt = Math.max(0, timesDisabledInt - 1);
		timesDisabledMut.setValue(timesDisabledInt);
	}

	public static boolean areNotificationsEnabled() {
		return timesDisabled.get().intValue() == 0;
	}
}
