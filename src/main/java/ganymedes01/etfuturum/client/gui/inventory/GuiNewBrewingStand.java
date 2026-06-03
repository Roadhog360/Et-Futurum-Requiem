package ganymedes01.etfuturum.client.gui.inventory;

import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.inventory.ContainerNewBrewingStand;
import ganymedes01.etfuturum.tileentities.TileEntityNewBrewingStand;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiNewBrewingStand extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Tags.MOD_ID + ":textures/gui/container/brewing_stand.png");
	private final TileEntityNewBrewingStand tile;

	public GuiNewBrewingStand(InventoryPlayer playerInvt, TileEntityNewBrewingStand tile) {
		super(new ContainerNewBrewingStand(playerInvt, tile));
		this.tile = tile;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		String s = tile.hasCustomInventoryName() ? tile.getInventoryName() : I18n.format(tile.getInventoryName());
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f0, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
		int i1 = tile.getBrewTime();

		if (i1 > 0) {
			int j1 = (int) (28.0F * (1.0F - i1 / 400.0F));

			if (j1 > 0)
				drawTexturedModalRect(k + 97, l + 16, 176, 0, 9, j1);

			int k1 = i1 / 2 % 7;

			switch (k1) {
				case 0:
					j1 = 29;
					break;
				case 1:
					j1 = 24;
					break;
				case 2:
					j1 = 20;
					break;
				case 3:
					j1 = 16;
					break;
				case 4:
					j1 = 11;
					break;
				case 5:
					j1 = 6;
					break;
				case 6:
					j1 = 0;
			}

			if (j1 > 0)
				drawTexturedModalRect(k + 63, l + 14 + 29 - j1, 185, 29 - j1, 12, j1);
		}

		int fuel = tile.getFuel();
		if (fuel > 0) {
			int size = (int) (18F * (fuel / (float) tile.getCurrentFuel()));
			drawTexturedModalRect(k + 60, l + 44, 176, 29, size, 4);
		}
	}
}