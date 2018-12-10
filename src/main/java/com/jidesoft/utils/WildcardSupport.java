package com.jidesoft.utils;

public interface WildcardSupport {
    /**
     * Gets the quantifier that indicates there is zero or one of the preceding element. Usually '?', the question mark is used for this quantifier.
     * For example, colou?r matches both "color" and "colour".
     *
     * @return the quantifier that indicates there is zero or one of the preceding element.
     */
    char getZeroOrOneQuantifier();

    /**
     * Gets the quantifier that indicates there is zero or more of the preceding element. Usually '*', the asterisk is used for this quantifier.
     * For example, ab*c matches "ac", "abc", "abbc", "abbbc", and so on.
     *
     * @return the quantifier that indicates there is zero or more of the preceding element.
     */
    char getZeroOrMoreQuantifier();

    /**
     * Gets the quantifier that indicates there is one or more of the preceding element. Usually '+', the plus sign is used for this quantifier.
     * For example, ab+c matches "abc", "abbc", "abbbc", and so on, but not "ac".
     *
     * @return the quantifier that indicates there is one or more of the preceding element.
     */
    char getOneOrMoreQuantifier();

    /**
     * Converts a string with wildcards to a regular express that is compatible with {@link java.util.regex.Pattern}.
     * If the string has no wildcard, the same string will be returned.
     *
     * @param s a string with wildcards.
     * @return a regular express that is compatible with {@link java.util.regex.Pattern}.
     */
    String convert(String s);
}
