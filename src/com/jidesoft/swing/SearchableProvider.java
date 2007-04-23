/*
 * @(#)SearchableProvider.java 10/11/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import java.awt.event.KeyEvent;

/**
 * <code>SearchableProvider</code> is an interface that works with {@link Searchable} to
 * provide different way to supply the searching text.
 */
public interface SearchableProvider {
    /**
     * Gets the searching text.
     *
     * @return the searching text.
     */
    String getSearchingText();

    /**
     * Returns true if the SearchableProvider doesn't accept keyboard input directly. In this case,
     * the Searchable component (such as JTextComponent, JTable, JList or JComboBox) will
     *
     * @return true or false.
     */
    boolean isPassive();

    void processKeyEvent(KeyEvent e);
}
