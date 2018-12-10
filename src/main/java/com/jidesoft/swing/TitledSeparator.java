/*
 * @(#)TitledSeparator.java 5/1/2008
 *
 * Copyright 2002 - 2008 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * <code>TitledSeparator</code> is a component used for separating components on a panel or in a GUI. Each
 * <code>TitledSeparator</code> is comprised of a title part and a graphical line. This component is preferred over
 * <code>TitledBorder</code> when no full border is desired. To see this component in action, please refer to the
 * TitledSeparator Demo. <p/> <b>Usage</b> <p/> This class presents a large number of constructors. As such,
 * configurating the <code>TitledSeparator</code> can be wholly achieved at construction time. The list below shows the
 * available constructors for this. <p/>
 * <code><pre>
 * TitledSeparator(String text)
 * TitledSeparator(String text, int textAlignment)
 * TitledSeparator(String text, int type, int textAlignment)
 * TitledSeparator(JComponent component, int textAlignment)
 * TitledSeparator(JComponent component, int type, int textAlignment)
 * TitledSeparator(JComponent component, Border border, int alignment)
 * TitledSeparator(JComponent component, int type, int textAlignment, int barAlignment)
 * TitledSeparator(JComponent component, Border border, int textAlignment, int barAlignment)
 * </pre></code>
 * <p/> Alternatively, you may use the empty constructor. When this approach is used, the text, border, text alignment
 * and border alignment will need to be explicitly set. For example:- <p/> <code>
 * <pre>
 * TitledSeparator separator = new TitledSeparator();
 * separator.setLabelComponent(new JLabel("Configured TitledSeparator"));
 * separator.setTextAlignment(right);
 * separator.setBarAlignment(center);
 * separator.setSeparatorBorder(new PartialEtchedBorder(PartialSide.SOUTH));
 * </pre>
 * </code> <p/> Please note, that this class should be constructed and modified from the EDT.<p> </p>
 */
public class TitledSeparator extends JPanel {

    /**
     * This value indicates an etched styled line separator.
     */
    public static final int TYPE_PARTIAL_ETCHED = 0;

    /**
     * This value indicates a solid line styled separator.
     */
    public static final int TYPE_PARTIAL_LINE = 1;

    /**
     * This value indicates a gradient line will be used for the separator.
     */
    public static final int TYPE_PARTIAL_GRADIENT_LINE = 2;

    /**
     * Indicates whether the text should be left, center or right aligned.
     *
     * @see #setTextAlignment
     */
    private int _textAlignment;

    /**
     * Indicates whether the horizontal bar should be top, center or bottom aligned.
     *
     * @see #setBarAlignment
     */
    private int _barAlignment;

    /**
     * The label holding the title for this TitledSeparator.
     *
     * @see #setLabelComponent
     */
    private JComponent _labelComponent;

    /**
     * The border used as the graphical line segment of this TitledSeparator.
     *
     * @see #setSeparatorBorder
     */
    private Border _border;

    /**
     * Identifies the label has changed.
     *
     * @see #setLabelComponent
     */
    public static final String PROPERTY_LABEL = "label";

    /**
     * Identifies the border has changed.
     *
     * @see #setSeparatorBorder
     */
    public static final String PROPERTY_SEPARATOR_BORDER = "separatorBorder";

    /**
     * Identifies a change in the bar alignment.
     *
     * @see #setBarAlignment
     */
    public static final String PROPERTY_BAR_ALIGNMENT = "barAlignment";

    /**
     * Identifies a change in the text alignment.
     *
     * @see #setTextAlignment
     */
    public static final String PROPERTY_TEXT_ALIGNMENT = "textAlignment";

    /******************************************************************************************************************
     *                                                    Construction.                                               *
     ******************************************************************************************************************/

    /**
     * Creates the default titled separator.
     */
    public TitledSeparator() {
        this("");
    }

    /**
     * Creates a default titled separator with the specified text.
     *
     * @param text the title of this titled separator
     */
    public TitledSeparator(String text) {
        this(text, TYPE_PARTIAL_ETCHED, SwingConstants.LEFT);
    }

    /**
     * Creates a titled separator with the specified text and alignment. The alignment determines whether the text will
     * lye on the right or left of the separator line.
     *
     * @param text          the title of this titled separator
     * @param textAlignment Valid values are: SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.LEADING or
     *                      SwingConstants.TRAILING.
     */
    public TitledSeparator(String text, int textAlignment) {
        this(new JLabel(text), TYPE_PARTIAL_ETCHED, textAlignment);
    }

