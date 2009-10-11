/*
 * @(#)XertoMetalUtils.java 11/3/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.xerto;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.icons.JideIconsFactory;
import com.jidesoft.icons.MenuCheckIcon;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.BasicRangeSliderUI;
import com.jidesoft.plaf.basic.Painter;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.plaf.vsnet.ResizeFrameBorder;
import com.jidesoft.plaf.vsnet.VsnetLookAndFeelExtension;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.utils.SecurityUtils;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;

/**
 * Initialize the uiClassID to BasicComponentUI mapping for JIDE components using Xerto style for MetalLookAndFeel.
 * Xerto Style is designed by Xerto at http://www.xerto.com.
 */
public class XertoMetalUtils extends VsnetLookAndFeelExtension {

    /**
     * Initializes class defaults with menu components UIDefaults.
     *
     * @param table ui default table
     */
    public static void initClassDefaultsWithMenu(UIDefaults table) {
        VsnetLookAndFeelExtension.initClassDefaultsWithMenu(table);
        initClassDefaultsForXerto(table);
    }

    /**
     * Initializes class defaults with menu components UIDefaults.
     *
     * @param table ui default table
     */
    public static void initClassDefaults(UIDefaults table) {
        VsnetLookAndFeelExtension.initClassDefaults(table);
        initClassDefaultsForXerto(table);
    }

    private static void initClassDefaultsForXerto(UIDefaults table) {
        int products = LookAndFeelFactory.getProductsUsed();

        final String xertoPackageName = "com.jidesoft.plaf.xerto.";

        if ((products & PRODUCT_COMPONENTS) != 0) {
            table.put("CollapsiblePaneUI", xertoPackageName + "XertoCollapsiblePaneUI");
        }

        if ((products & PRODUCT_DOCK) != 0) {
            table.put("SidePaneUI", xertoPackageName + "XertoSidePaneUI");
            table.put("DockableFrameUI", xertoPackageName + "XertoDockableFrameUI");
        }
    }

    /**
     * Initializes components defaults.
     *
     * @param table ui default table
     */
    public static void initComponentDefaultsWithMenu(UIDefaults table) {
        /// always want shading
        System.setProperty("shadingtheme", "true");

        Toolkit toolkit = Toolkit.getDefaultToolkit();

        Object defaultHighlightColor = UIDefaultsLookup.get("controlHighlight");
        Object selectionBackgroundColor = UIDefaultsLookup.get("controlShadow");
        Object menuTextColor = UIDefaultsLookup.get("control");

        Object menuFont = JideSwingUtilities.getMenuFont(toolkit, table);


        Object menuSelectionBackground = new ColorUIResource(XertoUtils.getMenuSelectionColor(UIDefaultsLookup.getColor("controlShadow")));

        Object menuBackground = new ColorUIResource(XertoUtils.getMenuBackgroundColor(UIDefaultsLookup.getColor("control")));

        Object separatorColor = new ColorUIResource(UIDefaultsLookup.getColor("controlShadow").brighter());

        Object[] uiDefaults = {
                "PopupMenuSeparator.foreground", separatorColor,
                "PopupMenuSeparator.background", menuBackground,

                "CheckBoxMenuItem.checkIcon", new MenuCheckIcon(JideIconsFactory.getImageIcon(JideIconsFactory.MENU_CHECKBOX_VSNET)),
                "CheckBoxMenuItem.selectionBackground", menuSelectionBackground,
                "CheckBoxMenuItem.selectionForeground", menuTextColor,
                "CheckBoxMenuItem.acceleratorSelectionForeground", menuTextColor,
                "CheckBoxMenuItem.mouseHoverBackground", menuSelectionBackground,
                "CheckBoxMenuItem.mouseHoverBorder", new BorderUIResource(BorderFactory.createLineBorder(new Color(10, 36, 106))),
                "CheckBoxMenuItem.margin", new InsetsUIResource(3, 0, 3, 0),
                "CheckBoxMenuItem.font", menuFont,
                "CheckBoxMenuItem.acceleratorFont", menuFont,
                "CheckBoxMenuItem.textIconGap", 8,

                "RadioButtonMenuItem.checkIcon", new MenuCheckIcon(JideIconsFactory.getImageIcon(JideIconsFactory.MENU_CHECKBOX_VSNET)),
                "RadioButtonMenuItem.selectionBackground", menuSelectionBackground,
                "RadioButtonMenuItem.selectionForeground", menuTextColor,
                "RadioButtonMenuItem.acceleratorSelectionForeground", menuTextColor,
                "RadioButtonMenuItem.mouseHoverBackground", menuSelectionBackground,
                "RadioButtonMenuItem.mouseHoverBorder", new BorderUIResource(BorderFactory.createLineBorder(new Color(10, 36, 106))),
                "RadioButtonMenuItem.margin", new InsetsUIResource(3, 0, 3, 0),
                "RadioButtonMenuItem.font", menuFont,
                "RadioButtonMenuItem.acceleratorFont", menuFont,
                "RadioButtonMenuItem.textIconGap", 8,

                "MenuBar.border", new BorderUIResource(BorderFactory.createEmptyBorder(2, 2, 2, 2)),
//            "MenuBar.border", new BorderUIResource(BorderFactory.createCompoundBorder(
//                    new PartialEtchedBorder(PartialEtchedBorder.LOWERED, PartialSide.SOUTH),
//                    BorderFactory.createEmptyBorder(2, 2, 2, 2))),

                "Menu.selectionBackground", menuSelectionBackground,
                "Menu.selectionForeground", menuTextColor,
                "Menu.mouseHoverBackground", menuSelectionBackground,
                "Menu.mouseHoverBorder", new BorderUIResource(BorderFactory.createLineBorder(new Color(10, 36, 106))),
                "Menu.margin", new InsetsUIResource(2, 7, 1, 7),
                "Menu.checkIcon", new MenuCheckIcon(JideIconsFactory.getImageIcon(JideIconsFactory.MENU_CHECKBOX_VSNET)),
                "Menu.textIconGap", 2,
                "Menu.font", menuFont,
                "Menu.acceleratorFont", menuFont,
                "Menu.submenuPopupOffsetX", 0,
                "Menu.submenuPopupOffsetY", 0,

                "PopupMenu.border", new BorderUIResource(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(102, 102, 102)), BorderFactory.createEmptyBorder(1, 1, 1, 1))),

