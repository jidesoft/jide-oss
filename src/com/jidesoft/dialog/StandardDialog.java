/*
* @(#)StandardDialog.java
*
* Copyright 2002 - 2003 JIDE Software. All rights reserved.
*/
package com.jidesoft.dialog;

import com.jidesoft.swing.DelegateAction;
import com.jidesoft.swing.JideSwingUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * StandardDialog is a dialog template. However several things are added to it to make it easier to use. <UL> <LI>
 * Laziness. The content will not be filled until pack() or show() are called. <LI> Default action and cancel action.
 * User can set the default action and cancel action of this dialog. By default, the ENTER key will trigger the default
 * action and the ESC key will trigger the cancel action and set the dialog result to RESULT_CANCELLED. <LI> Divide the
 * whole ContentPane of the dialog into three parts - content panel, button panel and banner panel. By default, they are
 * added to the CENTER, SOUTH and NORTH of a BorderLayout respectively. There isn't anything special about this. However
 * if all your dialogs use this pattern, it will automatically make the user interface more consistent. </UL>
 * <p/>
 * This class is abstract. Subclasses need to implement createBannerPanel(), createButtonPanel() and
 * createContentPanel()
 */
abstract public class StandardDialog extends JDialog implements ButtonNames {
    /**
     *
     */
    private static final long serialVersionUID = 9114102922553383723L;

    private boolean _lazyConstructorCalled = false;

    protected StandardDialogPane _standardDialogPane;

    /**
     * Dialog result.
     */
    public static final int RESULT_CANCELED = -1;
    /**
     * IT has a typo and was replaced with RESULT_CANCELED.
     */
    @Deprecated
    public static final int RESULT_CANCELLED = -1;

    /**
     * Dialog result.
     */
    public static final int RESULT_AFFIRMED = 0;


    // indicate user press OK or Cancel.
    private int _dialogResult = RESULT_CANCELED;
    public StandardDialogPropertyChangeListener _propertyChangeListener;

    public StandardDialog() {
        this((Frame) null);
    }

    public StandardDialog(Frame owner) {
        this(owner, true);
    }

    public StandardDialog(Frame owner, boolean modal) {
        this(owner, null, modal);
    }

    public StandardDialog(Frame owner, String title) {
        this(owner, title, true);
    }

