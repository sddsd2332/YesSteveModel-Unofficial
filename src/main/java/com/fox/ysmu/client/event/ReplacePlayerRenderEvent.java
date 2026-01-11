package com.fox.ysmu.client.event;

import com.fox.ysmu.Config;
import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.client.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = YesSteveModel.MOD_ID)
public class ReplacePlayerRenderEvent {
    @SubscribeEvent
    public static void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        EntityPlayerSP playerSelf = Minecraft.getMinecraft().player;
        if (player.equals(playerSelf) && Config.DISABLE_SELF_MODEL) {
            return;
        }
        if (!player.equals(playerSelf) && Config.DISABLE_OTHER_MODEL) {
            return;
        }
        event.setCanceled(true);
        float partialTicks = event.getPartialRenderTick();
        double ix = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double iy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double iz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        ClientProxy.getInstance().doRender(player, ix - Minecraft.getMinecraft().getRenderManager().renderPosX, iy - Minecraft.getMinecraft().getRenderManager().renderPosY, iz - Minecraft.getMinecraft().getRenderManager().renderPosZ,player.rotationYaw, partialTicks);
    }
}
