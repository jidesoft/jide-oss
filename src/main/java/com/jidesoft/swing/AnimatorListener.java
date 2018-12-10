package com.jidesoft.swing;

import java.awt.*;
import java.util.EventListener;

/**
 * This is a listener that can be used to customize the behaviour of animation.
 */
public interface AnimatorListener extends EventListener {

    /**
     * Called when the animation sequence starts.
     *
     * @param component the component for this animation
     */
    void animationStarts(Component component);

    /**
     * Called when the animation sequence runs at certain step..
     *
     * @param component the component for this animation
     * @param totalStep the total steps
     * @param step      the current step
     */
    void animationFrame(Component component, int totalStep, int step);

    /**
     * Called when the animation sequence ends.
     *
     * @param component the component for this animation
     */
    void animationEnds(Component component);
}
