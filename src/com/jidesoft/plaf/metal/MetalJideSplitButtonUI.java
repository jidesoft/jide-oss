/*
 * @(#)MetalJideSplitButtonUI.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.metal;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.LazyActionMap;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.plaf.basic.UIAction;
import com.jidesoft.swing.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * MetalJideSplitButtonUI implementation
 */
public class MetalJideSplitButtonUI extends MetalMenuUI {

    protected ThemePainter _painter;

    protected Color _shadowColor;
    protected Color _darkShadowColor;
    protected Color _highlight;
    protected Color _lightHighlightColor;

    private int _splitButtonMargin = 12;
    private int _splitButtonMarginOnMenu = 18;

    protected PropertyChangeListener _propertyChangeListener;
    private FocusListener _focusListener;

    @Override
    protected String getPropertyPrefix() {
        return "JideSplitButton";
    }

    public static ComponentUI createUI(JComponent c) {
        return new MetalJideSplitButtonUI();
    }

    @Override
    protected void installDefaults() {
        _painter = (ThemePainter) UIDefaultsLookup.get("Theme.painter");

        _shadowColor = UIDefaultsLookup.getColor("controlShadow");
        _darkShadowColor = UIDefaultsLookup.getColor("controlDkShadow");
        _highlight = UIDefaultsLookup.getColor("controlHighlight");
        _lightHighlightColor = UIDefaultsLookup.getColor("controlLtHighlight");

        super.installDefaults();
    }

    @Override
    protected void uninstallDefaults() {
        _painter = null;

        _shadowColor = null;
        _highlight = null;
        _lightHighlightColor = null;
        _darkShadowColor = null;

        super.uninstallDefaults();
    }

    @Override
    protected void installListeners() {
        if (_propertyChangeListener == null)
            _propertyChangeListener = createSplitButtonPropertyChangeListener(menuItem);

        if (_propertyChangeListener != null)
            menuItem.addPropertyChangeListener(_propertyChangeListener);
        super.installListeners();
        if (_focusListener == null) {
            _focusListener = new FocusListener() {
                public void focusGained(FocusEvent e) {
                    menuItem.repaint();
                }

                public void focusLost(FocusEvent e) {
                    menuItem.repaint();
                }
            };
        }
        menuItem.addFocusListener(_focusListener);
    }

    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();

        if (_propertyChangeListener != null)
            menuItem.removePropertyChangeListener(_propertyChangeListener);
        _propertyChangeListener = null;

