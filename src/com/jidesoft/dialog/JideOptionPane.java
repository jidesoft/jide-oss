/*
 * @(#)JideOptionPane.java 3/27/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.dialog;

import javax.swing.*;

/**
 * <code>JideOptionPane</code> is an enhanced version of JOptionPane.
 * <p/>
 * This component is still in beta, thus we didn't include the UIDefault needed by this component into LookAndFeelFactory by default.
 * If you want to use it, please refer to JideOptionPaneDemo's getDemoPanel method where we add all necessary UIDefaults
 * using UIDefaultCustomizer.
 */
public class JideOptionPane extends JOptionPane {
    private Object _title;
    private Object _details;
//    private boolean _bannerVisible = true;

    /**
     * Bound property name for <code>details</code>.
     */
    public static final String DETAILS_PROPERTY = "details";

    /**
     * Bound property name for <code>title</code>.
     */
    public static final String TITLE_PROPERTY = "title";

    public JideOptionPane() {
    }

    public JideOptionPane(Object message) {
        super(message);
    }

    public JideOptionPane(Object message, int messageType) {
        super(message, messageType);
    }

    public JideOptionPane(Object message, int messageType, int optionType) {
        super(message, messageType, optionType);
    }

    public JideOptionPane(Object message, int messageType, int optionType, Icon icon) {
        super(message, messageType, optionType, icon);
    }

    public JideOptionPane(Object message, int messageType, int optionType, Icon icon, Object[] options) {
        super(message, messageType, optionType, icon, options);
    }

    public JideOptionPane(Object message, int messageType, int optionType, Icon icon, Object[] options, Object initialValue) {
        super(message, messageType, optionType, icon, options, initialValue);
    }

    public static final int CLOSE_OPTION = 3;

    /**
     * Overrides the method in JOptionPane to allow a new option - CLOSE_OPTION.
     *
     * @param newType
     */
    @Override
    public void setOptionType(int newType) {
        if (newType != DEFAULT_OPTION && newType != YES_NO_OPTION &&
                newType != YES_NO_CANCEL_OPTION && newType != OK_CANCEL_OPTION
                && newType != CLOSE_OPTION)
            throw new RuntimeException("JOptionPane: option type must be one of JOptionPane.DEFAULT_OPTION, JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_CANCEL_OPTION or JOptionPane.OK_CANCEL_OPTION");

        int oldType = optionType;

        optionType = newType;
        firePropertyChange(OPTION_TYPE_PROPERTY, oldType, optionType);
    }

    /**
     * Sets the details object. The object can be a string or a component. If it is a string,
     * it will be put into a JTextArea. If it is a component, it will be used directly.
     * As long as the value is not null, a "Details" button will be added to button panel
     * allowing you to show or hide the details panel.
     *
     * @param details
     */
    public void setDetails(Object details) {
        Object oldDetails = _details;
        _details = details;
        firePropertyChange(DETAILS_PROPERTY, oldDetails, _details);
    }

    public Object getDetails() {
        return _details;
    }

    public Object getTitle() {
        return _title;
    }

    public void setTitle(Object title) {
        Object old = _title;
        _title = title;
        firePropertyChange(TITLE_PROPERTY, old, _title);
    }

//    public boolean isBannerVisible() {
//        return _bannerVisible;
//    }
//
//    public void setBannerVisible(boolean bannerVisible) {
//        _bannerVisible = bannerVisible;
//    }
}
