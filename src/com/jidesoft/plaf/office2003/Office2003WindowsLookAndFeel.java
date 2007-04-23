/*
 * @(#)${NAME}
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.office2003;

import com.jidesoft.plaf.vsnet.VsnetWindowsLookAndFeel;

import javax.swing.*;

/**
 * WindowsLookAndFeel with Offfice 2003 extension
 *
 * @deprecated Due to the change in LookAndFeelFactory, this class is no longer needed.
 *             The best way to set any L&F is to use UIManager.setLookAndFeel() as usual. then call
 *             installJideExtension() or installJideExtension(int style) to add UIDefaults needed by JIDE products.
 */
public class Office2003WindowsLookAndFeel extends VsnetWindowsLookAndFeel {
    public String getName() {
        return "Office 2003";
    }

    public String getDescription() {
        return "The Microsoft Office 2003 Look And Feel";
    }

    public String getID() {
        return "Office 2003";
    }

    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        Office2003WindowsUtils.initClassDefaults(table);
    }

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        Office2003WindowsUtils.initComponentDefaults(table);
    }
}
