package com.fox.ysmu.network.message;

import com.fox.ysmu.capability.Capabilities;
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

public class SyncAuthModels implements IMessage {

    private Set<ResourceLocation> authModels;

    public SyncAuthModels() {
    }

    public SyncAuthModels(Set<ResourceLocation> authModels) {
        this.authModels = authModels;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        this.authModels = Sets.newHashSet();
        PacketBuffer buffer = new PacketBuffer(buf);
        for (int i = 0; i < size; i++) {
            this.authModels.add(buffer.readResourceLocation());
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.authModels.size());
        PacketBuffer buffer = new PacketBuffer(buf);
        for (ResourceLocation modelId : this.authModels) {
            buffer.writeResourceLocation(modelId);
        }
    }


    public static class Handler implements IMessageHandler<SyncAuthModels, IMessage> {

        @Override
        public IMessage onMessage(SyncAuthModels message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                handleCapability(message);
            }
            return null;
        }
    }

    public Set<ResourceLocation> getAuthModels() {
        return authModels;
    }


    @SideOnly(Side.CLIENT)
    private static void handleCapability(SyncAuthModels message) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player != null) {
            Capabilities.getAuthModelsCap(player).ifPresent(cap -> {
                cap.setAuthModels(message.authModels);
            });
        }
    }

}
