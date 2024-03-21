package ganymedes01.etfuturum.backhand;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.backhand.client.renderer.RenderOffhandPlayer;
import ganymedes01.etfuturum.backhand.packets.BackhandSwapPacket;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import invtweaks.InvTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

public class BackhandClientEventHandler {

    public static final BackhandClientEventHandler INSTANCE = new BackhandClientEventHandler();

    public static final KeyBinding swapOffhand = new KeyBinding(StatCollector.translateToLocal("backhand.swapItem"), Keyboard.KEY_F, "key.categories.gameplay");

    public static int delay;
    public static boolean prevInvTweaksAutoRefill;
    public static boolean prevInvTweaksBreakRefill;

    public static int invTweaksDelay;
    public static boolean allowSwap = true;

    public static RenderOffhandPlayer renderOffhandPlayer = new RenderOffhandPlayer();
    public static EntityPlayer renderingPlayer;
    public static boolean cancelone = false;

    public static int rightClickCounter = 0;

    public static float ticksBeforeUse = 0;
    public static boolean prevRightClickHeld = false;
    public static int attackDelay = 0;
    public static ItemStack offhandItemUsed;

    public boolean isRightClickHeld() {
        return Minecraft.getMinecraft().gameSettings.keyBindUseItem.getIsKeyPressed();
    }

    public int getRightClickCounter() {
        return rightClickCounter;
    }

    public int getRightClickDelay() {
        return delay;
    }

    public void setRightClickCounter(int i) {
        rightClickCounter = i;
    }

    public boolean isLeftClickHeld() {
        return Minecraft.getMinecraft().gameSettings.keyBindAttack.getIsKeyPressed();
    }

    // public int getLeftClickCounter() {
    //     return Minecraft.getMinecraft().leftClickCounter;
    // }

    // public void setLeftClickCounter(int i) {
    //     Minecraft.getMinecraft().leftClickCounter = i;
    // }

    /*
     * ---------------------------
     * K E Y B O A R D   I N P U T
     * ---------------------------
     */

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;

