/*
 * @(#)InstallData.java 4/22/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

/**
 * The element used by CheckBoxList's ListModel. In order to allow check box in JList without messing up when list model
 * changes, we use this class to store the object itself and a boolean to indicated if the row is selected.
 */
public class DefaultSelectable implements Selectable {
    protected Object _object;
    protected boolean _selected = false;
    protected boolean _enabled = true;

    /**
     * Creates CheckBoxListElement with an actual object. In the case of CheckBoxList, instead of add the object
     * directly to ListModel, you should wrap it in CheckBoxListElement and add CheckBoxListElement into ListModel.
     *
     * @param object the actual object
     */
    public DefaultSelectable(Object object) {
        _object = object;
    }

    /**
     * Sets the actual element.
     *
     * @param object
     */
    public void setObject(Object object) {
        _object = object;
    }

    /**
     * Gets the actual element.
     *
     * @return the actual element.
     */
    public Object getObject() {
        return _object;
    }

    /**
     * Sets it as selected.
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        _selected = selected;
    }

    /**
     * Inverts the selection status.
     */
    public void invertSelected() {
        setSelected(!_selected);
    }

    /**
     * Gets the selected status.
     *
     * @return true if it is selected. Otherwise, false.
     */
    public boolean isSelected() {
        return _selected;
    }

    /**
     * Enabled selection change. Enabled false doesn't mean selected is false. If it is selected before,
     * setEnable(false) won't make selected become false. In the other word, setEnabled won't change the the value of
     * isSelected().
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        _enabled = enabled;
    }

    /**
     * Checks if selection change is allowed.
     *
     * @return true if selection change is allowed.
     */
    public boolean isEnabled() {
        return _enabled;
    }

    /**
     * Overrides to consider the hash code of the object only. From outside point of view, this class should behave just
     * like object itself. That's why we override hashCode.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return (_object != null ? _object.hashCode() : 0);
    }

    /**
     * Overrides to consider the toString() of object only. From outside point of view, this class should behave just
     * like object itself. That's why we override toString.
     *
     * @return toString() of object.
     */
    @Override
    public String toString() {
        return (_object != null ? _object.toString() : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DefaultSelectable) {
            if (getObject() == null && ((DefaultSelectable) obj).getObject() == null) {
                return true;
            }
            else if (getObject() == null && ((DefaultSelectable) obj).getObject() != null) {
                return false;
            }
            return getObject().equals(((DefaultSelectable) obj).getObject());
        }
        else if (obj == null && getObject() == null) {
            return true;
        }
        else {
            return false;
        }
    }
}
