/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.fox.ysmu.geckolib3.util;

import com.fox.ysmu.geckolib3.geo.IGeoRenderer;
import com.fox.ysmu.geckolib3.model.provider.GeoModelProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

public class AnimationUtils {

    public static double convertTicksToSeconds(double ticks) {
        return ticks / 20;
    }

    public static double convertSecondsToTicks(double seconds) {
        return seconds * 20;
    }

    /**
     * Gets the renderer for an entity
     */
    public static Render getRenderer(Entity entity) {
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

        return renderManager.getEntityRenderObject(entity);
    }

    @SuppressWarnings("rawtypes")
    public static GeoModelProvider getGeoModelForEntity(Entity entity) {
        Render entityRenderer = getRenderer(entity);

        if (entityRenderer instanceof IGeoRenderer) {
            return ((IGeoRenderer<?>) entityRenderer).getGeoModelProvider();
        }

        return null;
    }
}
