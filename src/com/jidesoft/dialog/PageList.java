/*
 * @(#)PageList.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.dialog;

import com.jidesoft.swing.JideSwingUtilities;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A list of AbstractDialogPage or its subclasses. It is used by MultiplePageDialog and Wizard.
 */
public class PageList extends DefaultComboBoxModel {
    /**
     * If you know the full title of any page, use this method to get the actual page from the list.
     *
     * @param title the full title.
     * @return the page with the title.
     */
    public AbstractDialogPage getPageByFullTitle(String title) {
        for (int i = 0; i < getSize(); i++) {
            AbstractDialogPage page = (AbstractDialogPage) getElementAt(i);
            if (page.getFullTitle().equals(title)) {
                return page;
            }
        }
        return null;
    }

    /**
     * Gets the page index if you know the full title of the page.
     *
     * @param title the full title.
     * @return the page index.
     */
    public int getPageIndexByFullTitle(String title) {
        for (int i = 0; i < getSize(); i++) {
            AbstractDialogPage page = (AbstractDialogPage) getElementAt(i);
            if (page.getFullTitle().equals(title)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the page count in the list.
     *
     * @return the page count in the list.
     */
    public int getPageCount() {
        return getSize();
    }

    /**
     * Appends a page to the end of the list.
     *
     * @param page the page to be appended.
     */
    public void append(AbstractDialogPage page) {
        addElement(page);
    }

    /**
     * Removes a page from the page list.
     *
     * @param page page to be removed.
     */
    public void remove(AbstractDialogPage page) {
        removeElement(page);
    }

    /**
     * Clear the page list.
     */
    public void clear() {
        removeAllElements();
    }

    /**
     * Inserts a page after the page with the specified full title. If we cannot find the page with the specified title,
     * the page will be added to the end as append(page).
     *
     * @param page  page to be inserted.
     * @param title the title of the page after when the new page will be inserted.
     */
    public void insertAfter(AbstractDialogPage page, String title) {
        int index = getPageIndexByFullTitle(title);
        if (index == -1 || index == getPageCount() - 1) {
            append(page);
        }
        else {
            insertElementAt(page, index + 1);
        }
    }

    /**
     * Gets the page at position.
     *
     * @param i the index
     * @return the page.
     */
    public AbstractDialogPage getPage(int i) {
        return (AbstractDialogPage) getElementAt(i);
    }

    /**
     * Gets the all page titles as vector.
     *
     * @return the vector which has all the page titles.
     */
    public List<String> getPageTitlesAsList() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < getPageCount(); i++) {
            AbstractDialogPage page = getPage(i);
            list.add(page.getTitle());
        }
        return list;
    }

    /**
     * Gets the current selected page.
     *
     * @return the current selected page.
     */
    public AbstractDialogPage getCurrentPage() {
        return ((AbstractDialogPage) getSelectedItem());
    }

    /**
     * Sets the current selected page.
     *
     * @param page the dialog page.
     */
    public void setCurrentPage(AbstractDialogPage page) {
        setCurrentPage(page, null);
    }

    protected boolean setCurrentPage(AbstractDialogPage page, Object source) {
        AbstractDialogPage oldPage = getCurrentPage();
        if (oldPage != null && !oldPage.equals(page)) {
            oldPage.setAllowClosing(true);
            oldPage.firePageEvent(source, PageEvent.PAGE_CLOSING);
            if (!oldPage.allowClosing()) {
                return false;
            }
            oldPage.firePageEvent(source, PageEvent.PAGE_CLOSED);
        }

        if (!JideSwingUtilities.equals(oldPage, page)) {
            setSelectedItem(page);
        }
        else {
            AbstractDialogPage newPage = getCurrentPage();
            if (newPage != null) {
                newPage.firePageEvent(source, PageEvent.PAGE_OPENED);
            }
        }

        return true;
    }
}
