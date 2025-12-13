package com.fox.ysmu.client.gui.button;

import com.fox.ysmu.capability.ModelInfoCapability;
import com.fox.ysmu.capability.ModelInfoCapabilityProvider;
import com.fox.ysmu.client.ClientModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class TextureCountButton extends FlatColorButton {
    public TextureCountButton(int x, int y) {
        super(x, y, 20, 20,"", (b) -> {
        });
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        this.updateDisplayString();
        super.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    private void updateDisplayString() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            if (player.hasCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null)) {
                ModelInfoCapability cap = player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null);
                if (cap != null) {
                    ResourceLocation modelId = cap.getModelId();
                    if (ClientModelManager.MODELS.containsKey(modelId)) {
                        this.displayString = String.valueOf(ClientModelManager.MODELS.get(modelId).size());
                        return;
                    }
                }
            }
            this.displayString = "";
        }
    }
}
