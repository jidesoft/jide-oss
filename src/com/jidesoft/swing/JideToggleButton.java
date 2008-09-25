/*
 * @(#)JideToggleButton.java 2/18/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.utils.SystemInfo;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


/**
 * An implementation of a two-state JideButton.
 */
public class JideToggleButton extends JideButton implements Accessible {
    private ItemListener _itemListener;

    /**
     * Creates an initially unselected toggle button without setting the text or image.
     */
    public JideToggleButton() {
        this(null, null, false);
    }

    /**
     * Creates an initially unselected toggle button with the specified image but no text.
     *
     * @param icon the image that the button should display
     */
    public JideToggleButton(Icon icon) {
        this(null, icon, false);
    }

    /**
     * Creates a toggle button with the specified image and selection state, but no text.
     *
     * @param icon     the image that the button should display
     * @param selected if true, the button is initially selected; otherwise, the button is initially unselected
     */
    public JideToggleButton(Icon icon, boolean selected) {
        this(null, icon, selected);
    }

    /**
     * Creates an unselected toggle button with the specified text.
     *
     * @param text the string displayed on the toggle button
     */
    public JideToggleButton(String text) {
        this(text, null, false);
    }

    /**
     * Creates a toggle button with the specified text and selection state.
     *
     * @param text     the string displayed on the toggle button
     * @param selected if true, the button is initially selected; otherwise, the button is initially unselected
     */
    public JideToggleButton(String text, boolean selected) {
        this(text, null, selected);
    }

    /**
     * Creates a toggle button where properties are taken from the Action supplied.
     *
     * @param a Action
     */
    public JideToggleButton(Action a) {
        this();
        setAction(a);
    }

    /**
     * Creates a toggle button that has the specified text and image, and that is initially unselected.
     *
     * @param text the string displayed on the button
     * @param icon the image that the button should display
     */
    public JideToggleButton(String text, Icon icon) {
        this(text, icon, false);
    }

    /**
     * Creates a toggle button with the specified text, image, and selection state.
     *
     * @param text     the text of the toggle button
     * @param icon     the image that the button should display
     * @param selected if true, the button is initially selected; otherwise, the button is initially unselected
     */
    public JideToggleButton(String text, Icon icon, boolean selected) {
        // Create the model
        setModel(new ToggleButtonModel());

        model.setSelected(selected);

        // initialize
        init(text, icon);
    }

    // *********************************************************************

    /**
     * The ToggleButton model
     * <p/>
     * <strong>Warning:</strong> Serialized objects of this class will not be compatible with future Swing releases. The
     * current serialization support is appropriate for short term storage or RMI between applications running the same
     * version of Swing.  As of 1.4, support for long term storage of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package. Please see {@link java.beans.XMLEncoder}.
     */
    public static class ToggleButtonModel extends DefaultButtonModel {

        /**
         * Creates a new ToggleButton Model
         */
        public ToggleButtonModel() {
        }

        /**
         * Checks if the button is selected.
         */
        @Override
        public boolean isSelected() {
//              if(getGroup() != null) {
//                  return getGroup().isSelected(this);
//              } else {
            return (stateMask & SELECTED) != 0;
//              }
        }


        /**
         * Sets the selected state of the button.
         *
         * @param b true selects the toggle button, false deselects the toggle button.
         */
        @Override
        public void setSelected(boolean b) {
            ButtonGroup group = getGroup();
            if (group != null) {
                // use the group model instead
                group.setSelected(this, b);
                b = group.isSelected(this);
            }

            if (isSelected() == b) {
                return;
            }

            if (b) {
                stateMask |= SELECTED;
            }
            else {
                stateMask &= ~SELECTED;
            }

            // Send ChangeEvent
            fireStateChanged();

            // Send ItemEvent
            fireItemStateChanged(new ItemEvent(this,
                    ItemEvent.ITEM_STATE_CHANGED,
                    this,
                    this.isSelected() ? ItemEvent.SELECTED : ItemEvent.DESELECTED));

        }

