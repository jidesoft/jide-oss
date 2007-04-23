/*
 * @(#)ButtonStyle.java 7/1/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

/**
 * The definitions of various button style. This is used by <code>JideButton</code>
 * and <code>JideSplitButton</code>.
 */
public interface ButtonStyle {
    public final static String BUTTON_STYLE_PROPERTY = "buttonStyle";

    final static int TOOLBAR_STYLE = 0;
    final static int TOOLBOX_STYLE = 1;
    final static int FLAT_STYLE = 2;
    final static int HYPERLINK_STYLE = 3;

    /**
     * Gets the button style.
     *
     * @return the button style.
     */
    int getButtonStyle();

    /**
     * Sets the button style.
     *
     * @param buttonStyle the button style.
     */
    void setButtonStyle(int buttonStyle);
}
