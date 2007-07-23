/*
 * @(#)JideSwingUtilities.java
 *
 * Copyright 2002 JIDE Software. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.WindowsDesktopProperty;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.utils.SecurityUtils;
import com.jidesoft.utils.SystemInfo;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlException;
import java.util.*;

/**
 * A utilities class for Swing.
 */
public class JideSwingUtilities implements SwingConstants {
    /**
     * Whether or not text is drawn anti-aliased.  This is only used if
     * <code>AA_TEXT_DEFINED</code> is true.
     */
    private static final boolean AA_TEXT;

    /**
     * Whether or not the system property 'swing.aatext' is defined.
     */
    private static final boolean AA_TEXT_DEFINED;

    /**
     * Key used in client properties to indicate whether or not the component
     * should use aa text.
     */
    public static final Object AA_TEXT_PROPERTY_KEY =
            new StringBuffer("AATextPropertyKey");

    static {
        Object aa = SecurityUtils.getProperty("swing.aatext", "false");
        AA_TEXT_DEFINED = (aa != null);
        AA_TEXT = "true".equals(aa);
    }

    /**
     * Create a Panel around a component so that
     * component aligns to left.
     *
     * @param object
     * @return a Panel
     */
    public static JPanel createLeftPanel(Component object) {
        JPanel ret = new NullPanel(new BorderLayout());
        ret.setOpaque(false);
        ret.add(object, BorderLayout.BEFORE_LINE_BEGINS);
        return ret;
    }

    /**
     * Create a Panel around a component so that
     * component aligns to right.
     *
     * @param object
     * @return a Panel
     */
    public static JPanel createRightPanel(Component object) {
        JPanel ret = new NullPanel(new BorderLayout());
        ret.setOpaque(false);
        ret.add(object, BorderLayout.AFTER_LINE_ENDS);
        return ret;
    }

    /**
     * Create a Panel around a component so that
     * component aligns to top.
     *
     * @param object
     * @return a Panel
     */
    public static JPanel createTopPanel(Component object) {
        JPanel ret = new NullPanel(new BorderLayout());
        ret.setOpaque(false);
        ret.add(object, BorderLayout.BEFORE_FIRST_LINE);
        return ret;
    }

    /**
     * Create a Panel around a component so that
     * component aligns to buttom.
     *
     * @param object
     * @return a Panel
     */
    public static JPanel createBottomPanel(Component object) {
        JPanel ret = new NullPanel(new BorderLayout());
        ret.setOpaque(false);
        ret.add(object, BorderLayout.AFTER_LAST_LINE);
        return ret;
    }

    /**
     * Create a Panel around a component so that
     * component is right in the middle.
     *
     * @param object
     * @return a Panel
     */
    public static JPanel createCenterPanel(Component object) {
        JPanel ret = new NullPanel(new GridBagLayout());
        ret.setOpaque(false);
        ret.add(object, new GridBagConstraints());
        return ret;
    }

    /**
     * Center the component to it's parent window.
     */
    public static void centerWindow(Window childToCenter) {
        childToCenter.setLocationRelativeTo(childToCenter.getParent());
//        Container parentWindow = childToCenter.getParent();
//
//        int width = (parentWindow.getWidth() - childToCenter.getWidth()) >> 1;
//        int height = (parentWindow.getHeight() - childToCenter.getHeight()) >> 1;
//
//        // according to javadoc of setLocation, it's relavent to parent window. but it's not the case.
//        Point location = parentWindow.getLocation();
//        width += location.x;
//        height += location.y;
//        childToCenter.setLocation(width, height);
    }

    /**
     * Center the window to the whole screen.
     */
    public static void globalCenterWindow(Window childToCenter) {
        childToCenter.setLocationRelativeTo(null);
//        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
//
//        // Get the bounds of the splash window
//        Rectangle frameDim = childToCenter.getBounds();
//
//        // Compute the location of the window
//        childToCenter.setLocation((screenDim.width - frameDim.width) >> 1, (screenDim.height - frameDim.height) >> 1);
    }

    /**
     * Paints an arrow shape.
     *
     * @param g
     * @param color
     * @param startX
     * @param startY
     * @param width
     * @param orientation
     */
    public static void paintArrow(Graphics g, Color color, int startX, int startY, int width, int orientation) {
        Color oldColor = g.getColor();
        g.setColor(color);
        width = width / 2 * 2 + 1; // make sure it's odd
        if (orientation == HORIZONTAL) {
            for (int i = 0; i < (width + 1) / 2; i++) {
                g.drawLine(startX + i, startY + i, startX + width - i - 1, startY + i);
            }
        }
        else {
            for (int i = 0; i < (width + 1) / 2; i++) {
                g.drawLine(startX + i, startY + i, startX + i, startY + width - i - 1);
            }
        }
        g.setColor(oldColor);
    }

    /**
     * Paints a cross shape.
     *
     * @param g
     * @param color
     * @param centerX
     * @param centerY
     * @param size
     * @param width
     */
    public static void paintCross(Graphics g, Color color, int centerX, int centerY, int size, int width) {
        g.setColor(color);
        size = size / 2; // make sure it's odd
        for (int i = 0; i < width; i++) {
            g.drawLine(centerX - size, centerY - size, centerX + size, centerY + size);
            g.drawLine(centerX + size, centerY - size, centerX - size, centerY + size);
            centerX++;
        }
    }

    /**
     * Gets the top level Frame of the component.
     *
     * @param component
     * @return the top level Frame. Null if we didn't find an ancestor which is instance of Frame.
     */
    public static Frame getFrame(Component component) {
        if (component == null) return null;

        if (component instanceof Frame) return (Frame) component;

        // Find framel
        Container p = component.getParent();
        while (p != null) {
            if (p instanceof Frame) {
                return (Frame) p;
            }
            p = p.getParent();
        }
        return null;
    }


    /**
     * Toggles between RTL and LTR.
     *
     * @param topContainer
     */
    public static void toggleRTLnLTR(Component topContainer) {
        ComponentOrientation co = topContainer.getComponentOrientation();
        if (co == ComponentOrientation.RIGHT_TO_LEFT)
            co = ComponentOrientation.LEFT_TO_RIGHT;
        else
            co = ComponentOrientation.RIGHT_TO_LEFT;
        topContainer.applyComponentOrientation(co);
    }

    /**
     * @param view1
     * @param view2
     * @param orientation
     * @deprecated there is a typo. Use {@link #synchronizeView(javax.swing.JViewport,javax.swing.JViewport,int)}.
     */
    public static void synchonizeView(final JViewport view1, final JViewport view2, final int orientation) {
        synchronizeView(view1, view2, orientation);
    }