                "MenuItem.checkIcon", new MenuCheckIcon(JideIconsFactory.getImageIcon(JideIconsFactory.MENU_CHECKBOX_VSNET)),
                "MenuItem.selectionBackground", menuSelectionBackground,
                "MenuItem.selectionForeground", menuTextColor,
                "MenuItem.acceleratorSelectionForeground", menuTextColor,
                "MenuItem.background", menuBackground,
                "MenuItem.selectionBorderColor", selectionBackgroundColor,
                "MenuItem.shadowWidth", 24,
                "MenuItem.shadowColor", defaultHighlightColor, // TODO: not exactly. The actual one a little bit brighter than it
                "MenuItem.textIconGap", 8,
                "MenuItem.accelEndGap", 18,
                "MenuItem.margin", new InsetsUIResource(4, 0, 3, 0),
                "MenuItem.font", menuFont,
                "MenuItem.acceleratorFont", menuFont
        };
        table.putDefaults(uiDefaults);
        initComponentDefaults(table);

        UIDefaultsLookup.put(table, "Theme.painter", XertoPainter.getInstance());

        // since it used BasicPainter, make sure it is after Theme.Painter is set first.
        Object popupMenuBorder = new BorderUIResource(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(((ThemePainter) UIDefaultsLookup.get("Theme.painter")).getMenuItemBorderColor()), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        table.put("PopupMenu.border", popupMenuBorder);
    }

    /**
     * Initializes components defaults with menu components UIDefaults.
     *
     * @param table ui default table
     */
    public static void initComponentDefaults(UIDefaults table) {
        /// always want shading
        System.setProperty("shadingtheme", "true");

        Toolkit toolkit = Toolkit.getDefaultToolkit();

        Object defaultBackgroundColor = UIDefaultsLookup.get("control");
        Object defaultTextColor = UIDefaultsLookup.get("controlText");
        Object defaultShadowColor = UIDefaultsLookup.get("controlShadow");
        Object defaultDarkShadowColor = UIDefaultsLookup.get("controlDkShadow");
        Object defaultHighlightColor = UIDefaultsLookup.get("controlHighlight");
        Object defaultLtHighlightColor = UIDefaultsLookup.get("controlLtHighlight");
        Object activeTitleBackgroundColor = UIDefaultsLookup.get("activeCaption");
        Object activeTitleTextColor = UIDefaultsLookup.get("activeCaptionText");
        Object selectionBackgroundColor = defaultShadowColor;
        Object mdiBackgroundColor = defaultShadowColor;
        Object menuTextColor = defaultTextColor;

        Object singleLineBorder = new BorderUIResource(BorderFactory.createLineBorder(UIDefaultsLookup.getColor("controlShadow")));

        Object controlFont = JideSwingUtilities.getControlFont(toolkit, table);
        Object toolbarFont = JideSwingUtilities.getMenuFont(toolkit, table);
        Object boldFont = JideSwingUtilities.getBoldFont(toolkit, table);

        Object resizeBorder = new XertoFrameBorder(new Insets(4, 4, 4, 4));


        Object defaultFormBackground = XertoUtils.getDefaultBackgroundColor(UIDefaultsLookup.getColor("control"));

        Object inactiveTabForground = (UIDefaultsLookup.getColor("controlShadow")).darker();

        Object focusedButtonColor = new ColorUIResource(XertoUtils.getFocusedButtonColor(UIDefaultsLookup.getColor("textHighlight")));


        Object selectedAndFocusedButtonColor = new ColorUIResource(XertoUtils.getSelectedAndFocusedButtonColor(UIDefaultsLookup.getColor("textHighlight")));

        Object selectedButtonColor = new ColorUIResource(XertoUtils.getSelectedButtonColor(UIDefaultsLookup.getColor("textHighlight")));


        Object gripperForeground = new ColorUIResource(XertoUtils.getGripperForegroundColor(UIDefaultsLookup.getColor("control")));

        Object commandBarBackground = new ColorUIResource(XertoUtils.getToolBarBackgroundColor(UIDefaultsLookup.getColor("control")));

        Painter gripperPainter = new Painter() {
            public void paint(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
                XertoPainter.getInstance().paintGripper(c, g, rect, orientation, state);
            }
        };

        Object buttonBorder = new BasicBorders.MarginBorder();

        ImageIcon sliderHorizontalImage = IconsFactory.getImageIcon(BasicRangeSliderUI.class, "icons/slider_horizontal.gif");
        ImageIcon sliderVerticalImage = IconsFactory.getImageIcon(BasicRangeSliderUI.class, "icons/slider_vertical.gif");

        Object[] uiDefaults = new Object[]{
                // common
                "JideLabel.font", controlFont,
                "JideLabel.background", defaultBackgroundColor,
                "JideLabel.foreground", defaultTextColor,

                "JideScrollPane.border", singleLineBorder,

                "JideButton.selectedAndFocusedBackground", selectedAndFocusedButtonColor,
                "JideButton.focusedBackground", focusedButtonColor,
                "JideButton.selectedBackground", selectedButtonColor,
                "JideButton.borderColor", selectionBackgroundColor,

                "JideButton.font", controlFont,
                "JideButton.background", defaultBackgroundColor,
                "JideButton.foreground", defaultTextColor,
                "JideButton.shadow", defaultShadowColor,
                "JideButton.darkShadow", defaultDarkShadowColor,
                "JideButton.light", defaultHighlightColor,
                "JideButton.highlight", defaultLtHighlightColor,
                "JideButton.border", buttonBorder,
                "JideButton.margin", new InsetsUIResource(3, 3, 3, 3),
                "JideButton.textIconGap", 2,
                "JideButton.textShiftOffset", 0,
                "JideButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
                        "SPACE", "pressed",
                        "released SPACE", "released",
//                "ENTER", "pressed",
//                "released ENTER", "released"   // no last two for metal
                }),

                "JideSplitPane.dividerSize", 3,
                "JideSplitPaneDivider.border", new BorderUIResource(BorderFactory.createEmptyBorder()),
                "JideSplitPaneDivider.background", defaultBackgroundColor,
                "JideSplitPaneDivider.gripperPainter", gripperPainter,

                "JideTabbedPane.defaultTabShape", JideTabbedPane.SHAPE_ROUNDED_VSNET,
                "JideTabbedPane.defaultResizeMode", JideTabbedPane.RESIZE_MODE_NONE,
                "JideTabbedPane.defaultTabColorTheme", JideTabbedPane.COLOR_THEME_OFFICE2003,

                "JideTabbedPane.tabRectPadding", 2,
                "JideTabbedPane.closeButtonMarginHorizonal", 3,
                "JideTabbedPane.closeButtonMarginVertical", 3,
                "JideTabbedPane.textMarginVertical", 4,
                "JideTabbedPane.noIconMargin", 2,
                "JideTabbedPane.iconMargin", 5,
                "JideTabbedPane.textPadding", 6,
                "JideTabbedPane.buttonSize", 18,
                "JideTabbedPane.buttonMargin", 5,
                "JideTabbedPane.fitStyleBoundSize", 8,
                "JideTabbedPane.fitStyleFirstTabMargin", 4,
                "JideTabbedPane.fitStyleIconMinWidth", 24,
                "JideTabbedPane.fitStyleTextMinWidth", 16,
                "JideTabbedPane.compressedStyleNoIconRectSize", 24,
                "JideTabbedPane.compressedStyleIconMargin", 12,
                "JideTabbedPane.compressedStyleCloseButtonMarginHorizontal", 0,
                "JideTabbedPane.compressedStyleCloseButtonMarginVertical", 0,
                "JideTabbedPane.fixedStyleRectSize", 60,
                "JideTabbedPane.closeButtonMargin", 2,
                "JideTabbedPane.gripLeftMargin", 4,
                "JideTabbedPane.closeButtonMarginSize", 6,
                "JideTabbedPane.closeButtonLeftMargin", 1,
                "JideTabbedPane.closeButtonRightMargin", 1,

                "JideTabbedPane.defaultTabBorderShadowColor", new ColorUIResource(115, 109, 99),

                "JideTabbedPane.gripperPainter", gripperPainter,
                "JideTabbedPane.border", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),
                "JideTabbedPane.background", new ColorUIResource(XertoUtils.getControlColor()),
                "JideTabbedPane.foreground", new ColorUIResource(XertoUtils.getTabForgroundColor()),
                "JideTabbedPane.light", defaultHighlightColor,
                "JideTabbedPane.highlight", defaultLtHighlightColor,
                "JideTabbedPane.shadow", defaultShadowColor,
                "JideTabbedPane.darkShadow", new ColorUIResource(Color.GRAY),
                "JideTabbedPane.tabInsets", new InsetsUIResource(1, 4, 1, 4),
                "JideTabbedPane.contentBorderInsets", new InsetsUIResource(0, 0, 0, 0),
                "JideTabbedPane.ignoreContentBorderInsetsIfNoTabs", Boolean.FALSE,
                "JideTabbedPane.tabAreaInsets", new InsetsUIResource(2, 4, 0, 4),
                "JideTabbedPane.tabAreaBackground", new ColorUIResource(XertoUtils.getApplicationFrameBackgroundColor()),
                "JideTabbedPane.tabAreaBackgroundLt", defaultLtHighlightColor,
                "JideTabbedPane.tabAreaBackgroundDk", defaultBackgroundColor,
                "JideTabbedPane.tabRunOverlay", 2,
                "JideTabbedPane.font", controlFont,
                "JideTabbedPane.selectedTabFont", controlFont,
                "JideTabbedPane.selectedTabTextForeground", new ColorUIResource(XertoUtils.getTabForgroundColor()),
                "JideTabbedPane.unselectedTabTextForeground", inactiveTabForground,
                "JideTabbedPane.selectedTabBackground", new ColorUIResource(XertoUtils.getSelectedTabBackgroundColor()),
                "JideTabbedPane.selectedTabBackgroundLt", new ColorUIResource(230, 139, 44),
                "JideTabbedPane.selectedTabBackgroundDk", new ColorUIResource(255, 199, 60),
                "JideTabbedPane.tabListBackground", new ColorUIResource(255, 255, 225),
                "JideTabbedPane.textIconGap", 4,
                "JideTabbedPane.showIconOnTab", Boolean.TRUE,
                "JideTabbedPane.showCloseButtonOnTab", Boolean.FALSE,
                "JideTabbedPane.closeButtonAlignment", SwingConstants.TRAILING,
                "JideTabbedPane.focusInputMap",
                new UIDefaults.LazyInputMap(new Object[]{
                        "RIGHT", "navigateRight",
                        "KP_RIGHT", "navigateRight",
                        "LEFT", "navigateLeft",
                        "KP_LEFT", "navigateLeft",
                        "UP", "navigateUp",
                        "KP_UP", "navigateUp",
                        "DOWN", "navigateDown",
                        "KP_DOWN", "navigateDown",
                        "ctrl DOWN", "requestFocusForVisibleComponent",
                        "ctrl KP_DOWN", "requestFocusForVisibleComponent",
                }),
                "JideTabbedPane.ancestorInputMap",
                new UIDefaults.LazyInputMap(new Object[]{
                        "ctrl PAGE_DOWN", "navigatePageDown",
                        "ctrl PAGE_UP", "navigatePageUp",
                        "ctrl UP", "requestFocus",
                        "ctrl KP_UP", "requestFocus",
                }),

                "ButtonPanel.order", "ACO",
                "ButtonPanel.oppositeOrder", "H",
                "ButtonPanel.buttonGap", 6,
                "ButtonPanel.groupGap", 6,
                "ButtonPanel.minButtonWidth", 75,

                "RangeSlider.lowerIcon", IconsFactory.getIcon(null, sliderHorizontalImage, 0, 0, 9, 8),
                "RangeSlider.upperIcon", IconsFactory.getIcon(null, sliderHorizontalImage, 0, 8, 9, 8),
                "RangeSlider.middleIcon", IconsFactory.getIcon(null, sliderHorizontalImage, 0, 16, 9, 6),
                "RangeSlider.lowerVIcon", IconsFactory.getIcon(null, sliderVerticalImage, 0, 0, 8, 9),
                "RangeSlider.upperVIcon", IconsFactory.getIcon(null, sliderVerticalImage, 8, 0, 8, 9),
                "RangeSlider.middleVIcon", IconsFactory.getIcon(null, sliderVerticalImage, 16, 0, 6, 9),

                "MeterProgressBar.border", new BorderUIResource(BorderFactory.createLineBorder(Color.BLACK)),
                "MeterProgressBar.background", new ColorUIResource(Color.BLACK),
                "MeterProgressBar.foreground", new ColorUIResource(Color.GREEN),
                "MeterProgressBar.cellForeground", new ColorUIResource(Color.GREEN),
                "MeterProgressBar.cellBackground", new ColorUIResource(0x008000),
                "MeterProgressBar.cellLength", 2,
                "MeterProgressBar.cellSpacing", 2,

                "Cursor.hsplit", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.HSPLIT),
                "Cursor.vsplit", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.VSPLIT),

                "Cursor.north", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.NORTH),
                "Cursor.south", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.SOUTH),
                "Cursor.east", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.EAST),
                "Cursor.west", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.WEST),
                "Cursor.tab", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.TAB),
                "Cursor.float", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.FLOAT),
                "Cursor.vertical", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.VERTICAL),
                "Cursor.horizontal", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.HORIZONTAL),
                "Cursor.delete", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.DELETE),
                "Cursor.drag", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.DROP),
                "Cursor.dragStop", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.NODROP),
                "Cursor.dragText", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.DROP_TEXT),
                "Cursor.dragTextStop", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.NODROP_TEXT),
                "Cursor.percentage", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.PERCENTAGE),
                "Cursor.moveEast", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.MOVE_EAST),
                "Cursor.moveWest", JideIconsFactory.getImageIcon(JideIconsFactory.Cursor.MOVE_WEST),

                "Gripper.size", 8,
                "Gripper.foreground", gripperForeground,
                "Gripper.painter", gripperPainter,

                "HeaderBox.background", defaultBackgroundColor,

                "Icon.floating", Boolean.FALSE,

                "Resizable.resizeBorder", resizeBorder,

                "JideSplitButton.font", controlFont,
                "JideSplitButton.margin", new InsetsUIResource(3, 3, 3, 7),
                "JideSplitButton.border", buttonBorder,
                "JideSplitButton.borderPainted", Boolean.FALSE,
                "JideSplitButton.textIconGap", 3,
                "JideSplitButton.selectionForeground", menuTextColor,
                "JideSplitButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
                        "SPACE", "pressed",
                        "released SPACE", "released",
