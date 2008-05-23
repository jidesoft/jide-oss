package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.JideSwingUtilities;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalComboBoxUI;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BasicJideComboBoxUI extends MetalComboBoxUI {
    private boolean _editable;

    public static BasicJideComboBoxUI createUI(JComponent c) {
        return new BasicJideComboBoxUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        _editable = comboBox.isEditable();
        comboBox.setEditable(true);
        JideSwingUtilities.installBorder(comboBox, createComboBoxBorder());
    }

    protected BasicJideComboBoxBorder createComboBoxBorder() {
        return new BasicJideComboBoxBorder();
    }


    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
        comboBox.setEditable(_editable);
        LookAndFeel.uninstallBorder(comboBox);
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        if (_rolloverListener == null) {
            _rolloverListener = createRolloverListener();
        }
        comboBox.addMouseListener(_rolloverListener);
    }

    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        comboBox.removeMouseListener(_rolloverListener);
        _rolloverListener = null;
    }

    protected RolloverListener createRolloverListener() {
        return new RolloverListener();
    }

    @Override
    protected JButton createArrowButton() {
        JButton button = new BasicJideComboBoxButton(
                comboBox, new BasicJideComboBoxIcon(),
                comboBox.isEditable(),
                currentValuePane, listBox);
        button.setMargin(new Insets(1, 3, 0, 4));
        button.setFocusPainted(comboBox.isEditable());
        button.addMouseListener(_rolloverListener);
        return button;
    }

    @Override
    public void unconfigureArrowButton() {
        super.unconfigureArrowButton();
        arrowButton.removeMouseListener(_rolloverListener);
    }

    @Override
    public void unconfigureEditor() {
        super.unconfigureEditor();
        editor.removeMouseListener(_rolloverListener);
        editor.removeFocusListener(_rolloverListener);
    }

    @Override
    public void configureEditor() {
        if (editor instanceof JComponent) {
            ((JComponent) editor).setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        }
        editor.addMouseListener(_rolloverListener);
        editor.addFocusListener(_rolloverListener);
    }

    // This is here because of a bug in the compiler.
    // When a protected-inner-class-savvy compiler comes out we
    // should move this into MetalComboBoxLayoutManager.
    @Override
    public void layoutComboBox(Container parent, MetalComboBoxLayoutManager manager) {
        if (arrowButton != null) {
            if (arrowButton instanceof BasicJideComboBoxButton) {
                Icon icon = ((BasicJideComboBoxButton) arrowButton).getComboIcon();
                Insets buttonInsets = arrowButton.getInsets();
                Insets insets = comboBox.getInsets();
                int buttonWidth = icon.getIconWidth() + buttonInsets.left +
                        buttonInsets.right;
                arrowButton.setBounds(
                        comboBox.getComponentOrientation().isLeftToRight()
                                ? (comboBox.getWidth() - insets.right - buttonWidth)
                                : insets.left,
                        insets.top, buttonWidth,
                        comboBox.getHeight() - insets.top - insets.bottom);
            }
            else {
                Insets insets = comboBox.getInsets();
                int width = comboBox.getWidth();
                int height = comboBox.getHeight();
                arrowButton.setBounds(
                        insets.left, insets.top,
                        width - (insets.left + insets.right),
                        height - (insets.top + insets.bottom));
            }
        }

        if (editor != null) {
            Rectangle cvb = rectangleForCurrentValue();
            editor.setBounds(cvb);
        }
    }

    public boolean isRollOver() {
        return _rollOver || (editor != null ? editor.hasFocus() : hasFocus);
    }

    public void setRollOver(boolean rollOver) {
        _rollOver = rollOver;
    }

    protected class BasicJideComboBoxBorder extends AbstractBorder implements UIResource {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color old = g.getColor();
            JComboBox box = (JComboBox) c;
            if (box.isPopupVisible() || isRollOver()) {
                g.setColor(UIDefaultsLookup.getColor("JideButton.borderColor"));
            }
            else {
                g.setColor(UIDefaultsLookup.getColor("TextField.background"));
            }
            // draw default border
            g.drawRect(x, y, width - 1, height - 1);

            g.setColor(old);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = 1;
            return insets;
        }
    }

    private boolean _rollOver = false;
    protected RolloverListener _rolloverListener;

    protected class RolloverListener extends MouseAdapter implements FocusListener {
        @Override
        public void mouseEntered(MouseEvent e) {
            setRollOver(true);
            comboBox.repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (comboBox.isPopupVisible()) {
                // this might be the ugliest hack I've ever written
                // so here is an explanation:
                // when a popup is visible, we need to show the white background until the
                // popup is closed if, however, the mouse re-enters, we do not want to reset the background color
                // In order to get around these problems we'll change the background to white, but a different instance
                // than HOVER_COLOR and perform an identity check when the popup closes.
                // If the background color still is not HOVER_COLOR (which it'd be if the mouse had re-entered meanwhile)
                // then we can override it with DEFAULT_COLOR.
                final PopupMenuListener l = new PopupMenuListener() {
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                        // it already is visible
                    }

                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                        Component comp = comboBox.getEditor().getEditorComponent();
                        setRollOver(false);
                        ((JPopupMenu) popup).removePopupMenuListener(this);
                    }

                    public void popupMenuCanceled(PopupMenuEvent e) {
                    }
                };
                ((JPopupMenu) popup).addPopupMenuListener(l);
                setRollOver(true);
            }
            else {
                setRollOver(false);
            }
            comboBox.repaint();
        }

        public void focusGained(FocusEvent e) {
            comboBox.repaint();
        }

        public void focusLost(FocusEvent e) {
            comboBox.repaint();
        }
    }

    protected class BasicJideComboBoxIcon implements Icon {
        public void paintIcon(Component c, Graphics g, int x, int y) {
            JideSwingUtilities.paintArrow(g, c.getForeground(), x, y, 5, SwingConstants.HORIZONTAL);
        }

        public int getIconWidth() {
            return 5;
        }

        public int getIconHeight() {
            return 3;
        }
    }
}
