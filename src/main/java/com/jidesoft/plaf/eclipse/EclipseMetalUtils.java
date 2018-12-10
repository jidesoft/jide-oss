/*
 * @(#)EclipssMetalUtils.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.eclipse;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.icons.JideIconsFactory;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.Painter;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.plaf.metal.MetalIconFactory;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.utils.ColorUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;

/**
 * Utility Class for WindowsLookAndFeel to add Eclipse related LookAndFeel style
 */
public class EclipseMetalUtils extends EclipseLookAndFeelExtension {

    /**
     * Initializes class defaults.
     *
     * @param table
     */
    public static void initClassDefaults(UIDefaults table) {
        EclipseLookAndFeelExtension.initClassDefaults(table);

        final String metalPackageName = "com.jidesoft.plaf.metal.";
        int products = LookAndFeelFactory.getProductsUsed();

        table.put("RangeSliderUI", metalPackageName + "MetalRangeSliderUI");

        if ((products & PRODUCT_GRIDS) != 0) {
            table.put("ExComboBoxUI", metalPackageName + "MetalExComboBoxUI");
        }
    }

    /**
     * Initializes components defaults.
     *
     * @param table
     */
    public static void initComponentDefaults(UIDefaults table) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        Object defaultTextColor = UIDefaultsLookup.get("controlText");
        Object defaultBackgroundColor = UIDefaultsLookup.get("control");
        Object defaultHighlightColor = UIDefaultsLookup.get("controlHighlight");
        Object defaultLtHighlightColor = UIDefaultsLookup.get("controlLtHighlight");
        Object defaultShadowColor = UIDefaultsLookup.get("controlShadow");
        Object defaultDarkShadowColor = UIDefaultsLookup.get("controlDkShadow");
        Object activeTitleTextColor = UIDefaultsLookup.get("activeCaptionText");
        Object activeTitleBackgroundColor = UIDefaultsLookup.getColor("activeCaption");
        Object activeTitleBarGradientColor = activeTitleBackgroundColor instanceof Color ? ((Color) activeTitleBackgroundColor).darker() : Color.gray.darker();
        Object activeTitleBorderColor = UIDefaultsLookup.get("controlDkShadow");
        Object inactiveTitleTextColor = UIDefaultsLookup.get("controlText");
        Object inactiveTitleBackgroundColor = UIDefaultsLookup.get("control");
        Object mdiBackgroundColor = UIDefaultsLookup.get("controlShadow");
        Object selectionBackgroundColor = UIDefaultsLookup.getColor("controlShadow");

        Object controlFont = JideSwingUtilities.getControlFont(toolkit, table);
        Object toolbarFont = JideSwingUtilities.getMenuFont(toolkit, table);
        Object boldFont = JideSwingUtilities.getBoldFont(toolkit, table);

        Border shadowBorder = BorderFactory.createCompoundBorder(new ShadowBorder(null, null, new Color(171, 168, 165), new Color(143, 141, 138), new Insets(0, 0, 2, 2)),
                BorderFactory.createLineBorder(Color.gray));

        Border documentBorder = shadowBorder;

        Object slidingEastFrameBorder = new FrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"), new Insets(0, 4, 0, 0));

        Object slidingWestFrameBorder = new FrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"), new Insets(0, 0, 0, 4));

        Object slidingNorthFrameBorder = new FrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"), new Insets(0, 0, 4, 0));

        Object slidingSouthFrameBorder = new FrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"), new Insets(4, 0, 0, 0));

        Object focusedButtonColor =
                new ColorUIResource(EclipseUtils.getFocusedButtonColor(UIDefaultsLookup.getColor("textHighlight")));

        Object selectedAndFocusedButtonColor =
                new ColorUIResource(EclipseUtils.getSelectedAndFocusedButtonColor(UIDefaultsLookup.getColor("textHighlight")));

        Object selectedButtonColor =
                new ColorUIResource(EclipseUtils.getSelectedButtonColor(UIDefaultsLookup.getColor("textHighlight")));

        Painter gripperPainter = new Painter() {
            public void paint(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
                Object p = UIDefaultsLookup.get("Theme.painter");
                if (p instanceof ThemePainter) {
                    ((ThemePainter) p).paintGripper(c, g, rect, orientation, state);
                }
                else {
                    EclipsePainter.getInstance().paintGripper(c, g, rect, orientation, state);
                }
            }
        };

