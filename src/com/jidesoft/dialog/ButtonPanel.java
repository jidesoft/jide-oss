/*
 * @(#)ButtonPanel.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.dialog;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.ArrowKeyNavigationSupport;

import javax.swing.*;
import java.awt.*;

/**
 * <code>ButtonPanel</code> can help to layout buttons easily in any dialogs.
 * <p/>
 * For more detail, please refer to JIDE Dialogs Developer Guide.
 */
public class ButtonPanel extends JPanel implements ButtonListener, ButtonNames {

    /**
     * This option will make all buttons have the same size. If all buttons have the same size, the GUI will certainly
     * look better.
     */
    public static final int SAME_SIZE = 0;

    /**
     * This option will make all buttons no less than a certain size. The size is different on different platforms. We
     * need this option because sometimes one button has a very long text. If all buttons have the same size, it will
     * make the button panel extremely long. Even though they have the same size but will look out of balance. This
     * option is not available if the buttons are arranged vertically.
     */
    public static final int NO_LESS_THAN = 1;

    /**
     * Client property key. If this client property is set to Boolean.TRUE, the button panel will always use the
     * component's preferred width instead of using minButtonWidth.
     */
    public static final String KEEP_PREFERRED_WIDTH = "keepPreferredWidth";

    /**
     * The button will produce an affirmative action. Typical affirmative buttons are OK, Save, Print, Replace etc. It
     * doesn't have to be positive either. For example, both Yes and No are affirmative action. This constant is used as
     * constraint parameter in {@link #addButton(javax.swing.AbstractButton,Object)} method.
     */
    public static final String AFFIRMATIVE_BUTTON = "AFFIRMATIVE";

    /**
     * The button will produce a cancel action. Typical cancel button is Cancel. This constant is used as constraint
     * parameter in {@link #addButton(javax.swing.AbstractButton,Object)} method.
     */
    public static final String CANCEL_BUTTON = "CANCEL";

    /**
     * The button will open some help windows. This constant is used as constraint parameter in {@link
     * #addButton(javax.swing.AbstractButton,Object)} method.
     */
    public static final String HELP_BUTTON = "HELP";

    /**
     * The button will produce an alternative action different neither an affirmative or cancel action. Typical
     * alternative button is Don't Save comparing with Save as affirmative action and Cancel as cancel action. This
     * constant is used as constraint parameter in {@link #addButton(javax.swing.AbstractButton,Object)} method.
     */
    public static final String OTHER_BUTTON = "ALTERNATIVE";

    private String _defaultOrder = UIDefaultsLookup.getString("ButtonPanel.order");

    private String _defaultOppositeOrder = UIDefaultsLookup.getString("ButtonPanel.oppositeOrder");

    private int _defaultButtonGap = UIDefaultsLookup.getInt("ButtonPanel.buttonGap");

    private int _defaultGroupGap = UIDefaultsLookup.getInt("ButtonPanel.groupGap");

    private int _defaultButtonWidth = UIDefaultsLookup.getInt("ButtonPanel.minButtonWidth");

    private int _alignment;

    private ButtonPanelLayout _layout;

    /**
     * Constructs a new <code>ButtonPanel</code> with right alignment.
     */
    public ButtonPanel() {
        this(SwingConstants.TRAILING);
    }

    /**
     * Constructs a new <code>ButtonPanel</code> with the specified alignment.
     *
     * @param alignment the alignment. The supported alignment are {@link SwingConstants#RIGHT}, {@link
     *                  SwingConstants#LEFT}, {@link SwingConstants#CENTER}, {@link SwingConstants#TOP} or {@link
     *                  SwingConstants#BOTTOM}.
     */
    public ButtonPanel(int alignment) {
        this(alignment, SAME_SIZE);
    }

    /**
     * Constructs a new <code>ButtonPanel</code> with default horizontal spacing and the given alignment.
     *
     * @param alignment     the alignment of the buttons. It can be one of <code>SwingConstants.LEFT</code> or
     *                      <code>SwingConstants.RIGHT</code> or <code>SwingConstants.TOP</code> or
     *                      <code>SwingConstants.BOTTOM</code> or <code>SwingConstants.CENTER</code>.
     * @param sizeContraint size constraint of the button. It can be either <code>SAME_SIZE</code> or
     *                      <code>NO_LESS_THAN</code>
     */

