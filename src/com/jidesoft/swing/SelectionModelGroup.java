/*
 * @(#)SelectionModelGroup.java 4/14/2008
 *
 * Copyright 2002 - 2008 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * This class is used to create a multiple-exclusion scope for a set of any selection model so that one selection model
 * can have selected index at a time.
 */
abstract public class SelectionModelGroup<T, V> implements Serializable {
    protected List<T> _models = new Vector();

    protected V _selectionListener;

    /**
     * Creates a new <code>SelectionModelGroup</code>.
     */
    public SelectionModelGroup() {
        _selectionListener = createSelectionListener();
    }

    abstract protected V createSelectionListener();

    abstract protected void addSelectionListener(T model, V listener);

    abstract protected void removeSelectionListener(T model, V listener);

    /**
     * Adds the ListSelectionModel to the group.
     *
     * @param model the ListSelectionModel to be added
     */
    public void add(T model) {
        if (model == null) {
            return;
        }
        if (!_models.contains(model)) {
            _models.add(model);
            addSelectionListener(model, _selectionListener);
        }
    }

    /**
     * Adds the ListSelectionModel to the group.
     *
     * @param model the ListSelectionModel to be added
     * @param index the index
     */
    public void add(int index, T model) {
        if (model == null) {
            return;
        }
        if (!_models.contains(model)) {
            if (index < 0 || index > _models.size()) {
                _models.add(model);
            }
            else {
                _models.add(index, model);
            }
            addSelectionListener(model, _selectionListener);
        }
    }

    /**
     * Removes the T from the group.
     *
     * @param model the T to be removed
     */
    public void remove(T model) {
        if (model == null) {
            return;
        }
        if (_models.remove(model)) {
            removeSelectionListener(model, _selectionListener);
        }
    }

    /**
     * Gets the registered models.
     *
     * @return the models.
     */
    public List<T> getElements() {
        return Collections.unmodifiableList(_models);
    }

    /**
     * Returns all the T that are participating in this group.
     *
     * @return an array of all Ts
     */
    public List<T> getModels() {
        return _models;
    }

    /**
     * Returns the number of T in the group.
     *
     * @return the T count
     */
    public int getModelCount() {
        if (_models == null) {
            return 0;
        }
        else {
            return _models.size();
        }
    }
}
