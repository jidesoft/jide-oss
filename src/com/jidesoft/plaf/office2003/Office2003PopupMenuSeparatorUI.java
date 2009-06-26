/*
 * @(#)VsnetPopupMenuSeparatorUI.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.office2003;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.plaf.vsnet.VsnetPopupMenuSeparatorUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * PopupMenuSeparator UI implementation
 */
public class Office2003PopupMenuSeparatorUI extends VsnetPopupMenuSeparatorUI {
    public static ComponentUI createUI(JComponent c) {
        return new Office2003PopupMenuSeparatorUI();
    }

    private ThemePainter _painter;

    @Override
    protected void installDefaults(JSeparator s) {
        _painter = (ThemePainter) UIDefaultsLookup.get("Theme.painter");
        super.installDefaults(s);
    }

    @Override
    protected void uninstallDefaults(JSeparator s) {
        _painter = null;
        super.uninstallDefaults(s);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        if (!(c.getParent() instanceof JPopupMenu)) {
            super.paint(g, c);
            return;
        }

        Dimension s = c.getSize();

        int defaultShadowWidth = UIDefaultsLookup.getInt("MenuItem.shadowWidth");
        int defaultTextIconGap = UIDefaultsLookup.getInt("MenuItem.textIconGap");

        if (c.getComponentOrientation().isLeftToRight()) {
            _painter.paintMenuShadow(c, g, new Rectangle(0, 0, defaultShadowWidth, HEIGHT), SwingConstants.HORIZONTAL, ThemePainter.STATE_DEFAULT);

            g.setColor(_painter.getMenuItemBackground());
            g.fillRect(defaultShadowWidth, 0, s.width - defaultShadowWidth, HEIGHT);

            g.setColor(_painter.getSeparatorForeground());
            g.drawLine(defaultShadowWidth + defaultTextIconGap, 1, s.width, 1);
        }
        else {
            _painter.paintMenuShadow(c, g, new Rectangle(s.width - defaultShadowWidth, 0, defaultShadowWidth, HEIGHT), SwingConstants.HORIZONTAL, ThemePainter.STATE_DEFAULT);

            g.setColor(_painter.getMenuItemBackground());
            g.fillRect(0, 0, s.width - defaultShadowWidth, HEIGHT);

            g.setColor(_painter.getSeparatorForeground());
            g.drawLine(0, 1, s.width - defaultShadowWidth - defaultTextIconGap, 1);
        }
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(0, HEIGHT);
    }

}
