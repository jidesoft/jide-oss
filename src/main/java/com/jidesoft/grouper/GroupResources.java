/*
 * @(#)GroupResources.java 5/19/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.grouper;

import java.util.Locale;
import java.util.ResourceBundle;

public class GroupResources {
    static final String BASENAME = "com.jidesoft.grouper.group";

    static final ResourceBundle RB = ResourceBundle.getBundle(BASENAME);

    public static ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle(BASENAME, locale);
    }
}
