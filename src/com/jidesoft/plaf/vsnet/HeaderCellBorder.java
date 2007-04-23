/*
 * @(#)HeaderCellBorder.java 8/3/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.vsnet;

import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import java.awt.*;

public class HeaderCellBorder implements Border, UIResource {
    public HeaderCellBorder() {
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(new Color(226, 222, 205));
        g.drawLine(0, height - 3, width, height - 3);

        g.setColor(new Color(214, 210, 194));
        g.drawLine(0, height - 2, width, height - 2);

        g.setColor(new Color(203, 199, 184));
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
