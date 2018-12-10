/*
 * @(#)PageListener.java	1.10 03/01/23
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.dialog;


import java.util.EventListener;


/**
 * Defines a listener interface which listens for PageEvent.
 */
public interface PageListener extends EventListener {
    /**
     * Invoked when the target of the listener has fired a page event.
     *
     * @param e a PageEvent object
     */
    void pageEventFired(PageEvent e);
}

