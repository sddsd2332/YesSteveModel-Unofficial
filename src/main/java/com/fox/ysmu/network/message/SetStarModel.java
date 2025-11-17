package com.fox.ysmu.network.message;

import com.fox.ysmu.capabilities.Capabilities;
import com.fox.ysmu.capabilities.StarModelsCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetStarModel implements IMessage {

    private String modelId;
    private boolean isAdd;

    public SetStarModel() {
    }

    private SetStarModel(ResourceLocation modelId, boolean isAdd) {
        this.modelId = modelId.toString();
        this.isAdd = isAdd;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.modelId = ByteBufUtils.readUTF8String(buf);
        this.isAdd = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.modelId);
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
            EntityPlayerMP sender = ctx.getServerHandler().player;
            if (sender != null) {
                handleEEP(message, sender);
            }
            return null;
        }

        private void handleEEP(SetStarModel message, EntityPlayerMP player) {
            if (player.hasCapability(Capabilities.STAR_MODELS_CAP, null)) {
                StarModelsCapability eep = player.getCapability(Capabilities.STAR_MODELS_CAP, null);
                if (eep != null) {
                    ResourceLocation modelLoc = message.modelId.isEmpty() ? null : new ResourceLocation(message.modelId);
                    if (message.isAdd) {
                        eep.addModel(modelLoc);
                    } else {
                        eep.removeModel(modelLoc);
                    }
                }
            }
        }
    }
}
