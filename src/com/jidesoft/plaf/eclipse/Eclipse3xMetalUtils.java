/*
 * @(#)Eclipse3xMetalUtils.java 11/16/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.eclipse;

import com.jidesoft.swing.JideTabbedPane;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.InsetsUIResource;

/**
 * Utility Class for WindowsLookAndFeel to add Eclipse3x related LookAndFeel style
 */
public class Eclipse3xMetalUtils extends EclipseMetalUtils {

    /**
     * Initializes class defaults.
     *
     * @param table
     */
    public static void initClassDefaults(UIDefaults table) {
        EclipseLookAndFeelExtension.initClassDefaults(table);
        table.put("JideTabbedPaneUI", "com.jidesoft.plaf.eclipse.Eclipse3xJideTabbedPaneUI");
    }

    /**
     * Initializes components defaults.
     *
     * @param table
     */
    public static void initComponentDefaults(UIDefaults table) {
        EclipseMetalUtils.initComponentDefaults(table);
        initComponentDefaultsForEclipse3x(table);
    }

    private static void initComponentDefaultsForEclipse3x(UIDefaults table) {
        Object uiDefaults[] = {
                "JideTabbedPane.defaultTabShape", JideTabbedPane.SHAPE_ECLIPSE3X,
                "JideTabbedPane.defaultTabColorTheme", JideTabbedPane.COLOR_THEME_WIN2K,
                "JideTabbedPane.defaultResizeMode", JideTabbedPane.RESIZE_MODE_NONE,
                "JideTabbedPane.closeButtonMarginSize", 10,
                "JideTabbedPane.iconMarginHorizon", 8,
                "JideTabbedPane.iconMarginVertical", 6,

                "JideTabbedPane.border", new BorderUIResource(BorderFactory.createEmptyBorder(1, 1, 1, 1)),
                "JideTabbedPane.contentBorderInsets", new InsetsUIResource(2, 2, 2, 2),
        };
        table.putDefaults(uiDefaults);
    }
}
