package com.fox.ysmu.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;


public class ModelInfoCapabilityProvider implements ICapabilitySerializable<NBTTagCompound> {

    @CapabilityInject(ModelInfoCapability.class)
    public static Capability<ModelInfoCapability> MODEL_INFO_CAP = null;
    private ModelInfoCapability instance = MODEL_INFO_CAP.getDefaultInstance();


    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == MODEL_INFO_CAP;
    }


    @Override
    public @Nullable <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == MODEL_INFO_CAP) {
            return MODEL_INFO_CAP.cast(createCapability());
        }
        return null;
    }


    @Nonnull
    private ModelInfoCapability createCapability() {
        if (instance == null) {
            this.instance = new ModelInfoCapability();
        }
        return instance;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return createCapability().serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        createCapability().deserializeNBT(nbt);
    }
}
