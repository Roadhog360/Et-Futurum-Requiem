package ganymedes01.etfuturum.backhand.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import ganymedes01.etfuturum.backhand.Backhand;
import mods.battlegear2.client.gui.controls.GuiToggleButton;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class BackhandConfigGui extends GuiScreen {
    private final GuiScreen parent;

    public BackhandConfigGui(GuiScreen parent) {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 75, this.height - 38, I18n.format("gui.done")));
        this.buttonList.add(new GuiToggleButton(10, this.width / 2 - 75, this.height / 2 - 12, I18n.format("backhandconfig.offhandRest") + ":" + Backhand.INSTANCE.RenderEmptyOffhandAtRest, this.fontRendererObj));

        for (Object obj : this.buttonList) {
            ((GuiButton)obj).xPosition = this.width/2 - ((GuiButton)obj).getButtonWidth()/2;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 1) {
                FMLClientHandler.instance().showGuiScreen(parent);
            }
            if (button.id == 10) {
                Backhand.INSTANCE.RenderEmptyOffhandAtRest = !Backhand.INSTANCE.RenderEmptyOffhandAtRest;
            }
            if(button instanceof GuiToggleButton){
                ((GuiToggleButton) button).toggleDisplayString();
            }
        }
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        this.drawDefaultBackground();
        this.drawGradientRect(0, 40, this.width, this.height-60, -1072689136, -804253680);
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        String configTitle = I18n.format("backhandconfig.title");
        this.fontRendererObj.drawString(configTitle, this.width/2 - this.fontRendererObj.getStringWidth(configTitle)/2, 20, 0xFFFFFF);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        BattlegearConfig.refreshConfig();
    }
}
