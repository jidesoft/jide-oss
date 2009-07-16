/*
 * @(#)ButtonEvent.java
 *
 * Copyright 2002 - 2003 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.dialog;

import java.awt.*;
import java.util.EventObject;

/**
 * An EventObject used to change the state of any button.
 */
public class ButtonEvent extends EventObject {

    /**
     * The first number in the range of IDs used for <code>ButtonEvent</code>.
     */
    public static final int BUTTON_EVENT_FIRST = AWTEvent.RESERVED_ID_MAX + 1300;

    /**
     * The last number in the range of IDs used for <code>DockableFrame</code> events.
     */
    public static final int BUTTON_EVENT_LAST = BUTTON_EVENT_FIRST + 9;

    /**
     * This event is fired when you want to show the button.
     */
    public static final int SHOW_BUTTON = BUTTON_EVENT_FIRST;

    /**
     * This event is fired when you want to hide the button.
     */
    public static final int HIDE_BUTTON = 1 + BUTTON_EVENT_FIRST;

    /**
     * This event is fired when you want to enable the button.
     */
    public static final int ENABLE_BUTTON = 2 + BUTTON_EVENT_FIRST;

    /**
     * This event is fired when you want to disable the button.
     */
    public static final int DISABLE_BUTTON = 3 + BUTTON_EVENT_FIRST;

    /**
     * This event is fired when you want to change the text of the button.
     */
    public static final int CHANGE_BUTTON_TEXT = 4 + BUTTON_EVENT_FIRST;

    /**
     * This event is fired when you want to change the mnemonic of the button.
     */
    public static final int CHANGE_BUTTON_MNEMONIC = 5 + BUTTON_EVENT_FIRST;

    /**
     * This event is fired when you want to change the tooltip of the button.
     */
    public static final int CHANGE_BUTTON_TOOLTIP = 6 + BUTTON_EVENT_FIRST;

    /**
     * This event is fired when you want to set focus to the button.
     */
    public static final int CHANGE_BUTTON_FOCUS = 7 + BUTTON_EVENT_FIRST;

    /**
     * This event is fired when you want to set the button to the default button in a root pane.
     */
    public static final int SET_DEFAULT_BUTTON = 8 + BUTTON_EVENT_FIRST;

    /**
     * This event is fired when you want to clear the default button from a root pane.
     */
    public static final int CLEAR_DEFAULT_BUTTON = 9 + BUTTON_EVENT_FIRST;

    private int _id;

    private String _buttonName;

    private String _userObject;

    /**
     * Creates a ButtonEvent with source, id and the button name.
     *
     * @param source
     * @param id
     * @param buttonName
     */
    public ButtonEvent(Object source, int id, String buttonName) {
        super(source);
        _id = id;
        _buttonName = buttonName;
        checkParam();
    }

    /**
     * Creates a ButtonEvent with source, id, the button name and a user object. User object is required for
     * CHANGE_BUTTON_TEXT and CHANGE_BUTTON_TOOLTIP event.
     *
     * @param source
     * @param id
     * @param buttonName
     * @param userObject
     */
    public ButtonEvent(Object source, int id, String buttonName, String userObject) {
        super(source);
        _id = id;
        _buttonName = buttonName;
        _userObject = userObject;
        checkParam();
    }

    private void checkParam() {
        if (getID() < BUTTON_EVENT_FIRST && getID() > BUTTON_EVENT_LAST) {
            throw new IllegalArgumentException(getID() + " is an invalid event id for ButtonEvent");
        }
        if (_buttonName == null || _buttonName.trim().length() == 0) {
            throw new IllegalArgumentException("buttonName cannot be null or empty");
        }
        if ((_userObject == null || _userObject.trim().length() == 0)
                && (getID() == CHANGE_BUTTON_TEXT
                || getID() == CHANGE_BUTTON_MNEMONIC
                || getID() == CHANGE_BUTTON_TOOLTIP)) {
            throw new IllegalArgumentException("userObject cannot be null or empty for " + paramString());
        }
    }

    /**
     * Returns the event id.
     *
     * @return event id.
     */
    public int getID() {
        return _id;
    }

    /**
     * Sets the event id.
     *
     * @param id
     */
    public void setID(int id) {
        _id = id;
    }

    /**
     * Gets the button name of this event object.
     *
     * @return the button name.
     */
    public String getButtonName() {
        return _buttonName;
    }

    /**
     * Sets the button name.
     *
     * @param buttonName
     */
    public void setButtonName(String buttonName) {
        _buttonName = buttonName;
    }

    /**
     * Gets the user object of this event object.
     *
     * @return the user object.
     */
    public String getUserObject() {
        return _userObject;
    }

    /**
     * Sets the user object.
     *
     * @param userObject
     */
    public void setUserObject(String userObject) {
        _userObject = userObject;
    }

    /**
     * Returns a parameter string identifying this event. This method is useful for event logging and for debugging.
     *
     * @return a string identifying the event and its attributes
     */
    public String paramString() {
        String typeStr;
        switch (getID()) {
            case SHOW_BUTTON:
                typeStr = "SHOW_BUTTON";
                break;
            case HIDE_BUTTON:
                typeStr = "HIDE_BUTTON";
                break;
            case ENABLE_BUTTON:
                typeStr = "ENABLE_BUTTON";
                break;
            case DISABLE_BUTTON:
                typeStr = "DISABLE_BUTTON";
                break;
            case CHANGE_BUTTON_TEXT:
                typeStr = "CHANGE_BUTTON_TEXT";
                break;
            case CHANGE_BUTTON_MNEMONIC:
                typeStr = "CHANGE_BUTTON_MNEMONIC";
                break;
            case CHANGE_BUTTON_TOOLTIP:
                typeStr = "CHANGE_BUTTON_TOOLTIP";
                break;
            case CHANGE_BUTTON_FOCUS:
                typeStr = "CHANGE_BUTTON_FOCUS";
                break;
            case SET_DEFAULT_BUTTON:
                typeStr = "SET_DEFAULT_BUTTON";
                break;
            default:
                typeStr = "BUTTON_EVENT_UNKNOWN";
        }
        return typeStr;
    }
}
