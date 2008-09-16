/*
 * @(#)MultipleEnumConverter.java 7/5/2008
 *
 * Copyright 2002 - 2008 JIDE Software Inc. All rights reserved.
 *
 */

package com.jidesoft.converter;

import java.lang.reflect.Array;

/**
 * MultipleEnumConverter is a special ArrayConverter that converts a string to/from array. Each element in the element
 * is converted to the object using a specified EnumConverter.
 */
public class MultipleEnumConverter extends ArrayConverter {
    private EnumConverter _enumConverter;

    public MultipleEnumConverter(String separator, EnumConverter converter) {
        super(separator, -1, converter.getType());
        _enumConverter = converter;
    }

    public Class<?> getType() {
        return Array.newInstance(_enumConverter.getType(), 0).getClass();
    }

    public EnumConverter getEnumConverter() {
        return _enumConverter;
    }

    public void setEnumConverter(EnumConverter enumConverter) {
        _enumConverter = enumConverter;
    }

    public String toString(Object object, ConverterContext context) {
        if (object == null) {
            return "";
        }
        if (object.getClass().isArray()) {
            int length = Array.getLength(object);
            Object[] values = new Object[length];
            for (int i = 0; i < length; i++) {
                Object o = Array.get(object, i);
                values[i] = o;
            }
            return arrayToString(values, context);
        }
        return "";
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public Object fromString(String string, ConverterContext context) {
        return arrayFromString(string, context);
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }

    @Override
    protected String toString(int i, Object o, ConverterContext context) {
        return _enumConverter != null ? _enumConverter.toString(o, context) : "" + o;
    }

    @Override
    protected Object fromString(int i, String s, ConverterContext context) {
        return _enumConverter != null ? _enumConverter.fromString(s, context) : s;
    }

    transient private ConverterContext _conext;

    /**
     * Gets the converter context of this converter. The name of the context is the name of the converter where you pass
     * in to EnumConverter's constructor.
     *
     * @return the converter context of this converter.
     */
    public ConverterContext getContext() {
        if (_conext == null) {
            _conext = ConverterContext.getArrayConverterContext(_enumConverter.getContext());
        }
        return _conext;
    }

//    public static void main(String[] args) {
//        final EnumConverter genderConverter = new EnumConverter("Gender", String.class,
//                new String[]{
//                        "*",
//                        "F",
//                        "M",
//                },
//                new String[]{
//                        "Any",
//                        "Female",
//                        "Male",
//                },
//                "");
//        MultipleEnumConverter converter  = new MultipleEnumConverter("; ", genderConverter);
//        Object o = converter.fromString("Female; Male", null);
//        String s = converter.toString(new String[]{"F", "M"}, null);
//        System.out.println(s);
//    }
}