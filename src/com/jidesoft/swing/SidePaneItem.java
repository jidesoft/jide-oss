/*
 * @(#)JideSidePaneItem.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import java.awt.*;
import java.awt.event.MouseListener;

/**
 * SidePaneItem is a data structure used by {@link SidePane}. It has a title, an icon, a component
 * and a mouse listener.
 *
 * @see SidePane
 */
public class SidePaneItem {
    private Icon _icon;
    private String _title;
    private Component _component;
    private Color _foreground;
    private Color _background;
    private MouseInputListener _mouseListener;
    private boolean _selected = false;

    /**
     * Constructs a SidePaneItem with title.
     *
     * @param title title of SidePaneItem
     */
    public SidePaneItem(String title) {
        this(title, null, null, null);
    }

    /**
     * Constructs a SidePaneItem with title and icon.
     *
     * @param title title of SidePaneItem
     * @param icon  icon of SidePaneItem
     */
    public SidePaneItem(String title, Icon icon) {
        this(title, icon, null, null);
    }

    /**
     * Constructs a SidePaneItem with title, icon and component.
     *
     * @param title     title of SidePaneItem
     * @param icon      icon of SidePaneItem
     * @param component component in SidePaneItem
     */
    public SidePaneItem(String title, Icon icon, Component component) {
        this(title, icon, component, null);
    }

    /**
     * Constructs a SidePaneItem with title, icon and component.
     *
     * @param title     title of SidePaneItem
     * @param icon      icon of SidePaneItem
     * @param component component in SidePaneItem
     * @param listener  mouse listener when user hover or click on SidePane
     */
    public SidePaneItem(String title, Icon icon, Component component, MouseInputListener listener) {
        setTitle(title);
        setIcon(icon);
        setComponent(component);
        setMouseInputListener(listener);
    }

    /**
     * Gets the icon.
     *
     * @return the icon
     */
    public Icon getIcon() {
        return _icon;
    }

    /**
     * Sets the icon.
     *
     * @param icon the new icon
     */
    public void setIcon(Icon icon) {
        _icon = icon;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        _title = title;
    }

    /**
     * Gets the component.
     *
     * @return the component
     */
    public Component getComponent() {
        return _component;
    }

    /**
     * Sets the component.
     *
     * @param component the new component
     */
    public void setComponent(Component component) {
        _component = component;
    }

    /**
     * Gets the mouse listener.
     *
     * @return the mouse listener
     */
    public MouseInputListener getMouseListener() {
        return _mouseListener;
    }

    /**
     * Sets the mouse listener.
     *
     * @param mouseListener the new mouse listener
     */
    public void setMouseInputListener(MouseInputListener mouseListener) {
        _mouseListener = mouseListener;
    }

    /**
     * True if the item is selected.
     *
     * @return true if the item is selected.
     */
    public boolean isSelected() {
        return _selected;
    }

    /**
     * Selects the item.
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        _selected = selected;
    }

    public Color getForeground() {
        return _foreground;
    }

    public void setForeground(Color foreground) {
        _foreground = foreground;
    }

    public Color getBackground() {
        return _background;
    }

    public void setBackground(Color background) {
        _background = background;
    }
}
