package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.tileentities.TileEntityCaveVines;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.ArrayList;
import java.util.Random;

public class BlockCaveVines extends BaseCaveVines implements IShearable, ITileEntityProvider
{
    public BlockCaveVines()
    {
        super(new String[]{"cave_vines", "cave_vines_lit"});
        this.setTickRandomly(true);
    }

    public TileEntity createNewTileEntity(World worldIn)
    {
        return createNewTileEntity(worldIn, 0);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCaveVines();
    }

    // Only call after making sure the block is clear to grow!
    public void growVine(World world, int x, int y, int z, boolean manualPlace) {
        Random rand = new Random();
        TileEntity oldTE = world.getTileEntity(x, y, z);
        int maxLength = rand.nextInt(26) + 2;
        if (oldTE instanceof TileEntityCaveVines)
        {
            maxLength = ((TileEntityCaveVines) oldTE).getMaxLength();
        }
        world.setBlock(x, y, z, ModBlocks.CAVE_VINE_PLANT.get(), world.getBlockMetadata(x, y, z), 3);
        if (!manualPlace && rand.nextInt(9) == 0)
        {
            world.setBlock(x, y - 1, z, this, 1, 3);
            world.updateLightByType(EnumSkyBlock.Block, x, y, z);
        }
        else
        {
            world.setBlock(x, y - 1, z, this, 0, 3);
        }
        TileEntity newTE = world.getTileEntity(x, y - 1, z);
        if (newTE instanceof TileEntityCaveVines)
        {
            ((TileEntityCaveVines) newTE).setMaxLength(maxLength);
        }
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityCaveVines teCaveVine)
        {
            return !teCaveVine.getTipSheared();
        }
        return false;
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityCaveVines teCaveVines)
        {
            teCaveVines.setTipSheared(true);
        }
        return null;
    }

    // Just grabbing the block container behavior since I really want to have a base class between cave vines.
    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z)
    {
        super.onBlockAdded(worldIn, x, y, z);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta)
    {
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
        worldIn.removeTileEntity(x, y, z);
    }

    @Override
    public boolean onBlockEventReceived(World worldIn, int x, int y, int z, int eventId, int eventData)
    {
        super.onBlockEventReceived(worldIn, x, y, z, eventId, eventData);
        TileEntity tileentity = worldIn.getTileEntity(x, y, z);
        return tileentity != null ? tileentity.receiveClientEvent(eventId, eventData) : false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (world.getTileEntity(x, y, z) instanceof TileEntityCaveVines teCaveVine)
        {
            System.out.println("Tip Sheared:  " + teCaveVine.getTipSheared());
            System.out.println("Max Length:" + teCaveVine.getMaxLength());
        }
        if (onBlockActivatedShared(world, x, y, z, player, side, hitX, hitY, hitZ)) return true;

        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null)
        {
            if (heldItem.getItem() instanceof ItemShears && isShearable(heldItem, world, x, y, z))
            {
                onSheared(heldItem, world, x, y, z, 0);
                world.playSoundAtEntity(player, "mob.sheep.shear", 1.0F, 1.0F);
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);

        if (!world.isRemote && world.isAirBlock(x, y - 1, z) && random.nextInt(10) == 0) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityCaveVines teCaveVines)
            {
                if (!teCaveVines.getTipSheared() && getLength(world, x, y, z) < teCaveVines.getMaxLength())
                {
                    growVine(world, x, y, z, false);
                }
            }
        }
    }

    private int getLength(World world, int x, int y, int z)
    {
        int i = 1;
        while(world.getBlock(x, y + i, z) instanceof BlockCaveVinesPlant)
        {
            i++;
        }
        return i;
    }
}
