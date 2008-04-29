/*
 * @(#)RangeSlider.java 11/22/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;

/**
 * <tt>RangeSlider</tt> is a slider that can be used to select a range. A regular slider has only one thumb. So it can
 * only be used to select one value. <tt>RangeSlider</tt> has two thumbs. Each one can be moved independently or both
 * are moved together.
 * <p/>
 * {@link #getLowValue()} will return the value of low range and {@link #getHighValue()} is the high range.
 */
public class RangeSlider extends JSlider {

    private static final String uiClassID = "RangeSliderUI";

    /**
     * Creates a horizontal range slider with the range 0 to 100 and initial low and high values both at 50.
     */
    public RangeSlider() {
    }

    /**
     * Creates a range slider using the specified orientation with the range 0 to 100 and initial low and high values
     * both at 50.
     *
     * @param orientation the orientation of the <code>RangeSlider</code>.
     */
    public RangeSlider(int orientation) {
        super(orientation);
    }

    /**
     * Creates a horizontal slider using the specified min and max with an initial value equal to the average of the min
     * plus max. and initial low and high values both at 50.
     *
     * @param min the minimum value of the slider.
     * @param max the maximum value of the slider.
     */
    public RangeSlider(int min, int max) {
        super(min, max);
    }

    /**
     * Creates a horizontal slider using the specified min, max, low and high value.
     *
     * @param min  the minimum value of the slider.
     * @param max  the maximum value of the slider.
     * @param low  the low value of the slider since it is a range.
     * @param high the high value of the slider since it is a range.
     */
    public RangeSlider(int min, int max, int low, int high) {
        super(new DefaultBoundedRangeModel(low, high - low,
                min, max));
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see javax.swing.JComponent#updateUI
     */
    @Override
    public void updateUI() {
        if (UIDefaultsLookup.get(uiClassID) == null) {
            LookAndFeelFactory.installJideExtension();
        }
        setUI(UIManager.getUI(this));
    }


    /**
     * Returns a string that specifies the name of the L&F class that renders this component.
     *
     * @return the string "RangeSliderUI"
     * @see javax.swing.JComponent#getUIClassID
     * @see javax.swing.UIDefaults#getUI
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Returns the range slider's low value.
     *
     * @return the range slider's low value.
     */
    public int getLowValue() {
        return getModel().getValue();
    }

    /**
     * Returns the range slider's high value.
     *
     * @return the range slider's high value.
     */
    public int getHighValue() {
        return getModel().getValue() + getModel().getExtent();
    }

    /**
     * Returns true if the specified value is within the range slider's range.
     *
     * @param value value
     * @return true if the specified value is within the range slider's range.
     */
    public boolean contains(int value) {
        return (value >= getLowValue() && value <= getHighValue());
    }

    /**
     * Sets the range slider's low value.  This method just forwards the value to the model.
     *
     * @param lowValue the new low value
     */
    public void setLowValue(int lowValue) {
        int high;
        if ((lowValue + getModel().getExtent()) > getMaximum()) {
            high = getMaximum();
        }
        else {
            high = getHighValue();
        }
        int extent = high - lowValue;

        getModel().setRangeProperties(lowValue, extent,
                getMinimum(), getMaximum(), true);
    }

    /**
     * Sets the range slider's high value.  This method just forwards the value to the model.
     *
     * @param highValue the new high value
     */
    public void setHighValue(int highValue) {
        getModel().setExtent(highValue - getLowValue());
    }
}
