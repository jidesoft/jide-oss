/*
 * @(#)EclipsePopupMenuSeparatorUI.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.eclipse;

import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;
import java.awt.*;

/**
 * PopupMenuSeparator UI implementation
 */
public class EclipsePopupMenuSeparatorUI extends BasicSeparatorUI {
    public static ComponentUI createUI(JComponent c) {
        return new EclipsePopupMenuSeparatorUI();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        if (!(c.getParent() instanceof JPopupMenu)) {
            super.paint(g, c);
        }

        Dimension s = c.getSize();

        Color foreground = UIDefaultsLookup.getColor("PopupMenuSeparator.foreground");
        Color background = UIDefaultsLookup.getColor("PopupMenuSeparator.background");

        g.setColor(background);
        g.drawLine(1, 0, s.width - 2, 0);

        g.setColor(foreground);
        g.drawLine(1, 1, s.width - 2, 1);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(0, 2);
    }

}
