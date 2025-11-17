package com.fox.ysmu.geckolib3.util;

import com.fox.ysmu.geckolib3.core.IAnimatable;
import com.fox.ysmu.geckolib3.core.manager.AnimationFactory;
import com.fox.ysmu.geckolib3.core.manager.InstancedAnimationFactory;
import com.fox.ysmu.geckolib3.core.manager.SingletonAnimationFactory;

public class GeckoLibUtil {

    public static AnimationFactory createFactory(IAnimatable animatable, boolean singletonObject) {
        return singletonObject ? new SingletonAnimationFactory(animatable) : new InstancedAnimationFactory(animatable);
    }
}
