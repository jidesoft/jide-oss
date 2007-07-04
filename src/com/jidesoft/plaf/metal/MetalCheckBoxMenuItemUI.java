/*
 * @(#)${NAME}
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.metal;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;


/**
 * CheckboxMenuItem UI implementation.
 */
public class MetalCheckBoxMenuItemUI extends MetalMenuItemUI {

    public static ComponentUI createUI(JComponent c) {
        return new MetalCheckBoxMenuItemUI();
    }

    @Override
    protected String getPropertyPrefix() {
        return "CheckBoxMenuItem";
    }
}








