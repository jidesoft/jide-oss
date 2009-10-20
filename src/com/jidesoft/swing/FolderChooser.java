/*
 * @(#)FolderChooser.java 10/9/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>FolderChooser</code> provides a simple mechanism for the user to choose a folder.
 * <p/>
 * In addition to supporting the basic folder choosing function, it also supports create new folder, delete an existing
 * folder. Another useful feature is recent list. It allows you to set a list of recent selected folders so that user
 * can choose them directly instead of navigating to it in the file system tree.
 * <p/>
 * The following code pops up a folder chooser for user to choose a folder.
 * <pre>
 *    FolderChooser chooser = new FolderChooser();
 *    int returnVal = chooser.showOpenDialog(parent);
 *    if(returnVal == FolderChooser.APPROVE_OPTION) {
 *       System.out.println("You chose to open this file: " +
 *            chooser.getSelectedFile().getName());
 *    }
 * </pre>
 */
public class FolderChooser extends JFileChooser {

    private static final String uiClassID = "FolderChooserUI";

    private List<String> _recentList;

    public static final String PROPERTY_RECENTLIST = "recentList";
    public static final String PROPERTY_RECENTLIST_VISIBLE = "recentListVisible";

    public static final int BUTTON_ALL = 0xFFFFFFFF;
    public static final int BUTTON_DELETE = 0x1;
    public static final int BUTTON_NEW = 0x2;
    public static final int BUTTON_REFRESH = 0x4;
    public static final int BUTTON_DESKTOP = 0x8;
    public static final int BUTTON_MY_DOCUMENTS = 0x10;

    /**
     * Property for <code>_availableButtons</code>.
     *
     * @see #setAvailableButtons(int)
     */
    public static final String PROPERTY_AVAILABLE_BUTTONS = "availableButtons";
    private int _availableButtons = BUTTON_ALL;
    private boolean _recentListVisible = true;

    public FolderChooser() {
    }