    /**
     * Synchonizes the two viewports. The view position in one view changes, the other view's view position will change too.
     * Generally speaking, if you want the two viewports to synchronize vertically, they should have the same height.
     * If horizonally, the same width.
     *
     * @param view1       the first viewport
     * @param view2       the second viewport
     * @param orientation the orientation. It could be either SwingConstants.HORIZONTAL or SwingConstants.VERTICAL.
     */
    public static void synchronizeView(final JViewport view1, final JViewport view2, final int orientation) {
        final ChangeListener c1 = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (orientation == HORIZONTAL) {
                    Point v1 = view1.getViewPosition();
                    Point v2 = view2.getViewPosition();
                    if (v1.x != v2.x) {
                        view2.setViewPosition(new Point(v1.x, v2.y));
                    }
                }
                else if (orientation == VERTICAL) {
                    Point v1 = view1.getViewPosition();
                    Point v2 = view2.getViewPosition();
                    if (v1.y != v2.y) {
                        view2.setViewPosition(new Point(v2.x, v1.y));
                    }
                }
            }
        };

        final ChangeListener c2 = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (orientation == HORIZONTAL) {
                    Point v1 = view1.getViewPosition();
                    Point v2 = view2.getViewPosition();
                    if (v1.x != v2.x) {
                        view1.setViewPosition(new Point(v2.x, v1.y));
                    }
                }
                else if (orientation == VERTICAL) {
                    Point v1 = view1.getViewPosition();
                    Point v2 = view2.getViewPosition();
                    if (v1.y != v2.y) {
                        view1.setViewPosition(new Point(v1.x, v2.y));
                    }
                }
            }
        };

        view1.addChangeListener(c1);
        view2.addChangeListener(c2);
    }

    public static int getButtonState(AbstractButton b) {
        ButtonModel model = b.getModel();
        if (!model.isEnabled()) {
            if (model.isSelected()) {
                return ThemePainter.STATE_DISABLE_SELECTED;
            }
            else {
                return ThemePainter.STATE_DISABLE;
            }
        }
        else if (b.hasFocus() && b.isFocusPainted()) {
            if (model.isSelected()) {
                return ThemePainter.STATE_PRESSED;
            }
            else {
                return ThemePainter.STATE_ROLLOVER;
            }
        }
        else if (model.isPressed() && model.isArmed()) {
            if (model.isRollover()) {
                return ThemePainter.STATE_PRESSED;
            }
        }
        else if (b.isRolloverEnabled() && model.isRollover()) {
            if (model.isSelected()) {
                return ThemePainter.STATE_PRESSED; // should be rollover selected
            }
            else {
                return ThemePainter.STATE_ROLLOVER;
            }
        }
        else if (model.isSelected()) {
            return ThemePainter.STATE_SELECTED;
        }
        return ThemePainter.STATE_DEFAULT;
    }

    public static int[] getButtonState(JideSplitButton b) {
        int[] states = new int[2];
        SplitButtonModel model = (SplitButtonModel) b.getModel();
        if (!model.isEnabled()) {
            if (model.isButtonSelected()) {
                states[0] = ThemePainter.STATE_DISABLE_SELECTED;
            }
            else {
                states[0] = ThemePainter.STATE_DISABLE;
            }
        }
        else if (b.hasFocus() && b.isFocusPainted()) {
            if (model.isButtonSelected()) {
                states[0] = ThemePainter.STATE_SELECTED;
                states[1] = ThemePainter.STATE_INACTIVE_ROLLOVER;
            }
            else if (model.isSelected()) {
                states[0] = ThemePainter.STATE_INACTIVE_ROLLOVER;
                states[1] = ThemePainter.STATE_SELECTED;
            }
            else {
                states[0] = ThemePainter.STATE_ROLLOVER;
                states[1] = ThemePainter.STATE_INACTIVE_ROLLOVER;
            }
        }
        else if (model.isPressed() && model.isArmed()) {
            if (model.isButtonRollover()) {
                states[0] = ThemePainter.STATE_PRESSED;
                states[1] = ThemePainter.STATE_INACTIVE_ROLLOVER;
            }
            else if (model.isRollover()) {
                states[0] = ThemePainter.STATE_INACTIVE_ROLLOVER;
                states[1] = ThemePainter.STATE_ROLLOVER;
            }
        }
        else if (b.isRolloverEnabled() && model.isButtonRollover()) {
            if (model.isButtonSelected()) {
                states[0] = ThemePainter.STATE_PRESSED;
                states[1] = ThemePainter.STATE_INACTIVE_ROLLOVER;
            }
            else if (model.isSelected()) {
                states[0] = ThemePainter.STATE_ROLLOVER;
                states[1] = ThemePainter.STATE_PRESSED;
            }
            else {
                states[0] = ThemePainter.STATE_ROLLOVER;
                states[1] = ThemePainter.STATE_INACTIVE_ROLLOVER;
            }
        }
        else if (b.isRolloverEnabled() && model.isRollover()) {
            if (model.isButtonSelected()) {
                states[0] = ThemePainter.STATE_PRESSED;
                states[1] = ThemePainter.STATE_ROLLOVER;
            }
            else if (model.isSelected()) {
                states[0] = ThemePainter.STATE_INACTIVE_ROLLOVER;
                states[1] = ThemePainter.STATE_PRESSED;
            }
            else {
                states[0] = ThemePainter.STATE_INACTIVE_ROLLOVER;
                states[1] = ThemePainter.STATE_ROLLOVER;
            }
        }
        else if (model.isButtonSelected()) {
            states[0] = ThemePainter.STATE_SELECTED;
            states[1] = ThemePainter.STATE_INACTIVE_ROLLOVER;
        }
        else if (model.isSelected()) {
            states[0] = ThemePainter.STATE_INACTIVE_ROLLOVER;
            states[1] = ThemePainter.STATE_SELECTED;
        }
        else {
            states[0] = ThemePainter.STATE_DEFAULT;
            states[1] = ThemePainter.STATE_DEFAULT;
        }
        return states;
    }

    /**
     * Checks if the two objects equal. If both are null, they are equal. If o1 and o2 both are Comparable, we will
     * use compareTo method to see if it equals 0.
     * At last, we will use <code>o1.equals(o2)</code> to compare.
     * If none of the above conditions match, we return false.
     *
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return true if the two objects are equal. Otherwise false.
     */
    public static boolean equals(Object o1, Object o2) {
        return equals(o1, o2, false);
    }

    /**
     * Checks if the two objects equal. If both are null, they are equal. If o1 and o2 both are Comparable, we will
     * use compareTo method to see if it equals 0. If considerArray is true and o1 and o2 are both array, we will compare each element in the array.
     * At last, we will use <code>o1.equals(o2)</code> to compare.
     * If none of the above conditions match, we return false.
     *
     * @param o1            the first object to compare
     * @param o2            the second object to compare
     * @param considerArray If true, and if o1 and o2 are both array, we will compare each element in the array instead of just compare the two array objects.
     * @return true if the two objects are equal. Otherwise false.
     */
    public static boolean equals(Object o1, Object o2, boolean considerArray) {
        if (o1 == null && o2 == null) {
            return true;
        }
        else if (o1 != null && o2 == null) {
            return false;
        }
        else if (o1 == null) {
            return false;
        }
        else
        if (o1 instanceof Comparable && o2 instanceof Comparable && o1.getClass().isAssignableFrom(o2.getClass())) {
            return ((Comparable) o1).compareTo(o2) == 0;
        }
        else
        if (o1 instanceof Comparable && o2 instanceof Comparable && o2.getClass().isAssignableFrom(o1.getClass())) {
            return ((Comparable) o2).compareTo(o1) == 0;
        }
        else {
            if (considerArray && o1.getClass().isArray() && o2.getClass().isArray()) {
                int length1 = Array.getLength(o1);
                int length2 = Array.getLength(o2);
                if (length1 != length2) {
                    return false;
                }
                for (int i = 0; i < length1; i++) {
                    boolean equals = equals(Array.get(o1, i), Array.get(o1, i));
                    if (!equals) {
                        return false;
                    }
                }
                return true;
            }
            else {
                return o1.equals(o2);
            }
        }
    }

    private static class GetPropertyAction
            implements java.security.PrivilegedAction {
        private String theProp;
        private String defaultVal;

        /**
         * Constructor that takes the name of the system property whose
         * string value needs to be determined.
         *
         * @param theProp the name of the system property.
         */
        public GetPropertyAction(String theProp) {
            this.theProp = theProp;
        }

        /**
         * Constructor that takes the name of the system property and the default
         * value of that property.
         *
         * @param theProp    the name of the system property.
         * @param defaultVal the default value.
         */
        public GetPropertyAction(String theProp, String defaultVal) {
            this.theProp = theProp;
            this.defaultVal = defaultVal;
        }

        /**
         * Determines the string value of the system property whose
         * name was specified in the constructor.
         *
         * @return the string value of the system property,
         *         or the default value if there is no property with that key.
         */
        public Object run() {
            String value = System.getProperty(theProp);
            return (value == null) ? defaultVal : value;
        }
    }

    /**
     * In JDK1.4, it uses a wrong font for Swing component in Windows L&F which
     * is actually one big reason for people to think Swing application ugly.
     * To address this issue, we changed the code to force to use Tahoma font
     * for all the fonts in L&F instead of using the system font.
     * <p/>
     * However this is a downside to this. Tahoma cannot display unicode characters
     * such as Chinese, Japanese and Korean. So if the locale is CJK ({@link SystemInfo#isCJKLocale()},
     * we shouldn't use Tahoma. If you are on JDK 1.5 and above, you shouldn't force to use Tahoma either
     * because JDK fixed it in 1.5 and above.
     * <p/>
     * There are also a few system properties you can set to control
     * if system font should be used. "swing.useSystemFontSettings"
     * is the one for all Swing applications. "Application.useSystemFontSettings" is the
     * one for a particular Swing application.
     * <p/>
     * This method considers all the cases above. If JDK is 1.5 and above, this method will return true.
     * If you are on Chinese, Japanese or Korean locale, it will return true. If "swing.useSystemFontSettings" property us true,
     * it will return true. If "Application.useSystemFontSettings" property is true, it will return true. Otherwise,
     * it will return false. All JIDE L&F considered the returned value and decide if Tahoma font should be used or not.
     *
     * @return true if the L&F should use system font.
     */
    public static boolean shouldUseSystemFont() {
        if (SystemInfo.isJdk15Above() || SystemInfo.isCJKLocale()) {
            return true;
        }

        String systemFonts = null;
        try {
            systemFonts = (String) java.security.AccessController.doPrivileged(new GetPropertyAction("swing.useSystemFontSettings"));
        }
        catch (AccessControlException e) {
            // ignore
        }

        boolean useSystemFontSettings = (systemFonts != null &&
                Boolean.valueOf(systemFonts));

        if (useSystemFontSettings) {
            Object value = UIDefaultsLookup.get("Application.useSystemFontSettings");

            useSystemFontSettings = (value != null ||
                    Boolean.TRUE.equals(value));
        }

        return "true".equals(SecurityUtils.getProperty("defaultFont", "false")) || useSystemFontSettings;
    }

    public static void printUIDefaults() {
        Enumeration e = UIManager.getDefaults().keys();
        java.util.List list = new ArrayList();

        System.out.println("Non-string keys ---");
        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            if (key instanceof String) {
                list.add(key);
            }
            else {
                System.out.println(key + " => " + UIDefaultsLookup.get(key));
            }
        }

        System.out.println();

        Object[] array = list.toArray(new Object[list.size()]);
        Arrays.sort(array);
        System.out.println("String keys ---");
        for (int i = 0; i < array.length; i++) {
            Object key = array[i];
            System.out.println(key + " => " + UIDefaultsLookup.get(key));
        }
    }

    /**
     * A simple handler used by setRecursively.
     * <pre>
     *  if ( condition() ) {
     *      action();
     *  }
     *  postAction();
     * </pre>.
     */
    public interface Handler {
        boolean condition(Component c);

        void action(Component c);

        void postAction(Component c);

    }

    /**
     * A simple handler used by getRecursively.
     * <code><pre>
     *  if ( condition() ) {
     *      return action();
     *  }
     * </pre></code>.
     */
    public interface GetHandler {
        boolean condition(Component c);

        Component action(Component c);
    }

    /**
     * Calls the handler recursively on a component.
     *
     * @param c       component
     * @param handler handler to be called
     */
    public static void setRecursively(final Component c, final Handler handler) {
        setRecursively0(c, handler);
        handler.postAction(c);
    }

    private static void setRecursively0(final Component c, final Handler handler) {
        if (handler.condition(c)) {
            handler.action(c);
        }
        Component[] children = null;

        if (c instanceof JMenu) {
            children = ((JMenu) c).getMenuComponents();
        }
        else if (c instanceof JTabbedPane) {
            JTabbedPane tabbedPane = (JTabbedPane) c;
            children = new Component[tabbedPane.getTabCount()];
            for (int i = 0; i < children.length; i++) {
                children[i] = tabbedPane.getComponentAt(i);
            }
        }
        else if (c instanceof Container) {
            children = ((Container) c).getComponents();
        }
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                setRecursively0(children[i], handler);
            }
        }
    }

    /**
     * Gets to a child of a component recursively based on certain condition.
     *
     * @param c       component
     * @param handler handler to be called
     */
    public static Component getRecursively(final Component c, final GetHandler handler) {
        return getRecursively0(c, handler);
    }

    private static Component getRecursively0(final Component c, final GetHandler handler) {
        if (handler.condition(c)) {
            return handler.action(c);
        }

        Component[] children = null;

        if (c instanceof JMenu) {
            children = ((JMenu) c).getMenuComponents();
        }
        else if (c instanceof Container) {
            children = ((Container) c).getComponents();
        }

        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                Component result = getRecursively0(children[i], handler);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Calls setEnabled method recursively on component. <code>Component</code> c is usually a <code>Container</code>
     *
     * @param c       component
     * @param enabled true if enable; false otherwise
     */
    public static void setEnabledRecursively(final Component c, final boolean enabled) {
        setRecursively(c, new Handler() {
            public boolean condition(Component c) {
                return true;
            }

            public void action(Component c) {
                c.setEnabled(enabled);
            }

            public void postAction(Component c) {
            }
        });
    }

    /**
     * Calls setRequestFocusEnabled method recursively on component. <code>Component</code> c is usually a <code>Container</code>
     *
     * @param c       component
     * @param enabled true if setRequestFocusEnabled to true; false otherwise
     */
    public static void setRequestFocusEnabledRecursively(final Component c, final boolean enabled) {
        setRecursively(c, new Handler() {
            public boolean condition(Component c) {
                return true;
            }

            public void action(Component c) {
                if (c instanceof JComponent)
                    ((JComponent) c).setRequestFocusEnabled(enabled);
            }

            public void postAction(Component c) {
            }
        });
    }

    private static PropertyChangeListener _setOpaqueTrueListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() instanceof JComponent) {
                ((JComponent) evt.getSource()).setOpaque(true);
            }
        }
    };

    private static PropertyChangeListener _setOpaqueFalseListener;
    private static final String OPAQUE_LISTENER = "setOpaqueRecursively.opaqueListener";

    /**
     * setOpaqueRecursively method will make all child components opaque true or false. But if you call
     * jcomponent.putClientProperty(SET_OPAQUE_RECURSIVELY_EXCLUDED, Boolean.TRUE), we will not touch
     * this particular component when setOpaqueRecursively.
     */
    public static final String SET_OPAQUE_RECURSIVELY_EXCLUDED = "setOpaqueRecursively.excluded";

    /**
     * Calls setOpaque method recursively on each component except
     * for JButton, JComboBox and JTextComponent.
     * <code>Component</code> c is usually a <code>Container</code>.
     * If you would like certain child component not affected by this call, you can
     * call jcomponent.putClientProperty(SET_OPAQUE_RECURSIVELY_EXCLUDED, Boolean.TRUE) before calling this method.
     *
     * @param c      component
     * @param opaque true if setOpaque to true; false otherwise
     */
    public static void setOpaqueRecursively(final Component c, final boolean opaque) {
        setRecursively(c, new Handler() {
            public boolean condition(Component c) {
                return !(c instanceof JComboBox || c instanceof JButton || c instanceof JTextComponent);
            }

            public void action(Component c) {
                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    if (Boolean.TRUE.equals(jc.getClientProperty(SET_OPAQUE_RECURSIVELY_EXCLUDED))) {
                        return;
                    }

                    jc.setOpaque(opaque);
                    if (jc.getClientProperty(OPAQUE_LISTENER) == null) {
                        if (opaque) {
                            if (_setOpaqueTrueListener == null) {
                                _setOpaqueTrueListener = new PropertyChangeListener() {
                                    public void propertyChange(PropertyChangeEvent evt) {
                                        if (evt.getSource() instanceof JComponent) {
                                            ((JComponent) evt.getSource()).setOpaque(true);
                                        }
                                    }
                                };
                            }
                            jc.addPropertyChangeListener("opaque", _setOpaqueTrueListener);
                            jc.putClientProperty("opaqueListener", _setOpaqueTrueListener);
                        }
                        else {
                            if (_setOpaqueFalseListener == null) {
                                _setOpaqueFalseListener = new PropertyChangeListener() {
                                    public void propertyChange(PropertyChangeEvent evt) {
                                        if (evt.getSource() instanceof JComponent) {
                                            ((JComponent) evt.getSource()).setOpaque(false);
                                        }
                                    }
                                };
                            }
                            jc.addPropertyChangeListener("opaque", _setOpaqueFalseListener);
                            jc.putClientProperty(OPAQUE_LISTENER, _setOpaqueFalseListener);
                        }
                    }
                }
            }

            public void postAction(Component c) {
            }
        });
    }

    public static Dimension getPreferredButtonSize(AbstractButton b, int textIconGap, boolean isHorizontal) {
        if (b.getComponentCount() > 0) {
            return null;
        }

        Icon icon = (Icon) b.getIcon();
        String text = b.getText();

        Font font = b.getFont();
        FontMetrics fm = b.getFontMetrics(font);

        Rectangle iconR = new Rectangle();
        Rectangle textR = new Rectangle();
        Rectangle viewR = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);

        layoutCompoundLabel((JComponent) b, fm, text, icon, isHorizontal,
                b.getVerticalAlignment(), b.getHorizontalAlignment(),
                b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
                viewR, iconR, textR, (text == null ? 0 : textIconGap));

        /* The preferred size of the button is the size of
         * the text and icon rectangles plus the buttons insets.
         */

        Rectangle r = iconR.union(textR);

        Insets insets = b.getInsets();
        r.width += insets.left + insets.right;
        r.height += insets.top + insets.bottom;

        return r.getSize();
    }

    /**
     * Compute and return the location of the icons origin, the
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewR rectangle.
     * The JComponents orientation (LEADING/TRAILING) will also be taken
     * into account and translated into LEFT/RIGHT values accordingly.
     */
    public static String layoutCompoundLabel(JComponent c,
                                             FontMetrics fm,
                                             String text,
                                             Icon icon,
                                             boolean isHorizontal,
                                             int verticalAlignment,
                                             int horizontalAlignment,
                                             int verticalTextPosition,
                                             int horizontalTextPosition,
                                             Rectangle viewR,
                                             Rectangle iconR,
                                             Rectangle textR,
                                             int textIconGap) {
        boolean orientationIsLeftToRight = true;
        int hAlign = horizontalAlignment;
        int hTextPos = horizontalTextPosition;

        if (c != null) {
            if (!(c.getComponentOrientation().isLeftToRight())) {
                orientationIsLeftToRight = false;
            }
        }

        // Translate LEADING/TRAILING values in horizontalAlignment
        // to LEFT/RIGHT values depending on the components orientation
        switch (horizontalAlignment) {
            case LEADING:
                hAlign = (orientationIsLeftToRight) ? LEFT : RIGHT;
                break;
            case TRAILING:
                hAlign = (orientationIsLeftToRight) ? RIGHT : LEFT;
                break;
        }

        // Translate LEADING/TRAILING values in horizontalTextPosition
        // to LEFT/RIGHT values depending on the components orientation
        switch (horizontalTextPosition) {
            case LEADING:
                hTextPos = (orientationIsLeftToRight) ? LEFT : RIGHT;
                break;
            case TRAILING:
                hTextPos = (orientationIsLeftToRight) ? RIGHT : LEFT;
                break;
        }

        return layoutCompoundLabelImpl(c,
                fm,
                text,
                icon,
                isHorizontal,
                verticalAlignment,
                hAlign,
                verticalTextPosition,
                hTextPos,
                viewR,
                iconR,
                textR,
                textIconGap);
    }

    /**
     * Compute and return the location of the icons origin, the
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewR rectangle.
     * This layoutCompoundLabel() does not know how to handle LEADING/TRAILING
     * values in horizontalTextPosition (they will default to RIGHT) and in
     * horizontalAlignment (they will default to CENTER).
     * Use the other version of layoutCompoundLabel() instead.
     */
    public static String layoutCompoundLabel(FontMetrics fm,
                                             String text,
                                             Icon icon,
                                             boolean isHorizontal,
                                             int verticalAlignment,
                                             int horizontalAlignment,
                                             int verticalTextPosition,
                                             int horizontalTextPosition,
                                             Rectangle viewR,
                                             Rectangle iconR,
                                             Rectangle textR,
                                             int textIconGap) {
        return layoutCompoundLabelImpl(null, fm, text, icon,
                isHorizontal,
                verticalAlignment,
                horizontalAlignment,
                verticalTextPosition,
                horizontalTextPosition,
                viewR, iconR, textR, textIconGap);
    }

    /**
     * Compute and return the location of the icons origin, the
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewR rectangle.
     * This layoutCompoundLabel() does not know how to handle LEADING/TRAILING
     * values in horizontalTextPosition (they will default to RIGHT) and in
     * horizontalAlignment (they will default to CENTER).
     * Use the other version of layoutCompoundLabel() instead.
     */
    private static String layoutCompoundLabelImpl(JComponent c,
                                                  FontMetrics fm,
                                                  String text,
                                                  Icon icon,
                                                  boolean isHorizontal,
                                                  int verticalAlignment,
                                                  int horizontalAlignment,
                                                  int verticalTextPosition,
                                                  int horizontalTextPosition,
                                                  Rectangle viewR,
                                                  Rectangle iconR,
                                                  Rectangle textR,
                                                  int textIconGap) {
        /* Initialize the icon bounds rectangle iconR.
         */
        if (isHorizontal)
            return layoutCompoundLabelImplHorizontal(c,
                    fm,
                    text,
                    icon,
                    verticalAlignment,
                    horizontalAlignment,
                    verticalTextPosition,
                    horizontalTextPosition,
                    viewR,
                    iconR,
                    textR,
                    textIconGap);
        else
            return layoutCompoundLabelImplVertical(c,
                    fm,
                    text,
                    icon,
                    verticalAlignment,
                    horizontalAlignment,
                    verticalTextPosition,
                    horizontalTextPosition,
                    viewR,
                    iconR,
                    textR,
                    textIconGap);

    }


    private static String getMaxLengthWord(String text) {
        if (text.indexOf(' ') == -1) {
            return text;
        }
        else {
            int minDiff = text.length();
            int minPos = -1;
            int mid = text.length() / 2;

            int pos = -1;
            while (true) {
                pos = text.indexOf(' ', pos + 1);
                if (pos == -1) {
                    break;
                }
                int diff = Math.abs(pos - mid);
                if (diff < minDiff) {
                    minDiff = diff;
                    minPos = pos;
                }
            }
            return minPos >= mid ? text.substring(0, minPos) : text.substring(minPos + 1);
        }
    }

    private static String layoutCompoundLabelImplHorizontal(JComponent c,
                                                            FontMetrics fm,
                                                            String text,
                                                            Icon icon,
                                                            int verticalAlignment,
                                                            int horizontalAlignment,
                                                            int verticalTextPosition,
                                                            int horizontalTextPosition,
                                                            Rectangle viewR,
                                                            Rectangle iconR,
                                                            Rectangle textR,
                                                            int textIconGap) {
        /* Initialize the icon bounds rectangle iconR.
         */

        if (icon != null) {
            iconR.width = icon.getIconWidth();
            iconR.height = icon.getIconHeight();
        }
        else {
            iconR.width = iconR.height = 0;
        }

        /* Initialize the text bounds rectangle textR.  If a null
         * or and empty String was specified we substitute "" here
         * and use 0,0,0,0 for textR.
         */

        boolean textIsEmpty = (text == null) || text.equals("");

        View v = null;
        if (textIsEmpty) {
            textR.width = textR.height = 0;
            text = "";
        }
        else {
            v = (c != null) ? (View) c.getClientProperty("html") : null;
            if (v != null) {
                textR.width = (int) v.getPreferredSpan(View.X_AXIS);
                textR.height = (int) v.getPreferredSpan(View.Y_AXIS);
            }
            else {
                if (false) { // TODO: debug switch
                    boolean wrapText = false;
                    if (verticalTextPosition == BOTTOM && horizontalTextPosition == CENTER) { // in this case, we will wrap the text into two lines
                        wrapText = true;
                    }

                    if (wrapText) {
                        textR.width = SwingUtilities.computeStringWidth(fm, getMaxLengthWord(text));
                        textR.height = fm.getHeight() + fm.getAscent() + 2; // gap between the two lines is 2.
                    }
                    else {
                        textR.width = SwingUtilities.computeStringWidth(fm, text) + 1; // add an extra pixel at the end of the text
                        textR.height = fm.getHeight();
                    }
                }
                else {
                    textR.width = SwingUtilities.computeStringWidth(fm, text); // add an extra pixel at the end of the text
                    textR.height = fm.getHeight();
                }
            }
        }

        /* Unless both text and icon are non-null, we effectively ignore
         * the value of textIconGap.  The code that follows uses the
         * value of gap instead of textIconGap.
         */

        int gap = (textIsEmpty || (icon == null)) ? 0 : textIconGap;

        if (!textIsEmpty) {

            /* If the label text string is too wide to fit within the available
             * space "..." and as many characters as will fit will be
             * displayed instead.
             */

            int availTextWidth;

            if (horizontalTextPosition == CENTER) {
                availTextWidth = viewR.width;
            }
            else {
                availTextWidth = viewR.width - (iconR.width + gap);
            }


            if (textR.width > availTextWidth) {
                if (v != null) {
                    textR.width = availTextWidth;
                }
                else {
                    String clipString = "...";
                    int totalWidth = SwingUtilities.computeStringWidth(fm, clipString);
                    int nChars;
                    for (nChars = 0; nChars < text.length(); nChars++) {
                        totalWidth += fm.charWidth(text.charAt(nChars));
                        if (totalWidth > availTextWidth) {
                            break;
                        }
                    }
                    text = text.substring(0, nChars) + clipString;
                    textR.width = SwingUtilities.computeStringWidth(fm, text);
                }
            }
        }

        /* Compute textR.x,y given the verticalTextPosition and
        * horizontalTextPosition properties
        */

        if (verticalTextPosition == TOP) {
            if (horizontalTextPosition != CENTER) {
                textR.y = 0;
            }
            else {
                textR.y = -(textR.height + gap);
            }
        }
        else if (verticalTextPosition == CENTER) {
            textR.y = (iconR.height >> 1) - (textR.height >> 1);
        }
        else { // (verticalTextPosition == BOTTOM)
            if (horizontalTextPosition != CENTER) {
                textR.y = iconR.height - textR.height;
            }
            else {
                textR.y = (iconR.height + gap);
            }
        }

        if (horizontalTextPosition == LEFT) {
            textR.x = -(textR.width + gap);
        }
        else if (horizontalTextPosition == CENTER) {
            textR.x = (iconR.width >> 1) - (textR.width >> 1);
        }
        else { // (horizontalTextPosition == RIGHT)
            textR.x = (iconR.width + gap);
        }

        /* labelR is the rectangle that contains iconR and textR.
         * Move it to its proper position given the labelAlignment
         * properties.
         *
         * To avoid actually allocating a Rectangle, Rectangle.union
         * has been inlined below.
         */
        int labelR_x = Math.min(iconR.x, textR.x);
        int labelR_width = Math.max(iconR.x + iconR.width,
                textR.x + textR.width) - labelR_x;
        int labelR_y = Math.min(iconR.y, textR.y);
        int labelR_height = Math.max(iconR.y + iconR.height,
                textR.y + textR.height) - labelR_y;

        int dx, dy;

        if (verticalAlignment == TOP) {
            dy = viewR.y - labelR_y;
        }
        else if (verticalAlignment == CENTER) {
            dy = (viewR.y + (viewR.height >> 1)) - (labelR_y + (labelR_height >> 1));
        }
        else { // (verticalAlignment == BOTTOM)
            dy = (viewR.y + viewR.height) - (labelR_y + labelR_height);
        }

        if (horizontalAlignment == LEFT) {
            dx = viewR.x - labelR_x;
        }
        else if (horizontalAlignment == RIGHT) {
            dx = (viewR.x + viewR.width) - (labelR_x + labelR_width);
        }
        else { // (horizontalAlignment == CENTER)
            dx = (viewR.x + (viewR.width >> 1)) -
                    (labelR_x + (labelR_width >> 1));
        }

        /* Translate textR and glypyR by dx,dy.
         */

        textR.x += dx;
        textR.y += dy;

        iconR.x += dx;
        iconR.y += dy;

        return text;
    }

    private static String layoutCompoundLabelImplVertical(JComponent c,
                                                          FontMetrics fm,
                                                          String text,
                                                          Icon icon,
                                                          int verticalAlignment,
                                                          int horizontalAlignment,
                                                          int verticalTextPosition,
                                                          int horizontalTextPosition,
                                                          Rectangle viewR,
                                                          Rectangle iconR,
                                                          Rectangle textR,
                                                          int textIconGap) {
        /* Initialize the icon bounds rectangle iconR.
         */

        if (icon != null) {
            iconR.width = icon.getIconWidth();
            iconR.height = icon.getIconHeight();
        }
        else {
            iconR.width = iconR.height = 0;
        }

        /* Initialize the text bounds rectangle textR.  If a null
         * or and empty String was specified we substitute "" here
         * and use 0,0,0,0 for textR.
         */

        boolean textIsEmpty = (text == null) || text.equals("");

        View v = null;
        if (textIsEmpty) {
            textR.width = textR.height = 0;
            text = "";
        }
        else {
            v = (c != null) ? (View) c.getClientProperty("html") : null;
            if (v != null) {
                textR.height = (int) v.getPreferredSpan(View.X_AXIS);
                textR.width = (int) v.getPreferredSpan(View.Y_AXIS);
            }
            else {
                textR.height = SwingUtilities.computeStringWidth(fm, text);
                textR.width = fm.getHeight();
            }
        }

        /* Unless both text and icon are non-null, we effectively ignore
         * the value of textIconGap.  The code that follows uses the
         * value of gap instead of textIconGap.
         */

        int gap = (textIsEmpty || (icon == null)) ? 0 : textIconGap;

        if (!textIsEmpty) {

            /* If the label text string is too wide to fit within the available
             * space "..." and as many characters as will fit will be
             * displayed instead.
             */

            int availTextHeight;

            if (horizontalTextPosition == CENTER) {
                availTextHeight = viewR.height;
            }
            else {
                availTextHeight = viewR.height - (iconR.height + gap);
            }


            if (textR.height > availTextHeight) {
                if (v != null) {
                    textR.height = availTextHeight;
                }
                else {
                    String clipString = "...";
                    int totalHeight = SwingUtilities.computeStringWidth(fm, clipString);
                    int nChars;
                    for (nChars = 0; nChars < text.length(); nChars++) {
                        totalHeight += fm.charWidth(text.charAt(nChars));
                        if (totalHeight > availTextHeight) {
                            break;
                        }
                    }
                    text = text.substring(0, nChars) + clipString;
                    textR.height = SwingUtilities.computeStringWidth(fm, text);
                }
            }
        }

        /* Compute textR.x,y given the verticalTextPosition and
        * horizontalTextPosition properties
        */

        if (verticalTextPosition == TOP) {
            if (horizontalTextPosition != CENTER) {
                textR.x = 0;
            }
            else {
                textR.x = -(textR.width + gap);
            }
        }
        else if (verticalTextPosition == CENTER) {
            textR.y = (iconR.width >> 1) - (textR.width >> 1);
        }
        else { // (verticalTextPosition == BOTTOM)
            if (horizontalTextPosition != CENTER) {
                textR.x = iconR.width - textR.width;
            }
            else {
                textR.x = (iconR.width + gap);
            }
        }

        if (horizontalTextPosition == LEFT) {
            textR.y = -(textR.height + gap);
        }
        else if (horizontalTextPosition == CENTER) {
            textR.y = (iconR.height >> 1) - (textR.height >> 1);
        }
        else { // (horizontalTextPosition == RIGHT)
            textR.y = (iconR.height + gap);
        }

        /* labelR is the rectangle that contains iconR and textR.
         * Move it to its proper position given the labelAlignment
         * properties.
         *
         * To avoid actually allocating a Rectangle, Rectangle.union
         * has been inlined below.
         */
        int labelR_x = Math.min(iconR.y, textR.y);
        int labelR_width = Math.max(iconR.y + iconR.height,
                textR.y + textR.height) - labelR_x;
        int labelR_y = Math.min(iconR.x, textR.x);
        int labelR_height = Math.max(iconR.x + iconR.width,
                textR.x + textR.width) - labelR_y;

        int dx, dy;
        int dIcony; // because we will retate icon, so the position will
        // be different from text. However after transform, they will be same

        if (verticalAlignment == TOP) {
            dy = viewR.x - labelR_y;
            dIcony = (viewR.x + viewR.width) - (labelR_y + labelR_height);
        }
        else if (verticalAlignment == CENTER) {
            dy = (viewR.x + (viewR.width >> 1)) - (labelR_y + (labelR_height >> 1));
            dIcony = dy;
        }
        else { // (verticalAlignment == BOTTOM)
            dy = (viewR.x + viewR.width) - (labelR_y + labelR_height);
            dIcony = viewR.x - labelR_y;
        }

        if (horizontalAlignment == LEFT) {
            dx = viewR.y - labelR_x;
        }
        else if (horizontalAlignment == RIGHT) {
            dx = (viewR.y + viewR.height) - (labelR_x + labelR_width);
        }
        else { // (horizontalAlignment == CENTER)
            dx = (viewR.y + (viewR.height >> 1)) -
                    (labelR_x + (labelR_width >> 1));
        }

        /* Translate textR and iconR by dx,dy.
         */

        textR.y += dx;
        textR.x += dy;

        iconR.y += dx;
        iconR.x += dIcony;

        return text;
    }

    public static int getOrientationOf(Component component) {
        if (component instanceof Alignable) {
            return ((Alignable) component).getOrientation();
        }
        else if (component instanceof JComponent) {
            Integer value = (Integer) ((JComponent) component).getClientProperty(Alignable.PROPERTY_ORIENTATION);
            if (value != null)
                return value;
        }
        return HORIZONTAL;
    }

    public static void setOrientationOf(Component component, int orientation) {
        int old = getOrientationOf(component);
        if (orientation != old) {
            if (component instanceof Alignable) {
                ((Alignable) component).setOrientation(orientation);
            }
            else if (component instanceof JComponent) {
                ((JComponent) component).putClientProperty(Alignable.PROPERTY_ORIENTATION, orientation);
            }
        }
    }

    public static void setChildrenOrientationOf(Container c, int orientation) {
        Component[] components = c.getComponents();
        for (int i = 0; i < components.length; ++i) {
            Component component = components[i];
            setOrientationOf(component, orientation);
        }
    }

    public static void paintBackground(Graphics g, Rectangle rect, Color border, Color bk) {
        Color old = g.getColor();
        g.setColor(bk);
        g.fillRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
        g.setColor(border);
        g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
        g.setColor(old);
    }

    public static void paintBackground(Graphics2D g2d, Rectangle rect, Color border, Paint paint) {
        Color old = g2d.getColor();
        g2d.setPaint(paint);
        g2d.fillRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
        g2d.setColor(border);
        g2d.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
        g2d.setColor(old);
    }

    /**
     * Returns whether or not text should be drawn antialiased.
     *
     * @param c JComponent to test.
     * @return Whether or not text should be drawn antialiased for the
     *         specified component.
     */
    private static boolean drawTextAntialiased(Component c) {
        if (!AA_TEXT_DEFINED) {
            if (c != null) {
                // Check if the component wants aa text
                if (c instanceof JComponent) {
                    Boolean aaProperty = (Boolean) ((JComponent) c).getClientProperty(AA_TEXT_PROPERTY_KEY);
                    return aaProperty != null ? aaProperty : false;
                }
                else {
                    return false;
                }
            }
            // No component, assume aa is off
            return false;
        }
        // 'swing.aatext' was defined, use its value.
        return AA_TEXT;
    }

    /**
     * Returns whether or not text should be drawn antialiased.
     *
     * @param aaText Whether or not aa text has been turned on for the
     *               component.
     * @return Whether or not text should be drawn antialiased.
     */
    public static boolean drawTextAntialiased(boolean aaText) {
        if (!AA_TEXT_DEFINED) {
            // 'swing.aatext' wasn't defined, use the components aa text value.
            return aaText;
        }
        // 'swing.aatext' was defined, use its value.
        return AA_TEXT;
    }

    public static void drawStringUnderlineCharAt(JComponent c, Graphics g, String text,
                                                 int underlinedIndex, int x, int y) {
        drawString(c, g, text, x, y);

        if (underlinedIndex >= 0 && underlinedIndex < text.length()) {
            FontMetrics fm = g.getFontMetrics();
            int underlineRectX = x + fm.stringWidth(text.substring(0, underlinedIndex));
            int underlineRectY = y;
            int underlineRectWidth = fm.charWidth(text.charAt(underlinedIndex));
            int underlineRectHeight = 1;
            g.fillRect(underlineRectX, underlineRectY + fm.getDescent() - 1,
                    underlineRectWidth, underlineRectHeight);
        }
    }

    static RenderingHints renderingHints = null;

    static {
        if (SystemInfo.isJdk6Above()) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            renderingHints = (RenderingHints) (tk.getDesktopProperty("awt.font.desktophints"));
            tk.addPropertyChangeListener("awt.font.desktophints", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getNewValue() instanceof RenderingHints) {
                        renderingHints = (RenderingHints) evt.getNewValue();
                    }
                }
            });
        }
    }

    /**
     * Get rendering hints from a Graphics instance.
     * "hintsToSave" is a Map of RenderingHint key-values.
     * For each hint key present in that map, the value of that
     * hint is obtained from the Graphics and stored as the value
     * for the key in savedHints.
     */
    private static RenderingHints getRenderingHints(Graphics2D g2d,
                                                    RenderingHints hintsToSave,
                                                    RenderingHints savedHints) {
        if (savedHints == null) {
            savedHints = new RenderingHints(null);
        }
        else {
            savedHints.clear();
        }
        if (hintsToSave.size() == 0) {
            return savedHints;
        }
        /* RenderingHints.keySet() returns Set*/
        Set objects = hintsToSave.keySet();
        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            RenderingHints.Key key = (RenderingHints.Key) o;
            Object value = g2d.getRenderingHint(key);
            savedHints.put(key, value);
        }

        return savedHints;
    }

    public static void drawString(JComponent c, Graphics g, String text, int x, int y) {
        if (SystemInfo.isJdk6Above()) {
            Graphics2D g2d = (Graphics2D) g;
            RenderingHints oldHints = null;
            if (renderingHints != null) {
                oldHints = getRenderingHints(g2d, renderingHints, null);
                g2d.addRenderingHints(renderingHints);
            }
            g2d.drawString(text, x, y);
            if (oldHints != null) {
                g2d.addRenderingHints(oldHints);
            }
        }
        else {
            // If we get here we're not printing
            if (drawTextAntialiased(c) && (g instanceof Graphics2D)) {
                Graphics2D g2 = (Graphics2D) g;
                Object oldAAValue = g2.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.drawString(text, x, y);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, oldAAValue);
            }
            else {
                g.drawString(text, x, y);
            }
        }
    }

    /**
     * Setups the graphics to draw text using anti-alias.
     * <p/>
     * Under JDK1.4 and JDK5, this method will use a system property "swing.aatext" to determine if anti-alias is used.
     * Under JDK6, we will read the system setting. For example, on Windows XP, there is a check box to turn on clear type anti-alias.
     * We will use the same settings.
     *
     * @param c
     * @param g
     * @return the old hints. You will need this value as the third parameter in {@link #restoreAntialiasing(java.awt.Component,java.awt.Graphics,Object)}.
     */
    public static Object setupAntialiasing(Component c, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Object oldHints = null;
        if (SystemInfo.isJdk6Above()) {
            oldHints = getRenderingHints(g2d, renderingHints, null);
            if (renderingHints != null) {
                g2d.addRenderingHints(renderingHints);
            }
        }
        else {
            oldHints = g2d.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            if (drawTextAntialiased(c)) {
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }
        }
        return oldHints;
    }

    /**
     * Restores the old setting for text anti-alias.
     *
     * @param c
     * @param g
     * @param oldHints the value returned from {@link #setupAntialiasing(java.awt.Component,java.awt.Graphics)}.
     */
    public static void restoreAntialiasing(Component c, Graphics g, Object oldHints) {
        Graphics2D g2d = (Graphics2D) g;
        if (SystemInfo.isJdk6Above()) {
            if (oldHints != null) {
                g2d.addRenderingHints((RenderingHints) oldHints);
            }
        }
        else {
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, oldHints);
        }
    }

    /**
     * Setups the graphics to draw shape using anti-alias.
     *
     * @param g
     * @return the old hints. You will need this value as the third parameter in {@link #restoreShapeAntialiasing(java.awt.Graphics,Object)}.
     */
    public static Object setupShapeAntialiasing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Object oldHints = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return oldHints;
    }

    /**
     * Restores the old setting for shape anti-alias.
     *
     * @param g
     * @param oldHints the value returned from {@link #setupShapeAntialiasing(java.awt.Graphics)}.
     */
    public static void restoreShapeAntialiasing(Graphics g, Object oldHints) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldHints);
    }

    public static void drawGrip(Graphics g, Rectangle rectangle, int maxLength, int maxThickness) {
        drawGrip(g, rectangle, maxLength, maxThickness, true);
    }

    public static void drawGrip(Graphics g, Rectangle rectangle, int maxLength, int maxThickness, boolean isSelected) {
        if (rectangle.width > rectangle.height) {
            int count = maxLength;
            if (maxLength * 3 > rectangle.width) {
                count = rectangle.width / 3;
            }
            int startX = rectangle.x + ((rectangle.width - (count * 3)) >> 1);
            int startY = rectangle.y + ((rectangle.height - (maxThickness * 3)) >> 1);
            for (int i = 0; i < maxThickness; i++) {
                for (int j = 0; j < count; j++) {
                    if (isSelected) {
                        g.setColor(UIDefaultsLookup.getColor("controlLtHighlight"));
                        g.drawLine(startX + j * 3, startY + i * 3, startX + j * 3, startY + i * 3);
                    }
                    g.setColor(UIDefaultsLookup.getColor("controlShadow"));
                    g.drawLine(startX + j * 3 + 1, startY + i * 3 + 1, startX + j * 3 + 1, startY + i * 3 + 1);
                }
            }
        }
        else {
            int count = maxLength;
            if (maxLength * 3 > rectangle.height) {
                count = rectangle.height / 3;
            }
            int startX = rectangle.x + ((rectangle.width - (maxThickness * 3)) >> 1);
            int startY = rectangle.y + ((rectangle.height - (count * 3)) >> 1);
            for (int i = 0; i < maxThickness; i++) {
                for (int j = 0; j < count; j++) {
                    if (isSelected) {
                        g.setColor(UIDefaultsLookup.getColor("controlLtHighlight"));
                        g.drawLine(startX + i * 3, startY + j * 3, startX + i * 3, startY + j * 3);
                    }
                    g.setColor(UIDefaultsLookup.getColor("controlShadow"));
                    g.drawLine(startX + i * 3 + 1, startY + j * 3 + 1, startX + i * 3 + 1, startY + j * 3 + 1);
                }
            }
        }
    }

    /**
     * Register the tab key with the container.
     *
     * @param container
     */
    public static void registerTabKey(Container container) {
        if (container instanceof JComponent) {
            ((JComponent) container).registerKeyboardAction(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    // JDK 1.3 Porting Hint
                    // comment out for now
                    DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), JComponent.WHEN_FOCUSED);
        }
        else {
            for (int i = 0; i < container.getComponentCount(); i++) {
                Component c = container.getComponent(i);
                // JDK 1.3 Porting Hint
                // change to isFocusTraversable()
                if (c instanceof JComponent && c.isFocusable()) {
                    ((JComponent) container).registerKeyboardAction(new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                            // JDK 1.3 Porting Hint
                            // comment out for now
                            DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
                        }
                    }, KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), JComponent.WHEN_FOCUSED);
                }
            }
        }
    }

    public static void fillGradient(Graphics g, Rectangle rect, int orientation) {
        Graphics2D g2d = (Graphics2D) g;
        // paint upper gradient
        Color col1 = new Color(255, 255, 255, 0);
        Color col2 = new Color(255, 255, 255, 48);
        Color col3 = new Color(0, 0, 0, 0);
        Color col4 = new Color(0, 0, 0, 32);

        if (orientation == SwingConstants.HORIZONTAL) {
            // paint upper gradient
            fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height >> 1), col2, col1, true);

            // paint lower gradient
            fillGradient(g2d, new Rectangle(rect.x, rect.y + (rect.height >> 1), rect.width, rect.height >> 1), col3, col4, true);
        }
        else {
            // paint left gradient
            fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width >> 1, rect.height), col2, col1, false);

            // paint right gradient
            fillGradient(g2d, new Rectangle(rect.x + (rect.width >> 1), rect.y, rect.width >> 1, rect.height), col3, col4, false);
        }
    }

    public static void fillSingleGradient(Graphics g, Rectangle rect, int orientation) {
        fillSingleGradient(g, rect, orientation, 127);
    }

    public static void fillSingleGradient(Graphics g, Rectangle rect, int orientation, int level) {
        Graphics2D g2d = (Graphics2D) g;
        Color col1 = new Color(255, 255, 255, 0);
        Color col2 = new Color(255, 255, 255, level);

        if (orientation == SwingConstants.SOUTH) {
            fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height), col2, col1, true);
        }
        else if (orientation == SwingConstants.NORTH) {
            fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height), col1, col2, true);
        }
        else if (orientation == SwingConstants.EAST) {
            fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height), col2, col1, false);
        }
        else if (orientation == SwingConstants.WEST) {
            fillGradient(g2d, new Rectangle(rect.x, rect.y, rect.width, rect.height), col1, col2, false);
        }
    }

    /**
     * containerContainsFocus, does the specified container contain the current
     * focusOwner?
     *
     * @param cont the specified container
     * @return Is the current focusOwner a descendent of the specified
     *         container, or the container itself?
     */
    public static boolean containerContainsFocus(Container cont) {
        Component focusOwner =
                KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        Component permFocusOwner =
                KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
        boolean focusOwned = false;
        focusOwned = ((focusOwner != null) && SwingUtilities.isDescendingFrom(focusOwner, cont));
        if (!focusOwned) {
            focusOwned = ((permFocusOwner != null) &&
                    SwingUtilities.isDescendingFrom(permFocusOwner, cont));
        }
        return focusOwned;
    }