        if (_focusListener != null) {
            menuItem.removeFocusListener(_focusListener);
        }
        _focusListener = null;
    }

    protected PropertyChangeListener createSplitButtonPropertyChangeListener(JComponent c) {
        return new PropertyChangeHandler();
    }


    /**
     * Returns the ui that is of type <code>klass</code>, or null if one can not be found.
     */
    static Object getUIOfType(ComponentUI ui, Class klass) {
        if (klass.isInstance(ui)) {
            return ui;
        }
        return null;
    }

    /**
     * Returns the InputMap for condition <code>condition</code>. Called as part of
     * <code>installKeyboardActions</code>.
     */
    public InputMap getInputMap(int condition, JComponent c) {
        if (condition == JComponent.WHEN_FOCUSED) {
            MetalJideSplitButtonUI ui = (MetalJideSplitButtonUI) getUIOfType(
                    ((JideSplitButton) c).getUI(), MetalJideSplitButtonUI.class);
            if (ui != null) {
                return (InputMap) UIDefaultsLookup.get(ui.getPropertyPrefix() + ".focusInputMap");
            }
        }
        return null;
    }

    @Override
    protected void installKeyboardActions() {
        super.installKeyboardActions();
        AbstractButton b = menuItem;

        LazyActionMap.installLazyActionMap(b, MetalJideSplitButtonUI.class,
                "JideSplitButton.actionMap");

        InputMap km = getInputMap(JComponent.WHEN_FOCUSED, b);

        SwingUtilities.replaceUIInputMap(b, JComponent.WHEN_FOCUSED, km);
    }

    @Override
    protected void uninstallKeyboardActions() {
        AbstractButton b = menuItem;
        SwingUtilities.replaceUIInputMap(b, JComponent.
                WHEN_IN_FOCUSED_WINDOW, null);
        SwingUtilities.replaceUIInputMap(b, JComponent.WHEN_FOCUSED, null);
        SwingUtilities.replaceUIActionMap(b, null);
        super.uninstallKeyboardActions();
    }

    @Override
    protected MouseInputListener createMouseInputListener(JComponent c) {
        return new MouseInputHandler();
    }

    @Override
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
        ButtonModel model = menuItem.getModel();
        int menuWidth = 0;
        int menuHeight = 0;
        if (JideSwingUtilities.getOrientationOf(menuItem) == SwingConstants.HORIZONTAL) {
            menuWidth = menuItem.getWidth();
            menuHeight = menuItem.getHeight();
        }
        else {
            menuWidth = menuItem.getHeight();
            menuHeight = menuItem.getWidth();
        }

        if (!((JMenu) menuItem).isTopLevelMenu()) {
            super.paintBackground(g, menuItem, bgColor);
            if (menuItem.isEnabled()) {
                if (model.isArmed() || model.isPressed() || isMouseOver()) {
                    g.setColor(selectionForeground);
                    g.drawLine(menuWidth - _splitButtonMarginOnMenu, 0, menuWidth - _splitButtonMarginOnMenu, menuHeight - 2);
                    JideSwingUtilities.paintArrow(g, selectionForeground, menuWidth - _splitButtonMarginOnMenu / 2 - 2, menuHeight / 2 - 3, 7, SwingConstants.VERTICAL);
                }
                else {
                    g.setColor(menuItem.getForeground());
                    g.drawLine(menuWidth - _splitButtonMarginOnMenu, 0, menuWidth - _splitButtonMarginOnMenu, menuHeight - 2);
                    JideSwingUtilities.paintArrow(g, menuItem.getForeground(), menuWidth - _splitButtonMarginOnMenu / 2 - 2, menuHeight / 2 - 3, 7, SwingConstants.VERTICAL);
                }
            }
            else {
                g.setColor(UIDefaultsLookup.getColor("controlDkShadow"));
                g.drawLine(menuWidth - _splitButtonMarginOnMenu, 0, menuWidth - _splitButtonMarginOnMenu, menuHeight - 2);
                JideSwingUtilities.paintArrow(g, UIDefaultsLookup.getColor("controlDkShadow"), menuWidth - _splitButtonMarginOnMenu / 2 - 2, menuHeight / 2 - 3, 7, SwingConstants.VERTICAL);
            }
            return;
        }

        if (menuItem.isOpaque()) {
            if (menuItem.getParent() != null) {
                g.setColor(menuItem.getParent().getBackground());
            }
            else {
                g.setColor(menuItem.getBackground());
            }
            g.fillRect(0, 0, menuWidth, menuHeight);
        }

        JideSplitButton b = (JideSplitButton) menuItem;
        if (menuItem instanceof ButtonStyle && ((ButtonStyle) menuItem).getButtonStyle() == ButtonStyle.TOOLBAR_STYLE) {
            if ((menuItem instanceof JMenu && model.isSelected())) {
                // Draw a dark shadow border without bottom
                getPainter().paintSelectedMenu(menuItem, g, new Rectangle(0, 0, menuWidth, menuHeight), JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_SELECTED);
            }
            else if (model.isArmed() || model.isPressed()) {
                Rectangle rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                if (((JideSplitButton) menuItem).isButtonEnabled()) {
                    getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_PRESSED);
                }
                rect = new Rectangle(menuWidth - _splitButtonMargin - 1 + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_ROLLOVER);
            }
            else if (model instanceof SplitButtonModel && ((DefaultSplitButtonModel) model).isButtonSelected()) {
                if ((isMouseOver() || b.hasFocus()) && model.isEnabled()) {
                    Rectangle rect = new Rectangle(menuWidth - _splitButtonMargin - 1 + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                    getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_ROLLOVER);
                    rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                    if (((JideSplitButton) menuItem).isButtonEnabled()) {
                        getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_PRESSED);
                    }
                }
                else {
                    Rectangle rect = new Rectangle(menuWidth - _splitButtonMargin - 1 + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                    getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_DEFAULT);
                    rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                    getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_SELECTED);
                }
            }
            else {
                if ((isMouseOver() || b.hasFocus()) && model.isEnabled()) {
                    if (isAlwaysDropdown(menuItem)) {
                        Rectangle rect = new Rectangle(0, 0, menuWidth, menuHeight);
                        getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_ROLLOVER);
                    }
                    else {
                        // Draw a line border with background
                        Rectangle rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                        if (((JideSplitButton) menuItem).isButtonEnabled()) {
                            getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_ROLLOVER);
                        }
                        rect = new Rectangle(menuWidth - _splitButtonMargin - 1 + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                        getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_ROLLOVER);
                    }
                }
            }
        }
        else if (menuItem instanceof ButtonStyle && ((ButtonStyle) menuItem).getButtonStyle() == ButtonStyle.FLAT_STYLE) {
            if ((menuItem instanceof JMenu && model.isSelected())) {
                // Draw a dark shadow border without bottom
                getPainter().paintSelectedMenu(menuItem, g, new Rectangle(0, 0, menuWidth, menuHeight), JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_SELECTED);
            }
            else if (model.isArmed() || model.isPressed()) {
                Rectangle rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                if (((JideSplitButton) menuItem).isButtonEnabled()) {
                    JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);
                }
                rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);

                if (!b.isOpaque()) {
                    rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                    paintSunkenBorder(g, rect);
                    rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                    paintRaisedBorder(g, rect);
                }
            }
            else if (model instanceof SplitButtonModel && ((DefaultSplitButtonModel) model).isButtonSelected()) {
                if ((isMouseOver() || b.hasFocus()) && model.isEnabled()) {
                    Rectangle rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                    JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);
                    rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                    if (((JideSplitButton) menuItem).isButtonEnabled()) {
                        JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);
                    }
                    if (!b.isOpaque()) {
                        rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                        paintSunkenBorder(g, rect);
                        rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                        paintRaisedBorder(g, rect);
                    }
                }
                else {
                    Rectangle rect = null;
                    if (b.isOpaque()) {
                        rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                        JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);
                    }
                    rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                    JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);

                    if (!b.isOpaque()) {
                        rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                        paintSunkenBorder(g, rect);
//                        rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
//                        paintRaisedBorder(g, rect);
                    }
                }

            }
            else {
                if ((isMouseOver() || b.hasFocus()) && model.isEnabled()) {
                    // Draw a line border with background
                    Rectangle rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                    if (((JideSplitButton) menuItem).isButtonEnabled()) {
                        JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);
                    }
                    rect = new Rectangle(menuWidth - _splitButtonMargin - getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                    JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);

                    rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                    paintRaisedBorder(g, rect);
                    rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                    paintRaisedBorder(g, rect);
                }
                else {
                    if (b.isOpaque()) {
                        Rectangle rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                        if (((JideSplitButton) menuItem).isButtonEnabled()) {
                            getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_DEFAULT);
                        }
                        rect = new Rectangle(menuWidth - _splitButtonMargin - 1 + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                        getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_DEFAULT);
                    }
                }
            }
        }
        else if (menuItem instanceof ButtonStyle && ((ButtonStyle) menuItem).getButtonStyle() == ButtonStyle.TOOLBOX_STYLE) {
            if ((menuItem instanceof JMenu && model.isSelected())) {
                // Draw a dark shadow border without bottom
                getPainter().paintSelectedMenu(menuItem, g, new Rectangle(0, 0, menuWidth, menuHeight), JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_SELECTED);
            }
            else if (model.isArmed() || model.isPressed()) {
                Rectangle rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                if (((JideSplitButton) menuItem).isButtonEnabled()) {
                    getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_PRESSED);
                }
                rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_ROLLOVER);

                if (!b.isOpaque()) {
                    rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                    paintSunken2Border(g, rect);
                    rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                    paintRaisedBorder(g, rect);
                }
            }
            else if (model instanceof SplitButtonModel && ((DefaultSplitButtonModel) model).isButtonSelected()) {
                if (isMouseOver() && model.isEnabled()) {
                    Rectangle rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                    getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_ROLLOVER);
                    rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                    if (((JideSplitButton) menuItem).isButtonEnabled()) {
                        getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_PRESSED);
                    }
                    if (!b.isOpaque()) {
                        rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                        paintSunken2Border(g, rect);
                        rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                        paintRaisedBorder(g, rect);
                    }
                }
                else {
                    Rectangle rect = null;
                    if (b.isOpaque()) {
                        rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                        getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_DEFAULT);
                    }
                    rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                    getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_SELECTED);

                    if (!b.isOpaque()) {
                        rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                        paintSunken2Border(g, rect);
                        rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                        paintRaisedBorder(g, rect);
                    }
                }

            }
            else {
                if (isMouseOver() && model.isEnabled()) {
                    // Draw a line border with background
                    Rectangle rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                    if (((JideSplitButton) menuItem).isButtonEnabled()) {
                        getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_ROLLOVER);
                    }
                    rect = new Rectangle(menuWidth - _splitButtonMargin - getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                    getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_ROLLOVER);

                    rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                    paintRaised2Border(g, rect);
                    rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                    paintRaised2Border(g, rect);
                }
                else {
                    if (b.isOpaque()) {
                        Rectangle rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                        if (((JideSplitButton) menuItem).isButtonEnabled()) {
                            getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_DEFAULT);
                        }
                        rect = new Rectangle(menuWidth - _splitButtonMargin - 1 + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                        getPainter().paintButtonBackground(menuItem, g, rect, JideSwingUtilities.getOrientationOf(menuItem), ThemePainter.STATE_DEFAULT);
                    }
                    else {
                        Rectangle rect = new Rectangle(0, 0, menuWidth - _splitButtonMargin, menuHeight);
                        paintRaisedBorder(g, rect);
                        rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
                        paintRaisedBorder(g, rect);
                    }
                }
            }
        }

        if (menuItem.isEnabled()) {
            JideSwingUtilities.paintArrow(g, menuItem.getForeground(), menuWidth - 10, menuHeight / 2 - 1, 5, SwingConstants.HORIZONTAL);
        }
        else {
            JideSwingUtilities.paintArrow(g, UIDefaultsLookup.getColor("controlDkShadow"), menuWidth - 10, menuHeight / 2 - 1, 5, SwingConstants.HORIZONTAL);
        }
    }

    private void paintSunkenBorder(Graphics g, Rectangle b) {
        Color old = g.getColor();
        g.setColor(_shadowColor);    // inner 3D border
        g.drawLine(b.x, b.y, b.x + b.width - 1, b.y);
        g.drawLine(b.x, b.y, b.x, b.y + b.height - 1);

        g.setColor(_lightHighlightColor);     // black drop shadow  __|
        g.drawLine(b.x, b.y + b.height - 1, b.x + b.width - 1, b.y + b.height - 1);
        g.drawLine(b.x + b.width - 1, b.y, b.x + b.width - 1, b.y + b.height - 1);
        g.setColor(old);
    }

    private void paintSunken2Border(Graphics g, Rectangle b) {
        Color old = g.getColor();
        g.setColor(_darkShadowColor);    // inner 3D border
        g.drawLine(b.x, b.y, b.x + b.width - 2, b.y);
        g.drawLine(b.x, b.y, b.x, b.y + b.height - 2);

        g.setColor(_shadowColor);    // inner 3D border
        g.drawLine(b.x + 1, b.y + 1, b.x + b.width - 3, b.y + 1);
        g.drawLine(b.x + 1, b.y + 1, b.x + 1, b.y + b.height - 3);

        g.setColor(_lightHighlightColor);     // black drop shadow  __|
        g.drawLine(b.x, b.y + b.height - 1, b.x + b.width - 1, b.y + b.height - 1);
        g.drawLine(b.x + b.width - 1, b.x, b.x + b.width - 1, b.y + b.height - 1);
        g.setColor(old);
    }

    private void paintRaised2Border(Graphics g, Rectangle b) {
        Color old = g.getColor();
        g.setColor(_lightHighlightColor);    // inner 3D border
        g.drawLine(b.x, b.y, b.x + b.width - 1, b.y);
        g.drawLine(b.x, b.y, b.x, b.y + b.height - 1);

        g.setColor(_shadowColor);     // gray drop shadow  __|
        g.drawLine(b.x + 1, b.y + b.height - 2, b.x + b.width - 2, b.y + b.height - 2);
        g.drawLine(b.x + b.width - 2, 1, b.x + b.width - 2, b.y + b.height - 2);

        g.setColor(_darkShadowColor);     // black drop shadow  __|
        g.drawLine(b.x, b.y + b.height - 1, b.x + b.width - 1, b.y + b.height - 1);
        g.drawLine(b.x + b.width - 1, b.y, b.x + b.width - 1, b.y + b.height - 1);
        g.setColor(old);
    }

    private void paintRaisedBorder(Graphics g, Rectangle b) {
        Color old = g.getColor();
        g.setColor(_lightHighlightColor);    // inner 3D border
        g.drawLine(b.x, b.y, b.x + b.width - 1, b.y);
        g.drawLine(b.x, b.y, b.x, b.y + b.height - 1);

        g.setColor(_shadowColor);     // black drop shadow  __|
        g.drawLine(b.x, b.y + b.height - 1, b.x + b.width - 1, b.y + b.height - 1);
        g.drawLine(b.x + b.width - 1, b.y, b.x + b.width - 1, b.y + b.height - 1);
        g.setColor(old);
    }

    @Override
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
        ButtonModel model = menuItem.getModel();
        FontMetrics fm = g.getFontMetrics();
        int mnemIndex = menuItem.getDisplayedMnemonicIndex();

        if (!model.isEnabled() || !((JideSplitButton) menuItem).isButtonEnabled()) {
            // *** paint the text disabled
            if (UIDefaultsLookup.get("MenuItem.disabledForeground") instanceof Color) {
                g.setColor(UIDefaultsLookup.getColor("MenuItem.disabledForeground"));
                JideSwingUtilities.drawStringUnderlineCharAt(menuItem, g, text, mnemIndex,
                        textRect.x,
                        textRect.y + fm.getAscent());
            }
            else {
                g.setColor(menuItem.getBackground().brighter());
                JideSwingUtilities.drawStringUnderlineCharAt(menuItem, g, text, mnemIndex,
                        textRect.x,
                        textRect.y + fm.getAscent());
                g.setColor(menuItem.getBackground().darker());
                JideSwingUtilities.drawStringUnderlineCharAt(menuItem, g, text, mnemIndex,
                        textRect.x - 1,
                        textRect.y + fm.getAscent() - 1);
            }
        }
        else {
            // *** paint the text normally
            if (model.isArmed() || (menuItem instanceof JMenu && model.isSelected())) {
                g.setColor(selectionForeground); // Uses protected field.
            }
            JideSwingUtilities.drawStringUnderlineCharAt(menuItem, g, text, mnemIndex,
                    textRect.x,
                    textRect.y + fm.getAscent());
        }
    }

    protected class MouseInputHandler implements MouseInputListener {
        public void mouseClicked(MouseEvent e) {
            cancelMenuIfNecessary(e);
        }

        /**
         * Invoked when the mouse has been clicked on the menu. This method clears or sets the selection path of the
         * MenuSelectionManager.
         *
         * @param e the mouse event
         */
        public void mousePressed(MouseEvent e) {
            JMenu menu = (JMenu) menuItem;
            if (!menu.isEnabled())
                return;

            setMouseOver(true);

            if (!SwingUtilities.isLeftMouseButton(e)) {
                return;
            }
            if (isClickOnButton(e, menu)) {
                if (((JideSplitButton) menuItem).isButtonEnabled()) {
                    // click button
                    menu.getModel().setArmed(true);
                    menu.getModel().setPressed(true);
                }
                if (!menu.hasFocus() && menu.isRequestFocusEnabled()) {
                    menu.requestFocus();
                }
            }
            else {
                downButtonPressed(menu);
            }
        }

        private boolean isClickOnButton(MouseEvent e, JMenu menu) {
            if (((JideSplitButton) menu).isAlwaysDropdown()) {
                return false;
            }

            boolean clickOnDropDown = false;
            int size = ((JMenu) menuItem).isTopLevelMenu() ? _splitButtonMargin : _splitButtonMarginOnMenu;
            if (JideSwingUtilities.getOrientationOf(menuItem) == SwingConstants.HORIZONTAL) {
                if (e.getPoint().getX() < menu.getWidth() - size) {
                    clickOnDropDown = true;
                }
            }
            else {
                if (e.getPoint().getY() < menu.getHeight() - size) {
                    clickOnDropDown = true;
                }
            }
            return clickOnDropDown;
        }

        /**
         * Invoked when the mouse has been released on the menu. Delegates the mouse event to the MenuSelectionManager.
         *
         * @param e the mouse event
         */
        public void mouseReleased(MouseEvent e) {
            JMenu menu = (JMenu) menuItem;
            if (!menu.isEnabled()) {
                return;
            }
            if (!isClickOnButton(e, menu)) {
                // these two lines order matters. In this order, it would not trigger actionPerformed.
                menuItem.getModel().setArmed(false);
                menuItem.getModel().setPressed(false);
            }
            cancelMenuIfNecessary(e);
        }

        private void cancelMenuIfNecessary(MouseEvent e) {
            JMenu menu = (JMenu) menuItem;
            if (!menu.isEnabled())
                return;
            if (isClickOnButton(e, menu)) {
                if (((JideSplitButton) menuItem).isButtonEnabled()) {
                    // click button
                    // these two lines order matters. In this order, it would trigger actionPerformed.
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        menu.getModel().setPressed(false);
                        menu.getModel().setArmed(false);
                    }
                    else {
                        menu.getModel().setArmed(false);
                        menu.getModel().setPressed(false);
                    }

                    MenuSelectionManager manager = MenuSelectionManager.defaultManager();
                    MenuElement[] menuElements = manager.getSelectedPath();
                    for (int i = menuElements.length - 1; i >= 0; i--) {
                        MenuElement menuElement = menuElements[i];
                        if (menuElement instanceof JPopupMenu && ((JPopupMenu) menuElement).isAncestorOf(menu)) {
                            menu.getModel().setRollover(false);
                            setMouseOver(false);
                            manager.clearSelectedPath();
                        }
                    }
                }
            }
            else {
//                MenuSelectionManager manager =
//                        MenuSelectionManager.defaultManager();
//                manager.processMouseEvent(e);
//                if (!e.isConsumed())
//                    manager.clearSelectedPath();
            }
        }

        /**
         * Invoked when the cursor enters the menu. This method sets the selected path for the MenuSelectionManager and
         * handles the case in which a menu item is used to pop up an additional menu, as in a hierarchical menu
         * system.
         *
         * @param e the mouse event; not used
         */
        public void mouseEntered(MouseEvent e) {
            JMenu menu = (JMenu) menuItem;
            if (!menu.isEnabled())
                return;

            MenuSelectionManager manager =
                    MenuSelectionManager.defaultManager();
            MenuElement selectedPath[] = manager.getSelectedPath();
            if (!menu.isTopLevelMenu()) {
                if (!(selectedPath.length > 0 &&
                        selectedPath[selectedPath.length - 1] ==
                                menu.getPopupMenu())) {
                    if (menu.getDelay() == 0) {
                        appendPath(getPath(), menu.getPopupMenu());
                    }
                    else {
                        manager.setSelectedPath(getPath());
                        setupPostTimer(menu);
                    }
                }
            }
            else {
                if (selectedPath.length > 0 &&
                        selectedPath[0] == menu.getParent()) {
                    MenuElement newPath[] = new MenuElement[3];
                    // A top level menu's parent is by definition
                    // a JMenuBar
                    newPath[0] = (MenuElement) menu.getParent();
                    newPath[1] = menu;
                    newPath[2] = menu.getPopupMenu();
                    manager.setSelectedPath(newPath);
                }
            }

            if (!SwingUtilities.isLeftMouseButton(e)) {
                setMouseOver(true);
            }
            menuItem.repaint();
        }

        public void mouseExited(MouseEvent e) {
            setMouseOver(false);
            menuItem.repaint();
        }

        /**
         * Invoked when a mouse button is pressed on the menu and then dragged. Delegates the mouse event to the
         * MenuSelectionManager.
         *
         * @param e the mouse event
         * @see java.awt.event.MouseMotionListener#mouseDragged
         */
        public void mouseDragged(MouseEvent e) {
            JMenu menu = (JMenu) menuItem;
            if (!menu.isEnabled())
                return;
            MenuSelectionManager.defaultManager().processMouseEvent(e);
        }

        public void mouseMoved(MouseEvent e) {
        }
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        if (!(c instanceof JMenu) || !((JMenu) c).isTopLevelMenu()) {
            return super.getMinimumSize(c);
        }

        Dimension d = getPreferredSize(c);
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            if (JideSwingUtilities.getOrientationOf(c) == SwingConstants.HORIZONTAL)
                d.width -= v.getPreferredSpan(View.X_AXIS) - v.getMinimumSpan(View.X_AXIS);
            else        // TODO: not sure if this is correct
                d.height -= v.getPreferredSpan(View.X_AXIS) - v.getMinimumSpan(View.X_AXIS);
        }

        int size = ((JMenu) menuItem).isTopLevelMenu() ? _splitButtonMargin : _splitButtonMarginOnMenu;
        d.width += size;

        return d;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        if (!(c instanceof JMenu) || !((JMenu) c).isTopLevelMenu()) {
            return super.getPreferredSize(c);
        }

        AbstractButton b = (AbstractButton) c;

        boolean isHorizontal = true;
        if (JideSwingUtilities.getOrientationOf(c) == SwingConstants.VERTICAL) {
            isHorizontal = false;
        }

        // JDK PORTING HINT
        // JDK1.3: No getIconTextGap, use defaultTextIconGap
        Dimension d = BasicGraphicsUtils.getPreferredButtonSize(b, defaultTextIconGap);
