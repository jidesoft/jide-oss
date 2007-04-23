/*
 * @(#)ColorFilter.java	Sep 30, 2002
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.icons;

import java.awt.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

/**
 * An image filter that brighten or darken an existing image.
 */
public class ColorFilter extends RGBImageFilter {
    private boolean brighter;
    private int percent;

    private static ColorFilter _colorFilter = new ColorFilter(false, 0);

    public static ColorFilter getInstance(boolean brighter, int percent) {
        _colorFilter.setBrighter(brighter);
        _colorFilter.setPercent(percent);
        return _colorFilter;
    }

    public void setBrighter(boolean brighter) {
        this.brighter = brighter;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    /**
     * Creates a dimmed image
     *
     * @deprecated the name is wrong. Replaced by createBrighterImage().
     */
    public static Image createDimmedImage(Image i) {
        ColorFilter filter = ColorFilter.getInstance(true, 30); // 30 percent brighter
        ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
        Image image = Toolkit.getDefaultToolkit().createImage(prod);
        return image;
    }

    /**
     * Creates a brighter image
     */
    public static Image createBrighterImage(Image i) {
        ColorFilter filter = ColorFilter.getInstance(true, 30); // 30 percent brighter
        ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
        Image image = Toolkit.getDefaultToolkit().createImage(prod);
        return image;
    }

    /**
     * Creates a dimmed image
     */
    public static Image createDarkerImage(Image i) {
        ColorFilter filter = ColorFilter.getInstance(false, 30); // 30 percent brighter
        ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
        Image image = Toolkit.getDefaultToolkit().createImage(prod);
        return image;
    }

    /**
     * Constructs a ColorFilter object that filters a color image to a
     * brighter or a darker image.
     *
     * @param b a boolean -- true if the pixels should be brightened
     * @param p an int in the range 0..100 that determines the percentage
     *          of gray, where 100 is the darkest gray, and 0 is the lightest
     * @deprecated use getInstance instead to reuse the same instance
     */
    public ColorFilter(boolean b, int p) {
        brighter = b;
        percent = p;
        canFilterIndexColorModel = true;
    }

    /**
     * Overrides <code>RGBImageFilter.filterRGB</code>.
     */
    public int filterRGB(int x, int y, int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        return (rgb & 0xff000000) | (convert(r) << 16) | (convert(g) << 8) | (convert(b) << 0);
    }

    private int convert(int color) {
        if (brighter)
            color += (255 - color) * percent / 100;
        else
            color -= (255 - color) * percent / 100;

        if (color < 0) color = 0;
        if (color > 255) color = 255;
        return color;
    }
}

