/*
 * MeterProgressBar.java
 * 
 * Created on 2007-10-2, 15:01:03
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jidesoft.swing;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;

public class MeterProgressBar extends JProgressBar {

    /**
     * @see #getUIClassID
     * @see #writeObject
     */
    private static final String uiClassID = "MeterProgressBarUI";

    public static final String PROPERTY_STYLE = "style";
    public static final int STYLE_PLAIN = 0;
    public static final int STYLE_GRADIENT = 1;

    /**
     * Holds value of property style.
     */
    private int _style = STYLE_GRADIENT;

    public MeterProgressBar() {
        super();
    }

    public MeterProgressBar(int orient) {
        super(orient);
    }

    public MeterProgressBar(int min, int max) {
        super(min, max);
    }

    public MeterProgressBar(int orient, int min, int max) {
        super(orient, min, max);
    }

    public MeterProgressBar(BoundedRangeModel newModel) {
        super(newModel);
    }

    /**
     * Returns a string that specifies the name of the l&f class that renders this component.
     *
     * @return String "MeterProgressBarUI"
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    @Override
    public void updateUI() {
        if (UIDefaultsLookup.get(uiClassID) == null) {
            LookAndFeelFactory.installJideExtension();
        }
        setUI(UIManager.getUI(this));
    }

    /**
     * Getter for property style.
     *
     * @return Value of property style.
     */
    public int getStyle() {
        return _style;
    }

    /**
     * Setter for property style.
     *
     * @param style New value of property style.
     */
    public void setStyle(int style) {
        if (style == STYLE_PLAIN || style == STYLE_GRADIENT) {
            if (_style != style) {
                int oldValue = _style;
                _style = style;
                firePropertyChange(PROPERTY_STYLE, oldValue, style);
            }
        }
        else {
            throw new IllegalArgumentException("style can be only PLAIN_STYLE or GRADIENT_STYLE");
        }
    }

}
