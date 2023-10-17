package ganymedes01.etfuturum.compat;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import ganymedes01.etfuturum.compat.cthandlers.*;
import ganymedes01.etfuturum.recipes.BlastFurnaceRecipes;
import ganymedes01.etfuturum.recipes.ModRecipes;
import ganymedes01.etfuturum.recipes.SmokerRecipes;
import minetweaker.MineTweakerAPI;
import minetweaker.MineTweakerImplementationAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.item.IngredientStack;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.api.oredict.IOreDictEntry;
import minetweaker.util.IEventHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class CompatCraftTweaker {

	public static void onPostInit() {
		MineTweakerAPI.registerClass(CTBlastFurnace.class);
		MineTweakerAPI.registerClass(CTSmoker.class);
		MineTweakerAPI.registerClass(CTSmithingTable.class);
		MineTweakerAPI.registerClass(CTBrewingFuels.class);
		MineTweakerAPI.registerClass(CTEnchantingFuels.class);
		MineTweakerAPI.registerClass(CTComposting.class);

		MineTweakerImplementationAPI.onReloadEvent(new ReloadEventHandler());
		MineTweakerImplementationAPI.onPostReload(new PostReloadEventHandler());
	}

	public static Object toObject(IIngredient iStack) {
		if (iStack == null)
			return null;
		else {
			if (iStack instanceof IOreDictEntry)
				return ((IOreDictEntry) iStack).getName();
			else if (iStack instanceof IItemStack)
				return minetweaker.api.minecraft.MineTweakerMC.getItemStack((IItemStack) iStack);
			else if (iStack instanceof IngredientStack) {
				IIngredient ingr = ReflectionHelper.getPrivateValue(IngredientStack.class, (IngredientStack) iStack, "ingredient");
				return toObject(ingr);
			} else
				return null;
		}
	}

	public static FluidStack toFluidStack(ILiquidStack iStack) {
		return minetweaker.api.minecraft.MineTweakerMC.getLiquidStack(iStack);
	}

	public static ItemStack getItemStack(IItemStack item) {
		return minetweaker.api.minecraft.MineTweakerMC.getItemStack(item);
	}

	public static ItemStack[] getItemStacks(List<IItemStack> item) {
		return minetweaker.api.minecraft.MineTweakerMC.getItemStacks(item);
	}

	public static Object getInternal(IIngredient pattern) {
		Object internal = pattern.getInternal();
		if (internal instanceof String || internal instanceof ItemStack) {
			return internal;
		}
		return null;
	}

	public static class ReloadEventHandler implements IEventHandler<MineTweakerImplementationAPI.ReloadEvent> {
		@Override
		public void handle(MineTweakerImplementationAPI.ReloadEvent event) {
			ModRecipes.unregisterModdedRawOres();
			ModRecipes.registerModdedRawOres();

			BlastFurnaceRecipes.smelting().setReloadingCT(true);
			BlastFurnaceRecipes.smelting().clearLists();
			SmokerRecipes.smelting().setReloadingCT(true);
			SmokerRecipes.smelting().clearLists();
		}
	}

	/**
	 * Rebakes the Ore Dictionary, then refreshes recipes and ensures recipe lists are sorted.
	 */
	public static class PostReloadEventHandler implements IEventHandler<MineTweakerImplementationAPI.ReloadEvent> {
		@Override
		public void handle(MineTweakerImplementationAPI.ReloadEvent event) {
			// CraftTweaker doesn't rebake OreDictionary.stackToId when you /mt reload or when the client connects to a server...
			// If your scripts change ore dicts, this can lead to issues where OreDictionary.getOreIDs(ItemStack) returns old unchanged ore ids.
			// Technically, I could avoid this issue by avoiding OreDictionary.stackToId and only using methods that involve OreDictionary.idToStack.
			// However, that would be slightly slower every time I want to check a stack's IDs. Rebaking the map means it's slower just once, and other mods may appreciate it.
			if (Loader.isModLoaded("NotEnoughItems") && FMLCommonHandler.instance().getSide() != Side.SERVER) {
				NEIEtFuturumConfig.clearCaches();
			}

			OreDictionary.rebakeMap();

			BlastFurnaceRecipes.smelting().setReloadingCT(false);
			SmokerRecipes.smelting().setReloadingCT(false);
		}
	}
}
