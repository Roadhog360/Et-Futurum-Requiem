package ganymedes01.etfuturum.backhand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.api.backhand.BackhandExtendedProperty;
import ganymedes01.etfuturum.backhand.packets.BackhandSyncItemPacket;
import ganymedes01.etfuturum.backhand.packets.BackhandWorldHotswapPacket;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.items.ItemArrowTipped;
import ganymedes01.etfuturum.network.BackhandHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

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
            ItemStack offhandItem = Backhand.INSTANCE.getOffhandItem(player);
            if((mainhandItem == null || mainhandItem.getItem() != Items.fireworks) && offhandItem != null && offhandItem.getItem() == Items.fireworks) {
                Backhand.INSTANCE.swapOffhandItem(player);
                fireworkHotSwapped = 1;
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (!Backhand.hasOffhandInventory(player)) {
            ItemStack offhandItem = Backhand.INSTANCE.getOffhandItem(player);
            player.func_146097_a(offhandItem, true, false);
            Backhand.INSTANCE.setPlayerOffhandItem(player,null);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer) || event.entityLiving.getHealth() - event.ammount > 0)
            return;
        try {
            Item totemItem = ModItems.TOTEM_OF_UNDYING.get();
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            ItemStack offhandItem = Backhand.INSTANCE.getOffhandItem(player);
            ItemStack mainhandItem = player.getCurrentEquippedItem();
            
            if (offhandItem == null) {
                return;
            }

            if (offhandItem.getItem() == totemItem && (mainhandItem == null || mainhandItem.getItem() != totemItem)) {
                Backhand.INSTANCE.swapOffhandItem(player);
                regularHotSwap = true;
                MinecraftForge.EVENT_BUS.post(event);
            }
        } catch (Exception ignored) {}
    }

    @SubscribeEvent
    public void onItemUseStart(PlayerUseItemEvent.Start event) {
        EntityPlayer player = event.entityPlayer;
        ItemStack offhandItem = Backhand.INSTANCE.getOffhandItem(player);
        ItemStack mainhandItem = player.getCurrentEquippedItem();

        //boolean offHandUse = BattlegearUtils.checkForRightClickFunction(offhandItem);
        boolean mainhandUse = Backhand.checkForRightClickFunction(mainhandItem);

        if (offhandItem != null && !mainhandUse) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onArrowNock(ArrowNockEvent event) {
        if (!ConfigFunctions.offhand.useOffhandArrows) {
            return;
        }

        boolean overrideWithOffhand = false;
        ItemStack offhandItem = Backhand.INSTANCE.getOffhandItem(event.entityPlayer);
        if (offhandItem != null) {
            try {
                if (offhandItem.getItem() instanceof ItemArrowTipped) {
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
        ItemStack offhandItem = Backhand.INSTANCE.getOffhandItem(player);
        ItemStack mainhandItem = player.getCurrentEquippedItem();
        boolean mainhandUse = Backhand.checkForRightClickFunction(mainhandItem);
        if (offhandItem == null || mainhandUse) {
            return;
        }
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && !tickStartItems.containsKey(player.getUniqueID())) {
            Backhand.INSTANCE.swapOffhandItem(player);
            regularHotSwap = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemStop(PlayerUseItemEvent.Stop event) {
        EntityPlayer player = event.entityPlayer;
        ItemStack mainhandItem = player.getCurrentEquippedItem();
        boolean mainhandUse = Backhand.checkForRightClickFunction(mainhandItem);
        if (Backhand.INSTANCE.getOffhandItem(player) == null || mainhandUse) {
            return;
        }

        if (!tickStartItems.containsKey(player.getUniqueID()) && !regularHotSwap) {
            Backhand.INSTANCE.swapOffhandItem(player);
            regularHotSwap = true;
        }

        if (!ConfigFunctions.offhand.useOffhandArrows || !(event.item.getItem() instanceof ItemBow)) {
            return;
        }

        boolean overrideWithOffhand = false;
        ItemStack offhandItem = Backhand.INSTANCE.getOffhandItem(event.entityPlayer);
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
                    Backhand.INSTANCE.swapOffhandItem(event.entityPlayer);
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

        if (ConfigFunctions.offhand.offhandTickHotswap) {
            List<EntityPlayer> players = (List<EntityPlayer>)event.world.playerEntities;
            for (EntityPlayer player : players) {
                ItemStack mainhand = player.getCurrentEquippedItem() == null ? null : player.getCurrentEquippedItem().copy();
                ItemStack offhand = Backhand.INSTANCE.getOffhandItem(player) == null ? null : Backhand.INSTANCE.getOffhandItem(player).copy();
                if (offhand == null) {
                    continue;
                }

                if (event.phase == TickEvent.Phase.START && !player.isUsingItem()) {
                    if (!Backhand.checkForRightClickFunction(mainhand)) {
                        if (!tickStartItems.containsKey(player.getUniqueID())) {
                            BackhandHandler.INSTANCE.sendPacketToPlayer(
                                    new BackhandWorldHotswapPacket(true).generatePacket(), (EntityPlayerMP) player
                            );
                        }
                        tickStartItems.put(player.getUniqueID(), Arrays.asList(mainhand, offhand) );
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
            Backhand.INSTANCE.setPlayerOffhandItem(player, tickStartItems.get(player.getUniqueID()).get(1));
            tickStartItems.remove(player.getUniqueID());
            BackhandHandler.INSTANCE.sendPacketToPlayer(
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
            if (BackhandEventHandler.regularHotSwap) {
               Backhand.INSTANCE.swapOffhandItem(player);
               BackhandEventHandler.regularHotSwap = false;
            }
            return;
        }

        ItemStack offhand = Backhand.INSTANCE.getOffhandItem(player);

        if (event.phase == TickEvent.Phase.END) {
            if (blacklistDelay > 0) {
                blacklistDelay--;
            }
            if (Backhand.INSTANCE.isOffhandBlacklisted(offhand)) {
                if (!ItemStack.areItemStacksEqual(offhand,prevStackInSlot)) {
                    blacklistDelay = 10;
                } else if (blacklistDelay == 0) {
                   Backhand.INSTANCE.setPlayerOffhandItem(player,null);

                    boolean foundSlot = false;
                    for (int i = 0; i < player.inventory.getSizeInventory() - 4; i++) {
                        if (i == ConfigFunctions.offhand.alternateOffhandSlot)
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

        if (Backhand.getOffhandEP(player).syncOffhand) {
            if (!tickStartItems.containsKey(player.getUniqueID())) {
                BackhandHandler.INSTANCE.sendPacketToAll(new BackhandSyncItemPacket(player).generatePacket());
            }
            Backhand.getOffhandEP(player).syncOffhand = false;
        }

        if (arrowHotSwapped) {
            if (offhand != null && offhand.getItem() != Items.arrow) {
               Backhand.INSTANCE.swapOffhandItem(player);
            }
            arrowHotSwapped = false;
        }
        if (regularHotSwap) {
           Backhand.INSTANCE.swapOffhandItem(player);
            regularHotSwap = false;
        }

        if (fireworkHotSwapped > 0) {
            fireworkHotSwapped--;
        } else if (fireworkHotSwapped == 0) {
            Backhand.INSTANCE.swapOffhandItem(player);
            fireworkHotSwapped--;
            MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_AIR,
                    (int)player.posX, (int)player.posY, (int)player.posZ, -1, player.worldObj));
                    Backhand.INSTANCE.swapOffhandItem(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (!(event.entity instanceof EntityPlayer && !(event.entity instanceof FakePlayer)))
            return;
        event.entity.registerExtendedProperties("OffhandStorage", new BackhandExtendedProperty((EntityPlayer) event.entity));
    }
}
