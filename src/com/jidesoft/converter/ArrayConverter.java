/*
 * @(#) ArrayConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import java.util.StringTokenizer;

/**
 * An abstract class that is extended by any converters that convert to/from an array-like format, such as 1, 2, 3.
 * Examples are Point. Point(100, 200) can convert to/from "100, 200" <br> You have the choice of what the separator is;
 * separator is the ", " in the Point example above.
 */
abstract public class ArrayConverter implements ObjectConverter {

    private String _separator;

    private int _size;

    private Class<?> _elementClass;

    private Class<?>[] _elementClasses;

    /**
     * Creates an ArrayConverter.
     *
     * @param separator    separator to separate values. It should contain at least non-empty character.
     * @param size         size of the array
     * @param elementClass class of the array element. Assume all elements have the same class type. If not, use the
     *                     constructor which takes Class<?>[] as parameter.
     */
    public ArrayConverter(String separator, int size, Class<?> elementClass) {
        _separator = separator;
        _size = size;
        _elementClass = elementClass;
    }

    /**
     * Creates an ArrayConverter.
     *
     * @param separator      separator to separate values. It should contain at least non-empty character.
     * @param size           size of the array
     * @param elementClasses classes of the array element. The length must be the same as size. If not,
     *                       IllegalArgumentException will be thrown.
     */
    public ArrayConverter(String separator, int size, Class<?>[] elementClasses) {
        if (separator == null || separator.trim().length() == 0) {
            throw new IllegalArgumentException("separator cannot be empty.");
        }
        if (elementClasses == null) {
            throw new IllegalArgumentException("elementClasses cannot be null.");
        }
        if (size != elementClasses.length) {
            throw new IllegalArgumentException("size must be equal to the length of elementClasses.");
        }
        _separator = separator;
        _size = size;
        _elementClasses = elementClasses;
    }

    /**
     * Converts from array to string by concating them with separators.
     *
     * @param objects an array of objects
     * @param context converter context
     * @return string all objects concatenated with separators
     */
    public String arrayToString(Object[] objects, ConverterContext context) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < objects.length; i++) {
            Object o = objects[i];
            buffer.append(toString(i, o, context));
            if (i != objects.length - 1) {
                buffer.append(_separator);
            }
        }
        return new String(buffer);
    }

    protected String toString(int i, Object o, ConverterContext context) {
        return _elementClass != null ? ObjectConverterManager.toString(o, _elementClass, context) : ObjectConverterManager.toString(o, _elementClasses[i], context);
    }

    /**
     * Converts from string to an array of objects, using separator to separate the string.
     *
     * @param string  string to be converted
     * @param context converter context
     * @return the array
     */
    public Object[] arrayFromString(String string, ConverterContext context) {
        if (string == null || string.trim().length() == 0) {
            return null;
        }
        StringTokenizer token = new StringTokenizer(string, _separator.trim());
        Object[] objects = new Object[_size != -1 ? _size : token.countTokens()];
        for (int i = 0; i < objects.length && token.hasMoreTokens(); i++) {
            String s = token.nextToken().trim();
            objects[i] = fromString(i, s, context);
        }
        return objects;
    }

    protected Object fromString(int i, String s, ConverterContext context) {
        return _elementClass != null ? ObjectConverterManager.fromString(s, _elementClass, context) : ObjectConverterManager.fromString(s, _elementClasses[i], context);
    }

    /**
     * Gets the element class for the array.
     *
     * @return the element class for the array.
     */
    public Class<?> getElementClass() {
        return _elementClass;
    }
}
