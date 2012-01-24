/*
 * @(#)UndoableModel.java 1/23/2012
 *
 * Copyright 2002 - 2012 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;
import java.util.Vector;

/**
 * Undoable Model which could be implemented for a row based model to achieve the undo/redo feature for the model.
 *
 * @since 3.3.4
 */
public interface UndoableModel {
    /**
     * Gets the UndoManager.
     *
     * @return the UndoManager.
     */
    UndoManager getUndoManager();

    /**
     * Gets the UndoableEditSupport instance to add UndoableListener.
     *
     * @return the UndoableEditSupport instance.
     */
    UndoableEditSupport getUndoableEditSupport();

    /**
     * Begins the edit of the model.
     *
     * @param isUndoRedo the flag to indicate if this operation is triggered by undo/redo
     */
    void beginCompoundEdit(boolean isUndoRedo);

    /**
     * Ends the edit of the model.
     */
    void endCompoundEdit();

    /**
     * Inserts a row at the designated row index.
     *
     * @param rowIndex the row index
     * @param rowData  the row data
     */
    void undoableInsertRow(int rowIndex, Vector rowData);

    /**
     * Removes a row at the designated row index.
     *
     * @param rowIndex the row index
     */
    void undoableRemoveRow(int rowIndex);

    /**
     * Updates a row at the designated row index with the new row data .
     *
     * @param rowIndex    the row index
     * @param newRowData  the row data
     */
    void undoableUpdateRow(int rowIndex, Vector newRowData);
}
