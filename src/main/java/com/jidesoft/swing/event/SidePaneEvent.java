/*
 * @(#)SidePaneEvent.java
 *
 * Copyright 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing.event;

import com.jidesoft.swing.SidePaneItem;

import java.awt.*;

/**
 * An <code>AWTEvent</code> that adds support for <code>SidePaneItem</code> objects as the event source.
 *
 * @see com.jidesoft.swing.SidePaneItem
 * @see SidePaneListener
 */
public class SidePaneEvent extends AWTEvent {

    public SidePaneEvent(SidePaneItem source, int id) {
        super(source, id);
    }

    /**
     * The first number in the range of IDs used for <code>SidePaneGroup</code> events.
     */
    public static final int SIDE_PANE_FIRST = AWTEvent.RESERVED_ID_MAX + 2100;

    /**
     * The last number in the range of IDs used for <code>DockableFrame</code> events.
     */
    public static final int SIDE_PANE_LAST = SIDE_PANE_FIRST + 1;

    /**
     * This event is delivered when a tab in the <code>SidePaneGroup</code> is selected.
     */
    public static final int SIDE_PANE_TAB_SELECTED = SIDE_PANE_FIRST;

    /**
     * This event is delivered when a tab in the <code>SidePaneGroup</code> is selected.
     */
    public static final int SIDE_PANE_TAB_DESELECTED = 1 + SIDE_PANE_FIRST;

    /**
     * Returns a parameter string identifying this event. This method is useful for event logging and for debugging.
     *
     * @return a string identifying the event and its attributes
     */
    @Override
    public String paramString() {
        String typeStr;
        switch (id) {
            case SIDE_PANE_TAB_SELECTED:
                typeStr = "SIDE_PANE_TAB_SELECTED";
                break;
            case SIDE_PANE_TAB_DESELECTED:
                typeStr = "SIDE_PANE_TAB_DESELECTED";
                break;
            default:
                typeStr = "SIDE_PANE_UNKNOWN";
        }
        return typeStr;
    }


    /**
     * Returns the originator of the event.
     *
     * @return the <code>SidePaneItem</code> object that originated the event
     */

    public SidePaneItem getSidePaneItem() {
        return (source instanceof SidePaneItem) ? (SidePaneItem) source : null;
    }


}
