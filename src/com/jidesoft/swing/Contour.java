/*
 * @(#)Contour.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import java.awt.*;

/**
 * A <code>Contour</code> is a lightweight component which only paints the outline of component when dragged. It is also
 * used as a placeholder for some information during dragging.
 * <p/>
 * Notes: this class has to be public so that JIDE can use it in different packages, not meant to release to end user as
 * a public API. JIDE will not guarantee the class will remain as it is.
 */
public class Contour extends JComponent implements IContour {
    public static final int PARTIAL_OUTLINE_MODE = 0;

    public static final int MIX_OUTLINE_MODE = 1;

    public static final int FULL_OUTLINE_MODE = 2;

    // Five values below should be configurable

    /**
     * width of the contour.
     */
    private int _thickness = 4;

    /**
     * the width of tab if in tab-docked mode.
     */
    private static final int TAB_WIDTH = 42;

    /**
     * the distance from the beginning to the tab if in tab-docked mode.
     */
    private static final int TAB_LEADING = 8;

    /**
     * the color of contour.
     */
    private Color _lineColor = new Color(136, 136, 136); // to fake the old contour color
//    private static final Color LINE_COLOR = Color.BLACK;

    /**
     * the stroke to paint special effect of a contour.
     */
//    private static final BasicStroke DOTTED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE,
//            BasicStroke.JOIN_ROUND, 1.0f, new float[]{0, 2, 0, 2}, 0);;

    /**
     * height of tab. It was used to draw contour. Application can set the height.
     */
    private int _tabHeight = 22;

    /**
     * <code>true</code> if docking is allowed; <code>false</code> otherwise. Usually when user pressed ctrl key, dock
     * is not allowed.
     */
    private boolean _allowDocking = true;

    /**
     * <code>true</code> if tab-docked; <code>false</code> otherwise.
     */
    private boolean _tabDocking;

    /**
     * Which side is the tab
     */
    private int _tabSide;

    /**
     * <code>true</code> if float; <code>false</code> otherwise.
     */
    private boolean _floating;

    /**
     * which component the dragged component will dock to.
     */
    private Component _attachedComponent;

    /**
     * which side the dragged component will dock to.
     */
    private int _attachedSide;

    /**
     * When you dragged a component, several other components could be dragged. For example, if user drags on title bar
     * of FrameContainer, all components in the FrameContainer are considered as dragged. If user drags on tab, only
     * selected one is dragged.
     * <p/>
     * <code>true</code> if all dragged components are affected; <code>false</code> otherwise.
     */
    private boolean _single;

    /**
     * When user press and release ctrl key, the contour will toggle between dock and float mode. These three fields are
     * used to remember previous state.
     */
    private JComponent _saveDraggedComponent;
    private int _saveX, _saveY;
    private int _saveMouseModifier;

    private Container _relativeContainer;

    private int _outlineMode = 0;

    private Outline _topOutline;
    private Outline _bottomOutline;
    private Outline _leftOutline;
    private Outline _rightOutline;

    /**
     * If ghost is true, it will not paint itself at all but paint to underlying LayerPane.
     */
    private boolean _ghost = false;

    private Component _glassPane;

    private boolean _changeCursor = false;

    /**
     * Default Constructor.
     */
    public Contour() {
        this(22);
        setOpaque(false);
        setDoubleBuffered(true);
    }

    /**
     * Constructor with tab height.
     *
     * @param tabHeight the tab height
     */
    public Contour(int tabHeight) {
        _thickness = UIDefaultsLookup.getInt("Contour.thickness") == 0 ? 4 : UIDefaultsLookup.getInt("Contour.thickness");
        _lineColor = UIDefaultsLookup.getColor("Contour.color") == null ? new Color(136, 136, 136) : UIDefaultsLookup.getColor("Contour.color");
        setTabHeight(tabHeight);
        if (getOutlineMode() != PARTIAL_OUTLINE_MODE) {
            initOutline();
        }
    }

    private void initOutline() {
//        System.out.println("1");
        _topOutline = new Outline();
        _bottomOutline = new Outline();
        _leftOutline = new Outline();
        _rightOutline = new Outline();
    }

    /**
     * Returns whether this component should use a buffer to paint.
     *
     * @return true if this component is double buffered, otherwise false
     */
    @Override
    public boolean isDoubleBuffered() {
        return true;
    }

