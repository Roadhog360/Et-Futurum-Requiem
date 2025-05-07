package ganymedes01.etfuturum.api.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.blocks.BlockGlowLichen;
import ganymedes01.etfuturum.client.renderer.boundingbox.GlowLichenBoundingBoxRenderer;
import ganymedes01.etfuturum.tileentities.TileEntityGlowLichen;
import net.minecraft.block.Block;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public class GlowLichenHighlightEvent 
{
    public GlowLichenBoundingBoxRenderer boxRenderer = new GlowLichenBoundingBoxRenderer();
    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        World world = event.player.worldObj;
        MovingObjectPosition target = event.target;

        if (target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int x = target.blockX;
            int y = target.blockY;
            int z = target.blockZ;
            Block block = world.getBlock(x, y, z);

            if (!(block instanceof BlockGlowLichen)) return;
            if (world.getTileEntity(x, y, z) instanceof TileEntityGlowLichen glowLichenTE) {
                event.setCanceled(true);
                boxRenderer.Render(glowLichenTE, x, y, z, event.player, event.partialTicks);
            }
        }
    }
}
