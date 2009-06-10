package com.jidesoft.plaf.basic;

import com.jidesoft.swing.JideLabel;
import com.jidesoft.swing.JideSwingUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;

public class BasicJideLabelUI extends BasicLabelUI {
    private static final LabelUI INSTANCE = new BasicJideLabelUI();

    public static ComponentUI createUI(JComponent c) {
        return INSTANCE;
    }

    @Override
    protected void installDefaults(JLabel c) {
        super.installDefaults(c);
        LookAndFeel.installColorsAndFont(c, "JideLabel.background", "JideLabel.foreground", "JideLabel.font");
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        Dimension d = super.getMinimumSize(c);
        if (JideSwingUtilities.getOrientationOf(c) == SwingConstants.HORIZONTAL) {
            return d;
        }
        else {
            //noinspection SuspiciousNameCombination
            return new Dimension(d.height, d.width); // swap width and height
        }
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        Dimension d = super.getMaximumSize(c);
        if (JideSwingUtilities.getOrientationOf(c) == SwingConstants.HORIZONTAL) {
            return d;
        }
        else {
            //noinspection SuspiciousNameCombination
            return new Dimension(d.height, d.width); // swap width and height
        }
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension d = super.getPreferredSize(c);
        if (JideSwingUtilities.getOrientationOf(c) == SwingConstants.HORIZONTAL) {
            return d;
        }
        else {
            //noinspection SuspiciousNameCombination
            return new Dimension(d.height, d.width); // swap width and height
        }
    }

    private static Rectangle paintIconR = new Rectangle();
    private static Rectangle paintTextR = new Rectangle();
    private static Rectangle paintViewR = new Rectangle();
    private static Insets paintViewInsets = new Insets(0, 0, 0, 0);

    @Override
    public void paint(Graphics g, JComponent c) {
        if (JideSwingUtilities.getOrientationOf(c) == SwingConstants.VERTICAL) {
            boolean clockwise = true;
            if (c instanceof JideLabel) {
                clockwise = ((JideLabel) c).isClockwise();
            }
            paintVertically(g, c,clockwise);
        }
        else {
            super.paint(g, c);
        }
    }

    public void paintVertically(Graphics g, JComponent c,boolean clockwise) {

        JLabel label = (JLabel) c;
        String text = label.getText();
        Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

        if ((icon == null) && (text == null)) {
            return;
        }

        FontMetrics fm = g.getFontMetrics();
        paintViewInsets = c.getInsets(paintViewInsets);

        paintViewR.x = paintViewInsets.left;
        paintViewR.y = paintViewInsets.top;

        // Use inverted height & width
        paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
        paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);

        paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
        paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

        String clippedText = layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);

        Graphics2D g2 = (Graphics2D) g;
        AffineTransform tr = g2.getTransform();
        if (clockwise) {
            g2.rotate(Math.PI / 2);
            g2.translate(0, -c.getWidth());
        }
        else {
            g2.rotate(-Math.PI / 2);
            g2.translate(-c.getHeight(), 0);
        }

        if (icon != null) {
            icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
        }

        if (text != null) {
            int textX = paintTextR.x;
            int textY = paintTextR.y + fm.getAscent();

            if (label.isEnabled()) {
                paintEnabledText(label, g, clippedText, textX, textY);
            }
            else {
                paintDisabledText(label, g, clippedText, textX, textY);
            }
        }


        g2.setTransform(tr);
    }

    public void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);
        if (JideLabel.PROPERTY_ORIENTATION==e.getPropertyName()) {
            if (e.getSource() instanceof JLabel) {
                JLabel label = (JLabel) e.getSource();
                label.revalidate();
            }
        }
        else if (JideLabel.PROPERTY_CLOCKWISE.equals(e.getPropertyName())) {
            if (e.getSource() instanceof JLabel) {
                JLabel label = (JLabel) e.getSource();
                label.repaint();
            }
        }
    }
}
