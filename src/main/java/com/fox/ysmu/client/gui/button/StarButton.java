package com.fox.ysmu.client.gui.button;

import com.fox.ysmu.capabilities.Capabilities;
import com.fox.ysmu.capabilities.ModelInfoCapability;
import com.fox.ysmu.capabilities.StarModelsCapability;
import com.fox.ysmu.network.NetworkHandler;
import com.fox.ysmu.network.message.SetStarModel;
import com.fox.ysmu.ysmu;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class StarButton extends FlatColorButton {
    private final static ResourceLocation ICON = new ResourceLocation(ysmu.MODID, "texture/icon.png");

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
            if (player.hasCapability(Capabilities.STAR_MODELS_CAP, null) && player.hasCapability(Capabilities.MODEL_INFO_CAP, null)) {
                ModelInfoCapability modelInfoEEP = player.getCapability(Capabilities.MODEL_INFO_CAP, null);
                StarModelsCapability starModelsEEP = player.getCapability(Capabilities.STAR_MODELS_CAP, null);
                if (modelInfoEEP != null && starModelsEEP != null) {
                    ResourceLocation modelId = modelInfoEEP.getModelId();
                    if (starModelsEEP.containModel(modelId)) {
                        mc.getTextureManager().bindTexture(ICON);
                        this.drawTexturedModalRect(this.x + startX, this.y + startY, 16, 0, 16, 16);
                    } else {
                        mc.getTextureManager().bindTexture(ICON);
                        this.drawTexturedModalRect(this.x + startX, this.y + startY, 0, 0, 16, 16);
                    }
                }
            }
        }
    }

    public void doPress() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            if (player.hasCapability(Capabilities.STAR_MODELS_CAP, null) && player.hasCapability(Capabilities.MODEL_INFO_CAP, null)) {
                ModelInfoCapability modelInfoCap = player.getCapability(Capabilities.MODEL_INFO_CAP, null);
                StarModelsCapability starModelsCap = player.getCapability(Capabilities.STAR_MODELS_CAP, null);
                if (modelInfoCap != null && starModelsCap != null) {
                    ResourceLocation modelId = modelInfoCap.getModelId();
                    if (starModelsCap.containModel(modelId)) {
                        starModelsCap.removeModel(modelId);
                        NetworkHandler.CHANNEL.sendToServer(SetStarModel.remove(modelId));
                    } else {
                        starModelsCap.addModel(modelId);
                        NetworkHandler.CHANNEL.sendToServer(SetStarModel.add(modelId));
                    }
                }
            }
        }
    }
}
