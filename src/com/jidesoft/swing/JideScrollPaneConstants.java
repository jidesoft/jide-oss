/*
 * @(#)${NAME}.java
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;

/**
 * Constants used with the JideScrollPane component.
 */
public interface JideScrollPaneConstants extends ScrollPaneConstants {
    /**
     * Identifies the area along the left side of the viewport between the
     * upper right corner and the lower right corner.
     */
    public static final String ROW_FOOTER = "ROW_FOOTER";
    /**
     * Identifies the area at the bottom where the viewport is between the
     * lower left corner and the lower right corner.
     */
    public static final String COLUMN_FOOTER = "COLUMN_FOOTER";
    /**
     * Identifies the area at the top where the viewport is between the
     * column header and main viewport.
     */
    public static final String SUB_COLUMN_HEADER = "SUB_COLUMN_HEADER";

    public static final String HORIZONTAL_LEFT = "HORIZONTAL_LEFT";      //NOI18N
    public static final String HORIZONTAL_RIGHT = "HORIZONTAL_RIGHT";    //NOI18N
    public static final String HORIZONTAL_LEADING = "HORIZONTAL_LEADING";      //NOI18N
    public static final String HORIZONTAL_TRAILING = "HORIZONTAL_TRAILING";      //NOI18N
    public static final String VERTICAL_TOP = "VERTICAL_TOP";            //NOI18N
    public static final String VERTICAL_BOTTOM = "VERTICAL_BOTTOM";      //NOI18N
}
