/*
 * @(#)RegistrationEvent.java 11/28/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

import java.awt.*;
import java.util.EventObject;

/**
 * An <code>AWTEvent</code> that adds support for registration objects as the event source.
 */
public class RegistrationEvent extends EventObject {
    /**
     * The first number in the range of IDs used for <code>DockableFrame</code> events.
     */
    public static final int REGISTRATION_EVENT_FIRST = AWTEvent.RESERVED_ID_MAX + 1400;

    /**
     * The last number in the range of IDs used for <code>DockableFrame</code> events.
     */
    public static final int REGISTRATION_EVENT_LAST = REGISTRATION_EVENT_FIRST + 3;

    /**
     * This event is delivered when the a new object is registered.
     */
    public static final int REGISTRATION_ADDED = REGISTRATION_EVENT_FIRST;

    /**
     * This event is delivered when the registered object is removed.
     */
    public static final int REGISTRATION_REMOVED = 1 + REGISTRATION_EVENT_FIRST;

    /**
     * This event is delivered when the whole registration is cleared
     */
    public static final int REGISTRATION_CLEARED = 2 + REGISTRATION_EVENT_FIRST;

    private int _id;
    private Object _object;
    private Object _context;
    private Object _key;

    /**
     * Create a REGISTRATION_CLEARED event.
     *
     * @param source
     * @param id     must be equal to REGISTRATION_CLEARED.
     */
    public RegistrationEvent(Object source, int id) {
        super(source);
        if (id != REGISTRATION_CLEARED) {
            throw new IllegalArgumentException("This constructor is only for REGISTRATION_CLEARED event.");
        }
        _id = id;
    }

    /**
     * Constructs an <code>RegistrationEvent</code> object.
     *
     * @param source the <code>Registration</code> object that originated the event
     * @param id     an integer indicating the type of event
     */
    public RegistrationEvent(Object source, int id, Object object, Object key, Object context) {
        super(source);
        _id = id;
        _object = object;
        _context = context;
        _key = key;
    }

    public Object getKey() {
        return _key;
    }

    public Object getContext() {
        return _context;
    }

    public Object getObject() {
        return _object;
    }

    public int getId() {
        return _id;
    }

    @Override
    public String toString() {
        String action;
        switch (getId()) {
            case REGISTRATION_ADDED:
                action = "ADDED ";
                break;
            case REGISTRATION_REMOVED:
                action = "REMOVED ";
                break;
            case REGISTRATION_CLEARED:
                action = "CLEARED ";
                break;
            default:
                action = "UNKNOWN " + getId() + " ";
                break;
        }
        return action + "{key = " + getKey() + "; context = " + getContext() + "; object = " + getObject();
    }
}
