package ganymedes01.etfuturum.client.gui.inventory;

import ganymedes01.etfuturum.inventory.ContainerBlastFurnace;
import ganymedes01.etfuturum.tileentities.TileEntityBlastFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiBlastFurnace extends GuiContainer {
	private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/blast_furnace.png");
	private final TileEntityBlastFurnace tileFurnace;

	public GuiBlastFurnace(InventoryPlayer p_i1091_1_, TileEntityBlastFurnace p_i1091_2_) {
		super(new ContainerBlastFurnace(p_i1091_1_, p_i1091_2_));
		this.tileFurnace = p_i1091_2_;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = this.tileFurnace.hasCustomInventoryName() ? this.tileFurnace.getInventoryName() : I18n.format(this.tileFurnace.getInventoryName());
		this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		if (this.tileFurnace.isBurning()) {
			int i1 = this.tileFurnace.getBurnTimeRemainingScaled(13);
			this.drawTexturedModalRect(k + 56, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
			i1 = this.tileFurnace.getCookProgressScaled(24);
			this.drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
		}
	}
}