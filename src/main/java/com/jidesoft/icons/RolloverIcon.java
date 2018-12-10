/*
 * @(#)NavitaionTreeIcon.java 11/3/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.icons;

import com.jidesoft.swing.JideSwingUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * <code>RolloverIcon</code> provides the expanded and collapsed tree icons that has rollover and fade effect. However
 * it can be used to implement icon for any other purpose, not just the tree icons.
 *
 * @since 3.3.0
 */
public class RolloverIcon implements Icon {
    /**
     * All colors used by the default icons. You can change them to fit your L&F.
     */
    public static Color COLOR_COLLAPSED_FILL_ROLLOVER = new Color(199, 235, 250);
    public static Color COLOR_COLLAPSED_FILL = Color.WHITE;
    public static Color COLOR_COLLAPSED_ROLLOVER = new Color(28, 196, 247);
    public static Color COLOR_COLLAPSED = new Color(165, 165, 165);
    public static Color COLOR_EXPANDED_FILL_ROLLOVER = new Color(130, 223, 251);
    public static Color COLOR_EXPANDED_FILL = new Color(89, 89, 89);
    public static Color COLOR_EXPANDED_ROLLOVER = new Color(28, 196, 247);
    public static Color COLOR_EXPANDED = new Color(35, 35, 35);

    public final static int ICON_EXPANDED = 0;
    public final static int ICON_COLLAPSED = 1;

    private Icon _normalIcon;
    private Icon _rolloverIcon;
    private IconRolloverSupport _rolloverSupport;

    /**
     * An interface that should be implemented on a component if you want to use an icon that supports rollover and fade
     * (fade in or fade out) effect.
     */
    public static interface IconRolloverSupport {
        /**
         * Checks if the mouse is over an icon. In the implementation, you can use a MouseMotionListener to detect the
         * mouse position and see if it is over the icon.
         *
         * @param x      x of the icon
         * @param y      y of the icon
         * @param width  icon width
         * @param height icon height
         * @return true if the mouse is over the icon. False if not.
         */
        boolean isIconRollover(int x, int y, int width, int height);

        /**
         * Checks if the icon should be faded. Because the fade is an animation, we will call {@link #getIconAlpha()}
         * immediately to find out the alpha value of the fade. In your implementation, you can use an Animator or any
         * other animation frameworks to change the iconAlpha value and ask the icon to repaint itself again.
         *
         * @return true of the icon should be faded. If it returns true, {@link #getIconAlpha()} will be called to find
         *         out the alpha value.
         */
        boolean isIconFade();

        /**
         * Gets the icon alpha value. We will paint the icon using the provided alpha value to create the fade effect.
         *
         * @return the icon alpha value.
         */
        float getIconAlpha();
    }

    /**
     * The default collapsed tree icon.
     */
    public static class DefaultCollapsedIcon implements Icon {
        private IconRolloverSupport _rolloverSupport;

        public DefaultCollapsedIcon(IconRolloverSupport rolloverSupport) {
            _rolloverSupport = rolloverSupport;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            Color orgColor = g2.getColor();
            Object o = JideSwingUtilities.setupShapeAntialiasing(g);
            GeneralPath path = new GeneralPath();
            if (c.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT) {
                path.moveTo(x + 4, y);
                path.lineTo(x, y + 4);
                path.lineTo(x + 4, y + 8);
            }
            else {
                path.moveTo(x, y);
                path.lineTo(x + 4, y + 4);
                path.lineTo(x, y + 8);
            }
            path.closePath();
            boolean rollover = _rolloverSupport.isIconRollover(x, y, getIconWidth(), getIconHeight());
            g2.setColor(rollover ? COLOR_COLLAPSED_FILL_ROLLOVER : COLOR_COLLAPSED_FILL);
            g2.fill(path);
            g2.setColor(rollover ? COLOR_COLLAPSED_ROLLOVER : COLOR_COLLAPSED);
            g2.draw(path);
            g2.setColor(orgColor);
            JideSwingUtilities.restoreShapeAntialiasing(g, o);
        }