    public ButtonPanel(int alignment, int sizeContraint) {
        _alignment = alignment;

        if (alignment != SwingConstants.LEFT && alignment != SwingConstants.RIGHT && alignment != SwingConstants.LEADING && alignment != SwingConstants.TRAILING
                && alignment != SwingConstants.TOP && alignment != SwingConstants.BOTTOM && alignment != SwingConstants.CENTER) {
            throw new IllegalArgumentException("Invalid alignment");
        }

        int axis = (_alignment == SwingConstants.CENTER || _alignment == SwingConstants.LEFT || _alignment == SwingConstants.RIGHT || _alignment == SwingConstants.TRAILING || _alignment == SwingConstants.LEADING)
                ? ButtonPanelLayout.X_AXIS : ButtonPanelLayout.Y_AXIS;

        _layout = new ButtonPanelLayout(this, axis, _alignment, sizeContraint, _defaultOrder, _defaultOppositeOrder, _defaultButtonGap, _defaultGroupGap);
        setLayout(_layout);

        new ArrowKeyNavigationSupport(new Class[]{AbstractButton.class}).install(this);
    }

    @Override
    public void updateUI() {
        if (UIDefaultsLookup.get("ButtonPanel.buttonGap") == null
                && UIDefaultsLookup.get("ButtonPanel.order") == null
                && UIDefaultsLookup.get("ButtonPanel.groupGap") == null) {
            LookAndFeelFactory.installJideExtension();
        }
        super.updateUI();
        reinstallDefaults();
    }

    protected void reinstallDefaults() {
        if (_layout != null) {
            if (_defaultButtonGap == _layout.getButtonGap()) {
                _defaultButtonGap = UIDefaultsLookup.getInt("ButtonPanel.buttonGap");
                _layout.setButtonGap(_defaultButtonGap);
            }
            if (_defaultGroupGap == _layout.getGroupGap()) {
                _defaultGroupGap = UIDefaultsLookup.getInt("ButtonPanel.groupGap");
                _layout.setGroupGap(_defaultGroupGap);
            }
            if (_defaultOrder.equals(_layout.getButtonOrder())) {
                _defaultOrder = UIDefaultsLookup.getString("ButtonPanel.order");
                _layout.setButtonOrder(_defaultOrder);
            }
            if (_defaultOppositeOrder.equals(_layout.getOppositeButtonOrder())) {
                _defaultOppositeOrder = UIDefaultsLookup.getString("ButtonPanel.oppositeOrder");
                _layout.setOppositeButtonOrder(_defaultOppositeOrder);
            }
            if (_defaultButtonWidth == _layout.getMinButtonWidth()) {
                _defaultButtonWidth = UIDefaultsLookup.getInt("ButtonPanel.minButtonWidth");
                _layout.setMinButtonWidth(_defaultButtonWidth);
            }
        }
    }

    /**
     * Sets the alignment. If the alignment is one of SwingConstants.CENTER, SwingConstants.LEFT, SwingConstants.RIGHT,
     * SwingConstants.LEADING or SwingConstants.TRAILING, the buttons will be laid out horizontally. If the alignment is
     * SwingConstants.TOP or SwingConstants.BOTTOM, the buttons will be laid out vertically.
     *
     * @param alignment the alignment. The supported alignment are {@link SwingConstants#RIGHT}, {@link
     *                  SwingConstants#LEFT}, {@link SwingConstants#CENTER}, {@link SwingConstants#TOP} or {@link
     *                  SwingConstants#BOTTOM}.
     */
    public void setAlignment(int alignment) {
        _alignment = alignment;
        int axis = (_alignment == SwingConstants.CENTER || _alignment == SwingConstants.LEFT || _alignment == SwingConstants.RIGHT || _alignment == SwingConstants.LEADING || _alignment == SwingConstants.TRAILING)
                ? ButtonPanelLayout.X_AXIS : ButtonPanelLayout.Y_AXIS;
        _layout.setAlignment(_alignment);
        _layout.setAxis(axis);
        _layout.layoutContainer(this);
    }

    /**
     * Gets the alignment of the ButtonPanel.
     *
     * @return the alignment of the ButtonPanel.
     */
    public int getAlignment() {
        return _alignment;
    }

