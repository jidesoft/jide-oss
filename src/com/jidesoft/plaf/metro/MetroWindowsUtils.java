/*
 * @(#)MetroWindowsUtils.java 5/11/2012
 *
 * Copyright 2002 - 2012 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.metro;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.icons.JideIconsFactory;
import com.jidesoft.icons.MenuCheckIcon;
import com.jidesoft.plaf.ExtWindowsDesktopProperty;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.WindowsDesktopProperty;
import com.jidesoft.plaf.basic.BasicPainter;
import com.jidesoft.plaf.basic.Painter;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.plaf.office2003.Office2003WindowsUtils;
import com.jidesoft.plaf.office2007.Office2007Painter;
import com.jidesoft.plaf.vsnet.ConvertListener;
import com.jidesoft.plaf.vsnet.VsnetWindowsUtils;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.utils.ColorUtils;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;
import java.awt.*;

/**
 * WindowsLookAndFeel with Metro extension
 */
public class MetroWindowsUtils extends VsnetWindowsUtils {

    /**
     * Initializes class defaults.
     *
     * @param table
     * @param withMenu
     */
    public static void initClassDefaults(UIDefaults table, boolean withMenu) {
        Office2003WindowsUtils.initClassDefaults(table, withMenu);

        int products = LookAndFeelFactory.getProductsUsed();

        table.put("JideTabbedPaneUI", "com.jidesoft.plaf.office2007.Office2007JideTabbedPaneUI");

        if ((products & PRODUCT_DOCK) != 0) {
            table.put("SidePaneUI", "com.jidesoft.plaf.office2007.Office2007SidePaneUI");
        }
    }

    /**
     * Initializes class defaults.
     *
     * @param table
     */
    public static void initClassDefaults(UIDefaults table) {
        initClassDefaults(table, true);
    }

    /**
     * Initializes components defaults.
     *
     * @param table
     */
    public static void initComponentDefaults(UIDefaults table) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        WindowsDesktopProperty defaultTextColor = new WindowsDesktopProperty("win.button.textColor", table.get("controlText"), toolkit);

        WindowsDesktopProperty defaultBackgroundColor = new WindowsDesktopProperty("win.3d.backgroundColor", table.get("control"), toolkit);

        WindowsDesktopProperty defaultLightColor = new WindowsDesktopProperty("win.3d.lightColor", table.get("controlHighlight"), toolkit);
        WindowsDesktopProperty defaultHighlightColor = new WindowsDesktopProperty("win.3d.highlightColor", table.get("controlLtHighlight"), toolkit);
        WindowsDesktopProperty defaultShadowColor = new WindowsDesktopProperty("win.3d.shadowColor", table.get("controlShadow"), toolkit);
        WindowsDesktopProperty defaultDarkShadowColor = new WindowsDesktopProperty("win.3d.darkShadowColor", table.get("controlDkShadow"), toolkit);

        Color defaultFormBackground = new ColorUIResource(0xBFDBFF);

        Object toolbarFont = JideSwingUtilities.getMenuFont(toolkit, table);
        Object boldFont = JideSwingUtilities.getBoldFont(toolkit, table);

        Painter gripperPainter = new Painter() {
            public void paint(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
                Object p = UIDefaultsLookup.get("Theme.painter");
                if (p instanceof ThemePainter) {
                    ((ThemePainter) p).paintGripper(c, g, rect, orientation, state);
                }
                else {
                    BasicPainter.getInstance().paintGripper(c, g, rect, orientation, state);
                }
            }
        };

        Object uiDefaults[] = new Object[]{
                "control", new ColorUIResource(0xF2F1F3),
                "MenuItem.checkIcon", new MenuCheckIcon(IconsFactory.getImageIcon(Office2007Painter.class, "icons/menu_checkbox.png")),
                "MenuItem.shadowColor", new ColorUIResource(0xE9EEEE),

                "PopupMenuSeparator.foreground", new ColorUIResource(0xC5C5C5),

                "JideTabbedPane.gripperPainter", gripperPainter,
                "JideSplitPaneDivider.gripperPainter", gripperPainter,
//                "Gallery.downIcon", IconsFactory.getImageIcon(Office2007Painter.class, "icons/gallery_down_button.png"),
//                "Gallery.downIconR", IconsFactory.getImageIcon(Office2007Painter.class, "icons/gallery_down_button_rollover.png"),

                "JideTabbedPane.defaultTabShape", JideTabbedPane.SHAPE_OFFICE2003,
                "JideTabbedPane.defaultTabColorTheme", JideTabbedPane.COLOR_THEME_OFFICE2003,
                "JideTabbedPane.contentBorderInsets", new InsetsUIResource(3, 3, 3, 3),
                "JideTabbedPane.background", defaultFormBackground,

                "JideButton.margin.vertical", new InsetsUIResource(2, 5, 1, 5),
                "JideButton.margin", new InsetsUIResource(3, 3, 3, 4),
                "JideButton.paintDefaultBorder", false,

                "JideSplitButton.margin.vertical", new InsetsUIResource(2, 5, 1, 5),
                "JideSplitButton.margin", new InsetsUIResource(3, 3, 3, 4),
                "JideSplitButton.nonActiveRolloverAlpha", 40,
                "JideSplitButton.textIconGap", 4,
//                "JideSplitButton.foreground", new Color(0x3e6aaa),
//                "JideButton.foreground", new Color(0x3e6aaa),
//                "JideLabel.foreground", new Color(0x3e6aaa),
                "JideLabel.background", table.get("Label.background"),
                "JideLabel.font", table.get("Label.font"),

                "Gripper.painter", gripperPainter,
                "Gripper.foreground", new ColorUIResource(0x6593cf),
                "Gripper.light", new ColorUIResource(0xFFFFFF),
                "NavigationComponent.selectionBackground", new ColorUIResource(51,153, 255),
        };
        table.putDefaults(uiDefaults);

