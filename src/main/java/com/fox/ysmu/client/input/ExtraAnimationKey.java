package com.fox.ysmu.client.input;

import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.network.PacketHandler;
import com.fox.ysmu.network.message.SetPlayAnimation;
import com.google.common.collect.Lists;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.List;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class ExtraAnimationKey {
    public static final List<KeyBinding> EXTRA_ANIMATION_KEYS = Lists.newArrayList();

    public static void registerKeyBindings() {
        for (int i = 0; i <= 7; i++) {
            String name = String.format("key.yes_steve_model.extra_animation.%d.desc", i);
            KeyBinding keyMapping = new KeyBinding(
                    name,
                    KeyConflictContext.IN_GAME,
                    KeyModifier.NONE,
                    Keyboard.KEY_NONE,
                    "key.category.yes_steve_model"
            );
            ClientRegistry.registerKeyBinding(keyMapping);
            EXTRA_ANIMATION_KEYS.add(keyMapping);
        }
    }

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
        for (KeyBinding key : EXTRA_ANIMATION_KEYS) {
            if (key.isPressed()) {
                YesSteveModel.packetHandler.sendToServer(new SetPlayAnimation(EXTRA_ANIMATION_KEYS.indexOf(key)));
                return;
            }
        }
    }
}
