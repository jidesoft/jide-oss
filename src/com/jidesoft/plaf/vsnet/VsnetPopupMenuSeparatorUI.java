/*
 * @(#)VsnetPopupMenuSeparatorUI.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.vsnet;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.ThemePainter;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;
import java.awt.*;

/**
 * PopupMenuSeparator UI implementation
 */
public class VsnetPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI {
    public static final int HEIGHT = 3;
    private ThemePainter _painter;

    public static ComponentUI createUI(JComponent c) {
        return new VsnetPopupMenuSeparatorUI();
    }

    @Override
    protected void installDefaults(JSeparator s) {
        _painter = (ThemePainter) UIDefaultsLookup.get("Theme.painter");
        super.installDefaults(s);
    }

    @Override
    protected void uninstallDefaults(JSeparator s) {
        super.uninstallDefaults(s);
        _painter = null;
    }

    public ThemePainter getPainter() {
        return _painter;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        if (!(c.getParent() instanceof JPopupMenu)) {
            super.paint(g, c);
            return;
        }

        getPainter().paintPopupMenuSepartor(c, g, new Rectangle(0, 0, c.getWidth(), c.getHeight()), SwingConstants.HORIZONTAL, ThemePainter.STATE_DEFAULT);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(0, HEIGHT);
    }

}
