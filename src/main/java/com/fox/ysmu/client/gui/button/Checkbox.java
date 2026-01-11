package com.fox.ysmu.client.gui.button;

import com.fox.ysmu.YesSteveModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class Checkbox extends Button {
    private static final ResourceLocation CHECKBOX = new ResourceLocation(YesSteveModel.MOD_ID, "texture/checkbox.png");
    private static final int BOX_SIZE = 20;
    private boolean selected;

    public Checkbox(int x, int y, String message, @Nonnull FontRenderer font, boolean selected, OnPress onPress) {
        super(x, y, BOX_SIZE + 4 + font.getStringWidth(message), BOX_SIZE, message, onPress);
        this.selected = selected;
    }

    public Checkbox(int x, int y, String message, FontRenderer font, boolean selected) {
        this(x, y, message, font, selected, (b) -> {});
    }

    protected void renderWidget(@Nonnull Minecraft mc, int mouseX, int mouseY, float pPartialTick) {
        int j = this.x + BOX_SIZE + 4;
        int k = this.y + (this.height >> 1) - 4;
        mc.getTextureManager().bindTexture(CHECKBOX);
        drawScaledCustomSizeModalRect(
                this.x, this.y,
                this.isMouseOver() ? 20 : 0, this.selected() ? 20 : 0,
                20, 20,
                BOX_SIZE, BOX_SIZE,
                40, 40
        );
        this.drawString(mc.fontRenderer, this.displayString, j, k, 14737632);
    }

    @Override
    public void onPress() {
        this.selected = !this.selected;
        super.onPress();
    }

    public boolean selected() {
        return this.selected;
    }
}
