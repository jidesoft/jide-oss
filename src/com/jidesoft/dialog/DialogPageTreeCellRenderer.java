/*
 * @(#)DialogPageTreeCellRenderer.java
 *
 * Copyright 2002 - 2003 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.dialog;

import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * A tree cell renderer for AbstractDialogPage.
 */
class DialogPageTreeCellRenderer extends JLabel implements TreeCellRenderer {
    protected Color _textSelectionColor;
    protected Color _textNonSelectionColor;
    protected Color _bkSelectionColor;
    protected Color _bkNonSelectionColor;
    protected Color _borderSelectionColor;

    protected boolean m_selected;

    private static final Icon SELECTED = TreeIconsFactory.getImageIcon(TreeIconsFactory.CellRenderer.SELECTED_B16);
    private static final Icon BLANK = TreeIconsFactory.getImageIcon(TreeIconsFactory.CellRenderer.BLANK_16);

    public DialogPageTreeCellRenderer() {
        super();
        _textSelectionColor = UIDefaultsLookup.getColor("Tree.selectionForeground");
        _textNonSelectionColor = UIDefaultsLookup.getColor("Tree.textForeground");
        _bkSelectionColor = UIDefaultsLookup.getColor("Tree.selectionBackground");
        _bkNonSelectionColor = UIDefaultsLookup.getColor("Tree.textBackground");
        _borderSelectionColor = UIDefaultsLookup.getColor("Tree.selectionBorderColor");
        setOpaque(false);
    }

    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value, boolean sel, boolean expanded, boolean leaf,
                                                  int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object obj = node.getUserObject();

        if (obj instanceof Boolean)
            setText("Retrieving data...");

        if (obj instanceof AbstractDialogPage) {
            AbstractDialogPage idata = (AbstractDialogPage) obj;
            setText(idata.getTitle());
        }
        else {
            setText(obj.toString());
            setIcon(null);
        }

        setFont(tree.getFont());
        setForeground(sel ? _textSelectionColor :
                _textNonSelectionColor);
        setBackground(sel ? _bkSelectionColor :
                _bkNonSelectionColor);

        if (leaf) {
            if (sel) {
                setIcon(SELECTED);
            }
            else {
                setIcon(BLANK);
            }
        }
        else {
            if (expanded) {
                setIcon(UIDefaultsLookup.getIcon("Tree.openIcon"));
            }
            else {
                setIcon(UIDefaultsLookup.getIcon("Tree.closedIcon"));
            }
        }
        m_selected = sel;
        setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        return this;
    }

    @Override
    public void paintComponent(Graphics g) {
        Color bColor = getBackground();
        Icon icon = getIcon();

        g.setColor(bColor);
        int offset = 0;
        if (icon != null && getText() != null)
            offset = (icon.getIconWidth() + getIconTextGap()) - 1;
        g.fillRect(offset, 0, getWidth() - 1 - offset,
                getHeight() - 1);

        if (m_selected) {
            g.setColor(_borderSelectionColor);
            g.drawRect(offset, 0, getWidth() - offset - 1, getHeight() - 1);
        }
        super.paintComponent(g);
    }
}
