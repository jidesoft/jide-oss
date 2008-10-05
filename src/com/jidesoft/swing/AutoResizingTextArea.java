/*
 * @(#)AutosizingTextArea.java 5/11/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.awt.*;

/**
 * An extended version of <code>JTextArea</code> that automatically resizes itself vertically. This component works best
 * when used in a layout that obeys preferred height of its components. For example, you can use a
 * <code>BorderLayout</code> and place <code>AutoResizingTextArea</code> to the north or south side. Similarly, you can
 * use a <code>JideBoxLayout</code> and use FLEXIBLE or FIX as the constraint.
 */
public class AutoResizingTextArea extends JTextArea {

    /**
     * Default maximum height of the text area in rows.
     */
    public static final int DEFAULT_MAX_ROWS = 20;

    /**
     * Default minimum height of the text area in rows.
     */
    public static final int DEFAULT_MIN_ROWS = 1;

    private int _maxRows;
    private int _minRows;

    /**
     * Creates a textarea with the default minimum and maximum number of rows.
     */
    public AutoResizingTextArea() {
        this(DEFAULT_MIN_ROWS, DEFAULT_MAX_ROWS);
    }

    /**
     * Creates a textarea with the specified minimum number of rows.
     *
     * @param minRows The minimum number of rows that this textarea can have.
     */
    public AutoResizingTextArea(int minRows) {
        this(minRows, DEFAULT_MAX_ROWS);
    }

    /**
     * Creates a textarea with the specified minimum and maximum number of rows.
     *
     * @param minRows The minimum number of rows that this textarea can have.
     * @param maxRows The maximum number of rows that this textarea can have.
     */
    public AutoResizingTextArea(int minRows, int maxRows) {
        super();
        setMinRows(minRows);
        setMaxRows(maxRows);
        setRows(minRows);
        setupDocument();
    }

    /**
     * Creates a textarea with the default minimum and maximum row count and the provided initial text. The textarea is
     * sized to fit the provided text.
     *
     * @param text The initial text to display.
     */
    public AutoResizingTextArea(String text) {
        this();
        setText(text);
    }

    /**
     * Create a new <code>AutoResizingTextArea</code> with a height bounded by the provided minimum and maximum row
     * counts and with its width dictated by the provided column count.
     *
     * @param minRows The minimum number of rows that this textarea can have
     * @param maxRows The maximum number of rows that this textarea can have.
     * @param columns The number of columns that this textarea has.
     */
    public AutoResizingTextArea(int minRows, int maxRows, int columns) {
        this(minRows, maxRows);
        setMinRows(minRows);
        setMaxRows(maxRows);
        setColumns(columns);
    }

    /**
     * Create a new <code>AutoResizingTextArea</code> with a height bounded by the provided minimum and maximum row
     * counts and with its width dictated by the provided column count. The textarea is sized to fit the provided text.
     *
     * @param text    The initial text to display in the textarea.
     * @param minRows The minimum number of rows that this textarea can have
     * @param maxRows The maximum number of rows that this textarea can have.
     * @param columns The number of columns that this textarea has.
     * @throws IllegalArgumentException if the rows or columns arguments are negative.
     */
    public AutoResizingTextArea(String text, int minRows, int maxRows, int columns) {
        this(minRows, maxRows, columns);
        setText(text);
    }

    /**
     * Create a new <code>AutoResizingTextArea</code> using a <code>Document</code>. The document will be set to the
     * text area using {@link #setDocument(javax.swing.text.Document)}.
     *
     * @param doc the document.
     */
    public AutoResizingTextArea(Document doc) {
        this();
        setDocument(doc);
    }

    /**
     * Constructs a new <code>AutoResizingTextArea</code> with the specified number of rows and columns, and the given
     * model.  All of the constructors feed through this constructor.
     *
     * @param doc     the model to use, or create a default one if null
     * @param text    the text to be displayed, null if none
     * @param minRows the minimum number of rows >= 0
     * @param maxRows the maximum number of rows >= 0
     * @param columns the number of columns >= 0
     * @throws IllegalArgumentException if the rows or columns arguments are negative.
     */

