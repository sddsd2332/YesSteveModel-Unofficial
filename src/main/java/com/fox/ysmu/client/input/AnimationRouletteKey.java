package com.fox.ysmu.client.input;

import com.fox.ysmu.client.gui.AnimationRouletteScreen;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class AnimationRouletteKey {
    public static final KeyBinding ANIMATION_ROULETTE_KEY =
        new KeyBinding("key.yes_steve_model.animation_roulette.desc", Keyboard.KEY_Z, "key.category.yes_steve_model");

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
        if (ANIMATION_ROULETTE_KEY.isKeyDown()) {
            Minecraft.getMinecraft().displayGuiScreen(new AnimationRouletteScreen());
        }
    }
}
