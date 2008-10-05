/*
 * @(#) Gripper.java
 * Last modified date: 2/9/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import com.jidesoft.plaf.GripperUI;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import java.awt.*;


/**
 * Gripper is a component that you can drag. Actually the component itself doesn't allow you to drag, it is up to the
 * component who uses this Gripper to add mouse motion listener and do the dragging. The Gripper will paint itself so
 * that user can tell immediately that it is something dragable.
 */
public class Gripper extends JComponent implements SwingConstants, Alignable, DragableHandle, UIResource {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "GripperUI";

    /**
     * Identifies a change from rollover enabled to disabled or back to enabled.
     */
    public static final String ROLLOVER_ENABLED_CHANGED_PROPERTY = "rolloverEnabled";

    private boolean _rolloverEnabled = false;

    private boolean _rollover;

    public static final String ROLLOVER_PROPERTY = "ROLLOVER";
    public static final String SELECTED_PROPERTY = "SELECTED";

    private int _orientation;

    private boolean _selected;

    /**
     * Creates a new horizontal separator.
     */
    public Gripper() {
        this(HORIZONTAL);
    }

    /**
     * Creates a new separator with the specified horizontal or vertical orientation.
     *
     * @param orientation an integer specifying <code>SwingConstants.HORIZONTAL</code> or
     *                    <code>SwingConstants.VERTICAL</code>
     * @throws IllegalArgumentException if <code>orientation</code> is neither <code>SwingConstants.HORIZONTAL</code>
     *                                  nor <code>SwingConstants.VERTICAL</code>
     */
    public Gripper(int orientation) {
        setOrientation(orientation);
        setFocusable(false);
        updateUI();
    }

    /**
     * Returns the L&F object that renders this component.
     *
     * @return the SeparatorUI object that renders this component
     */
    public GripperUI getUI() {
        return (GripperUI) ui;
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see javax.swing.JComponent#updateUI
     */
    @Override
    public void updateUI() {
        if (UIDefaultsLookup.get(uiClassID) == null) {
            LookAndFeelFactory.installJideExtension();
        }
        setUI(UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return the string "GripperUI"
     *
     * @see javax.swing.JComponent#getUIClassID
     * @see javax.swing.UIDefaults#getUI
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * return true if it supports vertical orientation.
     *
     * @return true if it supports vertical orientation
     */
    public boolean supportVerticalOrientation() {
        return true;
    }

    /**
     * return true if it supports horizontal orientation.
     *
     * @return true if it supports horizontal orientation
     */
    public boolean supportHorizontalOrientation() {
        return true;
    }

    /**
     * Changes the orientation.
     *
     * @param orientation the new orientation.
     */
    public void setOrientation(int orientation) {
        int old = _orientation;
        if (old != orientation) {
            _orientation = orientation;
            firePropertyChange(PROPERTY_ORIENTATION, old, orientation);
        }
    }

    /**
     * Gets the orientation.
     *
     * @return orientation
     */
    public int getOrientation() {
        return _orientation;
    }

    /**
     * Gets the cursor set in the component. If the component does not have a cursor set, the cursor of its parent is
     * returned. If no cursor is set in the entire hierarchy, <code>Cursor.DEFAULT_CURSOR</code> is returned.
     *
     * @see #setCursor
     * @since JDK1.1
     */
    @Override
    public Cursor getCursor() {
        if (isEnabled()) {
            return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        }
        else {
            return super.getCursor();
        }
    }

    /**
     * Gets the <code>rolloverEnabled</code> property.
     *
     * @return the value of the <code>rolloverEnabled</code> property
     *
     * @see #setRolloverEnabled
     */
    public boolean isRolloverEnabled() {
        return _rolloverEnabled;
    }

    /**
     * Sets the <code>rolloverEnabled</code> property, which must be <code>true</code> for rollover effects to occur.
     * The default value for the <code>rolloverEnabled</code> property is <code>false</code>. Some look and feels might
     * not implement rollover effects; they will ignore this property.
     *
     * @param b if <code>true</code>, rollover effects should be painted
     * @see #isRolloverEnabled
     */
    public void setRolloverEnabled(boolean b) {
        boolean oldValue = _rolloverEnabled;
        if (b != oldValue) {
            _rolloverEnabled = b;
            firePropertyChange(ROLLOVER_ENABLED_CHANGED_PROPERTY, oldValue, _rolloverEnabled);
            repaint();
        }
    }

    public boolean isRollover() {
        return _rollover;
    }

    public void setRollover(boolean rollover) {
        boolean oldValue = _rollover;
        if (rollover != oldValue) {
            _rollover = rollover;
            firePropertyChange(ROLLOVER_PROPERTY, oldValue, rollover);
            repaint();
        }
    }

    public boolean isSelected() {
        return _selected;
    }

    public void setSelected(boolean selected) {
        boolean oldValue = _selected;
        if (selected != oldValue) {
            _selected = selected;
            firePropertyChange(SELECTED_PROPERTY, oldValue, _selected);
            repaint();
        }
    }
}
