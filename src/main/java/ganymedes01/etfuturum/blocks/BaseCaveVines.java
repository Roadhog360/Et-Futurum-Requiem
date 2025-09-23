package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class BaseCaveVines extends Block implements IGrowable, IClimbableWithoutWall
{
    private final IIcon[] iicons = new IIcon[2];
    private final String[] iconNames;

    public BaseCaveVines(String[] iconNames)
    {
        super(Material.vine);
        this.setStepSound(soundTypeGrass)
                .setHardness(0.2F)
                .setBlockName(Utils.getUnlocalisedName("cave_vines"))
                .setBlockTextureName("cave_vines")
                .setBlockBounds(0.0625F, 0, 0.0625F, 0.9375F, 1, 0.9375F);
        this.iconNames = iconNames;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        return iicons[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        for (int i = 0; i < iconNames.length; i++)
        {
            iicons[i] = reg.registerIcon(iconNames[i]);
        }
    }

    @Override
    public int getRenderType()
    {
        return 1;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean canBlockStay(World worldIn, int x, int y, int z)
    {
        return (worldIn.getBlock(x, y + 1, z) instanceof BlockCaveVinesPlant) || worldIn.isSideSolid(x, y + 1, z, ForgeDirection.DOWN);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        return getPickBlock(target, world, x, y, z);
    }
    
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        return ModItems.GLOW_BERRIES_ITEM.newItemStack();
    }
    
    protected boolean onBlockActivatedShared(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.getBlockMetadata(x, y, z) == 1)
        {
            world.setBlockMetadataWithNotify(x, y, z, 0, 3);
            world.updateLightByType(EnumSkyBlock.Block, x, y, z);
            if (!world.isRemote)
            {
                world.spawnEntityInWorld(new EntityItem(world, x, y, z, ModItems.GLOW_BERRIES_ITEM.newItemStack(1)));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (!canBlockStay(worldIn, x, y, z))
        {
            if (!worldIn.isRemote)
            {
                worldIn.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(this));
            }
            worldIn.setBlockToAir(x, y, z);
        }
        else if (!(worldIn.getBlock(x, y - 1, z) instanceof BaseCaveVines))
        {
            worldIn.setBlock(x, y, z, ModBlocks.CAVE_VINE.get(), worldIn.getBlockMetadata(x, y, z), 3); // set the meta to the head
        }
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    /**
     * MCP name: {@code canFertilize}
     */
    @Override
    public boolean func_149851_a(World worldIn, int x, int y, int z, boolean isClient) {
        return worldIn.getBlockMetadata(x, y, z) == 0;
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
        if (i == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 1, 3);
            world.updateLightByType(EnumSkyBlock.Block, x, y, z);
        }
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 1) {
            return 14;
        }
        return 0;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        return null;
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        return true;
    }
    
    @Override
    public Item getItemDropped(int meta, Random random, int fortune)
    {
        if (meta == 1)
        {
            return ModItems.GLOW_BERRIES_ITEM.get();
        }
        return null;
    }
}
