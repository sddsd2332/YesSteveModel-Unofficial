package com.fox.ysmu.util;

import com.fox.ysmu.geckolib3.core.IAnimatable;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import java.util.concurrent.TimeUnit;

@SideOnly(Side.CLIENT)
public final class AnimatableCacheUtil {
    public static final Cache<ResourceLocation, IAnimatable> ANIMATABLE_CACHE = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    public static final Cache<ResourceLocation, IAnimatable> TEXTURE_GUI_CACHE = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    public static final Cache<ResourceLocation, Entity> ENTITIES_CACHE = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
}
