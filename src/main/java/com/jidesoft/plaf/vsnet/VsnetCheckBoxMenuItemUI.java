/*
 * @(#)VsnetCheckBoxMenuItemUI.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.vsnet;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;


/**
 * CheckboxMenuItem UI implementation.
 */
public class VsnetCheckBoxMenuItemUI extends VsnetMenuItemUI {

    public static ComponentUI createUI(JComponent c) {
        return new VsnetCheckBoxMenuItemUI();
    }

    @Override
    protected String getPropertyPrefix() {
        return "CheckBoxMenuItem";
    }
}








