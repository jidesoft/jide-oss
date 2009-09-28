package com.jidesoft.plaf.office2003;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.XPUtils;
import com.jidesoft.plaf.basic.BasicPainter;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.swing.ComponentStateSupport;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.utils.ColorUtils;
import com.jidesoft.utils.SystemInfo;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Painter for Office2003 L&F.
 * <p/>
 * Please note, this class is an internal class which is meant to be used by other JIDE classes only. Future version
 * might break your build if you use it.
 */
public class Office2003Painter extends BasicPainter {

    private static Office2003Painter _instance;

    public static ThemePainter getInstance() {
        if (_instance == null) {
            _instance = new Office2003Painter();
            PropertyChangeListener listener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (Office2003Painter.isNative()) {
                        if (XPUtils.PROPERTY_COLORNAME.equals(evt.getPropertyName())) {
                            if (evt.getNewValue() != null) {
                                _instance.setColorName((String) evt.getNewValue());
                            }
                            else {
                                _instance.setColorName("");
                            }
                        }
                        else if (XPUtils.PROPERTY_THEMEACTIVE.equals(evt.getPropertyName())) {
                            if (evt.getNewValue().equals(Boolean.FALSE))
                                _instance.setColorName("");
                            else {
                                _instance.setColorName(XPUtils.getColorName());
                            }
                        }
                    }
                }
            };
            Toolkit.getDefaultToolkit().addPropertyChangeListener(XPUtils.PROPERTY_COLORNAME, listener);
            Toolkit.getDefaultToolkit().addPropertyChangeListener(XPUtils.PROPERTY_THEMEACTIVE, listener);

