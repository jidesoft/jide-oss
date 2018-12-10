/*
 * @(#)${NAME}
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.aqua;

import com.jidesoft.plaf.basic.BasicJideSplitButtonUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;


/**
 * Menu UI implementation
 */
public class AquaJideSplitButtonUI extends BasicJideSplitButtonUI {

    public static ComponentUI createUI(JComponent x) {
        return new AquaJideSplitButtonUI();
    }

    /**
     * The gap between the button part and the drop down menu part.
     *
     * @return the gap.
     */
    @Override
    protected int getOffset() {
        return 2;
    }
}