        int products = LookAndFeelFactory.getProductsUsed();

        if ((products & PRODUCT_COMPONENTS) != 0) {
            ImageIcon collapsiblePaneImage = IconsFactory.getImageIcon(MetroWindowsUtils.class, "icons/collapsible_pane_vista.gif"); // 12 x 12 x 2
            final int collapsiblePaneSize = 12;

            uiDefaults = new Object[]{
                    "StatusBar.childrenOpaque", Boolean.FALSE,
                    "StatusBar.border", BorderFactory.createEmptyBorder(2, 0, 2, 0),
                    "MemoryStatusBarItem.fillColor", new ColorUIResource(0xfeba4f),

                    "CollapsiblePane.downIcon", IconsFactory.getIcon(null, collapsiblePaneImage, 0, 0, collapsiblePaneSize, collapsiblePaneSize),
                    "CollapsiblePane.upIcon", IconsFactory.getIcon(null, collapsiblePaneImage, 0, collapsiblePaneSize, collapsiblePaneSize, collapsiblePaneSize),
                    "CollapsiblePanes.backgroundLt", new ColorUIResource(0xbfdbff),
                    "CollapsiblePanes.backgroundDk", new ColorUIResource(0xbfdbff),
            };
            table.putDefaults(uiDefaults);
        }

        if ((products & PRODUCT_DOCK) != 0) {
            ImageIcon titleButtonImage = IconsFactory.getImageIcon(MetroWindowsUtils.class, "icons/title_buttons_metro.gif"); // 10 x 10 x 8
            final int titleButtonSize = 10;

            uiDefaults = new Object[]{
                    "ContentContainer.background", defaultFormBackground,
                    "SidePane.background", defaultFormBackground,

                    "DockableFrame.activeTitleBackground", new ColorUIResource(0xC0D9F0),

                    "DockableFrameTitlePane.gripperPainter", gripperPainter,

                    "DockableFrameTitlePane.hideIcon", JideIconsFactory.getScaledIcon(JideIconsFactory.FrameActions.HIDE),
                    "DockableFrameTitlePane.unfloatIcon", JideIconsFactory.getScaledIcon(JideIconsFactory.FrameActions.UNFLOAT),
                    "DockableFrameTitlePane.floatIcon", JideIconsFactory.getScaledIcon(JideIconsFactory.FrameActions.FLOAT),
                    "DockableFrameTitlePane.autohideIcon", JideIconsFactory.getScaledIcon(JideIconsFactory.FrameActions.AUTOHIDE),
                    "DockableFrameTitlePane.stopAutohideIcon", JideIconsFactory.getScaledIcon(JideIconsFactory.FrameActions.STOP_AUTOHIDE),
                    "DockableFrameTitlePane.hideAutohideIcon", JideIconsFactory.getScaledIcon(JideIconsFactory.FrameActions.HIDE_AUTOHIDE),
                    "DockableFrameTitlePane.maximizeIcon", JideIconsFactory.getScaledIcon(JideIconsFactory.FrameActions.MAXIMIZE),
                    "DockableFrameTitlePane.restoreIcon", JideIconsFactory.getScaledIcon(JideIconsFactory.FrameActions.RESTORE),

                    "DockableFrameTitlePane.backgroundLt", new ColorUIResource(ColorUtils.getDerivedColor(new Color(0xBFDBFF), .55f)),
                    "DockableFrameTitlePane.backgroundDk", new ColorUIResource(ColorUtils.getDerivedColor(new Color(0xBFDBFF), .45f)),
                    "DockableFrameTitlePane.activeBackgroundLt", new ColorUIResource(ColorUtils.getDerivedColor(new Color(0xFAD8A0), .55f)),
                    "DockableFrameTitlePane.activeBackgroundDk", new ColorUIResource(ColorUtils.getDerivedColor(new Color(0xFAD8A0), .45f)),

                    "DockableFrameTitlePane.margin", new InsetsUIResource(1, 6, 0, 6), // gap

                    "DockableFrameTitlePane.buttonGap", 2, // gap between buttons

                    "DockableFrame.activeTitleForeground", Color.WHITE,
                    "DockableFrame.inactiveTitleForeground", Color.BLACK,
                    "FrameContainer.contentBorderInsets", new InsetsUIResource(0, 0, 0, 0),
            };
            table.putDefaults(uiDefaults);
        }
        if ((products & PRODUCT_ACTION) != 0) {
            uiDefaults = new Object[]{
                    "Chevron.alwaysVisible", Boolean.TRUE,
            };
            table.putDefaults(uiDefaults);
        }

        if ((products & PRODUCT_GRIDS) != 0) {
            uiDefaults = new Object[]{
            };
            table.putDefaults(uiDefaults);
        }

        UIDefaultsLookup.put(table, "Theme.painter", MetroPainter.getInstance());

        // since it used BasicPainter, make sure it is after Theme.Painter is set first.
        Object popupMenuBorder = new ExtWindowsDesktopProperty(new String[]{"null"}, new Object[]{((ThemePainter) UIDefaultsLookup.get("Theme.painter")).getMenuItemBorderColor()}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new BorderUIResource(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder((Color) obj[0]), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
            }
        });
        table.put("PopupMenu.border", popupMenuBorder);
    }
}