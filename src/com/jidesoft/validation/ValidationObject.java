/*
 * @(#)ValidationObject.java	1.32 03/01/23
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.validation;

import java.util.EventObject;

/**
 * ValidationObject is an object containing the information that needed by Validator.
 * The base class has three things - source, new value and old value.
 * <p/>
 * The source is the object who has the Validator. In the case of cell
 * editor, cell editor will be the source. In the case of table, source will be
 * the table.
 * <p/>
 * Normally ValidationObject are accompanied by the old and new value.
 * If the new value is a primitive
 * type (such as int or boolean) it must be wrapped as the
 * corresponding java.lang.* Object type (such as Integer or Boolean).
 * <p/>
 * Null values may be provided for the old and the new values if their
 * true values are not known.
 * <p/>
 * Users can extend this class to create their own ValidationObject to provide
 * additional information that needed by Validator. For example, TableValidationObject
 * extends ValidationObject to add row and column information.
 */

public class ValidationObject extends EventObject {

    /**
     * New value.  May be null if not known.
     */
    private Object _newValue;

    /**
     * Previous value.  May be null if not known.
     */
    private Object _oldValue;

    /**
     * Constructs a new <code>ValidationObject</code>.
     *
     * @param source   The source that sends this ValidationObject.
     * @param oldValue The old value.
     * @param newValue The new value.
     */
    public ValidationObject(Object source, Object oldValue, Object newValue) {
        super(source);
        _newValue = newValue;
        _oldValue = oldValue;
    }


    /**
     * Sets the new value, expressed as an Object.
     *
     * @return The new value, expressed as an Object.
     */
    public Object getNewValue() {
        return _newValue;
    }

    /**
     * Gets the old value, expressed as an Object.
     *
     * @return The old value, expressed as an Object.
     */
    public Object getOldValue() {
        return _oldValue;
    }

    @Override
    public String toString() {
        String properties =
                " source=" + getSource() +
                        " oldValue=" + getOldValue() +
                        " newValue=" + getNewValue() +
                        " ";
        return getClass().getName() + "[" + properties + "]";
    }
}
