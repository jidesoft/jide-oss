/*
 * @(#)MetalMenuItemUI.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.metal;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.TopLevelMenuContainer;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * MetalMenuItem implementation
 */
public class MetalMenuItemUI extends MenuItemUI {
    protected JMenuItem menuItem = null;
    protected Color selectionBackground;
    protected Color selectionForeground;
    protected Color disabledForeground;
    protected Color acceleratorForeground;
    protected Color acceleratorSelectionForeground;
    private String acceleratorDelimiter;

    protected int defaultTextIconGap;
    protected Font acceleratorFont;

    protected MouseInputListener mouseInputListener;
    protected MenuDragMouseListener menuDragMouseListener;
    protected MenuKeyListener menuKeyListener;
    private PropertyChangeListener propertyChangeListener;

    protected Icon arrowIcon = null;
    protected Icon checkIcon = null;

    protected boolean oldBorderPainted;

    /**
     * Used for accelerator binding, lazily created.
     */
    InputMap windowInputMap;

    /* diagnostic aids -- should be false for production builds. */
    private static final boolean TRACE = false; // trace creates and disposes

    private static final boolean VERBOSE = false; // show reuse hits/misses
    private static final boolean DEBUG = false;  // show bad params, misc.

    /* Client Property keys for text and accelerator text widths */
    static final String MAX_TEXT_WIDTH = "maxTextWidth";
    static final String MAX_ACC_WIDTH = "maxAccWidth";


    // PORTING: add 1
    protected ThemePainter _painter;

    public static ComponentUI createUI(JComponent c) {
        return new MetalMenuItemUI();
    }

    @Override
    public void installUI(JComponent c) {
        menuItem = (JMenuItem) c;

        installDefaults();
        installComponents(menuItem);
        installListeners();
        installKeyboardActions();
    }


    protected void installDefaults() {
        // PORTING: add 1
        _painter = (ThemePainter) UIDefaultsLookup.get("Theme.painter");

        String prefix = getPropertyPrefix();

        acceleratorFont = UIDefaultsLookup.getFont("MenuItem.acceleratorFont");

        menuItem.setOpaque(true);
        if (menuItem.getMargin() == null ||
                (menuItem.getMargin() instanceof UIResource)) {
            menuItem.setMargin(UIDefaultsLookup.getInsets(prefix + ".margin"));
        }

        defaultTextIconGap = 4;   // Should be from table

        LookAndFeel.installBorder(menuItem, prefix + ".border");
        oldBorderPainted = menuItem.isBorderPainted();
        menuItem.setBorderPainted((Boolean) (UIDefaultsLookup.get(prefix + ".borderPainted")));
        LookAndFeel.installColorsAndFont(menuItem,
                prefix + ".background",
                prefix + ".foreground",
                prefix + ".font");

        // MenuItem specific defaults
        if (selectionBackground == null ||
                selectionBackground instanceof UIResource) {
            selectionBackground =
                    UIDefaultsLookup.getColor(prefix + ".selectionBackground");
        }
        if (selectionForeground == null ||
                selectionForeground instanceof UIResource) {
            selectionForeground =
                    UIDefaultsLookup.getColor(prefix + ".selectionForeground");
        }
        if (disabledForeground == null ||
                disabledForeground instanceof UIResource) {
            disabledForeground =
                    UIDefaultsLookup.getColor(prefix + ".disabledForeground");
        }
        if (acceleratorForeground == null ||
                acceleratorForeground instanceof UIResource) {
            acceleratorForeground =
                    UIDefaultsLookup.getColor(prefix + ".acceleratorForeground");
        }
        if (acceleratorSelectionForeground == null ||
                acceleratorSelectionForeground instanceof UIResource) {
            acceleratorSelectionForeground =
                    UIDefaultsLookup.getColor(prefix + ".acceleratorSelectionForeground");
        }
        // Get accelerator delimiter
        acceleratorDelimiter =
                UIDefaultsLookup.getString("MenuItem.acceleratorDelimiter");
        if (acceleratorDelimiter == null) {
            acceleratorDelimiter = "+";
        }
        // Icons
        if (arrowIcon == null ||
                arrowIcon instanceof UIResource) {
            arrowIcon = UIDefaultsLookup.getIcon(prefix + ".arrowIcon");
        }
        if (checkIcon == null ||
                checkIcon instanceof UIResource) {
            checkIcon = UIDefaultsLookup.getIcon(prefix + ".checkIcon");
        }
    }

    /**
     * @since 1.3
     */
    protected void installComponents(JMenuItem menuItem) {
        BasicHTML.updateRenderer(menuItem, menuItem.getText());
    }

    protected String getPropertyPrefix() {
        return "MenuItem";
    }

