package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockCaveVines;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemGlowBerries extends BaseFood {
    private final Block placedBlock;

    public ItemGlowBerries(Block placedBlock) {
        super(2, 0.1F, false);
        this.placedBlock = placedBlock;
        setNames("glow_berries");
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        Block block = world.getBlock(x, y, z);

        if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            side = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush)
        {
            if (side == 0)
            {
                --y;
            }

            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }
        }

        if (!player.canPlayerEdit(x, y, z, side, itemStack))
        {
            return false;
        }
        else if (itemStack.stackSize == 0)
        {
            return false;
        }
        else if (world.canPlaceEntityOnSide(ModBlocks.CAVE_VINE.get(), x, y, z, false, side, null, itemStack)) {

            if (placeBlockAt(itemStack, player, world, x, y, z, side, p_77648_8_, p_77648_9_, p_77648_10_)) {
                world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, ModBlocks.CAVE_VINE.get().stepSound.func_150496_b()/*getPlaceSound*/, (ModBlocks.CAVE_VINE.get().stepSound.getVolume() + 1.0F) / 2.0F, ModBlocks.CAVE_VINE.get().stepSound.getPitch() * 0.8F);
                --itemStack.stackSize;
            }

            return true;
        } else {
            return super.onItemUse(itemStack, player, world, x, y, z, side, p_77648_8_, p_77648_9_, p_77648_10_);
        }
    }

    private boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float subValueX, float subValueY, float subValueZ)
    {
        if (world.isSideSolid(x, y + 1, z, ForgeDirection.DOWN))
        {
            world.setBlock(x, y, z, ModBlocks.CAVE_VINE.get(), 0, 3);
            return true;
        }
        else if (world.getBlock(x, y + 1, z) instanceof BlockCaveVines headVine)
        {
            headVine.growVine(world, x, y + 1, z, true);
        }
        return false;
    }
}
