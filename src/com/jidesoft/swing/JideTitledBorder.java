/*
 * @(#)JideTitledBorder.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import java.awt.*;

/**
 * The source code is the same as TitledBorder in JDK 1.4.2 except field TEXT_INSET_H is 0 instead
 * of 5.
 */
public class JideTitledBorder extends AbstractBorder {

    protected String title;
    protected Border border;
    protected int titlePosition;
    protected int titleJustification;
    protected Font titleFont;
    protected Color titleColor;

    private Point textLoc = new Point();

    /**
     * Use the default vertical orientation for the title text.
     */
    static public final int DEFAULT_POSITION = 0;
    /**
     * Position the title above the border's top line.
     */
    static public final int ABOVE_TOP = 1;
    /**
     * Position the title in the middle of the border's top line.
     */
    static public final int TOP = 2;
    /**
     * Position the title below the border's top line.
     */
    static public final int BELOW_TOP = 3;
    /**
     * Position the title above the border's bottom line.
     */
    static public final int ABOVE_BOTTOM = 4;
    /**
     * Position the title in the middle of the border's bottom line.
     */
    static public final int BOTTOM = 5;
    /**
     * Position the title below the border's bottom line.
     */
    static public final int BELOW_BOTTOM = 6;

    /**
     * Use the default justification for the title text.
     */
    static public final int DEFAULT_JUSTIFICATION = 0;
    /**
     * Position title text at the left side of the border line.
     */
    static public final int LEFT = 1;
    /**
     * Position title text in the center of the border line.
     */
    static public final int CENTER = 2;
    /**
     * Position title text at the right side of the border line.
     */
    static public final int RIGHT = 3;
    /**
     * Position title text at the left side of the border line for left to right orientation, at the
     * right side of the border line for right to left orientation.
     */
    static public final int LEADING = 4;
    /**
     * Position title text at the right side of the border line for left to right orientation, at
     * the left side of the border line for right to left orientation.
     */
    static public final int TRAILING = 5;

    // Space between the border and the component's edge
    static protected final int EDGE_SPACING = 2;

    static protected final int TITLE_MARGIN = 5;

    // Space between the border and text
    static protected final int TEXT_SPACING = 2;

    // Horizontal inset of text that is left or right justified
    static protected final int TEXT_INSET_H = 0;

    /**
     * Creates a JideTitledBorder instance.
     *
     * @param title the title the border should display
     */
    public JideTitledBorder(String title) {
        this(null, title, LEADING, TOP, null, null);
    }

    /**
     * Creates a JideTitledBorder instance with the specified border and an empty title.
     *
     * @param border the border
     */
    public JideTitledBorder(Border border) {
        this(border, "", LEADING, TOP, null, null);
    }

    /**
     * Creates a JideTitledBorder instance with the specified border and title.
     *
     * @param border the border
     * @param title  the title the border should display
     */
    public JideTitledBorder(Border border, String title) {
        this(border, title, LEADING, TOP, null, null);
    }

    /**
     * Creates a JideTitledBorder instance with the specified border, title, title-justification,
     * and title-position.
     *
     * @param border             the border
     * @param title              the title the border should display
     * @param titleJustification the justification for the title
     * @param titlePosition      the position for the title
     */
    public JideTitledBorder(Border border,
                            String title,
                            int titleJustification,
                            int titlePosition) {
        this(border, title, titleJustification,
                titlePosition, null, null);
    }

    /**
     * Creates a JideTitledBorder instance with the specified border, title, title-justification,
     * title-position, and title-font.
     *
     * @param border             the border
     * @param title              the title the border should display
     * @param titleJustification the justification for the title
     * @param titlePosition      the position for the title
     * @param titleFont          the font for rendering the title
     */
    public JideTitledBorder(Border border,
                            String title,
                            int titleJustification,
                            int titlePosition,
                            Font titleFont) {
        this(border, title, titleJustification,
                titlePosition, titleFont, null);
    }

    /**
     * Creates a JideTitledBorder instance with the specified border, title, title-justification,
     * title-position, title-font, and title-color.
     *
     * @param border             the border
     * @param title              the title the border should display
     * @param titleJustification the justification for the title
     * @param titlePosition      the position for the title
     * @param titleFont          the font of the title
     * @param titleColor         the color of the title
     */
    public JideTitledBorder(Border border,
                            String title,
                            int titleJustification,
                            int titlePosition,
                            Font titleFont,
                            Color titleColor) {
        this.title = title;
        this.border = border;
        this.titleFont = titleFont;
        this.titleColor = titleColor;

        setTitleJustification(titleJustification);
        setTitlePosition(titlePosition);
    }

