package com.fox.ysmu.client.input;

import com.fox.ysmu.Config;
import com.fox.ysmu.client.gui.DisclaimerScreen;
import com.fox.ysmu.client.gui.PlayerModelScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class PlayerModelScreenKey {
    public static final KeyBinding PLAYER_MODEL_KEY = new KeyBinding(
            "key.yes_steve_model.player_model.desc",
            KeyConflictContext.IN_GAME,
            KeyModifier.ALT,
            Keyboard.KEY_Y,
            "key.category.yes_steve_model"
    );

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
        if (PLAYER_MODEL_KEY.isPressed()) {
            if (Config.DISCLAIMER_SHOW) {
                Minecraft.getMinecraft().displayGuiScreen(new DisclaimerScreen());
            } else {
                Minecraft.getMinecraft().displayGuiScreen(new PlayerModelScreen());
            }
        }
    }
}
