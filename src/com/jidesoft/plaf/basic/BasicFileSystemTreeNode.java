/*
 * @(#)FileTreeNode.java 10/9/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.basic;

import com.jidesoft.swing.FolderChooser;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class BasicFileSystemTreeNode extends LazyMutableTreeNode implements Comparable {
    private FolderChooser _folderChooser;
    private File _file;

    static HashMap _icons = new HashMap();

    static HashMap _nodes = new HashMap();

    protected BasicFileSystemTreeNode(File file) {
        this(file, null);
    }

    protected BasicFileSystemTreeNode(File file, FolderChooser folderChooser) {
        _file = file;
        _folderChooser = folderChooser;
    }

    @Override
    public boolean isLeaf() {
        if (!isLoaded()) {
            return false;
        }
        else {
            return super.isLeaf();
        }
    }

    public boolean hasChildren() {
        if (!_loaded) {
            if (BasicFolderChooserUI.isFileSystem(_file) && _file.isDirectory()) {
                File[] files = _folderChooser.getFileSystemView().getFiles(_file, _folderChooser.isFileHidingEnabled());
                for (File file : files) {
                    if (file.isDirectory()) {
                        return true;
                    }
                }
                _loaded = true; // no children so it is loaded
            }
            return false;
        }
        else {
            return getChildCount() != 0;
        }
    }

    @Override
    protected void initChildren() {
        if (_folderChooser == null) {
            return;
        }
        if (_file.isDirectory()) {
            File[] files = new File[0];
            try {
                files = _folderChooser.getFileSystemView().getFiles(_file, _folderChooser.isFileHidingEnabled());
            }
            catch (Error e) {
                // catch error like java.lang.InternalError: Unable to bind C:\blah blah\::{20D04FE0-3AEA-1069-A2D8-08002B30309D}\::{3D6BE802-FC0D-4595-A304-E611F97089DC} to parent
            }
            catch (Exception e) {
            }
            List children = new ArrayList();
            for (File file : files) {
                if (file.isDirectory()) {
                    BasicFileSystemTreeNode fileTreeNode = BasicFileSystemTreeNode.createFileSystemTreeNode(file, _folderChooser);
                    children.add(fileTreeNode);
                }
            }
            BasicFileSystemTreeNode[] results = (BasicFileSystemTreeNode[]) children.toArray(new BasicFileSystemTreeNode[children.size()]);
            Arrays.sort(results);
            for (BasicFileSystemTreeNode result : results) {
                add(result);
            }
        }
    }

    public File getFile() {
        return _file;
    }

    public String getName() {
        return getName(getFile());
    }

    public Icon getIcon() {
        Icon icon = (Icon) _icons.get(this);
        if (icon == null) {
            icon = getIcon(getFile());
            _icons.put(this, icon);
            return icon;
        }
        else {
            return icon;
        }
    }

    public String getTypeDescription() {
        String desc = getTypeDescription(getFile());
        return desc == null ? "" : desc;
    }

    public Icon getIcon(File file) {
        return _folderChooser.getFileSystemView().getSystemIcon(file);
    }

    public String getTypeDescription(File file) {
        return _folderChooser.getFileSystemView().getSystemTypeDescription(file);
    }

    public String getName(File file) {
        return _folderChooser.getFileSystemView().getSystemDisplayName(file);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final BasicFileSystemTreeNode that = (BasicFileSystemTreeNode) o;

        return !(_file != null ? !_file.equals(that._file) : that._file != null);

    }

    public int compareTo(Object o) {
        if (!(o instanceof BasicFileSystemTreeNode)) {
            return 0;
        }
        return getFile().compareTo(((BasicFileSystemTreeNode) o).getFile());
    }

    @Override
    public int hashCode() {
        return (_file != null ? _file.hashCode() : 0);
    }

    public boolean canEnqueue() {
        return !isLoaded()
                && !_folderChooser.getFileSystemView().isFloppyDrive(getFile())
                && !_folderChooser.getFileSystemView().isFileSystemRoot(getFile());
    }

    @Override
    public String toString() {
        return _file != null ? _file.toString() : "null";
    }

    /**
     * Caches the tree nodes ever created for the performance.
     *
     * @param file
     * @param folderChooser
     * @return tree node. If it is created before, returns the previous created instance.
     */
    public static BasicFileSystemTreeNode createFileSystemTreeNode(File file, FolderChooser folderChooser) {
        BasicFileSystemTreeNode node = (BasicFileSystemTreeNode) _nodes.get(file);
        if (node == null) {
            node = new BasicFileSystemTreeNode(file, folderChooser);
            _nodes.put(file, node);
        }
        return node;
    }

    /**
     * Clears the cache of all the tree nodes.
     */
    public static void clearCache() {
        _nodes.clear();
    }
}
