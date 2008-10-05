/*
 * @(#)TextStyle.java 9/6/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import java.awt.*;

/**
 * A data structure represents a style for a range of text. There are two categories of styles that currently supports.
 * One is the font style and color which includes bold, italic, superscript, subscript as well as the color of the text.
 * The other one is line color and style. The line style could be straight line, dotted line, waved line or any
 * customized style using Stroke. The line could be used as underline or strikethrough line.
 * <p/>
 * The name of StyleRange comes from SWT's StyleRange. We borrowed some design idea from it. StyledLabel is actually
 * very similar to SWT's StyledText. Saying that, the features of the two components are not exactly the same since the
 * purpose of the two components are quite different.
 */
public class StyleRange {
    public static final int STYLE_STRIKE_THROUGH = 0x1;
    public static final int STYLE_DOUBLE_STRIKE_THROUGH = STYLE_STRIKE_THROUGH << 1;
    public static final int STYLE_WAVED = STYLE_DOUBLE_STRIKE_THROUGH << 1;
    public static final int STYLE_UNDERLINED = STYLE_WAVED << 1;
    public static final int STYLE_DOTTED = STYLE_UNDERLINED << 1;
    public static final int STYLE_SUPERSCRIPT = STYLE_DOTTED << 1;
    public static final int STYLE_SUBSCRIPT = STYLE_SUPERSCRIPT << 1;

    private final int _fontStyle;
    private final Color _fontColor;

    private final Color _backgroundColor;

    private final Color _lineColor;
    private final Stroke _lineStroke;
    private final int _additionalStyle;

    private final int _start;
    private final int _length;

    private float _fontShrinkRatio = 1.5f;

    /**
     * Creates a style range with a specified font style.
     *
     * @param fontStyle Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     */
    public StyleRange(int fontStyle) {
        this(0, -1, fontStyle, null, 0, null, null);
    }

    /**
     * Creates a style range with a specified font color.
     *
     * @param fontColor the color of the text
     */
    public StyleRange(Color fontColor) {
        this(0, -1, -1, fontColor, 0, null, null);
    }

    /**
     * Creates a style range with a specified font style and font color.
     *
     * @param fontStyle Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param fontColor the color of the text
     */
    public StyleRange(int fontStyle, Color fontColor) {
        this(0, -1, fontStyle, fontColor, 0, null, null);
    }

    /**
     * Creates a style range with a specified font style and additional style.
     *
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use any | to connect two or more styles as long as it makes
     *                        sense.
     */
    public StyleRange(int fontStyle, int additionalStyle) {
        this(0, -1, fontStyle, null, additionalStyle, null, null);
    }

    /**
     * Creates a style range with a specified font style and additional style.
     *
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use any | to concat two or more styles as long as it makes
     *                        sense.
     * @param fontShrinkRatio the ratio that regular font size divides by subscript or superscript font size.
     */
    public StyleRange(int fontStyle, int additionalStyle, float fontShrinkRatio) {
        this(0, -1, fontStyle, null, additionalStyle, null, null, fontShrinkRatio);
    }

    /**
     * Creates a style range with a specified font style and a range.
     *
     * @param start     the start index of the range in a string
     * @param length    the length of the range.
     * @param fontStyle Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     */
    public StyleRange(int start, int length, int fontStyle) {
        this(start, length, fontStyle, null, 0, null, null);
    }

    /**
     * Creates a style range with a specified font style, font color and a range.
     *
     * @param start     the start index of the range in a string
     * @param length    the length of the range.
     * @param fontStyle Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param fontColor the color of the text.
     */
    public StyleRange(int start, int length, int fontStyle, Color fontColor) {
        this(start, length, fontStyle, fontColor, 0, null, null);
    }

    /**
     * Creates a style range with a specified font color and a range.
     *
     * @param start     the start index of the range in a string
     * @param length    the length of the range.
     * @param fontColor the color of the text.
     */
    public StyleRange(int start, int length, Color fontColor) {
        this(start, length, Font.PLAIN, fontColor, 0, null, null);
    }

    /**
     * Creates a style range with a specified font style, additional style and a range.
     *
     * @param start           the start index of the range in a string
     * @param length          the length of the range.
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use any | to concat two or more styles as long as it makes
     *                        sense.
     */
    public StyleRange(int start, int length, int fontStyle, int additionalStyle) {
        this(start, length, fontStyle, null, additionalStyle, null, null);
    }

    /**
     * Creates a style range with a specified font style, additional style and a range.
     *
     * @param start           the start index of the range in a string
     * @param length          the length of the range.
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use any | to concat two or more styles as long as it makes
     *                        sense.
     * @param fontShrinkRatio the ratio that regular font size divides by subscript or superscript font size.
     */
    public StyleRange(int start, int length, int fontStyle, int additionalStyle, float fontShrinkRatio) {
        this(start, length, fontStyle, null, additionalStyle, null, null, fontShrinkRatio);
    }

