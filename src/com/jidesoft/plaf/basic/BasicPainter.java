package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.*;
import com.jidesoft.utils.ColorUtils;
import com.jidesoft.utils.SecurityUtils;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import java.awt.*;

/**
 * Painter for JIDE styles.
 * <p/>
 * Please note, this class is an internal class which is meant to be used by other JIDE classes only. Future version
 * might break your build if you use it.
 */
public class BasicPainter implements SwingConstants, ThemePainter {
    private static BasicPainter _instance;

    public static ThemePainter getInstance() {
        if (_instance == null) {
            _instance = new BasicPainter();
        }
        return _instance;
    }

    protected Color _bk0;
    protected Color _bk1;
    protected Color _bk2;
    protected Color _bk3;
    protected Color _borderColor;

    public BasicPainter() {
    }

    public void installDefaults() {
        if (_bk0 == null) {
            _bk0 = UIDefaultsLookup.getColor("JideButton.background");
        }
        if (_bk1 == null) {
            _bk1 = UIDefaultsLookup.getColor("JideButton.focusedBackground");
        }
        if (_bk2 == null) {
            _bk2 = UIDefaultsLookup.getColor("JideButton.selectedBackground");
        }
        if (_bk3 == null) {
            _bk3 = UIDefaultsLookup.getColor("JideButton.selectedAndFocusedBackground");
        }
        if (_borderColor == null) {
            _borderColor = UIDefaultsLookup.getColor("JideButton.borderColor");
        }
    }

    public void uninstallDefaults() {
        _borderColor = null;
        _bk0 = null;
        _bk1 = null;
        _bk2 = null;
        _bk3 = null;
    }

    public Color getGripperForeground() {
        return UIDefaultsLookup.getColor("Gripper.foreground");
    }

    public Color getGripperForegroundLt() {
        return UIDefaultsLookup.getColor("JideButton.highlight");
    }

    public Color getSeparatorForeground() {
        return UIDefaultsLookup.getColor("JideButton.shadow");
    }

    public Color getSeparatorForegroundLt() {
        return UIDefaultsLookup.getColor("JideButton.highlight");
    }

    public Color getCollapsiblePaneContentBackground() {
        return UIDefaultsLookup.getColor("CollapsiblePane.contentBackground");
    }

    public Color getCollapsiblePaneTitleForeground() {
        return UIDefaultsLookup.getColor("CollapsiblePane.foreground");
    }

    public Color getCollapsiblePaneTitleForegroundEmphasized() {
        return UIDefaultsLookup.getColor("CollapsiblePane.emphasizedForeground");
    }

    public Color getCollapsiblePaneFocusTitleForegroundEmphasized() {
        return UIDefaultsLookup.getColor("CollapsiblePane.emphasizedForeground");
    }

    public Color getCollapsiblePaneFocusTitleForeground() {
        return UIDefaultsLookup.getColor("CollapsiblePane.foreground");
    }

    public ImageIcon getCollapsiblePaneUpIcon() {
        return (ImageIcon) UIDefaultsLookup.getIcon("CollapsiblePane.upIcon");
    }

    public ImageIcon getCollapsiblePaneDownIcon() {
        return (ImageIcon) UIDefaultsLookup.getIcon("CollapsiblePane.downIcon");
    }

    public ImageIcon getCollapsiblePaneUpIconEmphasized() {
        return getCollapsiblePaneUpIcon();
    }

    public ImageIcon getCollapsiblePaneDownIconEmphasized() {
        return getCollapsiblePaneDownIcon();
    }

    public ImageIcon getCollapsiblePaneTitleButtonBackground() {
        return (ImageIcon) UIDefaultsLookup.getIcon("CollapsiblePane.titleButtonBackground");
    }

    public ImageIcon getCollapsiblePaneTitleButtonBackgroundEmphasized() {
        return (ImageIcon) UIDefaultsLookup.getIcon("CollapsiblePane.titleButtonBackground.emphasized");
    }

    public ImageIcon getCollapsiblePaneUpMask() {
        return getCollapsiblePaneUpIcon();
    }

    public ImageIcon getCollapsiblePaneDownMask() {
        return getCollapsiblePaneDownIcon();
    }

