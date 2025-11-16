package com.fox.ysmu.client.input;

import com.fox.ysmu.client.gui.ExtraPlayerConfigScreen;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class ExtraPlayerConfigKey {
    public static final KeyBinding EXTRA_PLAYER_RENDER_KEY =
        new KeyBinding("key.yes_steve_model.open_extra_player_render.desc", Keyboard.KEY_P, "key.category.yes_steve_model");

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
      //  boolean isAltKeyDown = Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
        if (EXTRA_PLAYER_RENDER_KEY.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new ExtraPlayerConfigScreen());
        }
    }
}