    /**
     * Creates a style range with a specified font style, font color, and additional style.
     *
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param fontColor       the color of the text.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use any | to concat two or more styles as long as it makes
     *                        sense.
     */
    public StyleRange(int fontStyle, Color fontColor, int additionalStyle, Color lineColor) {
        this(0, -1, fontStyle, fontColor, additionalStyle, lineColor, null);
    }

    /**
     * Creates a style range with a specified font style, font color, and additional style.
     *
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param fontColor       the color of the text.
     * @param backgroundColor the background color of the text.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use any | to concat two or more styles as long as it makes
     *                        sense.
     */
    public StyleRange(int fontStyle, Color fontColor, Color backgroundColor, int additionalStyle, Color lineColor) {
        this(0, -1, fontStyle, fontColor, backgroundColor, additionalStyle, lineColor, null);
    }

    /**
     * Creates a style range with a specified font style, font color, additional style and a range.
     *
     * @param start           the start index of the range in a string
     * @param length          the length of the range.
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param fontColor       the color of the text.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use any | to concat two or more styles as long as it makes
     *                        sense.
     */
    public StyleRange(int start, int length, int fontStyle, Color fontColor, int additionalStyle) {
        this(start, length, fontStyle, fontColor, additionalStyle, null, null);
    }

    /**
     * Creates a style range with a specified font style, font color, additional style and a range.
     *
     * @param start           the start index of the range in a string
     * @param length          the length of the range.
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param fontColor       the color of the text.
     * @param backgroundColor the background color of the text.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use any | to concat two or more styles as long as it makes
     *                        sense.
     */
    public StyleRange(int start, int length, int fontStyle, Color fontColor, Color backgroundColor, int additionalStyle) {
        this(start, length, fontStyle, fontColor, backgroundColor, additionalStyle, null, null);
    }

    /**
     * Creates a style range with a specified font style, font color, additional style, and line color.
     *
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param fontColor       the color of the text.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use any | to concat two or more styles as long as it makes
     *                        sense.
     * @param lineColor       the color of the line.
     */
    public StyleRange(int fontStyle, Color fontColor, int additionalStyle, Color lineColor, Stroke lineStroke) {
        this(0, -1, fontStyle, fontColor, additionalStyle, lineColor, lineStroke);
    }

    /**
     * Creates a style range with a specified font style, font color, additional style, line color and a range.
     *
     * @param start           the start index of the range in a string
     * @param length          the length of the range.
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param fontColor       the color of the text.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use any | to concat two or more styles as long as it makes
     *                        sense.
     * @param lineColor       the color of the line.
     */
    public StyleRange(int start, int length, int fontStyle, Color fontColor, int additionalStyle, Color lineColor) {
        this(start, length, fontStyle, fontColor, additionalStyle, lineColor, null);
    }

    /**
     * Creates a style range with a specified font style, font color, additional style, line color and a range.
     *
     * @param start           the start index of the range in a string
     * @param length          the length of the range.
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param fontColor       the color of the text.
     * @param backgroundColor the background color of the text.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use any | to concat two or more styles as long as it makes
     *                        sense.
     * @param lineColor       the color of the line.
     */
    public StyleRange(int start, int length, int fontStyle, Color fontColor, Color backgroundColor, int additionalStyle, Color lineColor) {
        this(start, length, fontStyle, fontColor, backgroundColor, additionalStyle, lineColor, null);
    }

    /**
     * Creates a style range with a specified font style, font color, additional style, line color, line stroke and a
     * range.
     *
     * @param start           the start index of the range in a string
     * @param length          the length of the range.
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param fontColor       the color of the text.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use any | to concat two or more styles as long as it makes
     *                        sense.
     * @param lineColor       the color of the line.
     * @param lineStroke      the stroke of the line.
     */
    public StyleRange(int start, int length, int fontStyle, Color fontColor, int additionalStyle, Color lineColor, Stroke lineStroke) {
        this(start, length, fontStyle, fontColor, additionalStyle, lineColor, lineStroke, 1.5f);
    }

    /**
     * Creates a style range with a specified font style, font color, additional style, line color, line stroke and a
     * range.
     *
     * @param start           the start index of the range in a string
     * @param length          the length of the range.
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param fontColor       the color of the text.
     * @param backgroundColor the background color of the text.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use any | to concat two or more styles as long as it makes
     *                        sense.
     * @param lineColor       the color of the line.
     * @param lineStroke      the stroke of the line.
     */
    public StyleRange(int start, int length, int fontStyle, Color fontColor, Color backgroundColor, int additionalStyle, Color lineColor, Stroke lineStroke) {
        this(start, length, fontStyle, fontColor, backgroundColor, additionalStyle, lineColor, lineStroke, 1.5f);
    }

    /**
     * Creates a style range with a specified font style, font color, additional style, line color, line stroke and a
     * range.
     *
     * @param start           the start index of the range in a string
     * @param length          the length of the range.
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param fontColor       the color of the text.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use any | to concat two or more styles as long as it makes
     *                        sense.
     * @param lineColor       the color of the line.
     * @param lineStroke      the stroke of the line.
     * @param fontShrinkRatio the ratio that regular font size divides by subscript or superscript font size.
     */
    public StyleRange(int start, int length, int fontStyle, Color fontColor, int additionalStyle, Color lineColor, Stroke lineStroke, float fontShrinkRatio) {
        this(start, length, fontStyle, fontColor, null, additionalStyle, lineColor, lineStroke, fontShrinkRatio);
    }