    /**
     * Paint a rectangle or tab-shape using <code>DOTTED_STROKE<code> with width of <code>WIDTH<code>.
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        if (!_ghost) {
            paintOutline(g, false);
        }
    }

    private void paintOutline(Graphics g, boolean xorMode) {
        Rectangle bounds = getBounds();
        g.translate(-bounds.x, -bounds.y);

        if (xorMode) {
            bounds = SwingUtilities.convertRectangle(this, bounds, (getRelativeContainer() != null ? getRelativeContainer() : getParent()));
//            System.out.println(bounds);
        }
        else {
            g.setColor(_lineColor);
        }
// comment to make it draw faster
//        ((Graphics2D)g).setStroke(DOTTED_STROKE);

        if (getOutlineMode() != FULL_OUTLINE_MODE && isTabDocking()) {
            drawTab(g, bounds.x, bounds.y, bounds.width, bounds.height - 1,
                    _tabHeight - 1, TAB_WIDTH, TAB_LEADING,
                    _thickness, getTabSide());
        }
        else {
            drawRect(g, bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, _thickness);
        }
    }

    /**
     * Draws vertical orhorizontall lines. Try to test if fillRect then drawLine four times. Other unit test show it's
     * about fillRect is 75% faster than drawLine, so we decide to use fillRect to draw the contour.
     *
     * @param g
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param thick
     */
    private static void drawLine(Graphics g, int x1, int y1, int x2, int y2, int thick) {
        if (x1 == x2) {
            if (y2 > y1) {
                g.fillRect(x1, y1, thick, y2 - y1);
            }
            else {
                g.fillRect(x1, y2, thick, y1 - y2);
            }
        }
        else if (y1 == y2) {
            if (x2 > x1) {
                g.fillRect(x1, y1, x2 - x1, thick);
            }
            else {
                g.fillRect(x2, y1, x1 - x2, thick);
            }
        }

    }

    private static void drawRect(Graphics g, int x, int y, int width, int height, int thick) {
        if (width <= thick) {
            drawLine(g, x, y, x, y + height, thick);
        }
        else if (height <= thick) {
            drawLine(g, x, y, x + width, y, thick);
        }
        else {
            drawLine(g, x, y, x + width - thick, y, thick);
            drawLine(g, x + width - thick, y, x + width - thick, y + height - thick, thick);
            drawLine(g, x + width, y + height - thick, x + thick, y + height - thick, thick);
            drawLine(g, x, y + height, x, y + thick, thick);
        }
// commented because of using fillRect
//        g.drawLine(x, y, x + width - 1, y);
//        g.drawLine(x + width, y, x + width, y + height - 1);
//        g.drawLine(x + width, y + height, x + 1, y + height);
//        g.drawLine(x, y + height, x, y + 1);
    }

    private static void drawTab(Graphics g, int x, int y, int width, int height, int tabHeight, int tabWidth, int tabLeading, int thick, int side) {
        switch (side) {
            case SwingConstants.TOP:
                drawTopTab(g, x, y, width, height, tabHeight, tabWidth, tabLeading, thick);
                break;
            case SwingConstants.BOTTOM:
                drawBottomTab(g, x, y, width, height, tabHeight, tabWidth, tabLeading, thick);
                break;
        }
    }

//         TabDocking
//            |------
//            |     |
//         |---     |--------
//         |                |
//         |                |
//         |                |
//         |                |
//         |                |
//         -----------------|

