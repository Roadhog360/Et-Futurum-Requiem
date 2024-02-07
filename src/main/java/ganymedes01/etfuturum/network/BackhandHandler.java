package ganymedes01.etfuturum.network;

import java.util.Hashtable;
import java.util.Map;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import ganymedes01.etfuturum.backhand.packets.BackhandBasePacket;
import ganymedes01.etfuturum.backhand.packets.BackhandAttackPacket;
import ganymedes01.etfuturum.backhand.packets.BackhandConfigSyncPacket;
import ganymedes01.etfuturum.backhand.packets.BackhandPlaceBlockPacket;
import ganymedes01.etfuturum.backhand.packets.BackhandSwapClientPacket;
import ganymedes01.etfuturum.backhand.packets.BackhandSwapPacket;
import ganymedes01.etfuturum.backhand.packets.BackhandToServerPacket;
import ganymedes01.etfuturum.backhand.packets.BackhandWorldHotswapPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

public class BackhandHandler {

    public static final BackhandHandler INSTANCE = new BackhandHandler();

    public static FMLEventChannel Channel;
    public static FMLEventChannel ChannelPlayer;
    
    public Map<String, BackhandBasePacket> map = new Hashtable<>();
    public Map<String, FMLEventChannel> channels = new Hashtable<>();

    public BackhandHandler() {
        map.put(BackhandPlaceBlockPacket.packetName, new BackhandPlaceBlockPacket());
        map.put(BackhandToServerPacket.packetName, new BackhandToServerPacket());
        map.put(BackhandSwapPacket.packetName, new BackhandSwapPacket());
        map.put(BackhandSwapClientPacket.packetName, new BackhandSwapClientPacket());
        map.put(BackhandAttackPacket.packetName, new BackhandAttackPacket());
        map.put(BackhandWorldHotswapPacket.packetName, new BackhandWorldHotswapPacket());
        map.put(BackhandConfigSyncPacket.packetName, new BackhandConfigSyncPacket());
    }
    
    public void register(NetworkRegistry registry){
        FMLEventChannel eventChannel;
        for(String channel:map.keySet()){
            eventChannel = registry.newEventDrivenChannel(channel);
            eventChannel.register(this);
            channels.put(channel, eventChannel);
        }
        Channel = registry.newEventDrivenChannel("Backhand");
        ChannelPlayer = registry.newEventDrivenChannel("BackhandPlayer");
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        map.get(event.packet.channel()).process(event.packet.payload(), ((NetHandlerPlayServer)event.handler).playerEntity);
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event){
        map.get(event.packet.channel()).process(event.packet.payload(), Minecraft.getMinecraft().thePlayer);
    }

    public void sendPacketToPlayer(FMLProxyPacket packet, EntityPlayerMP player){
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            channels.get(packet.channel()).sendTo(packet, player);
        }
    }

    public void sendPacketToServer(FMLProxyPacket packet){
        packet.setTarget(Side.SERVER);
        channels.get(packet.channel()).sendToServer(packet);
    }

    public void sendPacketAround(Entity entity, double range, FMLProxyPacket packet){
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            channels.get(packet.channel()).sendToAllAround(packet, new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, range));
        }
    }

    public void sendPacketToAll(FMLProxyPacket packet){
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            channels.get(packet.channel()).sendToAll(packet);
        }
    }
}
