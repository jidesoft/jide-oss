package com.jidesoft.swing;

import java.awt.image.BufferedImage;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

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
