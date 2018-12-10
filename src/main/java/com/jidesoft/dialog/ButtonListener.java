/*
 * @(#)ButtonListener.java
 *
 * Copyright 2002 - 2003 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.dialog;


import java.util.EventListener;


/**
 * The listener interface for receiving button events.
 */
public interface ButtonListener extends EventListener {
    /**
     * Invoked when the target of the listener request button to change state.
     *
     * @param e a ButtonEvent object
     */
    void buttonEventFired(ButtonEvent e);
}

