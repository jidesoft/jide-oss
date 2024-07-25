/*
 * @(#)FirefoxSearchBarIconsFactory.java 10/12/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.icons.IconsFactory;

import javax.swing.*;

/**
 * A helper class to contain icons for SearchableBar.
 */
public class SearchableBarIconsFactory {

    public static class Buttons {
        public static final String CLOSE = "icons/search_close.png";
        public static final String CLOSE_ROLLOVER = "icons/search_close_rollover.png";
        public static final String HIGHLIGHTS = "icons/search_highlights.png";
        public static final String HIGHLIGHTS_SELECTED = "icons/search_highlights_selected.png";
        public static final String HIGHLIGHTS_DISABLED = "icons/search_highlights_disabled.png";
        public static final String HIGHLIGHTS_ROLLOVER = "icons/search_highlights_rollover.png";
        public static final String HIGHLIGHTS_ROLLOVER_SELECTED = "icons/search_highlights_selected_rollover.png";
        public static final String NEXT = "icons/search_next.png";
        public static final String NEXT_ROLLOVER = "icons/search_next_rollover.png";
        public static final String NEXT_DISABLED = "icons/search_next_disabled.png";
        public static final String PREVIOUS = "icons/search_previous.png";
        public static final String PREVIOUS_ROLLOVER = "icons/search_previous_rollover.png";
        public static final String PREVIOUS_DISABLED = "icons/search_previous_disabled.png";
        public static final String ERROR = "icons/search_error.png";
        public static final String REPEAT = "icons/search_repeat.png";
    }

    public static ImageIcon getImageIcon(String name) {
        if (name != null)
            return IconsFactory.getImageIcon(SearchableBarIconsFactory.class, name);
        else
            return null;
    }

    public static Icon getScaledIcon(String name) {
        if (name != null)
            return IconsFactory.getScaledIcon(SearchableBarIconsFactory.class, name);
        else
            return null;
    }

    public static void main(String[] argv) {
        IconsFactory.generateHTML(SearchableBarIconsFactory.class);
    }


}