    private static void drawTopTab(Graphics g, int x, int y, int width, int height, int tabHeight, int tabWidth, int tabLeading, int thick) {
        drawLine(g, x + width - thick, y + tabHeight, x + width - thick, y + height - 1, thick); // right

        drawLine(g, x + tabLeading + thick, y + tabHeight, x, y + tabHeight, thick); // top leading
        drawLine(g, x + tabWidth + tabLeading, y + tabHeight, x + tabWidth + tabLeading, y, thick); // tab right
        drawLine(g, x + tabWidth + tabLeading, y, x + tabLeading + 1, y, thick); // tab top
        drawLine(g, x + tabLeading, y, x + tabLeading, y + tabHeight, thick); // tab left
        drawLine(g, x + width, y + tabHeight, x + tabWidth + tabLeading, y + tabHeight, thick); // top rest

        drawLine(g, x, y + tabHeight, x, y + height, thick); // left

        drawLine(g, x, y + height - thick, x + width, y + height - thick, thick); // bottom

// commented because of using fillRect
//        g.drawLine(x, y, x + width - 1, y); // top
//        g.drawLine(x + width, y, x + width, y + height - tabHeight - 1); // right
//
//        g.drawLine(x + width, y + height - tabHeight, x + tabWidth + tabLeading + 1, y + height - tabHeight); // bottom rest
//        g.drawLine(x + tabWidth + tabLeading, y + height - tabHeight, x + tabWidth + tabLeading, y + height - 1); // tab right
//        g.drawLine(x + tabWidth + tabLeading, y + height, x + tabLeading + 1, y + height); // tab bottom
//        g.drawLine(x + tabLeading, y + height, x + tabLeading, y + height - tabHeight + 1); // tab left
//        g.drawLine(x + tabLeading, y + height - tabHeight, x + 1, y + height - tabHeight); // bottom leading
//
//        g.drawLine(x, y + height - tabHeight, x, y + 1); // left
    }

//         TabDocking
//         -----------------|
//         |                |
//         |                |
//         |                |
//         |                |
//         |                |
//         |---     |--------
//            |     |
//            |------

    private static void drawBottomTab(Graphics g, int x, int y, int width, int height, int tabHeight, int tabWidth, int tabLeading, int thick) {
        drawLine(g, x, y, x + width - 1, y, thick); // top
        drawLine(g, x + width - thick, y, x + width - thick, y + height - tabHeight - 1, thick); // right

        drawLine(g, x + width, y + height - tabHeight - thick, x + tabWidth + tabLeading, y + height - tabHeight - thick, thick); // bottom rest
        drawLine(g, x + tabWidth + tabLeading, y + height - tabHeight, x + tabWidth + tabLeading, y + height, thick); // tab right
        drawLine(g, x + tabWidth + tabLeading, y + height - thick, x + tabLeading + 1, y + height - thick, thick); // tab bottom
        drawLine(g, x + tabLeading, y + height, x + tabLeading, y + height - tabHeight, thick); // tab left
        drawLine(g, x + tabLeading + thick, y + height - tabHeight - thick, x + 1, y + height - tabHeight - thick, thick); // bottom leading

        drawLine(g, x, y + height - tabHeight, x, y + 1, thick); // left

// commented because of using fillRect
//        g.drawLine(x, y, x + width - 1, y); // top
//        g.drawLine(x + width, y, x + width, y + height - tabHeight - 1); // right
//
//        g.drawLine(x + width, y + height - tabHeight, x + tabWidth + tabLeading + 1, y + height - tabHeight); // bottom rest
//        g.drawLine(x + tabWidth + tabLeading, y + height - tabHeight, x + tabWidth + tabLeading, y + height - 1); // tab right
//        g.drawLine(x + tabWidth + tabLeading, y + height, x + tabLeading + 1, y + height); // tab bottom
//        g.drawLine(x + tabLeading, y + height, x + tabLeading, y + height - tabHeight + 1); // tab left
//        g.drawLine(x + tabLeading, y + height - tabHeight, x + 1, y + height - tabHeight); // bottom leading
//
//        g.drawLine(x, y + height - tabHeight, x, y + 1); // left
    }

    /**
     * Overwrite setBounds so that width and height are always even.
     *
     * @param r the new bounding rectangle for this component
     */
    @Override
    public void setBounds(Rectangle r) {
        setBounds(r.x, r.y, r.width, r.height);
    }

