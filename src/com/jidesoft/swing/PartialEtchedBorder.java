package com.jidesoft.swing;

import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 */
public class PartialEtchedBorder extends EtchedBorder implements PartialSide {

    private int _sides;

    public PartialEtchedBorder() {
        this(ALL);
    }

    public PartialEtchedBorder(int sides) {
        _sides = sides;
    }

    public PartialEtchedBorder(int etchType, int sides) {
        super(etchType);
        _sides = sides;
    }

    public PartialEtchedBorder(Color highlight, Color shadow, int sides) {
        super(highlight, shadow);
        _sides = sides;
    }

    public PartialEtchedBorder(int etchType, Color highlight, Color shadow, int sides) {
        super(etchType, highlight, shadow);
        _sides = sides;
    }

    public int getSides() {
        return _sides;
    }

    public void setSides(int sides) {
        _sides = sides;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        int w = width;
        int h = height;

        g.translate(x, y);

        if (c.getBackground() == null) {
            c.setBackground(Color.GRAY); // just a workaround to resolve the background is null issue.
        }

        Color shadowColor = getShadowColor(c);
        Color highlightColor = getHighlightColor(c);

        if (_sides == ALL) {
            g.setColor(etchType == LOWERED ? shadowColor : highlightColor);
            g.drawRect(0, 0, w - 2, h - 2);

            g.setColor(etchType == LOWERED ? highlightColor : shadowColor);
            g.drawLine(1, h - 3, 1, 1);
            g.drawLine(1, 1, w - 3, 1);

            g.drawLine(0, h - 1, w - 1, h - 1);
            g.drawLine(w - 1, h - 1, w - 1, 0);
        }
        else {
            if ((_sides & NORTH) != 0) {
                g.setColor(etchType == LOWERED ? shadowColor : highlightColor);
                g.drawLine(0, 0, w - 2, 0);
                g.setColor(etchType == LOWERED ? highlightColor : shadowColor);
                g.drawLine(1, 1, w - 2, 1);
            }
            if ((_sides & SOUTH) != 0) {
                g.setColor(etchType == LOWERED ? shadowColor : highlightColor);
                g.drawLine(0, h - 2, w - 1, h - 2);
                g.setColor(etchType == LOWERED ? highlightColor : shadowColor);
                g.drawLine(0, h - 1, w - 1, h - 1);
            }
            if ((_sides & WEST) != 0) {
                g.setColor(etchType == LOWERED ? shadowColor : highlightColor);
                g.drawLine(0, h - 2, 0, 0);
                g.setColor(etchType == LOWERED ? highlightColor : shadowColor);
                g.drawLine(1, h - 3, 1, 1);
            }
            if ((_sides & EAST) != 0) {
                g.setColor(etchType == LOWERED ? shadowColor : highlightColor);
                g.drawLine(w - 2, h - 2, w - 2, 0);
                g.setColor(etchType == LOWERED ? highlightColor : shadowColor);
                g.drawLine(w - 1, h - 1, w - 1, 0);
            }
        }
        g.translate(-x, -y);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        Insets borderInsets = super.getBorderInsets(c);
        if ((_sides & NORTH) == 0) {
            borderInsets.top = 0;
        }
        if ((_sides & SOUTH) == 0) {
            borderInsets.bottom = 0;
        }
        if ((_sides & WEST) == 0) {
            borderInsets.left = 0;
        }
        if ((_sides & EAST) == 0) {
            borderInsets.right = 0;
        }
        return borderInsets;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        Insets borderInsets = super.getBorderInsets(c, insets);
        if ((_sides & NORTH) == 0) {
            borderInsets.top = 0;
        }
        if ((_sides & SOUTH) == 0) {
            borderInsets.bottom = 0;
        }
        if ((_sides & WEST) == 0) {
            borderInsets.left = 0;
        }
        if ((_sides & EAST) == 0) {
            borderInsets.right = 0;
        }
        return borderInsets;
    }
}
