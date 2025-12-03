package com.fox.ysmu.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StarModelsCapabilityProvider implements ICapabilitySerializable<NBTTagList>{

    private final StarModelsCapability defaultImpl = new StarModelsCapability();

    public static void register() {
        CapabilityManager.INSTANCE.register(StarModelsCapability.class, new Capability.IStorage<>() {
            @Override
            public @Nullable NBTBase writeNBT(Capability<StarModelsCapability> capability, StarModelsCapability instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<StarModelsCapability> capability, StarModelsCapability instance, EnumFacing side, NBTBase nbt) {
                if (nbt instanceof NBTTagList tag) {
                    instance.deserializeNBT(tag);
                }
            }
        }, StarModelsCapability::new);
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == Capabilities.STAR_MODELS_CAP;
    }

    @Override
    public @Nullable <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == Capabilities.STAR_MODELS_CAP) {
            return Capabilities.STAR_MODELS_CAP.cast(defaultImpl);
        }
        return null;
    }

    @Override
    public NBTTagList serializeNBT() {
        return defaultImpl.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagList nbt) {
        defaultImpl.deserializeNBT(nbt);
    }
}
