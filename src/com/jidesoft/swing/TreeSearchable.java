/*
 * @(#)TreeSearchable.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>TreeSearchable</code> is an concrete implementation of {@link Searchable}
 * that enables the search function in JTree.
 * <p>It's very simple to use it. Assuming you have a JTree, all you need to do is to
 * call
 * <code><pre>
 * JTree tree = ....;
 * TreeSearchable searchable = new TreeSearchable(tree);
 * </pre></code>
 * Now the JTree will have the search function.
 * <p/>
 * There is very little customization you need to do to TreeSearchable. The only thing you might
 * need is when the element in the JTree needs a special conversion to convert to string. If so, you can overide
 * convertElementToString() to provide you own algorithm to do the conversion.
 * <code><pre>
 * JTree tree = ....;
 * TreeSearchable searchable = new TreeSearchable(tree) {
 *      protected String convertElementToString(Object object) {
 *          ...
 *      }
 * };
 * </pre></code>
 * <p/>
 * Additional customization can be done on the base Searchable class such as background and foreground color, keystrokes,
 * case sensitivity,
 */
public class TreeSearchable extends Searchable implements TreeModelListener, PropertyChangeListener {

    private boolean _recursive = false;
    private transient List<TreePath> _treePathes;

    public TreeSearchable(JTree tree) {
        super(tree);
        if (tree.getModel() != null) {
            tree.getModel().addTreeModelListener(this);
        }

        tree.addPropertyChangeListener(JTree.TREE_MODEL_PROPERTY, this);
    }

    /**
     * Checks if the searchable is recursive.
     *
     * @return true if searchabe is recursive.
     */
    public boolean isRecursive() {
        return _recursive;
    }

    /**
     * Sets the recursive attribute.
     * <p/>
     * If TreeSearchable is recursive, it will all tree nodes including those which are not visible
     * to find the matching node. Obviously, if your tree has unlimited number of tree nodes
     * or a potential huge number of tree nodes (such as a tree to represent file system),
     * the recursive attribute should be false. To avoid this potential problem in this case, we default it to false.
     *
     * @param recursive
     */
    public void setRecursive(boolean recursive) {
        _recursive = recursive;
        resetTreePathes();
    }

    @Override
    public void uninstallListeners() {
        super.uninstallListeners();
        if (_component instanceof JTree) {
            if (((JTree) _component).getModel() != null) {
                ((JTree) _component).getModel().removeTreeModelListener(this);
            }
        }
        _component.removePropertyChangeListener(JTree.TREE_MODEL_PROPERTY, this);
    }

    @Override
    protected void setSelectedIndex(int index, boolean incremental) {
        if (!isRecursive()) {
            if (incremental) {
                ((JTree) _component).addSelectionInterval(index, index);
            }
            else {
                ((JTree) _component).setSelectionRow(index);
            }
            ((JTree) _component).scrollRowToVisible(index);
        }
        else {
            Object elementAt = getElementAt(index);
            if (elementAt instanceof TreePath) { // else case should never happen
                TreePath path = (TreePath) elementAt;
                if (incremental) {
                    ((JTree) _component).addSelectionPath(path);
                }
                else {
                    ((JTree) _component).setSelectionPath(path);
                }
                ((JTree) _component).scrollPathToVisible(path);
            }
        }
    }

    @Override
    protected int getSelectedIndex() {
        if (!isRecursive()) {
            int ai[] = ((JTree) _component).getSelectionRows();
            return (ai != null && ai.length != 0) ? ai[0] : -1;
        }
        else {
            TreePath[] treePaths = ((JTree) _component).getSelectionPaths();
            if (treePaths != null && treePaths.length > 0) {
                return getTreePathes().indexOf(treePaths[0]);
            }
            else
                return -1;
        }
    }

    @Override
    protected Object getElementAt(int index) {
        if (index == -1) {
            return null;
        }
        if (!isRecursive()) {
            return ((JTree) _component).getPathForRow(index);
        }
        else {
            return getTreePathes().get(index);
        }
    }

    @Override
    protected int getElementCount() {
        if (!isRecursive()) {
            return ((JTree) _component).getRowCount();
        }
        else {
            return getTreePathes().size();
        }
    }

    /**
     * @deprecated spell error. Use {@link #populateTreePaths()} instead.
     */
    protected void populateTreePathes() {
        populateTreePaths();
    }

    /**
     * Recursively go through the tree to populate the tree pathes into a list and cache them.
     * <p/>
     * Tree pathes list is only used when recursive attriubute is true.
     */
    protected void populateTreePaths() {
        _treePathes = new ArrayList<TreePath>();
        Object root = ((JTree) _component).getModel().getRoot();
        populateTreePaths0(root, new TreePath(root), ((JTree) _component).getModel());
    }

    private void populateTreePaths0(Object node, TreePath path, TreeModel model) {
        if (((JTree) _component).isRootVisible() || path.getLastPathComponent() != ((JTree) _component).getModel().getRoot()) {
            // if root not visible, do not add root
            _treePathes.add(path);
        }
        for (int i = 0; i < model.getChildCount(node); i++) {
            Object childNode = model.getChild(node, i);
            populateTreePaths0(childNode, path.pathByAddingChild(childNode), model);
        }
    }

    /**
     * Reset the cached tree pathes list.
     * <p/>
     * Tree pathes list is only used when recursive attriubute is true.
     */
    protected void resetTreePathes() {
        _treePathes = null;
    }

    /**
     * Gets the cached tree pathes list. If it has never been cached before, this method
     * will create the cache.
     * <p/>
     * Tree pathes list is only used when recursive attriubute is true.
     *
     * @return the tree pathes list.
     */
    protected List<TreePath> getTreePathes() {
        if (_treePathes == null) {
            populateTreePaths();
        }
        return _treePathes;
    }

    /**
     * Converts the element in JTree to string. The element by default is TreePath.
     * The returned value will be <code>toString()</code> of the last path component in the TreePath.
     *
     * @param object
     * @return the string representing the TreePath in the JTree.
     */
    @Override
    protected String convertElementToString(Object object) {
        if (object instanceof TreePath) {
            Object treeNode = ((TreePath) object).getLastPathComponent();
            return treeNode.toString();
        }
        else if (object != null) {
            return object.toString();
        }
        else {
            return "";
        }
    }

    public void treeNodesChanged(TreeModelEvent e) {
        hidePopup();
        resetTreePathes();
    }

    public void treeNodesInserted(TreeModelEvent e) {
        hidePopup();
        resetTreePathes();
    }

    public void treeNodesRemoved(TreeModelEvent e) {
        hidePopup();
        resetTreePathes();
    }

    public void treeStructureChanged(TreeModelEvent e) {
        hidePopup();
        resetTreePathes();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (JTree.TREE_MODEL_PROPERTY.equals(evt.getPropertyName())) {
            hidePopup();

            if (evt.getOldValue() instanceof TreeModel) {
                ((TreeModel) evt.getOldValue()).removeTreeModelListener(this);
            }

            if (evt.getNewValue() instanceof TreeModel) {
                ((TreeModel) evt.getNewValue()).addTreeModelListener(this);
            }

            resetTreePathes();
        }
    }
}
