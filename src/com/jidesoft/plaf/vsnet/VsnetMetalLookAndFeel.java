/*
 * @(#)VsnetMetalLookAndFeel.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.vsnet;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * MetalLookAndFeel with Vsnet extension
 *
 * @deprecated Due to the change in LookAndFeelFactory, this class is no longer needed.
 *             The best way to set any L&F is to use UIManager.setLookAndFeel() as usual. then call
 *             installJideExtension() or installJideExtension(int style) to add UIDefaults needed by JIDE products.
 */
public class VsnetMetalLookAndFeel extends MetalLookAndFeel {
    @Override
    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        VsnetMetalUtils.initClassDefaults(table);
    }

    @Override
    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        VsnetMetalUtils.initComponentDefaults(table);
    }
}
