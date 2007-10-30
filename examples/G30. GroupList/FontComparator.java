/*
 * FontComparator.java
 *
 * Created on Oct 16, 2007, 12:17:05 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Font;
import java.util.Comparator;

public class FontComparator implements Comparator<Font> {

    public static final FontComparator INSTANCE = new FontComparator();

    private FontComparator() {
    }

    public int compare(Font f1, Font f2) {
        int diff = f1.getSize() - f2.getSize();
        return f1.getName().compareToIgnoreCase(f2.getName());
    }

}
