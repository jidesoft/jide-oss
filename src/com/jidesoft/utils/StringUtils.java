/*
 * @(#)StringUtils.java 8/19/2009
 *
 * Copyright 2002 - 2009 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.utils;

import java.util.List;

public class StringUtils {
    /**
     * Converts the supplied string to CamelCase by converting the first character to upper case and the rest of the
     * string to lower case.
     *
     * @param str
     * @return a string
     */
    public static String toCamelCase(String str) {
        String firstLetter = str.substring(0, 1);
        String rest = str.substring(1);
        return firstLetter.toUpperCase() + rest.toLowerCase();
    }

    /**
     * Constructs a list of items in a string form using a prefix and suffix to denote the start and end of the list and
     * a separator string in between the items. For example, with a prefix of '(', a suffix of ')', a separator of ','
     * and a list ["a", "b", "c"] it would generate the string "(a,b,c)"
     *
     * @param prefix
     * @param suffix
     * @param separator
     * @param objects
     * @return
     */
    public static String stringList(String prefix, String suffix, String separator, Object... objects) {
        StringBuilder builder = new StringBuilder(prefix);
        for (int i = 0; i < objects.length; i++) {
            builder.append(objects[i].toString());
            if (i < objects.length - 1) {
                builder.append(separator);
            }
        }
        builder.append(suffix);
        return builder.toString();
    }

    public static String stringList(Object[] objects) {
        return stringList("[", "]", ",", objects);
    }

    public static String stringList(List<?> objects) {
        if (objects == null) {
            return "";
        }
        return stringList(objects.toArray());
    }
}
