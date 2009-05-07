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

import java.util.Comparator;

/**
 * This is an updated version from the same named class from http://www.davekoelle.com/alphanum.html. The enhancement
 * JIDE did to this class is <ul> <li>1. Use JDK 5 generic to support CharSequence <li>2. Add support to case
 * insensitive comparison. </ul>
 */
public class AlphanumComparator implements Comparator<CharSequence> {
    private boolean _caseSensitive;

    public static final ComparatorContext CONTEXT = new ComparatorContext("Alphanum");
    public static final ComparatorContext CONTEXT_IGNORE_CASE = new ComparatorContext("Alphanum_Ignorecase");


    /**
     * Creates a case sensitive comparator to use the alphanum algorithm to compare the strings.
     */
    public AlphanumComparator() {
        this(true);
    }

    /**
     * Creates a comparator to use the alphanum algorithm to compare the strings.
     *
     * @param caseSensitive true or false.
     */
    public AlphanumComparator(boolean caseSensitive) {
        _caseSensitive = caseSensitive;
    }

    private boolean isDigit(char ch) {
        return ch >= 48 && ch <= 57;
    }

    /**
     * Length of string is passed in for improved efficiency (only need to calculate it once) *
     */
    @SuppressWarnings({"JavaDoc"})
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

    public int compare(CharSequence s1, CharSequence s2) {
        int thisMarker = 0;
        int thatMarker = 0;
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

//    /**
//     * * sort a set of string from stdin
//     */
//    public static void main(String[] args) {
//        try {
//            Vector<String> v = new Vector<String>();
//            for (int i = 0; i < 1; i++) {
//                v.add("a1");
//                v.add("a2");
//                v.add("a10");
//                v.add("a1c1");
//                v.add("a1c2");
//                v.add("a1c10");
//                v.add("a1c20");
//                v.add("a100");
//                v.add("b3");
//                v.add("c3");
//                v.add("cc3");
//                v.add("cc2");
//                v.add("1.doc");
//                v.add("2.doc");
//                v.add("3.doc");
//                v.add("4.doc");
//                v.add("5.doc");
//                v.add("6.doc");
//                v.add("7.doc");
//                v.add("8.doc");
//                v.add("9.doc");
//                v.add("10.doc");
//                v.add("11.doc");
//                v.add("12.doc");
//                v.add("13.doc");
//                v.add("14.doc");
//                v.add("15.doc");
//                v.add("16.doc");
//                v.add("17.doc");
//                v.add("18.doc");
//                v.add("19.doc");
//                v.add("20.doc");
//                v.add("100.doc");
//                v.add("101.doc");
//                v.add("102.doc");
//            }
//            long start = System.nanoTime();
//            Collections.sort(v, new AlphanumComparator());
//            long end = System.nanoTime();
//            System.out.println(end - start);
//            for (String s : v) System.out.println(s);
//        }
//        catch (Throwable error) {
//            error.printStackTrace();
//            System.exit(-1);
//        }
//    }
}
