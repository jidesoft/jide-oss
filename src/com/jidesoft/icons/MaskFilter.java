/*
 * @(#)MaskFilter.java
 *
 * Copyright 2002-2003 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.icons;

import java.awt.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

/**
 * An image filter that will replace one color in an image with another color.
 */
public class MaskFilter extends RGBImageFilter {
    private Color _newColor;
    private Color _oldColor;

    private static MaskFilter _maskFilter = null;

    public static MaskFilter getInstance(Color oldColor, Color newColor) {
        if (_maskFilter == null) {
            _maskFilter = new MaskFilter(oldColor, newColor);
        }
        else {
            _maskFilter.setOldColor(oldColor);
            _maskFilter.setNewColor(newColor);
        }
        return _maskFilter;
    }

    private void setNewColor(Color newColor) {
        _newColor = newColor;
    }

    private void setOldColor(Color oldColor) {
        _oldColor = oldColor;
    }

    /**
     * Creates an image from an existing one by replacing the old color with the new color.
     */
    public static Image createImage(Image i, Color oldColor, Color newColor) {
        MaskFilter filter = MaskFilter.getInstance(oldColor, newColor);
        ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
        Image image = Toolkit.getDefaultToolkit().createImage(prod);
        return image;
    }

    /**
     * Creates an image as negative of an existing one. It will basically replace the black color
     * with white color.
     */
    public static Image createNegativeImage(Image i) {
        return createImage(i, Color.black, Color.white);
    }

    /**
     * Constructs a MaskFilter object that filters color of image to another color Please note, you
     * can also use {@link #getInstance(java.awt.Color,java.awt.Color)} to reuse the same instance
     * of MaskFilter.
     *
     * @param oldColor old color in exist image that needs to be replaced by new color
     * @param newColor new color to replace the old color
     */
    public MaskFilter(Color oldColor, Color newColor) {
        _newColor = newColor;
        _oldColor = oldColor;
        canFilterIndexColorModel = true;
    }

    /**
     * Overrides <code>RGBImageFilter.filterRGB</code>.
     */
    @Override
    public int filterRGB(int x, int y, int rgb) {
        if (_newColor != null && _oldColor != null) {
            if (rgb == _oldColor.getRGB()) {
                return _newColor.getRGB();
            }
        }
        return rgb;
    }
}

