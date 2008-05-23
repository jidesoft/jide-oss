package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import java.awt.*;

public class BasicJideComboBoxButton extends JButton {
    protected JComboBox _comboBox;
    protected JList _listBox;
    protected CellRendererPane _rendererPane;
    protected Icon _comboIcon;
    protected boolean _iconOnly = false;

    public final JComboBox getComboBox() {
        return _comboBox;
    }

    public final void setComboBox(JComboBox cb) {
        _comboBox = cb;
    }

    public final Icon getComboIcon() {
        return _comboIcon;
    }

    public final void setComboIcon(Icon i) {
        _comboIcon = i;
    }

    public final boolean isIconOnly() {
        return _iconOnly;
    }

    public final void setIconOnly(boolean isIconOnly) {
        _iconOnly = isIconOnly;
    }


    public BasicJideComboBoxButton() {
        super("");
        addMouseListener(new BasicJideButtonListener(this));
        // this is required so that the rollover state is always updated correctly.
        // remove it and the button will not be notified of a rollover-event in most cases.
        DefaultButtonModel model = new DefaultButtonModel() {
            @Override
            public void setArmed(boolean armed) {
                super.setArmed(isPressed() || armed);
            }
        };
        setModel(model);
        customizeButton();
    }

    public BasicJideComboBoxButton(
            JComboBox cb, Icon i,
            CellRendererPane pane, JList list) {
        this();
        _comboBox = cb;
        _comboIcon = i;
        _rendererPane = pane;
        _listBox = list;
        setEnabled(_comboBox.isEnabled());
    }

    protected void customizeButton() {
        setFocusable(false);
        setBorderPainted(false);
        setRequestFocusEnabled(false);
    }

    public BasicJideComboBoxButton(JComboBox comboBox, Icon icon, boolean editable,
                                   CellRendererPane currentValuePane, JList listBox) {
        this(comboBox, icon, currentValuePane, listBox);
        _iconOnly = editable;
    }


    @Override
    protected void paintComponent(Graphics g) {
        Color old = g.getColor();
        ThemePainter painter = (ThemePainter) UIDefaultsLookup.get("Theme.painter");
        if (getModel().isSelected() || getModel().isPressed() || _comboBox.isPopupVisible()) {
            // paint pressed style
            painter.paintButtonBackground(this, g, new Rectangle(0, 0, getWidth(), getHeight()), 0, ThemePainter.STATE_PRESSED, false);
        }
        else if (getModel().isRollover() || ((BasicJideComboBoxUI) _comboBox.getUI()).isRollOver()) {
            // paint rollover
            painter.paintButtonBackground(this, g, new Rectangle(0, 0, getWidth(), getHeight()), 0, ThemePainter.STATE_ROLLOVER, false);
        }
        else {
            // paint default
            painter.paintButtonBackground(this, g, new Rectangle(0, 0, getWidth(), getHeight()), 0, ThemePainter.STATE_DEFAULT, false);
        }
        if (((BasicJideComboBoxUI) _comboBox.getUI()).isRollOver() || _comboBox.isPopupVisible()) {
            // draw left border
            g.setColor(painter.getMenuItemBorderColor());
            g.drawLine(0, 0, 0, getHeight());
        }

        paintIcon(g);
        g.setColor(old);
    }

    protected void paintIcon(Graphics g) {
        Insets insets = getInsets();

        int width = getWidth() - (insets.left + insets.right);
        int height = getHeight() - (insets.top + insets.bottom);

        if (height <= 0 || width <= 0) {
            return;
        }

        int left = insets.left;
        int top = insets.top;
        int bottom = top + (height - 1);

        int iconWidth;
        int iconLeft;

        // Paint the icon
        if (_comboIcon != null) {
            iconWidth = _comboIcon.getIconWidth();
            int iconHeight = _comboIcon.getIconHeight();
            int iconTop;

            if (_iconOnly) {
                iconLeft = (getWidth() / 2) - (iconWidth / 2);
                iconTop = (getHeight() / 2) - (iconHeight / 2) - 1;
            }
            else {
                iconLeft = left;
                iconTop = (top + ((bottom - top) / 2)) - (iconHeight / 2);
            }
            _comboIcon.paintIcon(this, g, iconLeft, iconTop);
        }
    }
}
