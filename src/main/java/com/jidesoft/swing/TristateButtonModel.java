/*
 * @(#)TristateButtonModel.java 5/20/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;


/**
 * Model for TristateCheckBox. It introduces a mixed state to represent check box in the mixed selected state.
 * ActionEvent will be fired when the state is changed.
 */
public class TristateButtonModel extends JToggleButton.ToggleButtonModel {
    private static final long serialVersionUID = 9179129427948325126L;

    /**
     * Identifies the "mixed" bit in the bitmask, which indicates that the button is partial selected.
     */
    public final static int MIXED = 1 << 7;

    public TristateButtonModel() {
    }

    public void setState(int state) {
        switch (state) {
            case TristateCheckBox.STATE_UNSELECTED:
                setSelected(false);
                break;
            case TristateCheckBox.STATE_SELECTED:
                setSelected(true);
                break;
            case TristateCheckBox.STATE_MIXED:
                setMixed(true);
                break;
        }
    }

    public int getState() {
        if (isMixed()) return TristateCheckBox.STATE_MIXED;
        else if (isSelected()) return TristateCheckBox.STATE_SELECTED;
        else return TristateCheckBox.STATE_UNSELECTED;
    }

    /**
     * We rotate between STATE_UNSELECTED, STATE_SELECTED and STATE_MIXED. Subclass can override this method to tell the
     * check box what next state is. Here is the default implementation.
     * <code><pre>
     * if (current == TristateCheckBox.STATE_UNSELECTED) {
     *     return TristateCheckBox.STATE_SELECTED;
     * }
     * else if (current == TristateCheckBox.STATE_SELECTED) {
     *     return TristateCheckBox.STATE_MIXED;
     * }
     * else if (current == TristateCheckBox.STATE_MIXED) {
     *     return TristateCheckBox.STATE_UNSELECTED;
     * }
     * </code></pre>
     *
     * @param current the current state
     * @return the next state of the current state.
     */
    protected int getNextState(int current) {
        if (current == TristateCheckBox.STATE_UNSELECTED) {
            return TristateCheckBox.STATE_SELECTED;
        }
        else if (current == TristateCheckBox.STATE_SELECTED) {
            return TristateCheckBox.STATE_MIXED;
        }
        else /*if (current == STATE_MIXED)*/ {
            return TristateCheckBox.STATE_UNSELECTED;
        }
    }

    @Override
    public void setPressed(boolean b) {
        if ((isPressed() == b) || !isEnabled()) {
            return;
        }

        if (!b && isArmed()) {
            updateState();
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
            fireActionPerformed(
                    new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                            getActionCommand(),
                            EventQueue.getMostRecentEventTime(),
                            modifiers));
        }
    }

    /**
     * Updates the state when the mouse is clicked. The default implementation is
     * <pre><code>
     * setState(getNextState(getState()));
     * </code></pre>
     */
    protected void updateState() {
        setState(getNextState(getState()));
    }

    @Override
    public void setSelected(boolean b) {
        boolean mixed = isMixed();
        if (mixed) {
            stateMask &= ~MIXED;
            internalSetSelected(!isSelected());
        }
        super.setSelected(b);
    }

    void internalSetSelected(boolean b) {
        if (b) {
            stateMask |= SELECTED;
        }
        else {
            stateMask &= ~SELECTED;
        }
    }

    public boolean isMixed() {
        return (stateMask & MIXED) != 0;
    }

    public void setMixed(boolean b) {
        if ((isMixed() == b)) {
            return;
        }

        if (b) {
            stateMask |= MIXED;
            stateMask |= SELECTED;  // make it selected
        }
        else {
            stateMask &= ~MIXED;
        }

        fireStateChanged();
    }
}
