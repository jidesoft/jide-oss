/*
 * @(#)ShortcutField.java 7/9/2002
 *
 * Copyright 2002 - 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.utils.SystemInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <code>LabeledTextField</code> is a combo component which includes text field and
 * an optional JLabel in the front and another optionial AbstractButton at the end.
 */
public class LabeledTextField extends JPanel {

    protected JTextField _textField;
    protected JLabel _label;
    protected AbstractButton _button;

    protected String _labelText;
    protected Icon _icon;

    public LabeledTextField() {
        this(null, null);
    }

    public LabeledTextField(Icon icon) {
        this(icon, null);
    }

    public LabeledTextField(Icon icon, String labelText) {
        super(new BorderLayout(3, 3));
        _icon = icon;
        _labelText = labelText;
        initComponent();
    }

    protected void initComponent() {
        _label = createLabel();
        if (_label != null) {
            _label.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                    showMenu();
                }

                public void mouseReleased(MouseEvent e) {
                }

                protected void showMenu() {
                    if (isEnabled()) {
                        JidePopupMenu menu = createContextMenu();
                        if (menu != null && menu.getComponentCount() > 0) {
                            Point location = _label.getLocation();
                            menu.show(LabeledTextField.this, location.x + (_label.getIcon() == null ? 1 : _label.getIcon().getIconWidth() / 2), location.y + _label.getHeight() + 1);
                        }
                    }
                }
            });
        }

        _button = createButton();

        _textField = createTextField();
        _textField.setColumns(20);

        if (_label != null) {
            add(_label, BorderLayout.BEFORE_LINE_BEGINS);
        }
        add(_textField);
        if (_button != null) {
            add(_button, BorderLayout.AFTER_LINE_ENDS);
        }

        updateUI();
    }

    /**
     * Creates a text field. By default it will return a JTextField with opaque set to false. Subclass
     * can override this method to create their own text field such as JFormattedTextField.
     *
     * @return a text field.
     */
    protected JTextField createTextField() {
        JTextField textField = new JTextField();
        SelectAllUtils.install(textField);
        textField.setOpaque(false);
        return textField;
    }

    /**
     * Creates a context menu. The context menu will be shown when user clicks on the label.
     *
     * @return a context menu.
     */
    protected JidePopupMenu createContextMenu() {
        return null;
    }

    public void updateUI() {
        super.updateUI();
        setBorder(BorderFactory.createCompoundBorder(UIManager.getBorder("TextField.border"), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        if (isEnabled()) {
            LookAndFeel.installColors(this, "TextField.background", "TextField.foreground");
        }
        else {
            LookAndFeel.installColors(this, "TextField.inactiveBackground", "TextField.foreground");
        }
        if (_textField != null) {
            _textField.setBorder(BorderFactory.createEmptyBorder());
        }
    }

    /**
     * Creates the button that appears after the text field. By default it returns null so there is no button. Subclass can
     * override it to create their own button. A typical usage of this is to create a browse button to browse a file or directory.
     *
     * @return the button.
     */
    protected AbstractButton createButton() {
        return null;
    }

    /**
     * Creates the label that appears before the text field. By default, it only has a search icon.
     *
     * @return the label.
     */
    protected JLabel createLabel() {
        JLabel label = new JLabel(_icon);
        label.setText(_labelText);
        return label;
    }

    /**
     * Sets the text that appears before the text field.
     *
     * @param label
     */
    public void setLabelText(String label) {
        _labelText = label;
        if (_label != null) {
            _label.setText(label);
        }
    }

    /**
     * Gets the text that appears before the text field.
     *
     * @return the text that appears before the text field. By default it's null, meaning no text.
     */
    public String getLabelText() {
        if (_label != null) {
            return _label.getText();
        }
        else {
            return _labelText;
        }
    }

    /**
     * Sets the icon that appears before the text field.
     *
     * @param icon
     */
    public void setIcon(Icon icon) {
        _icon = icon;
        if (_label != null) {
            _label.setIcon(icon);
        }
    }

    /**
     * Gets the icon that appears before the text field.
     *
     * @return the icon that appears before the text field.
     */
    public Icon getIcon() {
        if (_label != null) {
            return _label.getIcon();
        }
        else {
            return _icon;
        }
    }

    /**
     * Gets the JLabel that appears before text field.
     *
     * @return the JLabel that appears before text field.
     */
    public JLabel getLabel() {
        return _label;
    }

    /**
     * Gets the AbstractButton that appears after text field.
     *
     * @return the AbstractButton that appears after text field.
     */
    public AbstractButton getButton() {
        return _button;
    }

    /**
     * Sets the number of columns in this TextField, and then invalidate the layout.
     *
     * @param columns
     */
    public void setColumns(int columns) {
        if (getTextField() != null) {
            getTextField().setColumns(columns);
        }
    }

    /**
     * Gets the actual text field.
     *
     * @return the actual text field.
     */
    public JTextField getTextField() {
        return _textField;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            if (getTextField() != null) {
                getTextField().setEnabled(true);
            }
            if (getLabel() != null) {
                getLabel().setEnabled(true);
            }
            if (getButton() != null) {
                getButton().setEnabled(true);
            }
            setBackground(UIManager.getColor("TextField.background"));
        }
        else {
            if (getTextField() != null) {
                getTextField().setEnabled(false);
            }
            if (getLabel() != null) {
                getLabel().setEnabled(false);
            }
            if (getButton() != null) {
                getButton().setEnabled(false);
            }
            setBackground(UIManager.getColor("control"));
        }
    }

    public int getBaseline(int width, int height) {
        if (SystemInfo.isJdk6Above()) {
            try {
                Method method = Component.class.getMethod("getBaseline", new Class[]{int.class, int.class});
                Object value = method.invoke(_textField, new Object[]{new Integer(width), new Integer(height)});
                if (value instanceof Integer) {
                    return ((Integer) value).intValue();
                }
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}
