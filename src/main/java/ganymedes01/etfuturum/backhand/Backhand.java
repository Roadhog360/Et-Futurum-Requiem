package ganymedes01.etfuturum.backhand;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.BattlemodeHookContainerClass;
import mods.battlegear2.packet.BattlegearPacketHandler;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import scala.actors.threadpool.Arrays;
import scala.collection.immutable.List;

public class Backhand {
    public static Backhand INSTANCE = new Backhand();

    public static FMLEventChannel Channel;
    public static FMLEventChannel ChannelPlayer;

    @SidedProxy(clientSide = "ganymedes01.etfuturum.mixins.backhand.client.ClientProxy",
                serverSide = "ganymedes01.etfuturum.mixins.backhand.CommonProxy")
    public static BattlegearPacketHandler packetHandler;

    public static boolean OffhandAttack = false;
    public static boolean EmptyOffhand = false;
    public static boolean OffhandBreakBlocks = false;
    public static boolean UseOffhandArrows = true;
    public static boolean UseOffhandBow = true;
    public static boolean OffhandTickHotswap = true;
    public static int AlternateOffhandSlot = 9;
    public static boolean UseInventorySlot = false;
    public static String[] offhandBlacklist;

    public static boolean RenderEmptyOffhandAtRest = false;
    
    // TODO: Migrate! Currently not called.
    @Mod.EventHandler
    public void load(FMLPreInitializationEvent event) {
        Channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("Backhand");
        ChannelPlayer = NetworkRegistry.INSTANCE.newEventDrivenChannel("BackhandPlayer");

        BattlegearConfig.getConfig(new Configuration(event.getSuggestedConfigurationFile()));
        MinecraftForge.EVENT_BUS.register(BattlemodeHookContainerClass.INSTANCE);
        FMLCommonHandler.instance().bus().register(BattlemodeHookContainerClass.INSTANCE);
    }

    // TODO: Migrate! Currently not called.
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        packetHandler = new BattlegearPacketHandler();
        packetHandler.register();
    }

    public static MinecraftServer getServer(){
        return MinecraftServer.getServer();
    }

    public static boolean isOffhandBlacklisted(ItemStack stack) {
        if (stack == null)
            return false;

        for (String itemName : offhandBlacklist) {
            if (stack.getItem().delegate.name().equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    /*
     * S e r v e r   E v e n t   H a n d l e r
     */

    public static boolean arrowHotSwapped = false;
    public static boolean regularHotSwap = false;
    public static int fireworkHotSwapped = -1;

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

    /*
     * S e r v e r   T i  c k   H a n d l e r
     */

    public ItemStack prevStackInSlot;
    public int blacklistDelay = -1;

    public static HashMap<UUID,List<ItemStack>> tickStartItems = new HashMap<>();

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
                                    new OffhandWorldHotswapPacket(true).generatePacket(), (EntityPlayerMP) player
                            );
                        }
                        tickStartItems.put(player.getUniqueID(), Arrays.asList(mainhand, offhand));
                        player.setCurrentItemOrArmor(0, tickStartItems.get(player.getUniqueID()).get(1));
                    }
                } else {
                    ServerTickHandler.resetTickingHotswap(player);
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
                    new OffhandWorldHotswapPacket(false).generatePacket(), (EntityPlayerMP) player
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

        if (ServerEventsHandler.arrowHotSwapped) {
            if (offhand != null && offhand.getItem() != Items.arrow) {
                BattlegearUtils.swapOffhandItem(player);
            }
            ServerEventsHandler.arrowHotSwapped = false;
        }
        if (ServerEventsHandler.regularHotSwap) {
            BattlegearUtils.swapOffhandItem(player);
            ServerEventsHandler.regularHotSwap = false;
        }

        if (ServerEventsHandler.fireworkHotSwapped > 0) {
            ServerEventsHandler.fireworkHotSwapped--;
        } else if (ServerEventsHandler.fireworkHotSwapped == 0) {
            BattlegearUtils.swapOffhandItem(player);
            ServerEventsHandler.fireworkHotSwapped--;
            MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_AIR,
                    (int)player.posX, (int)player.posY, (int)player.posZ, -1, player.worldObj));
            BattlegearUtils.swapOffhandItem(player);
        }
    }
}