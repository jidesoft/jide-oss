/*
 * @(#)FolderToolBar.java 10/6/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.basic;

import com.jidesoft.swing.FolderChooser;
import com.jidesoft.utils.SystemInfo;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Manages the optional folder toolbar that sits above the folder view's tree view panel
 */
public class FolderToolBar extends JToolBar {
    private static final FileSystemView _fsv = FileSystemView.getFileSystemView();

    private JButton _deleteFolderBtn;
    private JButton _newFolderBtn;
    private JButton _refreshBtn;
    private JButton _desktopBtn;
    private JButton _myDocumentsBtn;
    private JComboBox _recentFoldersList;

    private List<FolderToolBarListener> _listeners = new ArrayList<FolderToolBarListener>(1);

    public static final String DELETE_BUTTON_NAME = "FolderChooser.toolbar.delete";
    public static final String NEW_BUTTON_NAME = "FolderChooser.toolbar.new";
    public static final String REFRESH_BUTTON_NAME = "FolderChooser.toolbar.refresh";
    public static final String DESKTOP_BUTTON_NAME = "FolderChooser.toolbar.desktop";
    public static final String MY_DOCUMENTS_BUTTON_NAME = "FolderChooser.toolbar.mydocuments";

    /**
     * Constructor to create the FolderToolBar.
     *
     * @param showRecentFolders the flag indicating if the recent folders should be shown
     * @param recentFoldersList the recent folders list if the flag is true
     */
    public FolderToolBar(boolean showRecentFolders, List<String> recentFoldersList) {
        setFloatable(false);
        setupToolBar(showRecentFolders, recentFoldersList);
    }

    /**
     * Enable the delete button.
     */
    public void enableDelete() {
        _deleteFolderBtn.setEnabled(true);
    }

    /**
     * Disable the delete button.
     */
    public void disableDelete() {
        _deleteFolderBtn.setEnabled(false);
    }

    /**
     * Enable the new folder button.
     */
    public void enableNewFolder() {
        _newFolderBtn.setEnabled(true);
    }

    /**
     * Disable the new folder button.
     */
    public void disableNewFolder() {
        _newFolderBtn.setEnabled(false);
    }

    /**
     * Get the button instance from the name.
     *
     * @param buttonName the name of the button
     * @return the JButton instance. null if not found.
     */
    public JButton getButton(String buttonName) {
        if (buttonName == null) {
            return null;
        }
        if (buttonName.equals(DELETE_BUTTON_NAME)) {
            return _deleteFolderBtn;
        }
        if (buttonName.equals(NEW_BUTTON_NAME)) {
            return _newFolderBtn;
        }
        if (buttonName.equals(REFRESH_BUTTON_NAME)) {
            return _refreshBtn;
        }
        if (buttonName.equals(DESKTOP_BUTTON_NAME)) {
            return _desktopBtn;
        }
        if (buttonName.equals(MY_DOCUMENTS_BUTTON_NAME)) {
            return _myDocumentsBtn;
        }
        return null;
    }

    /*
     * Creates the toolbar buttons and dropdown
     *
     * @param showRecentFolders the flag if show recent folders
     * @param recentFoldersList the recent folders list
     */

