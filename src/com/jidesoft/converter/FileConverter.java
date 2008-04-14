/*
 * @(#) FileConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import java.io.File;

/**
 * Converter which converts File to String and converts it back.
 */
public class FileConverter implements ObjectConverter {
    public String toString(Object object, ConverterContext context) {
        if (object == null || !(object instanceof File)) {
            return null;
        }
        else {
            return ((File) object).getAbsolutePath();
        }
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public Object fromString(String string, ConverterContext context) {
        if (string == null || string.trim().length() == 0) {
            return null;
        }
        return new File(string);
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
