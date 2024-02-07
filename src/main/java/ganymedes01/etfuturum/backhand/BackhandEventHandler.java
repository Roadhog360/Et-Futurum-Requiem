package ganymedes01.etfuturum.backhand;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.backhand.client.renderer.RenderOffhandPlayer;
import ganymedes01.etfuturum.backhand.packets.BackhandSwapPacket;
import ganymedes01.etfuturum.backhand.packets.BackhandWorldHotswapPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import scala.actors.threadpool.Arrays;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.List;

public class BackhandEventHandler {

    public static final BackhandEventHandler INSTANCE = new BackhandEventHandler();

    public static HashMap<UUID,List<ItemStack>> tickStartItems = new HashMap<>();
    public static boolean arrowHotSwapped = false;
    public static boolean regularHotSwap = false;
    public static int fireworkHotSwapped = -1;

    public ItemStack prevStackInSlot;
    public int blacklistDelay = -1;

    @SubscribeEvent
    public void onPlayerInteractNonVanilla(PlayerInteractEvent event) {
        if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
            EntityPlayer player = event.entityPlayer;
            ItemStack mainhandItem = player.getHeldItem();
            ItemStack offhandItem = BattlegearUtils.getOffhandItem(player);
            if((mainhandItem == null || mainhandItem.getItem() != Items.fireworks) && offhandItem != null && offhandItem.getItem() == Items.fireworks) {
                BattlegearUtils.swapOffhandItem(player);
                fireworkHotSwapped = 1;
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (!BattlegearUtils.hasOffhandInventory(player)) {
            ItemStack offhandItem = BattlegearUtils.getOffhandItem(player);
            player.func_146097_a(offhandItem, true, false);
            BattlegearUtils.setPlayerOffhandItem(player,null);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer) || event.entityLiving.getHealth() - event.ammount > 0)
            return;
        try {
            Class<?> totemItem = Class.forName("ganymedes01.etfuturum.items.ItemTotemUndying");

            EntityPlayer player = (EntityPlayer) event.entityLiving;
            ItemStack offhandItem = BattlegearUtils.getOffhandItem(player);
            ItemStack mainhandItem = player.getCurrentEquippedItem();
            if (offhandItem == null) {
                return;
            }

            if (totemItem.isInstance(offhandItem.getItem()) && (mainhandItem == null || !totemItem.isInstance(mainhandItem.getItem()))) {
                BattlegearUtils.swapOffhandItem(player);
                regularHotSwap = true;
                MinecraftForge.EVENT_BUS.post(event);
            }
        } catch (Exception ignored) {}
    }

