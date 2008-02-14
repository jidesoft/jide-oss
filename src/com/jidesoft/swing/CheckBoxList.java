/*
 * @(#)CheckBoxList.java 4/21/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * <code>CheckBoxList</code> is a special JList which uses JCheckBox as the list element. In
 * addition to regular JList feature, it also allows you select any number of elements in the list
 * by selecting the check boxes.
 * <p/>
 * To select an element, user can mouse click on the check box, or highlight the rows and press
 * SPACE key to toggle the selections.
 * <p/>
 * We used cell renderer feature in JList to add the check box in each row. However you can still
 * set your own cell renderer just like before using {@link #setCellRenderer(javax.swing.ListCellRenderer)}.
 * CheckBoxList will use your cell renderer and automatically put a check box before it.
 * <p/>
 * Please note, we changed CheckBoxList implementation in 1.9.2 release. The old CheckBoxList class
 * is renamed to {@link CheckBoxListWithSelectable}. If you want to use the old implementation, you
 * can use CheckBoxListWithSelectable instead. The main difference between the two implementation is
 * at how the selection state is kept. In new implementation, the selection state is kept at a
 * separate ListSelectionModel which you can get using {@link CheckBoxList#getCheckBoxListSelectionModel()}.
 * If you need to add a check to a check box or to find out if a check box is checked, you need to
 * ask the getCheckBoxListSelectionModel() to do it. The old implementation kept the selection state
 * at Selectable object in the ListModel. The new implementation is also inline with that of {@link
 * CheckBoxTree}.
 */
public class CheckBoxList extends JList {

    protected CheckBoxListCellRenderer _listCellRenderer;

    public final static String PROPERTY_CHECKBOX_ENABLED = "checkBoxEnabled";

    private boolean _checkBoxEnabled = true;

    private CheckBoxListSelectionModel _checkBoxListSelectionModel;
    protected Handler _handler;

    /**
     * Constructs a <code>CheckBoxList</code> with an empty model.
     */
    public CheckBoxList() {
        init();
    }

    /**
     * Constructs a <code>CheckBoxList</code> that displays the elements in the specified
     * <code>Vector</code>.
     *
     * @param listData the <code>Vector</code> to be loaded into the data model
     */
    public CheckBoxList(final Vector<?> listData) {
        super(listData);
        init();
    }

    /**
     * Constructs a <code>CheckBoxList</code> that displays the elements in the specified
     * <code>Object[]</code>.
     *
     * @param listData the array of Objects to be loaded into the data model
     */
    public CheckBoxList(final Object[] listData) {
        super(listData);
        init();
    }

    /**
     * Constructs a <code>CheckBoxList</code> that displays the elements in the specified,
     * non-<code>null</code> model. All <code>CheckBoxList</code> constructors delegate to this
     * one.
     * <p/>
     *
     * @param dataModel the data model for this list
     *
     * @throws IllegalArgumentException if <code>dataModel</code> is <code>null</code>
     */
    public CheckBoxList(ListModel dataModel) {
        super(dataModel);
        init();
    }

    /**
     * Initialize the CheckBoxList.
     */
    protected void init() {
        _checkBoxListSelectionModel = createCheckBoxListSelectionModel(getModel());
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        _listCellRenderer = createCellRenderer();
        _handler = createHandler();
        _checkBoxListSelectionModel.addListSelectionListener(_handler);
        JideSwingUtilities.insertMouseListener(this, _handler, 0);
        addKeyListener(_handler);
        addPropertyChangeListener("model", _handler);
        ListModel model = getModel();
        if (model != null) {
            model.addListDataListener(_handler);
        }
    }

    protected CheckBoxListSelectionModel createCheckBoxListSelectionModel(ListModel model) {
        return new CheckBoxListSelectionModel(model);
    }

    /**
     * Creates the cell renderer.
     *
     * @return the cell renderer.
     */
    protected CheckBoxListCellRenderer createCellRenderer() {
        return new CheckBoxListCellRenderer();
    }

    /**
     * Creates the mouse listener and key listener used by CheckBoxList.
     *
     * @return the Handler.
     */
    protected Handler createHandler() {
        return new Handler(this);
    }

