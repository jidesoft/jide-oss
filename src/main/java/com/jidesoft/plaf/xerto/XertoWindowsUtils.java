/*
 * @(#)XertoWindowsUtils.java 6/13/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.xerto;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.icons.JideIconsFactory;
import com.jidesoft.icons.MenuCheckIcon;
import com.jidesoft.plaf.ExtWindowsDesktopProperty;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.WindowsDesktopProperty;
import com.jidesoft.plaf.basic.Painter;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.plaf.office2003.Office2003WindowsUtils;
import com.jidesoft.plaf.vsnet.ConvertListener;
import com.jidesoft.plaf.vsnet.HeaderCellBorder;
import com.jidesoft.plaf.vsnet.ResizeFrameBorder;
import com.jidesoft.plaf.vsnet.VsnetLookAndFeelExtension;
import com.jidesoft.plaf.windows.WindowsIconFactory;
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
 * Initialize the uiClassID to BasicComponentUI mapping for JIDE components using Xerto style for WindowsLookAndFeel.
 * Xerto Style is designed by Xerto at http://www.xerto.com.
 */
public class XertoWindowsUtils extends Office2003WindowsUtils {

    /**
     * Initializes class defaults with menu components UIDefaults.
     *
     * @param table ui default table
     */
    public static void initClassDefaultsWithMenu(UIDefaults table) {
        VsnetLookAndFeelExtension.initClassDefaultsWithMenu(table);
        initClassDefaults(table);
    }

