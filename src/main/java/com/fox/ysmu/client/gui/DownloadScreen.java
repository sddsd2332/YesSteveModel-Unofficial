package com.fox.ysmu.client.gui;

import com.fox.ysmu.client.gui.button.FlatColorButton;
import com.fox.ysmu.util.Keep;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class DownloadScreen extends Screen {
    private final PlayerModelScreen parent;
    private int x;
    private int y;

    public DownloadScreen(PlayerModelScreen parent) {
        this.parent = parent;
    }

    @Override
    @Keep
    public void initGui() {
        this.x = (width - 420) / 2;
        this.y = (height - 235) / 2;

        addButton(new FlatColorButton(x + 5, y, 80, 18, I18n.format("gui.yes_steve_model.model.return"), (b) -> this.mc.displayGuiScreen(parent)));
    }

    @Override
    @Keep
    public void drawScreen(int pMouseX, int pMouseY, float pPartialTick) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, TextFormatting.DARK_RED + "Coming Soooooooooooooooooooooooooon™", width / 2, height / 2 - 5, 0xFFFFFFFF);
        super.drawScreen(pMouseX, pMouseY, pPartialTick);
    }
}