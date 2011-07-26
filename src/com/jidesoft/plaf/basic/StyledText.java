/*
 * @(#)StyledText.java 7/25/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.basic;

import com.jidesoft.swing.StyleRange;

/**
 * The class to define style ranges inside a StyledLabel.
 *
 * @since 3.1.2
 */
public class StyledText {
    StyleRange styleRange;
    String text;

    /**
     * The constructor with text only.
     *
     * @param text       the text
     */
    public StyledText(String text) {
        this.text = text;
    }

    /**
     * The constructor with both text and style range.
     *
     * @param text       the text
     * @param styleRange the style range
     */
    public StyledText(String text, StyleRange styleRange) {
        this.text = text;
        this.styleRange = styleRange;
    }
}
