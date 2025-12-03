package com.fox.ysmu.network.message;

import com.fox.ysmu.client.ClientModelManager;
import com.fox.ysmu.data.EncryptTools;
import com.fox.ysmu.data.ModelData;
import com.fox.ysmu.model.ServerModelManager;
import com.fox.ysmu.util.ThreadTools;
import com.fox.ysmu.util.UuidUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;

import java.nio.file.Path;
import java.util.UUID;

public class RequestLoadModel implements IMessage {

    private String fileName;

    public RequestLoadModel() {
    }

    public RequestLoadModel(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        fileName = new PacketBuffer(buf).readString(32767);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        new PacketBuffer(buf).writeString(fileName);
    }

    public static class Handler implements IMessageHandler<RequestLoadModel, IMessage> {

        @Override
        public IMessage onMessage(RequestLoadModel message, MessageContext ctx) {
            if (ctx.side.isClient()) {
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
                    Path modelFile = ServerModelManager.CACHE_CLIENT.resolve(fileName);
                    byte[] fileBytes = FileUtils.readFileToByteArray(modelFile.toFile());
                    ModelData data = EncryptTools.decryptModel(UuidUtils.asBytes(uuid), ClientModelManager.PASSWORD, fileBytes);
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
