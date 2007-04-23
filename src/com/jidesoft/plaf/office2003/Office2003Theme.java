/*
 * @(#)Office2003Theme.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.office2003;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

/**
 */
public class Office2003Theme extends UIDefaults {
    private String _themeName;

    public final Object[] _uiDefaultsSelection = new Object[]{
            "selection.Rollover", new ColorUIResource(255, 238, 194), // focused
            "selection.RolloverLt", new ColorUIResource(255, 244, 204),
            "selection.RolloverDk", new ColorUIResource(255, 208, 145),

            "selection.Selected", new ColorUIResource(255, 192, 111), // selected;
            "selection.SelectedLt", new ColorUIResource(255, 213, 140),
            "selection.SelectedDk", new ColorUIResource(255, 173, 85),

            "selection.Pressed", new ColorUIResource(254, 128, 62), // focused and selected;
            "selection.PressedLt", new ColorUIResource(255, 211, 142),
            "selection.PressedDk", new ColorUIResource(254, 145, 78)
    };

    public Office2003Theme(String themeName) {
        _themeName = themeName;
        putDefaults(_uiDefaultsSelection);
    }

    public String getThemeName() {
        return _themeName;
    }

    public void setThemeName(String themeName) {
        _themeName = themeName;
    }
}
