package com.jidesoft.plaf.xerto;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * VerticalLabelUI - used to replace the UI on a JLabel to make it vertical
 *
 * @author Created by Jasper Potts (10-Jun-2004)
 * @version 1.0
 */
public class VerticalLabelUI extends BasicLabelUI {
    static {
        labelUI = new VerticalLabelUI(false);
    }

    protected boolean clockwise;

    public VerticalLabelUI(boolean clockwise) {
        super();
        this.clockwise = clockwise;
    }


    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension dim = super.getPreferredSize(c);
        return new Dimension(dim.height, dim.width);
    }

    private static Rectangle s_oPaintIconRectangle = new Rectangle();
    private static Rectangle s_oPaintTextRectangle = new Rectangle();
    private static Rectangle s_oPaintViewRectangle = new Rectangle();
    private static Insets s_oPaintViewInsets = new Insets(0, 0, 0, 0);

    @Override
    public void paint(Graphics i_oGraphics, JComponent i_oComponent) {
        JLabel oLabel = (JLabel) i_oComponent;
        String oText = oLabel.getText();
        Icon oIcon = (oLabel.isEnabled()) ? oLabel.getIcon() : oLabel.getDisabledIcon();

        if ((oIcon == null) && (oText == null)) {
            return;
        }

        FontMetrics oFontMetrics = i_oGraphics.getFontMetrics();
        s_oPaintViewInsets = i_oComponent.getInsets(s_oPaintViewInsets);

        s_oPaintViewRectangle.x = s_oPaintViewInsets.left;
        s_oPaintViewRectangle.y = s_oPaintViewInsets.top;

        // Use inverted height & width
        s_oPaintViewRectangle.height = i_oComponent.getWidth() - (s_oPaintViewInsets.left + s_oPaintViewInsets.right);
        s_oPaintViewRectangle.width = i_oComponent.getHeight() - (s_oPaintViewInsets.top + s_oPaintViewInsets.bottom);

        s_oPaintIconRectangle.x = s_oPaintIconRectangle.y = s_oPaintIconRectangle.width = s_oPaintIconRectangle.height = 0;
        s_oPaintTextRectangle.x = s_oPaintTextRectangle.y = s_oPaintTextRectangle.width = s_oPaintTextRectangle.height = 0;

        String sClippedText =
                layoutCL(oLabel, oFontMetrics, oText, oIcon, s_oPaintViewRectangle, s_oPaintIconRectangle, s_oPaintTextRectangle);

        Graphics2D g2 = (Graphics2D) i_oGraphics;
        AffineTransform oTransform = g2.getTransform();
        if (clockwise) {
            g2.rotate(Math.PI / 2);
            g2.translate(0, -i_oComponent.getWidth());
        }
        else {
            g2.rotate(-Math.PI / 2);
            g2.translate(-i_oComponent.getHeight(), 0);
        }

        if (oIcon != null) {
            oIcon.paintIcon(i_oComponent, i_oGraphics, s_oPaintIconRectangle.x, s_oPaintIconRectangle.y);
        }

        if (oText != null) {
            int iTextX = s_oPaintTextRectangle.x;
            int iTextY = s_oPaintTextRectangle.y + oFontMetrics.getAscent();

            if (oLabel.isEnabled()) {
                paintEnabledText(oLabel, i_oGraphics, sClippedText, iTextX, iTextY);
            }
            else {
                paintDisabledText(oLabel, i_oGraphics, sClippedText, iTextX, iTextY);
            }
        }

        g2.setTransform(oTransform);
    }
}