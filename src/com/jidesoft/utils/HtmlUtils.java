/*
 * @(#)HtmlUtils.java 6/8/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.utils;

public class HtmlUtils {
    public static final String HTML_START = "<html>";
    public static final String HTML_END = "</html>";
    public static final String HTML_LINE_BREAK = "<br>";

    public static String formatHtmlSubString(String text) {
        if (text != null) {
            if (text.toLowerCase().startsWith(HTML_START)) {
                text = text.substring(HTML_START.length());
            }
            if (text.toLowerCase().endsWith(HTML_END)) {
                text = text.substring(0, text.length() - HTML_END.length());
            }
            text = text.replaceAll("\r\n", HTML_LINE_BREAK);
            text = text.replaceAll("\r", HTML_LINE_BREAK);
            text = text.replaceAll("\n", HTML_LINE_BREAK);
            if (text.startsWith("/")) {
                return "&#47;" + text.substring(1);
            }
        }

        return text;
    }
}
