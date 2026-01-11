package com.fox.ysmu.client.event;

import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.capability.ModelInfoCapability;
import com.fox.ysmu.capability.ModelInfoCapabilityProvider;
import com.fox.ysmu.network.message.SetPlayAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = YesSteveModel.MOD_ID)
public class PlayerMoveEvent {
    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (isMoveKey() && player != null) {
            if (player.hasCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null)) {
                ModelInfoCapability eep = player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null);
                if (eep != null && eep.isPlayAnimation()) {
                    YesSteveModel.packetHandler.sendToServer(SetPlayAnimation.stop());
                }
            }
        }
    }

    private static boolean isMoveKey() {
        KeyBinding[] keyBindings = Minecraft.getMinecraft().gameSettings.keyBindings;
        for (KeyBinding keyBinding : keyBindings) {
            if ((keyBinding == Minecraft.getMinecraft().gameSettings.keyBindForward || keyBinding == Minecraft.getMinecraft().gameSettings.keyBindBack || keyBinding == Minecraft.getMinecraft().gameSettings.keyBindLeft || keyBinding == Minecraft.getMinecraft().gameSettings.keyBindRight || keyBinding == Minecraft.getMinecraft().gameSettings.keyBindJump || keyBinding == Minecraft.getMinecraft().gameSettings.keyBindSneak) && keyBinding.isPressed()) {
                return true;
            }
        }
        return false;
    }
}