        Object buttonBorder = new BasicBorders.MarginBorder();

        Object uiDefaults[] = {
                // common
                "JideLabel.font", controlFont,
                "JideLabel.background", defaultBackgroundColor,
                "JideLabel.foreground", defaultTextColor,

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
                "released SPACE", "released"
        }),

                "TristateCheckBox.icon", MetalIconFactory.getCheckBoxIcon(),

                "JideSplitPane.dividerSize", 3,
                "JideSplitPaneDivider.border", new BorderUIResource(BorderFactory.createEmptyBorder()),
                "JideSplitPaneDivider.background", defaultBackgroundColor,
                "JideSplitPaneDivider.gripperPainter", gripperPainter,

                "JideTabbedPane.defaultTabShape", JideTabbedPane.SHAPE_ECLIPSE,
                "JideTabbedPane.defaultTabColorTheme", JideTabbedPane.COLOR_THEME_WIN2K,

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
                "JideTabbedPane.fitStyleFirstTabMargin", 0,
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
                "JideTabbedPane.border", new BorderUIResource(shadowBorder),
                "JideTabbedPane.background", defaultBackgroundColor,
                "JideTabbedPane.foreground", defaultTextColor,
                "JideTabbedPane.light", defaultHighlightColor,
                "JideTabbedPane.highlight", defaultLtHighlightColor,
                "JideTabbedPane.shadow", defaultShadowColor,
                "JideTabbedPane.darkShadow", defaultDarkShadowColor,
                "JideTabbedPane.tabInsets", new InsetsUIResource(1, 4, 1, 4),
                "JideTabbedPane.contentBorderInsets", new InsetsUIResource(1, 0, 0, 0),
                "JideTabbedPane.ignoreContentBorderInsetsIfNoTabs", Boolean.FALSE,
                "JideTabbedPane.tabAreaInsets", new InsetsUIResource(0, 0, 0, 0),
                "JideTabbedPane.tabAreaBackground", defaultBackgroundColor,
                "JideTabbedPane.tabAreaBackgroundLt", defaultLtHighlightColor,
                "JideTabbedPane.tabAreaBackgroundDk", defaultBackgroundColor,
                "JideTabbedPane.tabRunOverlay", 2,
                "JideTabbedPane.font", controlFont,
                "JideTabbedPane.selectedTabFont", controlFont,
                "JideTabbedPane.selectedTabTextForeground", activeTitleTextColor,
                "JideTabbedPane.unselectedTabTextForeground", inactiveTitleTextColor,
                "JideTabbedPane.selectedTabBackground", defaultBackgroundColor,
                "JideTabbedPane.selectedTabBackgroundLt", new ColorUIResource(230, 139, 44),
                "JideTabbedPane.selectedTabBackgroundDk", new ColorUIResource(255, 199, 60),
                "JideTabbedPane.tabListBackground", UIDefaultsLookup.getColor("List.background"),
                "JideTabbedPane.textIconGap", 4,
                "JideTabbedPane.showIconOnTab", Boolean.FALSE,
                "JideTabbedPane.showCloseButtonOnTab", Boolean.TRUE,
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

                "Resizable.resizeBorder", new BorderUIResource(shadowBorder),

                "Gripper.size", 8,
                "Gripper.size", 8,
                "Gripper.painter", gripperPainter,

                "MenuBar.border", new BorderUIResource(BorderFactory.createEmptyBorder(2, 2, 2, 2)),
                "Icon.floating", Boolean.FALSE,

                "JideSplitButton.font", controlFont,
                "JideSplitButton.margin", new InsetsUIResource(3, 3, 3, 7),
                "JideSplitButton.border", buttonBorder,
                "JideSplitButton.borderPainted", Boolean.FALSE,
                "JideSplitButton.textIconGap", 4,
                "JideSplitButton.selectionBackground", selectionBackgroundColor,
                "JideSplitButton.selectionForeground", defaultTextColor,
                "JideSplitButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
                "SPACE", "pressed",
                "released SPACE", "released",
                "DOWN", "downPressed",
                "released DOWN", "downReleased"
        }),

                "ButtonPanel.order", "ACO",
                "ButtonPanel.oppositeOrder", "H",
                "ButtonPanel.buttonGap", 5,
                "ButtonPanel.groupGap", 5,
                "ButtonPanel.minButtonWidth", 57,

                "HeaderBox.background", defaultBackgroundColor,

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
        };
        table.putDefaults(uiDefaults);

        int products = LookAndFeelFactory.getProductsUsed();

        if ((products & PRODUCT_DOCK) != 0) {
            ImageIcon titleButtonImage = IconsFactory.getImageIcon(EclipseMetalUtils.class, "icons/title_buttons_eclipse.gif"); // 16 x 16 x 8
            final int titleButtonSize = 16;

            uiDefaults = new Object[]{
                    "Workspace.background", mdiBackgroundColor,

                    "SidePane.margin", new InsetsUIResource(1, 1, 1, 1),
                    "SidePane.iconTextGap", 2,
                    "SidePane.textBorderGap", 13,
                    "SidePane.itemGap", 4,
                    "SidePane.groupGap", 3,
                    "SidePane.foreground", defaultDarkShadowColor,
                    "SidePane.background", defaultBackgroundColor,
                    "SidePane.lineColor", defaultShadowColor,
                    "SidePane.buttonBackground", defaultBackgroundColor,
                    "SidePane.font", controlFont,
                    "SidePane.orientation", 1,
                    "SidePane.showSelectedTabText", Boolean.FALSE,
                    "SidePane.alwaysShowTabText", Boolean.FALSE,

                    "DockableFrame.defaultIcon", JideIconsFactory.getImageIcon(JideIconsFactory.DockableFrame.BLANK),
                    "DockableFrame.background", defaultBackgroundColor,
                    "DockableFrame.border", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),
                    "DockableFrame.floatingBorder", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),
                    "DockableFrame.slidingEastBorder", slidingEastFrameBorder,
                    "DockableFrame.slidingWestBorder", slidingWestFrameBorder,
                    "DockableFrame.slidingNorthBorder", slidingNorthFrameBorder,
                    "DockableFrame.slidingSouthBorder", slidingSouthFrameBorder,

                    "DockableFrame.activeTitleBackground", activeTitleBackgroundColor,
                    "DockableFrame.activeTitleBackground2", activeTitleBarGradientColor, //EclipseUtils.getLighterColor((Color)windowTitleBackground),
                    "DockableFrame.activeTitleForeground", activeTitleTextColor,
                    "DockableFrame.inactiveTitleBackground", inactiveTitleBackgroundColor,
                    "DockableFrame.inactiveTitleForeground", inactiveTitleTextColor,
                    "DockableFrame.activeTitleBorderColor", activeTitleBorderColor,
                    "DockableFrame.inactiveTitleBorderColor", activeTitleBorderColor,
                    "DockableFrame.titleBorder", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),
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
                    "DockableFrameTitlePane.titleBarComponent", Boolean.TRUE,

                    "DockableFrameTitlePane.alwaysShowAllButtons", Boolean.FALSE, // true if show all three buttons no matter if the buttons is available. false if only show buttons which is available
                    "DockableFrameTitlePane.buttonsAlignment", SwingConstants.TRAILING, // trailing or leading
                    "DockableFrameTitlePane.titleAlignment", SwingConstants.LEADING, // trailing or leading or center
                    "DockableFrameTitlePane.buttonGap", 3, // gap between buttons
                    "DockableFrameTitlePane.showIcon", Boolean.TRUE, // show icon or not, the alignment is the same as titleAlignment
                    "DockableFrameTitlePane.margin", new InsetsUIResource(0, 6, 0, 6), // gap
                    "DockableFrameTitlePane.use3dButtons", Boolean.TRUE,

                    "ContentContainer.background", defaultBackgroundColor,
                    "ContentContainer.vgap", 1,
                    "ContentContainer.hgap", 1,
                    "MainContainer.border", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),

                    "DockingFramework.changeCursor", Boolean.TRUE,
                    "Contour.color", new ColorUIResource(136, 136, 136),
                    "Contour.thickness", 2,

            };
            table.putDefaults(uiDefaults);
        }

        if ((products & PRODUCT_COMPONENTS) != 0) {
            ImageIcon collapsiblePaneImage = IconsFactory.getImageIcon(EclipseMetalUtils.class, "icons/collapsible_pane_eclipse.gif"); // 11 x 11 x 2
            final int collapsiblePaneSize = 11;

            uiDefaults = new Object[]{
                    "CollapsiblePanes.border", new BorderUIResource(BorderFactory.createEmptyBorder(12, 12, 12, 12)),
                    "CollapsiblePanes.gap", 15,

                    "CollapsiblePane.background", defaultBackgroundColor instanceof Color ? (ColorUtils.getDerivedColor((Color) defaultBackgroundColor, 0.45f)) : defaultBackgroundColor,
                    "CollapsiblePane.contentBackground", defaultHighlightColor,
                    "CollapsiblePane.foreground", defaultTextColor,
                    "CollapsiblePane.emphasizedBackground", activeTitleBackgroundColor,
                    "CollapsiblePane.emphasizedForeground", activeTitleTextColor,
                    "CollapsiblePane.border", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),
                    "CollapsiblePane.font", controlFont,

                    "CollapsiblePane.contentBorder", new BorderUIResource(BorderFactory.createEmptyBorder(8, 10, 8, 10)),

                    "CollapsiblePane.titleBorder", new BorderUIResource(BorderFactory.createEmptyBorder(3, 3, 3, 3)),
                    "CollapsiblePane.titleFont", boldFont,
                    "CollapsiblePane.downIcon", IconsFactory.getIcon(null, collapsiblePaneImage, 0, 0, collapsiblePaneSize, collapsiblePaneSize),
                    "CollapsiblePane.upIcon", IconsFactory.getIcon(null, collapsiblePaneImage, 0, collapsiblePaneSize, collapsiblePaneSize, collapsiblePaneSize),

                    "StatusBarItem.border", new BorderUIResource(BorderFactory.createEtchedBorder()),

                    "StatusBar.border", new BorderUIResource(BorderFactory.createEmptyBorder(2, 0, 0, 0)),
                    "StatusBar.margin", new Insets(2, 0, 0, 0),
                    "StatusBar.gap", 3,
                    "StatusBar.background", defaultBackgroundColor,
                    "StatusBar.font", controlFont,
                    "MemoryStatusBarItem.fillColor", new ColorUIResource(236, 233, 176),

                    "DocumentPane.groupBorder", new BorderUIResource(documentBorder),
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
                    "CommandBar.font", toolbarFont,
                    "CommandBar.background", defaultBackgroundColor,
                    "CommandBar.foreground", defaultTextColor,
                    "CommandBar.shadow", defaultShadowColor,
                    "CommandBar.darkShadow", defaultDarkShadowColor,
                    "CommandBar.light", defaultHighlightColor,
                    "CommandBar.highlight", defaultLtHighlightColor,
                    "CommandBar.border", new BorderUIResource(BorderFactory.createEmptyBorder(2, 1, 2, 1)),
                    "CommandBar.borderVert", new BorderUIResource(BorderFactory.createEmptyBorder(2, 1, 2, 1)),
                    "CommandBar.borderFloating", new BorderUIResource(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder((Color) activeTitleBackgroundColor, 2),
                    BorderFactory.createEmptyBorder(1, 1, 1, 1))),
                    "CommandBar.separatorSize", 3,
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
                    "CommandBar.minimumSize", new DimensionUIResource(20, 20),

                    "CommandBar.separatorSize", new DimensionUIResource(5, 20),

                    // *** Separator
                    "CommandBarSeparator.background", new Color(219, 216, 209),
                    "CommandBarSeparator.foreground", new Color(166, 166, 166),

                    "Chevron.size", 11,
                    "Chevron.alwaysVisible", Boolean.FALSE,

            };
            table.putDefaults(uiDefaults);
        }

        if ((products & PRODUCT_GRIDS) != 0) {
            uiDefaults = new Object[]{
                    "AbstractComboBox.useJButton", Boolean.TRUE,
                    "NestedTableHeader.cellBorder", UIDefaultsLookup.getBorder("TableHeader.cellBorder"),
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

        UIDefaultsLookup.put(table, "Theme.painter", EclipsePainter.getInstance());
    }
}
