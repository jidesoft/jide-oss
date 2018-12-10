/*
 * @(#)FolderToolBarListener.java 10/6/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.basic;

import java.io.File;

/**
 * Interface for listeners to the folder tool bar
 */
interface FolderToolBarListener {
    void deleteFolderButtonClicked();

    void newFolderButtonClicked();

    void myDocumentsButtonClicked();

    void desktopButtonClicked();

    void recentFolderSelected(File file);

    void refreshButtonClicked();
}
