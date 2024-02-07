package ganymedes01.etfuturum.backhand;

import ganymedes01.etfuturum.api.backhand.BackhandExtendedProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class Backhand {
    public static Backhand INSTANCE = new Backhand();

    public boolean OffhandAttack = false;
    public boolean EmptyOffhand = false;
    public boolean OffhandBreakBlocks = false;
    public boolean UseOffhandArrows = true;
    public boolean UseOffhandBow = true;
    public boolean OffhandTickHotswap = true;
    public int AlternateOffhandSlot = 9;
    public boolean UseInventorySlot = false;
    public String[] offhandBlacklist;
    public boolean RenderEmptyOffhandAtRest = false;
   
    public boolean isOffhandBlacklisted(ItemStack stack) {
        if (stack == null)
            return false;

        for (String itemName : offhandBlacklist) {
            if (stack.getItem().delegate.name().equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    public void setPlayerOffhandItem(EntityPlayer player, ItemStack stack) {
        if (!isOffhandBlacklisted(stack)) {
            if (UseInventorySlot) {
                player.inventory.setInventorySlotContents(AlternateOffhandSlot, stack);
            } else {
                getOffhandEP(player).setOffhandItem(stack);
            }
        }
    }

    public ItemStack getOffhandItem(EntityPlayer player) {
        if (UseInventorySlot) {
            return player.inventory.getStackInSlot(AlternateOffhandSlot);
        } else {
            return getOffhandEP(player).getOffhandItem();
        }
    }

    public void swapOffhandItem(EntityPlayer player) {
        final ItemStack mainhandItem = player.getCurrentEquippedItem();
        final ItemStack offhandItem = getOffhandItem(player);
        setPlayerCurrentItem(player, offhandItem);
        setPlayerOffhandItem(player, mainhandItem);
    }

    public static BackhandExtendedProperty getOffhandEP(EntityPlayer player) {
        return ((BackhandExtendedProperty)player.getExtendedProperties("OffhandStorage"));
    }

    public static boolean hasOffhandInventory(EntityPlayer player) {
        return player.inventory instanceof InventoryPlayerBattle;
    }

    public static void setPlayerCurrentItem(EntityPlayer player, ItemStack stack) {
        player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
    }
}