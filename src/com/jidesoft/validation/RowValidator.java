package com.jidesoft.validation;

import java.util.EventListener;

/**
 * <code>Validator</code> is an interface for validating a row in table.
 */
public interface RowValidator extends EventListener {

    /**
     * Validates the value in the RowValidationObject.
     * <p/>
     * The event has the row index and the JTable which is the source.
     * The implementation of this method should validate if the
     * row is valid. If the row is valid, simply return ValidationResult.OK.
     * Otherwise, constructs a ValidationResult and provide the id and message.
     * You can also return RowValidationResult which has an array of invalid column indices.
     * Usually when you validate a row, you will be able to figure out values in one or several columns
     * are invalid. You can pass them to RowValidationResult and return it. Listeners for the RowValidator can decide to display
     * some error indications to indicate which cells are invalid.
     *
     * @param vo the ValidationObject
     * @return ValidationResult
     */
    ValidationResult validating(RowValidationObject vo);
}
