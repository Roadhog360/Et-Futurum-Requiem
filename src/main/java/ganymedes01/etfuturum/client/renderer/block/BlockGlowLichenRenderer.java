package ganymedes01.etfuturum.client.renderer.block;

import ganymedes01.etfuturum.tileentities.TileEntityGlowLichen;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockGlowLichenRenderer extends BlockModelBase {
    public BlockGlowLichenRenderer(int renderId) {
        super(renderId);
    }

    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        TileEntity te = world.getTileEntity(x,y,z);
        float blockOffset = 0.00625F;
        Tessellator tess = Tessellator.instance;
        IIcon blockIcon = block.getIcon(0, 0);
        
        
        tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        
        int colorMult = block.colorMultiplier(world, x, y, z);
        float r = (float)(colorMult >> 16 & 255) / 255.0F;
        float g = (float)(colorMult >> 8 & 255) / 255.0F;
        float b = (float)(colorMult & 255) / 255.0F;
        tess.setColorOpaque_F(r, g, b);
        
        if (te instanceof TileEntityGlowLichen glowLichenTe)
        {
            int sideGrowth = glowLichenTe.getSideMap();
            
            // DOWN (ordinal 0)
            if ((sideGrowth & (1 << ForgeDirection.DOWN.ordinal())) != 0) {
                renderer.setRenderBounds(0.0F, blockOffset, 0.0F, 1.0F, blockOffset, 1.0F);
                renderer.renderFaceYPos(block, x, y, z, blockIcon);
                renderer.renderFaceYNeg(block, x, y, z, blockIcon);
            }

            // UP (ordinal 1)
            if ((sideGrowth & (1 << ForgeDirection.UP.ordinal())) != 0) {
                renderer.setRenderBounds(0.0F, 1.0F - blockOffset, 0.0F, 1.0F, 1.0F - blockOffset, 1.0F);
                renderer.renderFaceYPos(block, x, y, z, blockIcon);
                renderer.renderFaceYNeg(block, x, y, z, blockIcon);
            }

            // NORTH (ordinal 2)
            if ((sideGrowth & (1 << ForgeDirection.NORTH.ordinal())) != 0) {
                renderer.setRenderBounds(0.0F, 0.0F, blockOffset, 1.0F, 1.0F, blockOffset);
                renderer.renderFaceZPos(block, x, y, z, blockIcon);
                renderer.renderFaceZNeg(block, x, y, z, blockIcon);
            }

            // SOUTH (ordinal 3)
            if ((sideGrowth & (1 << ForgeDirection.SOUTH.ordinal())) != 0) {
                renderer.setRenderBounds(0.0F, 0.0F, 1.0F - blockOffset, 1.0F, 1.0F, 1.0F - blockOffset);
                renderer.renderFaceZPos(block, x, y, z, blockIcon);
                renderer.renderFaceZNeg(block, x, y, z, blockIcon);
            }

            // WEST (ordinal 4)
            if ((sideGrowth & (1 << ForgeDirection.WEST.ordinal())) != 0) {
                renderer.setRenderBounds(blockOffset, 0.0F, 0.0F, blockOffset, 1.0F, 1.0F);
                renderer.renderFaceXPos(block, x, y, z, blockIcon);
                renderer.renderFaceXNeg(block, x, y, z, blockIcon);
            }

            // EAST (ordinal 5)
            if ((sideGrowth & (1 << ForgeDirection.EAST.ordinal())) != 0) {
                renderer.setRenderBounds(1.0F - blockOffset, 0.0F, 0.0F, 1.0F - blockOffset, 1.0F, 1.0F);
                renderer.renderFaceXPos(block, x, y, z, blockIcon);
                renderer.renderFaceXNeg(block, x, y, z, blockIcon);
            }
        }
        return true;
    }
}

