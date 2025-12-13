package com.fox.ysmu.client.gui;

import com.fox.ysmu.Config;
import com.fox.ysmu.client.gui.button.Button;
import com.fox.ysmu.client.gui.button.Checkbox;
import com.fox.ysmu.util.Keep;
import net.minecraft.client.resources.I18n;

import java.util.List;

public class DisclaimerScreen extends Screen {
    private Checkbox readCheckbox;
    private int x;
    private int y;

    public DisclaimerScreen() {
    }

    @Override
    @Keep
    public void initGui() {
        String mainText = I18n.format("gui.yes_steve_model.disclaimer.text");
        List<String> splitMainText = this.listLineBreakStringToWidth(mainText, 400);
        int totalHeight = splitMainText.size() * this.fontRenderer.FONT_HEIGHT + 20 + 20 + 10 + 20;
        this.x = (this.width - 400) / 2;
        this.y = (this.height - totalHeight) / 2;

        String readCheckboxText = I18n.format("gui.yes_steve_model.disclaimer.read");
        int readTextWidth = this.fontRenderer.getStringWidth(readCheckboxText);
        this.readCheckbox = new Checkbox((this.width - readTextWidth) / 2, this.y + totalHeight - 50, readCheckboxText, this.fontRenderer, !Config.DISCLAIMER_SHOW);
        addButton(this.readCheckbox);
        addButton(new Button((this.width - 300) / 2, this.y + totalHeight - 20, 300, 20, I18n.format("gui.yes_steve_model.disclaimer.close"), b -> {
            if (this.readCheckbox.selected()) {
                Config.DISCLAIMER_SHOW = false;
                this.mc.displayGuiScreen(new PlayerModelScreen());
            } else {
                this.mc.displayGuiScreen(null);
            }
        }));
    }

    @Override
    public void drawScreen(int pMouseX, int pMouseY, float pPartialTick) {
        this.drawDefaultBackground();
        this.drawWordWrap(I18n.format("gui.yes_steve_model.disclaimer.text"), this.x, this.y, 400, 0xffffffff);
        super.drawScreen(pMouseX, pMouseY, pPartialTick);
    }
}
