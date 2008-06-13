package com.jidesoft.swing;

import java.awt.*;

public class TabEditingEvent extends AWTEvent {
    /**
     * The first number in the range of IDs used for <code>TabChangeEvent</code>.
     */
    public static final int TAB_EDITING_STARTED = AWTEvent.RESERVED_ID_MAX + 1100;
    public static final int TAB_EDITING_STOPPED = TAB_EDITING_STARTED + 1;
    public static final int TAB_EDITING_CANCELLED = TAB_EDITING_STOPPED + 1;

    private int _tabIndex;
    private String _oldTitle;
    private String _newTitle;

    public TabEditingEvent(Object source, int id, int tabIndex) {
        super(source, id);
        _tabIndex = tabIndex;
    }

    public TabEditingEvent(Object source, int id, int tabIndex, String oldTitle, String newTitle) {
        super(source, id);
        _tabIndex = tabIndex;
        _oldTitle = oldTitle;
        _newTitle = newTitle;
    }

    /**
     * Gets the tab index where the tab editing happened.
     *
     * @return the tab index.
     */
    public int getTabIndex() {
        return _tabIndex;
    }

    /**
     * Gets the old the title. If the event is to indicate the tab editing is started, this will be the current title.
     * If tab editing is cancelled, it will still be the current title.
     *
     * @return the old title.
     */
    public String getOldTitle() {
        return _oldTitle;
    }

    /**
     * The new title after tab editing. If the event is to indicate the tab editing is started, this will be null. If
     * tab editing is cancelled, it will be the same as the getOldTitle.
     *
     * @return the new title.
     */
    public String getNewTitle() {
        return _newTitle;
    }
}
