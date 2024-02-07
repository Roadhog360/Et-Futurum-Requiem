package ganymedes01.etfuturum.backhand;

import net.minecraft.item.ItemStack;

public class Backhand {
    public static Backhand INSTANCE = new Backhand();

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
}