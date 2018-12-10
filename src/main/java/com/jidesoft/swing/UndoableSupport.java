/*
 * @(#)UndoableSupport.java 1/23/2012
 *
 * Copyright 2002 - 2012 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

/**
 * A general interface to provide undoable support within Swing component.
 *
 * @since 3.3.4
 */
public interface UndoableSupport {
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
}
