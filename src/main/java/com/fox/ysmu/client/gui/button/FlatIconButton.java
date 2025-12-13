package com.fox.ysmu.client.gui.button;


import com.fox.ysmu.YesSteveModel;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class FlatIconButton extends FlatColorButton {
    private final static ResourceLocation ICON = new ResourceLocation(YesSteveModel.MOD_ID, "texture/icon.png");
    private final int textureX;
    private final int textureY;

    public FlatIconButton(int x, int y, int width, int height, int textureX, int textureY, OnPress onPress) {
        super(x, y, width, height, "", onPress);
        this.textureX = textureX;
        this.textureY = textureY;
    }

    @Override
    public void renderWidget(@Nonnull Minecraft mc, int mouseX, int mouseY, float pPartialTick) {
        super.renderWidget(mc, mouseX, mouseY, pPartialTick);
        int startX = (this.width - 16) / 2;
        int startY = (this.height - 16) / 2;
        mc.getTextureManager().bindTexture(ICON);
        drawScaledCustomSizeModalRect(this.x + startX, this.y + startY, textureX, textureY, 16, 16, 16, 16, 256, 256);
    }
}
