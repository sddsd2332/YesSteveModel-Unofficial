package com.fox.ysmu.client;

import com.fox.ysmu.CommonProxy;
import com.fox.ysmu.capabilities.Capabilities;
import com.fox.ysmu.client.animation.AnimationRegister;
import com.fox.ysmu.client.entity.CustomPlayerEntity;
import com.fox.ysmu.client.input.*;
import com.fox.ysmu.client.renderer.CustomPlayerRenderer;
import com.fox.ysmu.capabilities.AuthModelsCapability;
import com.fox.ysmu.capabilities.StarModelsCapability;
import com.fox.ysmu.network.message.SyncAuthModels;
import com.fox.ysmu.network.message.SyncStarModels;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import com.fox.ysmu.geckolib3.geo.GeoReplacedEntityRenderer;

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
        ClientRegistry.registerKeyBinding(DebugAnimationKey.DEBUG_ANIMATION_KEY);
        ExtraAnimationKey.registerKeyBindings();
        ClientRegistry.registerKeyBinding(ExtraPlayerConfigKey.EXTRA_PLAYER_RENDER_KEY);
        ClientRegistry.registerKeyBinding(PlayerModelScreenKey.PLAYER_MODEL_KEY);
    }

    public static CustomPlayerRenderer getInstance() {
        return CUSTOM_PLAYER_RENDERER;
    }

    @Override
    public void handleAuthModels(SyncAuthModels message) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player != null) {
            if (player.hasCapability(Capabilities.AUTH_MODELS_CAP, null)) {
                AuthModelsCapability eep = player.getCapability(Capabilities.AUTH_MODELS_CAP, null);
                if (eep != null) {
                    eep.setAuthModels(message.getAuthModels());
                }
            }
        }
    }

    @Override
    public void handleStarModels(SyncStarModels message) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player != null) {
            if (player.hasCapability(Capabilities.STAR_MODELS_CAP, null)) {
                StarModelsCapability eep = player.getCapability(Capabilities.STAR_MODELS_CAP, null);
                if (eep != null) {
                    eep.setStarModels(message.getStarModels());
                }
            }
        }
    }

}
