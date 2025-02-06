package ganymedes01.etfuturum.lib;

import net.minecraftforge.common.util.ForgeDirection;

public class WorldLocation 
{
    public int x,y,z;
    public WorldLocation(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void moveInDirection(ForgeDirection direction)
    {
        switch(direction)
        {
            case DOWN:
                this.y--;
                break;
            case UP:
                this.y++;
                break;
            case NORTH:
                this.z--;
                break;
            case SOUTH:
                this.z++;
                break;
            case WEST:
                this.x--;
                break;
            case EAST:
                this.x++;
                break;
            case UNKNOWN:
        }
    }
    
    public void moveInDirection(int direction)
    {
        moveInDirection(ForgeDirection.getOrientation(direction));
    }
}
