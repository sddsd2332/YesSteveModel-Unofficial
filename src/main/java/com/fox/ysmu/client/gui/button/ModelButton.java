package com.fox.ysmu.client.gui.button;

import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.capability.StarModelsCapability;
import com.fox.ysmu.capability.StarModelsCapabilityProvider;
import com.fox.ysmu.event.CapabilityEvent;
import com.fox.ysmu.network.message.SetModelAndTexture;
import com.fox.ysmu.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.List;

public class ModelButton extends Button {
    private final static ResourceLocation ICON = new ResourceLocation(YesSteveModel.MOD_ID, "texture/icon.png");
    private final Pair<ResourceLocation, List<ResourceLocation>> modelInfo;
    private final boolean needAuth;
    private final int color;
    private final List<String> tooltips;
    private final EntityPlayer player;

    public ModelButton(int pX, int pY, boolean needAuth, Pair<ResourceLocation, List<ResourceLocation>> modelInfo, List<String> tooltips, EntityPlayer player) {
        super(pX, pY, 52, 90, modelInfo.getLeft().getPath(), (b) -> {
        });
        this.modelInfo = modelInfo;
        this.needAuth = needAuth;
        this.color = needAuth ? 0x7F_000000 : 0xFF_434242;
        this.tooltips = tooltips;
        this.player = player;
    }

    @Override
    public void onPress() {
        if (needAuth) {
            return;
        }
        CapabilityEvent.getModelInfoCap(player).ifPresent(cap -> cap.setModelAndTexture(modelInfo.getLeft(), modelInfo.getRight().get(0)));
        EntityPlayerSP localPlayer = Minecraft.getMinecraft().player;
        if (player.equals(localPlayer)) {
            YesSteveModel.packetHandler.sendToServer(new SetModelAndTexture(modelInfo.getLeft(), modelInfo.getRight().get(0)));
        }
    }

    @Override
    public void renderWidget(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTick) {
        FontRenderer font = mc.fontRenderer;

        this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, this.color, this.color);
        RenderUtil.scissor(this.x, this.y, this.width, this.height - 20);
        RenderUtil.renderEntityInInventory(this.x + this.width / 2, this.y + this.height / 2 + 20, 30, player, modelInfo.getLeft(), modelInfo.getRight().get(0));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        List<String> split = font.listFormattedStringToWidth(this.displayString, 45);
        if (split.size() > 1) {
            this.drawCenteredString(font, split.get(0), this.x + this.width / 2, this.y + this.height - 19, 0xF3EFE0);
            this.drawCenteredString(font, split.get(1), this.x + this.width / 2, this.y + this.height - 10, 0xF3EFE0);
        } else {
            this.drawCenteredString(font, this.displayString, this.x + this.width / 2, this.y + this.height - 15, 0xF3EFE0);
        }
        if (!this.needAuth && this.isMouseOver()) {
            this.drawGradientRect(this.x, this.y + 1, this.x + 1, this.y + this.height - 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x + this.width - 1, this.y + 1, this.x + this.width, this.y + this.height - 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0xff_F3EFE0, 0xff_F3EFE0);
        }

        // 收藏图标
        if (player.hasCapability(StarModelsCapabilityProvider.STAR_MODELS_CAP, null)) {
            StarModelsCapability cap = player.getCapability(StarModelsCapabilityProvider.STAR_MODELS_CAP, null);
            if (cap != null && cap.containModel(modelInfo.getLeft())) {
                mc.getTextureManager().bindTexture(ICON);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.drawTexturedModalRect(this.x + this.width - 14, this.y, 16, 0, 16, 16);
            }
        }

        if (needAuth) {
            this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x9f_222222, 0x9f_222222);
        }
    }

    public void renderComponentTooltip(GuiScreen screen, int pMouseX, int pMouseY) {
        if (this.isMouseOver() && tooltips != null) {
            screen.drawHoveringText(tooltips, pMouseX, pMouseY);
        }
    }


    @Override
    public boolean mousePressed(@Nonnull Minecraft mc, int mouseX, int mouseY) {
        return !this.needAuth && super.mousePressed(mc, mouseX, mouseY);
    }
}
