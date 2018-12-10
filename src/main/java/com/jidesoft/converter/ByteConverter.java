/*
 * @(#)ByteConverter.java 1/29/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
        Number number = parseNumber(string);
        return number != null ? number.byteValue() : null;
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
