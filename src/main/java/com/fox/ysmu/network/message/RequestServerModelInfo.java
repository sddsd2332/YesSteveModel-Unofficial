package com.fox.ysmu.network.message;


import com.fox.ysmu.model.format.Type;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class RequestServerModelInfo implements IMessage {

    private List<Info> customModels;
    private List<Info> authModels;

    public RequestServerModelInfo() {
    }

    public RequestServerModelInfo(List<Info> customModels, List<Info> authModels) {
        this.customModels = customModels;
        this.authModels = authModels;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        List<Info> outCustomModels = Lists.newArrayList();
        List<Info> outAuthModels = Lists.newArrayList();
        PacketBuffer buffer = new PacketBuffer(buf);
        int customModelsSize = buffer.readVarInt();
        for (int i = 0; i < customModelsSize; i++) {
            outCustomModels.add(bufferToInfo(buffer));
        }
        int authModelsSize = buffer.readVarInt();
        for (int i = 0; i < authModelsSize; i++) {
            outAuthModels.add(bufferToInfo(buffer));
        }
        customModels = outCustomModels;
        authModels = outAuthModels;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeVarInt(this.customModels.size());
        for (Info info : this.customModels) {
            infoToBuffer(buffer, info);
        }
        buffer.writeVarInt(this.authModels.size());
        for (Info info : this.authModels) {
            infoToBuffer(buffer, info);
        }
    }

    public static class Handler implements IMessageHandler<RequestServerModelInfo, IMessage> {

        @Override
        public IMessage onMessage(RequestServerModelInfo message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                openGui(message);
                //Minecraft.getMinecraft().displayGuiScreen(new ModelManageScreen(message.customModels, message.authModels));
            }
            return null;
        }
    }

    /**
     * TODO
     */
    @SideOnly(Side.CLIENT)
    private static void openGui(RequestServerModelInfo message) {
        Minecraft mc = Minecraft.getMinecraft();
        //   mc.displayGuiScreen(new ModelManageScreen(message.customModels, message.authModels));
    }

    private static void infoToBuffer(PacketBuffer buf, Info info) {
        buf.writeString(info.fileName);
        buf.writeEnumValue(info.type);
        buf.writeLong(info.size);
    }

    private static Info bufferToInfo(PacketBuffer buf) {
        return new Info(buf.readString(32767), buf.readEnumValue(Type.class), buf.readLong());
    }

    public static class Info {

        private String fileName;
        private Type type;
        private long size;

        public Info() {
        }

        public Info(String fileName, Type type, long size) {
            this.fileName = fileName;
            this.type = type;
            this.size = size;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }
    }
}
