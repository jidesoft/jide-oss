package com.jidesoft.swing;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.util.*;

/**
 * <code>CheckBoxTreeSelectionModel</code> is a selection _model based on {@link DefaultTreeSelectionModel} and use in
 * {@link CheckBoxTree} to keep track of the checked tree paths.
 *
 * @author Santhosh Kumar T
 */
public class CheckBoxTreeSelectionModel extends DefaultTreeSelectionModel implements TreeModelListener {
    private TreeModel _model;
    private boolean _digIn = true;
    private CheckBoxTree _tree;
    /**
     * Used in {@link #areSiblingsSelected(javax.swing.tree.TreePath)} for those paths pending added so that they are not
     * in the selection model right now.
     */
    protected Set<TreePath> _pathHasAdded;

    private boolean _singleEventMode = false;
    private static final long serialVersionUID = 1368502059666946634L;

    public CheckBoxTreeSelectionModel(TreeModel model) {
        setModel(model);
        setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    }

    void setTree(CheckBoxTree tree) {
        _tree = tree;
    }

    public CheckBoxTreeSelectionModel(TreeModel model, boolean digIn) {
        setModel(model);
        _digIn = digIn;
    }

    public TreeModel getModel() {
        return _model;
    }

    public void setModel(TreeModel model) {
        if (_model != model) {
            if (_model != null) {
                _model.removeTreeModelListener(this);
            }
            _model = model;
            if (_model != null) {
                _model.addTreeModelListener(this);
            }
        }
    }

    /**
     * Gets the dig-in mode. If the CheckBoxTree is in dig-in mode, checking the parent node will check all the
     * children. Correspondingly, getSelectionPaths() will only return the parent tree path. If not in dig-in mode, each
     * tree node can be checked or unchecked independently
     *
     * @return true or false.
     */
    public boolean isDigIn() {
        return _digIn;
    }

    /**
     * Sets the dig-in mode. If the CheckBoxTree is in dig-in mode, checking the parent node will check all the
     * children. Correspondingly, getSelectionPaths() will only return the parent tree path. If not in dig-in mode, each
     * tree node can be checked or unchecked independently
     *
     * @param digIn true to enable dig-in mode. False to disable it.
     */
    public void setDigIn(boolean digIn) {
        _digIn = digIn;
    }

    /**
     * Tests whether there is any unselected node in the subtree of given path.
     * <p/>
     * Inherited from JTree, the TreePath must be a path instance inside the tree model. If you populate a new TreePath
     * instance on the fly, it would not work.
     *
     * @param path check if the path is partially selected.
     * @return true if partially. Otherwise false.
     */
    public boolean isPartiallySelected(TreePath path) {
        if (isPathSelected(path, true))
            return false;
        TreePath[] selectionPaths = getSelectionPaths();
        if (selectionPaths == null)
            return false;
        for (TreePath selectionPath : selectionPaths) {
            if (isDescendant(selectionPath, path))
                return true;
        }
        return false;
    }

    @Override
    public boolean isRowSelected(int row) {
        return isPathSelected(_tree.getPathForRow(row), _tree.isDigIn());
    }

    /**
     * Check if the parent path is really selected.
     * <p/>
     * The default implementation is just return true. In filterable scenario, you could override this method to check
     * more.
     * <p/>
     * Inherited from JTree, the TreePath must be a path instance inside the tree model. If you populate a new TreePath
     * instance on the fly, it would not work.
     *
     * @param path the original path to be checked
     * @param parent the parent part which is closest to the original path and is selected
     * @return true if the path is actually selected without any doubt. Otherwise false.
     */
    protected boolean isParentActuallySelected(TreePath path, TreePath parent) {
        return true;
    }

