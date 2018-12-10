/*
 * @(#)JideSidePaneGroup.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.swing.event.SidePaneEvent;
import com.jidesoft.swing.event.SidePaneListener;

import javax.swing.event.EventListenerList;
import java.awt.*;
import java.util.ArrayList;

/**
 * A data structure used by {@link SidePane} to represent a group of {@link SidePaneItem}.
 * <p/>
 * Each group usually has one <code>SidePaneItem</code> selected.
 */
public class SidePaneGroup extends ArrayList<SidePaneItem> {

    /**
     * the selected item.
     */
    private SidePaneItem _selectedItem = null;

    /**
     * A list of event listeners for this component.
     */
    protected EventListenerList listenerList;

    /**
     * Default constructor.
     */
    public SidePaneGroup() {
    }

    /**
     * Gets the selected item. If there is no one is selected, return the first one. If there is no
     * items at all, return null.
     *
     * @return the selected item
     */
    public SidePaneItem getSelectedItem() {
        if (_selectedItem != null)
            return _selectedItem;
        else if (size() > 0)
            return get(0);
        else
            return null;
    }

    /**
     * Sets the selected item.
     *
     * @param selectedItem the item to be selected
     */
    public void setSelectedItem(SidePaneItem selectedItem) {
        boolean changedSelection = (selectedItem != _selectedItem);
        if (changedSelection && (_selectedItem != null)) {
            fireSidePaneEvent(_selectedItem, SidePaneEvent.SIDE_PANE_TAB_DESELECTED);
        }

        if (_selectedItem != null) {
            _selectedItem.setSelected(false);
        }
        _selectedItem = selectedItem;
        if (_selectedItem != null) {
            _selectedItem.setSelected(true);
        }

        if (changedSelection && (selectedItem != null)) {
            fireSidePaneEvent(selectedItem, SidePaneEvent.SIDE_PANE_TAB_SELECTED);
        }
    }

    /**
     * Gets the selected index.
     *
     * @return the index of the selected item
     */
    public int getSelectedIndex() {
        return indexOf(_selectedItem);
    }

    /**
     * Sets the selected index.
     *
     * @param index the index of the item to be selected
     */
    public void setSelectedIndex(int index) {
        setSelectedItem(get(index));
    }

    /**
     * Get longest title in this group. This is just a handy method which shouldn't really need to
     * be here.
     *
     * @return the longest title
     */
    public String getLongestTitle() {
        int length = 0;
        String title = "";
        for (int i = 0; i < size(); i++) {
            SidePaneItem item = get(i);
            if (item.getTitle().length() > length) {
                length = item.getTitle().length();
                title = item.getTitle();
            }
        }
        return title;
    }

    /**
     * Removes the component from this group.
     *
     * @param comp component to be removed
     *
     * @return <code>true</code> if the component is removed
     */
    public boolean removeComponent(Component comp) {
        if (comp == null)
            return false;
        for (int i = 0; i < this.size(); i++) {
            SidePaneItem item = this.get(i);
            if (item.getComponent().equals(comp)) {
                remove(item);
                if (item.equals(_selectedItem) && size() > 0) {
                    _selectedItem = get(0);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the component exists in this group.
     *
     * @param comp component to be checked
     *
     * @return <code>true</code> if the component exists
     */
    public boolean exists(Component comp) {
        if (comp == null)
            return false;
        for (int i = 0; i < this.size(); i++) {
            SidePaneItem item = this.get(i);
            if (item.getComponent().equals(comp))
                return true;
        }
        return false;
    }

    /**
     * Checks if the component exists in this group.
     *
     * @param comp component to be checked
     *
     * @return <code>true</code> if the component exists
     */
    public SidePaneItem getSidePaneItem(Component comp) {
        if (comp == null)
            return null;
        for (int i = 0; i < this.size(); i++) {
            SidePaneItem item = this.get(i);
            if (item.getComponent().equals(comp))
                return item;
        }
        return null;
    }

    private boolean initListenerList(boolean create) {
        if (listenerList == null && create) {
            listenerList = new EventListenerList();
            return true;
        }
        else {
            return listenerList != null;
        }
    }

    /**
     * Adds the specified listener to receive side pane events from this side pane group.
     *
     * @param l the side pane listener
     */
    public void addSidePaneListener(SidePaneListener l) {
        if (initListenerList(true)) {
            if (!JideSwingUtilities.isListenerRegistered(listenerList, SidePaneListener.class, l)) {
                listenerList.add(SidePaneListener.class, l);
            }
        }
    }

    /**
     * Removes the specified side pane listener so that it no longer receives side pane events from
     * this side pane group.
     *
     * @param l the dockable frame listener
     */
    public void removeSidePaneListener(SidePaneListener l) {
        if (initListenerList(false)) {
            listenerList.remove(SidePaneListener.class, l);
        }
    }

    /**
     * Returns an array of all the <code>SidePaneListener</code>s added to this
     * <code>SidePaneGroup</code> with <code>addSidePaneListener</code>.
     *
     * @return all of the <code>SidePaneListener</code>s added or an empty array if no listeners
     *         have been added
     *
     * @see #addSidePaneListener
     */
    public SidePaneListener[] getSidePaneListeners() {
        if (initListenerList(false)) {
            return listenerList.getListeners(SidePaneListener.class);
        }
        else {
            return new SidePaneListener[0];
        }
    }

    /**
     * Fires a side pane event.
     *
     * @param sidePaneItem the event source
     * @param id           the type of the event being fired; one of the following: If the event
     *                     type is not one of the above, nothing happens.
     */
    protected void fireSidePaneEvent(SidePaneItem sidePaneItem, int id) {
        if (initListenerList(false)) {
            Object[] listeners = listenerList.getListenerList();
            SidePaneEvent e = null;
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == SidePaneListener.class) {
                    if (e == null) {
                        e = new SidePaneEvent(sidePaneItem, id);
                    }
                    switch (e.getID()) {
                        case SidePaneEvent.SIDE_PANE_TAB_SELECTED:
                            ((SidePaneListener) listeners[i + 1]).sidePaneTabSelected(e);
                            break;
                        case SidePaneEvent.SIDE_PANE_TAB_DESELECTED:
                            ((SidePaneListener) listeners[i + 1]).sidePaneTabDeselected(e);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
