/*
 * @(#)FileSystemTree.java 9/12/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.basic;

import com.jidesoft.swing.FolderChooser;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.TreeSearchable;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;

class BasicFileSystemTree extends JTree {
    public BasicFileSystemTree(FolderChooser folderChooser) {
        super(new BasicFileSystemTreeModel(folderChooser));
        initComponents();
    }

    protected void initComponents() {
        setCellRenderer(new BasicFileSystemTreeCellRenderer());
        setShowsRootHandles(false);
        setRootVisible(false);
        setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        setRowHeight(JideSwingUtilities.getLineHeight(this, 17));
        expandRow(0);
        FolderTreeListener treeListener = new FolderTreeListener();
        addTreeWillExpandListener(treeListener);
        addTreeExpansionListener(treeListener);
        new TreeSearchable(this) {
            @Override
            protected String convertElementToString(Object object) {
                if (object instanceof TreePath) {
                    Object treeNode = ((TreePath) object).getLastPathComponent();
                    if (treeNode instanceof BasicFileSystemTreeNode) {
                        return ((BasicFileSystemTreeNode) treeNode).getName();
                    }
                }
                return super.convertElementToString(object);
            }
        };
    }

    private class FolderTreeListener implements TreeWillExpandListener, TreeExpansionListener {
        private Cursor oldCursor;

        // ------------------------------------------------------------------------------------------
        // TreeWillExpandListener methods

        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            // change to busy cursor
            Window window = SwingUtilities.getWindowAncestor(BasicFileSystemTree.this);
            if (window != null) {
                oldCursor = window.getCursor();
                window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }
        }

        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        }

        // ------------------------------------------------------------------------------------------
        // TreeExpansionListener methods

        public void treeExpanded(TreeExpansionEvent event) {
            // change cursor back
            Window window = SwingUtilities.getWindowAncestor(BasicFileSystemTree.this);
            if (window != null) {
                window.setCursor(oldCursor != null ? oldCursor : Cursor.getDefaultCursor());
            }
            oldCursor = null;
        }

        public void treeCollapsed(TreeExpansionEvent event) {
        }

    }

    @Override
    public String getToolTipText(MouseEvent event) {
        TreePath path = getPathForLocation(event.getX(), event.getY());
        if (path != null && path.getLastPathComponent() instanceof BasicFileSystemTreeNode) {
            BasicFileSystemTreeNode node = (BasicFileSystemTreeNode) path.getLastPathComponent();
            String typeDescription = node.getTypeDescription();
            if (typeDescription == null || typeDescription.length() == 0) {
                return node.toString();
            }
            else {
                return node.toString() + " - " + typeDescription;
            }
        }
        else {
            return null;
        }
    }
}
