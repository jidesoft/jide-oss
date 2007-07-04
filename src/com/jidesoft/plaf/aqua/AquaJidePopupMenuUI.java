/*
 * @(#)AquaJidePopupMenuUI.java 12/13/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.aqua;

import apple.laf.AquaPopupMenuUI;
import com.jidesoft.swing.SimpleScrollPane;

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
        PopupFactory popupFactory = PopupFactory.getSharedInstance();
        SimpleScrollPane contents = new SimpleScrollPane(popupMenu, SimpleScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, SimpleScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contents.getScrollUpButton().setOpaque(true);
        contents.getScrollDownButton().setOpaque(true);
        contents.setBorder(BorderFactory.createEmptyBorder());
        return popupFactory.getPopup(popupMenu.getInvoker(), contents, x, y);
    }
}
