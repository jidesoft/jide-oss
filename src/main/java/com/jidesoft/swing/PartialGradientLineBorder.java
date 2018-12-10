/*
 * @(#)PartialGradientLineBorder.java 4/13/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * This is a border which allows you to have gradient line only at one side or several sides.
 */
public class PartialGradientLineBorder extends AbstractBorder implements PartialSide {

    private int _sides = ALL;
    private Color[] _colors;
    protected int _thickness;

    public PartialGradientLineBorder(Color[] colors) {
        this(colors, 1);
    }

    public PartialGradientLineBorder(Color[] colors, int thickness) {
        this(colors, thickness, ALL);
    }

    public PartialGradientLineBorder(Color[] colors, int thickness, int sides) {
        if (colors.length < 2) {
            throw new IllegalArgumentException("Array \"colors\" should have at least 2 elements.");
        }
        _colors = colors;
        _thickness = thickness;
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
        Color oldColor = g.getColor();
        Graphics2D g2d = (Graphics2D) g;
        int i = 0;
        if ((_sides & NORTH) != 0) {
            JideSwingUtilities.fillGradient(g2d, new Rectangle(x, y, width, _thickness), _colors[i++], _colors[i++], false);
        }
        if ((_sides & SOUTH) != 0) {
            if (i >= _colors.length) {
                i -= 2;
            }
            JideSwingUtilities.fillGradient(g2d, new Rectangle(x, y + height - _thickness, width, _thickness), _colors[i++], _colors[i++], false);
        }
        if ((_sides & WEST) != 0) {
            if (i >= _colors.length) {
                i -= 2;
            }
            JideSwingUtilities.fillGradient(g2d, new Rectangle(x, y, _thickness, height), _colors[i++], _colors[i++], true);
        }
        if ((_sides & EAST) != 0) {
            if (i >= _colors.length) {
                i -= 2;
            }
            JideSwingUtilities.fillGradient(g2d, new Rectangle(x + width - _thickness, y, _thickness, height), _colors[i++], _colors[i], true);
        }
        g.setColor(oldColor);
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
