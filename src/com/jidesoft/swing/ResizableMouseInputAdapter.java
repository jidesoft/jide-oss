/*
 * @(#)ResizableMouseInputAdapter.java 3/4/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Mouse input listener to control the resizing of <code>Resizable</code> component.
 */
public class ResizableMouseInputAdapter extends MouseInputAdapter {

    /**
     * Starting bounds of the dragged component.
     */
    private static Rectangle _startingBounds;

    /**
     * _eventMouseScreenX is the mousePressed location in absolute coordinate system.
     */
    private int _eventMouseScreenX;
    /**
     * _eventMouseScreenY is the mousePressed location in absolute coordinate system.
     */
    private int _eventMouseScreenY;

    /**
     * The resize direction.
     */
    private int _resizeCorner;

    protected static final int RESIZE_NONE = 0;

    private boolean _discardRelease = false;

    private Resizable _resizable;

    public ResizableMouseInputAdapter(Resizable resizable) {
        _resizable = resizable;
    }

    private boolean isResizable(int resizeDir) {
        return _resizable != null && (_resizable.getResizableCorners() & resizeDir) != 0;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        _resizeCorner = RESIZE_NONE;

        _startingBounds = _resizable.getComponent().getBounds();
        if (_resizable.isTopLevel()) {
            Point location = new Point(_startingBounds.x, _startingBounds.y);
            SwingUtilities.convertPointToScreen(location, _resizable.getComponent());
            _startingBounds.x = location.x;
            _startingBounds.y = location.y;
        }

        Point p = new Point(e.getX(), e.getY());
        SwingUtilities.convertPointToScreen(p, (Component) e.getSource());
        _eventMouseScreenX = p.x;
        _eventMouseScreenY = p.y;

        if (e.getSource() instanceof Resizable.ResizeCorner) {
            Resizable.ResizeCorner corner = (Resizable.ResizeCorner) e.getSource();
            _resizeCorner = corner.getCorner();
        }
        // resize component
        else if (e.getSource() == _resizable.getComponent()) {
            Insets i = _resizable.getResizeInsets();
            if (e.getX() <= i.left) {
                if (i.top > 0 && e.getY() < _resizable.getResizeCornerSize() + i.top) {
                    _resizeCorner = Resizable.UPPER_LEFT;
                }
                else if (i.bottom > 0 && e.getY() > _resizable.getComponent().getHeight()
                        - _resizable.getResizeCornerSize() - i.bottom) {
                    _resizeCorner = Resizable.LOWER_LEFT;
                }
                else {
                    _resizeCorner = Resizable.LEFT;
                }
            }
            else if (i.right > 0 && e.getX() >= _resizable.getComponent().getWidth() - i.right) {
                if (i.top > 0 && e.getY() < _resizable.getResizeCornerSize() + i.top) {
                    _resizeCorner = Resizable.UPPER_RIGHT;
                }
                else if (i.bottom > 0 && e.getY() > _resizable.getComponent().getHeight()
                        - _resizable.getResizeCornerSize() - i.bottom) {
                    _resizeCorner = Resizable.LOWER_RIGHT;
                }
                else {
                    _resizeCorner = Resizable.RIGHT;
                }
            }
            else if (i.top > 0 && e.getY() <= i.top) {
                if (i.left > 0 && e.getX() < _resizable.getResizeCornerSize() + i.left) {
                    _resizeCorner = Resizable.UPPER_LEFT;
                }
                else if (i.right > 0 && e.getX() > _resizable.getComponent().getWidth()
                        - _resizable.getResizeCornerSize() - i.right) {
                    _resizeCorner = Resizable.UPPER_RIGHT;
                }
                else {
                    _resizeCorner = Resizable.UPPER;
                }
            }
            else if (i.bottom > 0 && e.getY() >= _resizable.getComponent().getHeight() - i.bottom) {
                if (i.left > 0 && e.getX() < _resizable.getResizeCornerSize() + i.left) {
                    _resizeCorner = Resizable.LOWER_LEFT;
                }
                else if (i.right > 0 && e.getX() > _resizable.getComponent().getWidth()
                        - _resizable.getResizeCornerSize() - i.right) {
                    _resizeCorner = Resizable.LOWER_RIGHT;
                }
                else {
                    _resizeCorner = Resizable.LOWER;
                }
            }
            else {
                /* the mouse press happened inside the frame, not in the
                   border */
                _discardRelease = true;
                return;
            }
        }

        Cursor s = Cursor.getDefaultCursor();
        if (isResizable(_resizeCorner)) {
            boolean ltr = _resizable.getComponent().getComponentOrientation().isLeftToRight();
            switch (_resizeCorner) {
                case Resizable.LOWER:
                    s = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
                    break;
                case Resizable.UPPER:
                    s = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
                    break;
                case Resizable.LEFT:
                    s = Cursor.getPredefinedCursor(ltr ? Cursor.W_RESIZE_CURSOR : Cursor.E_RESIZE_CURSOR);
                    break;
                case Resizable.RIGHT:
                    s = Cursor.getPredefinedCursor(ltr ? Cursor.E_RESIZE_CURSOR : Cursor.W_RESIZE_CURSOR);
                    break;
                case Resizable.LOWER_RIGHT:
                    s = Cursor.getPredefinedCursor(ltr ? Cursor.SE_RESIZE_CURSOR : Cursor.SW_RESIZE_CURSOR);
                    break;
                case Resizable.LOWER_LEFT:
                    s = Cursor.getPredefinedCursor(ltr ? Cursor.SW_RESIZE_CURSOR : Cursor.SE_RESIZE_CURSOR);
                    break;
                case Resizable.UPPER_LEFT:
                    s = Cursor.getPredefinedCursor(ltr ? Cursor.NW_RESIZE_CURSOR : Cursor.NE_RESIZE_CURSOR);
                    break;
                case Resizable.UPPER_RIGHT:
                    s = Cursor.getPredefinedCursor(ltr ? Cursor.NE_RESIZE_CURSOR : Cursor.NW_RESIZE_CURSOR);
                    break;
            }

            Container c = _resizable.getComponent().getTopLevelAncestor();

            if (c instanceof JFrame) {
                ((JFrame) c).getGlassPane().setVisible(true);
                ((JFrame) c).getGlassPane().setCursor(s);
            }
            else if (c instanceof JApplet) {
                ((JApplet) c).getGlassPane().setVisible(true);
                ((JApplet) c).getGlassPane().setCursor(s);
            }
            else if (c instanceof JWindow) {
                ((JWindow) c).getGlassPane().setVisible(true);
                ((JWindow) c).getGlassPane().setCursor(s);
            }
            else if (c instanceof JDialog) {
                ((JDialog) c).getGlassPane().setVisible(true);
                ((JDialog) c).getGlassPane().setCursor(s);
            }

            _resizable.beginResizing(_resizeCorner);
        }
        else {
            _resizeCorner = RESIZE_NONE;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (_startingBounds == null) {
            return;
        }

        Point p = new Point(e.getX(), e.getY());
        Component c = (Component) e.getSource();
        SwingUtilities.convertPointToScreen(p, c);

        int deltaX = _eventMouseScreenX - p.x;
        int deltaY = _eventMouseScreenY - p.y;

        Dimension min = _resizable.getComponent().getMinimumSize();
        Dimension max = _resizable.getComponent().getMaximumSize();

        Point point = new Point(_resizable.getComponent().getX(), _resizable.getComponent().getY());

        if (_resizable.isTopLevel()) {
            SwingUtilities.convertPointToScreen(point, _resizable.getComponent());
        }

        int newX = point.x;
        int newY = point.y;
        int newW;
        int newH;

        Rectangle parentBounds = _resizable.isTopLevel() ? new Rectangle(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)
                : _resizable.getComponent().getParent().getBounds();

        int actualResizeCorner = _resizeCorner;
        boolean ltr = _resizable.getComponent().getComponentOrientation().isLeftToRight();
        if (!ltr) {
            switch (_resizeCorner) {
                case Resizable.UPPER_LEFT:
                    actualResizeCorner = Resizable.UPPER_RIGHT;
                case Resizable.UPPER_RIGHT:
                    actualResizeCorner = Resizable.UPPER_LEFT;
                case Resizable.LOWER_LEFT:
                    actualResizeCorner = Resizable.LOWER_RIGHT;
                case Resizable.LOWER_RIGHT:
                    actualResizeCorner = Resizable.LOWER_LEFT;
                case Resizable.LEFT:
                    actualResizeCorner = Resizable.RIGHT;
                case Resizable.RIGHT:
                    actualResizeCorner = Resizable.LEFT;
            }
        }
        switch (actualResizeCorner) {
            case RESIZE_NONE:
                return;
            case Resizable.UPPER:
                if (_startingBounds.height + deltaY < min.height)
                    deltaY = -(_startingBounds.height - min.height);
                else if (_startingBounds.height + deltaY > max.height)
                    deltaY = max.height - _startingBounds.height;
//                if (_startingBounds.y - deltaY < 0)
//                    deltaY = _startingBounds.y;

                newX = _startingBounds.x;
                newY = _startingBounds.y - deltaY;
                newW = _startingBounds.width;
                newH = _startingBounds.height + deltaY;
//                System.out.println("dragging delta " + deltaY + " newH " + newH);
                break;
            case Resizable.UPPER_RIGHT:
                if (_startingBounds.height + deltaY < min.height)
                    deltaY = -(_startingBounds.height - min.height);
                else if (_startingBounds.height + deltaY > max.height)
                    deltaY = max.height - _startingBounds.height;
//                if (_startingBounds.y - deltaY < 0)
//                    deltaY = _startingBounds.y;

                if (_startingBounds.width - deltaX < min.width)
                    deltaX = _startingBounds.width - min.width;
                else if (_startingBounds.width - deltaX > max.width)
                    deltaX = -(max.width - _startingBounds.width);
                if (_startingBounds.x + _startingBounds.width - deltaX > parentBounds.width)
                    deltaX = _startingBounds.x + _startingBounds.width - parentBounds.width;

                newX = _startingBounds.x;
                newY = _startingBounds.y - deltaY;
                newW = _startingBounds.width - deltaX;
                newH = _startingBounds.height + deltaY;
                break;
            case Resizable.RIGHT:
                if (_startingBounds.width - deltaX < min.width)
                    deltaX = _startingBounds.width - min.width;
                else if (_startingBounds.width - deltaX > max.width)
                    deltaX = -(max.width - _startingBounds.width);
                if (_startingBounds.x + _startingBounds.width - deltaX > parentBounds.width)
                    deltaX = _startingBounds.x + _startingBounds.width - parentBounds.width;

                newW = _startingBounds.width - deltaX;
                newH = _startingBounds.height;
                break;
            case Resizable.LOWER_RIGHT:
                if (_startingBounds.width - deltaX < min.width)
                    deltaX = _startingBounds.width - min.width;
                else if (_startingBounds.width - deltaX > max.width)
                    deltaX = -(max.width - _startingBounds.width);
                if (_startingBounds.x + _startingBounds.width - deltaX > parentBounds.width)
                    deltaX = _startingBounds.x + _startingBounds.width - parentBounds.width;

                if (_startingBounds.height - deltaY < min.height)
                    deltaY = _startingBounds.height - min.height;
                else if (_startingBounds.height - deltaY > max.height)
                    deltaY = -(max.height - _startingBounds.height);
                if (_startingBounds.y + _startingBounds.height - deltaY > parentBounds.height)
                    deltaY = _startingBounds.y + _startingBounds.height -
                            parentBounds.height;

                newW = _startingBounds.width - deltaX;
                newH = _startingBounds.height - deltaY;
                break;
            case Resizable.LOWER:
                if (_startingBounds.height - deltaY < min.height)
                    deltaY = _startingBounds.height - min.height;
                else if (_startingBounds.height - deltaY > max.height)
                    deltaY = -(max.height - _startingBounds.height);
                if (_startingBounds.y + _startingBounds.height - deltaY > parentBounds.height)
                    deltaY = _startingBounds.y + _startingBounds.height -
                            parentBounds.height;

                newW = _startingBounds.width;
                newH = _startingBounds.height - deltaY;
                break;
            case Resizable.LOWER_LEFT:
                if (_startingBounds.height - deltaY < min.height)
                    deltaY = _startingBounds.height - min.height;
                else if (_startingBounds.height - deltaY > max.height)
                    deltaY = -(max.height - _startingBounds.height);
                if (_startingBounds.y + _startingBounds.height - deltaY > parentBounds.height)
                    deltaY = _startingBounds.y + _startingBounds.height - parentBounds.height;

                if (_startingBounds.width + deltaX < min.width)
                    deltaX = -(_startingBounds.width - min.width);
                else if (_startingBounds.width + deltaX > max.width)
                    deltaX = max.width - _startingBounds.width;
//                if (_startingBounds.x - deltaX < 0) {
//                    deltaX = _startingBounds.x;
//                }

                newX = _startingBounds.x - deltaX;
                newY = _startingBounds.y;
                newW = _startingBounds.width + deltaX;
                newH = _startingBounds.height - deltaY;
                break;
            case Resizable.LEFT:
                if (_startingBounds.width + deltaX < min.width)
                    deltaX = -(_startingBounds.width - min.width);
                else if (_startingBounds.width + deltaX > max.width)
                    deltaX = max.width - _startingBounds.width;
//                if (_startingBounds.x - deltaX < 0)
//                    deltaX = _startingBounds.x;

                newX = _startingBounds.x - deltaX;
                newY = _startingBounds.y;
                newW = _startingBounds.width + deltaX;
                newH = _startingBounds.height;
//                System.out.println("dragging delta " + deltaX + " newW " + newW);
                break;
            case Resizable.UPPER_LEFT:
                if (_startingBounds.width + deltaX < min.width)
                    deltaX = -(_startingBounds.width - min.width);
                else if (_startingBounds.width + deltaX > max.width)
                    deltaX = max.width - _startingBounds.width;
//                if (_startingBounds.x - deltaX < 0)
//                    deltaX = _startingBounds.x;

                if (_startingBounds.height + deltaY < min.height)
                    deltaY = -(_startingBounds.height - min.height);
                else if (_startingBounds.height + deltaY > max.height)
                    deltaY = max.height - _startingBounds.height;
//                if (_startingBounds.y - deltaY < 0) {
//                    deltaY = _startingBounds.y;
//                }

                newX = _startingBounds.x - deltaX;
                newY = _startingBounds.y - deltaY;
                newW = _startingBounds.width + deltaX;
                newH = _startingBounds.height + deltaY;
                break;
            default:
                return;
        }

        _resizable.resizing(_resizeCorner, newX, newY, newW, newH);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        _startingBounds = null;

        if (_discardRelease) {
            _discardRelease = false;
            return;
        }

        if (_resizeCorner != RESIZE_NONE) {
            Container c = _resizable.getComponent().getTopLevelAncestor();
            if (c instanceof JFrame) {
                ((JFrame) _resizable.getComponent().getTopLevelAncestor()).getGlassPane().setCursor(Cursor.getDefaultCursor());

                ((JFrame) _resizable.getComponent().getTopLevelAncestor()).getGlassPane().setVisible(false);
            }
            else if (c instanceof JApplet) {
                ((JApplet) c).getGlassPane().setCursor(Cursor.getDefaultCursor());
                ((JApplet) c).getGlassPane().setVisible(false);
            }
            else if (c instanceof JWindow) {
                ((JWindow) c).getGlassPane().setCursor(Cursor.getDefaultCursor());
                ((JWindow) c).getGlassPane().setVisible(false);
            }
            else if (c instanceof JDialog) {
                ((JDialog) c).getGlassPane().setCursor(Cursor.getDefaultCursor());
                ((JDialog) c).getGlassPane().setVisible(false);
            }

            _resizable.endResizing(_resizeCorner);

            _eventMouseScreenX = 0;
            _eventMouseScreenY = 0;
            _startingBounds = null;
            _resizeCorner = RESIZE_NONE;
        }
    }

    /**
     * mouseMoved is for resize only. When mouse moves over borders and corners, it will change to different cursor to
     * indicate it's resizable.
     *
     * @param e mouse event
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (e.getSource() instanceof Resizable.ResizeCorner) {
            Resizable.ResizeCorner corner = (Resizable.ResizeCorner) e.getSource();
            boolean ltr = corner.getComponentOrientation().isLeftToRight();
            switch (corner.getCorner()) {
                case Resizable.LOWER_RIGHT:
                    corner.setCursor(Cursor.getPredefinedCursor(ltr ? Cursor.SE_RESIZE_CURSOR : Cursor.SW_RESIZE_CURSOR));
                    return;
                case Resizable.UPPER_RIGHT:
                    corner.setCursor(Cursor.getPredefinedCursor(ltr ? Cursor.NE_RESIZE_CURSOR : Cursor.NW_RESIZE_CURSOR));
                    return;
                case Resizable.LOWER_LEFT:
                    corner.setCursor(Cursor.getPredefinedCursor(ltr ? Cursor.SW_RESIZE_CURSOR : Cursor.SE_RESIZE_CURSOR));
                    return;
                case Resizable.UPPER_LEFT:
                    corner.setCursor(Cursor.getPredefinedCursor(ltr ? Cursor.NW_RESIZE_CURSOR : Cursor.NE_RESIZE_CURSOR));
                    return;
            }
        }
        else if (e.getSource() == _resizable.getComponent()) {
            Insets i = _resizable.getResizeInsets();
            boolean ltr = _resizable.getComponent().getComponentOrientation().isLeftToRight();
            if (e.getX() <= i.left) {
                if (isResizable(Resizable.UPPER_LEFT) && i.top > 0 && e.getY() < _resizable.getResizeCornerSize() + i.top)
                    _resizable.getComponent().setCursor(Cursor.getPredefinedCursor(ltr ? Cursor.NW_RESIZE_CURSOR : Cursor.NE_RESIZE_CURSOR));
                else if (isResizable(Resizable.LOWER_LEFT) && i.bottom > 0 && e.getY() > _resizable.getComponent().getHeight() - _resizable.getResizeCornerSize() - i.bottom)
                    _resizable.getComponent().setCursor(Cursor.getPredefinedCursor(ltr ? Cursor.SW_RESIZE_CURSOR : Cursor.SE_RESIZE_CURSOR));
                else if (isResizable(Resizable.LEFT))
                    _resizable.getComponent().setCursor(Cursor.getPredefinedCursor(ltr ? Cursor.W_RESIZE_CURSOR : Cursor.E_RESIZE_CURSOR));
                else
                    _resizable.getComponent().setCursor(Cursor.getDefaultCursor());
            }
            else if (e.getX() >= _resizable.getComponent().getWidth() - i.right) {
                if (isResizable(Resizable.UPPER_RIGHT) && i.top > 0 && e.getY() < _resizable.getResizeCornerSize() + i.top)
                    _resizable.getComponent().setCursor(Cursor.getPredefinedCursor(ltr ? Cursor.NE_RESIZE_CURSOR : Cursor.NW_RESIZE_CURSOR));
                else if (isResizable(Resizable.LOWER_LEFT) && i.bottom > 0 && e.getY() > _resizable.getComponent().getHeight() - _resizable.getResizeCornerSize() - i.bottom)
                    _resizable.getComponent().setCursor(Cursor.getPredefinedCursor(ltr ? Cursor.SE_RESIZE_CURSOR : Cursor.SW_RESIZE_CURSOR));
                else if (isResizable(Resizable.RIGHT))
                    _resizable.getComponent().setCursor(Cursor.getPredefinedCursor(ltr ? Cursor.E_RESIZE_CURSOR : Cursor.W_RESIZE_CURSOR));
                else
                    _resizable.getComponent().setCursor(Cursor.getDefaultCursor());
            }
            else if (e.getY() <= i.top) {
                if (isResizable(Resizable.UPPER_LEFT) && i.left > 0 && e.getX() < _resizable.getResizeCornerSize() + i.left)
                    _resizable.getComponent().setCursor(Cursor.getPredefinedCursor(ltr ? Cursor.NW_RESIZE_CURSOR : Cursor.NE_RESIZE_CURSOR));
                else if (isResizable(Resizable.UPPER_RIGHT) && i.right > 0 && e.getX() > _resizable.getComponent().getWidth() - _resizable.getResizeCornerSize() - i.right)
                    _resizable.getComponent().setCursor(Cursor.getPredefinedCursor(ltr ? Cursor.NE_RESIZE_CURSOR : Cursor.NW_RESIZE_CURSOR));
                else if (isResizable(Resizable.UPPER))
                    _resizable.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                else
                    _resizable.getComponent().setCursor(Cursor.getDefaultCursor());
            }
            else if (e.getY() >= _resizable.getComponent().getHeight() - i.bottom) {
                if (isResizable(Resizable.LOWER_LEFT) && i.left > 0 && e.getX() < _resizable.getResizeCornerSize() + i.left)
                    _resizable.getComponent().setCursor(Cursor.getPredefinedCursor(ltr ? Cursor.SW_RESIZE_CURSOR : Cursor.SE_RESIZE_CURSOR));
                else if (isResizable(Resizable.LOWER_RIGHT) && i.right > 0 && e.getX() > _resizable.getComponent().getWidth() - _resizable.getResizeCornerSize() - i.right)
                    _resizable.getComponent().setCursor(Cursor.getPredefinedCursor(ltr ? Cursor.SE_RESIZE_CURSOR : Cursor.SW_RESIZE_CURSOR));
                else if (isResizable(Resizable.LOWER))
                    _resizable.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                else
                    _resizable.getComponent().setCursor(Cursor.getDefaultCursor());
            }
            else
                _resizable.getComponent().setCursor(Cursor.getDefaultCursor());
            return;
        }

        _resizable.getComponent().setCursor(Cursor.getDefaultCursor());
    }

    // implements java.awt.event.MouseListener
    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() instanceof Resizable.ResizeCorner) {
            Resizable.ResizeCorner corner = (Resizable.ResizeCorner) e.getSource();
            corner.setCursor(Cursor.getDefaultCursor());
        }
        else {
            _resizable.getComponent().setCursor(Cursor.getDefaultCursor());
        }
    }
}
