/*
 * @(#)JideOptionPane.java 3/27/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.dialog;

import com.jidesoft.plaf.basic.BasicJideOptionPaneUI;
import com.jidesoft.swing.JideSwingUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

/**
 * <code>JideOptionPane</code> is an enhanced version of JOptionPane.
 * <p/>
 * This component is still in beta, thus we didn't include the UIDefault needed by this component into
 * LookAndFeelFactory by default. If you want to use it, please refer to JideOptionPaneDemo's getDemoPanel method where
 * we add all necessary UIDefaults using UIDefaultCustomizer.
 */
public class JideOptionPane extends JOptionPane {
    private static final long serialVersionUID = 1916857052448620771L;
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
        initComponents();
    }

    public JideOptionPane(Object message) {
        super(message);
        initComponents();
    }

    public JideOptionPane(Object message, int messageType) {
        super(message, messageType);
        initComponents();
    }

    public JideOptionPane(Object message, int messageType, int optionType) {
        super(message, messageType, optionType);
        initComponents();
    }

    public JideOptionPane(Object message, int messageType, int optionType, Icon icon) {
        super(message, messageType, optionType, icon);
        initComponents();
    }

    public JideOptionPane(Object message, int messageType, int optionType, Icon icon, Object[] options) {
        super(message, messageType, optionType, icon, options);
        initComponents();
    }

    public JideOptionPane(Object message, int messageType, int optionType, Icon icon, Object[] options, Object initialValue) {
        super(message, messageType, optionType, icon, options, initialValue);
        initComponents();
    }

    protected void initComponents() {
    }

    /**
     * A new type for the option pane to have only the close button.
     */
    public static final int CLOSE_OPTION = 3;

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
        ((BasicJideOptionPaneUI) getUI()).setDetailsVisible(visible);
    }

    /**
     * Gets the localized string from resource bundle. Subclass can override it to provide its own string. Available
     * keys are defined in buttons.properties that begin with "Button.".
     *
     * @param key the resource string key
     *
     * @return the localized string.
     */
    public String getResourceString(String key) {
        return ButtonResources.getResourceBundle(getLocale()).getString(key);
    }

    /**
     * Checks if the details component is visible.
     *
     * @return true if visible. Otherwise false.
     */
    public boolean isDetailsVisible() {
        return ((BasicJideOptionPaneUI) getUI()).isDetailsVisible();
    }

    @Override
    public void setLocale(Locale l) {
        if (!JideSwingUtilities.equals(l, getLocale())) {
            super.setLocale(l);
            updateUI();
        }
    }

    //    public boolean isBannerVisible() {
