package com.jidesoft.icons;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * <code>IconSetManager</code> contains all the icon sets that you purchased in one class so that you can access all of
 * them from one single place and switch to different icon set just by calling {@link #setActiveIconSetName(String)}.
 * <p/>
 * In order to use IconSetManager, you need to define and register the JIDE icon set first. You can do something like
 * this.
 * <code><pre>
 * public static IconSetManager ICON_SET_MANAGER = new IconSetManager();
 * <p/>
 * static {
 *      ICON_SET_MANAGER.add("vista", new int[]{16, 24, 32, 48, 64, 96, 128, 256}, "/com/jidesoft/icons/vista");
 *      ICON_SET_MANAGER.add("xp", new int[]{16, 24, 32, 48, 64}, "/com/jidesoft/icons/xp");
 *      // add more if you have. Right now JIDE only provides one icon set but we will add more
 * }
 * </pre></code>
 * Later on if you want to use it, for example, to get a File-New icon, you just call
 * <code><pre>
 * ICON_SET_MANAGER.getImageIcon(IconSet.File.NEW, size);
 * </pre></code>
 * The size is the size you want such as 16, 24, 32, 48, or 64 depending on what sizes are available.
 */
public class IconSetManager {
    private Map<String, IconSet> _availableStyles;
    private String _activeIconSetName = null;
    private IconSet _activeIconSet = null;

    /**
     * finds the icon set if it is available.
     *
     * @param iconSetName the icon set name
     * @return the icon set. Null if not found.
     */
    public IconSet findIconSet(String iconSetName) {
        if (_availableStyles == null) {
            return null;
        }
        return _availableStyles.get(iconSetName);
    }

    /**
     * Gets the active icon set name. The active icon set will be used when you call {@link #getImageIcon(String)}
     * method.
     *
     * @return the active icon set name.
     */
    public String getActiveIconSetName() {
        return _activeIconSetName;
    }

    /**
     * gets the active icon set.
     *
     * @return the active icon set.
     */
    public IconSet getActiveIconSet() {
        return _activeIconSet;
    }

    public void add(String name, int[] sizes, String packageName) {
        if (_availableStyles == null) {
            _availableStyles = new HashMap();
        }
        _availableStyles.put(name, new IconSet(name, sizes, packageName));
        if (_availableStyles.size() == 1) {
            setActiveIconSetName(name);
        }
    }

    public void remove(String name) {
        if (_availableStyles != null) {
            _availableStyles.remove(name);
        }
    }

    /**
     * Sets the active icon set name. If the icon set is not found, IllegalArgumentException will be thrown.
     *
     * @param activeIconSetName the new active icon set name.
     * @throws IllegalArgumentException if the icon set is not found by that name.
     */
    public void setActiveIconSetName(String activeIconSetName) {
        IconSet iconSet = findIconSet(activeIconSetName);
        if (iconSet == null) {
            throw new IllegalArgumentException("Icon set \"" + activeIconSetName + "\" not found");
        }
        _activeIconSet = iconSet;
        _activeIconSetName = activeIconSetName;
    }

    /**
     * Gets the ImageIcon.
     *
     * @param iconName the icon name as defined in IconSet.
     * @return the ImageIcon.
     */
    public ImageIcon getImageIcon(String iconName) {
        return getImageIcon(iconName, 16);
    }

    /**
     * Gets the ImageIcon.
     *
     * @param iconName the icon name as defined in IconSet.
     * @param size     the icon size. If the size is not available, it will find the closest size that is larger than
     *                 the requested size.
     * @return the ImageIcon.
     */
    public ImageIcon getImageIcon(String iconName, int size) {
        String packageName = _activeIconSet.getPackageName();
        int actualSize = _activeIconSet.getNextAvailableSize(size);
        String fullIconName = packageName + "/png/" + actualSize + "x" + actualSize + "/" + iconName;
        ImageIcon icon = IconsFactory.getImageIcon(IconSetManager.class, fullIconName);
        if (actualSize == size) {
            return icon;
        }
        else {
            return IconsFactory.getScaledImage(null, icon, size, size);
        }
    }

    /**
     * Gets the ImageIcon.
     *
     * @param iconName        the icon name as defined in IconSet.
     * @param size            the icon size. If the size is not available, it will find the closest size that is larger
     *                        than the requested size.
     * @param overlayIconName the overlay icon name as defined in IconSet.
     * @param location        the location as defined in SwingConstants - CENTER, NORTH, SOUTH, WEST, EAST, NORTH_EAST,
     *                        NORTH_WEST, SOUTH_WEST and SOUTH_EAST.
     * @return the ImageIcon.
     */
    public ImageIcon getOverlayImageIcon(String iconName, int size, String overlayIconName, int location) {
        return getOverlayImageIcon(iconName, size, overlayIconName, location, new Insets(0, 0, 0, 0));
    }

    /**
     * Gets the ImageIcon with an overlay icon on it.
     *
     * @param iconName        the icon name as defined in IconSet.
     * @param size            the icon size. If the size is not available, it will find the closest size that is larger
     *                        than the requested size.
     * @param overlayIconName the overlay icon name as defined in IconSet. The icons in the Overlay category are half
     *                        the size of the regular icons. So this method will take the middle portion of the overlay
     *                        icon specified in the overlayIconName and use it as the overlay icon.
     * @param location        the location as defined in SwingConstants - CENTER, NORTH, SOUTH, WEST, EAST, NORTH_EAST,
     *                        NORTH_WEST, SOUTH_WEST and SOUTH_EAST.
     * @param insets          the margin of the overlay icon to the border of the icon.
     * @return the ImageIcon.
     */
    public ImageIcon getOverlayImageIcon(String iconName, int size, String overlayIconName, int location, Insets insets) {
        ImageIcon icon = getImageIcon(iconName, size);
        if (icon == null) {
            return null;
        }
        ImageIcon overlay = getImageIcon(overlayIconName, size);
        if (overlay == null) {
            return icon;
        }

        // grab the middle portion of the overlay icon only
        overlay = IconsFactory.getIcon(null, overlay, size / 4, size / 4, size / 2, size / 2);

        return IconsFactory.getOverlayIcon(null, icon, overlay, location, insets);
    }

    /**
     * Gets the ImageIcon with an overlay icon on it.
     *
     * @param iconName        the icon name as defined in IconSet.
     * @param size            the icon size. If the size is not available, it will find the closest size that is larger
     *                        than the requested size.
     * @param overlayIconName the overlay icon name as defined in IconSet. The overlay icon doesn't have to be in the
     *                        overlay category. It can be any regular icon. Of course the size of the overlay icon
     *                        should be smaller than the size of the regular icon.
     * @param overlayIconSize the size of the overlay icon.
     * @param location        the location as defined in SwingConstants - CENTER, NORTH, SOUTH, WEST, EAST, NORTH_EAST,
     *                        NORTH_WEST, SOUTH_WEST and SOUTH_EAST.
     * @param insets          the margin of the overlay icon to the border of the icon.
     * @return the ImageIcon.
     */
    public ImageIcon getOverlayImageIcon(String iconName, int size, String overlayIconName, int overlayIconSize, int location, Insets insets) {
        ImageIcon icon = getImageIcon(iconName, size);
        if (icon == null) {
            return null;
        }
        ImageIcon overlay = getImageIcon(overlayIconName, overlayIconSize);
        if (overlay == null) {
            return icon;
        }

        return IconsFactory.getOverlayIcon(null, icon, overlay, location, insets);
    }

//    public static void main(String[] args) {
//        IconSetManager manager = new IconSetManager();
//        manager.add("xp", new int[]{16, 24, 32, 48, 64}, "/com/jidesoft/icons/xp");
//        JFrame frame = new JFrame();
//        frame.add(new JLabel(manager.getImageIcon(IconSet.Direction.BOTTOM, 64)));
//        frame.pack();
//        frame.setVisible(true);
//    }
}
