/*
 * SortFontGroupListDemo.java
 *
 * Created on Oct 16, 2007, 11:48:16 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jidesoft.list.GroupList;
import com.jidesoft.list.ListModelWrapper;
import com.jidesoft.list.SortableGroupableListModel;
import com.jidesoft.list.SortableListModel;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideTitledBorder;
import com.jidesoft.swing.PartialEtchedBorder;
import com.jidesoft.swing.PartialSide;
import com.jidesoft.swing.SearchableUtils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListModel;

public class SortFontGroupListDemo extends AbstractDemo {

    private FontModel model;
    private SortableGroupableListModel sortableListModel;

    public SortFontGroupListDemo() {
    }

    public String getName() {
        return "GroupList (Font Demo)";
    }

    public String getProduct() {
        return PRODUCT_NAME_GRIDS;
    }

    @Override
    public String getDemoFolder() {
        return "G30. GroupList";
    }

    @Override
    public String getDescription() {
        return "This is a demo of GroupList. GroupList is a JList supporting grouping. In this demo, we add \"Recently Used Fonts\" group to a regular JList. You can double click on a cell to put that cell into the \"Recently Used Fonts\" group.\n" +
                "\n" +
                "Demoed classes:\n" +
                "com.jidesoft.list.GroupList\n" +
                "com.jidesoft.list.GroupableListModel";
    }

    @Override
    public int getAttributes() {
        return ATTRIBUTE_NEW;
    }

    @Override
    public Component getOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new JideBoxLayout(panel, JideBoxLayout.Y_AXIS, 2));
 
        JRadioButton ascending = new JRadioButton("Sort Ascending");
        ascending.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    sortableListModel.setSortOrder(SortableListModel.SORT_ASCENDING);
                }
            }
        });

        JRadioButton descending = new JRadioButton("Sort Descending");
        descending.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    sortableListModel.setSortOrder(SortableListModel.SORT_DESCENDING);
                }
            }
        });
        final JRadioButton unsorted = new JRadioButton("Reset", true);
        unsorted.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    sortableListModel.setSortOrder(SortableListModel.UNSORTED);
                }
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(unsorted);
        group.add(ascending);
        group.add(descending);

        JButton shuffle = new JButton("Shuffle");
        shuffle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                unsorted.setSelected(true);
                model.shuffle();
            }
        });

        panel.add(unsorted);
        panel.add(ascending);
        panel.add(descending);
        panel.add(shuffle);
        return panel;
    }

    public JComponent getDemoPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));

        model = new FontModel();
        model.shuffle();
        model.putFont((Font) model.getElementAt(2));
        model.putFont((Font) model.getElementAt(model.getSize() - 1));

        sortableListModel = new SortableGroupableListModel(model);
        sortableListModel.setComparator(FontComparator.INSTANCE);
        final GroupList list = new GroupList(sortableListModel);
        SearchableUtils.installSearchable(list);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 1 && e.getClickCount() >= 2) {
                    int selectedIndex = list.getSelectedIndex();
                    Object element = list.getModel().getElementAt(selectedIndex);
                    if(element instanceof Font) {
                        Font font = (Font) element;
                        model.putFont(font);
                    }
                }
            }
        });
        configureList(list);

        JPanel listPanel = new JPanel(new BorderLayout(2, 2));
        listPanel.setBorder(BorderFactory.createCompoundBorder(new JideTitledBorder(new PartialEtchedBorder(PartialEtchedBorder.LOWERED, PartialSide.NORTH), "Filtered Font List", JideTitledBorder.LEADING, JideTitledBorder.ABOVE_TOP),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        list.registerKeyboardAction(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ListModel model = list.getModel();
                int[] selection = list.getSelectedIndices();
                int[] temp;

                while(model instanceof ListModelWrapper && !(model instanceof FontModel)) {
                    temp = new int[selection.length];
                    for (int i = 0; i < selection.length; i++) {
                        temp[i] = ((ListModelWrapper)model).getActualIndexAt(selection[i]);
                    }
                    selection = temp;
                    model = ((ListModelWrapper)model).getActualModel();
                }

                if(model instanceof FontModel) {
                    Arrays.sort(selection);

                    for (int i = selection.length - 1; i >= 0; i--) {
                        int index = selection[i];
                        if(0 <= index && index < model.getSize()) {
                            ( (FontModel) model).remove(index);
                        }
                    }
                }
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);
        listPanel.add(new JScrollPane(list));
        listPanel.add(new JLabel("Double click to put the font into the recently used font group"), BorderLayout.BEFORE_FIRST_LINE);

        panel.add(listPanel, BorderLayout.CENTER);
        return panel;
    }

    private void configureList(GroupList list) {
        list.setCellRenderer(new FontRenderer());
        list.setGroupCellRenderer(new GroupCellRenderer());
        list.setVisibleRowCount(20);
    }

    public static void main(String[] args) {
        LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
        showAsFrame(new SortFontGroupListDemo());
    }

}
