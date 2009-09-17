/*
 * @(#)JideCursors.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import java.awt.*;

/**
 * A utility class that create additional cursors used by JIDE products.
 * <p/>
 * Notes: this class has to be public so that JIDE can use it in different packages, not meant to release to end user as
 * a public API. JIDE will not guarantee the class will remain as it is.
 */
public class JideCursors {

    /**
     * First id of Cursors used in JIDE products.
     */
    public static final int FIRST_CUSTOM_CURSOR = 20;

    /**
     * The horizontal split cursor type.
     */
    public static final int HSPLIT_CURSOR = 20;

    /**
     * The vertical split cursor type.
     */
    public static final int VSPLIT_CURSOR = 21;

    /**
     * The drag cursor type.
     */
    public static final int DRAG_CURSOR = 22;

    /**
     * The no-drop cursor type.
     */
    public static final int DRAG_STOP_CURSOR = 23;

    /**
     * The cursor point pointing to north side.
     */
    public static final int NORTH_CURSOR = 24;

    /**
     * The cursor point pointing to south side.
     */
    public static final int SOUTH_CURSOR = 25;

    /**
     * The cursor point pointing to east side.
     */
    public static final int EAST_CURSOR = 26;

    /**
     * The cursor point pointing to west side.
     */
    public static final int WEST_CURSOR = 27;

    /**
     * The cursor point pointing when dragged item will be in tabbed pane.
     */
    public static final int TAB_CURSOR = 28;

    /**
     * The cursor point when dragged item is floating.
     */
    public static final int FLOAT_CURSOR = 29;

    /**
     * The cursor point when dragged item will be inserted in between.
     */
    public static final int VERTICAL_CURSOR = 30;

    /**
     * The cursor point when dragged item will be inserted in between.
     */
    public static final int HORIZONTAL_CURSOR = 31;

    /**
     * The cursor point when dragged item will be inserted in between.
     */
    public static final int DELETE_CURSOR = 32;

    /**
     * The drag cursor type for text.
     */
    public static final int DRAG_TEXT_CURSOR = 33;

    /**
     * The no-drop cursor type for text.
     */
    public static final int DRAG_TEXT_STOP_CURSOR = 34;


    /**
     * The cursor for changing percentage.
     */
    public static final int PERCENTAGE_CURSOR = 35;

    /**
     * The cursor for moving toward east.
     */
    public static final int MOVE_EAST_CURSOR = 36;

    /**
     * The cursor for moving toward west.
     */
    public static final int MOVE_WEST_CURSOR = 37;

    /**
     * Last id of cursor used by JIDE products.
     */
    public static final int LAST_CUSTOM_CURSOR = 38;

    private static final Cursor[] predefined = new Cursor[LAST_CUSTOM_CURSOR - FIRST_CUSTOM_CURSOR + 1];

    static {
        for (int i = FIRST_CUSTOM_CURSOR; i < LAST_CUSTOM_CURSOR; i++)
            getPredefinedCursor(i);
    }

    /**
     * Returns a cursor object with the specified predefined type.
     *
     * @param type the type of predefined cursor
     * @throws IllegalArgumentException if the specified cursor type is invalid
     */
    static public Cursor getPredefinedCursor(int type) {
        if (type < FIRST_CUSTOM_CURSOR || type > LAST_CUSTOM_CURSOR) {
            throw new IllegalArgumentException("illegal cursor type");
        }
        if (predefined[type - FIRST_CUSTOM_CURSOR] == null) {
            predefined[type - FIRST_CUSTOM_CURSOR] = createCursor(type);
        }
        return predefined[type - FIRST_CUSTOM_CURSOR];
    }

    /**
     * Creates a cursor specified by type.
     *
     * @param type cursor type
     * @return the cursor with that type
     */
    protected static Cursor createCursor(int type) {
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension bestSize = toolkit.getBestCursorSize(32, 32);
            int maxColor = toolkit.getMaximumCursorColors();
            switch (type) {
                case HSPLIT_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.hsplit");
                        if (icon == null)
                            return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
                        return toolkit.createCustomCursor(icon.getImage(), new Point(15, 15), "Horizonal Split");
                    }
                    return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
                }
                case VSPLIT_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.vsplit");
                        if (icon == null)
                            return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
                        return toolkit.createCustomCursor(icon.getImage(), new Point(15, 15), "Vertical Split");
                    }
                    return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
                }
                case DRAG_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.drag");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(17, 12), "Drag");
                    }
                    return Cursor.getDefaultCursor();
                }
                case DRAG_STOP_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.dragStop");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(17, 12), "Drag Stop");
                    }
                    return Cursor.getDefaultCursor();
                }
                case DRAG_TEXT_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.dragText");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(0, 0), "Drag Text");
                    }
                    return Cursor.getDefaultCursor();
                }
                case DRAG_TEXT_STOP_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.dragTextStop");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(15, 15), "Drag Text Stop");
                    }
                    return Cursor.getDefaultCursor();
                }
                case NORTH_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.north");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(15, 10), "North");
                    }
                    return Cursor.getDefaultCursor();
                }
                case SOUTH_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.south");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(15, 20), "South");
                    }
                    return Cursor.getDefaultCursor();
                }
                case EAST_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.east");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(20, 15), "East");
                    }
                    return Cursor.getDefaultCursor();
                }
                case WEST_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.west");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(10, 15), "West");
                    }
                    return Cursor.getDefaultCursor();
                }
                case TAB_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.tab");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(15, 15), "Tabbed");
                    }
                    return Cursor.getDefaultCursor();
                }
                case FLOAT_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.float");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(15, 15), "Floating");
                    }
                    return Cursor.getDefaultCursor();
                }
                case VERTICAL_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.vertical");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(15, 15), "Vertical");
                    }
                    return Cursor.getDefaultCursor();
                }
                case HORIZONTAL_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.horizontal");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(15, 15), "Horizontal");
                    }
                    return Cursor.getDefaultCursor();
                }
                case DELETE_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.delete");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(10, 10), "Delete");
                    }
                    return Cursor.getDefaultCursor();
                }
                case PERCENTAGE_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.percentage");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(20, 15), "Percentage");
                    }
                    return Cursor.getDefaultCursor();
                }
                case MOVE_EAST_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.moveEast");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(11, 15), "Move East");
                    }
                    return Cursor.getDefaultCursor();
                }
                case MOVE_WEST_CURSOR: {
                    if (bestSize.width != 0 && maxColor > 3) {
                        ImageIcon icon = (ImageIcon) UIDefaultsLookup.getIcon("Cursor.moveWest");
                        if (icon == null)
                            return Cursor.getDefaultCursor();
                        return toolkit.createCustomCursor(icon.getImage(), new Point(20, 15), "Move West");
                    }
                    return Cursor.getDefaultCursor();
                }
            }
            return null;
        }
        catch (Exception e) {
            return Cursor.getDefaultCursor(); // mainly for HeadlessException.
        }
    }

}