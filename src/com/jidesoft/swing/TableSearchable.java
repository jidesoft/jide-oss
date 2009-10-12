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
 * <code>TableSearchable</code> is an concrete implementation of {@link Searchable} that enables the search function in
 * JTable. <p>It's very simple to use it. Assuming you have a JTable, all you need to do is to call
 * <code><pre>
 * JTable table = ....;
 * TableSearchable searchable = new TableSearchable(table);
 * </pre></code>
 * Now the JTable will have the search function.
 * <p/>
 * As JTable is a two dimension data, the search is a little different from JList and JTree which both have one
 * dimension data. So there is a little work you need to do in order to convert from two dimension data to one dimension
 * data. We use the selection mode to determine how to convert. There is a special property called mainIndex. You can
 * set it using setMainIndex(). If the JTable is in row selection mode, mainIndex will be the column that you want
 * search at. Please note you can change mainIndex at any time.
 * <p/>
 * On the other hand, if the JTable is in column selection mode, mainIndex will be the row that you want search at.
 * There is one more case when cell selection is enabled. In this case, mainIndex will be ignore; all cells will be
 * searched.
 * <p/>
 * In three cases above, the keys for find next and find previous are different too. In row selection mode, up/down
 * arrow are the keys. In column selection mode, left/right arrow are keys. In cell selection mode, both up and left
 * arrow are keys to find previous occurrence, both down and right arrow are keys to find next occurrence.
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
 * Additional customization can be done on the base Searchable class such as background and foreground color,
 * keystrokes, case sensitivity,
 */
public class TableSearchable extends Searchable implements TableModelListener, PropertyChangeListener {

    private int[] _searchColumnIndices = {0};

    public TableSearchable(JTable table) {
        super(table);
    }

    @Override
    public void installListeners() {
        super.installListeners();
        if (_component instanceof JTable) {
            ((JTable) _component).getModel().addTableModelListener(this);
            _component.addPropertyChangeListener("model", this);
        }
    }

    @Override
    public void uninstallListeners() {
        super.uninstallListeners();
        if (_component instanceof JTable) {
            ((JTable) _component).getModel().removeTableModelListener(this);
            _component.removePropertyChangeListener("model", this);
        }
    }

    @Override
    protected void setSelectedIndex(int index, boolean incremental) {
        int majorIndex, minorIndex;
        JTable table = ((JTable) _component);
        if (isColumnSelectionAllowed(table)) {
            minorIndex = index;
            majorIndex = getMainIndex();
            addTableSelection(table, majorIndex, minorIndex, incremental);
        }
        else if (isRowSelectionAllowed(table)) {
            majorIndex = index;
            minorIndex = table.convertColumnIndexToView(getMainIndex());
            addTableSelection(table, majorIndex, minorIndex, incremental);
        }
        else { // cell selection allowed
            int columnCount = table.getColumnCount();
            if (columnCount == 0) {
                return;
            }
            majorIndex = index / columnCount;
            minorIndex = index % columnCount;
            addTableSelection(table, majorIndex, minorIndex, incremental);
        }
    }

    /**
     * Selects the cell at the specified row and column index. If incremental is true, the previous selection will not
     * be cleared. This method will use {@link JTable#changeSelection(int,int,boolean,boolean)} method to select the
     * cell if the row and column index is in the range and the cell was not selected. The last two parameters of
     * changeSelection is true and false respectively.
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
        // NOTES: must sync with TableShrinkSearchableSupport#isColumnSelectionAllowed.
        return getSearchColumnIndices().length == 1 && (table.getColumnSelectionAllowed() && !table.getRowSelectionAllowed());
    }

    /**
     * Is the row selection allowed?
     *
     * @param table the table.
     * @return true if the table is the row selection.
     */
    protected boolean isRowSelectionAllowed(JTable table) {
        // NOTES: must sync with TableShrinkSearchableSupport#isRowSelectionAllowed.
        return getSearchColumnIndices().length == 1 && (!table.getColumnSelectionAllowed() && table.getRowSelectionAllowed());
    }

    /**
     * Are we trying to search on multi-columns (but NOT all columns)?
     *
     * @return true if the search is set to look at multi-columns (but NOT all columns).
     */
    protected boolean isSearchSelectedRows() {
        return getSearchColumnIndices().length > 1;
    }

