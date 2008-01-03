package com.jidesoft.validation;

/**
 * ValidationResult for RowValidator.
 * It extends ValidationResult and adds invalidColumns as a new field.
 */
public class RowValidationResult extends ValidationResult {
    protected int[] _invalidColumns;


    public RowValidationResult() {
    }

    public RowValidationResult(int id) {
        super(id);
    }

    public RowValidationResult(boolean valid) {
        super(valid);
    }

    public RowValidationResult(int id, String message) {
        super(id, message);
    }

    public RowValidationResult(int id, boolean valid, String message) {
        super(id, valid, message);
    }

    public RowValidationResult(int id, boolean valid, int failBehavoir) {
        super(id, valid, failBehavoir);
    }

    public RowValidationResult(int id, boolean valid, int failBehavoir, String message) {
        super(id, valid, failBehavoir, message);
    }

    public RowValidationResult(int[] invalidColumns) {
        _invalidColumns = invalidColumns;
    }

    public RowValidationResult(int id, int[] invalidColumns) {
        super(id);
        _invalidColumns = invalidColumns;
    }

    public RowValidationResult(boolean valid, int[] invalidColumns) {
        super(valid);
        _invalidColumns = invalidColumns;
    }

    public RowValidationResult(int id, String message, int[] invalidColumns) {
        super(id, message);
        _invalidColumns = invalidColumns;
    }

    public RowValidationResult(int id, boolean valid, String message, int[] invalidColumns) {
        super(id, valid, message);
        _invalidColumns = invalidColumns;
    }

    public RowValidationResult(int id, boolean valid, int failBehavoir, int[] invalidColumns) {
        super(id, valid, failBehavoir);
        _invalidColumns = invalidColumns;
    }

    public RowValidationResult(int id, boolean valid, int failBehavoir, String message, int[] invalidColumns) {
        super(id, valid, failBehavoir, message);
        _invalidColumns = invalidColumns;
    }

    /**
     * Gets the invalid columns.
     *
     * @return the invalid columns.
     */
    public int[] getInvalidColumns() {
        return _invalidColumns;
    }
}
