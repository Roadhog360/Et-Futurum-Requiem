package ganymedes01.etfuturum.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import org.jetbrains.annotations.ApiStatus;

public class BannerPatternHelper {

	@SuppressWarnings("unchecked")
	@ApiStatus.AvailableSince("1.4.2")
	public static void addPattern(String name, String id, ItemStack craftingItem) throws ClassNotFoundException {
		EnumHelper.addEnum((Class<Enum<?>>) Class.forName("ganymedes01.etfuturum.tileentities.TileEntityBanner$EnumBannerPattern"), name.toUpperCase(), new Class[]{String.class, String.class, ItemStack.class}, new Object[]{name, id, craftingItem});
	}

	@SuppressWarnings("unchecked")
	@ApiStatus.AvailableSince("1.4.2")
	public static void addPattern(String name, String id, String craftingTop, String craftingMid, String craftingBot) throws ClassNotFoundException {
		EnumHelper.addEnum((Class<Enum<?>>) Class.forName("ganymedes01.etfuturum.tileentities.TileEntityBanner$EnumBannerPattern"), name.toUpperCase(), new Class[]{String.class, String.class, String.class, String.class, String.class}, new Object[]{name, id, craftingTop, craftingMid, craftingBot});
	}
}