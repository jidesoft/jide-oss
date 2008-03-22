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
    private boolean _brighter;
    private static int _percent = 30;
    private static ColorFilter _colorFilter;

    public static ColorFilter getInstance(boolean brighter, int percent) {
        if (_colorFilter == null) {
            _colorFilter = new ColorFilter(brighter, percent);
        }
        else {
            _colorFilter.setBrighter(brighter);
            setPercent(percent);
        }
        return _colorFilter;
    }

    public void setBrighter(boolean brighter) {
        _brighter = brighter;
    }

    public static void setPercent(int percent) {
        _percent = percent;
    }

    /**
     * Creates a brighter image
     *
     * @param i the original image
     *
     * @return a brighter image
     */
    public static Image createBrighterImage(Image i) {
        ColorFilter filter = ColorFilter.getInstance(true, _percent);
        ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(prod);
    }

    /**
     * Creates a brighter image with a given percentage of brightness
     *
     * @param i the original image
     * @param p percentage of brightness
     *
     * @return a brighter image
     */
    public static Image createBrighterImage(Image i, int p) {
        setPercent(p);
        return createBrighterImage(i);
    }


    /**
     * Creates a darker image
     *
     * @param i the original image
     *
     * @return a darker image.
     */
    public static Image createDarkerImage(Image i) {
        ColorFilter filter = ColorFilter.getInstance(false, _percent);
        ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(prod);
    }


    /**
     * Creates a darker image with a given percentage of darkness
     *
     * @param i the original image
     * @param p percentage of darkness
     *
     * @return a darker image.
     */
    public static Image createDarkerImage(Image i, int p) {
        setPercent(p);
        return createDarkerImage(i);
    }


    /**
     * Constructs a ColorFilter object that filters a color image to a brighter or a darker image.
     * Please note, you can also use {@link #getInstance(boolean,int)} to reuse the same instance of
     * ColorFilter.
     *
     * @param b a boolean -- true if the pixels should be brightened
     * @param p an int in the range 0..100 that determines the percentage of gray, where 100 is the
     *          darkest gray, and 0 is the lightest
     */
    public ColorFilter(boolean b, int p) {
        _brighter = b;
        _percent = p;
        canFilterIndexColorModel = true;
    }

    /**
     * Overrides <code>RGBImageFilter.filterRGB</code>.
     */
    @Override
    public int filterRGB(int x, int y, int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        return rgb & 0xff000000 | convert(r) << 16 | convert(g) << 8 | convert(b);
    }

    private int convert(int color) {
        if (_brighter)
            color += (255 - color) * _percent / 100;
        else
            color -= (255 - color) * _percent / 100;

        if (color < 0) color = 0;
        if (color > 255) color = 255;
        return color;
    }
}

