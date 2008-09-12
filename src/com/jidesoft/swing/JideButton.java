/*
 * @(#)JideButton.java
 *
 * Copyright 2002-2003 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.ThemePainter;

import javax.swing.*;
import java.awt.*;

/**
 * JideButton is a replacement for JButton when it is used on toolbar (or command bar in the case of JIDE Action
 * Framework).
 */
public class JideButton extends JButton implements Alignable, ButtonStyle, ComponentStateSupport, AlignmentSupport {

    private static final String uiClassID = "JideButtonUI";

    /**
     * Bound property name for always show hyperlink property.
     */
    public static final String PROPERTY_ALWAYS_SHOW_HYPERLINK = "alwaysShowHyperlink";

    private boolean _alwaysShowHyperlink = false;

    private int _buttonStyle = TOOLBAR_STYLE;

    /**
     * By default, if a JideButton is added to a popup menu, clicking on the button will dismiss the popup menu. However
     * if you change the default behavior, you can use this client property and set it to Boolean.FALSE.
     */
    public static final String CLIENT_PROPERTY_HIDE_POPUPMENU = "JideButton.hidePopupMenu";

    /**
     * Creates a button with no set text or icon.
     */
    public JideButton() {
        this(null, null);
    }

    /**
     * Creates a button with an icon.
     *
     * @param icon the Icon image to display on the button
     */
    public JideButton(Icon icon) {
        this(null, icon);
    }

    /**
     * Creates a button with text.
     *
     * @param text the text of the button
     */
    public JideButton(String text) {
        this(text, null);
    }

    /**
     * Creates a button where properties are taken from the <code>Action</code> supplied.
     *
     * @param a the <code>Action</code> used to specify the new button
     * @since 1.3
     */
    public JideButton(Action a) {
        this();
        setAction(a);
    }

