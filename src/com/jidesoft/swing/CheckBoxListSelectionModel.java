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
        _model = model;
    }
}
