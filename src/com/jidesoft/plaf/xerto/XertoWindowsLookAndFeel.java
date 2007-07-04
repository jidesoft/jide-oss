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
    @Override
    public String getName() {
        return "Xerto";
    }

    @Override
    public String getDescription() {
        return "The Xerto Look And Feel";
    }

    @Override
    public String getID() {
        return "Xerto";
    }

    @Override
    public boolean isSupportedLookAndFeel() {
        return isNativeLookAndFeel();
    }

    @Override
    public boolean isNativeLookAndFeel() {
        return SystemInfo.isWindows();
    }

    @Override
    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        XertoWindowsUtils.initClassDefaultsWithMenu(table);
    }

    @Override
    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        XertoWindowsUtils.initComponentDefaultsWithMenu(table);
    }
}