    private void setupToolBar(boolean showRecentFolders, List<String> recentFoldersList) {

        // add to toolbar
        if (showRecentFolders) {
            _recentFoldersList = new JComboBox(new DefaultComboBoxModel());
            if (recentFoldersList != null && recentFoldersList.size() > 0) {
                _recentFoldersList.setModel(new DefaultComboBoxModel((recentFoldersList.toArray())));
            }
            _recentFoldersList.setEditable(false);
            _recentFoldersList.setRenderer(new FileListCellRenderer(_recentFoldersList.getRenderer()));
            _recentFoldersList.addPopupMenuListener(new PopupMenuListener() {
                private boolean m_wasCancelled = false;

                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    m_wasCancelled = false;
                }

                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    if (e.getSource() instanceof JComboBox) {
                        JComboBox box = (JComboBox) e.getSource();

                        Object selectedFile = box.getModel().getSelectedItem();
                        // if popup was not cancelled then select the folder
                        if (!m_wasCancelled && selectedFile != null) {
//                            System.out.println("User selected file: " + selectedFile.getAbsolutePath());
                            if (selectedFile instanceof File) {
                                recentFolderSelected((File) selectedFile);
                            }
                            else {
                                recentFolderSelected(new File("" + selectedFile));
                            }
                        }
                    }
                }

                public void popupMenuCanceled(PopupMenuEvent e) {
                    m_wasCancelled = true;
                }
            });
            _recentFoldersList.setPrototypeDisplayValue("AAAAAAAAAAAAAAAAAA");
            final ResourceBundle resourceBundle = FolderChooserResource.getResourceBundle(Locale.getDefault());
            add(new JLabel(resourceBundle.getString("FolderChooser.toolbar.recent")));
            add(_recentFoldersList);
        }
        else {
            add(Box.createHorizontalGlue());
        }

        _desktopBtn = new NoFocusButton(new ToolBarAction(null,
                SystemInfo.isWindows() ? _fsv.getSystemIcon(_fsv.getHomeDirectory()) : BasicFolderChooserIconsFactory.getImageIcon(BasicFolderChooserIconsFactory.ToolBar.HOME)) {
            public void actionPerformed(ActionEvent e) {
                desktopButtonClicked();
            }
        });

        final ResourceBundle resourceBundle = FolderChooserResource.getResourceBundle(Locale.getDefault());
        _desktopBtn.setToolTipText(SystemInfo.isWindows() ? resourceBundle.getString(DESKTOP_BUTTON_NAME) : resourceBundle.getString("FolderChooser.toolbar.home"));
        _desktopBtn.setName(DESKTOP_BUTTON_NAME);
        add(_desktopBtn);

        if (SystemInfo.isWindows()) {
            _myDocumentsBtn = new NoFocusButton(new ToolBarAction(null, _fsv.getSystemIcon(_fsv.getDefaultDirectory())) {
                public void actionPerformed(ActionEvent e) {
                    myDocumentsButtonClicked();
                }
            });
            _myDocumentsBtn.setToolTipText(resourceBundle.getString(MY_DOCUMENTS_BUTTON_NAME));
            _myDocumentsBtn.setName(MY_DOCUMENTS_BUTTON_NAME);
            add(_myDocumentsBtn);
        }
        // dredge up appropriate icons
        Icon deleteIcon = BasicFolderChooserIconsFactory.getImageIcon(BasicFolderChooserIconsFactory.ToolBar.DELETE);

        _deleteFolderBtn = new NoFocusButton(new ToolBarAction(null, deleteIcon) {
            public void actionPerformed(ActionEvent e) {
                deleteFolderButtonClicked();
            }
        });

        _deleteFolderBtn.setToolTipText(resourceBundle.getString(DELETE_BUTTON_NAME));
        _deleteFolderBtn.setName(DELETE_BUTTON_NAME);

        Icon newFolderIcon = BasicFolderChooserIconsFactory.getImageIcon(BasicFolderChooserIconsFactory.ToolBar.NEW);
        _newFolderBtn = new NoFocusButton(new ToolBarAction(null, newFolderIcon) {
            public void actionPerformed(ActionEvent e) {
                newFolderButtonClicked();
            }
        });

        _newFolderBtn.setToolTipText(resourceBundle.getString(NEW_BUTTON_NAME));
        _newFolderBtn.setName(NEW_BUTTON_NAME);

        Icon refreshIcon = BasicFolderChooserIconsFactory.getImageIcon(BasicFolderChooserIconsFactory.ToolBar.REFRESH);
        _refreshBtn = new NoFocusButton(new ToolBarAction(null, refreshIcon) {
            public void actionPerformed(ActionEvent e) {
                refreshButtonClicked();
            }
        });

        _refreshBtn.setToolTipText(resourceBundle.getString(REFRESH_BUTTON_NAME));
        _refreshBtn.setName(REFRESH_BUTTON_NAME);

        add(_deleteFolderBtn);
        add(_newFolderBtn);
        add(_refreshBtn);
    }

    boolean isButtonVisible(String buttonName, int availableButtons) {
        if (DELETE_BUTTON_NAME.equals(buttonName)) {
            return (availableButtons & FolderChooser.BUTTON_DELETE) != 0;
        }
        else if (NEW_BUTTON_NAME.equals(buttonName)) {
            return (availableButtons & FolderChooser.BUTTON_NEW) != 0;
        }
        else if (REFRESH_BUTTON_NAME.equals(buttonName)) {
            return (availableButtons & FolderChooser.BUTTON_REFRESH) != 0;
        }
        else if (DESKTOP_BUTTON_NAME.equals(buttonName)) {
            return (availableButtons & FolderChooser.BUTTON_DESKTOP) != 0;
        }
        else if (MY_DOCUMENTS_BUTTON_NAME.equals(buttonName)) {
            return (availableButtons & FolderChooser.BUTTON_MY_DOCUMENTS) != 0;
        }
        return true;
    }


    private class FileListCellRenderer implements ListCellRenderer {
        protected ListCellRenderer _delegateRenderer;

        private FileListCellRenderer(ListCellRenderer delegateRenderer) {
            _delegateRenderer = delegateRenderer;
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel renderer = (JLabel) _delegateRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            File f = null;
            if (value instanceof File) {
                f = (File) value;
            }
            else if (value != null) {
                f = new File(value.toString());
            }

            if (f != null && f.exists()) {
                String text = _fsv.getSystemDisplayName(f);
                Icon icon = _fsv.getSystemIcon(f);
                renderer.setIcon(icon);
                renderer.setText(text);
                renderer.setToolTipText(f.getAbsolutePath());
            }
            else {
                String filePath = value == null ? "" : value.toString();
                renderer.setText(filePath);
                renderer.setToolTipText(filePath);
            }
            return renderer;
        }
    }

    // ----------------------------------------------------------------
    // Listener methods
    // ----------------------------------------------------------------

    /**
     * Add the FolderToolBarListener.
     *
     * @param listener the listener
     */
    public void addListener(FolderToolBarListener listener) {
        _listeners.add(listener);
    }

    /**
     * Remove the FolderToolBarListener.
     *
     * @param listener the listener
     */
    public void removeListener(FolderToolBarListener listener) {
        _listeners.remove(listener);
    }

    /**
     * Clear all FolderToolBarListener's.
     */
    public void clearListeners() {
        _listeners.clear();
    }

    private void deleteFolderButtonClicked() {
        for (FolderToolBarListener listener : _listeners) {
            listener.deleteFolderButtonClicked();
        }
    }

    private void newFolderButtonClicked() {
        for (FolderToolBarListener listener : _listeners) {
            listener.newFolderButtonClicked();
        }
    }

    private void refreshButtonClicked() {
        for (FolderToolBarListener listener : _listeners) {
            listener.refreshButtonClicked();
        }
    }

    private void myDocumentsButtonClicked() {
        for (FolderToolBarListener listener : _listeners) {
            listener.myDocumentsButtonClicked();
        }
    }

    private void desktopButtonClicked() {
        for (FolderToolBarListener listener : _listeners) {
            listener.desktopButtonClicked();
        }
    }

    private void recentFolderSelected(File recentFolder) {
        for (FolderToolBarListener listener : _listeners) {
            listener.recentFolderSelected(recentFolder);
        }
    }

    /**
     * Set the recent folders list.
     *
     * @param recentFoldersList the recent folders list
     */
    public void setRecentList(List<String> recentFoldersList) {
        if (recentFoldersList != null) {
            _recentFoldersList.setModel(new DefaultComboBoxModel((recentFoldersList.toArray())));
        }
    }

    private abstract class ToolBarAction extends AbstractAction {
        public ToolBarAction(String name, Icon icon) {
            super(name, icon);
        }
    }

    static class NoFocusButton extends JButton {
        public NoFocusButton(Action a) {
            super(a);
            setRequestFocusEnabled(false);
            setFocusable(false);

            // on jdk1.6, the button size is wrong
            Insets margin = getMargin();
            margin.left = margin.top;
            margin.right = margin.bottom;
            setMargin(margin);
        }
    }
}