    /**
     * Creates a titled separator with the specified text, border type and alignment. The alignment determines whether
     * the text will lye on the right or left of the separator line.
     *
     * @param text          the title of this titled separator
     * @param type          the style of border used as the separator line. Valid values are:-
     *                      TitledSeparator.TYPE_PARTIAL_ETCHED, TitledSeparator.TYPE_PARTIAL_LINE or
     *                      TitledSeparator.TYPE_PARTIAL_GRADIENT_LINE.
     * @param textAlignment Valid values are: SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.LEADING or
     *                      SwingConstants.TRAILING.
     */
    public TitledSeparator(String text, int type, int textAlignment) {
        this(new JLabel(text), type, textAlignment);
    }

    /**
     * Creates a titled separator with the specified component and alignment. The alignment determines whether the
     * specified component will lye on the right or left of the separator line.
     *
     * @param labelComponent the component to be used as the titled part of this separator
     * @param textAlignment  Valid values are: SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.LEADING or
     *                       SwingConstants.TRAILING.
     */
    public TitledSeparator(JComponent labelComponent, int textAlignment) {
        this(labelComponent, TYPE_PARTIAL_ETCHED, textAlignment);
    }

    /**
     * Creates a titled separator with the specified component, border type and alignment. The alignment determines
     * whether the specified component will lye on the right or left of the separator line.
     *
     * @param labelComponent the component to be used as the titled part of this separator
     * @param type           the style of border used as the separator line. Valid values are:-
     *                       TitledSeparator.TYPE_PARTIAL_ETCHED, TitledSeparator.TYPE_PARTIAL_LINE or
     *                       TitledSeparator.TYPE_PARTIAL_GRADIENT_LINE.
     * @param textAlignment  Valid values are: SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.LEADING or
     *                       SwingConstants.TRAILING.
     */
    public TitledSeparator(JComponent labelComponent, int type, int textAlignment) {
        this(labelComponent, type, textAlignment, SwingConstants.CENTER);
    }

    /**
     * Creates a titled separator with the specified component, border and alignment. The alignment determines whether
     * the specified component will lye on the right or left of the separator line.
     *
     * @param labelComponent the component to be used as the titled part of this separator
     * @param border         the border to be used as the separator line
     * @param textAlignment  Valid values are: SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.LEADING or
     *                       SwingConstants.TRAILING.
     */
    public TitledSeparator(JComponent labelComponent, Border border, int textAlignment) {
        this(labelComponent, border, textAlignment, SwingConstants.CENTER);
    }

    /**
     * Creates a titled separator with the specified component, border type, alignment and vertical alignment. The
     * alignment determines whether the specified component will lye on the right or left of the separator line. The
     * vertical alignment refers to the alignment of the separator line -- top, middle or bottom. Whenspecifyingg a
     * border, the border is set with default values, for example: both <code>TYPE_PARTIAL_LINE</code> and
     * <code>TYPE_PARTIAL_LINE</code> are configured with a 'thickness' of one pixel. If the the border required is not
     * the default version, please use another constructor: TitledSeparator(component, border, alignment,
     * verticalAlignment).
     *
     * @param labelComponent the component to be used as the titled part of this separator
     * @param type           the style of border used as the separator line. Valid values are:-
     *                       TitledSeparator.TYPE_PARTIAL_ETCHED, TitledSeparator.TYPE_PARTIAL_LINE or
     *                       TitledSeparator.TYPE_PARTIAL_GRADIENT_LINE.
     * @param textAlignment  Valid values are: SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.LEADING or
     *                       SwingConstants.TRAILING.
     * @param barAlignment   Valid values are: SwingConstants.TOP, SwingConstants.CENTER or SwingConstants.BOTTOM.
     */
    public TitledSeparator(JComponent labelComponent, int type, int textAlignment, int barAlignment) {
        final Border border;
        int side = PartialSide.SOUTH;
        int thickness = 1;

        Color color = labelComponent.getBackground();
        if (color == null) {
            color = UIDefaultsLookup.getColor("Label.background");
        }
        switch (type) {
            case TYPE_PARTIAL_LINE:
                Color c = color.darker();
                border = new PartialLineBorder(c, thickness, side);
                break;

            case TYPE_PARTIAL_GRADIENT_LINE:
                Color startColor = color.darker();
                Color finishColor = color.brighter();
                Color[] colors = {startColor, finishColor};
                border = new PartialGradientLineBorder(colors, thickness, side);
                break;

            case TYPE_PARTIAL_ETCHED:
            default:
                border = new PartialEtchedBorder(side);
                break;
        }

        _labelComponent = labelComponent;
        _border = border;
        _textAlignment = textAlignment;
        _barAlignment = barAlignment;
        validateTitledSeparator();
    }

