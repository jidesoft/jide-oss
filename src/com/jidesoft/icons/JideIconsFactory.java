/*
 * @(#)JIdeIconsFactory.java
 *
 * Copyright 2002-2003 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.icons;

import javax.swing.*;

/**
 * A helper class to contain icons for demo of JIDE products.
 * Those icons are copyrighted by JIDE Software, Inc.
 */
public class JideIconsFactory {

    public static class FileType {
        public final static String TEXT = "jide/file_text.gif";
        public final static String JAVA = "jide/file_java.gif";
        public final static String HTML = "jide/file_html.gif";
    }

    public static class View {
        public final static String HTML = "jide/view_html.gif";
        public final static String DESIGN = "jide/view_design.gif";
    }

    public static class DockableFrame {
        public final static String BLANK = "jide/dockableframe_blank.gif";
        public final static String FRAME1 = "jide/dockableframe_1.gif";
        public final static String FRAME2 = "jide/dockableframe_2.gif";
        public final static String FRAME3 = "jide/dockableframe_3.gif";
        public final static String FRAME4 = "jide/dockableframe_4.gif";
        public final static String FRAME5 = "jide/dockableframe_5.gif";
        public final static String FRAME6 = "jide/dockableframe_6.gif";
        public final static String FRAME7 = "jide/dockableframe_7.gif";
        public final static String FRAME8 = "jide/dockableframe_8.gif";
        public final static String FRAME9 = "jide/dockableframe_9.gif";
        public final static String FRAME10 = "jide/dockableframe_10.gif";
        public final static String FRAME11 = "jide/dockableframe_11.gif";
        public final static String FRAME12 = "jide/dockableframe_12.gif";
        public final static String FRAME13 = "jide/dockableframe_13.gif";
        public final static String FRAME14 = "jide/dockableframe_14.gif";
        public final static String FRAME15 = "jide/dockableframe_15.gif";
        public final static String FRAME16 = "jide/dockableframe_16.gif";
        public final static String FRAME17 = "jide/dockableframe_17.gif";
        public final static String FRAME18 = "jide/dockableframe_18.gif";
        public final static String FRAME19 = "jide/dockableframe_19.gif";
        public final static String FRAME20 = "jide/dockableframe_20.gif";
    }

    public static class Cursor {
        public final static String HSPLIT = "jide/cursor_h_split.gif";
        public final static String VSPLIT = "jide/cursor_v_split.gif";

        public final static String NORTH = "jide/cursor_north.gif";
        public final static String SOUTH = "jide/cursor_south.gif";
        public final static String EAST = "jide/cursor_east.gif";
        public final static String WEST = "jide/cursor_west.gif";
        public final static String TAB = "jide/cursor_tab.gif";
        public final static String FLOAT = "jide/cursor_float.gif";
        public final static String VERTICAL = "jide/cursor_vertical.gif";
        public final static String HORIZONTAL = "jide/cursor_horizontal.gif";

        public final static String DROP = "jide/cursor_drag.gif";
        public final static String NODROP = "jide/cursor_drag_stop.gif";
        public final static String DELETE = "jide/cursor_delete.gif";

        public final static String DROP_TEXT = "jide/cursor_drag_text.gif";
        public final static String NODROP_TEXT = "jide/cursor_drag_text_stop.gif";
    }

    public static class WindowMenu {
        public final static String NEW_HORIZONTAL_TAB = "jide/windows_new_horizontal_tab_group.gif";
        public final static String NEW_VERTICAL_TAB = "jide/windows_new_vertical_tab_group.gif";
    }

    public static class Arrow {
        public final static String DOWN = "jide/direction_down.gif";
        public final static String UP = "jide/direction_up.gif";
        public final static String LEFT = "jide/direction_left.gif";
        public final static String RIGHT = "jide/direction_right.gif";
        public final static String DOT = "jide/direction_dot.gif";
    }


    public final static String TAIL = "jide/tail.gif";

    public final static String MENU_CHECKBOX_VSNET = "jide/menu_checkbox_vsnet.gif";

    public final static String MENU_CHECKBOX_ECLIPSE = "jide/menu_checkbox_eclipse.gif";

    public final static String MENU_RADIOBUTTON_VSNET = "jide/menu_radiobutton_vnset.gif";

    public final static String MENU_RADIOBUTTON_ECLIPSE = "jide/menu_radiobutton_eclipse.gif";

    public final static String JIDE32 = "jide/jide32.png";
    public final static String JIDE50 = "jide/jide50.png";

    public final static String JIDELOGO = "jide/jide_logo.png";


    public final static String JIDELOGO_SMALL = "jide/jide_logo_small.png";
    public final static String JIDELOGO_SMALL2 = "jide/jide_logo_small_2.png";

    public static ImageIcon getImageIcon(String name) {
        if (name != null)
            return IconsFactory.getImageIcon(JideIconsFactory.class, name);
        else
            return null;
    }

    public static void main(String[] argv) {
        IconsFactory.generateHTML(JideIconsFactory.class);
    }


}
