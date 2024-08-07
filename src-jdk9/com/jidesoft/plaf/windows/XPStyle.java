/**
 * Copied from JDK source code and modified to provide additional integration between JIDE components and native windows L&F.
 */
package com.jidesoft.plaf.windows;

import com.jidesoft.plaf.windows.TMSchema.Part;
import com.jidesoft.plaf.windows.TMSchema.Prop;
import com.jidesoft.plaf.windows.TMSchema.State;
import com.jidesoft.plaf.windows.TMSchema.TypeEnum;
import com.jidesoft.utils.ReflectionUtils;
import com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsComboBoxUI;
import sun.awt.image.SunWritableRaster;
import sun.awt.windows.ThemeReader;
import sun.security.action.GetPropertyAction;
import sun.swing.CachedPainter;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.AccessController;
import java.util.HashMap;


/**
 * Implements Windows XP Styles for the Windows Look and Feel.
 *
 * @author Leif Samuelsson
 */
public class XPStyle {
    // Singleton instance of this class
    private static XPStyle xp;

    // Singleton instance of SkinPainter
    private static SkinPainter skinPainter = new SkinPainter();

    private static Boolean themeActive = null;

    private HashMap<String, Border> borderMap;
    private HashMap<String, Color> colorMap;

    private boolean flatMenus;

