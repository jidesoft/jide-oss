/*
 * @(#)PaintPanel.java 2/22/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A panel which support Paint as background.
 */
public class PaintPanel extends JPanel {
    private Paint _backgroundPaint;

    public PaintPanel() {
    }

    public PaintPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    public PaintPanel(LayoutManager layout) {
        super(layout);
    }

    public PaintPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    /**
     * Gets the Paint that will be used to paint background.
     *
     * @return the background paint.
     */
    public Paint getBackgroundPaint() {
        return _backgroundPaint;
    }

    /**
     * Sets the Paint that will be used to paint background.
     *
     * @param backgroundPaint
     */
    public void setBackgroundPaint(Paint backgroundPaint) {
        _backgroundPaint = backgroundPaint;
    }

    public static TexturePaint createTexturePaint(JPanel panel, Image img, int x, int y, int w, int h) {
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tG2 = bi.createGraphics();
        tG2.drawImage(img, x, y, Color.white, panel);
        Rectangle r = new Rectangle(0, 0, w, h);
        return new TexturePaint(bi, r);
    }

    protected Color _startColor;
    protected Color _endColor;
    protected boolean _isVertical;

    /**
     * This method allows you to use gradient background without using {@link #setBackgroundPaint(java.awt.Paint)}
     * method. You can use GradientPaint to do the same thing. However if you use this method, it will use fast gradient
     * paint defined in JideSwingUtilities to do the painting.
     *
     * @param startColor start color of the gradient
     * @param endColor   end color of the gradient
     * @param isVertical vertical or not
     */
    public void setGradientPaint(Color startColor, Color endColor, boolean isVertical) {
        setStartColor(startColor);
        setEndColor(endColor);
        setVertical(isVertical);
    }

    public Color getStartColor() {
        return _startColor;
    }

    public void setStartColor(Color startColor) {
        _startColor = startColor;
    }

    public Color getEndColor() {
        return _endColor;
    }

    public void setEndColor(Color endColor) {
        _endColor = endColor;
    }

    public boolean isVertical() {
        return _isVertical;
    }

    public void setVertical(boolean vertical) {
        _isVertical = vertical;
    }

    /**
     * Paints the background.
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getStartColor() != null && getEndColor() != null) {
            JideSwingUtilities.fillGradient((Graphics2D) g, new Rectangle(0, 0, getWidth(), getHeight()), getStartColor(), getEndColor(), isVertical());
        }
        else if (isOpaque() && getBackgroundPaint() != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(getBackgroundPaint());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}

