package com.jidesoft.utils;

/**
 * Default implementation of {@link WildcardSupport}. It uses the following three chars as the wildcards.
 * <ul>
 * <li> '?' The question mark indicates there is zero or one of the preceding element. For example, colou?r matches both "color" and "colour".
 * <li>'*' The asterisk indicates there are zero or more of the preceding element. For example, ab*c matches "ac", "abc", "abbc", "abbbc", and so on.
 * <li>'+' The plus sign indicates that there is one or more of the preceding element. For example, ab+c matches "abc", "abbc", "abbbc", and so on, but not "ac".
 * </ul>
 */
public class DefaultWildcardSupport extends AbstractWildcardSupport {

    public char getZeroOrOneQuantifier() {
        return '?';
    }

    public char getZeroOrMoreQuantifier() {
        return '*';
    }

    public char getOneOrMoreQuantifier() {
        return '+';
    }
}
