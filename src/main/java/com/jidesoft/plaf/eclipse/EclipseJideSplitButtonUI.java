/*
 * @(#)EclipseJideSplitButtonUI.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.eclipse;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.LazyActionMap;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.plaf.basic.UIAction;
import com.jidesoft.swing.*;
import com.jidesoft.utils.SecurityUtils;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * EclipseJideSplitButtonUI implementation.
 */
public class EclipseJideSplitButtonUI extends EclipseMenuUI {

    protected ThemePainter _painter;

    protected Color _shadowColor;
    protected Color _darkShadowColor;
    protected Color _highlight;
    protected Color _lightHighlightColor;

    protected int _splitButtonMargin = 12;
    protected int _splitButtonMarginOnMenu = 20;

    private FocusListener _focusListener;

    private static final String propertyPrefix = "JideSplitButton";

    @Override
    protected String getPropertyPrefix() {
        return propertyPrefix;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new EclipseJideSplitButtonUI();
    }

    @Override
    protected void installDefaults() {
        _painter = (ThemePainter) UIDefaultsLookup.get("Theme.painter");
        _shadowColor = UIDefaultsLookup.getColor("controlShadow");
        _darkShadowColor = UIDefaultsLookup.getColor("controlDkShadow");
        _highlight = UIDefaultsLookup.getColor("controlHighlight");
        _lightHighlightColor = UIDefaultsLookup.getColor("controlLtHighlight");
        menuItem.setRolloverEnabled(true);

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
        if (_focusListener != null) {
            menuItem.removeFocusListener(_focusListener);
        }
    }

