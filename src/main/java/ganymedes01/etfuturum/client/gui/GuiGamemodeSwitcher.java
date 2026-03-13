package ganymedes01.etfuturum.client.gui;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.spectator.SpectatorMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GuiGamemodeSwitcher extends GuiScreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/gamemode_switcher.png");

	private static final int BUTTON_SIZE = 26;

	private final WorldSettings.GameType currentGameMode;
	private WorldSettings.GameType gameMode = null;
	private int lastMouseX;
	private int lastMouseY;
	private boolean mouseUsedForSelection;
	private final List<GuiGamemodeButton> gameModeButtons = Lists.newArrayList();

	private static WorldSettings.GameType previousGameType;

	public GuiGamemodeSwitcher() {
		super();
		this.currentGameMode = this.getPreviousGameMode();
	}

	private static WorldSettings.GameType nextGameType(WorldSettings.GameType type) {
		switch (type) {
			case CREATIVE: {
				return WorldSettings.GameType.SURVIVAL;
			}
			case SURVIVAL: {
				return WorldSettings.GameType.ADVENTURE;
			}
			case ADVENTURE: {
				if (ConfigMixins.enableSpectatorMode)
					return SpectatorMode.SPECTATOR_GAMETYPE;
				else
					return WorldSettings.GameType.CREATIVE;
			}
			default:
				return WorldSettings.GameType.CREATIVE;
		}
	}

	private WorldSettings.GameType getPreviousGameMode() {
		if (previousGameType == null) {
			previousGameType = Minecraft.getMinecraft().playerController.currentGameType == WorldSettings.GameType.CREATIVE ? WorldSettings.GameType.SURVIVAL : WorldSettings.GameType.CREATIVE;
		}
		return previousGameType;
	}

	@Override
	protected void keyTyped(char typedChar, int keycode) {
		if (keycode == Keyboard.KEY_F4) {
			this.mouseUsedForSelection = false;
			this.gameMode = nextGameType(this.gameMode);
			return;
		}
		super.keyTyped(typedChar, keycode);
	}

	@Override
	public void handleInput() {
		super.handleInput();
		checkForClose();
	}

	@Override
	public void initGui() {
		super.initGui();
		this.gameMode = this.currentGameMode;
		Map<WorldSettings.GameType, ItemStack> map = new LinkedHashMap<>();
		map.put(WorldSettings.GameType.CREATIVE, new ItemStack(Blocks.grass));
		map.put(WorldSettings.GameType.SURVIVAL, new ItemStack(Items.iron_sword));
		map.put(WorldSettings.GameType.ADVENTURE, new ItemStack(Items.map));
		if (ConfigMixins.enableSpectatorMode)
			map.put(SpectatorMode.SPECTATOR_GAMETYPE, new ItemStack(Items.ender_eye));

		int i = 0;
		int totalWidth = map.size() * 31 - 5;
		for (Map.Entry<WorldSettings.GameType, ItemStack> entry : map.entrySet()) {
			this.gameModeButtons.add(new GuiGamemodeButton(entry.getValue(), entry.getKey(), this.width / 2 - totalWidth / 2 + i * 31, this.height / 2 - 31));
			i++;
		}
	}

	private static String getSelectNextText() {
		ChatComponentTranslation f4 = new ChatComponentTranslation("debug.gamemodes.press_f4");
		f4.getChatStyle().setColor(EnumChatFormatting.AQUA);
		return new ChatComponentTranslation("debug.gamemodes.select_next", f4).getFormattedText();
	}

	private void apply() {
		if (mc.playerController == null || mc.thePlayer == null || gameMode == null) {
			return;
		}
		if (this.gameMode != mc.playerController.currentGameType) {
			previousGameType = mc.playerController.currentGameType;
			mc.thePlayer.sendChatMessage("/gamemode " + this.gameMode.getID());
		}
	}

	private boolean checkForClose() {
		if (!Keyboard.isKeyDown(Keyboard.KEY_F3)) {
			this.apply();
			mc.displayGuiScreen(null);
			return true;
		}
		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float delta) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		mc.getTextureManager().bindTexture(TEXTURE);
		int i = this.width / 2 - 62;
		int j = this.height / 2 - 31 - 27;
		func_146110_a(i, j, 0, 0, 125, 75, 128, 128); // drawModalRectWithCustomSizedTexture
		super.drawScreen(mouseX, mouseY, delta);
		if (this.gameMode != null) {
			drawCenteredString(mc.fontRenderer, I18n.format("gameMode." + this.gameMode.getName()), this.width / 2, this.height / 2 - 31 - 20, -1);
		}
		drawCenteredString(mc.fontRenderer, getSelectNextText(), this.width / 2, this.height / 2 + 5, 0xFFFFFF);
		if (!this.mouseUsedForSelection) {
			this.lastMouseX = mouseX;
			this.lastMouseY = mouseY;
			this.mouseUsedForSelection = true;
		}
		boolean mouseDidNotMove = this.lastMouseX == mouseX && this.lastMouseY == mouseY;
		RenderHelper.enableGUIStandardItemLighting();
		for (GuiGamemodeButton buttonWidget : this.gameModeButtons) {
			buttonWidget.render();
			if (this.gameMode != null) {
				buttonWidget.setSelected(gameMode == buttonWidget.type);
			}
			if (mouseDidNotMove) continue;
			if (mouseX < buttonWidget.x || mouseY < buttonWidget.y || mouseX >= (buttonWidget.x + BUTTON_SIZE) || mouseY >= (buttonWidget.y + BUTTON_SIZE))
				continue;
			this.gameMode = buttonWidget.type;
		}
		RenderHelper.enableStandardItemLighting();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	class GuiGamemodeButton extends Gui {
		private final ItemStack icon;
		private final WorldSettings.GameType type;
		private final int x, y;

		GuiGamemodeButton(ItemStack icon, WorldSettings.GameType type, int x, int y) {
			this.icon = icon;
			this.type = type;
			this.x = x;
			this.y = y;
		}

		private boolean isSelected = false;

		void setSelected(boolean b) {
			isSelected = b;
		}

		void render() {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			mc.getTextureManager().bindTexture(TEXTURE);
			func_146110_a(this.x, this.y, 0, 75, 26, 26, 128, 128); // drawModalRectWithCustomSizedTexture
			if (isSelected) {
				func_146110_a(this.x, this.y, 26, 75, 26, 26, 128, 128); // drawModalRectWithCustomSizedTexture
			}
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			itemRender.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), icon, this.x + 5, this.y + 5);
		}
	}
}
