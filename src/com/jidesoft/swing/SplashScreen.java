/*
 * @(#)SplashScreen.java
 *
 * Copyright 2002 JIDE Software. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;

/**
 * A simple splash screen that allows to display company logo,
 * product version information etc during application startup.
 * <pre><code>
 * SplashScreen.create(an ImageIcon);
 * SplashScreen.show(); // to show it
 * ...   // applicate starts up
 * SplashScreen.hide(); // to hide it
 * </code></pre>
 */
public class SplashScreen {

    private static JWindow _splashScreen = null;
    private static JLabel _splashLabel = null;

    /**
     * Create the splash with an image.
     *
     * @param icon
     */
    public static void create(ImageIcon icon) {
        _splashLabel = new JLabel(icon);
        _splashScreen = new JWindow();
        _splashScreen.getContentPane().add(_splashLabel);
        _splashScreen.pack();
        JideSwingUtilities.globalCenterWindow(_splashScreen);
    }

    /**
     * Show the splash screen.
     */
    public static void show() {
        if (_splashScreen != null)
            _splashScreen.setVisible(true);
    }

    /**
     * Hide the spash screen.
     */
    public static void hide() {
        if (_splashScreen != null) {
            _splashScreen.setVisible(false);
            _splashScreen.dispose();
            _splashScreen = null;
            _splashLabel = null;
        }
    }

}
