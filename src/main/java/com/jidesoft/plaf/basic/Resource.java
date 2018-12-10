package com.jidesoft.plaf.basic;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 */
public class Resource {
    static final String BASENAME = "com.jidesoft.plaf.basic.basic";

    static final ResourceBundle RB = ResourceBundle.getBundle(BASENAME);

    public static ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle(BASENAME, locale);
    }
}
