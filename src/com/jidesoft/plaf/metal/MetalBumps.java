/*
 * @(#)MetalBumps.java 4/15/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.metal;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Implements the bumps used throughout the Metal Look and Feel.
 *
 * @author Tom Santos
 * @author Steve Wilson
 * @version 1.22 12/03/01
 */


public class MetalBumps implements Icon {

    protected int xBumps;
    protected int yBumps;
    protected Color topColor;
    protected Color shadowColor;
    protected Color backColor;

    protected static Vector buffers = new Vector();
    protected BumpBuffer buffer;

    public MetalBumps(Dimension bumpArea) {
        this(bumpArea.width, bumpArea.height);
    }

    public MetalBumps(int width, int height) {
        this(width, height, MetalLookAndFeel.getPrimaryControlHighlight(),
                MetalLookAndFeel.getPrimaryControlDarkShadow(),
                MetalLookAndFeel.getPrimaryControlShadow());
    }

    public MetalBumps(int width, int height,
                      Color newTopColor, Color newShadowColor, Color newBackColor) {
        setBumpArea(width, height);
        setBumpColors(newTopColor, newShadowColor, newBackColor);
    }

    private BumpBuffer getBuffer(GraphicsConfiguration gc, Color aTopColor,
                                 Color aShadowColor, Color aBackColor) {
        if (buffer != null && buffer.hasSameConfiguration(
                gc, aTopColor, aShadowColor, aBackColor)) {
            return buffer;
        }
        BumpBuffer result = null;

        Enumeration elements = buffers.elements();

        while (elements.hasMoreElements()) {
            BumpBuffer aBuffer = (BumpBuffer) elements.nextElement();
            if (aBuffer.hasSameConfiguration(gc, aTopColor, aShadowColor,
                    aBackColor)) {
                result = aBuffer;
                break;
            }
        }
        if (result == null) {
            result = new BumpBuffer(gc, topColor, shadowColor, backColor);
            buffers.addElement(result);
        }
        return result;
    }

    public void setBumpArea(Dimension bumpArea) {
        setBumpArea(bumpArea.width, bumpArea.height);
    }

    public void setBumpArea(int width, int height) {
        xBumps = width >> 1;
        yBumps = height >> 1;
    }

    public void setBumpColors(Color newTopColor, Color newShadowColor, Color newBackColor) {
        topColor = newTopColor;
        shadowColor = newShadowColor;
        backColor = newBackColor;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        GraphicsConfiguration gc = (g instanceof Graphics2D) ?
                (GraphicsConfiguration) ((Graphics2D) g).
                        getDeviceConfiguration() : null;

        buffer = getBuffer(gc, topColor, shadowColor, backColor);

        int bufferWidth = buffer.getImageSize().width;
        int bufferHeight = buffer.getImageSize().height;
        int iconWidth = getIconWidth();
        int iconHeight = getIconHeight();
        int x2 = x + iconWidth;
        int y2 = y + iconHeight;
        int savex = x;

        while (y < y2) {
            int h = Math.min(y2 - y, bufferHeight);
            for (x = savex; x < x2; x += bufferWidth) {
                int w = Math.min(x2 - x, bufferWidth);
                g.drawImage(buffer.getImage(),
                        x, y, x + w, y + h,
                        0, 0, w, h,
                        null);
            }
            y += bufferHeight;
        }
    }

    public int getIconWidth() {
        return xBumps << 1;
    }

    public int getIconHeight() {
        return yBumps << 1;
    }
}

class BumpBuffer {

    static final int IMAGE_SIZE = 64;
    static Dimension imageSize = new Dimension(IMAGE_SIZE, IMAGE_SIZE);

    transient Image image;
    Color topColor;
    Color shadowColor;
    Color backColor;
    private GraphicsConfiguration gc;

    public BumpBuffer(GraphicsConfiguration gc, Color aTopColor,
                      Color aShadowColor, Color aBackColor) {
        this.gc = gc;
        topColor = aTopColor;
        shadowColor = aShadowColor;
        backColor = aBackColor;
        createImage();
        fillBumpBuffer();
    }

    public boolean hasSameConfiguration(GraphicsConfiguration gc,
                                        Color aTopColor, Color aShadowColor,
                                        Color aBackColor) {
        if (this.gc != null) {
            if (!this.gc.equals(gc)) {
                return false;
            }
        }
        else if (gc != null) {
            return false;
        }
        return topColor.equals(aTopColor) &&
                shadowColor.equals(aShadowColor) &&
                backColor.equals(aBackColor);
    }

    /**
     * Returns the Image containing the bumps appropriate for the passed in
     * <code>GraphicsConfiguration</code>.
     */
    public Image getImage() {
        return image;
    }

    public Dimension getImageSize() {
        return imageSize;
    }

    /**
     * Paints the bumps into the current image.
     */
    private void fillBumpBuffer() {
        Graphics g = image.getGraphics();

        g.setColor(backColor);
        g.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);

        g.setColor(topColor);
        for (int x = 0; x < IMAGE_SIZE; x += 4) {
            for (int y = 0; y < IMAGE_SIZE; y += 4) {
                g.drawLine(x, y, x, y);
                g.drawLine(x + 2, y + 2, x + 2, y + 2);
            }
        }

        g.setColor(shadowColor);
        for (int x = 0; x < IMAGE_SIZE; x += 4) {
            for (int y = 0; y < IMAGE_SIZE; y += 4) {
                g.drawLine(x + 1, y + 1, x + 1, y + 1);
                g.drawLine(x + 3, y + 3, x + 3, y + 3);
            }
        }
        g.dispose();
    }

    /**
     * Creates the image appropriate for the passed in
     * <code>GraphicsConfiguration</code>, which may be null.
     */
    private void createImage() {
        if (gc != null) {
            image = gc.createCompatibleImage(IMAGE_SIZE, IMAGE_SIZE);
        }
        else {
            int cmap[] = {backColor.getRGB(), topColor.getRGB(),
                    shadowColor.getRGB()};
            IndexColorModel icm = new IndexColorModel(8, 3, cmap, 0, false, -1,
                    DataBuffer.TYPE_BYTE);
            image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE,
                    BufferedImage.TYPE_BYTE_INDEXED, icm);
        }
    }
}

