package ganymedes01.etfuturum.backhand;

import ganymedes01.etfuturum.api.backhand.BackhandExtendedProperty;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class Backhand {
    public static Backhand INSTANCE = new Backhand();
   
    public boolean isOffhandBlacklisted(ItemStack stack) {
        if (stack == null)
            return false;

        for (String itemName : ConfigFunctions.offhand.offhandBlacklist) {
            if (stack.getItem().delegate.name().equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    public void setPlayerOffhandItem(EntityPlayer player, ItemStack stack) {
        if (!isOffhandBlacklisted(stack)) {
            if (ConfigFunctions.offhand.useInventorySlot) {
                player.inventory.setInventorySlotContents(ConfigFunctions.offhand.alternateOffhandSlot, stack);
            } else {
                getOffhandEP(player).setOffhandItem(stack);
            }
        }
    }

    public ItemStack getOffhandItem(EntityPlayer player) {
        if (ConfigFunctions.offhand.useInventorySlot) {
            return player.inventory.getStackInSlot(ConfigFunctions.offhand.alternateOffhandSlot);
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
        return player.inventory instanceof InventoryPlayer;
    }

    public static void setPlayerCurrentItem(EntityPlayer player, ItemStack stack) {
        player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
    }
}