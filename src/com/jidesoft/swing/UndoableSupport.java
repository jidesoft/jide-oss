/*
 * @(#)UndoableSupport.java 1/21/2012
 *
 * Copyright 2002 - 2012 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

/**
 * A general interface to provide undoable support within Swing component.
 */
public interface UndoableSupport {
    UndoManager getUndoManager();

    UndoableEditSupport getUndoableEditSupport();

    void beginCompoundEdit(boolean isUndoRedo);

    void endCompoundEdit();
}
