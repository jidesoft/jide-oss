package com.jidesoft.dialog;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Resource class for defines commonly used buttons
 */
public class ButtonResources {
    static final String BASENAME = "com.jidesoft.dialog.buttons";

    static final ResourceBundle RB = ResourceBundle.getBundle(BASENAME);

    public static ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle(BASENAME, locale);
    }
}
