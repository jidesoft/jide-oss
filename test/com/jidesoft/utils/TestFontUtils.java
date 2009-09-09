/*
 * @(#)TestFontUtils.java 9/9/2009
 *
 * Copyright 2002 - 2009 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.utils;

import com.jidesoft.swing.FontUtils;
import junit.framework.TestCase;

import javax.swing.*;
import java.awt.*;

public class TestFontUtils extends TestCase {
    public void testAddFont() {
        Font font = UIManager.getFont("Label.font");
        for (int i = 0; i < 100; i++) {
            FontUtils.getCachedDerivedFont(font, Font.BOLD, 4 + i);
        }
        assertEquals(100, FontUtils.getDerivedFontCacheSize());
        try {
            byte[] block = new byte[200 * 1024 * 1024];
        }
        catch (OutOfMemoryError ex) {
            // ignore
        }
        assertEquals(0, FontUtils.getDerivedFontCacheSize());
    }
}
