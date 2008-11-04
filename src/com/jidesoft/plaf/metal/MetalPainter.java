/*
 * @(#)${NAME}
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.metal;

import com.jidesoft.plaf.basic.BasicPainter;
import com.jidesoft.plaf.basic.ThemePainter;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;

/**
 * Painter for Metal L&F.
 * <p/>
 * Please note, this class is an internal class which is meant to be used by other JIDE classes only. Future version
 * might break your build if you use it.
 */
public class MetalPainter extends BasicPainter {

    private static MetalPainter _instance;

    public static ThemePainter getInstance() {
        if (_instance == null) {
            _instance = new MetalPainter();
        }
        return _instance;
    }

    public MetalPainter() {
    }

    @Override
    public void paintGripper(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (orientation == HORIZONTAL) {
            MetalBumps bumps = new MetalBumps(rect.width, rect.height - 6,
                    state == ThemePainter.STATE_SELECTED ? MetalLookAndFeel.getPrimaryControlHighlight() : MetalLookAndFeel.getControlHighlight(),
                    state == ThemePainter.STATE_SELECTED ? MetalLookAndFeel.getPrimaryControlDarkShadow() : MetalLookAndFeel.getControlDarkShadow(),
                    null);
            bumps.paintIcon(null, g, rect.x, rect.y + 3);
        }
        else {
            MetalBumps bumps = new MetalBumps(rect.width - 6, rect.height,
                    state == ThemePainter.STATE_SELECTED ? MetalLookAndFeel.getPrimaryControlHighlight() : MetalLookAndFeel.getControlHighlight(),
                    state == ThemePainter.STATE_SELECTED ? MetalLookAndFeel.getPrimaryControlDarkShadow() : MetalLookAndFeel.getControlDarkShadow(),
                    null);
            bumps.paintIcon(null, g, rect.x + 3, rect.y);
        }
    }

    @Override
    public void paintDockableFrameTitlePane(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        boolean isSelected = state == STATE_SELECTED;

        int width = rect.width;
        int height = rect.height;

        Color background = isSelected ? MetalLookAndFeel.getWindowTitleBackground() : MetalLookAndFeel.getWindowTitleInactiveBackground();
        String gradientKey = isSelected ? "InternalFrame.activeTitleGradient" : "InternalFrame.inactiveTitleGradient";
        if (!drawGradient(c, g, gradientKey, 0, 0, width, height, true)) {
            g.setColor(background);
            g.fillRect(0, 0, width, height);
        }
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
        Object colors = UIManager.get(key);
        if (!(colors instanceof List)) {
            return false;
        }

        java.util.List gradient = (java.util.List) colors;
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

    // copy from MetalUtils but removed most of the caching feature
    private static class GradientPainter {
        /**
         * Instance used for painting.  This is the only instance that is ever created.
         */
        public static final GradientPainter INSTANCE = new GradientPainter();

        GradientPainter() {
            super();
        }

        public void paint(Component c, Graphics2D g,
                          java.util.List gradient, int x, int y, int w,
                          int h, boolean isVertical) {
            int imageWidth;
            int imageHeight;
            if (isVertical) {
                imageWidth = w;
                imageHeight = h;
            }
            else {
                imageWidth = w;
                imageHeight = h;
            }
            synchronized (c.getTreeLock()) {
                paint(c, g, imageWidth, imageHeight, gradient, isVertical);
            }
        }

        protected void paint(Component c, Graphics g, int w, int h, java.util.List gradient, boolean isVertical) {
            Graphics2D g2 = (Graphics2D) g;
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


