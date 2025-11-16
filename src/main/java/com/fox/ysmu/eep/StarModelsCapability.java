package com.fox.ysmu.eep;

import com.google.common.collect.Sets;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Set;

public class StarModelsCapability  implements INBTSerializable<NBTTagList> {

    private Set<ResourceLocation> starModels = Sets.newHashSet();

    public void addModel(ResourceLocation modelId) {
        starModels.add(modelId);
    }

    public void copyFrom(StarModelsCapability source) {
        this.starModels = source.starModels;
    }

    public void removeModel(ResourceLocation modelId) {
        starModels.remove(modelId);
    }

    public boolean containModel(ResourceLocation modelId) {
        return starModels.contains(modelId);
    }

    public Set<ResourceLocation> getStarModels() {
        return starModels;
    }

    public void setStarModels(Set<ResourceLocation> starModels) {
        this.starModels = starModels;
    }

    public void clear() {
        starModels.clear();
    }


    @Override
    public NBTTagList serializeNBT() {
        NBTTagList listTag = new NBTTagList();
        for (ResourceLocation modelId : starModels) {
            listTag.appendTag(new NBTTagString(modelId.toString()));
        }
        return listTag;
    }

    @Override
    public void deserializeNBT(NBTTagList nbt) {
        this.starModels.clear();
        for (NBTBase tag : nbt) {
            if (tag instanceof NBTTagString string) {
                starModels.add(new ResourceLocation(string.getString()));
            }

        }
    }
}
