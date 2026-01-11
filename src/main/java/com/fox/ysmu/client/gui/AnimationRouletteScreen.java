package com.fox.ysmu.client.gui;

import com.fox.ysmu.Config;
import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.client.ClientModelManager;
import com.fox.ysmu.client.input.ExtraAnimationKey;
import com.fox.ysmu.event.CapabilityEvent;
import com.fox.ysmu.network.message.SetPlayAnimation;
import com.fox.ysmu.util.ModelIdUtil;
import com.github.bsideup.jabel.Desugar;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnimationRouletteScreen extends GuiScreen {
    private int selectId = -1;
    private String[] names;


    private static final float DRAWS = 300;

    private static final float INNER = 40, OUTER = 100;
    private static final float MIDDLE_DISTANCE = (INNER + OUTER) / 2F;
    private static final float SELECT_RADIUS = 10, SELECT_RADIUS_WITH_PARENT = 20;

    @Override
    public void initGui() {
        if (this.mc != null && this.mc.player != null) {
            CapabilityEvent.getModelInfoCap(this.mc.player).ifPresent(cap -> {
                ResourceLocation modelId = cap.getModelId();
                if (ClientModelManager.EXTRA_ANIMATION_NAME.containsKey(ModelIdUtil.getMainId(modelId))) {
                    this.names = ClientModelManager.EXTRA_ANIMATION_NAME.get(ModelIdUtil.getMainId(modelId));
                }
            });
        }
    }

    @Override
    public void drawScreen(int pMouseX, int pMouseY, float pPartialTick) {
        float centerX = this.width / 2F;
        float centerY = this.height / 2F;
        render(pMouseX, pMouseY, centerX, centerY);
    }


    private void render(int mouseX, int mouseY, float centerX, float centerY) {
        int activeModes = 8;
        float angleSize = 360F / activeModes;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.translate(centerX, centerY, 0);
        GlStateManager.disableTexture2D();

        // draw base
        // Note: While there might be slightly better performance only drawing part of the Torus given
        // other bits may be drawn by hovering or current selection, it is not practical to do so due
        // to floating point precision causing some values to have gaps in the torus, and also the light
        // colors occasionally being harder to see without the added back layer torus
        GlStateManager.color(0.3F, 0.3F, 0.3F, 0.5F);
        drawTorus(0, 360);

        // Draw current hovered selection and selection highlighter
        double xDiff = mouseX - centerX;
        double yDiff = mouseY - centerY;
        double distanceFromCenter = length(xDiff, yDiff);
        if (distanceFromCenter > SELECT_RADIUS) {
            // draw mouse selection highlight
            float angle = (float) (RAD_TO_DEG * MathHelper.atan2(yDiff, xDiff));
            float modeSize = 180F / activeModes;
            GlStateManager.color(0.8F, 0.8F, 0.8F, 0.3F);
            drawTorus(angle - modeSize, angleSize);

            float selectionAngle = wrapDegrees(angle + modeSize + 90F);
            int selectionDrawnPos = (int) (selectionAngle * (activeModes / 360F));
            this.selectId = selectionDrawnPos;

            // draw hovered selection
            GlStateManager.color(0.6F, 0.6F, 0.6F, 0.7F);
            drawTorus(-90F + 360F * (-0.5F + selectionDrawnPos) / activeModes, angleSize);
        } else {
            this.selectId = -1;
        }

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableTexture2D();

        List<PositionedText> textToDraw = new ArrayList<>();

        int position = 0;
        for (int i = 0; i < activeModes; i++) {
            float degrees = 270 + 360 * ((float) position++ / activeModes);
            float angle = DEG_TO_RAD * degrees;
            float x = MathHelper.cos(angle) * MIDDLE_DISTANCE;
            float y = MathHelper.sin(angle) * MIDDLE_DISTANCE;
            TextComponentString keyText = new TextComponentString("[ ");
            keyText.getStyle().setColor(TextFormatting.YELLOW);
            KeyBinding keyMapping = ExtraAnimationKey.EXTRA_ANIMATION_KEYS.get(i);
            if (keyMapping.getKeyCode() == Keyboard.KEY_NONE) {
                keyText.appendSibling(new TextComponentTranslation("key.yes_steve_model.extra_animation.none"));
            } else {
                keyText.appendSibling(new TextComponentString(keyMapping.getDisplayName()));
            }
            keyText.appendSibling(new TextComponentString(" ]"));
            textToDraw.add(new PositionedText(x, y - 2, keyText));

            TextComponentString desc = new TextComponentString("");
            desc.getStyle().setColor(TextFormatting.YELLOW);
            if (this.names != null && this.names.length > i && StringUtils.isNoneBlank(this.names[i])) {
                desc.appendSibling(new TextComponentTranslation(names[i]));
            } else {
                desc.appendSibling(new TextComponentTranslation("key.yes_steve_model.extra_animation." + i + ".desc"));
            }
            textToDraw.add(new PositionedText(x, y - 8, desc));
        }
        textToDraw.forEach(toDraw -> {
            GlStateManager.pushMatrix();
            GlStateManager.translate(toDraw.x, toDraw.y, 0);
            GlStateManager.scale(0.6F, 0.6F, 0.6F);
            ITextComponent text = toDraw.text;
            drawString(fontRenderer, text.getFormattedText(), -fontRenderer.getStringWidth(text.getFormattedText()) / 2F, 8, 0xCCFFFFFF, true);
            GlStateManager.popMatrix();
        });

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.popMatrix();
    }


    @Desugar
    record PositionedText(float x, float y, ITextComponent text) {
    }

    @Override
    protected void mouseClicked(int pMouseX, int pMouseY, int pButton) throws IOException {
        if (-1 < selectId && selectId < 8 && mc != null) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            YesSteveModel.packetHandler.sendToServer(new SetPlayAnimation(selectId));
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

    private void drawTorus(float startAngle, float sizeAngle) {
        drawTorus(startAngle, sizeAngle, INNER, OUTER);
    }

    private void drawTorus(float startAngle, float sizeAngle, float inner, float outer) {
        BufferBuilder vertexBuffer = Tessellator.getInstance().getBuffer();
        vertexBuffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION);
        float draws = DRAWS * (sizeAngle / 360F);
        for (int i = 0; i <= draws; i++) {
            float degrees = startAngle + (i / DRAWS) * 360;
            float angle = DEG_TO_RAD * degrees;
            float cos = MathHelper.cos(angle);
            float sin = MathHelper.sin(angle);
            vertexBuffer.pos(outer * cos, outer * sin, 0).endVertex();
            vertexBuffer.pos(inner * cos, inner * sin, 0).endVertex();
        }
        Tessellator.getInstance().draw();
    }

    public static final float DEG_TO_RAD = ((float) Math.PI / 180F);

    public static final float RAD_TO_DEG = (180F / (float) Math.PI);

    public static double length(double pXDistance, double pYDistance) {
        return Math.sqrt(lengthSquared(pXDistance, pYDistance));
    }

    public static double lengthSquared(double pXDistance, double pYDistance) {
        return pXDistance * pXDistance + pYDistance * pYDistance;
    }

    public static float wrapDegrees(float angle) {
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public static int drawString(FontRenderer font, String component, float x, float y, int color, boolean drawShadow) {
        return font.drawString(component, x, y, color, drawShadow);
    }
}
