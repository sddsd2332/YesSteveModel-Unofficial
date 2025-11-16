package com.fox.ysmu.network.message;

import com.fox.ysmu.capabilities.Capabilities;
import com.fox.ysmu.eep.ModelInfoCapability;
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
            EntityPlayerMP sender = ctx.getServerHandler().player;
            if (sender != null && STOP <= message.extraAnimationId && message.extraAnimationId < 8) {
                handleEEP(message, sender);
            }
            return null;
        }

        private void handleEEP(SetPlayAnimation message, EntityPlayerMP player) {
            if (player.hasCapability(Capabilities.ModelInfo, null)) {
                ModelInfoCapability modelIdEEP = player.getCapability(Capabilities.ModelInfo, null);
                if (modelIdEEP != null) {
                    if (message.extraAnimationId == STOP) {
                        modelIdEEP.stopAnimation();
                    } else {
                        modelIdEEP.playAnimation("extra" + message.extraAnimationId);
                    }
                }
            }
        }
    }
}