    /**
     * Initializes class defaults with menu components UIDefaults.
     *
     * @param table ui default table
     */
    public static void initClassDefaults(UIDefaults table) {
        Office2003WindowsUtils.initClassDefaults(table, false);
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

        WindowsDesktopProperty defaultHighlightColor = new WindowsDesktopProperty("win.3d.lightColor", UIDefaultsLookup.get("controlHighlight"), toolkit);
        WindowsDesktopProperty selectionBackgroundColor = new WindowsDesktopProperty("win.item.highlightColor", UIDefaultsLookup.get("controlShadow"), toolkit);
        WindowsDesktopProperty menuTextColor = new WindowsDesktopProperty("win.menu.textColor", UIDefaultsLookup.get("control"), toolkit);

        Object menuFont = JideSwingUtilities.getMenuFont(toolkit, table);

        Object menuSelectionBackground = new ExtWindowsDesktopProperty(//Actual color 182, 189, 210
                new String[]{"win.item.highlightColor"}, new Object[]{UIDefaultsLookup.get("controlShadow")}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new ColorUIResource(XertoUtils.getMenuSelectionColor((Color) obj[0]));
            }
        });

        Object menuBackground = new ExtWindowsDesktopProperty(//Actual color 249, 248, 247
                new String[]{"win.3d.backgroundColor"}, new Object[]{UIDefaultsLookup.get("control")}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new ColorUIResource(XertoUtils.getMenuBackgroundColor((Color) obj[0]));
            }
        });

        Object separatorColor = new ExtWindowsDesktopProperty(// Not exactly right
                new String[]{"win.3d.shadowColor"}, new Object[]{UIDefaultsLookup.get("controlShadow")}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new ColorUIResource(((Color) obj[0]).brighter());
            }
        });

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
        Object popupMenuBorder = new ExtWindowsDesktopProperty(new String[]{"null"}, new Object[]{((ThemePainter) UIDefaultsLookup.get("Theme.painter")).getMenuItemBorderColor()}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new BorderUIResource(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder((Color) obj[0]), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
            }
        });
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

        WindowsDesktopProperty defaultHighlightColor = new WindowsDesktopProperty("win.3d.lightColor", UIDefaultsLookup.get("controlHighlight"), toolkit);
        WindowsDesktopProperty defaultLtHighlightColor = new WindowsDesktopProperty("win.3d.highlightColor", UIDefaultsLookup.get("controlLtHighlight"), toolkit);
        WindowsDesktopProperty selectionBackgroundColor = new WindowsDesktopProperty("win.item.highlightColor", UIDefaultsLookup.get("controlShadow"), toolkit);
        WindowsDesktopProperty mdiBackgroundColor = new WindowsDesktopProperty("win.mdi.backgroundColor", UIDefaultsLookup.get("controlShadow"), toolkit);
        WindowsDesktopProperty menuTextColor = new WindowsDesktopProperty("win.menu.textColor", UIDefaultsLookup.get("controlText"), toolkit);
        WindowsDesktopProperty defaultTextColor = new WindowsDesktopProperty("win.button.textColor", UIDefaultsLookup.get("controlText"), toolkit);
        WindowsDesktopProperty defaultBackgroundColor = new WindowsDesktopProperty("win.3d.backgroundColor", UIDefaultsLookup.get("control"), toolkit);
        WindowsDesktopProperty defaultShadowColor = new WindowsDesktopProperty("win.3d.shadowColor", UIDefaultsLookup.get("controlShadow"), toolkit);
        WindowsDesktopProperty defaultDarkShadowColor = new WindowsDesktopProperty("win.3d.darkShadowColor", UIDefaultsLookup.get("controlDkShadow"), toolkit);
        WindowsDesktopProperty activeTitleBackgroundColor = new WindowsDesktopProperty("win.frame.activeCaptionColor", UIDefaultsLookup.get("activeCaption"), toolkit);
        WindowsDesktopProperty activeTitleTextColor = new WindowsDesktopProperty("win.frame.captionTextColor", UIDefaultsLookup.get("activeCaptionText"), toolkit);

        WindowsDesktopProperty highContrast = new WindowsDesktopProperty("win.highContrast.on", UIDefaultsLookup.get("highContrast"), toolkit);

        Object singleLineBorder = new ExtWindowsDesktopProperty(new String[]{"win.3d.shadowColor"}, new Object[]{UIDefaultsLookup.get("controlShadow")}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new BorderUIResource(BorderFactory.createLineBorder((Color) obj[0]));
            }
        });

        Object controlFont = JideSwingUtilities.getControlFont(toolkit, table);
        Object toolbarFont = JideSwingUtilities.getMenuFont(toolkit, table);
        Object boldFont = JideSwingUtilities.getBoldFont(toolkit, table);

        Object resizeBorder = new ExtWindowsDesktopProperty(new String[]{"win.3d.lightColor", "win.3d.highlightColor", "win.3d.shadowColor", "win.3d.darkShadowColor"},
                new Object[]{UIDefaultsLookup.get("control"), UIDefaultsLookup.get("controlLtHighlight"), UIDefaultsLookup.get("controlShadow"), UIDefaultsLookup.get("controlDkShadow")}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new XertoFrameBorder(new Insets(4, 4, 4, 4));
            }
        });


        Object defaultFormBackground = new ExtWindowsDesktopProperty(new String[]{"win.3d.backgroundColor"}, new Object[]{UIDefaultsLookup.get("control")}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new ColorUIResource(XertoUtils.getDefaultBackgroundColor((Color) obj[0]));
            }
        });

        Object inactiveTabForground = new ExtWindowsDesktopProperty(// Not exactly right
                new String[]{"win.3d.shadowColor"}, new Object[]{UIDefaultsLookup.get("controlShadow")}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new ColorUIResource(((Color) obj[0]).darker());
            }
        });

        Object focusedButtonColor = new ExtWindowsDesktopProperty(new String[]{"win.item.highlightColor"}, new Object[]{UIDefaultsLookup.get("textHighlight")}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new ColorUIResource(XertoUtils.getFocusedButtonColor((Color) obj[0]));
            }
        });

        Object selectedAndFocusedButtonColor = new ExtWindowsDesktopProperty(new String[]{"win.item.highlightColor"}, new Object[]{UIDefaultsLookup.get("textHighlight")}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new ColorUIResource(XertoUtils.getSelectedAndFocusedButtonColor((Color) obj[0]));
            }
        });

        Object selectedButtonColor = new ExtWindowsDesktopProperty(new String[]{"win.item.highlightColor"}, new Object[]{UIDefaultsLookup.get("textHighlight")}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new ColorUIResource(XertoUtils.getSelectedButtonColor((Color) obj[0]));
            }
        });


        Object gripperForeground = new ExtWindowsDesktopProperty(new String[]{"win.3d.backgroundColor"}, new Object[]{UIDefaultsLookup.get("control")}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new ColorUIResource(XertoUtils.getGripperForegroundColor((Color) obj[0]));
            }
        });

        Object commandBarBackground = new ExtWindowsDesktopProperty(new String[]{"win.3d.backgroundColor"}, new Object[]{UIDefaultsLookup.get("control")}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return new ColorUIResource(XertoUtils.getToolBarBackgroundColor((Color) obj[0]));
            }
        });

        Painter gripperPainter = new Painter() {
            public void paint(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
                Object p = UIDefaultsLookup.get("Theme.painter");
                if (p instanceof ThemePainter) {
                    ((ThemePainter) p).paintGripper(c, g, rect, orientation, state);
                }
                else {
                    XertoPainter.getInstance().paintGripper(c, g, rect, orientation, state);
                }
            }
        };

        Object buttonBorder = new BasicBorders.MarginBorder();

        Object[] uiDefaults = new Object[]{
                "Theme.highContrast", highContrast,
                "Content.background", defaultBackgroundColor,

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
                "JideButton.textIconGap", 4,
                "JideButton.textShiftOffset", 0,
                "JideButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
                "SPACE", "pressed",
                "released SPACE", "released",
                "ENTER", "pressed",
                "released ENTER", "released"   // no last two for metal
        }),

                "TristateCheckBox.icon", WindowsIconFactory.getCheckBoxIcon(),

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
                "JideTabbedPane.closeButtonLeftMargin", 2,
                "JideTabbedPane.closeButtonRightMargin", 2,

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
                "JideTabbedPane.contentBorderInsets", new InsetsUIResource(1, 1, 1, 1),
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
                "JideTabbedPane.tabListBackground", UIDefaultsLookup.getColor("List.background"),
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

                "JideSplitButton.font", controlFont,
                "JideSplitButton.margin", new InsetsUIResource(3, 3, 3, 7),
                "JideSplitButton.border", buttonBorder,
                "JideSplitButton.borderPainted", Boolean.FALSE,
                "JideSplitButton.textIconGap", 4,
                "JideSplitButton.selectionForeground", menuTextColor,
                "JideSplitButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
                "SPACE", "pressed",
                "released SPACE", "released",
                "ENTER", "pressed",
                "released ENTER", "released", // no these two for metal
                "DOWN", "downPressed",
                "released DOWN", "downReleased",
        }),

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

                "TextArea.font", controlFont,
        };
        table.putDefaults(uiDefaults);

        int products = LookAndFeelFactory.getProductsUsed();

        if ((products & PRODUCT_DOCK) != 0) {
            ImageIcon titleButtonImage = IconsFactory.getImageIcon(XertoWindowsUtils.class, "icons/title_buttons_xerto.gif"); // 10 x 10 x 8
            final int titleButtonSize = 10;

            FrameBorder frameBorder = new FrameBorder();

            boolean useShadowBorder = "true".equals(SecurityUtils.getProperty("jide.shadeSlidingBorder", "false"));

            Object slidingEastFrameBorder = new ExtWindowsDesktopProperty(new String[]{"win.3d.lightColor", "win.3d.highlightColor", "win.3d.shadowColor", "win.3d.darkShadowColor"},
                    new Object[]{UIDefaultsLookup.get("control"), UIDefaultsLookup.get("controlLtHighlight"), UIDefaultsLookup.get("controlShadow"), UIDefaultsLookup.get("controlDkShadow")}, toolkit, new ConvertListener() {
                public Object convert(Object[] obj) {
                    return new SlidingFrameBorder((Color) obj[0], (Color) obj[1], (Color) obj[2], (Color) obj[3],
                            new Insets(1, SlidingFrameBorder.SHADOW_SIZE + 5, 1, 0));
                }
            });

            Object slidingWestFrameBorder = new ExtWindowsDesktopProperty(new String[]{"win.3d.lightColor", "win.3d.highlightColor", "win.3d.shadowColor", "win.3d.darkShadowColor"},
                    new Object[]{UIDefaultsLookup.get("control"), UIDefaultsLookup.get("controlLtHighlight"), UIDefaultsLookup.get("controlShadow"), UIDefaultsLookup.get("controlDkShadow")}, toolkit, new ConvertListener() {
                public Object convert(Object[] obj) {
                    return new SlidingFrameBorder((Color) obj[0], (Color) obj[1], (Color) obj[2], (Color) obj[3],
                            new Insets(1, 0, 1, SlidingFrameBorder.SHADOW_SIZE + 5));
                }
            });

            Object slidingNorthFrameBorder = new ExtWindowsDesktopProperty(new String[]{"win.3d.lightColor", "win.3d.highlightColor", "win.3d.shadowColor", "win.3d.darkShadowColor"},
                    new Object[]{UIDefaultsLookup.get("control"), UIDefaultsLookup.get("controlLtHighlight"), UIDefaultsLookup.get("controlShadow"), UIDefaultsLookup.get("controlDkShadow")}, toolkit, new ConvertListener() {
                public Object convert(Object[] obj) {
                    return new SlidingFrameBorder((Color) obj[0], (Color) obj[1], (Color) obj[2], (Color) obj[3],
                            new Insets(0, 1, SlidingFrameBorder.SHADOW_SIZE + 5, 1));
                }
            });

            Object slidingSouthFrameBorder = new ExtWindowsDesktopProperty(new String[]{"win.3d.lightColor", "win.3d.highlightColor", "win.3d.shadowColor", "win.3d.darkShadowColor"},
                    new Object[]{UIDefaultsLookup.get("control"), UIDefaultsLookup.get("controlLtHighlight"), UIDefaultsLookup.get("controlShadow"), UIDefaultsLookup.get("controlDkShadow")}, toolkit, new ConvertListener() {
                public Object convert(Object[] obj) {
                    return new SlidingFrameBorder((Color) obj[0], (Color) obj[1], (Color) obj[2], (Color) obj[3],
                            new Insets(SlidingFrameBorder.SHADOW_SIZE + 5, 1, 0, 1));
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
            final int SIZE = 12;
            final int MASK_SIZE = 12;
            ImageIcon collapsiblePaneImage = IconsFactory.getImageIcon(XertoWindowsUtils.class, "icons/collapsible_pane_xerto.png"); // 12 x 12
            ImageIcon collapsiblePaneMask = IconsFactory.getImageIcon(XertoWindowsUtils.class, "icons/collapsible_pane_mask.png"); // 12 x 12
            ImageIcon normalIcon = IconsFactory.getIcon(null, collapsiblePaneImage, 0, 0, SIZE, SIZE);
            ImageIcon emphasizedIcon = IconsFactory.getIcon(null, collapsiblePaneImage, SIZE, 0, SIZE, SIZE);
            ImageIcon downMark = IconsFactory.getIcon(null, collapsiblePaneMask, 0, 0, MASK_SIZE, MASK_SIZE);
            ImageIcon upMark = IconsFactory.getIcon(null, collapsiblePaneMask, 0, MASK_SIZE, MASK_SIZE, MASK_SIZE);

            ColorUIResource collapsiblePaneBackground = new ColorUIResource(172, 168, 153);

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
                    "NestedTableHeader.cellBorder", new HeaderCellBorder(),

                    "GroupList.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{
                    "TAB", "selectNextGroup",
                    "shift TAB", "selectPreviousGroup",
            }),
            };
            table.putDefaults(uiDefaults);
        }

        if ((products & PRODUCT_DIFF) != 0) {
            uiDefaults = new Object[]{
                    "DiffMerge.changed", new ColorUIResource(196, 196, 255),
                    "DiffMerge.deleted", new ColorUIResource(200, 200, 200),
                    "DiffMerge.inserted", new ColorUIResource(196, 255, 196),
                    "DiffMerge.conflicted", new ColorUIResource(255, 153, 153),
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
