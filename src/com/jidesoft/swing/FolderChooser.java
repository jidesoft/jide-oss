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
import java.util.List;

/**
 * <code>FolderChooser</code> provides a simple mechanism for the user to
 * choose a folder.
 * <p/>
 * In addition to supporting the basic folder choosing function, it also supports create new folder, delete an existing
 * folder. Another useful feature is recent list. It allows you to set a list of recent selected folders so that user
 * can choose them directly inatead of navigating to it in the file system tree.
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

    private List _recentList;

    public final static String PROPERTY_RECENTLIST = "recentList";

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
    public List getRecentList() {
        return _recentList;
    }

    /**
     * Sets the recent folder list. The element in the list should be {@link File}.
     * Property change event on {@link FolderChooser#PROPERTY_RECENTLIST} will be fired when recent folder list is changed.
     *
     * @param recentList the recent folder list.
     */
    public void setRecentList(List recentList) {
        List old = _recentList;
        _recentList = recentList;
        firePropertyChange(PROPERTY_RECENTLIST, old, recentList);
    }

    /**
     * Resets the UI property to a value from the current look and
     * feel.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        if (UIDefaultsLookup.get(uiClassID) == null) {
            LookAndFeelFactory.installJideExtension();
        }
        setUI(UIManager.getUI(this));
    }

    /**
     * Returns a string that specifies the name of the L&F class
     * that renders this component.
     *
     * @return the string "FolderChooserUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
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
//     * @param dir
//     */
//    public void setCurrentDirectory(File dir) {
//        super.setSelectedFile(dir);
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
}
