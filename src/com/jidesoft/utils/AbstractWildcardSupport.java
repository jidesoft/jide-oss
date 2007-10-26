package com.jidesoft.utils;

/**
 * Abstract implementation of {@link WildcardSupport}. It implements the convert method but leave the other three methods
 * defining the wildcards to the subclass.
 */
abstract public class AbstractWildcardSupport implements WildcardSupport {

    public String convert(String s) {
        // if it doesn't have the two special characters we support, we don't need to use regular expression.
        int posAny = s.indexOf(getZeroOrMoreQuantifier());
        int posOne = s.indexOf(getZeroOrOneQuantifier());
        int posOneOrMore = s.indexOf(getOneOrMoreQuantifier());
        //
        if (posAny == -1 && posOne == -1 && posOneOrMore == -1) {
            return s;
        }

        StringBuffer buffer = new StringBuffer();
        int length = s.length();
        buffer.append('^');
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            // replace '?' with '.'
            if (c == '?') {
                buffer.append(".");
            }
            else if (c == '*') {
                buffer.append(".*");
            }
            else if ("(){}[].^$\\".indexOf(c) != -1) { // escape all other special characters
                buffer.append('\\');
                buffer.append(c);
            }
            else {
                buffer.append(c);
            }
        }

        return buffer.toString();
    }
}
