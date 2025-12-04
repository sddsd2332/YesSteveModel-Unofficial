package com.fox.ysmu.capability;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.Optional;

public class Capabilities {


    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(ModelInfoCapability.class, new ModelInfoCapability.Storage(), ModelInfoCapability::new);
        CapabilityManager.INSTANCE.register(AuthModelsCapability.class, new AuthModelsCapability.Storage(), AuthModelsCapability::new);
        CapabilityManager.INSTANCE.register(StarModelsCapability.class, new StarModelsCapability.Storage(), StarModelsCapability::new);
    }

    public static Optional<ModelInfoCapability> getModelInfoCap(Entity player) {
        if (player.hasCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null)) {
            return Optional.ofNullable(player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<AuthModelsCapability> getAuthModelsCap(Entity player) {
        if (player.hasCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP, null)) {
            return Optional.ofNullable(player.getCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP, null));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<StarModelsCapability> getStarModelsCap(Entity player) {
        if (player.hasCapability(StarModelsCapabilityProvider.STAR_MODELS_CAP, null)) {
            return Optional.ofNullable(player.getCapability(StarModelsCapabilityProvider.STAR_MODELS_CAP, null));
        } else {
            return Optional.empty();
        }
    }

}