//                "ENTER", "pressed",
//                "released ENTER", "released", // no these two for metal
                        "DOWN", "downPressed",
                        "released DOWN", "downReleased"
                }),
        };
        table.putDefaults(uiDefaults);

        int products = LookAndFeelFactory.getProductsUsed();

        if ((products & PRODUCT_DOCK) != 0) {
            ImageIcon titleButtonImage = IconsFactory.getImageIcon(XertoWindowsUtils.class, "icons/title_buttons_xerto.gif"); // 10 x 10 x 8
            final int titleButtonSize = 10;

            FrameBorder frameBorder = new FrameBorder();

            boolean useShadowBorder = "true".equals(SecurityUtils.getProperty("jide.shadeSlidingBorder", "false"));

            Object slidingEastFrameBorder = new SlidingFrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"),
                    new Insets(1, SlidingFrameBorder.SHADOW_SIZE + 5, 1, 0));

            Object slidingWestFrameBorder = new SlidingFrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"),
                    new Insets(1, 0, 1, SlidingFrameBorder.SHADOW_SIZE + 5));

            Object slidingNorthFrameBorder = new SlidingFrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"),
                    new Insets(0, 1, SlidingFrameBorder.SHADOW_SIZE + 5, 1));

            Object slidingSouthFrameBorder = new SlidingFrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"),
                    new Insets(SlidingFrameBorder.SHADOW_SIZE + 5, 1, 0, 1));

            Object slidingEastFrameBorder2 = new ResizeFrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"),
                    new Insets(0, 4, 0, 0));

            Object slidingWestFrameBorder2 = new ResizeFrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"),
                    new Insets(0, 0, 0, 4));

            Object slidingNorthFrameBorder2 = new ResizeFrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"),
                    new Insets(0, 0, 4, 0));

            Object slidingSouthFrameBorder2 = new ResizeFrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"),
                    new Insets(4, 0, 0, 0));

            uiDefaults = new Object[]{
                    // dock
                    "Workspace.background", mdiBackgroundColor,

                    "SidePane.margin", new InsetsUIResource(1, 1, 1, 1),
                    "SidePane.iconTextGap", 2,
                    "SidePane.textBorderGap", 13,
                    "SidePane.itemGap", 5,
                    "SidePane.groupGap", 13,
                    "SidePane.foreground", defaultDarkShadowColor,
                    "SidePane.background", new ColorUIResource(XertoUtils.getApplicationFrameBackgroundColor()),
                    "SidePane.lineColor", defaultShadowColor,
                    "SidePane.buttonBackground", new ColorUIResource(XertoUtils.getLightControlColor()),
                    "SidePane.selectedButtonBackground", selectedButtonColor,
                    "SidePane.selectedButtonForeground", defaultTextColor,
                    "SidePane.font", controlFont,
                    "SidePane.orientation", 1,
                    "SidePane.showSelectedTabText", Boolean.TRUE,
                    "SidePane.alwaysShowTabText", Boolean.FALSE,

                    "DockableFrame.defaultIcon", JideIconsFactory.getImageIcon(JideIconsFactory.DockableFrame.BLANK),
                    "DockableFrame.background", defaultBackgroundColor,
                    "DockableFrame.border", frameBorder,
                    "DockableFrame.floatingBorder", new BorderUIResource(BorderFactory.createLineBorder(XertoUtils.getFrameBorderColor())),
                    "DockableFrame.slidingEastBorder", useShadowBorder ? slidingEastFrameBorder : slidingEastFrameBorder2,
                    "DockableFrame.slidingWestBorder", useShadowBorder ? slidingWestFrameBorder : slidingWestFrameBorder2,
                    "DockableFrame.slidingNorthBorder", useShadowBorder ? slidingNorthFrameBorder : slidingNorthFrameBorder2,
                    "DockableFrame.slidingSouthBorder", useShadowBorder ? slidingSouthFrameBorder : slidingSouthFrameBorder2,

                    "DockableFrame.activeTitleBackground", activeTitleBackgroundColor,
                    "DockableFrame.activeTitleForeground", new ColorUIResource(Color.WHITE),
                    "DockableFrame.inactiveTitleBackground", defaultBackgroundColor,
                    "DockableFrame.inactiveTitleForeground", new ColorUIResource(Color.WHITE),
                    "DockableFrame.titleBorder", new BorderUIResource(BorderFactory.createEmptyBorder(1, 0, 1, 0)),
                    "DockableFrame.activeTitleBorderColor", activeTitleBackgroundColor,
                    "DockableFrame.inactiveTitleBorderColor", defaultShadowColor,
                    "DockableFrame.font", controlFont,

                    "DockableFrameTitlePane.gripperPainter", gripperPainter,
                    "DockableFrameTitlePane.font", controlFont,
                    "DockableFrameTitlePane.hideIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 0, titleButtonSize, titleButtonSize),
                    "DockableFrameTitlePane.unfloatIcon", IconsFactory.getIcon(null, titleButtonImage, 0, titleButtonSize, titleButtonSize, titleButtonSize),
                    "DockableFrameTitlePane.floatIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 2 * titleButtonSize, titleButtonSize, titleButtonSize),
                    "DockableFrameTitlePane.autohideIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 3 * titleButtonSize, titleButtonSize, titleButtonSize),
                    "DockableFrameTitlePane.stopAutohideIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 4 * titleButtonSize, titleButtonSize, titleButtonSize),
                    "DockableFrameTitlePane.hideAutohideIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 5 * titleButtonSize, titleButtonSize, titleButtonSize),
                    "DockableFrameTitlePane.maximizeIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 6 * titleButtonSize, titleButtonSize, titleButtonSize),
                    "DockableFrameTitlePane.restoreIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 7 * titleButtonSize, titleButtonSize, titleButtonSize),
                    "DockableFrameTitlePane.titleBarComponent", Boolean.FALSE,

                    "DockableFrameTitlePane.alwaysShowAllButtons", Boolean.FALSE, // true if show all three buttons no matter if the buttons is available. false if only show buttons which is available
                    "DockableFrameTitlePane.buttonsAlignment", SwingConstants.TRAILING, // trailing or leading
                    "DockableFrameTitlePane.titleAlignment", SwingConstants.LEADING, // trailing or leading or center
                    "DockableFrameTitlePane.buttonGap", 0, // gap between buttons
                    "DockableFrameTitlePane.showIcon", Boolean.TRUE, // show icon or not, the alignment is the same as titleAlignment
                    "DockableFrameTitlePane.margin", new InsetsUIResource(0, 3, 0, 3), // gap

                    "Contour.color", new ColorUIResource(136, 136, 136),
                    "Contour.thickness", 4,

                    "ContentContainer.background", defaultFormBackground,
                    "ContentContainer.vgap", 3,
                    "ContentContainer.hgap", 3,

                    "DockingFramework.changeCursor", Boolean.FALSE,

                    "FrameContainer.contentBorderInsets", new InsetsUIResource(0, 0, 0, 0),
            };
            table.putDefaults(uiDefaults);
        }

        if ((products & PRODUCT_COMPONENTS) != 0) {
            ColorUIResource collapsiblePaneBackground = new ColorUIResource(236, 234, 217);
            final int SIZE = 12;
            final int MASK_SIZE = 12;
            ImageIcon collapsiblePaneImage = IconsFactory.getImageIcon(XertoMetalUtils.class, "icons/collapsible_pane_xerto.png"); // 12 x 12
            ImageIcon collapsiblePaneMask = IconsFactory.getImageIcon(XertoMetalUtils.class, "icons/collapsible_pane_mask.png"); // 12 x 12
            ImageIcon normalIcon = IconsFactory.getIcon(null, collapsiblePaneImage, 0, 0, SIZE, SIZE);
            ImageIcon emphasizedIcon = IconsFactory.getIcon(null, collapsiblePaneImage, SIZE, 0, SIZE, SIZE);
            ImageIcon downMark = IconsFactory.getIcon(null, collapsiblePaneMask, 0, 0, MASK_SIZE, MASK_SIZE);
            ImageIcon upMark = IconsFactory.getIcon(null, collapsiblePaneMask, 0, MASK_SIZE, MASK_SIZE, MASK_SIZE);

            uiDefaults = new Object[]{
                    // components
                    "CollapsiblePanes.border", new BorderUIResource(BorderFactory.createEmptyBorder(12, 12, 12, 12)),
                    "CollapsiblePanes.gap", 5,

                    "CollapsiblePane.background", collapsiblePaneBackground,
                    "CollapsiblePane.contentBackground", defaultLtHighlightColor,
                    "CollapsiblePane.foreground", new ColorUIResource(XertoUtils.getTextColor(collapsiblePaneBackground)),
                    "CollapsiblePane.emphasizedBackground", collapsiblePaneBackground,
                    "CollapsiblePane.emphasizedForeground", new ColorUIResource(XertoUtils.getTextColor(XertoUtils.getEmBaseColor(collapsiblePaneBackground))),
                    "CollapsiblePane.border", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),

                    "CollapsiblePane.font", controlFont,

                    "CollapsiblePane.contentBorder", new BorderUIResource(BorderFactory.createEmptyBorder(8, 10, 8, 10)),

                    "CollapsiblePane.titleBorder", new BorderUIResource(BorderFactory.createEmptyBorder()),
                    "CollapsiblePane.titleFont", boldFont,

                    "CollapsiblePane.downIcon", IconsFactory.getOverlayIcon(null, normalIcon, downMark, SwingConstants.CENTER),
                    "CollapsiblePane.upIcon", IconsFactory.getOverlayIcon(null, normalIcon, upMark, SwingConstants.CENTER),
                    "CollapsiblePane.downIcon.emphasized", IconsFactory.getOverlayIcon(null, emphasizedIcon, downMark, SwingConstants.CENTER),
                    "CollapsiblePane.upIcon.emphasized", IconsFactory.getOverlayIcon(null, emphasizedIcon, upMark, SwingConstants.CENTER),
                    "CollapsiblePane.titleButtonBackground", normalIcon,
                    "CollapsiblePane.titleButtonBackground.emphasized", emphasizedIcon,

                    "StatusBarItem.border", new BorderUIResource(BorderFactory.createEmptyBorder(0, 1, 0, 1)),

                    "StatusBar.border", new StatusBarBorder(),
                    "StatusBar.gap", 2,
                    "StatusBar.background", defaultBackgroundColor,
                    "StatusBar.font", controlFont,
                    "MemoryStatusBarItem.fillColor", new ColorUIResource(236, 233, 176),

                    "DocumentPane.groupBorder", new BorderUIResource(BorderFactory.createLineBorder(Color.gray)),
                    "DocumentPane.newHorizontalGroupIcon", JideIconsFactory.getImageIcon(JideIconsFactory.WindowMenu.NEW_HORIZONTAL_TAB),
                    "DocumentPane.newVerticalGroupIcon", JideIconsFactory.getImageIcon(JideIconsFactory.WindowMenu.NEW_VERTICAL_TAB),
                    "DocumentPane.boldActiveTab", Boolean.FALSE,

                    "OutlookTabbedPane.buttonStyle", JideButton.TOOLBOX_STYLE,
                    "FloorTabbedPane.buttonStyle", JideButton.TOOLBOX_STYLE,
            };
            table.putDefaults(uiDefaults);
        }

        if ((products & PRODUCT_ACTION) != 0) {
            uiDefaults = new Object[]{
                    // action
                    "CommandBar.font", toolbarFont,
                    "CommandBar.background", commandBarBackground,
                    "CommandBar.foreground", defaultTextColor,
                    "CommandBar.shadow", defaultShadowColor,
                    "CommandBar.darkShadow", defaultDarkShadowColor,
                    "CommandBar.light", defaultHighlightColor,
                    "CommandBar.highlight", defaultLtHighlightColor,
                    "CommandBar.border", new BorderUIResource(BorderFactory.createEmptyBorder(1, 1, 1, 1)),
                    "CommandBar.borderVert", new BorderUIResource(BorderFactory.createEmptyBorder(1, 1, 1, 1)),
                    "CommandBar.borderFloating", new BorderUIResource(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UIDefaultsLookup.getColor("activeCaption"), 2),
                            BorderFactory.createEmptyBorder(1, 1, 1, 1))),
                    "CommandBar.ancestorInputMap",
                    new UIDefaults.LazyInputMap(new Object[]{
                            "UP", "navigateUp",
                            "KP_UP", "navigateUp",
                            "DOWN", "navigateDown",
                            "KP_DOWN", "navigateDown",
                            "LEFT", "navigateLeft",
                            "KP_LEFT", "navigateLeft",
                            "RIGHT", "navigateRight",
                            "KP_RIGHT", "navigateRight"
                    }),
                    "CommandBar.titleBarSize", 17,
                    "CommandBar.titleBarButtonGap", 1,
                    "CommandBar.titleBarBackground", activeTitleBackgroundColor,
                    "CommandBar.titleBarForeground", activeTitleTextColor,
                    "CommandBar.titleBarFont", boldFont,

                    "CommandBar.separatorSize", 5,

                    // *** Separator
                    "CommandBarSeparator.background", XertoUtils.getControlColor(),
                    "CommandBarSeparator.foreground", XertoUtils.getControlMidShadowColor(),

                    "Chevron.size", 11,
            };
            table.putDefaults(uiDefaults);
        }

        if ((products & PRODUCT_GRIDS) != 0) {
            uiDefaults = new Object[]{
                    // grid
                    "NestedTableHeader.cellBorder", UIDefaultsLookup.getBorder("TableHeader.cellBorder"),

                    "GroupList.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{
                            "TAB", "selectNextGroup",
                            "shift TAB", "selectPreviousGroup",
                    }),
            };
            table.putDefaults(uiDefaults);
        }

        if (!JideSwingUtilities.shouldUseSystemFont()) {
            Object uiDefaultsFont[] = {
                    "TabbedPane.font", controlFont,
                    "TitledBorder.font", boldFont,
                    "TableHeader.font", controlFont,
                    "Table.font", controlFont,
                    "List.font", controlFont,
                    "Tree.font", controlFont,
                    "ToolTip.font", controlFont,
                    "CheckBox.font", controlFont,
                    "RadioButton.font", controlFont,
                    "Label.font", controlFont,
                    "Panel.font", controlFont,
                    "TextField.font", controlFont,
                    "ComboBox.font", controlFont,
                    "Button.font", controlFont
            };
            table.putDefaults(uiDefaultsFont);
        }

        UIDefaultsLookup.put(table, "Theme.painter", XertoPainter.getInstance());
    }
}
