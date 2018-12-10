/*
 * @(#)OverlayableIconsFactory.java 8/10/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import com.jidesoft.icons.IconsFactory;

import javax.swing.*;

/**
 * A helper class to contain icons for the overlayable components
 * Those icons are copyrighted by JIDE Software, Inc.
 */
public class OverlayableIconsFactory {
    public static final String ATTENTION = "icons/overlay_attention.png";
    public static final String CORRECT = "icons/overlay_correct.png";
    public static final String ERROR = "icons/overlay_error.png";
    public static final String INFO = "icons/overlay_info.png";
    public static final String QUESTION = "icons/overlay_question.png";

    public static ImageIcon getImageIcon(String name) {
        if (name != null)
            return IconsFactory.getImageIcon(OverlayableIconsFactory.class, name);
        else
            return null;
    }

    public static void main(String[] argv) {
        IconsFactory.generateHTML(OverlayableIconsFactory.class);
    }


}
