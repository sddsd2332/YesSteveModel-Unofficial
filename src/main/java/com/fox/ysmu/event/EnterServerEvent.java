package com.fox.ysmu.event;

import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.model.ServerModelManager;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = YesSteveModel.MOD_ID)
public final class EnterServerEvent {

    @SubscribeEvent
    public static void onLoggedInServer(PlayerEvent.PlayerLoggedInEvent event) {
        ServerModelManager.sendRequestSyncModelMessage(event.player);
    }


}
