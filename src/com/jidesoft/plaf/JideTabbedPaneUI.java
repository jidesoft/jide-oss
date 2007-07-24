/*
 * @(#)DockableFrameUI.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf;

import com.jidesoft.swing.JideTabbedPane;

import javax.swing.plaf.TabbedPaneUI;
import java.awt.*;

/**
 * ComponentUI for JideTabbedPane.
 */
public abstract class JideTabbedPaneUI extends TabbedPaneUI {
    /**
     * Gets the tab panel for the JideTabbedPane. The tab panel contains all the tabs and the tabbed pane buttons (close, scroll left/right, list buttons).
     * Sometimes you have to use this tab panel. For example, if you want to add a mouse listener to get double click event on tabs, you must use this tab panel to add
     * mouse listener. In addition, as the tab panel is part of the TabbedPaneUI which is recreated when updateUI is called (which usually happens after switching L&F), you should
     * override updateUI method in JideTabbedPane to add mouse listener so that it will get added again after updateUI.
     *
     * @return the tab panel.
     */
    abstract public Component getTabPanel();

    /**
     * Edits the tab at the index.
     *
     * @param tabIndex the tab index.
     * @return true if editing started. Otherwise false if the tab is already in editing mode when this method is called.
     */
    abstract public boolean editTabAt(int tabIndex);

    /**
     * Checks if the tab is being edited.
     *
     * @return true or false.
     */
    abstract public boolean isTabEditing();

    /**
     * Stops the editing and commits the change.
     */
    abstract public void stopTabEditing();

    /**
     * Cancels the editing and discards the change.
     */
    abstract public void cancelTabEditing();

    /**
     * Gets the tab index that is editing, if any. -1 if no tab is being edited.
     *
     * @return the tab index or -1.
     */
    abstract public int getEditingTabIndex();

    /**
     * Scroll the selected tab visible in case the tab is outside of the viewport. This method is called by {@link JideTabbedPane#scrollSelectedTabToVisible(boolean)} method.
     *
     * @param scrollLeft true to scroll the first tab visible first then scroll left to make
     *                   the selected tab visible. This will get a more consistent result.
     *                   If false, it will simple scroll the selected tab visible. Sometimes the
     *                   tab will appear as the first visible tab or the last visible tab depending on
     *                   the previous viewport position.
     */
    abstract public void ensureActiveTabIsVisible(boolean scrollLeft);

}
