package com.jidesoft.swing;

import javax.swing.*;

public class CheckBoxListSelectionModel extends DefaultListSelectionModel {
    private ListModel _model;

    public CheckBoxListSelectionModel() {
        setSelectionMode(MULTIPLE_INTERVAL_SELECTION);
    }

    public CheckBoxListSelectionModel(ListModel model) {
        _model = model;
        setSelectionMode(MULTIPLE_INTERVAL_SELECTION);
    }

    public ListModel getModel() {
        return _model;
    }

    public void setModel(ListModel model) {
        int oldLength = 0;
        int newLength = 0;
        if (_model != null) {
            oldLength = _model.getSize();
        }
        _model = model;
        if (_model != null) {
            newLength = _model.getSize();
        }
        if (oldLength > newLength) {
            removeIndexInterval(newLength, oldLength);
        }
    }
}
