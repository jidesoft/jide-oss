package com.jidesoft.utils;

import java.io.Serializable;

/**
 * Abstract implementation of {@link WildcardSupport}. It implements the convert method but leave the other three
 * methods defining the wildcards to the subclass.
 */
abstract public class AbstractWildcardSupport implements WildcardSupport, Serializable {

    public String convert(String s) {
        // if it doesn't have the two special characters we support, we don't need to use regular expression.
        char zeroOrMoreQuantifier = getZeroOrMoreQuantifier();
        int posAny = zeroOrMoreQuantifier == 0 ? -1 : s.indexOf(zeroOrMoreQuantifier);
        char zeroOrOneQuantifier = getZeroOrOneQuantifier();
        int posOne = zeroOrOneQuantifier == 0 ? -1 : s.indexOf(zeroOrOneQuantifier);
        char oneOrMoreQuantifier = getOneOrMoreQuantifier();
        int posOneOrMore = oneOrMoreQuantifier == 0 ? -1 : s.indexOf(oneOrMoreQuantifier);
        //
        if (posAny == -1 && posOne == -1 && posOneOrMore == -1) {
            return s;
        }

        StringBuffer buffer = new StringBuffer();
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (zeroOrOneQuantifier != 0 && c == zeroOrOneQuantifier) {
                buffer.append(".");
            }
            else if (zeroOrMoreQuantifier != 0 && c == zeroOrMoreQuantifier) {
                buffer.append(".*");
            }
            else if (oneOrMoreQuantifier != 0 && c == oneOrMoreQuantifier) {
                buffer.append("..*");
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
