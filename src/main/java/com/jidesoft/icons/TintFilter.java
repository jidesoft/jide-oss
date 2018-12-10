/*
 * @(#)TintFilter.java 8/17/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.icons;

import java.awt.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

/**
 * An image filter that tints the image with a color.
 */
public class TintFilter extends RGBImageFilter {
    private int _alpha, _red, _green, _blue;
    private int _width, _height;
    private Insets _insets;

    public TintFilter(Color color, int width, int height, Insets insets) {
        _alpha = color.getAlpha();
        _red = color.getRed();
        _green = color.getGreen();
        _blue = color.getBlue();
        _width = width;
        _height = height;
        _insets = insets;
        canFilterIndexColorModel = true;
    }

    public int filterRGB(int x, int y, int rgb) {
        if (_insets != null && (x < _insets.left || x > _width - _insets.right - 1 || y < _insets.top || y > _height - _insets.bottom - 1)) {
            return rgb;
        }
        else {
            Color color = new Color(rgb, true);
            int r = Math.min(Math.max(color.getRed() + (color.getRed() * _red) / 100, 0), 255);
            int g = Math.min(Math.max(color.getGreen() + (color.getGreen() * _green) / 100, 0), 255);
            int b = Math.min(Math.max(color.getBlue() + (color.getBlue() * _blue) / 100, 0), 255);
            int a = Math.min(Math.max(color.getAlpha() + (color.getAlpha() * _alpha) / 100, 0), 255);
            return a << 24 | r << 16 | g << 8 | b;
        }
    }

    /**
     * Creates a tinted image
     *
     * @param i      the original image
     * @param color  the color to be tinted
     * @param insets the insets. The border area with the insets will not be tinted.
     * @return a tinted image
     */
    public static Image createTintedImage(Image i, Color color, Insets insets) {
        TintFilter filter = new TintFilter(color, i.getWidth(null), i.getHeight(null), insets);
        ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(prod);
    }
}
