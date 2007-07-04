/*
 * @(#)PageEvent.java
 *
 * Copyright 2002 - 2003 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.dialog;

import java.awt.*;
import java.util.EventObject;

/**
 * An <code>EventObject</code> that adds support for
 * <code>AbstractPage</code> objects as the event source.
 */
public class PageEvent extends EventObject {

    /**
     * The first number in the range of IDs used for <code>AbstractPage</code> events.
     */
    public static final int PAGE_EVENT_FIRST = AWTEvent.RESERVED_ID_MAX + 5100;

    /**
     * The last number in the range of IDs used for <code>AbstractPage</code> events.
     */
    public static final int PAGE_EVENT_LAST = PAGE_EVENT_FIRST + 4;

    /**
     * This event is delivered when the the data becomes invalid.
     */
    public static final int PAGE_OPENED = 0 + PAGE_EVENT_FIRST;

    /**
     * This event is delivered when the page is about to close.
     */
    public static final int PAGE_CLOSING = 1 + PAGE_EVENT_FIRST;

    /**
     * This event is delivered when the page is closed.
     */
    public static final int PAGE_CLOSED = 2 + PAGE_EVENT_FIRST;

    private int _id;

    /**
     * Constructs an <code>AbstractPageEvent</code> object.
     *
     * @param source the <code>AbstractPage</code> object that originated the event
     * @param id     an integer indicating the type of event
     */
    public PageEvent(Object source, int id) {
        super(source);
        _id = id;
    }


    /**
     * Returns the event type.
     *
     * @return event id.
     */
    public int getID() {
        return _id;
    }

    /**
     * Sets event type.
     *
     * @param id
     */
    public void setID(int id) {
        _id = id;
    }

    /**
     * Returns a parameter string identifying this event.
     * This method is useful for event logging and for debugging.
     *
     * @return a string identifying the event and its attributes
     */
    public String paramString() {
        String typeStr;
        switch (getID()) {
            case PAGE_OPENED:
                typeStr = "PAGE_OPENED";
                break;
            case PAGE_CLOSING:
                typeStr = "PAGE_CLOSING";
                break;
            case PAGE_CLOSED:
                typeStr = "PAGE_CLOSED";
                break;
            default:
                typeStr = "PAGE_EVENT_UNKNOWN";
        }
        return typeStr;
    }

    @Override
    public String toString() {
        return "PageEvent{" +
                "id=" + paramString() +
                "}";
    }
}
