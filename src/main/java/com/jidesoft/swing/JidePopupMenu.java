/*
 * @(#)JidePopupMenu.java
 *
 * Copyright 2002 JIDE Software. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.utils.PortingUtils;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.PopupMenuUI;
import java.awt.*;

/**
 * This component extends JPopupMenu and adds a method to display the menu inside the screen even if the mouse pointer
 * is near the edge of the screen.
 * <p/>
 * It also puts the menu items into a scroll pane. When there are too many menu items that can't fit into one screen,
 * the scroll pane will scroll up and down so that you can still get to all menu items.
 */
public class JidePopupMenu extends JPopupMenu implements Scrollable {

    private static final String uiClassID = "JidePopupMenuUI";
    private static final String uiClassIDAlternative = "PopupMenuUI";

    private boolean _useLightWeightPopup;

    private int _visibleMenuItemCount;

    /**
     * Constructs a <code>JPopupMenu</code> without an "invoker".
     */
    public JidePopupMenu() {
        setupPopupMenu();
    }

    /**
     * Constructs a <code>JPopupMenu</code> with the specified title.
     *
     * @param label the string that a UI may use to display as a title for the popup menu.
     */
    public JidePopupMenu(String label) {
        super(label);
        setupPopupMenu();
    }

    @Override
    public String getUIClassID() {
        return UIDefaultsLookup.getBoolean("JidePopupMenu.uiExtensionDisabled") ? uiClassIDAlternative : uiClassID;
    }

    private void setupPopupMenu() {
        addPopupMenuListener(new ToolTipSwitchPopupMenuListener());
    }

    @Override
    public void updateUI() {
        if (UIDefaultsLookup.get(getUIClassID()) == null) {
            LookAndFeelFactory.installJideExtension();
        }
        setUI((PopupMenuUI) UIManager.getUI(this));
    }

    /**
     * Displays the PopUpMenu at a specified position.
     *
     * @param invoker the component that triggers the event
     * @param x       mouse X position on screen
     * @param y       mouse Y position on screen
     */
    @Override
    public void show(Component invoker, int x, int y) {
        Point p = getPopupMenuOrigin(invoker, x, y);
        super.show(invoker, p.x, p.y);
    }


    /**
     * Figures out the sizes needed to calculate the menu position.
     *
     * @param invoker the component that triggers the event
     * @param x       mouse X position on screen
     * @param y       mouse Y position on screen
     * @return new position
     */
    protected Point getPopupMenuOrigin(Component invoker, int x, int y) {
        Dimension size = getPreferredScrollableViewportSize();

        Point p = new Point(x, y);
        SwingUtilities.convertPointToScreen(p, invoker);
        Rectangle bounds = PortingUtils.ensureOnScreen(new Rectangle(p, size));
        p = bounds.getLocation();
        SwingUtilities.convertPointFromScreen(p, invoker);
        return p;
    }

    @Override
    public void setLocation(int x, int y) {
        // TODO: this is really a hack. Two classes will call this method. One is when the JPopupMenu is show. The other
        if (isVisible() && y <= 0) {
            move(x, y); // cannot call setLocation because it will be recursive call. So call deprecated move in order to bypass this.
        }
        else {
            super.setLocation(x, y);
        }
    }

    public Dimension getPreferredScrollableViewportSize() {
        Dimension size = getPreferredSize();
        Dimension screenSize = PortingUtils.getLocalScreenSize(this);
        Container container = SwingUtilities.getAncestorOfClass(SimpleScrollPane.class, this);
        if (container instanceof SimpleScrollPane) {
            SimpleScrollPane scrollPane = (SimpleScrollPane) container;
            int height = screenSize.height;
            // limit it to the height determined by the visible menu item count
            if (getVisibleMenuItemCount() > 0) {
                int totalHeight = getVisibleMenuItemCount() * getScrollableUnitIncrement(null, 0, 0);
                if (height > totalHeight) {
                    height = totalHeight;
                }
            }
            size.height = Math.min(size.height, height - scrollPane.getScrollUpButton().getPreferredSize().height - scrollPane.getScrollDownButton().getPreferredSize().height);
        }
        return size;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return new JMenuItem("ABC").getPreferredSize().height;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return new JMenuItem("ABC").getPreferredSize().height * 5;
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    private class ToolTipSwitchPopupMenuListener implements PopupMenuListener {
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            _useLightWeightPopup = ToolTipManager.sharedInstance().isLightWeightPopupEnabled();
            ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
        }

        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            ToolTipManager.sharedInstance().setLightWeightPopupEnabled(_useLightWeightPopup);
        }

        public void popupMenuCanceled(PopupMenuEvent e) {
            ToolTipManager.sharedInstance().setLightWeightPopupEnabled(_useLightWeightPopup);
        }
    }

    /**
     * Gets the maximum visible menu item count.
     *
     * @return the maximum visible menu item count.
     * @since 3.6.2
     */
    public int getVisibleMenuItemCount() {
        return _visibleMenuItemCount;
    }

    /**
     * Sets the visible menu item count. It will control the popup menu height along with the screen size, whichever is
     * smaller.
     *
     * @param visibleMenuItemCount the maximum menu item count to be shown. -1 means no limit.
     * @since 3.6.2
     */
    public void setVisibleMenuItemCount(int visibleMenuItemCount) {
        if (_visibleMenuItemCount != visibleMenuItemCount) {
            int oldValue = _visibleMenuItemCount;
            _visibleMenuItemCount = visibleMenuItemCount;
            firePropertyChange("visibleMenuCount", oldValue, visibleMenuItemCount);
        }
    }

}
