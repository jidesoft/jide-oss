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
     * ConverterContext for color to convert to RGB and alpha string.
     */
    public static ConverterContext CONTEXT_RGBA = new ConverterContext("Color.rgba");


    /**
     * ConverterContext for color to convert to HEX string.
     */
    public static ConverterContext CONTEXT_HEX_WITH_ALPHA = new ConverterContext("Color.HexWithAlpha");

    /**
     * Create a default color converter.
     */
    public ColorConverter() {
    }
}
