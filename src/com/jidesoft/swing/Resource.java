/*
 * @(#)Resource.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import java.util.Locale;
import java.util.ResourceBundle;

class Resource {
    static final String BASENAME = "com.jidesoft.swing.swing";

    static final ResourceBundle RB = ResourceBundle.getBundle(BASENAME);

    public static ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle(BASENAME, locale);
    }
}
