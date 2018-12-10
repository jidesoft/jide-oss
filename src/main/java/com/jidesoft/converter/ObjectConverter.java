/*
 * @(#) ObjectConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;


/**
 * An interface that can convert a object to a String and convert from
 * String to object.
 */
public interface ObjectConverter {

    /**
     * Converts from object to String based on current locale.
     *
     * @param object  object to be converted
     * @param context converter context to be used
     * @return the String
     */
    abstract String toString(Object object, ConverterContext context);

    /**
     * If it supports toString method.
     *
     * @param object  object to be converted
     * @param context converter context to be used
     * @return true if supports toString
     */
    abstract boolean supportToString(Object object, ConverterContext context);

    /**
     * Converts from String to an object.
     *
     * @param string  the string
     * @param context context to be converted
     * @return the object converted from string
     */
    abstract Object fromString(String string, ConverterContext context);

    /**
     * If it supports fromString.
     *
     * @param string  the string
     * @param context context to be converted
     * @return true if it supports
     */
    abstract boolean supportFromString(String string, ConverterContext context);
}
