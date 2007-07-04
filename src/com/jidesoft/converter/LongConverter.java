/*
 * @(#)LongConverter.java 3/9/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;


/**
 * Converter which converts Long to String and converts it back.
 */
public class LongConverter extends NumberConverter {
    public LongConverter() {
        this(DecimalFormat.getIntegerInstance());
    }

    public LongConverter(NumberFormat format) {
        super(format);
    }

    public Object fromString(String string, ConverterContext context) {
        try {
            return getNumberFormat().parse(string).longValue();
        }
        catch (ParseException e) {
            return null;
        }
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