        if (swapOffhand.getIsKeyPressed() && Keyboard.isKeyDown(Keyboard.getEventKey()) && allowSwap) {
            allowSwap = false;
            try {
                this.getClass().getMethod("invTweaks$swapPatch");
                invTweaks$swapPatch();
            } catch (Exception ignored) {}
            ((EntityClientPlayerMP)player).sendQueue.addToSendQueue(
                    new BackhandSwapPacket(player).generatePacket()
            );
        }
    }

    /*
     * -------------------------------
     * I N V E N T O R Y   T W E A K S
     * -------------------------------
     */

    @SideOnly(Side.CLIENT)
    @Method(modid="inventorytweaks")
    public void invTweaks$swapPatch() {
        if (invTweaksDelay <= 0) {
            prevInvTweaksAutoRefill = Boolean.parseBoolean(InvTweaks.getConfigManager().getConfig().getProperty("enableAutoRefill"));
            prevInvTweaksBreakRefill = Boolean.parseBoolean(InvTweaks.getConfigManager().getConfig().getProperty("autoRefillBeforeBreak"));
            InvTweaks.getConfigManager().getConfig().setProperty("enableAutoRefill", "false");
            InvTweaks.getConfigManager().getConfig().setProperty("autoRefillBeforeBreak", "false");
        }
        invTweaksDelay = 15;
    }

    @SideOnly(Side.CLIENT)
    @Method(modid="inventorytweaks")
    @SubscribeEvent
    public void invTweaks$onClientTick(TickEvent.ClientTickEvent event) {
        if (invTweaksDelay > 0) {
            invTweaksDelay--;
            if (invTweaksDelay == 0) {
                try {
                    this.getClass().getMethod("restoreInvTweaksConfigs");
                    invTweaks$restoreInvTweaksConfigs();
                } catch (Exception ignored) {}
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Method(modid="inventorytweaks")
    public void invTweaks$restoreInvTweaksConfigs() {
        InvTweaks.getConfigManager().getConfig().setProperty("enableAutoRefill",String.valueOf(prevInvTweaksAutoRefill));
        InvTweaks.getConfigManager().getConfig().setProperty("autoRefillBeforeBreak",String.valueOf(prevInvTweaksBreakRefill));
    }

    /* 
     * -------------------------
     * R E N D E R   H O T B A R
     * -------------------------
     */
 
    /*
     * Extends the players hotbar by rendering an overlay.
     */
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
            Minecraft mc = Minecraft.getMinecraft();
            renderHotbar(mc.ingameGUI, event.resolution.getScaledWidth(), event.resolution.getScaledHeight(), event.partialTicks);
        }
    }

    /*
     * Renders the offhand hotbar.
     */
    protected void renderHotbar(GuiIngame gui, int width, int height, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack itemstack = Backhand.INSTANCE.getOffhandItem(mc.thePlayer);
        if (itemstack == null) {
            return;
        }

        mc.mcProfiler.startSection("actionBar");

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("textures/gui/widgets.png"));

        gui.drawTexturedModalRect(width / 2 - 125, height - 22, 0, 0, 11, 22);
        gui.drawTexturedModalRect(width / 2 - 125 + 11, height - 22, 182 - 11, 0, 11, 22);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        int x = width / 2 - 122;
        int z = height - 16 - 3;
        renderOffhandInventorySlot(x, z, partialTicks);

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        mc.mcProfiler.endSection();
    }

    /*
     * Renders the offhand inventory slot on the left side of the hotbar.
     */
    protected void renderOffhandInventorySlot(int p_73832_2_, int p_73832_3_, float p_73832_4_) {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack itemstack = Backhand.INSTANCE.getOffhandItem(mc.thePlayer);

        if (itemstack != null)
        {
            float f1 = itemstack.animationsToGo - p_73832_4_;

            if (f1 > 0.0F)
            {
                GL11.glPushMatrix();
                float f2 = 1.0F + f1 / 5.0F;
                GL11.glTranslatef(p_73832_2_ + 8, p_73832_3_ + 12, 0.0F);
                GL11.glScalef(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
                GL11.glTranslatef((-(p_73832_2_ + 8)), (-(p_73832_3_ + 12)), 0.0F);
            }

            RenderItem.getInstance().renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, p_73832_2_, p_73832_3_);

            if (f1 > 0.0F)
            {
                GL11.glPopMatrix();
            }

            RenderItem.getInstance().renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, p_73832_2_, p_73832_3_);
        }
    }

    public static int renderPass;
    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        renderPass = event.renderPass;
    }

    /* 
     * -------------------------
     * R E N D E R   P L A Y E R
     * -------------------------
     */

    /**
     * Bend the models when the item in left hand is used
     * And stop the right hand inappropriate bending
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderLivingPre(RenderLivingEvent.Pre event){
        if(event.entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) event.entity;
            renderingPlayer = entityPlayer;
            ItemStack offhand = Backhand.INSTANCE.getOffhandItem(entityPlayer);
            if (offhand != null && event.renderer instanceof RenderPlayer) {
                RenderPlayer renderer = ((RenderPlayer) event.renderer);
                renderer.modelArmorChestplate.heldItemLeft = renderer.modelArmor.heldItemLeft = renderer.modelBipedMain.heldItemLeft = 1;
                if (entityPlayer.getItemInUseCount() > 0 && entityPlayer.getItemInUse() == offhand) {
                    EnumAction enumaction = offhand.getItemUseAction();
                    if (enumaction == EnumAction.block) {
                        renderer.modelArmorChestplate.heldItemLeft = renderer.modelArmor.heldItemLeft = renderer.modelBipedMain.heldItemLeft = 3;
                    } else if (enumaction == EnumAction.bow) {
                        renderer.modelArmorChestplate.aimedBow = renderer.modelArmor.aimedBow = renderer.modelBipedMain.aimedBow = true;
                    }
                    ItemStack mainhand = entityPlayer.inventory.getCurrentItem();
                    renderer.modelArmorChestplate.heldItemRight = renderer.modelArmor.heldItemRight = renderer.modelBipedMain.heldItemRight = mainhand != null ? 1 : 0;
                }
            }
        }
    }

    /**
     * Reset models to default values
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderPlayerPost(RenderPlayerEvent.Post event){
        event.renderer.modelArmorChestplate.heldItemLeft = event.renderer.modelArmor.heldItemLeft = event.renderer.modelBipedMain.heldItemLeft = 0;
    }

    /*
     * Renders 3rd person offhand
     */
    @SubscribeEvent
    public void onRenderPlayerSpecialsPost(RenderPlayerEvent.Specials.Post event) {
        if (!ConfigFunctions.offhand.allowEmptyOffhand && Backhand.INSTANCE.getOffhandItem(event.entityPlayer) == null) {
            return;
        }

        GL11.glPushMatrix();
        ModelBiped biped = (ModelBiped) event.renderer.modelBipedMain;
        RenderOffhandPlayer.itemRenderer.updateEquippedItem();
        renderOffhandPlayer.updateFovModifierHand();
        RenderOffhandPlayer.itemRenderer.renderOffhandItemIn3rdPerson(event.entityPlayer, biped, event.partialRenderTick);
        GL11.glPopMatrix();
    }

    /* 
     * ----------------------
     * T I C K   P L A Y E R
     * ----------------------
     */

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerTick(TickEvent.PlayerTickEvent event){
        Minecraft mc = Minecraft.getMinecraft();
        if(event.player == mc.thePlayer) {
            if (event.phase == TickEvent.Phase.START) {
                if (ticksBeforeUse > 0)
                    ticksBeforeUse--;
                tickStart(mc.thePlayer);
            }
        }
        
        if (delay > 0) {
            delay--;
        }

        // if (!ConfigFunctions.offhand.offhandBreakBlocks) {
        //     return;
        // }

        // if (!ConfigFunctions.offhand.allowEmptyOffhand && Backhand.INSTANCE.getOffhandItem(event.player) == null) {
        //     return;
        // }

        // if (!isRightClickHeld()) {
        //     setRightClickCounter(0);
        // }

        // ItemStack mainHandItem = event.player.getCurrentEquippedItem();
        // ItemStack offhandItem = Backhand.INSTANCE.getOffhandItem(event.player);

        // if (mainHandItem != null && (Backhand.checkForRightClickFunction(mainHandItem) || offhandItem == null)) {
        //     return;
        // }

        // if (event.player.worldObj.isRemote && getLeftClickCounter() <= 0 && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
        //     if (event.player.capabilities.allowEdit) {
        //         if (isRightClickHeld() && !(mainHandItem != null && Backhand.isItemBlock(mainHandItem.getItem()))) { // if it's a block and we should try break it
        //             MovingObjectPosition mop = BattlemodeHookContainerClass.getRaytraceBlock(event.player);
        //             if (offhandItem != null && BattlemodeHookContainerClass.isItemBlock(offhandItem.getItem())) {
        //                 if (!Backhand.usagePriorAttack(offhandItem) && mop != null) {
        //                     BattlegearClientTickHandler.tryBreakBlockOffhand(mop, offhandItem, mainHandItem, event);
        //                     setLeftClickCounter(10);
        //                 } else {
        //                     mc.playerController.resetBlockRemoving();
        //                 }
        //             } else {
        //                 if (mop != null && !Backhand.usagePriorAttack(offhandItem) && !Backhand.canBlockBeInteractedWith(mc.theWorld, mop.blockX, mop.blockY, mop.blockZ)) {
        //                     BattlegearClientTickHandler.tryBreakBlockOffhand(mop, offhandItem, mainHandItem, event);
        //                     setLeftClickCounter(10);
        //                 } else {
        //                     mc.playerController.resetBlockRemoving();
        //                 }
        //             }
        //         } else if (!isLeftClickHeld()) {
        //             mc.playerController.resetBlockRemoving();
        //         }
        //     }
        // }
    }

    @SideOnly(Side.CLIENT)
    public void tickStart(EntityPlayer player) {
        ItemStack mainhand = player.getCurrentEquippedItem();
        ItemStack offhand = Backhand.INSTANCE.getOffhandItem(player);
        boolean mainhandUse = Backhand.checkForRightClickFunction(mainhand);
        boolean offhandUse = Backhand.checkForRightClickFunction(offhand);
        Minecraft mc = Minecraft.getMinecraft();

        if (attackDelay > 0) {
            attackDelay--;
        }

        boolean usedItem = false;
        if (offhand != null) {
            if (mc.gameSettings.keyBindUseItem.getIsKeyPressed()) {
                if (ticksBeforeUse == 0) {
                    usedItem = tryCheckUseItem(offhand, player);
                }
            } else {
                ticksBeforeUse = 0;
            }
        }
        if (mc.gameSettings.keyBindUseItem.getIsKeyPressed() && attackDelay == 0 && !usedItem) {
            if (!prevRightClickHeld && player.getItemInUse() == null && !mainhandUse && !offhandUse) {
                // tryAttackEntity(player);
            }
            prevRightClickHeld = true;
        } else {
            prevRightClickHeld = false;
        }
        if (player.getItemInUse() == null) {
            offhandItemUsed = null;
        }
    }

    // public void tryAttackEntity(EntityPlayer player) {
    //     Minecraft mc = Minecraft.getMinecraft();
    //     if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
    //         Entity target = mc.objectMouseOver.entityHit;
    //         ((EntityClientPlayerMP) player).sendQueue.addToSendQueue(
    //                 new OffhandAttackPacket(player,target).generatePacket()
    //         );
    //     }
    // }

    @SideOnly(Side.CLIENT)
    public boolean tryCheckUseItem(ItemStack offhandItem, EntityPlayer player){
        Minecraft mc = Minecraft.getMinecraft();
        MovingObjectPosition mouseOver = mc.objectMouseOver;

        if (offhandItem.getItem() instanceof ItemBow) {
            return false;
        }

        ItemStack mainHandItem = player.getCurrentEquippedItem();
        if (mainHandItem != null && (Backhand.checkForRightClickFunction(mainHandItem) || Backhand.isItemBlock(mainHandItem.getItem()) || player.getItemInUse() == mainHandItem)) {
            ticksBeforeUse = 10;
            return false;
        }

        if (mouseOver != null && mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            if (BattlegearUtils.blockHasUse(player.worldObj.getBlock(mouseOver.blockX, mouseOver.blockY,mouseOver.blockZ))
                && !Backhand.INSTANCE.getOffhandItem(player).getItem().doesSneakBypassUse(player.worldObj, mouseOver.blockX, mouseOver.blockY,mouseOver.blockZ, player)
                && !(offhandItem.getItem() instanceof ItemBlock)) {
                ticksBeforeUse = 4;
                return false;
            }
        }

        boolean interacted = false;
        if (BattlegearUtils.usagePriorAttack(offhandItem)) {
            boolean flag = true;
            if (mouseOver != null)
            {
                if (mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
                {
                    if(mc.playerController.interactWithEntitySendPacket(player, mouseOver.entityHit)) {
                        flag = false;
                        interacted = true;
                    }
                }

                if (flag)
                {
                    offhandItem = Backhand.INSTANCE.getOffhandItem(player);
                    PlayerEventChild.UseOffhandItemEvent useItemEvent = new PlayerEventChild.UseOffhandItemEvent(new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_AIR, 0, 0, 0, -1, player.worldObj), offhandItem);
                    if (offhandItem != null && !MinecraftForge.EVENT_BUS.post(useItemEvent)) {
                        interacted = BattlemodeHookContainerClass.tryUseItem(player, offhandItem, Side.CLIENT);
                    }
                }

                offhandItem = Backhand.INSTANCE.getOffhandItem(player);
                if (offhandItem != null && mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    int j = mouseOver.blockX;
                    int k = mouseOver.blockY;
                    int l = mouseOver.blockZ;
                    if (!player.worldObj.getBlock(j, k, l).isAir(player.worldObj, j, k, l)) {
                        final int size = offhandItem.stackSize;
                        int i1 = mouseOver.sideHit;
                        PlayerEventChild.UseOffhandItemEvent useItemEvent = new PlayerEventChild.UseOffhandItemEvent(new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, j, k, l, i1, player.worldObj), offhandItem);
                        if (player.capabilities.allowEdit || !Backhand.isItemBlock(offhandItem.getItem())) {
                            if (!MinecraftForge.EVENT_BUS.post(useItemEvent) && onPlayerPlaceBlock(mc.playerController, player, offhandItem, j, k, l, i1, mouseOver.hitVec)) {
                                ((IBattlePlayer) player).swingOffItem();
                                interacted = true;
                            }
                        }
                        if (offhandItem.stackSize == 0) {
                            Backhand.INSTANCE.setPlayerOffhandItem(player, null);
                        }
                    }
                }
            }
            ticksBeforeUse = 4;
        }

        return interacted;
    }
}
