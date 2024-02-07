package ganymedes01.etfuturum.backhand.client.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BackhandClientGuiHandler implements IGuiHandler {
    
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public void sendAnimationPacket(EnumBGAnimations animation, EntityPlayer entityPlayer) {
        if (entityPlayer instanceof EntityClientPlayerMP) {
            ((EntityClientPlayerMP) entityPlayer).sendQueue.addToSendQueue(
                    new BattlegearAnimationPacket(animation, entityPlayer).generatePacket());
        }
    }
}
