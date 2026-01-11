package com.fox.ysmu.client.gui;

import com.fox.ysmu.Config;
import com.fox.ysmu.util.RenderUtil;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.List;

public class ExtraPlayerConfigScreen extends Screen {
    private static final char RESET_KEY = 'r';
    private int posX;
    private int posY;
    private float scale;
    private float yawOffset;
    private boolean isChangePos = false;
    private boolean isChangeScale = false;

    private static final int LEFT_MOUSE_BUTTON = 0;
    private static final int RIGHT_MOUSE_BUTTON = 1;
    private int lastMouseX;

    public ExtraPlayerConfigScreen() {
        this.posX = Config.PLAYER_POS_X;
        this.posY = Config.PLAYER_POS_Y;
        this.scale = (float) Config.PLAYER_SCALE;
        this.yawOffset = (float) Config.PLAYER_YAW_OFFSET;
    }

    @Override
    public void drawScreen(int pMouseX, int pMouseY, float pPartialTick) {
        int startX = this.posX;
        int startY = this.posY;
        int endX = (int) (startX + this.scale * 1);
        int endY = (int) (startY + this.scale * 2);

        this.drawVerticalLine(width / 2 - 1, -2, height + 2, 0x9FFFFFFF);
        this.drawHorizontalLine(-2, width + 2, height / 2 - 1, 0x9FFFFFFF);

        this.drawVerticalLine(10, -2, height + 2, 0x9FFFFFFF);
        this.drawVerticalLine(width - 10, -2, height + 2, 0x9FFFFFFF);
        this.drawHorizontalLine(-2, width + 2, 10, 0x9FFFFFFF);
        this.drawHorizontalLine(-2, width + 2, height - 10, 0x9FFFFFFF);

        this.drawVerticalLine(startX, startY, endY, 0xFFFF0000);
        this.drawVerticalLine(endX, startY, endY, 0xFFFF0000);
        this.drawHorizontalLine(startX, endX, startY, 0xFFFF0000);
        this.drawHorizontalLine(startX, endX, endY, 0xFFFF0000);

        this.drawGradientRect(startX, startY, endX, endY, 0x4FFFFFFF, 0x4FFFFFFF);

        this.drawGradientRect(startX - 5, startY - 5, startX + 5, startY + 5, 0xFF00FF9F, 0xFF00FF9F);
        this.drawGradientRect(endX - 5, endY - 5, endX + 5, endY + 5, 0xFF00009F, 0xFF00009F);

        int y = 15;
        String component = I18n.format("gui.yes_steve_model.extra_player_render.tips");
        List<String> split = this.fontRenderer.listFormattedStringToWidth(component, 500);
        for (String charSequence : split) {
            int w = this.fontRenderer.getStringWidth(charSequence);
            this.drawString(this.fontRenderer, charSequence, width - 15 - w, y, 0xFFFFFF);
            y += 10;
        }

        if (this.mc.player != null) {
            RenderUtil.renderPlayerEntity(this.mc.player, this.posX, this.posY, this.scale, this.yawOffset, 50, pPartialTick);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        boolean xIn = this.posX - 5 < mouseX && mouseX < this.posX + 5;
        boolean yIn = this.posY - 5 < mouseY && mouseY < this.posY + 5;
        if (button == LEFT_MOUSE_BUTTON && xIn && yIn) {
            this.isChangePos = true;
        }
        int endX = (int) (this.posX + this.scale * 1);
        int endY = (int) (this.posY + this.scale * 2);
        boolean xIn2 = endX - 5 < mouseX && mouseX < endX + 5;
        boolean yIn2 = endY - 5 < mouseY && mouseY < endY + 5;
        if (button == LEFT_MOUSE_BUTTON && xIn2 && yIn2) {
            this.isChangeScale = true;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.isChangePos = false;
        this.isChangeScale = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {
        if (isChangeScale) {
            double scale1 = mouseX - this.posX;
            double scale2 = (double) (mouseY - this.posY) / 2;
            this.scale = (float) Math.min(scale1, scale2);
        }
        if (isChangePos) {
            this.posX = mouseX;
            this.posY = mouseY;
        }
        float dragX = mouseX - this.lastMouseX;
        if (button == RIGHT_MOUSE_BUTTON) {
            this.yawOffset += (dragX * 2);
        }
        this.lastMouseX = mouseX;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (Character.toLowerCase(typedChar) == RESET_KEY && isAltKeyDown()) {
            this.posX = 10;
            this.posY = 10;
            this.scale = 40;
            this.yawOffset = 5;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        Config.PLAYER_POS_X = this.posX;
        Config.PLAYER_POS_Y = this.posY;
        Config.PLAYER_SCALE = this.scale;
        Config.PLAYER_YAW_OFFSET = this.yawOffset;
        Config.save();
        super.onGuiClosed();
    }
}