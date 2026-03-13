package ganymedes01.etfuturum.client.gui.inventory;

import ganymedes01.etfuturum.inventory.ContainerChestGeneric;
import ganymedes01.etfuturum.tileentities.TileEntityShulkerBox;
import ganymedes01.etfuturum.tileentities.TileEntityShulkerBox.ShulkerBoxType;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class GuiShulkerBox extends GuiContainer {

	private static final ResourceLocation[] backgrounds = Arrays.stream(ShulkerBoxType.VALUES)
			.map(t -> t.getGuiTextureName() == null ? new ResourceLocation("textures/gui/container/shulker_box.png") :
					new ResourceLocation(String.format("etfuturum:textures/gui/container/ironshulkerbox/%s.png", t.getGuiTextureName())))
			.toArray(ResourceLocation[]::new);
	private final IInventory upperChestInventory;
	private final IInventory lowerChestInventory;
//	private final int inventoryRows;

	private final TileEntityShulkerBox.ShulkerBoxType type;

	public GuiShulkerBox(IInventory playerInventory, IInventory chestInventory) {
		super(new ContainerChestGeneric(playerInventory, chestInventory, ((TileEntityShulkerBox) chestInventory).getRowSize(), chestInventory.getSizeInventory() != 27));
		this.upperChestInventory = playerInventory;
		this.lowerChestInventory = chestInventory;

		this.type = ((TileEntityShulkerBox) chestInventory).type;
		this.xSize = type.getXSize();
		this.ySize = type.getYSize();
		this.allowUserInput = false;
//		this.inventoryRows = chestInventory.getSizeInventory() / type.getRowSize();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (type == ShulkerBoxType.VANILLA) {
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

		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
}
