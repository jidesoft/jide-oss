/*
 * @(#)ResizableSupport.java 3/4/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.border.Border;

/**
 * An interface to indicate a component support Resizable.
 */
public interface ResizableSupport {
    /**
     * Gets the border of the component. Resizing function depends on a non-empty border.
     *
     * @return the border.
     */
    Border getBorder();

    /**
     * Sets the border of the component.
     *
     * @param border the border
     */
    void setBorder(Border border);

    /**
     * Gets the underlying Resizable. Any resizable component should have a Resizable.
     *
     * @return the Resizable.
     */
    Resizable getResizable();
}
