package com.fox.ysmu.client.gui.button;

import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.capability.Capabilities;
import com.fox.ysmu.network.message.SetStarModel;
import com.fox.ysmu.util.Keep;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class StarButton extends FlatColorButton {
    private final static ResourceLocation ICON = new ResourceLocation(YesSteveModel.MOD_ID, "texture/icon.png");

    public StarButton(int x, int y) {
        super(x, y, 20, 20, "", (b) -> {
        });
    }

    @Override
    @Keep
    public void renderWidget(@Nonnull Minecraft mc, int mouseX, int mouseY, float pPartialTick) {
        super.renderWidget(mc, mouseX, mouseY, pPartialTick);
        int startX = (this.width - 16) / 2;
        int startY = (this.height - 16) / 2;
        EntityPlayer player = mc.player;
        if (player != null) {
            Capabilities.getModelInfoCap(player).ifPresent(modelInfoCap -> Capabilities.getStarModelsCap(player).ifPresent(starModelsCap -> {
                ResourceLocation modelId = modelInfoCap.getModelId();
                mc.getTextureManager().bindTexture(ICON);
                if (starModelsCap.containModel(modelId)) {
                    drawScaledCustomSizeModalRect(this.x + startX, this.y + startY, 16, 0, 16, 16, 16, 16, 256, 256);
                } else {
                    drawScaledCustomSizeModalRect(this.x + startX, this.y + startY, 0, 0, 16, 16, 16, 16, 256, 256);
                }
            }));
        }
    }

    @Override
    @Keep
    public void onPress() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            Capabilities.getModelInfoCap(player).ifPresent(modelInfoCap -> Capabilities.getStarModelsCap(player).ifPresent(starModelsCap -> {
                ResourceLocation modelId = modelInfoCap.getModelId();
                if (starModelsCap.containModel(modelId)) {
                    starModelsCap.removeModel(modelId);
                    YesSteveModel.packetHandler.sendToServer(SetStarModel.remove(modelId));
                } else {
                    starModelsCap.addModel(modelId);
                    YesSteveModel.packetHandler.sendToServer(SetStarModel.add(modelId));
                }
            }));
        }
    }
}