//        return _bannerVisible;
//    }
//
//    public void setBannerVisible(boolean bannerVisible) {
//        _bannerVisible = bannerVisible;
//    }

    /**
     * Shows a question-message dialog requesting input from the user. The dialog uses the default frame, which usually
     * means it is centered on the screen.
     *
     * @param message the <code>Object</code> to display
     *
     * @throws java.awt.HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static String showInputDialog(Object message)
            throws HeadlessException {
        return showInputDialog(null, message);
    }

    /**
     * Shows a question-message dialog requesting input from the user, with the input value initialized to
     * <code>initialSelectionValue</code>. The dialog uses the default frame, which usually means it is centered on the
     * screen.
     *
     * @param message               the <code>Object</code> to display
     * @param initialSelectionValue the value used to initialize the input field
     *
     * @since 1.4
     */
    public static String showInputDialog(Object message, Object initialSelectionValue) {
        return showInputDialog(null, message, initialSelectionValue);
    }

    /**
     * Shows a question-message dialog requesting input from the user parented to <code>parentComponent</code>. The
     * dialog is displayed on top of the <code>Component</code>'s frame, and is usually positioned below the
     * <code>Component</code>.
     *
     * @param parentComponent the parent <code>Component</code> for the dialog
     * @param message         the <code>Object</code> to display
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static String showInputDialog(Component parentComponent,
                                         Object message) throws HeadlessException {
        return showInputDialog(parentComponent, message, UIManager.getString("OptionPane.inputDialogTitle"), QUESTION_MESSAGE);
    }

    /**
     * Shows a question-message dialog requesting input from the user and parented to <code>parentComponent</code>. The
     * input value will be initialized to <code>initialSelectionValue</code>. The dialog is displayed on top of the
     * <code>Component</code>'s frame, and is usually positioned below the <code>Component</code>.
     *
     * @param parentComponent       the parent <code>Component</code> for the dialog
     * @param message               the <code>Object</code> to display
     * @param initialSelectionValue the value used to initialize the input field
     *
     * @since 1.4
     */
    public static String showInputDialog(Component parentComponent, Object message,
                                         Object initialSelectionValue) {
        return (String) showInputDialog(parentComponent, message,
                UIManager.getString("OptionPane.inputDialogTitle"), QUESTION_MESSAGE, null, null,
                initialSelectionValue);
    }

    /**
     * Shows a dialog requesting input from the user parented to <code>parentComponent</code> with the dialog having the
     * title <code>title</code> and message type <code>messageType</code>.
     *
     * @param parentComponent the parent <code>Component</code> for the dialog
     * @param message         the <code>Object</code> to display
     * @param title           the <code>String</code> to display in the dialog title bar
     * @param messageType     the type of message that is to be displayed: <code>ERROR_MESSAGE</code>,
     *                        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
     *                        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static String showInputDialog(Component parentComponent,
                                         Object message, String title, int messageType)
            throws HeadlessException {
        return (String) showInputDialog(parentComponent, message, title,
                messageType, null, null, null);
    }

    /**
     * Prompts the user for input in a blocking dialog where the initial selection, possible selections, and all other
     * options can be specified. The user will able to choose from <code>selectionValues</code>, where <code>null</code>
     * implies the user can input whatever they wish, usually by means of a <code>JTextField</code>.
     * <code>initialSelectionValue</code> is the initial value to prompt the user with. It is up to the UI to decide how
     * best to represent the <code>selectionValues</code>, but usually a <code>JComboBox</code>, <code>JList</code>, or
     * <code>JTextField</code> will be used.
     *
     * @param parentComponent       the parent <code>Component</code> for the dialog
     * @param message               the <code>Object</code> to display
     * @param title                 the <code>String</code> to display in the dialog title bar
     * @param messageType           the type of message to be displayed: <code>ERROR_MESSAGE</code>,
     *                              <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
     *                              <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
     * @param icon                  the <code>Icon</code> image to display
     * @param selectionValues       an array of <code>Object</code>s that gives the possible selections
     * @param initialSelectionValue the value used to initialize the input field
     *
     * @return user's input, or <code>null</code> meaning the user canceled the input
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static Object showInputDialog(Component parentComponent,
                                         Object message, String title, int messageType, Icon icon,
                                         Object[] selectionValues, Object initialSelectionValue)
            throws HeadlessException {
        JideOptionPane pane = new JideOptionPane(message, messageType,
                OK_CANCEL_OPTION, icon,
                null, null);

        if (parentComponent != null) {
            pane.setLocale(parentComponent.getLocale());
        }
        pane.setWantsInput(true);
        pane.setSelectionValues(selectionValues);
        pane.setInitialSelectionValue(initialSelectionValue);
        pane.setComponentOrientation(((parentComponent == null) ?
                getRootFrame() : parentComponent).getComponentOrientation());

        int style = styleFromMessageType(messageType);
        JDialog dialog = pane.createDialog(parentComponent, title, style);

        pane.selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();

        Object value = pane.getInputValue();

        if (value == UNINITIALIZED_VALUE) {
            return null;
        }
        return value;
    }

    private static int styleFromMessageType(int messageType) {
        switch (messageType) {
            case ERROR_MESSAGE:
                return JRootPane.ERROR_DIALOG;
            case QUESTION_MESSAGE:
                return JRootPane.QUESTION_DIALOG;
            case WARNING_MESSAGE:
                return JRootPane.WARNING_DIALOG;
            case INFORMATION_MESSAGE:
                return JRootPane.INFORMATION_DIALOG;
            case PLAIN_MESSAGE:
            default:
                return JRootPane.PLAIN_DIALOG;
        }
    }

    /**
     * Brings up an information-message dialog titled "Message".
     *
     * @param parentComponent determines the <code>Frame</code> in which the dialog is displayed; if <code>null</code>,
     *                        or if the <code>parentComponent</code> has no <code>Frame</code>, a default
     *                        <code>Frame</code> is used
     * @param message         the <code>Object</code> to display
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static void showMessageDialog(Component parentComponent,
                                         Object message) throws HeadlessException {
        showMessageDialog(parentComponent, message, UIManager.getString("OptionPane.messageDialogTitle"),
                INFORMATION_MESSAGE);
    }

    /**
     * Brings up a dialog that displays a message using a default icon determined by the <code>messageType</code>
     * parameter.
     *
     * @param parentComponent determines the <code>Frame</code> in which the dialog is displayed; if <code>null</code>,
     *                        or if the <code>parentComponent</code> has no <code>Frame</code>, a default
     *                        <code>Frame</code> is used
     * @param message         the <code>Object</code> to display
     * @param title           the title string for the dialog
     * @param messageType     the type of message to be displayed: <code>ERROR_MESSAGE</code>,
     *                        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
     *                        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static void showMessageDialog(Component parentComponent,
                                         Object message, String title, int messageType)
            throws HeadlessException {
        showMessageDialog(parentComponent, message, title, messageType, null);
    }

    /**
     * Brings up a dialog displaying a message, specifying all parameters.
     *
     * @param parentComponent determines the <code>Frame</code> in which the dialog is displayed; if <code>null</code>,
     *                        or if the <code>parentComponent</code> has no <code>Frame</code>, a default
     *                        <code>Frame</code> is used
     * @param message         the <code>Object</code> to display
     * @param title           the title string for the dialog
     * @param messageType     the type of message to be displayed: <code>ERROR_MESSAGE</code>,
     *                        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
     *                        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
     * @param icon            an icon to display in the dialog that helps the user identify the kind of message that is
     *                        being displayed
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static void showMessageDialog(Component parentComponent,
                                         Object message, String title, int messageType, Icon icon)
            throws HeadlessException {
        showOptionDialog(parentComponent, message, title, DEFAULT_OPTION,
                messageType, icon, null, null);
    }

    /**
     * Brings up a dialog with the options <i>Yes</i>, <i>No</i> and <i>Cancel</i>; with the title, <b>Select an
     * Option</b>.
     *
     * @param parentComponent determines the <code>Frame</code> in which the dialog is displayed; if <code>null</code>,
     *                        or if the <code>parentComponent</code> has no <code>Frame</code>, a default
     *                        <code>Frame</code> is used
     * @param message         the <code>Object</code> to display
     *
     * @return an integer indicating the option selected by the user
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static int showConfirmDialog(Component parentComponent,
                                        Object message) throws HeadlessException {
        return showConfirmDialog(parentComponent, message,
                UIManager.getString("OptionPane.titleText"),
                YES_NO_CANCEL_OPTION);
    }

    /**
     * Brings up a dialog where the number of choices is determined by the <code>optionType</code> parameter.
     *
     * @param parentComponent determines the <code>Frame</code> in which the dialog is displayed; if <code>null</code>,
     *                        or if the <code>parentComponent</code> has no <code>Frame</code>, a default
     *                        <code>Frame</code> is used
     * @param message         the <code>Object</code> to display
     * @param title           the title string for the dialog
     * @param optionType      an int designating the options available on the dialog: <code>YES_NO_OPTION</code>,
     *                        <code>YES_NO_CANCEL_OPTION</code>, or <code>OK_CANCEL_OPTION</code>
     *
     * @return an int indicating the option selected by the user
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static int showConfirmDialog(Component parentComponent,
                                        Object message, String title, int optionType)
            throws HeadlessException {
        return showConfirmDialog(parentComponent, message, title, optionType,
                QUESTION_MESSAGE);
    }

    /**
     * Brings up a dialog where the number of choices is determined by the <code>optionType</code> parameter, where the
     * <code>messageType</code> parameter determines the icon to display. The <code>messageType</code> parameter is
     * primarily used to supply a default icon from the Look and Feel.
     *
     * @param parentComponent determines the <code>Frame</code> in which the dialog is displayed; if <code>null</code>,
     *                        or if the <code>parentComponent</code> has no <code>Frame</code>, a default
     *                        <code>Frame</code> is used.
     * @param message         the <code>Object</code> to display
     * @param title           the title string for the dialog
     * @param optionType      an integer designating the options available on the dialog: <code>YES_NO_OPTION</code>,
     *                        <code>YES_NO_CANCEL_OPTION</code>, or <code>OK_CANCEL_OPTION</code>
     * @param messageType     an integer designating the kind of message this is; primarily used to determine the icon
     *                        from the pluggable Look and Feel: <code>ERROR_MESSAGE</code>,
     *                        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
     *                        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
     *
     * @return an integer indicating the option selected by the user
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static int showConfirmDialog(Component parentComponent,
                                        Object message, String title, int optionType, int messageType)
            throws HeadlessException {
        return showConfirmDialog(parentComponent, message, title, optionType,
                messageType, null);
    }

    /**
     * Brings up a dialog with a specified icon, where the number of choices is determined by the
     * <code>optionType</code> parameter. The <code>messageType</code> parameter is primarily used to supply a default
     * icon from the look and feel.
     *
     * @param parentComponent determines the <code>Frame</code> in which the dialog is displayed; if <code>null</code>,
     *                        or if the <code>parentComponent</code> has no <code>Frame</code>, a default
     *                        <code>Frame</code> is used
     * @param message         the Object to display
     * @param title           the title string for the dialog
     * @param optionType      an int designating the options available on the dialog: <code>YES_NO_OPTION</code>,
     *                        <code>YES_NO_CANCEL_OPTION</code>, or <code>OK_CANCEL_OPTION</code>
     * @param messageType     an int designating the kind of message this is, primarily used to determine the icon from
     *                        the pluggable Look and Feel: <code>ERROR_MESSAGE</code>, <code>INFORMATION_MESSAGE</code>,
     *                        <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>, or
     *                        <code>PLAIN_MESSAGE</code>
     * @param icon            the icon to display in the dialog
     *
     * @return an int indicating the option selected by the user
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static int showConfirmDialog(Component parentComponent,
                                        Object message, String title, int optionType,
                                        int messageType, Icon icon) throws HeadlessException {
        return showOptionDialog(parentComponent, message, title, optionType,
                messageType, icon, null, null);
    }

    /**
     * Brings up a dialog with a specified icon, where the initial choice is determined by the <code>initialValue</code>
     * parameter and the number of choices is determined by the <code>optionType</code> parameter.
     * <p/>
     * If <code>optionType</code> is <code>YES_NO_OPTION</code>, or <code>YES_NO_CANCEL_OPTION</code> and the
     * <code>options</code> parameter is <code>null</code>, then the options are supplied by the look and feel.
     * <p/>
     * The <code>messageType</code> parameter is primarily used to supply a default icon from the look and feel.
     *
     * @param parentComponent determines the <code>Frame</code> in which the dialog is displayed;  if <code>null</code>,
     *                        or if the <code>parentComponent</code> has no <code>Frame</code>, a default
     *                        <code>Frame</code> is used
     * @param message         the <code>Object</code> to display
     * @param title           the title string for the dialog
     * @param optionType      an integer designating the options available on the dialog: <code>DEFAULT_OPTION</code>,
     *                        <code>YES_NO_OPTION</code>, <code>YES_NO_CANCEL_OPTION</code>, or
     *                        <code>OK_CANCEL_OPTION</code>
     * @param messageType     an integer designating the kind of message this is, primarily used to determine the icon
     *                        from the pluggable Look and Feel: <code>ERROR_MESSAGE</code>,
     *                        <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
     *                        <code>QUESTION_MESSAGE</code>, or <code>PLAIN_MESSAGE</code>
     * @param icon            the icon to display in the dialog
     * @param options         an array of objects indicating the possible choices the user can make; if the objects are
     *                        components, they are rendered properly; non-<code>String</code> objects are rendered using
     *                        their <code>toString</code> methods; if this parameter is <code>null</code>, the options
     *                        are determined by the Look and Feel
     * @param initialValue    the object that represents the default selection for the dialog; only meaningful if
     *                        <code>options</code> is used; can be <code>null</code>
     *
     * @return an integer indicating the option chosen by the user, or <code>CLOSED_OPTION</code> if the user closed the
     *         dialog
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static int showOptionDialog(Component parentComponent,
                                       Object message, String title, int optionType, int messageType,
                                       Icon icon, Object[] options, Object initialValue)
            throws HeadlessException {
        JideOptionPane pane = new JideOptionPane(message, messageType,
                optionType, icon,
                options, initialValue);

        if (parentComponent != null) {
            pane.setLocale(parentComponent.getLocale());
        }
        pane.setInitialValue(initialValue);
        pane.setComponentOrientation(((parentComponent == null) ?
                getRootFrame() : parentComponent).getComponentOrientation());

        int style = styleFromMessageType(messageType);
        JDialog dialog = pane.createDialog(parentComponent, title, style);

        pane.selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();

        Object selectedValue = pane.getValue();

        if (selectedValue == null)
            return CLOSED_OPTION;
        if (options == null) {
            if (selectedValue instanceof Integer)
                return ((Integer) selectedValue).intValue();
            return CLOSED_OPTION;
        }
        for (int counter = 0, maxCounter = options.length;
             counter < maxCounter; counter++) {
            if (options[counter].equals(selectedValue))
                return counter;
        }
        return CLOSED_OPTION;
    }

    /**
     * Creates and returns a new <code>JDialog</code> wrapping <code>this</code> centered on the
     * <code>parentComponent</code> in the <code>parentComponent</code>'s frame. <code>title</code> is the title of the
     * returned dialog. The returned <code>JDialog</code> will not be resizable by the user, however programs can invoke
     * <code>setResizable</code> on the <code>JDialog</code> instance to change this property. The returned
     * <code>JDialog</code> will be set up such that once it is closed, or the user clicks on one of the buttons, the
     * optionpane's value property will be set accordingly and the dialog will be closed.  Each time the dialog is made
     * visible, it will reset the option pane's value property to <code>JOptionPane.UNINITIALIZED_VALUE</code> to ensure
     * the user's subsequent action closes the dialog properly.
     *
     * @param parentComponent determines the frame in which the dialog is displayed; if the <code>parentComponent</code>
     *                        has no <code>Frame</code>, a default <code>Frame</code> is used
     * @param title           the title string for the dialog
     *
     * @return a new <code>JDialog</code> containing this instance
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public JDialog createDialog(Component parentComponent, String title)
            throws HeadlessException {
        int style = styleFromMessageType(getMessageType());
        return createDialog(parentComponent, title, style);
    }

    /**
     * Creates and returns a new parentless <code>JDialog</code> with the specified title. The returned
     * <code>JDialog</code> will not be resizable by the user, however programs can invoke <code>setResizable</code> on
     * the <code>JDialog</code> instance to change this property. The returned <code>JDialog</code> will be set up such
     * that once it is closed, or the user clicks on one of the buttons, the optionpane's value property will be set
     * accordingly and the dialog will be closed.  Each time the dialog is made visible, it will reset the option pane's
     * value property to <code>JOptionPane.UNINITIALIZED_VALUE</code> to ensure the user's subsequent action closes the
     * dialog properly.
     *
     * @param title the title string for the dialog
     *
     * @return a new <code>JDialog</code> containing this instance
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @since 1.6
     */
    public JDialog createDialog(String title) throws HeadlessException {
        int style = styleFromMessageType(getMessageType());
        JDialog dialog = new JDialog((Dialog) null, title, true);
        initDialog(dialog, style, null);
        return dialog;
    }

    private JDialog createDialog(Component parentComponent, String title,
                                 int style)
            throws HeadlessException {

        final JDialog dialog;

        Window window = JideOptionPane.getWindowForComponent(parentComponent);
        if (window instanceof Frame) {
            dialog = new JDialog((Frame) window, title, true);
        }
        else {
            dialog = new JDialog((Dialog) window, title, true);
        }
//        if (window instanceof SwingUtilities.SharedOwnerFrame) {
//            WindowListener ownerShutdownListener =
//                    (WindowListener) SwingUtilities.getSharedOwnerFrameShutdownListener();
//            dialog.addWindowListener(ownerShutdownListener);
//        }
        initDialog(dialog, style, parentComponent);
        return dialog;
    }

    private void initDialog(final JDialog dialog, int style, Component parentComponent) {
        dialog.setComponentOrientation(this.getComponentOrientation());
        Container contentPane = dialog.getContentPane();

        contentPane.setLayout(new BorderLayout());
        contentPane.add(this, BorderLayout.CENTER);
        dialog.setResizable(false);
        if (JDialog.isDefaultLookAndFeelDecorated()) {
            boolean supportsWindowDecorations =
                    UIManager.getLookAndFeel().getSupportsWindowDecorations();
            if (supportsWindowDecorations) {
                dialog.setUndecorated(true);
                getRootPane().setWindowDecorationStyle(style);
            }
        }
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        WindowAdapter adapter = new WindowAdapter() {
            private boolean gotFocus = false;

            public void windowClosing(WindowEvent we) {
                setValue(null);
            }

            public void windowGainedFocus(WindowEvent we) {
                // Once window gets focus, set initial focus
                if (!gotFocus) {
                    selectInitialValue();
                    gotFocus = true;
                }
            }
        };
        dialog.addWindowListener(adapter);
        dialog.addWindowFocusListener(adapter);
        dialog.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                // reset value to ensure closing works properly
                setValue(JOptionPane.UNINITIALIZED_VALUE);
            }
        });
        addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                // Let the defaultCloseOperation handle the closing
                // if the user closed the window without selecting a button
                // (newValue = null in that case).  Otherwise, close the dialog.
                if (dialog.isVisible() && event.getSource() == JideOptionPane.this &&
                        (event.getPropertyName().equals(VALUE_PROPERTY)) &&
                        event.getNewValue() != null &&
                        event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
                    dialog.setVisible(false);
                }
            }
        });
    }

    /**
     * Returns the specified component's <code>Frame</code>.
     *
     * @param parentComponent the <code>Component</code> to check for a <code>Frame</code>
     *
     * @return the <code>Frame</code> that contains the component, or <code>getRootFrame</code> if the component is
     *         <code>null</code>, or does not have a valid <code>Frame</code> parent
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see #getRootFrame
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static Frame getFrameForComponent(Component parentComponent)
            throws HeadlessException {
        if (parentComponent == null)
            return getRootFrame();
        if (parentComponent instanceof Frame)
            return (Frame) parentComponent;
        return JOptionPane.getFrameForComponent(parentComponent.getParent());
    }

    /**
     * Returns the specified component's toplevel <code>Frame</code> or <code>Dialog</code>.
     *
     * @param parentComponent the <code>Component</code> to check for a <code>Frame</code> or <code>Dialog</code>
     *
     * @return the <code>Frame</code> or <code>Dialog</code> that contains the component, or the default frame if the
     *         component is <code>null</code>, or does not have a valid <code>Frame</code> or <code>Dialog</code>
     *         parent
     *
     * @throws HeadlessException if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    static Window getWindowForComponent(Component parentComponent)
            throws HeadlessException {
        if (parentComponent == null)
            return getRootFrame();
        if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
            return (Window) parentComponent;
        return JideOptionPane.getWindowForComponent(parentComponent.getParent());
    }
}
