/*
 * @(#)IconUtils.java 7/25/2024
 *
 * Copyright 2002 - 2024 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.icons;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class IconUtils {
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
//        return new ScaledImageIcon(new ImageIcon(image));
        return new ImageIcon(image);
    }

    private static boolean colorMatches(Color c1, Color c2) {
        return c1.getRGB() == c2.getRGB();
    }

    public static ImageIcon iconToImageIcon(Icon icon) {
        if (icon instanceof ImageIcon) {
            return (ImageIcon) icon;
        } else {
            int width = icon.getIconWidth();
            int height = icon.getIconHeight();
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bufferedImage.getGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            return new ImageIcon(bufferedImage);
        }
    }
}