    /**
     * Creates a button with initial text and an icon.
     *
     * @param text the text of the button
     * @param icon the Icon image to display on the button
     */
    public JideButton(String text, Icon icon) {
        // Create the model
        setModel(new DefaultButtonModel());
        // initialize
        init(text, icon);
        setRolloverEnabled(true);
        setFocusable(true);
        setRequestFocusEnabled(false);
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

    private int _orientation;

    /**
     * The button orientation.
     *
     * @return the orientation.
     */
    public int getOrientation() {
        return _orientation;
    }

    public void setOrientation(int orientation) {
        if (_orientation != orientation) {
            int old = _orientation;
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
     * Gets the button style.
     *
     * @return the button style.
     */
    public int getButtonStyle() {
        return _buttonStyle;
    }

    /**
     * Sets the button style.
     *
     * @param buttonStyle one of the following values: {@link #TOOLBAR_STYLE} (default), {@link #TOOLBOX_STYLE}, {@link
     *                    #FLAT_STYLE} and {@link #HYPERLINK_STYLE}.
     */
    public void setButtonStyle(int buttonStyle) {
        if (buttonStyle < 0 || buttonStyle > HYPERLINK_STYLE) {
            throw new IllegalArgumentException("Only TOOLBAR_STYLE, TOOLBOX_STYLE, FLAT_STYLE and HYPERLINK_STYLE are supported");
        }
        if (buttonStyle == _buttonStyle)
            return;

        int oldStyle = _buttonStyle;
        _buttonStyle = buttonStyle;

        firePropertyChange(BUTTON_STYLE_PROPERTY, oldStyle, _buttonStyle);
    }

    /**
     * Checks the alwaysShowHyperlink property value.
     *
     * @return true if the hyperlink is always visible. False if the hyperlink will be visible only when mouse rolls
     *         over.
     */
    public boolean isAlwaysShowHyperlink() {
        return _alwaysShowHyperlink;
    }

    /**
     * Sets the property if hyperlink (the underline) should be visible all the time. By default the hyperlink is
     * visible when mouse is over the button. If set to true, the hyperlink will always be visible.
     * <p/>
     * Please notes, this is an option only available when button style is set to HYPERLINK_STYLE.
     *
     * @param alwaysShowHyperlink a boolean value. True means the button will always show hyperlink. False means it will
     *                            show hyperlink only when mouse is over the button.
     */
    public void setAlwaysShowHyperlink(boolean alwaysShowHyperlink) {
        if (_alwaysShowHyperlink != alwaysShowHyperlink) {
            boolean old = _alwaysShowHyperlink;
            _alwaysShowHyperlink = alwaysShowHyperlink;
            firePropertyChange(PROPERTY_ALWAYS_SHOW_HYPERLINK, old, alwaysShowHyperlink);
        }
    }

    @Override
    public Cursor getCursor() {
        if (getButtonStyle() == HYPERLINK_STYLE
                && isRolloverEnabled() && getModel().isRollover()
                && ((getText() != null && getText().length() > 0) || getIcon() != null)) {
            return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        }
        else {
            return super.getCursor();
        }
    }

    private Color _defaultForeground;
    private Color _rolloverBackground;
    private Color _selectedBackground;
    private Color _pressedBackground;
    private Color _rolloverForeground;
    private Color _selectedForeground;
    private Color _pressedForeground;

    public Color getDefaultForeground() {
        return _defaultForeground;
    }

    public void setDefaultForeground(Color defaultForeground) {
        _defaultForeground = defaultForeground;
    }

    private Color getRolloverBackground() {
        return _rolloverBackground;
    }

    private void setRolloverBackground(Color rolloverBackground) {
        _rolloverBackground = rolloverBackground;
    }

    private Color getSelectedBackground() {
        return _selectedBackground;
    }

    private void setSelectedBackground(Color selectedBackground) {
        _selectedBackground = selectedBackground;
    }

    private Color getPressedBackground() {
        return _pressedBackground;
    }

    private void setPressedBackground(Color pressedBackground) {
        _pressedBackground = pressedBackground;
    }

    private Color getRolloverForeground() {
        return _rolloverForeground;
    }

    private void setRolloverForeground(Color rolloverForeground) {
        _rolloverForeground = rolloverForeground;
    }

    private Color getSelectedForeground() {
        return _selectedForeground;
    }

    private void setSelectedForeground(Color selectedForeground) {
        _selectedForeground = selectedForeground;
    }

    private Color getPressedForeground() {
        return _pressedForeground;
    }

    private void setPressedForeground(Color pressedForeground) {
        _pressedForeground = pressedForeground;
    }

    /**
     * Gets the background for different states. The states are defined in ThemePainter as constants. Not all states are
     * supported by all components. If the state is not supported or background is never set, it will return null.
     * <p/>
     * Please note, each L&F will have its own way to paint the different backgrounds. This method allows you to
     * customize it for each component to use a different background. So if you want the background to be used, don't
     * use a ColorUIResource because UIResource is considered as a setting set by the L&F and any L&F can choose to
     * ignore it.
     *
     * @param state the button state. Valid values are ThemePainter.STATE_DEFAULT, ThemePainter.STATE_ROLLOVER,
     *              ThemePainter.STATE_SELECTED and ThemePainter.STATE_PRESSED.
     * @return the background for different states.
     */
    public Color getBackgroundOfState(int state) {
        switch (state) {
            case ThemePainter.STATE_DEFAULT:
                return getBackground();
            case ThemePainter.STATE_ROLLOVER:
                return getRolloverBackground();
            case ThemePainter.STATE_SELECTED:
                return getSelectedBackground();
            case ThemePainter.STATE_PRESSED:
                return getPressedBackground();
        }
        return null;
    }

    /**
     * Sets the background for different states.  The states are defined in ThemePainter as constants. Not all states
     * are supported by all components. If the state is not supported or background is never set, it will return null.
     * <p/>
     * Please note, each L&F will have its own way to paint the different backgrounds. This method allows you to
     * customize it for each component to use a different background. So if you want the background to be used, don't
     * use a ColorUIResource because UIResource is considered as a setting set by the L&F and any L&F can choose to
     * ignore it.
     *
     * @param state the button state. Valid values are ThemePainter.STATE_DEFAULT, ThemePainter.STATE_ROLLOVER,
     *              ThemePainter.STATE_SELECTED and ThemePainter.STATE_PRESSED.
     * @param color the new background for the state.
     */
    public void setBackgroundOfState(int state, Color color) {
        switch (state) {
            case ThemePainter.STATE_DEFAULT:
                setBackground(color);
                break;
            case ThemePainter.STATE_ROLLOVER:
                setRolloverBackground(color);
                break;
            case ThemePainter.STATE_SELECTED:
                setSelectedBackground(color);
                break;
            case ThemePainter.STATE_PRESSED:
                setPressedBackground(color);
                break;
        }
    }

    /**
     * Gets the foreground for different states. The states are defined in ThemePainter as constants. Not all states are
     * supported by all components. If the state is not supported or foreground is never set, it will return null.
     * <p/>
     * Please note, each L&F will have its own way to paint the different foregrounds. This method allows you to
     * customize it for each component to use a different foreground. So if you want the foreground to be used, don't
     * use a ColorUIResource because UIResource is considered as a setting set by the L&F and any L&F can choose to
     * ignore it.
     *
     * @param state the button state. Valid values are ThemePainter.STATE_DEFAULT, ThemePainter.STATE_ROLLOVER,
     *              ThemePainter.STATE_SELECTED and ThemePainter.STATE_PRESSED.
     * @return the foreground for different states.
     */
    public Color getForegroundOfState(int state) {
        switch (state) {
            case ThemePainter.STATE_DEFAULT:
                return getDefaultForeground();
            case ThemePainter.STATE_ROLLOVER:
                return getRolloverForeground();
            case ThemePainter.STATE_SELECTED:
                return getSelectedForeground();
            case ThemePainter.STATE_PRESSED:
                return getPressedForeground();
        }
        return null;
    }


    /**
     * Sets the foreground for different states.  The states are defined in ThemePainter as constants. Not all states
     * are supported by all components. If the state is not supported or foreground is never set, it will return null.
     * <p/>
     * Please note, each L&F will have its own way to paint the different foregrounds. This method allows you to
     * customize it for each component to use a different foreground. So if you want the foreground to be used, don't
     * use a ColorUIResource because UIResource is considered as a setting set by the L&F and any L&F can choose to
     * ignore it.
     *
     * @param state the button state. Valid values are ThemePainter.STATE_DEFAULT, ThemePainter.STATE_ROLLOVER,
     *              ThemePainter.STATE_SELECTED and ThemePainter.STATE_PRESSED.
     * @param color the new foreground for the state.
     */
    public void setForegroundOfState(int state, Color color) {
        switch (state) {
            case ThemePainter.STATE_DEFAULT:
                setDefaultForeground(color);
                break;
            case ThemePainter.STATE_ROLLOVER:
                setRolloverForeground(color);
                break;
            case ThemePainter.STATE_SELECTED:
                setSelectedForeground(color);
                break;
            case ThemePainter.STATE_PRESSED:
                setPressedForeground(color);
                break;
        }
    }
}
