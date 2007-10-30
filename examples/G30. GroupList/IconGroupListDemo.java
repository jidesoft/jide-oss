/*
 * @(#)IconGroupListDemo.java 9/7/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.list.DefaultGroupableListModel;
import com.jidesoft.list.GroupList;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.PartialLineBorder;
import com.jidesoft.swing.PartialSide;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class IconGroupListDemo extends AbstractDemo {
    protected GroupList _list;

    public IconGroupListDemo() {
    }

    public String getName() {
        return "GroupList (Icon Demo)";
    }

    public String getProduct() {
        return PRODUCT_NAME_GRIDS;
    }

    public String getDemoFolder() {
        return "G30. GroupList";
    }

    public String getDescription() {
        return "This is a demo of GroupList. GroupList is a JList supporting grouping. In this demo, we provide a list of cells with just icon. The cells can be arranged in four different mode with grouping.\n" +
                "\n" +
                "Demoed classes:\n" +
                "com.jidesoft.list.GroupList\n" +
                "com.jidesoft.list.GroupableListModel";
    }

    public int getAttributes() {
        return ATTRIBUTE_NEW;
    }

    public Component getOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new JideBoxLayout(panel, JideBoxLayout.Y_AXIS, 2));
        JRadioButton v = new JRadioButton("Vertical");
        v.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    _list.setLayoutOrientation(JList.VERTICAL);
                }
            }
        });

        JRadioButton vw = new JRadioButton("Vertical Wrap");
        vw.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    _list.setLayoutOrientation(JList.VERTICAL_WRAP);
                }
            }
        });

        JRadioButton h = new JRadioButton("Horizontal");
        h.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    _list.setLayoutOrientation(GroupList.HORIZONTAL);
                }
            }
        });

        JRadioButton hw = new JRadioButton("Horizontal Wrap");
        hw.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    _list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
                }
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(h);
        group.add(v);
        group.add(hw);
        group.add(vw);

        final JCheckBox hs = new JCheckBox("Allows Selecting Group Cells", false);
        hs.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                _list.setGroupCellSelectable(hs.isSelected());
            }
        });

        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(_list.getPreferredColumnCount(), 1, 50, 1));
        spinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _list.setPreferredColumnCount((Integer) spinner.getValue());
            }
        });

        JPanel spinnerPanel = new JPanel(new BorderLayout());
        spinnerPanel.add(new JLabel("Preferred Column Count: "), BorderLayout.BEFORE_LINE_BEGINS);
        spinnerPanel.add(spinner);

        panel.add(h);
        panel.add(v);
        panel.add(hw);
        panel.add(vw);
        panel.add(Box.createVerticalStrut(4), JideBoxLayout.FIX);
        panel.add(hs);
        panel.add(Box.createVerticalStrut(4), JideBoxLayout.FIX);
        panel.add(JideSwingUtilities.createLeftPanel(spinnerPanel));

        hw.setSelected(true);
        panel.setBorder(BorderFactory.createTitledBorder("Layout Orientation"));

        return panel;
    }

    public JComponent getDemoPanel() {
        _list = new GroupList(createShapeListModel());
//        list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//            public void valueChanged(ListSelectionEvent e) {
//                System.out.println(e);
//                System.out.println(list.getModel().getElementAt(e.getLastIndex()));
//            }
//        });
        configureList(_list);

        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.add(new JScrollPane(_list));

        return panel;
    }

    private void configureList(GroupList list) {
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setCellRenderer(new IconGroupListDemo.IconRenderer());
        list.setSelectionBackground(new Color(252, 236, 166));
        list.setGroupCellRenderer(new GroupCellRenderer());
        list.setPreferredColumnCount(10);
    }


    public static void main(String[] args) {
        LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
        showAsFrame(new IconGroupListDemo());
    }

    private static class IconRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, "", index, isSelected, cellHasFocus);
            label.setIcon((Icon) value);
            return label;
        }

    }

    private static class GroupCellRenderer extends DefaultListCellRenderer {

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

    private DefaultGroupableListModel createShapeListModel() {
        DefaultGroupableListModel listModel = new DefaultGroupableListModel();
        ImageIcon icon = IconsFactory.getImageIcon(IconGroupListDemo.class, "shapes.png");
        int[][] values = {
                {12}, {12, 12, 10}, {12, 12, 3}, {12, 12, 4}, {12, 8}, {12, 4}
        };
        String[] groups = {
                "Lines", "Basic Shapes", "Block Arrows", "Flow Chart", "Callouts", "Stars and Banners"
        };
        int row = 0;
        final int size = 20;
        for (int i = 0; i < values.length; i++) {
            int[] rows = values[i];
            for (int j = 0; j < rows.length; j++) {
                for (int k = 0; k < rows[j]; k++) {
                    listModel.addElement(IconsFactory.getIcon(null, icon, k * size, row * size, size, size));
                    listModel.setGroupAt(groups[i], listModel.getSize() - 1);
                }
                row++;
            }
        }
        return listModel;
    }
}
