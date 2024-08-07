/*
 * @(#)JIdeIconsFactory.java
 *
 * Copyright 2002-2003 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.icons;

import javax.swing.*;

/**
 * A helper class to contain icons for demo of JIDE products. Those icons are copyrighted by JIDE Software, Inc.
 */
public class JideIconsFactory {

    public static class FileType {
        public static final String TEXT = "jide/file_type_txt.png";
        public static final String JAVA = "jide/file_type_java.png";
        public static final String HTML = "jide/file_type_html.png";
    }

    public static class View {
        public static final String HTML = "jide/view_html.png";
        public static final String DESIGN = "jide/view_design.png";
    }

    public static class DockableFrame {
        public static final String BLANK = "jide/dockableframe_blank.gif";
        public static final String FRAME1 = "jide/dockableframe_1.gif";
        public static final String FRAME2 = "jide/dockableframe_2.gif";
        public static final String FRAME3 = "jide/dockableframe_3.gif";
        public static final String FRAME4 = "jide/dockableframe_4.gif";
        public static final String FRAME5 = "jide/dockableframe_5.gif";
        public static final String FRAME6 = "jide/dockableframe_6.gif";
        public static final String FRAME7 = "jide/dockableframe_7.gif";
        public static final String FRAME8 = "jide/dockableframe_8.gif";
        public static final String FRAME9 = "jide/dockableframe_9.gif";
        public static final String FRAME10 = "jide/dockableframe_10.gif";
        public static final String FRAME11 = "jide/dockableframe_11.gif";
        public static final String FRAME12 = "jide/dockableframe_12.gif";
        public static final String FRAME13 = "jide/dockableframe_13.gif";
        public static final String FRAME14 = "jide/dockableframe_14.gif";
        public static final String FRAME15 = "jide/dockableframe_15.gif";
        public static final String FRAME16 = "jide/dockableframe_16.gif";
        public static final String FRAME17 = "jide/dockableframe_17.gif";
        public static final String FRAME18 = "jide/dockableframe_18.gif";
        public static final String FRAME19 = "jide/dockableframe_19.gif";
        public static final String FRAME20 = "jide/dockableframe_20.gif";
    }

    public static class Cursor {
        public static final String NORTH = "jide/cursor_north.png";
        public static final String SOUTH = "jide/cursor_south.png";
        public static final String EAST = "jide/cursor_east.png";
        public static final String WEST = "jide/cursor_west.png";
        public static final String TAB = "jide/cursor_tab.png";
        public static final String FLOAT = "jide/cursor_float.png";
        public static final String VERTICAL = "jide/cursor_vertical.png";
        public static final String HORIZONTAL = "jide/cursor_horizontal.png";

        public static final String DROP = "jide/cursor_drag.png";
        public static final String NODROP = "jide/cursor_drag_stop.png";
        public static final String DELETE = "jide/cursor_delete.png";

        public static final String DROP_TEXT = "jide/cursor_drag_text.png";
        public static final String NODROP_TEXT = "jide/cursor_drag_text_stop.png";

        public static final String PERCENTAGE = "jide/cursor_percentage.png";
        public static final String MOVE_EAST = "jide/cursor_move_east.png";
        public static final String MOVE_WEST = "jide/cursor_move_west.png";
    }

    public static class WindowMenu {
        public static final String NEW_HORIZONTAL_TAB = "jide/windows_new_horizontal_tab_group.png";
        public static final String NEW_VERTICAL_TAB = "jide/windows_new_vertical_tab_group.png";
    }

    public static class Arrow {
        public static final String DOWN = "jide/direction_down.png";
        public static final String UP = "jide/direction_up.png";
        public static final String LEFT = "jide/direction_left.png";
        public static final String RIGHT = "jide/direction_right.png";
        public static final String DOT = "jide/direction_dot.png";
        public static final String DOUBLE_LEFT = "jide/direction_left_2.png";
        public static final String DOUBLE_RIGHT = "jide/direction_right_2.png";
    }

    public static class FrameActions {
        public static final String HIDE = "jide/frame_action_hide.png";
        public static final String AUTOHIDE = "jide/frame_action_autohide.png";
        public static final String STOP_AUTOHIDE = "jide/frame_action_stopAutohide.png";
        public static final String HIDE_AUTOHIDE = "jide/frame_action_hideAutohide.png";
        public static final String FLOAT = "jide/frame_action_float.png";
        public static final String UNFLOAT = "jide/frame_action_unfloat.png";
        public static final String MAXIMIZE = "jide/frame_action_maximize.png";
        public static final String RESTORE = "jide/frame_action_restore.png";
    }

    public static class Move {
        public static final String ADD = "jide/move_add.png";
        public static final String REMOVE = "jide/move_remove.png";
        public static final String ACCEPT = "jide/move_accept.png";
        public static final String LEFT = "jide/move_left.png";
        public static final String RIGHT = "jide/move_right.png";
        public static final String LEFT_ALL = "jide/move_all_left.png";
        public static final String RIGHT_RIGHT = "jide/move_all_right.png";
        public static final String UP = "jide/move_up.png";
        public static final String DOWN = "jide/move_down.png";
        public static final String START = "jide/move_start.png";
        public static final String END = "jide/move_end.png";
        public static final String TOP = "jide/move_top.png";
        public static final String BOTTOM = "jide/move_bottom.png";
    }


    public static final String TAIL = "jide/tail.gif";

    public static final String MENU_CHECKBOX_VSNET = "jide/menu_checkbox_vsnet.png";

    public static final String MENU_CHECKBOX_ECLIPSE = "jide/menu_checkbox_eclipse.png";

    public static final String MENU_RADIOBUTTON_VSNET = "jide/menu_radiobutton_vsnet.png";

    public static final String MENU_RADIOBUTTON_ECLIPSE = "jide/menu_radiobutton_eclipse.png";

    public static final String SAVE = "jide/file_save.png";

    public static final String JIDE32 = "jide/jide32.png";
    public static final String JIDE50 = "jide/jide50.png";

    public static final String JIDELOGO = "jide/jide_logo.png";


    public static final String JIDELOGO_SMALL = "jide/jide_logo_small.png";
    public static final String JIDELOGO_SMALL2 = "jide/jide_logo_small_2.png";

    public static ImageIcon getImageIcon(String name) {
        if (name != null)
            return IconsFactory.getImageIcon(JideIconsFactory.class, name);
        else
            return null;
    }

    public static Icon getScaledIcon(String name) {
        if (name != null)
            return IconsFactory.getScaledIcon(JideIconsFactory.class, name);
        else
            return null;
    }

    public static void main(String[] argv) {
        IconsFactory.generateHTML(JideIconsFactory.class);
    }


}
