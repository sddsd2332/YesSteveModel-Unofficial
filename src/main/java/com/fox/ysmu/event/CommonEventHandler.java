package com.fox.ysmu.event;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import com.fox.ysmu.eep.ExtendedAuthModels;
import com.fox.ysmu.eep.ExtendedModelInfo;
import com.fox.ysmu.eep.ExtendedStarModels;
import com.fox.ysmu.model.ServerModelManager;
import com.fox.ysmu.network.NetworkHandler;
import com.fox.ysmu.network.message.SyncAuthModels;
import com.fox.ysmu.network.message.SyncModelInfo;
import com.fox.ysmu.network.message.SyncStarModels;
import com.fox.ysmu.ysmu;


import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod.EventBusSubscriber
public class CommonEventHandler {
    // WARNING:If you don't know what this does,DO NOT CHANGE IT
    // TODO 不安全的实现方法
    public static final DataParameter<Byte> MOTION_DATAWATCHER_ID  = EntityDataManager.<Byte>createKey(EntityPlayer.class, DataSerializers.BYTE);
    //public static final int MOTION_DATAWATCHER_ID = 28;
    public static final int ON_GROUND = 0x01;
    public static final int IS_FLYING = 0x02;

    @SubscribeEvent
    public static void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        // 调用发送模型同步请求的方法
        if (event.player != null) {
            ServerModelManager.sendRequestSyncModelMessage(event.player);
        }
    }

    @SubscribeEvent
    public static void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.getEntity() instanceof EntityPlayer player) {
            player.getDataManager().register(MOTION_DATAWATCHER_ID, (byte) 0);
            // 注册 AuthModels
            if (ExtendedAuthModels.get(player) == null) {
                ExtendedAuthModels.register(player);
            }
            // 注册 ModelInfo
            if (ExtendedModelInfo.get(player) == null) {
                ExtendedModelInfo.register(player);
            }
            // 注册 StarModels
            if (ExtendedStarModels.get(player) == null) {
                ExtendedStarModels.register(player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        if (event.isWasDeath()) { // TODO 跨维度？
            // 复制 AuthModels 数据
            ExtendedAuthModels oldAuthProps = ExtendedAuthModels.get(event.getOriginal());
            ExtendedAuthModels newAuthProps = ExtendedAuthModels.get(event.getEntityPlayer());
            if (oldAuthProps != null && newAuthProps != null) {
                newAuthProps.copyFrom(oldAuthProps);
            }

            // 复制 ModelInfo 数据
            ExtendedModelInfo oldModelProps = ExtendedModelInfo.get(event.getOriginal());
            ExtendedModelInfo newModelProps = ExtendedModelInfo.get(event.getEntityPlayer());
            if (oldModelProps != null && newModelProps != null) {
                newModelProps.copyFrom(oldModelProps);
            }

            // 3. 复制 StarModels 数据
            ExtendedStarModels oldStarProps = ExtendedStarModels.get(event.getOriginal());
            ExtendedStarModels newStarProps = ExtendedStarModels.get(event.getEntityPlayer());
            if (oldStarProps != null && newStarProps != null) {
                newStarProps.copyFrom(oldStarProps);
            }
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof EntityPlayer trackPlayer) {
            EntityPlayer player = event.getEntityPlayer();
            ExtendedModelInfo eep = ExtendedModelInfo.get(trackPlayer);
            if (eep != null) {
                SyncModelInfo syncMsg = new SyncModelInfo(trackPlayer.getEntityId(), eep);
                NetworkHandler.sendToClientPlayer(syncMsg, player);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer player) {
            ExtendedModelInfo modelInfoEEP = ExtendedModelInfo.get(player);
            if (modelInfoEEP != null) {
                if (player instanceof EntityPlayerMP serverPlayer) {
                    ExtendedAuthModels authModelsEEP = ExtendedAuthModels.get(player);
                    if (authModelsEEP != null) {
                        NetworkHandler
                            .sendToClientPlayer(new SyncAuthModels(authModelsEEP.getAuthModels()), serverPlayer);
                        if (ServerModelManager.AUTH_MODELS.contains(modelInfoEEP.getModelId().getPath())
                            && !authModelsEEP.containModel(modelInfoEEP.getModelId())) {
                            ResourceLocation defaultModelId = new ResourceLocation(ysmu.MODID, "default");
                            ResourceLocation defaultTextureId = new ResourceLocation(ysmu.MODID, "default/default.png");
                            modelInfoEEP.setModelAndTexture(defaultModelId, defaultTextureId);
                        }
                    }
                    SyncModelInfo syncMsg = new SyncModelInfo(serverPlayer.getEntityId(), modelInfoEEP);
                    NetworkHandler.sendToClientPlayer(syncMsg, serverPlayer);
                } else {
                    modelInfoEEP.markDirty();
                }
            }
            ExtendedStarModels starModelsEEP = ExtendedStarModels.get(player);
            if (starModelsEEP != null) {
                if (player instanceof EntityPlayerMP serverPlayer) {
                    NetworkHandler.sendToClientPlayer(new SyncStarModels(starModelsEEP.getStarModels()), serverPlayer);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == null) {
            return;
        }
        EntityPlayer player = event.player;
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            updateData(player);
            ExtendedModelInfo eep = ExtendedModelInfo.get(player);
            if (eep != null && eep.isDirty()) {
                SyncModelInfo syncMsg = new SyncModelInfo(player.getEntityId(), eep);
                NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(
                    player.dimension,
                    player.posX,
                    player.posY,
                    player.posZ,
                    64.0D // 64个方块的范围，这是一个常用值
                );
                NetworkHandler.CHANNEL.sendToAllAround(syncMsg, targetPoint);
                eep.setDirty(false);
            }
        }
    }

    private static void updateData(EntityPlayer player){
        byte oldData = player.getDataManager().get(MOTION_DATAWATCHER_ID);
        byte newData;
        boolean oldOnGround = (oldData & ON_GROUND) != 0;
        boolean oldIsFlying = (oldData & IS_FLYING) != 0;
        boolean currentOnGround = player.onGround;
        boolean currentIsFlying = player.capabilities.isFlying;
        if (oldOnGround != currentOnGround) {
            if (currentOnGround) {
                newData = (byte) (oldData | ON_GROUND);
            } else {
                newData = (byte) (oldData & ~ON_GROUND);
            }
            player.getDataManager().set(MOTION_DATAWATCHER_ID, newData);
        }
        if (oldIsFlying != currentIsFlying) {
            if (currentIsFlying) {
                newData = (byte) (oldData | IS_FLYING);
            } else {
                newData = (byte) (oldData & ~IS_FLYING);
            }
            player.getDataManager().set(MOTION_DATAWATCHER_ID, newData);
        }
    }
}
