package com.fox.ysmu.client.gui.button;

import com.fox.ysmu.capability.Capabilities;
import com.fox.ysmu.capability.ModelInfoCapability;
import com.fox.ysmu.client.ClientModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class TextureCountButton extends FlatColorButton {
    public TextureCountButton(int id, int x, int y) {
        super(id, x, y, 20, 20, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        // 在绘制前更新显示文本
        this.updateDisplayString();
        // 调用父类方法进行绘制
        super.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    private void updateDisplayString() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            if (player.hasCapability(Capabilities.MODEL_INFO_CAP, null)) {
                ModelInfoCapability cap = player.getCapability(Capabilities.MODEL_INFO_CAP, null);
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
