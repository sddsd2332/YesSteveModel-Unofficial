package com.fox.ysmu.network.message;

import com.fox.ysmu.capabilities.Capabilities;
import com.fox.ysmu.capabilities.ModelInfoCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.fox.ysmu.util.ThreadTools;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class SyncModelInfo implements IMessage {

    private int entityId;
    private NBTTagCompound modelInfoNBT;

    public SyncModelInfo() {}

    public SyncModelInfo(int entityId, ModelInfoCapability modelInfo) {
        this.entityId = entityId;
        this.modelInfoNBT = new NBTTagCompound();
        if (modelInfo != null) {
            modelInfo.deserializeNBT(this.modelInfoNBT);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.modelInfoNBT = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        ByteBufUtils.writeTag(buf, this.modelInfoNBT);
    }

    public static class Handler implements IMessageHandler<SyncModelInfo, IMessage> {

        @Override
        public IMessage onMessage(SyncModelInfo message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                handleEEP(message);
            }
            return null;
        }

        private void handleEEP(SyncModelInfo message) {
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
                            if (player.hasCapability(Capabilities.MODEL_INFO_CAP, null)) {
                                ModelInfoCapability eep = player.getCapability(Capabilities.MODEL_INFO_CAP, null);
                                if (eep != null) {
                                    eep.deserializeNBT(message.modelInfoNBT);
                                }
                            }
                        }

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }
}
