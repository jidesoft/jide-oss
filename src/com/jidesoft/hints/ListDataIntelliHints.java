/*
 * @(#)ListAutoCompleter.java 7/2/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.hints;

import javax.swing.text.JTextComponent;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>ListDataIntelliHints</code> is a concrete implementation of {@link com.jidesoft.hints.IntelliHints}. It
 * provides hints from a known list of data. It is similar to auto complete text field except the list will be filtered
 * depending on what user types in so far.
 */
public class ListDataIntelliHints extends AbstractListIntelliHints {

    private boolean _caseSensitive = false;
    private List<?> _completionList;

    public ListDataIntelliHints(JTextComponent comp, List<?> completionList) {
        super(comp);
        setCompletionList(completionList);
    }

    public ListDataIntelliHints(JTextComponent comp, String[] completionList) {
        super(comp);
        setCompletionList(completionList);
    }

    /**
     * Gets the list of hints.
     *
     * @return the list of hints.
     */
    public List<?> getCompletionList() {
        return _completionList;
    }

    /**
     * Sets a new list of hints.
     *
     * @param completionList
     */
    public void setCompletionList(List<?> completionList) {
        _completionList = completionList;
    }

    /**
     * Sets a new list of hints.
     *
     * @param completionList
     */
    public void setCompletionList(String[] completionList) {
        final String[] list = completionList;
        _completionList = new AbstractList() {
            @Override
            public Object get(int index) {
                return list[index];
            }

            @Override
            public int size() {
                return list.length;
            }
        };
    }

    public boolean updateHints(Object context) {
        if (context == null) {
            return false;
        }
        String s = context.toString();
        int substringLen = s.length();
        List<String> possibleStrings = new ArrayList<String>();
        for (Object o : getCompletionList()) {
            String listEntry = (String) o;
            if (substringLen > listEntry.length())
                continue;

            if (!isCaseSensitive()) {
                if (s.equalsIgnoreCase(listEntry.substring(0, substringLen)))
                    possibleStrings.add(listEntry);
            }
            else if (listEntry.startsWith(s))
                possibleStrings.add(listEntry);
        }

        Object[] objects = possibleStrings.toArray();
        setListData(objects);
        return objects.length > 0;
    }

    /**
     * Checks if it used case sensitive search. By default it's false.
     *
     * @return if it's case sensitive.
     */
    public boolean isCaseSensitive() {
        return _caseSensitive;
    }

    /**
     * Sets the case sensitive flag. By default, it's false meaning it's a case insensitive search.
     *
     * @param caseSensitive
     */
    public void setCaseSensitive(boolean caseSensitive) {
        _caseSensitive = caseSensitive;
    }

}