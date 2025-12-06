package com.fox.ysmu.network;

import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.network.message.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class PacketHandler {


    public final SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel("ysmu_network");

    public void init() {
        netHandler.registerMessage(SyncModelFiles.Handler.class, SyncModelFiles.class, 0, Side.SERVER);
        netHandler.registerMessage(SendModelFile.Handler.class, SendModelFile.class, 1, Side.CLIENT);
        netHandler.registerMessage(RequestSyncModel.Handler.class, RequestSyncModel.class, 2, Side.CLIENT);
        netHandler.registerMessage(RequestLoadModel.Handler.class, RequestLoadModel.class, 3, Side.CLIENT);
        netHandler.registerMessage(SyncModelInfo.Handler.class, SyncModelInfo.class, 4, Side.CLIENT);
        netHandler.registerMessage(SetModelAndTexture.Handler.class, SetModelAndTexture.class, 5, Side.SERVER);
        netHandler.registerMessage(SyncAuthModels.Handler.class, SyncAuthModels.class, 6, Side.CLIENT);
        netHandler.registerMessage(SetPlayAnimation.Handler.class, SetPlayAnimation.class, 7, Side.SERVER);
        netHandler.registerMessage(SyncStarModels.Handler.class, SyncStarModels.class, 8, Side.CLIENT);
        netHandler.registerMessage(SetStarModel.Handler.class, SetStarModel.class, 9, Side.SERVER);
        netHandler.registerMessage(RequestServerModelInfo.Handler.class, RequestServerModelInfo.class, 10, Side.CLIENT);
        netHandler.registerMessage(CompleteFeedback.Handler.class, CompleteFeedback.class, 12, Side.CLIENT);
    }


    public  void sendToClientPlayer(IMessage message, EntityPlayer player) {
        if (player instanceof EntityPlayerMP mp) {
            netHandler.sendTo(message, mp);
        }
    }

    public void sendToAllTracking(IMessage message, Entity entity) {
        //Range is ignored for sendToAllTracking, and only gets sent to clients that have the location loaded
        sendToAllTracking(message, entity.dimension, entity.posX, entity.posY, entity.posZ);
    }

    public void sendToAllTracking(IMessage message, int dimension, double x, double y, double z) {
        //Range is ignored for sendToAllTracking, and only gets sent to clients that have the location loaded
        sendToAllTracking(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, 1));
    }

    public synchronized void sendToAllTracking(IMessage message, NetworkRegistry.TargetPoint point) {
        netHandler.sendToAllTracking(message, point);
    }

    public static EntityPlayer getPlayer(MessageContext context) {
        return YesSteveModel.proxy.getPlayer(context);
    }

    public static void handlePacket(Runnable runnable, EntityPlayer player) {
        YesSteveModel.proxy.handlePacket(runnable, player);
    }

    public void sendToServer(IMessage message) {
        netHandler.sendToServer(message);
    }

}
