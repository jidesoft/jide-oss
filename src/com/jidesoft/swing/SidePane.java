/*
 * @(#)SidePane.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.SidePaneUI;
import com.jidesoft.plaf.UIDefaultsLookup;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.swing.*;
import javax.swing.plaf.PanelUI;
import java.util.ArrayList;
import java.util.List;

/**
 * SidePane is a component that can display several buttons
 * horzontally or vertically. It usually attaches to side of
 * a JFrame.
 * <p/>
 * Buttons in SidePane can be grouped. Each group is called {@link SidePaneGroup}.
 * Each button in the group is called {@link SidePaneItem}.
 */
public class SidePane extends JPanel implements SwingConstants, Accessible {

    /**
     * A list holds <code>SideGroup</code>
     */
    private final List<SidePaneGroup> _groups = new ArrayList<SidePaneGroup>();

    /**
     * the side which this component is attached to.
     * Possible values are:<ul>
     * <li><code>SwingConstants.NORTH</code>
     * <li><code>SwingConstants.SOUTH</code>
     * <li><code>SwingConstants.WEST</code>
     * <li><code>SwingConstants.EAST</code>
     * </ul>
     */
    private int _attachedSide;

    /**
     * if action is fired when mouse rollover.
     */
    private boolean _rollover = true;

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "SidePaneUI";

    /**
     * Constructor thats takes the side which this component is attached to.
     *
     * @param attachedSide the side which this component is attached to.
     *                     Possible values are:<ul>
     *                     <li><code>SwingConstants.NORTH</code>
     *                     <li><code>SwingConstants.SOUTH</code>
     *                     <li><code>SwingConstants.WEST</code>
     *                     <li><code>SwingConstants.EAST</code>
     *                     </ul>
     * @throws IllegalArgumentException if the value is not one of NORTH, SOUTH, WEST, or EAST.
     */
    public SidePane(int attachedSide) {
        setAttachedSide(attachedSide);
    }

    /**
     * Returns the UI object which implements the L&F for this component.
     *
     * @return a <code>TabbedPaneUI</code> object
     * @see #setUI
     */
    @Override
    public SidePaneUI getUI() {
        return (SidePaneUI) ui;
    }

    /**
     * Sets the UI object which implements the L&F for this component.
     *
     * @param ui the new UI object
     * @see javax.swing.UIDefaults#getUI
     */
    public void setUI(SidePaneUI ui) {
        super.setUI(ui);
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see javax.swing.JComponent#updateUI
     */
    @Override
    public void updateUI() {
        if (UIDefaultsLookup.get(uiClassID) == null) {
            LookAndFeelFactory.installJideExtension();
        }
        setUI((SidePaneUI) UIManager.getUI(this));
    }


    /**
     * Returns the name of the UI class that implements the
     * L&F for this component.
     *
     * @return the string "TabbedPaneUI"
     * @see javax.swing.JComponent#getUIClassID
     * @see javax.swing.UIDefaults#getUI
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Adds a <code>SidePaneGroup<code> to this component.
     * Do nothing if there is nothing in the group.
     *
     * @param group the group to be added
     */
    public void addGroup(SidePaneGroup group) {
        getGroups().add(group);
    }

    /**
     * Removes a <code>SidePaneGroup<code> from this component.
     *
     * @param group the group to be removed
     */
    public void removeGroup(SidePaneGroup group) {
        getGroups().remove(group);
    }

    /**
     * Removes a <code>SidePaneGroup<code> at a spefific index from this component.
     *
     * @param index position of the group to be removed
     */
    public void removeGroup(int index) {
        removeGroup(getGroups().get(index));
    }

    /**
     * Gets the list of groups in this components.
     *
     * @return the list of groups
     */
    public List<SidePaneGroup> getGroups() {
        return _groups;
    }

    /**
     * Gets attached side.
     *
     * @return the attached side
     */
    public int getAttachedSide() {
        return _attachedSide;
    }

    /**
     * Sets the attached side. It will call <code>updateUI</code> automatically.
     * Possible values are:<ul>
     * <li><code>SwingConstants.NORTH</code>
     * <li><code>SwingConstants.SOUTH</code>
     * <li><code>SwingConstants.WEST</code>
     * <li><code>SwingConstants.EAST</code>
     * </ul>
     * <p/>
     * Note: Please call this method before this component is rendered on screen.
     *
     * @param attachedSide the attached side
     * @throws IllegalArgumentException if the value is not one of valid values
     */
    public void setAttachedSide(int attachedSide) {
        if (attachedSide != SwingConstants.NORTH && attachedSide != SwingConstants.SOUTH &&
                attachedSide != SwingConstants.WEST && attachedSide != SwingConstants.EAST) {
            throw new IllegalArgumentException("illegal attached side: must be NORTH, SOUTH, WEST, or EAST");
        }
        _attachedSide = attachedSide;
        updateUI();
    }

    /**
     * Is the side pane expand when mouse moves over?
     *
     * @return if true, side pane will expand when mouse moves over.
     */
    public boolean isRollover() {
        return _rollover;
    }

    /**
     * Set if the side pane expand when mouse moves over.
     *
     * @param rollover
     */
    public void setRollover(boolean rollover) {
        _rollover = rollover;
        updateUI();
    }

    /**
     * Gets the AccessibleContext associated with this JToolBar.
     * For tool bars, the AccessibleContext takes the form of an
     * AccessibleJToolBar.
     * A new AccessibleJToolBar instance is created if necessary.
     *
     * @return an AccessibleJToolBar that serves as the
     *         AccessibleContext of this JToolBar
     */
    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleSidePane();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the
     * <code>SidePane</code> class.  It provides an implementation of the
     * Java Accessibility API appropriate to side pane user-interface elements.
     */
    protected class AccessibleSidePane extends AccessibleJPanel {

        /**
         * Get the state of this object.
         *
         * @return an instance of AccessibleStateSet containing the current
         *         state set of the object
         * @see javax.accessibility.AccessibleState
         */
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
            return states;
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the object
         */
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PAGE_TAB_LIST; // consider it as a tab list
        }
    } // inner class AccessibleSidePane
}
