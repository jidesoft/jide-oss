/*
 * @(#)MetalUtils.java 5/19/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.metal;

import sun.swing.CachedPainter;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;

/**
 * This is a dumping ground for random stuff we want to use in several places.
 *
 * @author Steve Wilson
 * @version %I% %G%
 */

public class MetalUtils {

    /**
     * This draws the "Flush 3D Border" which is used throughout the Metal L&F
     */
    static void drawFlush3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate(x, y);
        g.setColor(MetalLookAndFeel.getControlDarkShadow());
        g.drawRect(0, 0, w - 2, h - 2);
        g.setColor(MetalLookAndFeel.getControlHighlight());
        g.drawRect(1, 1, w - 2, h - 2);
        g.setColor(MetalLookAndFeel.getControl());
        g.drawLine(0, h - 1, 1, h - 2);
        g.drawLine(w - 1, 0, w - 2, 1);
        g.translate(-x, -y);
    }

    /**
     * This draws a variant "Flush 3D Border" It is used for things like pressed buttons.
     */
    static void drawPressed3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate(x, y);

        drawFlush3DBorder(g, 0, 0, w, h);

        g.setColor(MetalLookAndFeel.getControlShadow());
        g.drawLine(1, 1, 1, h - 2);
        g.drawLine(1, 1, w - 2, 1);
        g.translate(-x, -y);
    }

    static void drawActiveButtonBorder(Graphics g, int x, int y, int w, int h) {
        drawFlush3DBorder(g, x, y, w, h);
        g.setColor(MetalLookAndFeel.getPrimaryControl());
        g.drawLine(x + 1, y + 1, x + 1, h - 3);
        g.drawLine(x + 1, y + 1, w - 3, x + 1);
        g.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
        g.drawLine(x + 2, h - 2, w - 2, h - 2);
        g.drawLine(w - 2, y + 2, w - 2, h - 2);
    }

    //
    // Ocean specific stuff.
    // 

    /**
     * Draws a radial type gradient. The gradient will be drawn vertically if <code>vertical</code> is true, otherwise
     * horizontally. The UIManager key consists of five values: r1 r2 c1 c2 c3. The gradient is broken down into four
     * chunks drawn in order from the origin. <ol> <li>Gradient r1 % of the size from c1 to c2 <li>Rectangle r2 % of the
     * size in c2. <li>Gradient r1 % of the size from c2 to c1 <li>The remaining size will be filled with a gradient
     * from c1 to c3. </ol>
     *
     * @param c        Component rendering to
     * @param g        Graphics to draw to.
     * @param key      UIManager key used to look up gradient values.
     * @param x        X coordinate to draw from
     * @param y        Y coordinate to draw from
     * @param w        Width to draw to
     * @param h        Height to draw to
     * @param vertical Direction of the gradient
     * @return true if <code>key</code> exists, otherwise false.
     */
    static boolean drawGradient(Component c, Graphics g, String key,
                                int x, int y, int w, int h, boolean vertical) {
        java.util.List gradient = (java.util.List) UIManager.get(key);
        if (gradient == null || !(g instanceof Graphics2D)) {
            return false;
        }

        if (w <= 0 || h <= 0) {
            return true;
        }

        GradientPainter.INSTANCE.paint(
                c, (Graphics2D) g, gradient, x, y, w, h, vertical);
        return true;
    }


    private static class GradientPainter extends CachedPainter {
        /**
         * Instance used for painting.  This is the only instance that is ever created.
         */
        public static final GradientPainter INSTANCE = new GradientPainter(8);

        // Size of images to create. For vertical gradients this is the width,
        // otherwise it's the height.
        private static final int IMAGE_SIZE = 64;

        /**
         * This is the actual width we're painting in, or last painted to.
         */
        private int w;
        /**
         * This is the actual height we're painting in, or last painted to
         */
        private int h;


        GradientPainter(int count) {
            super(count);
        }

        public void paint(Component c, Graphics2D g,
                          java.util.List gradient, int x, int y, int w,
                          int h, boolean isVertical) {
            int imageWidth;
            int imageHeight;
            if (isVertical) {
                imageWidth = IMAGE_SIZE;
                imageHeight = h;
            }
            else {
                imageWidth = w;
                imageHeight = IMAGE_SIZE;
            }
            synchronized (c.getTreeLock()) {
                this.w = w;
                this.h = h;
                paint(c, g, x, y, imageWidth, imageHeight,
                        gradient, isVertical);
            }
        }

        protected void paintToImage(Component c, Image image, Graphics g,
                                    int w, int h, Object[] args) {
            Graphics2D g2 = (Graphics2D) g;
            java.util.List gradient = (java.util.List) args[0];
            boolean isVertical = ((Boolean) args[1]).booleanValue();
            // Render to the VolatileImage
            if (isVertical) {
                drawVerticalGradient(g2,
                        ((Number) gradient.get(0)).floatValue(),
                        ((Number) gradient.get(1)).floatValue(),
                        (Color) gradient.get(2),
                        (Color) gradient.get(3),
                        (Color) gradient.get(4), w, h);
            }
            else {
                drawHorizontalGradient(g2,
                        ((Number) gradient.get(0)).floatValue(),
                        ((Number) gradient.get(1)).floatValue(),
                        (Color) gradient.get(2),
                        (Color) gradient.get(3),
                        (Color) gradient.get(4), w, h);
            }
        }

        protected void paintImage(Component c, Graphics g,
                                  int x, int y, int imageW, int imageH,
                                  Image image, Object[] args) {
            boolean isVertical = ((Boolean) args[1]).booleanValue();
            // Render to the screen
            g.translate(x, y);
            if (isVertical) {
                for (int counter = 0; counter < w; counter += IMAGE_SIZE) {
                    int tileSize = Math.min(IMAGE_SIZE, w - counter);
                    g.drawImage(image, counter, 0, counter + tileSize, h,
                            0, 0, tileSize, h, null);
                }
            }
            else {
                for (int counter = 0; counter < h; counter += IMAGE_SIZE) {
                    int tileSize = Math.min(IMAGE_SIZE, h - counter);
                    g.drawImage(image, 0, counter, w, counter + tileSize,
                            0, 0, w, tileSize, null);
                }
            }
            g.translate(-x, -y);
        }

        private void drawVerticalGradient(Graphics2D g, float ratio1,
                                          float ratio2, Color c1, Color c2,
                                          Color c3, int w, int h) {
            int mid = (int) (ratio1 * h);
            int mid2 = (int) (ratio2 * h);
            if (mid > 0) {
                g.setPaint(getGradient((float) 0, (float) 0, c1, (float) 0,
                        (float) mid, c2));
                g.fillRect(0, 0, w, mid);
            }
            if (mid2 > 0) {
                g.setColor(c2);
                g.fillRect(0, mid, w, mid2);
            }
            if (mid > 0) {
                g.setPaint(getGradient((float) 0, (float) mid + mid2, c2,
                        (float) 0, (float) mid * 2 + mid2, c1));
                g.fillRect(0, mid + mid2, w, mid);
            }
            if (h - mid * 2 - mid2 > 0) {
                g.setPaint(getGradient((float) 0, (float) mid * 2 + mid2, c1,
                        (float) 0, (float) h, c3));
                g.fillRect(0, mid * 2 + mid2, w, h - mid * 2 - mid2);
            }
        }

        private void drawHorizontalGradient(Graphics2D g, float ratio1,
                                            float ratio2, Color c1, Color c2,
                                            Color c3, int w, int h) {
            int mid = (int) (ratio1 * w);
            int mid2 = (int) (ratio2 * w);
            if (mid > 0) {
                g.setPaint(getGradient((float) 0, (float) 0, c1,
                        (float) mid, (float) 0, c2));
                g.fillRect(0, 0, mid, h);
            }
            if (mid2 > 0) {
                g.setColor(c2);
                g.fillRect(mid, 0, mid2, h);
            }
            if (mid > 0) {
                g.setPaint(getGradient((float) mid + mid2, (float) 0, c2,
                        (float) mid * 2 + mid2, (float) 0, c1));
                g.fillRect(mid + mid2, 0, mid, h);
            }
            if (w - mid * 2 - mid2 > 0) {
                g.setPaint(getGradient((float) mid * 2 + mid2, (float) 0, c1,
                        w, (float) 0, c3));
                g.fillRect(mid * 2 + mid2, 0, w - mid * 2 - mid2, h);
            }
        }

        private GradientPaint getGradient(float x1, float y1,
                                          Color c1, float x2, float y2,
                                          Color c2) {
            return new GradientPaint(x1, y1, c1, x2, y2, c2, true);
        }
    }
}
