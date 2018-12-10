/*
 * @(#)NavigationHelper.java 11/5/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import com.jidesoft.utils.ColorUtils;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;

/**
 * <code>NavigationComponentHelper</code> is a helper class to implement on JTree, JList and JTable (or JIDE table
 * subclasses) so they can be used for the navigation purpose. In order to make the component suitable for the
 * navigation, we want the selection effect to be easily noticeable and covers the row (instead of just one cell or one
 * node in the case of JTable and JTree respectively). We also want to have rollover effect the mouse is over a row.
 * Further more, the selection should have different color when the component is focused so that when multiple
 * navigation components are used, we can tell which one is active. Some L&Fs already do it by default but the most L&Fs
 * don't do it. This class provides some common code to make the implementation easy.
 */
abstract public class NavigationComponentHelper {
    private int _rolloverRow = -1;
    private Point _mousePosition = null;

    /**
     * Gets the bounds of the row.
     *
     * @param row the bounds of the specific row.
     * @return the bounds of the row. Or null if there is no row at all or the specified row doesn't exist.
     */
    protected abstract Rectangle getRowBounds(int row);

    protected abstract int rowAtPoint(Point p);

    protected abstract int[] getSelectedRows();

    @SuppressWarnings({"UnusedParameters"})
    public void mouseMoved(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
    }

    /**
     * Paints the rollover row and selection rows.
     *
     * @param g the Graphics
     * @param c the component
     */
    public void paint(Graphics g, JComponent c) {
        if (_rolloverRow != -1) {
            paintRolloverRow(g, c, _rolloverRow);
        }
        int[] rows = getSelectedRows();
        if (rows != null) {
            for (int row : rows) {
                paintSelectedRow(g, c, row);
            }
        }
    }

    /**
     * Paints the selected row. This method is called after the tree is painted. It will paint over the content of the
     * tree. In order to not cover the content, the painting code must be semi-transparent. By default, we paint it
     * using the selection color which can be retrieved from UIDefault "Tree.selectionBackground"  but with an alpha
     * between 70 to 100 to create a gradient effect.
     *
     * @param g   the Graphics
     * @param c   the component
     * @param row the row index
     */
    protected void paintSelectedRow(Graphics g, JComponent c, int row) {
        Color selectedColor = getSelectionColor(c);
        Rectangle bounds = getRowBounds(row);
        if (bounds != null) {
            bounds.width -= 1;
            bounds.height -= 1;
            paintRow(g, row, bounds, selectedColor, 30, 70, 50, 128);
        }
    }

    /**
     * Gets the color to paint the selected rows.
     *
     * @param c the component
     * @return the selection color.
     * @since 3.4.6
     */
    protected Color getSelectionColor(JComponent c) {
        Color selectedColor = UIManager.getColor("NavigationComponent.selectionBackground");
        if (selectedColor == null) {
            selectedColor = UIManager.getColor("Tree.selectionBackground");
        }
        if (!c.hasFocus()) {
            selectedColor = ColorUtils.toGrayscale(selectedColor).brighter();
            if (Color.WHITE.equals(selectedColor)) {
                selectedColor = new Color(202, 202, 202);
            }
        }
        return selectedColor;
    }

    /**
     * Paints the rollover row. This method is called after the tree is painted. It will paint over the content of the
     * tree. In order to not cover the content, the painting code must be semi-transparent. By default, we paint it
     * using the selection color which can be retrieved from UIDefault "Tree.selectionBackground"  but with an alpha
     * between 10 to 40 to create a gradient effect.
     *
     * @param g   the Graphics
     * @param c   the component
     * @param row the row index
     */
    @SuppressWarnings({"UnusedParameters"})
    protected void paintRolloverRow(Graphics g, JComponent c, int row) {
        Color selectedColor = UIManager.getColor("Tree.selectionBackground");
        Rectangle bounds = getRowBounds(row);
        if (bounds != null) {
            bounds.width -= 1;
            bounds.height -= 1;
            paintRow(g, row, bounds, selectedColor, 10, 40, 20, 100);
        }
    }