    public Color getBackgroundDk() {
        return UIDefaultsLookup.getColor("JideButton.background");
    }

    public Color getBackgroundLt() {
        return UIDefaultsLookup.getColor("JideButton.background");
    }

    public Color getSelectionSelectedDk() {
        return _bk2;
    }

    public Color getSelectionSelectedLt() {
        return _bk2;
    }

    public Color getMenuItemBorderColor() {
        return UIDefaultsLookup.getColor("MenuItem.selectionBorderColor");
    }

    public Color getMenuItemBackground() {
        return UIDefaultsLookup.getColor("MenuItem.background");
    }

    public Color getCommandBarTitleBarBackground() {
        return UIDefaultsLookup.getColor("CommandBar.titleBarBackground");
    }

    public Color getControl() {
        return UIDefaultsLookup.getColor("JideButton.background");
    }

    public Color getControlLt() {
        return getControlShadow();
    }

    public Color getControlDk() {
        return getControlShadow();
    }

    public Color getControlShadow() {
        return UIDefaultsLookup.getColor("JideButton.shadow");
    }

    public Color getTitleBarBackground() {
        return UIDefaultsLookup.getColor("DockableFrame.activeTitleBackground");
    }

    public Color getDockableFrameTitleBarActiveForeground() {
        return UIDefaultsLookup.getColor("DockableFrame.activeTitleForeground");
    }

    public Color getDockableFrameTitleBarInactiveForeground() {
        return UIDefaultsLookup.getColor("DockableFrame.inactiveTitleForeground");
    }

    public Color getTabbedPaneSelectDk() {
        return UIDefaultsLookup.getColor("JideTabbedPane.selectedTabBackgroundDk");
    }

    public Color getTabbedPaneSelectLt() {
        return UIDefaultsLookup.getColor("JideTabbedPane.selectedTabBackgroundlt");
    }

    public Color getTabAreaBackgroundDk() {
        return UIDefaultsLookup.getColor("JideTabbedPane.tabAreaBackgroundDk");
    }

    public Color getTabAreaBackgroundLt() {
        return UIDefaultsLookup.getColor("JideTabbedPane.tabAreaBackgroundLt");
    }

    public Color getOptionPaneBannerForeground() {
        return new ColorUIResource(255, 255, 255);
    }

    public Color getOptionPaneBannerDk() {
        return new ColorUIResource(45, 96, 249);
    }

    public Color getOptionPaneBannerLt() {
        return new ColorUIResource(0, 52, 206);
    }

