/*
 * @(#)AquaJideUtils.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.aqua;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.icons.JideIconsFactory;
import com.jidesoft.plaf.ExtWindowsDesktopProperty;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.vsnet.ConvertListener;
import com.jidesoft.plaf.vsnet.VsnetLookAndFeelExtension;
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

/**
 * AquaJideUtils to add Jide extension to AquaLookAndFeel
 */
public class AquaJideUtils extends VsnetLookAndFeelExtension {
    /**
     * Initializes class defaults.
     *
     * @param table
     */
    public static void initClassDefaults(UIDefaults table) {
        VsnetLookAndFeelExtension.initClassDefaults(table);

        final String aquaPackageName = "com.jidesoft.plaf.aqua.";

        int products = LookAndFeelFactory.getProductsUsed();

        table.put("JideSplitButtonUI", aquaPackageName + "AquaJideSplitButtonUI");
        table.put("JidePopupMenuUI", aquaPackageName + "AquaJidePopupMenuUI");
        table.put("JideTabbedPaneUI", aquaPackageName + "AquaJideTabbedPaneUI");
        table.put("GripperUI", aquaPackageName + "AquaGripperUI");
        table.put("RangeSliderUI", aquaPackageName + "AquaRangeSliderUI");

        if ((products & PRODUCT_GRIDS) != 0) {
            table.put("JideTableUI", aquaPackageName + "AquaJideTableUI");
            table.put("NavigableTableUI", aquaPackageName + "AquaNavigableTableUI");
            table.put("CellSpanTableUI", aquaPackageName + "AquaCellSpanTableUI");
            table.put("TreeTableUI", aquaPackageName + "AquaTreeTableUI");
            table.put("HierarchicalTableUI", aquaPackageName + "AquaHierarchicalTableUI");
            table.put("CellStyleTableHeaderUI", aquaPackageName + "AquaCellStyleTableHeaderUI");
            table.put("SortableTableHeaderUI", aquaPackageName + "AquaSortableTableHeaderUI");
            table.put("NestedTableHeaderUI", aquaPackageName + "AquaNestedTableHeaderUI");
            table.put("EditableTableHeaderUI", aquaPackageName + "AquaEditableTableHeaderUI");
            table.put("AutoFilterTableHeaderUI", aquaPackageName + "AquaAutoFilterTableHeaderUI");
            table.put("GroupTableHeaderUI", aquaPackageName + "AquaGroupTableHeaderUI");
            table.put("ExComboBoxUI", aquaPackageName + "AquaExComboBoxUI");
        }

        if ((products & PRODUCT_DOCK) != 0) {
            table.put("SidePaneUI", aquaPackageName + "AquaSidePaneUI");
            table.put("DockableFrameUI", aquaPackageName + "AquaDockableFrameUI");
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

        Object defaultBackgroundColor = UIDefaultsLookup.get("Panel.background"); // AquaImageFactory.getWindowBackgroundColorUIResource();

        Object defaultLightColor = UIDefaultsLookup.get("controlHighlight");
        Object defaultHighlightColor = UIDefaultsLookup.get("controlLtHighlight");
        Object defaultShadowColor = UIDefaultsLookup.get("controlShadow");
        Object defaultDarkShadowColor = UIDefaultsLookup.get("controlDkShadow");

        Object mdiBackgroundColor = UIDefaultsLookup.get("Panel.background"); // AquaImageFactory.getWindowBackgroundColorUIResource();

        Object controlFont = UIDefaultsLookup.get("Button.font"); // new UIDefaults.ProxyLazyValue("apple.laf.AquaLookAndFeel", "getControlTextFont");

        Object controlSmallFont = UIDefaultsLookup.get("TabbedPane.smallFont"); // new UIDefaults.ProxyLazyValue("apple.laf.AquaLookAndFeel", "getControlTextSmallFont");

        Object boldFont = UIDefaultsLookup.get("Button.font"); // new UIDefaults.ProxyLazyValue("apple.laf.AquaLookAndFeel", "getControlTextFont");

        Object resizeBorder = BorderFactory.createLineBorder(new Color(230, 230, 230), 2);

        Object defaultFormBackground = new ExtWindowsDesktopProperty(// Not exactly right
                new String[]{"win.3d.shadowColor"}, new Object[]{UIDefaultsLookup.get("control")}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return obj[0];
            }
        });