        /**
         * Sets the pressed state of the toggle button.
         */
        @Override
        public void setPressed(boolean b) {
            if ((isPressed() == b) || !isEnabled()) {
                return;
            }

            if (!b && isArmed()) {
                setSelected(!this.isSelected());
            }

            if (b) {
                stateMask |= PRESSED;
            }
            else {
                stateMask &= ~PRESSED;
            }

            fireStateChanged();

            if (!isPressed() && isArmed()) {
                int modifiers = 0;
                AWTEvent currentEvent = EventQueue.getCurrentEvent();
                if (currentEvent instanceof InputEvent) {
                    modifiers = ((InputEvent) currentEvent).getModifiers();
                }
                else if (currentEvent instanceof ActionEvent) {
                    modifiers = ((ActionEvent) currentEvent).getModifiers();
                }
                fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                        getActionCommand(),
                        EventQueue.getMostRecentEventTime(),
                        modifiers));
            }

        }
    }

    // to support SELECTED_KEY
    static boolean hasSelectedKey(Action a) {
        return SystemInfo.isJdk6Above() && (a != null && a.getValue(Action.SELECTED_KEY) != null);
    }

    static boolean isSelected(Action a) {
        return SystemInfo.isJdk6Above() && Boolean.TRUE.equals(a.getValue(Action.SELECTED_KEY));
    }

    /**
     * Sets the selected state of the button from the action.  This is defined here, but not wired up.  Subclasses like
     * JToggleButton and JCheckBoxMenuItem make use of it.
     *
     * @param a the Action
     */
    private void setSelectedFromAction(Action a) {
        boolean selected = false;
        if (a != null) {
            selected = isSelected(a);
        }
        if (selected != isSelected()) {
            // This won't notify ActionListeners, but that should be
            // ok as the change is coming from the Action.
            setSelected(selected);
            // Make sure the change actually took effect
            if (!selected && isSelected()) {
                if (getModel() instanceof DefaultButtonModel) {
                    ButtonGroup group = ((DefaultButtonModel) getModel()).getGroup();
                    if (group != null && SystemInfo.isJdk6Above()) {
                        group.clearSelection();
                    }
                }
            }
        }
    }

    @Override
    protected void actionPropertyChanged(Action action, String propertyName) {
        if (SystemInfo.isJdk6Above()) {
            super.actionPropertyChanged(action, propertyName);
            if (Action.SELECTED_KEY.equals(propertyName) && hasSelectedKey(action)) {
                setSelectedFromAction(action);
            }
        }
    }

    @Override
    protected void configurePropertiesFromAction(Action a) {
        super.configurePropertiesFromAction(a);
        if (hasSelectedKey(a)) {
            setSelectedFromAction(a);
        }
    }

    @Override
    protected ItemListener createItemListener() {
        if (_itemListener == null) {
            _itemListener = new ItemListener() {
                public void itemStateChanged(ItemEvent event) {
                    fireItemStateChanged(event);
                    Action action = getAction();
                    if (action != null && hasSelectedKey(action)) {
                        boolean selected = isSelected();
                        boolean isActionSelected = isSelected(action);
                        if (isActionSelected != selected) {
                            action.putValue(Action.SELECTED_KEY, selected);
                        }
                    }
                }
            };
        }
        return _itemListener;
    }

    // to support SELECTED_KEY - end

/////////////////
// Accessibility support
////////////////

    /**
     * Gets the AccessibleContext associated with this JToggleButton. For toggle buttons, the AccessibleContext takes
     * the form of an AccessibleJToggleButton. A new AccessibleJToggleButton instance is created if necessary.
     *
     * @return an AccessibleJToggleButton that serves as the AccessibleContext of this JToggleButton
     */
    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJToggleButton();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the <code>JToggleButton</code> class.  It provides an
     * implementation of the Java Accessibility API appropriate to toggle button user-interface elements.
     * <p/>
     * <strong>Warning:</strong> Serialized objects of this class will not be compatible with future Swing releases. The
     * current serialization support is appropriate for short term storage or RMI between applications running the same
     * version of Swing.  As of 1.4, support for long term storage of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package. Please see {@link java.beans.XMLEncoder}.
     */
    protected class AccessibleJToggleButton extends AccessibleAbstractButton
            implements ItemListener {

        public AccessibleJToggleButton() {
            super();
            JideToggleButton.this.addItemListener(this);
        }

        /**
         * Fire accessible property change events when the state of the toggle button changes.
         */
        public void itemStateChanged(ItemEvent e) {
            JideToggleButton button = (JideToggleButton) e.getSource();
            if (JideToggleButton.this.accessibleContext != null) {
                if (button.isSelected()) {
                    JideToggleButton.this.accessibleContext.firePropertyChange(AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                            null, AccessibleState.CHECKED);
                }
                else {
                    JideToggleButton.this.accessibleContext.firePropertyChange(AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                            AccessibleState.CHECKED, null);
                }
            }
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the object
         */
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TOGGLE_BUTTON;
        }
    } // inner class AccessibleJToggleButton
}
  
