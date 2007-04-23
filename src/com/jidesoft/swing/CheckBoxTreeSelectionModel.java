package com.jidesoft.swing;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

/**
 * <code>CheckBoxTreeSelectionModel</code> is a selection _model based on {@link DefaultTreeSelectionModel} and use
 * in {@link CheckBoxTree} to keep track of the checked tree paths.
 *
 * @author Santhosh Kumar T
 */
public class CheckBoxTreeSelectionModel extends DefaultTreeSelectionModel {
    private TreeModel _model;
    private boolean _digIn = true;
    private CheckBoxTree _tree;

    private boolean _singleEventMode = false;

    public CheckBoxTreeSelectionModel(TreeModel model) {
        _model = model;
        setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    }

    void setTree(CheckBoxTree tree) {
        _tree = tree;
    }

    public CheckBoxTreeSelectionModel(TreeModel model, boolean digIn) {
        _model = model;
        _digIn = digIn;
    }

    public TreeModel getModel() {
        return _model;
    }

    public void setModel(TreeModel model) {
        _model = model;
    }

    /**
     * Gets the dig-in mode. If the CheckBoxTree is in dig-in mode, checking the parent node
     * will check all the children. Correspondingly, getSelectionPaths() will only return the
     * parent tree path. If not in dig-in mode, each tree node can be checked or unchecked independently
     *
     * @return true or false.
     */
    public boolean isDigIn() {
        return _digIn;
    }

    /**
     * Sets the dig-in mode. If the CheckBoxTree is in dig-in mode, checking the parent node
     * will check all the children. Correspondingly, getSelectionPaths() will only return the
     * parent tree path. If not in dig-in mode, each tree node can be checked or unchecked independently
     *
     * @param digIn
     */
    public void setDigIn(boolean digIn) {
        _digIn = digIn;
    }

    /**
     * Tests whether there is any unselected node in the subtree of given path.
     */
    public boolean isPartiallySelected(TreePath path) {
        if (isPathSelected(path, true))
            return false;
        TreePath[] selectionPaths = getSelectionPaths();
        if (selectionPaths == null)
            return false;
        for (int j = 0; j < selectionPaths.length; j++) {
            if (isDescendant(selectionPaths[j], path))
                return true;
        }
        return false;
    }

    /**
     * Tells whether given path is selected. if dig is true,
     * then a path is assumed to be selected, if one of its ancestor is selected.
     *
     * @param path
     * @param digIn
     * @return true if the path is selected.
     */
    public boolean isPathSelected(TreePath path, boolean digIn) {
        if (!digIn)
            return super.isPathSelected(path);

        while (path != null && !super.isPathSelected(path)) {
            path = path.getParentPath();
        }
        return path != null;
    }

    /**
     * is path1 descendant of path2.
     */
    private boolean isDescendant(TreePath path1, TreePath path2) {
        Object obj1[] = path1.getPath();
        Object obj2[] = path2.getPath();
        for (int i = 0; i < obj2.length; i++) {
            if (obj1[i] != obj2[i])
                return false;
        }
        return true;
    }

    private boolean _fireEvent = true;

    protected void notifyPathChange(Vector changedPaths, TreePath oldLeadSelection) {
        if (_fireEvent) {
            super.notifyPathChange(changedPaths, oldLeadSelection);
        }
    }

