/*
 * @(#)SplitButtonGroup.java 2/18/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;

/**
 * SplitButtonGroup extends ButtonGroup to provide the same button grouping function
 * for JideToggleSplitButton.
 * <p/>
 * SplitButtonGroup supports regular JButton or JideButton as well.
 */
public class SplitButtonGroup extends ButtonGroup {
    /**
     * The current selection.
     */
    ButtonModel selection = null;

    /**
     * Creates a new <code>ButtonGroup</code>.
     */
    public SplitButtonGroup() {
    }

    /**
     * Adds the button to the group.
     *
     * @param b the button to be added
     */
    @Override
    public void add(AbstractButton b) {
        if (b == null) {
            return;
        }
        buttons.addElement(b);

        if (b instanceof JideSplitButton) {
            if (((JideSplitButton) b).isButtonSelected()) {
                if (selection == null) {
                    selection = b.getModel();
                }
                else {
                    ((JideSplitButton) b).setButtonSelected(false);
                }
            }
        }
        else {
            if (b.isSelected()) {
                if (selection == null) {
                    selection = b.getModel();
                }
                else {
                    b.setSelected(false);
                }
            }
        }

        b.getModel().setGroup(this);
    }

    /**
     * Removes the button from the group.
     *
     * @param b the button to be removed
     */
    @Override
    public void remove(AbstractButton b) {
        if (b == null) {
            return;
        }
        buttons.removeElement(b);
        if (b.getModel() == selection) {
            selection = null;
        }
        b.getModel().setGroup(null);
    }

    /**
     * Sets the selected value for the <code>ButtonModel</code>.
     * Only one button in the group may be selected at a time.
     *
     * @param m the <code>ButtonModel</code>
     * @param b <code>true</code> if this button is to be
     *          selected, otherwise <code>false</code>
     */
    @Override
    public void setSelected(ButtonModel m, boolean b) {
        if (b && m != null && m != selection) {
            ButtonModel oldSelection = selection;
            selection = m;
            if (oldSelection != null) {
                if (oldSelection instanceof SplitButtonModel)
                    ((SplitButtonModel) oldSelection).setButtonSelected(false);
                else
                    oldSelection.setSelected(false);
            }
            if (m instanceof SplitButtonModel)
                ((SplitButtonModel) m).setButtonSelected(true);
            else
                m.setSelected(true);
        }
    }

    /**
     * Returns whether a <code>ButtonModel</code> is selected.
     *
     * @return <code>true</code> if the button is selected,
     *         otherwise returns <code>false</code>
     */
    @Override
    public boolean isSelected(ButtonModel m) {
        return (m == selection);
    }

}
