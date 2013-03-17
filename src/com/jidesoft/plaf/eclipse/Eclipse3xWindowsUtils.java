/*
 * @(#)Eclipse3xWindowsUtils.java 3/17/2013
 *
 * Copyright 2002 - 2013 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.eclipse;

import com.jidesoft.swing.JideTabbedPane;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.InsetsUIResource;

/**
 * Utility Class for WindowsLookAndFeel to add Eclipse3x related LookAndFeel style
 */
public class Eclipse3xWindowsUtils extends EclipseWindowsUtils {

    /**
     * Initializes class defaults with menu components UIDefaults.
     *
     * @param table
     */
    public static void initClassDefaultsWithMenu(UIDefaults table) {
        EclipseWindowsUtils.initClassDefaultsWithMenu(table);
        initClassDefaults(table);
    }

    /**
     * Initializes class defaults.
     *
     * @param table
     */
    public static void initClassDefaults(UIDefaults table) {
        EclipseWindowsUtils.initClassDefaults(table);
        table.put("JideTabbedPaneUI", "com.jidesoft.plaf.eclipse.Eclipse3xJideTabbedPaneUI");
    }

    /**
     * Initializes components defaults.
     *
     * @param table
     */
    public static void initComponentDefaults(UIDefaults table) {
        EclipseWindowsUtils.initComponentDefaults(table);
        initComponentDefaultsForEclipse3x(table);
    }

    /**
     * Initializes components defaults with menu components UIDefaults.
     *
     * @param table
     */
    public static void initComponentDefaultsWithMenu(UIDefaults table) {
        EclipseWindowsUtils.initComponentDefaultsWithMenu(table);
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