    public void addSelectionPaths(TreePath[] paths) {
        if (!isDigIn()) {
            super.addSelectionPaths(paths);
            return;
        }

        boolean fireEventAtTheEnd = false;
        if (isSingleEventMode() && _fireEvent) {
            _fireEvent = false;
            fireEventAtTheEnd = true;
        }

        try {
            // unselect all descendants of paths[]
            ArrayList toBeRemoved = new ArrayList();
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                TreePath[] selectionPaths = getSelectionPaths();
                if (selectionPaths == null)
                    break;
                for (int j = 0; j < selectionPaths.length; j++) {
                    if (isDescendant(selectionPaths[j], path))
                        toBeRemoved.add(selectionPaths[j]);
                }
            }
            if (toBeRemoved.size() > 0) {
                delegateRemoveSelectionPaths((TreePath[]) toBeRemoved.toArray(new TreePath[toBeRemoved.size()]));
            }

            // if all siblings are selected then unselect them and select parent recursively
            // otherwize just select that path.
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                TreePath temp = null;
                while (areSiblingsSelected(path)) {
                    temp = path;
                    if (path.getParentPath() == null)
                        break;
                    path = path.getParentPath();
                }
                if (temp != null) {
                    if (temp.getParentPath() != null) {
                        addSelectionPath(temp.getParentPath());
                    }
                    else {
                        if (!isSelectionEmpty()) {
                            removeSelectionPaths(getSelectionPaths());
                        }
                        delegateAddSelectionPaths(new TreePath[]{temp});
                    }
                }
                else {
                    delegateAddSelectionPaths(new TreePath[]{path});
                }
            }
        }
        finally {
            _fireEvent = true;
            if (isSingleEventMode() && fireEventAtTheEnd) {
                notifyPathChange(paths, true, paths[0]);
            }
        }
    }

    /**
     * tells whether all siblings of given path are selected.
     *
     * @param path
     * @return true if the siblings are all selected.
     */
    private boolean areSiblingsSelected(TreePath path) {
        TreePath parent = path.getParentPath();
        if (parent == null)
            return true;
        Object node = path.getLastPathComponent();
        Object parentNode = parent.getLastPathComponent();

        int childCount = _model.getChildCount(parentNode);
        for (int i = 0; i < childCount; i++) {
            Object childNode = _model.getChild(parentNode, i);
            if (childNode == node)
                continue;
            TreePath childPath = parent.pathByAddingChild(childNode);
            if (_tree != null && (!_tree.isCheckBoxVisible(childPath) || !_tree.isCheckBoxEnabled(childPath)))
                continue;
            if (!isPathSelected(childPath)) {
                return false;
            }
        }
        return true;
    }

    public void removeSelectionPaths(TreePath[] paths) {
        if (!isDigIn()) {
            super.removeSelectionPaths(paths);
            return;
        }

        List toBeRemoved = new ArrayList();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            if (path.getPathCount() == 1) {
                toBeRemoved.add(path);
            }
            else {
                toggleRemoveSelection(path);
            }
        }
        if (toBeRemoved.size() > 0) {
            delegateRemoveSelectionPaths((TreePath[]) toBeRemoved.toArray(new TreePath[toBeRemoved.size()]));
        }
    }

    /**
     * If any ancestor node of given path is selected then unselect
     * it and selection all its descendants except given path and descendants.
     * Otherwise just unselect the given path
     */
    private void toggleRemoveSelection(TreePath path) {
        boolean fireEventAtTheEnd = false;
        if (isSingleEventMode() && _fireEvent) {
            _fireEvent = false;
            fireEventAtTheEnd = true;
        }

        try {
            Stack stack = new Stack();
            TreePath parent = path.getParentPath();
            while (parent != null && !isPathSelected(parent)) {
                stack.push(parent);
                parent = parent.getParentPath();
            }
            if (parent != null)
                stack.push(parent);
            else {
                delegateRemoveSelectionPaths(new TreePath[]{path});
                return;
            }

            List toBeAdded = new ArrayList();
            while (!stack.isEmpty()) {
                TreePath temp = (TreePath) stack.pop();
                TreePath peekPath = stack.isEmpty() ? path : (TreePath) stack.peek();
                Object node = temp.getLastPathComponent();
                Object peekNode = peekPath.getLastPathComponent();
                int childCount = _model.getChildCount(node);
                for (int i = 0; i < childCount; i++) {
                    Object childNode = _model.getChild(node, i);
                    if (childNode != peekNode) {
                        TreePath treePath = temp.pathByAddingChild(childNode);
                        if (_tree.isCheckBoxVisible(treePath) && _tree.isCheckBoxEnabled(treePath)) {
                            toBeAdded.add(treePath);
                        }
                    }
                }
            }
            if (toBeAdded.size() > 0) {
                delegateAddSelectionPaths((TreePath[]) toBeAdded.toArray(new TreePath[toBeAdded.size()]));
            }
            delegateRemoveSelectionPaths(new TreePath[]{parent});
        }
        finally {
            _fireEvent = true;
            if (isSingleEventMode() && fireEventAtTheEnd) {
                notifyPathChange(new TreePath[]{path}, false, path);
            }
        }
    }

    public boolean isSingleEventMode() {
        return _singleEventMode;
    }

    /**
     * Single event mode is a mode that always fires only one event when you select or unselect a tree node.
     * <p/>
     * Taking this tree as an example,
     * <p/>
     * <code><pre>
     * A -- a
     *   |- b
     *   |- c
     * </code></pre>
     * Case 1: Assuming b and c are selected at this point, you click on a.
     * <br>
     * <ul>
     * <li>In non-single event mode, you will get select-A, deselect-b and deselect-c three events
     * <li>In single event mode, you will only get select-a.
     * </ul>
     * <p/>
     * Case 2: Assuming none of the nodes are selected, you click on A. In this case, both modes result in the same behavior.
     * <ul>
     * <li>In non-single event mode, you will get only select-A event.
     * <li>In single event mode, you will only get select-A too.
     * </ul>
     * Case 3: Assuming b and c are selected and now you click on A.
     * <ul>
     * <li>In non-single event mode, you will get select-A event as well as deselect-b and deselect-c event.
     * <li>In single event mode, you will only get select-A.
     * </ul>
     * As you can see, single event mode will always fire the event on the nodes you select. However it doesn't reflect
     * what really happened inside the selection model. So if you want to get
     * a complete picture of the selection state inside selection model, you should use {@link #getSelectionPaths()} to find out.
     * In non-single event mode, the events reflect what happened inside the selection model. So you can get a complete picture
     * of the exact state without asking the selection model. The downside is it will generate too many events. With this option, you
     * can decide which mode you want to use that is the best for your case.
     * <p/>
     * By default, singleEventMode is set to false to be compatible with the older versions that don't have this option.
     *
     * @param singleEventMode
     */
    public void setSingleEventMode(boolean singleEventMode) {
        _singleEventMode = singleEventMode;
    }

    /**
     * Notifies listeners of a change in path. changePaths should contain
     * instances of PathPlaceHolder.
     */
    protected void notifyPathChange(TreePath[] changedPaths, boolean isNew, TreePath oldLeadSelection) {
        int cPathCount = changedPaths.length;
        boolean[] newness = new boolean[cPathCount];

        for (int counter = 0; counter < cPathCount; counter++) {
            newness[counter] = isNew;
        }

        TreeSelectionEvent event = new TreeSelectionEvent
                (this, changedPaths, newness, oldLeadSelection, leadPath);

        fireValueChanged(event);
    }

    // do not use it for now
    private boolean _batchMode = false;

    private boolean isBatchMode() {
        return _batchMode;
    }

    public void setBatchMode(boolean batchMode) {
        _batchMode = batchMode;
        if (!_batchMode) {
            super.addSelectionPaths((TreePath[]) _toBeAdded.toArray(new TreePath[_toBeAdded.size()]));
            _toBeAdded.clear();
            super.removeSelectionPaths((TreePath[]) _toBeRemoved.toArray(new TreePath[_toBeRemoved.size()]));
            _toBeRemoved.clear();
        }
    }

    private List _toBeAdded = new ArrayList();
    private List _toBeRemoved = new ArrayList();

    private void delegateRemoveSelectionPaths(TreePath[] paths) {
        if (!isBatchMode()) {
            super.removeSelectionPaths(paths);
        }
        else {
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                _toBeRemoved.add(path);
                _toBeAdded.remove(path);
            }
        }
    }

    private void delegateRemoveSelectionPath(TreePath path) {
        if (!isBatchMode()) {
            super.removeSelectionPath(path);
        }
        else {
            _toBeRemoved.add(path);
            _toBeAdded.remove(path);
        }

    }

    private void delegateAddSelectionPaths(TreePath[] paths) {
        if (!isBatchMode()) {
            super.addSelectionPaths(paths);
        }
        else {
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                _toBeAdded.add(path);
                _toBeRemoved.remove(path);
            }
        }
    }

    private void delegateAddSelectionPath(TreePath path) {
        if (!isBatchMode()) {
            super.addSelectionPath(path);
        }
        else {
            _toBeAdded.add(path);
            _toBeRemoved.remove(path);
        }
    }
}