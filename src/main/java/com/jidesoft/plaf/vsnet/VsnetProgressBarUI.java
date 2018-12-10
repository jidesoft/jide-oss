/*
 * @(#)VsnetProgressBarUI.java 6/5/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.vsnet;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.JideSwingUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

/**
 * A better ProgressBarUI for indeterminate progress bar.
 * <p/>
 * v * <b>Credit:</b> This implementation is based on work from Santhosh Kumar - santhosh@in.fiorano.com.
 */

public class VsnetProgressBarUI extends BasicProgressBarUI implements ActionListener {
    /**
     * Interval (in ms) between repaints of the indeterminate progress bar.
     * The value of this method is set
     * (every time the progress bar changes to indeterminate mode)
     * using the
     * "ProgressBar.repaintInterval" key in the defaults table.
     */
    private int repaintInterval;

    private int x = 0, y = 0, delta = +1;
    private Timer timer = null;


    public static ComponentUI createUI(JComponent x) {
        return new VsnetProgressBarUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        initRepaintInterval(); //initialize repaint interval
    }

    @Override
    protected void startAnimationTimer() {
        if (timer == null) {
            timer = new Timer(getRepaintInterval() / 20, this);
        }
        x = y = 0;
        delta = 1;
        timer.start();
    }

    @Override
    protected void stopAnimationTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        // style1
        if (x == 0)
            delta = +1;
        else if (x == progressBar.getWidth())
            delta = -1;
        x += delta;

        progressBar.repaint();
    }

    /**
     * Returns the desired number of milliseconds between repaints.
     * This value is meaningful
     * only if the progress bar is in indeterminate mode.
     * The repaint interval determines how often the
     * default animation thread's timer is fired.
     * It's also used by the default indeterminate progress bar
     * painting code when determining
     * how far to move the bouncing box per frame.
     * The repaint interval is specified by
     * the "ProgressBar.repaintInterval" UI default.
     *
     * @return the repaint interval, in milliseconds
     */
    protected int getRepaintInterval() {
        return repaintInterval;
    }

    private int initRepaintInterval() {
        repaintInterval = UIDefaultsLookup.getInt("ProgressBar.repaintInterval");
        return repaintInterval;
    }

    private Rectangle boxRect;

    @Override
    public void paintIndeterminate(Graphics g, JComponent c) {
        if (!(g instanceof Graphics2D)) {
            return;
        }

        Color startColor = progressBar.getForeground();
        Color endColor = VsnetUtils.getLighterColor(startColor, 0.9f);

        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        Graphics2D g2d = (Graphics2D) g;

        // Paint the bouncing box.
        boolean isVertical = c.getHeight() > c.getWidth();
        if (delta > 0) {
            boxRect = new Rectangle(0, 0, x, progressBar.getHeight() - 1);
            JideSwingUtilities.fillNormalGradient(g2d, boxRect, endColor, startColor, isVertical);
        }
        else {
            boxRect = new Rectangle(x, 0, progressBar.getWidth() - x, progressBar.getHeight() - 1);
            JideSwingUtilities.fillNormalGradient(g2d, boxRect, startColor, endColor, isVertical);
        }

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
                paintString(g2d, b.left, b.top,
                        barRectWidth, barRectHeight,
                        boxRect.x, boxRect.width, b);
            }
            else {
                paintString(g2d, b.left, b.top,
                        barRectWidth, barRectHeight,
                        boxRect.y, boxRect.height, b);
            }
        }
    }

    /**
     * Paints the progress string.
     *
     * @param g          Graphics used for drawing.
     * @param x          x location of bounding box
     * @param y          y location of bounding box
     * @param width      width of bounding box
     * @param height     height of bounding box
     * @param fillStart  start location, in x or y depending on orientation,
     *                   of the filled portion of the progress bar.
     * @param amountFull size of the fill region, either width or height
     *                   depending upon orientation.
     * @param b          Insets of the progress bar.
     */
    private void paintString(Graphics g, int x, int y, int width, int height,
                             int fillStart, int amountFull, Insets b) {
        if (!(g instanceof Graphics2D)) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        String progressString = progressBar.getString();
        g2.setFont(progressBar.getFont());
        Point renderLocation = getStringPlacement(g2, progressString,
                x, y, width, height);
        Rectangle oldClip = g2.getClipBounds();

        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            g2.setColor(getSelectionBackground());
            JideSwingUtilities.drawString(progressBar, g2, progressString,
                    renderLocation.x, renderLocation.y);
            g2.setColor(getSelectionForeground());
            g2.clipRect(fillStart, y, amountFull, height);
            JideSwingUtilities.drawString(progressBar, g2, progressString,
                    renderLocation.x, renderLocation.y);
        }
        else { // VERTICAL
            g2.setColor(getSelectionBackground());
            AffineTransform rotate =
                    AffineTransform.getRotateInstance(Math.PI / 2);
            g2.setFont(progressBar.getFont().deriveFont(rotate));
            renderLocation = getStringPlacement(g2, progressString,
                    x, y, width, height);
            JideSwingUtilities.drawString(progressBar, g2, progressString,
                    renderLocation.x, renderLocation.y);
            g2.setColor(getSelectionForeground());
            g2.clipRect(x, fillStart, width, amountFull);
            JideSwingUtilities.drawString(progressBar, g2, progressString,
                    renderLocation.x, renderLocation.y);
        }
        g2.setClip(oldClip);
    }
}