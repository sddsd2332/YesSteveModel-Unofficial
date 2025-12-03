package com.fox.ysmu.network.message;

import com.fox.ysmu.capability.Capabilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetPlayAnimation implements IMessage {

    private static final int STOP = -1;
    private int extraAnimationId;

    public SetPlayAnimation() {
    }

    public SetPlayAnimation(int extraAnimationId) {
        this.extraAnimationId = extraAnimationId;
    }

    public static SetPlayAnimation stop() {
        return new SetPlayAnimation(STOP);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.extraAnimationId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.extraAnimationId);
    }

    public static class Handler implements IMessageHandler<SetPlayAnimation, IMessage> {

        @Override
        public IMessage onMessage(SetPlayAnimation message, MessageContext ctx) {
            if (ctx.side.isServer()) {
                EntityPlayerMP sender = ctx.getServerHandler().player;
                if (sender != null) {
                    if (STOP <= message.extraAnimationId && message.extraAnimationId < 8) {
                        handleCapability(message, sender);
                    }
                }
            }
            return null;
        }

        private void handleCapability(SetPlayAnimation message, EntityPlayerMP player) {
            Capabilities.getModelInfoCap(player).ifPresent(modelIdCap -> {
                if (message.extraAnimationId == STOP) {
                    modelIdCap.stopAnimation();
                } else {
                    modelIdCap.playAnimation("extra" + message.extraAnimationId);
                }
            });
        }
    }
}
