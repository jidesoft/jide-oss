package com.jidesoft.validation;

import java.util.EventListener;

/**
 * <code>Validator</code> is an interface for validating a row in table.
 */
public interface RowValidator extends EventListener {

    /**
     * Validates the value in the RowValidationObject.
     * <p/>
     * The event has the row index and the JTable which is the source. The implementation of this method should validate
     * if the row is valid. If the row is valid, simply return ValidationResult.OK. Otherwise, constructs a
     * ValidationResult and provide the id and message. You can also return RowValidationResult which has an array of
     * invalid column indices. Usually when you validate a row, you will be able to figure out values in one or several
     * columns are invalid. You can pass them to RowValidationResult and return it. Listeners for the RowValidator can
     * decide to display some error indications to indicate which cells are invalid.
     * <p/>
     * Although <code>ValidationResult</code> allows user to specify the fail behavior such as FAIL_BEHAVIOR_REVERT,
     * FAIL_BEHAVIOR_RESET or FAIL_BEHAVIOR_PERSIST, in the case of RowValidator, none of the options make sense so the
     * fail behavior you set will be ignored. So it is better you process the error right there in the validating
     * method. See the code below for an example.
     * <code><pre>
     * public ValidationResult validating(RowValidationObject vo) {
     *     boolean valid = ...; // validate the row
     *     if (valid) {
     *         return null;
     *     }
     *     else {
     *         PortingUtils.notifyUser();  // notify the user
     *         table.editCellAt(vo.getRowIndex(), 5); // force editing mode in case this is the invalid cell.
     *         return new RowValidationResult(false, new int[]{5});
     *     }
     * } </pre></code>
     * <p/>
     * Please note all the RowValidators added to a JideTable will be used for to validate any rows.
     *
     * @param vo the ValidationObject
     * @return ValidationResult
     */
    ValidationResult validating(RowValidationObject vo);
}
