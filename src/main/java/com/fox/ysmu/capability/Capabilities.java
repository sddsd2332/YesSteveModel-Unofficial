package com.fox.ysmu.capability;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.Optional;

public class Capabilities {

    @CapabilityInject(AuthModelsCapability.class)
    public static Capability<AuthModelsCapability> AUTH_MODELS_CAP = null;

    @CapabilityInject(ModelInfoCapability.class)
    public static Capability<ModelInfoCapability> MODEL_INFO_CAP = null;

    @CapabilityInject(StarModelsCapability.class)
    public static Capability<StarModelsCapability> STAR_MODELS_CAP = null;

    public static void registerCapabilities() {
        AuthModelsCapabilityProvider.register();
        ModelInfoCapabilityProvider.register();
        StarModelsCapabilityProvider.register();
    }

    public static Optional<ModelInfoCapability> getModelInfoCap(Entity player) {
        if (player.hasCapability(MODEL_INFO_CAP, null)) {
            return Optional.ofNullable(player.getCapability(MODEL_INFO_CAP, null));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<AuthModelsCapability> getAuthModelsCap(Entity player) {
        if (player.hasCapability(AUTH_MODELS_CAP, null)) {
            return Optional.ofNullable(player.getCapability(AUTH_MODELS_CAP, null));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<StarModelsCapability> getStarModelsCap(Entity player) {
        if (player.hasCapability(STAR_MODELS_CAP, null)) {
            return Optional.ofNullable(player.getCapability(STAR_MODELS_CAP, null));
        } else {
            return Optional.empty();
        }
    }

}
