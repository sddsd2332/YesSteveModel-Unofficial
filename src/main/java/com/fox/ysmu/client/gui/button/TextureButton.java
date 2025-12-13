package com.fox.ysmu.client.gui.button;

import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.capability.Capabilities;
import com.fox.ysmu.network.message.SetModelAndTexture;
import com.fox.ysmu.util.Keep;
import com.fox.ysmu.util.ModelIdUtil;
import com.fox.ysmu.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.List;

public class TextureButton extends Button {
    private final ResourceLocation modelId;
    private final ResourceLocation textureId;
    private final String name;
    private final EntityPlayer player;

    public TextureButton(int pX, int pY, ResourceLocation modelId, ResourceLocation textureId, EntityPlayer player) {
        super(pX, pY, 54, 102, "", (b) -> {
        });
        this.modelId = modelId;
        this.textureId = textureId;
        this.name = ModelIdUtil.getSubNameFromId(textureId);
        this.player = player;
    }

    @Override
    @Keep
    public void onPress() {
        Capabilities.getModelInfoCap(player).ifPresent(cap -> {
            cap.setModelAndTexture(modelId, textureId);
            if (player.equals(Minecraft.getMinecraft().player)) {
                YesSteveModel.packetHandler.sendToServer(new SetModelAndTexture(modelId, textureId));
            }
        });
    }

    @Override
    @Keep
    public void renderWidget(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTick) {
        FontRenderer font = mc.fontRenderer;

        this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF_434242, 0xFF_434242);
        RenderUtil.scissor(this.x, this.y, this.width, this.height - 20);
        RenderUtil.renderEntityInInventory(this.x + this.width / 2, this.y + this.height / 2 + 24, 35, mc.player, modelId, textureId);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        List<String> split = font.listFormattedStringToWidth(name, 50);
        if (split.size() > 1) {
            this.drawCenteredString(font, split.get(0), this.x + this.width / 2, this.y + this.height - 19, 0xF3EFE0);
            this.drawCenteredString(font, split.get(1), this.x + this.width / 2, this.y + this.height - 10, 0xF3EFE0);
        } else {
            this.drawCenteredString(font, name, this.x + this.width / 2, this.y + this.height - 15, 0xF3EFE0);
        }
        if (this.isMouseOver()) {
            this.drawGradientRect(this.x, this.y + 1, this.x + 1, this.y + this.height - 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x + this.width - 1, this.y + 1, this.x + this.width, this.y + this.height - 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0xff_F3EFE0, 0xff_F3EFE0);
        }
    }
}
