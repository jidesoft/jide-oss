/*
 * @(#)FrameBorder.java
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.xerto;

import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import java.awt.*;

/**
 * This class is an implementation of the DockableFrame border.
 */
public class SlidingFrameBorder implements Border, UIResource {
    public static final int SHADOW_SIZE = 10;
    protected Color _highlight;
    protected Color _lightHighlight;
    protected Color _shadow;
    protected Color _darkShadow;
    protected Insets _insets;
    private static double LOG10 = Math.log(10);

    public SlidingFrameBorder(Color highlight, Color lightHighlight,
                              Color shadow, Color darkShadow, Insets insets) {
        _highlight = highlight;
        _lightHighlight = lightHighlight;
        _shadow = shadow;
        _darkShadow = darkShadow;
        _insets = insets;
    }

    /**
     * Returns the insets of the border.
     *
     * @param c the component for which this border insets value applies
     */
    public Insets getBorderInsets(Component c) {
        return _insets;
    }

    /**
     * Returns whether or not the border is opaque.  If the border is opaque, it is responsible for filling in it's own
     * background when painting.
     */
    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y,
                            int width, int height) {
        if (_insets.top >= SHADOW_SIZE) {  //
            g.setColor(XertoUtils.getFrameBorderColor());
            g.drawRect(x, y + _insets.top - 1, width, height - _insets.top + 1);

            g.setColor(_highlight);
            g.drawLine(x, y + _insets.top - 5, x + width, y + _insets.top - 5);
            g.setColor(_lightHighlight);
            g.drawLine(x, y + _insets.top - 4, x + width, y + _insets.top - 4);
            g.setColor(_highlight);
            g.drawLine(x, y + _insets.top - 3, x + width, y + _insets.top - 3);
            g.drawLine(x, y + _insets.top - 2, x + width, y + _insets.top - 2);

            paintGradient(g, new Rectangle(x, y, width, _insets.top - 5), true, 40, false);
        }

        if (_insets.left >= SHADOW_SIZE) { //
            g.setColor(XertoUtils.getFrameBorderColor());
            g.drawRect(x + _insets.left - 1, y, width - _insets.left, height);

            g.setColor(_highlight);
            g.drawLine(x + _insets.left - 5, y, x + _insets.left - 5, y + height);
            g.setColor(_lightHighlight);
            g.drawLine(x + _insets.left - 4, y, x + _insets.left - 4, y + height);
            g.setColor(_highlight);
            g.drawLine(x + _insets.left - 3, y, x + _insets.left - 3, y + height);
            g.drawLine(x + _insets.left - 2, y, x + _insets.left - 2, y + height);

            paintGradient(g, new Rectangle(x, y, _insets.left - 5, height), false, 40, false);
        }

        if (_insets.bottom >= SHADOW_SIZE) {
            g.setColor(XertoUtils.getFrameBorderColor());
            g.drawRect(x, y, width, height - _insets.bottom);

            g.setColor(_highlight);
            g.drawLine(x, y + height - _insets.bottom + 1, x + width, y + height - _insets.bottom + 1);
            g.drawLine(x, y + height - _insets.bottom + 2, x + width, y + height - _insets.bottom + 2);
            g.setColor(_shadow);
            g.drawLine(x, y + height - _insets.bottom + 3, x + width, y + height - _insets.bottom + 3);
            g.setColor(_darkShadow);
            g.drawLine(x, y + height - _insets.bottom + 4, x + width, y + height - _insets.bottom + 4);

            paintGradient(g, new Rectangle(x, y + height - _insets.bottom + 4, width, _insets.bottom - 5), true, 100, true);
        }

        if (_insets.right >= SHADOW_SIZE) {
            g.setColor(XertoUtils.getFrameBorderColor());
            g.drawRect(x, y, width - _insets.right, height);

            g.setColor(_highlight);
            g.drawLine(x + width - _insets.right + 1, y, x + width - _insets.right + 1, y + height);
            g.drawLine(x + width - _insets.right + 2, y, x + width - _insets.right + 2, y + height);
            g.setColor(_shadow);
            g.drawLine(x + width - _insets.right + 3, y, x + width - _insets.right + 3, y + height);
            g.setColor(_darkShadow);
            g.drawLine(x + width - _insets.right + 4, y, x + width - _insets.right + 4, y + height);

            paintGradient(g, new Rectangle(x + width - _insets.right + 4, y, _insets.right - 5, height), false, 100, true);
        }
    }

    public static void paintGradient(Graphics g, Rectangle rect, boolean isVertical, int darkness, boolean lighter) {
        if (isVertical) {
            for (int i = 1; i < rect.height; i++) {
                int iAlpha = (int) ((1 - Math.log(i) / LOG10) * darkness);
                g.setColor(new Color(0, 0, 0, iAlpha));
                if (lighter) {
                    g.drawLine(rect.x, rect.y + i, rect.x + rect.width, rect.y + i);
                }
                else {
                    g.drawLine(rect.x, rect.y + rect.height - i, rect.x + rect.width, rect.y + rect.height - i);
                }
            }
        }
        else {
            for (int i = 1; i < rect.width; i++) {
                int iAlpha = (int) ((1 - Math.log(i) / LOG10) * darkness);
                g.setColor(new Color(0, 0, 0, iAlpha));
                if (lighter) {
                    g.drawLine(rect.x + i, rect.y, rect.x + i, rect.y + rect.height);
                }
                else {
                    g.drawLine(rect.x + rect.width - i, rect.y, rect.x + rect.width - i, rect.y + rect.height);
                }
            }
        }
    }
}
