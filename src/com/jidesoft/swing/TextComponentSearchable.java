/*
 * @(#)TextComponentSearchable.java 10/11/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.swing.event.SearchableEvent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <code>TextComponentSearchable</code> is an concrete implementation of {@link Searchable} that enables the search
 * function in JTextComponent. <p>It's very simple to use it. Assuming you have a JTextComponent, all you need to do is
 * to call
 * <code><pre>
 * JTextComponent textComponent = ....;
 * TextComponentSearchable searchable = new TextComponentSearchable(textComponent);
 * </pre></code>
 * Now the JTextComponent will have the search function.
 * <p/>
 * There is very little customization you need to do to ListSearchable. The only thing you might need is when the
 * element in the JTextComponent needs a special conversion to convert to string. If so, you can override
 * convertElementToString() to provide you own algorithm to do the conversion.
 * <code><pre>
 * JTextComponent textComponent = ....;
 * TextComponentSearchable searchable = new ListSearchable(textComponent) {
 *      protected String convertElementToString(Object object) {
 *          ...
 *      }
 * <p/>
 *      protected boolean isActivateKey(KeyEvent e) { // change to a different activation key
 *          return (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_F &&
 * (KeyEvent.CTRL_MASK & e.getModifiers()) != 0);
 *      }
 * };
 * </pre></code>
 * <p/>
 * Additional customization can be done on the base Searchable class such as background and foreground color,
 * keystrokes, case sensitivity. TextComponentSearchable also has a special attribute called highlightColor. You can
 * change it using {@link #setHighlightColor(java.awt.Color)}.
 * <p/>
 * Due to the special case of JTextComponent, the searching doesn't support wild card '*' or '?' as in other
 * Searchables. The other difference is JTextComponent will keep the highlights after search popup hides. If you want to
 * hide the highlights, just press ESC again (the first ESC will hide popup; the second ESC will hide all highlights if
 * any).
 */
public class TextComponentSearchable extends Searchable implements DocumentListener, PropertyChangeListener {
    private Highlighter.HighlightPainter _highlightPainter;
    private static final Color DEFAULT_HIGHLIGHT_COLOR = new Color(204, 204, 255);
    private Color _highlightColor = null;
    private int _selectedIndex = -1;
    private HighlighCache _highlighCache;

    public TextComponentSearchable(JTextComponent textComponent) {
        super(textComponent);
        _highlighCache = new HighlighCache();
        installHighlightsRemover();
        setHighlightColor(DEFAULT_HIGHLIGHT_COLOR);
    }

    /**
     * Uninstalls the handler for ESC key to remove all highlights
     */
    public void uninstallHighlightsRemover() {
        DelegateAction.restoreAction(_component, JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
    }

    /**
     * Installs the handler for ESC key to remove all highlights
     */
    public void installHighlightsRemover() {
        DelegateAction.replaceAction(_component, JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), new DelegateAction() {
            private static final long serialVersionUID = 8572103995404313032L;

            @Override
            public boolean isDelegateEnabled() {
                if (_component instanceof JTextComponent) {
                    Iterator highlights = _highlighCache.getAllHighlights();
                    if (!highlights.hasNext()) {
                        return false;
                    }
                }
                return super.isDelegateEnabled();
            }

            @Override
            public boolean delegateActionPerformed(ActionEvent e) {
                if (_component instanceof JTextComponent) {
                    Iterator highlights = _highlighCache.getAllHighlights();
                    if (!highlights.hasNext()) {
                        return false;
                    }
                }
                removeAllHighlights();
                return true;
            }
        });
    }

    @Override
    public void installListeners() {
        super.installListeners();
        if (_component instanceof JTextComponent) {
            ((JTextComponent) _component).getDocument().addDocumentListener(this);
            _component.addPropertyChangeListener("document", this);
        }
    }

    @Override
    public void uninstallListeners() {
        super.uninstallListeners();
        if (_component instanceof JTextComponent) {
            ((JTextComponent) _component).getDocument().removeDocumentListener(this);
            _component.removePropertyChangeListener("document", this);
        }
    }

