/*
 * @(#)FastGradientPainter.java 12/12/2004
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

class FastGradientPainter {
    private static GradientCache gradientCache = new GradientCache();

    //no instantiation
    private FastGradientPainter() {
    }

    /**
     * Clears the gradient cache
     */
    public static void clearGradientCache() {
        gradientCache.clear();
    }

    /**
     * Draws a rectangular gradient in a vertical or horizontal direction. The drawing operations are hardware optimized
     * whenever possible using the Java2D hardware rendering facilities. The result is gradient rendering approaching
     * the performance of flat color rendering.
     *
     * @param g2         Graphics2D instance to use for rendering
     * @param s          shape confines of gradient
     * @param startColor starting color for gradient
     * @param endColor   ending color fro gradient
     * @param isVertical specifies a vertical or horizontal gradient
     */
    public static void drawGradient(Graphics2D g2, Shape s, Color startColor, Color endColor, boolean isVertical) {
        Rectangle r = s.getBounds();
        if (r.height <= 0 || r.width <= 0) return;

        int length = isVertical ? r.height : r.width;
        GradientInfo info = new GradientInfo(g2.getDeviceConfiguration(), length, startColor, endColor, isVertical);

        BufferedImage gradient = gradientCache.retrieve(info);
        if (gradient == null) {
            gradient = createGradientTile(info);
            gradientCache.store(info, gradient);
        }

        Shape prevClip = null;
        boolean nonRectangular = false;
        if (!r.equals(s)) {
            nonRectangular = true;
            prevClip = g2.getClip();
            g2.clip(s);
        }
        if (isVertical) {
            int w = gradient.getWidth();
            int loops = r.width / w;
            for (int i = 0; i < loops; i++)
                g2.drawImage(gradient, r.x + i * w, r.y, null);
            int rem = r.width % w;
            if (rem > 0) {
                g2.drawImage(gradient, r.x + loops * w, r.y, r.x + loops * w + rem, r.y + length, 0, 0, rem, length, null);
            }
        }
        else {
            int h = gradient.getHeight();
            int loops = r.height / h;
            for (int i = 0; i < loops; i++)
                g2.drawImage(gradient, r.x, r.y + i * h, null);
            int rem = r.height % h;
            if (rem > 0) {
                g2.drawImage(gradient, r.x, r.y + loops * h, r.x + length, r.y + loops * h + rem, 0, 0, length, rem, null);
            }
        }
        if (nonRectangular) {
            g2.setClip(prevClip);
        }
    }

    private static BufferedImage createGradientTile(GradientInfo info) {
        boolean t = info.startColor.getTransparency() > 1 || info.endColor.getTransparency() > 1;

        int dx, dy, w, h;
        if (info.isVertical) {
            dx = 0;
            h = dy = info.length;
            w = 32;
        }
        else {
            w = dx = info.length;
            dy = 0;
            h = 32;
        }
        BufferedImage img = info.gfxConfig.createCompatibleImage(w, h, t ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
        Paint gp = new GradientPaint(0, 0, info.startColor, dx, dy, info.endColor);

        Graphics2D g = img.createGraphics();
        g.setPaint(gp);
        g.fillRect(0, 0, w, h);
        g.dispose();
        return img;
    }
}

/**
 * Contains all information pertaining to a particular gradient.
 */
class GradientInfo {
    GraphicsConfiguration gfxConfig;
    int length;
    Color startColor, endColor;
    boolean isVertical;

    public GradientInfo(GraphicsConfiguration gc, int ln, Color sc, Color ec, boolean v) {
        gfxConfig = gc;
        length = ln;
        startColor = sc;
        endColor = ec;
        isVertical = v;
    }

    boolean isEquivalent(GradientInfo gi) {
        return (gi.gfxConfig.equals(gfxConfig) && gi.length == length && gi.startColor.equals(startColor) && gi.endColor.equals(endColor) && gi.isVertical == isVertical);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GradientInfo)) return false;
        return isEquivalent((GradientInfo) o);
    }

    @Override
    public String toString() {
        return "Direction:" + (isVertical ? "ver" : "hor") + ", Length: " + Integer.toString(length) + ", Color1: " + Integer.toString(startColor.getRGB(), 16) + ", Color2: " + Integer.toString(endColor.getRGB(), 16);
    }
}

/**
 * A cache utilizing SoftReferences under the hood for memory efficient handling of gradients.
 */
class GradientCache {
    private GradientCacheEntry[] gradients;
    private int size;
    private int threshold;
    private final float loadFactor;
    private final ReferenceQueue queue = new ReferenceQueue();

