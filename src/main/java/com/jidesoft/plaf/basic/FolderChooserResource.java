/*
 * @(#)FolderChooserResource.java 11/10/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.plaf.basic;

import java.util.Locale;
import java.util.ResourceBundle;

public class FolderChooserResource {
    static final String BASENAME = "com.jidesoft.plaf.basic.folderChooser";

    static final ResourceBundle RB = ResourceBundle.getBundle(BASENAME);

    public static ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle(BASENAME, locale);
    }
}
