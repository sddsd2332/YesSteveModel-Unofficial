package com.fox.ysmu.event;

import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.capability.*;
import com.fox.ysmu.model.ServerModelManager;
import com.fox.ysmu.network.message.SyncAuthModels;
import com.fox.ysmu.network.message.SyncModelInfo;
import com.fox.ysmu.network.message.SyncStarModels;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = YesSteveModel.MOD_ID)
public class CapabilityEvent {
    private static final ResourceLocation MODEL_INFO_CAP = new ResourceLocation(YesSteveModel.MOD_ID, "model_id");
    private static final ResourceLocation AUTH_MODELS_CAP = new ResourceLocation(YesSteveModel.MOD_ID, "own_models");
    private static final ResourceLocation STAR_MODELS_CAP = new ResourceLocation(YesSteveModel.MOD_ID, "star_models");

    @SubscribeEvent
    public static void onAttachCapabilityEvent(AttachCapabilitiesEvent<Entity> event) {
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
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        Optional<ModelInfoCapability> oldModelInfoCap = getModelInfoCap(event.getOriginal());
        Optional<AuthModelsCapability> oldAuthModelsCap = getAuthModelsCap(event.getOriginal());
        Optional<StarModelsCapability> oldStarModelsCap = getStarModelsCap(event.getOriginal());

        Optional<ModelInfoCapability> newModelInfoCap = getModelInfoCap(event.getEntityPlayer());
        Optional<AuthModelsCapability> newAuthModelsCap = getAuthModelsCap(event.getEntityPlayer());
        Optional<StarModelsCapability> newStarModelsCap = getStarModelsCap(event.getEntityPlayer());

        newModelInfoCap.ifPresent(newModelInfo -> oldModelInfoCap.ifPresent(newModelInfo::copyFrom));
        newAuthModelsCap.ifPresent(newAuthModels -> oldAuthModelsCap.ifPresent(newAuthModels::copyFrom));
        newStarModelsCap.ifPresent(newStarModels -> oldStarModelsCap.ifPresent(newStarModels::copyFrom));
    }

    @SubscribeEvent
    public static void onTrackingPlayer(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof EntityPlayer trackPlayer) {
            EntityPlayer player = event.getEntityPlayer();
            getModelInfoCap(trackPlayer).ifPresent(cap -> {
                SyncModelInfo syncMsg = new SyncModelInfo(trackPlayer.getEntityId(), cap);
                YesSteveModel.packetHandler.sendToClientPlayer(syncMsg, player);
            });
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer player) {
            getModelInfoCap(player).ifPresent(modelInfoCap -> {
                if (player instanceof EntityPlayerMP serverPlayer) {
                    getAuthModelsCap(player).ifPresent(authModelsCap -> {
                        YesSteveModel.packetHandler.sendToClientPlayer(new SyncAuthModels(authModelsCap.getAuthModels()), serverPlayer);
                        if (ServerModelManager.AUTH_MODELS.contains(modelInfoCap.getModelId().getPath()) && !authModelsCap.containModel(modelInfoCap.getModelId())) {
                            ResourceLocation defaultModelId = new ResourceLocation(YesSteveModel.MOD_ID, "default");
                            ResourceLocation defaultTextureId = new ResourceLocation(YesSteveModel.MOD_ID, "default/default.png");
                            modelInfoCap.setModelAndTexture(defaultModelId, defaultTextureId);
                        }
                    });
                    try {
                        YesSteveModel.LOGGER.info("Waiting to synchronize the model data of player: {}", player.getName());
                        YesSteveModel.LOGGER.info("This may result in a 'Failed to load texture' error, but it does not affect the game state.");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    SyncModelInfo syncMsg = new SyncModelInfo(serverPlayer.getEntityId(), modelInfoCap);
                    YesSteveModel.packetHandler.sendToClientPlayer(syncMsg, serverPlayer);
                } else {
                    modelInfoCap.markDirty();
                }
            });

            getStarModelsCap(player).ifPresent(starModelCap -> {
                if (player instanceof EntityPlayerMP serverPlayer) {
                    YesSteveModel.packetHandler.sendToClientPlayer(new SyncStarModels(starModelCap.getStarModels()), serverPlayer);
                }
            });
        }
    }

    /**
     * 同步客户端服务端数据
     */
    @SubscribeEvent
    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.player == null) return;
        EntityPlayer player = event.player;
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            getModelInfoCap(player).ifPresent(cap -> {
                if (cap.isDirty()) {
                    SyncModelInfo syncMsg = new SyncModelInfo(player.getEntityId(), cap);
                    if (player.getServer() == null) {
                        return;
                    }
                    player.getServer().getPlayerList().getPlayers().forEach(p -> YesSteveModel.packetHandler.sendToClientPlayer(syncMsg, p));
                    cap.setDirty(false);
                }
            });

        }
    }

    public static Optional<ModelInfoCapability> getModelInfoCap(Entity player) {
        return getCapability(player, ModelInfoCapabilityProvider.MODEL_INFO_CAP);
    }

    public static Optional<AuthModelsCapability> getAuthModelsCap(Entity player) {
        return getCapability(player, AuthModelsCapabilityProvider.AUTH_MODELS_CAP);
    }

    public static Optional<StarModelsCapability> getStarModelsCap(Entity player) {
        return getCapability(player, StarModelsCapabilityProvider.STAR_MODELS_CAP);
    }

    public static <T> Optional<T> getCapability(ICapabilityProvider provider, Capability<T> capability) {
        if (provider != null && provider.hasCapability(capability, null)) {
            return Optional.ofNullable(provider.getCapability(capability, null));
        }
        return Optional.empty();
    }
}
