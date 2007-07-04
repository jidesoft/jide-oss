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
    @Override
    public String getName() {
        return "Eclipse";
    }

    @Override
    public String getDescription() {
        return "The Eclipse Look And Feel";
    }

    @Override
    public String getID() {
        return "Eclipse";
    }

    @Override
    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        EclipseMetalUtils.initClassDefaults(table);
    }

    @Override
    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        EclipseMetalUtils.initComponentDefaults(table);
    }
}
