package com.fox.ysmu.network.message;

import com.fox.ysmu.capability.Capabilities;
import com.fox.ysmu.capability.ModelInfoCapability;
import com.fox.ysmu.util.ThreadTools;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

public class SyncModelInfo implements IMessage {

    private int entityId;
    private ModelInfoCapability capability;

    public SyncModelInfo() {
    }

    public SyncModelInfo(int entityId, ModelInfoCapability capability) {
        this.entityId = entityId;
        this.capability = capability;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        PacketBuffer buffer = new PacketBuffer(buf);
        try {
            NBTTagCompound compoundTag = buffer.readCompoundTag();
            ModelInfoCapability cap = new ModelInfoCapability();
            if (compoundTag != null) {
                cap.deserializeNBT(compoundTag);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        new PacketBuffer(buf).writeCompoundTag(capability.serializeNBT());
    }

    public static class Handler implements IMessageHandler<SyncModelInfo, IMessage> {

        @Override
        public IMessage onMessage(SyncModelInfo message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                handleCapability(message);
            }
            return null;
        }


        @SideOnly(Side.CLIENT)
        private void handleCapability(SyncModelInfo message) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.world != null) {
                ThreadTools.THREAD_POOL.submit(() -> {
                    try {
                        int time = 0;
                        while (mc.world.getEntityByID(message.entityId) == null && time < 5) {
                            Thread.sleep(500);
                            time++;
                        }
                        Entity entity = mc.world.getEntityByID(message.entityId);
                        if (entity instanceof EntityPlayer player) {
                            Capabilities.getModelInfoCap(player).ifPresent(cap -> {
                                cap.copyFrom(message.capability);
                            });
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }
}
