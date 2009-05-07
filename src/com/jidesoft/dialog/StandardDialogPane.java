/*
 * @(#)StandardDialogPane.java 3/18/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.dialog;

import com.jidesoft.swing.DelegateAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * StandardDialogPane is the content pane of StandardDialog. It can also be used when you want the template of
 * StandardDialog but you don't want to use JDialog. <UL> <LI> Introduce laziness. The content will not be filled until
 * pack() or show() be called. <LI> Default action and cancel action. User can set default action and cancel action of
 * this dialog. By default, ENTER key will trigger the default action and ESC key will trigger the cancel action and set
 * the dialog result to RESULT_CANCELLED. <LI> Divide the whole ContentPane of the dialog into three parts - content
 * panel, button panel and banner panel. By default, they are added to CENTER, SOUTH and NORTH of a BorderLayout
 * respectively. There isn't anything special about this. However if all your dialogs use this pattern, it will
 * automatically make the user interface more consistent. </UL>
 * <p/>
 * This class is abstract. Subclasses need to implement createBannerPanel(), createButtonPanel() and
 * createContentPanel()
 * <p/>
 * <code>StandardDialogPane</code> has lazy loading feature. So when you are done setup the page list, you need to call
 * {@link #initComponents()} to initialize everything. This method will be called automatically if the dialog pane is
 * added to StandardDialog.
 */
abstract public class StandardDialogPane extends JPanel implements ButtonNames {
    private boolean _lazyConstructorCalled = false;

    protected JComponent _bannerPanel;
    protected JComponent _contentPanel;
    protected ButtonPanel _buttonPanel;

    private Action _defaultCancelAction;

    private Action _defaultAction;

    private Component _initFocusedComponent;
    public static final String PROPERTY_CANCEL_ACTION = "defaultCancelAction";
    public static final String PROPERTY_DEFAULT_ACTION = "defaultAction";

    public StandardDialogPane() throws HeadlessException {
    }

    /**
     * Get default cancel action. Default cancel action will be triggered when ESC is pressed.
     *
     * @return the default cancel action
     */
    public Action getDefaultCancelAction() {
        return _defaultCancelAction;
    }

    /**
     * Set default cancel action. Default cancel action will be triggered when ESC is pressed.
     *
     * @param defaultCancelAction the default cancel action
     */
    public void setDefaultCancelAction(Action defaultCancelAction) {
        Action oldAction = _defaultCancelAction;
        _defaultCancelAction = defaultCancelAction;
        firePropertyChange(PROPERTY_CANCEL_ACTION, oldAction, _defaultCancelAction);
    }

    /**
     * Gets the default action. Default action will be trigger when ENTEY key is pressed.
     *
     * @return the default action.
     */
    public Action getDefaultAction() {
        return _defaultAction;
    }

    /**
     * Sets the default action. Default action will be trigger when ENTEY key is pressed.
     *
     * @param defaultAction the default action.
     */
    public void setDefaultAction(Action defaultAction) {
        Action oldAction = _defaultAction;
        _defaultAction = defaultAction;
        firePropertyChange(PROPERTY_DEFAULT_ACTION, oldAction, _defaultAction);
    }

    /**
     * Call three createXxxPanel methods and layout them using BorderLayout. By default, banner panel, content panel and
     * button panel are added to NORTH, CENTER and SOUTH of BorderLayout respectively.
     * <p/>
     * You can override this method if you want to layout them in another way.
     */
    public void initComponents() {
        _buttonPanel = createButtonPanel();
        _bannerPanel = createBannerPanel();
        _contentPanel = createContentPanel();
        layoutComponents(_bannerPanel, _contentPanel, _buttonPanel);
        if (getRootPane() != null) {
            if (getRootPane().getDefaultButton() != null) {
                getRootPane().getDefaultButton().requestFocus();
            }

            if (getDefaultCancelAction() != null) {
                getRootPane().registerKeyboardAction(new DelegateAction(getDefaultCancelAction()) {
                    @Override
                    public boolean delegateActionPerformed(ActionEvent e) {
                        MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
                        if (selectedPath != null && selectedPath.length > 0) {
                            MenuSelectionManager.defaultManager().clearSelectedPath();
                            return true;
                        }
                        return false;
                    }
                }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
            }
            if (getDefaultAction() != null) {
                getRootPane().registerKeyboardAction(getDefaultAction(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            }
        }
    }

    /**
     * Setups the layout for the three panels - banner panel, content panel and button panel. By default, we will use
     * BorderLayout, put the content panel in the middle, banner panel on the top and button panel either right or
     * bottom depending on its alignment.
     * <p/>
     * Subclass can override it to do your own layout. The three panels are the three parameters.
     *
     * @param bannerPanel
     * @param contentPanel
     * @param buttonPanel
     */
    protected void layoutComponents(Component bannerPanel, Component contentPanel, ButtonPanel buttonPanel) {
        setLayout(new BorderLayout());
        if (bannerPanel != null) {
            add(bannerPanel, BorderLayout.BEFORE_FIRST_LINE);
        }
        if (contentPanel != null) {
            add(contentPanel, BorderLayout.CENTER);
        }
        if (buttonPanel != null) {
            if (buttonPanel.getAlignment() == SwingConstants.TOP
                    || buttonPanel.getAlignment() == SwingConstants.BOTTOM) {
                add(buttonPanel, BorderLayout.AFTER_LINE_ENDS);
            }
            else {
                add(buttonPanel, BorderLayout.AFTER_LAST_LINE);
            }
        }
    }

    /**
     * Gets the initial focused component when dialog is shown.
     *
     * @return the initial focused component
     */
    public Component getInitFocusedComponent() {
        return _initFocusedComponent;
    }

    /**
     * Sets the initial focused component when dialog is shown.
     *
     * @param initFocusedComponent
     */
    public void setInitFocusedComponent(Component initFocusedComponent) {
        _initFocusedComponent = initFocusedComponent;
    }

    /**
     * Gets the banner panel created by createBannerPanel.
     *
     * @return the banner panel.
     */
    public JComponent getBannerPanel() {
        return _bannerPanel;
    }

    /**
     * Gets the banner panel created by createContentPanel.
     *
     * @return the content panel.
     */
    public JComponent getContentPanel() {
        return _contentPanel;
    }

    /**
     * Gets the banner panel created by createButtonPanel.
     *
     * @return the button panel.
     */
    public ButtonPanel getButtonPanel() {
        return _buttonPanel;
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
     *
     * @see ButtonPanel
     */
    abstract public ButtonPanel createButtonPanel();
}