//        d.width += b.getMargin().left + b.getMargin().right;
//        d.height += b.getMargin().bottom + b.getMargin().top;

        int size = ((JMenu) menuItem).isTopLevelMenu() ? _splitButtonMargin : _splitButtonMarginOnMenu;
        d.width += size;

        if (isHorizontal)
            return d;
        else
            return new Dimension(d.height, d.width); // swap width and height
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        if (!(c instanceof JMenu) || !((JMenu) c).isTopLevelMenu()) {
            return super.getMaximumSize(c);
        }

        Dimension d = getPreferredSize(c);
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            d.width += v.getMaximumSpan(View.X_AXIS) - v.getPreferredSpan(View.X_AXIS);
        }
        boolean isHorizontal = true;
        if (JideSwingUtilities.getOrientationOf(c) == SwingConstants.VERTICAL) {
            isHorizontal = false;
        }

        int size = ((JMenu) menuItem).isTopLevelMenu() ? _splitButtonMargin : _splitButtonMarginOnMenu;
        if (isHorizontal)
            d.width += size;
        else
            d.height += size;
        return d;
    }

    protected void paintIcon(JMenuItem b, Graphics g) {
        ButtonModel model = b.getModel();

        // Paint the Icon
        if (b.getIcon() != null) {
            // rotate back since we don't want to paint icon in a rotated way.
            if (JideSwingUtilities.getOrientationOf(b) == SwingConstants.VERTICAL) {
                g.translate(0, b.getWidth() - 1);
                ((Graphics2D) g).rotate(-Math.PI / 2);
            }
            Icon icon;
            if (!model.isEnabled()) {
                icon = b.getDisabledIcon();
                if (icon == null) {
                    icon = b.getIcon();
                    if (icon instanceof ImageIcon) {
                        icon = IconsFactory.createGrayImage(((ImageIcon) icon).getImage());
                    }
                    else {
                        icon = IconsFactory.createGrayImage(b, icon);
                    }
                }
            }
            else if (model.isPressed() && model.isArmed()) {
                icon = b.getPressedIcon();
                if (icon == null) {
                    // Use default icon
                    icon = b.getIcon();
                }
            }
            else {
                icon = b.getIcon();
            }

            if (icon != null) {
                icon.paintIcon(b, g, iconRect.x, iconRect.y);
//                if (model.isRollover() && !model.isPressed() && !model.isSelected()) {
//                    icon.paintIcon(b, g, iconRect.x, iconRect.y);
//                }
//                else {
//                    icon.paintIcon(b, g, iconRect.x, iconRect.y);
//                }
            }

            if (JideSwingUtilities.getOrientationOf(b) == SwingConstants.VERTICAL) {
                ((Graphics2D) g).rotate(Math.PI / 2);
                g.translate(0, -b.getHeight() + 1);
            }
        }
    }

    protected int getOffset() {
        return 0;
    }

    protected boolean isAlwaysDropdown(JMenuItem menuItem) {
        if (menuItem instanceof JideSplitButton) {
            return ((JideSplitButton) menuItem).isAlwaysDropdown();
        }
        else {
            return false;
        }
    }

    /**
     * Actions for Buttons. Two type of action are supported: pressed: Moves the button to a pressed state released:
     * Disarms the button.
     */
    private static class Actions extends UIAction {
        private static final String PRESS = "pressed";
        private static final String RELEASE = "released";
        private static final String DOWN_PRESS = "downPressed";
        private static final String DOWN_RELEASE = "downReleased";

        Actions(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            AbstractButton b = (AbstractButton) e.getSource();
            String key = getName();

            // if isAlwaysDropDown it true, treat PRESS as DOWN_PRESS
            if (PRESS.equals(key) && ((JideSplitButton) b).isAlwaysDropdown()) {
                key = DOWN_PRESS;
            }

            if (PRESS.equals(key)) {
                ButtonModel model = b.getModel();
                model.setArmed(true);
                model.setPressed(true);
                if (!b.hasFocus()) {
                    b.requestFocus();
                }
            }
            else if (RELEASE.equals(key)) {
                ButtonModel model = b.getModel();
                model.setPressed(false);
                model.setArmed(false);
            }
            else if (DOWN_PRESS.equals(key)) {
                downButtonPressed((JMenu) b);
            }
            else if (DOWN_RELEASE.equals(key)) {
            }
        }

        @Override
        public boolean isEnabled(Object sender) {
            if (sender != null && (sender instanceof AbstractButton) &&
                    !((AbstractButton) sender).getModel().isEnabled()) {
                return false;
            }
            else {
                return true;
            }
        }
    }

    /**
     * Populates Buttons actions.
     */
    public static void loadActionMap(LazyActionMap map) {
        map.put(new Actions(Actions.PRESS));
        map.put(new Actions(Actions.RELEASE));
        map.put(new Actions(Actions.DOWN_PRESS));
        map.put(new Actions(Actions.DOWN_RELEASE));
    }

    protected static void downButtonPressed(JMenu menu) {
        MenuSelectionManager manager = MenuSelectionManager.defaultManager();
        if (menu.isTopLevelMenu()) {
            if (menu.isSelected()) {
                manager.clearSelectedPath();
            }
            else {
                //Container cnt = menu.getParent();
                Container cnt = getFirstParentMenuElement(menu);

                if (cnt != null && cnt instanceof MenuElement) {
                    ArrayList<Component> parents = new ArrayList<Component>();
                    while (cnt instanceof MenuElement) {
                        parents.add(0, cnt);
                        if (cnt instanceof JPopupMenu) {
                            cnt = (Container) ((JPopupMenu) cnt).getInvoker();
                        }
                        else {
                            //cnt = cnt.getParent();
                            cnt = getFirstParentMenuElement(cnt);
                        }
                    }

                    MenuElement me[] = new MenuElement[parents.size() + 1];
                    for (int i = 0; i < parents.size(); i++) {
                        Container container = (Container) parents.get(i);
                        me[i] = (MenuElement) container;
                    }
                    me[parents.size()] = menu;
                    manager.setSelectedPath(me);
                }
                else {
                    MenuElement me[] = new MenuElement[1];
                    me[0] = menu;
                    manager.setSelectedPath(me);
                }
            }
        }

        MenuElement selectedPath[] = manager.getSelectedPath();
        if (selectedPath.length > 0 &&
                selectedPath[selectedPath.length - 1] != menu.getPopupMenu()) {
            if (menu.isTopLevelMenu() ||
                    menu.getDelay() == 0) {
                appendPath(selectedPath, menu.getPopupMenu());
            }
            else {
                setupPostTimer(menu);
            }
        }
    }

    protected static Container getFirstParentMenuElement(Component comp) {
        Container parent = comp.getParent();

        while (parent != null) {
            if (parent instanceof MenuElement)
                return parent;

            parent = parent.getParent();
        }

        return null;
    }

    private class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String prop = e.getPropertyName();
            if (prop.equals(ButtonStyle.BUTTON_STYLE_PROPERTY)) {
                AbstractButton b = (AbstractButton) e.getSource();
                b.repaint();
            }
        }
    }

    @Override
    protected int getRightMargin() {
        return _splitButtonMargin - 1;
    }

}
