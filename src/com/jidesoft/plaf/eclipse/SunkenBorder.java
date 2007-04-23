/*
 * @(#)SunkenBorder.java 2/12/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.eclipse;

import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import java.awt.*;

public class SunkenBorder implements Border, UIResource {
    protected Color _highlight;
    protected Color _lightHighlight;
    protected Color _shadow;
    protected Color _darkShadow;
    protected Insets _insets;

    public SunkenBorder(Color highlight, Color lightHighlight,
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
            g.setColor(_shadow);
            g.drawLine(x, y, x + width, y);
        }
        if (_insets.left > 0) {
            g.setColor(_shadow);
            g.drawLine(x, y, x, y + height);
        }
        if (_insets.bottom > 0) {
            g.setColor(_lightHighlight);
            g.drawLine(x, y + height - 1, x + width, y + height - 1);
        }
        if (_insets.right > 0) {
            g.setColor(_lightHighlight);
            g.drawLine(x + width - 1, y, x + width - 1, y + height);
        }
    }
}
