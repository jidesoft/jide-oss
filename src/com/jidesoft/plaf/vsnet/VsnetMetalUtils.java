/*
 * @(#)VsnetMetalUtils.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.vsnet;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.icons.JideIconsFactory;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.BasicRangeSliderUI;
import com.jidesoft.plaf.basic.Painter;
import com.jidesoft.plaf.metal.MetalPainter;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.JideTabbedPane;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;
import java.beans.Beans;

/**
 * Initialize the uiClassID to BasicComponentUI mapping for JIDE components using Vsnet style for MetalLookAndFeel.
 */
public class VsnetMetalUtils extends VsnetLookAndFeelExtension {

    /**
     * Initializes class defaults.
     *
     * @param table the UI defaults
     */
    public static void initClassDefaultsWithMenu(UIDefaults table) {
        if (!Beans.isDesignTime()) {
            table.put("MenuUI", "com.jidesoft.plaf.metal.MetalMenuUI");
        }
        initClassDefaults(table);
    }

    public static void initClassDefaults(UIDefaults table) {
        VsnetLookAndFeelExtension.initClassDefaults(table);

        final String metalPackageName = "com.jidesoft.plaf.metal.";

        // common
        table.put("JideSplitButtonUI", metalPackageName + "MetalJideSplitButtonUI");
    }

