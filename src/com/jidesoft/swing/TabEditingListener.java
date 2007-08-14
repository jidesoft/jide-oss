package com.jidesoft.swing;

import java.util.EventListener;

/**
 * Defines an object which listens for TabEditingEvent.
 */
public interface TabEditingListener extends EventListener {
    /**
     * This tells the listeners the tab editing is started
     */
    public void editingStarted(TabEditingEvent e);

    /**
     * This tells the listeners the tab editing is stopped
     */
    public void editingStopped(TabEditingEvent e);

    /**
     * This tells the listeners the tab editing is canceled
     */
    public void editingCanceled(TabEditingEvent e);
}
