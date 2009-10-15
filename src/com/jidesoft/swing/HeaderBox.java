/*
 * @(#)HeaderBox.java 4/27/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;

/**
 * HeaderBox is a special component that is used in JIDE Pivot Grid product to mimic a button with table header style.
 */
public class HeaderBox extends JideButton {

    private static final String uiClassID = "HeaderBoxUI";

    public static final String CLIENT_PROPERTY_TABLE_CELL_EDITOR = "HeaderBox.isTableCellEditor";

    /**
     * Creates a button with no set text or icon.
     */
    public HeaderBox() {
        setOpaque(false);
        setRolloverEnabled(true);
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see javax.swing.JComponent#updateUI
     */
    @Override
    public void updateUI() {
        if (UIDefaultsLookup.get(uiClassID) == null) {
            LookAndFeelFactory.installJideExtension();
        }
        setUI(UIManager.getUI(this));
    }


    /**
     * Returns a string that specifies the name of the L&F class that renders this component.
     *
     * @return the string "HeaderBoxUI"
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }
}