    public StandardDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        initDialog();
    }

    public StandardDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initDialog();
    }

    public StandardDialog(Dialog owner) {
        this(owner, true);
    }

    public StandardDialog(Dialog owner, boolean modal) {
        this(owner, null, modal);
    }

    public StandardDialog(Dialog owner, String title) {
        this(owner, title, true);
    }

    public StandardDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        initDialog();
    }

    public StandardDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initDialog();
    }

    public StandardDialog(Window owner) {
        this(owner, (String) null);
    }

    public StandardDialog(Window owner, String title) {
        this(owner, title, ModalityType.APPLICATION_MODAL);
    }

    public StandardDialog(Window owner, ModalityType modalityType) {
        this(owner, "", modalityType);
    }

    public StandardDialog(Window owner, String title, ModalityType modalityType) {
        super(owner, title, modalityType);
        initDialog();
    }

    public StandardDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        initDialog();
    }

    private void initDialog() {
        _standardDialogPane = createStandardDialogPane();
        _propertyChangeListener = new StandardDialogPropertyChangeListener();
        _standardDialogPane.addPropertyChangeListener(_propertyChangeListener);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * Gets the result.
     *
     * @return the result.
     */
    public int getDialogResult() {
        return _dialogResult;
    }

    /**
     * Sets the dialog result.
     *
     * @param dialogResult the new dialog result.
     */
    public void setDialogResult(int dialogResult) {
        _dialogResult = dialogResult;
    }

    /**
     * Get default cancel action. Default cancel action will be triggered when ESC is pressed.
     *
     * @return the default cancel action
     */
    public Action getDefaultCancelAction() {
        return _standardDialogPane.getDefaultCancelAction();
    }

    /**
     * Set default cancel action. Default cancel action will be triggered when ESC is pressed.
     *
     * @param defaultCancelAction the default cancel action
     */
    public void setDefaultCancelAction(Action defaultCancelAction) {
        _standardDialogPane.setDefaultCancelAction(defaultCancelAction);
    }

    /**
     * Gets the default action. Default action will be trigger when ENTER key is pressed.
     *
     * @return the default action.
     */
    public Action getDefaultAction() {
        return _standardDialogPane.getDefaultAction();
    }

    /**
     * Sets the default action. Default action will be trigger when ENTER key is pressed.
     *
     * @param defaultAction the default action.
     */
    public void setDefaultAction(Action defaultAction) {
        _standardDialogPane.setDefaultAction(defaultAction);
    }

    @Override
    public void pack() {
        try {
            initialize();
        }
        catch (Exception e) {
            JideSwingUtilities.throwException(e);
        }
        super.pack();
    }

    /**
     * @deprecated As of JDK version 1.5, replaced by {@link Component#setVisible(boolean)
     * Component.setVisible(boolean)}.
     */
    @Override
    @Deprecated
    public void show() {
        try {
            initialize();
        }
        catch (Exception e) {
            JideSwingUtilities.throwException(e);
        }
        super.show();
    }

    /**
     * Force the initComponent() method implemented in the child class to be called. If this method is called more than
     * once on a given object, all calls but the first do nothing.
     */
    public synchronized final void initialize() {
        if ((!_lazyConstructorCalled) && (getParent() != null)) {
            initComponents();
            _lazyConstructorCalled = true;
            validate();
        }
    }

    /**
     * Call three createXxxPanel methods and layout them using BorderLayout. By default, banner panel, content panel and
     * button panel are added to NORTH, CENTER and SOUTH of BorderLayout respectively.
     * <p/>
     * You can override this method if you want to layout them in another way.
     */
    protected void initComponents() {
        getContentPane().setLayout(new BorderLayout());
        _standardDialogPane.initComponents();
        getContentPane().add(_standardDialogPane);

        if (getInitFocusedComponent() != null) {
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowActivated(WindowEvent e) {
                    getInitFocusedComponent().requestFocus();
                }
            });
        }
    }


    /**
     * Gets the initial focused component when dialog is shown.
     *
     * @return the initial focused component
     */
    public Component getInitFocusedComponent() {
        return _standardDialogPane.getInitFocusedComponent();
    }

    /**
     * Sets the initial focused component when dialog is shown.
     *
     * @param initFocusedComponent the initial focused component.
     */
    public void setInitFocusedComponent(Component initFocusedComponent) {
        _standardDialogPane.setInitFocusedComponent(initFocusedComponent);
    }

    /**
     * Gets the banner panel created by createBannerPanel.
     *
     * @return the banner panel.
     */
    public JComponent getBannerPanel() {
        return _standardDialogPane.getBannerPanel();
    }

    /**
     * Gets the banner panel created by createContentPanel.
     *
     * @return the content panel.
     */
    public JComponent getContentPanel() {
        return _standardDialogPane.getContentPanel();
    }

    /**
     * Gets the banner panel created by createButtonPanel.
     *
     * @return the button panel.
     */
    public ButtonPanel getButtonPanel() {
        return _standardDialogPane.getButtonPanel();
    }

    public StandardDialogPane getStandardDialogPane() {
        return _standardDialogPane;
    }

    /**
     * Subclasses should implement this method to create the banner panel. By default banner panel will appear on top of
     * the dialog unless you override initComponent() method. Banner panel is really used to balance the layout of
     * dialog to make the dialog looking good. However it can be used to show some help text. It is highly recommended
     * to use our {@link BannerPanel}
     * <p/>
     * If subclass doesn't want to have a banner panel, just return null.
     *
     * @return the banner panel.
     */
    abstract public JComponent createBannerPanel();

    /**
     * Subclasses should implement this method to create the content panel. This is the main panel of the dialog which
     * will be added to the center of the dialog. Subclass should never return null.
     *
     * @return the content panel.
     */
    abstract public JComponent createContentPanel();

    /**
     * Subclasses should implement this method to create the button panel. 90% of dialogs have buttons. It is highly
     * recommended to use our {@link ButtonPanel}.
     *
     * @return the button panel.
     * @see ButtonPanel
     */
    abstract public ButtonPanel createButtonPanel();

    /**
     * Creates a standard OK/Cancel button panel.
     *
     * @return a button panel that has OK/Cancel button.
     */
    public ButtonPanel createOKCancelButtonPanel() {
        ButtonPanel buttonPanel = new ButtonPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 6, 10));

        AbstractAction okAction = new AbstractAction(JideSwingUtilities.getOKString(getLocale())) {
            private static final long serialVersionUID = -326622280892936635L;

            public void actionPerformed(ActionEvent e) {
                setDialogResult(RESULT_AFFIRMED);
                setVisible(false);
            }
        };
        AbstractAction cancelAction = new AbstractAction(JideSwingUtilities.getCancelString(getLocale())) {
            private static final long serialVersionUID = 7131352846873132805L;

            public void actionPerformed(ActionEvent e) {
                setDialogResult(RESULT_CANCELED);
                setVisible(false);
            }
        };
        JButton okButton = new JButton(okAction);
        okButton.setName(OK);
        buttonPanel.addButton(okButton, ButtonPanel.AFFIRMATIVE_BUTTON);
        JButton cancelButton = new JButton(cancelAction);
        cancelButton.setName(CANCEL);
        buttonPanel.addButton(cancelButton, ButtonPanel.CANCEL_BUTTON);

        setDefaultCancelAction(cancelAction);
        setDefaultAction(okAction);
        getRootPane().setDefaultButton(okButton);

        return buttonPanel;
    }

    protected StandardDialogPane createStandardDialogPane() {
        return new DefaultStandardDialogPane();
    }

    class StandardDialogPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (StandardDialogPane.PROPERTY_CANCEL_ACTION.equals(evt.getPropertyName())) {
                DelegateAction delegateAction = new DelegateAction(getDefaultCancelAction()) {
                    private static final long serialVersionUID = -2136676357204671812L;

                    @Override
                    public boolean delegateActionPerformed(ActionEvent e) {
                        if (hasSelectionPath()) {
                            MenuSelectionManager.defaultManager().clearSelectedPath();
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean isDelegateEnabled() {
                        return hasSelectionPath();
                    }

                    private boolean hasSelectionPath() {
                        MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
                        return selectedPath != null && selectedPath.length > 0;
                    }
                };
                getRootPane().unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
                getRootPane().registerKeyboardAction(delegateAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
            }
            else if (StandardDialogPane.PROPERTY_DEFAULT_ACTION.equals(evt.getPropertyName())) {
                getRootPane().unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
                getRootPane().registerKeyboardAction(getDefaultAction(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

            }
        }
    }

    protected class DefaultStandardDialogPane extends StandardDialogPane {
        /**
         *
         */
        private static final long serialVersionUID = -6976658176495038104L;

        @Override
        public JComponent createBannerPanel() {
            return StandardDialog.this.createBannerPanel();
        }

        @Override
        public JComponent createContentPanel() {
            return StandardDialog.this.createContentPanel();
        }

        @Override
        public ButtonPanel createButtonPanel() {
            return StandardDialog.this.createButtonPanel();
        }
    }
}