//<syd_0002>

    public static boolean componentIsPermanentFocusOwner(Component comp) {
        return ((comp != null) && (KeyboardFocusManager.getCurrentKeyboardFocusManager().
                getPermanentFocusOwner() == comp));
    }

//</syd_0002>

    public static void installColorsAndFont(Component c,
                                            Color background,
                                            Color foreground,
                                            Font font) {
        installFont(c, font);
        installColors(c, background, foreground);
    }

    public static void installFont(Component c, Font font) {
        Font f = c.getFont();
        if (f == null || f instanceof UIResource) {
            c.setFont(font);
        }
    }

    public static void installColors(Component c,
                                     Color background, Color foreground) {
        Color bg = c.getBackground();
        if (background != null && (bg == null || bg instanceof UIResource)) {
            c.setBackground(background);
        }

        Color fg = c.getForeground();
        if (foreground != null && (fg == null || fg instanceof UIResource)) {
            c.setForeground(foreground);
        }
    }

    public static void installBorder(JComponent c, Border defaultBorder) {
        Border border = c.getBorder();
        if (border == null || border instanceof UIResource) {
            c.setBorder(defaultBorder);
        }
    }

    public static void fillNormalGradient(Graphics2D g2d, Shape s, Color startColor, Color endColor, boolean isVertical) {
        Rectangle rect = s.getBounds();
        GradientPaint paint = null;
        if (isVertical) {
            paint = new GradientPaint(rect.x, rect.y, startColor, rect.x, rect.height + rect.y, endColor, true); // turn cyclic to true will be faster
        }
        else {
            paint = new GradientPaint(rect.x, rect.y, startColor, rect.width + rect.x, rect.y, endColor, true);  // turn cyclic to true will be faster
        }
        Paint old = g2d.getPaint();
        g2d.setPaint(paint);
        g2d.fill(s);
        g2d.setPaint(old);
    }

    /**
     * Fills a gradient using the startColor and endColor specified. This is a fast version of fill gradient
     * which will not only leverage hardware acceleration, but also cache GradientPaint and reuse it.
     * <p/>
     * We also leave an option to use the normal GradientPaint to paint the gradient. To do so, just set a system property
     * "normalGradientPaint" to "false".
     *
     * @param g2d
     * @param s
     * @param startColor
     * @param endColor
     * @param isVertical
     */
    public static void fillGradient(Graphics2D g2d, Shape s, Color startColor, Color endColor, boolean isVertical) {
        if ("true".equals(SecurityUtils.getProperty("normalGradientPaint", "false"))) {
            fillNormalGradient(g2d, s, startColor, endColor, isVertical);
        }
        else {
            FastGradientPainter.drawGradient(g2d, s, startColor, endColor, isVertical);
        }
    }

    /**
     * Gets the top modal dialog of current window.
     *
     * @param w
     * @return the top modal dialog of current window.
     */
    public static Window getTopModalDialog(Window w) {
        Window[] ws = w.getOwnedWindows();
        for (int i = 0; i < ws.length; i++) {
            if (ws[i].isVisible() && ws[i] instanceof Dialog && ((Dialog) ws[i]).isModal()) {
                return (getTopModalDialog(ws[i]));
            }
        }
        return w;
    }

    /**
     * For internal usage only.
     */
    public static void traceFocus() {
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String oldName = evt.getOldValue() == null ? "null" : evt.getOldValue().getClass().getName();
                System.out.println(evt.getPropertyName() + ": " + oldName + " ==> " +
                        (evt.getNewValue() == null ? "null" : evt.getNewValue().getClass().getName()));
            }
        };
        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", listener);
        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("permanentFocusOwner", listener);
        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("activeWindow", listener);
    }

    /**
     * For internal usage only.
     */
    public static JPanel createTableModelModifier(final DefaultTableModel tableModel) {
        JPanel tableModelPanel = new JPanel(new BorderLayout(6, 6));
        final JTable table = new JTable(tableModel);
        tableModelPanel.add(new JScrollPane(table));
        ButtonPanel buttonPanel = new ButtonPanel();

        JButton insert = new JButton("Insert");
        insert.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Vector rowData = tableModel.getDataVector();
                int index = table.getSelectedRow();
                if (index != -1) {
                    Vector v = (Vector) rowData.get(index);
                    Vector clone = new Vector();
                    for (int i = 0; i < v.size(); i++) {
                        if (i == 0) {
                            clone.add((int) (Math.random() * 10));
                        }
                        else {
                            clone.add("" + v.get(i));
                        }
                    }
                    tableModel.insertRow(index, clone);
                }
            }
        });

        JButton delete = new JButton("Delete");
        delete.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int[] rows = table.getSelectedRows();
                for (int i = rows.length - 1; i >= 0; i--) {
                    int row = rows[i];
                    tableModel.removeRow(row);
                }
            }
        });

        JButton clear = new JButton("Clear");
        clear.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    tableModel.removeRow(0);
                }
            }
        });

        buttonPanel.add(insert);
        buttonPanel.add(delete);
        buttonPanel.add(clear);
        tableModelPanel.add(buttonPanel, BorderLayout.AFTER_LAST_LINE);
        return tableModelPanel;
    }

    /**
     * Find some subcomponent of the specified container that will accept focus.
     * <p/>
     * Note that this doesn't do something smart like trying to walk the
     * hierarchy horizontally at each level so that the focused subcomponent is
     * as high as possible. Rather, it drills vertically. It's just a safety
     * valve so that focus can be requested somewhere rather than being lost.
     *
     * @param container
     * @return a focusable subcomponent
     */
    public static Component findSomethingFocusable(Container container) {
        if (passesFocusabilityTest(container)) {
            container.requestFocusInWindow();
            return container;
        }
        Component[] comps;
        Component comp;
        comps = container.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (passesFocusabilityTest(comps[i])) {
                container.requestFocusInWindow();
                return container;
            }
            else if (comps[i] instanceof Container) {
                comp = findSomethingFocusable((Container) (comps[i]));
                if (comp != null) {
                    return comp;
                }
            }
        }
        return null;
    }

    /**
     * There are four standard tests which determine if Swing will be able to
     * request focus for a component. Test them.
     *
     * @param comp
     * @return does the specified component pass the four focusability tests
     */
    public static boolean passesFocusabilityTest(Component comp) {
        return ((comp != null) &&
                comp.isEnabled() && comp.isDisplayable() &&
                comp.isVisible() && comp.isFocusable());
    }

    /**
     * Ignore the exception. This method does nothing. However it's a
     * good practice to use this method so that we can easily find
     * out the place that ignoring exception. In development phase,
     * we can log a message in this method so that we can verify if it
     * makes sense to ignore.
     *
     * @param e
     */
    public static void ignoreException(Exception e) {
    }

    /**
     * Prints out the message of the exception.
     *
     * @param e
     */
    public static void printException(Exception e) {
        System.err.println(e.getLocalizedMessage());
    }

    /**
     * Throws the exception. If the exception is RuntimeException, just throw it. Otherwise,
     * wrap it in RuntimeException and throw it.
     *
     * @param e
     */
    public static void throwException(Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        else {
            throw new RuntimeException(e);
        }
    }

    /**
     * Throws the InvocationTargetException. Usually InvocationTargetException
     * has a nested exception as target exception. If the target exception is a RuntimeException
     * or Error, we will throw it. Otherwise, we will wrap it inside RuntimeException and throw it.
     *
     * @param e
     */
    public static void throwInvocationTargetException(InvocationTargetException e) {
        // in most cases, target exception will be RuntimeException
        // but to be on saferside(it may be Error) we explicitly check it
        if (e.getTargetException() instanceof RuntimeException) {
            throw (RuntimeException) e.getTargetException();
        }
        else if (e.getTargetException() instanceof Error) {
            throw (Error) e.getTargetException();
        }
        else {
            throw new RuntimeException(e.getTargetException());
        }
    }

    public static int findDisplayedMnemonicIndex(String text, int mnemonic) {
        if (text == null || mnemonic == '\0') {
            return -1;
        }

        char uc = Character.toUpperCase((char) mnemonic);
        char lc = Character.toLowerCase((char) mnemonic);

        int uci = text.indexOf(uc);
        int lci = text.indexOf(lc);

        if (uci == -1) {
            return lci;
        }
        else if (lci == -1) {
            return uci;
        }
        else {
            return (lci < uci) ? lci : uci;
        }
    }

    /**
     * Gets the first occurence of the component with specified type in the container. It used deep-first searching
     * to find it.
     *
     * @param c
     * @param container
     * @return the first occurence of the component with specified type in the container. Null if nothing is found.
     */
    public static Component getDescendantOfClass(Class c, Container container) {
        if (container == null || c == null)
            return null;

        Component[] components = container.getComponents();

        for (int i = 0; i < components.length; i++) {
            Component component = components[i];
            if (c.isInstance(component)) {
                return component;
            }
            if (component instanceof Container) {
                Component found = getDescendantOfClass(c, (Container) component);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    public static float getDefaultFontSize() {
        // read the font size from system property.
        String fontSize = SecurityUtils.getProperty("jide.fontSize", null);
        float defaultFontSize = -1f;
        try {
            if (fontSize != null) {
                defaultFontSize = Float.parseFloat(fontSize);
            }
        }
        catch (NumberFormatException e) {
        }

        return defaultFontSize;
    }

    public static Object getMenuFont(Toolkit toolkit, UIDefaults table) {
        Object menuFont = null;
        // read the font size from system property.
        float defaultFontSize = getDefaultFontSize();

        if (JideSwingUtilities.shouldUseSystemFont()) {
            if (defaultFontSize == -1/* || SystemInfo.isCJKLocale()*/) {
                menuFont = table.getFont("ToolBar.font");
            }
            else {
                menuFont = new WindowsDesktopProperty("win.menu.font", table.getFont("ToolBar.font"), toolkit, defaultFontSize);
            }
        }
        else {
            menuFont = SecurityUtils.createFontUIResource("Tahoma", Font.PLAIN, defaultFontSize != -1f ? (int) defaultFontSize : 11);
        }

        if (menuFont == null) {
            return getControlFont(toolkit, table);
        }
        else {
            return menuFont;
        }
    }

    public static Object getControlFont(Toolkit toolkit, UIDefaults table) {
        Object controlFont = null;
        // read the font size from system property.
        float defaultFontSize = getDefaultFontSize();

        if (JideSwingUtilities.shouldUseSystemFont()) {
            Font font = table.getFont("Label.font");
            if (font == null) {
                font = new Font("Tahoma", Font.PLAIN, 12); // use default font
            }
            if (defaultFontSize == -1/* || SystemInfo.isCJKLocale()*/) {
                controlFont = font;
            }
            else {
                controlFont = new WindowsDesktopProperty("win.defaultGUI.font", font, toolkit, defaultFontSize);
            }
        }
        else {
            controlFont = SecurityUtils.createFontUIResource("Tahoma", Font.PLAIN, defaultFontSize != -1f ? (int) defaultFontSize : 11);
        }

        return controlFont;
    }

    public static Object getBoldFont(Toolkit toolkit, UIDefaults table) {
        if (SystemInfo.isCJKLocale()) {
            return getControlFont(toolkit, table);
        }
        else {
            Object boldFont = null;
            // read the font size from system property.
            float defaultFontSize = getDefaultFontSize();

            if (JideSwingUtilities.shouldUseSystemFont()) {
                Font font = table.getFont("Label.font");
                if (font == null) {
                    font = new Font("Tahoma", Font.PLAIN, 12); // use default font
                }
                if (defaultFontSize == -1) {
                    boldFont = new FontUIResource(font.deriveFont(Font.BOLD));
                }
                else {
                    boldFont = new WindowsDesktopProperty("win.defaultGUI.font", font, toolkit, defaultFontSize, Font.BOLD);
                }
            }
            else {
                boldFont = SecurityUtils.createFontUIResource("Tahoma", Font.BOLD, defaultFontSize != -1f ? (int) defaultFontSize : 11);
            }
            return boldFont;
        }
    }

    public static void drawShadow(Graphics g, Component c, int x, int y, int w, int h) {
        ShadowFactory factory = new ShadowFactory(6, 0.7f, Color.GRAY);
        BufferedImage temp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = temp.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, temp.getWidth(), temp.getHeight());
        g2.dispose();
        BufferedImage shadow = factory.createShadow(temp);
        g.drawImage(shadow, x, y, c);
    }

    static {
        Font.getFont("defaultFont");
        Font.getFont("emphasizedFont");
    }

    /**
     * Draws a border based on an image. The image can be divided into nine different areas. Each area size is determined
     * by the insets.
     */
    public static void drawImageBorder(Graphics g, ImageIcon img, Rectangle rect, Insets ins, boolean drawCenter) {
        int left = ins.left;
        int right = ins.right;
        int top = ins.top;
        int bottom = ins.bottom;
        int x = rect.x;
        int y = rect.y;
        int w = rect.width;
        int h = rect.height;

        // top
        g.drawImage(img.getImage(), x, y, x + left, y + top,
                0, 0, left, top, null);
        g.drawImage(img.getImage(), x + left, y, x + w - right, y + top,
                left, 0, img.getIconWidth() - right, top, null);
        g.drawImage(img.getImage(), x + w - right, y, x + w, y + top,
                img.getIconWidth() - right, 0, img.getIconWidth(), top, null);

        // middle
        g.drawImage(img.getImage(), x, y + top, x + left, y + h - bottom,
                0, top, left, img.getIconHeight() - bottom, null);
        g.drawImage(img.getImage(), x + left, y + top, x + w - right, y + h - bottom,
                left, top, img.getIconWidth() - right, img.getIconHeight() - bottom, null);
        g.drawImage(img.getImage(), x + w - right, y + top, x + w, y + h - bottom,
                img.getIconWidth() - right, top, img.getIconWidth(), img.getIconHeight() - bottom, null);

        // bottom
        g.drawImage(img.getImage(), x, y + h - bottom, x + left, y + h,
                0, img.getIconHeight() - bottom, left, img.getIconHeight(), null);
        g.drawImage(img.getImage(), x + left, y + h - bottom, x + w - right, y + h,
                left, img.getIconHeight() - bottom, img.getIconWidth() - right, img.getIconHeight(), null);
        g.drawImage(img.getImage(), x + w - right, y + h - bottom, x + w, y + h,
                img.getIconWidth() - right, img.getIconHeight() - bottom, img.getIconWidth(), img.getIconHeight(), null);

        if (drawCenter) {
            g.drawImage(img.getImage(), x + left, y + top, x + w - right, y + h - bottom,
                    left, top, img.getIconWidth() - right, img.getIconHeight() - bottom, null);
        }
    }

    /**
     * Copied from BasicLookAndFeel as the method is package local.
     *
     * @param component
     * @return if request focus is success or not.
     */
    public static boolean compositeRequestFocus(Component component) {
        if (component instanceof Container) {
            Container container = (Container) component;
            if (container.isFocusCycleRoot()) {
                FocusTraversalPolicy policy = container.getFocusTraversalPolicy();
                Component comp = policy.getDefaultComponent(container);
                if (comp != null) {
                    comp.requestFocus();
                    return true;
                }
            }
            Container rootAncestor = container.getFocusCycleRootAncestor();
            if (rootAncestor != null) {
                FocusTraversalPolicy policy = rootAncestor.getFocusTraversalPolicy();
                Component comp = policy.getComponentAfter(rootAncestor, container);

                if (comp != null && SwingUtilities.isDescendingFrom(comp, container)) {
                    comp.requestFocus();
                    return true;
                }
            }
        }
        if (component.isFocusable()) {
            component.requestFocus();
            return true;
        }
        return false;
    }

    public static boolean isAncestorOfFocusOwner(Component component) {
        boolean hasFocus = false;
        Component focusOwner = DefaultFocusManager.getCurrentManager().getFocusOwner();
        if (component == focusOwner || (component instanceof Container && ((Container) component).isAncestorOf(focusOwner))) {
            hasFocus = true;
        }
        return hasFocus;
    }

    /**
     * Gets the top level Window of the component.
     *
     * @param component
     * @return the top level Frame. Null if we didn't find an ancestor which is instance of Frame.
     * @deprecated Please use {@link #getWindowForComponent(java.awt.Component)} instead. getWindowForComponent
     *             method is the same as the same name method in JOptionPane. We have to copy it here because it's not public.
     *             getWindowForComponent is better than this method is because it will give you a shared root frame even when parentComponent is null.
     *             You can refer to {@link javax.swing.JOptionPane#getRootFrame()} for more information.
     */
    public static Window getWindow(Component component) {
        if (component == null) return null;

        if (component instanceof Window) return (Window) component;

        // Find framel
        Container p = component.getParent();
        while (p != null) {
            if (p instanceof Window) {
                return (Window) p;
            }
            p = p.getParent();
        }
        return null;
    }

    public static Window getWindowForComponent(Component parentComponent)
            throws HeadlessException {
        if (parentComponent == null)
            return JOptionPane.getRootFrame();
        if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
            return (Window) parentComponent;
        return getWindowForComponent(parentComponent.getParent());
    }

    /**
     * Checks if the key listener is already registerd on the component.
     *
     * @param component the component
     * @param l         the listener
     * @return true if already registered. Otherwise false.
     */
    public static boolean isKeyListenerRegistered(Component component, KeyListener l) {
        KeyListener[] listeners = component.getKeyListeners();
        for (KeyListener listener : listeners) {
            if (listener == l) {
                return true;
            }
        }
        return false;
    }

    /**
     * Inserts the key listener at the particular index in the listeners' chain.
     *
     * @param component
     * @param l
     * @param index
     */
    public static void insertKeyListener(Component component, KeyListener l, int index) {
        KeyListener[] listeners = component.getKeyListeners();
        for (KeyListener listener : listeners) {
            component.removeKeyListener(listener);
        }
        for (int i = 0; i < listeners.length; i++) {
            KeyListener listener = listeners[i];
            if (index == i) {
                component.addKeyListener(l);
            }
            component.addKeyListener(listener);
        }
        // inex is too large, add to the end.
        if (index > listeners.length - 1) {
            component.addKeyListener(l);
        }
    }

    /**
     * Checks if the mouse listener is already registerd on the component.
     *
     * @param component the component
     * @param l         the listener
     * @return true if already registered. Otherwise false.
     */
    public static boolean isMouseListenerRegistered(Component component, MouseListener l) {
        MouseListener[] listeners = component.getMouseListeners();
        for (MouseListener listener : listeners) {
            if (listener == l) {
                return true;
            }
        }
        return false;
    }

    /**
     * Inserts the mouse listener at the particular index in the listeners' chain.
     *
     * @param component
     * @param l
     * @param index
     */
    public static void insertMouseListener(Component component, MouseListener l, int index) {
        MouseListener[] listeners = component.getMouseListeners();
        for (MouseListener listener : listeners) {
            component.removeMouseListener(listener);
        }
        for (int i = 0; i < listeners.length; i++) {
            MouseListener listener = listeners[i];
            if (index == i) {
                component.addMouseListener(l);
            }
            component.addMouseListener(listener);
        }
        // inex is too large, add to the end.
        if (index > listeners.length - 1) {
            component.addMouseListener(l);
        }
    }

    /**
     * Checks if the mouse motion listener is already registerd on the component.
     *
     * @param component the component
     * @param l         the listener
     * @return true if already registered. Otherwise false.
     */
    public static boolean isMouseMotionListenerRegistered(Component component, MouseMotionListener l) {
        MouseMotionListener[] listeners = component.getMouseMotionListeners();
        for (MouseMotionListener listener : listeners) {
            if (listener == l) {
                return true;
            }
        }
        return false;
    }

    /**
     * Inserts the mouse motion listener at the particular index in the listeners' chain.
     *
     * @param component
     * @param l
     * @param index
     */
    public static void insertMouseMotionListener(Component component, MouseMotionListener l, int index) {
        MouseMotionListener[] listeners = component.getMouseMotionListeners();
        for (MouseMotionListener listener : listeners) {
            component.removeMouseMotionListener(listener);
        }
        for (int i = 0; i < listeners.length; i++) {
            MouseMotionListener listener = listeners[i];
            if (index == i) {
                component.addMouseMotionListener(l);
            }
            component.addMouseMotionListener(listener);
        }
        // inex is too large, add to the end.
        if (index > listeners.length - 1) {
            component.addMouseMotionListener(l);
        }
    }

    /**
     * Gets the scroll pane around the component.
     *
     * @param innerComponent
     * @return the scroll pane. Null if the component is not in any JScrollPane.
     */
    public static Component getScrollPane(Component innerComponent) {
        Component component = innerComponent;
        if (component.getParent() != null && component.getParent().getParent() != null && component.getParent().getParent() instanceof JScrollPane) {
            component = (JComponent) component.getParent().getParent();
            return component;
        }
        else {
            return null;
        }
    }

    /**
     * Checks if the listener is always registered to the EventListenerList to avoid duplicated registration of the same listener
     *
     * @param list the EventListenerList to register the listener.
     * @param t    the type of the EventListener.
     * @param l    the listener.
     * @return true if already registered. Otherwise false.
     */
    public static boolean isListenerRegistered(EventListenerList list, Class t, EventListener l) {
        Object[] objects = list.getListenerList();
        return isListenerRegistered(objects, t, l);
    }

    /**
     * Checks if the listener is always registered to the Component to avoid duplicated registration of the same listener
     *
     * @param component the component that you want to register the listener.
     * @param t         the type of the EventListener.
     * @param l         the listener.
     * @return true if already registered. Otherwise false.
     */
    public static boolean isListenerRegistered(Component component, Class t, EventListener l) {
        Object[] objects = component.getListeners(t);
        return isListenerRegistered(objects, t, l);
    }

    private static boolean isListenerRegistered(Object[] objects, Class t, EventListener l) {
        for (int i = 0; i < objects.length; i++) {
            Object listener = objects[i];
            if (t.isAssignableFrom(listener.getClass()) && listener == l) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the first child of the component that is the specified type.
     *
     * @param clazz
     * @param c
     * @return the first child of the component that is the specified type.
     */
    public static Component getFirstChildOf(final Class clazz, Component c) {
        return getRecursively(c, new GetHandler() {
            public boolean condition(Component c) {
                return clazz.isAssignableFrom(c.getClass());
            }

            public Component action(Component c) {
                return c;
            }
        });
    }

    public static Vector convertDefaultComboBoxModelToVector(DefaultComboBoxModel model) {
        Vector v = new Vector();
        for (int i = 0; i < model.getSize(); i++) {
            v.add(model.getElementAt(i));
        }
        return v;

    }

    /**
     * To make sure the row is visible. If the table's horizontal scroll bar is visible, the method will
     * not change the horizontal scroll bar's position.
     *
     * @param table
     * @param row
     */
    public static void ensureRowVisible(JTable table, int row) {
        Rectangle r = table.getVisibleRect();
        // Hack! make above and below visible if necessary
        // TODO: how to center it or make it the first?
        Rectangle rMid = table.getCellRect(row, 0, true);
        Rectangle rBefore = null, rAfter = null;
        if (row < table.getModel().getRowCount() - 1)
            rAfter = table.getCellRect(row + 1, 0, true);
        if (row > 0)
            rBefore = table.getCellRect(row - 1, 0, true);

        int yLow = (int) rMid.getMinY();
        int yHi = (int) rMid.getMaxY();
        int xLow = r.x;
        int xHi = r.x + r.width;

        if (rBefore != null)
            yLow = (int) rBefore.getMinY();

        if (rAfter != null) {
            yHi = (int) rAfter.getMaxY();
        }

        Rectangle rScrollTo = new Rectangle(xLow, yLow, xHi - xLow, yHi - yLow);
        if (!r.contains(rScrollTo) && rScrollTo.height != 0) {
            table.scrollRectToVisible(rScrollTo);
        }
    }
}