    @Override
    protected void setSelectedIndex(int index, boolean incremental) {
        if (_component instanceof JTextComponent) {
            if (index == -1) {
                removeAllHighlights();
                _selectedIndex = -1;
                return;
            }

            if (!incremental) {
                removeAllHighlights();
            }

            String text = getSearchingText();
            try {
                addHighlight(index, text, incremental);
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds highlight to text component at specified index and text.
     *
     * @param index       the index of the text to be highlighted
     * @param text        the text to be highlighted
     * @param incremental if this is an incremental adding highlight
     * @throws BadLocationException
     */
    protected void addHighlight(final int index, final String text, boolean incremental) throws BadLocationException {
        if (_component instanceof JTextComponent) {
            final JTextComponent textComponent = ((JTextComponent) _component);
            Object obj = textComponent.getHighlighter().addHighlight(index, index + text.length(), _highlightPainter);
            _highlighCache.addHighlight(obj);
            _selectedIndex = index;
            if (!incremental) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        scrollTextVisible(textComponent, index, text.length());
                    }
                };
                SwingUtilities.invokeLater(runnable);
            }
        }
    }

    private void scrollTextVisible(JTextComponent textComponent, int index, int length) {
        // scroll highlight visible
        if (index != -1) {
            // Scroll the component if needed so that the composed text
            // becomes visible.
            try {
                Rectangle begin = textComponent.modelToView(index);
                if (begin == null) {
                    return;
                }
                Rectangle end = textComponent.modelToView(index + length);
                if (end == null) {
                    return;
                }
                Rectangle bounds = _component.getVisibleRect();
                if (begin.x <= bounds.width) { // make sure if scroll back to the beginning as long as selected rect is visible
                    begin.width = end.x;
                    begin.x = 0;
                }
                else {
                    begin.width = end.x - begin.x;
                }
                textComponent.scrollRectToVisible(begin);
            }
            catch (BadLocationException ble) {
            }
        }
    }

    /**
     * Removes all highlights from the text component.
     */
    protected void removeAllHighlights() {
        if (_component instanceof JTextComponent) {
            Iterator itor = _highlighCache.getAllHighlights();
            while (itor.hasNext()) {
                Object o = itor.next();
                ((JTextComponent) _component).getHighlighter().removeHighlight(o);
            }
            _highlighCache.removeAllHighlights();
        }
    }

    @Override
    protected int getSelectedIndex() {
        if (_component instanceof JTextComponent) {
            return _selectedIndex;
        }
        return 0;
    }

    @Override
    protected Object getElementAt(int index) {
        String text = getSearchingText();
        if (text != null) {
            if (_component instanceof JTextComponent) {
                int endIndex = index + text.length();
                int elementCount = getElementCount();
                if (endIndex > elementCount) {
                    endIndex = getElementCount();
                }
                try {
                    return ((JTextComponent) _component).getDocument().getText(index, endIndex - index + 1);
                }
                catch (BadLocationException e) {
                    return null;
                }
            }
        }
        return "";
    }

    @Override
    protected int getElementCount() {
        if (_component instanceof JTextComponent) {
            return ((JTextComponent) _component).getDocument().getLength();
        }
        return 0;
    }

