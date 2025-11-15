package com.fox.ysmu.client.gui;

import com.fox.ysmu.Config;
import com.fox.ysmu.client.ClientModelManager;
import com.fox.ysmu.client.input.ExtraAnimationKey;
import com.fox.ysmu.eep.ExtendedModelInfo;
import com.fox.ysmu.network.NetworkHandler;
import com.fox.ysmu.network.message.SetPlayAnimation;
import com.fox.ysmu.util.ModelIdUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class AnimationRouletteScreen extends GuiScreen {
    private int x;
    private int y;
    private int selectId = -1;
    private String[] names;

    @Override
    public void initGui() {
        this.x = width / 2;
        this.y = height / 2 - 8;

        if (mc != null && mc.player != null) {
            ExtendedModelInfo eep = ExtendedModelInfo.get(mc.player);
            if (eep != null) {
                ResourceLocation modelId = eep.getModelId();
                if (ClientModelManager.EXTRA_ANIMATION_NAME.containsKey(ModelIdUtil.getMainId(modelId))) {
                    this.names = ClientModelManager.EXTRA_ANIMATION_NAME.get(ModelIdUtil.getMainId(modelId));
                }
            }
        }
    }

    @Override
    public void drawScreen(int pMouseX, int pMouseY, float pPartialTick) {
        drawRoulette(pMouseX, pMouseY);
        drawRouletteText();
    }

    @Override
    protected void mouseClicked(int pMouseX, int pMouseY, int pButton) throws IOException {
        if (-1 < selectId && selectId < 8 && mc != null) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            NetworkHandler.CHANNEL.sendToServer(new SetPlayAnimation(selectId));
            if (mc.player != null && Config.PRINT_ANIMATION_ROULETTE_MSG) {
                mc.player.sendMessage(new TextComponentTranslation("message.yes_steve_model.model.animation_roulette.play", selectId));
            }
            mc.displayGuiScreen(null);
        }
        super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void drawRouletteText() {
        int count = 8;
        float startDeg = (float) (Math.PI / count);
        for (int i = 0; i < count; i++) {
            int r = 65;
            TextComponentString keyText = new TextComponentString("[ ");
            keyText.getStyle().setColor(TextFormatting.YELLOW);
            KeyBinding keyMapping = ExtraAnimationKey.EXTRA_ANIMATION_KEYS.get(i);
            if (keyMapping.getKeyCode() == Keyboard.KEY_NONE) {
                keyText.appendSibling(new TextComponentTranslation("key.yes_steve_model.extra_animation.none"));
            } else {
                keyText.appendSibling(new TextComponentString(keyMapping.getKeyDescription()));
            }
            keyText.appendSibling(new TextComponentString(" ]"));
            int textX = (int) (x + r * MathHelper.cos(startDeg));
            int textY = (int) (y + r * MathHelper.sin(startDeg) - (float) this.fontRenderer.FONT_HEIGHT / 2);
            if (this.names != null && this.names.length > i && StringUtils.isNoneBlank(this.names[i])) {
                this.drawCenteredString(fontRenderer, this.names[i], textX, textY - 8, 0xF3EFE0);
            } else {
                this.drawCenteredString(fontRenderer, String.valueOf(i), textX, textY - 8, 0xF3EFE0);
            }
            this.drawCenteredString(fontRenderer, keyText.getFormattedText(), textX, textY + 4, 0xF3EFE0);
            startDeg = (float) (startDeg + 2 * Math.PI / count);
        }
    }

    private void drawRoulette(int mouseX, int mouseY) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tessellator = Tessellator.getInstance();

        int count = 8;
        float theta = (float) Math.atan2(mouseY - y, mouseX - x);
        if (theta < 0) {
            theta = (float) (Math.PI * 2 + theta);
        }
        float distance = MathHelper.sqrt((mouseY - y) * (mouseY - y) + (mouseX - x) * (mouseX - x));
        boolean isSelected = false;
        for (int i = 0; i < count; i++) {
            float spacingDeg = (float) (Math.PI / 90);
            float startDeg = (float) ((2 * Math.PI / count) * i + spacingDeg);
            float endDeg = (float) ((2 * Math.PI / count) * (i + 1) - spacingDeg);
            if (startDeg < theta && theta < endDeg && 50 < distance && distance < 100) {
                drawFan(tessellator, 25, 105, startDeg, endDeg, 0xf0FFB100);
                isSelected = true;
                this.selectId = i;
            } else {
                drawFan(tessellator, 25, 105, startDeg, endDeg, 0x90000000);
            }
        }
        if (!isSelected) {
            this.selectId = -1;
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void drawFan(Tessellator tessellator, float rIn, float rOut, float startDeg, float endDeg, int color) {
        float alpha = (color >> 24 & 255) / 255.0F;
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        bufferBuilder.color(red, green, blue, alpha);
        bufferBuilder.pos(x + rOut * MathHelper.cos(startDeg), y + rOut * MathHelper.sin(startDeg), 0).endVertex();
        bufferBuilder.pos(x + rIn * MathHelper.cos(startDeg), y + rIn * MathHelper.sin(startDeg), 0).endVertex();
        bufferBuilder.pos(x + rIn * MathHelper.cos(endDeg), y + rIn * MathHelper.sin(endDeg), 0).endVertex();
        bufferBuilder.pos(x + rOut * MathHelper.cos(endDeg), y + rOut * MathHelper.sin(endDeg), 0).endVertex();
        tessellator.draw();
    }
}
