package ganymedes01.etfuturum.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockCaveVinesPlant extends BaseCaveVines
{
    public BlockCaveVinesPlant()
    {
        super(new String[] {"cave_vines_plant", "cave_vines_plant_lit"});
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        return onBlockActivatedShared(world, x, y, z, player, side, hitX, hitY, hitZ);
    }
}
