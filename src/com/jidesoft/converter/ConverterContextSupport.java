/*
 * @(#) ConverterContextSupport.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;


/**
 * The interface indicates the class who extends it can support ConverterContext.
 *
 * @see ConverterContext
 */
public interface ConverterContextSupport {

    /**
     * Sets the converter context.
     *
     * @param context converter context
     */
    void setConverterContext(ConverterContext context);

    /**
     * Gets the converter context.
     *
     * @return converter context
     */
    ConverterContext getConverterContext();

    /**
     * Gets the class of the value.
     *
     * @return the class of the value.
     */
    Class<?> getType();

    /**
     * Sets the class of the value.
     *
     * @param clazz
     */
    void setType(Class<?> clazz);

}
