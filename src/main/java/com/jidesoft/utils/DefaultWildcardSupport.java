package com.jidesoft.utils;

/**
 * Default implementation of {@link WildcardSupport}. It uses the following three chars as the wildcards.
 * <ul>
 * <li> '?' The question mark indicates there is exact one of missing element. For example, colo?r matches
 * "colour" but not "color" or "colouur".
 * <li>'*' The asterisk indicates there are zero or more of the missing elements. For example, ab*c matches
 * "abc", "abbc", "abdbc", and so on.
 * <li>'+' The plus sign indicates there are at least one of the missing elements. For example, ab+c matches
 * "abbc", "abdbc", but not "abc".
 * </ul>
 */
public class DefaultWildcardSupport extends AbstractWildcardSupport {
    private static final long serialVersionUID = -5528733766095113518L;

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