    /**
     * Adds button to ButonPanel as AFFIRMATIVE_BUTTON.
     *
     * @param button a button
     */
    public void addButton(AbstractButton button) {
        addButton(button, AFFIRMATIVE_BUTTON);
    }

    /**
     * Adds button to ButonPanel with specified type.
     *
     * @param button a button.
     * @param index  the position in the button panel's list at which to insert the component; -1 means insert at the
     *               end component
     */
    public void addButton(AbstractButton button, int index) {
        addButton(button, AFFIRMATIVE_BUTTON, index);
    }

    /**
     * Adds button to ButonPanel with specified constraint. The valid constraints are {@link #AFFIRMATIVE_BUTTON},
     * {@link #CANCEL_BUTTON},{@link #OTHER_BUTTON} and {@link #HELP_BUTTON}. The main purpose of the constraints is to
     * determine how the buttons are laid out on different platforms according to the OS convention. For example, on
     * Windows, AFFIRMATIVE_BUTTON appears on the right hand side of CANCEL_BUTTON. On Mac OS X, AFFIRMATIVE_BUTTON will
     * appear on the left hand side of CANCEL_BUTTON.
     *
     * @param button     a button.
     * @param constraint one of constraints.
     */
    public void addButton(AbstractButton button, Object constraint) {
        addButton(button, constraint, -1);
    }

    /**
     * Adds button to ButonPanel with specified type.
     *
     * @param button     a button.
     * @param constraint String of one of types.
     * @param index      the position in the button panel's list at which to insert the component; -1 means insert at
     *                   the end component
     */
    public void addButton(AbstractButton button, Object constraint, int index) {
        add(button, constraint, index);
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        // TODO: if index is not 0, it could be a problem
        if (constraints == null) {
            constraints = AFFIRMATIVE_BUTTON;
        }
        super.addImpl(comp, constraints, index);
    }

    /**
     * Removes the button. It's the same as {@link #remove(java.awt.Component)}.
     *
     * @param button a button
     */
    public void removeButton(AbstractButton button) {
        remove(button);
    }

    /**
     * Gets the button order.
     *
     * @return the button order.
     */
    public String getButtonOrder() {
        return _layout.getButtonOrder();
    }

    /**
     * Sets the button order.
     *
     * @param buttonOrder the new button order.
     */
    public void setButtonOrder(String buttonOrder) {
        _layout.setButtonOrder(buttonOrder);
    }

    /**
     * Gets the opposite button order.
     *
     * @return the opposite button order.
     */
    public String getOppositeButtonOrder() {
        return _layout.getOppositeButtonOrder();
    }

    /**
     * Sets the opposite button order.
     *
     * @param oppositeButtonOrder the new opposite button order.
     */
    public void setOppositeButtonOrder(String oppositeButtonOrder) {
        _layout.setOppositeButtonOrder(oppositeButtonOrder);
    }

    /**
     * Gets the size constraint.
     *
     * @return the size constraint.
     */
    public int getSizeConstraint() {
        return _layout.getSizeConstraint();
    }

    /**
     * Sets the size constraint. Valid values are {@link #NO_LESS_THAN} and {@link #SAME_SIZE}. The size constraint will
     * apply to all components except if the component client property {@link ButtonPanel#KEEP_PREFERRED_WIDTH} is set
     * to Boolean.TRUE.
     *
     * @param sizeContraint the size constraint.
     */
    public void setSizeConstraint(int sizeContraint) {
        _layout.setSizeConstraint(sizeContraint);
    }

    /**
     * Gets the gap between two button groups.
     *
     * @return the gap between two button groups.
     */
    public int getGroupGap() {
        return _layout.getGroupGap();
    }

    /**
     * Sets the gap between two button groups.
     *
     * @param groupGap the gap between button groups.
     */
    public void setGroupGap(int groupGap) {
        _layout.setGroupGap(groupGap);
    }

    /**
     * Gets the gap between two buttons in the same group.
     *
     * @return the gap between two buttons in the same group.
     */
    public int getButtonGap() {
        return _layout.getButtonGap();
    }

    /**
     * Sets the gap between two buttons in the same group.
     *
     * @param buttonGap the gap between buttons.
     */
    public void setButtonGap(int buttonGap) {
        _layout.setButtonGap(buttonGap);
    }

    /**
     * Gets the minimum button width.
     *
     * @return the minimum button width.
     */
    public int getMinButtonWidth() {
        return _layout.getMinButtonWidth();
    }

