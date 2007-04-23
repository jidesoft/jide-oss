/*
 * @(#)Office2003RangeSliderUI.java 12/7/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.office2003;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.BasicRangeSliderUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

public class Office2003RangeSliderUI extends BasicRangeSliderUI {

    public Office2003RangeSliderUI(JSlider slider) {
        super(slider);
    }

    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent slider) {
        return new Office2003RangeSliderUI((JSlider) slider);
    }

    protected void setMouseRollover(int handle) {
        resetAllIcons();
        switch (handle) {
            case MOUSE_HANDLE_MIN:
                _lowerIcon = UIDefaultsLookup.getIcon("RangeSlider.lowerRIcon");
                _lowerIconV = UIDefaultsLookup.getIcon("RangeSlider.lowerVRIcon");
                break;
            case MOUSE_HANDLE_MAX:
                _upperIcon = UIDefaultsLookup.getIcon("RangeSlider.upperRIcon");
                _upperIconV = UIDefaultsLookup.getIcon("RangeSlider.upperVRIcon");
                break;
            case MOUSE_HANDLE_MIDDLE:
                _middleIcon = UIDefaultsLookup.getIcon("RangeSlider.middleRIcon");
                _middleIconV = UIDefaultsLookup.getIcon("RangeSlider.middleVRIcon");
                break;
            case MOUSE_HANDLE_NONE:
                break;
        }
        slider.repaint();
    }
}
