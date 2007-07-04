/*
 * @(#) DefaultObjectConverter.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.converter;

import javax.swing.*;
import java.text.ParseException;


/**
 * Default object converter. It converts an object to a String using either toString()
 * or the AbstractFormatter specified in the ConverterContex's userObject.
 * <p/>
 * For example,
 * <code><pre>
 *  MaskFormatter mask = null;
 *  try {
 *      mask = new MaskFormatter("###-##-####");
 *  }
 *  catch (ParseException e) {
 *      e.printStackTrace();
 *  }
 *  ConverterContext ssnConverterContext = new ConverterContext("SSN", mask);
 * </pre></code>
 * If so, it will use the MaskFormatter's stringToValue and valueToString methods to do the conversion.
 */
public class DefaultObjectConverter implements ObjectConverter {
    public DefaultObjectConverter() {
    }

    public String toString(Object object, ConverterContext context) {
        if (context != null && context.getUserObject() instanceof JFormattedTextField.AbstractFormatter) {
            try {
                return ((JFormattedTextField.AbstractFormatter) context.getUserObject()).valueToString(object);
            }
            catch (ParseException e) {
                // ignore
            }
        }
        return object == null ? "" : object.toString();
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public Object fromString(String string, ConverterContext context) {
        if (context != null && context.getUserObject() instanceof JFormattedTextField.AbstractFormatter) {
            try {
                return ((JFormattedTextField.AbstractFormatter) context.getUserObject()).stringToValue(string);
            }
            catch (ParseException e) {
                // ignore
            }
        }
        return string;
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}