    @SubscribeEvent
    public void onItemUseStart(PlayerUseItemEvent.Start event) {
        EntityPlayer player = event.entityPlayer;
        ItemStack offhandItem = BattlegearUtils.getOffhandItem(player);
        ItemStack mainhandItem = player.getCurrentEquippedItem();

        //boolean offHandUse = BattlegearUtils.checkForRightClickFunction(offhandItem);
        boolean mainhandUse = BattlegearUtils.checkForRightClickFunction(mainhandItem);

        if (offhandItem != null && !mainhandUse) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onArrowNock(ArrowNockEvent event) {
        if (!Backhand.UseOffhandArrows) {
            return;
        }

        boolean overrideWithOffhand = false;
        ItemStack offhandItem = BattlegearUtils.getOffhandItem(event.entityPlayer);
        if (offhandItem != null) {
            try {
                Class<?> etFuturumArrow = Class.forName("ganymedes01.etfuturum.items.ItemArrowTipped");
                if (etFuturumArrow.isInstance(offhandItem.getItem())) {
                    overrideWithOffhand = true;
                }
            } catch (Exception ignored) {}

            if (Items.arrow == offhandItem.getItem()) {
                overrideWithOffhand = true;
            }

            if (overrideWithOffhand) {
                event.setCanceled(true);
                event.entityPlayer.setItemInUse(event.result, event.result.getItem().getMaxItemUseDuration(event.result));
            }
        }
    }

    @SubscribeEvent
    public void onItemFinish(PlayerUseItemEvent.Finish event) {
        EntityPlayer player = event.entityPlayer;
        ItemStack offhandItem = BattlegearUtils.getOffhandItem(player);
        ItemStack mainhandItem = player.getCurrentEquippedItem();
        boolean mainhandUse = BattlegearUtils.checkForRightClickFunction(mainhandItem);
        if (offhandItem == null || mainhandUse) {
            return;
        }
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && !ServerTickHandler.tickStartItems.containsKey(player.getUniqueID())) {
            BattlegearUtils.swapOffhandItem(player);
            regularHotSwap = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemStop(PlayerUseItemEvent.Stop event) {
        EntityPlayer player = event.entityPlayer;
        ItemStack mainhandItem = player.getCurrentEquippedItem();
        boolean mainhandUse = BattlegearUtils.checkForRightClickFunction(mainhandItem);
        if (BattlegearUtils.getOffhandItem(player) == null || mainhandUse) {
            return;
        }

        if (!ServerTickHandler.tickStartItems.containsKey(player.getUniqueID()) && !regularHotSwap) {
            BattlegearUtils.swapOffhandItem(player);
            regularHotSwap = true;
        }

        if (!Backhand.UseOffhandArrows || !(event.item.getItem() instanceof ItemBow)) {
            return;
        }

        boolean overrideWithOffhand = false;
        ItemStack offhandItem = BattlegearUtils.getOffhandItem(event.entityPlayer);
        if (offhandItem != null) {
            try {
                Class<?> etFuturumArrow = Class.forName("ganymedes01.etfuturum.items.ItemArrowTipped");
                if (etFuturumArrow.isInstance(offhandItem.getItem())) {
                    overrideWithOffhand = true;
                }
            } catch (Exception ignored) {}

            if (Items.arrow == offhandItem.getItem()) {
                overrideWithOffhand = true;
            }

            if (overrideWithOffhand) {
                arrowHotSwapped = true;
                if (offhandItem.getItem() != Items.arrow) {
                    BattlegearUtils.swapOffhandItem(event.entityPlayer);
                }
            }
        }
    }
 
     @SubscribeEvent
     @SuppressWarnings("unchecked")
     public void onUpdateWorld(TickEvent.WorldTickEvent event) {
         if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
             return;
         }
 
         if (Backhand.OffhandTickHotswap) {
             List<EntityPlayer> players = event.world.playerEntities;
             for (EntityPlayer player : players) {
                 ItemStack mainhand = player.getCurrentEquippedItem() == null ? null : player.getCurrentEquippedItem().copy();
                 ItemStack offhand = BattlegearUtils.getOffhandItem(player) == null ? null : BattlegearUtils.getOffhandItem(player).copy();
                 if (offhand == null) {
                     continue;
                 }
 
                 if (event.phase == TickEvent.Phase.START && !player.isUsingItem()) {
                     if (!BattlegearUtils.checkForRightClickFunction(mainhand)) {
                         if (!tickStartItems.containsKey(player.getUniqueID())) {
                             Backhand.packetHandler.sendPacketToPlayer(
                                     new BackhandWorldHotswapPacket(true).generatePacket(), (EntityPlayerMP) player
                             );
                         }
                         tickStartItems.put(player.getUniqueID(), Arrays.asList(mainhand, offhand));
                         player.setCurrentItemOrArmor(0, tickStartItems.get(player.getUniqueID()).get(1));
                     }
                 } else {
                     resetTickingHotswap(player);
                 }
             }
         }
     }
 
     public static void resetTickingHotswap(EntityPlayer player) {
         if (tickStartItems.containsKey(player.getUniqueID())) {
             player.setCurrentItemOrArmor(0, tickStartItems.get(player.getUniqueID()).get(0));
             BattlegearUtils.setPlayerOffhandItem(player, tickStartItems.get(player.getUniqueID()).get(1));
             tickStartItems.remove(player.getUniqueID());
             Backhand.packetHandler.sendPacketToPlayer(
                     new BackhandWorldHotswapPacket(false).generatePacket(), (EntityPlayerMP) player
             );
         }
     }
 
     @SubscribeEvent(
             priority = EventPriority.HIGHEST
     )
     public void onUpdatePlayer(TickEvent.PlayerTickEvent event)
     {
         EntityPlayer player = event.player;
         if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
             if (ServerEventsHandler.regularHotSwap) {
                 BattlegearUtils.swapOffhandItem(player);
                 ServerEventsHandler.regularHotSwap = false;
             }
             return;
         }
 
         ItemStack offhand = BattlegearUtils.getOffhandItem(player);
 
         if (event.phase == TickEvent.Phase.END) {
             if (blacklistDelay > 0) {
                 blacklistDelay--;
             }
             if (Backhand.isOffhandBlacklisted(offhand)) {
                 if (!ItemStack.areItemStacksEqual(offhand,prevStackInSlot)) {
                     blacklistDelay = 10;
                 } else if (blacklistDelay == 0) {
                     BattlegearUtils.setPlayerOffhandItem(player,null);
 
                     boolean foundSlot = false;
                     for (int i = 0; i < player.inventory.getSizeInventory() - 4; i++) {
                         if (i == Backhand.AlternateOffhandSlot)
                             continue;
                         if (player.inventory.getStackInSlot(i) == null) {
                             player.inventory.setInventorySlotContents(i,offhand);
                             foundSlot = true;
                             break;
                         }
                     }
                     if (!foundSlot) {
                         player.entityDropItem(offhand,0);
                     }
                     player.inventoryContainer.detectAndSendChanges();
                 }
             }
             prevStackInSlot = offhand;
         }
 
         if (BattlegearUtils.getOffhandEP(player).syncOffhand) {
             if (!tickStartItems.containsKey(player.getUniqueID())) {
                 Backhand.packetHandler.sendPacketToAll(new BattlegearSyncItemPacket(player).generatePacket());
             }
             BattlegearUtils.getOffhandEP(player).syncOffhand = false;
         }
 
         if (arrowHotSwapped) {
             if (offhand != null && offhand.getItem() != Items.arrow) {
                 BattlegearUtils.swapOffhandItem(player);
             }
             arrowHotSwapped = false;
         }
         if (regularHotSwap) {
             BattlegearUtils.swapOffhandItem(player);
             regularHotSwap = false;
         }
 
         if (fireworkHotSwapped > 0) {
             fireworkHotSwapped--;
         } else if (fireworkHotSwapped == 0) {
             BattlegearUtils.swapOffhandItem(player);
             fireworkHotSwapped--;
             MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_AIR,
                     (int)player.posX, (int)player.posY, (int)player.posZ, -1, player.worldObj));
             BattlegearUtils.swapOffhandItem(player);
         }
     }
}