            if (Office2003Painter.isNative()) {
                try {
                    if (XPUtils.isXPStyleOn()) {
                        _instance.setColorName(XPUtils.getColorName());
                    }
                    else {
                        _instance.setColorName("");
                    }
                }
                catch (UnsupportedOperationException e) {
                    _instance.setColorName("");
                }
            }
        }
        return _instance;
    }

    private String _colorName = XPUtils.DEFAULT;

    private static boolean _native = SystemInfo.isWindowsXP() || SystemInfo.isWindowsVistaAbove();

    private static Office2003Theme _defaultTheme = new DefaultOffice2003Theme();
    private static Office2003Theme _normalTheme = new Office2003Theme(XPUtils.GRAY);
    private static Office2003Theme _blueTheme = new Office2003Theme(XPUtils.BLUE);
    private static Office2003Theme _homeSteadTheme = new Office2003Theme(XPUtils.HOMESTEAD);
    private static Office2003Theme _metallicTheme = new Office2003Theme(XPUtils.METALLIC);

    private static Map<String, Office2003Theme> _themeCache = new TreeMap<String, Office2003Theme>();

    static {
        _themeCache.put(_defaultTheme.getThemeName(), _defaultTheme);
        _themeCache.put(_normalTheme.getThemeName(), _normalTheme);
        _themeCache.put(_blueTheme.getThemeName(), _blueTheme);
        _themeCache.put(_homeSteadTheme.getThemeName(), _homeSteadTheme);
        _themeCache.put(_metallicTheme.getThemeName(), _metallicTheme);

        final int SIZE = 20;
        final int MASK_SIZE = 11;

        int products = LookAndFeelFactory.getProductsUsed();

        Object uiDefaultsNormal[] = {
                "control", new ColorUIResource(219, 216, 209),
                "controlLt", new ColorUIResource(245, 244, 242),
                "controlDk", new ColorUIResource(213, 210, 202),
                "controlShadow", new ColorUIResource(128, 128, 128),

                "TabbedPane.selectDk", new ColorUIResource(230, 139, 44),
                "TabbedPane.selectLt", new ColorUIResource(255, 199, 60),

                "OptionPane.bannerLt", new ColorUIResource(0, 52, 206),
                "OptionPane.bannerDk", new ColorUIResource(45, 96, 249),
                "OptionPane.bannerForeground", new ColorUIResource(255, 255, 255),

                "Separator.foreground", new ColorUIResource(166, 166, 166),
                "Separator.foregroundLt", new ColorUIResource(255, 255, 255),

                "Gripper.foreground", new ColorUIResource(160, 160, 160),
                "Gripper.foregroundLt", new ColorUIResource(255, 255, 255),

                "Chevron.backgroundLt", new ColorUIResource(160, 160, 160),
                "Chevron.backgroundDk", new ColorUIResource(128, 128, 128),

                "Divider.backgroundLt", new ColorUIResource(110, 110, 110),
                "Divider.backgroundDk", new ColorUIResource(90, 90, 90),

                "backgroundLt", new ColorUIResource(245, 245, 244),
                "backgroundDk", new ColorUIResource(212, 208, 200),

                "selection.border", new ColorUIResource(0, 0, 128),

                "MenuItem.background", new ColorUIResource(249, 248, 247),

                "DockableFrameTitlePane.backgroundLt", new ColorUIResource(243, 242, 240),
                "DockableFrameTitlePane.backgroundDk", new ColorUIResource(212, 208, 200),
                "DockableFrameTitlePane.activeForeground", new ColorUIResource(0, 0, 0),
                "DockableFrameTitlePane.inactiveForeground", new ColorUIResource(0, 0, 0),
                "DockableFrame.backgroundLt", new ColorUIResource(234, 232, 228),
                "DockableFrame.backgroundDk", new ColorUIResource(234, 232, 228),

                "CommandBar.titleBarBackground", new ColorUIResource(128, 128, 128),
        };
        _normalTheme.putDefaults(uiDefaultsNormal);

        if ((products & LookAndFeelFactory.PRODUCT_COMPONENTS) != 0) {
            ImageIcon collapsiblePaneImage = IconsFactory.getImageIcon(Office2003WindowsUtils.class, "icons/collapsible_pane_gray.png"); // 20 x 20
            ImageIcon collapsiblePaneMask = IconsFactory.getImageIcon(Office2003WindowsUtils.class, "icons/collapsible_pane_mask.png"); // 11 x 11
            ImageIcon normalIcon = IconsFactory.getIcon(null, collapsiblePaneImage, 0, 0, SIZE, SIZE);
            ImageIcon emphasizedIcon = IconsFactory.getIcon(null, collapsiblePaneImage, SIZE, 0, SIZE, SIZE);
            ImageIcon downMark = IconsFactory.getIcon(null, collapsiblePaneMask, 0, 0, MASK_SIZE, MASK_SIZE);
            ImageIcon upMark = IconsFactory.getIcon(null, collapsiblePaneMask, 0, MASK_SIZE, MASK_SIZE, MASK_SIZE);
            uiDefaultsNormal = new Object[]{
                    "CollapsiblePane.contentBackground", new ColorUIResource(255, 255, 255),
                    "CollapsiblePanes.backgroundLt", new ColorUIResource(160, 160, 160),
                    "CollapsiblePanes.backgroundDk", new ColorUIResource(128, 128, 128),
                    "CollapsiblePaneTitlePane.backgroundLt", new ColorUIResource(255, 255, 255),
                    "CollapsiblePaneTitlePane.backgroundDk", new ColorUIResource(213, 210, 202),
                    "CollapsiblePaneTitlePane.foreground", new ColorUIResource(91, 91, 91),
                    "CollapsiblePaneTitlePane.foreground.focus", new ColorUIResource(137, 137, 137),
                    "CollapsiblePaneTitlePane.backgroundLt.emphasized", new ColorUIResource(68, 68, 68),
                    "CollapsiblePaneTitlePane.backgroundDk.emphasized", new ColorUIResource(94, 94, 94),
                    "CollapsiblePaneTitlePane.foreground.emphasized", new ColorUIResource(255, 255, 255),
                    "CollapsiblePaneTitlePane.foreground.focus.emphasized", new ColorUIResource(230, 230, 230),
                    "CollapsiblePane.downIcon", IconsFactory.getOverlayIcon(null, normalIcon, downMark, SwingConstants.CENTER),
                    "CollapsiblePane.upIcon", IconsFactory.getOverlayIcon(null, normalIcon, upMark, SwingConstants.CENTER),
                    "CollapsiblePane.downIcon.emphasized", IconsFactory.getOverlayIcon(null, emphasizedIcon, downMark, SwingConstants.CENTER),
                    "CollapsiblePane.upIcon.emphasized", IconsFactory.getOverlayIcon(null, emphasizedIcon, upMark, SwingConstants.CENTER),
                    "CollapsiblePane.upMask", upMark,
                    "CollapsiblePane.downMask", downMark,
                    "CollapsiblePane.titleButtonBackground", normalIcon,
                    "CollapsiblePane.titleButtonBackground.emphasized", emphasizedIcon,
            };
            _normalTheme.putDefaults(uiDefaultsNormal);
        }

        Object uiDefaultsBlue[] = {
                "control", new ColorUIResource(196, 219, 249),
                "controlLt", new ColorUIResource(218, 234, 253),
                "controlDk", new ColorUIResource(129, 169, 226),
                "controlShadow", new ColorUIResource(59, 67, 156),

                "TabbedPane.selectDk", new ColorUIResource(230, 139, 44),
                "TabbedPane.selectLt", new ColorUIResource(255, 199, 60),

                "OptionPane.bannerLt", new ColorUIResource(0, 52, 206),
                "OptionPane.bannerDk", new ColorUIResource(45, 96, 249),
                "OptionPane.bannerForeground", new ColorUIResource(255, 255, 255),

                "Separator.foreground", new ColorUIResource(106, 140, 203),
                "Separator.foregroundLt", new ColorUIResource(241, 249, 255),

                "Gripper.foreground", new ColorUIResource(39, 65, 118),
                "Gripper.foregroundLt", new ColorUIResource(255, 255, 255),

                "Chevron.backgroundLt", new ColorUIResource(117, 166, 241),
                "Chevron.backgroundDk", new ColorUIResource(0, 53, 145),

                "Divider.backgroundLt", new ColorUIResource(89, 135, 214),
                "Divider.backgroundDk", new ColorUIResource(0, 45, 150),

                "backgroundLt", new ColorUIResource(195, 218, 249),
                "backgroundDk", new ColorUIResource(158, 190, 245),

                "selection.border", new ColorUIResource(0, 0, 128),

                "MenuItem.background", new ColorUIResource(246, 246, 246),

                "DockableFrameTitlePane.backgroundLt", new ColorUIResource(218, 234, 253),
                "DockableFrameTitlePane.backgroundDk", new ColorUIResource(123, 164, 224),
                "DockableFrameTitlePane.activeForeground", new ColorUIResource(0, 0, 0),
                "DockableFrameTitlePane.inactiveForeground", new ColorUIResource(0, 0, 0),
                "DockableFrame.backgroundLt", new ColorUIResource(221, 236, 254),
                "DockableFrame.backgroundDk", new ColorUIResource(221, 236, 254),

                "CommandBar.titleBarBackground", new ColorUIResource(42, 102, 201),
        };
        _blueTheme.putDefaults(uiDefaultsBlue);

        if ((products & LookAndFeelFactory.PRODUCT_COMPONENTS) != 0) {
            ImageIcon collapsiblePaneImage = IconsFactory.getImageIcon(Office2003WindowsUtils.class, "icons/collapsible_pane_blue.png"); // 20 x 20
            ImageIcon collapsiblePaneMask = IconsFactory.getImageIcon(Office2003WindowsUtils.class, "icons/collapsible_pane_mask.png"); // 11 x 11
            ImageIcon normalIcon = IconsFactory.getIcon(null, collapsiblePaneImage, 0, 0, SIZE, SIZE);
            ImageIcon emphasizedIcon = IconsFactory.getIcon(null, collapsiblePaneImage, SIZE, 0, SIZE, SIZE);
            ImageIcon upMark = IconsFactory.getIcon(null, collapsiblePaneMask, 0, MASK_SIZE, MASK_SIZE, MASK_SIZE);
            ImageIcon downMark = IconsFactory.getIcon(null, collapsiblePaneMask, 0, 0, MASK_SIZE, MASK_SIZE);
            uiDefaultsNormal = new Object[]{
                    "CollapsiblePane.contentBackground", new ColorUIResource(214, 223, 247),
                    "CollapsiblePanes.backgroundLt", new ColorUIResource(123, 162, 231),
                    "CollapsiblePanes.backgroundDk", new ColorUIResource(103, 125, 217),
                    "CollapsiblePaneTitlePane.backgroundLt", new ColorUIResource(255, 255, 255),
                    "CollapsiblePaneTitlePane.backgroundDk", new ColorUIResource(198, 211, 247),
                    "CollapsiblePaneTitlePane.foreground", new ColorUIResource(33, 93, 198),
                    "CollapsiblePaneTitlePane.foreground.focus", new ColorUIResource(65, 142, 254),
                    "CollapsiblePaneTitlePane.backgroundLt.emphasized", new ColorUIResource(0, 73, 181),
                    "CollapsiblePaneTitlePane.backgroundDk.emphasized", new ColorUIResource(41, 93, 206),

                    "CollapsiblePaneTitlePane.foreground.emphasized", new ColorUIResource(255, 255, 255),
                    "CollapsiblePaneTitlePane.foreground.focus.emphasized", new ColorUIResource(65, 142, 254),
                    "CollapsiblePane.downIcon", IconsFactory.getOverlayIcon(null, normalIcon, downMark, SwingConstants.CENTER),
                    "CollapsiblePane.upIcon", IconsFactory.getOverlayIcon(null, normalIcon, upMark, SwingConstants.CENTER),
                    "CollapsiblePane.downIcon.emphasized", IconsFactory.getOverlayIcon(null, emphasizedIcon, downMark, SwingConstants.CENTER),
                    "CollapsiblePane.upIcon.emphasized", IconsFactory.getOverlayIcon(null, emphasizedIcon, upMark, SwingConstants.CENTER),
                    "CollapsiblePane.upMask", upMark,
                    "CollapsiblePane.downMask", downMark,
                    "CollapsiblePane.titleButtonBackground", normalIcon,
                    "CollapsiblePane.titleButtonBackground.emphasized", emphasizedIcon,
            };
            _blueTheme.putDefaults(uiDefaultsNormal);
        }

        // green theme
        Object uiDefaultsHomeStead[] = {
                "control", new ColorUIResource(209, 222, 173),
                "controlLt", new ColorUIResource(244, 247, 222),
                "controlDk", new ColorUIResource(183, 198, 145),
                "controlShadow", new ColorUIResource(96, 128, 88),

                "TabbedPane.selectDk", new ColorUIResource(207, 114, 37),
                "TabbedPane.selectLt", new ColorUIResource(227, 145, 79),

                "OptionPane.bannerLt", new ColorUIResource(150, 185, 120),
                "OptionPane.bannerDk", new ColorUIResource(179, 214, 149),
                "OptionPane.bannerForeground", new ColorUIResource(255, 255, 255),

                "Separator.foreground", new ColorUIResource(96, 128, 88),
                "Separator.foregroundLt", new ColorUIResource(244, 247, 242),

                "Gripper.foreground", new ColorUIResource(81, 94, 51),
                "Gripper.foregroundLt", new ColorUIResource(255, 255, 255),

                "Chevron.backgroundLt", new ColorUIResource(176, 194, 140),
                "Chevron.backgroundDk", new ColorUIResource(96, 119, 107),

                "Divider.backgroundLt", new ColorUIResource(120, 142, 111),
                "Divider.backgroundDk", new ColorUIResource(73, 91, 67),

                "backgroundLt", new ColorUIResource(242, 240, 228),
                "backgroundDk", new ColorUIResource(217, 217, 167),

                "selection.border", new ColorUIResource(63, 93, 56),

                "MenuItem.background", new ColorUIResource(244, 244, 238),

                "DockableFrameTitlePane.backgroundLt", new ColorUIResource(237, 242, 212),
                "DockableFrameTitlePane.backgroundDk", new ColorUIResource(181, 196, 143),
                "DockableFrameTitlePane.activeForeground", new ColorUIResource(0, 0, 0),
                "DockableFrameTitlePane.inactiveForeground", new ColorUIResource(0, 0, 0),
                "DockableFrame.backgroundLt", new ColorUIResource(243, 242, 231),
                "DockableFrame.backgroundDk", new ColorUIResource(243, 242, 231),

                "CommandBar.titleBarBackground", new ColorUIResource(116, 134, 94),

        };
        _homeSteadTheme.putDefaults(uiDefaultsHomeStead);

        if ((products & LookAndFeelFactory.PRODUCT_COMPONENTS) != 0) {
            ImageIcon collapsiblePaneImage = IconsFactory.getImageIcon(Office2003WindowsUtils.class, "icons/collapsible_pane_homestead.png"); // 20 x 20
            ImageIcon collapsiblePaneMask = IconsFactory.getImageIcon(Office2003WindowsUtils.class, "icons/collapsible_pane_mask.png"); // 11 x 11
            ImageIcon normalIcon = IconsFactory.getIcon(null, collapsiblePaneImage, 0, 0, SIZE, SIZE);
            ImageIcon emphasizedIcon = IconsFactory.getIcon(null, collapsiblePaneImage, SIZE, 0, SIZE, SIZE);
            ImageIcon downMark = IconsFactory.getIcon(null, collapsiblePaneMask, 0, 0, MASK_SIZE, MASK_SIZE);
            ImageIcon upMark = IconsFactory.getIcon(null, collapsiblePaneMask, 0, MASK_SIZE, MASK_SIZE, MASK_SIZE);
            uiDefaultsNormal = new Object[]{
                    "CollapsiblePane.contentBackground", new ColorUIResource(246, 246, 246),
                    "CollapsiblePanes.backgroundLt", new ColorUIResource(204, 217, 173),
                    "CollapsiblePanes.backgroundDk", new ColorUIResource(165, 189, 132),
                    "CollapsiblePaneTitlePane.backgroundLt", new ColorUIResource(254, 252, 236),
                    "CollapsiblePaneTitlePane.backgroundDk", new ColorUIResource(224, 231, 184),
                    "CollapsiblePaneTitlePane.foreground", new ColorUIResource(86, 102, 45),
                    "CollapsiblePaneTitlePane.foreground.focus", new ColorUIResource(114, 146, 29),
                    "CollapsiblePaneTitlePane.backgroundLt.emphasized", new ColorUIResource(119, 140, 64),
                    "CollapsiblePaneTitlePane.backgroundDk.emphasized", new ColorUIResource(150, 168, 103),
                    "CollapsiblePaneTitlePane.foreground.emphasized", new ColorUIResource(255, 255, 255),
                    "CollapsiblePaneTitlePane.foreground.focus.emphasized", new ColorUIResource(224, 231, 151),
                    "CollapsiblePane.downIcon", IconsFactory.getOverlayIcon(null, normalIcon, downMark, SwingConstants.CENTER),
                    "CollapsiblePane.upIcon", IconsFactory.getOverlayIcon(null, normalIcon, upMark, SwingConstants.CENTER),
                    "CollapsiblePane.downIcon.emphasized", IconsFactory.getOverlayIcon(null, emphasizedIcon, downMark, SwingConstants.CENTER),
                    "CollapsiblePane.upIcon.emphasized", IconsFactory.getOverlayIcon(null, emphasizedIcon, upMark, SwingConstants.CENTER),
                    "CollapsiblePane.upMask", upMark,
                    "CollapsiblePane.downMask", downMark,
                    "CollapsiblePane.titleButtonBackground", normalIcon,
                    "CollapsiblePane.titleButtonBackground.emphasized", emphasizedIcon,
            };
            _homeSteadTheme.putDefaults(uiDefaultsNormal);
        }

        Object uiDefaultsMetallic[] = {
                "control", new ColorUIResource(219, 218, 228),
                "controlLt", new ColorUIResource(243, 244, 250),
                "controlDk", new ColorUIResource(153, 151, 181),
                "controlShadow", new ColorUIResource(124, 124, 148),

                "TabbedPane.selectDk", new ColorUIResource(230, 139, 44),
                "TabbedPane.selectLt", new ColorUIResource(255, 200, 60),

                "OptionPane.bannerLt", new ColorUIResource(181, 195, 222),
                "OptionPane.bannerDk", new ColorUIResource(120, 140, 167),
                "OptionPane.bannerForeground", new ColorUIResource(255, 255, 255),

                "Separator.foreground", new ColorUIResource(110, 109, 143),
                "Separator.foregroundLt", new ColorUIResource(255, 255, 255),

                "Gripper.foreground", new ColorUIResource(84, 84, 117),
                "Gripper.foregroundLt", new ColorUIResource(255, 255, 255),

                "Chevron.backgroundLt", new ColorUIResource(179, 178, 200),
                "Chevron.backgroundDk", new ColorUIResource(118, 116, 146),

                "Divider.backgroundLt", new ColorUIResource(168, 167, 191),
                "Divider.backgroundDk", new ColorUIResource(119, 118, 151),

                "backgroundLt", new ColorUIResource(243, 243, 247),
                "backgroundDk", new ColorUIResource(215, 215, 229),

                "selection.border", new ColorUIResource(75, 75, 111),

                "MenuItem.background", new ColorUIResource(253, 250, 255),

                "DockableFrameTitlePane.backgroundLt", new ColorUIResource(240, 240, 248),
                "DockableFrameTitlePane.backgroundDk", new ColorUIResource(147, 145, 176),
                "DockableFrameTitlePane.activeForeground", new ColorUIResource(0, 0, 0),
                "DockableFrameTitlePane.inactiveForeground", new ColorUIResource(0, 0, 0),
                "DockableFrame.backgroundLt", new ColorUIResource(238, 238, 244),
                "DockableFrame.backgroundDk", new ColorUIResource(238, 238, 244),

                "CommandBar.titleBarBackground", new ColorUIResource(122, 121, 153),
        };
        _metallicTheme.putDefaults(uiDefaultsMetallic);

        if ((products & LookAndFeelFactory.PRODUCT_COMPONENTS) != 0) {
            ImageIcon collapsiblePaneImage = IconsFactory.getImageIcon(Office2003WindowsUtils.class, "icons/collapsible_pane_metallic.png"); // 20 x 20
            ImageIcon collapsiblePaneMask = IconsFactory.getImageIcon(Office2003WindowsUtils.class, "icons/collapsible_pane_mask.png"); // 11 x 11
            ImageIcon normalIcon = IconsFactory.getIcon(null, collapsiblePaneImage, 0, 0, SIZE, SIZE);
            ImageIcon emphasizedIcon = IconsFactory.getIcon(null, collapsiblePaneImage, SIZE, 0, SIZE, SIZE);
            ImageIcon downMark = IconsFactory.getIcon(null, collapsiblePaneMask, 0, 0, MASK_SIZE, MASK_SIZE);
            ImageIcon upMark = IconsFactory.getIcon(null, collapsiblePaneMask, 0, MASK_SIZE, MASK_SIZE, MASK_SIZE);
            uiDefaultsNormal = new Object[]{
                    "CollapsiblePane.contentBackground", new ColorUIResource(240, 241, 245),
                    "CollapsiblePanes.backgroundLt", new ColorUIResource(196, 200, 212),
                    "CollapsiblePanes.backgroundDk", new ColorUIResource(177, 179, 200),
                    "CollapsiblePaneTitlePane.backgroundLt", new ColorUIResource(255, 255, 255),
                    "CollapsiblePaneTitlePane.backgroundDk", new ColorUIResource(214, 215, 224),
                    "CollapsiblePaneTitlePane.foreground", new ColorUIResource(63, 61, 61),
                    "CollapsiblePaneTitlePane.foreground.focus", new ColorUIResource(126, 124, 124),
                    "CollapsiblePaneTitlePane.backgroundLt.emphasized", new ColorUIResource(119, 119, 145),
                    "CollapsiblePaneTitlePane.backgroundDk.emphasized", new ColorUIResource(180, 182, 199),
                    "CollapsiblePaneTitlePane.foreground.emphasized", new ColorUIResource(255, 255, 255),
                    "CollapsiblePaneTitlePane.foreground.focus.emphasized", new ColorUIResource(230, 230, 230),

                    "CollapsiblePane.downIcon", IconsFactory.getOverlayIcon(null, normalIcon, downMark, SwingConstants.CENTER),
                    "CollapsiblePane.upIcon", IconsFactory.getOverlayIcon(null, normalIcon, upMark, SwingConstants.CENTER),
                    "CollapsiblePane.downIcon.emphasized", IconsFactory.getOverlayIcon(null, emphasizedIcon, downMark, SwingConstants.CENTER),
                    "CollapsiblePane.upIcon.emphasized", IconsFactory.getOverlayIcon(null, emphasizedIcon, upMark, SwingConstants.CENTER),
                    "CollapsiblePane.upMask", upMark,
                    "CollapsiblePane.downMask", downMark,
                    "CollapsiblePane.titleButtonBackground", normalIcon,
                    "CollapsiblePane.titleButtonBackground.emphasized", emphasizedIcon,
            };
            _metallicTheme.putDefaults(uiDefaultsNormal);
        }
    }

    protected Office2003Painter() {
    }

    public void addTheme(Office2003Theme theme) {
        _themeCache.put(theme.getThemeName(), theme);
    }

    public Office2003Theme getTheme(String themeName) {
        return _themeCache.get(themeName);
    }

    public void removeTheme(String themeName) {
        _themeCache.remove(themeName);
    }

    public Collection<Office2003Theme> getAvailableThemes() {
        return _themeCache.values();
    }

    @Override
    public void installDefaults() {
    }

    @Override
    public void uninstallDefaults() {
    }

    public String getColorName() {
        return _colorName;
    }

    public void setColorName(String colorName) {
        _colorName = colorName;
    }

    public static boolean isNative() {
        return _native;
    }

    public static void setNative(boolean aNative) {
        _native = aNative;
    }

    public Office2003Theme getCurrentTheme() {
        if (getColorName() == null || getColorName().trim().length() == 0 || _themeCache.get(getColorName()) == null) {
            return _themeCache.get(XPUtils.DEFAULT);
        }
        else {
            return _themeCache.get(getColorName());
        }
    }

    @Override
    public void paintButtonBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state, boolean showBorder) {
        Color startColor = null;
        Color endColor = null;
        Color background = null;
        switch (state) {
            case STATE_DEFAULT:
                background = c.getBackground();
                if (!(background instanceof UIResource)) {
                    startColor = ColorUtils.getDerivedColor(background, 0.6f);
                    endColor = ColorUtils.getDerivedColor(background, 0.4f);
                    showBorder = false;
                }
                else {
                    startColor = getCurrentTheme().getColor("controlLt");
                    endColor = getCurrentTheme().getColor("controlDk");
                }

                break;
            case STATE_ROLLOVER:
                if (c instanceof ComponentStateSupport) {
                    background = ((ComponentStateSupport) c).getBackgroundOfState(state);
                }
                if (background != null && !(background instanceof UIResource)) {
                    startColor = ColorUtils.getDerivedColor(background, 0.6f);
                    endColor = ColorUtils.getDerivedColor(background, 0.4f);
                }
                else {
                    startColor = getCurrentTheme().getColor("selection.RolloverLt");
                    endColor = getCurrentTheme().getColor("selection.RolloverDk");
                }
                break;
            case STATE_SELECTED:
                if (c instanceof ComponentStateSupport) {
                    background = ((ComponentStateSupport) c).getBackgroundOfState(state);
                }
                if (background != null && !(background instanceof UIResource)) {
                    startColor = ColorUtils.getDerivedColor(background, 0.6f);
                    endColor = ColorUtils.getDerivedColor(background, 0.4f);
                }
                else {
                    startColor = getCurrentTheme().getColor("selection.SelectedLt");
                    endColor = getCurrentTheme().getColor("selection.SelectedDk");
                }
                break;
            case STATE_PRESSED:
                if (c instanceof ComponentStateSupport) {
                    background = ((ComponentStateSupport) c).getBackgroundOfState(state);
                }
                if (background != null && !(background instanceof UIResource)) {
                    startColor = ColorUtils.getDerivedColor(background, 0.4f);
                    endColor = ColorUtils.getDerivedColor(background, 0.6f);
                }
                else {
                    startColor = getCurrentTheme().getColor("selection.PressedDk");
                    endColor = getCurrentTheme().getColor("selection.PressedLt");
                }
                break;
        }

        if (startColor != null && endColor != null) {
            paintBackground(c, (Graphics2D) g, rect, showBorder ? getCurrentTheme().getColor("selection.border") : null, startColor, endColor, orientation);
        }
    }

    protected void paintBackground(JComponent c, Graphics2D g2d, Rectangle rect, Color borderColor, Color startColor, Color endColor, int orientation) {
        if (borderColor != null) {
            if (startColor != null && endColor != null) {
                JideSwingUtilities.fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height), startColor, endColor, orientation == SwingConstants.HORIZONTAL);
            }
            boolean paintDefaultBorder = true;
            Object o = c.getClientProperty("JideButton.paintDefaultBorder");
            if (o instanceof Boolean) {
                paintDefaultBorder = (Boolean) o;
            }
            if (paintDefaultBorder) {
                Color oldColor = g2d.getColor();
                g2d.setColor(borderColor);
                Object position = c.getClientProperty(JideButton.CLIENT_PROPERTY_SEGMENT_POSITION);
                if (position == null || JideButton.SEGMENT_POSITION_ONLY.equals(position)) {
                    g2d.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
                }
                else if (JideButton.SEGMENT_POSITION_FIRST.equals(position)) {
                    if (orientation == SwingConstants.HORIZONTAL) {
                        g2d.drawRect(rect.x, rect.y, rect.width, rect.height - 1);
                    }
                    else {
                        g2d.drawRect(rect.x, rect.y, rect.width - 1, rect.height);
                    }
                }
                else if (JideButton.SEGMENT_POSITION_MIDDLE.equals(position)) {
                    if (orientation == SwingConstants.HORIZONTAL) {
                        g2d.drawRect(rect.x, rect.y, rect.width, rect.height - 1);
                    }
                    else {
                        g2d.drawRect(rect.x, rect.y, rect.width - 1, rect.height);
                    }
                }
                else if (JideButton.SEGMENT_POSITION_LAST.equals(position)) {
                    if (orientation == SwingConstants.HORIZONTAL) {
                        g2d.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
                    }
                    else {
                        g2d.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
                    }
                }
                g2d.setColor(oldColor);
            }
        }
        else {
            if (startColor != null && endColor != null) {
                JideSwingUtilities.fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height), startColor, endColor, orientation == SwingConstants.HORIZONTAL);
            }
        }
    }

    @Override
    public void paintChevronBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        if (state == STATE_DEFAULT) {
            paintChevron(c, g2d, getCurrentTheme().getColor("Chevron.backgroundLt"), getCurrentTheme().getColor("Chevron.backgroundDk"), rect, orientation);
        }
        else if (state == STATE_ROLLOVER) {
            paintChevron(c, g2d, getCurrentTheme().getColor("selection.RolloverLt"), getCurrentTheme().getColor("selection.RolloverDk"), rect, orientation);
        }
        else if (state == STATE_SELECTED) {
            paintChevron(c, g2d, getCurrentTheme().getColor("selection.SelectedDk"), getCurrentTheme().getColor("selection.SelectedLt"), rect, orientation);
        }
        else if (state == STATE_PRESSED) {
            paintChevron(c, g2d, getCurrentTheme().getColor("selection.PressedDk"), getCurrentTheme().getColor("selection.PressedLt"), rect, orientation);
        }
    }

    @Override
    public void paintDividerBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        JideSwingUtilities.fillGradient(g2d, rect,
                getCurrentTheme().getColor("Divider.backgroundLt"), getCurrentTheme().getColor("Divider.backgroundDk"), true);
    }

    protected void paintChevron(JComponent c, Graphics2D g2d, Color color1, Color color2, Rectangle rect, int orientation) {
        if (orientation == SwingConstants.HORIZONTAL) {
            // don't use fast gradient painter as it has some problem
            if (!c.getComponentOrientation().isLeftToRight()) {
                JideSwingUtilities.fillGradient(g2d, new Rectangle(rect.x, rect.y + 2, rect.width - 2, rect.height - 4), color1, color2, true);
                g2d.setColor(color1);
                g2d.drawLine(rect.x + 2, rect.y, rect.x + rect.width - 1, rect.y);
                g2d.drawLine(rect.x + 1, rect.y + 1, rect.x + rect.width - 2, rect.y + 1);
                g2d.setColor(color2);
                g2d.drawLine(rect.x + 1, rect.y + rect.height - 2, rect.x + rect.width - 2, rect.y + rect.height - 2);
                g2d.drawLine(rect.x + 2, rect.y + rect.height - 1, rect.x + rect.width - 1, rect.y + rect.height - 1);
            }
            else {
                JideSwingUtilities.fillGradient(g2d, new Rectangle(rect.x + 2, rect.y + 2, rect.width - 2, rect.height - 4), color1, color2, true);
                g2d.setColor(color1);
                g2d.drawLine(rect.x, rect.y, rect.x + rect.width - 3, rect.y);
                g2d.drawLine(rect.x + 1, rect.y + 1, rect.x + rect.width - 2, rect.y + 1);
                g2d.setColor(color2);
                g2d.drawLine(rect.x + 1, rect.y + rect.height - 2, rect.x + rect.width - 2, rect.y + rect.height - 2);
                g2d.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width - 3, rect.y + rect.height - 1);
            }
        }
        else {
            // don't use fast gradient painter as it has some problem
            JideSwingUtilities.fillGradient(g2d, new Rectangle(rect.x + 2, rect.y + 2, rect.width - 4, rect.height - 2), color1, color2, false);
            g2d.setColor(color1);
            g2d.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height - 3);
            g2d.drawLine(rect.x + 1, rect.y + 1, rect.x + 1, rect.y + rect.height - 2);
            g2d.setColor(color2);
            g2d.drawLine(rect.x + rect.width - 2, rect.y + 1, rect.x + rect.width - 2, rect.y + rect.height - 2);
            g2d.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width - 1, rect.y + rect.height - 3);
        }
    }

    @Override
    public Color getColor(Object key) {
        return getCurrentTheme().getColor(key);
    }

    @Override
    public void paintCommandBarBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        JideSwingUtilities.fillGradient(g2d, new RoundRectangle2D.Float(rect.x, rect.y, rect.width, rect.height, 4, 4),
                getCurrentTheme().getColor("controlLt"), getCurrentTheme().getColor("controlDk"), orientation == SwingConstants.HORIZONTAL);
        g2d.setColor(getCurrentTheme().getColor("controlShadow"));
        if (orientation == SwingConstants.HORIZONTAL) {
            g2d.drawLine(rect.x + 2, rect.y + rect.height - 1, rect.x + rect.width - 3, rect.y + rect.height - 1);
        }
        else {
            g2d.drawLine(rect.x + rect.width - 1, rect.y + 2, rect.x + rect.width - 1, rect.y + rect.height - 3);
        }
    }

    @Override
    public void paintFloatingCommandBarBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        JideSwingUtilities.fillGradient(g2d, rect,
                getCurrentTheme().getColor("controlLt"), getCurrentTheme().getColor("controlDk"), orientation == SwingConstants.HORIZONTAL);
    }

    @Override
    public void paintMenuShadow(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        if (c.getComponentOrientation().isLeftToRight()) {
            JideSwingUtilities.fillGradient(g2d, rect, getCurrentTheme().getColor("controlLt"), getCurrentTheme().getColor("controlDk"), orientation != SwingConstants.HORIZONTAL);
        }
        else {
            JideSwingUtilities.fillGradient(g2d, rect, getCurrentTheme().getColor("controlDk"), getCurrentTheme().getColor("controlLt"), orientation != SwingConstants.HORIZONTAL);
        }
    }

    @Override
    public Color getControl() {
        return getCurrentTheme().getColor("control");
    }

    @Override
    public Color getControlLt() {
        return getCurrentTheme().getColor("controlLt");
    }

    @Override
    public Color getControlDk() {
        return getCurrentTheme().getColor("controlDk");
    }

    @Override
    public Color getControlShadow() {
        return getCurrentTheme().getColor("controlShadow");
    }

    @Override
    public Color getGripperForeground() {
        return getCurrentTheme().getColor("Gripper.foreground");
    }

    @Override
    public Color getGripperForegroundLt() {
        return getCurrentTheme().getColor("Gripper.foregroundLt");
    }

    @Override
    public Color getSeparatorForeground() {
        return getCurrentTheme().getColor("Separator.foreground");
    }

    @Override
    public Color getSeparatorForegroundLt() {
        return getCurrentTheme().getColor("Separator.foregroundLt");
    }

    @Override
    public Color getCollapsiblePaneContentBackground() {
        return getCurrentTheme().getColor("CollapsiblePane.contentBackground");
    }

    @Override
    public Color getCollapsiblePaneTitleForeground() {
        return getCurrentTheme().getColor("CollapsiblePaneTitlePane.foreground");
    }

    @Override
    public Color getCollapsiblePaneFocusTitleForeground() {
        return getCurrentTheme().getColor("CollapsiblePaneTitlePane.foreground.focus");
    }

    @Override
    public Color getCollapsiblePaneTitleForegroundEmphasized() {
        return getCurrentTheme().getColor("CollapsiblePaneTitlePane.foreground.emphasized");
    }

    @Override
    public Color getCollapsiblePaneFocusTitleForegroundEmphasized() {
        return getCurrentTheme().getColor("CollapsiblePaneTitlePane.foreground.focus.emphasized");
    }

    @Override
    public ImageIcon getCollapsiblePaneUpIcon() {
        return (ImageIcon) getCurrentTheme().getIcon("CollapsiblePane.upIcon");
    }

    @Override
    public ImageIcon getCollapsiblePaneDownIcon() {
        return (ImageIcon) getCurrentTheme().getIcon("CollapsiblePane.downIcon");
    }

    @Override
    public ImageIcon getCollapsiblePaneUpIconEmphasized() {
        return (ImageIcon) getCurrentTheme().getIcon("CollapsiblePane.upIcon.emphasized");
    }

    @Override
    public ImageIcon getCollapsiblePaneDownIconEmphasized() {
        return (ImageIcon) getCurrentTheme().getIcon("CollapsiblePane.downIcon.emphasized");
    }

    @Override
    public ImageIcon getCollapsiblePaneTitleButtonBackground() {
        return (ImageIcon) getCurrentTheme().getIcon("CollapsiblePane.titleButtonBackground");
    }

    @Override
    public ImageIcon getCollapsiblePaneTitleButtonBackgroundEmphasized() {
        return (ImageIcon) getCurrentTheme().getIcon("CollapsiblePane.titleButtonBackground.emphasized");
    }

    @Override
    public ImageIcon getCollapsiblePaneUpMask() {
        return (ImageIcon) getCurrentTheme().getIcon("CollapsiblePane.upMask");
    }

    @Override
    public ImageIcon getCollapsiblePaneDownMask() {
        return (ImageIcon) getCurrentTheme().getIcon("CollapsiblePane.downMask");
    }

    @Override
    public Color getBackgroundDk() {
        return getCurrentTheme().getColor("backgroundDk");
    }

    @Override
    public Color getBackgroundLt() {
        return getCurrentTheme().getColor("backgroundLt");
    }

    @Override
    public Color getSelectionSelectedDk() {
        return getCurrentTheme().getColor("selection.SelectedDk");
    }

    @Override
    public Color getSelectionSelectedLt() {
        return getCurrentTheme().getColor("selection.SelectedLt");
    }

    @Override
    public Color getMenuItemBorderColor() {
        return getCurrentTheme().getColor("selection.border");
    }

    @Override
    public Color getMenuItemBackground() {
        return getCurrentTheme().getColor("MenuItem.background");
    }

    @Override
    public Color getCommandBarTitleBarBackground() {
        return getCurrentTheme().getColor("CommandBar.titleBarBackground");
    }

    @Override
    public Color getDockableFrameTitleBarActiveForeground() {
        return getCurrentTheme().getColor("DockableFrameTitlePane.activeForeground");
    }

    @Override
    public Color getDockableFrameTitleBarInactiveForeground() {
        return getCurrentTheme().getColor("DockableFrameTitlePane.inactiveForeground");
    }

    @Override
    public Color getTitleBarBackground() {
        return getCurrentTheme().getColor("DockableFrameTitlePane.backgroundDk");
    }

    @Override
    public Color getOptionPaneBannerForeground() {
        return getCurrentTheme().getColor("OptionPane.bannerForeground");
    }

    @Override
    public Color getTabbedPaneSelectDk() {
        return getCurrentTheme().getColor("TabbedPane.selectDk");
    }

    @Override
    public Color getTabbedPaneSelectLt() {
        return getCurrentTheme().getColor("TabbedPane.selectLt");
    }

    @Override
    public Color getOptionPaneBannerDk() {
        return getCurrentTheme().getColor("OptionPane.bannerDk");
    }

    @Override
    public Color getOptionPaneBannerLt() {
        return getCurrentTheme().getColor("OptionPane.bannerLt");
    }

    @Override
    public void paintContentBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        JideSwingUtilities.fillGradient(g2d, rect, getBackgroundDk(), getBackgroundLt(), false);
    }

    @Override
    public void paintGripper(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (rect.width > 30) {
            orientation = SwingConstants.VERTICAL;
        }
        else if (rect.height > 30) {
            orientation = SwingConstants.HORIZONTAL;
        }

        int h = (orientation == SwingConstants.HORIZONTAL) ? rect.height : rect.width;
        int count = Math.min(9, (h - 6) / 4);
        int y = rect.y;
        int x = rect.x;

        if (orientation == SwingConstants.HORIZONTAL) {
            y += rect.height / 2 - count * 2;
            x += rect.width / 2 - 1;
        }
        else {
            x += rect.width / 2 - count * 2;
            y += rect.height / 2 - 1;
        }

        for (int i = 0; i < count; i++) {
            g.setColor(getGripperForegroundLt());
            g.fillRect(x + 1, y + 1, 2, 2);
            g.setColor(getGripperForeground());
            g.fillRect(x, y, 2, 2);
            if (orientation == SwingConstants.HORIZONTAL) {
                y += 4;
            }
            else {
                x += 4;
            }
        }
    }

    @Override
    public void paintChevronMore(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        int startX = rect.x + 4;
        int startY = rect.x + 5;

        int oppositeOrientation = orientation == SwingConstants.HORIZONTAL ? SwingConstants.VERTICAL : SwingConstants.HORIZONTAL;

        if (orientation == SwingConstants.HORIZONTAL) {
            if (!c.getComponentOrientation().isLeftToRight()) {
                startX = rect.width - 8;
                startX--;
                startY++;
                JideSwingUtilities.paintArrow(c, g, Color.WHITE, startX, startY, 3, oppositeOrientation);
                startX -= 4;
                JideSwingUtilities.paintArrow(c, g, Color.WHITE, startX, startY, 3, oppositeOrientation);
                startX++;
                startX += 4;
                startY--;
                JideSwingUtilities.paintArrow(c, g, Color.BLACK, startX, startY, 3, oppositeOrientation);
                startX -= 4;
                JideSwingUtilities.paintArrow(c, g, Color.BLACK, startX, startY, 3, oppositeOrientation);
            }
            else {
                startX++;
                startY++;
                JideSwingUtilities.paintArrow(g, Color.WHITE, startX, startY, 3, oppositeOrientation);
                startX += 4;
                JideSwingUtilities.paintArrow(g, Color.WHITE, startX, startY, 3, oppositeOrientation);
                startX--;
                startX -= 4;
                startY--;
                JideSwingUtilities.paintArrow(g, Color.BLACK, startX, startY, 3, oppositeOrientation);
                startX += 4;
                JideSwingUtilities.paintArrow(g, Color.BLACK, startX, startY, 3, oppositeOrientation);
            }
        }
        else if (orientation == SwingConstants.VERTICAL) {
            startX++;
            startY++;
            JideSwingUtilities.paintArrow(g, Color.WHITE, startX, startY, 3, oppositeOrientation);
            startY += 4;
            JideSwingUtilities.paintArrow(g, Color.WHITE, startX, startY, 3, oppositeOrientation);
            startX--;
            startY--;
            startY -= 4;
            JideSwingUtilities.paintArrow(g, Color.BLACK, startX, startY, 3, oppositeOrientation);
            startY += 4;
            JideSwingUtilities.paintArrow(g, Color.BLACK, startX, startY, 3, oppositeOrientation);
        }
    }

    @Override
    public void paintChevronOption(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        int startX;
        int startY;
        if (orientation == SwingConstants.HORIZONTAL) {
            if (!c.getComponentOrientation().isLeftToRight()) {
                startX = rect.x + 2;
                startY = rect.y + rect.height - 10;
            }
            else {
                startX = rect.x + rect.width - 8;
                startY = rect.y + rect.height - 10;
            }
        }
        else if (orientation == SwingConstants.VERTICAL) {
            startX = rect.x + rect.width - 10;
            startY = rect.y + rect.height - 8;
        }
        else {
            return;
        }

        startX++;
        startY++;
        g.setColor(Color.WHITE);
        paintDown(g, startX, startY, orientation);

        startX--;
        startY--;
        g.setColor(Color.BLACK);
        paintDown(g, startX, startY, orientation);
    }

    private void paintDown(Graphics g, int startX, int startY, int orientation) {
        if (orientation == SwingConstants.HORIZONTAL) {
            g.drawLine(startX, startY, startX + 4, startY);
            JideSwingUtilities.paintArrow(g, g.getColor(), startX, startY + 3, 5, SwingConstants.HORIZONTAL);
        }
        else {
            g.drawLine(startX, startY, startX, startY + 4);
            JideSwingUtilities.paintArrow(g, g.getColor(), startX + 3, startY, 5, orientation);
        }
    }

    @Override
    public void paintDockableFrameBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        JideSwingUtilities.fillGradient(g2d,
                new Rectangle(rect.x, rect.y, rect.width, rect.height),
                getCurrentTheme().getColor("DockableFrame.backgroundLt"),
                getCurrentTheme().getColor("DockableFrame.backgroundDk"),
                orientation == SwingConstants.HORIZONTAL);
    }

    @Override
    public void paintDockableFrameTitlePane(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        int x = rect.x;
        int y = rect.y;
        int w = rect.width;
        int h = rect.height;
        if (c.getBorder() != null) {
            Insets insets = c.getBorder().getBorderInsets(c);
            x += insets.left;
            y += insets.top;
            w -= insets.right + insets.left;
            h -= insets.top + insets.bottom;
        }
        rect = new Rectangle(x, y, w, h);

        boolean active = state == STATE_SELECTED;
        Graphics2D g2d = (Graphics2D) g;
        JideSwingUtilities.fillGradient(g2d, rect,
                active ? getCurrentTheme().getColor("selection.SelectedLt") : getCurrentTheme().getColor("DockableFrameTitlePane.backgroundLt"),
                active ? getCurrentTheme().getColor("selection.SelectedDk") : getCurrentTheme().getColor("DockableFrameTitlePane.backgroundDk"),
                orientation == SwingConstants.HORIZONTAL);
    }

    private void paintCollapsiblePaneTitlePane(Graphics2D g2d, Color colorLt, Color colorDk, int orientation, Rectangle rect) {
        Color old = g2d.getColor();
        g2d.setColor(colorLt);
        switch (orientation) {
            case SwingConstants.EAST:
                g2d.drawLine(rect.x + 2, rect.y, rect.x + rect.width - 1, rect.y);
                g2d.drawLine(rect.x + 1, rect.y + 1, rect.x + rect.width - 1, rect.y + 1);
                JideSwingUtilities.fillGradient(g2d,
                        new Rectangle(rect.x, rect.y + 2, rect.width, rect.height - 4),
                        colorLt,
                        colorDk,
                        true);
                g2d.setColor(colorDk);
                g2d.drawLine(rect.x + 1, rect.y + rect.height - 2, rect.x + rect.width - 1, rect.y + rect.height - 2);
                g2d.drawLine(rect.x + 2, rect.y + rect.height - 1, rect.x + rect.width - 1, rect.y + rect.height - 1);
                break;
            case SwingConstants.WEST:
                g2d.drawLine(rect.x, rect.y, rect.x + rect.width - 3, rect.y);
                g2d.drawLine(rect.x, rect.y + 1, rect.x + rect.width - 1, rect.y + 1);
                JideSwingUtilities.fillGradient(g2d,
                        new Rectangle(rect.x, rect.y + 2, rect.width, rect.height - 4),
                        colorLt,
                        colorDk,
                        true);
                g2d.setColor(colorDk);
                g2d.drawLine(rect.x, rect.y + rect.height - 2, rect.x + rect.width - 1, rect.y + rect.height - 2);
                g2d.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width - 2, rect.y + rect.height - 1);
                break;
            case SwingConstants.NORTH:
                g2d.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height - 2);
                g2d.drawLine(rect.x + 1, rect.y, rect.x + 1, rect.y + rect.height - 1);
                JideSwingUtilities.fillGradient(g2d,
                        new Rectangle(rect.x + 2, rect.y, rect.width - 4, rect.height),
                        colorLt,
                        colorDk,
                        false);
                g2d.setColor(colorDk);
                g2d.drawLine(rect.x + rect.width - 2, rect.y, rect.x + rect.width - 2, rect.y + rect.height - 1);
                g2d.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width - 1, rect.y + rect.height - 2);
                break;
            case SwingConstants.SOUTH:
                g2d.drawLine(rect.x, rect.y + 2, rect.x, rect.y + rect.height - 1);
                g2d.drawLine(rect.x + 1, rect.y + 1, rect.x + 1, rect.y + rect.height - 1);
                JideSwingUtilities.fillGradient(g2d,
                        new Rectangle(rect.x + 2, rect.y, rect.width - 4, rect.height),
                        colorLt,
                        colorDk,
                        false);
                g2d.setColor(colorDk);
                g2d.drawLine(rect.x + rect.width - 2, rect.y + 1, rect.x + rect.width - 2, rect.y + rect.height - 1);
                g2d.drawLine(rect.x + rect.width - 1, rect.y + 2, rect.x + rect.width - 1, rect.y + rect.height - 1);
                break;
        }
        g2d.setColor(old);
    }

    @Override
    public void paintCollapsiblePaneTitlePaneBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        Color background = c.getBackground();
        Color colorLt;
        Color colorDk;
        if (!(background instanceof UIResource)) {
            colorLt = ColorUtils.getDerivedColor(background, 0.6f);
            colorDk = ColorUtils.getDerivedColor(background, 0.5f);
        }
        else {
            colorLt = getCurrentTheme().getColor("CollapsiblePaneTitlePane.backgroundLt");
            colorDk = getCurrentTheme().getColor("CollapsiblePaneTitlePane.backgroundDk");
        }
        paintCollapsiblePaneTitlePane(g2d, colorLt, colorDk, orientation, rect);
    }


    @Override
    public void paintCollapsiblePaneTitlePaneBackgroundEmphasized(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        Color background = c.getBackground();
        Color colorLt;
        Color colorDk;
        if (!(background instanceof UIResource)) {
            colorLt = ColorUtils.getDerivedColor(background, 0.5f);
            colorDk = ColorUtils.getDerivedColor(background, 0.4f);
        }
        else {
            colorLt = getCurrentTheme().getColor("CollapsiblePaneTitlePane.backgroundLt.emphasized");
            colorDk = getCurrentTheme().getColor("CollapsiblePaneTitlePane.backgroundDk.emphasized");
        }
        paintCollapsiblePaneTitlePane(g2d, colorLt, colorDk, orientation, rect);
    }

    @Override
    public void paintCollapsiblePanesBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        if (!(c.getBackground() instanceof UIResource)) {
            JideSwingUtilities.fillGradient(g2d,
                    new Rectangle(rect.x, rect.y, rect.width, rect.height),
                    c.getBackground(),
                    ColorUtils.getDerivedColor(c.getBackground(), 0.6f),
                    orientation == SwingConstants.HORIZONTAL);
        }
        else {
            JideSwingUtilities.fillGradient(g2d,
                    new Rectangle(rect.x, rect.y, rect.width, rect.height),
                    getCurrentTheme().getColor("CollapsiblePanes.backgroundLt"),
                    getCurrentTheme().getColor("CollapsiblePanes.backgroundDk"),
                    orientation == SwingConstants.HORIZONTAL);
        }

    }

    private void paintCollapsiblePaneTitlePanePlain(Graphics2D g2d, Color colorDk, Color colorLt, int orientation, Rectangle rect) {
        Rectangle rectangle;
        switch (orientation) {
            case SwingConstants.EAST:
                rectangle = new Rectangle(rect.x + rect.width - 1, rect.y, 1, rect.height);
                break;
            case SwingConstants.WEST:
                rectangle = new Rectangle(rect.x, rect.y, 1, rect.height);
                break;
            case SwingConstants.NORTH:
                rectangle = new Rectangle(rect.x, rect.y, rect.width, 1);
                break;
            case SwingConstants.SOUTH:
            default:
                rectangle = new Rectangle(rect.x, rect.y + rect.height - 1, rect.width, 1);
                break;
        }
        JideSwingUtilities.fillGradient(g2d, rectangle,
                colorLt,
                colorDk,
                orientation == SwingConstants.NORTH || orientation == SwingConstants.SOUTH);
    }

    @Override
    public void paintCollapsiblePaneTitlePaneBackgroundPlainEmphasized(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        Color colorLt = getCurrentTheme().getColor("CollapsiblePaneTitlePane.backgroundLt.emphasized");
        Color colorDk = getCurrentTheme().getColor("CollapsiblePaneTitlePane.backgroundDk.emphasized");
        paintCollapsiblePaneTitlePanePlain(g2d, colorDk, colorLt, orientation, rect);
    }


    @Override
    public void paintCollapsiblePaneTitlePaneBackgroundPlain(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        Color colorLt = getCurrentTheme().getColor("CollapsiblePaneTitlePane.backgroundLt");
        Color colorDk = getCurrentTheme().getColor("CollapsiblePaneTitlePane.backgroundDk");
        paintCollapsiblePaneTitlePanePlain(g2d, colorDk, colorLt, orientation, rect);
    }

    @Override
    public void paintCollapsiblePaneTitlePaneBackgroundSeparatorEmphasized(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        Color colorLt = getCurrentTheme().getColor("CollapsiblePaneTitlePane.backgroundLt.emphasized");
        Color colorDk = getCurrentTheme().getColor("CollapsiblePaneTitlePane.backgroundDk.emphasized");
        JideSwingUtilities.fillGradient(g2d,
                new Rectangle(rect.x, rect.y, rect.width, rect.height),
                colorDk,
                colorLt,
                orientation == SwingConstants.NORTH || orientation == SwingConstants.SOUTH);
    }

    @Override
    public void paintCollapsiblePaneTitlePaneBackgroundSeparator(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g;
        Color colorLt = getCurrentTheme().getColor("backgroundLt");
        Color colorDk = getCurrentTheme().getColor("backgroundDk");
        JideSwingUtilities.fillGradient(g2d,
                new Rectangle(rect.x, rect.y, rect.width, rect.height),
                colorLt,
                colorDk,
                orientation == SwingConstants.NORTH || orientation == SwingConstants.SOUTH);
    }


    @Override
    public void paintTabAreaBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (c instanceof JideTabbedPane && ((JideTabbedPane) c).getColorTheme() != JideTabbedPane.COLOR_THEME_OFFICE2003) {
            super.paintTabAreaBackground(c, g, rect, orientation, state);
        }
        else {
            // set color of the tab area
            if (c.isOpaque()) {
                Object o = c.getClientProperty("JideTabbedPane.gradientTabArea");
                boolean useGradient = o instanceof Boolean ? (Boolean) o : UIDefaultsLookup.getBoolean("JideTabbedPane.gradientTabArea", true);
                if (c instanceof JideTabbedPane && useGradient) {
                    Graphics2D g2d = (Graphics2D) g;
                    Color startColor = getTabAreaBackgroundDk();
                    Color endColor = getTabAreaBackgroundLt();
                    int placement = ((JideTabbedPane) c).getTabPlacement();
                    switch (placement) {
                        case TOP:
                            JideSwingUtilities.fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height), startColor, endColor, true);
                            break;
                        case BOTTOM:
                            JideSwingUtilities.fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height), endColor, startColor, true);
                            break;
                        case LEFT:
                            JideSwingUtilities.fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height), startColor, endColor, false);
                            break;
                        case RIGHT:
                            JideSwingUtilities.fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height), endColor, startColor, false);
                            break;
                    }
                }
                else {
                    g.setColor(UIDefaultsLookup.getColor("control"));
                    g.fillRect(rect.x, rect.y, rect.width, rect.height);
                }
            }
        }
    }

    @Override
    public void paintHeaderBoxBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        super.paintHeaderBoxBackground(c, g, rect, orientation, state);
        if (state == STATE_ROLLOVER) {
            g.setColor(ColorUtils.getDerivedColor(getCurrentTheme().getColor("selection.Rollover"), 0.30f));
            g.drawLine(rect.x + 1, rect.y + rect.height - 3, rect.x + rect.width - 2, rect.y + rect.height - 3);
            g.setColor(ColorUtils.getDerivedColor(getCurrentTheme().getColor("selection.Rollover"), 0.35f));
            g.drawLine(rect.x + 2, rect.y + rect.height - 2, rect.x + rect.width - 3, rect.y + rect.height - 2);
            g.setColor(ColorUtils.getDerivedColor(getCurrentTheme().getColor("selection.Rollover"), 0.40f));
            g.drawLine(rect.x + 3, rect.y + rect.height - 1, rect.x + rect.width - 4, rect.y + rect.height - 1);
        }
    }

    @Override
    public void paintToolBarSepartor(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        int h = (orientation == SwingConstants.HORIZONTAL) ? rect.height : rect.width;
        h -= 9;
        int x;
        int y;

        if (JideSwingUtilities.getOrientationOf(c) == SwingConstants.HORIZONTAL) {
            y = rect.y + 5;
            x = rect.x + 1;
            g.setColor(getSeparatorForeground());
            g.drawLine(x, y, x, y + h);
            g.setColor(getSeparatorForegroundLt());
            g.drawLine(x + 1, y + 1, x + 1, y + h + 1);
        }
        else {
            y = rect.y + 1;
            x = rect.x + 5;
            g.setColor(getSeparatorForeground());
            g.drawLine(x, y, x + h, y);
            g.setColor(getSeparatorForegroundLt());
            g.drawLine(x + 1, y + 1, x + 1 + h, y + 1);
        }
    }

    @Override
    public void paintPopupMenuSepartor(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        int defaultShadowWidth = UIDefaultsLookup.getInt("MenuItem.shadowWidth");
        int defaultTextIconGap = UIDefaultsLookup.getInt("MenuItem.textIconGap");

        if (c.getComponentOrientation().isLeftToRight()) {
            paintMenuShadow(c, g, new Rectangle(rect.x, rect.y, defaultShadowWidth, rect.height), SwingConstants.HORIZONTAL, ThemePainter.STATE_DEFAULT);

            g.setColor(getMenuItemBackground());
            g.fillRect(rect.x + defaultShadowWidth, rect.y, rect.width - defaultShadowWidth, rect.height);

            g.setColor(getSeparatorForeground());
            g.drawLine(rect.x + defaultShadowWidth + defaultTextIconGap, rect.y + 1, rect.x + rect.width, rect.y + 1);
        }
        else {
            paintMenuShadow(c, g, new Rectangle(rect.x + rect.width - defaultShadowWidth, rect.y, defaultShadowWidth, rect.height), SwingConstants.HORIZONTAL, ThemePainter.STATE_DEFAULT);

            g.setColor(getMenuItemBackground());
            g.fillRect(rect.x, rect.y, rect.width - defaultShadowWidth, rect.height);

            g.setColor(getSeparatorForeground());
            g.drawLine(rect.x, rect.y + 1, rect.x + rect.width - defaultShadowWidth - defaultTextIconGap, rect.y + 1);
        }
    }

    public void paintStatusBarSepartor(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        int h = (orientation == SwingConstants.HORIZONTAL) ? c.getHeight() : c.getWidth();
        h -= 3;
        int y;
        int x;

        if (orientation == SwingConstants.HORIZONTAL) {
            x = rect.x;
            y = rect.y + 1;
            g.setColor(UIDefaultsLookup.getColor("controlShadow"));
            g.drawLine(x, y, x, y + h);
            g.setColor(UIDefaultsLookup.getColor("controlLtHighlight"));
            g.drawLine(x + 1, y, x + 1, y + h);
        }
        else {
            x = rect.x + 1;
            y = rect.y;
            g.setColor(UIDefaultsLookup.getColor("controlShadow"));
            g.drawLine(x, y, x + h, y);
            g.setColor(UIDefaultsLookup.getColor("controlLtHighlight"));
            g.drawLine(x, y + 1, x + h, y + 1);
        }

    }

    public void fillBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state, Color color) {
        Graphics2D g2d = (Graphics2D) g.create();
        if (orientation == SwingConstants.HORIZONTAL) {
            JideSwingUtilities.fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height), ColorUtils.getDerivedColor(color, 0.60f),
                    ColorUtils.getDerivedColor(color, 0.40f), true);
        }
        else {
            JideSwingUtilities.fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height), ColorUtils.getDerivedColor(color, 0.55f),
                    color, false);
        }
        g2d.dispose();
    }
}