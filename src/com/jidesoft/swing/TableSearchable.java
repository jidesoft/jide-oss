/*
 * @(#)TableSearchable.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.swing.event.SearchableEvent;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * <code>TableSearchable</code> is an concrete implementation of {@link Searchable}
 * that enables the search function in JTable.
 * <p>It's very simple to use it. Assuming you have a JTable, all you need to do is to
 * call
 * <code><pre>
 * JTable table = ....;
 * TableSearchable searchable = new TableSearchable(table);
 * </pre></code>
 * Now the JTable will have the search function.
 * <p/>
 * As JTable is a two dimension data, the search is a little different from JList and JTree which both have
 * one dimension data. So there is a little work you need to do in order to convert from two dimension data
 * to one dimension data. We use the selection mode to determine how to convert. There is a special property
 * called mainIndex. You can set it using setMainIndex(). If the JTable is in row selection mode, mainIndex will
 * be the column that you want search at. Please note you can change mainIndex at any time.
 * <p/>
 * On the other hand, if the JTable is in column selection mode, mainIndex will be the row that you want search at.
 * There is one more case when cell selection is enabled. In this case, mainIndex will be ignore; all cells will be searched.
 * <p/>
 * In three cases above, the keys for find next and find previous are different too. In row selection mode, up/down arrow are the keys.
 * In column selection mode, left/right arrow are keys. In cell selection mode, both up and left arrow are keys to
 * find previous occurence, both down and right arrow are keys to find next occurence.
 * <p/>
 * In addition, you might need to override convertElementToString() to provide you own algorithm to do the conversion.
 * <code><pre>
 * JTable table = ....;
 * TableSearchable searchable = new TableSearchable(table) {
 *      protected String convertElementToString(Object object) {
 *          ...
 *      }
 * };
 * </pre></code>
 * <p/>
 * Additional customization can be done on the base Searchable class such as background and foreground color, keystrokes,
 * case sensitivity,
 */
public class TableSearchable extends Searchable implements TableModelListener, PropertyChangeListener {

    private int _mainIndex = 0;

    public TableSearchable(JTable table) {
        super(table);
        table.getModel().addTableModelListener(this);
        table.addPropertyChangeListener("model", this);
    }

    @Override
    public void uninstallListeners() {
        super.uninstallListeners();
        if (_component instanceof JTable) {
            ((JTable) _component).getModel().removeTableModelListener(this);
        }
        _component.removePropertyChangeListener("model", this);
    }

    @Override
    protected void setSelectedIndex(int index, boolean incremental) {
        int majorIndex, minorIndex;
        JTable table = ((JTable) _component);
        if (isColumnSelectionAllowed(table)) {
            majorIndex = index;
            minorIndex = getMainIndex();
            addTableSelection(table, majorIndex, minorIndex, incremental);
        }
        else if (isRowSelectionAllowed(table)) {
            majorIndex = index;
            minorIndex = getMainIndex();
            addTableSelection(table, majorIndex, minorIndex, incremental);
        }
        else { // cell selection allowed
            majorIndex = index / table.getColumnModel().getColumnCount();
            minorIndex = index % table.getColumnModel().getColumnCount();
            addTableSelection(table, majorIndex, minorIndex, incremental);
        }
    }

    /**
     * Selects the cell at the specified row and column index. If incremental is true, the previous selection will not be cleared.
     * This method will use {@link JTable#changeSelection(int,int,boolean,boolean)} method to select the cell
     * if the row and column index is in the range and the cell was not selected. The last two parameters of changeSelection
     * is true and false respectively.
     *
     * @param table       the table
     * @param rowIndex    the row index of the cell.
     * @param columnIndex the column index of the cell
     * @param incremental false to clear all previous selection. True to keep the previous selection.
     */
    protected void addTableSelection(JTable table, int rowIndex, int columnIndex, boolean incremental) {
        if (!incremental)
            table.clearSelection();
        if (rowIndex >= 0 && columnIndex >= 0 && rowIndex < table.getRowCount() && columnIndex < table.getColumnCount()
                && !table.isCellSelected(rowIndex, columnIndex)) {
            table.changeSelection(rowIndex, columnIndex, true, false);
        }
    }

    /**
     * Is the column selection allowed?
     *
     * @param table the table.
     * @return true if the table is the column selection.
     */
    protected boolean isColumnSelectionAllowed(JTable table) {
        return getMainIndex() != -1 && (table.getColumnSelectionAllowed() && !table.getRowSelectionAllowed());
    }

    /**
     * Is the row selection allowed?
     *
     * @param table the table.
     * @return true if the table is the row selection.
     */
    protected boolean isRowSelectionAllowed(JTable table) {
        return getMainIndex() != -1 && (!table.getColumnSelectionAllowed() && table.getRowSelectionAllowed());
    }

