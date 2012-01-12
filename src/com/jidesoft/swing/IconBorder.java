/*
 * @(#)IconBorder.java 1/11/2012
 *
 * Copyright 2002 - 2012 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * IconBorder creates a border that places an Icon in the border on one or several sides. For example, if you want to an
 * icon on the left side, you make the left inset to be the width of the icon, and 0 for the top, right and bottom
 * insets. Then you can call {@link #setHorizontalIconAlignment(int)} and set it to TOP or CENTER or BOTTOM.
 * <p/>
 * This border is useful when attempting to add Icons to pre-existing components without requiring specialty painting.
 * For example, use in the CellStyle to decorate the cell renderer with an icon.
 *
 * @since 3.3.3
 */
public class IconBorder extends MatteBorder {
    private static final long serialVersionUID = -5657747462265533365L;
    private int _horizontalIconAlignment = -1;
    private int _verticalIconAlignment = -1;

    /**
     * Creates an IconBorder with an icon. The right inset is the same as the icon width and 0 for all other insets. The
     * vertical icon alignment is set to TOP by default.
     *
     * @param icon the icon.
     */
    public IconBorder(Icon icon) {
        super(0, 0, 0, icon.getIconWidth(), icon);
        _verticalIconAlignment = SwingConstants.TOP;
    }

    /**
     * Creates an IconBorder with an icon. The right inset is the same as the icon width and 0 for all other insets.
     *
     * @param icon                  the icon.
     * @param verticalIconAlignment the vertical icon alignment.
     */
    public IconBorder(Icon icon, int verticalIconAlignment) {
        super(0, 0, 0, icon.getIconWidth(), icon);
        _verticalIconAlignment = verticalIconAlignment;
    }

    /**
     * Creates an IconBorder. The icon alignment is set to either TOP or TRAILING depending on value of the insets.
     *
     * @param borderInsets the insets of the border.
     * @param icon         the icon
     */
    public IconBorder(Insets borderInsets, Icon icon) {
        super(borderInsets, icon);
        if (borderInsets.right > 0 || borderInsets.left > 0) {
            _verticalIconAlignment = SwingConstants.TOP;
        }
        else if (borderInsets.top > 0 || borderInsets.bottom > 0) {
            _horizontalIconAlignment = SwingConstants.TRAILING;
        }
    }

    /**
     * Creates an IconBorder. The icon alignment is set to either TOP or TRAILING depending on value of the insets.
     *
     * @param top    the top inset of the border
     * @param left   the left inset of the border
     * @param bottom the bottom inset of the border
     * @param right  the right inset of the border
     * @param icon   the icon.
     */
    public IconBorder(int top, int left, int bottom, int right, Icon icon) {
        super(top, left, bottom, right, icon);
        if (right > 0 || left > 0) {
            _verticalIconAlignment = SwingConstants.TOP;
        }
        else if (top > 0 || bottom > 0) {
            _horizontalIconAlignment = SwingConstants.TRAILING;
        }
    }

    /**
     * Gets the icon alignment on the x axis. It is used to paint the icon when the top or bottom insets are not 0.
     *
     * @return the horizontal icon alignment.
     */
    public int getHorizontalIconAlignment() {
        return _horizontalIconAlignment;
    }

    /**
     * Sets the alignment of the icon relative to the component contents. This must be one of the following
     * SwingConstants: <pre>
     * <ul>
     *     <li> LEADING: respects the component orientation
     *     <li> TRAILING: respects the component orientation
     *     <li> LEFT
     *     <li> RIGHT
     *     <li> CENTER
     * </ul>
     * </pre>
     *
     * @param horizontalIconAlignment one of the five SwingConstants listed above.
     */
    public void setHorizontalIconAlignment(int horizontalIconAlignment) {
        _horizontalIconAlignment = horizontalIconAlignment;
    }

    /**
     * Gets the icon alignment on the y axis. It is used to paint the icon when the left or right insets are not 0.
     *
     * @return the vertical icon alignment.
     */
    public int getVerticalIconAlignment() {
        return _verticalIconAlignment;
    }

