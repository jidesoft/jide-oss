/*
 * @(#)BasicCheckBoxListUI.java 3/18/2009
 *
 * Copyright 2002 - 2009 JIDE Software Inc. All rights reserved.
 *
 */

package com.jidesoft.plaf.basic;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;
import java.awt.*;

public class BasicCheckBoxListUI extends BasicListUI {
    public BasicCheckBoxListUI() {
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new BasicCheckBoxListUI();
    }

    @Override
    public int locationToIndex(JList aList, Point location) {
        int index = super.locationToIndex(aList, location);
        int size = list.getModel().getSize();
        if (index < size - 1) {
            return index;
        }

        // To avoid the behavior that clicking on the blank area selects the last item in the list.
        int y = location.y;
        Insets insets = list.getInsets();
        int maxRow = size - 1; // it is only valid for JList.VERTICAL scenario, no good solution for other orientations
        int row;
        if (cellHeights == null) {
            row = (cellHeight == 0) ? 0 :
                           ((y - insets.top) / cellHeight);
        }
        else if (size > cellHeights.length) {
            return index;
        }
        else {
            int rowYOffset = insets.top;
            for (row = 0; row < size; row++) {
                if (y >= rowYOffset && y < rowYOffset + cellHeights[row]) {
                    break;
                }
                rowYOffset += cellHeights[row];
            }
        }
        if (row > maxRow) {
            return -1;
        }
        else {
            return index;
        }
    }
}