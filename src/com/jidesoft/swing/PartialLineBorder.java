package com.jidesoft.swing;

import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * This is a better version of LineBorder which allows you to show line only at one side or several
 * sides and supports rounded corner.
 */
public class PartialLineBorder extends LineBorder implements PartialSide {

    private int _sides = ALL;
    private int _roundedCornerSize = 5;

    public PartialLineBorder(Color color) {
        super(color);
    }

    public PartialLineBorder(Color color, int thickness) {
        super(color, thickness);
    }

    public PartialLineBorder(Color color, int thickness, boolean roundedCorners) {
        super(color, thickness, roundedCorners);
    }

    public PartialLineBorder(Color color, int thickness, boolean roundedCorners, int roundedCornerSize) {
        super(color, thickness, roundedCorners);
        _roundedCornerSize = roundedCornerSize;
    }

    public PartialLineBorder(Color color, int thickness, int side) {
        super(color, thickness);
        _sides = side;
    }

    public int getSides() {
        return _sides;
    }

    public void setSides(int sides) {
        _sides = sides;
    }

    public int getRoundedCornerSize() {
        return _roundedCornerSize;
    }

    public void setRoundedCornerSize(int roundedCornerSize) {
        _roundedCornerSize = roundedCornerSize;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color oldColor = g.getColor();
        int i;

        g.setColor(lineColor);
        for (i = 0; i < thickness; i++) {
            if (_sides == ALL) {
                if (!roundedCorners)
                    g.drawRect(x + i, y + i, width - i - i - 1, height - i - i - 1);
                else {
                    Object o = JideSwingUtilities.setupShapeAntialiasing(g);
                    g.drawRoundRect(x + i, y + i, width - i - i - 1, height - i - i - 1, _roundedCornerSize, _roundedCornerSize);
                    JideSwingUtilities.restoreShapeAntialiasing(g, o);
                }
            }

            else {
                if ((_sides & NORTH) != 0) {
                    g.drawLine(x, y + i, x + width - 1, y + i);
                }
                if ((_sides & SOUTH) != 0) {
                    g.drawLine(x, y + height - i - 1, x + width - 1, y + height - i - 1);
                }
                if ((_sides & WEST) != 0) {
                    g.drawLine(x + i, y, x + i, y + height - 1);
                }
                if ((_sides & EAST) != 0) {
                    g.drawLine(x + width - i - 1, y, x + width - i - 1, y + height - 1);
                }
            }

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
