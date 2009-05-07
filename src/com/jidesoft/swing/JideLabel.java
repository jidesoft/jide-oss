package com.jidesoft.swing;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;

/**
 * Just like <code>JideButton</code> comparing to <code>JButton</code>, <code>JideLabel</code> is like
 * <code>JLabel</code> except it is used on <code>JToolBar</code> or <code>CommandBar</code>. However it doesn't look
 * any different from a regular <code>JLabel</code> unless you override "JideLabel.foreground", "JideLabel.background",
 * or "JideLabel.font" etc UIDefaults.
 * <p/>
 * <code>JideLabel</code> also can be used in a vertical layout. If you call {@link #setOrientation(int)} and set it to
 * {@link javax.swing.SwingConstants#VERTICAL}, the text and icon on the label will be laid out vertically. As
 * <code>CommandBar</code> supports vertical layout, this is perfect for it. You can also control the rotating direction
 * by calling {@link #setClockwise(boolean)}. By default, it rotates clockwise.
 */
public class JideLabel extends JLabel implements Alignable, AlignmentSupport {
    private static final String uiClassID = "JideLabelUI";
    public static final String PROPERTY_CLOCKWISE = "clockwise";

    private boolean _clockwise = true;
    private int _orientation;


    public JideLabel() {
    }

    public JideLabel(String text) {
        super(text);
    }

    public JideLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
    }

    public JideLabel(Icon image) {
        super(image);
    }

    public JideLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    public JideLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
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
     * Returns a string that specifies the name of the L&F class that renders this component.
     *
     * @return the string "ButtonUI"
     *
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * The button orientation.
     *
     * @return the orientation.
     */
    public int getOrientation() {
        return _orientation;
    }

    public void setOrientation(int orientation) {
        int old = _orientation;
        if (old != orientation) {
            _orientation = orientation;
            firePropertyChange(PROPERTY_ORIENTATION, old, orientation);
        }
    }

    /**
     * return true if it supports vertical orientation.
     *
     * @return true if it supports vertical orientation
     */
    public boolean supportVerticalOrientation() {
        return true;
    }

    /**
     * return true if it supports horizontal orientation.
     *
     * @return true if it supports horizontal orientation
     */
    public boolean supportHorizontalOrientation() {
        return true;
    }

    /**
     * Checks if the rotation is clockwise.
     *
     * @return true or false.
     */
    public boolean isClockwise() {
        return _clockwise;
    }

    /**
     * Sets the rotation direction.
     *
     * @param clockwise true or false.
     */
    public void setClockwise(boolean clockwise) {
        boolean old = _clockwise;
        if (clockwise != _clockwise) {
            _clockwise = clockwise;
            firePropertyChange(PROPERTY_CLOCKWISE, old, _clockwise);
        }
    }
}
