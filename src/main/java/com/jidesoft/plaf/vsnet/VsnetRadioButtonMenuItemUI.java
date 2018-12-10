/*
 * @(#)VsnetRadioButtonMenuItemUI.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.vsnet;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 * RadioButtonMenuItem UI implementation
 */
public class VsnetRadioButtonMenuItemUI extends VsnetMenuItemUI {
    public static ComponentUI createUI(JComponent b) {
        return new VsnetRadioButtonMenuItemUI();
    }

    @Override
    protected String getPropertyPrefix() {
        return "RadioButtonMenuItem";
    }
}

