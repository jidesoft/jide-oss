/*
 * @(#)PointSpinner.java 4/8/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.spinner;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * <code>PointSpinner</code> is a spinner that is specialized in displaying x and y value of a point.
 * <p/>
 *
 * @author Nako Ruru
 */
public class PointSpinner extends JSpinner {

    /**
     * Constructs a complete spinner with pair of next/previous buttons and an editor for the
     * <code>SpinnerModel</code>.
     *
     * @param model
     */
    public PointSpinner(SpinnerPointModel model) {
        setModel(model);
        setEditor(createEditor(model));
        setOpaque(true);
        updateUI();
        customizeSpinner();
    }


    /**
     * Constructs a spinner with an <code>Integer SpinnerNumberModel</code> with initial value 0 and no minimum or
     * maximum limits.
     */
    public PointSpinner() {
        this(new SpinnerPointModel());
    }

    private JComponent createEditor(SpinnerPointModel model) {
        return new PointEditor(this);
    }

    /**
     *
     */
    public static class PointEditor extends DefaultEditor {

        /**
         * @param spinner
         */
        public PointEditor(JSpinner spinner) {
            super(spinner);
            if (!(spinner.getModel() instanceof SpinnerPointModel)) {
                throw new IllegalArgumentException(
                        "model not a SpinnerPointModel");
            }
            final SpinnerPointModel model = (SpinnerPointModel) spinner.getModel();

            JFormattedTextField.AbstractFormatter formatter = PointFormatter.getInstance();
            DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
            final JFormattedTextField ftf = getTextField();
            ftf.setEditable(true);
            ftf.setFormatterFactory(factory);
            ftf.setHorizontalAlignment(JTextField.RIGHT);

            /* TBD - initializing the column width of the text field
             * is imprecise and doing it here is tricky because
             * the developer may configure the formatter later.
             */
            String min = Integer.toString(Integer.MIN_VALUE);
            ftf.setColumns(4 + 2 * min.length());

            ftf.addPropertyChangeListener("value", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    String text = ftf.getText();
                    int comma = text.indexOf(',');
                    String digit;
                    int number;
                    if (model.getField() == SpinnerPointModel.FIELD_X) {
                        digit = text.substring(text.indexOf('(') + 1, comma).trim();
                        number = text.indexOf(digit);
                    }
                    else {
                        digit = text.substring(comma + 1, text.indexOf(')')).trim();
                        number = text.lastIndexOf(digit);
                    }
                    ftf.select(number, number + digit.length());
                }
            });
        }
    }

    private void updateField() {
        JComponent editor = getEditor();
        if (editor instanceof PointEditor && getModel() instanceof SpinnerPointModel) {
            JFormattedTextField ftf = ((PointEditor) editor).getTextField();
            SpinnerPointModel model = (SpinnerPointModel) getModel();
            int comma = ftf.getText().indexOf(',');
            int caret = ftf.getCaretPosition();
            model.setField(caret <= comma ? SpinnerPointModel.FIELD_X : SpinnerPointModel.FIELD_Y);
        }
    }

    @Override
    public Object getNextValue() {
        updateField();
        return super.getNextValue();
    }

    @Override
    public Object getPreviousValue() {
        updateField();
        return super.getPreviousValue();
    }

    protected void customizeSpinner() {
        SpinnerWheelSupport.installMouseWheelSupport(this);
    }
}
