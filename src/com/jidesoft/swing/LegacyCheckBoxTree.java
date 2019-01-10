/*
 * @(#)CheckBoxTree.java 8/11/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Position;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

/**
 * <strong>This class is deprecated and provided to support compatibility with {@link LegacyTristateCheckBox}. We will not provide support
 * for this class. You likely want to use {@link CheckBoxTree}.</strong>
 * <p/>
 * CheckBoxTree is a special JTree which uses JCheckBox as the tree renderer. In addition to regular JTree's features,
 * it also allows you select any number of tree nodes in the tree by selecting the check boxes. <p>To select an element,
 * user can mouse click on the check box, or select one or several tree nodes and press SPACE key to toggle the check
 * box selection for all selected tree nodes.
 * <p/>
 * In order to retrieve which tree paths are selected, you need to call {@link #getCheckBoxTreeSelectionModel()}. It
 * will return the selection model that keeps track of which tree paths have been checked. For example {@link
 * CheckBoxTreeSelectionModel#getSelectionPaths()} will give the list of paths which have been checked.
 * @deprecated Use {@link CheckBoxTree}.
 */
public class LegacyCheckBoxTree extends JTree {

    public static final String PROPERTY_CHECKBOX_ENABLED = "checkBoxEnabled";
    public static final String PROPERTY_CLICK_IN_CHECKBOX_ONLY = "clickInCheckBoxOnly";
    public static final String PROPERTY_DIG_IN = "digIn";

    protected LegacyCheckBoxTreeCellRenderer _treeCellRenderer;

    private LegacyCheckBoxTreeSelectionModel _checkBoxTreeSelectionModel;

    private boolean _checkBoxEnabled = true;
    private boolean _clickInCheckBoxOnly = true;
    private PropertyChangeListener _modelChangeListener;
    private LegacyTristateCheckBox _checkBox;
    private boolean _selectPartialOnToggling = true;

    public LegacyCheckBoxTree() {
        init();
    }

    public LegacyCheckBoxTree(Object[] value) {
        super(value);
        init();
    }

    public LegacyCheckBoxTree(Vector<?> value) {
        super(value);
        init();
    }

    public LegacyCheckBoxTree(Hashtable<?, ?> value) {
        super(value);
        init();
    }

    public LegacyCheckBoxTree(TreeNode root) {
        super(root);
        init();
    }

    public LegacyCheckBoxTree(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
        init();
    }

    public LegacyCheckBoxTree(TreeModel newModel) {
        super(newModel);
        init();
    }

