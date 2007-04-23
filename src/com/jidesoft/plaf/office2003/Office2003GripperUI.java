/*
 * @(#)Office2003GripperUI.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.office2003;

import com.jidesoft.plaf.vsnet.VsnetGripperUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 */
public class Office2003GripperUI extends VsnetGripperUI {
    public static ComponentUI createUI(JComponent c) {
        return new Office2003GripperUI();
    }
}
