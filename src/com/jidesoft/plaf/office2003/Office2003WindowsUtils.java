/**
 * @(#)Office2003WindowsUtils.java
 *
 * Copyright 2002 - 2004 JIDE Software. All rights reserved.
 */
package com.jidesoft.plaf.office2003;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.plaf.ExtWindowsDesktopProperty;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.WindowsDesktopProperty;
import com.jidesoft.plaf.basic.Painter;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.plaf.vsnet.ConvertListener;
import com.jidesoft.plaf.vsnet.HeaderCellBorder;
import com.jidesoft.plaf.vsnet.ResizeFrameBorder;
import com.jidesoft.plaf.vsnet.VsnetWindowsUtils;
import com.jidesoft.plaf.xerto.SlidingFrameBorder;
import com.jidesoft.plaf.xerto.StatusBarBorder;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.utils.SecurityUtils;
import com.jidesoft.utils.SystemInfo;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;
import java.awt.*;

/**
 * WindowsLookAndFeel with Office2003 extension
 */
public class Office2003WindowsUtils extends VsnetWindowsUtils {

    /**
     * Initializes class defaults.
     *
     * @param table
     * @param withMenu
     */
    public static void initClassDefaults(UIDefaults table, boolean withMenu) {
        if (withMenu) {
            VsnetWindowsUtils.initClassDefaultsWithMenu(table);
        }
        else {
            VsnetWindowsUtils.initClassDefaults(table);
        }

        int products = LookAndFeelFactory.getProductsUsed();

        table.put("JideTabbedPaneUI", "com.jidesoft.plaf.office2003.Office2003JideTabbedPaneUI");

        if ((products & PRODUCT_DOCK) != 0) {
            table.put("SidePaneUI", "com.jidesoft.plaf.office2003.Office2003SidePaneUI");
        }

        if ((products & PRODUCT_COMPONENTS) != 0) {
            table.put("CollapsiblePaneUI", "com.jidesoft.plaf.office2003.Office2003CollapsiblePaneUI");
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

        WindowsDesktopProperty defaultTextColor = new WindowsDesktopProperty("win.button.textColor", UIDefaultsLookup.get("controlText"), toolkit);
        WindowsDesktopProperty defaultBackgroundColor = new WindowsDesktopProperty("win.3d.backgroundColor", UIDefaultsLookup.get("control"), toolkit);
        WindowsDesktopProperty defaultLightColor = new WindowsDesktopProperty("win.3d.lightColor", UIDefaultsLookup.get("controlHighlight"), toolkit);
        WindowsDesktopProperty defaultHighlightColor = new WindowsDesktopProperty("win.3d.highlightColor", UIDefaultsLookup.get("controlLtHighlight"), toolkit);
        WindowsDesktopProperty defaultShadowColor = new WindowsDesktopProperty("win.3d.shadowColor", UIDefaultsLookup.get("controlShadow"), toolkit);
        WindowsDesktopProperty defaultDarkShadowColor = new WindowsDesktopProperty("win.3d.darkShadowColor", UIDefaultsLookup.get("controlDkShadow"), toolkit);

        Object toolbarFont = JideSwingUtilities.getMenuFont(toolkit, table);
        Object boldFont = JideSwingUtilities.getBoldFont(toolkit, table);

        Painter gripperPainter = new Painter() {
            public void paint(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
                Office2003Painter.getInstance().paintGripper(c, g, rect, orientation, state);
            }
        };

        ImageIcon sliderHorizontalImage = IconsFactory.getImageIcon(Office2003WindowsUtils.class, "icons/slider_horizontal.gif");
        ImageIcon sliderVerticalImage = IconsFactory.getImageIcon(Office2003WindowsUtils.class, "icons/slider_vertical.gif");

        Object uiDefaults[] = new Object[]{
                "JideTabbedPane.defaultTabShape", JideTabbedPane.SHAPE_OFFICE2003,
                "JideTabbedPane.defaultTabColorTheme", JideTabbedPane.COLOR_THEME_OFFICE2003,
                "JideTabbedPane.contentBorderInsets", new InsetsUIResource(3, 3, 3, 3),

                "JideTabbedPane.gripperPainter", gripperPainter,
                "JideTabbedPane.alwaysShowLineBorder", Boolean.FALSE,
                "JideTabbedPane.showFocusIndicator", Boolean.TRUE,

                "JideSplitPaneDivider.gripperPainter", gripperPainter,

                "Gripper.size", 8,
                "Gripper.painter", gripperPainter,
                "Icon.floating", Boolean.FALSE,

                "RangeSlider.lowerIcon", IconsFactory.getIcon(null, sliderHorizontalImage, 0, 0, 9, 10),
                "RangeSlider.upperIcon", IconsFactory.getIcon(null, sliderHorizontalImage, 0, 10, 9, 10),
                "RangeSlider.middleIcon", IconsFactory.getIcon(null, sliderHorizontalImage, 0, 20, 9, 7),
                "RangeSlider.lowerRIcon", IconsFactory.getIcon(null, sliderHorizontalImage, 9, 0, 9, 10),
                "RangeSlider.upperRIcon", IconsFactory.getIcon(null, sliderHorizontalImage, 9, 10, 9, 10),
                "RangeSlider.middleRIcon", IconsFactory.getIcon(null, sliderHorizontalImage, 9, 20, 9, 7),

                "RangeSlider.lowerVIcon", IconsFactory.getIcon(null, sliderVerticalImage, 0, 0, 10, 9),
                "RangeSlider.upperVIcon", IconsFactory.getIcon(null, sliderVerticalImage, 10, 0, 10, 9),
                "RangeSlider.middleVIcon", IconsFactory.getIcon(null, sliderVerticalImage, 20, 0, 7, 9),
                "RangeSlider.lowerVRIcon", IconsFactory.getIcon(null, sliderVerticalImage, 0, 9, 10, 9),
                "RangeSlider.upperVRIcon", IconsFactory.getIcon(null, sliderVerticalImage, 10, 9, 10, 9),
                "RangeSlider.middleVRIcon", IconsFactory.getIcon(null, sliderVerticalImage, 20, 9, 7, 9),

                "JideScrollPane.border", UIDefaultsLookup.getBorder("ScrollPane.border"),

                "Menu.margin", new InsetsUIResource(2, 7, 3, 7),

                "Menu.submenuPopupOffsetX", 1,
                "Menu.submenuPopupOffsetY", 0,
                "MenuBar.border", new BorderUIResource(BorderFactory.createEmptyBorder(1, 2, 1, 2)),

                "PopupMenu.background", new UIDefaults.ActiveValue() {
                    public Object createValue(UIDefaults table) {
                        return Office2003Painter.getInstance().getMenuItemBackground();
                    }
                },
        };
        table.putDefaults(uiDefaults);

        int products = LookAndFeelFactory.getProductsUsed();

        if ((products & PRODUCT_DOCK) != 0) {
            boolean useShadowBorder = "true".equals(SecurityUtils.getProperty("jide.shadeSlidingBorder", "false"));

            Object slidingEastFrameBorder = new ExtWindowsDesktopProperty(new String[]{"win.3d.lightColor", "win.3d.highlightColor", "win.3d.shadowColor", "win.3d.darkShadowColor"},
                    new Object[]{UIDefaultsLookup.get("control"), UIDefaultsLookup.get("controlLtHighlight"), UIDefaultsLookup.get("controlShadow"), UIDefaultsLookup.get("controlDkShadow")}, toolkit, new ConvertListener() {
                        public Object convert(Object[] obj) {
                            return new SlidingFrameBorder((Color) obj[0], (Color) obj[1], (Color) obj[2], (Color) obj[3],
                                    new Insets(1, SlidingFrameBorder.SHADOW_SIZE + 5, 1, 1));
                        }
                    });

            Object slidingWestFrameBorder = new ExtWindowsDesktopProperty(new String[]{"win.3d.lightColor", "win.3d.highlightColor", "win.3d.shadowColor", "win.3d.darkShadowColor"},
                    new Object[]{UIDefaultsLookup.get("control"), UIDefaultsLookup.get("controlLtHighlight"), UIDefaultsLookup.get("controlShadow"), UIDefaultsLookup.get("controlDkShadow")}, toolkit, new ConvertListener() {
                        public Object convert(Object[] obj) {
                            return new SlidingFrameBorder((Color) obj[0], (Color) obj[1], (Color) obj[2], (Color) obj[3],
                                    new Insets(1, 1, 1, SlidingFrameBorder.SHADOW_SIZE + 5));
                        }
                    });

            Object slidingNorthFrameBorder = new ExtWindowsDesktopProperty(new String[]{"win.3d.lightColor", "win.3d.highlightColor", "win.3d.shadowColor", "win.3d.darkShadowColor"},
                    new Object[]{UIDefaultsLookup.get("control"), UIDefaultsLookup.get("controlLtHighlight"), UIDefaultsLookup.get("controlShadow"), UIDefaultsLookup.get("controlDkShadow")}, toolkit, new ConvertListener() {
                        public Object convert(Object[] obj) {
                            return new SlidingFrameBorder((Color) obj[0], (Color) obj[1], (Color) obj[2], (Color) obj[3],
                                    new Insets(1, 1, SlidingFrameBorder.SHADOW_SIZE + 5, 1));
                        }
                    });

            Object slidingSouthFrameBorder = new ExtWindowsDesktopProperty(new String[]{"win.3d.lightColor", "win.3d.highlightColor", "win.3d.shadowColor", "win.3d.darkShadowColor"},
                    new Object[]{UIDefaultsLookup.get("control"), UIDefaultsLookup.get("controlLtHighlight"), UIDefaultsLookup.get("controlShadow"), UIDefaultsLookup.get("controlDkShadow")}, toolkit, new ConvertListener() {
                        public Object convert(Object[] obj) {
                            return new SlidingFrameBorder((Color) obj[0], (Color) obj[1], (Color) obj[2], (Color) obj[3],
                                    new Insets(SlidingFrameBorder.SHADOW_SIZE + 5, 1, 1, 1));
                        }
                    });

            Object slidingEastFrameBorder2 = new ExtWindowsDesktopProperty(new String[]{"win.3d.lightColor", "win.3d.highlightColor", "win.3d.shadowColor", "win.3d.darkShadowColor"},
                    new Object[]{UIDefaultsLookup.get("control"), UIDefaultsLookup.get("controlLtHighlight"), UIDefaultsLookup.get("controlShadow"), UIDefaultsLookup.get("controlDkShadow")}, toolkit, new ConvertListener() {
                        public Object convert(Object[] obj) {
                            return new ResizeFrameBorder((Color) obj[0], (Color) obj[1], (Color) obj[2], (Color) obj[3],
                                    new Insets(0, 4, 0, 0));
                        }
                    });

            Object slidingWestFrameBorder2 = new ExtWindowsDesktopProperty(new String[]{"win.3d.lightColor", "win.3d.highlightColor", "win.3d.shadowColor", "win.3d.darkShadowColor"},
                    new Object[]{UIDefaultsLookup.get("control"), UIDefaultsLookup.get("controlLtHighlight"), UIDefaultsLookup.get("controlShadow"), UIDefaultsLookup.get("controlDkShadow")}, toolkit, new ConvertListener() {
                        public Object convert(Object[] obj) {
                            return new ResizeFrameBorder((Color) obj[0], (Color) obj[1], (Color) obj[2], (Color) obj[3],
                                    new Insets(0, 0, 0, 4));
                        }
                    });

            Object slidingNorthFrameBorder2 = new ExtWindowsDesktopProperty(new String[]{"win.3d.lightColor", "win.3d.highlightColor", "win.3d.shadowColor", "win.3d.darkShadowColor"},
                    new Object[]{UIDefaultsLookup.get("control"), UIDefaultsLookup.get("controlLtHighlight"), UIDefaultsLookup.get("controlShadow"), UIDefaultsLookup.get("controlDkShadow")}, toolkit, new ConvertListener() {
                        public Object convert(Object[] obj) {
                            return new ResizeFrameBorder((Color) obj[0], (Color) obj[1], (Color) obj[2], (Color) obj[3],
                                    new Insets(0, 0, 4, 0));
                        }
                    });

            Object slidingSouthFrameBorder2 = new ExtWindowsDesktopProperty(new String[]{"win.3d.lightColor", "win.3d.highlightColor", "win.3d.shadowColor", "win.3d.darkShadowColor"},
                    new Object[]{UIDefaultsLookup.get("control"), UIDefaultsLookup.get("controlLtHighlight"), UIDefaultsLookup.get("controlShadow"), UIDefaultsLookup.get("controlDkShadow")}, toolkit, new ConvertListener() {
                        public Object convert(Object[] obj) {
                            return new ResizeFrameBorder((Color) obj[0], (Color) obj[1], (Color) obj[2], (Color) obj[3],
                                    new Insets(4, 0, 0, 0));
                        }
                    });

            uiDefaults = new Object[]{
                    // dock
                    "SidePane.foreground", defaultTextColor,
                    "SidePane.lineColor", new UIDefaults.ActiveValue() {
                        public Object createValue(UIDefaults table) {
                            return Office2003Painter.getInstance().getControlShadow();
                        }
                    },
                    "StatusBarItem.border", new BorderUIResource(BorderFactory.createEmptyBorder(0, 1, 0, 1)),
                    "StatusBar.border", new StatusBarBorder(),

                    "DockableFrame.titleBorder", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),
                    "DockableFrameTitlePane.use3dButtons", Boolean.FALSE,
                    "DockableFrameTitlePane.gripperPainter", gripperPainter,
                    "DockableFrameTitlePane.margin", new InsetsUIResource(1, 6, 1, 6), // gap
                    "DockableFrameTitlePane.contentFilledButtons", true,

                    "DockableFrame.activeTitleForeground", UIDefaultsLookup.getColor("DockableFrame.inactiveTitleForeground"),

                    "DockableFrame.slidingEastBorder", useShadowBorder ? slidingEastFrameBorder : slidingEastFrameBorder2,
                    "DockableFrame.slidingWestBorder", useShadowBorder ? slidingWestFrameBorder : slidingWestFrameBorder2,
                    "DockableFrame.slidingNorthBorder", useShadowBorder ? slidingNorthFrameBorder : slidingNorthFrameBorder2,
                    "DockableFrame.slidingSouthBorder", useShadowBorder ? slidingSouthFrameBorder : slidingSouthFrameBorder2,

                    "FrameContainer.contentBorderInsets", new InsetsUIResource(3, 3, 3, 3),
            };
            table.putDefaults(uiDefaults);
        }
        if ((products & PRODUCT_ACTION) != 0) {
            Object floatingBorder = new ExtWindowsDesktopProperty(new String[]{"win.3d.titleBarColor"},
                    new Object[]{UIDefaultsLookup.get("controlShadow")}, toolkit, new ConvertListener() {
                        public Object convert(Object[] obj) {
                            return new BorderUIResource(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder((Color) obj[0], 2),
                                    BorderFactory.createEmptyBorder(1, 1, 1, 1)));
                        }
                    });

            WindowsDesktopProperty activeTitleTextColor = new WindowsDesktopProperty("win.frame.captionTextColor", UIDefaultsLookup.get("activeCaptionText"), toolkit);
            WindowsDesktopProperty activeTitleBackgroundColor = new WindowsDesktopProperty("win.frame.activeCaptionColor", UIDefaultsLookup.get("activeCaption"), toolkit);

            uiDefaults = new Object[]{
                    // action
                    "CommandBar.font", toolbarFont,
                    "CommandBar.background", defaultBackgroundColor,
                    "CommandBar.foreground", defaultTextColor,
                    "CommandBar.shadow", defaultShadowColor,
                    "CommandBar.darkShadow", defaultDarkShadowColor,
                    "CommandBar.light", defaultLightColor,
                    "CommandBar.highlight", defaultHighlightColor,
                    "CommandBar.border", new BorderUIResource(BorderFactory.createEmptyBorder(1, 2, 2, 0)),
                    "CommandBar.borderVert", new BorderUIResource(BorderFactory.createEmptyBorder(2, 1, 0, 2)),
                    "CommandBar.borderFloating", new BorderUIResource(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UIDefaultsLookup.getColor("activeCaption"), 2),
                            BorderFactory.createEmptyBorder(1, 1, 1, 1))),
                    "CommandBar.floatingBorder", floatingBorder,
                    "CommandBar.separatorSize", 5,
                    "CommandBar.titleBarSize", 17,
                    "CommandBar.titleBarButtonGap", 1,
                    "CommandBar.titleBarBackground", activeTitleBackgroundColor,
                    "CommandBar.titleBarForeground", SystemInfo.isWindowsVistaAbove() ? new ColorUIResource(Color.WHITE) : activeTitleTextColor,
                    "CommandBar.titleBarFont", boldFont,

                    "Chevron.size", 13,
                    "Chevron.alwaysVisible", Boolean.TRUE,
            };
            table.putDefaults(uiDefaults);
        }

        if ((products & PRODUCT_GRIDS) != 0) {
            uiDefaults = new Object[]{
                    "AbstractComboBox.useJButton", Boolean.FALSE,
                    "NestedTableHeader.cellBorder", new HeaderCellBorder(),

                    "GroupList.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{
                            "TAB", "selectNextGroup",
                            "shift TAB", "selectPreviousGroup",
                    }),
            };
            table.putDefaults(uiDefaults);
        }

        UIDefaultsLookup.put(table, "Theme.painter", Office2003Painter.getInstance());

        // since it used BasicPainter, make sure it is after Theme.Painter is set first.
        Object popupMenuBorder = new ExtWindowsDesktopProperty(new String[]{"null"}, new Object[]{((ThemePainter) UIDefaultsLookup.get("Theme.painter")).getMenuItemBorderColor()}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new BorderUIResource(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder((Color) obj[0]), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
            }
        });
        table.put("PopupMenu.border", popupMenuBorder);
    }
}
