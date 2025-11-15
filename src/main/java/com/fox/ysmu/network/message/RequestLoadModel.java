package com.fox.ysmu.network.message;

import java.io.File;
import java.util.UUID;

import net.minecraft.client.Minecraft;

import org.apache.commons.io.FileUtils;

import com.fox.ysmu.client.ClientModelManager;
import com.fox.ysmu.data.EncryptTools;
import com.fox.ysmu.data.ModelData;
import com.fox.ysmu.model.ServerModelManager;
import com.fox.ysmu.util.ThreadTools;
import com.fox.ysmu.util.UuidUtils;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class RequestLoadModel implements IMessage {

    private String fileName;

    public RequestLoadModel() {}

    public RequestLoadModel(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.fileName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.fileName);
    }

    public static class Handler implements IMessageHandler<RequestLoadModel, IMessage> {

        @Override
        public IMessage onMessage(RequestLoadModel message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                ClientModelManager.CACHE_MD5.add(message.fileName);
                loadModel(message.fileName);
            }
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void loadModel(String fileName) {
        ThreadTools.THREAD_POOL.submit(() -> {
            try {
                while (ClientModelManager.PASSWORD == null) {
                    Thread.sleep(500);
                }
                if (Minecraft.getMinecraft().player != null) {
                    UUID uuid = Minecraft.getMinecraft().player.getUniqueID();
                    File modelFile = ServerModelManager.CACHE_CLIENT.resolve(fileName)
                        .toFile();
                    byte[] fileBytes = FileUtils.readFileToByteArray(modelFile);
                    ModelData data = EncryptTools
                        .decryptModel(UuidUtils.asBytes(uuid), ClientModelManager.PASSWORD, fileBytes);
                    if (data != null) {
                        Minecraft.getMinecraft().addScheduledTask(() -> ClientModelManager.registerAll(data));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
