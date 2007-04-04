/*
 * @(#) IntegerConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Converter which converts Number to String and converts it back. You can pass in
 * a NumberFormat as UserObject of ConverterContext if you want to control the
 * format of the number such as maximum decimal point etc.
 */
abstract public class NumberConverter implements ObjectConverter {
    private NumberFormat _numberFormat;

    /**
     * Creates a number converter with no NumberFormat.
     */
    public NumberConverter() {
    }

    /**
     * Creates the number converter with a specified NumberFormat.
     *
     * @param format
     */
    public NumberConverter(NumberFormat format) {
        _numberFormat = format;
    }

    public String toString(Object object, ConverterContext context) {
        // format on userOjbect has a higher priority.
        try {
            if (context == null || context.getUserObject() == null || !(context.getUserObject() instanceof NumberFormat)) {
                return getNumberFormat().format(object);
            }
            else {
                NumberFormat format = (NumberFormat) context.getUserObject();
                return format.format(object);
            }
        }
        catch (IllegalArgumentException e) {
            return "";
        }
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public void setNumberFormat(NumberFormat numberFormat) {
        _numberFormat = numberFormat;
    }

    protected NumberFormat getNumberFormat() {
        if (_numberFormat == null) {
            _numberFormat = DecimalFormat.getInstance();
        }
        return _numberFormat;
    }
}
