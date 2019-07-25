/*
 * @(#)BasicStyledLabelUI.java 6/8/2012
 *
 * Copyright 2002 - 2012 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.FontUtils;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.StyleRange;
import com.jidesoft.swing.StyledLabel;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.text.View;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class BasicStyledLabelUI extends BasicLabelUI implements SwingConstants {
    public static Comparator<StyleRange> _comparator;

    protected static BasicStyledLabelUI styledLabelUI = new BasicStyledLabelUI();

    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return styledLabelUI;
    }

    class StyledText {
        StyleRange styleRange;
        String text;

        public StyledText(String text) {
            this.text = text;
        }

        public StyledText(String text, StyleRange styleRange) {
            this.text = text;
            this.styleRange = styleRange;
        }
    }

    private final List<StyledText> _styledTexts = new ArrayList<StyledText>();
    private int _preferredRowCount = 1;

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);
        if (StyledLabel.PROPERTY_STYLE_RANGE.equals(e.getPropertyName())) {
            synchronized (_styledTexts) {
                _styledTexts.clear();
            }
            if (e.getSource() instanceof StyledLabel) {
                ((StyledLabel) e.getSource()).revalidate();
                ((StyledLabel) e.getSource()).repaint();
            }
        }
        else if (StyledLabel.PROPERTY_IGNORE_COLOR_SETTINGS.equals(e.getPropertyName())) {
            if (e.getSource() instanceof StyledLabel) {
                ((StyledLabel) e.getSource()).repaint();
            }
        }
    }

    @Override
    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        View v = (l != null) ? (View) l.getClientProperty("html") : null;
        if (v != null) {
            super.paintEnabledText(l, g, s, textX, textY);
        }
        else {
            paintStyledText((StyledLabel) l, g, textX, textY);
        }
    }

    @Override
    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        View v = (l != null) ? (View) l.getClientProperty("html") : null;
        if (v != null) {
            super.paintDisabledText(l, g, s, textX, textY);
        }
        else {
            paintStyledText((StyledLabel) l, g, textX, textY);
        }
    }

    protected void buildStyledText(StyledLabel label) {
        synchronized (_styledTexts) {
            _styledTexts.clear();
            StyleRange[] styleRanges = label.getStyleRanges();
            if (_comparator == null) {
                _comparator = new Comparator<StyleRange>() {
                    public int compare(StyleRange r1, StyleRange r2) {
                        if (r1.getStart() < r2.getStart()) {
                            return -1;
                        }
                        else if (r1.getStart() > r2.getStart()) {
                            return 1;
                        }
                        else {
                            return 0;
                        }
                    }
                };
            }
            Arrays.sort(styleRanges, _comparator);

            String s = label.getText();
            if (s != null && s.length() > 0) { // do not do anything if the text is empty
                int index = 0;
                for (StyleRange styleRange : styleRanges) {
                    if (index >= s.length()) {
                        break;
                    }
                    if (styleRange.getStart() > index) { // fill in the gap
                        String text = s.substring(index, Math.min(styleRange.getStart(), s.length()));
                        StyleRange newRange = new StyleRange(index, styleRange.getStart() - index, -1);
                        addStyledTexts(text, newRange, label);
                        index = styleRange.getStart();
                    }

                    if (styleRange.getStart() == index) { // exactly on
                        if (styleRange.getLength() == -1) {
                            String text = s.substring(index);
                            addStyledTexts(text, styleRange, label);
                            index = s.length();
                        }
                        else {
                            String text = s.substring(index, Math.min(index + styleRange.getLength(), s.length()));
                            addStyledTexts(text, styleRange, label);
                            index += styleRange.getLength();
                        }
                    }
                    else if (styleRange.getStart() < index) { // overlap
                        // ignore
                    }
                }
                if (index < s.length()) {
                    String text = s.substring(index, s.length());
                    StyleRange range = new StyleRange(index, s.length() - index, -1);
                    addStyledTexts(text, range, label);
                }
            }
        }
    }

    private void addStyledTexts(String text, StyleRange range, StyledLabel label) {
        range = new StyleRange(range); // keep the passed-in parameter no change
        int index1 = text.indexOf('\r');
        int index2 = text.indexOf('\n');
        while (index1 >= 0 || index2 >= 0) {
            int index = index1 >= 0 ? index1 : -1;
            if (index2 >= 0 && (index2 < index1 || index < 0)) {
                index = index2;
            }
            String subString = text.substring(0, index);
            StyleRange newRange = new StyleRange(range);
            newRange.setStart(range.getStart());
            newRange.setLength(index);
            _styledTexts.add(new StyledText(subString, newRange));
            int length = 1;
            if (text.charAt(index) == '\r' && index + 1 < text.length() && text.charAt(index + 1) == '\n') {
                length++;
            }
            newRange = new StyleRange(range);
            newRange.setStart(range.getStart() + index);
            newRange.setLength(length);
            _styledTexts.add(new StyledText(text.substring(index, index + length), newRange));

            text = text.substring(index + length);
            range.setStart(range.getStart() + index + length);
            range.setLength(range.getLength() - index - length);

            index1 = text.indexOf('\r');
            index2 = text.indexOf('\n');
        }
        if (text.length() > 0) {
            _styledTexts.add(new StyledText(text, range));
        }
    }

    private boolean _gettingPreferredSize;
    @Override
    public Dimension getPreferredSize(JComponent c) {
        _gettingPreferredSize = true;
        Dimension preferredSize;
        try {
            preferredSize = super.getPreferredSize(c);
        }
        finally {
            _gettingPreferredSize = false;
        }
        return preferredSize;
    }

    @Override
    protected String layoutCL(JLabel label, FontMetrics fontMetrics, String text, Icon icon, Rectangle viewR, Rectangle iconR, Rectangle textR) {
        Dimension size = null;
        if (label instanceof StyledLabel) {
            int oldPreferredWidth = ((StyledLabel) label).getPreferredWidth();
            int oldRows = ((StyledLabel) label).getRows();
            try {
                if (((StyledLabel) label).isLineWrap() && label.getWidth() > 0) {
                    ((StyledLabel) label).setPreferredWidth(label.getWidth());
                }
                size = getPreferredSize((StyledLabel) label);
                if (oldPreferredWidth > 0 && oldPreferredWidth < label.getWidth()) {
                    ((StyledLabel) label).setPreferredWidth(oldPreferredWidth);
                    size = getPreferredSize((StyledLabel) label);
                }
                else if (((StyledLabel) label).isLineWrap() && ((StyledLabel) label).getMinRows() > 0) {
                    ((StyledLabel) label).setPreferredWidth(0);
                    ((StyledLabel) label).setRows(0);
                    Dimension minSize = getPreferredSize((StyledLabel) label);
                    if (minSize.height > size.height) {
                        size = minSize;
                    }
                }
            }
            finally {
                ((StyledLabel) label).setPreferredWidth(oldPreferredWidth);
                ((StyledLabel) label).setRows(oldRows);
            }
        }
        else {
            size = label.getPreferredSize();
        }
        textR.width = size.width;
        textR.height = size.height;
        if (label.getIcon() != null) {
            textR.width -= label.getIcon().getIconWidth() + label.getIconTextGap();
        }

        return layoutCompoundLabel(
                label,
                fontMetrics,
                text,
                icon,
                label.getVerticalAlignment(),
                label.getHorizontalAlignment(),
                label.getVerticalTextPosition(),
                label.getHorizontalTextPosition(),
                viewR,
                iconR,
                textR,
                label.getIconTextGap());
    }

    /**
     * Gets the preferred size of the text portion of the StyledLabel including the icon.
     *
     * @param label the StyledLabel
     * @return the preferred size.
     */
    protected Dimension getPreferredSize(StyledLabel label) {
        buildStyledText(label);

        Font font = getFont(label);
        FontMetrics fm = label.getFontMetrics(font);
        FontMetrics fm2;
        int defaultFontSize = font.getSize();
        boolean lineWrap = label.isLineWrap() || (label.getText() != null && (label.getText().contains("\r") || label.getText().contains("\n")));
        synchronized (_styledTexts) {
            StyledText[] texts = _styledTexts.toArray(new StyledText[_styledTexts.size()]);

            // get maximum row height first by comparing all fonts of styled texts
            int maxRowHeight = fm.getHeight();
            for (StyledText styledText : texts) {
                StyleRange style = styledText.styleRange;
                int size = (style != null && (style.isSuperscript() || style.isSubscript())) ? Math.round((float) defaultFontSize / style.getFontShrinkRatio()) : defaultFontSize;

                font = getFont(label);
                int styleHeight = fm.getHeight();
                if (style != null && ((style.getFontStyle() != -1 && font.getStyle() != style.getFontStyle()) || font.getSize() != size)) {
                    font = FontUtils.getCachedDerivedFont(font, style.getFontStyle() == -1 ? font.getStyle() : style.getFontStyle(), size);
                    fm2 = label.getFontMetrics(font);
                    styleHeight = fm2.getHeight();
                }
                styleHeight++;
/*
                if (style != null) {
                    if (style.isWaved()) {
                        styleHeight += 4;
                    }
                    else if (style.isDotted()) {
                        styleHeight += 3;
                    }
                    else if (style.isUnderlined()) {
                        styleHeight += 2;
                    }
                }
*/
                maxRowHeight = Math.max(maxRowHeight, styleHeight);
            }

            int naturalRowCount = 1;
            int nextRowStartIndex = 0;
            int width = 0;
            int maxWidth = 0;
            List<Integer> lineWidths = new ArrayList<Integer>();
            // get one line width
            for (StyledText styledText : _styledTexts) {
                StyleRange style = styledText.styleRange;
                int size = (style != null &&
                        (style.isSuperscript() || style.isSubscript())) ? Math.round((float) defaultFontSize / style.getFontShrinkRatio()) : defaultFontSize;
                font = getFont(label);
                String s = styledText.text.substring(nextRowStartIndex);
                if (s.startsWith("\r") || s.startsWith("\n")) {
                    lineWidths.add(width);
                    maxWidth = Math.max(width, maxWidth);
                    width = 0;
                    naturalRowCount++;
                    if (label.getMaxRows() > 0 && naturalRowCount > label.getMaxRows()) {
                        break;
                    }
                    continue;
                }
                if (style != null && ((style.getFontStyle() != -1 && font.getStyle() != style.getFontStyle()) || font.getSize() != size)) {
                    font = FontUtils.getCachedDerivedFont(font, style.getFontStyle() == -1 ? font.getStyle() : style.getFontStyle(), size);
                    fm2 = label.getFontMetrics(font);
                    width += fm2.stringWidth(s);
                }
                else {
//                    fm2 = fm;
                    width += fm.stringWidth(s);
                }
            }
            lineWidths.add(width);
            maxWidth = Math.max(width, maxWidth);
            int maxLineWidth = maxWidth;
            _preferredRowCount = naturalRowCount;

            // if getPreferredWidth() is not set but getRows() is set, get maximum width and row count based on the required rows.
            if (lineWrap && label.getPreferredWidth() <= 0 && label.getRows() > 0) {
                maxWidth = getMaximumWidth(label, maxWidth, naturalRowCount, label.getRows());
            }

            // if calculated maximum width is larger than label's maximum size, wrap again to get the updated row count and use the label's maximum width as the maximum width.
            int preferredWidth = label.getPreferredWidth();
            if (preferredWidth > 0 && label.getInsets() != null) {
                preferredWidth -= label.getInsets().left + label.getInsets().right;
            }
            if (label.getIcon() != null && label.getHorizontalTextPosition() != SwingConstants.CENTER) {
                preferredWidth -= label.getIcon().getIconWidth() + label.getIconTextGap();
            }
            if (lineWrap && preferredWidth > 0 && maxWidth > preferredWidth) {
                maxWidth = getLayoutWidth(label, preferredWidth);
            }

            // label.getPreferredWidth() <= 0 && label.getMaxRows() > 0 && rowCount > label.getMaxRows(), recalculate the maximum width according to the maximum rows
            if (lineWrap && label.getMaxRows() > 0 && _preferredRowCount > label.getMaxRows()) {
                if (label.getPreferredWidth() <= 0) {
                    maxWidth = getMaximumWidth(label, maxWidth, naturalRowCount, label.getMaxRows());
                }
                else {
                    _preferredRowCount = label.getMaxRows();
                }
            }

            // label.getPreferredWidth() <= 0 && label.getMinRows() > 0 && rowCount < label.getMinRows(), recalculate the maximum width according to the minimum rows
            if (lineWrap && label.getPreferredWidth() <= 0 && label.getMinRows() > 0 && _preferredRowCount < label.getMinRows()) {
                maxWidth = getMaximumWidth(label, maxWidth, naturalRowCount, label.getMinRows());
            }
            if (_gettingPreferredSize && label.getRows() > 0 && _preferredRowCount > label.getRows() && (label.getPreferredWidth() <= 0 || label.getPreferredWidth() >= maxLineWidth || naturalRowCount > label.getRows())) {
                _preferredRowCount = label.getRows();
                maxLineWidth = 0;
                for (int i = 0; i < lineWidths.size() && i < _preferredRowCount; i++) {
                    maxLineWidth = Math.max(maxLineWidth, lineWidths.get(i));
                }
            }
            Dimension dimension = new Dimension(Math.min(maxWidth, maxLineWidth), (maxRowHeight + Math.max(0, label.getRowGap())) * _preferredRowCount);
            if (label.getIcon() != null) {
                dimension = new Dimension(dimension.width + label.getIconTextGap() + label.getIcon().getIconWidth(), dimension.height);
            }
            return dimension;
        }
    }

    private int getLayoutWidth(StyledLabel label, int maxWidth) {
        int nextRowStartIndex;
        Font font = getFont(label);
        int defaultFontSize = font.getSize();
        FontMetrics fm = label.getFontMetrics(font);
        FontMetrics fm2;
        nextRowStartIndex = 0;
        int x = 0;
        _preferredRowCount = 1;
        for (int i = 0; i < _styledTexts.size(); i++) {
            StyledText styledText = _styledTexts.get(i);
            StyleRange style = styledText.styleRange;
            if (styledText.text.contains("\r") || styledText.text.contains("\n")) {
                x = 0;
                _preferredRowCount++;
                continue;
            }

            int size = (style != null &&
                    (style.isSuperscript() || style.isSubscript())) ? Math.round((float) defaultFontSize / style.getFontShrinkRatio()) : defaultFontSize;

            font = getFont(label); // cannot omit this one
            if (style != null && ((style.getFontStyle() != -1 && font.getStyle() != style.getFontStyle()) || font.getSize() != size)) {
                font = FontUtils.getCachedDerivedFont(font, style.getFontStyle() == -1 ? font.getStyle() : style.getFontStyle(), size);
                fm2 = label.getFontMetrics(font);
            }
            else {
                fm2 = fm;
            }

            String s = styledText.text.substring(nextRowStartIndex);

            int strWidth = fm2.stringWidth(s);

            boolean wrapped = false;
            int widthLeft = maxWidth - x;
            if (widthLeft < strWidth) {
                wrapped = true;
                int availLength = s.length() * widthLeft / strWidth + 1;
                int nextWordStartIndex;
                int nextRowStartIndexInSubString = 0;
                boolean needBreak = false;
                boolean needContinue = false;
                int loopCount = 0;
                do {
                    String subString = s.substring(0, Math.min(availLength, s.length()));
                    int firstRowWordEndIndex = findFirstRowWordEndIndex(subString);
                    nextWordStartIndex = firstRowWordEndIndex < 0 ? 0 : findNextWordStartIndex(s, firstRowWordEndIndex);
                    if (firstRowWordEndIndex < 0) {
                        if (x != 0) {
                            x = 0;
                            i--;
                            _preferredRowCount++;
                            needContinue = true;
                            break;
                        }
                        else {
                            firstRowWordEndIndex = 0;
                            nextWordStartIndex = Math.min(s.length(), availLength);
                        }
                    }

                    if (label.getMaxRows() > 0 && _preferredRowCount >= label.getMaxRows()) {
                        needBreak = true;
                    }

                    nextRowStartIndexInSubString = firstRowWordEndIndex + 1;
                    String subStringThisRow = s.substring(0, Math.min(nextRowStartIndexInSubString, s.length()));
                    strWidth = fm2.stringWidth(subStringThisRow);
                    if (strWidth > widthLeft) {
                        availLength = subString.length() * widthLeft / strWidth;
                    }
                    loopCount++;
                    if (loopCount > 50) {
                        System.err.println("Painting Styled Label Error: " + styledText);
                        break;
                    }
                } while (strWidth > widthLeft && availLength > 0);
                if (needBreak) {
                    break;
                }
                if (needContinue) {
                    continue;
                }
                while (nextRowStartIndexInSubString < nextWordStartIndex) {
                    strWidth += fm2.charWidth(s.charAt(nextRowStartIndexInSubString));
                    if (strWidth >= widthLeft) {
                        break;
                    }
                    nextRowStartIndexInSubString++;
                }
                String subStringThisRow = s.substring(0, Math.min(nextRowStartIndexInSubString, s.length()));
                strWidth = fm2.stringWidth(subStringThisRow);
                while (nextRowStartIndexInSubString < nextWordStartIndex) {
                    strWidth += fm2.charWidth(s.charAt(nextRowStartIndexInSubString));
                    if (strWidth >= widthLeft) {
                        break;
                    }
                    nextRowStartIndexInSubString++;
                }
                s = s.substring(0, Math.min(nextRowStartIndexInSubString, s.length()));
                strWidth = fm2.stringWidth(s);
                nextRowStartIndex += nextRowStartIndexInSubString;
            }
            else {
                nextRowStartIndex = 0;
            }

            if (wrapped) {
                _preferredRowCount++;
                x = 0;
                i--;
            }
            else {
                x += strWidth;
            }
        }
        return maxWidth;
    }

    private int getMaximumWidth(StyledLabel label, int maxWidth, int naturalRowCount, int limitedRows) {
        int textWidth = label.getWidth() - label.getInsets().left - label.getInsets().right;
        if (label.getIcon() != null) {
            textWidth -= label.getIcon().getIconWidth() + label.getIconTextGap();
        }
        if (naturalRowCount > 1) {
            int proposedMaxWidthMin = 1;
            int proposedMaxWidthMax = maxWidth;
            _preferredRowCount = naturalRowCount;
            while (proposedMaxWidthMin < proposedMaxWidthMax) {
                int middle = (proposedMaxWidthMax + proposedMaxWidthMin) / 2;
                maxWidth = getLayoutWidth(label, middle);
                if (_preferredRowCount > limitedRows) {
                    proposedMaxWidthMin = middle + 1;
                    _preferredRowCount = naturalRowCount;
                }
                else {
                    proposedMaxWidthMax = middle - 1;
                }
            }
            return maxWidth + maxWidth / 20;
        }

        int estimatedWidth = maxWidth / limitedRows + 1;
        int x = 0;
        int nextRowStartIndex = 0;
        Font font = getFont(label);
        FontMetrics fm = label.getFontMetrics(font);
        int defaultFontSize = font.getSize();
        FontMetrics fm2;
        for (int i = 0; i < _styledTexts.size(); i++) {
            StyledText styledText = _styledTexts.get(i);
            StyleRange style = styledText.styleRange;
            int size = (style != null && (style.isSuperscript() || style.isSubscript())) ? Math.round((float) defaultFontSize / style.getFontShrinkRatio()) : defaultFontSize;
            font = getFont(label);
            if (style != null && ((style.getFontStyle() != -1 && font.getStyle() != style.getFontStyle()) || font.getSize() != size)) {
                font = FontUtils.getCachedDerivedFont(font, style.getFontStyle() == -1 ? font.getStyle() : style.getFontStyle(), size);
                fm2 = label.getFontMetrics(font);
            }
            else {
                fm2 = fm;
            }

            String s = styledText.text.substring(nextRowStartIndex);
            int strWidth = fm2.stringWidth(s);
            int widthLeft = estimatedWidth - x;
            if (widthLeft < strWidth) {
                int availLength = s.length() * widthLeft / strWidth + 1;
                String subString = s.substring(0, Math.min(availLength, s.length()));
                int firstRowWordEndIndex = findFirstRowWordEndIndex(subString);
                int nextWordStartIndex = findNextWordStartIndex(s, firstRowWordEndIndex);
                if (firstRowWordEndIndex < 0) {
                    if (nextWordStartIndex < s.length()) {
                        firstRowWordEndIndex = findFirstRowWordEndIndex(s.substring(0, nextWordStartIndex));
                    }
                    else {
                        firstRowWordEndIndex = nextWordStartIndex;
                    }
                }
                int nextRowStartIndexInSubString = firstRowWordEndIndex + 1;
                String subStringThisRow = s.substring(0, Math.min(nextRowStartIndexInSubString, s.length()));
                strWidth = fm2.stringWidth(subStringThisRow);
                while (nextRowStartIndexInSubString < nextWordStartIndex) {
                    strWidth += fm2.charWidth(s.charAt(nextRowStartIndexInSubString));
                    nextRowStartIndexInSubString++;
                    if (strWidth >= widthLeft) {
                        break;
                    }
                }
                s = s.substring(0, Math.min(nextRowStartIndexInSubString, s.length()));
                strWidth = fm2.stringWidth(s);
                nextRowStartIndex += nextRowStartIndexInSubString;
                if (x + strWidth >= maxWidth) {
                    x = Math.max(x, strWidth);
                    break;
                }
                if (x + strWidth >= estimatedWidth) {
                    x += strWidth;
                    break;
                }
                i--;
            }
            x += strWidth;
        }
        int paintWidth = x;
        if (label.getInsets() != null) {
            paintWidth += label.getInsets().left + label.getInsets().right;
        }
        int paintRows = internalPaintStyledText(label, null, 0, 0, paintWidth);
        if (paintRows != limitedRows) {
            maxWidth = Math.min(maxWidth, textWidth);
            while (paintRows > limitedRows && paintWidth < maxWidth) {
                paintWidth += 2;
                paintRows = internalPaintStyledText(label, null, 0, 0, paintWidth);
            }
            while (paintRows < limitedRows && paintWidth > 0) {
                paintWidth -= 2;
                paintRows = internalPaintStyledText(label, null, 0, 0, paintWidth);
            }
            x = paintWidth;
            if (label.getInsets() != null) {
                x -= label.getInsets().left + label.getInsets().right;
            }
        }
        _preferredRowCount = limitedRows;
        return x;
    }

    /**
     * Gets the font from the label.
     *
     * @param label the label.
     * @return the font. If label's getFont is null, we will use Label.font instead.
     */
    protected Font getFont(StyledLabel label) {
        Font font = label.getFont();
        if (font == null) {
            font = UIDefaultsLookup.getFont("Label.font");
        }
        return font;
    }

    protected void paintStyledText(StyledLabel label, Graphics g, int textX, int textY) {
        label.setTruncated(false);
        int paintWidth = label.getWidth();
        if (label.isLineWrap()) {
            int oldPreferredWidth = label.getPreferredWidth();
            int oldRows = label.getRows();
            try {
                label.setRows(0);
                paintWidth = getPreferredSize(label).width;
                label.setPreferredWidth(oldPreferredWidth > 0 ? Math.min(label.getWidth(), oldPreferredWidth) : label.getWidth());
                Dimension sizeOnWidth = getPreferredSize(label);
                if (sizeOnWidth.width < paintWidth) {
                    paintWidth = sizeOnWidth.width;
                }
            }
            finally {
                label.setPreferredWidth(oldPreferredWidth);
                label.setRows(oldRows);
            }
        }
        Color oldColor = g.getColor();
        int textWidth = label.getWidth();
        if (label.getInsets() != null) {
            textWidth -= label.getInsets().left + label.getInsets().right;
        }
        if (label.getIcon() != null && label.getHorizontalTextPosition() != SwingConstants.CENTER) {
            textWidth -= label.getIcon().getIconWidth() + label.getIconTextGap();
        }
        paintWidth = Math.min(paintWidth, textWidth);
        internalPaintStyledText(label, g, textX, textY, paintWidth);
        g.setColor(oldColor);
    }

    private int internalPaintStyledText(StyledLabel label, Graphics g, int textX, int textY, int paintWidth) {
        int labelHeight = label.getHeight();
        if (labelHeight <= 0) {
            labelHeight = Integer.MAX_VALUE;
        }
        Insets insets = label.getInsets();
        if (insets != null) {
            labelHeight -= insets.top + insets.bottom;
        }
        int leftMostX = 0;
        if (insets != null) {
            leftMostX += insets.left;
        }
        if (label.getIcon() != null) {
            int horizontalTextPosition = label.getHorizontalTextPosition();
            if ((horizontalTextPosition == SwingConstants.TRAILING && label.getComponentOrientation().isLeftToRight()) ||(horizontalTextPosition == SwingConstants.LEADING && !label.getComponentOrientation().isLeftToRight())) {
                horizontalTextPosition = SwingConstants.RIGHT;
            }
            if (horizontalTextPosition == SwingConstants.RIGHT) {
                leftMostX += label.getIcon().getIconWidth() + label.getIconTextGap();
            }
        }

        int startX = textX < leftMostX ? leftMostX : textX;
        int y;
        int endX = paintWidth + startX;
        int x = startX;
        int mnemonicIndex = label.getDisplayedMnemonicIndex();
        if (LookAndFeelFactory.isWindowsLookAndFeel(UIManager.getLookAndFeel()) &&
                LookAndFeelFactory.isMnemonicHidden()) {
            mnemonicIndex = -1;
        }

        int charDisplayed = 0;
        boolean displayMnemonic;
        int mneIndex = 0;
        Font font = getFont(label);
        FontMetrics fm = label.getFontMetrics(font);
        FontMetrics fm2;
        FontMetrics nextFm2 = null;
        int defaultFontSize = font.getSize();

        synchronized (_styledTexts) {
            String nextS = "";
            int maxRowHeight = fm.getHeight();
            int minStartY = fm.getAscent();
            int horizontalAlignment = label.getHorizontalAlignment();
            switch (horizontalAlignment) {
                case LEADING:
                    horizontalAlignment = label.getComponentOrientation().isLeftToRight() ? LEFT : RIGHT;
                    break;
                case TRAILING:
                    horizontalAlignment = label.getComponentOrientation().isLeftToRight() ? RIGHT : LEFT;
                    break;
            }
            for (StyledText styledText : _styledTexts) {
                StyleRange style = styledText.styleRange;
                int size = (style != null && (style.isSuperscript() || style.isSubscript())) ? Math.round((float) defaultFontSize / style.getFontShrinkRatio()) : defaultFontSize;

                font = getFont(label);
                if (style != null && ((style.getFontStyle() != -1 && font.getStyle() != style.getFontStyle()) || font.getSize() != size)) {
                    font = FontUtils.getCachedDerivedFont(font, style.getFontStyle() == -1 ? font.getStyle() : style.getFontStyle(), size);
                    fm2 = label.getFontMetrics(font);
                    maxRowHeight = Math.max(maxRowHeight, fm2.getHeight());
                    minStartY = Math.max(minStartY, fm2.getAscent());
                }
            }
            boolean lineWrap = label.isLineWrap();
            if (!lineWrap) {
                for (StyledText styledText : _styledTexts) {
                    if (styledText.text.endsWith("\n")) {
                        lineWrap = true;
                        break;
                    }
                }
            }
            if (lineWrap && textY < minStartY) {
                textY = minStartY;
            }

            int nextRowStartIndex = 0;
            int rowCount = 0;
            int rowStartOffset = 0;
            for (int i = 0; i < _styledTexts.size(); i++) {
                StyledText styledText = _styledTexts.get(i);
                StyleRange style = styledText.styleRange;

                if (mnemonicIndex >= 0 && styledText.text.length() - nextRowStartIndex > mnemonicIndex - charDisplayed) {
                    displayMnemonic = true;
                    mneIndex = mnemonicIndex - charDisplayed;
                }
                else {
                    displayMnemonic = false;
                }
                charDisplayed += styledText.text.length() - nextRowStartIndex;
                if (styledText.text.contains("\r") || styledText.text.contains("\n")) {
                    boolean lastRow = (label.getMaxRows() > 0 && rowCount >= label.getMaxRows() - 1) || textY + maxRowHeight + Math.max(0, label.getRowGap()) > labelHeight;
                    if (horizontalAlignment != LEFT && g != null) {
                        if (lastRow && i != _styledTexts.size() - 1) {
                            x += fm.stringWidth("...");
                        }
                        paintRow(label, g, startX, x, endX, textY, rowStartOffset, style.getStart() + styledText.text.length(), lastRow);
                    }
                    rowStartOffset = style.getStart();
                    nextRowStartIndex = 0;
                    nextFm2 = null;
                    if (!lastRow) {
                        rowStartOffset += style.getLength();
                        rowCount++;
                        x = startX;
                        textY += maxRowHeight + Math.max(0, label.getRowGap());
                        continue; // continue to paint "..." if lastRow is true
                    }
                    else if (horizontalAlignment != LEFT && g != null) {
                        break;
                    }
                }

                y = textY;

                if (nextFm2 == null) {
                    int size = (style != null &&
                            (style.isSuperscript() || style.isSubscript())) ? Math.round((float) defaultFontSize / style.getFontShrinkRatio()) : defaultFontSize;

                    font = getFont(label);
                    if (style != null && ((style.getFontStyle() != -1 && font.getStyle() != style.getFontStyle()) || font.getSize() != size)) {
                        font = FontUtils.getCachedDerivedFont(font, style.getFontStyle() == -1 ? font.getStyle() : style.getFontStyle(), size);
                        fm2 = label.getFontMetrics(font);
                    }
                    else {
                        fm2 = fm;
                    }
                }
                else {
                    fm2 = nextFm2;
                }

                if (g != null) {
                    g.setFont(font);
                }

                boolean stop = false;
                String s = styledText.text.substring(Math.min(nextRowStartIndex, styledText.text.length()));
                if (s.contains("\r") || s.contains("\n")) {
                    s = "...";
                    stop = true;
                }

                int strWidth = fm2.stringWidth(s);

                boolean wrapped = false;
                int widthLeft = endX - x;
                if (widthLeft < strWidth && widthLeft >= 0) {
                    if (label.isLineWrap() && ((label.getMaxRows() > 0 && rowCount < label.getMaxRows() - 1) || label.getMaxRows() <= 0) && y + maxRowHeight + Math.max(0, label.getRowGap()) <= labelHeight) {
                        wrapped = true;
                        int availLength = s.length() * widthLeft / strWidth + 1;
                        int nextWordStartIndex;
                        int nextRowStartIndexInSubString = 0;
                        boolean needBreak = false;
                        boolean needContinue = false;
                        int loopCount = 0;
                        do {
                            String subString = s.substring(0, Math.max(0, Math.min(availLength, s.length())));
                            int firstRowWordEndIndex = findFirstRowWordEndIndex(subString);
                            nextWordStartIndex = firstRowWordEndIndex < 0 ? 0 : findNextWordStartIndex(s, firstRowWordEndIndex);
                            if (firstRowWordEndIndex < 0) {
                                if (x != startX) {
                                    boolean lastRow = label.getMaxRows() > 0 && rowCount >= label.getMaxRows() - 1;
                                    if (horizontalAlignment != LEFT && g != null) {
                                        paintRow(label, g, startX, x, endX, textY, rowStartOffset, style.getStart() + Math.min(nextRowStartIndex, styledText.text.length()), lastRow);
                                    }
                                    textY += maxRowHeight + Math.max(0, label.getRowGap());
                                    x = startX;
                                    i--;
                                    rowCount++;
                                    rowStartOffset = style.getStart() + Math.min(nextRowStartIndex, styledText.text.length());
                                    if (lastRow) {
                                        needBreak = true;
                                    }
                                    needContinue = true;
                                    break;
                                }
                                else {
                                    firstRowWordEndIndex = 0;
                                    nextWordStartIndex = Math.min(s.length(), availLength);
                                }
                            }
                            nextRowStartIndexInSubString = firstRowWordEndIndex + 1;
                            String subStringThisRow = s.substring(0, Math.min(nextRowStartIndexInSubString, s.length()));
                            strWidth = fm2.stringWidth(subStringThisRow);
                            if (strWidth > widthLeft) {
                                availLength = subString.length() * widthLeft / strWidth;
                            }
                            loopCount++;
                            if (loopCount > 15) {
                                System.err.println("Painting Styled Label Error: " + styledText);
                                break;
                            }
                        } while (strWidth > widthLeft && availLength > 0);
                        if (needBreak) {
                            break;
                        }
                        if (needContinue) {
                            continue;
                        }
                        while (nextRowStartIndexInSubString < nextWordStartIndex) {
                            strWidth += fm2.charWidth(s.charAt(nextRowStartIndexInSubString));
                            if (strWidth >= widthLeft) {
                                break;
                            }
                            nextRowStartIndexInSubString++;
                        }
                        s = s.substring(0, Math.min(nextRowStartIndexInSubString, s.length()));
                        strWidth = fm2.stringWidth(s);
                        charDisplayed -= styledText.text.length() - nextRowStartIndex;
                        if (displayMnemonic) {
                            if (mnemonicIndex >= 0 && s.length() > mnemonicIndex - charDisplayed) {
                                displayMnemonic = true;
                                mneIndex = mnemonicIndex - charDisplayed;
                            }
                            else {
                                displayMnemonic = false;
                            }
                        }
                        charDisplayed += s.length();
                        nextRowStartIndex += nextRowStartIndexInSubString;
                    }
                    else {
                        // use this method to clip string
                        s = SwingUtilities.layoutCompoundLabel(label, fm2, s, null, label.getVerticalAlignment(), label.getHorizontalAlignment(),
                                label.getVerticalTextPosition(), label.getHorizontalTextPosition(), new Rectangle(x, y, widthLeft, labelHeight), new Rectangle(), new Rectangle(), 0);
                        strWidth = fm2.stringWidth(s);
                    }
                    stop = !lineWrap || y + maxRowHeight + Math.max(0, label.getRowGap()) > labelHeight || (label.getMaxRows() > 0 && rowCount >= label.getMaxRows() - 1);
                }
                else if (lineWrap) {
                    nextRowStartIndex = 0;
                }
                else if (i < _styledTexts.size() - 1) {
                    StyledText nextStyledText = _styledTexts.get(i + 1);
                    String nextText = nextStyledText.text;
                    StyleRange nextStyle = nextStyledText.styleRange;
                    int size = (nextStyle != null &&
                            (nextStyle.isSuperscript() || nextStyle.isSubscript())) ? Math.round((float) defaultFontSize / nextStyle.getFontShrinkRatio()) : defaultFontSize;

                    font = getFont(label);
                    if (nextStyle != null && ((nextStyle.getFontStyle() != -1 && font.getStyle() != nextStyle.getFontStyle()) || font.getSize() != size)) {
                        font = FontUtils.getCachedDerivedFont(font, nextStyle.getFontStyle() == -1 ? font.getStyle() : nextStyle.getFontStyle(), size);
                        nextFm2 = label.getFontMetrics(font);
                    }
                    else {
                        nextFm2 = fm;
                    }
                    if (nextS.equals("...")) {
                        nextS = SwingUtilities.layoutCompoundLabel(label, nextFm2, nextText, null, label.getVerticalAlignment(), label.getHorizontalAlignment(),
                                label.getVerticalTextPosition(), label.getHorizontalTextPosition(), new Rectangle(x + strWidth, y, widthLeft - strWidth, labelHeight), new Rectangle(), new Rectangle(), 0);
                        if (nextFm2.stringWidth(nextS) > widthLeft - strWidth) {
                            s = SwingUtilities.layoutCompoundLabel(label, fm2, s, null, label.getVerticalAlignment(), label.getHorizontalAlignment(),
                                    label.getVerticalTextPosition(), label.getHorizontalTextPosition(), new Rectangle(x, y, strWidth - 1, labelHeight), new Rectangle(), new Rectangle(), 0);
                            strWidth = fm2.stringWidth(s);
                            stop = true;
                        }
                    }
                }

                // start of actual painting
                if (rowCount > 0 && x == startX && s.startsWith(" ")) {
                    s = s.substring(1);
                    strWidth = fm2.stringWidth(s);
                }
                if (horizontalAlignment == LEFT && g != null) {
                    if (style != null && style.isSuperscript()) {
                        y -= fm.getHeight() - fm2.getHeight();
                    }

                    if (style != null && style.getBackgroundColor() != null) {
                        g.setColor(style.getBackgroundColor());
                        g.fillRect(x, y - fm2.getHeight(), strWidth, fm2.getHeight() + 4);
                    }

                    Color textColor = (style != null && !label.isIgnoreColorSettings() && style.getFontColor() != null) ? style.getFontColor() : label.getForeground();
                    if (!label.isEnabled()) {
                        textColor = UIDefaultsLookup.getColor("Label.disabledForeground");
                    }
                    g.setColor(textColor);

                    if (displayMnemonic) {
                        JideSwingUtilities.drawStringUnderlineCharAt(label, g, s, mneIndex, x, y);
                    }
                    else {
                        JideSwingUtilities.drawString(label, g, s, x, y);
                    }

                    if (style != null) {
                        Stroke oldStroke = ((Graphics2D) g).getStroke();
                        if (style.getLineStroke() != null) {
                            ((Graphics2D) g).setStroke(style.getLineStroke());
                        }

                        if (!label.isIgnoreColorSettings() && style.getLineColor() != null) {
                            g.setColor(style.getLineColor());
                        }

                        if (style.isStrikethrough()) {
                            int lineY = y + (fm2.getDescent() - fm2.getAscent()) / 2;
                            g.drawLine(x, lineY, x + strWidth - 1, lineY);
                        }
                        if (style.isDoublestrikethrough()) {
                            int lineY = y + (fm2.getDescent() - fm2.getAscent()) / 2;
                            g.drawLine(x, lineY - 1, x + strWidth - 1, lineY - 1);
                            g.drawLine(x, lineY + 1, x + strWidth - 1, lineY + 1);
                        }
                        if (style.isUnderlined()) {
                            int lineY = y + 1;
                            g.drawLine(x, lineY, x + strWidth - 1, lineY);
                        }
                        if (style.isDotted()) {
                            int dotY = y + 1;
                            for (int dotX = x; dotX < x + strWidth; dotX += 4) {
                                g.drawRect(dotX, dotY, 1, 1);
                            }
                        }
                        if (style.isWaved()) {
                            int waveY = y + 1;
                            for (int waveX = x; waveX < x + strWidth; waveX += 4) {
                                if (waveX + 2 <= x + strWidth - 1)
                                    g.drawLine(waveX, waveY + 2, waveX + 2, waveY);
                                if (waveX + 4 <= x + strWidth - 1)
                                    g.drawLine(waveX + 3, waveY + 1, waveX + 4, waveY + 2);
                            }
                        }
                        if (style.getLineStroke() != null) {
                            ((Graphics2D) g).setStroke(oldStroke);
                        }
                    }
                }
                // end of actual painting

                if (stop) {
                    if (horizontalAlignment != LEFT && g != null) {
                        x += strWidth;
                        paintRow(label, g, startX, x, endX, textY, rowStartOffset, label.getText().length(), true);
                    }
                    label.setTruncated(true);
                    break;
                }

                if (wrapped) {
                    boolean lastRow = (label.getMaxRows() > 0 && rowCount >= label.getMaxRows() - 1) || textY + maxRowHeight + Math.max(0, label.getRowGap()) > labelHeight;
                    if (horizontalAlignment != LEFT && g != null) {
                        x += strWidth;
                        paintRow(label, g, startX, x, endX, textY, rowStartOffset, style.getStart() + Math.min(nextRowStartIndex, styledText.text.length()), lastRow);
                    }
                    textY += maxRowHeight + Math.max(0, label.getRowGap());
                    x = startX;
                    i--;
                    rowCount++;
                    rowStartOffset = style.getStart() + Math.min(nextRowStartIndex, styledText.text.length());
                    if (lastRow) {
                        break;
                    }
                }
                else {
                    x += strWidth;
                }
                if (i == _styledTexts.size() - 1) {
                    if (horizontalAlignment != LEFT && g != null) {
                        paintRow(label, g, startX, x, endX, textY, rowStartOffset, -1, true);
                    }
                }
            }
            return (int) Math.ceil((double) textY / maxRowHeight);
        }
    }

    private void paintRow(StyledLabel label, Graphics g, int leftAlignmentX, int thisLineEndX, int rightMostX, int textY, int startOffset, int endOffset, boolean lastRow) {
        if (g == null) {
            return;
        }
        int horizontalTextPosition = label.getHorizontalTextPosition();
        int horizontalAlignment = label.getHorizontalAlignment();
        if ((horizontalTextPosition == SwingConstants.TRAILING && !label.getComponentOrientation().isLeftToRight()) ||(horizontalTextPosition == SwingConstants.LEADING && label.getComponentOrientation().isLeftToRight())) {
            horizontalTextPosition = SwingConstants.LEFT;
        }
        if ((horizontalTextPosition == SwingConstants.LEADING && !label.getComponentOrientation().isLeftToRight()) ||(horizontalTextPosition == SwingConstants.TRAILING && label.getComponentOrientation().isLeftToRight())) {
            horizontalTextPosition = SwingConstants.RIGHT;
        }
        if ((horizontalAlignment == SwingConstants.TRAILING && !label.getComponentOrientation().isLeftToRight()) ||(horizontalAlignment == SwingConstants.LEADING && label.getComponentOrientation().isLeftToRight())) {
            horizontalAlignment = SwingConstants.LEFT;
        }
        if ((horizontalAlignment == SwingConstants.LEADING && !label.getComponentOrientation().isLeftToRight()) ||(horizontalAlignment == SwingConstants.TRAILING && label.getComponentOrientation().isLeftToRight())) {
            horizontalAlignment = SwingConstants.RIGHT;
        }

        Insets insets = label.getInsets();
        int textX = leftAlignmentX;
        int paintWidth = thisLineEndX - leftAlignmentX;
        if (horizontalAlignment == RIGHT) {
            paintWidth = thisLineEndX - textX;
            textX = label.getWidth() - paintWidth;
            if (insets != null) {
                textX -= insets.right;
            }
            if (label.getIcon() != null && horizontalTextPosition == SwingConstants.LEFT) {
                textX -= label.getIcon().getIconWidth() + label.getIconTextGap();
            }
        }
        else if (horizontalAlignment == CENTER) {
            int leftMostX = 0;
            if (horizontalTextPosition == SwingConstants.RIGHT && label.getIcon() != null) {
                leftMostX += label.getIcon().getIconWidth() + label.getIconTextGap();
            }
            int labelWidth = label.getWidth();
            if (insets != null) {
                labelWidth -= insets.right + insets.left;
                leftMostX += insets.left;
            }
            if (label.getIcon() != null && horizontalTextPosition != SwingConstants.CENTER) {
                labelWidth -= label.getIcon().getIconWidth() + label.getIconTextGap();
            }
            textX = leftMostX + (labelWidth - paintWidth) / 2;
        }
        paintWidth = Math.min(paintWidth, rightMostX - leftAlignmentX);

        int mnemonicIndex = label.getDisplayedMnemonicIndex();
        if (LookAndFeelFactory.isWindowsLookAndFeel(UIManager.getLookAndFeel()) &&
                LookAndFeelFactory.isMnemonicHidden()) {
            mnemonicIndex = -1;
        }

        int charDisplayed = 0;
        boolean displayMnemonic;
        int mneIndex = 0;
        Font font = getFont(label);
        FontMetrics fm = label.getFontMetrics(font);
        FontMetrics fm2;
        FontMetrics nextFm2 = null;
        int defaultFontSize = font.getSize();

        int x = textX;
        for (int i = 0; i < _styledTexts.size() && (endOffset < 0 || charDisplayed < endOffset); i++) {
            StyledText styledText = _styledTexts.get(i);
            StyleRange style = styledText.styleRange;
            int length = style.getLength();
            if (length < 0) {
                length = styledText.text.length();
            }
            if (style.getStart() + length <= startOffset) {
                charDisplayed += length;
                continue;
            }
            int nextRowStartIndex = style.getStart() >= startOffset ? 0 : startOffset - style.getStart();
            charDisplayed += nextRowStartIndex;

            if (mnemonicIndex >= 0 && styledText.text.length() - nextRowStartIndex > mnemonicIndex - charDisplayed) {
                displayMnemonic = true;
                mneIndex = mnemonicIndex - charDisplayed;
            }
            else {
                displayMnemonic = false;
            }
            int paintLength = styledText.text.length() - nextRowStartIndex;
            if (endOffset >= 0 && charDisplayed + paintLength >= endOffset) {
                paintLength = endOffset - charDisplayed;
            }
            charDisplayed += paintLength;

            int y = textY;

            if (nextFm2 == null) {
                int size = (style != null &&
                        (style.isSuperscript() || style.isSubscript())) ? Math.round((float) defaultFontSize / style.getFontShrinkRatio()) : defaultFontSize;

                font = getFont(label);
                if (style != null && ((style.getFontStyle() != -1 && font.getStyle() != style.getFontStyle()) || font.getSize() != size)) {
                    font = FontUtils.getCachedDerivedFont(font, style.getFontStyle() == -1 ? font.getStyle() : style.getFontStyle(), size);
                    fm2 = label.getFontMetrics(font);
                }
                else {
                    fm2 = fm;
                }
            }
            else {
                fm2 = nextFm2;
            }

            g.setFont(font);

            String s = styledText.text.substring(Math.min(nextRowStartIndex, styledText.text.length()));
            if (startOffset > 0 && x == textX && s.startsWith(" ")) {
                s = s.substring(1);
            }
            if (s.length() > paintLength) {
                s = s.substring(0, paintLength);
            }
            if (s.contains("\r") || s.contains("\n")) {
                if (styledText.styleRange.getStart() + styledText.styleRange.getLength() >= endOffset) {
                    break;
                }
                s = "...";
            }

            int strWidth = fm2.stringWidth(s);

            int widthLeft = paintWidth + textX - x;
            if (widthLeft < strWidth) {
                if (strWidth <= 0) {
                    return;
                }
                if (label.isLineWrap() && !lastRow) {
                    int availLength = s.length() * widthLeft / strWidth + 1;
                    int nextWordStartIndex;
                    int nextRowStartIndexInSubString;
                    int loopCount = 0;
                    do {
                        String subString = s.substring(0, Math.max(0, Math.min(availLength, s.length())));
                        int firstRowWordEndIndex = findFirstRowWordEndIndex(subString);
                        nextWordStartIndex = firstRowWordEndIndex < 0 ? 0 : findNextWordStartIndex(s, firstRowWordEndIndex);
                        if (firstRowWordEndIndex < 0) {
                            if (x == textX) {
                                firstRowWordEndIndex = 0;
                                nextWordStartIndex = Math.min(s.length(), availLength);
                            }
                        }
                        nextRowStartIndexInSubString = firstRowWordEndIndex + 1;
                        String subStringThisRow = s.substring(0, Math.min(nextRowStartIndexInSubString, s.length()));
                        strWidth = fm2.stringWidth(subStringThisRow);
                        if (strWidth > widthLeft) {
                            availLength = subString.length() * widthLeft / strWidth;
                        }
                        loopCount++;
                        if (loopCount > 50) {
                            System.err.println("Painting Styled Label Error: " + styledText);
                            break;
                        }
                    }
                    while (strWidth > widthLeft && availLength > 0);
                    while (nextRowStartIndexInSubString < nextWordStartIndex) {
                        strWidth += fm2.charWidth(s.charAt(nextRowStartIndexInSubString));
                        if (strWidth >= widthLeft) {
                            break;
                        }
                        nextRowStartIndexInSubString++;
                    }
                    s = s.substring(0, Math.min(nextRowStartIndexInSubString, s.length()));
                    strWidth = fm2.stringWidth(s);
                    charDisplayed -= styledText.text.length() - nextRowStartIndex;
                    if (displayMnemonic) {
                        if (mnemonicIndex >= 0 && s.length() > mnemonicIndex - charDisplayed) {
                            displayMnemonic = true;
                            mneIndex = mnemonicIndex - charDisplayed;
                        }
                        else {
                            displayMnemonic = false;
                        }
                    }
                    charDisplayed += s.length();
                    nextRowStartIndex += nextRowStartIndexInSubString;
                }
                else {
                    // use this method to clip string
                    s = SwingUtilities.layoutCompoundLabel(label, fm2, s, null, label.getVerticalAlignment(), label.getHorizontalAlignment(),
                            label.getVerticalTextPosition(), label.getHorizontalTextPosition(), new Rectangle(x, y, widthLeft, label.getHeight()), new Rectangle(), new Rectangle(), 0);
                    strWidth = fm2.stringWidth(s);
                }
            }
            else if (label.isLineWrap()) {
                nextRowStartIndex = 0;
            }
            else if (i < _styledTexts.size() - 1) {
                StyledText nextStyledText = _styledTexts.get(i + 1);
                String nextText = nextStyledText.text;
                StyleRange nextStyle = nextStyledText.styleRange;
                int size = (nextStyle != null &&
                        (nextStyle.isSuperscript() || nextStyle.isSubscript())) ? Math.round((float) defaultFontSize / nextStyle.getFontShrinkRatio()) : defaultFontSize;

                font = getFont(label);
                if (nextStyle != null && ((nextStyle.getFontStyle() != -1 && font.getStyle() != nextStyle.getFontStyle()) || font.getSize() != size)) {
                    font = FontUtils.getCachedDerivedFont(font, nextStyle.getFontStyle() == -1 ? font.getStyle() : nextStyle.getFontStyle(), size);
                    nextFm2 = label.getFontMetrics(font);
                }
                else {
                    nextFm2 = fm;
                }
                if (nextFm2.stringWidth(nextText) > widthLeft - strWidth) {
                    String nextS = SwingUtilities.layoutCompoundLabel(label, nextFm2, nextText, null, label.getVerticalAlignment(), label.getHorizontalAlignment(),
                            label.getVerticalTextPosition(), label.getHorizontalTextPosition(), new Rectangle(x + strWidth, y, widthLeft - strWidth, label.getHeight()), new Rectangle(), new Rectangle(), 0);
                    if (nextFm2.stringWidth(nextS) > widthLeft - strWidth) {
                        s = SwingUtilities.layoutCompoundLabel(label, fm2, s, null, label.getVerticalAlignment(), label.getHorizontalAlignment(),
                                label.getVerticalTextPosition(), label.getHorizontalTextPosition(), new Rectangle(x, y, strWidth - 1, label.getHeight()), new Rectangle(), new Rectangle(), 0);
                        strWidth = fm2.stringWidth(s);
                    }
                }
            }

            // start of actual painting
            if (style != null && style.isSuperscript()) {
                y -= fm.getHeight() - fm2.getHeight();
            }

            if (style != null && style.getBackgroundColor() != null) {
                g.setColor(style.getBackgroundColor());
                g.fillRect(x, y - fm2.getHeight(), strWidth, fm2.getHeight() + 4);
            }

            Color textColor = (style != null && !label.isIgnoreColorSettings() && style.getFontColor() != null) ? style.getFontColor() : label.getForeground();
            if (!label.isEnabled()) {
                textColor = UIDefaultsLookup.getColor("Label.disabledForeground");
            }
            g.setColor(textColor);

            if (displayMnemonic) {
                JideSwingUtilities.drawStringUnderlineCharAt(label, g, s, mneIndex, x, y);
            }
            else {
                JideSwingUtilities.drawString(label, g, s, x, y);
            }

            if (style != null) {
                Stroke oldStroke = ((Graphics2D) g).getStroke();
                if (style.getLineStroke() != null) {
                    ((Graphics2D) g).setStroke(style.getLineStroke());
                }

                if (!label.isIgnoreColorSettings() && style.getLineColor() != null) {
                    g.setColor(style.getLineColor());
                }

                if (style.isStrikethrough()) {
                    int lineY = y + (fm2.getDescent() - fm2.getAscent()) / 2;
                    g.drawLine(x, lineY, x + strWidth - 1, lineY);
                }
                if (style.isDoublestrikethrough()) {
                    int lineY = y + (fm2.getDescent() - fm2.getAscent()) / 2;
                    g.drawLine(x, lineY - 1, x + strWidth - 1, lineY - 1);
                    g.drawLine(x, lineY + 1, x + strWidth - 1, lineY + 1);
                }
                if (style.isUnderlined()) {
                    int lineY = y + 1;
                    g.drawLine(x, lineY, x + strWidth - 1, lineY);
                }
                if (style.isDotted()) {
                    int dotY = y + 1;
                    for (int dotX = x; dotX < x + strWidth; dotX += 4) {
                        g.drawRect(dotX, dotY, 1, 1);
                    }
                }
                if (style.isWaved()) {
                    int waveY = y + 1;
                    for (int waveX = x; waveX < x + strWidth; waveX += 4) {
                        if (waveX + 2 <= x + strWidth - 1)
                            g.drawLine(waveX, waveY + 2, waveX + 2, waveY);
                        if (waveX + 4 <= x + strWidth - 1)
                            g.drawLine(waveX + 3, waveY + 1, waveX + 4, waveY + 2);
                    }
                }
                if (style.getLineStroke() != null) {
                    ((Graphics2D) g).setStroke(oldStroke);
                }
            }
            // end of actual painting
            x += strWidth;
        }
    }

    private int findNextWordStartIndex(String string, int firstRowEndIndex) {
        boolean skipFirstWord = firstRowEndIndex < 0;
        for (int i = firstRowEndIndex + 1; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                if (!skipFirstWord) {
                    return i;
                }
            }
            else {
                skipFirstWord = false;
            }
        }
        return string.length();
    }

    private int findFirstRowWordEndIndex(String string) {
        boolean spaceFound = false;
        for (int i = string.length() - 1; i >= 0; i--) {
            char c = string.charAt(i);
            if (!spaceFound) {
                if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                    spaceFound = true;
                }
            }
            else {
                if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Compute and return the location of the icons origin, the location of origin of the text baseline, and a possibly
     * clipped version of the compound labels string.  Locations are computed relative to the viewR rectangle. The
     * JComponents orientation (LEADING/TRAILING) will also be taken into account and translated into LEFT/RIGHT values
     * accordingly.
     *
     * @param c the component
     * @param fm the font metrics
     * @param text the text
     * @param icon the icon
     * @param verticalAlignment vertical alignment mode
     * @param horizontalAlignment horizontal alignment mode
     * @param verticalTextPosition vertical text position
     * @param horizontalTextPosition horizontal text position
     * @param viewR view rectangle
     * @param iconR icon rectangle
     * @param textR text rectangle
     * @param textIconGap the gap between text and icon
     * @return the layout string
     */
    public static String layoutCompoundLabel(JComponent c,
                                             FontMetrics fm,
                                             String text,
                                             Icon icon,
                                             int verticalAlignment,
                                             int horizontalAlignment,
                                             int verticalTextPosition,
                                             int horizontalTextPosition,
                                             Rectangle viewR,
                                             Rectangle iconR,
                                             Rectangle textR,
                                             int textIconGap) {
        boolean orientationIsLeftToRight = true;
        int hAlign = horizontalAlignment;
        int hTextPos = horizontalTextPosition;

        if (c != null) {
            if (!(c.getComponentOrientation().isLeftToRight())) {
                orientationIsLeftToRight = false;
            }
        }

        // Translate LEADING/TRAILING values in horizontalAlignment
        // to LEFT/RIGHT values depending on the components orientation
        switch (horizontalAlignment) {
            case LEADING:
                hAlign = (orientationIsLeftToRight) ? LEFT : RIGHT;
                break;
            case TRAILING:
                hAlign = (orientationIsLeftToRight) ? RIGHT : LEFT;
                break;
        }

        // Translate LEADING/TRAILING values in horizontalTextPosition
        // to LEFT/RIGHT values depending on the components orientation
        switch (horizontalTextPosition) {
            case LEADING:
                hTextPos = (orientationIsLeftToRight) ? LEFT : RIGHT;
                break;
            case TRAILING:
                hTextPos = (orientationIsLeftToRight) ? RIGHT : LEFT;
                break;
        }

        return layoutCompoundLabelImpl(c,
                fm,
                text,
                icon,
                verticalAlignment,
                hAlign,
                verticalTextPosition,
                hTextPos,
                viewR,
                iconR,
                textR,
                textIconGap);
    }

    /**
     * Compute and return the location of the icons origin, the location of origin of the text baseline, and a possibly
     * clipped version of the compound labels string.  Locations are computed relative to the viewR rectangle. This
     * layoutCompoundLabel() does not know how to handle LEADING/TRAILING values in horizontalTextPosition (they will
     * default to RIGHT) and in horizontalAlignment (they will default to CENTER). Use the other version of
     * layoutCompoundLabel() instead.
     *
     * @param fm the font metrics
     * @param text the text
     * @param icon the icon
     * @param verticalAlignment vertical alignment mode
     * @param horizontalAlignment horizontal alignment mode
     * @param verticalTextPosition vertical text position
     * @param horizontalTextPosition horizontal text position
     * @param viewR view rectangle
     * @param iconR icon rectangle
     * @param textR text rectangle
     * @param textIconGap the gap between text and icon
     * @return the layout string
     */
    public static String layoutCompoundLabel(
            FontMetrics fm,
            String text,
            Icon icon,
            int verticalAlignment,
            int horizontalAlignment,
            int verticalTextPosition,
            int horizontalTextPosition,
            Rectangle viewR,
            Rectangle iconR,
            Rectangle textR,
            int textIconGap) {
        return layoutCompoundLabelImpl(null, fm, text, icon,
                verticalAlignment,
                horizontalAlignment,
                verticalTextPosition,
                horizontalTextPosition,
                viewR, iconR, textR, textIconGap);
    }

    /**
     * Compute and return the location of the icons origin, the location of origin of the text baseline, and a possibly
     * clipped version of the compound labels string.  Locations are computed relative to the viewR rectangle. This
     * layoutCompoundLabel() does not know how to handle LEADING/TRAILING values in horizontalTextPosition (they will
     * default to RIGHT) and in horizontalAlignment (they will default to CENTER). Use the other version of
     * layoutCompoundLabel() instead.
     *
     * @param c the component
     * @param fm the font metrics
     * @param text the text
     * @param icon the icon
     * @param verticalAlignment vertical alignment mode
     * @param horizontalAlignment horizontal alignment mode
     * @param verticalTextPosition vertical text position
     * @param horizontalTextPosition horizontal text position
     * @param viewR view rectangle
     * @param iconR icon rectangle
     * @param textR text rectangle
     * @param textIconGap the gap between text and icon
     * @return the layout string
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private static String layoutCompoundLabelImpl(
            JComponent c,
            FontMetrics fm,
            String text,
            Icon icon,
            int verticalAlignment,
            int horizontalAlignment,
            int verticalTextPosition,
            int horizontalTextPosition,
            Rectangle viewR,
            Rectangle iconR,
            Rectangle textR,
            int textIconGap) {
        /* Initialize the icon bounds rectangle iconR.
         */

        if (icon != null) {
            iconR.width = icon.getIconWidth();
            iconR.height = icon.getIconHeight();
        }
        else {
            iconR.width = iconR.height = 0;
        }

        /* Initialize the text bounds rectangle textR.  If a null
         * or and empty String was specified we substitute "" here
         * and use 0,0,0,0 for textR.
         */

        boolean textIsEmpty = (text == null) || text.equals("");
        int lsb = 0;
        /* Unless both text and icon are non-null, we effectively ignore
         * the value of textIconGap.
         */
        int gap;

        View v;
        if (textIsEmpty) {
            textR.width = textR.height = 0;
            text = "";
            gap = 0;
        }
        else {
            int availTextWidth;
            gap = (icon == null) ? 0 : textIconGap;

            if (horizontalTextPosition == CENTER) {
                availTextWidth = viewR.width;
            }
            else {
                availTextWidth = viewR.width - (iconR.width + gap);
            }
            v = (c != null) ? (View) c.getClientProperty("html") : null;
            if (v != null) {
                textR.width = Math.min(availTextWidth, (int) v.getPreferredSpan(View.X_AXIS));
                textR.height = (int) v.getPreferredSpan(View.Y_AXIS);
            }
            else {
// this is only place that is changed for StyledLabel
//                textR.width = SwingUtilities2.stringWidth(c, fm, text);
//                lsb = SwingUtilities2.getLeftSideBearing(c, fm, text);
//                if (lsb < 0) {
//                    // If lsb is negative, add it to the width and later
//                    // adjust the x location. This gives more space than is
//                    // actually needed.
//                    // This is done like this for two reasons:
//                    // 1. If we set the width to the actual bounds all
//                    //    callers would have to account for negative lsb
//                    //    (pref size calculations ONLY look at width of
//                    //    textR)
//                    // 2. You can do a drawString at the returned location
//                    //    and the text won't be clipped.
//                    textR.width -= lsb;
//                }
//                if (textR.width > availTextWidth) {
//                    text = SwingUtilities2.clipString(c, fm, text,
//                            availTextWidth);
//                    textR.width = SwingUtilities2.stringWidth(c, fm, text);
//                }
//                textR.height = fm.getHeight();
            }
        }

        /* Compute textR.x,y given the verticalTextPosition and
        * horizontalTextPosition properties
        */

        if (verticalTextPosition == TOP) {
            if (horizontalTextPosition != CENTER) {
                textR.y = 0;
            }
            else {
                textR.y = -(textR.height + gap);
            }
        }
        else if (verticalTextPosition == CENTER) {
            textR.y = (iconR.height / 2) - (textR.height / 2);
        }
        else { // (verticalTextPosition == BOTTOM)
            if (horizontalTextPosition != CENTER) {
                textR.y = iconR.height - textR.height;
            }
            else {
                textR.y = (iconR.height + gap);
            }
        }

        if (horizontalTextPosition == LEFT) {
            textR.x = -(textR.width + gap);
        }
        else if (horizontalTextPosition == CENTER) {
            textR.x = (iconR.width / 2) - (textR.width / 2);
        }
        else { // (horizontalTextPosition == RIGHT)
            textR.x = (iconR.width + gap);
        }

        /* labelR is the rectangle that contains iconR and textR.
         * Move it to its proper position given the labelAlignment
         * properties.
         *
         * To avoid actually allocating a Rectangle, Rectangle.union
         * has been inlined below.
         */
        int labelR_x = Math.min(iconR.x, textR.x);
        int labelR_width = Math.max(iconR.x + iconR.width,
                textR.x + textR.width) - labelR_x;
        int labelR_y = Math.min(iconR.y, textR.y);
        int labelR_height = Math.max(iconR.y + iconR.height,
                textR.y + textR.height) - labelR_y;

        int dx, dy;

        if (verticalAlignment == TOP) {
            dy = viewR.y - labelR_y;
        }
        else if (verticalAlignment == CENTER) {
            dy = (viewR.y + (viewR.height / 2)) - (labelR_y + (labelR_height / 2));
        }
        else { // (verticalAlignment == BOTTOM)
            dy = (viewR.y + viewR.height) - (labelR_y + labelR_height);
        }

        if (horizontalAlignment == LEFT) {
            dx = viewR.x - labelR_x;
        }
        else if (horizontalAlignment == RIGHT) {
            dx = (viewR.x + viewR.width) - (labelR_x + labelR_width);
        }
        else { // (horizontalAlignment == CENTER)
            dx = (viewR.x + (viewR.width / 2)) -
                    (labelR_x + (labelR_width / 2));
        }

        /* Translate textR and glypyR by dx,dy.
         */

        textR.x += dx;
        textR.y += dy;

        iconR.x += dx;
        iconR.y += dy;

        if (lsb < 0) {
            // lsb is negative. Shift the x location so that the text is
            // visually drawn at the right location.
            textR.x -= lsb;
        }
        int maxIconY = viewR.height / 2;
        Insets insets = c.getInsets();
        int leftMostX = viewR.x;
        int rightMostX = viewR.width;
        rightMostX -= iconR.width;
        if (horizontalTextPosition == SwingConstants.CENTER) {
            if (viewR.width < textR.width) {
                iconR.x = (leftMostX + rightMostX) / 2;
            }
            else {
                int leftMostTextX = textR.x;
                int rightMostTextX = textR.x + textR.width - iconR.width;
                iconR.x = textR.x + (textR.width - iconR.width) / 2;
            }
        }
        else if (iconR.x < leftMostX) {
            textR.x += leftMostX - iconR.x;
            iconR.x = leftMostX;
        }
        else if (iconR.x > rightMostX && horizontalAlignment != LEFT) {
            iconR.x = rightMostX;
            textR.x -= iconR.x - rightMostX;
        }
        if (insets != null) {
            maxIconY -= (insets.bottom + insets.top) / 2;
        }
        if (icon != null) {
            maxIconY -= icon.getIconHeight() / 2;
        }
        if (verticalAlignment == TOP) {
            iconR.y = Math.min(maxIconY, iconR.y);
        }
        else if (verticalAlignment == BOTTOM) {
            iconR.y = Math.max(maxIconY, iconR.y);
        }

        return text;
    }
}
