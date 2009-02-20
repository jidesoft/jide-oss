/*
 * @(#)JideOptionPane.java 3/27/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.dialog;

import com.jidesoft.plaf.basic.BasicJideOptionPaneUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * <code>JideOptionPane</code> is an enhanced version of JOptionPane.
 * <p/>
 * This component is still in beta, thus we didn't include the UIDefault needed by this component into
 * LookAndFeelFactory by default. If you want to use it, please refer to JideOptionPaneDemo's getDemoPanel method where
 * we add all necessary UIDefaults using UIDefaultCustomizer.
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

    /**
     * A new type for the option pane to have only the close button.
     */
    public static final int CLOSE_OPTION = 3;

    private static void installActionListener(Object[] options) {
        for (Object item : options) {
            if (!(item instanceof JButton)) {
                continue;
            }
            final JButton button = (JButton)item;
            button.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() instanceof JButton) {
                        Component parent = (Component)e.getSource();
                        while (parent != null && !(parent instanceof JOptionPane)) {
                            parent = parent.getParent();
                        }
                        if (parent != null) {
                            JOptionPane pane = (JOptionPane)parent;
                            pane.setValue(button);
                        }
                        while (parent != null && !(parent instanceof Frame || parent instanceof Dialog)) {
                            parent = parent.getParent();
                        }
                        if (parent instanceof Dialog) {
                            ((Dialog)parent).dispose();
                        }
                    }
                }
            });

        }
    }

    /**
     * Brings up an internal dialog panel with a specified icon, where
     * the initial choice is determined by the <code>initialValue</code>
     * parameter and the number of choices is determined by the
     * <code>optionType</code> parameter.
     * <p>
     * You could put JButton into the options array, so you can obtain both icon and text in the buttons. This is the enhancement comparing to {@link JOptionPane#showInternalOptionDialog(java.awt.Component, Object, String, int, int, javax.swing.Icon, Object[], Object)}.
     * <p>
     * If <code>optionType</code> is <code>YES_NO_OPTION</code>, or
     * <code>YES_NO_CANCEL_OPTION</code>
     * and the <code>options</code> parameter is <code>null</code>,
     * then the options are supplied by the Look and Feel.
     * <p>
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the look and feel.
     *
     * @param parentComponent determines the <code>Frame</code>
     *		in which the dialog is displayed; if <code>null</code>,
     *		or if the <code>parentComponent</code> has no
     *		<code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the object to display in the dialog; a
     *		<code>Component</code> object is rendered as a
     *		<code>Component</code>; a <code>String</code>
     *		object is rendered as a string. Other objects are
     *		converted to a <code>String</code> using the
     *		<code>toString</code> method
     * @param title     the title string for the dialog
     * @param optionType an integer designating the options available
     *		on the dialog: <code>YES_NO_OPTION</code>,
     *		or <code>YES_NO_CANCEL_OPTION</code>
     * @param messageType an integer designating the kind of message this is;
     *		primarily used to determine the icon from the
     *		pluggable Look and Feel: <code>ERROR_MESSAGE</code>,
     *		<code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>,
     *		or <code>PLAIN_MESSAGE</code>
     * @param icon      the icon to display in the dialog
     * @param options   an array of objects indicating the possible choices
     *          the user can make; if the objects are components, they
     *          are rendered properly; non-<code>String</code>
     *		objects are rendered using their <code>toString</code>
     *		methods; if this parameter is <code>null</code>,
     *		the options are determined by the Look and Feel
     * @param initialValue the object that represents the default selection
     *          for the dialog; only meaningful if <code>options</code>
     *		is used; can be <code>null</code>
     * @return an integer indicating the option chosen by the user,
     *          or <code>CLOSED_OPTION</code> if the user closed the Dialog
     */
    public static int showInternalOptionDialog(Component parentComponent,
                                       Object message,
                                       String title, int optionType,
                                       int messageType, Icon icon,
                                       Object[] options, Object initialValue) {
        installActionListener(options);
        return JOptionPane.showInternalOptionDialog(parentComponent, message, title, optionType, messageType, icon, options, initialValue);
    }


    /**
     * Brings up a dialog with a specified icon, where the initial
     * choice is determined by the <code>initialValue</code> parameter and
     * the number of choices is determined by the <code>optionType</code>
     * parameter.
     * <p>
     * You could put JButton into the options array, so you can obtain both icon and text in the buttons. This is the enhancement comparing to {@link JOptionPane#showInternalOptionDialog(java.awt.Component, Object, String, int, int, javax.swing.Icon, Object[], Object)}.
     * <p>
     * If <code>optionType</code> is <code>YES_NO_OPTION</code>,
     * or <code>YES_NO_CANCEL_OPTION</code>
     * and the <code>options</code> parameter is <code>null</code>,
     * then the options are
     * supplied by the look and feel.
     * <p>
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the look and feel.
     *
     * @param parentComponent determines the <code>Frame</code>
     *			in which the dialog is displayed;  if
     *                  <code>null</code>, or if the
     *			<code>parentComponent</code> has no
     *			<code>Frame</code>, a
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param optionType an integer designating the options available on the
     *                  dialog: <code>DEFAULT_OPTION</code>,
     *                  <code>YES_NO_OPTION</code>,
     *                  <code>YES_NO_CANCEL_OPTION</code>,
     *                  or <code>OK_CANCEL_OPTION</code>
     * @param messageType an integer designating the kind of message this is,
     *                  primarily used to determine the icon from the
     *			pluggable Look and Feel: <code>ERROR_MESSAGE</code>,
     *			<code>INFORMATION_MESSAGE</code>,
     *                  <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *			or <code>PLAIN_MESSAGE</code>
     * @param icon      the icon to display in the dialog
     * @param options   an array of objects indicating the possible choices
     *                  the user can make; if the objects are components, they
     *                  are rendered properly; non-<code>String</code>
     *			objects are
     *                  rendered using their <code>toString</code> methods;
     *                  if this parameter is <code>null</code>,
     *			the options are determined by the Look and Feel
     * @param initialValue the object that represents the default selection
     *                  for the dialog; only meaningful if <code>options</code>
     *			is used; can be <code>null</code>
     * @return an integer indicating the option chosen by the user,
     *         		or <code>CLOSED_OPTION</code> if the user closed
     *                  the dialog
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static int showOptionDialog(Component parentComponent,
        Object message, String title, int optionType, int messageType,
        Icon icon, Object[] options, Object initialValue)
        throws HeadlessException {
        installActionListener(options);
        return JOptionPane.showOptionDialog(parentComponent, message, title, optionType, messageType, icon, options, initialValue);
    }

    /**
     * Overrides the method in JOptionPane to allow a new option - CLOSE_OPTION.
     *
     * @param newType the type of the option pane.
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
     * Sets the details object. The object can be a string or a component. If it is a string, it will be put into a
     * JTextArea. If it is a component, it will be used directly. As long as the value is not null, a "Details" button
     * will be added to button panel allowing you to show or hide the details panel.
     *
     * @param details the details.
     */
    public void setDetails(Object details) {
        Object oldDetails = _details;
        _details = details;
        firePropertyChange(DETAILS_PROPERTY, oldDetails, _details);
    }

    /**
     * Gets the details object. The object can be a string or a component. If it is a string, it will be put into a
     * JTextArea. If it is a component, it will be used directly. As long as the value is not null, a "Details" button
     * will be added to button panel allowing you to show or hide the details panel.
     *
     * @return the details object.
     */
    public Object getDetails() {
        return _details;
    }

    /**
     * Gets the title of the option pane.
     *
     * @return the title of the option pane.
     */
    public Object getTitle() {
        return _title;
    }

    /**
     * Sets the title of the option pane.
     *
     * @param title the new title of the option pane.
     */
    public void setTitle(Object title) {
        Object old = _title;
        _title = title;
        firePropertyChange(TITLE_PROPERTY, old, _title);
    }

    /**
     * Sets the details component visible. Please note that you need to call this method before the option pane is
     * shown. The visible flag is actually stored on a static field so if you set one option pane visible, all option
     * panes' details component will be visible.
     *
     * @param visible true or false.
     */
    public void setDetailsVisible(boolean visible) {
        BasicJideOptionPaneUI.setDetailsVisible(visible);
        updateUI();
    }

    /**
     * Checks if the details component is visible.
     *
     * @return true if visible. Otherwise false.
     */
    public boolean isDetailsVisible() {
        return BasicJideOptionPaneUI.isDetailsVisible();
    }

//    public boolean isBannerVisible() {
//        return _bannerVisible;
//    }
//
//    public void setBannerVisible(boolean bannerVisible) {
//        _bannerVisible = bannerVisible;
//    }
}
