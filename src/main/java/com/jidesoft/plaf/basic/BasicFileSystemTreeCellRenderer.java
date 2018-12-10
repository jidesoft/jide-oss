/*
 * @(#)FileTreeCellRenderer.java 9/12/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.basic;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

class BasicFileSystemTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof BasicFileSystemTreeNode) {
            BasicFileSystemTreeNode fileTreeNode = (BasicFileSystemTreeNode) value;
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, fileTreeNode.getName(), sel, expanded, leaf, row, hasFocus);
            try {
                label.setIcon(fileTreeNode.getIcon());
            }
            catch (Exception e) {
                System.out.println(fileTreeNode.getFile().getAbsolutePath());
            }
            return label;
        }
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }
}
