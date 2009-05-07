/*
 * @(#)JideButtonUI.java	Nov 20, 2002
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.basic;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.plaf.JideButtonUI;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.ComponentStateSupport;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.utils.SecurityUtils;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import java.awt.*;

/**
 * JideButtonUI implementation
 */
public class BasicJideButtonUI extends JideButtonUI {
    // Visual constants
    protected int defaultTextIconGap;

    // Offset controlled by set method
    private int shiftOffset = 0;
    protected int defaultTextShiftOffset;

    // Has the shared instance defaults been initialized?
    private boolean defaults_initialized = false;

    private static final String propertyPrefix = "JideButton" + ".";

    protected ThemePainter _painter;

    protected Color _shadowColor;
    protected Color _darkShadowColor;
    protected Color _highlight;
    protected Color _lightHighlightColor;

    protected Color _focusColor;

    protected boolean _isFloatingIcon = false;

    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c) {
        return new BasicJideButtonUI();
    }

    protected String getPropertyPrefix() {
        return propertyPrefix;
    }


    // ********************************
    //          Install PLAF
    // ********************************
    @Override
    public void installUI(JComponent c) {
        installDefaults((AbstractButton) c);
        installListeners((AbstractButton) c);
        installKeyboardActions((AbstractButton) c);
        BasicHTML.updateRenderer(c, ((AbstractButton) c).getText());
    }

    protected void installDefaults(AbstractButton b) {
        // load shared instance defaults
        _painter = (ThemePainter) UIDefaultsLookup.get("Theme.painter");

        String pp = getPropertyPrefix();
        if (!defaults_initialized) {
            defaultTextIconGap = UIDefaultsLookup.getInt(pp + "textIconGap");
            defaultTextShiftOffset = UIDefaultsLookup.getInt(pp + "textShiftOffset");

            _focusColor = UIDefaultsLookup.getColor("Button.focus"); // use Button.focus since we didn't install JideButton.focus.

            // next four lines part of optimized component defaults installation
            /* defaultForeground = UIManagerLookup.getColor(pp + "foreground");
             defaultBackground = UIManagerLookup.getColor(pp + "background");
             defaultFont = UIManagerLookup.getFont(pp + "font");
             defaultBorder = UIManagerLookup.getBorder(pp + "border");*/

            _shadowColor = UIDefaultsLookup.getColor("controlShadow");
            _darkShadowColor = UIDefaultsLookup.getColor("controlDkShadow");
            _highlight = UIDefaultsLookup.getColor("controlHighlight");
            _lightHighlightColor = UIDefaultsLookup.getColor("controlLtHighlight");

            defaults_initialized = true;
        }

//        // set the following defaults on the button
//        if (b.isContentAreaFilled()) {
//            b.setOpaque(true);
//        }
//        else {
//            b.setOpaque(false);
//        }

        updateMargin(b);

        // *** begin optimized defaults install ***

/*	Color currentForeground = b.getForeground();
	Color currentBackground = b.getBackground();
	Font currentFont = b.getFont();
	Border currentBorder = b.getBorder();

	if (currentForeground == null || currentForeground instanceof UIResource) {
	      b.setForeground(defaultForeground);
	}

	if (currentBackground == null || currentBackground instanceof UIResource) {
              b.setBackground(defaultBackground);
	}

	if (currentFont == null || currentFont instanceof UIResource) {
	      b.setFont(defaultFont);
	}

	if (currentBorder == null || currentBorder instanceof UIResource) {
	      b.setBorder(defaultBorder);
	} */

        // *** end optimized defaults install ***

        // old code below works for component defaults installation, but it is slow
        LookAndFeel.installColorsAndFont(b, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(b, pp + "border");

        _isFloatingIcon = UIDefaultsLookup.getBoolean("Icon.floating");
    }

    protected void installListeners(AbstractButton b) {
        BasicButtonListener listener = createButtonListener(b);
        if (listener != null) {
            // put the listener in the button's client properties so that
            // we can get at it later
            b.putClientProperty(this, listener);

            b.addMouseListener(listener);
            b.addMouseMotionListener(listener);
            b.addFocusListener(listener);
            b.addPropertyChangeListener(listener);
            b.addChangeListener(listener);
        }
    }

    protected void installKeyboardActions(AbstractButton b) {
        BasicButtonListener listener = (BasicButtonListener) b.getClientProperty(this);
        if (listener != null) {
            listener.installKeyboardActions(b);
        }
    }


    // ********************************
    //         Uninstall PLAF
    // ********************************
    @Override
    public void uninstallUI(JComponent c) {
        uninstallKeyboardActions((AbstractButton) c);
        uninstallListeners((AbstractButton) c);
        uninstallDefaults((AbstractButton) c);
        BasicHTML.updateRenderer(c, "");
    }

    protected void uninstallKeyboardActions(AbstractButton b) {
        BasicButtonListener listener = (BasicButtonListener) b.getClientProperty(this);
        if (listener != null) {
            listener.uninstallKeyboardActions(b);
        }
    }

    protected void uninstallListeners(AbstractButton b) {
        BasicButtonListener listener = (BasicButtonListener) b.getClientProperty(this);
        b.putClientProperty(this, null);
        if (listener != null) {
            b.removeMouseListener(listener);
            b.removeMouseMotionListener(listener);
            b.removeFocusListener(listener);
            b.removeChangeListener(listener);
            b.removePropertyChangeListener(listener);
        }
    }

    protected void uninstallDefaults(AbstractButton b) {
        _painter = null;
        _focusColor = null;


        _shadowColor = null;
        _highlight = null;
        _lightHighlightColor = null;
        _darkShadowColor = null;
        defaults_initialized = false;
    }

    // ********************************
    //        Create Listeners
    // ********************************

    protected BasicButtonListener createButtonListener(AbstractButton b) {
        return new BasicJideButtonListener(b);
    }

    public int getDefaultTextIconGap(AbstractButton b) {
        return defaultTextIconGap;
    }

    protected Color getFocusColor() {
        return _focusColor;
    }

    /* These rectangles/insets are allocated once for all
     * ButtonUI.paint() calls.  Re-using rectangles rather than
     * allocating them in each paint call substantially reduced the time
     * it took paint to run.  Obviously, this method can't be re-entered.
     */
    protected static Rectangle viewRect = new Rectangle();
    protected static Rectangle textRect = new Rectangle();
    protected static Rectangle iconRect = new Rectangle();

    // ********************************
    //          Paint Methods
    // ********************************

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        boolean isHorizontal = true;

        if (JideSwingUtilities.getOrientationOf(c) == SwingConstants.VERTICAL) {
            isHorizontal = false;
        }


        FontMetrics fm = g.getFontMetrics();

        Insets i = c.getInsets();

        viewRect.x = i.left;
        viewRect.y = i.top;
        viewRect.width = b.getWidth() - (i.right + viewRect.x);
        viewRect.height = b.getHeight() - (i.bottom + viewRect.y);

        textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

        paintBackground(g, b);

        Font f = c.getFont();
        g.setFont(f);

        // layout the text and icon
        String text = JideSwingUtilities.layoutCompoundLabel(c, fm, b.getText(), b.getIcon(),
                isHorizontal,
                b.getVerticalAlignment(), b.getHorizontalAlignment(),
                b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
                viewRect, iconRect, textRect,
                // JDK PORTING HINT
                // JDK1.3: getIconTextGap, use defaultTextIconGap
                b.getText() == null ? 0 : b.getIconTextGap()); // use the bigger one of both gaps. Not really the best way.

        clearTextShiftOffset();

        paintIcon(b, g);

        if (text != null && !text.equals("")) {
            View v = (View) c.getClientProperty(BasicHTML.propertyKey);
            if (v != null) {
                v.paint(g, textRect);
            }
            else {
                paintText(g, b, textRect, text);
            }
        }
    }

    protected void paintIcon(AbstractButton b, Graphics g) {
        // Paint the Icon
        if (b.getIcon() != null) {
            Icon icon = getIcon(b);

            ButtonModel model = b.getModel();
            if (icon != null) {
                if (isFloatingIcon() && model.isEnabled()) {
                    if (model.isRollover() && !model.isPressed() && !model.isSelected()) {
                        if (!"true".equals(SecurityUtils.getProperty("shadingtheme", "false")) && b instanceof JideButton && ((JideButton) b).getButtonStyle() == JideButton.TOOLBAR_STYLE) {
                            if (icon instanceof ImageIcon) {
                                ImageIcon shadow = IconsFactory.createGrayImage(((ImageIcon) icon).getImage());
                                shadow.paintIcon(b, g, iconRect.x + 1, iconRect.y + 1);
                            }
                            else {
                                ImageIcon shadow = IconsFactory.createGrayImage(b, icon);
                                shadow.paintIcon(b, g, iconRect.x + 1, iconRect.y + 1);
                            }
                            icon.paintIcon(b, g, iconRect.x - 1, iconRect.y - 1);
                        }
                        else {
                            icon.paintIcon(b, g, iconRect.x, iconRect.y);
                        }
                    }
                    else {
                        icon.paintIcon(b, g, iconRect.x, iconRect.y);
                    }
                }
                else {
                    icon.paintIcon(b, g, iconRect.x, iconRect.y);
                }
            }
        }
    }

    protected Icon getIcon(AbstractButton b) {
        ButtonModel model = b.getModel();
        Icon icon = b.getIcon();
        Icon tmpIcon = null;
        if (!model.isEnabled()) {
            if (model.isSelected()) {
                tmpIcon = b.getDisabledSelectedIcon();
            }
            else {
                tmpIcon = b.getDisabledIcon();
            }

            // create default disabled icon
            if (tmpIcon == null) {
                if (icon instanceof ImageIcon) {
                    icon = IconsFactory.createGrayImage(((ImageIcon) icon).getImage());
                }
                else {
                    icon = IconsFactory.createGrayImage(b, icon);
                }
            }
        }
        else if (model.isPressed() && model.isArmed()) {
            tmpIcon = b.getPressedIcon();
            if (tmpIcon != null) {
                // revert back to 0 offset
                clearTextShiftOffset();
            }
        }
        else if (b.isRolloverEnabled() && model.isRollover()) {
            if (model.isSelected()) {
                tmpIcon = b.getRolloverSelectedIcon();
            }
            else {
                tmpIcon = b.getRolloverIcon();
            }
        }
        else if (model.isSelected()) {
            tmpIcon = b.getSelectedIcon();
        }

        if (tmpIcon != null) {
            icon = tmpIcon;
        }
        return icon;
    }

    protected boolean isFloatingIcon() {
        return _isFloatingIcon;
    }

    /**
     * As of Java 2 platform v 1.4 this method should not be used or overridden. Use the paintText method which takes
     * the AbstractButton argument.
     */
    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        AbstractButton b = (AbstractButton) c;
        boolean isHorizontal = true;
        if (JideSwingUtilities.getOrientationOf(c) == SwingConstants.VERTICAL) {
            isHorizontal = false;
        }

        ButtonModel model = b.getModel();
        FontMetrics fm = g.getFontMetrics();
        // JDK PORTING HINT
        // JDK1.3: No getDisplayedMnemonicIndex, use getMnemonic
        int mnemonicIndex = b.getDisplayedMnemonicIndex();

        if (!isHorizontal) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.rotate(Math.PI / 2);
            g2d.translate(0, -c.getWidth() + 1);

            /* Draw the Text */
            if (model.isEnabled()) {
                /*** paint the text normally */
                g2d.setColor(getForegroundOfState(b));

                // JDK PORTING HINT
                // JDK1.3: No drawStringUnderlineCharAt, draw the string then draw the underline
                JideSwingUtilities.drawStringUnderlineCharAt(b, g2d, text, mnemonicIndex,
                        textRect.y + getTextShiftOffset(),
                        textRect.x + fm.getAscent() + getTextShiftOffset());
                if (b instanceof JideButton && ((JideButton) b).getButtonStyle() == JideButton.HYPERLINK_STYLE
                        && (((JideButton) b).isAlwaysShowHyperlink() || b.getModel().isRollover())) {
                    g.drawLine(textRect.x, textRect.y, textRect.x, textRect.y + textRect.height);
                }
            }
            else {
                /*** paint the text disabled ***/
                /*** paint the text disabled ***/
                Color color = UIDefaultsLookup.getColor("Button.disabledForeground");
                g2d.setColor(color == null ? b.getBackground().darker() : color);

                // JDK PORTING HINT
                // JDK1.3: No drawStringUnderlineCharAt, draw the string then draw the underline
                JideSwingUtilities.drawStringUnderlineCharAt(b, g2d, text, mnemonicIndex,
                        textRect.y, textRect.x + fm.getAscent());
            }

            g2d.dispose();
        }
        else {
            /* Draw the Text */
            Color old = g.getColor();
            if (model.isEnabled()) {
                /*** paint the text normally */
                g.setColor(getForegroundOfState(b));

                // JDK PORTING HINT
                // JDK1.3: No drawStringUnderlineCharAt, draw the string then draw the underline
                JideSwingUtilities.drawStringUnderlineCharAt(b, g, text, mnemonicIndex,
                        textRect.x + getTextShiftOffset(),
                        textRect.y + fm.getAscent() + getTextShiftOffset());
                if (b instanceof JideButton && ((JideButton) b).getButtonStyle() == JideButton.HYPERLINK_STYLE
                        && (((JideButton) b).isAlwaysShowHyperlink() || b.getModel().isRollover())) {
                    g.drawLine(textRect.x, textRect.y + textRect.height - 2, textRect.x + textRect.width, textRect.y + textRect.height - 2);
                }
            }
            else {
                /*** paint the text disabled ***/
                Color color = UIDefaultsLookup.getColor("Button.disabledForeground");
                g.setColor(color == null ? b.getBackground().darker() : color);

                // JDK PORTING HINT
                // JDK1.3: No drawStringUnderlineCharAt, draw the string then draw the underline
                JideSwingUtilities.drawStringUnderlineCharAt(b, g, text, mnemonicIndex,
                        textRect.x, textRect.y + fm.getAscent());
            }
            g.setColor(old);
        }
    }

    protected Color getForegroundOfState(AbstractButton b) {
        int state = JideSwingUtilities.getButtonState(b);
        Color foreground = null;
        if (b instanceof ComponentStateSupport) {
            foreground = ((ComponentStateSupport) b).getForegroundOfState(state);
        }
        if (foreground == null || foreground instanceof UIResource) {
            foreground = b.getForeground();
        }
        return foreground;
    }

    /**
     * Method which renders the text of the current button.
     * <p/>
     *
     * @param g        Graphics context
     * @param b        Current button to render
     * @param textRect Bounding rectangle to render the text.
     * @param text     String to render
     * @since 1.4
     */
    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
        paintText(g, (JComponent) b, textRect, text);
    }

    protected void paintBackground(Graphics g, AbstractButton b) {
        boolean paintDefaultBorder = true;
        boolean paintBackground;
        Object o = b.getClientProperty("JideButton.paintDefaultBorder");
        if (o instanceof Boolean) {
            paintDefaultBorder = (Boolean) o;
        }
        o = b.getClientProperty("JideButton.alwaysPaintBackground");
        if (o instanceof Boolean) {
            paintBackground = (Boolean) o;
        }
        else {
            paintBackground = b.isOpaque();
        }

        if (paintBackground) {
            g.setColor(b.getBackground());
            g.fillRect(0, 0, b.getWidth(), b.getHeight());
        }

        if (b.isContentAreaFilled()) {
            if (b instanceof JideButton && ((JideButton) b).getButtonStyle() == JideButton.TOOLBAR_STYLE) {
                Rectangle rect = new Rectangle(0, 0, b.getWidth(), b.getHeight());
                int state = JideSwingUtilities.getButtonState(b);
                if (state != ThemePainter.STATE_DEFAULT) {
                    getPainter().paintButtonBackground(b, g, rect, 0, state);
                }
                else {
                    if (paintBackground) {
                        getPainter().paintButtonBackground(b, g, rect, JideSwingUtilities.getOrientationOf(b), state);
                        if ("true".equals(SecurityUtils.getProperty("shadingtheme", "false"))) {
                            JideSwingUtilities.fillGradient(g, rect, JideSwingUtilities.getOrientationOf(b));
                        }
                    }
                }
            }
            else if (b instanceof JideButton && ((JideButton) b).getButtonStyle() == JideButton.FLAT_STYLE) {
                paintBackgroundInFlatStyle(g, b, paintBackground);
            }
            else if (b instanceof JideButton && ((JideButton) b).getButtonStyle() == JideButton.TOOLBOX_STYLE) {
                paintBackgroundInToolboxStyle(g, b, paintBackground, paintDefaultBorder);
            }
        }
    }

    private void paintBackgroundInFlatStyle(Graphics g, AbstractButton b, boolean paintBackground) {
        Rectangle rect = new Rectangle(0, 0, b.getWidth(), b.getHeight());
        int state = JideSwingUtilities.getButtonState(b);
        switch (state) {
            case ThemePainter.STATE_SELECTED:
                JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);
                g.setColor(_shadowColor);    // inner 3D border
                g.drawLine(rect.x, rect.y, rect.width - 1, rect.y);
                g.drawLine(rect.x, rect.y, rect.x, rect.height - 1);

                g.setColor(_lightHighlightColor);     // black drop shadow  __|
                g.drawLine(rect.x, rect.height - 1, rect.width - 1, rect.height - 1);
                g.drawLine(rect.width - 1, rect.y, rect.width - 1, rect.height - 1);
                break;
            case ThemePainter.STATE_PRESSED:
                JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);
                g.setColor(_shadowColor);    // inner 3D border
                g.drawLine(rect.x, rect.y, rect.width - 1, rect.y);
                g.drawLine(rect.x, rect.y, rect.x, rect.height - 1);

                g.setColor(_lightHighlightColor);     // black drop shadow  __|
                g.drawLine(rect.x, rect.height - 1, rect.width - 1, rect.height - 1);
                g.drawLine(rect.width - 1, rect.y, rect.width - 1, rect.height - 1);
                break;
            case ThemePainter.STATE_ROLLOVER:
                JideSwingUtilities.paintBackground(g, rect, _highlight, _highlight);
                g.setColor(_lightHighlightColor);    // inner 3D border
                g.drawLine(rect.x, rect.y, rect.width - 1, rect.y);
                g.drawLine(rect.x, rect.y, rect.x, rect.height - 1);

                g.setColor(_shadowColor);     // black drop shadow  __|
                g.drawLine(rect.x, rect.height - 1, rect.width - 1, rect.height - 1);
                g.drawLine(rect.width - 1, rect.y, rect.width - 1, rect.height - 1);
                break;
            case ThemePainter.STATE_DEFAULT:
                if (paintBackground) {
                    getPainter().paintButtonBackground(b, g, rect, JideSwingUtilities.getOrientationOf(b), ThemePainter.STATE_DEFAULT);
                }
                break;
        }
    }

    private void paintBackgroundInToolboxStyle(Graphics g, AbstractButton b, boolean paintBackground, boolean paintDefaultBorder) {
        Rectangle rect = new Rectangle(0, 0, b.getWidth(), b.getHeight());
        if (b.getModel().isPressed()) {
            getPainter().paintButtonBackground(b, g, rect, JideSwingUtilities.getOrientationOf(b), ThemePainter.STATE_PRESSED);
            if (paintDefaultBorder) {
                g.setColor(_darkShadowColor);    // inner 3D border
                g.drawLine(0, 0, b.getWidth() - 2, 0);
                g.drawLine(0, 0, 0, b.getHeight() - 2);

                g.setColor(_shadowColor);    // inner 3D border
                g.drawLine(1, 1, b.getWidth() - 3, 1);
                g.drawLine(1, 1, 1, b.getHeight() - 3);

                g.setColor(_lightHighlightColor);     // black drop shadow  __|
                g.drawLine(0, b.getHeight() - 1, b.getWidth() - 1, b.getHeight() - 1);
                g.drawLine(b.getWidth() - 1, 0, b.getWidth() - 1, b.getHeight() - 1);
            }
        }
        else if (b.getModel().isSelected() && b.getModel().isRollover()) {
            getPainter().paintButtonBackground(b, g, rect, JideSwingUtilities.getOrientationOf(b), ThemePainter.STATE_PRESSED);
            if (paintDefaultBorder) {
                g.setColor(_darkShadowColor);    // inner 3D border
                g.drawLine(0, 0, b.getWidth() - 2, 0);
                g.drawLine(0, 0, 0, b.getHeight() - 2);

                g.setColor(_shadowColor);    // inner 3D border
                g.drawLine(1, 1, b.getWidth() - 3, 1);
                g.drawLine(1, 1, 1, b.getHeight() - 3);

                g.setColor(_lightHighlightColor);     // black drop shadow  __|
                g.drawLine(0, b.getHeight() - 1, b.getWidth() - 1, b.getHeight() - 1);
                g.drawLine(b.getWidth() - 1, 0, b.getWidth() - 1, b.getHeight() - 1);
            }
        }
        else if (b.getModel().isSelected()) {
            getPainter().paintButtonBackground(b, g, rect, JideSwingUtilities.getOrientationOf(b), ThemePainter.STATE_SELECTED);
            if (paintDefaultBorder) {
                g.setColor(_darkShadowColor);    // inner 3D border
                g.drawLine(0, 0, b.getWidth() - 2, 0);
                g.drawLine(0, 0, 0, b.getHeight() - 2);

                g.setColor(_shadowColor);    // inner 3D border
                g.drawLine(1, 1, b.getWidth() - 3, 1);
                g.drawLine(1, 1, 1, b.getHeight() - 3);

                g.setColor(_lightHighlightColor);     // black drop shadow  __|
                g.drawLine(0, b.getHeight() - 1, b.getWidth() - 1, b.getHeight() - 1);
                g.drawLine(b.getWidth() - 1, 0, b.getWidth() - 1, b.getHeight() - 1);
            }
        }
        else if (b.getModel().isRollover() || (b.hasFocus() && b.isFocusPainted())) {
            getPainter().paintButtonBackground(b, g, rect, JideSwingUtilities.getOrientationOf(b), ThemePainter.STATE_ROLLOVER);
            if (paintDefaultBorder) {
                g.setColor(_lightHighlightColor);    // inner 3D border
                g.drawLine(0, 0, b.getWidth() - 1, 0);
                g.drawLine(0, 0, 0, b.getHeight() - 1);

                g.setColor(_shadowColor);     // gray drop shadow  __|
                g.drawLine(1, b.getHeight() - 2, b.getWidth() - 2, b.getHeight() - 2);
                g.drawLine(b.getWidth() - 2, 1, b.getWidth() - 2, b.getHeight() - 2);

                g.setColor(_darkShadowColor);     // black drop shadow  __|
                g.drawLine(0, b.getHeight() - 1, b.getWidth() - 1, b.getHeight() - 1);
                g.drawLine(b.getWidth() - 1, 0, b.getWidth() - 1, b.getHeight() - 1);
            }
        }
        else {
            if (paintBackground) {
                getPainter().paintButtonBackground(b, g, rect, JideSwingUtilities.getOrientationOf(b), ThemePainter.STATE_DEFAULT);
            }
            else {
                g.setColor(_lightHighlightColor);    // inner 3D border
                g.drawLine(0, 0, b.getWidth() - 1, 0);
                g.drawLine(0, 0, 0, b.getHeight() - 1);

                g.setColor(_shadowColor);     // black drop shadow  __|
                g.drawLine(0, b.getHeight() - 1, b.getWidth() - 1, b.getHeight() - 1);
                g.drawLine(b.getWidth() - 1, 0, b.getWidth() - 1, b.getHeight() - 1);
            }
        }

        if (paintBackground) {
            g.setColor(_lightHighlightColor);    // inner 3D border
            g.drawLine(0, 0, b.getWidth() - 1, 0);
            g.drawLine(0, 0, 0, b.getHeight() - 1);

            g.setColor(_shadowColor);     // black drop shadow  __|
            g.drawLine(0, b.getHeight() - 1, b.getWidth() - 1, b.getHeight() - 1);
            g.drawLine(b.getWidth() - 1, 0, b.getWidth() - 1, b.getHeight() - 1);
        }
    }

    protected void clearTextShiftOffset() {
        this.shiftOffset = 0;
    }

    protected void setTextShiftOffset() {
        this.shiftOffset = defaultTextShiftOffset;
    }

    protected int getTextShiftOffset() {
        return shiftOffset;
    }

    // ********************************
    //          Layout Methods
    // ********************************
    @Override
    public Dimension getMinimumSize(JComponent c) {
        Dimension d = getPreferredSize(c);
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            if (JideSwingUtilities.getOrientationOf(c) == SwingConstants.HORIZONTAL)
                d.width -= v.getPreferredSpan(View.X_AXIS) - v.getMinimumSpan(View.X_AXIS);
            else // TODO: not sure if this is correct
                d.height -= v.getPreferredSpan(View.X_AXIS) - v.getMinimumSpan(View.X_AXIS);
        }
        return d;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        AbstractButton b = (AbstractButton) c;

        Dimension d = BasicGraphicsUtils.getPreferredButtonSize(b, b.getIconTextGap());
        if (JideSwingUtilities.getOrientationOf(c) == SwingConstants.HORIZONTAL) {
            return d;
        }
        else {
            return new Dimension(d.height, d.width); // swap width and height
        }
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        Dimension d = getPreferredSize(c);
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            d.width += v.getMaximumSpan(View.X_AXIS) - v.getPreferredSpan(View.X_AXIS);
        }
        return d;
    }

    public ThemePainter getPainter() {
        return _painter;
    }

    protected void updateMargin(AbstractButton b) {
        String pp = getPropertyPrefix();
        if (b.getMargin() == null || (b.getMargin() instanceof UIResource)) {
            if (shouldWrapText(b)) {
                b.setMargin(UIDefaultsLookup.getInsets(pp + "margin.vertical"));
            }
            else {
                b.setMargin(UIDefaultsLookup.getInsets(pp + "margin"));
            }
        }
    }

    /**
     * Checks if we should wrap text on a button. If the vertical text position is bottom and horizontal text position
     * is center, we will wrap the text.
     *
     * @param c
     * @return true or false.
     */
    public static boolean shouldWrapText(Component c) {
        // return false for now before we support the text wrapping
        return false;

//        boolean wrapText = false;
//        if (c instanceof AbstractButton) {
//            if (((AbstractButton) c).getVerticalTextPosition() == SwingConstants.BOTTOM && ((AbstractButton) c).getHorizontalTextPosition() == SwingConstants.CENTER) {
//                wrapText = true;
//            }
//        }
//        return wrapText;
    }
}
