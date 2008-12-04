/*
 * @(#)JideToggleSplitButton.java 2/18/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

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
 * <p/>
 * <strong>Warning:</strong> {@code SplitButtonGroup} has to be used in place of {@code ButtonGroup} for {@code
 * JideToggleSplitButton}s.
 */

public class JideToggleSplitButton extends JideSplitButton implements Accessible {
    /**
     * Creates an initially unselected toggle button without setting the text or image.
     */
    public JideToggleSplitButton() {
        this(null, null, false);
    }

    /**
     * Creates an initially unselected toggle button with the specified image but no text.
     *
     * @param icon the image that the button should display
     */
    public JideToggleSplitButton(Icon icon) {
        this(null, icon, false);
    }

    /**
     * Creates a toggle button with the specified image and selection state, but no text.
     *
     * @param icon     the image that the button should display
     * @param selected if true, the button is initially selected; otherwise, the button is initially unselected
     */
    public JideToggleSplitButton(Icon icon, boolean selected) {
        this(null, icon, selected);
    }

    /**
     * Creates an unselected toggle button with the specified text.
     *
     * @param text the string displayed on the toggle button
     */
    public JideToggleSplitButton(String text) {
        this(text, null, false);
    }

    /**
     * Creates a toggle button with the specified text and selection state.
     *
     * @param text     the string displayed on the toggle button
     * @param selected if true, the button is initially selected; otherwise, the button is initially unselected
     */
    public JideToggleSplitButton(String text, boolean selected) {
        this(text, null, selected);
    }

    /**
     * Creates a toggle button where properties are taken from the Action supplied.
     *
     * @since 1.3
     */
    public JideToggleSplitButton(Action a) {
        this();
        setAction(a);
    }

    /**
     * Creates a toggle button that has the specified text and image, and that is initially unselected.
     *
     * @param text the string displayed on the button
     * @param icon the image that the button should display
     */
    public JideToggleSplitButton(String text, Icon icon) {
        this(text, icon, false);
    }

    /**
     * Creates a toggle button with the specified text, image, and selection state.
     *
     * @param text     the text of the toggle button
     * @param icon     the image that the button should display
     * @param selected if true, the button is initially selected; otherwise, the button is initially unselected
     */
    public JideToggleSplitButton(String text, Icon icon, boolean selected) {
        // Create the model
        setModel(new ToggleSplitButtonModel());

        ((SplitButtonModel) model).setButtonSelected(selected);

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
    public static class ToggleSplitButtonModel extends DefaultSplitButtonModel {

        /**
         * Creates a new ToggleButton Model
         */
        public ToggleSplitButtonModel() {
        }

        /**
         * Checks if the button is selected.
         */
        @Override
        public boolean isButtonSelected() {
//              if(getGroup() != null) {
//                  return getGroup().isSelected(this);
//              } else {
            return (stateMask & BUTTON_SELECTED) != 0;
//              }
        }


        /**
         * Sets the selected state of the button.
         *
         * @param b true selects the toggle button, false deselects the toggle button.
         */
        @Override
        public void setButtonSelected(boolean b) {
            ButtonGroup group = getGroup();
            if (group != null) {
                // use the group model instead
                group.setSelected(this, b);
                b = group.isSelected(this);
            }

            if (isButtonSelected() == b) {
                return;
            }

            if (b) {
                stateMask |= BUTTON_SELECTED;
            }
            else {
                stateMask &= ~BUTTON_SELECTED;
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

            if (b == false && isArmed()) {
                setButtonSelected(!this.isButtonSelected());
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
            JideToggleSplitButton.this.addItemListener(this);
        }

        /**
         * Fire accessible property change events when the state of the toggle button changes.
         */
        public void itemStateChanged(ItemEvent e) {
            JideToggleSplitButton tb = (JideToggleSplitButton) e.getSource();
            if (JideToggleSplitButton.this.accessibleContext != null) {
                if (tb.isSelected()) {
                    JideToggleSplitButton.this.accessibleContext.firePropertyChange(AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                            null, AccessibleState.CHECKED);
                }
                else {
                    JideToggleSplitButton.this.accessibleContext.firePropertyChange(AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
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
  
