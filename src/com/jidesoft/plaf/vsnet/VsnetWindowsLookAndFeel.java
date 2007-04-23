/*
 * @(#)VsnetLookAndFeel.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.vsnet;

import com.jidesoft.utils.SystemInfo;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.*;

/**
 * WindowsLookAndFeel with Visual Studio .NET extension
 *
 * @deprecated Due to the change in LookAndFeelFactory, this class is no longer needed.
 *             The best way to set any L&F is to use UIManager.setLookAndFeel() as usual. then call
 *             installJideExtension() or installJideExtension(int style) to add UIDefaults needed by JIDE products.
 */
public class VsnetWindowsLookAndFeel extends WindowsLookAndFeel {
    public String getName() {
        return "Visual Studio .NET";
    }

    public String getDescription() {
        return "The Microsoft Visual Studio .NET Look And Feel";
    }

    public String getID() {
        return "Visual Studio .NET";
    }

    public boolean isSupportedLookAndFeel() {
        return isNativeLookAndFeel();
    }

    public boolean isNativeLookAndFeel() {
        return SystemInfo.isWindows();
    }

    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        VsnetWindowsUtils.initClassDefaultsWithMenu(table);
    }

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        VsnetWindowsUtils.initComponentDefaultsWithMenu(table);
    }
}
