/*
 * @(#)MaskFilter.java
 *
 * Copyright 2002-2003 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.icons;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

/**
 * An image filter that replaces one color in an image with another color.
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

    public static Icon replaceIconColor(Icon originalIcon, Color originalColor, Color newColor) {
        // Convert Icon to BufferedImage
        BufferedImage image = new BufferedImage(
                originalIcon.getIconWidth(),
                originalIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        originalIcon.paintIcon(null, g2d, 0, 0);
        g2d.dispose();

        // Replace color in BufferedImage
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color pixelColor = new Color(image.getRGB(x, y), true);
                if (colorMatches(pixelColor, originalColor)) {
                    image.setRGB(x, y, newColor.getRGB());
                }
            }
        }

        // Convert BufferedImage back to Icon
        return new ImageIcon(image);
    }

    private static boolean colorMatches(Color c1, Color c2) {
        return c1.getRGB() == c2.getRGB();
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

    public static Icon createIcon(Icon i, Color oldColor, Color newColor) {
        return replaceIconColor(i, oldColor, newColor);
    }

    /**
     * Creates an image as negative of an existing one. It will basically replace the black color with white color.
     */
    public static Image createNegativeImage(Image i) {
        return createImage(i, Color.black, Color.white);
    }

    /**
     * Constructs a MaskFilter object that filters color of image to another color Please note, you can also use {@link
     * #getInstance(java.awt.Color, java.awt.Color)} to reuse the same instance of MaskFilter.
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

