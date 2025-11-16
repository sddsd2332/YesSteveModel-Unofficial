package com.fox.ysmu.capabilities;

import com.fox.ysmu.eep.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Capabilities {

    @CapabilityInject(AuthModelsCapability.class)
    public static Capability<AuthModelsCapability> AuthModels = null;

    @CapabilityInject(ModelInfoCapability.class)
    public static Capability<ModelInfoCapability> ModelInfo = null;

    @CapabilityInject(StarModelsCapability.class)
    public static Capability<StarModelsCapability> StarModels =null;

    public static void registerCapabilities() {
        AuthModelsCapabilityProvider.register();
        ModelInfoCapabilityProvider.register();
        StarModelsCapabilityProvider.register();
    }

}
