/*
 * @(#)ShadowRenderer.java 9/3/2012
 *
 * Copyright 2002 - 2012 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import java.awt.image.BufferedImage;

/**
 * An interface for adding a shadow to an in-memory image
 */
public interface ShadowRenderer {
    /**
     * Creates and returns a shadow image based on the supplied image
     *
     * @param image
     *
     * @return
     */
    public BufferedImage createShadow(final BufferedImage image);
}
