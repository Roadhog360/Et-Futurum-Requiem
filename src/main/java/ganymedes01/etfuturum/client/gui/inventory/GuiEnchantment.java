package ganymedes01.etfuturum.client.gui.inventory;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.inventory.ContainerEnchantment;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.util.glu.Project;
import roadhog360.hogutils.api.blocksanditems.utils.ItemMetaPair;
import roadhog360.hogutils.api.hogtags.helpers.ItemTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GuiEnchantment extends GuiContainer {

	private final ResourceLocation TEXTURE;
	/**
	 * The ResourceLocation containing the texture for the Book rendered above the enchantment table
	 */
	private static final ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE = new ResourceLocation("textures/entity/enchanting_table_book.png");
	/**
	 * The ModelBook instance used for rendering the book on the Enchantment table
	 */
	private static final ModelBook MODEL_BOOK = new ModelBook();
	/**
	 * The player inventory currently bound to this GuiEnchantment instance.
	 */
	private final InventoryPlayer playerInventory;
	/**
	 * A Random instance for use with the enchantment gui
	 */
	private final Random random = new Random();
	/**
	 * The same reference as {@link GuiContainer#inventorySlots}, downcasted to {@link ContainerEnchantment}.
	 */
	private final ContainerEnchantment container;
	public int ticks;
	public float flip;
	public float oFlip;
	public float flipT;
	public float flipA;
	public float open;
	public float oOpen;
	ItemStack last;
	private final String nameable;

	/**
	 * How long has the player seen the "lapis lazuli" text?
	 */
	private int viewingTicks = 0;
	private ItemStack displayItem;
	private final List<ItemMetaPair> displayStacks;
	private final Random displayRand = new Random();
	private static final ItemStack LAPIS = new ItemStack(Items.dye, 1, 4); //This is used to match with lapis to put it at the front of the list

	public GuiEnchantment(InventoryPlayer p_i45502_1_, World worldIn, String p_i45502_3_) {
		super(new ContainerEnchantment(p_i45502_1_, worldIn, 0, 0, 0));
		playerInventory = p_i45502_1_;
		container = (ContainerEnchantment) inventorySlots;
		nameable = p_i45502_3_;

		TEXTURE = new ResourceLocation(container.noFuel ? "textures/gui/container/enchanting_table.png" :
				Tags.MOD_ID + ":textures/gui/container/enchanting_table.png");

		displayStacks = new ObjectArrayList<>(ItemTags.getInTag(Tags.MOD_ID + ":enchantment_fuel"));
		if (displayStacks.size() > 1) {
			for (int i = 1; i < displayStacks.size(); i++) {
				ItemMetaPair mapping = displayStacks.get(i);
				if (mapping.getMeta() == 4 && mapping.get() == Items.dye) {
					displayStacks.remove(i);
					displayStacks.add(0, mapping);
					break;
				}
			}
		}
		displayItem = container.noFuel ? LAPIS : displayStacks.get(0).newItemStack();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(nameable == null ? I18n.format("container.enchant") : nameable, 12, 5, 4210752);
		fontRendererObj.drawString(I18n.format(playerInventory.getInventoryName()), 8, ySize - 96 + 2, 4210752);
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen() {
		super.updateScreen();
		if (displayStacks.size() > 1) { //We only need this if there are 2 or more elements in the list
			viewingTicks++;
		}
		func_147068_g();
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		int var4 = (width - xSize) / 2;
		int var5 = (height - ySize) / 2;

		for (int var6 = 0; var6 < 3; ++var6) {
			int var7 = mouseX - (var4 + 60);
			int var8 = mouseY - (var5 + 14 + 19 * var6);

			if (var7 >= 0 && var8 >= 0 && var7 < 108 && var8 < 19 && container.enchantItem(mc.thePlayer, var6))
				mc.playerController.sendEnchantPacket(container.windowId, var6);
		}
	}

	/**
	 * Args : renderPartialTicks, mouseX, mouseY
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		OpenGLHelper.colour(1, 1, 1);
		mc.getTextureManager().bindTexture(TEXTURE);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
		OpenGLHelper.pushMatrix();
		OpenGLHelper.matrixMode(5889);
		OpenGLHelper.pushMatrix();
		OpenGLHelper.loadIdentity();
		ScaledResolution var6 = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		OpenGLHelper.viewport((var6.getScaledWidth() - 320) / 2 * var6.getScaleFactor(), (var6.getScaledHeight() - 240) / 2 * var6.getScaleFactor(), 320 * var6.getScaleFactor(), 240 * var6.getScaleFactor());
		OpenGLHelper.translate(-0.34F, 0.23F, 0.0F);
		Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
		float var7 = 1.0F;
		OpenGLHelper.matrixMode(5888);
		OpenGLHelper.loadIdentity();
		RenderHelper.enableStandardItemLighting();
		OpenGLHelper.translate(0.0F, 3.3F, -16.0F);
		OpenGLHelper.scale(var7, var7, var7);
		float var8 = 5.0F;
		OpenGLHelper.scale(var8, var8, var8);
		OpenGLHelper.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_BOOK_TEXTURE);
		OpenGLHelper.rotate(20.0F, 1.0F, 0.0F, 0.0F);
		float var9 = oOpen + (open - oOpen) * partialTicks;
		OpenGLHelper.translate((1.0F - var9) * 0.2F, (1.0F - var9) * 0.1F, (1.0F - var9) * 0.25F);
		OpenGLHelper.rotate(-(1.0F - var9) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
		OpenGLHelper.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		float var10 = oFlip + (flip - oFlip) * partialTicks + 0.25F;
		float var11 = oFlip + (flip - oFlip) * partialTicks + 0.75F;
		var10 = (var10 - MathHelper.truncateDoubleToInt(var10)) * 1.6F - 0.3F;
		var11 = (var11 - MathHelper.truncateDoubleToInt(var11)) * 1.6F - 0.3F;

		if (var10 < 0.0F)
			var10 = 0.0F;

		if (var11 < 0.0F)
			var11 = 0.0F;

		if (var10 > 1.0F)
			var10 = 1.0F;

		if (var11 > 1.0F)
			var11 = 1.0F;

		OpenGLHelper.enableRescaleNormal();
		MODEL_BOOK.render(null, 0.0F, var10, var11, var9, 0.0F, 0.0625F);
		OpenGLHelper.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		OpenGLHelper.matrixMode(5889);
		OpenGLHelper.viewport(0, 0, mc.displayWidth, mc.displayHeight);
		OpenGLHelper.popMatrix();
		OpenGLHelper.matrixMode(5888);
		OpenGLHelper.popMatrix();
		RenderHelper.disableStandardItemLighting();
		OpenGLHelper.colour(1.0F, 1.0F, 1.0F);
		EnchantmentNameParts.instance.reseedRandomGenerator(container.enchantmentSeed);
		int var12 = container.getLapisAmount();

		for (int i1 = 0; i1 < 3; i1++) {
			int var14 = k + 60;
			int var15 = var14 + 20;
			byte var16 = 86;
			String s = EnchantmentNameParts.instance.generateNewRandomName();
			zLevel = 0.0F;
			mc.getTextureManager().bindTexture(TEXTURE);
			int j1 = container.enchantLevels[i1];
			OpenGLHelper.colour(1.0F, 1.0F, 1.0F);

			if (j1 == 0)
				drawTexturedModalRect(var14, l + 14 + 19 * i1, 0, 185, 108, 19);
			else {
				String s1 = "" + j1;
				FontRenderer fontrenderer = mc.standardGalacticFontRenderer;
				int k1 = 6839882;

				if ((var12 < i1 + 1 || mc.thePlayer.experienceLevel < j1) && !mc.thePlayer.capabilities.isCreativeMode) {
					drawTexturedModalRect(var14, l + 14 + 19 * i1, 0, 185, 108, 19);
					drawTexturedModalRect(var14 + 1, l + 15 + 19 * i1, 16 * i1, 239, 16, 16);
					fontrenderer.drawSplitString(s, var15, l + 16 + 19 * i1, var16, (k1 & 16711422) >> 1);
					k1 = 4226832;
				} else {
					int l1 = mouseX - (k + 60);
					int i2 = mouseY - (l + 14 + 19 * i1);

					if (l1 >= 0 && i2 >= 0 && l1 < 108 && i2 < 19) {
						drawTexturedModalRect(var14, l + 14 + 19 * i1, 0, 204, 108, 19);
						k1 = 16777088;
					} else
						drawTexturedModalRect(var14, l + 14 + 19 * i1, 0, 166, 108, 19);

					drawTexturedModalRect(var14 + 1, l + 15 + 19 * i1, 16 * i1, 223, 16, 16);
					fontrenderer.drawSplitString(s, var15, l + 16 + 19 * i1, var16, k1);
					k1 = 8453920;
				}

				fontrenderer = mc.fontRenderer;
				fontrenderer.drawStringWithShadow(s1, var15 + 86 - fontrenderer.getStringWidth(s1), l + 16 + 19 * i1 + 7, k1);
				OpenGLHelper.colour(1, 1, 1);
			}
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		boolean var4 = mc.thePlayer.capabilities.isCreativeMode;
		int var5 = container.getLapisAmount();

		for (int var6 = 0; var6 < 3; ++var6) {
			int var7 = container.enchantLevels[var6];
			int var8 = container.enchantmentIds[var6];
			int var9 = var6 + 1;

			if (func_146978_c/*isPointInRegion*/(60, 14 + 19 * var6, 108, 17, mouseX, mouseY) && var7 > 0 && var8 >= 0) {
				ArrayList<String> var10 = Lists.newArrayList();
				String var11;
				Enchantment ench = Enchantment.enchantmentsList[var8 % Enchantment.enchantmentsList.length];
				if (ench != null) {
					var11 = ench.getTranslatedName((var8) >> 8);
					var10.add(EnumChatFormatting.WHITE.toString() + EnumChatFormatting.ITALIC + I18n.format("container.enchant.clue", var11));
				}

				if (!var4) {
					if (ench != null) {
						var10.add("");
					}

					if (mc.thePlayer.experienceLevel < var7)
						var10.add(EnumChatFormatting.RED + I18n.format("container.enchant.level.required") + ": " + container.enchantLevels[var6]);
					else {
						if (displayStacks.size() > 1) {
							if (viewingTicks > 40) {
								viewingTicks = 0;
								ArrayList<ItemStack> tempList = new ArrayList<>();
								for(ItemMetaPair mapping : displayStacks) {
									ItemStack stack = mapping.newItemStack();
									if(stack.isItemEqual(displayItem)) {
										tempList.add(stack);
									}
								}
								displayItem = tempList.get(displayRand.nextInt(tempList.size()));
								//We don't want to display the same item name twice in a row (because then it looks like it's being displayed for a longer time)
							}
						}
						if (!container.noFuel) {
							String name;

							if (displayItem.isItemEqual(LAPIS)) {
								name = StatCollector.translateToLocal("container.enchant.lapis." + (var9 > 1 ? "many" : "one"));
							} else {
								name = "%s " + StatCollector.translateToLocal(displayItem.getUnlocalizedName() + ".name");
							}

							var11 = String.format(name, var9);
							var10.add((var5 >= var9 ? EnumChatFormatting.GRAY : EnumChatFormatting.RED) + var11);
						}

						var11 = I18n.format(var9 == 1 ? "container.enchant.level.one" : "container.enchant.level.many", var9);

						var10.add(EnumChatFormatting.GRAY + var11);
					}
				}

				drawHoveringText(var10, mouseX, mouseY, fontRendererObj);
				break;
			}
		}
	}

	public void func_147068_g() {
		ItemStack var1 = inventorySlots.getSlot(0).getStack();

		if (!ItemStack.areItemStacksEqual(var1, last)) {
			last = var1;

			do
				flipT += random.nextInt(4) - random.nextInt(4);
			while (flip <= flipT + 1.0F && flip >= flipT - 1.0F);
		}

		++ticks;
		oFlip = flip;
		oOpen = open;
		boolean var2 = false;

		for (int var3 = 0; var3 < 3; ++var3)
			if (container.enchantLevels[var3] != 0) {
				var2 = true;
				break;
			}

		if (var2)
			open += 0.2F;
		else
			open -= 0.2F;

		open = MathHelper.clamp_float(open, 0.0F, 1.0F);
		float var5 = (flipT - flip) * 0.4F;
		float var4 = 0.2F;
		var5 = MathHelper.clamp_float(var5, -var4, var4);
		flipA += (var5 - flipA) * 0.9F;
		flip += flipA;
	}
}