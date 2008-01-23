/*
 * @(#)CheckBoxTreeCellRenderer.java 8/11/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.Serializable;


/**
 * Renderers an item in a tree using JCheckBox.
 */
public class CheckBoxTreeCellRenderer extends NullPanel implements TreeCellRenderer, Serializable {

    protected static Border noFocusBorder;

    /**
     * The checkbox that is used to paint the check box in cell renderer
     */
    protected TristateCheckBox _checkBox = null;
    protected JComponent _emptyBox = null;
    protected JCheckBox _protoType;

    /**
     * The label which appears after the check box.
     */
    protected TreeCellRenderer _actualTreeRenderer;

    /**
     * Constructs a default renderer object for an item
     * in a list.
     */
    public CheckBoxTreeCellRenderer() {
        this(null);
    }

    public CheckBoxTreeCellRenderer(TreeCellRenderer renderer) {
        if (noFocusBorder == null) {
            noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        }
        _protoType = new TristateCheckBox();
        _checkBox = createCheckBox();
        _emptyBox = (JComponent) Box.createHorizontalStrut(_protoType.getPreferredSize().width);
        _checkBox.setOpaque(false);
        setBorder(noFocusBorder);
        setLayout(new BorderLayout(0, 0));
        _actualTreeRenderer = renderer;
    }

    private TristateCheckBox createCheckBox() {
        return new NullTristateCheckBox();
    }

    public TreeCellRenderer getActualTreeRenderer() {
        return _actualTreeRenderer;
    }

    public void setActualTreeRenderer(TreeCellRenderer actualTreeRenderer) {
        _actualTreeRenderer = actualTreeRenderer;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        _checkBox.setPreferredSize(new Dimension(_protoType.getPreferredSize().width, 0));
        _emptyBox.setPreferredSize(new Dimension(_protoType.getPreferredSize().width, 0));
        setComponentOrientation(tree.getComponentOrientation());

        TreePath path = tree.getPathForRow(row);
        if (path != null && tree instanceof CheckBoxTree) {
            CheckBoxTreeSelectionModel selectionModel = ((CheckBoxTree) tree).getCheckBoxTreeSelectionModel();
            if (selectionModel != null) {
                _checkBox.setEnabled(((CheckBoxTree) tree).isCheckBoxEnabled() && ((CheckBoxTree) tree).isCheckBoxEnabled(path));
                if (selectionModel.isPathSelected(path, selectionModel.isDigIn()))
                    _checkBox.setState(TristateCheckBox.SELECTED);
                else
                    _checkBox.setState(selectionModel.isDigIn() && selectionModel.isPartiallySelected(path) ? null : TristateCheckBox.NOT_SELECTED);
            }
        }

        if (_actualTreeRenderer != null) {
            JComponent treeCellRendererComponent = (JComponent) _actualTreeRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            Border border = treeCellRendererComponent.getBorder();
            setBorder(border);
            treeCellRendererComponent.setBorder(BorderFactory.createEmptyBorder());
            if (path == null || !(tree instanceof CheckBoxTree) || ((CheckBoxTree) tree).isCheckBoxVisible(path)) {
                remove(_emptyBox);
                add(_checkBox, BorderLayout.BEFORE_LINE_BEGINS);
            }
            else {
                remove(_checkBox);
                add(_emptyBox, BorderLayout.AFTER_LINE_ENDS); // expand the tree node size to be the same as the one with check box.
            }
            add(treeCellRendererComponent);
        }

        return this;
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        if (_actualTreeRenderer instanceof JComponent) {
            Point p = event.getPoint();
            p.translate(-_checkBox.getWidth(), 0);
            MouseEvent newEvent = new MouseEvent(((JComponent) _actualTreeRenderer), event.getID(),
                    event.getWhen(),
                    event.getModifiers(),
                    p.x, p.y, event.getClickCount(),
                    event.isPopupTrigger());

            String tip = ((JComponent) _actualTreeRenderer).getToolTipText(
                    newEvent);

            if (tip != null) {
                return tip;
            }
        }
        return super.getToolTipText(event);
    }

}