    public void paintSelectedMenu(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Color oldColor = g.getColor();
        g.setColor(UIDefaultsLookup.getColor("JideButton.darkShadow"));
        g.drawLine(rect.x, rect.y + rect.height, rect.x, rect.y + 1);
        g.drawLine(rect.x + rect.width - 2, rect.y, rect.x + rect.width - 2, rect.y + rect.height);
        if (orientation == SwingConstants.HORIZONTAL) {
            g.drawLine(rect.x, rect.y, rect.x + rect.width - 3, rect.y);
        }
        else {
            g.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width - 3, rect.y + rect.height - 1);
        }
        g.setColor(oldColor);
    }

    public void paintMenuItemBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        paintMenuItemBackground(c, g, rect, orientation, state, true);
    }

    public void paintMenuItemBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state, boolean showBorder) {
        paintButtonBackground(c, g, rect, orientation, state, showBorder);
    }

    public void paintButtonBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        paintButtonBackground(c, g, rect, orientation, state, true);
    }

    public void paintButtonBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state, boolean showBorder) {
        installDefaults();
        Color background = null;
        switch (state) {
            case STATE_DEFAULT:
                background = c.getBackground();
                if (background == null || background instanceof UIResource) {
                    background = _bk0;
                }
                paintBackground(c, g, rect, showBorder ? _borderColor : null, background, orientation);
                break;
            case STATE_ROLLOVER:
                if (c instanceof ComponentStateSupport) {
                    background = ((ComponentStateSupport) c).getBackgroundOfState(STATE_ROLLOVER);
                }
                if (background == null || background instanceof UIResource) {
                    background = _bk1;
                }
                paintBackground(c, g, rect, showBorder ? _borderColor : null, background, orientation);
                break;
            case STATE_SELECTED:
                if (c instanceof ComponentStateSupport) {
                    background = ((ComponentStateSupport) c).getBackgroundOfState(STATE_SELECTED);
                }
                if (background == null || background instanceof UIResource) {
                    background = _bk2;
                }
                paintBackground(c, g, rect, showBorder ? _borderColor : null, background, orientation);
                break;
            case STATE_PRESSED:
                if (c instanceof ComponentStateSupport) {
                    background = ((ComponentStateSupport) c).getBackgroundOfState(STATE_PRESSED);
                }
                if (background == null || background instanceof UIResource) {
                    background = _bk3;
                }
                paintBackground(c, g, rect, showBorder ? _borderColor : null, background, orientation);
                break;
        }
    }

    protected void paintBackground(JComponent c, Graphics g, Rectangle rect, Color borderColor, Color background, int orientation) {
        Color oldColor = g.getColor();
        if (borderColor != null) {
            boolean paintDefaultBorder = true;
            Object o = c.getClientProperty("JideButton.paintDefaultBorder");
            if (o instanceof Boolean) {
                paintDefaultBorder = (Boolean) o;
            }
            if (paintDefaultBorder) {
                g.setColor(borderColor);
                Object position = c.getClientProperty(JideButton.CLIENT_PROPERTY_SEGMENT_POSITION);
                if (position == null || JideButton.SEGMENT_POSITION_ONLY.equals(position)) {
                    g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
                }
                else if (JideButton.SEGMENT_POSITION_FIRST.equals(position)) {
                    if (orientation == SwingConstants.HORIZONTAL) {
                        g.drawRect(rect.x, rect.y, rect.width, rect.height - 1);
                    }
                    else {
                        g.drawRect(rect.x, rect.y, rect.width - 1, rect.height);
                    }
                }
                else if (JideButton.SEGMENT_POSITION_MIDDLE.equals(position)) {
                    if (orientation == SwingConstants.HORIZONTAL) {
                        g.drawRect(rect.x, rect.y, rect.width, rect.height - 1);
                    }
                    else {
                        g.drawRect(rect.x, rect.y, rect.width - 1, rect.height);
                    }
                }
                else if (JideButton.SEGMENT_POSITION_LAST.equals(position)) {
                    if (orientation == SwingConstants.HORIZONTAL) {
                        g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
                    }
                    else {
                        g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
                    }
                }
            }
            g.setColor(background);
            g.fillRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
        }
        else {
            g.setColor(background);
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
        g.setColor(oldColor);
    }

    public void paintChevronBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (state != STATE_DEFAULT) {
            paintButtonBackground(c, g, rect, orientation, state);
        }
    }

    public void paintDividerBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Color oldColor = g.getColor();
        g.setColor(UIDefaultsLookup.getColor("SplitPane.background"));
        g.fillRect(0, 0, rect.width, rect.height);
        g.setColor(oldColor);
    }

    public void paintCommandBarBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        g.setColor(UIDefaultsLookup.getColor("CommandBar.background"));
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 2, 2);
    }

    public void paintFloatingCommandBarBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        g.setColor(UIDefaultsLookup.getColor("CommandBar.background"));
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    public void paintMenuShadow(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        Color oldColor = g.getColor();
        g.setColor(UIDefaultsLookup.getColor("MenuItem.shadowColor"));
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        g.setColor(oldColor);
    }

    public void paintContentBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        g.setColor(UIDefaultsLookup.getColor("control"));
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    public void paintStatusBarBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (c.isOpaque()) {
            paintContentBackground(c, g, rect, orientation, state);
        }
    }

    public void paintCommandBarTitlePane(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        g.setColor(getCommandBarTitleBarBackground());
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    public void paintGripper(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
//        int w = Math.min(30, rect.width);
//        int h = rect.height;

        // basic painter always use horizontal line to paint grippers. It's just they are short and more lines when paints vertical gripper
        // and long and fewer lines when paints horizontally.
        g.setColor(getGripperForeground());

        // shrink the rect size
        if (rect.width > rect.height) {
            rect.x = rect.x + rect.width / 2 - 10;
            rect.width = 22;
        }
        else {
            rect.y = rect.y + rect.height / 2 - 10;
            rect.height = 22;
        }

        if (orientation == SwingConstants.HORIZONTAL) {
            if (rect.width <= 30) {
                final int MARGIN = 3;
                for (int i = 0; i < (rect.height - 2 * MARGIN) / 2; i++) {
                    g.drawLine(rect.x + 3, rect.y + MARGIN + i * 2, rect.x + rect.width - MARGIN, rect.y + MARGIN + i * 2);
                }
            }
            else { // for gripper in popup
                final int MARGIN = 2;
                for (int i = 0; i < (rect.height - 2 * MARGIN) / 2; i++) {
                    g.drawLine((rect.width - rect.width) / 2, rect.y + MARGIN + i * 2, (rect.width + rect.width) / 2, rect.y + MARGIN + i * 2);
                }
            }
        }
        else {
            final int MARGIN = 3;
            int count = (rect.width - 2 * MARGIN) / 2;
            for (int i = 0; i < count; i++) {
                int x = rect.x + rect.width / 2 - count + i * 2;
                g.drawLine(x, rect.y + MARGIN, x, rect.y + rect.height - MARGIN);
            }
        }
    }

    public void paintChevronMore(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        g.setColor(UIDefaultsLookup.getColor("CommandBar.darkShadow"));
        if (orientation == SwingConstants.HORIZONTAL) {
            if (!c.getComponentOrientation().isLeftToRight()) {
                int y = rect.y + 4;
                for (int i = -2; i <= 2; i++) {
                    int offset = Math.abs(i);
                    g.drawLine(rect.x + 2 + offset, y, rect.x + 3 + offset, y);
                    g.drawLine(rect.x + 6 + offset, y, rect.x + 7 + offset, y);
                    y++;
                }
            }
            else {
                int y = rect.y + 4;
                for (int i = -2; i <= 2; i++) {
                    int offset = -Math.abs(i);
                    g.drawLine(rect.x + 4 + offset, y, rect.x + 5 + offset, y);
                    g.drawLine(rect.x + 8 + offset, y, rect.x + 9 + offset, y);
                    y++;
                }
            }
        }
        else if (orientation == SwingConstants.VERTICAL) {
            int x = rect.x + 4;
            for (int i = -2; i <= 2; i++) {
                int offset = -Math.abs(i);
                g.drawLine(x, rect.y + 4 + offset, x, rect.y + 5 + offset);
                g.drawLine(x, rect.y + 8 + offset, x, rect.y + 9 + offset);
                x++;
            }
        }
    }

    public void paintChevronOption(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        int startX;
        int startY;
        if (orientation == SwingConstants.HORIZONTAL || !c.getComponentOrientation().isLeftToRight()) {
            startX = rect.x + 3;
            startY = rect.y + rect.height - 7;
        }
        else {
            startX = rect.x + rect.width - 7;
            startY = rect.y + 3;
        }
        if (orientation == SwingConstants.HORIZONTAL || !c.getComponentOrientation().isLeftToRight()) {
            JideSwingUtilities.paintArrow(g, UIDefaultsLookup.getColor("CommandBar.darkShadow"), startX, startY, 5, SwingConstants.HORIZONTAL);
        }
        else {
            JideSwingUtilities.paintArrow(g, UIDefaultsLookup.getColor("CommandBar.darkShadow"), startX, startY, 5, orientation);
        }
    }

    public void paintFloatingChevronOption(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        int startX = rect.width / 2 - 4;
        int startY = rect.height / 2 - 2;
        if (state == STATE_ROLLOVER) {
            JideSwingUtilities.paintArrow(g, Color.BLACK, startX, startY, 9, orientation);
        }
        else {
            JideSwingUtilities.paintArrow(g, Color.WHITE, startX, startY, 9, orientation);
        }
    }

    public void paintDockableFrameBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        g.setColor(UIDefaultsLookup.getColor("DockableFrame.background"));
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    public void paintDockableFrameTitlePane(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        int x = rect.x;
        int y = rect.y;
        int w = rect.width - 1;
        int h = rect.height;
        if (c.getBorder() != null) {
            Insets insets = c.getBorder().getBorderInsets(c);
            x += insets.left;
            y += insets.top;
            w -= insets.right + insets.left;
            h -= insets.top + insets.bottom;
        }
        rect = new Rectangle(x + 1, y + 1, w - 1, h - 1);
        if (state == STATE_SELECTED) {
            g.setColor(UIDefaultsLookup.getColor("DockableFrame.activeTitleBorderColor"));
            if ("true".equals(SecurityUtils.getProperty("shadingtheme", "false"))) {
                g.drawRoundRect(x, y, w, h, 2, 2);
            }
            else {
                g.drawRect(x, y, w, h);
            }
            g.setColor(UIDefaultsLookup.getColor("DockableFrame.activeTitleBackground"));
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
        else {
            g.setColor(UIDefaultsLookup.getColor("DockableFrame.inactiveTitleBorderColor"));
            g.drawRoundRect(x, y, w, h, 2, 2);
            g.setColor(UIDefaultsLookup.getColor("DockableFrame.inactiveTitleBackground"));
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
        if ("true".equals(SecurityUtils.getProperty("shadingtheme", "false"))) {
            JideSwingUtilities.fillGradient(g, rect, SwingConstants.HORIZONTAL);
        }
    }

    public void paintCollapsiblePaneTitlePaneBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (!(c.getBackground() instanceof UIResource)) {
            g.setColor(c.getBackground());
        }
        else {
            g.setColor(UIDefaultsLookup.getColor("CollapsiblePane.background"));
        }
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    public void paintCollapsiblePaneTitlePaneBackgroundEmphasized(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (!(c.getBackground() instanceof UIResource)) {
            g.setColor(c.getBackground());
        }
        else {
            g.setColor(UIDefaultsLookup.getColor("CollapsiblePane.emphasizedBackground"));
        }
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    public void paintCollapsiblePanesBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (!(c.getBackground() instanceof UIResource)) {
            g.setColor(c.getBackground());
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
        else {
            g.setColor(UIDefaultsLookup.getColor("TextField.background"));
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
    }

    public void paintCollapsiblePaneTitlePaneBackgroundPlainEmphasized(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        g.setColor(UIDefaultsLookup.getColor("CollapsiblePane.emphasizedBackground"));
        g.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width, rect.y + rect.height - 1);
    }

    public void paintCollapsiblePaneTitlePaneBackgroundPlain(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (!(c.getBackground() instanceof UIResource)) {
            g.setColor(c.getBackground());
        }
        else {
            g.setColor(UIDefaultsLookup.getColor("CollapsiblePane.background"));
        }
        switch (orientation) {
            case SwingConstants.EAST:
                g.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width - 1, rect.y + rect.height - 1);
                break;
            case SwingConstants.WEST:
                g.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height - 1);
                break;
            case SwingConstants.NORTH:
                g.drawLine(rect.x, rect.y, rect.x + rect.width, rect.y);
            case SwingConstants.SOUTH:
            default:
                g.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width, rect.y + rect.height - 1);
                break;
        }
    }

    public void paintCollapsiblePaneTitlePaneBackgroundSeparatorEmphasized(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        g.setColor(UIManager.getColor("CollapsiblePane.emphasizedBackground"));
        g.fillRect(rect.x, rect.y, rect.x + rect.width, rect.height);
    }

    public void paintCollapsiblePaneTitlePaneBackgroundSeparator(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        g.setColor(UIManager.getColor("CollapsiblePane.background"));
        g.fillRect(rect.x, rect.y, rect.x + rect.width, rect.height);
    }

    public Color getColor(Object key) {
        return UIDefaultsLookup.getColor(key);
    }

    public void paintTabAreaBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (c.isOpaque() && c instanceof JideTabbedPane) {
            JideTabbedPane tabbedPane = (JideTabbedPane) c;
            int tabShape = tabbedPane.getTabShape();
            int colorTheme = tabbedPane.getColorTheme();
            if (tabShape == JideTabbedPane.SHAPE_BOX) {
                g.setColor(UIDefaultsLookup.getColor("control"));
            }
            else {
                if (colorTheme == JideTabbedPane.COLOR_THEME_WIN2K) {
                    g.setColor(UIDefaultsLookup.getColor("control"));

                }
                else if (colorTheme == JideTabbedPane.COLOR_THEME_VSNET) {
                    g.setColor(UIDefaultsLookup.getColor("JideTabbedPane.tabAreaBackground"));
                }
                else {
                    g.setColor(UIDefaultsLookup.getColor("control"));
                }
            }
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
    }

    public void paintTabBackground(JComponent c, Graphics g, Shape region, Color[] colors, int orientation, int state) {
        Graphics2D g2d = (Graphics2D) g.create();
        if (state == STATE_DEFAULT) {
            Color backgroundStart = colors[0];
            Color backgroundEnd = colors[1];
            if (backgroundEnd != null && backgroundStart != null) {
                int tabPlacement = JideTabbedPane.TOP;
                if (c instanceof JideTabbedPane) {
                    tabPlacement = ((JideTabbedPane) c).getTabPlacement();
                }
                switch (tabPlacement) {
                    case JideTabbedPane.LEFT:
                        JideSwingUtilities.fillGradient(g2d, region, backgroundStart, backgroundEnd, false);
                        break;
                    case JideTabbedPane.RIGHT:
                        JideSwingUtilities.fillGradient(g2d, region, backgroundEnd, backgroundStart, false);
                        break;
                    case JideTabbedPane.BOTTOM:
                        JideSwingUtilities.fillGradient(g2d, region, backgroundEnd, backgroundStart, true);
                        break;
                    case JideTabbedPane.TOP:
                    default:
                        JideSwingUtilities.fillGradient(g2d, region, backgroundStart, backgroundEnd, true);
                        break;
                }
            }
        }
        g2d.dispose();
    }

    public void paintTabContentBorder(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {

    }

    public void paintSidePaneItemBackground(JComponent c, Graphics g, Rectangle rect, Color[] colors, int orientation, int state) {
        Color startColor = colors != null && colors.length > 0 ? colors[0] : c.getBackground();
        Color endColor = colors != null && colors.length > 1 ? colors[1] : startColor;
        switch (orientation) {
            case SwingConstants.WEST:
                JideSwingUtilities.fillGradient((Graphics2D) g, rect,
                        startColor, endColor, false);
                break;
            case SwingConstants.EAST:
                JideSwingUtilities.fillGradient((Graphics2D) g, rect,
                        endColor, startColor, false);
                break;
            case SwingConstants.NORTH:
                JideSwingUtilities.fillGradient((Graphics2D) g, rect,
                        startColor, endColor, true);
                break;
            case SwingConstants.SOUTH:
                JideSwingUtilities.fillGradient((Graphics2D) g, rect,
                        endColor, startColor, true);
                break;
        }
    }

    public void paintHeaderBoxBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        boolean isCellEditor = Boolean.TRUE.equals(c.getClientProperty(HeaderBox.CLIENT_PROPERTY_TABLE_CELL_EDITOR));

        Color baseColor = c.getBackground();
        if (baseColor instanceof UIResource) {
            baseColor = UIDefaultsLookup.getColor("HeaderBox.background");
            if (baseColor == null) {
                baseColor = UIDefaultsLookup.getColor("control");
            }
        }
        if (state == STATE_PRESSED || state == STATE_SELECTED || state == STATE_ROLLOVER) {
            Color color = ColorUtils.getDerivedColor(baseColor, 0.48f);
            if (isCellEditor) {
                g.setColor(color);
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
            }
            else {
                g.setColor(color);
                g.fillRoundRect(rect.x, rect.y, rect.width - 1, rect.height - 1, 4, 4);

                g.setColor(ColorUtils.getDerivedColor(baseColor, 0.40f));
                g.drawRoundRect(rect.x, rect.y, rect.width - 1, rect.height - 1, 6, 6);
            }

            g.setColor(ColorUtils.getDerivedColor(baseColor, 0.45f));
            g.drawLine(rect.x + 1, rect.y + rect.height - 3, rect.x + rect.width - 2, rect.y + rect.height - 3);

            g.setColor(ColorUtils.getDerivedColor(baseColor, 0.43f));
            g.drawLine(rect.x + 2, rect.y + rect.height - 2, rect.x + rect.width - 3, rect.y + rect.height - 2);

            g.setColor(ColorUtils.getDerivedColor(baseColor, 0.40f));
            g.drawLine(rect.x + 3, rect.y + rect.height - 1, rect.x + rect.width - 4, rect.y + rect.height - 1);
        }
        else {
            if (isCellEditor) {
                g.setColor(baseColor);
                g.fillRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
            }
            else {
                g.setColor(baseColor);
                g.fillRoundRect(rect.x, rect.y, rect.width - 1, rect.height - 1, 2, 2);

                g.setColor(ColorUtils.getDerivedColor(baseColor, .42f));
                g.drawRoundRect(rect.x, rect.y, rect.width - 1, rect.height - 1, 2, 4);
            }

            g.setColor(ColorUtils.getDerivedColor(baseColor, .48f));
            g.drawLine(rect.x + 1, rect.y + rect.height - 3, rect.x + rect.width - 2, rect.y + rect.height - 3);
            g.setColor(ColorUtils.getDerivedColor(baseColor, .47f));
            g.drawLine(rect.x + 1, rect.y + rect.height - 2, rect.x + rect.width - 2, rect.y + rect.height - 2);

            if (isCellEditor) {
                g.setColor(new Color(198, 197, 178));
                g.drawLine(rect.x + rect.width - 3, rect.y + 4, rect.x + rect.width - 3, rect.y + rect.height - 7);
                g.setColor(Color.WHITE);
                g.drawLine(rect.x + rect.width - 2, rect.y + 4, rect.x + rect.width - 2, rect.y + rect.height - 7);
            }
        }
    }

    public void paintToolBarSepartor(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        if (c.isOpaque()) {
            g.setColor(c.getBackground());
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }
        if (orientation == SwingConstants.HORIZONTAL) {
            g.setColor(c.getForeground());
            g.drawLine(rect.x + rect.width / 2, rect.y + 1, rect.x + rect.width / 2, rect.y + rect.height - 2);
        }
        else { // HORIZONTAL
            g.setColor(c.getForeground());
            g.drawLine(rect.x + 1, rect.y + rect.height / 2, rect.x + rect.width - 2, rect.y + rect.height / 2);
        }
    }

    public void paintPopupMenuSepartor(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
        int defaultShadowWidth = UIDefaultsLookup.getInt("MenuItem.shadowWidth");
        int defaultTextIconGap = UIDefaultsLookup.getInt("MenuItem.textIconGap");
        Color shadowColor = UIDefaultsLookup.getColor("MenuItem.shadowColor");
        Color foreground = UIDefaultsLookup.getColor("PopupMenuSeparator.foreground");
        Color background = UIDefaultsLookup.getColor("PopupMenuSeparator.background");

        g.setColor(shadowColor);
        if (c.getComponentOrientation().isLeftToRight()) {
            g.fillRect(0, 0, defaultShadowWidth, rect.height);
            if ("true".equals(SecurityUtils.getProperty("shadingtheme", "false"))) {
                JideSwingUtilities.fillSingleGradient(g, new Rectangle(rect.x, rect.y, defaultShadowWidth, rect.height), SwingConstants.EAST, 255);
            }

            g.setColor(background);
            g.fillRect(rect.x + defaultShadowWidth, rect.y, rect.width - defaultShadowWidth, rect.height);

            g.setColor(foreground);
            g.drawLine(rect.x + defaultShadowWidth + defaultTextIconGap, rect.y + 1, rect.x + rect.width, rect.y + 1);
        }
        else {
            g.fillRect(rect.x + rect.width, rect.y, defaultShadowWidth, rect.height);
            if ("true".equals(SecurityUtils.getProperty("shadingtheme", "false"))) {
                JideSwingUtilities.fillSingleGradient(g, new Rectangle(rect.x + rect.width - defaultTextIconGap, rect.y, defaultShadowWidth, 2), SwingConstants.WEST, 255);
            }

            g.setColor(background);
            g.fillRect(rect.x, rect.y, rect.width - defaultShadowWidth, rect.height);

            g.setColor(foreground);
            g.drawLine(rect.x, rect.y + 1, rect.x + rect.width - defaultShadowWidth - defaultTextIconGap, rect.y + 1);
        }
    }

    public void paintStatusBarSepartor(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {

    }

    public void fillBackground(JComponent c, Graphics g, Rectangle rect, int orientation, int state, Color color) {
        Color oldColor = g.getColor();
        g.setColor(color);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        g.setColor(oldColor);
    }
}
