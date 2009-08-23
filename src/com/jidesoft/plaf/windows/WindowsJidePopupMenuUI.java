/*
 * @(#)WindowsJidePopupMenuUI.java 12/13/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.windows;

import com.jidesoft.plaf.basic.BasicJidePopupMenuUI;
import com.sun.java.swing.plaf.windows.WindowsPopupMenuUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

public class WindowsJidePopupMenuUI extends WindowsPopupMenuUI {
    public WindowsJidePopupMenuUI() {
    }

    public static ComponentUI createUI(JComponent c) {
        return new WindowsJidePopupMenuUI();
    }

    @Override
    public Popup getPopup(JPopupMenu popupMenu, int x, int y) {
        Popup popup = BasicJidePopupMenuUI.addScrollPaneIfNecessary(popupMenu, x, y);
        return popup == null ? super.getPopup(popupMenu, x, y) : popup;
    }
}
