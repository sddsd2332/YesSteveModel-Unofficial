package com.fox.ysmu.capability;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class StarModelsCapabilityProvider implements ICapabilitySerializable<NBTTagList> {
    @CapabilityInject(StarModelsCapability.class)
    public static Capability<StarModelsCapability> STAR_MODELS_CAP = null;
    private StarModelsCapability instance = STAR_MODELS_CAP.getDefaultInstance();


    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == STAR_MODELS_CAP;
    }

    @Override
    public @Nullable <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == STAR_MODELS_CAP) {
            return STAR_MODELS_CAP.cast(createCapability());
        }
        return null;
    }

    @Nonnull
    private StarModelsCapability createCapability() {
        if (instance == null) {
            this.instance = new StarModelsCapability();
        }
        return instance;
    }

    @Override
    public NBTTagList serializeNBT() {
        return createCapability().serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagList nbt) {
        createCapability().deserializeNBT(nbt);
    }
}