    public AutoResizingTextArea(Document doc, String text, int minRows, int maxRows, int columns) {
        super(doc, text, minRows, columns);
        setMaxRows(maxRows);
        setMinRows(minRows);
        setupDocument();
    }

    /**
     * Sets the number of visible rows. The row value will be forced to the boundaries of the range [minRows ...
     * maxRows] if it is outside that range.
     *
     * @param rows The number of rows to show
     */
    @Override
    public void setRows(int rows) {
        int oldRow = super.getRows();
        int newRow = clipRowCount(rows);
        super.setRows(newRow);

        numberOfRowsUpdated(oldRow, newRow);
    }

    /**
     * Called when the number of rows is updated. By default, it will get the parent scroll pane and call revalidate.
     * Subclass can override it to customize the behavior when number of rows is updated.
     *
     * @param oldRow the previous row count.
     * @param newRow the new row count.
     */
    protected void numberOfRowsUpdated(int oldRow, int newRow) {
        // look for a parent ScrollPane and revalidate its container
        // otherwise revalidate the text area's container
        JScrollPane scroll = getParentScrollPane();
        if (scroll != null) {
            Container parent = scroll.getParent();
            if (parent != null && parent instanceof JComponent) {
                JComponent component = (JComponent) parent;
                component.revalidate();
            }
        }
    }

    /**
     * Gets the maximum number of rows that will be displayed. You can set it using {@link #setMaxRows(int)} or passed
     * in using constructor such as {@link #AutoResizingTextArea(int,int)}.
     *
     * @return the maximum number of rows that will be displayed.
     */
    public int getMaxRows() {
        return _maxRows;
    }

    /**
     * Sets the maximum number of rows that will be displayed.
     *
     * @param maxRows The maximum number of rows.
     */
    public void setMaxRows(int maxRows) {
        _maxRows = maxRows;
        setRows(clipRowCount(getRows()));
    }

    /**
     * Gets the minimum number of rows that will be displayed. You can set it using {@link #setMinRows(int)} or passed
     * in using constructor such as {@link #AutoResizingTextArea(int,int)}.
     *
     * @return the minimum number of rows that will be displayed.
     */
    public int getMinRows() {
        return _minRows;
    }

    /**
     * Sets the minimum number of rows that will be displayed
     *
     * @param minRows The minimum number of rows.
     */
    public void setMinRows(int minRows) {
        _minRows = minRows;
        setRows(clipRowCount(getRows()));
    }

    private void setupDocument() {
        getDocument().addDocumentListener(new ResizingDocumentListener());
    }

    /**
     * Clips the given row count to fall within the range [m_minRows .. m_maxRows] (inclusive).
     *
     * @param rows The row count to clip.
     * @return a row count clipped to the above range
     */
    private int clipRowCount(int rows) {
        int r = Math.min(_maxRows, rows); // clip to upper bounds
        r = Math.max(_minRows, r); // clip to lower bounds
        return r;
    }

    /**
     * Listens to document change events and updates the row count appropriately.
     */
    private class ResizingDocumentListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            updateSize(e);
        }

        public void removeUpdate(DocumentEvent e) {
            updateSize(e);
        }

        public void changedUpdate(DocumentEvent e) {
            updateSize(e);
        }

    }

    /**
     * Updates the row count depending on the number of lines in the underlying Document object.
     *
     * @param e the <code>DocumentEvent</code>.
     */
    private void updateSize(DocumentEvent e) {

        Element[] roots = e.getDocument().getRootElements();
        Element root = roots[0];

        // ASSUMPTION: each child element of the first root element represents one line of text
        // NB: This may not be valid for document types other than the default.
        int rowCount = root.getElementCount();
        setRows(clipRowCount(rowCount));

    }

    /**
     * Gets the parent scroll pane if any.
     *
     * @return the parent scroll pane. If not found, null will be returned.
     */
    private JScrollPane getParentScrollPane() {
        Component parent = getParent();
        if (parent != null && parent instanceof JViewport) {
            return (JScrollPane) parent.getParent();
        }
        return null;
    }
}