    /**
     * Creates a titled separator with the specified component, border, alignment and vertical alignment. The alignment
     * determines whether the specified component will lye on the right or left of the separator line. The vertical
     * alignment refers to the alignment of the separator line -- top, middle or bottom.
     *
     * @param labelComponent the component to be used as the titled part of this separator
     * @param border         the border to be used as the separator line
     * @param textAlignment  Valid values are: SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.LEADING or
     *                       SwingConstants.TRAILING.
     * @param barAlignment   Valid values are: SwingConstants.TOP, SwingConstants.CENTER or SwingConstants.BOTTOM.
     */
    public TitledSeparator(JComponent labelComponent, Border border, int textAlignment, int barAlignment) {

        _labelComponent = labelComponent;
        _border = border;
        _textAlignment = textAlignment;
        _barAlignment = barAlignment;
        validateTitledSeparator();
    }

    /******************************************************************************************************************
     *                                                  Getter's/Setters.                                              *
     ******************************************************************************************************************/

    /**
     * Sets the labelComponent part of this Titled Separator.
     *
     * @param labelComponent the component holding the text to display as the title.
     * @see #getLabelComponent
     */
    public void setLabelComponent(JComponent labelComponent) {
        JComponent oldValue = _labelComponent;
        if (!JideSwingUtilities.equals(oldValue, labelComponent)) {
            _labelComponent = labelComponent;
            firePropertyChange(PROPERTY_LABEL, oldValue, _labelComponent);
            validateTitledSeparator();
            repaint();
        }
    }

    /**
     * Returns the label part of this Titled Separator.
     *
     * @return the component holding the text to display as the title.
     */
    public JComponent getLabelComponent() {
        return _labelComponent;
    }

    /**
     * Sets the border to be used as the graphical line region of this Titled Separator.
     *
     * @param border Any border of type: com.jidesoft.swing.PartialEtchedBorder, com.jidesoft.swing.PartialLineBorder or
     *               com.jidesoft.swing.PartialGradientLineBorder.
     * @see #getSeparatorBorder
     */
    public void setSeparatorBorder(Border border) {
        Border oldValue = _border;
        if (!JideSwingUtilities.equals(oldValue, border)) {
            _border = border;
            firePropertyChange(PROPERTY_SEPARATOR_BORDER, oldValue, _border);
            validateTitledSeparator();
            repaint();
        }
    }

    /**
     * Returns the border type.
     *
     * @return the border type. Valid return values are: TitledSeparator.TYPE_PARTIAL_ETCHED,
     *         TitledSeparator.TYPE_PARTIAL_LINE, TitledSeparator.TYPE_PARTIAL_GRADIENT_LINE or -1 if the current border
     *         is none of the above.
     */
    public Border getSeparatorBorder() {
        return _border;
    }

    /**
     * Sets the texts horizontal alignment.
     *
     * @param textAlignment an int representing the texts horizontal position. Valid values are: SwingConstants.LEFT,
     *                      SwingConstants.RIGHT, SwingConstants.LEADING or SwingConstants.TRAILING.
     * @see #getTextAlignment
     */
    public void setTextAlignment(int textAlignment) {
        int oldValue = _textAlignment;
        if (textAlignment != oldValue) {
            _textAlignment = textAlignment;
            firePropertyChange(PROPERTY_TEXT_ALIGNMENT, oldValue, _textAlignment);
            validateTitledSeparator();
            repaint();
        }
    }

    /**
     * Returns the texts horizontal alignment.
     *
     * @return the texts horizontal alignment. Valid return values are: SwingConstants.LEFT, SwingConstants.RIGHT, or
     *         SwingConstants.CENTER.
     */
    public int getTextAlignment() {
        return _textAlignment;
    }

    /**
     * Sets the bars vertical alignment.
     *
     * @param barAlignment Valid values are: SwingConstants.TOP, SwingConstants.CENTER or SwingConstants.BOTTOM.
     * @see #getBarAlignment
     */
    public void setBarAlignment(int barAlignment) {
        int oldValue = _barAlignment;
        if (barAlignment != oldValue) {
            _barAlignment = barAlignment;
            firePropertyChange(PROPERTY_BAR_ALIGNMENT, oldValue, _barAlignment);
            validateTitledSeparator();
            repaint();
        }
    }

