/*
 * @(#)BasicOffice2003Theme.java 3/17/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.office2003;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.utils.ColorUtils;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * <tt>BasicOffice2003Theme</tt> is a special Office2003 theme that uses a base color to calculate
 * all other colors used by JIDE components. This is done regardless what current XP theme you are using.
 */
public class BasicOffice2003Theme extends Office2003Theme {
    private Color _baseColor;

    public BasicOffice2003Theme(String themeName) {
        super(themeName);
    }

    /**
     * Sets the base color. The Office2003 theme will use this color as base color and calculate
     * all other colors that will be used by JIDE components.
     *
     * @param color                 the base color.
     * @param derivedSelectionColor if deriving selection colors from the base color. Selection color is used in
     *                              places like JideButton's background and DockableFrame's title pane background.
     * @param prefix                the prefix is for the expand/collapse icon on collapsible pane. Available prefixes are
     *                              "default", "blue", "homestead", "metallic", and "gray". You can decide which one to use depending
     *                              on what base color is. In most cases, "default" works just fine. But if your base color has more blue,
     *                              using "blue" will produce a better result. If green, you can use "homestead".
     */
    public void setBaseColor(Color color, boolean derivedSelectionColor, String prefix) {
        _baseColor = color;
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        Color selectionColor = Color.getHSBColor(hsb[0], (hsb[1] > 0.01) ? 0.45f : hsb[0], 0.90f);

        Object uiDefaults[] = {
                "control", ColorUtils.getDerivedColor(color, 0.9f),
                "controlLt", ColorUtils.getDerivedColor(color, 0.95f),
                "controlDk", ColorUtils.getDerivedColor(color, 0.8f),
                "controlShadow", ColorUtils.getDerivedColor(color, 0.4f),

                "TabbedPane.selectDk", ColorUtils.getDerivedColor(selectionColor, 0.40f),
                "TabbedPane.selectLt", ColorUtils.getDerivedColor(selectionColor, 0.55f),

                "OptionPane.bannerLt", ColorUtils.getDerivedColor(color, 0.5f),
                "OptionPane.bannerDk", ColorUtils.getDerivedColor(color, 0.3f),
                "OptionPane.bannerForeground", new ColorUIResource(255, 255, 255),

                "Separator.foreground", ColorUtils.getDerivedColor(color, 0.4f),
                "Separator.foregroundLt", ColorUtils.getDerivedColor(color, 1.0f),

                "Gripper.foreground", ColorUtils.getDerivedColor(color, 0.6f),
                "Gripper.foregroundLt", ColorUtils.getDerivedColor(color, 0.92f),

                "Chevron.backgroundLt", ColorUtils.getDerivedColor(color, 0.85f),
                "Chevron.backgroundDk", ColorUtils.getDerivedColor(color, 0.75f),

                "Divider.backgroundLt", ColorUtils.getDerivedColor(color, 0.9f),
                "Divider.backgroundDk", ColorUtils.getDerivedColor(color, 0.97f),

                "backgroundLt", ColorUtils.getDerivedColor(color, 0.95f),
                "backgroundDk", ColorUtils.getDerivedColor(color, 0.9f),

                "CommandBar.titleBarBackground", ColorUtils.getDerivedColor(color, 0.6f),
                "MenuItem.background", ColorUtils.getDerivedColor(color, 0.95f),

                "DockableFrameTitlePane.backgroundLt", ColorUtils.getDerivedColor(color, 0.92f),
                "DockableFrameTitlePane.backgroundDk", ColorUtils.getDerivedColor(color, 0.85f),
                "DockableFrameTitlePane.activeForeground", new ColorUIResource(0, 0, 0),
                "DockableFrameTitlePane.inactiveForeground", new ColorUIResource(0, 0, 0),
                "DockableFrame.backgroundLt", ColorUtils.getDerivedColor(color, 0.92f),
                "DockableFrame.backgroundDk", ColorUtils.getDerivedColor(color, 0.89f),

                "selection.border", ColorUtils.getDerivedColor(color, 0.5f)
        };

        putDefaults(uiDefaults);

        int products = LookAndFeelFactory.getProductsUsed();
        if ((products & LookAndFeelFactory.PRODUCT_COMPONENTS) != 0) {
            final int SIZE = 20;
            final int MASK_SIZE = 11;
            ImageIcon collapsiblePaneImage = IconsFactory.getImageIcon(Office2003WindowsUtils.class, "icons/collapsible_pane_" + prefix + ".png"); // 20 x 20
            ImageIcon collapsiblePaneMask = IconsFactory.getImageIcon(Office2003WindowsUtils.class, "icons/collapsible_pane_mask.png"); // 11 x 11
            ImageIcon normalIcon = IconsFactory.getIcon(null, collapsiblePaneImage, 0, 0, SIZE, SIZE);
            ImageIcon emphasizedIcon = IconsFactory.getIcon(null, collapsiblePaneImage, SIZE, 0, SIZE, SIZE);
            ImageIcon downMark = IconsFactory.getIcon(null, collapsiblePaneMask, 0, 0, MASK_SIZE, MASK_SIZE);
            ImageIcon upMark = IconsFactory.getIcon(null, collapsiblePaneMask, 0, MASK_SIZE, MASK_SIZE, MASK_SIZE);
            uiDefaults = new Object[]{
                    "CollapsiblePane.contentBackground", ColorUtils.getDerivedColor(color, 0.98f),
                    "CollapsiblePanes.backgroundLt", ColorUtils.getDerivedColor(color, 0.82f),
                    "CollapsiblePanes.backgroundDk", ColorUtils.getDerivedColor(color, 0.78f),
                    "CollapsiblePaneTitlePane.backgroundLt", ColorUtils.getDerivedColor(color, 0.98f),
                    "CollapsiblePaneTitlePane.backgroundDk", ColorUtils.getDerivedColor(color, 0.93f),
                    "CollapsiblePaneTitlePane.foreground", new ColorUIResource(63, 61, 61),
                    "CollapsiblePaneTitlePane.foreground.focus", new ColorUIResource(126, 124, 124),
                    "CollapsiblePaneTitlePane.backgroundLt.emphasized", ColorUtils.getDerivedColor(color, 0.7f),
                    "CollapsiblePaneTitlePane.backgroundDk.emphasized", ColorUtils.getDerivedColor(color, 0.72f),
                    "CollapsiblePaneTitlePane.foreground.emphasized", new ColorUIResource(255, 255, 255),
                    "CollapsiblePaneTitlePane.foreground.focus.emphasized", new ColorUIResource(230, 230, 230),

                    "CollapsiblePane.downIcon", IconsFactory.getOverlayIcon(null, normalIcon, downMark, SwingConstants.CENTER),
                    "CollapsiblePane.upIcon", IconsFactory.getOverlayIcon(null, normalIcon, upMark, SwingConstants.CENTER),
                    "CollapsiblePane.downIcon.emphasized", IconsFactory.getOverlayIcon(null, emphasizedIcon, downMark, SwingConstants.CENTER),
                    "CollapsiblePane.upIcon.emphasized", IconsFactory.getOverlayIcon(null, emphasizedIcon, upMark, SwingConstants.CENTER),
                    "CollapsiblePane.upMask", upMark,
                    "CollapsiblePane.downMask", downMark,
                    "CollapsiblePane.titleButtonBackground", normalIcon,
                    "CollapsiblePane.titleButtonBackground.emphasized", emphasizedIcon,
            };
            putDefaults(uiDefaults);
        }

        if (derivedSelectionColor) {
            Object[] uiDefaultsSelection = new Object[]{
                    "selection.Rollover", selectionColor,
                    "selection.RolloverLt", ColorUtils.getDerivedColor(selectionColor, 0.55f),
                    "selection.RolloverDk", ColorUtils.getDerivedColor(selectionColor, 0.45f),

                    "selection.Selected", ColorUtils.getDerivedColor(selectionColor, 0.45f),
                    "selection.SelectedLt", ColorUtils.getDerivedColor(selectionColor, 0.55f),
                    "selection.SelectedDk", ColorUtils.getDerivedColor(selectionColor, 0.50f),

                    "selection.Pressed", ColorUtils.getDerivedColor(selectionColor, 0.4f), // focused and selected;
                    "selection.PressedLt", ColorUtils.getDerivedColor(selectionColor, 0.45f),
                    "selection.PressedDk", ColorUtils.getDerivedColor(selectionColor, 0.35f)
            };
            putDefaults(uiDefaultsSelection);
        }

    }

    /**
     * Gets the base color for this theme. It is set using
     * {@link #setBaseColor(java.awt.Color,boolean,String)} method.
     *
     * @return the base color.
     */
    public Color getBaseColor() {
        return _baseColor;
    }
}
