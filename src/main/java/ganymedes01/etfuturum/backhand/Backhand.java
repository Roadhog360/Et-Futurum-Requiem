package ganymedes01.etfuturum.backhand;

import ganymedes01.etfuturum.api.backhand.BackhandExtendedProperty;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

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
    
    private static String[] itemBlackListMethodNames = {
        "func_77648_a", // onItemUse
        "func_77659_a", // onItemRightClick
        "func_111207_a" // itemInteractionForEntity
    };

    /**
     * Method arguments classes that are not allowed in {@link Item} subclasses for common wielding
     */
    private static Class[][] itemBlackListMethodParams = {
        new Class[] { ItemStack.class, EntityPlayer.class, World.class, int.class, int.class, int.class, int.class, float.class, float.class, float.class },
        new Class[] { ItemStack.class, World.class, EntityPlayer.class },
        new Class[] { ItemStack.class, EntityPlayer.class, EntityLivingBase.class }
    };

    @SuppressWarnings("unchecked")
    public static boolean checkForRightClickFunction(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        try {
            if (stack.getItemUseAction() == EnumAction.block || stack.getItemUseAction() == EnumAction.none) {

                Class c = stack.getItem().getClass();
                while (!(c.equals(Item.class) || c.equals(ItemTool.class) || c.equals(ItemSword.class))) {
                    try {
                        for (int i = 0; i < itemBlackListMethodNames.length; i++) {
                            try {
                                c.getDeclaredMethod(itemBlackListMethodNames[i], itemBlackListMethodParams[i]);
                                return true;
                            } catch (NoSuchMethodException ignored) {}
                        }
                    } catch (NoClassDefFoundError ignored) {}

                    c = c.getSuperclass();
                }

                return false;
            } else {
                return true;
            }
        } catch (NullPointerException e) {
            return true;
        }
    }

    // public static boolean isItemBlock(Item item) {
    //     return item instanceof ItemBlock || item instanceof ItemDoor || item instanceof ItemSign || item instanceof ItemReed || item instanceof ItemSeedFood || item instanceof ItemRedstone || item instanceof ItemBucket || item instanceof ItemSkull;
    // }

    // /**
    //  * Defines a item which "use" (effect on right click) should have priority over its "attack" (effect on left click)
    //  * @param itemStack the item which will be "used", instead of attacking
    //  * @return true if such item prefer being "used"
    //  */
    // public static boolean usagePriorAttack(ItemStack itemStack){
    //     if (itemStack == null) {
    //         return false;
    //     }
    //     if(itemStack.getItemUseAction() == EnumAction.drink || itemStack.getItemUseAction() == EnumAction.eat || itemStack.getItemUseAction() == EnumAction.bow) {
    //         return true;
    //     }
    //     return !(itemStack.getItem() instanceof ItemSword) && (checkForRightClickFunction(itemStack) || isCommonlyUsable(itemStack.getItem()));
    // }

    // /**
    //  * Defines items that are usually usable (the vanilla instances do, at least)
    //  * @param item the instance to consider for usability
    //  * @return true if it is commonly usable
    //  */
    // public static boolean isCommonlyUsable(Item item){
    //     return isBow(item) || item.getClass().toString().equalsIgnoreCase("class D.f") || item instanceof ItemBed || item instanceof ItemHangingEntity || item instanceof ItemBook || isItemBlock(item) || item instanceof ItemHoe || item instanceof ItemSnowball || item instanceof ItemEnderPearl || item instanceof ItemEgg || item instanceof ItemMonsterPlacer;
    // }

    // /**
    //  * Defines a bow
    //  * @param item the instance
    //  * @return true if it is considered a generic enough bow
    //  */
    // public static boolean isBow(Item item){
    //     return item instanceof ItemBow;
    // }
}