    protected void installListeners() {
        if ((mouseInputListener = createMouseInputListener(menuItem)) != null) {
            menuItem.addMouseListener(mouseInputListener);
            menuItem.addMouseMotionListener(mouseInputListener);
        }
        if ((menuDragMouseListener = createMenuDragMouseListener(menuItem)) != null) {
            menuItem.addMenuDragMouseListener(menuDragMouseListener);
        }
        if ((menuKeyListener = createMenuKeyListener(menuItem)) != null) {
            menuItem.addMenuKeyListener(menuKeyListener);
        }
        if ((propertyChangeListener = createPropertyChangeListener(menuItem)) != null) {
            menuItem.addPropertyChangeListener(propertyChangeListener);
        }
    }

    protected void installKeyboardActions() {
        ActionMap actionMap = getActionMap();

        SwingUtilities.replaceUIActionMap(menuItem, actionMap);
        updateAcceleratorBinding();
    }

    @Override
    public void uninstallUI(JComponent c) {
        menuItem = (JMenuItem) c;
        uninstallDefaults();
        uninstallComponents(menuItem);
        uninstallListeners();
        uninstallKeyboardActions();

        //Remove the textWidth and accWidth values from the parent's Client Properties.
        Container parent = menuItem.getParent();
        if ((parent != null && parent instanceof JComponent) &&
                !(menuItem instanceof JMenu && ((JMenu) menuItem).isTopLevelMenu())) {
            JComponent p = (JComponent) parent;
            p.putClientProperty(MetalMenuItemUI.MAX_ACC_WIDTH, null);
            p.putClientProperty(MetalMenuItemUI.MAX_TEXT_WIDTH, null);
        }

        menuItem = null;
    }


    protected void uninstallDefaults() {
        // PORTING: add 1
        _painter = null;

        LookAndFeel.uninstallBorder(menuItem);
        menuItem.setBorderPainted(oldBorderPainted);
        if (menuItem.getMargin() instanceof UIResource)
            menuItem.setMargin(null);
        if (arrowIcon instanceof UIResource)
            arrowIcon = null;
        if (checkIcon instanceof UIResource)
            checkIcon = null;
    }

    /**
     * @since 1.3
     */
    protected void uninstallComponents(JMenuItem menuItem) {
        BasicHTML.updateRenderer(menuItem, "");
    }

