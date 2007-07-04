/*
 * @(#)MyComputerTreeNode.java 8/19/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.basic;

import com.jidesoft.swing.FolderChooser;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.Arrays;

class MyComputerTreeNode extends LazyMutableTreeNode {
    private FolderChooser _folderChooser;

    public MyComputerTreeNode(FolderChooser folderChooser) {
        super(folderChooser.getFileSystemView());
        _folderChooser = folderChooser;
    }

    @Override
    protected void initChildren() {
        FileSystemView fsv = (FileSystemView) getUserObject();
        File[] roots = fsv.getRoots();
        if (roots != null) {
            Arrays.sort(roots);
            for (int i = 0, c = roots.length; i < c; i++) {
                BasicFileSystemTreeNode newChild = BasicFileSystemTreeNode.createFileSystemTreeNode(roots[i], _folderChooser);
                add(newChild);
            }
        }
    }

    @Override
    public String toString() {
        return "/";
    }
}