    /**
     * Gets the selected index.
     *
     * @return the selected index.
     */
    @Override
    protected int getSelectedIndex() {
        JTable table = (JTable) _component;
        if (isColumnSelectionAllowed(table)) {
            return table.getColumnModel().getSelectionModel().getAnchorSelectionIndex();
        }
        else if (isRowSelectionAllowed(table)) {
            return table.getSelectionModel().getAnchorSelectionIndex();
        }
        else { // cell selection allowed
            return table.getSelectionModel().getAnchorSelectionIndex() * table.getColumnCount() + table.getColumnModel().getSelectionModel().getAnchorSelectionIndex();
        }
    }

    @Override
    protected Object getElementAt(int index) {
        JTable table = (JTable) _component;
        if (isColumnSelectionAllowed(table)) { // column selection mode
            return getValueAt(table, getMainIndex(), index);
        }
        else if (isRowSelectionAllowed(table)) { // row selection mode
            return getValueAt(table, index, table.convertColumnIndexToView(getMainIndex()));
        }
        else if (isSearchSelectedRows()) { // search on multi columns
            int columnIndex = index % table.getColumnCount();
            boolean doNotSearch = true;
            for (int i : getSearchColumnIndices()) {
                if (i == columnIndex) {
                    doNotSearch = false;
                }
            }

            if (doNotSearch) {
                return null;
            }

            int rowIndex = index / table.getColumnCount();
            return getValueAt(table, rowIndex, columnIndex);
        }
        else { // cell selection allowed
            int columnIndex = index % table.getColumnCount();
            int rowIndex = index / table.getColumnCount();
            return getValueAt(table, rowIndex, columnIndex);
        }
    }

    private Object getValueAt(JTable table, int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < table.getRowCount() && columnIndex >= 0 && columnIndex < table.getColumnCount()) {
            return table.getValueAt(rowIndex, columnIndex);
        }
        else {
            return null;
        }
    }

    @Override
    protected int getElementCount() {
        JTable table = ((JTable) _component);
        if (isColumnSelectionAllowed(table)) {
            return table.getColumnCount();
        }
        else if (isRowSelectionAllowed(table)) {
            return table.getRowCount();
        }
        else { // cell selection allowed
            return table.getColumnCount() * table.getRowCount();
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
     * Gets the indexes of the column to be searched.
     *
     * @return the indexes of the column to be searched.
     */
    public int[] getSearchColumnIndices() {
        return _searchColumnIndices;
    }

    /**
     * Gets the index of the column to be searched.
     *
     * @return the index of the column to be searched.
     */
    public int getMainIndex() {
        if (_searchColumnIndices.length == 0) {
            return -1;
        }

        return _searchColumnIndices[0];
    }

    /**
     * Sets the main indexes. Main indexes are the columns index which you want to be searched.
     *
     * @param columnIndices the index of the columns to be searched. If empty, all columns will be searched.
     */
    public void setSearchColumnIndices(int[] columnIndices) {
        if (columnIndices == null) {
            columnIndices = new int[0];
        }

        int[] old = _searchColumnIndices;
        if (!JideSwingUtilities.equals(old, columnIndices, true)) {
            _searchColumnIndices = columnIndices;
            hidePopup();
        }
    }

    /**
     * Sets the main index. Main index is the column index which you want to be searched.
     *
     * @param mainIndex the index of the column to be searched. If -1, all columns will be searched.
     */
    public void setMainIndex(int mainIndex) {
        int[] temp = {mainIndex};
        if (mainIndex < 0) {
            temp = new int[0];
        }
        int[] old = _searchColumnIndices;
        if (old != temp) {
            _searchColumnIndices = temp;
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
        if (isProcessModelChangeEvent()) {
            hidePopup();
            fireSearchableEvent(new SearchableEvent(this, SearchableEvent.SEARCHABLE_MODEL_CHANGE));
        }
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
        int selectedRow = ((JTable) _component).getSelectedRow();
        int selectedColumn = ((JTable) _component).getSelectedColumn();
        return selectedRow != -1 && selectedColumn != -1 && ((JTable) _component).isCellEditable(selectedRow, selectedColumn);
    }
}
