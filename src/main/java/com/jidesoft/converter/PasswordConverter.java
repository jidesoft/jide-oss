/*
 * @(#)PasswordConverter.java 7/30/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.converter;

import java.util.Arrays;


/**
 * Converter which converts String to String and converts it back.
 */
public class PasswordConverter extends DefaultObjectConverter {
    /**
     * ConverterContext if the String is a file name.
     */
    public static ConverterContext CONTEXT = new ConverterContext("Password");
    private char _echoChar = '*';

    public PasswordConverter() {
    }

    /**
     * Creates a PasswordConverter.
     *
     * @param echoChar The echo char. It is used to replace the real password so that other people can't see what user is typing.
     */
    public PasswordConverter(char echoChar) {
        _echoChar = echoChar;
    }

    @Override
    public String toString(Object object, ConverterContext context) {
        if (object instanceof char[]) {
            int length = ((char[]) object).length;
            char[] chars = new char[length];
            Arrays.fill(chars, getEchoChar());
            return new String(chars);
        }
        else if (object != null) {
            int length = object.toString().length();
            char[] chars = new char[length];
            Arrays.fill(chars, getEchoChar());
            return new String(chars);
        }
        else {
            return "";
        }
    }

    public char getEchoChar() {
        return _echoChar;
    }

    public void setEchoChar(char echoChar) {
        _echoChar = echoChar;
    }

    @Override
    public boolean supportFromString(String string, ConverterContext context) {
        return false;
    }

    @Override
    public Object fromString(String string, ConverterContext context) {
        return null;
    }
}
