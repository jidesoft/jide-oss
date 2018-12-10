/*
 * @(#)Selectable.java 5/11/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

/**
 * Selectable is an interface indicating something is selectable.
 */
public interface Selectable {
    /**
     * Sets it as selected.
     *
     * @param selected
     */
    void setSelected(boolean selected);

    /**
     * Gets the selected status.
     *
     * @return true if it is selected. Otherwise, false.
     */
    boolean isSelected();

    /**
     * Inverts the selection status.
     */
    void invertSelected();

    /**
     * Enabled selection change. Enabled false doesn't mean selected is false. If it is selected before,
     * setEnable(false) won't make selected become false. In the other word, setEnabled won't change the the value of
     * isSelected().
     *
     * @param enabled
     */
    void setEnabled(boolean enabled);

    /**
     * Checks if selection change is allowed.
     *
     * @return true if selection change is allowed.
     */
    boolean isEnabled();
}
