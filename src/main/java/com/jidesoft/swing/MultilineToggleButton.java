package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;

/**
 * A toggle button (JCheckBox or JRadioButton) that can wrap its label to extend to multiple lines. Use CHECKBOX_TYPE or
 * RADIOBUTTON_TYPE to specify the type of toggle button to create.
 *
 * @author Bao Trang
 */
public class MultilineToggleButton extends JPanel {

    private JToggleButton _button;
    private MultilineLabel _label;

    public static int CHECKBOX_TYPE = 0;
    public static int RADIOBUTTON_TYPE = 1;

    /**
     * constructor.
     *
     * @param type     the type of toggle button to create
     * @param labelTxt the label
     */
    public MultilineToggleButton(int type, String labelTxt) {
        if (type == CHECKBOX_TYPE) {
            _button = new JCheckBox();
        }
        else if (type == RADIOBUTTON_TYPE) {
            _button = new JRadioButton();
        }
        else {
            _button = new JToggleButton();
        }
        _label = new MultilineLabel(labelTxt);
        build();
    }

    /**
     * builds the component
     */
    private void build() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(_button);
        add(Box.createHorizontalGlue());
        add(_label);
    }

    public void setTopAlignment() {
        _button.setAlignmentY(Component.TOP_ALIGNMENT);
        _label.setAlignmentY(Component.TOP_ALIGNMENT);
    }

    public void setCenterAlignment() {
        _button.setAlignmentY(Component.CENTER_ALIGNMENT);
        _label.setAlignmentY(Component.CENTER_ALIGNMENT);
    }

    /**
     * get the toggle button
     *
     * @return toggle button
     */
    public JToggleButton getToggleButton() {
        return _button;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        _label.setVisible(b);
        _button.setVisible(b);
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        _button.setEnabled(b);
        _label.setEnabled(b);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        MultilineToggleButton chkCertA = new MultilineToggleButton(MultilineToggleButton.CHECKBOX_TYPE, "Very Long Text Goes Here");
        MultilineToggleButton radioButton = new MultilineToggleButton(MultilineToggleButton.RADIOBUTTON_TYPE, "Very Long Text");

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(chkCertA);
        frame.add(radioButton);

        frame.pack();
        frame.setVisible(true);
    }
}