    /**
     * Returns the bars vertical alignment.
     *
     * @return the barsverticall alignment. Valid values are: SwingConstants.TOP, SwingConstants.CENTER or
     *         SwingConstants.BOTTOM.
     */
    public int getBarAlignment() {
        return _barAlignment;
    }

    /******************************************************************************************************************
     *                                                  Configuration.                                                *
     ******************************************************************************************************************/

    /**
     * Responsible for configurating this <code>TitledSeparator<code>. This is the method through which all constructors
     * ultimately feed.<p>
     * <p/>
     * Creates a titled separator with the specified component, border, alignment and vertical alignment. The alignment
     * determines whether the specified component will lye on the right or left of the separator line. The vertical
     * alignment refers to the alignment of the separator line -- top, middle or bottom.
     */
    public void validateTitledSeparator() {
        /*
         * Bounds checking and argument configuration...
         */
        if (_labelComponent == null) {
            throw new NullPointerException("Component must not be null.");
        }

        if (_border == null) {
            throw new NullPointerException("border must not be null.");
        }

        if (!(_textAlignment == SwingConstants.LEFT ||
                _textAlignment == SwingConstants.RIGHT ||
                _textAlignment == SwingConstants.CENTER ||
                _textAlignment == SwingConstants.LEADING ||
                _textAlignment == SwingConstants.TRAILING)) {

            boolean ltr = getComponentOrientation().isLeftToRight();
            // invalid alignment. We default to left or right
            if (ltr) {
                _textAlignment = SwingConstants.LEFT;
            }
            else {
                _textAlignment = SwingConstants.RIGHT;
            }
        }

        if (_textAlignment == SwingConstants.LEADING ||
                _textAlignment == SwingConstants.TRAILING) {
            boolean LTR = getComponentOrientation().isLeftToRight();
            if (LTR) {
                _textAlignment = SwingConstants.LEFT;
            }
            else {
                _textAlignment = SwingConstants.RIGHT;
            }
        }

        if (!(_barAlignment == SwingConstants.TOP ||
                _barAlignment == SwingConstants.CENTER ||
                _barAlignment == SwingConstants.BOTTOM)) {
            // invalid alignment. We default to center.
            _barAlignment = SwingConstants.CENTER;
        }

        /*
         * Ensure we have a clean container to add to.
         */
        removeAll();

        /*
         * Next, we need to configure both the label and panel so that the text and separator are aligned 'vertically'.
         */
        if (_labelComponent instanceof JLabel) {
            ((JLabel) _labelComponent).setVerticalAlignment(SwingConstants.BOTTOM);
        }

        int top = 0, left = 0, bottom = 0, right = 0;
        Dimension compDimension = _labelComponent.getPreferredSize();
        int preferredHeight = compDimension.height;
        int separatorThickness = 2;
        try {
            separatorThickness = _border.getBorderInsets(null).bottom;
        }
        catch (NullPointerException e) {
            // ignore
        }

        if (_textAlignment == SwingConstants.LEFT) {
            left = 4;
        }
        else if (_textAlignment == SwingConstants.RIGHT) {
            right = 4;
        }
        else if (_textAlignment == SwingConstants.CENTER) {
            left = 4;
            right = 4;
        }

        if (_barAlignment == SwingConstants.CENTER) {
            bottom = (preferredHeight / 2) - (separatorThickness / 2);
        }
        else if (_barAlignment == SwingConstants.TOP) {
            bottom = preferredHeight - separatorThickness;
        }
        else if (_barAlignment == SwingConstants.BOTTOM) {
            bottom = 0;
        }

        Border margin = new EmptyBorder(top, left, bottom, right);
        JComponent separator = new JPanel();
        separator.setBorder(new CompoundBorder(margin, _border));

        /*
         * Finally, we can add the text (the label) and the separator (the panel) to this container
         * (this TitledSeparator).
         */
        setLayout(new JideBoxLayout(this, JideBoxLayout.X_AXIS));
        setOpaque(false);

        if (_textAlignment == SwingConstants.LEFT) {

            add(_labelComponent);
            add(separator, JideBoxLayout.VARY);
        }
        else if (_textAlignment == SwingConstants.RIGHT) {

            add(separator, JideBoxLayout.VARY);
            add(_labelComponent);
        }
        else {

            JComponent separator2 = new JPanel();
            separator2.setBorder(new CompoundBorder(margin, _border));

            add(separator, JideBoxLayout.FLEXIBLE);
            add(_labelComponent, JideBoxLayout.FIX);
            add(separator2, JideBoxLayout.FLEXIBLE);
        }
    }
}