    /**
     * Initializes components defaults.
     *
     * @param table the UI defaults
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
        Object activeTitleBackgroundColor = UIDefaultsLookup.get("activeCaption");
        Object activeTitleBorderColor = UIDefaultsLookup.get("controlDkShadow");
        Object inactiveTitleTextColor = UIDefaultsLookup.get("controlText");
        Object inactiveTitleBackgroundColor = UIDefaultsLookup.get("control");
        Object mdiBackgroundColor = UIDefaultsLookup.get("controlShadow");

        Object controlFont = JideSwingUtilities.getControlFont(toolkit, table);
        Object toolbarFont = JideSwingUtilities.getMenuFont(toolkit, table);

        Object singleLineBorder = new BorderUIResource(BorderFactory.createLineBorder(UIDefaultsLookup.getColor("controlShadow")));

        Object slidingEastFrameBorder = new ResizeFrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"),
                new Insets(0, 4, 0, 0));

        Object slidingWestFrameBorder = new ResizeFrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"),
                new Insets(0, 0, 0, 4));

        Object slidingNorthFrameBorder = new ResizeFrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"),
                new Insets(0, 0, 4, 0));

        Object slidingSouthFrameBorder = new ResizeFrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"),
                new Insets(4, 0, 0, 0));

        Object resizeBorder = new ResizeFrameBorder(UIDefaultsLookup.getColor("control"), UIDefaultsLookup.getColor("controlLtHighlight"), UIDefaultsLookup.getColor("controlShadow"), UIDefaultsLookup.getColor("controlDkShadow"),
                new Insets(4, 4, 4, 4));

        Object defaultFormBackgroundColor = VsnetUtils.getLighterColor((Color) activeTitleBackgroundColor);

        Color highlightColor = UIDefaultsLookup.getColor("textHighlight");

        Object focusedButtonColor =
                new ColorUIResource(VsnetUtils.getRolloverButtonColor(highlightColor));

        Object selectedAndFocusedButtonColor =
                new ColorUIResource(VsnetUtils.getSelectedAndRolloverButtonColor(highlightColor));

        Object selectedButtonColor =
                new ColorUIResource(VsnetUtils.getSelectedButtonColor(highlightColor));

        Painter gripperPainter = new Painter() {
            public void paint(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
                MetalPainter.getInstance().paintGripper(c, g, rect, orientation, state);
            }
        };

        Object buttonBorder = new BasicBorders.MarginBorder();

        ImageIcon sliderHorizontalImage = IconsFactory.getImageIcon(BasicRangeSliderUI.class, "icons/slider_horizontal.gif");
        ImageIcon sliderVerticalImage = IconsFactory.getImageIcon(BasicRangeSliderUI.class, "icons/slider_vertical.gif");

        Object uiDefaults[] = {
                // common
                "JideLabel.font", controlFont,
                "JideLabel.background", defaultBackgroundColor,
                "JideLabel.foreground", defaultTextColor,

                "JideButton.selectedAndFocusedBackground", selectedAndFocusedButtonColor,
                "JideButton.focusedBackground", focusedButtonColor,
                "JideButton.selectedBackground", selectedButtonColor,
                "JideButton.borderColor", Color.black,

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
                        "released SPACE", "released"
                }),

                "JideScrollPane.border", singleLineBorder,

                "JideSplitPane.dividerSize", 3,
                "JideSplitPaneDivider.border", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),
                "JideSplitPaneDivider.background", defaultBackgroundColor,
                "JideSplitPaneDivider.gripperPainter", gripperPainter,

                "JideTabbedPane.defaultTabShape", JideTabbedPane.SHAPE_VSNET,
                "JideTabbedPane.defaultResizeMode", JideTabbedPane.RESIZE_MODE_NONE,
                "JideTabbedPane.defaultTabColorTheme", JideTabbedPane.COLOR_THEME_VSNET,

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
                "JideTabbedPane.background", defaultFormBackgroundColor,
                "JideTabbedPane.foreground", defaultTextColor,
                "JideTabbedPane.light", defaultHighlightColor,
                "JideTabbedPane.highlight", defaultLtHighlightColor,
                "JideTabbedPane.shadow", defaultShadowColor,
                "JideTabbedPane.darkShadow", defaultTextColor,
                "JideTabbedPane.tabInsets", new InsetsUIResource(1, 4, 1, 4),
                "JideTabbedPane.contentBorderInsets", new InsetsUIResource(2, 2, 2, 2),
                "JideTabbedPane.ignoreContentBorderInsetsIfNoTabs", Boolean.FALSE,
                "JideTabbedPane.tabAreaInsets", new InsetsUIResource(2, 4, 0, 4),
                "JideTabbedPane.tabAreaBackground", defaultFormBackgroundColor,
                "JideTabbedPane.tabAreaBackgroundLt", defaultLtHighlightColor,
                "JideTabbedPane.tabAreaBackgroundDk", defaultBackgroundColor,
                "JideTabbedPane.tabRunOverlay", 2,
                "JideTabbedPane.font", controlFont,
                "JideTabbedPane.selectedTabFont", controlFont,
                "JideTabbedPane.selectedTabTextForeground", defaultDarkShadowColor,
                "JideTabbedPane.unselectedTabTextForeground", defaultDarkShadowColor,
                "JideTabbedPane.selectedTabBackground", defaultBackgroundColor,
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

                "Resizable.resizeBorder", resizeBorder,

                "Gripper.size", 8,
                "Gripper.foreground", defaultBackgroundColor,
                "Gripper.painter", gripperPainter,

                "MenuBar.border", new BorderUIResource(BorderFactory.createEmptyBorder(1, 0, 1, 0)),
                "Icon.floating", Boolean.FALSE,

                "JideSplitButton.font", controlFont,
                "JideSplitButton.margin", new InsetsUIResource(3, 3, 3, 7),
                "JideSplitButton.border", buttonBorder,
                "JideSplitButton.borderPainted", Boolean.FALSE,
                "JideSplitButton.textIconGap", 3,
                "JideSplitButton.selectionBackground", UIDefaultsLookup.getColor("MenuItem.selectionBackground"),
                "JideSplitButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
                        "SPACE", "pressed",
                        "released SPACE", "released",
                        "DOWN", "downPressed",
                        "released DOWN", "downReleased"
                }),

                "RangeSlider.lowerIcon", IconsFactory.getIcon(null, sliderHorizontalImage, 0, 0, 9, 8),
                "RangeSlider.upperIcon", IconsFactory.getIcon(null, sliderHorizontalImage, 0, 8, 9, 8),
                "RangeSlider.middleIcon", IconsFactory.getIcon(null, sliderHorizontalImage, 0, 16, 9, 6),
                "RangeSlider.lowerVIcon", IconsFactory.getIcon(null, sliderVerticalImage, 0, 0, 8, 9),
                "RangeSlider.upperVIcon", IconsFactory.getIcon(null, sliderVerticalImage, 8, 0, 8, 9),
                "RangeSlider.middleVIcon", IconsFactory.getIcon(null, sliderVerticalImage, 16, 0, 6, 9),

                "ButtonPanel.order", "ACO",
                "ButtonPanel.oppositeOrder", "H",
                "ButtonPanel.buttonGap", 5,
                "ButtonPanel.groupGap", 5,
                "ButtonPanel.minButtonWidth", 57,

                "MeterProgressBar.border", new BorderUIResource(BorderFactory.createLineBorder(Color.BLACK)),
                "MeterProgressBar.background", new ColorUIResource(Color.BLACK),
                "MeterProgressBar.foreground", new ColorUIResource(Color.GREEN),
                "MeterProgressBar.cellForeground", new ColorUIResource(Color.GREEN),
                "MeterProgressBar.cellBackground", new ColorUIResource(0x008000),
                "MeterProgressBar.cellLength", 2,
                "MeterProgressBar.cellSpacing", 2,

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

            ImageIcon titleButtonImage = IconsFactory.getImageIcon(VsnetMetalUtils.class, "icons/title_buttons_metal.gif"); // 16 x 16
            final int titleButtonSize = 16;

            uiDefaults = new Object[]{
                    // dock
                    "Workspace.background", mdiBackgroundColor,

                    "SidePane.margin", new InsetsUIResource(1, 1, 1, 1),
                    "SidePane.iconTextGap", 2,
                    "SidePane.textBorderGap", 13,
                    "SidePane.itemGap", 5,
                    "SidePane.groupGap", 13,
                    "SidePane.foreground", defaultTextColor,
                    "SidePane.background", defaultFormBackgroundColor,
                    "SidePane.lineColor", defaultDarkShadowColor,
                    "SidePane.buttonBackground", defaultBackgroundColor,
                    "SidePane.font", controlFont,
                    "SidePane.orientation", 1,
                    "SidePane.showSelectedTabText", Boolean.TRUE,
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
                    "DockableFrame.activeTitleForeground", activeTitleTextColor,
                    "DockableFrame.inactiveTitleBackground", inactiveTitleBackgroundColor,
                    "DockableFrame.inactiveTitleForeground", inactiveTitleTextColor,
                    "DockableFrame.titleBorder", new BorderUIResource(BorderFactory.createEmptyBorder(1, 0, 3, 0)),
                    "DockableFrame.inactiveTitleBorderColor", activeTitleBorderColor,
                    "DockableFrame.activeTitleBorderColor", activeTitleBorderColor,
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
                    "DockableFrameTitlePane.buttonGap", 2, // gap between buttons
                    "DockableFrameTitlePane.showIcon", Boolean.FALSE, // show icon or not, the alignment is the same as titleAlignment
                    "DockableFrameTitlePane.margin", new InsetsUIResource(0, 6, 0, 6), // gap
                    "DockableFrameTitlePane.use3dButtons", Boolean.TRUE,

                    "ContentContainer.background", defaultFormBackgroundColor,
                    "ContentContainer.vgap", 1,
                    "ContentContainer.hgap", 1,
                    "MainContainer.border", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),

                    "Contour.color", new ColorUIResource(136, 136, 136),
                    "Contour.thickness", 4,

                    "DockingFramework.changeCursor", Boolean.FALSE,

                    "FrameContainer.contentBorderInsets", new InsetsUIResource(2, 0, 0, 0),

            };
            table.putDefaults(uiDefaults);
        }

        if ((products & PRODUCT_COMPONENTS) != 0) {
            ImageIcon collapsiblePaneImage = IconsFactory.getImageIcon(VsnetMetalUtils.class, "icons/collapsible_pane_metal.gif"); // 12 x 12 x 2
            final int collapsiblePaneSize = 12;

            uiDefaults = new Object[]{
                    // components
                    "CollapsiblePanes.border", new BorderUIResource(BorderFactory.createEmptyBorder(12, 12, 12, 12)),
                    "CollapsiblePanes.gap", 15,

                    "CollapsiblePane.background", defaultBackgroundColor,
                    "CollapsiblePane.contentBackground", defaultHighlightColor,
                    "CollapsiblePane.foreground", defaultTextColor,
                    "CollapsiblePane.emphasizedBackground", activeTitleBackgroundColor,
                    "CollapsiblePane.emphasizedForeground", activeTitleTextColor,
                    "CollapsiblePane.border", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),
                    "CollapsiblePane.font", controlFont,

                    "CollapsiblePane.contentBorder", new BorderUIResource(BorderFactory.createEmptyBorder(8, 10, 8, 10)),

                    "CollapsiblePane.titleBorder", new BorderUIResource(BorderFactory.createEmptyBorder(3, 3, 3, 3)),
                    "CollapsiblePane.titleFont", controlFont,
                    "CollapsiblePane.downIcon", IconsFactory.getIcon(null, collapsiblePaneImage, 0, 0, collapsiblePaneSize, collapsiblePaneSize),
                    "CollapsiblePane.upIcon", IconsFactory.getIcon(null, collapsiblePaneImage, 0, collapsiblePaneSize, collapsiblePaneSize, collapsiblePaneSize),

                    "StatusBarItem.border", BorderFactory.createEtchedBorder(),

                    "StatusBar.border", new BorderUIResource(BorderFactory.createEmptyBorder(2, 0, 0, 0)),
                    "StatusBar.gap", 5,
                    "StatusBar.background", defaultBackgroundColor,
                    "StatusBar.font", controlFont,
                    "MemoryStatusBarItem.fillColor", new ColorUIResource(236, 233, 176),

                    "DocumentPane.groupBorder", new BorderUIResource(BorderFactory.createLineBorder(Color.gray)),
                    "DocumentPane.newHorizontalGroupIcon", JideIconsFactory.getImageIcon(JideIconsFactory.WindowMenu.NEW_HORIZONTAL_TAB),
                    "DocumentPane.newVerticalGroupIcon", JideIconsFactory.getImageIcon(JideIconsFactory.WindowMenu.NEW_VERTICAL_TAB),
                    "DocumentPane.boldActiveTab", Boolean.TRUE,

                    "OutlookTabbedPane.buttonStyle", JideButton.TOOLBOX_STYLE,
                    "FloorTabbedPane.buttonStyle", JideButton.TOOLBOX_STYLE,
            };
            table.putDefaults(uiDefaults);
        }

        if ((products & PRODUCT_ACTION) != 0) {
            uiDefaults = new Object[]{
                    // action
                    "CommandBar.font", toolbarFont,
                    "CommandBar.background", defaultBackgroundColor,
                    "CommandBar.foreground", defaultTextColor,
                    "CommandBar.shadow", defaultShadowColor,
                    "CommandBar.darkShadow", defaultDarkShadowColor,
                    "CommandBar.light", defaultHighlightColor,
                    "CommandBar.highlight", defaultLtHighlightColor,
                    "CommandBar.border", new BorderUIResource(BorderFactory.createEmptyBorder(1, 1, 1, 1)),
                    "CommandBar.borderVert", new BorderUIResource(BorderFactory.createEmptyBorder(1, 1, 1, 1)),
                    "CommandBar.borderFloating", new BorderUIResource(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder((Color) activeTitleBackgroundColor, 2),
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
                    "CommandBar.titleBarFont", controlFont,
                    "CommandBar.minimumSize", new DimensionUIResource(16, 16),

                    "CommandBar.separatorSize", 5,

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

                    "GroupList.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[]{
                            "TAB", "selectNextGroup",
                            "shift TAB", "selectPreviousGroup",
                    }),
            };
            table.putDefaults(uiDefaults);
        }

        // make the spinner has the same font as text field
        table.put("Spinner.font", UIDefaultsLookup.get("TextField.font"));
        table.put("Spinner.margin", UIDefaultsLookup.get("TextField.margin"));
        table.put("FormattedTextField.font", UIDefaultsLookup.get("TextField.font"));

        UIDefaultsLookup.put(table, "Theme.painter", MetalPainter.getInstance());
    }
}