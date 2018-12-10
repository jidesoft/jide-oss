/*
 * @(#)TableValidationObject.java
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.validation;

/**
 * TableValidationObject extends ValidationObject
 * to provide row and column information of the value to be validated.
 */
public class TableValidationObject extends ValidationObject {
    private int _row;
    private int _column;

    public TableValidationObject(Object source, Object oldValue, Object newValue) {
        super(source, oldValue, newValue);
    }

    public TableValidationObject(Object source, Object oldValue, Object newValue, int row, int column) {
        super(source, oldValue, newValue);
        _row = row;
        _column = column;
    }

    /**
     * Gets the row.
     *
     * @return the row.
     */
    public int getRow() {
        return _row;
    }

    /**
     * Sets the row.
     *
     * @param row the row.
     */
    public void setRow(int row) {
        _row = row;
    }

    /**
     * Gets the column.
     *
     * @return the column.
     */
    public int getColumn() {
        return _column;
    }

    /**
     * Sets the column.
     *
     * @param column the column.
     */
    public void setColumn(int column) {
        _column = column;
    }

    @Override
    public String toString() {
        String properties =
                " source=" + getSource() +
                        " oldValue=" + getOldValue() +
                        " newValue=" + getNewValue() +
                        " row=" + getRow() +
                        " column=" + getColumn() +
                        " ";
        return getClass().getName() + "[" + properties + "]";
    }
}