    /**
     * Paints the border for the specified component with the specified position and size.
     *
     * @param c      the component for which this border is being painted
     * @param g      the paint graphics
     * @param x      the x position of the painted border
     * @param y      the y position of the painted border
     * @param width  the width of the painted border
     * @param height the height of the painted border
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        Border border = getBorder();

        if (getTitle() == null || getTitle().equals("")) {
            if (border != null) {
                border.paintBorder(c, g, x, y, width, height);
            }
            return;
        }

        Rectangle grooveRect = new Rectangle(x + EDGE_SPACING, y + EDGE_SPACING,
                width - (EDGE_SPACING << 1),
                height - (EDGE_SPACING << 1));
        Font font = g.getFont();
        Color color = g.getColor();

        g.setFont(getFont(c));

        FontMetrics fm = g.getFontMetrics();
        int fontHeight = fm.getHeight();
        int descent = fm.getDescent();
        int ascent = fm.getAscent();
        int diff;
        int stringWidth = fm.stringWidth(getTitle());
        Insets insets;

        if (border != null) {
            insets = border.getBorderInsets(c);
        }
        else {
            insets = new Insets(0, 0, 0, 0);
        }

        int titlePos = getTitlePosition();
        switch (titlePos) {
            case ABOVE_TOP:
                diff = ascent + descent + (Math.max(EDGE_SPACING,
                        TEXT_SPACING << 1) - EDGE_SPACING);
                grooveRect.y += diff;
                grooveRect.height -= diff;
                textLoc.y = grooveRect.y - (descent + TEXT_SPACING);
                break;
            case TOP:
            case DEFAULT_POSITION:
                diff = Math.max(0, ((ascent >> 1) + TEXT_SPACING) - EDGE_SPACING);
                grooveRect.y += diff;
                grooveRect.height -= diff;
                textLoc.y = (grooveRect.y - descent) +
                        ((insets.top + ascent + descent) >> 1);
                break;
            case BELOW_TOP:
                textLoc.y = grooveRect.y + insets.top + ascent + TEXT_SPACING;
                break;
            case ABOVE_BOTTOM:
                textLoc.y = (grooveRect.y + grooveRect.height) -
                        (insets.bottom + descent + TEXT_SPACING);
                break;
            case BOTTOM:
                grooveRect.height -= fontHeight >> 1;
                textLoc.y = ((grooveRect.y + grooveRect.height) - descent) +
                        (((ascent + descent) - insets.bottom) >> 1);
                break;
            case BELOW_BOTTOM:
                grooveRect.height -= fontHeight;
                textLoc.y = grooveRect.y + grooveRect.height + ascent +
                        TEXT_SPACING;
                break;
        }

        int justification = getTitleJustification();
        if (c.getComponentOrientation().isLeftToRight()) {
            if (justification == LEADING ||
                    justification == DEFAULT_JUSTIFICATION) {
                justification = LEFT;
            }
            else if (justification == TRAILING) {
                justification = RIGHT;
            }
        }
        else {
            if (justification == LEADING ||
                    justification == DEFAULT_JUSTIFICATION) {
                justification = RIGHT;
            }
            else if (justification == TRAILING) {
                justification = LEFT;
            }
        }

        switch (justification) {
            case LEFT:
                textLoc.x = grooveRect.x + TEXT_INSET_H + insets.left;
                break;
            case RIGHT:
                textLoc.x = (grooveRect.x + grooveRect.width) -
                        (stringWidth + TEXT_INSET_H + insets.right);
                break;
            case CENTER:
                textLoc.x = grooveRect.x +
                        ((grooveRect.width - stringWidth) >> 1);
                break;
        }

        // If title is positioned in middle of border AND its fontsize
        // is greater than the border's thickness, we'll need to paint
        // the border in sections to leave space for the component's background
        // to show through the title.
        //
        if (border != null) {
            if (((titlePos == TOP || titlePos == DEFAULT_POSITION) && (grooveRect.y > textLoc.y - ascent)) ||
                    (titlePos == BOTTOM && (grooveRect.y + grooveRect.height < textLoc.y + descent))) {

                Rectangle clipRect = new Rectangle();

                // save original clip
                Rectangle saveClip = g.getClipBounds();

                // paint strip left of text
                clipRect.setBounds(saveClip);
                if (computeIntersection(clipRect, x, y, textLoc.x - TITLE_MARGIN - x, height)) {
                    g.setClip(clipRect);
                    border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                            grooveRect.width, grooveRect.height);
                }

                // paint strip right of text
                clipRect.setBounds(saveClip);
                if (computeIntersection(clipRect, textLoc.x + stringWidth + TITLE_MARGIN, y,
                        x + width - (textLoc.x + stringWidth + TITLE_MARGIN), height)) {
                    g.setClip(clipRect);
                    border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                            grooveRect.width, grooveRect.height);
                }

                if (titlePos == TOP || titlePos == DEFAULT_POSITION) {
                    // paint strip below text
                    clipRect.setBounds(saveClip);
                    if (computeIntersection(clipRect, textLoc.x - TITLE_MARGIN, textLoc.y + descent,
                            stringWidth + 2 * TITLE_MARGIN, y + height - textLoc.y - descent)) {
                        g.setClip(clipRect);
                        border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                                grooveRect.width, grooveRect.height);
                    }

                }
                else { // titlePos == BOTTOM
                    // paint strip above text
                    clipRect.setBounds(saveClip);
                    if (computeIntersection(clipRect, textLoc.x - TITLE_MARGIN, y,
                            stringWidth + 2 * TITLE_MARGIN, textLoc.y - ascent - y)) {
                        g.setClip(clipRect);
                        border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                                grooveRect.width, grooveRect.height);
                    }
                }

                // restore clip
                g.setClip(saveClip);

            }
            else {
                border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                        grooveRect.width, grooveRect.height);
            }
        }

        g.setColor(getTitleColor());
        JideSwingUtilities.drawString((JComponent) c, g, getTitle(), textLoc.x, textLoc.y);

        g.setFont(font);
        g.setColor(color);
    }

    /**
     * Returns the insets of the border.
     *
     * @param c the component for which this border insets value applies
     */
    @Override
    public Insets getBorderInsets(Component c) {
        return getBorderInsets(c, new Insets(0, 0, 0, 0));
    }

