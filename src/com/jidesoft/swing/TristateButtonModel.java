/*
 * @(#)TristateButtonModel.java 5/20/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;


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

    TristateButtonModel() {
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

    @Override
    public void setSelected(boolean b) {
        boolean mixed = isMixed();
        if (mixed) {
            stateMask &= ~MIXED;
            internalSetSelected(!isSelected());
        }
        super.setSelected(b);
        fireActionPerformed(new ActionEvent(this, 0, "stateChanged"));
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
        if ((isMixed() == b) || !isEnabled()) {
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
        fireActionPerformed(new ActionEvent(this, 0, "stateChanged"));
    }
}
