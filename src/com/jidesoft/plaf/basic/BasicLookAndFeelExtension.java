/*
 * @(#)BasicLookAndFeelExtension.java 4/15/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.LookAndFeelExtension;
import com.jidesoft.plaf.LookAndFeelFactory;

import javax.swing.*;

/**
 * Initialize the uiClassID to BasicComponentUI mapping for JIDE components. The JComponent classes define their own
 * uiClassID constants (see AbstractComponent.getUIClassID).  This table must map those constants to a BasicComponentUI
 * class of the appropriate type.
 */
public class BasicLookAndFeelExtension implements LookAndFeelExtension {

    /**
     * Initializes class defaults.
     *
     * @param table UIDefaults table
     */
    public static void initClassDefaults(UIDefaults table) {
        int products = LookAndFeelFactory.getProductsUsed();

        final String basicPackageName = "com.jidesoft.plaf.basic.";

        // common
        table.put("JidePopupMenuUI", basicPackageName + "BasicJidePopupMenuUI");
        table.put("HeaderBoxUI", basicPackageName + "BasicHeaderBoxUI");
        table.put("RangeSliderUI", basicPackageName + "BasicRangeSliderUI");
        table.put("FolderChooserUI", basicPackageName + "BasicFolderChooserUI");
        table.put("StyledLabelUI", basicPackageName + "BasicStyledLabelUI");
        table.put("GripperUI", basicPackageName + "BasicGripperUI");
        table.put("JidePopupUI", basicPackageName + "BasicJidePopupUI");
        table.put("JideTabbedPaneUI", basicPackageName + "BasicJideTabbedPaneUI");
        table.put("JideLabelUI", basicPackageName + "BasicJideLabelUI");
        table.put("JideButtonUI", basicPackageName + "BasicJideButtonUI");
        table.put("JideSplitButtonUI", basicPackageName + "BasicJideSplitButtonUI");
        table.put("JideComboBoxUI", basicPackageName + "BasicJideComboBoxUI");
        table.put("MeterProgressBarUI", basicPackageName + "MeterProgressBarUI");
        table.put("CheckBoxListUI", basicPackageName + "BasicCheckBoxListUI");

        if ((products & PRODUCT_GRIDS) != 0) {
            // grids
            table.put("JideTableUI", basicPackageName + "BasicJideTableUI");
            table.put("NavigableTableUI", basicPackageName + "BasicNavigableTableUI");
            table.put("CellSpanTableUI", basicPackageName + "BasicCellSpanTableUI");
            table.put("TreeTableUI", basicPackageName + "BasicTreeTableUI");
            table.put("HierarchicalTableUI", basicPackageName + "BasicHierarchicalTableUI");
            table.put("NestedTableHeaderUI", basicPackageName + "BasicNestedTableHeaderUI");
            table.put("EditableTableHeaderUI", basicPackageName + "BasicEditableTableHeaderUI");
            table.put("GroupListUI", basicPackageName + "BasicGroupListUI");
        }

        if ((products & PRODUCT_DOCK) != 0) {
            // dock
            table.put("SidePaneUI", basicPackageName + "BasicSidePaneUI");
            table.put("DockableFrameUI", basicPackageName + "BasicDockableFrameUI");
        }

        if ((products & PRODUCT_COMPONENTS) != 0) {
            // components
            table.put("CollapsiblePaneUI", basicPackageName + "BasicCollapsiblePaneUI");
            table.put("StatusBarSeparatorUI", basicPackageName + "BasicStatusBarSeparatorUI");
        }

        if ((products & PRODUCT_ACTION) != 0) {
            // action
            table.put("CommandBarUI", basicPackageName + "BasicCommandBarUI");
            table.put("CommandBarSeparatorUI", basicPackageName + "BasicCommandBarSeparatorUI");
            table.put("ChevronUI", basicPackageName + "BasicChevronUI");
            table.put("CommandBarTitleBarUI", basicPackageName + "BasicCommandBarTitleBarUI");
        }
    }
}
