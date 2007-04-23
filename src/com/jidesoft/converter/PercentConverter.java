/*
 * @(#)PercentConverter.java 4/10/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

import java.text.NumberFormat;

/**
 * Converter which converts percentage to String and converts it back.
 */
public class PercentConverter extends NumberFormatConverter {
    public static ConverterContext CONTEXT = new ConverterContext("Percent");

    public PercentConverter() {
        this(NumberFormat.getPercentInstance());
    }

    public PercentConverter(NumberFormat format) {
        super(format);
    }
}
