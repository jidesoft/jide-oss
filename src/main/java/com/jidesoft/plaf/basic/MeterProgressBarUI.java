/*
 * MeterProgressBarUI.java
 * 
 * Created on 2007-10-1, 16:42:13
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.MeterProgressBar;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MeterProgressBarUI extends BasicProgressBarUI {

    protected Color _cellBackground;
    protected Color _cellForeground;
    protected int _cellLength;
    protected int _cellSpacing;
    private PropertyChangeListener _propertyChangeListener;

    public static ComponentUI createUI(JComponent c) {
        return new MeterProgressBarUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        LookAndFeel.installBorder(progressBar, "MeterProgressBar.border");
        LookAndFeel.installColors(progressBar, "MeterProgressBar.background", "MeterProgressBar.foreground");
        _cellForeground = UIDefaultsLookup.getColor("MeterProgressBar.cellForeground");
        _cellBackground = UIDefaultsLookup.getColor("MeterProgressBar.cellBackground");
        _cellLength = UIDefaultsLookup.getInt("MeterProgressBar.cellLength");
        _cellSpacing = UIDefaultsLookup.getInt("MeterProgressBar.cellSpacing");
    }


    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
        _cellBackground = null;
        _cellForeground = null;
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        progressBar.addPropertyChangeListener(
                _propertyChangeListener = new PropertyChangeHandler());
    }

    @Override
    protected void uninstallListeners() {
        progressBar.removePropertyChangeListener(_propertyChangeListener);
        super.uninstallListeners();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        // amount of progress to draw
        int amountFull = getAmountFull(b, barRectWidth, barRectHeight);
        int orientation = progressBar.getOrientation();
        float width = orientation == JProgressBar.HORIZONTAL ? barRectHeight : barRectWidth;

        Graphics2D g2 = (Graphics2D) g.create();

        //paint cell background
        g2.setColor(_cellBackground);
        g2.setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        if (orientation == JProgressBar.HORIZONTAL) {
            g2.drawLine(b.left, barRectHeight / 2 + b.top,
                    b.left + barRectWidth, barRectHeight / 2 + b.top);
        }
        else {
            g2.drawLine(barRectWidth / 2 + b.left, b.top + barRectHeight,
                    barRectWidth / 2 + b.left, b.top);
        }

        //paint cell foreground
        if (((MeterProgressBar) c).getStyle() == MeterProgressBar.STYLE_PLAIN) {
            g2.setColor(_cellForeground);
            if (orientation == JProgressBar.HORIZONTAL) {
                if (c.getComponentOrientation().isLeftToRight()) {
                    g2.drawLine(b.left, barRectHeight / 2 + b.top,
                            amountFull + b.left, barRectHeight / 2 + b.top);
                }
                else {
                    g2.drawLine(barRectWidth + b.left, barRectHeight / 2 + b.top,
                            barRectWidth + b.left - amountFull, barRectHeight / 2 + b.top);
                }
            }
            else {
                g2.drawLine(barRectWidth / 2 + b.left, b.top + barRectHeight,
                        barRectWidth / 2 + b.left, b.top + barRectHeight - amountFull);
            }
        }
        else {
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            if (orientation == JProgressBar.HORIZONTAL) {
                Rectangle rect = new Rectangle(b.left, b.top, amountFull, barRectHeight / 2);
                if (!c.getComponentOrientation().isLeftToRight()) {
                    rect.x += barRectWidth - amountFull;
                }
                JideSwingUtilities.fillGradient(g2, rect, _cellForeground, _cellBackground, true);
                rect.y += barRectHeight / 2;
                JideSwingUtilities.fillGradient(g2, rect, _cellBackground, _cellForeground, true);
            }
            else {
                Rectangle rect = new Rectangle(b.left, b.top + barRectHeight - amountFull,
                        barRectWidth / 2, amountFull);
                JideSwingUtilities.fillGradient(g2, rect, _cellForeground, _cellBackground, false);
                rect.x += barRectWidth / 2;
                JideSwingUtilities.fillGradient(g2, rect, _cellBackground, _cellForeground, false);
            }
        }

        //paint backgound
        g2.setColor(progressBar.getBackground());
        g2.setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0.f, new float[]{_cellLength, _cellSpacing}, 0.f));
        // draw each individual cell
        if (orientation == JProgressBar.HORIZONTAL) {
            g2.drawLine(b.left - _cellSpacing, barRectHeight / 2 + b.top,
                    b.left + barRectWidth, barRectHeight / 2 + b.top);
        }
        else {
            g2.drawLine(barRectWidth / 2 + b.left, b.top + barRectHeight + _cellSpacing,
                    barRectWidth / 2 + b.left, b.top);
        }

        g2.dispose();
    }

    private class PropertyChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (MeterProgressBar.PROPERTY_STYLE.equals(evt.getPropertyName())) {
                progressBar.repaint();
            }
        }

    }

}
