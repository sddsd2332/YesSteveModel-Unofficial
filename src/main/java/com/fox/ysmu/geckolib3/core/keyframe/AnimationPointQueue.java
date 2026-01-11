/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.fox.ysmu.geckolib3.core.keyframe;

import com.fox.ysmu.geckolib3.core.processor.IBone;

import java.util.LinkedList;

/**
 * An animation point queue holds a queue of Animation Points which are used in
 * the AnimatedEntityModel to lerp between values
 */
@SuppressWarnings("serial")
public class AnimationPointQueue extends LinkedList<AnimationPoint> {

    public IBone model;
}
