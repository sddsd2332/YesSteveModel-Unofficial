package com.fox.ysmu.client.input;


import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class DebugAnimationKey {
    public static boolean DEBUG = false;

    public static final KeyBinding DEBUG_ANIMATION_KEY =
            new KeyBinding("key.yes_steve_model.debug_animation.desc", Keyboard.KEY_B, "key.category.yes_steve_model");

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
      //  boolean isAltKeyDown = Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
        if (DEBUG_ANIMATION_KEY.isKeyDown()) {
            DEBUG = !DEBUG;
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player == null) {
                return;
            }
            if (DEBUG) {
                player.sendMessage(new TextComponentString(I18n.format("message.yes_steve_model.model.debug_animation.true")));
            } else {
                player.sendMessage(new TextComponentString(I18n.format("message.yes_steve_model.model.debug_animation.false")));
            }
        }
    }
}
