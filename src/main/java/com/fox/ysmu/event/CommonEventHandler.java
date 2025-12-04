package com.fox.ysmu.event;


import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.capability.*;
import com.fox.ysmu.model.ServerModelManager;
import com.fox.ysmu.network.NetworkHandler;
import com.fox.ysmu.network.message.SyncAuthModels;
import com.fox.ysmu.network.message.SyncModelInfo;
import com.fox.ysmu.network.message.SyncStarModels;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Optional;


public class CommonEventHandler {

    public CommonEventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private final ResourceLocation MODEL_INFO_CAP = new ResourceLocation(YesSteveModel.MOD_ID, "model_id");
    private final ResourceLocation AUTH_MODELS_CAP = new ResourceLocation(YesSteveModel.MOD_ID, "own_models");
    private final ResourceLocation STAR_MODELS_CAP = new ResourceLocation(YesSteveModel.MOD_ID, "star_models");

    @SubscribeEvent
    public void attachCaps(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer player) {
            if (!player.hasCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null) && !event.getCapabilities().containsKey(MODEL_INFO_CAP)) {
                event.addCapability(MODEL_INFO_CAP, new ModelInfoCapabilityProvider());
            }
            if (!player.hasCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP, null) && !event.getCapabilities().containsKey(AUTH_MODELS_CAP)) {
                event.addCapability(AUTH_MODELS_CAP, new AuthModelsCapabilityProvider());
            }
            if (!player.hasCapability(StarModelsCapabilityProvider.STAR_MODELS_CAP, null) && !event.getCapabilities().containsKey(STAR_MODELS_CAP)) {
                event.addCapability(STAR_MODELS_CAP, new StarModelsCapabilityProvider());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {

        Optional<ModelInfoCapability> oldModelInfoCap = Capabilities.getModelInfoCap(event.getOriginal());
        Optional<AuthModelsCapability> oldAuthModelsCap = Capabilities.getAuthModelsCap(event.getOriginal());
        Optional<StarModelsCapability> oldStarModelsCap = Capabilities.getStarModelsCap(event.getOriginal());

        Optional<ModelInfoCapability> newModelInfoCap = Capabilities.getModelInfoCap(event.getEntityPlayer());
        Optional<AuthModelsCapability> newAuthModelsCap = Capabilities.getAuthModelsCap(event.getEntityPlayer());
        Optional<StarModelsCapability> newStarModelsCap = Capabilities.getStarModelsCap(event.getEntityPlayer());

        newModelInfoCap.ifPresent((newModelInfo) -> oldModelInfoCap.ifPresent(newModelInfo::copyFrom));
        newAuthModelsCap.ifPresent((newAuthModels) -> oldAuthModelsCap.ifPresent(newAuthModels::copyFrom));
        newStarModelsCap.ifPresent((newStarModels) -> oldStarModelsCap.ifPresent(newStarModels::copyFrom));
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof EntityPlayer trackPlayer) {
            EntityPlayer player = event.getEntityPlayer();
            Capabilities.getModelInfoCap(trackPlayer).ifPresent(cap -> {
                SyncModelInfo syncMsg = new SyncModelInfo(trackPlayer.getEntityId(), cap);
                NetworkHandler.sendToClientPlayer(syncMsg, player);
            });
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer player) {
            Capabilities.getModelInfoCap(player).ifPresent(modelInfoCap -> {
                if (player instanceof EntityPlayerMP serverPlayer) {
                    Capabilities.getAuthModelsCap(player).ifPresent(authModelsCap -> {
                        NetworkHandler.sendToClientPlayer(new SyncAuthModels(authModelsCap.getAuthModels()), serverPlayer);
                        if (ServerModelManager.AUTH_MODELS.contains(modelInfoCap.getModelId().getPath()) && !authModelsCap.containModel(modelInfoCap.getModelId())) {
                            ResourceLocation defaultModelId = new ResourceLocation(YesSteveModel.MOD_ID, "default");
                            ResourceLocation defaultTextureId = new ResourceLocation(YesSteveModel.MOD_ID, "default/default.png");
                            modelInfoCap.setModelAndTexture(defaultModelId, defaultTextureId);
                        }
                    });
                    SyncModelInfo syncMsg = new SyncModelInfo(serverPlayer.getEntityId(), modelInfoCap);
                    NetworkHandler.sendToClientPlayer(syncMsg, serverPlayer);
                } else {
                    modelInfoCap.markDirty();
                }
            });

            Capabilities.getStarModelsCap(player).ifPresent(starModelCap -> {
                if (player instanceof EntityPlayerMP serverPlayer) {
                    NetworkHandler.sendToClientPlayer(new SyncStarModels(starModelCap.getStarModels()), serverPlayer);
                }
            });
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == null) {
            return;
        }
        EntityPlayer player = event.player;
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            Capabilities.getModelInfoCap(player).ifPresent(cap -> {
                if (cap.isDirty()) {
                    SyncModelInfo syncMsg = new SyncModelInfo(player.getEntityId(), cap);
                    if (player.getServer() == null) {
                        return;
                    }
                    player.getServer().getPlayerList().getPlayers().forEach(p -> NetworkHandler.sendToClientPlayer(syncMsg, p));
                    cap.setDirty(false);
                }
            });

        }
    }

}
