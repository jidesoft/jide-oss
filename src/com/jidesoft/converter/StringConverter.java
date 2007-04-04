/*
 * @(#) IntegerConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;


/**
 * Converter which converts String to String and converts it back.
 */
public class StringConverter extends DefaultObjectConverter {
    /**
     * ConverterContext if the String is a file name.
     */
    public static ConverterContext CONTEXT_FILENAME = new ConverterContext("String.FileName");

    /**
     * ConverterContext if the String is a multiple line text.
     */
    public static ConverterContext CONTEXT_MULTILINE = new ConverterContext("String.Multiline");
}