    protected void uninstallListeners() {
        if (mouseInputListener != null) {
            menuItem.removeMouseListener(mouseInputListener);
            menuItem.removeMouseMotionListener(mouseInputListener);
        }
        if (menuDragMouseListener != null) {
            menuItem.removeMenuDragMouseListener(menuDragMouseListener);
        }
        if (menuKeyListener != null) {
            menuItem.removeMenuKeyListener(menuKeyListener);
        }
        if (propertyChangeListener != null) {
            menuItem.removePropertyChangeListener(propertyChangeListener);
        }

        mouseInputListener = null;
        menuDragMouseListener = null;
        menuKeyListener = null;
        propertyChangeListener = null;
    }

    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIActionMap(menuItem, null);
        if (windowInputMap != null) {
            SwingUtilities.replaceUIInputMap(menuItem, JComponent.
                    WHEN_IN_FOCUSED_WINDOW, null);
            windowInputMap = null;
        }
    }

    protected MouseInputListener createMouseInputListener(JComponent c) {
        return new MouseInputHandler();
    }

    protected MenuDragMouseListener createMenuDragMouseListener(JComponent c) {
        return new MenuDragMouseHandler();
    }

    protected MenuKeyListener createMenuKeyListener(JComponent c) {
        return new MenuKeyHandler();
    }

    private PropertyChangeListener createPropertyChangeListener(JComponent c) {
        return new PropertyChangeHandler();
    }

    ActionMap getActionMap() {
        String propertyPrefix = getPropertyPrefix();
        String uiKey = propertyPrefix + ".actionMap";
        ActionMap am = (ActionMap) UIDefaultsLookup.get(uiKey);
        if (am == null) {
            am = createActionMap();
            UIManager.getLookAndFeelDefaults().put(uiKey, am);
        }
        return am;
    }

    ActionMap createActionMap() {
        ActionMap map = new ActionMapUIResource();
        map.put("doClick", new ClickAction());
        return map;
    }

    InputMap createInputMap(int condition) {
        if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
            return new ComponentInputMapUIResource(menuItem);
        }
        return null;
    }

    void updateAcceleratorBinding() {
        KeyStroke accelerator = menuItem.getAccelerator();

        if (windowInputMap != null) {
            windowInputMap.clear();
        }
        if (accelerator != null) {
            if (windowInputMap == null) {
                windowInputMap = createInputMap(JComponent.
                        WHEN_IN_FOCUSED_WINDOW);
                SwingUtilities.replaceUIInputMap(menuItem,
                        JComponent.WHEN_IN_FOCUSED_WINDOW, windowInputMap);
            }
            windowInputMap.put(accelerator, "doClick");
        }
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        Dimension d = null;
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            d = getPreferredSize(c);
            d.width -= v.getPreferredSpan(View.X_AXIS) - v.getMinimumSpan(View.X_AXIS);
        }
        return d;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return getPreferredMenuItemSize(c,
                checkIcon,
                arrowIcon,
                defaultTextIconGap);
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        Dimension d = null;
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            d = getPreferredSize(c);
            d.width += v.getMaximumSpan(View.X_AXIS) - v.getPreferredSpan(View.X_AXIS);
        }
        return d;
    }

    // these rects are used for painting and preferred size calculations.
    // they used to be regenerated constantly.  Now they are reused.
    static Rectangle zeroRect = new Rectangle(0, 0, 0, 0);
    static Rectangle iconRect = new Rectangle();
    static Rectangle textRect = new Rectangle();
    static Rectangle acceleratorRect = new Rectangle();
    static Rectangle checkIconRect = new Rectangle();
    static Rectangle arrowIconRect = new Rectangle();
    static Rectangle viewRect = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);
    static Rectangle r = new Rectangle();

    private void resetRects() {
        iconRect.setBounds(zeroRect);
        textRect.setBounds(zeroRect);
        acceleratorRect.setBounds(zeroRect);
        checkIconRect.setBounds(zeroRect);
        arrowIconRect.setBounds(zeroRect);
        viewRect.setBounds(0, 0, Short.MAX_VALUE, Short.MAX_VALUE);
        r.setBounds(zeroRect);
    }

    protected Dimension getPreferredMenuItemSize(JComponent c,
                                                 Icon checkIcon,
                                                 Icon arrowIcon,
                                                 int defaultTextIconGap) {
        JMenuItem b = (JMenuItem) c;
        Icon icon = b.getIcon();
        String text = b.getText();
        KeyStroke accelerator = b.getAccelerator();
        String acceleratorText = "";

        if (accelerator != null) {
            int modifiers = accelerator.getModifiers();
            if (modifiers > 0) {
                acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
                //acceleratorText += "-";
                acceleratorText += acceleratorDelimiter;
            }
            int keyCode = accelerator.getKeyCode();
            if (keyCode != 0) {
                acceleratorText += KeyEvent.getKeyText(keyCode);
            }
            else {
                acceleratorText += accelerator.getKeyChar();
            }
        }

        Font font = b.getFont();
        FontMetrics fm = b.getFontMetrics(font);
        FontMetrics fmAccel = b.getFontMetrics(acceleratorFont);

        resetRects();

        layoutMenuItem(fm, text, fmAccel, acceleratorText, icon, checkIcon, arrowIcon,
                b.getVerticalAlignment(), b.getHorizontalAlignment(),
                b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
                viewRect, iconRect, textRect, acceleratorRect, checkIconRect, arrowIconRect,
                text == null ? 0 : defaultTextIconGap,
                defaultTextIconGap);
        // find the union of the icon and text rects
        r.setBounds(textRect);
        r = SwingUtilities.computeUnion(iconRect.x,
                iconRect.y,
                iconRect.width,
                iconRect.height,
                r);
        //   r = iconRect.union(textRect);

        // To make the accelerator texts appear in a column, find the widest MenuItem text
        // and the widest accelerator text.

        //Get the parent, which stores the information.
        Container parent = menuItem.getParent();

        //Check the parent, and see that it is not a top-level menu.
        if (parent != null && parent instanceof JComponent &&
                !(menuItem instanceof JMenu && ((JMenu) menuItem).isTopLevelMenu())) {
            JComponent p = (JComponent) parent;

            //Get widest text so far from parent, if no one exists null is returned.
            Integer maxTextWidth = (Integer) p.getClientProperty(MetalMenuItemUI.MAX_TEXT_WIDTH);
            Integer maxAccWidth = (Integer) p.getClientProperty(MetalMenuItemUI.MAX_ACC_WIDTH);

            int maxTextValue = maxTextWidth != null ? maxTextWidth : 0;
            int maxAccValue = maxAccWidth != null ? maxAccWidth : 0;

            //Compare the text widths, and adjust the r.width to the widest.
            if (r.width < maxTextValue) {
                r.width = maxTextValue;
            }
            else {
                p.putClientProperty(MetalMenuItemUI.MAX_TEXT_WIDTH, r.width);
            }

            //Compare the accelerator widths.
            if (acceleratorRect.width > maxAccValue) {
                maxAccValue = acceleratorRect.width;
                p.putClientProperty(MetalMenuItemUI.MAX_ACC_WIDTH, acceleratorRect.width);
            }

            //Add on the widest accelerator
            r.width += maxAccValue;
            r.width += defaultTextIconGap;

        }

        if (useCheckAndArrow()) {
            // Add in the checkIcon
            r.width += checkIconRect.width;
            r.width += defaultTextIconGap;

            // Add in the arrowIcon
            r.width += defaultTextIconGap;
            r.width += arrowIconRect.width;
        }

        r.width += 2 * defaultTextIconGap;

        Insets insets = b.getInsets();
        if (insets != null) {
            r.width += insets.left + insets.right;
            r.height += insets.top + insets.bottom;
        }

        // if the width is even, bump it up one. This is critical
        // for the focus dash line to draw properly
        if (r.width % 2 == 0) {
            r.width++;
        }

        // if the height is even, bump it up one. This is critical
        // for the text to center properly
        if (r.height % 2 == 0) {
            r.height++;
        }
/*
	if(!(b instanceof JMenu && ((JMenu) b).isTopLevelMenu()) ) {
	    
	    // Container parent = menuItem.getParent();
	    JComponent p = (JComponent) parent;
	    
	    System.out.println("MaxText: "+p.getClientProperty(BasicMenuItemUI.MAX_TEXT_WIDTH));
	    System.out.println("MaxACC"+p.getClientProperty(BasicMenuItemUI.MAX_ACC_WIDTH));
	    
	    System.out.println("returning pref.width: " + r.width);
	    System.out.println("Current getSize: " + b.getSize() + "\n");
        }*/
        if (JideSwingUtilities.getOrientationOf(menuItem) == SwingConstants.HORIZONTAL) {
            return r.getSize();
        }
        else {
            return new Dimension(r.height, r.width);
        }
    }

    /**
     * We draw the background in paintMenuItem() so override update (which fills the background of opaque components by
     * default) to just call paint().
     */
    @Override
    public void update(Graphics g, JComponent c) {
        paint(g, c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        paintMenuItem(g, c, checkIcon, arrowIcon,
                selectionBackground, selectionForeground,
                defaultTextIconGap);
    }


    protected void paintMenuItem(Graphics g, JComponent c,
                                 Icon checkIcon, Icon arrowIcon,
                                 Color background, Color foreground,
                                 int defaultTextIconGap) {
        JMenuItem b = (JMenuItem) c;
        ButtonModel model = b.getModel();

        int menuWidth = 0;
        int menuHeight = 0;

        if (JideSwingUtilities.getOrientationOf(menuItem) == SwingConstants.HORIZONTAL) {
            //   Dimension size = b.getSize();
            menuWidth = b.getWidth();
            menuHeight = b.getHeight();
        }
        else {
            //   Dimension size = b.getSize();
            menuWidth = b.getHeight();
            menuHeight = b.getWidth();
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform oldAt = g2d.getTransform();
            g2d.rotate(Math.PI / 2);
            g2d.translate(0, -menuHeight + 1);
        }

        Insets i = c.getInsets();

        resetRects();

        viewRect.setBounds(0, 0, menuWidth, menuHeight);

        viewRect.x += i.left;
        viewRect.y += i.top;
        viewRect.width -= (i.right + viewRect.x);
        viewRect.height -= (i.bottom + viewRect.y);


        Font holdf = g.getFont();
        Font f = c.getFont();
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics(f);
        FontMetrics fmAccel = g.getFontMetrics(acceleratorFont);

        // get Accelerator text
        KeyStroke accelerator = b.getAccelerator();
        String acceleratorText = "";
        if (accelerator != null) {
            int modifiers = accelerator.getModifiers();
            if (modifiers > 0) {
                acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
                //acceleratorText += "-";
                acceleratorText += acceleratorDelimiter;
            }

            int keyCode = accelerator.getKeyCode();
            if (keyCode != 0) {
                acceleratorText += KeyEvent.getKeyText(keyCode);
            }
            else {
                acceleratorText += accelerator.getKeyChar();
            }
        }

        // layout the text and icon
        String text = layoutMenuItem(fm, b.getText(), fmAccel, acceleratorText, b.getIcon(),
                checkIcon, arrowIcon,
                b.getVerticalAlignment(), b.getHorizontalAlignment(),
                b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
                viewRect, iconRect, textRect, acceleratorRect,
                checkIconRect, arrowIconRect,
                b.getText() == null ? 0 : defaultTextIconGap,
                defaultTextIconGap);

        // Paint background
        paintBackground(g, b, background);

        Color holdc = g.getColor();

        // Paint the Check
        if (checkIcon != null) {
            if (model.isArmed() || (c instanceof JMenu && model.isSelected())) {
                g.setColor(foreground);
            }
            else {
                g.setColor(holdc);
            }
            if (useCheckAndArrow())
                checkIcon.paintIcon(c, g, checkIconRect.x, checkIconRect.y);
            g.setColor(holdc);
        }

        // Paint the Icon
        if (b.getIcon() != null) {
            Icon icon;
            if (!model.isEnabled()) {
                icon = b.getDisabledIcon();
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

            if (icon != null)
                icon.paintIcon(c, g, iconRect.x, iconRect.y);
        }

        // Draw the Text
        if (text != null) {
            View v = (View) c.getClientProperty(BasicHTML.propertyKey);
            if (v != null) {
                v.paint(g, textRect);
            }
            else {
                paintText(g, b, textRect, text);
            }
        }

        // Draw the Accelerator Text
        if (acceleratorText != null && !acceleratorText.equals("")) {

            //Get the maxAccWidth from the parent to calculate the offset.
            int accOffset = 0;
            Container parent = menuItem.getParent();
            if (parent != null && parent instanceof JComponent) {
                JComponent p = (JComponent) parent;
                Integer maxValueInt = (Integer) p.getClientProperty(MetalMenuItemUI.MAX_ACC_WIDTH);
                int maxValue = maxValueInt != null ?
                        maxValueInt : acceleratorRect.width;

                //Calculate the offset, with which the accelerator texts will be drawn with.
                accOffset = maxValue - acceleratorRect.width;
            }

            g.setFont(acceleratorFont);
            if (!model.isEnabled()) {
                // *** paint the acceleratorText disabled
                if (disabledForeground != null) {
                    g.setColor(disabledForeground);
                    JideSwingUtilities.drawString(menuItem, g, acceleratorText,
                            acceleratorRect.x - accOffset,
                            acceleratorRect.y + fmAccel.getAscent());
                }
                else {
                    g.setColor(b.getBackground().brighter());
                    JideSwingUtilities.drawString(menuItem, g, acceleratorText,
                            acceleratorRect.x - accOffset,
                            acceleratorRect.y + fmAccel.getAscent());
                    g.setColor(b.getBackground().darker());
                    JideSwingUtilities.drawString(menuItem, g, acceleratorText,
                            acceleratorRect.x - accOffset - 1,
                            acceleratorRect.y + fmAccel.getAscent() - 1);
                }
            }
            else {
                // *** paint the acceleratorText normally
                if (model.isArmed() || (c instanceof JMenu && model.isSelected())) {
                    g.setColor(acceleratorSelectionForeground);
                }
                else {
                    g.setColor(acceleratorForeground);
                }
                JideSwingUtilities.drawString(menuItem, g, acceleratorText,
                        acceleratorRect.x - accOffset,
                        acceleratorRect.y + fmAccel.getAscent());
            }
        }

        // Paint the Arrow
        if (arrowIcon != null) {
            if (model.isArmed() || (c instanceof JMenu && model.isSelected()))
                g.setColor(foreground);
            if (useCheckAndArrow())
                arrowIcon.paintIcon(c, g, arrowIconRect.x, arrowIconRect.y);
        }
        g.setColor(holdc);
        g.setFont(holdf);
    }

    /**
     * Draws the background of the menu item.
     *
     * @param g        the paint graphics
     * @param menuItem menu item to be painted
     * @param bgColor  selection background color
     * @since 1.4
     */
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
        ButtonModel model = menuItem.getModel();
        Color oldColor = g.getColor();
        int menuWidth = menuItem.getWidth();
        int menuHeight = menuItem.getHeight();

        if (menuItem.isOpaque()) {
            if (model.isArmed() || (menuItem instanceof JMenu && model.isSelected())) {
                g.setColor(bgColor);
                g.fillRect(0, 0, menuWidth, menuHeight);
            }
            else {
                g.setColor(menuItem.getBackground());
                g.fillRect(0, 0, menuWidth, menuHeight);
            }
            g.setColor(oldColor);
        }
    }

    /**
     * Renders the text of the current menu item.
     * <p/>
     *
     * @param g        graphics context
     * @param menuItem menu item to render
     * @param textRect bounding rectangle for rendering the text
     * @param text     string to render
     * @since 1.4
     */
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
        ButtonModel model = menuItem.getModel();
        FontMetrics fm = g.getFontMetrics();
        int mnemIndex = menuItem.getDisplayedMnemonicIndex();

        if (!model.isEnabled()) {
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


    /**
     * Compute and return the location of the icons origin, the location of origin of the text baseline, and a possibly
     * clipped version of the compound labels string.  Locations are computed relative to the viewRect rectangle.
     */

    private String layoutMenuItem(FontMetrics fm,
                                  String text,
                                  FontMetrics fmAccel,
                                  String acceleratorText,
                                  Icon icon,
                                  Icon checkIcon,
                                  Icon arrowIcon,
                                  int verticalAlignment,
                                  int horizontalAlignment,
                                  int verticalTextPosition,
                                  int horizontalTextPosition,
                                  Rectangle viewRect,
                                  Rectangle iconRect,
                                  Rectangle textRect,
                                  Rectangle acceleratorRect,
                                  Rectangle checkIconRect,
                                  Rectangle arrowIconRect,
                                  int textIconGap,
                                  int menuItemGap) {
        if (icon != null)
            if ((icon.getIconHeight() == 0) || (icon.getIconWidth() == 0))
                icon = null;    // An exception may occur in case of zero sized icons

        viewRect.width -= getRightMargin(); // this line is mainly for JideSplitButton
        String newText = SwingUtilities.layoutCompoundLabel(menuItem, fm, text, icon, verticalAlignment,
                horizontalAlignment, verticalTextPosition,
                horizontalTextPosition, viewRect, iconRect, textRect,
                textIconGap);

        // only do it for split menu
        if (getRightMargin() > 0) {
            text = newText;
        }
        viewRect.width += getRightMargin(); // this line is mainly for JideSplitButton

        /* Initialize the acceelratorText bounds rectangle textRect.  If a null
         * or and empty String was specified we substitute "" here
         * and use 0,0,0,0 for acceleratorTextRect.
         */
        if ((acceleratorText == null) || acceleratorText.equals("")) {
            acceleratorRect.width = acceleratorRect.height = 0;
            acceleratorText = "";
        }
        else {
            acceleratorRect.width = SwingUtilities.computeStringWidth(fmAccel, acceleratorText);
            acceleratorRect.height = fmAccel.getHeight();
        }

        /* Initialize the checkIcon bounds rectangle's width & height.
         */

        if (useCheckAndArrow()) {
            if (checkIcon != null) {
                checkIconRect.width = checkIcon.getIconWidth();
                checkIconRect.height = checkIcon.getIconHeight();
            }
            else {
                checkIconRect.width = checkIconRect.height = 0;
            }

            /* Initialize the arrowIcon bounds rectangle width & height.
             */

            if (arrowIcon != null) {
                arrowIconRect.width = arrowIcon.getIconWidth();
                arrowIconRect.height = arrowIcon.getIconHeight();
            }
            else {
                arrowIconRect.width = arrowIconRect.height = 0;
            }
        }

        Rectangle labelRect = iconRect.union(textRect);
        if (menuItem.getComponentOrientation().isLeftToRight()) {
            if (getRightMargin() == 0) {
                textRect.x += menuItemGap;
                iconRect.x += menuItemGap;
            }

            // Position the Accelerator text rect
            acceleratorRect.x = viewRect.x + viewRect.width - arrowIconRect.width
                    - menuItemGap - acceleratorRect.width;

            // Position the Check and Arrow Icons
            if (useCheckAndArrow()) {
                checkIconRect.x = viewRect.x + menuItemGap;
                textRect.x += menuItemGap + checkIconRect.width;
                iconRect.x += menuItemGap + checkIconRect.width;
                arrowIconRect.x = viewRect.x + viewRect.width - menuItemGap
                        - arrowIconRect.width;
            }
        }
        else {
            if (getRightMargin() == 0) {
                textRect.x -= menuItemGap;
                iconRect.x -= menuItemGap;
            }

            // Position the Accelerator text rect
            acceleratorRect.x = viewRect.x + arrowIconRect.width + menuItemGap;

            // Position the Check and Arrow Icons
            if (useCheckAndArrow()) {
                checkIconRect.x = viewRect.x + viewRect.width - menuItemGap
                        - checkIconRect.width;
                textRect.x -= menuItemGap + checkIconRect.width;
                iconRect.x -= menuItemGap + checkIconRect.width;
                arrowIconRect.x = viewRect.x + menuItemGap;
            }
        }

        // Align the accelerator text and the check and arrow icons vertically
        // with the center of the label rect.
        acceleratorRect.y = labelRect.y + (labelRect.height / 2) - (acceleratorRect.height / 2);
        if (useCheckAndArrow()) {
            arrowIconRect.y = labelRect.y + (labelRect.height / 2) - (arrowIconRect.height / 2);
            checkIconRect.y = labelRect.y + (labelRect.height / 2) - (checkIconRect.height / 2);
        }

        /*
        System.out.println("Layout: text="+menuItem.getText()+"\n\tv="
                           +viewRect+"\n\tc="+checkIconRect+"\n\ti="
                           +iconRect+"\n\tt="+textRect+"\n\tacc="
                           +acceleratorRect+"\n\ta="+arrowIconRect+"\n");
        */

        return text;
    }

    /*
     * Returns false if the component is a JMenu and it is a top
     * level menu (on the menubar).
     */
    private boolean useCheckAndArrow() {
        boolean b = true;
        if ((menuItem instanceof JMenu) &&
                (((JMenu) menuItem).isTopLevelMenu())) {
            b = false;
        }
        return b;
    }

    public MenuElement[] getPath() {
        MenuSelectionManager m = MenuSelectionManager.defaultManager();
        MenuElement oldPath[] = m.getSelectedPath();
        MenuElement newPath[];
        int i = oldPath.length;
        if (i == 0)
            return new MenuElement[0];
        Component parent = menuItem.getParent();
        if (oldPath[i - 1].getComponent() == parent) {
            // The parent popup menu is the last so far
            newPath = new MenuElement[i + 1];
            System.arraycopy(oldPath, 0, newPath, 0, i);
            newPath[i] = menuItem;
        }
        else {
            // A sibling menuitem is the current selection
            // 
            //  This probably needs to handle 'exit submenu into 
            // a menu item.  Search backwards along the current
            // selection until you find the parent popup menu,
            // then copy up to that and add yourself...
            int j;
            for (j = oldPath.length - 1; j >= 0; j--) {
                if (oldPath[j].getComponent() == parent)
                    break;
            }
            newPath = new MenuElement[j + 2];
            System.arraycopy(oldPath, 0, newPath, 0, j + 1);
            newPath[j + 1] = menuItem;
            /*
            System.out.println("Sibling condition -- ");
            System.out.println("Old array : ");
            printMenuElementArray(oldPath, false);
            System.out.println("New array : ");
            printMenuElementArray(newPath, false);
            */
        }
        return newPath;
    }

    void printMenuElementArray(MenuElement path[], boolean dumpStack) {
        System.out.println("Path is(");
        int i, j;
        for (i = 0, j = path.length; i < j; i++) {
            for (int k = 0; k <= i; k++)
                System.out.print("  ");
            MenuElement me = path[i];
            if (me instanceof JMenuItem)
                System.out.println(((JMenuItem) me).getText() + ", ");
            else if (me == null)
                System.out.println("NULL , ");
            else
                System.out.println("" + me + ", ");
        }
        System.out.println(")");

        if (dumpStack == true)
            Thread.dumpStack();
    }

    protected class MouseInputHandler implements MouseInputListener {
        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e)) {
                return;
            }

            if (menuItem != null && menuItem.isEnabled()) {
                MenuSelectionManager manager = MenuSelectionManager.defaultManager();
                Point p = e.getPoint();
                if (p.x >= 0 && p.x < menuItem.getWidth() &&
                        p.y >= 0 && p.y < menuItem.getHeight()) {
                    doClick(manager);
                }
                else {
                    manager.processMouseEvent(e);
                }
            }
        }

        public void mouseEntered(MouseEvent e) {
            if (menuItem != null && menuItem.isEnabled()) {
                MenuSelectionManager manager = MenuSelectionManager.defaultManager();
                int modifiers = e.getModifiers();
                // 4188027: drag enter/exit added in JDK 1.1.7A, JDK1.2
                if ((modifiers & (InputEvent.BUTTON1_MASK |
                        InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
                    MenuSelectionManager.defaultManager().processMouseEvent(e);
                }
                else {
                    manager.setSelectedPath(getPath());
                }
            }
        }

        public void mouseExited(MouseEvent e) {
            if (menuItem != null && menuItem.isEnabled()) {
                MenuSelectionManager manager = MenuSelectionManager.defaultManager();

                int modifiers = e.getModifiers();
                // 4188027: drag enter/exit added in JDK 1.1.7A, JDK1.2
                if ((modifiers & (InputEvent.BUTTON1_MASK |
                        InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
                    MenuSelectionManager.defaultManager().processMouseEvent(e);
                }
                else {

                    MenuElement path[] = manager.getSelectedPath();
                    if (path.length > 1) {
                        MenuElement newPath[] = new MenuElement[path.length - 1];
                        int i, c;
                        for (i = 0, c = path.length - 1; i < c; i++)
                            newPath[i] = path[i];
                        manager.setSelectedPath(newPath);
                    }
                }
            }
        }

        public void mouseDragged(MouseEvent e) {
            MenuSelectionManager.defaultManager().processMouseEvent(e);
        }

        public void mouseMoved(MouseEvent e) {
        }
    }


    private class MenuDragMouseHandler implements MenuDragMouseListener {
        public void menuDragMouseEntered(MenuDragMouseEvent e) {
        }

        public void menuDragMouseDragged(MenuDragMouseEvent e) {
            if (menuItem != null && menuItem.isEnabled()) {
                MenuSelectionManager manager = e.getMenuSelectionManager();
                MenuElement path[] = e.getPath();
                manager.setSelectedPath(path);
            }
        }

        public void menuDragMouseExited(MenuDragMouseEvent e) {
        }

        public void menuDragMouseReleased(MenuDragMouseEvent e) {
            if (menuItem != null && menuItem.isEnabled()) {
                MenuSelectionManager manager = e.getMenuSelectionManager();
                Point p = e.getPoint();
                if (p.x >= 0 && p.x < menuItem.getWidth() &&
                        p.y >= 0 && p.y < menuItem.getHeight()) {
                    doClick(manager);
                }
                else {
                    manager.clearSelectedPath();
                }
            }
        }
    }

    private class MenuKeyHandler implements MenuKeyListener {

        /**
         * Handles the mnemonic key typed in the MenuItem if this menuItem is in a standalone popup menu. This
         * invocation normally handled in BasicMenuUI.MenuKeyHandler.menuKeyPressed. Ideally, the MenuKeyHandlers for
         * both BasicMenuItemUI and BasicMenuUI can be consolidated into BasicPopupMenuUI but that would require an
         * semantic change. This would result in a performance win since we can shortcut a lot of the needless
         * processing from MenuSelectionManager.processKeyEvent(). See 4670831.
         */
        public void menuKeyTyped(MenuKeyEvent e) {
            if (menuItem != null && menuItem.isEnabled()) {
                int key = menuItem.getMnemonic();
                if (key == 0 || e.getPath().length != 2) // Hack! Only proceed if in a JPopupMenu
                    return;
                if (lower((char) key) == lower(e.getKeyChar())) {
                    MenuSelectionManager manager =
                            e.getMenuSelectionManager();
                    doClick(manager);
                    e.consume();
                }
            }
        }

        public void menuKeyPressed(MenuKeyEvent e) {
        }

        public void menuKeyReleased(MenuKeyEvent e) {
        }

        private char lower(char keyChar) {
            return Character.toLowerCase(keyChar);
        }
    }

    private class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();

            if (name.equals("labelFor") || name.equals("displayedMnemonic") ||
                    name.equals("accelerator")) {
                updateAcceleratorBinding();
            }
            else if (name.equals("text") || "font".equals(name) ||
                    "foreground".equals(name)) {
                // remove the old html view client property if one
                // existed, and install a new one if the text installed
                // into the JLabel is html source.
                JMenuItem lbl = ((JMenuItem) e.getSource());
                String text = lbl.getText();
                BasicHTML.updateRenderer(lbl, text);
            }
        }
    }

    private static class ClickAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JMenuItem mi = (JMenuItem) e.getSource();
            MenuSelectionManager.defaultManager().clearSelectedPath();
            mi.doClick();
        }
    }

    /**
     * Call this method when a menu item is to be activated. This method handles some of the details of menu item
     * activation such as clearing the selected path and messaging the JMenuItem's doClick() method.
     *
     * @param msm A MenuSelectionManager. The visual feedback and internal bookkeeping tasks are delegated to this
     *            MenuSelectionManager. If <code>null</code> is passed as this argument, the
     *            <code>MenuSelectionManager.defaultManager</code> is used.
     * @see javax.swing.MenuSelectionManager
     * @see javax.swing.JMenuItem#doClick(int)
     * @since 1.4
     */
    protected void doClick(MenuSelectionManager msm) {
        // Visual feedback
        if (msm == null) {
            msm = MenuSelectionManager.defaultManager();
        }
        msm.clearSelectedPath();
        menuItem.doClick(0);
    }

    /**
     * This is to see if the menu item in question is part of the system menu on an internal frame. The Strings that are
     * being checked can be found in MetalInternalFrameTitlePaneUI.java, WindowsInternalFrameTitlePaneUI.java, and
     * MotifInternalFrameTitlePaneUI.java.
     *
     * @since 1.4
     */
    private boolean isInternalFrameSystemMenu() {
        String actionCommand = menuItem.getActionCommand();
        if ((actionCommand.equals("Close")) ||
                (actionCommand.equals("Minimize")) ||
                (actionCommand.equals("Restore")) ||
                (actionCommand.equals("Maximize"))) {
            return true;
        }
        else {
            return false;
        }
    }

    // PORTING: add 3
    public ThemePainter getPainter() {
        return _painter;
    }

    protected boolean isDownArrowVisible(Container c) {
        if (c instanceof TopLevelMenuContainer && ((TopLevelMenuContainer) c).isMenuBar()) {
            return false;
        }
        else if (c instanceof TopLevelMenuContainer && !((TopLevelMenuContainer) c).isMenuBar()) {
            return true;
        }
        else if (c instanceof JMenuBar) {
            return false;
        }
        else {
            return true;
        }
    }

    protected int getRightMargin() {
        return 0;
    }
}
