/*
 * @(#)Calculator.java 7/11/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import com.jidesoft.utils.PortingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * <tt>Calculator</tt> is a component that can do simple arithmetic calculation. Since it extends JPanel, you can use it
 * at any place in your application.
 * <p/>
 * To make it more flexible, the <tt>Calculator</tt> has no text field to display the result. You can create your own
 * JTextField or JLabel to display the result. Here is a simple example to create a text field and associate it with
 * Calculator.
 * <pre><code>
 * final JTextField textField = new JTextField();
 * textField.setColumns(20);
 * textField.setHorizontalAlignment(JTextField.TRAILING);
 * Calculator calculator = new Calculator();
 * calculator.registerKeyboardActions(textField, JComponent.WHEN_FOCUSED);
 * calculator.addPropertyChangeListener(Calculator.PROPERTY_DISPLAY_TEXT, new PropertyChangeListener() {
 *     public void propertyChange(PropertyChangeEvent evt) {
 *         textField.setText("" + evt.getNewValue());
 *     }
 * });
 * calculator.clear();
 * </code></pre>
 * With the code above, user can type in directly into text field and do the calculation. If you just want to display
 * the result and don't mind if the text field accepts keyboard input, you don't need to call registerKeyboardActions
 * method.
 * <p/>
 * All numeric and operator keys work as expected. Here are a few special keys that worth mentioning <ul> <li> 'C', 'c'
 * or ESC to clear current result <li> '!' to make current displayed number from positive to negative (or from negative
 * to positive) <li> ENTER is equivalent to '='.. </ul>
 * <p/>
 * Another interesting way to use Calculator is to use it without using GUI.
 * <pre><code>
 * Calculator calculator = new Calculator();
 * calculator.input('1');
 * calculator.input('0');
 * calculator.input('*');
 * calculator.input('2');
 * calculator.input('4');
 * calculator.input('=');
 * System.out.println("10 * 24 = " + calculator.getDisplayText());
 * </code></pre>
 * The print out will be "10 * 24 = 240".
 * <p/>
 * There are several methods you can use to get internal state of the Calculator. <ul> <li> {@link #getDisplayText()}:
 * to get the result that should be displayed. Please note, this method return a string. <li> {@link #getResult()}: to
 * get the last calculated result. This method returns a double value. <li> {@link #getOperator()}: to get the current
 * operator <li> {@link #isOverflow()}: to check if there is an overflow. Usually if you try to divide by zero, you will
 * get an overflow. </ul>
 */
public class Calculator extends JPanel implements ActionListener {

    private double _result;
    private StringBuffer _op1;
    private StringBuffer _op2;
    private int _operator = OPERATOR_NONE;
    private String _displayText;
    private boolean _overflow = false;
    private boolean _negationOp1 = true;
    private boolean _backspaceOp1 = false;
    private boolean _backspaceOp2 = false;
    private boolean _clearOperatorPending = false;
    private boolean _isFakedEqualPressed = false; // When it is true, it means the focus is lost. So the result should not be updated.
    private boolean _resultCalculated = false;

    public static final int OPERATOR_NONE = -1;
    public static final int OPERATOR_ADD = 0;
    public static final int OPERATOR_MINUS = 1;
    public static final int OPERATOR_MULTIPLY = 2;
    public static final int OPERATOR_DIVIDE = 3;

    private AbstractButton _addButton;
    private AbstractButton _minusButton;
    private AbstractButton _multiplyButton;
    private AbstractButton _divideButton;
    private AbstractButton _pointButton;
    private AbstractButton _equalButton;
    private AbstractButton _backspaceButton;
    private AbstractButton _clearButton;
    private AbstractButton _negativeButton;
    private AbstractButton[] _numberButtons;
    private char _actualCharPoint;

    private NumberFormat _displayFormat;

