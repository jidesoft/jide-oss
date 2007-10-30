/*
 * GroupCellRenderer.java
 * 
 * Created on 2007-10-20, 11:35:38
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jidesoft.swing.PartialLineBorder;
import com.jidesoft.swing.PartialSide;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

class GroupCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        label.setBackground(isSelected ? list.getSelectionBackground() : new Color(221, 231, 238));
        label.setForeground(new Color(0, 21, 110));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setBorder(BorderFactory.createCompoundBorder(new PartialLineBorder(Color.LIGHT_GRAY, 1, PartialSide.SOUTH),
                BorderFactory.createEmptyBorder(2, 6, 2, 2)));
        return label;
    }
}
