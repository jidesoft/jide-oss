package com.jidesoft.swing;

import java.awt.*;

/**
 * This is a listener that can be used to customize the behaviour of animation.
 */
public interface AnimatorListener {

    /**
     * Called when the animation sequence starts.
     *
     * @param component
     */
    void animationStarts(Component component);

    /**
     * Called when the animation sequence runs at certain step..
     *
     * @param component
     */
    void animationFrame(Component component, int totalStep, int step);

    /**
     * Called when the animation sequence ends.
     *
     * @param component
     */
    void animationEnds(Component component);
}