    /**
     * Creates a style range with a specified font style, font color, additional style, line color, line stroke and a
     * range.
     *
     * @param start           the start index of the range in a string
     * @param length          the length of the range.
     * @param fontStyle       Valid values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     * @param fontColor       the color of the text.
     * @param backgroundColor the background color of the text.
     * @param additionalStyle Valid additional styles are defined as constants in {@link StyleRange}. The names begin
     *                        with STYLE_. You can also use bitwise OR "|" to concat any two or more styles as long as
     *                        it makes sense.
     * @param lineColor       the color of the line.
     * @param lineStroke      the stroke of the line.
     * @param fontShrinkRatio the ratio that regular font size divides by subscript or superscript font size.
     */
    public StyleRange(int start, int length, int fontStyle, Color fontColor, Color backgroundColor, int additionalStyle, Color lineColor, Stroke lineStroke, float fontShrinkRatio) {
        if (length == 0) {
            throw new IllegalArgumentException("The length of StyleRange cannot be 0.");
        }

        _start = start;
        _length = length;
        _fontColor = fontColor;
        _fontStyle = fontStyle;
        _backgroundColor = backgroundColor;
        _lineColor = lineColor;
        _lineStroke = lineStroke;
        _additionalStyle = additionalStyle;
        _fontShrinkRatio = fontShrinkRatio;
    }

    /**
     * Gets the start index of the range.
     *
     * @return the start index of the range.
     */
    public int getStart() {
        return _start;
    }

    /**
     * Gets the length of the range.
     *
     * @return the length of the range.
     */
    public int getLength() {
        return _length;
    }

    /**
     * Gets the font style. Possible values are Font.PLAIN, Font.ITALIC, Font.BOLD or Font.BOLD | Font.ITALIC.
     *
     * @return the font style.
     */
    public int getFontStyle() {
        return _fontStyle;
    }

    /**
     * Gets the font color.
     *
     * @return the font color.
     */
    public Color getFontColor() {
        return _fontColor;
    }

    /**
     * Gets the background color.
     *
     * @return the background color.
     */
    public Color getBackgroundColor() {
        return _backgroundColor;
    }

    /**
     * Gets the additional style. Possible additional styles are defined as constants in {@link StyleRange}. The names
     * begin with STYLE_. The value could also be two or more styles concatenated by | as long as it makes sense. It
     * could be more convenient to use methods {@link #isStrikethrough()}, {@link #isDoublestrikethrough()}, {@link
     * #isDotted()}, {@link #isWaved()}, {@link #isUnderlined()}, {@link #isSubscript()}, {@link #isSuperscript()} to
     * see what's the additional style.
     *
     * @return the additional style.
     */
    public int getAdditionalStyle() {
        return _additionalStyle;
    }

    /**
     * Gets the line color.
     *
     * @return the line color.
     */
    public Color getLineColor() {
        return _lineColor;
    }

    /**
     * Gets the line stroke.
     *
     * @return the line stroke.
     */
    public Stroke getLineStroke() {
        return _lineStroke;
    }

    /**
     * Checks if the text has strike through style.
     *
     * @return true if the text has strike through style.
     */
    public boolean isStrikethrough() {
        return (_additionalStyle & STYLE_STRIKE_THROUGH) != 0;
    }

    /**
     * Checks if the text has double strike through style.
     *
     * @return true if the text has double strike through style.
     */
    public boolean isDoublestrikethrough() {
        return (_additionalStyle & STYLE_DOUBLE_STRIKE_THROUGH) != 0;
    }

    /**
     * Checks if the line has waved style.
     *
     * @return true if the line has waved style.
     */
    public boolean isWaved() {
        return (_additionalStyle & STYLE_WAVED) != 0;
    }

    /**
     * Checks if the text has underlined style.
     *
     * @return true if the text has underlined style.
     */
    public boolean isUnderlined() {
        return (_additionalStyle & STYLE_UNDERLINED) != 0;
    }

    /**
     * Checks if the line has dotted style.
     *
     * @return true if the line has dotted style.
     */
    public boolean isDotted() {
        return (_additionalStyle & STYLE_DOTTED) != 0;
    }

    /**
     * Checks if the text is superscript.
     *
     * @return true if the text is superscript.
     */
    public boolean isSuperscript() {
        return (_additionalStyle & STYLE_SUPERSCRIPT) != 0;
    }

    /**
     * Checks if the text is subscript.
     *
     * @return true if the text is subscript.
     */
    public boolean isSubscript() {
        return (_additionalStyle & STYLE_SUBSCRIPT) != 0;
    }

    /**
     * Gets the font shrink ratio for superscript and subscript.
     *
     * @return the shrink ratio.
     */
    public float getFontShrinkRatio() {
        return _fontShrinkRatio;
    }
}
