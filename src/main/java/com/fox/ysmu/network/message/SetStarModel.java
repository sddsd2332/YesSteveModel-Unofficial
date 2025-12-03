package com.fox.ysmu.network.message;

import com.fox.ysmu.capability.Capabilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetStarModel implements IMessage {

    private ResourceLocation modelId;
    private boolean isAdd;

    public SetStarModel() {
    }

    private SetStarModel(ResourceLocation modelId, boolean isAdd) {
        this.modelId = modelId;
        this.isAdd = isAdd;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.modelId = new PacketBuffer(buf).readResourceLocation();
        this.isAdd = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        new PacketBuffer(buf).writeResourceLocation(modelId);
        buf.writeBoolean(this.isAdd);
    }

    public static SetStarModel add(ResourceLocation modelId) {
        return new SetStarModel(modelId, true);
    }

    public static SetStarModel remove(ResourceLocation modelId) {
        return new SetStarModel(modelId, false);
    }

    public static class Handler implements IMessageHandler<SetStarModel, IMessage> {

        @Override
        public IMessage onMessage(SetStarModel message, MessageContext ctx) {
            if (ctx.side.isServer()) {
                EntityPlayerMP sender = ctx.getServerHandler().player;
                if (sender != null) {
                    handleCapability(message, sender);
                }
            }
            return null;
        }

        private void handleCapability(SetStarModel message, EntityPlayerMP player) {
            Capabilities.getStarModelsCap(player).ifPresent(cap -> {
                if (message.isAdd) {
                    cap.addModel(message.modelId);
                } else {
                    cap.removeModel(message.modelId);
                }
            });
        }
    }
}
