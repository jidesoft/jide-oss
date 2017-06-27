package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class CheckBoxListSelectionModel extends DefaultListSelectionModel implements ListDataListener {
    private static final long serialVersionUID = -4133723317923726786L;
    private ListModel _model;
    private boolean _allEntryConsidered = true;
    private int _allEntryIndex = -1;
    private boolean _allEntryIndexSet = false;

    public CheckBoxListSelectionModel() {
        setSelectionMode(MULTIPLE_INTERVAL_SELECTION);
    }

    public CheckBoxListSelectionModel(ListModel model) {
        _model = model;
        if (isAllEntryConsidered()) {
            _allEntryIndex = findAllEntryIndex();
            _model.addListDataListener(this);
        }
        setSelectionMode(MULTIPLE_INTERVAL_SELECTION);
    }

    /**
     * Gets the index of the "all" entry in the CheckBoxList.
     *
     * @return the index of the "all" entry. -1 if no "all" entry exists.
     * @see #setAllEntryIndex(int)
     * @since 3.3.5
     */
    public int getAllEntryIndex() {
        return _allEntryIndex;
    }

    /**
     * Sets the index of the "all" entry in the CheckBoxList.
     * <p/>
     * If the CheckBoxList has an "all" entry, check that entry on/off will check/uncheck all other entries. Uncheck any
     * other entry will uncheck the "all" entry.
     *
     * @param allEntryIndex the index of the "all" entry.
     */
    public void setAllEntryIndex(int allEntryIndex) {
        _allEntryIndex = allEntryIndex;
        _allEntryIndexSet = _allEntryIndex != -1;
    }

    private int findAllEntryIndex() {
        if (getModel() == null) {
            return -1;
        }
        int size = getModel().getSize();
        for (int i = 0; i < size; i++) {
            if (JideSwingUtilities.equals(getModel().getElementAt(i), CheckBoxList.ALL_ENTRY)) {
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
            if (isAllEntryConsidered()) {
                _model.addListDataListener(this);
                _allEntryIndex = findAllEntryIndex();
            }
            else if (!_allEntryIndexSet) {
                _allEntryIndex = -1;
            }
        }
        if (oldLength > newLength) {
            removeIndexInterval(newLength, oldLength);
        }
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        if (isAllEntryConsidered()) {
            _allEntryIndex = findAllEntryIndex();
            updateAllEntryIf();
        }
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        if (isAllEntryConsidered()) {
            _allEntryIndex = findAllEntryIndex();
            updateAllEntryIf();
        }
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        if (isAllEntryConsidered()) {
            _allEntryIndex = findAllEntryIndex();
            updateAllEntryIf();
        }
    }

    protected void updateAllEntryIf() {
        if (_allEntryIndex != -1) {
            if (isSelectedIndex(_allEntryIndex))
                unselectAllIf();
            else
                selectAllIf();
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
        if (index < 0) {
            return;
        }
        if (before) {
            boolean old = isSelectedIndex(index);
            boolean adjusting = getValueIsAdjusting();
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
                super.setValueIsAdjusting(adjusting);
            }
        }
        else {
            super.insertIndexInterval(index, length, before);
        }
    }

    /**
     * Gets the flag indicating if this CheckBoxListSelectionModel should consider the CheckBoxList.ALL item if there is
     * one.
     *
     * @return true if need check. Otherwise false.
     * @see #setAllEntryConsidered(boolean)
     * @since 3.3.3
     */
    public boolean isAllEntryConsidered() {
        return _allEntryConsidered;
    }

    /**
     * Sets the flag indicating if this CheckBoxListSelectionModel should consider the CheckBoxList.ALL item if there is
     * one.
     * <p/>
     * By default, the flag is true. If you want to improve the performance and don't have "all" entry, or if you do
     * have an entry similar to "all" entry but want to treat it as a normal entry, please set it to false.
     *
     * @param allEntryConsidered the flag
     * @since 3.3.3
     */
    public void setAllEntryConsidered(boolean allEntryConsidered) {
        _allEntryConsidered = allEntryConsidered;
        if (_model != null) {
            _model.removeListDataListener(this);
        }
        if (isAllEntryConsidered()) {
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
        return selected || (getAllEntryIndex() >= 0 && super.isSelectedIndex(getAllEntryIndex()));
    }

    private boolean selectAll(int index0, int index1) {
        if (getAllEntryIndex() < 0) {
            return false;
        }
        if ((index0 == 0 && index1 == getModel().getSize() - 1) || (index1 == 0 && index0 == getModel().getSize() - 1)) {
            return false;
        }
        if ((index0 >= getAllEntryIndex() && index1 <= getAllEntryIndex()) || (index1 >= getAllEntryIndex() && index0 <= getAllEntryIndex())) {
            setSelectionInterval(0, getModel().getSize() - 1);
            return true;
        }
        else {
            return false;
        }
    }

    private boolean unselectAll(int index0, int index1) {
        if (getAllEntryIndex() < 0) {
            return false;
        }
        if (index0 == getAllEntryIndex() || index1 == getAllEntryIndex()) {
            clearSelection();
            return true;
        }
        else {
            return false;
        }
    }

    private void selectAllIf() {
        if (getAllEntryIndex() < 0) {
            return;
        }
        for (int i = getModel().getSize() - 1; i >= 0; i--) {
            if (i != getAllEntryIndex() && !super.isSelectedIndex(i)) {
                return;
            }
        }
        super.addSelectionInterval(getAllEntryIndex(), getAllEntryIndex());
    }

    private void unselectAllIf() {
        if (getAllEntryIndex() < 0) {
            return;
        }
        for (int i = getModel().getSize() - 1; i >= 0; i--) {
            if (i != getAllEntryIndex() && !super.isSelectedIndex(i)) {
                super.removeSelectionInterval(getAllEntryIndex(), getAllEntryIndex());
                return;
            }
        }
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
        boolean adjusting = getValueIsAdjusting();
        setValueIsAdjusting(true);
        try {
            if (!selectAll(index0, index1)) {
                super.setSelectionInterval(index0, index1);
                selectAllIf();
            }
        }
        finally {
            setValueIsAdjusting(adjusting);
        }
    }

    @Override
    public int getMinSelectionIndex() {
        int index = super.getMinSelectionIndex();
        if (getAllEntryIndex() < 0) {
            return index;
        }
        if (super.isSelectedIndex(getAllEntryIndex()) && getAllEntryIndex() == 0) {
            return 1;
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
        boolean adjusting = getValueIsAdjusting();
        setValueIsAdjusting(true);
        try {
            if (!selectAll(index0, index1)) {
                super.addSelectionInterval(index0, index1);
                selectAllIf();
            }
        }
        finally {
            setValueIsAdjusting(adjusting);
        }
    }

    // implements javax.swing.ListSelectionModel

    @Override
    public void removeSelectionInterval(int index0, int index1) {
        boolean adjusting = getValueIsAdjusting();
        setValueIsAdjusting(true);
        try {
            if (!unselectAll(index0, index1)) {
                if (getAllEntryIndex() >= 0) {
                    super.removeSelectionInterval(getAllEntryIndex(), getAllEntryIndex());
                }
                super.removeSelectionInterval(index0, index1);
            }
        }
        finally {
            setValueIsAdjusting(adjusting);
        }
    }
}
