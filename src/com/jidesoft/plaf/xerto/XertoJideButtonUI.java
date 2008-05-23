/*
 * @(#)XertoJideButtonUI.java 5/6/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.xerto;

import com.jidesoft.plaf.basic.BasicJideButtonUI;
import com.sun.java.swing.plaf.windows.WindowsButtonUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 */
public class XertoJideButtonUI extends BasicJideButtonUI {
    private static WindowsButtonUI _buttonUI = new WindowsButtonUI();

    public static ComponentUI createUI(JComponent c) {
        return new XertoJideButtonUI();
    }

    // ********************************
    //          Install PLAF
    // ********************************
    @Override
    public void installUI(JComponent c) {
        _buttonUI.installUI(c);
        if (c instanceof JButton) {
            c.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
            ((JButton) c).setRolloverEnabled(true);
        }
    }

    @Override
    public void uninstallUI(JComponent c) {
        _buttonUI.uninstallUI(c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        _buttonUI.paint(g, c);
    }
}
