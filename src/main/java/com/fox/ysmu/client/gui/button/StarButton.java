package com.fox.ysmu.client.gui.button;

import com.fox.ysmu.capability.Capabilities;
import com.fox.ysmu.network.PacketHandler;
import com.fox.ysmu.network.message.SetStarModel;
import com.fox.ysmu.YesSteveModel;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class StarButton extends FlatColorButton {
    private final static ResourceLocation ICON = new ResourceLocation(YesSteveModel.MOD_ID, "texture/icon.png");

    public StarButton(int id, int x, int y) {
        super(id, x, y, 20, 20, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        super.drawButton(mc, mouseX, mouseY, partialTicks);
        int startX = (this.width - 16) / 2;
        int startY = (this.height - 16) / 2;
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            Capabilities.getModelInfoCap(player).ifPresent(modelInfoCap -> Capabilities.getStarModelsCap(player).ifPresent(starModelsCap -> {
                ResourceLocation modelId = modelInfoCap.getModelId();
                if (starModelsCap.containModel(modelId)) {
                    mc.getTextureManager().bindTexture(ICON);
                    this.drawTexturedModalRect(this.x + startX, this.y + startY, 16, 0, 16, 16);
                } else {
                    mc.getTextureManager().bindTexture(ICON);
                    this.drawTexturedModalRect(this.x + startX, this.y + startY, 0, 0, 16, 16);
                }
            }));
        }
    }

    public void doPress() {
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
