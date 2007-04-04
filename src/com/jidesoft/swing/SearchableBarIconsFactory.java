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
        public final static String CLOSE = "icons/close.png";
        public final static String CLOSE_ROLLOVER = "icons/closeR.png";
        public final static String HIGHLIGHTS = "icons/highlights.png";
        public final static String HIGHLIGHTS_SELECTED = "icons/highlightsS.png";
        public final static String HIGHLIGHTS_DISABLED = "icons/highlightsD.png";
        public final static String HIGHLIGHTS_ROLLOVER = "icons/highlightsR.png";
        public final static String HIGHLIGHTS_ROLLOVER_SELECTED = "icons/highlightsRS.png";
        public final static String NEXT = "icons/next.png";
        public final static String NEXT_ROLLOVER = "icons/nextR.png";
        public final static String NEXT_DISABLED = "icons/nextD.png";
        public final static String PREVIOUS = "icons/previous.png";
        public final static String PREVIOUS_ROLLOVER = "icons/previousR.png";
        public final static String PREVIOUS_DISABLED = "icons/previousD.png";
        public final static String ERROR = "icons/error.png";
        public final static String REPEAT = "icons/repeat.png";
    }

    public static ImageIcon getImageIcon(String name) {
        if (name != null)
            return IconsFactory.getImageIcon(SearchableBarIconsFactory.class, name);
        else
            return null;
    }

    public static void main(String[] argv) {
        IconsFactory.generateHTML(SearchableBarIconsFactory.class);
    }


}
