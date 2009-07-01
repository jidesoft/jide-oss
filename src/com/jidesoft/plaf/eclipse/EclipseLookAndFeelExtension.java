/*
 * @(#)EclipseLookAndFeelExtension.java 4/15/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.eclipse;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.basic.BasicLookAndFeelExtension;

import javax.swing.*;
import java.beans.Beans;

/**
 * Utility Class for WindowsLookAndFeel to add Eclipse related LookAndFeel style
 */
public class EclipseLookAndFeelExtension extends BasicLookAndFeelExtension {

    /**
     * Initializes class defaults with menu components UIDefaults.
     *
     * @param table
     */
    public static void initClassDefaultsWithMenu(UIDefaults table) {
        if (!Beans.isDesignTime()) {
            table.put("PopupMenuSeparatorUI", "com.jidesoft.plaf.eclipse.EclipsePopupMenuSeparatorUI");
            table.put("SeparatorUI", "com.jidesoft.plaf.eclipse.EclipsePopupMenuSeparatorUI");
            table.put("MenuUI", "com.jidesoft.plaf.eclipse.EclipseMenuUI");
            table.put("MenuItemUI", "com.jidesoft.plaf.eclipse.EclipseMenuItemUI");
            table.put("CheckBoxMenuItemUI", "com.jidesoft.plaf.eclipse.EclipseCheckBoxMenuItemUI");
            table.put("RadioButtonMenuItemUI", "com.jidesoft.plaf.eclipse.EclipseRadioButtonMenuItemUI");
        }
    }

    /**
     * Initializes class defaults.
     *
     * @param table
     */
    public static void initClassDefaults(UIDefaults table) {
        BasicLookAndFeelExtension.initClassDefaults(table);

        final String eclipsePackageName = "com.jidesoft.plaf.eclipse.";

        int products = LookAndFeelFactory.getProductsUsed();

        table.put("JideTabbedPaneUI", eclipsePackageName + "EclipseJideTabbedPaneUI");
        table.put("JideSplitButtonUI", eclipsePackageName + "EclipseJideSplitButtonUI");
        table.put("GripperUI", eclipsePackageName + "EclipseGripperUI");

        if ((products & PRODUCT_DOCK) != 0) {
            table.put("SidePaneUI", eclipsePackageName + "EclipseSidePaneUI");
            table.put("DockableFrameUI", eclipsePackageName + "EclipseDockableFrameUI");
        }

        if ((products & PRODUCT_COMPONENTS) != 0) {
            table.put("CollapsiblePaneUI", eclipsePackageName + "EclipseCollapsiblePaneUI");
        }

        if ((products & PRODUCT_ACTION) != 0) {
            table.put("CommandBarUI", eclipsePackageName + "EclipseCommandBarUI");
        }
    }
}
