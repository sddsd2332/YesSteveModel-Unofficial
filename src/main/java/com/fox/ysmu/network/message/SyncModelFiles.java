package com.fox.ysmu.network.message;

import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.data.EncryptTools;
import com.fox.ysmu.model.format.ServerModelInfo;
import com.fox.ysmu.network.PacketHandler;
import com.fox.ysmu.util.ThreadTools;
import com.fox.ysmu.util.UuidUtils;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
;
import static com.fox.ysmu.model.ServerModelManager.*;

public class SyncModelFiles implements IMessage {

    private String[] md5Info;

    public SyncModelFiles() {
    }

    public SyncModelFiles(String[] md5Info) {
        this.md5Info = md5Info;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        int count = buffer.readVarInt();
        String[] output = new String[count];
        for (int i = 0; i < count; i++) {
            output[i] = buffer.readString(32767);
        }
        md5Info = output;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeVarInt(md5Info.length);
        for (String md5 : md5Info) {
            buffer.writeString(md5);
        }
    }

    public static class Handler implements IMessageHandler<SyncModelFiles, IMessage> {

        @Override
        public IMessage onMessage(SyncModelFiles message, MessageContext ctx) {
            if (ctx.side.isServer()) {
                EntityPlayerMP sender = ctx.getServerHandler().player;
                if (sender != null) {
                    sendPassword(sender);
                    sendModelFiles(message.md5Info, sender);
                }
            }
            return null;
        }

        private void sendModelFiles(String[] md5Info, EntityPlayerMP sender) {
            Collection<String> cache = CACHE_NAME_INFO.values().stream().map(ServerModelInfo::getMd5).collect(Collectors.toList());
            List<String> output = Lists.newArrayList(cache);
            for (String md5 : md5Info) {
                if (cache.contains(md5)) {
                    output.remove(md5);
                    YesSteveModel.packetHandler.sendToClientPlayer(new RequestLoadModel(md5), sender);
                }
            }
            for (String md5 : output) {
                File modelFile = CACHE_SERVER.resolve(md5).toFile();
                try {
                    byte[] modelBytes = FileUtils.readFileToByteArray(modelFile);
                    ThreadTools.THREAD_POOL.submit(() -> YesSteveModel.packetHandler.sendToClientPlayer(new SendModelFile(modelBytes), sender));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendPassword(EntityPlayerMP sender) {
            try {
                byte[] password = FileUtils.readFileToByteArray(PASSWORD_FILE.toFile());
                byte[] uuid = UuidUtils.asBytes(sender.getUniqueID());
                byte[] output = EncryptTools.encryptPassword(uuid, password);
                ThreadTools.THREAD_POOL.submit(() -> YesSteveModel.packetHandler.sendToClientPlayer(new SendModelFile(output), sender));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
