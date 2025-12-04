package com.fox.ysmu.capability;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class AuthModelsCapabilityProvider implements ICapabilitySerializable<NBTTagList> {
    @CapabilityInject(AuthModelsCapability.class)
    public static Capability<AuthModelsCapability> AUTH_MODELS_CAP = null;
    private AuthModelsCapability instance = AUTH_MODELS_CAP.getDefaultInstance();


    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == AUTH_MODELS_CAP;
    }

    @Override
    public @Nullable <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == AUTH_MODELS_CAP) {
            return AUTH_MODELS_CAP.cast(createCapability());
        }
        return null;
    }

    @Nonnull
    private AuthModelsCapability createCapability() {
        if (instance == null) {
            this.instance = new AuthModelsCapability();
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