package ganymedes01.etfuturum.compat.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ganymedes01.etfuturum.EtFuturumLootTables;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.api.CompostingRegistry;
import ganymedes01.etfuturum.core.utils.ItemStackMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static codechicken.lib.gui.GuiDraw.*;

public class ComposterHandler extends TemplateRecipeHandler implements ICraftingHandler, IUsageHandler {
	public final PositionedStack composter = new PositionedStack(new ItemStack(ModBlocks.COMPOSTER.get()), 75, 78, false);
	public final List<PositionedStack> outputs = new ArrayList<>();
	public final ItemStackMap<TableProps> props = new ItemStackMap<>();

	public class CompostedRecipe extends CachedRecipe {
		public CompostedRecipe(List<PositionedStack> inputs) {
			this.inputs = inputs;
		}

		public final List<PositionedStack> inputs;

		@Override
		public List<PositionedStack> getIngredients() {
			return this.getCycledIngredients(cycleticks / 20, this.inputs);
		}

		@Override
		public PositionedStack getResult() {
			return composter;
		}

		@Override
		public List<PositionedStack> getOtherStacks() {
			return outputs;
		}
	}

	private void composeRecipeMap(ItemStack predicate) {
		List<ItemStack> inputs = new ArrayList<>(CompostingRegistry.getComposts().keySet());
		WeightedRandomChestContent[] tables = EtFuturumLootTables.COMPOSTER_LOOT.getItems(Minecraft.getMinecraft().thePlayer.worldObj.rand);
		if (!itemCheck(predicate, inputs) && !tableCheck(predicate, tables) && !this.composter.contains(predicate))
			return;
		arecipes.clear();
		outputs.clear();
		props.clear();

		List<List<PositionedStack>> in = new ArrayList<>();
		int i = inputs.size();
		int x, y;
		while (i > 0) {
			List<PositionedStack> slots = new ArrayList<>();
			int size = Math.min(i, 36);
			x = 3;
			y = 3;
			for (int k = 0; k < size; ) {
				slots.add(this.resolveIngredient(inputs.remove(0), x, y));
				x += 18;
				k++;
				if (k % 9 == 0) {
					x = 3;
					y += 18;
				}
			}
			in.add(slots);
			i -= size;
		}

		int max = tables.length;
		i = 0;
		x = (168 - (18 * Math.min(max, 9))) >> 1;
		y = (210 - (max / 9 * 18)) >> 1;
		for (WeightedRandomChestContent table : tables) {
			this.outputs.add(new PositionedStack(table.theItemId, x, y, false));
			this.props.put(table.theItemId, new TableProps(table.theMinimumChanceToGenerateItem, table.theMaximumChanceToGenerateItem, table.itemWeight));
			x += 18;
			i++;
			if (i == 9) {
				max -= 9;
				x = (168 - (18 * Math.min(max, 9))) >> 1;
				y += 18;
				i = 0;
			}
		}

		for (List<PositionedStack> slots : in) {
			arecipes.add(new CompostedRecipe(slots));
		}
	}

	private PositionedStack resolveIngredient(ItemStack stack, int x, int y) {
		if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
			List<ItemStack> stacks = new ArrayList<>();
			Item item = stack.getItem();
			for (CreativeTabs tab : item.getCreativeTabs()) {
				item.getSubItems(item, tab, stacks);
			}
			return new PositionedStack(stacks, x, y, true);
		} else return new PositionedStack(stack, x, y, false);
	}

	private boolean itemCheck(ItemStack stack, Collection<ItemStack> inputs) {
		for (ItemStack input : inputs) {
			if (NEIClientUtils.areStacksSameTypeCrafting(input, stack)) return true;
		}
		return false;
	}

	private boolean tableCheck(ItemStack stack, WeightedRandomChestContent[] table) {
		for (WeightedRandomChestContent cont : table) {
			if (NEIClientUtils.areStacksSameType(cont.theItemId, stack)) return true;
		}
		return false;
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals(this.getOverlayIdentifier())) {
			composeRecipeMap(null);
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (WeightedRandomChestContent item : EtFuturumLootTables.COMPOSTER_LOOT.getItems(Minecraft.getMinecraft().thePlayer.worldObj.rand)) {
			if (NEIClientUtils.areStacksSameType(item.theItemId, result)) composeRecipeMap(result);
		}
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if (ingredients.length > 0 && ingredients[0] instanceof ItemStack) {
			if (inputId.equals(this.getOverlayIdentifier())) {
				composeRecipeMap(null);
			} else {
				loadUsageRecipes((ItemStack) ingredients[0]);
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if (itemCheck(ingredient, CompostingRegistry.getComposts().keySet()) || this.composter.contains(ingredient)) {
			composeRecipeMap(ingredient);
		}
	}

	@Override
	public void drawBackground(int recipe) {
		GL11.glColor4f(1, 1, 1, 1);
		changeTexture(getGuiTexture());
		drawTexturedModalRect(0, 0, 0, 0, 166, 100);
	}

	@Override
	public void drawExtras(int recipe) {
		CompostedRecipe cached = (CompostedRecipe) arecipes.get(recipe);
		GL11.glPushMatrix();
		GL11.glScalef(0.5f, 0.5f, 1f);
		for (PositionedStack stack : cached.getIngredients()) {
			int chance = CompostingRegistry.getCompostChance(stack.items[0]);
			String str = chance + "%";
			GuiDraw.drawString(str, (stack.relx + 16 - (fontRenderer.getStringWidth(str) >> 1)) << 1, (stack.rely + 12) << 1, 0xFFFFFF, true);
		}
		GL11.glPopMatrix();
	}

	@Override
	public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currenttip, int recipe) {
		if (stack != null && this.props.containsKey(stack)) {
			TableProps props = this.props.get(stack);
			currenttip.add(currenttip.size() - 1, "§9" + I18n.format("efr.nei.amount") + ":§6 " + props.minSize + "§7~§6" + props.maxSize);
			currenttip.add(currenttip.size() - 1, "§9" + I18n.format("efr.nei.weight") + ":§6 " + props.wt);
		}
		return super.handleItemTooltip(gui, stack, currenttip, recipe);
	}

	@Override
	public int recipiesPerPage() {
		return 1;
	}

	@Override
	public String getOverlayIdentifier() {
		return Tags.MOD_ID + ".composter";
	}

	@Override
	public String getGuiTexture() {
		return Tags.MOD_ID + ":textures/gui/nei/board.png";
	}

	@Override
	public String getRecipeName() {
		return I18n.format("efr.nei.composter");
	}

	/**
	 * Bogus class that captures loot table properties
	 */
	public static class TableProps {
		public final int minSize, maxSize, wt;

		public TableProps(int minSize, int maxSize, int wt) {
			this.minSize = minSize;
			this.maxSize = maxSize;
			this.wt = wt;
		}
	}
}
