/*
 * @(#)BasicStyledLabelUI.java 9/6/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.StyleRange;
import com.jidesoft.swing.StyledLabel;
import com.jidesoft.swing.FontUtils;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

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
                    if (styleRange.getStart() > index) { // fill in the gap
                        _styledTexts.add(new StyledText(s.substring(index, styleRange.getStart())));
                        index = styleRange.getStart();
                    }

                    if (styleRange.getStart() == index) { // exactly on
                        if (styleRange.getLength() == -1) {
                            _styledTexts.add(new StyledText(s.substring(index), styleRange));
                            index = s.length();
                        }
                        else {
                            _styledTexts.add(new StyledText(s.substring(index, Math.min(index + styleRange.getLength(), s.length())), styleRange));
                            index += styleRange.getLength();
                        }
                    }
                    else if (styleRange.getStart() < index) { // overlap
                        // ignore
                    }
                }
                if (index < s.length()) {
                    _styledTexts.add(new StyledText(s.substring(index, s.length())));
                }
            }
        }
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

    protected Dimension getPreferredSize(StyledLabel label) {
        buildStyledText(label);

        int width = 0;
        Font font = getFont(label);
        FontMetrics fm = label.getFontMetrics(font);
        FontMetrics fm2;
        int defaultFontSize = font.getSize();
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
                maxRowHeight = Math.max(maxRowHeight, styleHeight);
            }

            int rowCount = 1;
            int nextRowStartIndex = 0;
            // get one line width
            for (StyledText styledText : _styledTexts) {
                StyleRange style = styledText.styleRange;
                int size = (style != null &&
                        (style.isSuperscript() || style.isSubscript())) ? Math.round((float) defaultFontSize / style.getFontShrinkRatio()) : defaultFontSize;
                font = getFont(label);
                String s = styledText.text.substring(nextRowStartIndex);
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
            int maxWidth = width;
            int oneLineWidth = width;

            // if getPreferredWidth() is not set but getRows() is set, get maximum width and row count based on the required rows.
            if (label.isLineWrap() && label.getPreferredWidth() <= 0 && label.getRows() > 0) {
                maxWidth = getMaximumWidth(label, oneLineWidth, label.getRows());
                rowCount = label.getRows();
            }

            // if calculated maximum width is larger than label's maximum size, wrap again to get the updated row count and use the label's maximum width as the maximum width.
            if (label.isLineWrap() && label.getPreferredWidth() > 0 && maxWidth > label.getPreferredWidth()) {
                maxWidth = label.getPreferredWidth();
                nextRowStartIndex = 0;
                int x = 0;
                rowCount = 1;
                int characterThisRow = 0;
                for (int i = 0; i < _styledTexts.size(); i++) {
                    StyledText styledText = _styledTexts.get(i);
                    StyleRange style = styledText.styleRange;

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
                                    rowCount++;
                                    characterThisRow = 0;
                                    if (label.getMaxRows() > 0 && rowCount >= label.getMaxRows()) {
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
                            if (loopCount > 5) {
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
                        characterThisRow += s.length();
                    }
                    else {
                        nextRowStartIndex = 0;
                    }

                    if (wrapped) {
                        rowCount++;
                        x = 0;
                        i--;
                        characterThisRow = 0;
                    }
                    else {
                        x += strWidth;
                    }
                }
            }

            // label.getPreferredWidth() <= 0 && label.getMaxRows() > 0 && rowCount > label.getMaxRows(), recalculate the maximum width according to the maximum rows
            if (label.isLineWrap() && label.getMaxRows() > 0 && rowCount > label.getMaxRows()) {
                if (label.getPreferredWidth() <= 0) {
                    maxWidth = getMaximumWidth(label, oneLineWidth, label.getMaxRows());
                }
                rowCount = label.getMaxRows();
            }

            // label.getPreferredWidth() <= 0 && label.getMinRows() > 0 && rowCount < label.getMinRows(), recalculate the maximum width according to the minimum rows
            if (label.isLineWrap() && label.getPreferredWidth() <= 0 && label.getMinRows() > 0 && rowCount < label.getMinRows()) {
                maxWidth = getMaximumWidth(label, oneLineWidth, label.getMinRows());
                rowCount = label.getMinRows();
            }
            _preferredRowCount = rowCount;
            return new Dimension(maxWidth, (maxRowHeight + Math.max(0, label.getRowGap())) * rowCount);
        }
    }

    private int getMaximumWidth(StyledLabel label, int oneLineWidth, int limitedRows) {
        int estimatedWidth = oneLineWidth / limitedRows + 1;
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
                if (x + strWidth >= oneLineWidth) {
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
        int paintWidth = label.getWidth();
        if (label.isLineWrap()) {
            int oldPreferredWidth = label.getPreferredWidth();
            int oldRows = label.getRows();
            try {
                label.setRows(0);
                paintWidth = getPreferredSize(label).width;
                label.setPreferredWidth(label.getWidth());
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

        int x = textX < label.getInsets().left ? label.getInsets().left : textX;
        int y;
        paintWidth += x;
        int mnemonicIndex = label.getDisplayedMnemonicIndex();
        if (UIManager.getLookAndFeel() instanceof WindowsLookAndFeel &&
                WindowsLookAndFeel.isMnemonicHidden()) {
            mnemonicIndex = -1;
        }

        Color oldColor = g.getColor();

        int charDisplayed = 0;
        boolean displayMnemonic;
        int mneIndex = 0;
        Font font = getFont(label);
        FontMetrics fm = label.getFontMetrics(font);
        FontMetrics fm2;
        FontMetrics nextFm2 = null;
        int defaultFontSize = font.getSize();

        synchronized (_styledTexts) {
            String nextS;
            int maxRowHeight = fm.getHeight();
            int minStartY = fm.getAscent();
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
            if (label.isLineWrap() && textY < minStartY) {
                textY = minStartY;
            }

            int nextRowStartIndex = 0;
            int rowCount = 0;
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

                g.setFont(font);

                String s = styledText.text.substring(Math.min(nextRowStartIndex, styledText.text.length()));

                int strWidth = fm2.stringWidth(s);

                boolean stop = false;
                boolean wrapped = false;
                int widthLeft = paintWidth - x;
                if (widthLeft < strWidth) {
                    if (label.isLineWrap() && ((label.getMaxRows() > 0 && rowCount < label.getMaxRows() - 1) || label.getMaxRows() <= 0) && y + maxRowHeight + Math.max(0, label.getRowGap()) <= label.getHeight()) {
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
                                if (x != textX) {
                                    textY += maxRowHeight + Math.max(0, label.getRowGap());
                                    x = textX;
                                    i--;
                                    rowCount++;
                                    if (label.getMaxRows() > 0 && rowCount >= label.getMaxRows()) {
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
                            if (loopCount > 5) {
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
                                label.getVerticalTextPosition(), label.getHorizontalTextPosition(), new Rectangle(x, y, widthLeft, label.getHeight()), new Rectangle(), new Rectangle(), 0);
                        strWidth = fm2.stringWidth(s);
                    }
                    stop = !label.isLineWrap() || textY >= label.getHeight();
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
                        nextS = SwingUtilities.layoutCompoundLabel(label, nextFm2, nextText, null, label.getVerticalAlignment(), label.getHorizontalAlignment(),
                                label.getVerticalTextPosition(), label.getHorizontalTextPosition(), new Rectangle(x + strWidth, y, widthLeft - strWidth, label.getHeight()), new Rectangle(), new Rectangle(), 0);
                        if (nextFm2.stringWidth(nextS) > widthLeft - strWidth) {
                            s = SwingUtilities.layoutCompoundLabel(label, fm2, s, null, label.getVerticalAlignment(), label.getHorizontalAlignment(),
                                    label.getVerticalTextPosition(), label.getHorizontalTextPosition(), new Rectangle(x, y, strWidth - 1, label.getHeight()), new Rectangle(), new Rectangle(), 0);
                            strWidth = fm2.stringWidth(s);
                            stop = true;
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

                if (stop) {
                    break;
                }

                if (wrapped) {
                    textY += maxRowHeight + Math.max(0, label.getRowGap());
                    x = textX;
                    i--;
                    rowCount++;
                    if ((label.getMaxRows() > 0 && rowCount >= label.getMaxRows()) || textY > label.getHeight()) {
                        break;
                    }
                }
                else {
                    x += strWidth;
                }
            }
        }

        g.setColor(oldColor);
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

        return text;
    }
}
