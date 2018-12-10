/*
 * @(#)Overlay.java 3/2/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;

/**
 * <code>Overlayable</code> provides a way to add a number of components on top of another component as the overlay
 * components. Usually we make a component implementing Overlayable interface although it is not required. This
 * interface will allow user to add/remove other components as overlay components and set their location independently.
 */
public interface Overlayable extends SwingConstants {
    /**
     * Client property. If a component has this property, the property will be an Overlayable. The component is the
     * actual component of the Overlayable.
     */
    public static final String CLIENT_PROPERTY_OVERLAYABLE = "Overlayable.overlayable";

    /**
     * Adds an overlay component to the center.
     *
     * @param component the overlay component.
     */
    void addOverlayComponent(JComponent component);

    /**
     * Adds an overlay component at the specified location. The location could be one of the following values. <ul>
     * <li>{@link SwingConstants#CENTER} <li>{@link SwingConstants#SOUTH} <li>{@link SwingConstants#NORTH} <li>{@link
     * SwingConstants#WEST} <li>{@link SwingConstants#EAST} <li>{@link SwingConstants#NORTH_EAST} <li>{@link
     * SwingConstants#NORTH_WEST} <li>{@link SwingConstants#SOUTH_EAST} <li>{@link SwingConstants#SOUTH_WEST} </ul>
     *
     * @param component the overlay component.
     * @param location  the overlay location.
     */
    void addOverlayComponent(JComponent component, int location);

    /**
     * Adds an overlay component at the specified location. The location could be one of the following values. <ul>
     * <li>{@link SwingConstants#CENTER} <li>{@link SwingConstants#SOUTH} <li>{@link SwingConstants#NORTH} <li>{@link
     * SwingConstants#WEST} <li>{@link SwingConstants#EAST} <li>{@link SwingConstants#NORTH_EAST} <li>{@link
     * SwingConstants#NORTH_WEST} <li>{@link SwingConstants#SOUTH_EAST} <li>{@link SwingConstants#SOUTH_WEST} </ul>
     *
     * @param component the overlay component.
     * @param location  the overlay location.
     * @param index     the overlay index. 0 means the first overlay component. -1 means the last overlay component.
     */
    void addOverlayComponent(JComponent component, int location, int index);

    /**
     * Removes an overlay component that was added before.
     *
     * @param component
     */
    void removeOverlayComponent(JComponent component);

    /**
     * Gets the overlay component.
     *
     * @return the overlay component.
     */
    JComponent[] getOverlayComponents();

    /**
     * Sets the overlay component location. The valid values are defined in SwingConstants. They are <ul> <li>{@link
     * SwingConstants#CENTER} <li>{@link SwingConstants#SOUTH} <li>{@link SwingConstants#NORTH} <li>{@link
     * SwingConstants#WEST} <li>{@link SwingConstants#EAST} <li>{@link SwingConstants#NORTH_EAST} <li>{@link
     * SwingConstants#NORTH_WEST} <li>{@link SwingConstants#SOUTH_EAST} <li>{@link SwingConstants#SOUTH_WEST} </ul>
     *
     * @param location the overlay component location.
     */
    void setOverlayLocation(JComponent component, int location);

    /**
     * Gets the overlay component location. If -1, it means the component doesn't exit.
     *
     * @return the overlay component location.
     */
    int getOverlayLocation(JComponent component);

    /**
     * Gets the insets of the overlay component relative to the border of the component. This will affect the actual
     * location of the overlay component except CENTER. If an edge of the insets is greater than 0, it will move the
     * overlay component outwards on that edge. On the opposite, if the value is negative, it will move inward.
     *
     * @return the insets of the overlay component relative to the border of the component.
     */
    Insets getOverlayLocationInsets();

    /**
     * Sets the insets of the overlay component relative to the border of the component.
     *
     * @param insets the insets of the overlay component relative to the border of the component.
     */
    void setOverlayLocationInsets(Insets insets);

    /**
     * Sets all the overlay components visible or invisible. If you want to set one overlay component visible/invisible,
     * you just need to call setVisible of that component.
     *
     * @param visible true to set it visible. False to invisible.
     */
    void setOverlayVisible(boolean visible);
}
