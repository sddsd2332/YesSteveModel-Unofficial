package com.fox.ysmu.network.message;

import com.fox.ysmu.client.upload.UploadManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CompleteFeedback implements IMessage {

    public CompleteFeedback() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<CompleteFeedback, IMessage> {

        @Override
        public IMessage onMessage(CompleteFeedback message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                UploadManager.finishUpload();
            }
            return null;
        }
    }
}
