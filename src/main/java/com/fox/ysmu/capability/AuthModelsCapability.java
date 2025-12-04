package com.fox.ysmu.capability;

import com.google.common.collect.Sets;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Set;


public class AuthModelsCapability  {

    private Set<ResourceLocation> authModels = Sets.newHashSet();

    public void addModel(ResourceLocation modelId) {
        authModels.add(modelId);
    }

    public void copyFrom(AuthModelsCapability source) {
        this.authModels = source.authModels;
    }

    public void removeModel(ResourceLocation modelId) {
        authModels.remove(modelId);
    }

    public boolean containModel(ResourceLocation modelId) {
        return true;
    }

    public Set<ResourceLocation> getAuthModels() {
        return authModels;
    }

    public void setAuthModels(Set<ResourceLocation> authModels) {
        this.authModels = authModels;
    }

    public void clear() {
        authModels.clear();
    }



    public NBTTagList serializeNBT() {
        NBTTagList listTag = new NBTTagList();
        for (ResourceLocation modelId : authModels) {
            listTag.appendTag(new NBTTagString(modelId.toString()));
        }
        return listTag;
    }


    public void deserializeNBT(NBTTagList nbt) {
        this.authModels.clear();
        for (NBTBase tag : nbt) {
            if (tag instanceof NBTTagString string) {
                authModels.add(new ResourceLocation(string.getString()));
            }
        }
    }

    public static class Storage implements Capability.IStorage<AuthModelsCapability> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<AuthModelsCapability> capability, AuthModelsCapability instance, EnumFacing side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<AuthModelsCapability> capability, AuthModelsCapability instance, EnumFacing side, NBTBase nbt) {
            if (nbt instanceof NBTTagList tag) {
                instance.deserializeNBT(tag);
            }
        }
    }
}
