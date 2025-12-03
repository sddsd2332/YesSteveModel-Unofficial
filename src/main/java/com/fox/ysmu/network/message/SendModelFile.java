package com.fox.ysmu.network.message;

import com.fox.ysmu.client.ClientModelManager;
import com.fox.ysmu.model.ServerModelManager;
import com.fox.ysmu.util.Md5Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class SendModelFile implements IMessage {

    private byte[] data;

    public SendModelFile() {
    }

    public SendModelFile(byte[] data) {
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        data = buffer.readByteArray();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeByteArray(data);
    }

    public static class Handler implements IMessageHandler<SendModelFile, IMessage> {

        @Override
        public IMessage onMessage(SendModelFile message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                if (message.data.length == 48) {
                    ClientModelManager.PASSWORD = message.data;
                } else {
                    String fileName = Md5Utils.md5Hex(message.data).toUpperCase(Locale.US);
                    File file = ServerModelManager.CACHE_CLIENT.resolve(fileName).toFile();
                    try {
                        FileUtils.writeByteArrayToFile(file, message.data);
                        RequestLoadModel.loadModel(fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
