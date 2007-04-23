/*
 * @(#)VsnetSeparatorUI.java 3/3/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.vsnet;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.utils.SecurityUtils;
import com.sun.java.swing.plaf.windows.WindowsSeparatorUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * PopupMenuSeparator UI implementation
 */
public class VsnetSeparatorUI extends WindowsSeparatorUI {
    public static final int HEIGHT = 3;

    public static ComponentUI createUI(JComponent c) {
        return new VsnetSeparatorUI();
    }

    protected void installDefaults(JSeparator s) {
    }

    public void paint(Graphics g, JComponent c) {
        if (!(c.getParent() instanceof JPopupMenu)) {
            super.paint(g, c);
            return;
        }

        Dimension s = c.getSize();

        int defaultShadowWidth = UIDefaultsLookup.getInt("MenuItem.shadowWidth");
        int defaultTextIconGap = UIDefaultsLookup.getInt("MenuItem.textIconGap");
        Color shadowColor = UIDefaultsLookup.getColor("MenuItem.shadowColor");
        Color foreground = UIDefaultsLookup.getColor("PopupMenuSeparator.foreground");
        Color background = UIDefaultsLookup.getColor("PopupMenuSeparator.background");

        g.setColor(shadowColor);
        if (c.getComponentOrientation().isLeftToRight()) {
            g.fillRect(0, 0, defaultShadowWidth, HEIGHT);
            if ("true".equals(SecurityUtils.getProperty("shadingtheme", "false"))) {
                JideSwingUtilities.fillSingleGradient(g, new Rectangle(0, 0, defaultShadowWidth, HEIGHT), SwingConstants.EAST, 255);
            }

            g.setColor(background);
            g.fillRect(defaultShadowWidth, 0, s.width - defaultShadowWidth, HEIGHT);

            g.setColor(foreground);
            g.drawLine(defaultShadowWidth + defaultTextIconGap, 1, s.width, 1);
        }
        else {
            g.fillRect(s.width, 0, defaultShadowWidth, HEIGHT);
            if ("true".equals(SecurityUtils.getProperty("shadingtheme", "false"))) {
                JideSwingUtilities.fillSingleGradient(g, new Rectangle(s.width - defaultTextIconGap, 0, defaultShadowWidth, 2), SwingConstants.WEST, 255);
            }

            g.setColor(background);
            g.fillRect(0, 0, s.width - defaultShadowWidth, HEIGHT);

            g.setColor(foreground);
            g.drawLine(defaultTextIconGap, 1, s.width - defaultShadowWidth, 1);
        }
    }

    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(0, HEIGHT);
    }

}
