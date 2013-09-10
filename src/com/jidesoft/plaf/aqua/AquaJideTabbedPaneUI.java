/*
 * @(#)WindowsTabbedPaneUI.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.aqua;

import com.jidesoft.plaf.vsnet.VsnetJideTabbedPaneUI;
import com.jidesoft.swing.TabColorProvider;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;


/**
 * JideTabbedPane UI implementation
 */
public class AquaJideTabbedPaneUI extends VsnetJideTabbedPaneUI
{
    public static ComponentUI createUI(JComponent c)
    {
        return new AquaJideTabbedPaneUI();
    }


    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement,
                                      int tabIndex,
                                      int x, int y, int w, int h,
                                      boolean isSelected)
    {
        super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);

        if (_tabPane.getTabColorProvider() != null && _tabPane.getTabColorProvider().getBackgroundAt(tabIndex) != null)
        {
            return;
        }

        if (tabIndex >= 0 && tabIndex < _tabPane.getTabCount())
        {
            Component component = _tabPane.getComponentAt(tabIndex);
            if (component instanceof TabColorProvider && ((TabColorProvider) component).getTabBackground() != null)
            {
                return;
            }
        }

        if (!PAINT_TAB_BACKGROUND)
        {
            return;
        }

        if (!isSelected)
        {
            return;
        }

        Color[] color = AquaJideUtils.isGraphite() ? AquaJideUtils.AQUA_GRAPHITE : AquaJideUtils.AQUA_BLUE;

        if (tabRegion != null)
        {
            Graphics2D g2d = (Graphics2D) g;
            switch (tabPlacement)
            {
                case LEFT:
                    AquaJideUtils.fillAquaGradientVertical(g2d, tabRegion, color);
                    break;
                case RIGHT:
                    AquaJideUtils.fillAquaGradientVertical(g2d, tabRegion, color);
                    break;
                case BOTTOM:
                    AquaJideUtils.fillAquaGradientHorizontal(g2d, tabRegion, color);
                    break;
                case TOP:
                default:
                    AquaJideUtils.fillAquaGradientHorizontal(g2d, tabRegion, color);
                    break;
            }

        }
    }


    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected)
    {
        // no focus rect here
    }

    @Override
    protected boolean isRoundedCorner()
    {
        return true;
    }

    protected boolean isShading()
    {
        return true;
    }

    @Override
    protected Color getBorderEdgeColor()
    {
        return _shadow;
    }

    @Override
    protected TabCloseButton createNoFocusButton(int type)
    {
        return new AquaTabCloseButton(type);
    }


    private static final Color COLOR1 = new Color(130, 130, 130);
    private static final Color COLOR2 = new Color(86, 86, 86);
    private static final Color COLOR3 = new Color(252, 252, 252);

    public class AquaTabCloseButton extends TabCloseButton
    {
        /**
         * Resets the UI property to a value from the current look and feel.
         * @see JComponent#updateUI
         */
        @Override
        public void updateUI()
        {
            super.updateUI();
            setMargin(new Insets(0, 0, 0, 0));
            setBorder(BorderFactory.createEmptyBorder());
            setFocusPainted(false);
        }

        public AquaTabCloseButton()
        {
            this(CLOSE_BUTTON);
        }

        public AquaTabCloseButton(int type)
        {
            addMouseMotionListener(this);
            addMouseListener(this);
            setContentAreaFilled(false);
            setType(type);
        }

        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(16, 16);
        }

        @Override
        public Dimension getMinimumSize()
        {
            return new Dimension(5, 5);
        }

        @Override
        public Dimension getMaximumSize()
        {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            if (!isEnabled())
            {
                setMouseOver(false);
                setMousePressed(false);
            }

            // draw "icons" antialiased
            AquaJideUtils.antialiasShape(g, true);

            Color color = g.getColor();

            if (isMouseOver() && isMousePressed())
            {
                g.setColor(COLOR1);
                g.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
                g.setColor(COLOR2);
            }
            else if (isMouseOver())
            {
                g.setColor(COLOR1);
                g.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
                g.setColor(COLOR3);
            }
            else
            {
                g.setColor(COLOR1);
            }
            int centerX = getWidth() >> 1;
            int centerY = getHeight() >> 1;
            switch (getType())
            {
                case CLOSE_BUTTON:
                    if (isEnabled())
                    {
                        g.drawLine(centerX - 2, centerY - 2, centerX + 2, centerY + 2);
                        g.drawLine(centerX - 3, centerY - 2, centerX + 1, centerY + 2);
                        g.drawLine(centerX + 2, centerY - 2, centerX - 2, centerY + 2);
                        g.drawLine(centerX + 1, centerY - 2, centerX - 3, centerY + 2);
                    }
                    else
                    {
                        g.drawLine(centerX - 3, centerY - 3, centerX + 3, centerY + 3);
                        g.drawLine(centerX + 3, centerY - 3, centerX - 3, centerY + 3);
                    }
                    break;
                case EAST_BUTTON:
                    //  a bit smaller on Aqua
                    //
                    //  |
                    //  ||
                    //  |||
                    //  |||*
                    //  |||
                    //  ||
                    //  |
                    //
                {
                    if (_tabPane.getTabPlacement() == TOP || _tabPane.getTabPlacement() == BOTTOM)
                    {
                        int x = centerX + 2, y = centerY; // start point. mark as * above
                        if (isEnabled())
                        {
                            g.drawLine(x - 3, y - 3, x - 3, y + 3);
                            g.drawLine(x - 2, y - 2, x - 2, y + 2);
                            g.drawLine(x - 1, y - 1, x - 1, y + 1);
                            g.drawLine(x, y, x, y);
                        }
                        else
                        {
                            g.drawLine(x - 3, y - 3, x, y);
                            g.drawLine(x - 3, y - 3, x - 3, y + 3);
                            g.drawLine(x - 3, y + 3, x, y);
                        }
                    }
                    else
                    {
                        int x = centerX, y = centerY + 2; // start point. mark as * above
                        if (isEnabled())
                        {
                            g.drawLine(x - 3, y - 3, x + 3, y - 3);
                            g.drawLine(x - 2, y - 2, x + 2, y - 2);
                            g.drawLine(x - 1, y - 1, x + 1, y - 1);
                            g.drawLine(x, y, x, y);
                        }
                        else
                        {
                            g.drawLine(x - 3, y - 3, x, y);
                            g.drawLine(x - 3, y - 3, x + 3, y - 3);
                            g.drawLine(x + 3, y - 3, x, y);
                        }
                    }
                }
                break;
                case WEST_BUTTON:
                {
                    // a bit smaller on Aqua
                    //
                    //     |
                    //    ||
                    //   |||
                    //  *|||
                    //   |||
                    //    ||
                    //     |
                    //
                    //
                    {
                        if (_tabPane.getTabPlacement() == TOP || _tabPane.getTabPlacement() == BOTTOM)
                        {
                            int x = centerX - 3, y = centerY; // start point. mark as * above
                            if (isEnabled())
                            {
                                g.drawLine(x, y, x, y);
                                g.drawLine(x + 1, y - 1, x + 1, y + 1);
                                g.drawLine(x + 2, y - 2, x + 2, y + 2);
                                g.drawLine(x + 3, y - 3, x + 3, y + 3);
                            }
                            else
                            {
                                g.drawLine(x, y, x + 3, y - 3);
                                g.drawLine(x, y, x + 3, y + 3);
                                g.drawLine(x + 3, y - 3, x + 3, y + 3);
                            }
                        }
                        else
                        {
                            int x = centerX, y = centerY - 2; // start point. mark as * above
                            if (isEnabled())
                            {
                                g.drawLine(x, y, x, y);
                                g.drawLine(x - 1, y + 1, x + 1, y + 1);
                                g.drawLine(x - 2, y + 2, x + 2, y + 2);
                                g.drawLine(x - 3, y + 3, x + 3, y + 3);
                            }
                            else
                            {
                                g.drawLine(x, y, x - 3, y + 3);
                                g.drawLine(x, y, x + 3, y + 3);
                                g.drawLine(x - 3, y + 3, x + 3, y + 3);
                            }
                        }
                    }
                    break;
                }
                case LIST_BUTTON:
                {
                    int x = centerX, y = centerY + 2; // start point. mark as * above
                    g.drawLine(x - 3, y - 3, x + 3, y - 3);
                    g.drawLine(x - 2, y - 2, x + 2, y - 2);
                    g.drawLine(x - 1, y - 1, x + 1, y - 1);
                    g.drawLine(x, y, x, y);
                    break;
                }
            }

            g.setColor(color);
            // Disable antialiasing for shapes
            AquaJideUtils.antialiasShape(g, false);
        }

        @Override
        public boolean isOpaque()
        {
            return false;
        }
    }

    @Override
    protected void prepareEditor(TabEditor e, int tabIndex)
    {
        e.setOpaque(true);
        super.prepareEditor(e, tabIndex);
    }

}