    public static final char CHAR_CLEAR = 'c';
    public static final char CHAR_POINT = '.';
    public static final char CHAR_ADD = '+';
    public static final char CHAR_MINUS = '-';
    public static final char CHAR_MULTIPLY = '*';
    public static final char CHAR_DIVIDE = '/';
    public static final char CHAR_EQUAL = '=';
    public static final char CHAR_NEGATIVE = '!';
    public static final char CHAR_BACKSPACE = '<';
    public static final char CHAR_0 = '0';
    public static final char CHAR_1 = '1';
    public static final char CHAR_2 = '2';
    public static final char CHAR_3 = '3';
    public static final char CHAR_4 = '4';
    public static final char CHAR_5 = '5';
    public static final char CHAR_6 = '6';
    public static final char CHAR_7 = '7';
    public static final char CHAR_8 = '8';
    public static final char CHAR_9 = '9';

    public static final String PROPERTY_DISPLAY_TEXT = "displayText";
    public static final String PROPERTY_OPERATOR = "operator";

    private int _buttonWidth = 24;
    private int _buttonHeight = 24;
    private int _buttonGap = 2;

    /**
     * Creates a <code>Calculator</code>.
     */
    public Calculator() {
        _op1 = new StringBuffer();
        _op2 = new StringBuffer();
        _displayFormat = NumberFormat.getNumberInstance();
        configureNumberFormat();
        initComponents();
        registerKeyboardActions(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    @Override
    public void setLocale(Locale l) {
        unregisterKeyboardActions(this);
        super.setLocale(l);
        _op1 = new StringBuffer();
        _op2 = new StringBuffer();
        _displayFormat = NumberFormat.getNumberInstance(getLocale());
        configureNumberFormat();
        _actualCharPoint = getDisplayFormat().format(2.01).charAt(1);
        _pointButton.setText("" + _actualCharPoint);
        registerKeyboardActions(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /**
     * Configures the number format for displaying purpose.
     */
    protected void configureNumberFormat() {
        _displayFormat.setMaximumFractionDigits(20);
        _displayFormat.setMinimumFractionDigits(0);
        _displayFormat.setGroupingUsed(false);
    }

    /**
     * Checks if the key event a valid key event that can be accepted by the Calculator.
     *
     * @param keyEvent the key event.
     * @return true if it is a valid key event for the Calculator.
     */
    public static boolean isValidKeyEvent(KeyEvent keyEvent) {
        char c = keyEvent.getKeyChar();
        return (keyEvent.getModifiers() & ~KeyEvent.SHIFT_MASK) != 0 // if it has any modify, ignore it
                || Character.isDigit(c) || isOperator(keyEvent)
                || isEnterOrEqual(keyEvent)
                || c == KeyEvent.VK_PERIOD || c == CHAR_CLEAR || Character.toLowerCase(c) == CHAR_CLEAR
                || c == KeyEvent.VK_ESCAPE
                || c == KeyEvent.VK_BACK_SPACE;
    }

    /**
     * Checks if the key event a key event for operators. In the other words, if it is {@link #CHAR_ADD}, {@link
     * #CHAR_MINUS}, {@link #CHAR_MULTIPLY} or {@link #CHAR_DIVIDE}, this method will return true.
     *
     * @param keyEvent the key event.
     * @return true if it is a valid key event is an operator.
     */
    public static boolean isOperator(KeyEvent keyEvent) {
        char c = keyEvent.getKeyChar();
        return c == CHAR_ADD || c == CHAR_MINUS || c == CHAR_MULTIPLY || c == CHAR_DIVIDE;
    }

    /**
     * Checks if the key event a key event for enter. In the other words, if it is {@link KeyEvent#VK_ENTER} or {@link
     * KeyEvent#VK_EQUALS}, this method will return true.
     *
     * @param keyEvent the key event.
     * @return true if it is a valid key event is an enter key or an equal key.
     */
    public static boolean isEnterOrEqual(KeyEvent keyEvent) {
        char c = keyEvent.getKeyChar();
        return c == KeyEvent.VK_ENTER || c == KeyEvent.VK_EQUALS;
    }

    /**
     * Registers necessary keyboard actions onto the component. Usually the component is a <code>JTextField</code>.
     *
     * @param component the component where the key input will be taken and passed to the <code>Calculator</code>.
     * @param condition the condition as defined in {@link JComponent#registerKeyboardAction(java.awt.event.ActionListener,javax.swing.KeyStroke,int)}.
     */
    public void registerKeyboardActions(JComponent component, int condition) {
        boolean isCellEditor = isCellEditor();
        component.registerKeyboardAction(this, "" + CHAR_ADD, KeyStroke.getKeyStroke(CHAR_ADD), condition);
        component.registerKeyboardAction(this, "" + CHAR_MINUS, KeyStroke.getKeyStroke(CHAR_MINUS), condition);
        component.registerKeyboardAction(this, "" + CHAR_MULTIPLY, KeyStroke.getKeyStroke(CHAR_MULTIPLY), condition);
        component.registerKeyboardAction(this, "" + CHAR_DIVIDE, KeyStroke.getKeyStroke(CHAR_DIVIDE), condition);
        component.registerKeyboardAction(this, "" + CHAR_EQUAL, KeyStroke.getKeyStroke(CHAR_EQUAL), condition);
        if (!isCellEditor)
            component.registerKeyboardAction(this, "" + CHAR_EQUAL, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), condition);
        component.registerKeyboardAction(this, "" + CHAR_0, KeyStroke.getKeyStroke(CHAR_0), condition);
        component.registerKeyboardAction(this, "" + CHAR_1, KeyStroke.getKeyStroke(CHAR_1), condition);
        component.registerKeyboardAction(this, "" + CHAR_2, KeyStroke.getKeyStroke(CHAR_2), condition);
        component.registerKeyboardAction(this, "" + CHAR_3, KeyStroke.getKeyStroke(CHAR_3), condition);
        component.registerKeyboardAction(this, "" + CHAR_4, KeyStroke.getKeyStroke(CHAR_4), condition);
        component.registerKeyboardAction(this, "" + CHAR_5, KeyStroke.getKeyStroke(CHAR_5), condition);
        component.registerKeyboardAction(this, "" + CHAR_6, KeyStroke.getKeyStroke(CHAR_6), condition);
        component.registerKeyboardAction(this, "" + CHAR_7, KeyStroke.getKeyStroke(CHAR_7), condition);
        component.registerKeyboardAction(this, "" + CHAR_8, KeyStroke.getKeyStroke(CHAR_8), condition);
        component.registerKeyboardAction(this, "" + CHAR_9, KeyStroke.getKeyStroke(CHAR_9), condition);
        component.registerKeyboardAction(this, "" + _actualCharPoint, KeyStroke.getKeyStroke(_actualCharPoint), condition);
        component.registerKeyboardAction(this, "" + CHAR_BACKSPACE, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), condition);
        if (!isCellEditor)
            component.registerKeyboardAction(this, "" + CHAR_CLEAR, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), condition);
        if (!isCellEditor)
            component.registerKeyboardAction(this, "" + CHAR_CLEAR, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        component.registerKeyboardAction(this, "" + CHAR_CLEAR, KeyStroke.getKeyStroke(Character.toUpperCase(CHAR_CLEAR)), condition);
        component.registerKeyboardAction(this, "" + CHAR_CLEAR, KeyStroke.getKeyStroke(Character.toLowerCase(CHAR_CLEAR)), condition);
    }

    /**
     * Unregisters the keyboard actions you registered using {@link #registerKeyboardActions(javax.swing.JComponent,int)}.
     *
     * @param component the component.
     */
    public void unregisterKeyboardActions(JComponent component) {
        boolean isCellEditor = isCellEditor();
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_ADD));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_MINUS));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_MULTIPLY));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_DIVIDE));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_EQUAL));
        if (!isCellEditor) component.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_0));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_1));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_2));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_3));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_4));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_5));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_6));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_7));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_8));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(CHAR_9));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(_actualCharPoint));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
        if (!isCellEditor) component.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(Character.toUpperCase(CHAR_CLEAR)));
        component.unregisterKeyboardAction(KeyStroke.getKeyStroke(Character.toLowerCase(CHAR_CLEAR)));
    }

    protected void initComponents() {
        setLayout(new CalculatorLayoutManager());
        add(_addButton = createButton("+"));
        add(_minusButton = createButton("-"));
        add(_multiplyButton = createButton("*"));
        add(_divideButton = createButton("/"));
        _numberButtons = new AbstractButton[10];
        for (int i = 0; i <= 9; i++) {
            add(_numberButtons[i] = createButton("" + i));
        }
        _actualCharPoint = getDisplayFormat().format(2.01).charAt(1);
        add(_pointButton = createButton("" + _actualCharPoint));
        add(_equalButton = createButton("="));
        add(_backspaceButton = createButton(null, new BackspaceIcon()));
        add(_negativeButton = createButton(null, new ToggleNegativeIcon()));
        add(_clearButton = createButton("C"));
    }

    /**
     * Get the flag indicating if the result was calculated at least once.
     *
     * @return true if the result was calculated at least once. Otherwise false.
     */
    public boolean isResultCalculated() {
        return _resultCalculated;
    }

    /**
     * Set the flag indicating if the result was calculated at least once.
     *
     * @param resultCalculated the flag
     */
    public void setResultCalculated(boolean resultCalculated) {
        _resultCalculated = resultCalculated;
    }

    class BackspaceIcon implements Icon {
        public BackspaceIcon() {
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Object save = JideSwingUtilities.setupShapeAntialiasing(g);
            Color old = g.getColor();
            g.setColor(c.getForeground());
            g.drawLine(x, y + 3, x + 3, y);
            g.drawLine(x, y + 3, x + 3, y + 6);
            g.drawLine(x + 3, y + 3, x + 7, y + 3);
            g.setColor(old);
            JideSwingUtilities.restoreShapeAntialiasing(g, save);
        }

        public int getIconWidth() {
            return 7;
        }

        public int getIconHeight() {
            return 7;
        }
    }

    class ToggleNegativeIcon implements Icon {
        public ToggleNegativeIcon() {
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color old = g.getColor();
            Object save = JideSwingUtilities.setupShapeAntialiasing(g);
            g.setColor(c.getForeground());
            g.drawLine(x, y + 2, x + 6, y + 2);
            g.drawLine(x, y + 7, x + 6, y + 7);
            g.drawLine(x + 3, y, x + 3, y + 5);
            g.setColor(old);
            JideSwingUtilities.restoreShapeAntialiasing(g, save);
        }

        public int getIconWidth() {
            return 7;
        }

        public int getIconHeight() {
            return 7;
        }
    }

    /**
     * Creates the button that is used in the Calculator. By default, it will create a JideButton. Here is the code. You
     * can override it to create your own button. This method is used to create all buttons except the backspace and the
     * +/- button. So if you want to override it, it's better to override {@link #createButton(String,javax.swing.Icon)}
     * method.
     *
     * @param text the text on the button.
     * @return the button.
     */
    protected AbstractButton createButton(String text) {
        return createButton(text, null);
    }

    /**
     * Creates the button that is used in the Calculator. By default, it will create a JideButton. Here is the code. You
     * can override it to create your own button.
     * <pre><code>
     * AbstractButton button = new JideButton(text, icon);
     * button.setOpaque(true);
     * button.setContentAreaFilled(true);
     * button.setRequestFocusEnabled(false);
     * button.setFocusable(false);
     * button.addActionListener(this);
     * return button;
     * </code></pre>
     *
     * @param text the text on the button.
     * @param icon the icon on the button.
     * @return the button.
     */
    protected AbstractButton createButton(String text, Icon icon) {
        AbstractButton button = new JideButton(text, icon);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setRequestFocusEnabled(false);
        button.setFocusable(false);
        button.addActionListener(this);
        return button;
    }

    /**
     * Checks if the calculator is in overflow state.
     *
     * @return true if overflow.
     */
    public boolean isOverflow() {
        return _overflow;
    }

    /**
     * Sets the overflow flag.
     *
     * @param overflow the overflow flag.
     */
    public void setOverflow(boolean overflow) {
        _overflow = overflow;
    }

    /**
     * Inputs a char to the calculator. Please note, not all chars are acceptable. Valid chars are defined in {@link
     * Calculator} class as CHAR_XXX constants.
     *
     * @param c the char input char.
     */
    public void input(char c) {
        if (CHAR_CLEAR == Character.toLowerCase(c) || CHAR_CLEAR == Character.toUpperCase(c)) {
            clear();
            return;
        }

        if (_overflow) {
            beep();
            return;
        }

        if (Character.isDigit(c) || _actualCharPoint == c) {
            if (_clearOperatorPending) {
                setOperator(OPERATOR_NONE);
                _op1.setLength(0);
                _clearOperatorPending = false;
            }
            if (getOperator() == OPERATOR_NONE) {
                if (_actualCharPoint != c || _op1.indexOf("" + _actualCharPoint) == -1) {
                    _op1.append(c);
                    _backspaceOp1 = true;
                    _backspaceOp2 = false;
                    setDisplayText(_op1.toString());
                }
                else {
                    beep();
                }
            }
            else {
                if (_actualCharPoint != c || _op2.indexOf("" + _actualCharPoint) == -1) {
                    _op2.append(c);
                    _backspaceOp2 = true;
                    _backspaceOp1 = false;
                    setDisplayText(_op2.toString());
                }
                else {
                    beep();
                }
            }
        }
        else {
            switch (c) {
                case CHAR_ADD:
                    operatorPressed(OPERATOR_ADD);
                    break;
                case CHAR_MINUS:
                    operatorPressed(OPERATOR_MINUS);
                    break;
                case CHAR_MULTIPLY:
                    operatorPressed(OPERATOR_MULTIPLY);
                    break;
                case CHAR_DIVIDE:
                    operatorPressed(OPERATOR_DIVIDE);
                    break;
                case CHAR_EQUAL:
                    calculateResult(true);
                    _clearOperatorPending = true;
                    break;
                case CHAR_NEGATIVE:
                    if (_negationOp1) {
                        negativePressed(_op1);
                        setDisplayText(_op1.toString());
                    }
                    else {
                        negativePressed(_op2);
                        setDisplayText(_op2.toString());
                    }
                    break;
                case CHAR_BACKSPACE:
                    if (_backspaceOp1) {
                        backspacePressed(_op1);
                        setDisplayText(_op1.toString());
                    }
                    else if (_backspaceOp2) {
                        backspacePressed(_op2);
                        setDisplayText(_op2.toString());
                    }
                    else {
                        beep();
                    }
                    break;
            }
        }
    }

    private void operatorPressed(int operator) {
        if (_op1.length() == 0) { // _op1 does not have input yet, make it 0
            _op1.append("0");
        }
        else if (getOperator() == OPERATOR_NONE) { // normal process
            _op2.setLength(0);
            calculateResult(false);
        }
        else {
            if (_op2.length() == 0) { // two operators input continuously
                beep();
                return;
            }
            else { // enable 3+3+3
                _isFakedEqualPressed = false;
                calculateResult(true);
                _op1.setLength(0);
                _op1.append(((Double) _result).toString());
                _op2.setLength(0);
            }
        }
        setOperator(operator);
        _negationOp1 = false;
        _clearOperatorPending = false;
    }

    protected void beep() {
        PortingUtils.notifyUser();
    }

    private void negativePressed(StringBuffer buf) {
        if (buf.length() == 0) {
            return;
        }
        if (buf.charAt(0) == CHAR_MINUS) {
            buf.deleteCharAt(0);
        }
        else {
            buf.insert(0, CHAR_MINUS);
        }
    }

    private void backspacePressed(StringBuffer buf) {
        if (buf.length() == 0) {
            return;
        }
        buf.deleteCharAt(buf.length() - 1);
    }

    /**
     * Update the result as if the equal was pressed. This method can be used while Enter key is pressed and you need
     * keep the calculated result.
     */
    public void updateResult() {
        _isFakedEqualPressed = false;
        calculateResult(true);
    }

    private void calculateResult(boolean equalPressed) {
        if (getOperator() == OPERATOR_NONE) {
            return;
        }

        if (_op1.length() == 0) {
            beep();
            return;
        }

        if (equalPressed) {
            if (_op2.length() == 0) {
                _op2.append(_op1);
            }
        }
        else if (_op2.length() == 0) {
            return;
        }
        Double op1;
        Double op2;
        try {
            op1 = getDisplayFormat().parse(_op1.toString()).doubleValue();
            op2 = getDisplayFormat().parse(_op2.toString()).doubleValue();
        }
        catch (ParseException e) {
            op1 = 0.0;
            op2 = 0.0;
        }
        if (!_isFakedEqualPressed) {
            try {
                switch (getOperator()) {
                    case OPERATOR_ADD:
                        _result = op1 + op2;
                        break;
                    case OPERATOR_MINUS:
                        _result = op1 - op2;
                        break;
                    case OPERATOR_MULTIPLY:
                        _result = op1 * op2;
                        break;
                    case OPERATOR_DIVIDE:
                        if (op2 == 0) {
                            _result = Double.NaN;
                            _overflow = true;
                        }
                        else {
                            _result = op1 / op2;
                        }
                        break;
                }
            }
            catch (Exception e) {
                _overflow = true;
            }
        }

        if (_overflow) {
            setDisplayText("E");
        }
        else {
            setResultCalculated(true);
            _op1.setLength(0);
            if (_displayFormat != null) {
                String displayText = _displayFormat.format(_result);
                setDisplayText(displayText);
            }
            else {
                setDisplayText("" + _result);
            }
            _op1.append(getDisplayText());
            _negationOp1 = true;
            _backspaceOp1 = true;
            _backspaceOp2 = false;
        }
    }

    private void clearOps() {
        setOperator(OPERATOR_NONE);
        _op1.setLength(0);
        _op2.setLength(0);
    }

    /**
     * Clears the internal state and reset the calculator.
     */
    public void clear() {
        clearOps();
        _overflow = false;
        _clearOperatorPending = false;
        setDisplayText("0");
    }

    /**
     * Gets the last calculated result.
     *
     * @return the last calculated result.
     */
    public double getResult() {
        return _result;
    }

    /**
     * Gets the display text.
     *
     * @return the display text.
     */
    public String getDisplayText() {
        return _displayText;
    }

    /**
     * Sets the display text and fire property change event on property named {@link #PROPERTY_DISPLAY_TEXT}.
     *
     * @param displayText the displayed text.
     */
    public void setDisplayText(String displayText) {
        String old = _displayText;
        _displayText = displayText;
        firePropertyChange(PROPERTY_DISPLAY_TEXT, old, _displayText);
    }

    /**
     * Gets the current operator.
     *
     * @return the current operator.
     */
    public int getOperator() {
        return _operator;
    }

    /**
     * Sets the operator and fire property change event on property named {@link #PROPERTY_OPERATOR}.
     *
     * @param operator the operator.
     */
    public void setOperator(int operator) {
        int old = _operator;
        if (old != operator) {
            _operator = operator;
            firePropertyChange(PROPERTY_OPERATOR, new Integer(old), new Integer(operator));
        }
    }

    private class CalculatorLayoutManager implements LayoutManager {
        public CalculatorLayoutManager() {
        }

        public void addLayoutComponent(String name, Component comp) {

        }

        public void removeLayoutComponent(Component comp) {
        }

        public Dimension preferredLayoutSize(Container parent) {
            return minimumLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(getButtonWidth() * 4 + getButtonGap() * 3, getButtonHeight() * 5 + getButtonGap() * 4);
        }

        public void layoutContainer(Container parent) {
            int x = 0;
            int y = 0;

            int w = getButtonWidth();
            int h = getButtonHeight();
            int gap = getButtonGap();

            _numberButtons[7].setBounds(x, y, w, h);
            x += w + gap;
            _numberButtons[8].setBounds(x, y, w, h);
            x += w + gap;
            _numberButtons[9].setBounds(x, y, w, h);
            x += w + gap;
            _divideButton.setBounds(x, y, w, h);

            x = 0;
            y += h + gap;

            _numberButtons[4].setBounds(x, y, w, h);
            x += w + gap;
            _numberButtons[5].setBounds(x, y, w, h);
            x += w + gap;
            _numberButtons[6].setBounds(x, y, w, h);
            x += w + gap;
            _multiplyButton.setBounds(x, y, w, h);

            x = 0;
            y += h + gap;

            _numberButtons[1].setBounds(x, y, w, h);
            x += w + gap;
            _numberButtons[2].setBounds(x, y, w, h);
            x += w + gap;
            _numberButtons[3].setBounds(x, y, w, h);
            x += w + gap;
            _minusButton.setBounds(x, y, w, h);

            x = 0;
            y += h + gap;

            _numberButtons[0].setBounds(x, y, w, h);
            x += w + gap;
            _pointButton.setBounds(x, y, w, h);
            x += w + gap;
            _negativeButton.setBounds(x, y, w, h);
            x += w + gap;
            _addButton.setBounds(x, y, w, h);

            x = 0;
            y += h + gap;

            _clearButton.setBounds(x, y, w, h);
            x += w + gap;
            _backspaceButton.setBounds(x, y, w, h);
            x += w + gap;
            _equalButton.setBounds(x, y, w * 2 + gap, h);
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (_addButton == source) {
            input(CHAR_ADD);
        }
        else if (_minusButton == source) {
            input(CHAR_MINUS);
        }
        else if (_multiplyButton == source) {
            input(CHAR_MULTIPLY);
        }
        else if (_divideButton == source) {
            input(CHAR_DIVIDE);
        }
        else if (_equalButton == source) {
            _isFakedEqualPressed = e.getActionCommand() != null && e.getActionCommand().equals("Faked");
            input(CHAR_EQUAL);
        }
        else if (_pointButton == source) {
            input(_actualCharPoint);
        }
        else if (_negativeButton == source) {
            input(CHAR_NEGATIVE);
        }
        else if (_backspaceButton == source) {
            input(CHAR_BACKSPACE);
        }
        else if (_clearButton == source) {
            input(CHAR_CLEAR);
        }
        else {
            boolean found = false;
            for (int i = 0; i <= 9; i++) {
                if (_numberButtons[i] == source) {
                    input(("" + i).charAt(0));
                    found = true;
                    break;
                }
            }
            if (!found) {
                if (e.getActionCommand() != null && e.getActionCommand().length() > 0) {
                    fakePressButton(e.getActionCommand().charAt(0));
                }
                else {
                    fakePressButton(CHAR_EQUAL);
                }
            }
        }
    }

    /**
     * Press the button. By default, we will trigger the action directly on this button. However subclass can override
     * it to call doClick to mimic the user pressing the button.
     *
     * @param button the button
     */
    protected void fakePressButton(AbstractButton button) {
        actionPerformed(new ActionEvent(button, 0, null));
    }

    private void fakePressButton(char c) {
        if (c == _actualCharPoint) {
            fakePressButton(_pointButton);
            return;
        }
        switch (c) {
            case CHAR_CLEAR:
                fakePressButton(_clearButton);
                break;
            case CHAR_BACKSPACE:
                fakePressButton(_backspaceButton);
                break;
            case CHAR_EQUAL:
                fakePressButton(_equalButton);
                break;
            case CHAR_NEGATIVE:
                fakePressButton(_negativeButton);
                break;
            case CHAR_ADD:
                fakePressButton(_addButton);
                break;
            case CHAR_MINUS:
                fakePressButton(_minusButton);
                break;
            case CHAR_MULTIPLY:
                fakePressButton(_multiplyButton);
                break;
            case CHAR_DIVIDE:
                fakePressButton(_divideButton);
                break;
            case CHAR_0:
                fakePressButton(_numberButtons[0]);
                break;
            case CHAR_1:
                fakePressButton(_numberButtons[1]);
                break;
            case CHAR_2:
                fakePressButton(_numberButtons[2]);
                break;
            case CHAR_3:
                fakePressButton(_numberButtons[3]);
                break;
            case CHAR_4:
                fakePressButton(_numberButtons[4]);
                break;
            case CHAR_5:
                fakePressButton(_numberButtons[5]);
                break;
            case CHAR_6:
                fakePressButton(_numberButtons[6]);
                break;
            case CHAR_7:
                fakePressButton(_numberButtons[7]);
                break;
            case CHAR_8:
                fakePressButton(_numberButtons[8]);
                break;
            case CHAR_9:
                fakePressButton(_numberButtons[9]);
                break;
        }
    }

    /**
     * Gets the display format for the number.
     *
     * @return the display format for the number.
     */
    public NumberFormat getDisplayFormat() {
        return _displayFormat;
    }

    /**
     * Sets the display format for the number.
     *
     * @param displayFormat the display format.
     */
    public void setDisplayFormat(NumberFormat displayFormat) {
        _displayFormat = displayFormat;
    }

    /**
     * Calculates the pending calculation. If the Calculator has both operations and a valid operator, this method will
     * do the calculation and set the display text and result.
     */
    public void commit() {
        if (!_clearOperatorPending) {
            actionPerformed(new ActionEvent(_equalButton, 0, "Faked"));
        }
    }

    /**
     * Gets the button width.
     *
     * @return the button width.
     */
    public int getButtonWidth() {
        return _buttonWidth;
    }

    /**
     * Sets the button width.
     *
     * @param buttonWidth the new button width.
     */
    public void setButtonWidth(int buttonWidth) {
        _buttonWidth = buttonWidth;
    }

    /**
     * Gets the button height.
     *
     * @return the button height.
     */
    public int getButtonHeight() {
        return _buttonHeight;
    }

    /**
     * Sets the button height.
     *
     * @param buttonHeight the new button height.
     */
    public void setButtonHeight(int buttonHeight) {
        _buttonHeight = buttonHeight;
    }

    /**
     * Gets the gap between buttons. Default is 2.
     *
     * @return the gap between buttons.
     */
    public int getButtonGap() {
        return _buttonGap;
    }

    public void setButtonGap(int buttonGap) {
        _buttonGap = buttonGap;
    }

    /**
     * If this method return true, ENTER and ESCAPE key will be registered. Otherwise they will not be. The reason we do
     * so because the two keys are conflicted with keys in JTable.
     *
     * @return true or false.
     */
    protected boolean isCellEditor() {
        return false;
    }

    public void setInitialValue(String value) {
        _op1.setLength(0);
        _op1.append(value);
        _backspaceOp1 = true;
        _backspaceOp2 = false;
        setDisplayText(_op1.toString());
    }

    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        calculator.input('1');
        calculator.input('0');
        calculator.input('*');
        calculator.input('2');
        calculator.input('4');
        calculator.input('=');
//        System.out.println("10 * 24 = " + calculator.getDisplayText());
    }

}
