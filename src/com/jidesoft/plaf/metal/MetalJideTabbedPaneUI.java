/*
 * @(#)WindowsTabbedPaneUI.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.metal;

import com.jidesoft.plaf.vsnet.VsnetJideTabbedPaneUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;


/**
 * JideTabbedPane UI implementation
 */
public class MetalJideTabbedPaneUI extends VsnetJideTabbedPaneUI {
    public static ComponentUI createUI(JComponent c) {
        return new MetalJideTabbedPaneUI();
    }
}



