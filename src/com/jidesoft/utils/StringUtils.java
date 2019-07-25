/*
 * @(#)StringUtils.java 8/19/2009
 *
 * Copyright 2002 - 2009 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.utils;

import com.jidesoft.range.Range;
import com.jidesoft.range.StringRange;

import java.util.List;

public class StringUtils {
    /**
     * Converts the supplied string to CamelCase by converting the first character to upper case and the rest of the
     * string to lower case.
     *
     * @param str the input string
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
     * @param prefix    the prefix for the concatenated string.
     * @param suffix    the suffix for the concatenated string.
     * @param separator the separator between the elements.
     * @param objects   the array of the elements.
     * @return a concatenated string of the elements in the array.
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

    /**
     * Returns the min String in the strings list.
     *
     * @param strings the numbers to calculate the min.
     * @return the min String in the strings list.
     */
    public static String min(List<String> strings) {
        String min = null;
        for (String value : strings) {
            if (min == null || value.compareTo(min) < 0) {
                min = value;
            }
        }
        return min;
    }

    /**
     * Returns the max String in the strings list.
     *
     * @param strings the numbers to calculate the max.
     * @return the max String in the strings list.
     */
    public static String max(List<String> strings) {
        String max = null;
        for (String value : strings) {
            if (max == null || value.compareTo(max) > 0) {
                max = value;
            }
        }
        return max;
    }

    /**
     * Returns the range of numbers.
     *
     * @param strings the numbers to calculate the range.
     * @return the range of the numbers.
     */
    public static Range<String> range(List<String> strings) {
        String min = null;
        String max = null;
        for (String value : strings) {
            if (max == null || value.compareTo(max) > 0) {
                max = value;
            }
            if (min == null || value.compareTo(min) < 0) {
                min = value;
            }
        }
        return new StringRange(min, max);
    }

    public static int countChar(char c, String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (c == text.charAt(i)) {
                count++;
            }
        }
        return count;
    }
}
