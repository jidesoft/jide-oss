/*
 * @(#)SidePaneListener.java
 *
 * Copyright 2004 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing.event;

import java.util.EventListener;

/**
 * The listener interface for receiving side pane events.
 */
public interface SidePaneListener extends EventListener {

    /**
     * Invoked when a tab is selected in the <code>SidePaneGroup</code>.
     *
     * @param e SidePaneEvent
     */
    public void sidePaneTabSelected(SidePaneEvent e);

    /**
     * Invoked when a tab is deselected in the <code>SidePaneGroup</code>.
     *
     * @param e SidePaneEvent
     */
    public void sidePaneTabDeselected(SidePaneEvent e);

}