    /**
     * Overwrite setBounds so that width and height are always even.
     * <p/>
     * It looks ugly for either dimension is odd when painting with <code>DOTTED_STROKE</code>
     *
     * @param x      the new <i>x</i>-coordinate of this component
     * @param y      the new <i>y</i>-coordinate of this component
     * @param width  the new <code>width</code> of this component
     * @param height the new <code>height</code> of this component
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
        if (isLightweight()) {
            if (getOutlineMode() == PARTIAL_OUTLINE_MODE) {
                if (_ghost && (getRelativeContainer() != null || getParent() != null)) {
                    Graphics g = (getRelativeContainer() != null ? getRelativeContainer() : getParent()).getGraphics();
                    g.setXORMode(_lineColor);
                    paintOutline(g, true);
                    super.setBounds(x, y,
                            width % 2 == 0 ? width + 1 : width,
                            height % 2 == 0 ? height + 1 : height);
//                    paintOutline(g, true);
                }
                else {
                    super.setBounds(x, y,
                            width % 2 == 0 ? width + 1 : width,
                            height % 2 == 0 ? height + 1 : height);
                }
            }
            else {
                Rectangle rectangle = new Rectangle(x, y,
                        width % 2 == 0 ? width + 1 : width,
                        height % 2 == 0 ? height + 1 : height);
                if (getParent() != null) {
                    Rectangle parentRectangle = getParent().getBounds();
                    super.setBounds(x, y,
                            width % 2 == 0 ? width + 1 : width,
                            height % 2 == 0 ? height + 1 : height);
                    if (getOutlineMode() == MIX_OUTLINE_MODE && parentRectangle.contains(rectangle)) {
                        _leftOutline.setVisible(false);
                        _rightOutline.setVisible(false);
                        _topOutline.setVisible(false);
                        _bottomOutline.setVisible(false);
                    }
                    else {
                        super.setVisible(false);
                        Point point = rectangle.getLocation();
                        SwingUtilities.convertPointToScreen(point, getParent());
                        arrangeOutline(rectangle, point);
                    }
                }
            }
        }
        else {
            if (getRelativeContainer() != null) {
                Point point = new Point(x, y);
                SwingUtilities.convertPointToScreen(point, getRelativeContainer());
                super.setBounds(point.x, point.y,
                        width % 2 == 0 ? width + 1 : width,
                        height % 2 == 0 ? height + 1 : height);
            }
        }
    }

    private void arrangeOutline(Rectangle rectangle, Point point) {
//        boolean beyondLeft = rectangle.x < 0;
//        boolean beyondTop = rectangle.y < 0;
//        boolean beyondRight = rectangle.x + rectangle.width > parentRectangle.x + parentRectangle.width;
//        boolean beyondBottom = rectangle.y + rectangle.height > parentRectangle.y + parentRectangle.height;
        _leftOutline.setBounds(point.x, point.y, _thickness, rectangle.height - _thickness);
        _topOutline.setBounds(point.x + _thickness, point.y, rectangle.width - _thickness - 1, _thickness);
        _rightOutline.setBounds(point.x + rectangle.width - _thickness - 1, point.y + _thickness, _thickness, rectangle.height - _thickness);
        _bottomOutline.setBounds(point.x, point.y + rectangle.height - _thickness, rectangle.width - 1, _thickness);
        if (!_topOutline.isVisible()) {
            _topOutline.setVisible(true);
        }
        if (!_leftOutline.isVisible()) {
            _leftOutline.setVisible(true);
        }
        if (!_rightOutline.isVisible()) {
            _rightOutline.setVisible(true);
        }
        if (!_bottomOutline.isVisible()) {
            _bottomOutline.setVisible(true);
        }
    }

    /**
     * Gets tab height.
     *
     * @return tab height
     */
    public int getTabHeight() {
        return _tabHeight;
    }

    /**
     * Sets the tab height.
     *
     * @param tabHeight
     */
    public void setTabHeight(int tabHeight) {
        _tabHeight = tabHeight;
    }

    /**
     * Returns true if the contour is in tab-dock mode.
     *
     * @return true if tab-docking; false otherwise
     */
    public boolean isTabDocking() {
        return _tabDocking;
    }

    /**
     * Sets the tab-docking mode.
     *
     * @param tabDocking new mode
     */
    public void setTabDocking(boolean tabDocking) {
        _tabDocking = tabDocking;
        updateCursor();
    }

    /**
     * Gets the side of the tab.
     *
     * @return the side of the tab
     */
    public int getTabSide() {
        return _tabSide;
    }

    /**
     * Sets the side of the tab.
     *
     * @param tabSide
     */
    public void setTabSide(int tabSide) {
        _tabSide = tabSide;
    }

    /**
     * Returns true if the contour is in floating mode.
     *
     * @return true if floating; false otherwise
     */
    public boolean isFloating() {
        return _floating;
    }

    /**
     * Sets the floating mode.
     *
     * @param floating new mode
     */
    public void setFloating(boolean floating) {
        _floating = floating;
        updateCursor();
    }

    /**
     * Gets the attached component of this contour.
     *
     * @return the attached component
     */
    public Component getAttachedComponent() {
        return _attachedComponent;
    }