    /**
     * Gets the selected index.
     *
     * @return the selected index.
     */
    @Override
    protected int getSelectedIndex() {
        JTable table = ((JTable) _component);
        if (isColumnSelectionAllowed(table)) {
            return table.getColumnModel().getSelectionModel().getLeadSelectionIndex();
        }
        else if (isRowSelectionAllowed(table)) {
            return table.getSelectionModel().getLeadSelectionIndex();
        }
        else { // cell selection allowed
            return table.getSelectionModel().getLeadSelectionIndex() * table.getColumnCount() + table.getColumnModel().getSelectionModel().getLeadSelectionIndex();
        }
    }

    @Override
    protected Object getElementAt(int index) {
        TableModel model = ((JTable) _component).getModel();
        JTable table = ((JTable) _component);
        if (isColumnSelectionAllowed(table)) { // column selection mode
            return getValueAt(model, getMainIndex(), table.convertColumnIndexToModel(index));
        }
        else if (isRowSelectionAllowed(table)) { // row selection mode
            return getValueAt(model, index, table.convertColumnIndexToModel(getMainIndex()));
        }
        else { // cell selection allowed
            int columnIndex = index % table.getColumnModel().getColumnCount();
            int rowIndex = index / table.getColumnModel().getColumnCount();
            return getValueAt(model, rowIndex, table.convertColumnIndexToModel(columnIndex));
        }
    }

    private Object getValueAt(TableModel model, int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < model.getRowCount() && columnIndex >= 0 && columnIndex < model.getColumnCount()) {
            return model.getValueAt(rowIndex, columnIndex);
        }
        else {
            return null;
        }
    }

    @Override
    protected int getElementCount() {
        TableModel model = ((JTable) _component).getModel();
        JTable table = ((JTable) _component);
        if (isColumnSelectionAllowed(table)) {
            return table.getColumnModel().getColumnCount();
        }
        else if (isRowSelectionAllowed(table)) {
            return model.getRowCount();
        }
        else { // cell selection allowed
            return table.getColumnModel().getColumnCount() * model.getRowCount();
        }
    }

    @Override
    protected String convertElementToString(Object item) {
        if (item != null) {
            return item.toString();
        }
        else {
            return "";
        }
    }

    /**
     * Gets the index of the column to be searched.
     *
     * @return the index of the column to be searched.
     */
    public int getMainIndex() {
        return _mainIndex;
    }

    /**
     * Sets the main index. Main index is the column index which you want to be searched.
     *
     * @param mainIndex the index of the column to be searched. If -1, all columns will be searched.
     */
    public void setMainIndex(int mainIndex) {
        int old = _mainIndex;
        if (old != mainIndex) {
            _mainIndex = mainIndex;
            hidePopup();
        }
    }

    @Override
    protected boolean isFindNextKey(KeyEvent e) {
        int keyCode = e.getKeyCode();
        JTable table = ((JTable) _component);
        if (isColumnSelectionAllowed(table)) {
            return keyCode == KeyEvent.VK_RIGHT;
        }
        else if (isRowSelectionAllowed(table)) {
            return keyCode == KeyEvent.VK_DOWN;
        }
        else { // cell selection allowed
            return keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_RIGHT;
        }
    }

    @Override
    protected boolean isFindPreviousKey(KeyEvent e) {
        int keyCode = e.getKeyCode();
        JTable table = ((JTable) _component);
        if (isColumnSelectionAllowed(table)) {
            return keyCode == KeyEvent.VK_LEFT;
        }
        else if (isRowSelectionAllowed(table)) {
            return keyCode == KeyEvent.VK_UP;
        }
        else { // cell selection allowed
            return keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_LEFT;
        }
    }

    public void tableChanged(TableModelEvent e) {
        hidePopup();
        fireSearchableEvent(new SearchableEvent(this, SearchableEvent.SEARCHABLE_MODEL_CHANGE));
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("model".equals(evt.getPropertyName())) {
            hidePopup();

            if (evt.getOldValue() instanceof TableModel) {
                ((TableModel) evt.getOldValue()).removeTableModelListener(this);
            }

            if (evt.getNewValue() instanceof TableModel) {
                ((TableModel) evt.getNewValue()).addTableModelListener(this);
            }
            fireSearchableEvent(new SearchableEvent(this, SearchableEvent.SEARCHABLE_MODEL_CHANGE));
        }
    }

    @Override
    protected boolean isActivateKey(KeyEvent e) {
        boolean editable = isSelectedCellEditable();
        return !editable && super.isActivateKey(e);
    }

    /**
     * Checks if the selected cell is editable. If yes, we will not activate Searchable when key is typed.
     *
     * @return true if the selected cell is editable.
     */
    protected boolean isSelectedCellEditable() {
        int selectedRow = ((JTable) _component).getSelectionModel().getLeadSelectionIndex();
        int selectedColumn = ((JTable) _component).getColumnModel().getSelectionModel().getLeadSelectionIndex();
        return ((JTable) _component).isCellEditable(selectedRow, selectedColumn);
    }
}
