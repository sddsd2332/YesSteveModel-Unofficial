package com.fox.ysmu.client.renderer;

import com.fox.ysmu.client.entity.CustomPlayerEntity;
import com.fox.ysmu.client.model.CustomPlayerModel;
import com.fox.ysmu.client.renderer.layer.CustomPlayerElytraLayer;
import com.fox.ysmu.client.renderer.layer.CustomPlayerItemInHandLayer;
import com.fox.ysmu.event.CapabilityEvent;
import com.fox.ysmu.event.api.SpecialPlayerRenderEvent;
import com.fox.ysmu.geckolib3.geo.GeoReplacedEntityRenderer;
import com.fox.ysmu.geckolib3.geo.render.built.GeoModel;
import com.fox.ysmu.geckolib3.resource.GeckoLibCache;
import com.fox.ysmu.util.ModelIdUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

public class CustomPlayerRenderer extends GeoReplacedEntityRenderer<CustomPlayerEntity> {

    private GeoModel geoModel;

    @SuppressWarnings("all")
    public CustomPlayerRenderer() {
        super(Minecraft.getMinecraft().getRenderManager(), new CustomPlayerModel(), new CustomPlayerEntity());
        addLayer(new CustomPlayerItemInHandLayer<>(this));
        addLayer(new CustomPlayerElytraLayer<>(this));
    }

    @Override
    public void doRender(EntityLivingBase entity, double x, double y, double z, float entityYaw,
                         float partialTicks) {
        if (this.animatable != null && entity instanceof EntityPlayer player) {
            CapabilityEvent.getModelInfoCap(player).ifPresent(cap -> {
                this.animatable.setPlayer(player);
                this.animatable.setMainModel(ModelIdUtil.getMainId(cap.getModelId()));
                this.animatable.setTexture(cap.getSelectTexture());
            });
            if (MinecraftForge.EVENT_BUS.post(new SpecialPlayerRenderEvent(player, this.animatable, ModelIdUtil.getModelIdFromMainId(this.animatable.getMainModel())))) {
                return;
            }
        }
        ResourceLocation location = this.modelProvider.getModelLocation(animatable);
        GeoModel geoModel = GeckoLibCache.getInstance().getGeoModels().get(location);
        if (geoModel != null) {
            this.geoModel = geoModel;
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }

    }

    @Override
    public float getWidthScale(Object animatable) {
        if (this.animatable != null) {
            return this.animatable.getWidthScale();
        }
        return super.getWidthScale(animatable);
    }

    @Override
    public float getHeightScale(Object animatable) {
        if (this.animatable != null) {
            return this.animatable.getHeightScale();
        }
        return super.getHeightScale(animatable);
    }

    public CustomPlayerEntity getCustomPlayerEntity() {
        return this.animatable;
    }

    @Nullable
    public GeoModel getGeoModel() {
        return geoModel;
    }
}