        Object inactiveTabForeground = new ExtWindowsDesktopProperty(// Not exactly right
                new String[]{"win.3d.shadowColor"}, new Object[]{UIDefaultsLookup.get("controlShadow")}, toolkit, new ConvertListener() {
            public Object convert(Object[] obj) {
                return ((Color) obj[0]).darker();
            }
        });

        Object focusedButtonColor = UIDefaultsLookup.get("Menu.selectionBackground"); // AquaImageFactory.getMenuSelectionBackgroundColorUIResource();

        Object selectedAndFocusedButtonColor = UIDefaultsLookup.get("Menu.selectionBackground"); // AquaImageFactory.getMenuSelectionBackgroundColorUIResource();

        Object selectedButtonColor = UIDefaultsLookup.get("Menu.selectionBackground"); // AquaImageFactory.getMenuSelectionBackgroundColorUIResource();

        Object selectionBackgroundColor = UIDefaultsLookup.get("TextField.selectionBackground"); // AquaImageFactory.getTextSelectionBackgroundColorUIResource();

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
                "JideButton.light", defaultLightColor,
                "JideButton.highlight", defaultHighlightColor,

                "JideButton.border", buttonBorder,
                "JideButton.margin", new InsetsUIResource(3, 3, 3, 3),
                "JideButton.textIconGap", 4,
                "JideButton.textShiftOffset", 0,
                "JideButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
                "SPACE", "pressed",
                "released SPACE", "released",
                "ENTER", "pressed",
                "released ENTER", "released"
        }),

                "TristateCheckBox.icon", null,
                "TristateCheckBox.setMixed.clientProperty", new Object[]{"JButton.selectedState", "indeterminate"},
                "TristateCheckBox.clearMixed.clientProperty", new Object[]{"JButton.selectedState", null},

                "JideSplitPane.dividerSize", 3,
                "JideSplitPaneDivider.border", new BorderUIResource(BorderFactory.createEmptyBorder()),
                "JideSplitPaneDivider.background", defaultBackgroundColor,

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
                "JideTabbedPane.closeButtonLeftMargin", 2,
                "JideTabbedPane.closeButtonRightMargin", 2,

                "JideTabbedPane.defaultTabBorderShadowColor", new ColorUIResource(115, 109, 99),

                "JideTabbedPane.border", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),
                "JideTabbedPane.background", defaultFormBackground,
                "JideTabbedPane.foreground", defaultTextColor,
                "JideTabbedPane.light", defaultLightColor,
                "JideTabbedPane.highlight", defaultHighlightColor,
                "JideTabbedPane.shadow", defaultShadowColor,
                "JideTabbedPane.tabInsets", new InsetsUIResource(1, 4, 1, 4),
                "JideTabbedPane.contentBorderInsets", new InsetsUIResource(2, 2, 2, 2),
                "JideTabbedPane.ignoreContentBorderInsetsIfNoTabs", Boolean.FALSE,
                "JideTabbedPane.tabAreaInsets", new InsetsUIResource(2, 4, 0, 4),
                "JideTabbedPane.tabAreaBackground", defaultFormBackground,
                "JideTabbedPane.tabAreaBackgroundLt", defaultHighlightColor,
                "JideTabbedPane.tabAreaBackgroundDk", defaultBackgroundColor,
                "JideTabbedPane.tabRunOverlay", 2,
                "JideTabbedPane.font", controlSmallFont,
                "JideTabbedPane.selectedTabFont", controlSmallFont,
                "JideTabbedPane.darkShadow", defaultTextColor,
                "JideTabbedPane.selectedTabTextForeground", defaultTextColor,
                "JideTabbedPane.unselectedTabTextForeground", inactiveTabForeground,
                "JideTabbedPane.selectedTabBackground", defaultBackgroundColor,
                "JideTabbedPane.selectedTabBackgroundLt", new ColorUIResource(230, 139, 44),
                "JideTabbedPane.selectedTabBackgroundDk", new ColorUIResource(255, 199, 60),
                "JideTabbedPane.tabListBackground", UIDefaultsLookup.getColor("List.background"),
                "JideTabbedPane.textIconGap", 4,
                "JideTabbedPane.showIconOnTab", Boolean.TRUE,
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

                "Resizable.resizeBorder", resizeBorder,

                "MeterProgressBar.border", new BorderUIResource(BorderFactory.createLineBorder(Color.BLACK)),
                "MeterProgressBar.background", new ColorUIResource(Color.BLACK),
                "MeterProgressBar.foreground", new ColorUIResource(Color.GREEN),
                "MeterProgressBar.cellForeground", new ColorUIResource(Color.GREEN),
                "MeterProgressBar.cellBackground", new ColorUIResource(0x008000),
                "MeterProgressBar.cellLength", 2,
                "MeterProgressBar.cellSpacing", 2,

                "ButtonPanel.order", "CA",
                "ButtonPanel.oppositeOrder", "HO",
                "ButtonPanel.buttonGap", 6,
                "ButtonPanel.groupGap", 12,
                "ButtonPanel.minButtonWidth", 69,

                "Contour.color", new ColorUIResource(136, 136, 136),
                "Contour.thickness", 4,

                "Gripper.size", 8,
                "Gripper.foreground", defaultShadowColor,

                "Icon.floating", Boolean.FALSE,

                "JideScrollPane.border", table.getBorder("ScrollPane.border"),

                "JideSplitButton.font", controlFont,
                "JideSplitButton.background", defaultBackgroundColor,
                "JideSplitButton.foreground", defaultTextColor,
                "JideSplitButton.margin", new InsetsUIResource(3, 3, 3, 7),
                "JideSplitButton.border", buttonBorder,
                "JideSplitButton.borderPainted", Boolean.FALSE,
                "JideSplitButton.textIconGap", 4,
                "JideSplitButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
                "SPACE", "pressed",
                "released SPACE", "released",
                "ENTER", "pressed",
                "released ENTER", "released",
                "DOWN", "downPressed",
                "released DOWN", "downReleased"
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
        };
        table.putDefaults(uiDefaults);

        int products = LookAndFeelFactory.getProductsUsed();

        if ((products & PRODUCT_DOCK) != 0) {
            Color slidingBorderColor = new Color(190, 190, 190);
            Object slidingEastFrameBorder = BorderFactory.createLineBorder(slidingBorderColor);
            Object slidingWestFrameBorder = BorderFactory.createLineBorder(slidingBorderColor);
            Object slidingNorthFrameBorder = BorderFactory.createLineBorder(slidingBorderColor);
            Object slidingSouthFrameBorder = BorderFactory.createLineBorder(slidingBorderColor);

            uiDefaults = new Object[]{
                    "DockableFrame.usingMacStandardIcons", Boolean.TRUE,

                    "DockableFrame.defaultIcon", JideIconsFactory.getImageIcon(JideIconsFactory.DockableFrame.BLANK),
                    "DockableFrame.background", defaultBackgroundColor,
                    "DockableFrame.border", new BorderUIResource(BorderFactory.createLineBorder(Color.lightGray, 1)),
                    "DockableFrame.floatingBorder", new BorderUIResource(BorderFactory.createLineBorder(Color.lightGray, 1)),
                    "DockableFrame.slidingEastBorder", slidingEastFrameBorder,
                    "DockableFrame.slidingWestBorder", slidingWestFrameBorder,
                    "DockableFrame.slidingNorthBorder", slidingNorthFrameBorder,
                    "DockableFrame.slidingSouthBorder", slidingSouthFrameBorder,

                    "DockableFrame.activeTitleBackground", UIDefaultsLookup.getColor("InternalFrame.activeTitleBackground"),
                    "DockableFrame.activeTitleForeground", UIDefaultsLookup.getColor("InternalFrame.activeTitleForeground"),
                    "DockableFrame.inactiveTitleBackground", UIDefaultsLookup.getColor("InternalFrame.inactiveTitleBackground"),
                    "DockableFrame.inactiveTitleForeground", UIDefaultsLookup.getColor("InternalFrame.inactiveTitleForeground"),
                    "DockableFrame.titleBorder", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),
                    "DockableFrame.activeTitleBorderColor", UIDefaultsLookup.getColor("InternalFrame.activeTitleBackground"),
                    "DockableFrame.inactiveTitleBorderColor", defaultShadowColor,
                    "DockableFrame.font", controlFont,

                    "DockableFrameTitlePane.font", controlSmallFont,
                    "DockableFrameTitlePane.titleBarComponent", Boolean.FALSE,

                    "DockableFrameTitlePane.alwaysShowAllButtons", Boolean.TRUE, // true if show all three buttons no matter if the buttons is available. false if only show buttons which is available
                    "DockableFrameTitlePane.buttonsAlignment", SwingConstants.LEADING, // trailing or leading
                    "DockableFrameTitlePane.titleAlignment", SwingConstants.CENTER, // trailing or leading or center
                    "DockableFrameTitlePane.buttonGap", 3, // gap between buttons
                    "DockableFrameTitlePane.showIcon", Boolean.FALSE, // show icon or not, the alignment is the same as titleAlignment
                    "DockableFrameTitlePane.margin", new InsetsUIResource(0, 10, 0, 3), // gap

                    "SidePane.margin", new InsetsUIResource(1, 1, 1, 1),
                    "SidePane.iconTextGap", 2,
                    "SidePane.textBorderGap", 13,
                    "SidePane.itemGap", 5,
                    "SidePane.groupGap", 8,
                    "SidePane.foreground", defaultDarkShadowColor,
                    "SidePane.background", defaultFormBackground,
                    "SidePane.lineColor", new Color(151, 151, 151),
                    "SidePane.buttonBackground", new Color(133, 133, 133),
                    "SidePane.selectedButtonBackground", new Color(133, 133, 133),
                    "SidePane.selectedButtonForeground", defaultTextColor,
                    "SidePane.font", controlSmallFont,
                    "SidePane.orientation", 1,
                    "SidePane.showSelectedTabText", Boolean.TRUE,
                    "SidePane.alwaysShowTabText", Boolean.TRUE,

                    "Workspace.background", mdiBackgroundColor,

                    "DockingFramework.changeCursor", Boolean.FALSE,

                    "FrameContainer.contentBorderInsets", new InsetsUIResource(0, 0, 0, 0),

                    "ContentContainer.background", defaultFormBackground,
                    "ContentContainer.vgap", 1,
                    "ContentContainer.hgap", 1,
                    "MainContainer.border", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),
            };
            table.putDefaults(uiDefaults);
        }

        if ((products & PRODUCT_COMPONENTS) != 0) {
            ImageIcon collapsiblePaneImage = IconsFactory.getImageIcon(AquaJideUtils.class, "icons/collapsible_pane_aqua.gif"); // 12 x 12 x 2
            final int collapsiblePaneSize = 12;

            uiDefaults = new Object[]{
                    "CollapsiblePanes.border", new BorderUIResource(BorderFactory.createEmptyBorder(12, 12, 12, 12)),
                    "CollapsiblePanes.gap", 15,

                    "CollapsiblePane.background", UIDefaultsLookup.getColor("InternalFrame.inactiveTitleBackground"),
                    "CollapsiblePane.contentBackground", defaultHighlightColor,
                    "CollapsiblePane.foreground", UIDefaultsLookup.getColor("InternalFrame.activeTitleForeground"),
                    "CollapsiblePane.emphasizedBackground", UIDefaultsLookup.getColor("InternalFrame.activeTitleBackground"),
                    "CollapsiblePane.emphasizedForeground", UIDefaultsLookup.getColor("InternalFrame.activeTitleForeground"),
                    "CollapsiblePane.border", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)),
                    "CollapsiblePane.font", controlFont,

                    "CollapsiblePane.contentBorder", new BorderUIResource(BorderFactory.createEmptyBorder(8, 10, 8, 10)),

                    "CollapsiblePane.titleBorder", new BorderUIResource(BorderFactory.createEmptyBorder(3, 3, 3, 3)),
                    "CollapsiblePane.titleFont", boldFont,
                    "CollapsiblePane.downIcon", IconsFactory.getIcon(null, collapsiblePaneImage, 0, 0, collapsiblePaneSize, collapsiblePaneSize),
                    "CollapsiblePane.upIcon", IconsFactory.getIcon(null, collapsiblePaneImage, 0, collapsiblePaneSize, collapsiblePaneSize, collapsiblePaneSize),

                    "StatusBarItem.border", new BorderUIResource(BorderFactory.createLineBorder(UIDefaultsLookup.getColor("controlShadow"), 1)),

                    "StatusBar.border", new BorderUIResource(BorderFactory.createEmptyBorder(2, 0, 0, 0)),
                    "StatusBar.gap", 2,
                    "StatusBar.background", defaultBackgroundColor,
                    "StatusBar.font", controlFont,
                    "StatusBar.paintResizableIcon", Boolean.TRUE,
                    "MemoryStatusBarItem.fillColor", new ColorUIResource(236, 233, 176),

                    "DocumentPane.groupBorder", new BorderUIResource(BorderFactory.createLineBorder(Color.gray)),
                    "DocumentPane.newHorizontalGroupIcon", JideIconsFactory.getImageIcon(JideIconsFactory.WindowMenu.NEW_HORIZONTAL_TAB),
                    "DocumentPane.newVerticalGroupIcon", JideIconsFactory.getImageIcon(JideIconsFactory.WindowMenu.NEW_VERTICAL_TAB),
                    "DocumentPane.boldActiveTab", Boolean.TRUE,

                    "OutlookTabbedPane.buttonStyle", JideButton.TOOLBAR_STYLE,
                    "FloorTabbedPane.buttonStyle", JideButton.TOOLBAR_STYLE,
            };
            table.putDefaults(uiDefaults);
        }
        if ((products & PRODUCT_ACTION) != 0) {
            uiDefaults = new Object[]{


                    "CommandBar.font", controlFont,
                    "CommandBar.background", defaultBackgroundColor,
                    "CommandBar.foreground", defaultTextColor,
                    "CommandBar.shadow", defaultShadowColor,
                    "CommandBar.darkShadow", defaultDarkShadowColor,
                    "CommandBar.light", defaultLightColor,
                    "CommandBar.highlight", defaultHighlightColor,
                    "CommandBar.border", new BorderUIResource(BorderFactory.createEmptyBorder(1, 1, 1, 1)),
                    "CommandBar.borderVert", new BorderUIResource(BorderFactory.createEmptyBorder(1, 1, 1, 1)),
                    "CommandBar.borderFloating", new BorderUIResource(BorderFactory.createEmptyBorder(2, 2, 2, 2)),
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
                    "CommandBar.titleBarBackground", UIDefaultsLookup.getColor("InternalFrame.activeTitleBackground"),
                    "CommandBar.titleBarForeground", defaultTextColor,
                    "CommandBar.titleBarFont", boldFont,
                    "CommandBar.minimumSize", new DimensionUIResource(20, 20),

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
                    "AbstractComboBox.useJButton", Boolean.FALSE,

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

        UIDefaultsLookup.put(table, "Theme.painter", AquaPainter.getInstance());
    }

    public static boolean isGraphite() {
        String appleAquaColorVariant = AquaPreferences.getString("AppleAquaColorVariant");
        return "6".equals(appleAquaColorVariant);
    }

    // HALF buttons have to colours
    // lighter upper half
    // darker  lower half
    public static final Color[] HALF_LIGHT = {
            new Color(251, 251, 251),
            new Color(237, 237, 237)
    };

    public static final Color[] HALF_DARK = {
            new Color(133, 133, 133),
            new Color(125, 125, 125)
    };

    // AQUA gradients consist of two gradients for each half
    // light upper half
    // dark  upper half
    // light lower half
    // dark  lower half
    public static final Color[] AQUA_WHITE = {
            new Color(252, 252, 252),
            new Color(236, 236, 236),
            new Color(225, 225, 225),
            new Color(255, 255, 255)
    };

    public static final Color[] AQUA_BLUE = {
            new Color(221, 225, 244),
            new Color(139, 187, 238),
            new Color(100, 168, 242),
            new Color(187, 255, 255)
    };

    public static final Color[] AQUA_GRAPHITE = {
            new Color(231, 233, 235),
            new Color(182, 188, 198),
            new Color(158, 158, 180),
            new Color(231, 241, 255)
    };


    public static final Color[] AQUA_BANNER_WHITE = {
            new Color(255, 255, 255),
            new Color(248, 248, 248),
            new Color(228, 228, 228),
            new Color(239, 239, 239)
    };

    public static final Color[] AQUA_BANNER_BLUE = {
            new Color(103, 159, 254),
            new Color(73, 132, 253),
            new Color(51, 132, 253),
            new Color(84, 170, 254)
    };

    public static Color[] reverse(Color[] colors) {
        Color[] reverse = new Color[colors.length];
        for (int i = 0; i < colors.length; i++) {
            reverse[i] = colors[colors.length - i - 1];
        }
        return reverse;
    }

    public static void fillAquaGradientHorizontal(Graphics g, final Shape shape, final Color[] colors) {
        Color[] c = colors;
        if (c == null || c.length != 4) {
            c = AQUA_WHITE;
        }

        Graphics2D g2d = (Graphics2D) g;
// cause icon and text not showing when close button is on tab.
//        Shape oldClipShape = g2d.getClip();
//        g2d.setClip(shape);
        Rectangle rect = shape.getBounds();
        Rectangle r2 = new Rectangle(rect.x, rect.y + rect.height / 2, rect.width, rect.height / 2);
        Rectangle r1 = new Rectangle(rect.x, rect.y, rect.width, rect.height / 2);
        JideSwingUtilities.fillGradient(g2d, r1, c[0], c[1], true);
        JideSwingUtilities.fillGradient(g2d, r2, c[2], c[3], true);
//        g2d.setClip(oldClipShape);
    }

    public static void fillAquaGradientVertical(Graphics g, final Shape shape, final Color[] colors) {
        Color[] c = colors;
        if (c == null || c.length != 4) {
            c = AQUA_WHITE;
        }

        Graphics2D g2d = (Graphics2D) g;
//        Shape oldClipShape = g2d.getClip();
//        g2d.setClip(shape);
        Rectangle rect = shape.getBounds();
        Rectangle r2 = new Rectangle(rect.x + rect.width / 2, rect.y, rect.width / 2, rect.height);
        Rectangle r1 = new Rectangle(rect.x, rect.y, rect.width / 2, rect.height);
        JideSwingUtilities.fillGradient(g2d, r1, c[0], c[1], false);
        JideSwingUtilities.fillGradient(g2d, r2, c[2], c[3], false);
//        g.setClip(oldClipShape);
    }

    public static void fillSquareButtonHorizontal(Graphics g, Shape shape, final Color[] colors) {
        Color[] c = colors;
        if (c == null || c.length != 2) {
            c = HALF_LIGHT;
        }

        Graphics2D g2d = (Graphics2D) g;
//        Shape oldClipShape = g2d.getClip();
//        g2d.setClip(shape);
        Rectangle rect = shape.getBounds();
        g2d.setColor(c[0]);
        g2d.fillRect(rect.x, rect.y, rect.width, rect.height / 2);
        g2d.setColor(c[1]);
        g2d.fillRect(rect.x, rect.y + rect.height / 2, rect.width, rect.height / 2);

//        g2d.setClip(oldClipShape);
    }

    public static void fillSquareButtonVertical(Graphics g, Shape shape, final Color[] colors) {
        Color[] c = colors;
        if (c == null || c.length != 2) {
            c = HALF_LIGHT;
        }

        Graphics2D g2d = (Graphics2D) g;
//        Shape oldClipShape = g2d.getClip();
//        g2d.setClip(shape);
        Rectangle rect = shape.getBounds();
        g.setColor(c[0]);
        g.fillRect(rect.x, rect.y, rect.width / 2, rect.height);
        g.setColor(c[1]);
        g.fillRect(rect.x + rect.width / 2, rect.y, rect.width / 2, rect.height);
//        g2d.setClip(oldClipShape);
    }


    public static void antialiasShape(Graphics g, boolean onoff) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                onoff ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

    }

    public static void antialiasText(Graphics g, boolean onoff) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                onoff ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

    }

}
