/*
 * @(#)Sticky.java 7/26/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * <code>Sticky</code> is a helper class to make JList or JTree changing selection when mouse moves. To use it, you
 * simply call
 * <pre><code>
 * JList list = new JList();
 * new Sticky(list);
 * </code></pre>
 * or
 * <pre><code>
 * JTree tree = new JTree();
 * new Sticky(tree);
 * </code></pre>
 */
public class Sticky {
    private JComponent _target;
    private static final StrickyMouseMotionListener STICKY_MOUSE_MOTION_LISTENER = new StrickyMouseMotionListener();

    public Sticky(JList list) {
        _target = list;
        install();
    }

    public Sticky(JTree tree) {
        _target = tree;
        install();
    }

    public Sticky(JTable table) {
        _target = table;
        install();
    }

    /**
     * Installs the listener to make the list or tree sticky. This method is called by constructor, so you don't need to
     * call it unless you called {@link #uninstall()} to remove the listener.
     */
    public void install() {
        _target.addMouseMotionListener(STICKY_MOUSE_MOTION_LISTENER);
    }

    /**
     * Uninstalls the listener.
     */
    public void uninstall() {
        _target.removeMouseMotionListener(STICKY_MOUSE_MOTION_LISTENER);
    }

    static private class StrickyMouseMotionListener extends MouseMotionAdapter {
        //
        // MouseMotionListener:
        // NOTE: this is added to both the List and ComboBox
        //
        @Override
        public void mouseMoved(MouseEvent anEvent) {
            if (anEvent.getSource() instanceof JList) {
                JList list = (JList) anEvent.getSource();
                Point location = anEvent.getPoint();
                Rectangle r = new Rectangle();
                list.computeVisibleRect(r);
                if (r.contains(location)) {
                    updateListSelectionForEvent(anEvent, list, false);
                }
            }
            else if (anEvent.getSource() instanceof JTree) {
                JTree tree = (JTree) anEvent.getSource();
                Point location = anEvent.getPoint();
                Rectangle r = new Rectangle();
                tree.computeVisibleRect(r);
                if (r.contains(location)) {
                    updateTreeSelectionForEvent(anEvent, tree, false);
                }
            }
            else if (anEvent.getSource() instanceof JTable) {
                JTable table = (JTable) anEvent.getSource();
                Point location = anEvent.getPoint();
                Rectangle r = new Rectangle();
                table.computeVisibleRect(r);
                if (r.contains(location)) {
                    updateTableSelectionForEvent(anEvent, table, false);
                }
            }
        }
    }

    /**
     * A utility method used by the event listeners.  Given a mouse event, it changes the list selection to the list
     * item below the mouse.
     */
    private static void updateListSelectionForEvent(MouseEvent anEvent, JList list, boolean shouldScroll) {
        // XXX - only seems to be called from this class. shouldScroll flag is
        // never true
        Point location = anEvent.getPoint();
        if (list == null)
            return;
        int index = list.locationToIndex(location);
        if (index == -1) {
            if (location.y < 0)
                index = 0;
            else
                index = list.getModel() == null ? 0 : list.getModel().getSize() - 1;
        }
        if (list.getSelectedIndex() != index && index >= 0 && index < list.getModel().getSize()) {
            list.setSelectedIndex(index);
            if (shouldScroll)
                list.ensureIndexIsVisible(index);
        }
    }

    /**
     * A utility method used by the event listeners.  Given a mouse event, it changes the list selection to the list
     * item below the mouse.
     */
    private static void updateTreeSelectionForEvent(MouseEvent anEvent, JTree tree, boolean shouldScroll) {
        Point location = anEvent.getPoint();
        if (tree == null)
            return;
        int index = tree.getRowForLocation(location.x, location.y);
        if (index != -1) {
            TreePath pathForRow = tree.getPathForRow(index);
            if (tree.getSelectionPath() != pathForRow) {
                tree.setSelectionRow(index);
                if (shouldScroll)
                    tree.makeVisible(pathForRow);
            }
        }
    }

    /**
     * A utility method used by the event listeners.  Given a mouse event, it changes the table selection to the table
     * item below the mouse.
     */
    private static void updateTableSelectionForEvent(MouseEvent anEvent, JTable table, boolean shouldScroll) {
        // XXX - only seems to be called from this class. shouldScroll flag is
        // never true
        Point location = anEvent.getPoint();
        if (table == null)
            return;
        int index = table.rowAtPoint(location);
        if (index == -1) {
            if (location.y < 0)
                index = 0;
            else
                index = table.getModel() == null ? 0 : table.getModel().getRowCount() - 1;
        }
        if (table.getSelectedRow() != index) {
            table.getSelectionModel().setSelectionInterval(index, index);
            if (shouldScroll)
                JideSwingUtilities.ensureRowVisible(table, index);
        }
    }
}
