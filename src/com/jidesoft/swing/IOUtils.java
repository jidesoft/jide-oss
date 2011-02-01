/*
 * @(#)IOUtils.java 1/31/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import java.io.IOException;
import java.io.InputStream;

/**
 * IO utility class to provide some utility methods to read stream.
 */
public class IOUtils {
    public static final int MAX_BUFFER_SIZE = 0x1000000;

    public static boolean isXmlFormat(byte[] byteArray) {
        if (byteArray == null) {
            return false;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < Math.min(byteArray.length, 10); i++) {
            buffer.append((char) byteArray[i]);
        }
        String string = buffer.toString().toLowerCase();
        return string.contains("xml");
    }

    public static byte[] bufferStreamToArray(InputStream stream) throws IOException {
        byte[] maxBuffer = new byte[MAX_BUFFER_SIZE];
        int length = stream.read(maxBuffer, 0, MAX_BUFFER_SIZE);
        if (length < 0 || length >= MAX_BUFFER_SIZE) {
            return new byte[0];
        }
        byte[] arrayToReturn = new byte[length];
        System.arraycopy(maxBuffer, 0, arrayToReturn, 0, length);
        return arrayToReturn;
    }
}
