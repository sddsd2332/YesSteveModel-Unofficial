package com.fox.ysmu.client.gui;

import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.client.gui.button.Button;
import com.fox.ysmu.model.ServerModelManager;
import net.minecraft.client.resources.I18n;

import java.io.File;

public class OpenModelFolderScreen extends Screen {
    private final PlayerModelScreen screen;

    protected OpenModelFolderScreen(PlayerModelScreen screen) {
        this.screen = screen;
    }

    @Override
    public void initGui() {
        int x = (width - 310) / 2;
        int y = height / 2 + 60;
        this.addButton(new Button(x, y, 150, 20, I18n.format("gui.yes_steve_model.open_model_folder.open"), (b) -> {
            try {
                Class<?> oclass = Class.forName("java.awt.Desktop");
                Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
                oclass.getMethod("open", File.class).invoke(object, ServerModelManager.CUSTOM.toFile());
            } catch (Exception e) {
                YesSteveModel.LOGGER.error("Problem opening mods folder", e);
            }
        }));
        this.addButton(new Button(x + 160, y, 150, 20, I18n.format("gui.yes_steve_model.model.return"), (b) -> {
            this.mc.displayGuiScreen(this.screen);
        }));
    }

    @Override
    public void drawScreen(int pMouseX, int pMouseY, float pPartialTick) {
        this.drawDefaultBackground();
        this.drawWordWrap(I18n.format("gui.yes_steve_model.open_model_folder.tips"),
                (width - 400) / 2, height / 2 - 80, 400, 0XFFFFFF);
        super.drawScreen(pMouseX, pMouseY, pPartialTick);
    }
}
