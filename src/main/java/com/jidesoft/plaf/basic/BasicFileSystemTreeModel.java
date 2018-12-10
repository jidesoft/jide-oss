/*
 * @(#)FileSystemTreeModel.java 9/12/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.basic;

import com.jidesoft.swing.FolderChooser;

import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

class BasicFileSystemTreeModel extends DefaultTreeModel {
    private FileSystemView _fileSystemView;

    public BasicFileSystemTreeModel(FolderChooser folderChooser) {
        super(new MyComputerTreeNode(folderChooser));
    }

    public FileSystemView getFileSystemView() {
        if (_fileSystemView == null) {
            _fileSystemView = FileSystemView.getFileSystemView();
        }
        return _fileSystemView;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof DefaultMutableTreeNode) {
            return ((DefaultMutableTreeNode) parent).getChildAt(index);
        }
        else {
            return null;
        }
    }

    public void removePath(TreePath path, int index, Object deletedObject) {
        TreePath parentPath = path.getParentPath();
        Object source = parentPath.getLastPathComponent();
        Object[] paths = parentPath.getPath();
        if (((LazyMutableTreeNode) source).isLoaded()) {
            ((DefaultMutableTreeNode) source).remove((MutableTreeNode) deletedObject);
        }
        fireTreeNodesRemoved(
                source,
                paths,
                new int[]{index},
                new Object[]{deletedObject});
    }

    public void addPath(TreePath parent, int insertionIndex, Object insertedObject) {
//        TreePath parentPath = parent;
//        Object source = parentPath.getLastPathComponent();
//        if (((LazyMutableTreeNode) source).isLoaded()) {
//            ((DefaultMutableTreeNode) source).insert((MutableTreeNode) insertedObject, insertionIndex);
//        }
        fireTreeNodesInserted(parent.getLastPathComponent(),
                parent.getPath(),
                new int[]{insertionIndex},
                new Object[]{insertedObject});
    }
}
