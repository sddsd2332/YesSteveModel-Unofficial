package com.fox.ysmu.client.gui;

import com.fox.ysmu.Config;
import com.fox.ysmu.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ExtraPlayerScreen {

    public static void render(RenderGameOverlayEvent.Text event) {
        if (Config.DISABLE_PLAYER_RENDER) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        if (minecraft.currentScreen instanceof ExtraPlayerConfigScreen) {
            return;
        }
        double posX = Config.PLAYER_POS_X;
        double posY = Config.PLAYER_POS_Y;
        float scale = (float) Config.PLAYER_SCALE;
        float yawOffset = (float) Config.PLAYER_YAW_OFFSET;
        RenderUtil.renderPlayerEntity(player, posX, posY, scale, yawOffset, -500, event.getPartialTicks());
    }
}
