package com.fox.ysmu.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModelInfoCapabilityProvider implements ICapabilitySerializable<NBTTagCompound> {

    private final ModelInfoCapability defaultImpl = new ModelInfoCapability();

    public static void register() {
        CapabilityManager.INSTANCE.register(ModelInfoCapability.class, new Capability.IStorage<>() {
            @Override
            public @Nullable NBTBase writeNBT(Capability<ModelInfoCapability> capability, ModelInfoCapability instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<ModelInfoCapability> capability, ModelInfoCapability instance, EnumFacing side, NBTBase nbt) {
                if (nbt instanceof NBTTagCompound tag) {
                    instance.deserializeNBT(tag);
                }
            }
        }, ModelInfoCapability::new);
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == Capabilities.MODEL_INFO_CAP;
    }

    @Override
    public @Nullable <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == Capabilities.MODEL_INFO_CAP) {
            return Capabilities.MODEL_INFO_CAP.cast(defaultImpl);
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return defaultImpl.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        defaultImpl.deserializeNBT(nbt);
    }
}
