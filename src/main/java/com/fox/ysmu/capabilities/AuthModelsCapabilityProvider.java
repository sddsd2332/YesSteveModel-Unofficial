package com.fox.ysmu.capabilities;

import com.fox.ysmu.ysmu;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AuthModelsCapabilityProvider  implements ICapabilitySerializable<NBTTagList> {

    public static final ResourceLocation EXT_PROP_NAME = new ResourceLocation(ysmu.MODID, "AuthModels");
    private final AuthModelsCapability defaultImpl = new AuthModelsCapability();

    public static void register() {
        CapabilityManager.INSTANCE.register(AuthModelsCapability.class, new Capability.IStorage<>() {
            @Override
            public @Nullable NBTBase writeNBT(Capability<AuthModelsCapability> capability, AuthModelsCapability instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<AuthModelsCapability> capability, AuthModelsCapability instance, EnumFacing side, NBTBase nbt) {
                if (nbt instanceof NBTTagList tag) {
                    instance.deserializeNBT(tag);
                }
            }
        }, AuthModelsCapability::new);
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == Capabilities.AUTH_MODELS_CAP;
    }

    @Override
    public @Nullable <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == Capabilities.AUTH_MODELS_CAP) {
            return Capabilities.AUTH_MODELS_CAP.cast(defaultImpl);
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