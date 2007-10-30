/*
 * FontRenderer.java
 * 
 * Created on 2007-10-20, 11:34:13
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Component;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

class FontRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        String fontName;
        if (value instanceof Font) {
            fontName = ((Font) value).getName();
        }
        else {
            fontName = "" + value;
        }
        Component cellRendererComponent = super.getListCellRendererComponent(
                list, fontName, index, isSelected, cellHasFocus);
        if (value instanceof Font) {
            cellRendererComponent.setFont(((Font) value).deriveFont(14f));
        }
        return cellRendererComponent;
    }

}