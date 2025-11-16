package com.fox.ysmu.client.gui.button;

import com.fox.ysmu.capabilities.Capabilities;
import com.fox.ysmu.eep.ModelInfoCapability;
import com.fox.ysmu.eep.StarModelsCapability;
import com.fox.ysmu.ysmu;
import com.fox.ysmu.network.NetworkHandler;
import com.fox.ysmu.network.message.OpenModelGuiMessage;
import com.fox.ysmu.network.message.SetModelAndTexture;
import com.fox.ysmu.network.message.SetNpcModelAndTexture;
import com.fox.ysmu.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ModelButton extends GuiButton {
    private final static ResourceLocation ICON = new ResourceLocation(ysmu.MODID, "texture/icon.png");
    private final Pair<ResourceLocation, List<ResourceLocation>> modelInfo;
    private final boolean needAuth;
    private final int color;
    public final List<ITextComponent> tooltips;
    private final EntityPlayer player;

    public ModelButton(int id, int pX, int pY, boolean needAuth, Pair<ResourceLocation, List<ResourceLocation>> modelInfo,
                       List<ITextComponent> tooltips, EntityPlayer player) {
        super(id, pX, pY, 52, 90, "");
        this.modelInfo = modelInfo;
        this.needAuth = needAuth;
        this.color = needAuth ? 0x7F_000000 : 0xFF_434242;
        this.tooltips = tooltips;
        this.player = player;
        this.displayString = modelInfo.getLeft().getPath();
    }

    public void doPress() {
        if (this.needAuth) {
            return;
        }
        if (player.hasCapability(Capabilities.ModelInfo, null)) {
            ModelInfoCapability eep = player.getCapability(Capabilities.ModelInfo, null);
            if (eep != null) {
                eep.setModelAndTexture(modelInfo.getLeft(), modelInfo.getRight().get(0));
            }
            if (player.equals(Minecraft.getMinecraft().player)) {
                NetworkHandler.CHANNEL.sendToServer(new SetModelAndTexture(modelInfo.getLeft(), modelInfo.getRight().get(0)));
            } else {
                NetworkHandler.CHANNEL.sendToServer(new SetNpcModelAndTexture(modelInfo.getLeft(), modelInfo.getRight().get(0), OpenModelGuiMessage.CURRENT_NPC_ID));
            }
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        FontRenderer font = mc.fontRenderer;
        // Hover状态
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        // 绘制背景(原graphics.fillGradient)
        this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, this.color, this.color);
        // 剪裁测试（缩放）
        int scale = new ScaledResolution(mc).getScaleFactor();
        int scissorX = this.x * scale;
        // 在GL11中，Y轴的原点在左下角，所以需要从屏幕总高度中减去
        int scissorY = mc.displayHeight - ((this.y + this.height - 20) * scale);
        int scissorW = this.width * scale;
        int scissorH = (this.height - 20) * scale;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorW, scissorH);
        // 渲染实体
        RenderUtil.renderEntityInInventory(this.x + this.width / 2, this.y + this.height / 2 + 20, 30, mc.player, modelInfo.getLeft(), modelInfo.getRight().get(0));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        // 渲染文本
        // font.split->listFormattedStringToWidth
        List<String> split = font.listFormattedStringToWidth(this.displayString, 45);
        if (split.size() > 1) {
            this.drawCenteredString(font, split.get(0), this.x + this.width / 2, this.y + this.height - 19, 0xF3EFE0);
            this.drawCenteredString(font, split.get(1), this.x + this.width / 2, this.y + this.height - 10, 0xF3EFE0);
        } else {
            this.drawCenteredString(font, this.displayString, this.x + this.width / 2, this.y + this.height - 15, 0xF3EFE0);
        }
        // 悬停时的高亮边框
        if (!this.needAuth && this.hovered) {
            this.drawGradientRect(this.x, this.y + 1, this.x + 1, this.y + this.height - 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x + this.width - 1, this.y + 1, this.x + this.width, this.y + this.height - 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0xff_F3EFE0, 0xff_F3EFE0);
        }
        // 收藏图标
        if (player.hasCapability(Capabilities.StarModels, null)) {
            StarModelsCapability eep = player.getCapability(Capabilities.StarModels, null);
            if (eep != null && eep.containModel(modelInfo.getLeft())) {
                // graphics.blit
                mc.getTextureManager().bindTexture(ICON);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.drawTexturedModalRect(this.x + this.width - 14, this.y, 16, 0, 16, 16);
            }
        }
        if (needAuth) {
            this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x9F222222, 0x9F222222);
        }
    }
}
