package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

/**
 * Original code http://forums.java.net/jive/thread.jspa?forumID=73&threadID=14674 under "Do whatever you want with this
 * code" license
 */
@SuppressWarnings("serial")
class ScrollPaneOverview extends JComponent {
    
    private static final int MAX_SIZE = 400;
    private static final int MAX_SCALE = 20;

    private Component _owner;
    private JScrollPane _scrollPane;
    private Component _viewComponent;

    private JPopupMenu _popupMenu;

    private BufferedImage _image;
    private Rectangle _startRectangle;
    private Rectangle _rectangle;
    private Point _startPoint;
    private double _scale;
    private int xOffset;
    private int yOffset;

    private Color _selectionBorder = Color.BLACK;

    public ScrollPaneOverview(JScrollPane scrollPane, Component owner) {
        _scrollPane = scrollPane;
        _owner = owner;
        _image = null;
        _startRectangle = null;
        _rectangle = null;
        _startPoint = null;
        _scale = 0.0;

        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        MouseInputListener mil = new MouseInputAdapter() {
            // A new approach suggested by thomas.bierhance@sdm.de

            @Override
            public void mousePressed(MouseEvent e) {
                if (_startPoint != null) {
                    Point newPoint = e.getPoint();
                    int deltaX = (int) ((newPoint.x - _startPoint.x) / _scale);
                    int deltaY = (int) ((newPoint.y - _startPoint.y) / _scale);
                    scroll(deltaX, deltaY);
                }
                _startPoint = null;
                _startRectangle = _rectangle;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (_startPoint == null) {
                    _startPoint = new Point(_rectangle.x + _rectangle.width / 2, _rectangle.y + _rectangle.height / 2);
                }
                Point newPoint = e.getPoint();
                moveRectangle(newPoint.x - _startPoint.x, newPoint.y - _startPoint.y);
            }
        };
        addMouseListener(mil);
        addMouseMotionListener(mil);
        _popupMenu = new JPopupMenu();
        _popupMenu.setLayout(new BorderLayout());
        _popupMenu.add(this, BorderLayout.CENTER);
    }

    public void setSelectionBorderColor(Color selectionBorder) {
        _selectionBorder = selectionBorder;
    }

    public Color getSelectionBorder() {
        return _selectionBorder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (_image == null || _rectangle == null)
            return;
        Graphics2D g2d = (Graphics2D) g;
        Insets insets = getInsets();
        int xOffset = insets.left;
        int yOffset = insets.top;

        g.setColor(_scrollPane.getViewport().getView().getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(_image, xOffset, yOffset, null);

        int availableWidth = getWidth() - insets.left - insets.right;
        int availableHeight = getHeight() - insets.top - insets.bottom;
        Area area = new Area(new Rectangle(xOffset, yOffset, availableWidth, availableHeight));
        area.subtract(new Area(_rectangle));
        g.setColor(new Color(255, 255, 255, 128));
        g2d.fill(area);

        Color oldcolor = g.getColor();
        g.setColor(_selectionBorder);
        g.drawRect(_rectangle.x, _rectangle.y, _rectangle.width, _rectangle.height);

        g.setColor(oldcolor);
    }

    @Override
    public Dimension getPreferredSize() {
        if (_image == null || _rectangle == null)
            return new Dimension();
        Insets insets = getInsets();
        return new Dimension(_image.getWidth(null) + insets.left + insets.right, _image.getHeight(null) + insets.top + insets.bottom);
    }

    public void display() {
        _viewComponent = _scrollPane.getViewport().getView();
        if (_viewComponent == null) {
            return;
        }

        int maxSize = Math.max(MAX_SIZE, Math.max(_scrollPane.getWidth(), _scrollPane.getHeight()) / 2);

        int width = Math.min(_viewComponent.getWidth(), _scrollPane.getViewport().getWidth() * MAX_SCALE);
        if (width == 0) {
            return;
        }
        int height = Math.min(_viewComponent.getHeight(), _scrollPane.getViewport().getHeight() * MAX_SCALE);
        if (height == 0) {
            return;
        }
        double scaleX = (double) maxSize / width;
        double scaleY = (double) maxSize / height;

        _scale = Math.max(1.0 / MAX_SCALE, Math.min(scaleX, scaleY));

        _image = new BufferedImage((int) (width * _scale), (int) (height * _scale), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = _image.createGraphics();

        // If the view is larger than the max scale allows only the the top left most part will now be painted
        // One solution would be paint only the part around the current position, but I can't get it to paint - Walter Laan.
        // note that without limiting the scale, the width/height will become zero (illegal for BufferedImage)
        // See CornerScrollerVisualTest in the test folder
        
//        g.setColor(_viewComponent.getBackground());
//        g.fillRect(0, 0, _viewComponent.getWidth(), _viewComponent.getHeight());
//        Point viewPosition = _scrollPane.getViewport().getViewPosition();
//        xOffset = Math.max(0, viewPosition.x - (width / 2)); 
//        yOffset = Math.max(0, viewPosition.y - (height / 2)); 
//        g.translate(-xOffset, -yOffset);
//        g.setClip(0, 0, width, height);
        
        g.scale(_scale, _scale);
        g.setClip(xOffset, yOffset, width, height);
        /// {{{ Qian Qian 10/72007
        boolean wasDoubleBuffered = _viewComponent.isDoubleBuffered();
        try {
            if (_viewComponent instanceof JComponent) {
                ((JComponent) _viewComponent).setDoubleBuffered(false);
            }
            _viewComponent.paint(g);
        }
        finally {
            if (_viewComponent instanceof JComponent) {
                ((JComponent) _viewComponent).setDoubleBuffered(wasDoubleBuffered);
            }
            g.dispose();
        }
        /// QianQian 10/7/2007 }}}
        _startRectangle = _scrollPane.getViewport().getViewRect();
        Insets insets = getInsets();
        _startRectangle.x = (int) (_scale * _startRectangle.x + insets.left);
        _startRectangle.y = (int) (_scale * _startRectangle.y + insets.right);
        _startRectangle.width *= _scale;
        _startRectangle.height *= _scale;
        _rectangle = _startRectangle;
        Point centerPoint = new Point(_rectangle.x + _rectangle.width / 2, _rectangle.y + _rectangle.height / 2);
        _popupMenu.show(_owner, -centerPoint.x, -centerPoint.y);
    }

    private void moveRectangle(int aDeltaX, int aDeltaY) {
        if (_startRectangle == null)
            return;
        Insets insets = getInsets();
        Rectangle newRect = new Rectangle(_startRectangle);
        newRect.x += aDeltaX;
        newRect.y += aDeltaY;
        newRect.x = Math.min(Math.max(newRect.x, insets.left), getWidth() - insets.right - newRect.width);
        newRect.y = Math.min(Math.max(newRect.y, insets.right), getHeight() - insets.bottom - newRect.height);
        Rectangle clip = new Rectangle();
        Rectangle.union(_rectangle, newRect, clip);
        clip.grow(2, 2);
        _rectangle = newRect;
        paintImmediately(clip);
    }

    private void scroll(int aDeltaX, int aDeltaY) {
        JComponent component = (JComponent) _scrollPane.getViewport().getView();
        Rectangle rect = component.getVisibleRect();
        rect.x += xOffset + aDeltaX;
        rect.y += yOffset + aDeltaY;
        component.scrollRectToVisible(rect);
        _popupMenu.setVisible(false);
    }
}