    GradientCache() {
        this.loadFactor = 0.75f;
        threshold = 16;
        gradients = new GradientCacheEntry[16];
    }

    BufferedImage retrieve(GradientInfo info) {
        int ln = info.length;
        GradientCacheEntry[] grads = getGradients();
        int index = bucket(ln, grads.length);
        GradientCacheEntry e = grads[index];

        while (e != null) {
            GradientInfo egi = e.getInfo();
            try {
                if (egi != null) {
                    if (e.length == ln && egi.isEquivalent(info)) {
                        return e.gradient;
                    }
                }
            }
            catch (NullPointerException npe) {
                // apparently egi will or e will be cleared anyways sometimes,
                // so we have to catch a possible NPE
                // I print the values to get a better understanding of the situation.
                // comment this if unacceptable or change to use logging if needed
//                System.err.println("e = " + e);
//                System.err.println("egi = " + egi);
            }
            e = e.next;
        }
        return null;
    }

    Object store(GradientInfo info, BufferedImage gradient) {
        GradientCacheEntry[] grads = getGradients();
        int i = bucket(info.length, grads.length);

        GradientCacheEntry e = grads[i];

        if (!entryNotInCache(e, info)) {
            System.err.println("Duplicate entry found!");
        }

        grads[i] = new GradientCacheEntry(info, gradient, queue, e);
        if (++size >= threshold)
            resize(grads.length << 1);
        return null;
    }

    void clear() {
        GradientCacheEntry[] a = getGradients();
        for(int i=0;i<a.length;i++) {
          a[i]=null;
        }
        size=0;
        threshold = 16;
        gradients = new GradientCacheEntry[16];
    }

    private boolean entryNotInCache(GradientCacheEntry e, GradientInfo info) {
        while (e != null && e.getInfo() != null) { // to fix a NPE
            if (e.length == info.length && e.getInfo().isEquivalent(info)) {
                return false;
            }
            e = e.next;
        }
        return true;
    }

    private void resize(int newCapacity) {
        GradientCacheEntry[] oldArray = getGradients();
        int oldCapacity = oldArray.length;
        if (oldCapacity == ((Integer.MAX_VALUE >> 1) + 1)) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        GradientCacheEntry[] newArray = new GradientCacheEntry[newCapacity];
        moveEntries(oldArray, newArray);
        gradients = newArray;

        if (size >= (threshold >> 1)) {
            threshold = (int) (newCapacity * loadFactor);
        }
        else {
            cleanOldCacheEntries();
            moveEntries(newArray, oldArray);
            gradients = oldArray;
        }
    }

    private GradientCacheEntry[] getGradients() {
        cleanOldCacheEntries();
        return gradients;
    }

    private static int bucket(int h, int length) {
        return h & (length - 1);
    }

    private void moveEntries(GradientCacheEntry[] src, GradientCacheEntry[] dest) {
        for (int j = 0; j < src.length; ++j) {
            GradientCacheEntry e = src[j];
            src[j] = null;
            while (e != null) {
                GradientCacheEntry next = e.next;
                Object o = e.get();
                if (o == null) {
                    e.next = null;
                    e.gradient = null;
                    size--;
                }
                else {
                    int i = bucket(e.length, dest.length);
                    e.next = dest[i];
                    dest[i] = e;
                }
                e = next;
            }
        }
    }

    private void cleanOldCacheEntries() {
        GradientCacheEntry e;
        while ((e = (GradientCacheEntry) queue.poll()) != null) {
            int i = bucket(e.length, gradients.length);

            GradientCacheEntry prev = gradients[i];
            GradientCacheEntry p = prev;
            while (p != null) {
                GradientCacheEntry next = p.next;
                if (p == e) {
                    if (prev == e)
                        gradients[i] = next;
                    else
                        prev.next = next;
                    e.next = null;
                    e.gradient = null;
                    size--;
                    break;
                }
                prev = p;
                p = next;
            }
        }
    }
}

class GradientCacheEntry extends SoftReference {
    GradientCacheEntry next;
    BufferedImage gradient;
    int length;

    GradientCacheEntry(GradientInfo info, BufferedImage gradient, ReferenceQueue queue, GradientCacheEntry next) {
        super(info, queue);
        this.next = next;
        this.gradient = gradient;
        length = info.length;
    }

    GradientInfo getInfo() {
        return (GradientInfo) get();
    }

    BufferedImage getGradient() {
        return gradient;
    }
}
