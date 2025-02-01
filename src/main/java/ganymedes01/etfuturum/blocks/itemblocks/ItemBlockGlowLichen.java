package ganymedes01.etfuturum.blocks.itemblocks;

import ganymedes01.etfuturum.tileentities.TileEntityGlowLichen;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemBlockGlowLichen extends ItemBlock 
{
    public ItemBlockGlowLichen(Block block) {
        super(block);
    }
    
    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) 
    {
        Block existingBlock = world.getBlock(x, y, z);

        if (existingBlock == field_150939_a) 
        {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityGlowLichen glowLichen) 
            {
                int sideBit = 1 << ForgeDirection.OPPOSITES[side];
                if ((glowLichen.getSideMap() | sideBit) == glowLichen.getSideMap())
                {
                    return false;
                }
                glowLichen.setSideMap(glowLichen.getSideMap() | sideBit);
            }
            return true;
        }
        
        if (!world.setBlock(x, y, z, field_150939_a, metadata, 3)) 
        {
            return false;
        }

        if (world.getBlock(x, y, z) == field_150939_a) 
        {
            field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
            field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
        }

        return true;
    }
}