    @Override
    public ListCellRenderer getCellRenderer() {
        if (_listCellRenderer != null) {
            _listCellRenderer.setActualListRenderer(super.getCellRenderer());
            return _listCellRenderer;
        }
        else {
            return super.getCellRenderer();
        }
    }

    public ListCellRenderer getActualCellRenderer() {
        return super.getCellRenderer();
    }

    protected static class Handler implements MouseListener, KeyListener, ListSelectionListener, PropertyChangeListener, ListDataListener {
        protected CheckBoxList _list;
        int _hotspot = new JCheckBox().getPreferredSize().width;


        public Handler(CheckBoxList list) {
            _list = list;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getOldValue() instanceof ListModel) {
                ((ListModel) evt.getOldValue()).removeListDataListener(this);
            }
            if (evt.getNewValue() instanceof ListModel) {
                _list.getCheckBoxListSelectionModel().setModel((ListModel) evt.getNewValue());
                ((ListModel) evt.getNewValue()).addListDataListener(this);
            }
        }

        protected boolean clicksInCheckBox(MouseEvent e) {
            int index = _list.locationToIndex(e.getPoint());
            Rectangle bounds = _list.getCellBounds(index, index);

            if (bounds != null) {
                if (_list.getComponentOrientation().isLeftToRight()) {
                    return e.getX() < bounds.x + _hotspot;
                }
                else {
                    return e.getX() > bounds.x + bounds.width - _hotspot;
                }
            }
            else {
                return false;
            }
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            if (!_list.isCheckBoxEnabled()) {
                return;
            }

            if (clicksInCheckBox(e)) {
                int index = _list.locationToIndex(e.getPoint());
                toggleSelection(index);
                e.consume();
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (!_list.isCheckBoxEnabled()) {
                return;
            }

            if (clicksInCheckBox(e)) {
                e.consume();
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

            if (!_list.isCheckBoxEnabled()) {
                return;
            }

            if (e.getModifiers() == 0 && e.getKeyChar() == KeyEvent.VK_SPACE)
                toggleSelections();
        }

        public void keyTyped(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
        }

        protected void toggleSelections() {
            int[] indices = _list.getSelectedIndices();
            CheckBoxListSelectionModel selectionModel = _list.getCheckBoxListSelectionModel();
            selectionModel.removeListSelectionListener(this);
            selectionModel.setValueIsAdjusting(true);
            try {
                if (indices.length > 0) {
                    boolean selected = selectionModel.isSelectedIndex(indices[0]);
                    for (int index : indices) {
                        if (!_list.isCheckBoxEnabled(index)) {
                            continue;
                        }
                        if (selected && selectionModel.isSelectedIndex(index)) {
                            selectionModel.removeSelectionInterval(index, index);
                        }
                        else if (!selected && !selectionModel.isSelectedIndex(index)) {
                            selectionModel.addSelectionInterval(index, index);
                        }
                    }
                }
            }
            finally {
                selectionModel.setValueIsAdjusting(false);
                selectionModel.addListSelectionListener(this);
                _list.repaint();
            }
        }


        public void valueChanged(ListSelectionEvent e) {
            _list.repaint();
        }

        protected void toggleSelection(int index) {
            if (!_list.isEnabled() || !_list.isCheckBoxEnabled(index)) {
                return;
            }

            CheckBoxListSelectionModel selectionModel = _list.getCheckBoxListSelectionModel();
            boolean selected = selectionModel.isSelectedIndex(index);
            selectionModel.removeListSelectionListener(this);
            try {
                if (selected)
                    selectionModel.removeSelectionInterval(index, index);
                else
                    selectionModel.addSelectionInterval(index, index);
            }
            finally {
                selectionModel.addListSelectionListener(this);
                _list.repaint();
            }
        }

        protected void toggleSelection() {
            int index = _list.getSelectedIndex();
            toggleSelection(index);
        }

        public void intervalAdded(ListDataEvent e) {
            int minIndex = Math.min(e.getIndex0(), e.getIndex1());
            int maxIndex = Math.max(e.getIndex0(), e.getIndex1());

            /* Sync the SelectionModel with the DataModel.
             */

            ListSelectionModel sm = _list.getCheckBoxListSelectionModel();
            if (sm != null) {
                sm.insertIndexInterval(minIndex, maxIndex - minIndex + 1, true);
            }
        }


        public void intervalRemoved(ListDataEvent e) {
            /* Sync the SelectionModel with the DataModel.
             */
            ListSelectionModel sm = _list.getCheckBoxListSelectionModel();
            if (sm != null) {
                sm.removeIndexInterval(e.getIndex0(), e.getIndex1());
            }
        }


        public void contentsChanged(ListDataEvent e) {
        }
    }

    @Override
    public int getNextMatch(String prefix, int startIndex, Position.Bias bias) {
        return -1;
    }

    /**
     * Gets the value of property checkBoxEnabled. If true, user can click on check boxes on each
     * tree node to select and unselect. If false, user can't click but you as developer can
     * programatically call API to select/unselect it.
     *
     * @return the value of property checkBoxEnabled.
     */
    public boolean isCheckBoxEnabled() {
        return _checkBoxEnabled;
    }

    /**
     * Checks if check box is enabled. There is no setter for it. The only way is to override this
     * method to return true or false.
     *
     * @param index the row index.
     *
     * @return true or false. If false, the check box on the particular row index will be disabled.
     */
    public boolean isCheckBoxEnabled(int index) {
        return true;
    }

    /**
     * Checks if check box is visible. There is no setter for it. The only way is to override this
     * method to return true or false.
     *
     * @param index whether the check box on the row index is visible.
     *
     * @return true or false. If false, there is not check box on the particular row index. By
     *         default, we always return true. You override this method to return true of false
     *         depending on your need.
     */
    public boolean isCheckBoxVisible(int index) {
        return true;
    }

    /**
     * Sets the value of property checkBoxEnabled.
     *
     * @param checkBoxEnabled true to allow to check the check box. False to disable it which means
     *                        user can see whether a row is checked or not but they cannot change
     *                        it.
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
     * Gets the ListSelectionModel that keeps the check boxes' state information for CheckBoxList.
     *
     * @return the ListSelectionModel that keeps the check boxes' state information for
     *         CheckBoxList.
     */
    public CheckBoxListSelectionModel getCheckBoxListSelectionModel() {
        return _checkBoxListSelectionModel;
    }

    public void setCheckBoxListSelectionModel(CheckBoxListSelectionModel checkBoxListSelectionModel) {
        _checkBoxListSelectionModel = checkBoxListSelectionModel;
        _checkBoxListSelectionModel.setModel(getModel());
    }

    /**
     * Returns an array of all of the selected indices in increasing order.
     *
     * @return all of the selected indices, in increasing order
     *
     * @see #removeSelectionInterval
     * @see #addListSelectionListener
     */
    public int[] getCheckBoxListSelectedIndices() {
        ListSelectionModel sm = getCheckBoxListSelectionModel();
        int iMin = sm.getMinSelectionIndex();
        int iMax = sm.getMaxSelectionIndex();

        if ((iMin < 0) || (iMax < 0)) {
            return new int[0];
        }

        int[] rvTmp = new int[1 + (iMax - iMin)];
        int n = 0;
        for (int i = iMin; i <= iMax; i++) {
            if (sm.isSelectedIndex(i)) {
                rvTmp[n++] = i;
            }
        }
        int[] rv = new int[n];
        System.arraycopy(rvTmp, 0, rv, 0, n);
        return rv;
    }


    /**
     * Selects a single cell and clear all other selections.
     *
     * @param index the index of the one cell to select
     *
     * @see ListSelectionModel#setSelectionInterval
     * @see #isSelectedIndex
     * @see #addListSelectionListener
     */
    public void setCheckBoxListSelectedIndex(int index) {
        if (index >= 0 && index < getModel().getSize()) {
            getCheckBoxListSelectionModel().setSelectionInterval(index, index);
        }
    }

    /**
     * Selects a single cell and keeps all previous selections.
     *
     * @param index the index of the one cell to select
     *
     * @see ListSelectionModel#setSelectionInterval
     * @see #isSelectedIndex
     * @see #addListSelectionListener
     */
    public void addCheckBoxListSelectedIndex(int index) {
        if (index >= 0 && index < getModel().getSize()) {
            getCheckBoxListSelectionModel().addSelectionInterval(index, index);
        }
    }

    /**
     * Deselects a single cell.
     *
     * @param index the index of the one cell to select
     *
     * @see ListSelectionModel#setSelectionInterval
     * @see #isSelectedIndex
     * @see #addListSelectionListener
     */
    public void removeCheckBoxListSelectedIndex(int index) {
        if (index >= 0 && index < getModel().getSize()) {
            getCheckBoxListSelectionModel().removeSelectionInterval(index, index);
        }
    }

    /**
     * Selects a set of cells.
     *
     * @param indices an array of the indices of the cells to select
     *
     * @see ListSelectionModel#addSelectionInterval
     * @see #isSelectedIndex
     * @see #addListSelectionListener
     */
    public void setCheckBoxListSelectedIndices(int[] indices) {
        ListSelectionModel sm = getCheckBoxListSelectionModel();
        try {
            sm.setValueIsAdjusting(true);
            sm.clearSelection();
            int size = getModel().getSize();
            for (int indice : indices) {
                if (indice >= 0 && indice < size) {
                    sm.addSelectionInterval(indice, indice);
                }
            }
        }
        finally {
            sm.setValueIsAdjusting(false);
        }
    }

    /**
     * Sets the selected elements.
     *
     * @param elements sets the select elements. All the rows that have the value in the array will
     *                 be checked.
     */
    public void setSelectedObjects(Object[] elements) {
        Map<Object, String> selected = new HashMap<Object, String>();
        for (Object element : elements) {
            selected.put(element, "");
        }
        setSelectedObjects(selected);
    }

    /**
     * Sets the selected elements.
     *
     * @param elements sets the select elements. All the rows that have the value in the Vector will
     *                 be checked.
     */
    public void setSelectedObjects(Vector<?> elements) {
        Map<Object, String> selected = new HashMap<Object, String>();
        for (Object element : elements) {
            selected.put(element, "");
        }
        setSelectedObjects(selected);
    }

    private void setSelectedObjects(Map<Object, String> selected) {
        int[] indices = new int[selected.size()];
        Arrays.fill(indices, -1);
        int index = 0;
        for (int i = 0; i < getModel().getSize(); i++) {
            Object elementAt = getModel().getElementAt(i);
            if (selected.get(elementAt) != null) {
                indices[index++] = i;
            }
        }
        setCheckBoxListSelectedIndices(indices);
    }

    /**
     * @return the values that are checked.
     *
     * @deprecated We keep this method in order to be compatible with {@link
     *             CheckBoxListWithSelectable}. You should use {@link #getCheckBoxListSelectedValues()}
     *             instead. Or you can use {@link #getCheckBoxListSelectionModel()} to get the
     *             selection model and get the selected indices from there.
     */
    public Object[] getSelectedObjects() {
        return getCheckBoxListSelectedValues();
    }

    /**
     * Returns an array of the values for the selected cells. The returned values are sorted in
     * increasing index order.
     *
     * @return the selected values or an empty list if nothing is selected
     *
     * @see #isSelectedIndex
     * @see #getModel
     * @see #addListSelectionListener
     */
    public Object[] getCheckBoxListSelectedValues() {
        ListSelectionModel sm = getCheckBoxListSelectionModel();
        ListModel dm = getModel();

        int iMin = sm.getMinSelectionIndex();
        int iMax = sm.getMaxSelectionIndex();

        if ((iMin < 0) || (iMax < 0)) {
            return new Object[0];
        }

        Object[] rvTmp = new Object[1 + (iMax - iMin)];
        int n = 0;
        for (int i = iMin; i <= iMax; i++) {
            if (sm.isSelectedIndex(i)) {
                rvTmp[n++] = dm.getElementAt(i);
            }
        }
        Object[] rv = new Object[n];
        System.arraycopy(rvTmp, 0, rv, 0, n);
        return rv;
    }


    /**
     * Returns the first selected index; returns -1 if there is no selected item.
     *
     * @return the value of <code>getMinSelectionIndex</code>
     *
     * @see #getMinSelectionIndex
     * @see #addListSelectionListener
     */
    public int getCheckBoxListSelectedIndex() {
        return getCheckBoxListSelectionModel().getMinSelectionIndex();
    }


    /**
     * Returns the first selected value, or <code>null</code> if the selection is empty.
     *
     * @return the first selected value
     *
     * @see #getMinSelectionIndex
     * @see #getModel
     * @see #addListSelectionListener
     */
    public Object getCheckBoxListSelectedValue() {
        int i = getCheckBoxListSelectionModel().getMinSelectionIndex();
        return (i == -1) ? null : getModel().getElementAt(i);
    }

    /**
     * Selects the specified object from the list and clear all other selections.
     *
     * @param anObject     the object to select
     * @param shouldScroll true if the list should scroll to display the selected object, if one
     *                     exists; otherwise false
     */
    public void setCheckBoxListSelectedValue(Object anObject, boolean shouldScroll) {
        if (anObject == null)
            setSelectedIndex(-1);
        else {
            int i, c;
            ListModel dm = getModel();
            for (i = 0, c = dm.getSize(); i < c; i++)
                if (anObject.equals(dm.getElementAt(i))) {
                    setCheckBoxListSelectedIndex(i);
                    if (shouldScroll)
                        ensureIndexIsVisible(i);
                    repaint();  /** FIX-ME setSelectedIndex does not redraw all the time with the basic l&f**/
                    return;
                }
            setCheckBoxListSelectedIndex(-1);
        }
        repaint(); /** FIX-ME setSelectedIndex does not redraw all the time with the basic l&f**/
    }

    /**
     * Selects the specified object from the list and keep all previous selections.
     *
     * @param anObject     the object to be selected
     * @param shouldScroll true if the list should scroll to display the selected object, if one
     *                     exists; otherwise false
     */
    public void addCheckBoxListSelectedValue(Object anObject, boolean shouldScroll) {
        if (anObject != null) {
            int i, c;
            ListModel dm = getModel();
            for (i = 0, c = dm.getSize(); i < c; i++)
                if (anObject.equals(dm.getElementAt(i))) {
                    addCheckBoxListSelectedIndex(i);
                    if (shouldScroll)
                        ensureIndexIsVisible(i);
                    repaint();  /** FIX-ME setSelectedIndex does not redraw all the time with the basic l&f**/
                    return;
                }
        }
    }

    /**
     * Selects the specified objects from the list and keep all previous selections.
     *
     * @param objects the objects to be selected
     */
    public void addCheckBoxListSelectedValues(Object[] objects) {
        if (objects != null) {
            Map<Object, String> map = new HashMap();
            for (Object o : objects) {
                map.put(o, "");
            }
            int i, c;
            ListModel dm = getModel();
            boolean changed = false;
            for (i = 0, c = dm.getSize(); i < c; i++)
                if (map.get(dm.getElementAt(i)) != null) {
                    addCheckBoxListSelectedIndex(i);
                    changed = true;
                }
            if (changed) {
                repaint();
            }
            map.clear();
        }
    }

    /**
     * Deselects the specified object from the list.
     *
     * @param anObject     the object to select
     * @param shouldScroll true if the list should scroll to display the selected object, if one
     *                     exists; otherwise false
     */
    public void removeCheckBoxListSelectedValue(Object anObject, boolean shouldScroll) {
        if (anObject != null) {
            int i, c;
            ListModel dm = getModel();
            for (i = 0, c = dm.getSize(); i < c; i++)
                if (anObject.equals(dm.getElementAt(i))) {
                    removeCheckBoxListSelectedIndex(i);
                    if (shouldScroll)
                        ensureIndexIsVisible(i);
                    repaint();  /** FIX-ME setSelectedIndex does not redraw all the time with the basic l&f**/
                    return;
                }
        }
    }

    public void clearCheckBoxListSelection() {
        getCheckBoxListSelectionModel().clearSelection();
    }

    /**
     * Selects all objects in this list except those are disabled.
     */
    public void selectAll() {
        getCheckBoxListSelectionModel().setSelectionInterval(0, getModel().getSize() - 1);
    }

    /**
     * Unselects all objects in this list except those are disabled.
     */
    public void selectNone() {
        getCheckBoxListSelectionModel().removeIndexInterval(0, getModel().getSize() - 1);
    }
}
