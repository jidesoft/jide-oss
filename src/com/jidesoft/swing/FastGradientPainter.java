/*
 * @(#)FastGradientPainter.java 4/3/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import java.awt.*;
import java.awt.image.BufferedImage;

class FastGradientPainter {
    private static GradientCache gradientCache = new GradientCache();

    //no instantiation
    private FastGradientPainter() {
    }

    /**
     * Draws a rectangular gradient in a vertical or horizontal direction.
     * The drawing operations are hardware optimized whenever possible using the
     * Java2D hardware rendering facilities. The result is gradient rendering
     * approaching the performance of flat color rendering.
     *
     * @param g2         Graphics2D instance to use for rendering
     * @param s          shape confines of gradient
     * @param startColor starting color for gradient
     * @param endColor   ending color fro gradient
     * @param isVertical specifies a vertical or horizontal gradient
     */
    public static void drawGradient(Graphics2D g2, Shape s, Color startColor, Color endColor, boolean isVertical) {
        Rectangle r = s.getBounds();
        if (r.height <= 0 || r.width <= 0) return;

        int length = isVertical ? r.height : r.width;
        GradientInfo info = new GradientInfo(g2.getDeviceConfiguration(), length, startColor, endColor, isVertical);

        BufferedImage gradient = gradientCache.retrieve(info);
        if (gradient == null) {
            gradient = createGradientTile(info);
            gradientCache.store(info, gradient);
        }

        Shape prevClip = null;
        boolean nonRectangular = false;
        if (!r.equals(s)) {
            nonRectangular = true;
            prevClip = g2.getClip();
            g2.clip(s);
        }
        if (isVertical) {
            int w = gradient.getWidth();
            int loops = r.width / w;
            for (int i = 0; i < loops; i++)
                g2.drawImage(gradient, r.x + i * w, r.y, null);
            int rem = r.width % w;
            if (rem > 0) {
                g2.drawImage(gradient, r.x + loops * w, r.y, r.x + loops * w + rem, r.y + length, 0, 0, rem, length, null);
            }
        }
        else {
            int h = gradient.getHeight();
            int loops = r.height / h;
            for (int i = 0; i < loops; i++)
                g2.drawImage(gradient, r.x, r.y + i * h, null);
            int rem = r.height % h;
            if (rem > 0) {
                g2.drawImage(gradient, r.x, r.y + loops * h, r.x + length, r.y + loops * h + rem, 0, 0, length, rem, null);
            }
        }
        if (nonRectangular) {
            g2.setClip(prevClip);
        }
    }

    private static BufferedImage createGradientTile(GradientInfo info) {
        boolean t = info.startColor.getTransparency() > 1 || info.endColor.getTransparency() > 1;

        int dx, dy, w, h;
        if (info.isVertical) {
            dx = 0;
            h = dy = info.length;
            w = 32;
        }
        else {
            w = dx = info.length;
            dy = 0;
            h = 32;
        }
        BufferedImage img = info.gfxConfig.createCompatibleImage(w, h, t ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
        Paint gp = new GradientPaint(0, 0, info.startColor, dx, dy, info.endColor);

        Graphics2D g = img.createGraphics();
        g.setPaint(gp);
        g.fillRect(0, 0, w, h);
        g.dispose();
        return img;
    }
}

/**
 * Containts all information pertaining to a particular gradient.
 */
class GradientInfo {
    GraphicsConfiguration gfxConfig;
    int length;
    Color startColor, endColor;
    boolean isVertical;

    public GradientInfo(GraphicsConfiguration gc, int ln, Color sc, Color ec, boolean v) {
        gfxConfig = gc;
        length = ln;
        startColor = sc;
        endColor = ec;
        isVertical = v;
    }

    boolean isEquivalent(GradientInfo gi) {
        return (gi.gfxConfig.equals(gfxConfig) && gi.length == length && gi.startColor.equals(startColor) && gi.endColor.equals(endColor) && gi.isVertical == isVertical);
    }

    public boolean equals(Object o) {
        if (!(o instanceof GradientInfo)) return false;
        return isEquivalent((GradientInfo) o);
    }

    public String toString() {
        return "Direction:" + (isVertical ? "ver" : "hor") + ", Length: " + Integer.toString(length) + ", Color1: " + Integer.toString(startColor.getRGB(), 16) + ", Color2: " + Integer.toString(endColor.getRGB(), 16);
    }
}