    /**
     * Sets the attached components.
     *
     * @param attachedComponent attached component to be set
     */
    public void setAttachedComponent(Component attachedComponent) {
        _attachedComponent = attachedComponent;
    }

    /**
     * Gets the side of the attached component which the contour is attached to.
     *
     * @return side the attached side
     */
    public int getAttachedSide() {
        return _attachedSide;
    }

    /**
     * Sets the side of the attached component which the contour is attached to.
     *
     * @param attachedSide the new attached side to be set
     */
    public void setAttachedSide(int attachedSide) {
        _attachedSide = attachedSide;
        updateCursor();
    }

    private void updateCursor() {
        if (getGlassPane() == null) { // if glass pane is not set, no way to change cursor shape. 
            return;
        }

        // show a stop cursor if the floating is not allowed
        if (!isVisible() && _floating) {
            getGlassPane().setCursor(JideCursors.getPredefinedCursor(JideCursors.DRAG_STOP_CURSOR));
            return;
        }
        else {
            getGlassPane().setCursor(Cursor.getDefaultCursor());
        }

        if (!_changeCursor) {
            return;
        }

        if (!isVisible()) {
            getGlassPane().setCursor(Cursor.getDefaultCursor());
            return;
        }

        if (isVisible() && (!_allowDocking || _floating)) {
            getGlassPane().setCursor(JideCursors.getPredefinedCursor(JideCursors.FLOAT_CURSOR));
        }
        else if (isVisible() && _tabDocking) {
            getGlassPane().setCursor(JideCursors.getPredefinedCursor(JideCursors.TAB_CURSOR));
        }
        else if (getAttachedComponent() instanceof JideSplitPaneDivider) {
            if (((JideSplitPaneDivider) getAttachedComponent()).getJideSplitPane().getOrientation() == JideSplitPane.HORIZONTAL_SPLIT) {
                getGlassPane().setCursor(JideCursors.getPredefinedCursor(JideCursors.HORIZONTAL_CURSOR));
            }
            else {
                getGlassPane().setCursor(JideCursors.getPredefinedCursor(JideCursors.VERTICAL_CURSOR));
            }
        }
        else {
            switch (_attachedSide) {
                case 1:
                    getGlassPane().setCursor(JideCursors.getPredefinedCursor(JideCursors.NORTH_CURSOR));
                    break;
                case 2:
                    getGlassPane().setCursor(JideCursors.getPredefinedCursor(JideCursors.SOUTH_CURSOR));
                    break;
                case 4:
                    getGlassPane().setCursor(JideCursors.getPredefinedCursor(JideCursors.EAST_CURSOR));
                    break;
                case 8:
                    getGlassPane().setCursor(JideCursors.getPredefinedCursor(JideCursors.WEST_CURSOR));
                    break;
                default:
                    getGlassPane().setCursor(Cursor.getDefaultCursor());
                    break;
            }
        }
    }

    /**
     * When you dragged a component, several other components could be dragged. For example, if user drags on title bar
     * of FrameContainer, all components in the FrameContainer are considered as dragged. If user drags on tab, only
     * selected one is dragged.
     *
     * @return <code>true</code> if all dragged components are affected; <code>false</code> otherwise.
     */
    public boolean isSingle() {
        return _single;
    }

    /**
     * Sets the value of single.
     *
     * @param single <code>true</code> if all dragged components are affected; <code>false</code> otherwise.
     */
    public void setSingle(boolean single) {
        _single = single;
    }

    /**
     * Checks if docking is allowed.
     *
     * @return <code>true</code> if docking is allowed; <code>false</code> otherwise.
     */
    public boolean isAllowDocking() {
        return _allowDocking;
    }

    /**
     * Sets the value of docking.
     *
     * @param allowDocking <code>true</code> if docking is allowed; <code>false</code> otherwise.
     */
    public void setAllowDocking(boolean allowDocking) {
        _allowDocking = allowDocking;
        updateCursor();
    }

    public Container getRelativeContainer() {
        return _relativeContainer;
    }

    public void setRelativeContainer(Container relativeContainer) {
        _relativeContainer = relativeContainer;
    }

    /**
     * Gets saved X position of contour before it's hidden.
     *
     * @return saved X position
     */
    public int getSaveX() {
        return _saveX;
    }

