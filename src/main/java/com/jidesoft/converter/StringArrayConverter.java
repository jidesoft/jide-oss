package com.jidesoft.converter;

public class StringArrayConverter implements ObjectConverter {
    public static final ConverterContext CONTEXT = new ConverterContext("StringArrayConverter");
    private String _separator = ";";

    /**
     * Creates a StringArrayConverter using default constructor. The semicolon (";") will be used as the separator when
     * converting from string to array and vise versa.
     */
    public StringArrayConverter() {
    }

    /**
     * Creates a StringArrayConverter with a specified separator. Please make sure the separator is not used in the
     * character set used in each string element. For example, you want to use space as separator, then each string in
     * the string array must not use space.
     *
     * @param separator the separator used to separate string to an array.
     */
    public StringArrayConverter(String separator) {
        _separator = separator;
    }

    public String toString(Object object, ConverterContext context) {
        if (object == null) {
            return null;
        }
        if (object.getClass().isArray()) {
            String[] array = (String[]) object;
            StringBuffer b = new StringBuffer();
            for (int i = 0; i < array.length; i++) {
                if (i > 0) b.append(_separator);
                b.append(array[i]);
            }
            return b.toString();
        }
        return null;
    }

    public boolean supportToString(Object object, ConverterContext context) {
        return true;
    }

    public Object fromString(String string, ConverterContext context) {
        if (string.length() == 0) {
            return new String[0];
        }
        return string.split(_separator);
    }

    public boolean supportFromString(String string, ConverterContext context) {
        return true;
    }
}