    public FolderChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
    }

    public FolderChooser(File currentDirectory) {
        super(currentDirectory);
    }

    public FolderChooser(FileSystemView fsv) {
        super(fsv);
    }

    public FolderChooser(File currentDirectory, FileSystemView fsv) {
        super(currentDirectory, fsv);
    }

    public FolderChooser(String currentDirectoryPath, FileSystemView fsv) {
        super(currentDirectoryPath, fsv);
    }

    /**
     * Gets recent selected folder list. The element in the list is {@link File}.
     *
     * @return the recent selected folder list.
     */
    public List<String> getRecentList() {
        return _recentList;
    }

    /**
     * Sets the recent folder list. The element in the list should be {@link File}. Property change event on {@link
     * FolderChooser#PROPERTY_RECENTLIST} will be fired when recent folder list is changed.
     *
     * @param recentList the recent folder list.
     */
    public void setRecentList(List<String> recentList) {
        List<String> old = _recentList;
        _recentList = new ArrayList<String>();
        _recentList.addAll(recentList);
        firePropertyChange(PROPERTY_RECENTLIST, old, _recentList);
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
        JComponent c = getAccessory();
        if (c != null) {
            setAccessory(null);
        }
        setUI(UIManager.getUI(this));
        if (c != null) {
            setAccessory(c);
        }
    }

    /**
     * Returns a string that specifies the name of the L&F class that renders this component.
     *
     * @return the string "FolderChooserUI"
     *
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

// we have to remove these two overridden method because it causes problem in
// JFileChooser's setSelectedFile method where setCurrentDirectory
// is called with selected file's parent folder.

//    /**
//     * Current directory concept doesn't make sense in the case of FolderChooser. So we
//     * override this method of JFileChooser and delegate to {@link #setSelectedFile(java.io.File)}.
//     *
//     * @param directory
//     */
//    public void setCurrentDirectory(File directory) {
//        super.setSelectedFile(directory);
//    }
//
//    /**
//     * Current directory concept doesn't make sense in the case of FolderChooser. So we
//     * override this method of JFileChooser and delegate to {@link #getSelectedFile()}.
//     *
//     * @return the selected folder.
//     */
//    public File getCurrentDirectory() {
//        return super.getSelectedFile();
//    }

    /*
     * Added on 05/11/2008 in response to http://www.jidesoft.com/forum/viewtopic.php?p=26932#26932
     *
     * The addition below ensures Component#firePropertyChange is called, and thus fires the
     * appropriate 'bound property event' on all folder selection changes.
     *
     * @see BasicFolderChooserUI.FolderChooserSelectionListener#valueChanged
     */

    /**
     * Represents the highlighted folder in the 'folder tree' in the UI.
     *
     * @see #getSelectedFolder
     * @see #setSelectedFolder
     */
    private File _selectedFolder;

    /**
     * Returns the selected folder. This can be set either by the programmer via <code>setSelectedFolder</code> or by a
     * user action, such as selecting the folder from a 'folder tree' in the UI.
     *
     * @return the selected folder in the <i>folder tree<i>
     *
     * @see #setSelectedFolder
     */
    public File getSelectedFolder() {
        return _selectedFolder;
    }

    /**
     * Sets the selected folder.<p> </p> Property change event {@link JFileChooser#SELECTED_FILE_CHANGED_PROPERTY} will
     * be fired when a new folder is selected.
     *
     * @param selectedFolder the selected folder
     * @see #getSelectedFolder
     */
    public void setSelectedFolder(File selectedFolder) {
        File old = _selectedFolder;
        if (!JideSwingUtilities.equals(old, selectedFolder)) {
            _selectedFolder = selectedFolder;
            firePropertyChange(SELECTED_FILE_CHANGED_PROPERTY, old, _selectedFolder);
        }
    }

    /*
    * End of addition.
    *
    * Added on 05/11/2008 in response to http://www.jidesoft.com/forum/viewtopic.php?p=26932#26932
    */

    /*
    * Added on 05/27/2008 in response to http://www.jidesoft.com/forum/viewtopic.php?p=22885#22885
    *
    * The addition below allows an optional text field and "Go" button to be displayed on the folderChooser.
    * The user can type a path name into the field, and after hitting <Enter> or pressing the "Go" button,
    * the FolderChooser navigates to the specified folder in the tree (the folder viewer).
    */

    /**
     * Bound property for <code>_navigationFieldVisible</code>.
     *
     * @see #setNavigationFieldVisible
     */
    public static final String PROPERTY_NAVIGATION_FIELD_VISIBLE = "navigationFieldVisible";

    /**
     * Indicates whether the navigation text field is visible.
     *
     * @see #setNavigationFieldVisible
     * @see #isNavigationFieldVisible
     */
    private boolean _navigationFieldVisible;

    /**
     * Sets the navigation text fields visibility.
     *
     * @param navigationFieldVisible if true, the navigation text field is displayed; otherwise it is hidden.
     */
    public void setNavigationFieldVisible(boolean navigationFieldVisible) {
        boolean oldValue = _navigationFieldVisible;
        if (!JideSwingUtilities.equals(oldValue, navigationFieldVisible)) {
            _navigationFieldVisible = navigationFieldVisible;
            firePropertyChange(PROPERTY_NAVIGATION_FIELD_VISIBLE, oldValue, _navigationFieldVisible);
        }
    }

    /**
     * Determines whether the navigation text field is visible.
     *
     * @return true if the navigation text field is visible; otherwise false.
     */
    public boolean isNavigationFieldVisible() {
        return _navigationFieldVisible;
    }

    /**
     * Get the visibilities of each buttons on the title bar of dockable frame.
     *
     * @return the visibilities of each buttons. It's a bit wise OR of values specified at BUTTON_XXX.
     */
    public int getAvailableButtons() {
        return _availableButtons;
    }

    /**
     * Set the visibilities of each buttons on the title bar of dockable frame.
     *
     * @param availableButtons the visibilities of each buttons. It's a bit wise OR of values specified at BUTTON_XXX.
     */
    public void setAvailableButtons(int availableButtons) {
        if (getAvailableButtons() == availableButtons) {
            return;
        }
        int oldValue = getAvailableButtons();
        _availableButtons = availableButtons;
        firePropertyChange(PROPERTY_AVAILABLE_BUTTONS, oldValue, availableButtons);
    }

    /*
    * End of addition.
    *
    * Added on 05/27/2008 in response to http://www.jidesoft.com/forum/viewtopic.php?p=22885#22885
    */

    /**
     * Get the visibility of the recent list combobox.
     *
     * @return the visibility of the combobox.
     */
    public boolean isRecentListVisible() {
        return _recentListVisible;
    }

    /**
     * Set the visibility of the recent list combobox.
     *
     * @param recentListVisible the visibility of the combobox
     */
    public void setRecentListVisible(boolean recentListVisible) {
        if (_recentListVisible == recentListVisible) {
            return;
        }
        boolean oldValue = isRecentListVisible();
        _recentListVisible = recentListVisible;
        firePropertyChange(PROPERTY_RECENTLIST_VISIBLE, oldValue, recentListVisible);
    }
}
