/*
 * @(#)XertoFrameBorder.java 5/12/2005
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
class XertoFrameBorder implements Border, UIResource {
    private static Color[] _colors = new Color[]{
            new Color(142, 145, 128),
            new Color(172, 172, 153),
            new Color(203, 198, 181),
            new Color(213, 207, 188)/*,
        new Color(179, 176, 154)*/
    };
    private Insets _insets;

    public XertoFrameBorder(Insets insets) {
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

        for (int i = 0; i < _colors.length; i++) {
            g.setColor(_colors[i]);
            g.drawRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1);
        }
    }
}
