package com.jidesoft.validation;

import java.util.EventObject;

/**
 * RowValidationObject is an object containing the information that needed by RowValidator.
 * The base class has two things - the JTable which is the source (you need to cast to JTable), and the row index.
 * <p/>
 * Please note, the row index is the visual row index as the validation is triggered in the JideTable code.
 * In order to find the actual row index as in the table model, you will need TableModelWrapperUtils.getActualRowAt method.
 * <p/>
 * Users can extend this class to create their own RowValidationObject to provide
 * additional information that needed by RowValidator.
 */

public class RowValidationObject extends EventObject {

    private int _rowIndex;

    public RowValidationObject(Object source, int rowIndex) {
        super(source);
        _rowIndex = rowIndex;
    }

    /**
     * Gets the row index. the row index is the visual row index.
     * In order to find the actual row index as in the table model, you will need TableModelWrapperUtils.getActualRowAt method.
     *
     * @return the row index.
     */
    public int getRowIndex() {
        return _rowIndex;
    }

    @Override
    public String toString() {
        String properties =
                " source=" + getSource() +
                        " rowIndex=" + getRowIndex() +
                        " ";
        return getClass().getName() + "[" + properties + "]";
    }
}
