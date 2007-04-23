/*
 * @(#)NumberFormatConverter.java 4/10/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Converter which converts currency to String and converts it back.
 */
abstract public class NumberFormatConverter extends NumberConverter {
    public NumberFormatConverter(NumberFormat format) {
        super(format);
    }

    public Object fromString(String string, ConverterContext context) {
        try {
            return getNumberFormat().parse(string);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
