/*
 * @(#)FileIntelliHints.java 7/24/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.hints;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;

/**
 * <code>FileIntelliHints</code> is a concrete implementation of {@link com.jidesoft.hints.IntelliHints}.
 * It allows user to type in a file patch quickly by providing them the hints based on what
 * is existed on file system. You can use {@link #setFolderOnly(boolean)} to control if
 * the hints contain only the folders, or folders and files.
 */
public class FileIntelliHints extends AbstractListIntelliHints {
    private boolean _folderOnly = false;
    private boolean _showFullPath = true;
    private FilenameFilter _filter;

    public FileIntelliHints(JTextComponent comp) {
        super(comp);
    }

    /**
     * If the hints contain the folder names only.
     *
     * @return true if the hints contain the folder names only.
     */
    public boolean isFolderOnly() {
        return _folderOnly;
    }

    /**
     * Sets the property of folder only. If true, the hints will only show the folder names.
     * Otherwise, both folder and file names will be shown in the hints.
     *
     * @param folderOnly only provide hints for the folders.
     */
    public void setFolderOnly(boolean folderOnly) {
        _folderOnly = folderOnly;
    }

    /**
     * If the hints contain the full path.
     *
     * @return true if the hints contain the full path.
     */
    public boolean isShowFullPath() {
        return _showFullPath;
    }

    /**
     * Sets the property of showing full path. If true, the hints will show the full path.
     * Otherwise, it will only show the path after user typed in so far.
     *
     * @param showFullPath whether show the full path.
     */
    public void setShowFullPath(boolean showFullPath) {
        _showFullPath = showFullPath;
    }

    public boolean updateHints(Object value) {
        if (value == null) {
            return false;
        }
        String s = value.toString();
        if (s.length() == 0) {
            return false;
        }
        int index1 = s.lastIndexOf('\\');
        int index2 = s.lastIndexOf('/');
        int index = Math.max(index1, index2);
        if (index == -1)
            return false;
        String dir = s.substring(0, index + 1);
        final String prefix = index == s.length() - 1 ? null : s.substring(index + 1).toLowerCase();
        String[] files = new File(dir).list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (isFolderOnly()) {
                    if (new File(dir.getAbsolutePath() + File.separator + name).isFile()) {
                        return false;
                    }
                }
                boolean result = prefix == null || name.toLowerCase().startsWith(prefix);
                if (result && getFilter() != null) {
                    return getFilter().accept(dir, name);
                }
                return result;
            }
        });

        if (files == null || files.length == 0 || (files.length == 1 && files[0].equalsIgnoreCase(prefix))) {
            setListData(new String[0]);
            return false;
        }
        else {
            getList().setCellRenderer(new PrefixListCellRenderer(isShowFullPath() ? dir : ""));
            setListData(files);
            return true;
        }
    }

    @Override
    public void acceptHint(Object selected) {
        if (selected == null)
            return;

        String selectedValue = "" + selected;

        String value = getTextComponent().getText();
        int caretPosition = getTextComponent().getCaretPosition();
        int index1 = value.lastIndexOf('\\', caretPosition);
        int index2 = value.lastIndexOf('/', caretPosition);
        int index = Math.max(index1, index2);
        if (index == -1) {
            return;
        }
        int prefixlen = caretPosition - index - 1;
        try {
            getTextComponent().getDocument().insertString(caretPosition, selectedValue.substring(prefixlen), null);
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get FilenameFilter configured to this hints.
     * <p/>
     * By default, it returns null. You could set this field to let the IntelliHints only show the files meet your criteria.
     *
     * @return the FilenameFilter in use.
     */
    public FilenameFilter getFilter() {
        return _filter;
    }

    /**
     * Set FilenameFilter to this hints.
     *
     * @see #getFilter()
     * @param filter the FilenameFilter in use.
     */
    public void setFilter(FilenameFilter filter) {
        _filter = filter;
    }

    private class PrefixListCellRenderer extends DefaultListCellRenderer {
        private String _prefix;

        public PrefixListCellRenderer(String prefix) {
            _prefix = prefix;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            return super.getListCellRendererComponent(list, _prefix + value, index, isSelected, cellHasFocus);
        }
    }
}