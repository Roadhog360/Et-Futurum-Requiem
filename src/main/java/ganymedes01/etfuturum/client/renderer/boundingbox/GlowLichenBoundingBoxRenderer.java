package ganymedes01.etfuturum.client.renderer.boundingbox;

import ganymedes01.etfuturum.tileentities.TileEntityGlowLichen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class GlowLichenBoundingBoxRenderer 
{   
    // This is all hard coded. If someone wants to figure out an efficient way to do this for an arbitrary shape that would be nice,
    // that was too much work for this and I just was able to code this up in like 2 hours rather than figuring out how to do
    // it in all cases. Vanilla does it in a pretty complex way, but I'm sure it's possible to reverse engineer.
    public void Render(TileEntityGlowLichen te, int x, int y, int z, EntityPlayer player,
                       float partialTicks)
    {
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
        
        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorOpaque_I(-1);
        
        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
        
        // Gets rid of Z-fighting
        float boundingBoxOffset = 0.002F;
        
        // Adjust values based on player position
        double renderX = x - d0;
        double renderY = y - d1;
        double renderZ = z - d2;
        
        
        int sideGrowth = te.getSideMap();
        for (int i = 0; i < ForgeDirection.values().length; i++)
        {
            if ((sideGrowth & (1 << i)) != 0) 
            {
                switch (ForgeDirection.getOrientation(i))
                {
                    case DOWN:
                        // Outer
                        tessellator.startDrawing(3);
                        tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                        tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                        tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                        tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                        tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                        tessellator.draw();
                        // Inner
                        switch ((sideGrowth & 0b111100) >> 2)
                        {
                            case 0: // (nothing)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 1: // (north)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ  + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ  + 0.0625D - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 2: // (south)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 3: // (north south)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 4: // (west)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 5: // (west north)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 6: // (west south)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 7: // (west north south)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 8: // (east)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 9: // (east north)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 10: // (east south)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 11: // (east north south)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 12: // (west east)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 13: // (west east north)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 14: // (west east south)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 15: // (west east north south)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                        }
                        break;
                    case UP:
                        // Outer
                        tessellator.startDrawing(3);
                        tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                        tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                        tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                        tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                        tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                        tessellator.draw();
                        // Inner
                        switch ((sideGrowth & 0b111100) >> 2)
                        {
                            case 0:
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // North west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                
                                // South west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // South east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // North east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 1: // North
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ  + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ  + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // South west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                // South east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 2: // South
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // North east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                // North west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 3: // South-North
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 4: // West
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // South east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                // North east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 5: // West-North
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // South east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 6: // West-South
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // North east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 7: // West-North-South
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 8: // East
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // North west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                // South west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 9: // East-North
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // South west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 10: // East-South
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // North west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 11: // East-North-South
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 12: // East-West
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 13: // East-West-North
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 14: // East-West-South
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 15: // East-West-North-South
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                        }
                        break;
                    case NORTH:
                        // Outer
                        switch ((sideGrowth & 0b000011)) {
                            case 0: // (nothing)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 1: // (down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 2: // (up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 3: // (up down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                        }
                        switch ((sideGrowth & 0b110011)) {
                            case 0: // (nothing)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // up east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // up west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // down west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // down east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 1: // (down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // up east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // up west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 2: // (up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // down west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // down east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 3: // (up down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 16: // (west)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // up east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // down east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 17: // (west down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // up east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 18: // (west up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // down east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 19: // (west up down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 32: // (east)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // up west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // down west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 33: // (east down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // up west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 34: // (east up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // down west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 35: // (east up down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 48: // (east west)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 49: // (east west down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 50: // (east west up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 51: // (east west up down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                        }
                        break;
                    case SOUTH:
                        switch ((sideGrowth & 0b000011)) {
                            case 0: // Bits 1 and 2 are 00 (no up face or down face)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 1: // Bits 1 and 2 are 01 (only a down face)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 2: // Bits 1 and 2 are 10 (only up face)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 3: // Bits 1 and 2 are 11 (up and down face)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                        }
                        switch ((sideGrowth & 0b110011)) {
                            case 0: // (nothing)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // up east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                // up west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                // down west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                // down east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 1: // (down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // up east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                // up west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 2: // (up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // down west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                // down east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 3: // (up down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 16: // (west)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // up east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                // down east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 17: // (west down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // up east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 18: // (west up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // down east
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 19: // (west up down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 32: // (east)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // up west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                // down west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 33: // (east down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // up west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 34: // (east up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // down west
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 35: // (east up down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 48: // (east west)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 49: // (east west down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 50: // (east west up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 51: // (east west up down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                        }
                        break;
                    case WEST:
                        switch ((sideGrowth & 0b001111)) {
                            case 0: // (nothing)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // down south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                // down north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // up north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // up south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 1: // (down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // up north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // up south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 2: // (up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // down south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                // down north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 3: // (up down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 4:  // 000100 (north)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D  - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // down south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                // up south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 5:  // 000101 (north face and down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D  - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // up south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 6:  // 000110 (north face and up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // down south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 7:  // 000111 (north-up-down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 8:  // 001000 (south)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // down north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // up north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 9:  // 001001 (south-down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // up north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 10: // 001010 (south-up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // down south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 11: // 001011 (south-up-down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 12: // 001100 (south-north)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 13: // 001101 (south-north-down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 14: // 001110 (south-north-up)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0 + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.0625D - boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                        }
                        break;
                    case EAST:
                        switch ((sideGrowth & 0b001111)) {
                            case 0: // (nothing)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // down south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                // down north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // up north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // up south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 1: // (down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // up north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // up south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 2: // (up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // down south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                // down north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 3: // (up down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 4:  // 000100 (north)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D  - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // down south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                // up south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 5:  // 000101 (north down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D  - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // up south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 6:  // 000110 (north up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // down south
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 7:  // 000111 (north-up-down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 8:  // 001000 (south)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // down north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                // up north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 9:  // 001001 (south-down)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();

                                // corners
                                // up north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 10: // 001010 (south-up)
                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(3);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                
                                // corners
                                // down north
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 11: // 001011 (south-up-down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.0625D - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 0.9375D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 12: // 001100 (south-north)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 13: // 001101 (south-north-down)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY + 1.0D + boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                            case 14: // 001110 (south-north-up)
                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ - boundingBoxOffset);
                                tessellator.addVertex(renderX + 1.0D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 1.0D + boundingBoxOffset);
                                tessellator.draw();

                                tessellator.startDrawing(1);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.0625D - boundingBoxOffset);
                                tessellator.addVertex(renderX + 0.9375D + boundingBoxOffset, renderY - boundingBoxOffset, renderZ + 0.9375D + boundingBoxOffset);
                                tessellator.draw();
                                break;
                        }
                }
            }
        }
        
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
