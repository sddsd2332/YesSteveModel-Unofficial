package com.fox.ysmu.network.message;

import com.fox.ysmu.event.CapabilityEvent;
import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

public class SyncStarModels implements IMessage {

    private Set<ResourceLocation> starModels;

    public SyncStarModels() {
    }

    public SyncStarModels(Set<ResourceLocation> starModels) {
        this.starModels = starModels;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        int size = packetBuffer.readVarInt();
        Set<ResourceLocation> tmp = Sets.newHashSet();
        for (int i = 0; i < size; i++) {
            tmp.add(packetBuffer.readResourceLocation());
        }
        starModels = tmp;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeVarInt(starModels.size());
        for (ResourceLocation modelId : starModels) {
            packetBuffer.writeResourceLocation(modelId);
        }
    }

    public static class Handler implements IMessageHandler<SyncStarModels, IMessage> {

        @Override
        public IMessage onMessage(SyncStarModels message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                handleCapability(message);
            }
            return null;
        }
    }


    @SideOnly(Side.CLIENT)
    private static void handleCapability(SyncStarModels message) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player != null) {
            CapabilityEvent.getStarModelsCap(player).ifPresent(cap -> {
                cap.setStarModels(message.starModels);
            });
        }
    }

}
