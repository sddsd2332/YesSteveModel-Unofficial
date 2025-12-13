package com.fox.ysmu.client.gui.button;

import com.fox.ysmu.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class Button extends GuiButton {
    protected final OnPress onPress;

    public Button(int x, int y, int width, int height, String message, OnPress onPress) {
        super(0, x, y, width, height, message);
        this.onPress = onPress;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float pPartialTick) {
        if (!this.visible) return;
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.renderWidget(mc, mouseX, mouseY, pPartialTick);
        this.mouseDragged(mc, mouseX, mouseY);
    }

    protected void renderWidget(@Nonnull Minecraft mc, int mouseX, int mouseY, float pPartialTick) {
        int i = this.getHoverState(this.isMouseOver());
        mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
        this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderString(mc.fontRenderer, this.getFGColor());
    }

    public void renderString(FontRenderer font, int color) {
        this.renderScrollingString(font, 2, color);
    }

    protected void renderScrollingString(FontRenderer font, int width, int color) {
        int i = this.x + width;
        int j = this.x + this.width - width;
        renderScrollingString(font, this.displayString, i, this.y, j, this.y + this.height, color);
    }

    public void renderScrollingString(FontRenderer font, String text, int minX, int minY, int maxX, int maxY, int color) {
        renderScrollingString(font, text, (minX + maxX) / 2, minX, minY, maxX, maxY, color);
    }

    public void renderScrollingString(FontRenderer font, String text, int centerX, int minX, int minY, int maxX, int maxY, int color) {
        int i = font.getStringWidth(text);
        int j = (minY + maxY - 9) / 2 + 1;
        int k = maxX - minX;
        if (i > k) {
            int l = i - k;
            double d0 = (double) System.currentTimeMillis() / (double) 1000.0F;
            double d1 = Math.max((double) l * (double) 0.5F, (double) 3.0F);
            double d2 = Math.sin((Math.PI / 2D) * Math.cos((Math.PI * 2D) * d0 / d1)) / (double) 2.0F + (double) 0.5F;
            double d3 = RenderUtil.lerp(d2, 0.0F, l);
            RenderUtil.scissor(minX, minY, maxX - minX, maxY - minY);
            drawString(font, text, minX - (int) d3, j, color);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            int i1 = MathHelper.clamp(centerX, minX + i / 2, maxX - i / 2);
            this.drawCenteredString(font, text, i1, j, color);
        }
    }

    protected int getFGColor() {
        if (this.packedFGColour != 0) {
            return this.packedFGColour;
        } else if (!this.enabled) {
            return 10526880;
        } else if (this.hovered) {
            return 16777120;
        } else {
            return 14737632;
        }
    }

    /**
     * 应被 Gui 调用，而不是按钮自我调用。
     */
    public void onPress() {
        this.onPress.onPress(this);
    }

    public interface OnPress {
        void onPress(Button button);
    }
}