    /**
     * Gets saved Y position of contour before it's hidden.
     *
     * @return saved Y position
     */
    public int getSaveY() {
        return _saveY;
    }

    /**
     * Gets saved mouse modifier before the contour is hidden.
     *
     * @return saved mouse modifier
     */
    public int getSaveMouseModifier() {
        return _saveMouseModifier;
    }

    /**
     * Gets saved dragged component before the contour is hidden.
     *
     * @return saved dragged component
     */
    public JComponent getSaveDraggedComponent() {
        return _saveDraggedComponent;
    }

    /**
     * Stores information before the contour is hidden. Those information will be used to restore when the contour is
     * set visible again.
     *
     * @param comp              the dragged component
     * @param saveX             X position of the contour
     * @param saveY             Y position of the contour
     * @param saveMouseModifier mouse modifier in the MouseEvent
     */
    public void setDraggingInformation(JComponent comp, int saveX, int saveY, int saveMouseModifier) {
        _saveDraggedComponent = comp;
        _saveX = saveX;
        _saveY = saveY;
        _saveMouseModifier = saveMouseModifier;
    }

    public void cleanup() {
        if (getOutlineMode() != PARTIAL_OUTLINE_MODE) {
//            System.out.println("0");
            _leftOutline.dispose();
            _rightOutline.dispose();
            _topOutline.dispose();
            _bottomOutline.dispose();
            _leftOutline = null;
            _rightOutline = null;
            _topOutline = null;
            _bottomOutline = null;
        }
        if (getGlassPane() != null) {
            getGlassPane().setCursor(Cursor.getDefaultCursor());
        }
    }

//    private Screen _screen;
//    private Container _savedContainer;

    /**
     * Makes the component visible or invisible. Overrides <code>Component.setVisible</code>.
     *
     * @param aFlag true to make the component visible; false to make it invisible
     */
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        updateCursor();
        if (!aFlag && getOutlineMode() != PARTIAL_OUTLINE_MODE) {
            _leftOutline.setVisible(false);
            _rightOutline.setVisible(false);
            _topOutline.setVisible(false);
            _bottomOutline.setVisible(false);
        }
    }

    /**
     * Determines whether this component should be visible when its parent is visible. Components are initially visible,
     * with the exception of top level components such as <code>Frame</code> objects.
     *
     * @return <code>true</code> if the component is visible, <code>false</code> otherwise
     *
     * @see #setVisible
     * @since JDK1.0
     */
    @Override
    public boolean isVisible() {
        if (super.isVisible()) {
            return true;
        }
        else if (getOutlineMode() != PARTIAL_OUTLINE_MODE &&
                (_topOutline.isVisible() || _bottomOutline.isVisible() || _leftOutline.isVisible() || _rightOutline.isVisible())) {
            return true;
        }
        else
            return false;
    }

    class Outline extends JWindow {
        public Outline() {
            this.setVisible(false);
            setBackground(_lineColor);
        }

        @Override
        public void paint(Graphics g) {
            g.setColor(_lineColor);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

// Code for double buffer but doesn't work very well.
//        Image offscreen;
//
//        public void invalidate() {
//            super.invalidate();
//            offscreen = null;
//        }
//
//        public void update(Graphics g) {
//            paint(g);
//        }
//
//        public void paint(Graphics g) {
//            if (offscreen == null) {
//                offscreen = createImage(getSize().width, getSize().height);
//            }
//            Graphics og = offscreen.getGraphics();
//            // Here do something to draw your rectangle
//            // og.setClip(0,0,getSize().width, getSize().height);
//            super.paint(g);
//
//            g.drawImage(offscreen, 0, 0, null);
//            og.dispose();
//        }
    }

    public int getOutlineMode() {
        return _outlineMode;
    }

    public void setOutlineMode(int outlineMode) {
        if (outlineMode != PARTIAL_OUTLINE_MODE && _outlineMode == PARTIAL_OUTLINE_MODE) {
            initOutline();
        }
        _outlineMode = outlineMode;
    }

    public Component getGlassPane() {
        return _glassPane;
    }

    public void setGlassPane(Component glassPane) {
        _glassPane = glassPane;
    }

    public boolean isChangeCursor() {
        return _changeCursor;
    }

    public void setChangeCursor(boolean changeCursor) {
        _changeCursor = changeCursor;
    }
}
