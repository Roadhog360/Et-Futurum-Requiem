package ganymedes01.etfuturum.client.gui.inventory;

import ganymedes01.etfuturum.inventory.ContainerChestGeneric;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel.BarrelType;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class GuiBarrel extends GuiContainer {

	private static final ResourceLocation[] backgrounds = Arrays.stream(BarrelType.VALUES)
			.map(t -> t.getGuiTextureName() == null ? new ResourceLocation("textures/gui/container/barrel.png") :
					new ResourceLocation(String.format("etfuturum:textures/gui/container/ironbarrels/%s.png", t.getGuiTextureName())))
			.toArray(ResourceLocation[]::new);
	private final IInventory upperChestInventory;
	private final IInventory lowerChestInventory;
	/**
	 * window height is calculated with these values; the more rows, the higher
	 */
	private final int inventoryRows;

	private final BarrelType type;

	public GuiBarrel(IInventory playerInventory, IInventory chestInventory) {
		super(new ContainerChestGeneric(playerInventory, chestInventory, ((TileEntityBarrel) chestInventory).getRowSize(), chestInventory.getSizeInventory() != 27));
		this.upperChestInventory = playerInventory;
		this.lowerChestInventory = chestInventory;

		this.type = ((TileEntityBarrel) chestInventory).type;
		this.xSize = type.getXSize();
		this.ySize = type.getYSize();
		this.allowUserInput = false;
		this.inventoryRows = chestInventory.getSizeInventory() / type.getRowSize();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (type == BarrelType.WOOD) {
			this.fontRendererObj.drawString(this.lowerChestInventory.hasCustomInventoryName() ? this.lowerChestInventory.getInventoryName() : I18n.format(this.lowerChestInventory.getInventoryName()), 8, 6, 4210752);
			this.fontRendererObj.drawString(this.upperChestInventory.hasCustomInventoryName() ? this.upperChestInventory.getInventoryName() : I18n.format(this.upperChestInventory.getInventoryName()), 8, this.ySize - 96 + 2, 4210752);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(backgrounds[type.ordinal()]);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;

		if (type == BarrelType.NETHERITE || type == BarrelType.DARKSTEEL) {
			final Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(guiLeft, guiTop, 0, 0.0, 0.0);
			tessellator.addVertexWithUV(guiLeft, guiTop + ySize, 0, 0.0, 1.0);
			tessellator.addVertexWithUV(guiLeft + xSize, guiTop + ySize, 0, 1.0, 1.0);
			tessellator.addVertexWithUV(guiLeft + xSize, guiTop, 0, 1.0, 0.0);
			tessellator.draw();
			return;
		}

		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
}
