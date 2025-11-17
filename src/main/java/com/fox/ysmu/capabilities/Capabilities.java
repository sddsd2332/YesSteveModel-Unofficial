package com.fox.ysmu.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Capabilities {

    @CapabilityInject(AuthModelsCapability.class)
    public static Capability<AuthModelsCapability> AUTH_MODELS_CAP = null;

    @CapabilityInject(ModelInfoCapability.class)
    public static Capability<ModelInfoCapability> MODEL_INFO_CAP = null;

    @CapabilityInject(StarModelsCapability.class)
    public static Capability<StarModelsCapability> STAR_MODELS_CAP =null;

    public static void registerCapabilities() {
        AuthModelsCapabilityProvider.register();
        ModelInfoCapabilityProvider.register();
        StarModelsCapabilityProvider.register();
    }

}
