package com.fox.ysmu.network.message;

import com.fox.ysmu.event.CapabilityEvent;
import com.fox.ysmu.model.ServerModelManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetModelAndTexture implements IMessage {

    private ResourceLocation modelId;
    private ResourceLocation selectTexture;

    public SetModelAndTexture() {
    }

    public SetModelAndTexture(ResourceLocation modelId, ResourceLocation selectTexture) {
        this.modelId = modelId;
        this.selectTexture = selectTexture;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        this.modelId = buffer.readResourceLocation();
        this.selectTexture = buffer.readResourceLocation();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeResourceLocation(modelId);
        buffer.writeResourceLocation(selectTexture);
    }

    public static class Handler implements IMessageHandler<SetModelAndTexture, IMessage> {

        @Override
        public IMessage onMessage(SetModelAndTexture message, MessageContext ctx) {
            if (ctx.side.isServer()) {
                EntityPlayerMP sender = ctx.getServerHandler().player;
                if (sender != null) {
                    handleCapability(message, sender);
                }
            }
            return null;
        }

        private void handleCapability(SetModelAndTexture message, EntityPlayerMP player) {
            CapabilityEvent.getModelInfoCap(player).ifPresent(modelIdCap -> CapabilityEvent.getAuthModelsCap(player).ifPresent(ownModelsCap -> {
                if (!ServerModelManager.AUTH_MODELS.contains(message.modelId.getPath()) || ownModelsCap.containModel(message.modelId)) {
                    modelIdCap.setModelAndTexture(message.modelId, message.selectTexture);
                }
            }));
        }
    }


}
