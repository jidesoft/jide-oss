/*
 * @(#) ColorConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;


/**
 * Converter which converts Color to String and converts it back.
 */
abstract public class ColorConverter implements ObjectConverter {

    /**
     * ConverterContext for color to convert to RGB string.
     */
    public static ConverterContext CONTEXT_RGB = ConverterContext.DEFAULT_CONTEXT;

    /**
     * ConverterContext for color to convert to HEX string.
     */
    public static ConverterContext CONTEXT_HEX = new ConverterContext("Color.Hex");

    /**
     * Create a default color converter.
     */
    public ColorConverter() {
    }
}
