/*
 * @(#)HeaderCellBorder.java 8/3/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.vsnet;

import com.jidesoft.utils.ColorUtils;

import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import java.awt.*;

public class HeaderCellBorder implements Border, UIResource {
    public HeaderCellBorder() {
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color baseColor = c.getBackground();
        g.setColor(ColorUtils.getDerivedColor(baseColor, 0.45f));
        g.drawLine(0, height - 3, width, height - 3);

        g.setColor(ColorUtils.getDerivedColor(baseColor, 0.43f));
        g.drawLine(0, height - 2, width, height - 2);

        g.setColor(ColorUtils.getDerivedColor(baseColor, 0.40f));
        g.drawLine(0, height - 1, width, height - 1);
        g.drawLine(width - 1, 0, width - 1, height - 1);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 3, 1);
    }

    public boolean isBorderOpaque() {
        return false;
    }
}
