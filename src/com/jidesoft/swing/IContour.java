/*
 * @(#)Contour.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;

/**
 * A <code>Contour</code> is a lightweight component which only paints the outline of component when dragged. It is also
 * used as a placeholder for some information during dragging.
 * <p/>
 * Usually <code>Contour</code> is added to {@link JLayeredPane} of a {@link RootPaneContainer} so that it looks like
 * floating above other windows.
 * <p/>
 * Notes: this class has to be public so that JIDE can use it in different packages, not meant to release to end user as
 * a public API. JIDE will not guarantee the class will remain as it is.
 */
public interface IContour {

    public Rectangle getBounds();

    public boolean isLightweight();

    public void setBounds(Rectangle r);

    public void setBounds(int x, int y, int width, int height);

    public int getTabHeight();

    /**
     * Sets the tab height.
     *
     * @param tabHeight
     */
    public void setTabHeight(int tabHeight);

    /**
     * Returns true if the contour is in tab-dock mode.
     *
     * @return true if tab-docking; false otherwise
     */
    public boolean isTabDocking();

    /**
     * Sets the tab-docking mode.
     *
     * @param tabDocking new mode
     */
    public void setTabDocking(boolean tabDocking);

    /**
     * Gets the side of the tab.
     *
     * @return the side of the tab
     */
    public int getTabSide();

    /**
     * Sets the side of the tab.
     *
     * @param tabSide
     */
    public void setTabSide(int tabSide);

    /**
     * Returns true if the contour is in floating mode.
     *
     * @return true if floating; false otherwise
     */
    public boolean isFloating();

    /**
     * Sets the floating mode.
     *
     * @param floating new mode
     */
    public void setFloating(boolean floating);

    /**
     * Gets the attached component of this contour.
     *
     * @return the attached component
     */
    public Component getAttachedComponent();

    /**
     * Sets the attached components.
     *
     * @param attachedComponent attached component to be set
     */
    public void setAttachedComponent(Component attachedComponent);

    /**
     * Gets the side of the attached component which the contour is attached to.
     *
     * @return side the attached side
     */
    public int getAttachedSide();

    /**
     * Sets the side of the attached component which the contour is attached to.
     *
     * @param attachedSide the new attached side to be set
     */
    public void setAttachedSide(int attachedSide);

    /**
     * When you dragged a component, several other components could be dragged. For example, if user drags on title bar
     * of FrameContainer, all components in the FrameContainer are considered as dragged. If user drags on tab, only
     * selected one is dragged.
     *
     * @return <code>true</code> if all dragged components are affected; <code>false</code> otherwise.
     */
    public boolean isSingle();

    /**
     * Sets the value of single.
     *
     * @param single <code>true</code> if all dragged components are affected; <code>false</code> otherwise.
     */
    public void setSingle(boolean single);

    /**
     * Checks if docking is allowed.
     *
     * @return <code>true</code> if docking is allowed; <code>false</code> otherwise.
     */
    public boolean isAllowDocking();

    /**
     * Sets the value of docking.
     *
     * @param allowDocking <code>true</code> if docking is allowed; <code>false</code> otherwise.
     */
    public void setAllowDocking(boolean allowDocking);

    public Container getRelativeContainer();

    public void setRelativeContainer(Container relativeContainer);

    /**
     * Gets saved X position of contour before it's hidden.
     *
     * @return saved X position
     */
    public int getSaveX();

    /**
     * Gets saved Y position of contour before it's hidden.
     *
     * @return saved Y position
     */
    public int getSaveY();

    /**
     * Gets saved mouse modifier before the contour is hidden.
     *
     * @return saved mouse modifier
     */
    public int getSaveMouseModifier();

    /**
     * Gets saved dragged component before the contour is hidden.
     *
     * @return saved dragged component
     */
    public JComponent getSaveDraggedComponent();

    /**
     * Stores information before the contour is hidden. Those information will be used to restore when the contour is
     * set visible again.
     *
     * @param comp              the dragged component
     * @param saveX             X position of the contour
     * @param saveY             Y position of the contour
     * @param saveMouseModifier mouse modifier in the MouseEvent
     */
    public void setDraggingInformation(JComponent comp, int saveX, int saveY, int saveMouseModifier);

    public void cleanup();

//    private Screen _screen;
//    private Container _savedContainer;

    /**
     * Makes the component visible or invisible. Overrides <code>Component.setVisible</code>.
     *
     * @param aFlag true to make the component visible; false to make it invisible
     */
    public void setVisible(boolean aFlag);

    /**
     * Determines whether this component should be visible when its parent is visible. Components are initially visible,
     * with the exception of top level components such as <code>Frame</code> objects.
     *
     * @return <code>true</code> if the component is visible, <code>false</code> otherwise
     *
     * @see #setVisible
     * @since JDK1.0
     */
    public boolean isVisible();

    public void setGlassPane(Component glassPane);

    public Component getGlassPane();

    public void setChangeCursor(boolean changeCursor);
}
