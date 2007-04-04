package com.jidesoft.swing;

import java.awt.image.BufferedImage;
import java.lang.ref.ReferenceQueue;

/**
 * A cache utilizing SoftReferences under the hood for memory efficient handling
 * of gradients.
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
