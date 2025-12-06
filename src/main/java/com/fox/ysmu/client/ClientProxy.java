package com.fox.ysmu.client;

import com.fox.ysmu.CommonProxy;
import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.client.animation.AnimationRegister;
import com.fox.ysmu.client.entity.CustomPlayerEntity;
import com.fox.ysmu.client.input.AnimationRouletteKey;
import com.fox.ysmu.client.input.ExtraAnimationKey;
import com.fox.ysmu.client.input.ExtraPlayerConfigKey;
import com.fox.ysmu.client.input.PlayerModelScreenKey;
import com.fox.ysmu.client.renderer.CustomPlayerRenderer;
import com.fox.ysmu.client.renderer.ReplacePlayerHandRenderEvent;
import com.fox.ysmu.geckolib3.geo.GeoReplacedEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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

    @Override
    public EntityPlayer getPlayer(MessageContext context) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            return context.getServerHandler().player;
        }
        return Minecraft.getMinecraft().player;
    }

    @Override
    public void handlePacket(Runnable runnable, EntityPlayer player) {
        if (player == null || player.world.isRemote) {
            Minecraft.getMinecraft().addScheduledTask(runnable);
        } else {
            //Single player
            if (player.world instanceof WorldServer server) {
                server.addScheduledTask(runnable);
            } else {
                MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                if (server != null) {
                    server.addScheduledTask(runnable);
                } else {
                    YesSteveModel.LOGGER.error("Packet handler wanted to set a scheduled task, but we couldn't find a way to set one.");
                    YesSteveModel.LOGGER.error("Player = {}, World = {}", player, player.world);
                }
            }
        }
    }


}