    /**
     * Initialize the CheckBoxTree.
     */
    protected void init() {
        _checkBoxTreeSelectionModel = createCheckBoxTreeSelectionModel(getModel());
        _checkBoxTreeSelectionModel.setTree(this);
        Handler handler = createHandler();
        JideSwingUtilities.insertMouseListener(this, handler, 0);
        addKeyListener(handler);
        _checkBoxTreeSelectionModel.addTreeSelectionListener(handler);

        if (_modelChangeListener == null) {
            _modelChangeListener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (JTree.SELECTION_MODEL_PROPERTY.equals(evt.getPropertyName())) {
                        updateRowMapper();
                    }
                    if ("model".equals(evt.getPropertyName()) && evt.getNewValue() instanceof TreeModel) {
                        _checkBoxTreeSelectionModel.setModel((TreeModel) evt.getNewValue());
                    }
                }
            };
        }
        addPropertyChangeListener(JTree.SELECTION_MODEL_PROPERTY, _modelChangeListener);
        addPropertyChangeListener("model", _modelChangeListener);
        updateRowMapper();
    }

    /**
     * Creates the CheckBoxTreeSelectionModel.
     *
     * @param model the tree model.
     * @return the CheckBoxTreeSelectionModel.
     */
    protected LegacyCheckBoxTreeSelectionModel createCheckBoxTreeSelectionModel(TreeModel model) {
        return new LegacyCheckBoxTreeSelectionModel(model);
    }

    /**
     * RowMapper is necessary for contiguous selection.
     */
    private void updateRowMapper() {
        _checkBoxTreeSelectionModel.setRowMapper(getSelectionModel().getRowMapper());
    }

    private TreeCellRenderer _defaultRenderer;

    /**
     * Gets the cell renderer with check box.
     *
     * @return CheckBoxTree's own cell renderer which has the check box. The actual cell renderer you set by
     *         setCellRenderer() can be accessed by using {@link #getActualCellRenderer()}.
     */
    @Override
    public TreeCellRenderer getCellRenderer() {
        TreeCellRenderer cellRenderer = getActualCellRenderer();
        if (cellRenderer == null) {
            cellRenderer = getDefaultRenderer();
        }

        if (_treeCellRenderer == null) {
            _treeCellRenderer = createCellRenderer(cellRenderer);
        }
        else {
            _treeCellRenderer.setActualTreeRenderer(cellRenderer);
        }
        return _treeCellRenderer;
    }

    private TreeCellRenderer getDefaultRenderer() {
        if (_defaultRenderer == null)
            _defaultRenderer = new DefaultTreeCellRenderer();
        return _defaultRenderer;
    }

    /**
     * Gets the actual cell renderer. Since CheckBoxTree has its own check box cell renderer, this method will give you
     * access to the actual cell renderer which is either the default tree cell renderer or the cell renderer you set
     * using {@link #setCellRenderer(javax.swing.tree.TreeCellRenderer)}.
     *
     * @return the actual cell renderer
     */
    public TreeCellRenderer getActualCellRenderer() {
        if (_treeCellRenderer != null) {
            return _treeCellRenderer.getActualTreeRenderer();
        }
        else {
            return super.getCellRenderer();
        }
    }

    @Override
    public void setCellRenderer(TreeCellRenderer x) {
        if (x == null) {
            x = getDefaultRenderer();
        }
        super.setCellRenderer(x);
        if (_treeCellRenderer != null) {
            _treeCellRenderer.setActualTreeRenderer(x);
        }
    }


    /**
     * Creates the cell renderer.
     *
     * @param renderer the actual renderer for the tree node. This method will return a cell renderer that use a check
     *                 box and put the actual renderer inside it.
     * @return the cell renderer.
     */
    protected LegacyCheckBoxTreeCellRenderer createCellRenderer(TreeCellRenderer renderer) {
        final LegacyCheckBoxTreeCellRenderer checkBoxTreeCellRenderer = new LegacyCheckBoxTreeCellRenderer(renderer, getCheckBox());
        addPropertyChangeListener(CELL_RENDERER_PROPERTY, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                TreeCellRenderer treeCellRenderer = (TreeCellRenderer) evt.getNewValue();
                if (treeCellRenderer != checkBoxTreeCellRenderer) {
                    checkBoxTreeCellRenderer.setActualTreeRenderer(treeCellRenderer);
                }
                else {
                    checkBoxTreeCellRenderer.setActualTreeRenderer(null);
                }
            }
        });
        return checkBoxTreeCellRenderer;
    }

    /**
     * Creates the mouse listener and key listener used by CheckBoxTree.
     *
     * @return the Handler.
     */
    protected Handler createHandler() {
        return new Handler(this);
    }

    /**
     * Get the CheckBox used for CheckBoxTreeCellRenderer.
     *
     * @see #setCheckBox(LegacyTristateCheckBox)
     * @return the check box.
     */
    public LegacyTristateCheckBox getCheckBox() {
        return _checkBox;
    }

    /**
     * Set the CheckBox used for CheckBoxTreeCellRenderer.
     * <p>
     * By default, it's null. CheckBoxTreeCellRenderer then will create a default TristateCheckBox.
     *
     * @param checkBox the check box
     */
    public void setCheckBox(LegacyTristateCheckBox checkBox) {
        if (_checkBox != checkBox) {
            _checkBox = checkBox;
            _treeCellRenderer = null;
            revalidate();
            repaint();
        }
    }

    /**
     * Gets the flag indicating if toggling should select or deselect the partially selected node.
     *
     * @return true if select first. Otherwise false.
     * @see #setSelectPartialOnToggling(boolean)
     */
    public boolean isSelectPartialOnToggling() {
        return _selectPartialOnToggling;
    }

    /**
     * Sets the flag indicating if toggling should select or deselect the partially selected node.
     * <p/>
     * By default, the value is true to keep original behavior.
     *
     * @param selectPartialOnToggling the flag
     */
    public void setSelectPartialOnToggling(boolean selectPartialOnToggling) {
        _selectPartialOnToggling = selectPartialOnToggling;
    }

    protected static class Handler implements MouseListener, KeyListener, TreeSelectionListener {
        protected LegacyCheckBoxTree _tree;
        int _hotspot = new JCheckBox().getPreferredSize().width;
        private int _toggleCount = -1;

        public Handler(LegacyCheckBoxTree tree) {
            _tree = tree;
        }

        protected TreePath getTreePathForMouseEvent(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e)) {
                return null;
            }

            if (!_tree.isCheckBoxEnabled()) {
                return null;
            }

            TreePath path = _tree.getPathForLocation(e.getX(), e.getY());
            if (path == null)
                return null;

            if (clicksInCheckBox(e, path) || !_tree.isClickInCheckBoxOnly()) {
                return path;
            }
            else {
                return null;
            }
        }

        protected boolean clicksInCheckBox(MouseEvent e, TreePath path) {
            if (!_tree.isCheckBoxVisible(path)) {
                return false;
            }
            else {
                Rectangle bounds = _tree.getPathBounds(path);
                if (_tree.getComponentOrientation().isLeftToRight()) {
                    return e.getX() < bounds.x + _hotspot;
                }
                else {
                    return e.getX() > bounds.x + bounds.width - _hotspot;
                }
            }
        }

        private TreePath preventToggleEvent(MouseEvent e) {
            TreePath pathForMouseEvent = getTreePathForMouseEvent(e);
            if (pathForMouseEvent != null) {
                int toggleCount = _tree.getToggleClickCount();
                if (toggleCount != -1) {
                    _toggleCount = toggleCount;
                    _tree.setToggleClickCount(-1);
                }
            }
            return pathForMouseEvent;
        }

        public void mouseClicked(MouseEvent e) {
            if (e.isConsumed()) {
                return;
            }

            preventToggleEvent(e);
        }

        public void mousePressed(MouseEvent e) {
            if (e.isConsumed()) {
                return;
            }

            TreePath path = preventToggleEvent(e);
            if (path != null) {
                toggleSelections(new TreePath[] {path});
                e.consume();
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isConsumed()) {
                return;
            }

            TreePath path = preventToggleEvent(e);
            if (path != null) {
                e.consume();
            }
            if (_toggleCount != -1) {
                _tree.setToggleClickCount(_toggleCount);
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if (e.isConsumed()) {
                return;
            }

            if (!_tree.isCheckBoxEnabled()) {
                return;
            }

            if (e.getModifiers() == 0 && e.getKeyChar() == KeyEvent.VK_SPACE)
                toggleSelections();
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
        }

        public void valueChanged(TreeSelectionEvent e) {
            _tree.treeDidChange();
        }

        protected void toggleSelections() {
            TreePath[] treePaths = _tree.getSelectionPaths();
            toggleSelections(treePaths);
        }

        private void toggleSelections(TreePath[] treePaths) {
            if (treePaths == null || treePaths.length == 0 || !_tree.isEnabled()) {
                return;
            }
            if (treePaths.length == 1 && !_tree.isCheckBoxEnabled(treePaths[0])) {
                return;
            }
            LegacyCheckBoxTreeSelectionModel selectionModel = _tree.getCheckBoxTreeSelectionModel();
            List<TreePath> pathToAdded = new ArrayList<TreePath>();
            List<TreePath> pathToRemoved = new ArrayList<TreePath>();
            for (TreePath treePath : treePaths) {
                boolean selected = selectionModel.isPathSelected(treePath, selectionModel.isDigIn());
                if (selected) {
                    pathToRemoved.add(treePath);
                }
                else {
                    if (!_tree.isSelectPartialOnToggling() && selectionModel.isPartiallySelected(treePath)) {
                        TreePath[] selectionPaths = selectionModel.getSelectionPaths();
                        if (selectionPaths != null) {
                            for (TreePath selectionPath : selectionPaths) {
                                if (selectionModel.isDescendant(selectionPath, treePath)) {
                                    pathToRemoved.add(selectionPath);
                                }
                            }
                        }
                    }
                    else {
                        pathToAdded.add(treePath);
                    }
                }
            }
            selectionModel.removeTreeSelectionListener(this);
            try {
                if (pathToAdded.size() > 0) {
                    selectionModel.addSelectionPaths(pathToAdded.toArray(new TreePath[pathToAdded.size()]));
                }
                if (pathToRemoved.size() > 0) {
                    selectionModel.removeSelectionPaths(pathToRemoved.toArray(new TreePath[pathToRemoved.size()]));
                }
            }
            finally {
                selectionModel.addTreeSelectionListener(this);
                _tree.treeDidChange();
            }
        }
    }

    @Override
    public TreePath getNextMatch(String prefix, int startingRow, Position.Bias bias) {
        return null;
    }

    /**
     * Gets the selection model for the check boxes. To retrieve the state of check boxes, you should use this selection
     * model.
     *
     * @return the selection model for the check boxes.
     */
    public LegacyCheckBoxTreeSelectionModel getCheckBoxTreeSelectionModel() {
        return _checkBoxTreeSelectionModel;
    }

    /**
     * Gets the value of property checkBoxEnabled. If true, user can click on check boxes on each tree node to select
     * and deselect. If false, user can't click but you as developer can programmatically call API to select/deselect
     * it.
     *
     * @return the value of property checkBoxEnabled.
     */
    public boolean isCheckBoxEnabled() {
        return _checkBoxEnabled;
    }

    /**
     * Sets the value of property checkBoxEnabled.
     *
     * @param checkBoxEnabled true to allow to check the check box. False to disable it which means user can see whether
     *                        a row is checked or not but they cannot change it.
     */
    public void setCheckBoxEnabled(boolean checkBoxEnabled) {
        if (checkBoxEnabled != _checkBoxEnabled) {
            Boolean oldValue = _checkBoxEnabled ? Boolean.TRUE : Boolean.FALSE;
            Boolean newValue = checkBoxEnabled ? Boolean.TRUE : Boolean.FALSE;
            _checkBoxEnabled = checkBoxEnabled;
            firePropertyChange(PROPERTY_CHECKBOX_ENABLED, oldValue, newValue);
            repaint();
        }
    }

    /**
     * Checks if check box is enabled. There is no setter for it. The only way is to override this method to return true
     * or false.
     * <p/>
     * However, in digIn mode, user can still select the disabled node by selecting all children nodes of that node.
     * Also if user selects the parent node, the disabled children nodes will be selected too.
     *
     * @param path the tree path.
     * @return true or false. If false, the check box on the particular tree path will be disabled.
     */
    public boolean isCheckBoxEnabled(TreePath path) {
        return true;
    }

    /**
     * Checks if check box is visible. There is no setter for it. The only way is to override this method to return true
     * or false.
     *
     * @param path the tree path.
     * @return true or false. If false, the check box on the particular tree path will be disabled.
     */
    public boolean isCheckBoxVisible(TreePath path) {
        return true;
    }

    /**
     * Gets the dig-in mode. If the CheckBoxTree is in dig-in mode, checking the parent node will check all the
     * children. Correspondingly, getSelectionPaths() will only return the parent tree path. If not in dig-in mode, each
     * tree node can be checked or unchecked independently
     *
     * @return true or false.
     */
    public boolean isDigIn() {
        return getCheckBoxTreeSelectionModel().isDigIn();
    }

    /**
     * Sets the dig-in mode. If the CheckBoxTree is in dig-in mode, checking the parent node will check all the
     * children. Correspondingly, getSelectionPaths() will only return the parent tree path. If not in dig-in mode, each
     * tree node can be checked or unchecked independently
     *
     * @param digIn the new digIn mode.
     */
    public void setDigIn(boolean digIn) {
        boolean old = isDigIn();
        if (old != digIn) {
            getCheckBoxTreeSelectionModel().setDigIn(digIn);
            firePropertyChange(PROPERTY_DIG_IN, old, digIn);
        }
    }

    /**
     * Gets the value of property clickInCheckBoxOnly. If true, user can click on check boxes on each tree node to
     * select and deselect. If false, user can't click but you as developer can programmatically call API to
     * select/deselect it.
     *
     * @return the value of property clickInCheckBoxOnly.
     */
    public boolean isClickInCheckBoxOnly() {
        return _clickInCheckBoxOnly;
    }

    /**
     * Sets the value of property clickInCheckBoxOnly.
     *
     * @param clickInCheckBoxOnly true to allow to check the check box. False to disable it which means user can see
     *                            whether a row is checked or not but they cannot change it.
     */
    public void setClickInCheckBoxOnly(boolean clickInCheckBoxOnly) {
        if (clickInCheckBoxOnly != _clickInCheckBoxOnly) {
            boolean old = _clickInCheckBoxOnly;
            _clickInCheckBoxOnly = clickInCheckBoxOnly;
            firePropertyChange(PROPERTY_CLICK_IN_CHECKBOX_ONLY, old, _clickInCheckBoxOnly);
        }
    }
}
