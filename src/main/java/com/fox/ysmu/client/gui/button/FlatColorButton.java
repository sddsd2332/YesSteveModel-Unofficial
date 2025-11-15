package com.fox.ysmu.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.util.Collections;
import java.util.List;

public class FlatColorButton extends GuiButton {
    private boolean isSelect = false;
    public List<String> tooltips;

    public FlatColorButton(int id, int pX, int pY, int pWidth, int pHeight, String pMessage) {
        super(id, pX, pY, pWidth, pHeight, pMessage);
    }

    public FlatColorButton setTooltips(String key) {
        tooltips = Collections.singletonList(I18n.format(key));
        return this;
    }

    public FlatColorButton setTooltips(List<String> tooltips) {
        this.tooltips = tooltips;
        return this;
    }

    // renderWidget -> drawButton
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        FontRenderer font = mc.fontRenderer;
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int backgroundColor = isSelect ? 0xff_1E90FF : 0xff_434242;
        this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, backgroundColor, backgroundColor);

        if (this.hovered) {
            this.drawGradientRect(this.x, this.y + 1, this.x + 1, this.y + this.height - 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x + this.width - 1, this.y + 1, this.x + this.width, this.y + this.height - 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x, this.y + this.height - 1, this.y + this.width, this.y + this.height, 0xff_F3EFE0, 0xff_F3EFE0);
        }
        //GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawCenteredString(font, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, 0xF3EFE0);
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
