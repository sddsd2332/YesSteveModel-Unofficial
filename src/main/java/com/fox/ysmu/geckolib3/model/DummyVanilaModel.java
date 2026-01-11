package com.fox.ysmu.geckolib3.model;

import com.fox.ysmu.geckolib3.geo.GeoEntityRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

public class DummyVanilaModel extends ModelBase {

    public GeoEntityRenderer renderer;

    public DummyVanilaModel() {
    }

    public DummyVanilaModel(GeoEntityRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
                       float p_78088_6_, float p_78088_7_) {
        // renderer.doRender(p_78088_1_, 0,0,0, p_78088_1_.rotationYaw,
        // Minecraft.getMinecraft().timer.renderPartialTicks);
    }
}
