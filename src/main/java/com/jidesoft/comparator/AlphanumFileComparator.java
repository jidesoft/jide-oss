/*
 * @(#)AlphanumFileComparator.java 2/20/2013
 *
 * Copyright 2002 - 2013 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.comparator;

/*
 * The Alphanum Algorithm is an improved sorting algorithm for strings
 * containing numbers.  Instead of sorting numbers in ASCII order like
 * a standard sort, this algorithm sorts numbers in numeric order.
 *
 * The Alphanum Algorithm is discussed at http://www.DaveKoelle.com
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

import java.io.File;
import java.util.Comparator;

/**
 * A File comparator to compare the file name based on alphanum value. This class is copied from
 * <code>AlphanumComparator</code> except changing it to use File instead of CharSequence.
 */
public class AlphanumFileComparator implements Comparator<File> {
    private boolean _caseSensitive;

    public static final ComparatorContext CONTEXT = new ComparatorContext("Alphanum");
    public static final ComparatorContext CONTEXT_IGNORE_CASE = new ComparatorContext("Alphanum_Ignorecase");


    /**
     * Creates a case sensitive comparator to use the alphanum algorithm to compare the strings.
     */
    public AlphanumFileComparator() {
        this(true);
    }

    /**
     * Creates a comparator to use the alphanum algorithm to compare the strings.
     *
     * @param caseSensitive true or false.
     */
    public AlphanumFileComparator(boolean caseSensitive) {
        _caseSensitive = caseSensitive;
    }

    private boolean isDigit(char ch) {
        return ch >= 48 && ch <= 57;
    }

    /*
     * Length of string is passed in for improved efficiency (only need to calculate it once) *
     */
    private String getChunk(CharSequence s, int slength, int marker) {
        StringBuilder chunk = new StringBuilder();
        char c = s.charAt(marker);
        chunk.append(c);
        marker++;
        if (isDigit(c)) {
            while (marker < slength) {
                c = s.charAt(marker);
                if (!isDigit(c))
                    break;
                chunk.append(c);
                marker++;
            }
        }
        else {
            while (marker < slength) {
                c = s.charAt(marker);
                if (isDigit(c))
                    break;
                chunk.append(c);
                marker++;
            }
        }
        return chunk.toString();
    }

    public int compare(File f1, File f2) {
        int thisMarker = 0;
        int thatMarker = 0;
        String s1 = f1.getName();
        String s2 = f2.getName();
        int s1Length = s1.length();
        int s2Length = s2.length();

        while (thisMarker < s1Length && thatMarker < s2Length) {
            String thisChunk = getChunk(s1, s1Length, thisMarker);
            thisMarker += thisChunk.length();

            String thatChunk = getChunk(s2, s2Length, thatMarker);
            thatMarker += thatChunk.length();

            // If both chunks contain numeric characters, sort them numerically
            int result;
            if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
                // Simple chunk comparison by length.
                int thisChunkLength = thisChunk.length();
                result = thisChunkLength - thatChunk.length();
                // If equal, the first different number counts
                if (result == 0) {
                    for (int i = 0; i < thisChunkLength; i++) {
                        result = thisChunk.charAt(i) - thatChunk.charAt(i);
                        if (result != 0) {
                            return result;
                        }
                    }
                }
            }
            else {
                result = isCaseSensitive() ? thisChunk.compareTo(thatChunk) : thisChunk.compareToIgnoreCase(thatChunk);
            }

            if (result != 0)
                return result;
        }

        return s1Length - s2Length;
    }

    /**
     * Checks if the case is sensitive when comparing.
     *
     * @return true if the comparator is case sensitive.
     */
    public boolean isCaseSensitive() {
        return _caseSensitive;
    }

    /**
     * Sets the case sensitive flag. By default, it's true meaning the comparator is case sensitive.
     *
     * @param caseSensitive true or false.
     */
    public void setCaseSensitive(boolean caseSensitive) {
        _caseSensitive = caseSensitive;
    }
}
