package ganymedes01.etfuturum.compat;

import cpw.mods.ironchest.ChestChangerType;
import cpw.mods.ironchest.IronChest;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.ItemChestChanger;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.items.ItemBarrelUpgrade;
import ganymedes01.etfuturum.items.ItemShulkerBoxUpgrade;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import me.mrnavastar.r.R;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.utils.RecipeHelper;

import java.util.Map;

public class CompatIronChests {
	private static final Map<String, ItemChestChanger> upgradeItems = new Object2ObjectLinkedOpenHashMap<>();
	private static final Map<String, ChestChangerType> upgradeTypes = new Object2ObjectLinkedOpenHashMap<>();
	private static final Map<ChestChangerType, Pair<IronChestType, IronChestType>> upgradeMappings = new Reference2ObjectOpenHashMap<>();
	private static final Map<String, IronChestType> tiers = new Object2ObjectLinkedOpenHashMap<>();
	private static double renderDistance;
	static {
		// Collects all enabled chest upgrade typee
		R icInvoker = R.of(IronChest.class);
		renderDistance = getOrDefaultR(icInvoker, "TRANSPARENT_RENDER_INSIDE", Boolean.class, true)
				? getOrDefaultR(icInvoker, "TRANSPARENT_RENDER_DISTANCE", Double.class, 128D) : 0F;
		for(IronChestType type : IronChestType.values()) {
			R typeInvoker = R.of(type);
			if(callWithDefaultR(typeInvoker, "isEnabled", Boolean.class, true)) {
				tiers.put(type.name(), type);
			}
		}
		for(ChestChangerType type : ChestChangerType.values()) {
			R ccInvoker = R.of(type);
			IronChestType source = ccInvoker.get("source", IronChestType.class);
			IronChestType target = ccInvoker.get("target", IronChestType.class);
			boolean isEnabled = getOrDefaultR(ccInvoker, "isAllowed", Boolean.class, true) && tierExists(source.name()) && tierExists(target.name());
			if(isEnabled) {
				upgradeTypes.put(type.name(), type);
				upgradeMappings.put(type, Pair.of(source, target));
			}
		}
	}

	public static boolean upgradeExists(String from, String to) {
		return upgradeTypes.containsKey(from+to);
	}

	public static boolean tierExists(String type) {
		return tiers.containsKey(type);
	}

	public static void init() {
		for(ChestChangerType type : upgradeTypes.values()) {
			R ccInvoker = R.of(type);
			Item item = ccInvoker.get("item", ItemChestChanger.class);
			boolean isEnabled = item != null && item.delegate.name() != null;
			if(isEnabled) {
				upgradeItems.put(type.name(), ccInvoker.get("item", ItemChestChanger.class));
			}
		}
	}

	public static void registerRecipes() {
		if(ModItems.BARREL_UPGRADE.isEnabled()) {
			ItemBarrelUpgrade upgrade = ((ItemBarrelUpgrade) ModItems.BARREL_UPGRADE.get());
			for (int i = 0; i < upgrade.types.length; i++) {
				Item icUpgrade = upgradeItems.get(upgrade.getSource(i) + upgrade.getTarget(i));
				RecipeHelper.addShapedRecipe(ModItems.BARREL_UPGRADE.newItemStack(1, i), "X", 'X', new ItemStack(icUpgrade));
				RecipeHelper.addShapedRecipe(new ItemStack(icUpgrade), "X", 'X', ModItems.BARREL_UPGRADE.newItemStack(1, i));
			}
		}
		if(ModItems.SHULKER_BOX_UPGRADE.isEnabled()) {
			ItemShulkerBoxUpgrade upgrade = ((ItemShulkerBoxUpgrade) ModItems.SHULKER_BOX_UPGRADE.get());
			for (int i = 0; i < upgrade.types.length; i++) {
				Item icUpgrade = upgradeItems.get(upgrade.getSource(i) + upgrade.getTarget(i));
					RecipeHelper.addShapelessRecipe(RecipeHelper.Priority.NORMAL, ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, i), ModItems.SHULKER_SHELL.newItemStack(), new ItemStack(icUpgrade));
					RecipeHelper.addShapedRecipe(RecipeHelper.Priority.NORMAL, new ItemStack(icUpgrade), "X", 'X', ModItems.SHULKER_BOX_UPGRADE.newItemStack(1, i));
			}
		}
	}

	@Nullable
	public static String getUpgradeName(String from, Item item) {
		if(item instanceof ItemChestChanger changer && changer.getType().canUpgrade(tiers.get(from))) {
			Pair<IronChestType, IronChestType> types = upgradeMappings.get(changer.getType());
			if(types.first().name().equals(from.toUpperCase())) {
				return types.second().name();
			}
		}
		return null;
	}

	public static String getNextBarrelUpgrade(String current, ItemStack stack) {
		if(ModItems.BARREL_UPGRADE.isEnabled()) {
			if(stack.getItem() instanceof ItemBarrelUpgrade upgrade && upgrade.getSource(stack.getItemDamage()).equals(current)
					&& upgradeTypes.containsKey(current+upgrade.getTarget(stack.getItemDamage()))) {
				return upgrade.getTarget(stack.getItemDamage());
			}
			return null;
		}
		return CompatIronChests.getUpgradeName(current, stack.getItem());
	}

	public static String getNextShulkerUpgrade(String current, ItemStack stack) {
		if(ModItems.SHULKER_BOX_UPGRADE.isEnabled()) {
			if(stack.getItem() instanceof ItemShulkerBoxUpgrade upgrade && upgrade.getSource(stack.getItemDamage()).equals(current)
					&& upgradeTypes.containsKey(current+upgrade.getTarget(stack.getItemDamage()))) {
				return upgrade.getTarget(stack.getItemDamage());
			}
			return null;
		}
		return CompatIronChests.getUpgradeName(current.replace("VANILLA", "WOOD"), stack.getItem());
	}

	public static boolean enableCrystalRendering() {
		return crystalRenderDistance() > 0;
	}

	public static double crystalRenderDistance() {
		return renderDistance;
	}

	private static <T> T callWithDefaultR(R r, String name, Class<T> cast, T def) {
		try {
			return r.call(name, cast);
		} catch (Exception e) {
			return def;
		}
	}

	private static <T> T getOrDefaultR(R r, String name, Class<T> cast, T def) {
		try {
			return r.get(name, cast);
		} catch (Exception e) {
			return def;
		}
	}
}
