/*
 * @(#)ThemePainter.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.basic;

import javax.swing.*;
import java.awt.*;

/**
 * An interface which defines a list of methods that are used to paint the UI.
 * <p/>
 * Please note, this interface is still in development mode.
 * Future version might break your build if you use it now.
 */
public interface ThemePainter {
    public static final int STATE_DEFAULT = 0;
    public static final int STATE_PRESSED = 1;
    public static final int STATE_ROLLOVER = 2;
    public static final int STATE_SELECTED = 3;
    public static final int STATE_DISABLE = 4;
    public static final int STATE_DISABLE_SELECTED = 5;
    public static final int STATE_DISABLED_ROLLOVER = 6;

    void paintSelectedMenu(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintButtonBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintButtonBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state, boolean showBorder);

    void paintMenuItemBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintMenuItemBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state, boolean showBorder);

    void paintChevronBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintDividerBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintCommandBarBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintFloatingCommandBarBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintMenuShadow(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintGripper(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintChevronMore(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintChevronOption(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintFloatingChevronOption(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintContentBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintCommandBarTitlePane(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintDockableFrameBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintDockableFrameTitlePane(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintCollapsiblePaneTitlePaneBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintCollapsiblePaneTitlePaneBackgroundEmphasized(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintCollapsiblePanesBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintCollapsiblePaneTitlePaneBackgroundPlainEmphasized(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    void paintCollapsiblePaneTitlePaneBackgroundPlain(JComponent c, Graphics g, Rectangle rect, int orientation, int state);

    Color getMenuItemBorderColor();

    Color getGripperForeground();

    Color getGripperForegroundLt();

    Color getSeparatorForeground();

    Color getSeparatorForegroundLt();

    Color getCollapsiblePaneContentBackground();

    Color getCollapsiblePaneTitleForeground();

    Color getCollapsiblePaneTitleForegroundEmphasized();

    Color getCollapsiblePaneFocusTitleForeground();

    Color getCollapsiblePaneFocusTitleForegroundEmphasized();

    ImageIcon getCollapsiblePaneUpIcon();

    ImageIcon getCollapsiblePaneDownIcon();

    ImageIcon getCollapsiblePaneUpIconEmphasized();

    ImageIcon getCollapsiblePaneDownIconEmphasized();

    Color getBackgroundDk();

    Color getBackgroundLt();

    Color getSelectionSelectedDk();

    Color getSelectionSelectedLt();

    Color getMenuItemBackground();

    Color getCommandBarTitleBarBackground();

    Color getColor(Object key);

    Color getControl();

    Color getControlLt();

    Color getControlDk();

    Color getControlShadow();

    Color getDockableFrameTitleBarActiveForeground();

    Color getDockableFrameTitleBarInactiveForeground();

    Color getOptionPaneBannerDk();

    Color getOptionPaneBannerLt();

    Color getOptionPaneBannerForeground();

    Color getTabbedPaneSelectDk();

    Color getTabbedPaneSelectLt();

    Color getTabAreaBackgroundDk();

    Color getTabAreaBackgroundLt();
}