    static {
        invalidateStyle();

        // make sure whenever L&F changes, we clear the xpstyle.
        UIManager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ("lookAndFeel".equals(evt.getPropertyName()))
                    invalidateStyle();
            }
        });
    }

    /**
     * Static method for clearing the hashmap and loading the current XP style and theme
     */
    public static synchronized void invalidateStyle() {
        xp = null;
        themeActive = null;
        skinPainter.flush();
    }

    /**
     * Get the singleton instance of this class
     *
     * @return the singleton instance of this class or null if XP styles are not active or if this is not Windows XP
     */
    public static synchronized XPStyle getXP() {
        if (themeActive == null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            themeActive =
                    (Boolean) toolkit.getDesktopProperty("win.xpstyle.themeActive");
            if (themeActive == null) {
                themeActive = Boolean.FALSE;
            }
            if (themeActive.booleanValue()) {
                GetPropertyAction propertyAction =
                        new GetPropertyAction("swing.noxp");
                if (AccessController.doPrivileged(propertyAction) == null &&
                        ThemeReader.isThemed() &&
                        !(UIManager.getLookAndFeel()
                                instanceof WindowsClassicLookAndFeel)) {

                    xp = new XPStyle();
                }
            }
        }
        return xp;
    }

    public static boolean isVista() {
        XPStyle xp = XPStyle.getXP();
        return (xp != null && xp.isSkinDefined(null, Part.CP_DROPDOWNBUTTONRIGHT));
    }

    /**
     * Get a named <code>String</code> value from the current style
     *
     * @param part  a <code>Part</code>
     * @param state a <code>String</code>
     * @param prop  a <code>String</code>
     * @return a <code>String</code> or null if key is not found in the current style
     * <p/>
     * This is currently only used by WindowsInternalFrameTitlePane for painting title foreground and can be removed
     * when no longer needed
     */
    public String getString(Component c, Part part, State state, Prop prop) {
        return getTypeEnumName(c, part, state, prop);
    }

    public TypeEnum getTypeEnum(Component c, Part part, State state, Prop prop) {
        int enumValue = ThemeReader.getEnum(part.getControlName(c), part.getValue(),
                State.getValue(part, state),
                prop.getValue());
        return TypeEnum.getTypeEnum(prop, enumValue);
    }

    private static String getTypeEnumName(Component c, Part part, State state, Prop prop) {
        int enumValue = ThemeReader.getEnum(part.getControlName(c), part.getValue(),
                State.getValue(part, state),
                prop.getValue());
        if (enumValue == -1) {
            return null;
        }
        return TypeEnum.getTypeEnum(prop, enumValue).getName();
    }


    /**
     * Get a named <code>int</code> value from the current style
     *
     * @param part a <code>Part</code>
     * @return an <code>int</code> or null if key is not found in the current style
     */
    public int getInt(Component c, Part part, State state, Prop prop, int fallback) {
        return ThemeReader.getInt(part.getControlName(c), part.getValue(),
                State.getValue(part, state),
                prop.getValue());
    }

    /**
     * Get a named <code>Dimension</code> value from the current style
     *
     * @return a <code>Dimension</code> or null if key is not found in the current style
     * <p/>
     * This is currently only used by WindowsProgressBarUI and the value should probably be cached there instead of
     * here.
     */
    public Dimension getDimension(Component c, Part part, State state, Prop prop) {
        return ThemeReader.getPosition(part.getControlName(c), part.getValue(),
                State.getValue(part, state),
                prop.getValue());
    }

    /**
     * Get a named <code>Point</code> (e.g. a location or an offset) value from the current style
     *
     * @return a <code>Point</code> or null if key is not found in the current style
     * <p/>
     * This is currently only used by WindowsInternalFrameTitlePane for painting title foregound and can be removed when
     * no longer needed
     */
    public Point getPoint(Component c, Part part, State state, Prop prop) {
        Dimension d = ThemeReader.getPosition(part.getControlName(c), part.getValue(),
                State.getValue(part, state),
                prop.getValue());
        if (d != null) {
            return new Point(d.width, d.height);
        }
        else {
            return null;
        }
    }

    /**
     * Get a named <code>Insets</code> value from the current style
     *
     * @return an <code>Insets</code> object or null if key is not found in the current style
     * <p/>
     * This is currently only used to create borders and by WindowsInternalFrameTitlePane for painting title foregound.
     * The return value is already cached in those places.
     */
    public Insets getMargin(Component c, Part part, State state, Prop prop) {
        return ThemeReader.getThemeMargins(part.getControlName(c), part.getValue(),
                State.getValue(part, state),
                prop.getValue());
    }


    /**
     * Get a named <code>Color</code> value from the current style
     *
     * @return a <code>Color</code> or null if key is not found in the current style
     */
    public synchronized Color getColor(Skin skin, Prop prop, Color fallback) {
        String key = skin.toString() + "." + prop.name();
        Part part = skin.part;
        Color color = colorMap.get(key);
        if (color == null) {
            color = ThemeReader.getColor(part.getControlName(null), part.getValue(),
                    State.getValue(part, skin.state),
                    prop.getValue());
            if (color != null) {
                color = new ColorUIResource(color);
                colorMap.put(key, color);
            }
        }
        return (color != null) ? color : fallback;
    }

    Color getColor(Component c, Part part, State state, Prop prop, Color fallback) {
        return getColor(new Skin(c, part, state), prop, fallback);
    }


    /**
     * Get a named <code>Border</code> value from the current style
     *
     * @param part a <code>Part</code>
     * @return a <code>Border</code> or null if key is not found in the current style or if the style for the particular
     * part is not defined as "borderfill".
     */
    public synchronized Border getBorder(Component c, Part part) {
        if (part == Part.MENU) {
            // Special case because XP has no skin for menus
            if (flatMenus) {
                // TODO: The classic border uses this color, but we should
                // create a new UI property called "PopupMenu.borderColor"
                // instead.
                return new XPFillBorder(UIManager.getColor("InternalFrame.borderShadow"),
                        1);
            }
            else {
                return null;    // Will cause L&F to use classic border
            }
        }
        Skin skin = new Skin(c, part, null);
        Border border = borderMap.get(skin.string);
        if (border == null) {
            String bgType = getTypeEnumName(c, part, null, Prop.BGTYPE);
            if ("borderfill".equalsIgnoreCase(bgType)) {
                int thickness = getInt(c, part, null, Prop.BORDERSIZE, 1);
                Color color = getColor(skin, Prop.BORDERCOLOR, Color.black);
                border = new XPFillBorder(color, thickness);
                if (part == Part.CP_COMBOBOX) {
                    border = new XPStatefulFillBorder(color, thickness, part, Prop.BORDERCOLOR);
                }
            }
            else if ("imagefile".equalsIgnoreCase(bgType)) {
                Insets m = getMargin(c, part, null, Prop.SIZINGMARGINS);
                if (m != null) {
                    if (getBoolean(c, part, null, Prop.BORDERONLY)) {
                        border = new XPImageBorder(c, part);
                    }
                    else if (part == Part.CP_COMBOBOX) {
                        border = new EmptyBorder(1, 1, 1, 1);
                    }
                    else {
                        if (part == Part.TP_BUTTON) {
                            border = new XPEmptyBorder(new Insets(3, 3, 3, 3));
                        }
                        else {
                            border = new XPEmptyBorder(m);
                        }
                    }
                }
            }
            if (border != null) {
                borderMap.put(skin.string, border);
            }
        }
        return border;
    }

    private class XPFillBorder extends LineBorder implements UIResource {
        XPFillBorder(Color color, int thickness) {
            super(color, thickness);
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            Insets margin = null;
            //
            // Ideally we'd have an interface defined for classes which
            // support margins (to avoid this hackery), but we've
            // decided against it for simplicity
            //
            if (c instanceof AbstractButton) {
                margin = ((AbstractButton) c).getMargin();
            }
            else if (c instanceof JToolBar) {
                margin = ((JToolBar) c).getMargin();
            }
            else if (c instanceof JTextComponent) {
                margin = ((JTextComponent) c).getMargin();
            }
            insets.top = (margin != null ? margin.top : 0) + thickness;
            insets.left = (margin != null ? margin.left : 0) + thickness;
            insets.bottom = (margin != null ? margin.bottom : 0) + thickness;
            insets.right = (margin != null ? margin.right : 0) + thickness;

            return insets;
        }
    }

    private class XPStatefulFillBorder extends XPFillBorder {
        private final Part part;
        private final Prop prop;

        XPStatefulFillBorder(Color color, int thickness, Part part, Prop prop) {
            super(color, thickness);
            this.part = part;
            this.prop = prop;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            State state = State.NORMAL;
            // special casing for comboboxes.
            // there may be more special cases in the future
            if (c instanceof JComboBox) {
                JComboBox cb = (JComboBox) c;
                // note. in the future this should be replaced with a call
                // to BasicLookAndFeel.getUIOfType()
                if (cb.getUI() instanceof WindowsComboBoxUI) {
                    WindowsComboBoxUI wcb = (WindowsComboBoxUI) cb.getUI();
//                    state = wcb.getXPComboBoxState(cb);
                }
            }
            lineColor = getColor(c, part, state, prop, Color.black);
            super.paintBorder(c, g, x, y, width, height);
        }
    }

    private class XPImageBorder extends AbstractBorder implements UIResource {
        Skin skin;

        XPImageBorder(Component c, Part part) {
            this.skin = getSkin(c, part);
        }

        public void paintBorder(Component c, Graphics g,
                                int x, int y, int width, int height) {
            skin.paintSkin(g, x, y, width, height, null);
        }

//        public Insets getBorderInsets(Component c, Insets insets) {
//            Insets margin = null;
//            Insets borderInsets = skin.getContentMargin();
//            if (borderInsets == null) {
//                borderInsets = new Insets(0, 0, 0, 0);
//            }
//            //
//            // Ideally we'd have an interface defined for classes which
//            // support margins (to avoid this hackery), but we've
//            // decided against it for simplicity
//            //
//            if (c instanceof AbstractButton) {
//                margin = ((AbstractButton) c).getMargin();
//            }
//            else if (c instanceof JToolBar) {
//                margin = ((JToolBar) c).getMargin();
//            }
//            else if (c instanceof JTextComponent) {
//                margin = ((JTextComponent) c).getMargin();
//            }
//            insets.top = (margin != null ? margin.top : 0) + borderInsets.top;
//            insets.left = (margin != null ? margin.left : 0) + borderInsets.left;
//            insets.bottom = (margin != null ? margin.bottom : 0) + borderInsets.bottom;
//            insets.right = (margin != null ? margin.right : 0) + borderInsets.right;
//
//            return insets;
//        }
    }

    private class XPEmptyBorder extends EmptyBorder implements UIResource {
        XPEmptyBorder(Insets m) {
            super(m.top + 2, m.left + 2, m.bottom + 2, m.right + 2);
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            insets = super.getBorderInsets(c, insets);

            Insets margin = null;
            if (c instanceof AbstractButton) {
                Insets m = ((AbstractButton) c).getMargin();
                // if this is a toolbar button then ignore getMargin()
                // and subtract the padding added by the constructor
                if (c.getParent() instanceof JToolBar
                        && !(c instanceof JRadioButton)
                        && !(c instanceof JCheckBox)
                        && m instanceof InsetsUIResource) {
                    insets.top -= 2;
                    insets.left -= 2;
                    insets.bottom -= 2;
                    insets.right -= 2;
                }
                else {
                    margin = m;
                }
            }
            else if (c instanceof JToolBar) {
                margin = ((JToolBar) c).getMargin();
            }
            else if (c instanceof JTextComponent) {
                margin = ((JTextComponent) c).getMargin();
            }
            if (margin != null) {
                insets.top = margin.top + 2;
                insets.left = margin.left + 2;
                insets.bottom = margin.bottom + 2;
                insets.right = margin.right + 2;
            }
            return insets;
        }
    }

    public boolean isSkinDefined(Component c, Part part) {
        return (part.getValue() == 0)
                || ThemeReader.isThemePartDefined(
                part.getControlName(c), part.getValue(), 0);
    }


    /**
     * Get a <code>Skin</code> object from the current style for a named part (component type)
     *
     * @param part a <code>Part</code>
     * @return a <code>Skin</code> object
     */
    public synchronized Skin getSkin(Component c, Part part) {
        assert isSkinDefined(c, part) : "part " + part + " is not defined";
        return new Skin(c, part, null);
    }


    long getThemeTransitionDuration(Component c, Part part, State stateFrom,
                                    State stateTo, Prop prop) {
        return ThemeReader.getThemeTransitionDuration(part.getControlName(c),
                part.getValue(),
                State.getValue(part, stateFrom),
                State.getValue(part, stateTo),
                (prop != null) ? prop.getValue() : 0);
    }


    /**
     * A class which encapsulates attributes for a given part (component type) and which provides methods for painting
     * backgrounds and glyphs
     */
    public static class Skin {
        final Component component;
        final Part part;
        final State state;

        private final String string;
        private Dimension size = null;

        Skin(Component component, Part part) {
            this(component, part, null);
        }

        Skin(Part part, State state) {
            this(null, part, state);
        }

        Skin(Component component, Part part, State state) {
            this.component = component;
            this.part = part;
            this.state = state;

            String str = part.getControlName(component) + "." + part.name();
            if (state != null) {
                str += "(" + state.name() + ")";
            }
            string = str;
        }

//        Insets getContentMargin() {
//            /* idk: it seems margins are the same for all 'big enough'
//             * bounding rectangles.
//             */
//            int boundingWidth = 100;
//            int boundingHeight = 100;
//
//            return ThemeReader.getThemeBackgroundContentMargins(
//                    part.getControlName(null), part.getValue(),
//                    0, boundingWidth, boundingHeight);
//        }

        private int getWidth(State state) {
            if (size == null) {
                size = getPartSize(part, state);
            }
            return size.width;
        }

        int getWidth() {
            return getWidth((state != null) ? state : State.NORMAL);
        }

        private int getHeight(State state) {
            if (size == null) {
                size = getPartSize(part, state);
            }
            return size.height;
        }

        int getHeight() {
            return getHeight((state != null) ? state : State.NORMAL);
        }

        public String toString() {
            return string;
        }

        public boolean equals(Object obj) {
            return (obj instanceof Skin && ((Skin) obj).string.equals(string));
        }

        public int hashCode() {
            return string.hashCode();
        }

        /**
         * Paint a skin at x, y.
         *
         * @param g     the graphics context to use for painting
         * @param dx    the destination <i>x</i> coordinate
         * @param dy    the destination <i>y</i> coordinate
         * @param state which state to paint
         */
        public void paintSkin(Graphics g, int dx, int dy, State state) {
            if (state == null) {
                state = this.state;
            }
            paintSkin(g, dx, dy, getWidth(state), getHeight(state), state);
        }

        /**
         * Paint a skin in an area defined by a rectangle.
         *
         * @param g     the graphics context to use for painting
         * @param r     a <code>Rectangle</code> defining the area to fill, may cause the image to be stretched or
         *              tiled
         * @param state which state to paint
         */
        public void paintSkin(Graphics g, Rectangle r, State state) {
            paintSkin(g, r.x, r.y, r.width, r.height, state);
        }

        /**
         * Paint a skin at a defined position and size This method supports animation.
         *
         * @param g     the graphics context to use for painting
         * @param dx    the destination <i>x</i> coordinate
         * @param dy    the destination <i>y</i> coordinate
         * @param dw    the width of the area to fill, may cause the image to be stretched or tiled
         * @param dh    the height of the area to fill, may cause the image to be stretched or tiled
         * @param state which state to paint
         */
        public void paintSkin(Graphics g, int dx, int dy, int dw, int dh, State state) {
            paintSkinRaw(g, dx, dy, dw, dh, state);
        }

        /**
         * Paint a skin at a defined position and size. This method does not trigger animation. It is needed for the
         * animation support.
         *
         * @param g     the graphics context to use for painting
         * @param dx    the destination <i>x</i> coordinate.
         * @param dy    the destination <i>y</i> coordinate.
         * @param dw    the width of the area to fill, may cause the image to be stretched or tiled
         * @param dh    the height of the area to fill, may cause the image to be stretched or tiled
         * @param state which state to paint
         */
        public void paintSkinRaw(Graphics g, int dx, int dy, int dw, int dh, State state) {
            skinPainter.paint(null, g, dx, dy, dw, dh, this, state);
        }

        /**
         * Paint a skin at a defined position and size
         *
         * @param g          the graphics context to use for painting
         * @param dx         the destination <i>x</i> coordinate
         * @param dy         the destination <i>y</i> coordinate
         * @param dw         the width of the area to fill, may cause the image to be stretched or tiled
         * @param dh         the height of the area to fill, may cause the image to be stretched or tiled
         * @param state      which state to paint
         * @param borderFill should test if the component uses a border fill and skip painting if it is
         */
        public void paintSkin(Graphics g, int dx, int dy, int dw, int dh, State state,
                              boolean borderFill) {
            if (borderFill && "borderfill".equals(getTypeEnumName(component, part,
                    state, Prop.BGTYPE))) {
                return;
            }
            skinPainter.paint(null, g, dx, dy, dw, dh, this, state);
        }
    }

    private static class SkinPainter extends CachedPainter {
        SkinPainter() {
            super(30);
            flush();
        }

        public void flush() {
            super.flush();
        }

        protected void paintToImage(Component c, Image image, Graphics g,
                                    int w, int h, Object[] args) {
            // copied from JDK7 XPStyle. To make the code compilable under JDk6, we use RefectionUtils
            boolean accEnabled = false;
            Skin skin = (Skin) args[0];
            Part part = skin.part;
            State state = (State) args[1];
            if (state == null) {
                state = skin.state;
            }
            if (c == null) {
                c = skin.component;
            }
            BufferedImage bi = (BufferedImage) image;

            WritableRaster raster = bi.getRaster();
            DataBufferInt dbi = (DataBufferInt) raster.getDataBuffer();
            // Note that stealData() requires a markDirty() afterwards
            // since we modify the data in it.
            try {
                int[] data = (int[]) ReflectionUtils.callStatic(SunWritableRaster.class, "stealData", new Class[]{DataBufferInt.class, int.class}, new Object[]{dbi, 0});
                Graphics2D g2d = (Graphics2D) g;
                AffineTransform at = g2d.getTransform();
                Object result = ReflectionUtils.callStatic(ThemeReader.class, "paintBackground", new Class[]{int[].class, String.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class},
                        new Object[]{
                                data,
                                part.getControlName(c), part.getValue(),
                                State.getValue(part, state),
                                0, 0, w, h, w});

                if(result == null) {
                    int dpi = (int) (at.getScaleX() * 96);
                    ReflectionUtils.callStatic(ThemeReader.class, "paintBackground", new Class[]{int[].class, String.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class},
                            new Object[]{
                                    data,
                                    part.getControlName(c), part.getValue(),
                                    State.getValue(part, state),
                                    0, 0, w, h, w, dpi});
                }
                ReflectionUtils.callStatic(SunWritableRaster.class, "markDirty", new Class[]{DataBuffer.class}, new Object[]{dbi});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected Image createImage(Component c, int w, int h,
                                    GraphicsConfiguration config, Object[] args) {
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }
    }

    static class GlyphButton extends JButton {
        private Skin skin;

        public GlyphButton(Component parent, Part part) {
            XPStyle xp = getXP();
            skin = xp.getSkin(parent, part);
            setBorder(null);
            setContentAreaFilled(false);
            setMinimumSize(new Dimension(5, 5));
            setPreferredSize(new Dimension(16, 16));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        }

        public boolean isFocusTraversable() {
            return false;
        }

        protected State getState() {
            State state = State.NORMAL;
            if (!isEnabled()) {
                state = State.DISABLED;
            }
            else if (getModel().isPressed()) {
                state = State.PRESSED;
            }
            else if (getModel().isRollover()) {
                state = State.HOT;
            }
            return state;
        }

        public void paintComponent(Graphics g) {
            Dimension d = getSize();
            skin.paintSkin(g, 0, 0, d.width, d.height, getState());
        }

        public void setPart(Component parent, Part part) {
            XPStyle xp = getXP();
            skin = xp.getSkin(parent, part);
            revalidate();
            repaint();
        }

        protected void paintBorder(Graphics g) {
        }


    }

    // Private constructor
    private XPStyle() {
        flatMenus = getSysBoolean(Prop.FLATMENUS);

        colorMap = new HashMap<String, Color>();
        borderMap = new HashMap<String, Border>();
        // Note: All further access to the maps must be synchronized
    }


    private boolean getBoolean(Component c, Part part, State state, Prop prop) {
        return ThemeReader.getBoolean(part.getControlName(c), part.getValue(),
                State.getValue(part, state),
                prop.getValue());
    }


    static Dimension getPartSize(Part part, State state) {
        return ThemeReader.getPartSize(part.getControlName(null), part.getValue(),
                State.getValue(part, state));
    }

    private static boolean getSysBoolean(Prop prop) {
        // We can use any widget name here, I guess.
        return ThemeReader.getSysBoolean("window", prop.getValue());
    }
}
