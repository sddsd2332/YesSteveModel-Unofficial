package com.fox.ysmu.network.message;

import com.fox.ysmu.capability.ModelInfoCapability;
import com.fox.ysmu.event.CapabilityEvent;
import com.fox.ysmu.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
        PacketBuffer buffer = new PacketBuffer(buf);
        entityId = buffer.readVarInt();
        try {
            NBTTagCompound compoundTag = buffer.readCompoundTag();
            ModelInfoCapability cap = new ModelInfoCapability();
            if (compoundTag != null) {
                cap.deserializeNBT(compoundTag);
            }
            capability = cap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeVarInt(entityId);
        buffer.writeCompoundTag(capability.serializeNBT());
    }

    public static class Handler implements IMessageHandler<SyncModelInfo, IMessage> {

        @Override
        public IMessage onMessage(SyncModelInfo message, MessageContext context) {
            EntityPlayer player = PacketHandler.getPlayer(context);
            PacketHandler.handlePacket(() -> {
                Entity entity = player.world.getEntityByID(message.entityId);
                if (entity instanceof EntityPlayer entityPlayer) {
                    CapabilityEvent.getModelInfoCap(entityPlayer).ifPresent(cap -> {
                        cap.copyFrom(message.capability);
                    });
                }
            }, player);
            return null;
        }


    }
}