    /**
     * Sets the alignment of the icon relative to the component contents. This must be one of the following
     * SwingConstants: <pre>
     * <ul>
     *     <li> TOP
     *     <li> BOTTOM
     *     <li> CENTER
     * </ul>
     * </pre>
     *
     * @param verticalIconAlignment one of the three SwingConstants listed above.
     */
    public void setVerticalIconAlignment(int verticalIconAlignment) {
        _verticalIconAlignment = verticalIconAlignment;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if ((getHorizontalIconAlignment() == -1 && getVerticalIconAlignment() == -1) || tileIcon == null) {
            super.paintBorder(c, g, x, y, width, height);
        }
        else {
            Insets insets = getBorderInsets(c);
            Color oldColor = g.getColor();
            g.translate(x, y);

            // If the tileIcon failed loading, paint as gray.
            if (tileIcon != null) {
                color = (tileIcon.getIconWidth() == -1) ? Color.gray : null;
            }

            if (color != null) {
                g.setColor(color);
                g.fillRect(0, 0, width - insets.right, insets.top);
                g.fillRect(0, insets.top, insets.left, height - insets.top);
                g.fillRect(insets.left, height - insets.bottom, width - insets.left, insets.bottom);
                g.fillRect(width - insets.right, 0, insets.right, height - insets.bottom);

            }
            else if (tileIcon != null) {

                int tileW = tileIcon.getIconWidth();
                int tileH = tileIcon.getIconHeight();
                int xpos, ypos, startx, starty, iconAlignment;
                Graphics cg;

                if (insets.top > 0) {
                    // Paint top matte edge
                    cg = g.create();
                    cg.setClip(0, 0, width, insets.top);
                    startx = insets.left;
                    starty = 0;
                    iconAlignment = getVerticalIconAlignment();
                    if (!c.getComponentOrientation().isLeftToRight()) {
                        if (iconAlignment == SwingConstants.LEADING) {
                            iconAlignment = SwingConstants.RIGHT;
                        }
                        else if (iconAlignment == SwingConstants.TRAILING) {
                            iconAlignment = SwingConstants.LEFT;
                        }
                    }

                    switch (iconAlignment) {
                        case SwingConstants.LEADING:
                        case SwingConstants.LEFT:
                            xpos = startx;
                            ypos = starty;
                            break;
                        case SwingConstants.CENTER:
                            xpos = startx + (width - tileW) / 2;
                            ypos = starty;
                            break;
                        case SwingConstants.TRAILING:
                        case SwingConstants.RIGHT:
                        default:
                            xpos = startx + width - tileW;
                            ypos = starty;
                            break;
                    }
                    tileIcon.paintIcon(c, cg, xpos, ypos);
                    cg.dispose();
                }

                if (insets.left > 0) {
                    // Paint left matte edge
                    cg = g.create();
                    cg.setClip(0, insets.top, insets.left, height - insets.top);
                    starty = insets.top;
                    startx = 0;
                    switch (getVerticalIconAlignment()) {
                        case SwingConstants.LEADING:
                        case SwingConstants.TOP:
                            xpos = startx;
                            ypos = starty;
                            break;
                        case SwingConstants.CENTER:
                            xpos = startx;
                            ypos = starty + (height - tileH) / 2;
                            break;
                        case SwingConstants.TRAILING:
                        case SwingConstants.BOTTOM:
                        default:
                            xpos = startx;
                            ypos = starty + height - tileH;
                            break;
                    }
                    tileIcon.paintIcon(c, cg, xpos, ypos);
                    cg.dispose();
                }

                if (insets.bottom > 0) {
                    // Paint bottom matte edge
                    cg = g.create();
                    cg.setClip(insets.left, height - insets.bottom, width - insets.left, insets.bottom);
                    starty = (height - insets.bottom);
                    startx = insets.left;
                    iconAlignment = getHorizontalIconAlignment();
                    if (!c.getComponentOrientation().isLeftToRight()) {
                        if (iconAlignment == SwingConstants.LEADING) {
                            iconAlignment = SwingConstants.RIGHT;
                        }
                        else if (iconAlignment == SwingConstants.TRAILING) {
                            iconAlignment = SwingConstants.LEFT;
                        }
                    }

                    switch (iconAlignment) {
                        case SwingConstants.LEADING:
                        case SwingConstants.LEFT:
                            xpos = startx;
                            ypos = starty;
                            break;
                        case SwingConstants.CENTER:
                            xpos = startx + (width - tileW) / 2;
                            ypos = starty;
                            break;
                        case SwingConstants.TRAILING:
                        case SwingConstants.RIGHT:
                        default:
                            xpos = startx + width - tileW;
                            ypos = starty;
                            break;
                    }
                    tileIcon.paintIcon(c, cg, xpos, ypos);
                    cg.dispose();
                }

                if (insets.right > 0) {
                    // Paint right matte edge
                    cg = g.create();
                    cg.setClip(width - insets.right, insets.top, insets.right, height - insets.top - insets.bottom);
                    starty = insets.top;
                    startx = width - insets.right;
                    switch (getVerticalIconAlignment()) {
                        case SwingConstants.LEADING:
                        case SwingConstants.TOP:
                            xpos = startx;
                            ypos = starty;
                            break;
                        case SwingConstants.CENTER:
                            xpos = startx;
                            ypos = starty + (height - tileH) / 2;
                            break;
                        case SwingConstants.TRAILING:
                        case SwingConstants.BOTTOM:
                        default:
                            xpos = startx;
                            ypos = starty + height - tileH;
                            break;
                    }
                    tileIcon.paintIcon(c, cg, xpos, ypos);
                    cg.dispose();
                }
            }
            g.translate(-x, -y);
            g.setColor(oldColor);
        }
    }
}
