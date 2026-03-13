package ganymedes01.etfuturum.client.gui.inventory;

import ganymedes01.etfuturum.inventory.ContainerSmithingTable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiSmithingTable extends GuiContainer {
	public static final ResourceLocation texturePath = new ResourceLocation("textures/gui/container/smithing.png");
	public final ContainerSmithingTable container;

	public GuiSmithingTable(ContainerSmithingTable container) {
		super(container);
		this.inventorySlots = container;
		this.container = container;
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(texturePath);
		final int offsetX = (this.width - this.xSize) / 2;
		final int offsetY = (this.height - this.ySize) / 2;
		drawTexturedModalRect(offsetX, offsetY, 0, 0, xSize, ySize);
		if (container.unable()) drawTexturedModalRect(offsetX + 99, offsetY + 45, xSize, 0, 28, 21);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(I18n.format("gui.smithing.upgrade"), 60, 18, 0x000000);
	}
}
