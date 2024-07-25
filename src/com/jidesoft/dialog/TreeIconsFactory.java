/*
 * @(#)TreeIconsFactory.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.dialog;


import com.jidesoft.icons.IconsFactory;

import javax.swing.*;

/**
 * A helper class to contain icons for MultiplePage Dialog component.
 */
class TreeIconsFactory {

    static class CellRenderer {
        public static final String SELECTED_C16 = "icons/tree_selected_c16.png";
        public static final String SELECTED_B16 = "icons/tree_selected_b16.png";
        public static final String BLANK_16 = "icons/tree_blank_16.png";
    }

    public static ImageIcon getImageIcon(String name) {
        if (name != null)
            return IconsFactory.getImageIcon(TreeIconsFactory.class, name);
        else
            return null;
    }

    public static Icon getScaledIcon(String name) {
        if (name != null)
            return IconsFactory.getScaledIcon(TreeIconsFactory.class, name);
        else
            return null;
    }

    public static void main(String[] argv) {
        IconsFactory.generateHTML(TreeIconsFactory.class);
    }


}
