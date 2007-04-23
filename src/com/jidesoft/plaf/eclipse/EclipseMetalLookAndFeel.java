/*
 * @(#)VsnetMetalLookAndFeel.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.eclipse;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * MetalLookAndFeel with Eclipse extension
 *
 * @deprecated Due to the change in LookAndFeelFactory, this class is no longer needed.
 *             The best way to set any L&F is to use UIManager.setLookAndFeel() as usual. then call
 *             installJideExtension() or installJideExtension(int style) to add UIDefaults needed by JIDE products.
 */
public class EclipseMetalLookAndFeel extends MetalLookAndFeel {
    public String getName() {
        return "Eclipse";
    }

    public String getDescription() {
        return "The Eclipse Look And Feel";
    }

    public String getID() {
        return "Eclipse";
    }

    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        EclipseMetalUtils.initClassDefaults(table);
    }

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        EclipseMetalUtils.initComponentDefaults(table);
    }
}
