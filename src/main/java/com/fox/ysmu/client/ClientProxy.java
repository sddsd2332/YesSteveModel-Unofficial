package com.fox.ysmu.client;

import com.fox.ysmu.CommonProxy;
import com.fox.ysmu.client.animation.AnimationRegister;
import com.fox.ysmu.client.entity.CustomPlayerEntity;
import com.fox.ysmu.client.input.AnimationRouletteKey;
import com.fox.ysmu.client.input.ExtraAnimationKey;
import com.fox.ysmu.client.input.ExtraPlayerConfigKey;
import com.fox.ysmu.client.input.PlayerModelScreenKey;
import com.fox.ysmu.client.renderer.CustomPlayerRenderer;
import com.fox.ysmu.client.renderer.ReplacePlayerHandRenderEvent;
import com.fox.ysmu.geckolib3.geo.GeoReplacedEntityRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {

    private static CustomPlayerRenderer CUSTOM_PLAYER_RENDERER;

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        AnimationRegister.registerAnimationState();
        AnimationRegister.registerVariables();
        CUSTOM_PLAYER_RENDERER = new CustomPlayerRenderer();
        GeoReplacedEntityRenderer.registerReplacedEntity(CustomPlayerEntity.class, CUSTOM_PLAYER_RENDERER);
        ClientRegistry.registerKeyBinding(AnimationRouletteKey.ANIMATION_ROULETTE_KEY);
        ExtraAnimationKey.registerKeyBindings();
        ClientRegistry.registerKeyBinding(ExtraPlayerConfigKey.EXTRA_PLAYER_RENDER_KEY);
        ClientRegistry.registerKeyBinding(PlayerModelScreenKey.PLAYER_MODEL_KEY);
        MinecraftForge.EVENT_BUS.register(new ReplacePlayerHandRenderEvent());
        //  MinecraftForge.EVENT_BUS.register(new RenderFirstPlayerBackground());
    }

    public static CustomPlayerRenderer getInstance() {
        return CUSTOM_PLAYER_RENDERER;
    }


}
