/*
 * @(#)StyledLabel.java 9/6/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import java.util.List;
import java.util.Vector;

/**
 * <code>StyledLabel</code> is a special JLabel which can display text in different styles. It is a component between
 * JLabel and JTextPane. JLabel is simple, fast but has limited features. For example, you can't use different color to
 * draw the text. You may argue JLabel can use HTML tag to display text in different colors. However there are two main
 * reasons to use StyledLabel. First of all, StyledLabel is very fast and almost as fast as JLabel with plain text. HTML
 * JLabel is very slow. You can see StyledLabelPerformanceDemo.java in examples\B15. StyledLabel folder to see a
 * performace test of HTML JLabel and StyledLabel. HTML JLabel is also buggy. See bug report at <a
 * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4373575">http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4373575</a>.
 * Sun claimed it is fixed but it is not as another user pointed it out at the end. If you run the test case provided by
 * original submitter, you will immediately notice the tree node disappeared when you click on the tree nodes. This bug
 * is actually one of the main reasons we decided to create StyledLabel. JTextPane is powerful and can display text in
 * different color. But in the cases like cell renderer, JTextPane is obviously an overkill.
 * <p/>
 * StyledLabel sits between JLabel and JTextPane and provides a very simple and fast way to display text in different
 * color and style. It can also support decorations using all kinds of line styles.
 * <p/>
 * All the methods on JLabel still work as before. The methods added in StyledLabel are several methods for StyleRange,
 * such as {@link #addStyleRange(StyleRange)}, {@link #setStyleRanges(StyleRange[])}, {@link
 * #clearStyleRange(StyleRange)}, and {@link #clearStyleRanges()}.
 * <p/>
 * This is one thing about StyleRange that you should be aware of, which could be considered as a future enhancement
 * item, is that the StyleRanges can't overlap with each other. For example, if you defined a StyleRange that covers
 * from index 0 to index 3, you can't define any other StyleRange that overlaps with the first one. If you do so, the
 * second StyleRange will be ignored.
 * <p/>
 * We borrowed some ideas from SWT's StyledText when we designed StyledLabel, especially StyleRange concept. Saying
 * that, the features of the two components are not exactly the same since the purpose of the two components are quite
 * different.
 */
public class StyledLabel extends JLabel {

    private static final String uiClassID = "StyledLabelUI";

    /**
     * The list of StyleRanges.
     */
    private List<StyleRange> _styleRanges;

    private boolean _ignoreColorSettings;

    public static final String PROPERTY_STYLE_RANGE = "styleRange";
    public static final String PROPERTY_IGNORE_COLOR_SETTINGS = "ignoreColorSettings";

    public StyledLabel() {
    }

    public StyledLabel(Icon image) {
        super(image);
    }

    public StyledLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
    }

    public StyledLabel(String text) {
        super(text);
    }

    public StyledLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    public StyledLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    @Override
    public void updateUI() {
        if (UIDefaultsLookup.get(uiClassID) == null) {
            LookAndFeelFactory.installJideExtension();
        }
        setUI(UIManager.getUI(this));
    }


    /**
     * Returns a string that specifies the name of the L&F class that renders this component.
     *
     * @return the string "StyledLabelUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Adds a StyleRange into this <code>StyledLabel</code>.
     *
     * @param styleRange the new StyleRange.
     */
    public synchronized void addStyleRange(StyleRange styleRange) {
        if (styleRange == null) {
            throw new IllegalArgumentException("StyleRange cannot be null.");
        }
        List<StyleRange> ranges = internalGetStyleRanges();
        for (int i = ranges.size() - 1; i >= 0; i--) {
            StyleRange range = ranges.get(i);
            if (range.getStart() == styleRange.getStart() && range.getStart() == styleRange.getStart()) {
                ranges.remove(i);
            }
        }
        internalGetStyleRanges().add(styleRange);
        firePropertyChange(PROPERTY_STYLE_RANGE, null, styleRange);
    }

    /**
     * Clears all the old StyleRanges and adds a list of StyleRanges into this <code>StyledLabel</code>.
     *
     * @param styleRanges set the StyleRanges.
     */
    public synchronized void setStyleRanges(StyleRange[] styleRanges) {
        internalGetStyleRanges().clear();
        addStyleRanges(styleRanges);
    }

    /**
     * Adds a list of StyleRanges into this <code>StyledLabel</code>.
     *
     * @param styleRanges an array of StyleRanges.
     */
    public synchronized void addStyleRanges(StyleRange[] styleRanges) {
        if (styleRanges != null) {
            for (StyleRange styleRange : styleRanges) {
                internalGetStyleRanges().add(styleRange);
            }
            firePropertyChange(PROPERTY_STYLE_RANGE, null, styleRanges);
        }
        else {
            firePropertyChange(PROPERTY_STYLE_RANGE, null, null);
        }
    }

    /**
     * Gets the array of StyledText.
     *
     * @return the array of StyledText.
     */
    public synchronized StyleRange[] getStyleRanges() {
        List<StyleRange> list = internalGetStyleRanges();
        return list.toArray(new StyleRange[list.size()]);
    }

    private List<StyleRange> internalGetStyleRanges() {
        if (_styleRanges == null) {
            _styleRanges = new Vector();
        }
        return _styleRanges;
    }

    /**
     * Removes the StyleRange.
     *
     * @param styleRange the StyleRange to be removed.
     */
    public synchronized void clearStyleRange(StyleRange styleRange) {
        if (internalGetStyleRanges().remove(styleRange)) {
            firePropertyChange(PROPERTY_STYLE_RANGE, styleRange, null);
        }
    }

    /**
     * Clears all the StyleRanges.
     */
    public synchronized void clearStyleRanges() {
        internalGetStyleRanges().clear();
        firePropertyChange(PROPERTY_STYLE_RANGE, null, null);
    }

    /**
     * StyleRange could define color for the text and lines. However when StyledLabel is used in cell renderer, the
     * color could be conflict with selection color. So usually when it is used as cell renderer, the color defined in
     * StyleRange should be ignored when cell is selected. If so, the foreground is used to paint all text and lines.
     *
     * @return true if the color defined by StyleRange should be ignored.
     */
    public boolean isIgnoreColorSettings() {
        return _ignoreColorSettings;
    }

    /**
     * Sets if the color defined by StyleRange should be ignored. This flag is used when StyledLabel is used as a
     * selected cell renderer. Since the selection usually has it own unique selection background and foreground, the
     * color setting set on this StyledLabel could be unreadable on the selection background, it'd better if we don't
     * use any color settings in this case.
     *
     * @param ignoreColorSettings true or false.
     */
    public void setIgnoreColorSettings(boolean ignoreColorSettings) {
        boolean old = _ignoreColorSettings;
        if (old != ignoreColorSettings) {
            _ignoreColorSettings = ignoreColorSettings;
            firePropertyChange(PROPERTY_IGNORE_COLOR_SETTINGS, old, ignoreColorSettings);
        }
    }
}
