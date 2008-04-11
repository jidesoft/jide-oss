/*
 * @(#)SimpleStyleTreeCellRenderer.java
 *
 * Copyright 2002 - 2005 JIDE Software. All rights reserved.
 */

package com.jidesoft.tree;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.StyledLabel;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * A tree cell renderer based on StyledLabel. To use it, you should make your cell renderer extending this one and
 * override {@link #customizeStyledLabel(javax.swing.JTree,Object,boolean,boolean,boolean,int,boolean)} method. If your
 * overridden method, you can call setStyleRange() or setStyleRanges() based on the tree node value and row index.
 */
public class StyledTreeCellRenderer extends StyledLabel implements TreeCellRenderer {
    /**
     * Last tree the renderer was painted in.
     */
    private JTree tree;

    /**
     * Is the value currently selected.
     */
    protected boolean selected;
    /**
     * True if has focus.
     */
    protected boolean hasFocus;
    /**
     * True if draws focus border around icon as well.
     */
    private boolean drawsFocusBorderAroundIcon;
    /**
     * If true, a dashed line is drawn as the focus indicator.
     */
    private boolean drawDashedFocusIndicator;

    // If drawDashedFocusIndicator is true, the following are used.
    /**
     * Background color of the tree.
     */
    private Color treeBGColor;
    /**
     * Color to draw the focus indicator in, determined from the background. color.
     */
    private Color focusBGColor;

    // Icons
    /**
     * Icon used to show non-leaf nodes that aren't expanded.
     */
    transient protected Icon closedIcon;

    /**
     * Icon used to show leaf nodes.
     */
    transient protected Icon leafIcon;

    /**
     * Icon used to show non-leaf nodes that are expanded.
     */
    transient protected Icon openIcon;

    // Colors
    /**
     * Color to use for the foreground for selected nodes.
     */
    protected Color textSelectionColor;

    /**
     * Color to use for the foreground for non-selected nodes.
     */
    protected Color textNonSelectionColor;

    /**
     * Color to use for the background when a node is selected.
     */
    protected Color backgroundSelectionColor;

    /**
     * Color to use for the background when the node isn't selected.
     */
    protected Color backgroundNonSelectionColor;

    /**
     * Color to use for the focus indicator when the node has focus.
     */
    protected Color borderSelectionColor;

    /**
     * Returns a new instance of DefaultTreeCellRenderer.  Alignment is set to left aligned. Icons and text color are
     * determined from the UIManager.
     */
    public StyledTreeCellRenderer() {
        setLeafIcon(UIDefaultsLookup.getIcon("Tree.leafIcon"));
        setClosedIcon(UIDefaultsLookup.getIcon("Tree.closedIcon"));
        setOpenIcon(UIDefaultsLookup.getIcon("Tree.openIcon"));

        setTextSelectionColor(UIDefaultsLookup.getColor("Tree.selectionForeground"));
        setTextNonSelectionColor(UIDefaultsLookup.getColor("Tree.textForeground"));
        setBackgroundSelectionColor(UIDefaultsLookup.getColor("Tree.selectionBackground"));
        setBackgroundNonSelectionColor(UIDefaultsLookup.getColor("Tree.textBackground"));
        setBorderSelectionColor(UIDefaultsLookup.getColor("Tree.selectionBorderColor"));
        Object value = UIDefaultsLookup.get("Tree.drawsFocusBorderAroundIcon");
        drawsFocusBorderAroundIcon = (value != null && (Boolean) value);
        value = UIDefaultsLookup.get("Tree.drawDashedFocusIndicator");
        drawDashedFocusIndicator = (value != null && (Boolean) value);
    }


    /**
     * Returns the default icon, for the current laf, that is used to represent non-leaf nodes that are expanded.
     */
    public Icon getDefaultOpenIcon() {
        return UIDefaultsLookup.getIcon("Tree.openIcon");
    }

    /**
     * Returns the default icon, for the current laf, that is used to represent non-leaf nodes that are not expanded.
     */
    public Icon getDefaultClosedIcon() {
        return UIDefaultsLookup.getIcon("Tree.closedIcon");
    }

    /**
     * Returns the default icon, for the current laf, that is used to represent leaf nodes.
     */
    public Icon getDefaultLeafIcon() {
        return UIDefaultsLookup.getIcon("Tree.leafIcon");
    }

    /**
     * Sets the icon used to represent non-leaf nodes that are expanded.
     */
    public void setOpenIcon(Icon newIcon) {
        openIcon = newIcon;
    }

    /**
     * Returns the icon used to represent non-leaf nodes that are expanded.
     */
    public Icon getOpenIcon() {
        return openIcon;
    }

    /**
     * Sets the icon used to represent non-leaf nodes that are not expanded.
     */
    public void setClosedIcon(Icon newIcon) {
        closedIcon = newIcon;
    }

    /**
     * Returns the icon used to represent non-leaf nodes that are not expanded.
     */
    public Icon getClosedIcon() {
        return closedIcon;
    }

    /**
     * Sets the icon used to represent leaf nodes.
     */
    public void setLeafIcon(Icon newIcon) {
        leafIcon = newIcon;
    }

    /**
     * Returns the icon used to represent leaf nodes.
     */
    public Icon getLeafIcon() {
        return leafIcon;
    }

    /**
     * Sets the color the text is drawn with when the node is selected.
     */
    public void setTextSelectionColor(Color newColor) {
        textSelectionColor = newColor;
    }

    /**
     * Returns the color the text is drawn with when the node is selected.
     */
    public Color getTextSelectionColor() {
        return textSelectionColor;
    }

    /**
     * Sets the color the text is drawn with when the node isn't selected.
     */
    public void setTextNonSelectionColor(Color newColor) {
        textNonSelectionColor = newColor;
    }

    /**
     * Returns the color the text is drawn with when the node isn't selected.
     */
    public Color getTextNonSelectionColor() {
        return textNonSelectionColor;
    }

    /**
     * Sets the color to use for the background if node is selected.
     */
    public void setBackgroundSelectionColor(Color newColor) {
        backgroundSelectionColor = newColor;
    }


    /**
     * Returns the color to use for the background if node is selected.
     */
    public Color getBackgroundSelectionColor() {
        return backgroundSelectionColor;
    }

    /**
     * Sets the background color to be used for non selected nodes.
     */
    public void setBackgroundNonSelectionColor(Color newColor) {
        backgroundNonSelectionColor = newColor;
    }

    /**
     * Returns the background color to be used for non selected nodes.
     */
    public Color getBackgroundNonSelectionColor() {
        return backgroundNonSelectionColor;
    }

    /**
     * Sets the color to use for the border.
     */
    public void setBorderSelectionColor(Color newColor) {
        borderSelectionColor = newColor;
    }

    /**
     * Returns the color the border is drawn.
     */
    public Color getBorderSelectionColor() {
        return borderSelectionColor;
    }

    /**
     * Subclassed to map <code>FontUIResource</code>s to null. If <code>font</code> is null, or a
     * <code>FontUIResource</code>, this has the effect of letting the font of the JTree show through. On the other
     * hand, if <code>font</code> is non-null, and not a <code>FontUIResource</code>, the font becomes
     * <code>font</code>.
     */
    @Override
    public void setFont(Font font) {
        if (font instanceof FontUIResource)
            font = null;
        super.setFont(font);
    }

    /**
     * Gets the font of this component.
     *
     * @return this component's font; if a font has not been set for this component, the font of its parent is returned
     */
    @Override
    public Font getFont() {
        Font font = super.getFont();

        if (font == null && tree != null) {
            // Strive to return a non-null value, otherwise the html support
            // will typically pick up the wrong font in certain situations.
            font = tree.getFont();
        }
        return font;
    }

    /**
     * Subclassed to map <code>ColorUIResource</code>s to null. If <code>color</code> is null, or a
     * <code>ColorUIResource</code>, this has the effect of letting the background color of the JTree show through. On
     * the other hand, if <code>color</code> is non-null, and not a <code>ColorUIResource</code>, the background becomes
     * <code>color</code>.
     */
    @Override
    public void setBackground(Color color) {
        if (color instanceof ColorUIResource)
            color = null;
        super.setBackground(color);
    }

    /**
     * Configures the renderer based on the passed in components. The value is set from messaging the tree with
     * <code>convertValueToText</code>, which ultimately invokes <code>toString</code> on <code>value</code>. The
     * foreground color is set based on the selection and the icon is set based on the <code>leaf</code> and
     * <code>expanded</code> parameters.
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf, int row,
                                                  boolean hasFocus) {
        setOpaque(false);
        // There needs to be a way to specify disabled icons.
        if (!tree.isEnabled()) {
            setEnabled(false);
            if (leaf) {
                setDisabledIcon(getLeafIcon());
            }
            else if (expanded) {
                setDisabledIcon(getOpenIcon());
            }
            else {
                setDisabledIcon(getClosedIcon());
            }
        }
        else {
            setEnabled(true);
            if (leaf) {
                setIcon(getLeafIcon());
            }
            else if (expanded) {
                setIcon(getOpenIcon());
            }
            else {
                setIcon(getClosedIcon());
            }
        }

        setIgnoreColorSettings(sel);
        customizeStyledLabel(tree, value, sel, expanded, leaf, row, hasFocus);

        this.tree = tree;
        this.hasFocus = hasFocus;
        if (sel)
            setForeground(getTextSelectionColor());
        else
            setForeground(getTextNonSelectionColor());

        applyComponentOrientation(tree.getComponentOrientation());

        selected = sel;

        return this;
    }

    /**
     * Overrides this method to customize the styled label.
     *
     * @param tree
     * @param value
     * @param sel
     * @param expanded
     * @param leaf
     * @param row
     * @param hasFocus
     */
    protected void customizeStyledLabel(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        String stringValue = tree.convertValueToText(value, sel,
                expanded, leaf, row, hasFocus);
        clearStyleRanges();
        setText(stringValue);
    }

    /**
     * Paints the value.  The background is filled based on selected.
     */
    @Override
    public void paint(Graphics g) {
        Color bColor;

        if (selected) {
            bColor = getBackgroundSelectionColor();
        }
        else {
            bColor = getBackgroundNonSelectionColor();
            if (bColor == null)
                bColor = getBackground();
        }
        int imageOffset = -1;

        if (selected || isOpaque()) {
            if (bColor != null) {
                imageOffset = getLabelStart();
                g.setColor(bColor);
                if (getComponentOrientation().isLeftToRight()) {
                    g.fillRect(imageOffset, 0, getWidth() - imageOffset, getHeight());
                }
                else {
                    g.fillRect(0, 0, getWidth() - imageOffset, getHeight());
                }
            }
        }

        super.paint(g);

        if (hasFocus) {
            if (drawsFocusBorderAroundIcon) {
                imageOffset = 0;
            }
            else if (imageOffset == -1) {
                imageOffset = getLabelStart();
            }
            if (getComponentOrientation().isLeftToRight()) {
                paintFocus(g, imageOffset, 0, getWidth() - imageOffset,
                        getHeight());
            }
            else {
                paintFocus(g, 0, 0, getWidth() - imageOffset, getHeight());
            }
        }
    }

    private void paintFocus(Graphics g, int x, int y, int w, int h) {
        Color bsColor = getBorderSelectionColor();

        if (bsColor != null && (selected || !drawDashedFocusIndicator)) {
            g.setColor(bsColor);
            g.drawRect(x, y, w - 1, h - 1);
        }
        if (drawDashedFocusIndicator) {
            Color color;
            if (selected) {
                color = getBackgroundSelectionColor();
            }
            else {
                color = getBackgroundNonSelectionColor();
                if (color == null) {
                    color = getBackground();
                }
            }

            if (treeBGColor != color) {
                treeBGColor = color;
                focusBGColor = new Color(~color.getRGB());
            }
            g.setColor(focusBGColor);
            BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
        }
    }

    private int getLabelStart() {
        Icon icon = getIcon();
        if (icon != null && getText().trim().length() != 0) {
            return icon.getIconWidth() + Math.max(0, getIconTextGap());
        }
        return 0;
    }

    /**
     * Overrides <code>JComponent.getPreferredSize</code> to return slightly wider preferred size value.
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension retDimension = super.getPreferredSize();

        if (retDimension != null)
            retDimension = new Dimension(retDimension.width + 3,
                    retDimension.height);
        return retDimension;
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void validate() {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     *
     * @since 1.5
     */
    @Override
    public void invalidate() {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void revalidate() {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void repaint(Rectangle r) {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     *
     * @since 1.5
     */
    @Override
    public void repaint() {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        // Strings get interned...
        if (propertyName.equals("text"))
            super.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
    }

    /**
     * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }

}
