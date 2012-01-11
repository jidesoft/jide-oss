package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class CheckBoxListSelectionModel extends DefaultListSelectionModel implements ListDataListener{
    private static final long serialVersionUID = -4133723317923726786L;
    private ListModel _model;
    private boolean _checkAllEntry = true;
    private int _allEntryIndex = -1;

    public CheckBoxListSelectionModel() {
        setSelectionMode(MULTIPLE_INTERVAL_SELECTION);
    }

    public CheckBoxListSelectionModel(ListModel model) {
        _model = model;
        if (isCheckAllEntry()) {
            _allEntryIndex = findAllEntryIndex();
            _model.addListDataListener(this);
        }
        setSelectionMode(MULTIPLE_INTERVAL_SELECTION);
    }

    private int findAllEntryIndex() {
        if (getModel() == null) {
            return -1;
        }
        for (int i = getModel().getSize() - 1; i >= 0; i--) {
            if (JideSwingUtilities.equals(getModel().getElementAt(i), CheckBoxList.ALL)) {
                return i;
            }
        }
        return -1;
    }

    public ListModel getModel() {
        return _model;
    }

    public void setModel(ListModel model) {
        int oldLength = 0;
        int newLength = 0;
        if (_model != null) {
            oldLength = _model.getSize();
            _model.removeListDataListener(this);
        }
        _model = model;
        if (_model != null) {
            newLength = _model.getSize();
            _model.removeListDataListener(this);
            _model.addListDataListener(this);
        }
        if (oldLength > newLength) {
            removeIndexInterval(newLength, oldLength);
        }
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        if (isCheckAllEntry()) {
            _allEntryIndex = findAllEntryIndex();
        }
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        if (isCheckAllEntry()) {
            _allEntryIndex = findAllEntryIndex();
        }
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        if (isCheckAllEntry()) {
            _allEntryIndex = findAllEntryIndex();
        }
    }

    /**
     * Overrides so that inserting a row will not be selected automatically if the row after it is selected.
     *
     * @param index  the index where the rows will be inserted.
     * @param length the number of the rows that will be inserted.
     * @param before it's before or after the index.
     */
    @Override
    public void insertIndexInterval(int index, int length, boolean before) {
        if (before) {
            boolean old = isSelectedIndex(index);
            super.setValueIsAdjusting(true);
            try {
                if (old) {
                    removeSelectionInterval(index, index);
                }
                super.insertIndexInterval(index, length, before);
                if (old) {
                    addSelectionInterval(index + length, index + length);
                }
            }
            finally {
                super.setValueIsAdjusting(false);
            }
        }
        else {
            super.insertIndexInterval(index, length, before);
        }
    }

    /**
     * Gets the flag indicating if this CheckBoxListSelectionModel should check for CheckBoxList.ALL item.
     *
     * @return true if need check. Otherwise false.
     * @see #setCheckAllEntry(boolean)
     * @since 3.3.3
     */
    public boolean isCheckAllEntry() {
        return _checkAllEntry;
    }

    /**
     * Sets the flag indicating if this CheckBoxListSelectionModel should check for CheckBoxList.ALL item.
     * <p/>
     * By default, the flag is true. If you want to improve the performance and don't have "all" entry, please set it to false.
     *
     * @param checkAllEntry the flag
     * @since 3.3.3
     */
    public void setCheckAllEntry(boolean checkAllEntry) {
        _checkAllEntry = checkAllEntry;
        if (_model != null) {
            _model.removeListDataListener(this);
        }
        if (isCheckAllEntry()) {
            _allEntryIndex = findAllEntryIndex();
            if (_model != null) {
                _model.addListDataListener(this);
            }
        }
        else {
            _allEntryIndex = -1;
        }
    }

    @Override
    public boolean isSelectedIndex(int index) {
        boolean selected = super.isSelectedIndex(index);
        return selected || (_allEntryIndex >= 0 && super.isSelectedIndex(_allEntryIndex));
    }

    private boolean selectAll(int index0, int index1) {
        if (_allEntryIndex < 0) {
            return false;
        }
        if ((index0 == 0 && index1 == getModel().getSize() - 1) || (index1 == 0 && index0 == getModel().getSize() - 1)) {
            return false;
        }
        if ((index0 >= _allEntryIndex && index1 <= _allEntryIndex) || (index1 >= _allEntryIndex && index0 <= _allEntryIndex)) {
            setSelectionInterval(0, getModel().getSize() - 1);
            return true;
        }
        else {
            return false;
        }
    }

    private boolean unselectAll(int index0, int index1) {
        if (_allEntryIndex < 0) {
            return false;
        }
        if (index0 == _allEntryIndex || index1 == _allEntryIndex) {
            clearSelection();
            return true;
        } else {
            return false;
        }
    }

    private void selectAllIf() {
        if (_allEntryIndex < 0) {
            return;
        }
        for (int i = getModel().getSize() - 1; i >= 0; i--) {
            if (i != _allEntryIndex && !isSelectedIndex(i)) {
                return;
            }
        }
        super.addSelectionInterval(_allEntryIndex, _allEntryIndex);
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
        if (!selectAll(index0, index1)) {
            super.setSelectionInterval(index0, index1);
            selectAllIf();
        }
    }

    @Override
    public int getMinSelectionIndex() {
        int index = super.getMinSelectionIndex();
        if (_allEntryIndex < 0) {
            return index;
        }
        if (super.isSelectedIndex(_allEntryIndex) && _allEntryIndex == 0) {
            return 1; // todo: should return _allEntryIndex or not?
        }
        return index;
    }

/*
    @Override
    public int getMaxSelectionIndex() {
        if (super.isSelectedIndex(0)) {
            return 0;
        }
        return super.getMaxSelectionIndex();
    }
*/

    @Override
    public void addSelectionInterval(int index0, int index1) {
        if (!selectAll(index0, index1)) {
            super.addSelectionInterval(index0, index1);
            selectAllIf();
        }
    }

    // implements javax.swing.ListSelectionModel
    @Override
    public void removeSelectionInterval(int index0, int index1) {
        if (!unselectAll(index0, index1)) {
            if (_allEntryIndex >= 0) {
                super.removeSelectionInterval(_allEntryIndex, _allEntryIndex);
            }
            super.removeSelectionInterval(index0, index1);
        }
    }
}
