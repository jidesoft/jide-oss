/*
 * @(#)StringConverter.java 3/28/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

/**
 * An interface to convert a string to anther one.
 */
public interface StringConverter {
    /**
     * Convert a string to another string, for example, to make it shorter.
     *
     * @param str string to be converted
     * @return string after conversion
     */
    String convert(String str);
}
