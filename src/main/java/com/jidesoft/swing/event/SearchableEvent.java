/*
 * @(#)SearchableEvent.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing.event;

import com.jidesoft.swing.Searchable;

import java.awt.*;

/**
 * An <code>AWTEvent</code> that adds support for
 * <code>SearchableEvent</code> objects as the event source.
 *
 * @see com.jidesoft.swing.Searchable
 * @see SearchableListener
 */
public class SearchableEvent extends AWTEvent {

    private String _searchingText;
    private String _oldSearchingText;
    private String _matchingText;
    private Object _matchingObject;

    /**
     * Creates a <code>SearchableEvent</code>.
     *
     * @param source
     * @param id
     */
    public SearchableEvent(Searchable source, int id) {
        super(source, id);
    }

    /**
     * Creates a <code>SearchableEvent</code>. The searching text is the text that is being searched for.
     *
     * @param source
     * @param id
     * @param searchingText
     */
    public SearchableEvent(Object source, int id, String searchingText) {
        super(source, id);
        _searchingText = searchingText;
    }

    public SearchableEvent(Object source, int id, String searchingText, String oldSearchingText) {
        super(source, id);
        _searchingText = searchingText;
        _oldSearchingText = oldSearchingText;
    }

    public SearchableEvent(Object source, int id, String searchingText, Object matchingObject, String matchingText) {
        super(source, id);
        _searchingText = searchingText;
        _matchingObject = matchingObject;
        _matchingText = matchingText;
    }

    /**
     * The first number in the range of IDs used for <code>SearchableEvent</code>.
     */
    public static final int SEARCHABLE_FIRST = AWTEvent.RESERVED_ID_MAX + 1000;

    /**
     * The last number in the range of IDs used for <code>SearchableEvent</code>.
     */
    public static final int SEARCHABLE_LAST = SEARCHABLE_FIRST + 6;

    /**
     * To indicate the searching process started. It is fired when the search popup shows up.
     */
    public static final int SEARCHABLE_START = SEARCHABLE_FIRST;

    /**
     * To indicate the searching process stopped. It is fired when the search popup is gone.
     */
    public static final int SEARCHABLE_END = SEARCHABLE_FIRST + 1;

    /**
     * To indicate the searching process finds a matching element. In this case, <code>getSearchingText()</code> will return
     * the text that is being searched for. <code>getMatchingObject()</code> will return the element that matches the searching text.
     * <code>getMatchingText()</code> is the text converting from the the matching object.
     */
    public static final int SEARCHABLE_MATCH = SEARCHABLE_FIRST + 3;

    /**
     * To indicate the searching process doesn't find a matching element. In this case, <code>getSearchingText()</code> will return
     * the text that is being searched for. <code>getMatchingObject()</code> and <code>getMatchingText()</code> will be null.
     */
    public static final int SEARCHABLE_NOMATCH = SEARCHABLE_FIRST + 4;

    /**
     * To indicate the searching text changes. In this case, <code>getSearchingText()</code> will return
     * the text that is being searched for. <code>getOldSearchingText()</code> will return the previous searching text.
     */
    public static final int SEARCHABLE_CHANGE = SEARCHABLE_FIRST + 5;

    /**
     * To indicate the search component model is changed. The model could be ListModel in the case of JList, TableModel in the case
     * of JTable, etc.
     */
    public static final int SEARCHABLE_MODEL_CHANGE = SEARCHABLE_FIRST + 6;

    /**
     * Returns a parameter string identifying this event.
     * This method is useful for event logging and for debugging.
     *
     * @return a string identifying the event and its attributes
     */
    @Override
    public String paramString() {
        String typeStr;
        switch (id) {
            case SEARCHABLE_START:
                typeStr = "SEARCHABLE_START: searchingText = \"" + _searchingText + "\"";
                break;
            case SEARCHABLE_END:
                typeStr = "SEARCHABLE_END";
                break;
            case SEARCHABLE_MATCH:
                typeStr = "SEARCHABLE_MATCH: searchingText = \"" + _searchingText + "\" matchingText = \"" + _matchingText + "\"";
                break;
            case SEARCHABLE_NOMATCH:
                typeStr = "SEARCHABLE_NOMATCH: searchingText = \"" + _searchingText + "\"";
                break;
            case SEARCHABLE_CHANGE:
                typeStr = "SEARCHABLE_CHANGE: searchingText = \"" + _searchingText + "\" oldSearchingText = \"" + _oldSearchingText + "\"";
                break;
            case SEARCHABLE_MODEL_CHANGE:
                typeStr = "SEARCHABLE_MODEL";
                break;
            default:
                typeStr = "SEARCHABLE_UNKNOWN";
        }
        return typeStr;
    }


    /**
     * Returns the originator of the event.
     *
     * @return the <code>Searchable</code> object that originated the event
     */

    public Searchable getSearchable() {
        return (source instanceof Searchable) ? (Searchable) source : null;
    }

    /**
     * Gets the text that is being searched for. The returned value is valid for events SEARCHABLE_START,
     * SEARCHABLE_MATCH, SEARCHABLE_NOMATCH, and SEARCHABLE_CHANGE.
     *
     * @return the text that is being searched for.
     */
    public String getSearchingText() {
        return _searchingText;
    }

    /**
     * Gets the text that was searched for.
     * The returned value is only valid for event SEARCHABLE_CHANGE.
     *
     * @return the text that was searched for.
     */
    public String getOldSearchingText() {
        return _oldSearchingText;
    }

    /**
     * Gets the text that is converted from the object matching the searching text.
     * The returned value is only valid for events SEARCHABLE_MATCH.
     *
     * @return the text that is converted from the object matching the searching text.
     */
    public String getMatchingText() {
        return _matchingText;
    }

    /**
     * Gets the object that matches the searching text.
     * The returned value is only valid for events SEARCHABLE_MATCH.
     *
     * @return Gets the object that matches the searching text.
     */
    public Object getMatchingObject() {
        return _matchingObject;
    }
}
