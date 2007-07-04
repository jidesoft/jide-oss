/*
 * @(#)ByteConverter.java 1/29/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Converter which converts Byte to String and converts it back.
 */
public class ByteConverter extends NumberConverter {
    public ByteConverter() {
        this(DecimalFormat.getIntegerInstance());
    }

    public ByteConverter(NumberFormat format) {
        super(format);
    }

    public Object fromString(String string, ConverterContext context) {
        try {
            return getNumberFormat().parse(string).byteValue();
        }
        catch (ParseException e) {
            return null;
        }
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