    /**
     * Reinitialize the insets parameter with this Border's current Insets.
     *
     * @param c      the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        FontMetrics fm;
        int descent = 0;
        int ascent = 16;
        int height = 16;

        Border border = getBorder();
        if (border != null) {
            if (border instanceof AbstractBorder) {
                ((AbstractBorder) border).getBorderInsets(c, insets);
            }
            else {
                // Can't reuse border insets because the Border interface
                // can't be enhanced.
                Insets i = border.getBorderInsets(c);
                insets.top = i.top;
                insets.right = i.right;
                insets.bottom = i.bottom;
                insets.left = i.left;
            }
        }
        else {
            insets.left = insets.top = insets.right = insets.bottom = 0;
        }

        insets.left += EDGE_SPACING + (insets.left > 0 ? TEXT_SPACING : 0);
        insets.right += EDGE_SPACING + (insets.right > 0 ? TEXT_SPACING : 0);
        insets.top += EDGE_SPACING + (insets.top > 0 ? TEXT_SPACING : 0);
        insets.bottom += EDGE_SPACING + (insets.bottom > 0 ? TEXT_SPACING : 0);

        if (c == null || getTitle() == null || getTitle().equals("")) {
            return insets;
        }

        Font font = getFont(c);

        fm = c.getFontMetrics(font);

        if (fm != null) {
            descent = fm.getDescent();
            ascent = fm.getAscent();
            height = fm.getHeight();
        }

        switch (getTitlePosition()) {
            case ABOVE_TOP:
                insets.top += ascent + descent
                        + (Math.max(EDGE_SPACING, TEXT_SPACING << 1)
                        - EDGE_SPACING);
                break;
            case TOP:
            case DEFAULT_POSITION:
                insets.top += ascent + descent;
                break;
            case BELOW_TOP:
                insets.top += ascent + descent + TEXT_SPACING;
                break;
            case ABOVE_BOTTOM:
                insets.bottom += ascent + descent + TEXT_SPACING;
                break;
            case BOTTOM:
                insets.bottom += ascent + descent;
                break;
            case BELOW_BOTTOM:
                insets.bottom += height;
                break;
        }
        return insets;
    }

    /**
     * Returns whether or not the border is opaque.
     */
    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    /**
     * Returns the title of the titled border.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the border of the titled border.
     */
    public Border getBorder() {
        Border b = border;
        if (b == null)
            b = UIDefaultsLookup.getBorder("TitledBorder.border");
        return b;
    }

