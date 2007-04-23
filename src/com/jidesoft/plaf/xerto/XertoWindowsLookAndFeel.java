/*
 * @(#)XertoWindowsLookAndFeel.java 9/27/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.xerto;

import com.jidesoft.utils.SystemInfo;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.*;

/**
 * WindowsLookAndFeel with Xerto extension
 *
 * @deprecated Due to the change in LookAndFeelFactory, this class is no longer needed.
 *             The best way to set any L&F is to use UIManager.setLookAndFeel() as usual. then call
 *             installJideExtension() or installJideExtension(int style) to add UIDefaults needed by JIDE products.
 */
public class XertoWindowsLookAndFeel extends WindowsLookAndFeel {
    public String getName() {
        return "Xerto";
    }

    public String getDescription() {
        return "The Xerto Look And Feel";
    }

    public String getID() {
        return "Xerto";
    }

    public boolean isSupportedLookAndFeel() {
        return isNativeLookAndFeel();
    }

    public boolean isNativeLookAndFeel() {
        return SystemInfo.isWindows();
    }

    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        XertoWindowsUtils.initClassDefaultsWithMenu(table);
    }

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        XertoWindowsUtils.initComponentDefaultsWithMenu(table);
    }
}