    /**
     * Tells whether given path is selected. if dig is true, then a path is assumed to be selected, if one of its
     * ancestor is selected.
     * <p/>
     * Inherited from JTree, the TreePath must be a path instance inside the tree model. If you populate a new TreePath
     * instance on the fly, it would not work.
     *
     * @param path  check if the path is selected.
     * @param digIn whether we will check its descendants.
     * @return true if the path is selected.
     */
    public boolean isPathSelected(TreePath path, boolean digIn) {
        if (path == null) {
            return false;
        }

        if (!digIn)
            return super.isPathSelected(path);

        TreePath parent = path;
        while (parent != null && !super.isPathSelected(parent)) {
            parent = parent.getParentPath();
        }

        if (parent != null) {
            return isParentActuallySelected(path, parent);
        }

        if (_model == null) {
            return true;
        }

        Object node = path.getLastPathComponent();
        if (_model.getChildCount(node) == 0) {
            return false;
        }

        // find out if all children are selected
        boolean allChildrenSelected = true;
        for (int i = 0; i < _model.getChildCount(node); i++) {
            Object childNode = _model.getChild(node, i);
            if (!isPathSelected(path.pathByAddingChild(childNode), true)) {
                allChildrenSelected = false;
                break;
            }
        }
        // if all children are selected, let's select the parent path only
        if (_tree.isCheckBoxVisible(path) && allChildrenSelected) {
            addSelectionPaths(new TreePath[]{path});
        }
        return allChildrenSelected;
    }

    /**
     * is path1 descendant of path2.
     * <p/>
     * Inherited from JTree, the TreePath must be a path instance inside the tree model. If you populate a new TreePath
     * instance on the fly, it would not work.
     *
     * @param path1 the first path
     * @param path2 the second path
     * @return true if the first path is the descendant of the second path.
     */
    private boolean isDescendant(TreePath path1, TreePath path2) {
        Object obj1[] = path1.getPath();
        Object obj2[] = path2.getPath();
        if (obj1.length < obj2.length)
            return false;
        for (int i = 0; i < obj2.length; i++) {
            if (obj1[i] != obj2[i])
                return false;
        }
        return true;
    }

    private boolean _fireEvent = true;

    @SuppressWarnings({"RawUseOfParameterizedType"})
    @Override
    protected void notifyPathChange(Vector changedPaths, TreePath oldLeadSelection) {
        if (_fireEvent) {
            super.notifyPathChange(changedPaths, oldLeadSelection);
        }
    }

    /**
     * Overrides the method in DefaultTreeSelectionModel to consider digIn mode.
     * <p/>
     * Inherited from JTree, the TreePath must be a path instance inside the tree model. If you populate a new TreePath
     * instance on the fly, it would not work.
     *
     * @param pPaths the tree paths to be selected.
     */
    @Override
    public void setSelectionPaths(TreePath[] pPaths) {
        if (!isDigIn() || selectionMode == TreeSelectionModel.SINGLE_TREE_SELECTION) {
            super.setSelectionPaths(pPaths);
        }
        else {
            clearSelection();
            addSelectionPaths(pPaths);
        }
    }