    @SuppressWarnings({"UnusedParameters"})
    private void paintRow(Graphics g, int row, Rectangle bounds, Color color, int a1, int a2, int a3, int a4) {
        Object o = JideSwingUtilities.setupShapeAntialiasing(g);
        ((Graphics2D) g).setPaint(new LinearGradientPaint(bounds.x, bounds.y, bounds.x, bounds.y + bounds.height, new float[]{0.5f, 0.95f, 1f}, new Color[]{
                new Color(color.getRed(), color.getGreen(), color.getBlue(), a1),
                new Color(color.getRed(), color.getGreen(), color.getBlue(), a2),
                new Color(color.getRed(), color.getGreen(), color.getBlue(), a3)
        }, MultipleGradientPaint.CycleMethod.NO_CYCLE));
        int cornerSize = 5;
        g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, cornerSize, cornerSize);
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), a4));
        g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, cornerSize, cornerSize);
        JideSwingUtilities.restoreShapeAntialiasing(g, o);
    }

    public void setup(final JComponent c) {
        MouseInputAdapter inputAdapter = new MouseInputAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (c != null) {
                    _mousePosition = null;
                    int old = _rolloverRow;
                    _rolloverRow = -1;
                    if (old != -1) {
                        Rectangle bounds = getRowBounds(old);
                        if (bounds != null) {
                            c.repaint(bounds);
                        }
                    }
                }
                NavigationComponentHelper.this.mouseExited(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                NavigationComponentHelper.this.mouseEntered(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                NavigationComponentHelper.this.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                NavigationComponentHelper.this.mouseReleased(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                NavigationComponentHelper.this.mouseClicked(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (c != null) {
                    int row = rowAtPoint(e.getPoint());
                    if (row != -1) {
                        Rectangle bounds = getRowBounds(row);
                        if (c instanceof JTree) {
                            int maxIconSize = bounds != null ? bounds.height : ((JTree) c).getRowHeight();
                            if (_mousePosition != null) {
                                c.repaint(new Rectangle(_mousePosition.x - maxIconSize, _mousePosition.y - maxIconSize, 2 * maxIconSize, 2 * maxIconSize));
                            }
                            _mousePosition = e.getPoint();
                            if (_mousePosition != null) {
                                c.repaint(new Rectangle(_mousePosition.x - maxIconSize, _mousePosition.y - maxIconSize, 2 * maxIconSize, 2 * maxIconSize));
                            }
                        }
                        if (_rolloverRow != row) {
                            int old = _rolloverRow;
                            _rolloverRow = row;
                            if (old != -1) {
                                Rectangle oldBounds = getRowBounds(old);
                                if (oldBounds != null) {
                                    c.repaint(oldBounds);
                                }
                            }
                            if (bounds != null) {
                                c.repaint(bounds);
                            }
                        }
                    }
                    else {
                        int old = _rolloverRow;
                        _rolloverRow = -1;
                        if (old != -1) {
                            Rectangle bounds = getRowBounds(old);
                            if (bounds != null) {
                                c.repaint(bounds);
                            }
                        }
                    }
                }
                NavigationComponentHelper.this.mouseMoved(e);
            }
        };
        c.addMouseMotionListener(inputAdapter);
        c.addMouseListener(inputAdapter);
        c.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                repaintSelections(c);
                NavigationComponentHelper.this.focusGained(e);
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaintSelections(c);
                NavigationComponentHelper.this.focusLost(e);
            }
        });
    }

    public void repaintSelections(JComponent c) {
        int[] rows = getSelectedRows();
        if (rows != null) {
            for (int row : rows) {
                Rectangle bounds = getRowBounds(row);
                if (bounds != null) {
                    bounds.x = 0;
                    bounds.width = c.getWidth();
                    c.repaint(bounds);
                }
            }
        }
        if (_rolloverRow != -1) {
            Rectangle bounds = getRowBounds(_rolloverRow);
            if (bounds != null) {
                c.repaint(bounds);
            }
        }
    }

    /**
     * Gets the rollover row that currently has rollover effect.
     *
     * @return the row that has the rollover effect.
     */
    public int getRolloverRow() {
        return _rolloverRow;
    }

    /**
     * Sets the rollover row.
     *
     * @param rolloverRow the row to show the rollover effect.
     */
    public void setRolloverRow(int rolloverRow) {
        _rolloverRow = rolloverRow;
    }

    public Point getMousePosition() {
        return _mousePosition;
    }
}
