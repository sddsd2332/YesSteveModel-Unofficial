package com.fox.ysmu.network;

import com.fox.ysmu.network.message.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class NetworkHandler {


    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("ysmu_network");

    public static void init() {
        CHANNEL.registerMessage(SyncModelFiles.Handler.class, SyncModelFiles.class, 0, Side.SERVER);
        CHANNEL.registerMessage(SendModelFile.Handler.class, SendModelFile.class, 1, Side.CLIENT);
        CHANNEL.registerMessage(RequestSyncModel.Handler.class, RequestSyncModel.class, 2, Side.CLIENT);
        CHANNEL.registerMessage(RequestLoadModel.Handler.class, RequestLoadModel.class, 3, Side.CLIENT);
        CHANNEL.registerMessage(SyncModelInfo.Handler.class, SyncModelInfo.class, 4, Side.CLIENT);
        CHANNEL.registerMessage(SetModelAndTexture.Handler.class, SetModelAndTexture.class, 5, Side.SERVER);
        CHANNEL.registerMessage(SyncAuthModels.Handler.class, SyncAuthModels.class, 6, Side.CLIENT);
        CHANNEL.registerMessage(SetPlayAnimation.Handler.class, SetPlayAnimation.class, 7, Side.SERVER);
        CHANNEL.registerMessage(SyncStarModels.Handler.class, SyncStarModels.class, 8, Side.CLIENT);
        CHANNEL.registerMessage(SetStarModel.Handler.class, SetStarModel.class, 9, Side.SERVER);
        CHANNEL.registerMessage(RequestServerModelInfo.Handler.class, RequestServerModelInfo.class, 10, Side.CLIENT);
        CHANNEL.registerMessage(CompleteFeedback.Handler.class, CompleteFeedback.class, 12, Side.CLIENT);
    }


    public static void sendToClientPlayer(IMessage message, EntityPlayer player) {
        if (player instanceof EntityPlayerMP mp) {
            CHANNEL.sendTo(message, mp);
        }
    }
}