    /**
     * Sets the minimum button width.
     *
     * @param minButtonWidth the minimum button width.
     */
    public void setMinButtonWidth(int minButtonWidth) {
        _layout.setMinButtonWidth(minButtonWidth);
    }

    public void buttonEventFired(ButtonEvent e) {
        if (e.getID() == ButtonEvent.CLEAR_DEFAULT_BUTTON) {
            JRootPane rootPane = getRootPane();
            if (rootPane != null && rootPane.getDefaultButton() != null) {
                rootPane.setDefaultButton(null);
            }
        }
        else {
            for (int i = 0; i < getComponentCount(); i++) {
                final Component component = getComponent(i);
                if (e.getButtonName().equals(component.getName())) {
                    switch (e.getID()) {
                        case ButtonEvent.ENABLE_BUTTON:
                            component.setVisible(true);
                            if (component instanceof JButton && ((JButton) component).getAction() != null) {
                                ((JButton) component).getAction().setEnabled(true);
                            }
                            component.setEnabled(true);
                            break;
                        case ButtonEvent.DISABLE_BUTTON:
                            component.setEnabled(false);
                            if (component instanceof JButton && ((JButton) component).getAction() != null) {
                                ((JButton) component).getAction().setEnabled(false);
                            }
                            component.setVisible(true);
                            break;
                        case ButtonEvent.SHOW_BUTTON:
                            component.setVisible(true);
                            break;
                        case ButtonEvent.HIDE_BUTTON:
                            component.setVisible(false);
                            JRootPane rootPane = getRootPane();
                            if (rootPane != null && rootPane.getDefaultButton() == component) {
                                rootPane.setDefaultButton(null);
                            }
                            // to clear the initialDefaultButton in root pane 
                            if (rootPane != null && rootPane.getClientProperty("initialDefaultButton") == component) {
                                rootPane.putClientProperty("initialDefaultButton", null);
                            }
                            break;
                        case ButtonEvent.CHANGE_BUTTON_TEXT:
                            if (component instanceof AbstractButton) {
                                ((AbstractButton) component).setText(e.getUserObject());
                            }
                            break;
                        case ButtonEvent.CHANGE_BUTTON_MNEMONIC:
                            if (component instanceof AbstractButton) {
                                ((AbstractButton) component).setMnemonic(e.getUserObject().charAt(0));
                            }
                            break;
                        case ButtonEvent.CHANGE_BUTTON_TOOLTIP:
                            if (component instanceof AbstractButton) {
                                ((AbstractButton) component).setToolTipText(e.getUserObject());
                            }
                            break;
                        case ButtonEvent.CHANGE_BUTTON_FOCUS:
                            Runnable runnable = new Runnable() {
                                public void run() {
                                    component.requestFocus();
                                }
                            };
                            SwingUtilities.invokeLater(runnable);
                            break;
                        case ButtonEvent.SET_DEFAULT_BUTTON:
                            if (component instanceof JButton) {
                                if (getRootPane() != null) {
                                    getRootPane().setDefaultButton(((JButton) component));
                                }
                                else {
                                    _defaultButton = (JButton) component;
                                    _addNotify = true;
                                }
                            }
                            break;
                    }
                    break;
                }
            }
        }
    }

    private boolean _addNotify = false;
    private JButton _defaultButton;

    @Override
    public void addNotify() {
        super.addNotify();
        if (_addNotify) {
            JRootPane pane = getRootPane();
            if (_defaultButton != null && pane != null) {
                pane.setDefaultButton(_defaultButton);
                _addNotify = false;
                _defaultButton = null;
            }
        }
    }

    /**
     * Gets the button with the name. In order to use this method, you have to set a name to the button using {@link
     * AbstractButton#setName(String)} method. Please note, the name is not the same as the constraint in the second
     * parameter of {@link #add(java.awt.Component,Object)}.
     *
     * @param name the button name.
     * @return the button which has the name. null if there is no button with that name.
     *
     * @throws IllegalArgumentException if the name is null or empty.
     */
    public Component getButtonByName(String name) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        for (int i = 0; i < getComponentCount(); i++) {
            Component component = getComponent(i);
            if (name.equals(component.getName())) {
                return component;
            }
        }
        return null;
    }
}
