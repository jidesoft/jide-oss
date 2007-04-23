/*
 * @(#)ShortConverter.java 3/9/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;


/**
 * Converter which converts Short to String and converts it back.
 */
public class ShortConverter extends NumberConverter {
    public ShortConverter() {
        this(DecimalFormat.getIntegerInstance());
    }

    public ShortConverter(NumberFormat format) {
        super(format);
    }

    public Object fromString(String string, ConverterContext context) {
        try {
            return new Short(getNumberFormat().parse(string).shortValue());
        }
        catch (ParseException e) {
            return null;
        }
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