    /**
     * Converts the element in JTextComponent to string. The returned value will be the <code>toString()</code> of
     * whatever element that returned from <code>list.getModel().getElementAt(i)</code>.
     *
     * @param object
     * @return the string representing the element in the JTextComponent.
     */
    @Override
    protected String convertElementToString(Object object) {
        if (object != null) {
            return object.toString();
        }
        else {
            return "";
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (isProcessModelChangeEvent()) {
            hidePopup();
            _text = null;
            if (evt.getOldValue() instanceof Document) {
                ((Document) evt.getNewValue()).removeDocumentListener(this);
            }
            if (evt.getNewValue() instanceof Document) {
                ((Document) evt.getNewValue()).addDocumentListener(this);
            }
            fireSearchableEvent(new SearchableEvent(this, SearchableEvent.SEARCHABLE_MODEL_CHANGE));
        }
    }

    public void insertUpdate(DocumentEvent e) {
        if (isProcessModelChangeEvent()) {
            hidePopup();
            _text = null;
            fireSearchableEvent(new SearchableEvent(this, SearchableEvent.SEARCHABLE_MODEL_CHANGE));
        }
    }

    public void removeUpdate(DocumentEvent e) {
        if (isProcessModelChangeEvent()) {
            hidePopup();
            _text = null;
            fireSearchableEvent(new SearchableEvent(this, SearchableEvent.SEARCHABLE_MODEL_CHANGE));
        }
    }

    public void changedUpdate(DocumentEvent e) {
        if (isProcessModelChangeEvent()) {
            hidePopup();
            _text = null;
            fireSearchableEvent(new SearchableEvent(this, SearchableEvent.SEARCHABLE_MODEL_CHANGE));
        }
    }

    @Override
    protected boolean isActivateKey(KeyEvent e) {
        if (_component instanceof JTextComponent && ((JTextComponent) _component).isEditable()) {
            return (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_F && JideSwingUtilities.isMenuShortcutKeyDown(e));
        }
        else {
            return super.isActivateKey(e);
        }
    }

    /**
     * Gets the highlight color.
     *
     * @return the highlight color.
     */
    public Color getHighlightColor() {
        if (_highlightColor != null) {
            return _highlightColor;
        }
        else {
            return DEFAULT_HIGHLIGHT_COLOR;
        }
    }

    /**
     * Changes the highlight color.
     *
     * @param highlightColor
     */
    public void setHighlightColor(Color highlightColor) {
        _highlightColor = highlightColor;
        _highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(_highlightColor);
    }

    @Override
    public int findLast(String s) {
        if (_component instanceof JTextComponent) {
            String text = getDocumentText();
            if (isCaseSensitive()) {
                return text.lastIndexOf(s);
            }
            else {
                return lastIndexOf(text, s, text.length());
            }
        }
        else {
            return super.findLast(s);
        }
    }

    private String _text = null;

    /**
     * Gets the text from Document.
     *
     * @return the text of this JTextComponent. It used Document to get the text.
     */
    private String getDocumentText() {
        if (_text == null) {
            Document document = ((JTextComponent) _component).getDocument();
            try {
                String text = document.getText(0, document.getLength());
                _text = text;
            }
            catch (BadLocationException e) {
                return "";
            }
        }
        return _text;
    }

    @Override
    public int findFirst(String s) {
        if (_component instanceof JTextComponent) {
            String text = getDocumentText();
            if (isCaseSensitive()) {
                return text.indexOf(s);
            }
            else {
                return indexOf(text, s, 0);
            }
        }
        else {
            return super.findFirst(s);
        }
    }

    static int lastIndexOf(String source, String target, int fromIndex) {
        int sourceCount = source.length();
        int targetCount = target.length();
        int rightIndex = sourceCount - targetCount;
        if (fromIndex < 0) {
            return -1;
        }
        if (fromIndex > rightIndex) {
            fromIndex = rightIndex;
        }
        /* Empty string always matches. */
        if (targetCount == 0) {
            return fromIndex;
        }
        char[] lowerTarget = target.toLowerCase().toCharArray();
        char[] upperTarget = target.toUpperCase().toCharArray();

        int strLastIndex = targetCount - 1;
        int min = targetCount - 1;
        int i = min + fromIndex;

        while (i >= min) {
            while (i >= min && source.charAt(i) != lowerTarget[strLastIndex] && source.charAt(i) != upperTarget[strLastIndex]) {
                i--;
            }
            if (i < min) {
                break;
            }
            int j = i - 1;
            int start = j - (targetCount - 1);
            int k = strLastIndex - 1;

            while (j > start) {
                char ch = source.charAt(j);
                if (ch != lowerTarget[k] && ch != upperTarget[k]) {
                    i--;
                    break;
                }
                j--;
                k--;
            }
            if (j <= start) {
                return start + 1;
            }
        }
        return -1;
    }

    private static int indexOf(String source, String target, int fromIndex) {
        int sourceCount = source.length();
        int targetCount = target.length();
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }
        char[] lowerTarget = target.toLowerCase().toCharArray();
        char[] upperTarget = target.toUpperCase().toCharArray();

        int max = sourceCount - targetCount;

        for (int i = fromIndex; i <= max; i++) {
            /* Look for first character. */
            char c = source.charAt(i);
            if (c != lowerTarget[0] && c != upperTarget[0]) {
                i++;
                while (i <= max && source.charAt(i) != lowerTarget[0] && source.charAt(i) != upperTarget[0]) {
                    i++;
                }
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = 1; j < end; j++) {
                    char ch = source.charAt(j);
                    if (ch != lowerTarget[k] && ch != upperTarget[k]) {
                        break;
                    }
                    k++;
                }

                if (j == end) {
                    /* Found whole string. */
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int findFromCursor(String s) {
        if (isCountMatch()) {
            String text = getDocumentText();
            int oldIndex;
            int newIndex = 0;
            _matchCount = -1;
            do
            {
                oldIndex = newIndex;
                newIndex = (isCaseSensitive() ? text.indexOf(s, oldIndex) : indexOf(text, s, oldIndex)) + 1;
                _matchCount++;
            }
            while (newIndex > oldIndex);
        }

        if (isReverseOrder()) {
            return reverseFindFromCursor(s);
        }

        if (_component instanceof JTextComponent) {
            String text = getDocumentText();
            int selectedIndex = (getCursor() != -1 ? getCursor() : getSelectedIndex());
            if (selectedIndex < 0)
                selectedIndex = 0;
            int count = getElementCount();
            if (count == 0)
                return s.length() > 0 ? -1 : 0;

            // find from cursor
            int found = isCaseSensitive() ? text.indexOf(s, selectedIndex) : indexOf(text, s, selectedIndex);

            // if not found, start over from the beginning
            if (found == -1) {
                found = isCaseSensitive() ? text.indexOf(s, 0) : indexOf(text, s, 0);
                if (found >= selectedIndex) {
                    found = -1;
                }
            }

            return found;
        }
        else {
            return super.findFromCursor(s);
        }
    }

    @Override
    public int reverseFindFromCursor(String s) {
        if (!isReverseOrder()) {
            return findFromCursor(s);
        }

        if (_component instanceof JTextComponent) {
            String text = getDocumentText();
            int selectedIndex = (getCursor() != -1 ? getCursor() : getSelectedIndex());
            if (selectedIndex < 0)
                selectedIndex = 0;
            int count = getElementCount();
            if (count == 0)
                return s.length() > 0 ? -1 : 0;

            // find from cursor
            int found = isCaseSensitive() ? text.lastIndexOf(s, selectedIndex) : lastIndexOf(text, s, selectedIndex);

            // if not found, start over from the end
            if (found == -1) {
                found = isCaseSensitive() ? text.lastIndexOf(s, text.length() - 1) : lastIndexOf(text, s, text.length() - 1);
                if (found <= selectedIndex) {
                    found = -1;
                }
            }

            return found;
        }
        else {
            return super.findFromCursor(s);
        }
    }

    @Override
    public int findNext(String s) {
        if (_component instanceof JTextComponent) {
            String text = getDocumentText();
            int selectedIndex = (getCursor() != -1 ? getCursor() : getSelectedIndex());
            if (selectedIndex < 0)
                selectedIndex = 0;
            int count = getElementCount();
            if (count == 0)
                return s.length() > 0 ? -1 : 0;

            // find from cursor
            int found = isCaseSensitive() ? text.indexOf(s, selectedIndex + 1) : indexOf(text, s, selectedIndex + 1);

            // if not found, start over from the beginning
            if (found == -1 && isRepeats()) {
                found = isCaseSensitive() ? text.indexOf(s, 0) : indexOf(text, s, 0);
                if (found > selectedIndex) {
                    found = -1;
                }
            }

            return found;
        }
        else {
            return super.findNext(s);
        }
    }

    @Override
    public int findPrevious(String s) {
        if (_component instanceof JTextComponent) {
            String text = getDocumentText();
            int selectedIndex = (getCursor() != -1 ? getCursor() : getSelectedIndex());
            if (selectedIndex < 0)
                selectedIndex = 0;
            int count = getElementCount();
            if (count == 0)
                return s.length() > 0 ? -1 : 0;

            // find from cursor
            int found = isCaseSensitive() ? text.lastIndexOf(s, selectedIndex - 1) : lastIndexOf(text, s, selectedIndex - 1);

            // if not found, start over from the beginning
            if (found == -1 && isRepeats()) {
                found = isCaseSensitive() ? text.lastIndexOf(s, count - 1) : lastIndexOf(text, s, count - 1);
                if (found < selectedIndex) {
                    found = -1;
                }
            }

            return found;
        }
        else {
            return super.findPrevious(s);
        }
    }

    private class HighlighCache extends HashMap {
        public void addHighlight(Object obj) {
            put(obj, null);
        }

        public void removeHighlight(Object obj) {
            remove(obj);
        }

        public Iterator getAllHighlights() {
            return keySet().iterator();
        }

        public void removeAllHighlights() {
            clear();
        }
    }

    @Override
    public void hidePopup() {
        super.hidePopup();
        _selectedIndex = -1;
    }

    @Override
    protected void searchingTextEmpty() {
        setSelectedIndex(-1, false);
    }
}
