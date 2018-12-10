/*
 * @(#)FrameBorder.java
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.eclipse;

import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import java.awt.*;

/**
 * This class is an implementation of the DockableFrame border.
 */
class FrameBorder implements Border, UIResource {
    protected Color _highlight;
    protected Color _lightHighlight;
    protected Color _shadow;
    protected Color _darkShadow;
    protected Insets _insets;

    public FrameBorder(Color highlight, Color lightHighlight,
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
     * Returns whether or not the border is opaque.  If the border
     * is opaque, it is responsible for filling in it's own
     * background when painting.
     */
    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y,
                            int width, int height) {
        if (_insets.top > 0) {
            g.setColor(_highlight);
            g.drawLine(x, y, x + width, y);

            g.setColor(_lightHighlight);
            g.drawLine(x + 1, y + 1, x + width - 2, y + 1);
            if (_insets.left == 0) {
                g.drawLine(x, y + 1, x + 1, y + 1);
            }
            if (_insets.right == 0) {
                g.drawLine(x + width - 2, y + 1, x + width - 1, y + 1);
            }

            g.setColor(_highlight);
            g.drawLine(x + 2, y + 2, x + width - 4, y + 2);
            g.drawLine(x + 2, y + 3, x + width - 4, y + 3);

            if (_insets.left == 0) {
                g.drawLine(x, y + 2, x + 2, y + 2);
                g.drawLine(x, y + 3, x + 2, y + 3);
            }
            if (_insets.right == 0) {
                g.drawLine(x + width - 4, y + 2, x + width - 1, y + 2);
                g.drawLine(x + width - 4, y + 3, x + width - 1, y + 3);
            }
        }

        if (_insets.left > 0) {
            g.setColor(_highlight);
            g.drawLine(x, y, x, y + height);

            g.setColor(_lightHighlight);
            g.drawLine(x + 1, y + 1, x + 1, y + height - 2);

            if (_insets.top == 0) {
                g.drawLine(x + 1, y, x + 1, y + 1);
            }
            if (_insets.bottom == 0) {
                g.drawLine(x + 1, y + height - 2, x + 1, y + height - 1);
            }

            g.setColor(_highlight);
            g.drawLine(x + 2, y + 2, x + 2, y + height - 4);
            g.drawLine(x + 3, y + 2, x + 3, y + height - 4);
            if (_insets.top == 0) {
                g.drawLine(x + 2, y, x + 2, y + 2);
                g.drawLine(x + 3, y, x + 3, y + 2);
            }
            if (_insets.bottom == 0) {
                g.drawLine(x + 2, y + height - 4, x + 2, y + height - 1);
                g.drawLine(x + 3, y + height - 4, x + 3, y + height - 1);
            }
        }

        if (_insets.bottom > 0) {
            g.setColor(_darkShadow);
            g.drawLine(x, y + height - 1, x + width, y + height - 1);

            g.setColor(_shadow);
            g.drawLine(x + 1, y + height - 2, x + width - 2, y + height - 2);
            if (_insets.left == 0) {
                g.drawLine(x, y + height - 2, x + 1, y + height - 2);
            }
            if (_insets.right == 0) {
                g.drawLine(x + width - 2, y + height - 2, x + width - 1, y + height - 2);
            }

            g.setColor(_highlight);
            g.drawLine(x + 2, y + height - 3, x + width - 4, y + height - 3);
            g.drawLine(x + 2, y + height - 4, x + width - 4, y + height - 4);
            if (_insets.left == 0) {
                g.drawLine(x, y + height - 3, x + 2, y + height - 3);
                g.drawLine(x, y + height - 4, x + 2, y + height - 4);
            }
            if (_insets.right == 0) {
                g.drawLine(x + width - 4, y + height - 3, x + width - 1, y + height - 3);
                g.drawLine(x + width - 4, y + height - 4, x + width - 1, y + height - 4);
            }
        }

        if (_insets.right > 0) {
            g.setColor(_darkShadow);
            g.drawLine(x + width - 1, y, x + width - 1, y + height);

            g.setColor(_shadow);
            g.drawLine(x + width - 2, y + 1, x + width - 2, y + height - 2);
            if (_insets.top == 0) {
                g.drawLine(x + width - 2, y, x + width - 2, y + 1);
            }
            if (_insets.bottom == 0) {
                g.drawLine(x + width - 2, y + height - 2, x + width - 2, y + height - 1);
            }

            g.setColor(_highlight);
            g.drawLine(x + width - 3, y + 2, x + width - 3, y + height - 4);
            g.drawLine(x + width - 4, y + 2, x + width - 4, y + height - 4);
            if (_insets.top == 0) {
                g.drawLine(x + width - 3, y, x + width - 3, y + 2);
                g.drawLine(x + width - 4, y, x + width - 4, y + 2);
            }
            if (_insets.bottom == 0) {
                g.drawLine(x + width - 3, y + height - 4, x + width - 3, y + height - 1);
                g.drawLine(x + width - 4, y + height - 4, x + width - 4, y + height - 1);
            }
        }
    }
}
