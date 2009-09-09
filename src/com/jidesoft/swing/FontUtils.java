/*
 * @(#)FontUtils.java 9/8/2009
 *
 * Copyright 2002 - 2009 JIDE Software Inc. All rights reserved.
 *
 */

package com.jidesoft.swing;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;

/**
 * This is a global class to keep a record of Font so that we can improve the performance and memory usage in various
 * scenarios like StyledLabel.
 * <p/>
 * In this class, we have a global map of font and derived font. It probably could be huge after running a long time.
 * In that case, you need explicitly clear the font cache in this class by using {@link #clearCache()} . By default,
 * FontUtils will clear the cache once its memory exceeds the limit of {@link #getMaxCacheSize()} .
 */
public class FontUtils {
    private static class FontAttribute {
        private Font _font;
        private int _style;
        private float _size;

        FontAttribute(Font font, int style, float size) {
            _font = font;
            _style = style;
            _size = size;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FontAttribute)) {
                return false;
            }

            FontAttribute that = (FontAttribute) o;

            if (Float.compare(that._size, _size) != 0) {
                return false;
            }
            if (_style != that._style) {
                return false;
            }
            if (_font == null || !_font.equals(that._font)) {
                return false;
            }

            return true;
        }

        public int hashCode() {
            int result;
            result = _font.hashCode();
            result = 31 * result + _style;
            result = 31 * result + (_size != +0.0f ? Float.floatToIntBits(_size) : 0);
            return result;
        }
    }

    private static Map<Integer, Font> _fontCache;
    private static int MAX_CACHE_SIZE = 10000;

    /**
     * Get maximum font cache size.
     * <p/>
     * The default value is 10000. You can adjust the cache size depends on your application.
     *
     * @return the maximum font cache size.
     */
    public static int getMaxCacheSize() {
        return MAX_CACHE_SIZE;
    }

    /**
     * Set maximum font cache size.
     *
     * @param maxCacheSize the maximum font cache size
     */
    public static void setMaxCacheSize(int maxCacheSize) {
        MAX_CACHE_SIZE = maxCacheSize;
    }

    /**
     * Clear cache whenever needed. By default, we will clear the cache when the cache size is going to exceed the limit
     * defined by {@link #getMaxCacheSize()} .
     */
    public static void clearCache() {
        if (_fontCache != null) {
            _fontCache.clear();
            _fontCache = null;
        }
    }

    /**
     * Get derived font by font, style and size. At first it will get the derived font from cache. If it cannot hit the
     * derived font, it will invoke font.deriveFont to derive a font.
     *
     * @param font the original font
     * @param style the font style
     * @param size the font size
     * @return the derived font.
     */
    public static Font getCachedDerivedFont(Font font, int style, int size) {
        if (_fontCache == null) {
            _fontCache = new HashMap<Integer, Font>();
        }
        FontAttribute attribute = new FontAttribute(font, style, size);
        int hashCode = attribute.hashCode();
        Font derivedFont = _fontCache.get(hashCode);
        if (derivedFont == null) {
            derivedFont = font.deriveFont(style, size);
            _fontCache.put(hashCode, derivedFont);
        }
        if (_fontCache.size() >= getMaxCacheSize()) {
            clearCache();
        }
        return derivedFont;
    }
}
