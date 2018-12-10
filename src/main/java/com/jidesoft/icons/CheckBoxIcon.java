/*
 * @(#)CheckBoxIcon.java 3/6/2012
 *
 * Copyright 2002 - 2012 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.icons;

import com.jidesoft.swing.TristateCheckBox;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The icon to paint CheckBox icon safely with any L&F.
 *
 * @since 3.3.7
 */
public class CheckBoxIcon implements Icon {
    private TristateCheckBox _checkBox;
    private Icon _checkBoxIcon;
    private BufferedImage _uncheckImage;
    private BufferedImage _checkedImage;
    private BufferedImage _mixedImage;
    private int _state;

    /**
     * The constructor.
     */
    public CheckBoxIcon() {
    }

    private void validateCheckBox() {
        if (_checkBox == null || _checkBoxIcon != UIManager.getDefaults().getIcon("CheckBox.icon")) {
            _checkBox = new TristateCheckBox();
            _checkBox.setOpaque(false);
            _checkBoxIcon = UIManager.getDefaults().getIcon("CheckBox.icon");
            _uncheckImage = null;
            _checkedImage = null;
            _mixedImage = null;
        }
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        validateCheckBox();
        if (_uncheckImage == null) {
            GraphicsConfiguration graphicsConfiguration = ((Graphics2D) g).getDeviceConfiguration();
            _uncheckImage = graphicsConfiguration.createCompatibleImage(getIconWidth(), getIconHeight(), Transparency.TRANSLUCENT);
            _checkBox.setState(TristateCheckBox.STATE_UNSELECTED);
            _checkBox.setBounds(0, 0, getIconWidth(), getIconHeight());
            _checkBox.paint(_uncheckImage.getGraphics());

            _checkedImage = graphicsConfiguration.createCompatibleImage(getIconWidth(), getIconHeight(), Transparency.TRANSLUCENT);
            _checkBox.setState(TristateCheckBox.STATE_SELECTED);
            _checkBox.setBounds(0, 0, getIconWidth(), getIconHeight());
            _checkBox.paint(_checkedImage.getGraphics());

            _mixedImage = graphicsConfiguration.createCompatibleImage(getIconWidth(), getIconHeight(), Transparency.TRANSLUCENT);
            _checkBox.setState(TristateCheckBox.STATE_MIXED);
            _checkBox.setBounds(0, 0, getIconWidth(), getIconHeight());
            _checkBox.paint(_mixedImage.getGraphics());
        }
        if (getState() == TristateCheckBox.STATE_SELECTED) {
            g.drawImage(_checkedImage, x, y, null);
        }
        else if (getState() == TristateCheckBox.STATE_UNSELECTED) {
            g.drawImage(_uncheckImage, x, y, null);
        }
        else if (getState() == TristateCheckBox.STATE_MIXED) {
            g.drawImage(_mixedImage, x, y, null);
        }
    }

    @Override
    public int getIconWidth() {
        validateCheckBox();
        return _checkBox.getPreferredSize().width;
    }

    @Override
    public int getIconHeight() {
        validateCheckBox();
        return _checkBox.getPreferredSize().height;
    }

    /**
     * Sets the state of the icon to paint.
     * <p/>
     * The state could be {@link TristateCheckBox#STATE_SELECTED}, {@link TristateCheckBox#STATE_UNSELECTED} or
     * {@link TristateCheckBox#STATE_MIXED}.
     *
     * @param state the state
     */
    public void setState(int state) {
        _state = state;
    }

    /**
     * Gets the state of the icon to paint.
     *
     * @return the state.
     * @see #setState(int)
     */
    public int getState() {
        return _state;
    }
}
