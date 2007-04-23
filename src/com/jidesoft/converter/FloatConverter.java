/*
 * @(#)FloatConverter.java 3/9/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.converter;

import java.text.NumberFormat;
import java.text.ParseException;


/**
 * Converter which converts Float to String and converts it back.
 */
public class FloatConverter extends NumberConverter {
    public FloatConverter() {
    }

    public FloatConverter(NumberFormat format) {
        super(format);
    }

    public Object fromString(String string, ConverterContext context) {
        try {
            float value = getNumberFormat().parse(string).floatValue();
            return new Float(value);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
