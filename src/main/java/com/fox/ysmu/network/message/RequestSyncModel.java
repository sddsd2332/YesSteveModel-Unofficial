package com.fox.ysmu.network.message;



import com.fox.ysmu.client.ClientModelManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class RequestSyncModel implements IMessage {

    public RequestSyncModel() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<RequestSyncModel, IMessage> {

        @Override
        public IMessage onMessage(RequestSyncModel message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                ClientModelManager.sendSyncModelMessage();
            }
            return null;
        }
    }
}