    /*
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
     *
     * @param condition the condition
     * @param c         the component
     * @return the input map.
     */
    public InputMap getInputMap(int condition, JComponent c) {
        if (condition == JComponent.WHEN_FOCUSED) {
            EclipseJideSplitButtonUI ui = (EclipseJideSplitButtonUI) getUIOfType(
                    ((JideSplitButton) c).getUI(), EclipseJideSplitButtonUI.class);
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

        LazyActionMap.installLazyActionMap(b, EclipseJideSplitButtonUI.class,
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

    /**
     * Gets the bounds for the button part of the <code>JideSplitButton</code>.
     *
     * @param c           the component. In this case, it is the <code>JideSplitButton</code>.
     * @param orientation the orientation.
     * @param width       the width of the <code>JideSplitButton</code>
     * @param height      the height of the <code>JideSplitButton</code>.
     * @return the bounds for the button part of the <code>JideSplitButton</code>.
     */
    protected Rectangle getButtonRect(JComponent c, int orientation, int width, int height) {
        Rectangle rect;
        if (orientation == SwingConstants.HORIZONTAL && c.getComponentOrientation().isLeftToRight()) {
            rect = new Rectangle(0, 0, width - _splitButtonMargin, height);
        }
        else {
            rect = new Rectangle(_splitButtonMargin - 1, 0, width - _splitButtonMargin, height);
        }
        return rect;
    }

    /**
     * Gets the bounds for the drop down part of the <code>JideSplitButton</code>.
     *
     * @param c           the component. In this case, it is the <code>JideSplitButton</code>.
     * @param orientation the orientation.
     * @param width       the width of the <code>JideSplitButton</code>
     * @param height      the height of the <code>JideSplitButton</code>.
     * @return the bounds for the drop down part of the <code>JideSplitButton</code>.
     */
    protected Rectangle getDropDownRect(JComponent c, int orientation, int width, int height) {
        Object position = c.getClientProperty(JideButton.CLIENT_PROPERTY_SEGMENT_POSITION);
        Rectangle rect;
        if (c.getComponentOrientation().isLeftToRight()) {
            rect = new Rectangle(width - _splitButtonMargin - 1 + getOffset(), 0, _splitButtonMargin - getOffset(), height);
        }
        else {
            rect = new Rectangle(0, 0, _splitButtonMargin - getOffset(), height);
        }
        if (position == null || JideButton.SEGMENT_POSITION_ONLY.equals(position)) {
        }
        else if (JideButton.SEGMENT_POSITION_FIRST.equals(position)) {
            if (orientation == SwingConstants.HORIZONTAL) {
                rect.width++;
            }
            else {
                rect.height++;
            }
        }
        else if (JideButton.SEGMENT_POSITION_MIDDLE.equals(position)) {
            if (orientation == SwingConstants.HORIZONTAL) {
                rect.width++;
            }
            else {
                rect.height++;
            }
        }
        else if (JideButton.SEGMENT_POSITION_LAST.equals(position)) {
        }
        return rect;
    }

    protected void paintSunkenBorder(Graphics g, Rectangle b) {
        Color old = g.getColor();
        g.setColor(_shadowColor);    // inner 3D border
        g.drawLine(b.x, b.y, b.x + b.width - 1, b.y);
        g.drawLine(b.x, b.y, b.x, b.y + b.height - 1);

        g.setColor(_lightHighlightColor);     // black drop shadow  __|
        g.drawLine(b.x, b.y + b.height - 1, b.x + b.width - 1, b.y + b.height - 1);
        g.drawLine(b.x + b.width - 1, b.y, b.x + b.width - 1, b.y + b.height - 1);
        g.setColor(old);
    }

    protected void paintSunken2Border(Graphics g, Rectangle b) {
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

    protected void paintRaisedBorder(Graphics g, Rectangle b) {
        Color old = g.getColor();
        g.setColor(_lightHighlightColor);    // inner 3D border
        g.drawLine(b.x, b.y, b.x + b.width - 1, b.y);
        g.drawLine(b.x, b.y, b.x, b.y + b.height - 1);

        g.setColor(_shadowColor);     // black drop shadow  __|
        g.drawLine(b.x, b.y + b.height - 1, b.x + b.width - 1, b.y + b.height - 1);
        g.drawLine(b.x + b.width - 1, b.y, b.x + b.width - 1, b.y + b.height - 1);
        g.setColor(old);
    }

    protected void paintRaised2Border(Graphics g, Rectangle b) {
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

    private Color getForegroundOfState(JMenuItem menuItem) {
        int state = JideSwingUtilities.getButtonState(menuItem);
        Color foreground = null;
        if (menuItem instanceof ComponentStateSupport) {
            foreground = ((ComponentStateSupport) menuItem).getForegroundOfState(state);
        }
        if (foreground == null || foreground instanceof UIResource) {
            foreground = menuItem.getForeground();
        }
        return foreground;
    }

    @Override
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
        ButtonModel model = menuItem.getModel();
        int menuWidth;
        int menuHeight;
        int orientation = JideSwingUtilities.getOrientationOf(menuItem);
        if (orientation == SwingConstants.HORIZONTAL) {
            menuWidth = menuItem.getWidth();
            menuHeight = menuItem.getHeight();
        }
        else {
            menuWidth = menuItem.getHeight();
            menuHeight = menuItem.getWidth();
        }
        // have to change to HORIZONTAL because we rotate the Graphics already
        orientation = SwingConstants.HORIZONTAL;

        boolean paintBackground;
        Object o = menuItem.getClientProperty("JideSplitButton.alwaysPaintBackground");
        if (o instanceof Boolean) {
            paintBackground = (Boolean) o;
        }
        else {
            paintBackground = menuItem.isOpaque();
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
                    g.setColor(getForegroundOfState(menuItem));
                    g.drawLine(menuWidth - _splitButtonMarginOnMenu, 0, menuWidth - _splitButtonMarginOnMenu, menuHeight - 2);
                    JideSwingUtilities.paintArrow(g, getForegroundOfState(menuItem), menuWidth - _splitButtonMarginOnMenu / 2 - 2, menuHeight / 2 - 3, 7, SwingConstants.VERTICAL);
                }
            }
            else {
                g.setColor(UIDefaultsLookup.getColor("controlDkShadow"));
                g.drawLine(menuWidth - _splitButtonMarginOnMenu, 0, menuWidth - _splitButtonMarginOnMenu, menuHeight - 2);
                JideSwingUtilities.paintArrow(g, UIDefaultsLookup.getColor("controlDkShadow"), menuWidth - _splitButtonMarginOnMenu / 2 - 2, menuHeight / 2 - 3, 7, SwingConstants.VERTICAL);
            }
            return;
        }

        if (paintBackground) {
            if (menuItem.getParent() != null) {
                g.setColor(menuItem.getParent().getBackground());
            }
            else {
                g.setColor(menuItem.getBackground());
            }
            g.fillRect(0, 0, menuWidth, menuHeight);
        }

        JideSplitButton b = (JideSplitButton) menuItem;
        if (b.getButtonStyle() == ButtonStyle.TOOLBAR_STYLE) {
            if ((model.isSelected())) {
                if (b.isAlwaysDropdown()) {
                    Rectangle rect = new Rectangle(0, 0, menuWidth, menuHeight);
                    getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_ROLLOVER);
                }
                else if (b.getClientProperty(JideButton.CLIENT_PROPERTY_SEGMENT_POSITION) != null) {
                    Rectangle rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                    if (b.isButtonEnabled()) {
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_ROLLOVER);
                    }
                    else if (paintBackground) {
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_DISABLE_ROLLOVER);
                    }
                    rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                    getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_PRESSED);
                }
                else {
                    Rectangle rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                    if (b.isButtonEnabled()) {
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_SELECTED);
                    }
                    else if (paintBackground) {
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_DISABLE_SELECTED);
                    }
                    rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                    getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_SELECTED);
                    getPainter().paintSelectedMenu(b, g, new Rectangle(0, 0, menuWidth, menuHeight), orientation, ThemePainter.STATE_SELECTED);
                }
            }
            else if (model.isArmed() || model.isPressed()) {
                Rectangle rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                if (b.isButtonEnabled()) {
                    getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_PRESSED);
                }
                else if (paintBackground) {
                    getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_DISABLE);
                }
                rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_ROLLOVER);
            }
            else if (model instanceof SplitButtonModel && ((DefaultSplitButtonModel) model).isButtonSelected()) {
                if ((isMouseOver() || b.hasFocus()) && model.isEnabled()) {
                    Rectangle rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                    getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_ROLLOVER);
                    rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                    if (b.isButtonEnabled()) {
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_PRESSED);
                    }
                    else if (paintBackground) {
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_DISABLE);
                    }
                }
                else {
                    Rectangle rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                    getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_DEFAULT);
                    rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                    if (b.isButtonEnabled()) {
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_SELECTED);
                    }
                    else if (paintBackground) {
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_DISABLE_SELECTED);
                    }
                }
            }
            else if (((b.isRolloverEnabled() && isMouseOver()) || b.hasFocus()) && model.isEnabled()) {
                if (b.isAlwaysDropdown()) {
                    Rectangle rect = new Rectangle(0, 0, menuWidth, menuHeight);
                    getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_ROLLOVER);
                }
                else {
                    // Draw a line border with background
                    Rectangle rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                    if (b.isButtonEnabled()) {
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_ROLLOVER);
                    }
                    else if (paintBackground) {
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_DISABLE_ROLLOVER);
                    }
                    rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                    getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_ROLLOVER);
                }
            }
            else {
                if (paintBackground) {
                    Rectangle rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                    if (b.isEnabled() && b.isButtonEnabled()) {
                        getPainter().paintButtonBackground(b, g, rect, 0, ThemePainter.STATE_DEFAULT);
                    }
                    else {
                        getPainter().paintButtonBackground(b, g, rect, 0, ThemePainter.STATE_DISABLE);
                    }
                    if ("true".equals(SecurityUtils.getProperty("shadingtheme", "false"))) {
                        JideSwingUtilities.fillGradient(g, rect, SwingConstants.HORIZONTAL);
                    }
                    rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                    if (b.isEnabled()) {
                        getPainter().paintButtonBackground(b, g, rect, 0, ThemePainter.STATE_DEFAULT);
                    }
                    else {
                        getPainter().paintButtonBackground(b, g, rect, 0, ThemePainter.STATE_DISABLE);
                    }
                    if ("true".equals(SecurityUtils.getProperty("shadingtheme", "false"))) {
                        JideSwingUtilities.fillGradient(g, rect, SwingConstants.HORIZONTAL);
                    }
                }
            }
        }
        else if (b.getButtonStyle() == ButtonStyle.FLAT_STYLE) {
            if ((model.isSelected())) {
                // Draw a dark shadow border without bottom
                getPainter().paintSelectedMenu(b, g, new Rectangle(0, 0, menuWidth, menuHeight), orientation, ThemePainter.STATE_SELECTED);
            }
            else if (model.isArmed() || model.isPressed()) {
                Rectangle rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                if (b.isButtonEnabled()) {
                    JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);
                }
                rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);

                if (!b.isOpaque()) {
                    rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                    paintSunkenBorder(g, rect);
                    rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                    paintRaisedBorder(g, rect);
                }
            }
            else if (model instanceof SplitButtonModel && ((DefaultSplitButtonModel) model).isButtonSelected()) {
                if ((isMouseOver() || b.hasFocus()) && model.isEnabled()) {
                    Rectangle rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                    JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);
                    rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                    if (b.isButtonEnabled()) {
                        JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);
                    }
                    if (!b.isOpaque()) {
                        rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                        paintSunkenBorder(g, rect);
                        rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                        paintRaisedBorder(g, rect);
                    }
                }
                else {
                    Rectangle rect;
                    if (b.isOpaque()) {
                        rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                        JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);
                    }
                    rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                    JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);

                    if (!b.isOpaque()) {
                        rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                        paintSunkenBorder(g, rect);
//                        rect = new Rectangle(menuWidth - _splitButtonMargin + getOffset(), 0, _splitButtonMargin - getOffset(), menuHeight);
//                        paintRaisedBorder(g, rect);
                    }
                }

            }
            else {
                if (((b.isRolloverEnabled() && isMouseOver()) || b.hasFocus()) && model.isEnabled()) {
                    // Draw a line border with background
                    Rectangle rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                    if (b.isButtonEnabled()) {
                        JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);
                    }
                    rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                    JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);

                    if (b.isAlwaysDropdown()) {
                        rect = new Rectangle(0, 0, menuWidth, menuHeight);
                        paintRaisedBorder(g, rect);
                    }
                    else {
                        rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                        paintRaisedBorder(g, rect);
                        rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                        paintRaisedBorder(g, rect);
                    }
                }
                else {
                    if (b.isOpaque()) {
                        Rectangle rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                        if (b.isButtonEnabled()) {
                            getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_DEFAULT);
                        }
                        rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_DEFAULT);
                    }
                }
            }
        }
        else if (b.getButtonStyle() == ButtonStyle.TOOLBOX_STYLE) {
            if ((model.isSelected())) {
                // Draw a dark shadow border without bottom
                getPainter().paintSelectedMenu(b, g, new Rectangle(0, 0, menuWidth, menuHeight), orientation, ThemePainter.STATE_SELECTED);
            }
            else if (model.isArmed() || model.isPressed()) {
                Rectangle rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                if (b.isButtonEnabled()) {
                    getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_PRESSED);
                }
                rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_ROLLOVER);

                if (!b.isOpaque()) {
                    rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                    paintSunken2Border(g, rect);
                    rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                    paintRaisedBorder(g, rect);
                }
            }
            else if (model instanceof SplitButtonModel && ((DefaultSplitButtonModel) model).isButtonSelected()) {
                if (isMouseOver() && model.isEnabled()) {
                    Rectangle rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                    getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_ROLLOVER);
                    rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                    if (b.isButtonEnabled()) {
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_PRESSED);
                    }
                    if (!b.isOpaque()) {
                        rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                        paintSunken2Border(g, rect);
                        rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                        paintRaisedBorder(g, rect);
                    }
                }
                else {
                    Rectangle rect;
                    if (b.isOpaque()) {
                        rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_DEFAULT);
                    }
                    rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                    getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_SELECTED);

                    if (!b.isOpaque()) {
                        rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                        paintSunken2Border(g, rect);
                        rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                        paintRaisedBorder(g, rect);
                    }
                }

            }
            else {
                if (b.isRolloverEnabled() && isMouseOver() && model.isEnabled()) {
                    // Draw a line border with background
                    if (b.isAlwaysDropdown()) {
                        Rectangle rect = new Rectangle(0, 0, menuWidth, menuHeight);
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_ROLLOVER);
                        paintRaised2Border(g, rect);
                    }
                    else {
                        Rectangle rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                        if (b.isButtonEnabled()) {
                            getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_ROLLOVER);
                        }
                        rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_ROLLOVER);
                        rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                        paintRaised2Border(g, rect);
                        rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                        paintRaised2Border(g, rect);
                    }
                }
                else {
                    if (b.isOpaque()) {
                        Rectangle rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                        if (b.isButtonEnabled()) {
                            getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_DEFAULT);
                        }
                        rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                        getPainter().paintButtonBackground(b, g, rect, orientation, ThemePainter.STATE_DEFAULT);
                    }
                    else {
                        if (b.isAlwaysDropdown()) {
                            Rectangle rect = new Rectangle(0, 0, menuWidth, menuHeight);
                            paintRaisedBorder(g, rect);
                        }
                        else {
                            Rectangle rect = getButtonRect(b, orientation, menuWidth, menuHeight);
                            paintRaisedBorder(g, rect);
                            rect = getDropDownRect(b, orientation, menuWidth, menuHeight);
                            paintRaisedBorder(g, rect);
                        }
                    }
                }
            }
        }

        paintArrow(menuItem, g);
    }

    protected void paintArrow(JMenuItem menuItem, Graphics g) {
        int menuWidth;
        int menuHeight;
        int orientation = JideSwingUtilities.getOrientationOf(menuItem);
        if (orientation == SwingConstants.HORIZONTAL) {
            menuWidth = menuItem.getWidth();
            menuHeight = menuItem.getHeight();
        }
        else {
            menuWidth = menuItem.getHeight();
            menuHeight = menuItem.getWidth();
        }
        int startX;
        if (menuItem.getComponentOrientation().isLeftToRight()) {
            startX = menuWidth - 9;
        }
        else {
            startX = 4;
        }
        if (menuItem.isEnabled()) {
            JideSwingUtilities.paintArrow(g, getForegroundOfState(menuItem), startX, menuHeight / 2 - 1, 5, SwingConstants.HORIZONTAL);
        }
        else {
            JideSwingUtilities.paintArrow(g, UIDefaultsLookup.getColor("controlShadow"), startX, menuHeight / 2 - 1, 5, SwingConstants.HORIZONTAL);
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

        return d;
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        if (!(c instanceof JMenu) || !((JMenu) c).isTopLevelMenu()) {
            return super.getMaximumSize(c);
        }

        Dimension d = getPreferredSize(c);
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            if (JideSwingUtilities.getOrientationOf(c) == SwingConstants.HORIZONTAL)
                d.width += v.getMaximumSpan(View.X_AXIS) - v.getPreferredSpan(View.X_AXIS);
            else        // TODO: not sure if this is correct
                d.height += v.getMaximumSpan(View.X_AXIS) - v.getPreferredSpan(View.X_AXIS);
        }

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
            return !(sender != null && (sender instanceof AbstractButton) &&
                    !((AbstractButton) sender).getModel().isEnabled());
        }
    }

    protected int getOffset() {
        return 1;
    }

    /**
     * Populates Buttons actions.
     *
     * @param map the action map to load
     */
    public static void loadActionMap(LazyActionMap map) {
        map.put(new Actions(Actions.PRESS));
        map.put(new Actions(Actions.RELEASE));
        map.put(new Actions(Actions.DOWN_PRESS));
        map.put(new Actions(Actions.DOWN_RELEASE));
    }

    @Override
    protected void updateMnemonicBinding() {
        super.updateMnemonicBinding();
        int mnemonic = menuItem.getModel().getMnemonic();
        if (mnemonic != 0 && windowInputMap != null) {
            int[] shortcutKeys = (int[]) UIDefaultsLookup.get("Menu.shortcutKeys");
            if (shortcutKeys == null) {
                shortcutKeys = new int[]{KeyEvent.ALT_MASK};
            }
            for (int shortcutKey : shortcutKeys) {
                windowInputMap.put(KeyStroke.getKeyStroke(mnemonic,
                        shortcutKey, false),
                        "pressed");
                windowInputMap.put(KeyStroke.getKeyStroke(mnemonic,
                        shortcutKey, true),
                        "released");
            }
        }
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
}