    /**
     * Returns the title-position of the titled border.
     */
    public int getTitlePosition() {
        return titlePosition;
    }

    /**
     * Returns the title-justification of the titled border.
     */
    public int getTitleJustification() {
        return titleJustification;
    }

    /**
     * Returns the title-font of the titled border.
     */
    public Font getTitleFont() {
        Font f = titleFont;
        if (f == null)
            f = UIDefaultsLookup.getFont("TitledBorder.font");
        return f;
    }

    /**
     * Returns the title-color of the titled border.
     */
    public Color getTitleColor() {
        Color c = titleColor;
        if (c == null)
            c = UIDefaultsLookup.getColor("TitledBorder.titleColor");
        return c;
    }

    // REMIND(aim): remove all or some of these set methods?

    /**
     * Sets the title of the titled border. param title the title for the border
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the border of the titled border.
     *
     * @param border the border
     */
    public void setBorder(Border border) {
        this.border = border;
    }

    /**
     * Sets the title-position of the titled border.
     *
     * @param titlePosition the position for the border
     */
    public void setTitlePosition(int titlePosition) {
        switch (titlePosition) {
            case ABOVE_TOP:
            case TOP:
            case BELOW_TOP:
            case ABOVE_BOTTOM:
            case BOTTOM:
            case BELOW_BOTTOM:
            case DEFAULT_POSITION:
                this.titlePosition = titlePosition;
                break;
            default:
                throw new IllegalArgumentException(titlePosition +
                        " is not a valid title position.");
        }
    }

    /**
     * Sets the title-justification of the titled border.
     *
     * @param titleJustification the justification for the border
     */
    public void setTitleJustification(int titleJustification) {
        switch (titleJustification) {
            case DEFAULT_JUSTIFICATION:
            case LEFT:
            case CENTER:
            case RIGHT:
            case LEADING:
            case TRAILING:
                this.titleJustification = titleJustification;
                break;
            default:
                throw new IllegalArgumentException(titleJustification +
                        " is not a valid title justification.");
        }
    }

    /**
     * Sets the title-font of the titled border.
     *
     * @param titleFont the font for the border title
     */
    public void setTitleFont(Font titleFont) {
        this.titleFont = titleFont;
    }

    /**
     * Sets the title-color of the titled border.
     *
     * @param titleColor the color for the border title
     */
    public void setTitleColor(Color titleColor) {
        this.titleColor = titleColor;
    }

    /**
     * Returns the minimum dimensions this border requires in order to fully display the border and
     * title.
     *
     * @param c the component where this border will be drawn
     */
    public Dimension getMinimumSize(Component c) {
        Insets insets = getBorderInsets(c);
        Dimension minSize = new Dimension(insets.right + insets.left,
                insets.top + insets.bottom);
        Font font = getFont(c);
        FontMetrics fm = c.getFontMetrics(font);
        switch (titlePosition) {
            case ABOVE_TOP:
            case BELOW_BOTTOM:
                minSize.width = Math.max(fm.stringWidth(getTitle()), minSize.width);
                break;
            case BELOW_TOP:
            case ABOVE_BOTTOM:
            case TOP:
            case BOTTOM:
            case DEFAULT_POSITION:
            default:
                minSize.width += fm.stringWidth(getTitle());
        }
        return minSize;
    }

    protected Font getFont(Component c) {
        Font font;
        if ((font = getTitleFont()) != null) {
            return font;
        }
        else if (c != null && (font = c.getFont()) != null) {
            return font;
        }
        return new Font("Dialog", Font.PLAIN, 12);
    }

    private static boolean computeIntersection(Rectangle dest,
                                               int rx, int ry, int rw, int rh) {
        int x1 = Math.max(rx, dest.x);
        int x2 = Math.min(rx + rw, dest.x + dest.width);
        int y1 = Math.max(ry, dest.y);
        int y2 = Math.min(ry + rh, dest.y + dest.height);
        dest.x = x1;
        dest.y = y1;
        dest.width = x2 - x1;
        dest.height = y2 - y1;

        if (dest.width <= 0 || dest.height <= 0) {
            return false;
        }
        return true;
    }
}