        @Override
        public int getIconWidth() {
            return 6;
        }

        @Override
        public int getIconHeight() {
            return 9;
        }
    }

    /**
     * The default expanded tree icon.
     */
    public static class DefaultExpandedIcon implements Icon {
        private IconRolloverSupport _rolloverSupport;

        public DefaultExpandedIcon(IconRolloverSupport rolloverSupport) {
            _rolloverSupport = rolloverSupport;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            Color orgColor = g2.getColor();
            Object o = JideSwingUtilities.setupShapeAntialiasing(g);
            GeneralPath path = new GeneralPath();
            if (c.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT) {
                path.moveTo(x, y);
                path.lineTo(x, y + 5);
                path.lineTo(x + 5, y + 5);
            }
            else {
                path.moveTo(x + 5, y);
                path.lineTo(x + 5, y + 5);
                path.lineTo(x, y + 5);
            }
            path.closePath();
            boolean rollover = _rolloverSupport.isIconRollover(x, y, getIconWidth(), getIconHeight());
            g2.setColor(rollover ? COLOR_EXPANDED_FILL_ROLLOVER : COLOR_EXPANDED_FILL);
            g2.fill(path);
            g2.setColor(rollover ? COLOR_EXPANDED_ROLLOVER : COLOR_EXPANDED);
            g2.draw(path);
            g2.setColor(orgColor);
            JideSwingUtilities.restoreShapeAntialiasing(g, o);
        }

        @Override
        public int getIconWidth() {
            return 6;
        }

        @Override
        public int getIconHeight() {
            return 6;
        }
    }

    /**
     * Creates an RolloverIcon.
     *
     * @param rolloverSupport the IconRolloverSupport interface that should be implemented on a component.
     * @param iconType        the icon type. It could be either {@link #ICON_EXPANDED} or {@link #ICON_COLLAPSED}.
     */
    public RolloverIcon(IconRolloverSupport rolloverSupport, int iconType) {
        _rolloverSupport = rolloverSupport;
        switch (iconType) {
            case ICON_EXPANDED:
                _normalIcon = new DefaultExpandedIcon(rolloverSupport);
                break;
            case ICON_COLLAPSED:
                _normalIcon = new DefaultCollapsedIcon(rolloverSupport);
                break;
        }
        _rolloverIcon = null;
    }

    /**
     * Creates an RolloverIcon.
     *
     * @param rolloverSupport the IconRolloverSupport interface that should be implemented on a component.
     * @param normalIcon      the normal icon
     */
    public RolloverIcon(IconRolloverSupport rolloverSupport, Icon normalIcon) {
        _rolloverSupport = rolloverSupport;
        _normalIcon = normalIcon;
        _rolloverIcon = null;
    }

    /**
     * Creates an RolloverIcon.
     *
     * @param rolloverSupport the IconRolloverSupport interface that should be implemented on a component.
     * @param normalIcon      the normal icon. This icon will be used to determine the icon size. The size of the
     *                        rollover icon is ignored so you should make sure the two icons have the same size.
     * @param rolloverIcon    the rollover icon
     */
    public RolloverIcon(IconRolloverSupport rolloverSupport, Icon normalIcon, Icon rolloverIcon) {
        _rolloverSupport = rolloverSupport;
        _normalIcon = normalIcon;
        _rolloverIcon = rolloverIcon;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        Composite orgComposite = g2.getComposite();
        if (_rolloverSupport.isIconFade()) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, _rolloverSupport.getIconAlpha()));
        }
        boolean rollover = _rolloverIcon != null && _rolloverIcon != _normalIcon && _rolloverSupport.isIconRollover(x, y, getIconWidth(), getIconHeight());
        if (rollover) {
            _rolloverIcon.paintIcon(c, g2, x, y);
        }
        else {
            _normalIcon.paintIcon(c, g2, x, y);
        }
        g2.setComposite(orgComposite);
    }

    @Override
    public int getIconWidth() {
        return _normalIcon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return _normalIcon.getIconHeight();
    }
}
