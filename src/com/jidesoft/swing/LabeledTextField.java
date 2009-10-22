/*
 * @(#)ShortcutField.java 7/9/2002
 *
 * Copyright 2002 - 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.utils.SystemInfo;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <code>LabeledTextField</code> is a combo component which includes text field and an optional JLabel in the front and
 * another optional AbstractButton at the end.
 */
public class LabeledTextField extends JPanel {

    protected JTextField _textField;
    protected JLabel _label;
    protected AbstractButton _button;

    protected String _labelText;
    protected Icon _icon;
    protected String _hintText;
    protected JLabel _hintLabel;
    protected PopupMenuCustomizer _customizer;
    protected KeyStroke _contextMenuKeyStroke;

    /**
     * The PopupMenuCustomizer for the context menu when clicking on the label/icon before the text field.
     */
    public static interface PopupMenuCustomizer {
        void customize(LabeledTextField field, JPopupMenu menu);
    }

    public LabeledTextField() {
        this(null, null);
    }

    public LabeledTextField(Icon icon) {
        this(icon, null);
    }

    public LabeledTextField(Icon icon, String labelText) {
        super();
        _icon = icon;
        _labelText = labelText;
        initComponent();
    }

    protected void initComponent() {
        _label = createLabel();
        if (_label != null) {
            _label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    showContextMenu();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }
            });
        }
        _button = createButton();
        _textField = createTextField();
        initLayout(_label, _textField, _button);
        setContextMenuKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,  KeyEvent.ALT_DOWN_MASK));
        registerContextMenuKeyStroke(getContextMenuKeyStroke());
        updateUI();
    }

    private void registerContextMenuKeyStroke(KeyStroke keyStroke) {
        if (keyStroke != null) {
            registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showContextMenu();
                }
            }, keyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        }
    }

    private void unregisterContextMenuKeyStroke(KeyStroke keyStroke) {
        if (keyStroke != null)
            unregisterKeyboardAction(keyStroke);
    }

    /**
     * Shows the context menu.
     */
    protected void showContextMenu() {
        if (isEnabled()) {
            JPopupMenu menu = createContextMenu();
            PopupMenuCustomizer customizer = getPopupMenuCustomizer();
            if (customizer != null && menu != null) {
                customizer.customize(LabeledTextField.this, menu);
            }
            if (menu != null && menu.getComponentCount() > 0) {
                Point location = _label.getLocation();
                menu.show(LabeledTextField.this, location.x + (_label.getIcon() == null ? 1 : _label.getIcon().getIconWidth() / 2), location.y + _label.getHeight() + 1);
            }
        }
    }

    /**
     * Setup the layout of the components. By default, we used a border layout with label first, field in the center and
     * button last.
     *
     * @param label  the label
     * @param field  the text field.
     * @param button the button
     */
    protected void initLayout(final JLabel label, final JTextField field, final AbstractButton button) {
        setLayout(new BorderLayout(3, 3));
        if (label != null) {
            add(label, BorderLayout.BEFORE_LINE_BEGINS);
        }
        _hintLabel = new JLabel(getHintText());
        _hintLabel.setOpaque(false);
        Color foreground = UIDefaultsLookup.getColor("Label.disabledForeground");
        if (foreground == null) {
            foreground = Color.GRAY;
        }
        _hintLabel.setForeground(foreground);
        final DefaultOverlayable overlayable = new DefaultOverlayable(field, _hintLabel, DefaultOverlayable.WEST);
        overlayable.setOpaque(false);
        field.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
                adjustOverlay(field, overlayable);
            }

            public void focusGained(FocusEvent e) {
                adjustOverlay(field, overlayable);
            }
        });
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                adjustOverlay(field, overlayable);
            }

            public void removeUpdate(DocumentEvent e) {
                adjustOverlay(field, overlayable);
            }

            public void changedUpdate(DocumentEvent e) {
                adjustOverlay(field, overlayable);
            }
        });
        add(overlayable);
        if (button != null) {
            add(button, BorderLayout.AFTER_LINE_ENDS);
        }
    }

    private void adjustOverlay(JTextField field, DefaultOverlayable overlayable) {
        if (field.hasFocus()) {
            overlayable.setOverlayVisible(false);
        }
        else {
            String text = field.getText();
            if (text != null && text.length() != 0) {
                overlayable.setOverlayVisible(false);
            }
            else {
                overlayable.setOverlayVisible(true);
            }
        }
    }

    /**
     * Creates a text field. By default it will return a JTextField with opaque set to false. Subclass can override this
     * method to create their own text field such as JFormattedTextField.
     *
     * @return a text field.
     */
    protected JTextField createTextField() {
        JTextField textField = new OverlayTextField();
        SelectAllUtils.install(textField);
        JideSwingUtilities.setTextComponentTransparent(textField);
        textField.setColumns(20);
        return textField;
    }

    /**
     * Creates a context menu. The context menu will be shown when user clicks on the label.
     *
     * @return a context menu.
     */
    protected JidePopupMenu createContextMenu() {
        return new JidePopupMenu();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        Border textFieldBorder = UIDefaultsLookup.getBorder("TextField.border");
        if (textFieldBorder != null) {
            boolean big = textFieldBorder.getBorderInsets(this).top >= 2;
            if (big)
                setBorder(textFieldBorder);
            else
                setBorder(BorderFactory.createCompoundBorder(textFieldBorder, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        }
        else {
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        }
        if (isEnabled()) {
            LookAndFeel.installColors(this, "TextField.background", "TextField.foreground");
        }
        else {
            LookAndFeel.installColors(this, "TextField.inactiveBackground", "TextField.foreground");
        }
        if (textFieldBorder != null && _textField != null) {
            _textField.setBorder(BorderFactory.createEmptyBorder());
        }
    }

    /**
     * Creates the button that appears after the text field. By default it returns null so there is no button. Subclass
     * can override it to create their own button. A typical usage of this is to create a browse button to browse a file
     * or directory.
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
     * @param text the text that appears before the text field.
     */
    public void setLabelText(String text) {
        _labelText = text;
        if (_label != null) {
            _label.setText(text);
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
     * @param icon the icon that appears before the text field.
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
     * @param columns the number of columns for this text field.
     */
    public void setColumns(int columns) {
        if (getTextField() != null) {
            getTextField().setColumns(columns);
        }
    }

    /**
     * Sets the text in this TextField.
     *
     * @param text the new text in this TextField.
     */
    public void setText(String text) {
        if (getTextField() != null) {
            getTextField().setText(text);
        }
    }

    /**
     * Gets the text in this TextField.
     *
     * @return the text in this TextField.
     */
    public String getText() {
        if (getTextField() != null) {
            return getTextField().getText();
        }
        else {
            return null;
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

    @Override
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
            setBackground(UIDefaultsLookup.getColor("TextField.background"));
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
            setBackground(UIDefaultsLookup.getColor("control"));
        }
    }

    public int getBaseline(int width, int height) {
        if (SystemInfo.isJdk6Above()) {
            try {
                Method method = Component.class.getMethod("getBaseline", new Class[]{int.class, int.class});
                Object value = method.invoke(_textField, width, height);
                if (value instanceof Integer) {
                    return (Integer) value;
                }
            }
            catch (NoSuchMethodException e) {
                // ignore
            }
            catch (IllegalAccessException e) {
                // ignore
            }
            catch (InvocationTargetException e) {
                // ignore
            }
        }
        return -1;
    }

    /**
     * Gets the hint text when the field is empty and not focused.
     *
     * @return the hint text.
     */
    public String getHintText() {
        return _hintText;
    }

    /**
     * Sets the hint text.
     *
     * @param hintText the new hint text.
     */
    public void setHintText(String hintText) {
        _hintText = hintText;
        if (_hintLabel != null) {
            _hintLabel.setText(_hintText);
        }
    }

    /**
     * Gets the PopupMenuCustomizer.
     *
     * @return the PopupMenuCustomizer.
     */
    public PopupMenuCustomizer getPopupMenuCustomizer() {
        return _customizer;
    }

    /**
     * Sets the PopupMenuCustomizer. PopupMenuCustomizer can be used to do customize the popup menu for the
     * <code>LabeledTextField</code>.
     * <p/>
     * PopupMenuCustomizer has a customize method. The popup menu of this menu will be passed in. You can
     * add/remove/change the menu items in customize method. For example,
     * <code><pre>
     * field.setPopupMenuCustomzier(new LabeledTextField.PopupMenuCustomizer() {
     *     void customize(LabledTextField field, JPopupMenu menu) {
     *         menu.removeAll();
     *         menu.add(new JMenuItem("..."));
     *         menu.add(new JMenuItem("..."));
     *     }
     * }
     * </pre></code>
     * If the menu is never used, the two add methods will never be called thus improve the performance.
     *
     * @param customizer the PopupMenuCustomizer
     */
    public void setPopupMenuCustomizer(PopupMenuCustomizer customizer) {
        _customizer = customizer;
    }

    /**
     * Gets the keystroke that will bring up the context menu. If you never set it before, it will return SHIFT-F10 for
     * operating systems other than Mac OS X.
     *
     * @return the keystroke that will bring up the context menu.
     */
    public KeyStroke getContextMenuKeyStroke() {
        if (_contextMenuKeyStroke == null) {
            _contextMenuKeyStroke = !SystemInfo.isMacOSX() ? KeyStroke.getKeyStroke(KeyEvent.VK_F10, KeyEvent.SHIFT_MASK) : null;
        }
        return _contextMenuKeyStroke;
    }

    /**
     * Changes the keystroke that brings up the context menu which is normally shown when user clicks on the label icon
     * before the text field.
     *
     * @param contextMenuKeyStroke the new keystroke to bring up the context menu.
     */
    public void setContextMenuKeyStroke(KeyStroke contextMenuKeyStroke) {
        if (_contextMenuKeyStroke != null) {
            unregisterContextMenuKeyStroke(_contextMenuKeyStroke);
        }
        _contextMenuKeyStroke = contextMenuKeyStroke;
        if (_contextMenuKeyStroke != null) {
            registerContextMenuKeyStroke(_contextMenuKeyStroke);
        }
    }
}
