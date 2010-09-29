/*
 * @(#)AquaJidePopupMenuUI.java 12/13/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.aqua;

import com.apple.laf.AquaPopupMenuUI;
import com.jidesoft.plaf.basic.BasicJidePopupMenuUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

public class AquaJidePopupMenuUI extends AquaPopupMenuUI {
    public AquaJidePopupMenuUI() {
    }

    public static ComponentUI createUI(JComponent c) {
        return new AquaJidePopupMenuUI();
    }

    @Override
    public Popup getPopup(JPopupMenu popupMenu, int x, int y) {
        Popup popup = BasicJidePopupMenuUI.addScrollPaneIfNecessary(popupMenu, x, y);
        return popup == null ? super.getPopup(popupMenu, x, y) : popup;
    }
}