    /**
     * Overrides the method in DefaultTreeSelectionModel to consider digIn mode.
     * <p/>
     * Inherited from JTree, the TreePath must be a path instance inside the tree model. If you populate a new TreePath
     * instance on the fly, it would not work.
     *
     * @param paths the tree paths to be added to selection paths.
     */
    @Override
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
            _pathHasAdded = new HashSet<TreePath>();
            for (TreePath path : paths) {
                if (isPathSelected(path, isDigIn())) {
                    continue; // for non batch mode scenario, check if it is already selected by adding its parent possibly
                }
                if (isBatchMode()) {
                    // if the path itself is added by other insertion, just remove it
                    if (_toBeAdded.contains(path)) {
                        addToExistingSet(_pathHasAdded, path);
                        continue;
                    }
                    // check if its ancestor has already been added. If so, do nothing
                    boolean findAncestor = false;
                    for (TreePath addPath : _pathHasAdded) {
                        if (addPath.isDescendant(path)) {
                            findAncestor = true;
                            break;
                        }
                    }
                    if (findAncestor) {
                        continue;
                    }
                }
                TreePath temp = null;
                // if all siblings are selected then deselect them and select parent recursively
                // otherwise just select that path.
                while (areSiblingsSelected(path)) {
                    temp = path;
                    if (path.getParentPath() == null)
                        break;
                    path = path.getParentPath();
                }
                if (temp != null) {
                    if (temp.getParentPath() != null) {
                        delegateAddSelectionPaths(new TreePath[] {temp.getParentPath()});
                    }
                    else {
                        delegateAddSelectionPaths(new TreePath[]{temp});
                    }
                }
                else {
                    delegateAddSelectionPaths(new TreePath[]{path});
                }
                addToExistingSet(_pathHasAdded, path);
            }
            // deselect all descendants of paths[]
            List<TreePath> toBeRemoved = new ArrayList<TreePath>();
            for (TreePath path : _toBeAdded) {
                TreePath[] selectionPaths = getSelectionPaths();
                if (selectionPaths == null)
                    break;
                for (TreePath selectionPath : selectionPaths) {
                    if (isDescendant(selectionPath, path))
                        toBeRemoved.add(selectionPath);
                }
            }
            if (toBeRemoved.size() > 0) {
                delegateRemoveSelectionPaths(toBeRemoved.toArray(new TreePath[toBeRemoved.size()]));
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
     * <p/>
     * Inherited from JTree, the TreePath must be a path instance inside the tree model. If you populate a new TreePath
     * instance on the fly, it would not work.
     *
     * @param path the tree path
     * @return true if the siblings are all selected.
     */
    protected boolean areSiblingsSelected(TreePath path) {
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
            if (_tree != null && !_tree.isCheckBoxVisible(childPath)) {
                // if the checkbox is not visible, we check its children
                if (!isPathSelected(childPath, true) && (_pathHasAdded == null || !_pathHasAdded.contains(childPath))) {
                    return false;
                }
            }
            if (!isPathSelected(childPath) && (_pathHasAdded == null || !_pathHasAdded.contains(childPath))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void removeSelectionPaths(TreePath[] paths) {
        removeSelectionPaths(paths, true);
    }

    public void removeSelectionPaths(TreePath[] paths, boolean doFireEvent) {
        if (!isDigIn()) {
            super.removeSelectionPaths(paths);
            return;
        }

        boolean fireEventAtTheEnd = false;
        if (doFireEvent) {
            if (isSingleEventMode() && _fireEvent) {
                _fireEvent = false;
                fireEventAtTheEnd = true;
            }
        }
        try {
            Set<TreePath> pathHasRemoved = new HashSet<TreePath>();
            for (TreePath path : paths) {
                if (!isPathSelected(path, isDigIn())) {
                    continue; // for non batch mode scenario, check if it is already deselected by removing its parent possibly
                }
                TreePath upperMostSelectedAncestor = null;
                if (isBatchMode()) {
                    // if the path itself is added by other removal, just remove it
                    if (_toBeAdded.contains(path)) {
                        _toBeAdded.remove(path);
                        addToExistingSet(pathHasRemoved, path);
                        continue;
                    }
                    // check if its ancestor has already been removed. If so, do nothing
                    boolean findAncestor = false;
                    for (TreePath removedPath : pathHasRemoved) {
                        if (removedPath.isDescendant(path)) {
                            findAncestor = true;
                            break;
                        }
                    }
                    if (findAncestor) {
                        continue;
                    }
                    // remove all children path added by other removal
                    Set<TreePath> pathToRemoved = new HashSet<TreePath>();
                    for (TreePath pathToAdded : _toBeAdded) {
                        if (path.isDescendant(pathToAdded)) {
                            pathToRemoved.add(pathToAdded);
                        }
                    }
                    _toBeAdded.removeAll(pathToRemoved);
                    // find a parent path added by other removal, then use that parent to do following actions
                    for (TreePath pathToAdded : _toBeAdded) {
                        if (pathToAdded.isDescendant(path)) {
                            upperMostSelectedAncestor = pathToAdded;
                            break;
                        }
                    }
                }
                TreePath parent = path.getParentPath();
                Stack<TreePath> stack = new Stack<TreePath>();
                while (parent != null && (upperMostSelectedAncestor == null ? !isPathSelected(parent) : parent != upperMostSelectedAncestor)) {
                    stack.push(parent);
                    parent = parent.getParentPath();
                }
                if (parent != null)
                    stack.push(parent);
                else {
                    delegateRemoveSelectionPaths(new TreePath[]{path});
                    addToExistingSet(pathHasRemoved, path);
                    continue;
                }

                List<TreePath> toBeAdded = new ArrayList<TreePath>();
                while (!stack.isEmpty()) {
                    TreePath temp = stack.pop();
                    TreePath peekPath = stack.isEmpty() ? path : stack.peek();
                    Object node = temp.getLastPathComponent();
                    Object peekNode = peekPath.getLastPathComponent();
                    int childCount = _model.getChildCount(node);
                    for (int i = 0; i < childCount; i++) {
                        Object childNode = _model.getChild(node, i);
                        if (childNode != peekNode) {
                            TreePath treePath = temp.pathByAddingChild(childNode);
                            toBeAdded.add(treePath);
                        }
                    }
                }
                if (toBeAdded.size() > 0) {
                    delegateAddSelectionPaths(toBeAdded.toArray(new TreePath[toBeAdded.size()]));
                }
                delegateRemoveSelectionPaths(new TreePath[]{parent});
                addToExistingSet(pathHasRemoved, path);
            }
        }
        finally {
            _fireEvent = true;
            if (isSingleEventMode() && fireEventAtTheEnd) {
                notifyPathChange(paths, false, paths[0]);
            }
        }
    }

    private void addToExistingSet(Set<TreePath> pathHasOperated, TreePath pathToOperate) {
        if (pathHasOperated.contains(pathToOperate)) {
            return; // it is already removed
        }
        for (TreePath path : pathHasOperated) {
            if (path.isDescendant(pathToOperate)) {
                return; // its parent is removed, no need to add it
            }
        }
        // remove all children path exists in the set
        Set<TreePath> duplicatePathToErase = new HashSet<TreePath>();
        for (TreePath path : pathHasOperated) {
            if (pathToOperate.isDescendant(path)) {
                duplicatePathToErase.add(path);
            }
        }
        pathHasOperated.removeAll(duplicatePathToErase);
        pathHasOperated.add(pathToOperate);
    }

    public boolean isSingleEventMode() {
        return _singleEventMode;
    }

    /**
     * Single event mode is a mode that always fires only one event when you select or deselect a tree node.
     * <p/>
     * Taking this tree as an example,
     * <p/>
     * <code><pre>
     * A -- a
     *   |- b
     *   |- c
     * </code></pre>
     * Case 1: Assuming b and c are selected at this point, you click on a. <br> <ul> <li>In non-single event mode, you
     * will get select-A, deselect-b and deselect-c three events <li>In single event mode, you will only get select-a.
     * </ul>
     * <p/>
     * Case 2: Assuming none of the nodes are selected, you click on A. In this case, both modes result in the same
     * behavior. <ul> <li>In non-single event mode, you will get only select-A event. <li>In single event mode, you will
     * only get select-A too. </ul> Case 3: Assuming b and c are selected and now you click on A. <ul> <li>In non-single
     * event mode, you will get select-A event as well as deselect-b and deselect-c event. <li>In single event mode, you
     * will only get select-A. </ul> As you can see, single event mode will always fire the event on the nodes you
     * select. However it doesn't reflect what really happened inside the selection model. So if you want to get a
     * complete picture of the selection state inside selection model, you should use {@link #getSelectionPaths()} to
     * find out. In non-single event mode, the events reflect what happened inside the selection model. So you can get a
     * complete picture of the exact state without asking the selection model. The downside is it will generate too many
     * events. With this option, you can decide which mode you want to use that is the best for your case.
     * <p/>
     * By default, singleEventMode is set to false to be compatible with the older versions that don't have this
     * option.
     *
     * @param singleEventMode true or false.
     */
    public void setSingleEventMode(boolean singleEventMode) {
        _singleEventMode = singleEventMode;
    }

    /**
     * Notifies listeners of a change in path. changePaths should contain instances of PathPlaceHolder.
     *
     * @param changedPaths     the paths that are changed.
     * @param isNew            is it a new path.
     * @param oldLeadSelection the old selection.
     */
    protected void notifyPathChange(TreePath[] changedPaths, boolean isNew, TreePath oldLeadSelection) {
        if (_fireEvent) {
            int cPathCount = changedPaths.length;
            boolean[] newness = new boolean[cPathCount];

            for (int counter = 0; counter < cPathCount; counter++) {
                newness[counter] = isNew;
            }

            TreeSelectionEvent event = new TreeSelectionEvent
                    (this, changedPaths, newness, oldLeadSelection, leadPath);

            fireValueChanged(event);
        }
    }

    // do not use it for now
    private boolean _batchMode = false;

    boolean isBatchMode() {
        return _batchMode;
    }

    public void setBatchMode(boolean batchMode) {
        _batchMode = batchMode;
        if (!_batchMode) {
            super.addSelectionPaths(_toBeAdded.toArray(new TreePath[_toBeAdded.size()]));
            _toBeAdded.clear();
            super.removeSelectionPaths(_toBeRemoved.toArray(new TreePath[_toBeRemoved.size()]));
            _toBeRemoved.clear();
        }
    }

    private Set<TreePath> _toBeAdded = new HashSet<TreePath>();
    private Set<TreePath> _toBeRemoved = new HashSet<TreePath>();

    private void delegateRemoveSelectionPaths(TreePath[] paths) {
        if (!isBatchMode()) {
            super.removeSelectionPaths(paths);
        }
        else {
            for (TreePath path : paths) {
                _toBeRemoved.add(path);
                _toBeAdded.remove(path);
            }
        }
    }

//    private void delegateRemoveSelectionPath(TreePath path) {
//        if (!isBatchMode()) {
//            super.removeSelectionPath(path);
//        }
//        else {
//            _toBeRemoved.add(path);
//            _toBeAdded.remove(path);
//        }
//
//    }
//
    private void delegateAddSelectionPaths(TreePath[] paths) {
        if (!isBatchMode()) {
            super.addSelectionPaths(paths);
        }
        else {
            for (TreePath path : paths) {
                addToExistingSet(_toBeAdded, path);
                _toBeRemoved.remove(path);
            }
        }
    }

//    private void delegateAddSelectionPath(TreePath path) {
//        if (!isBatchMode()) {
//            super.addSelectionPath(path);
//        }
//        else {
//            _toBeAdded.add(path);
//            _toBeRemoved.remove(path);
//        }
//    }
//
    public void treeNodesChanged(TreeModelEvent e) {
        revalidateSelectedTreePaths();
    }

    public void treeNodesInserted(TreeModelEvent e) {

    }

    public void treeNodesRemoved(TreeModelEvent e) {
        revalidateSelectedTreePaths();
    }

    private static boolean isTreePathValid(TreeModel treeModel, TreePath path) {
        Object parent = treeModel.getRoot();
        for (int i = 0; i < path.getPathCount(); i++) {
            Object pathComponent = path.getPathComponent(i);
            if (i == 0) {
                if (pathComponent != parent) {
                    return false;
                }
            }
            else {
                boolean found = false;
                for (int j = 0; j < treeModel.getChildCount(parent); j++) {
                    Object child = treeModel.getChild(parent, j);
                    if (child == pathComponent) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
                parent = pathComponent;
            }
        }
        return true;
    }

    public void treeStructureChanged(TreeModelEvent e) {
        revalidateSelectedTreePaths();
    }

    private void revalidateSelectedTreePaths() {
        TreePath[] treePaths = getSelectionPaths();
        if (treePaths != null) {
            for (TreePath treePath : treePaths) {
                if (treePath != null && !isTreePathValid(_model, treePath)) {
                    super.removeSelectionPath(treePath);
                }
            }
        }
    }
}