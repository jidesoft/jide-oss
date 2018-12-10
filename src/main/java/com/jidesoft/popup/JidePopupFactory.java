/*
 * @(#)JidePopupFactory.java 4/25/2008
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.popup;

/**
 * This class creates instances of JidePopup components. It provides a consistent means for customizing popups without
 * the need for a custom UI or overriding all methods that create popups
 */
public class JidePopupFactory {
    private static int _popupType = -1;
    private static JidePopupFactory _popupFactory;

    public JidePopup createPopup() {
        JidePopup p = new JidePopup();

        if (_popupType != -1) {
            p.setPopupType(_popupType);
        }

        return p;
    }

    /**
     * Sets the type to automatically create. This forces all created popups to be of the specified type. By default, we
     * use LIGHT_WEIGHT_POPUP.
     *
     * @param poupType the popup type
     */
    public static void setPopupType(int poupType) {
        if ((poupType != JidePopup.LIGHT_WEIGHT_POPUP) && (poupType != JidePopup.HEAVY_WEIGHT_POPUP)) {
            throw new IllegalArgumentException(
                    "invalid popup type. It must be JidePopup.HEAVY_WEIGHT_POPUP or JidePopup.LIGHT_WEIGHT_POPUP.");
        }

        _popupType = poupType;
    }

    /**
     * Sets the <code>JidePopupFactory</code> that will be used to obtain <code>JidePopup</code>s. This will throw an
     * <code>IllegalArgumentException</code> if <code>factory</code> is null.
     *
     * @param factory the shared factory
     * @throws IllegalArgumentException if <code>factory</code> is null
     * @see #createPopup
     */
    public static void setSharedInstance(JidePopupFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("JidePopupFactory can not be null");
        }

        _popupFactory = factory;
    }

    /**
     * Get the type of popups that will automatically be created
     *
     * @return the type of popups that will automatically be created or -1 if the factory won't force a popup type.
     */
    public static int getPopupType() {
        return _popupType;
    }

    /**
     * Returns the shared <code>JidePopupFactory</code> which can be used to obtain <code>JidePopup</code>s.
     *
     * @return the shared factory
     */
    public static JidePopupFactory getSharedInstance() {
        if (_popupFactory == null) {
            _popupFactory = new JidePopupFactory();
        }

        return _popupFactory;
    }
}
