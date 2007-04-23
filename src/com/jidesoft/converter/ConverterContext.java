/*
 * @(#) ConverterContext.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

/**
 * The context object used by ObjectConverter.
 * For the same type, we may need different way to convert them. This context
 * is used so that user can register different converters for the same type.
 */
public class ConverterContext extends AbstractContext {
    /**
     * Default converter context with empty name and no user object.
     */
    public static ConverterContext DEFAULT_CONTEXT = new ConverterContext("");

    /**
     * Creates a converter context with a name.
     *
     * @param name
     */
    public ConverterContext(String name) {
        super(name);
    }

    /**
     * Creates a converter contex with a name and an object.
     *
     * @param name
     * @param object the user object. It can be used as any object to pass informaton along.
     */
    public ConverterContext(String name, Object object) {
        super(name, object);
    }
}
