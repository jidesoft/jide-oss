/*
 * @(#)LookAndFeelFactory.java 5/28/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf;

import com.jidesoft.icons.IconsFactory;
import com.jidesoft.icons.JideIconsFactory;
import com.jidesoft.plaf.basic.BasicPainter;
import com.jidesoft.plaf.basic.Painter;
import com.jidesoft.plaf.eclipse.Eclipse3xMetalUtils;
import com.jidesoft.plaf.eclipse.Eclipse3xWindowsUtils;
import com.jidesoft.plaf.eclipse.EclipseMetalUtils;
import com.jidesoft.plaf.eclipse.EclipseWindowsUtils;
import com.jidesoft.plaf.office2003.Office2003Painter;
import com.jidesoft.plaf.office2003.Office2003WindowsUtils;
import com.jidesoft.plaf.office2007.Office2007WindowsUtils;
import com.jidesoft.plaf.vsnet.VsnetMetalUtils;
import com.jidesoft.plaf.vsnet.VsnetWindowsUtils;
import com.jidesoft.plaf.xerto.XertoMetalUtils;
import com.jidesoft.plaf.xerto.XertoWindowsUtils;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.utils.ProductNames;
import com.jidesoft.utils.SecurityUtils;
import com.jidesoft.utils.SystemInfo;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import sun.swing.SwingLazyValue;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * JIDE Software created many new components that need their own ComponentUI classes and additional UIDefaults in
 * UIDefaults table. LookAndFeelFactory can take the UIDefaults from any existing look and feel and add the extra
 * UIDefaults JIDE components need.
 * <p/>
 * Before using any JIDE components, please make you call one of the two LookAndFeelFactory.installJideExtension(...)
 * methods. Basically, you set L&F using UIManager first just like before, then call installJideExtension. See code
 * below for an example.
 * <code><pre>
 * UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName()); // you need to catch the
 * exceptions
 * on this call.
 * LookAndFeelFactory.installJideExtension();
 * </pre></code>
 * LookAndFeelFactory.installJideExtension() method will check what kind of L&F you set and what operating system you
 * are on and decide which style of JIDE extension it will install. Here is the rule. <ul> <li> OS: Windows XP with XP
 * theme on, L&F: Windows L&F => OFFICE2003_STYLE <li> OS: any Windows, L&F: Windows L&F => VSNET_STYLE <li> OS: Linux,
 * L&F: any L&F based on Metal L&F => VSNET_STYLE <li> OS: Mac OS X, L&F: Aqua L&F => AQUA_STYLE <li> OS: any OS, L&F:
 * Quaqua L&F => AQUA_STYLE <li> Otherwise => VSNET_STYLE </ul> There is also another installJideExtension which takes
 * an int style parameter. You can pass in {@link #VSNET_STYLE}, {@link #ECLIPSE_STYLE}, {@link #ECLIPSE3X_STYLE},
 * {@link #OFFICE2003_STYLE}, or {@link #XERTO_STYLE}. In the other word, you will make the choice of style instead of
 * letting LookAndFeelFactory to decide one for you. Please note, there is no constant defined for AQUA_STYLE. The only
 * way to use it is when you are using Aqua L&F or Quaqua L&F and you call installJideExtension() method, the one
 * without parameter.
 * <p/>
 * LookAndFeelFactory supports a number of known L&Fs. You can see those L&Fs as constants whose names are something
 * like "_LNF" such as WINDOWS_LNF.
 * <p/>
 * If you are using a 3rd party L&F we are not officially supporting, we might need to customize it. Here are two
 * classes you can use. The first one is {@link UIDefaultsCustomizer}. You can add a number of customizers to
 * LookAndFeelFactory. After LookAndFeelFactory installJideExtension method is called, we will call customize() method
 * on each UIDefaultsCustomizer to add additional UIDefaults you specified. You can use UIDefaultsCustomizer to do
 * things like small tweaks to UIDefaults without the hassle of creating a new style.
 * <p/>
 * Most likely, we will not need to use {@link UIDefaultsInitializer} if you are use L&Fs such as WindowsLookAndFeel,
 * any L&Fs based on MetalLookAndFeel, or AquaLookAndFeel etc. The only exception is Synth L&F and any L&Fs based on it.
 * The reason is we calculate all colors we will use in JIDE components from existing well-known UIDefaults. For
 * example, we will use UIManagerLookup.getColor("activeCaption") to calculate a color that we can use in dockable
 * frame's title pane. We will use UIManagerLookup.getColor("control") to calculate a color that we can use as
 * background of JIDE component. Most L&Fs will fill those UIDefaults. However in Synth L&F, those UIDefaults may or may
 * not have a valid value. You will end up with NPE later in the code when you call installJideExtension. In this case,
 * you can add those extra UIDefaults in UIDefaultsInitializer. We will call it before installJideExtension is called so
 * that those UIDefaults are there ready for us to use. This is how added support to GTK L&F and Synthetica L&F.
 * <p/>
 * {@link #installJideExtension()} method will only add the additional UIDefaults to current ClassLoader. If you have
 * several class loaders in your system, you probably should tell the UIManager to use the class loader that called
 * <code>installJideExtension</code>. Otherwise, you might some unexpected errors. Here is how to specify the class
 * loaders.
 * <code><pre>
 * UIManager.put("ClassLoader", currentClass.getClassLoader()); // currentClass is the class where
 * the code is.
 * LookAndFeelFactory.installDefaultLookAndFeelAndExtension(); // or installJideExtension()
 * </pre></code>
 */
public class LookAndFeelFactory implements ProductNames {

    /**
     * Class name of Windows L&F provided in Sun JDK.
     */
    public static final String WINDOWS_LNF = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

    /**
     * Class name of Metal L&F provided in Sun JDK.
     */
    public static final String METAL_LNF = "javax.swing.plaf.metal.MetalLookAndFeel";

    /**
     * Class name of Aqua L&F provided in Apple Mac OS X JDK.
     */
    public static final String AQUA_LNF = "apple.laf.AquaLookAndFeel";

    /**
     * Class name of Aqua L&F provided in Apple Mac OS X JDK. This is the new package since Java Update 6.
     */
    public static final String AQUA_LNF_6 = "com.apple.laf.AquaLookAndFeel";

    /**
     * Class name of Quaqua L&F.
     */
    public static final String QUAQUA_LNF = "ch.randelshofer.quaqua.QuaquaLookAndFeel";

    /**
     * Class name of Quaqua Alloy L&F.
     */
    public static final String ALLOY_LNF = "com.incors.plaf.alloy.AlloyLookAndFeel";

    /**
     * Class name of Synthetica L&F.
     */
    public static final String SYNTHETICA_LNF = "de.javasoft.plaf.synthetica.SyntheticaLookAndFeel";

    private static final String SYNTHETICA_LNF_PREFIX = "de.javasoft.plaf.synthetica.Synthetica";

    /**
     * Class name of Plastic3D L&F before JGoodies Look 1.3 release.
     */
    public static final String PLASTIC3D_LNF = "com.jgoodies.plaf.plastic.Plastic3DLookAndFeel";

    /**
     * Class name of Plastic3D L&F after JGoodies Look 1.3 release.
     */
    public static final String PLASTIC3D_LNF_1_3 = "com.jgoodies.looks.plastic.Plastic3DLookAndFeel";

    /**
     * Class name of PlasticXP L&F.
     */
    public static final String PLASTICXP_LNF = "com.jgoodies.looks.plastic.PlasticXPLookAndFeel";

    /**
     * Class name of Tonic L&F.
     */
    public static final String TONIC_LNF = "com.digitprop.tonic.TonicLookAndFeel";

    /**
     * Class name of A03 L&F.
     */
    public static final String A03_LNF = "a03.swing.plaf.A03LookAndFeel";

    /**
     * Class name of Pgs L&F.
     */
    public static final String PGS_LNF = "com.pagosoft.plaf.PgsLookAndFeel";

    /*
     * Class name of Substance L&F.
     */
//    public static final String SUBSTANCE_LNF = "org.jvnet.substance.SubstanceLookAndFeel";

//    private static final String SUBSTANCE_LNF_PREFIX = "org.jvnet.substance.skin";

    /**
     * Class name of GTK L&F provided by Sun JDK.
     */
    public static final String GTK_LNF = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";

    /**
     * The name of Nimbus L&F. We didn't create a constant for Nimbus is because the package name will be changed in
     * JDK7 release
     */
    public static final String NIMBUS_LNF_NAME = "NimbusLookAndFeel";

    /**
     * A style that you can use with {@link #installJideExtension(int)} method. This style is the same as VSNET_STYLE
     * except it doesn't have menu related UIDefaults. You can only use this style if you didn't use any component from
     * JIDE Action Framework.
     * <p/>
     *
     * @see #VSNET_STYLE
     */
    public static final int VSNET_STYLE_WITHOUT_MENU = 0;

    /**
     * A style that you can use with {@link #installJideExtension(int)} method. This style mimics the visual style of
     * Microsoft Visual Studio .NET for the toolbars, menus and dockable windows.
     * <p/>
     * Vsnet style is a very simple style with no gradient. Although it works on almost all L&Fs in any operating
     * systems, it looks the best on Windows 2000 or 98, or on Windows XP when XP theme is not on. If XP theme is on, we
     * suggest you use Office2003 style or Xerto style. Since the style is so simple, it works with a lot of the 3rd
     * party L&F such as Tonic, Pgs, Alloy etc without causing too much noise. That's why this is also the default style
     * for any L&Fs we don't recognize when you call {@link #installJideExtension()}, the one with out style parameter.
     * If you would like another style to be used as the default style, you can call {@link #setDefaultStyle(int)}
     * method.
     * <p/>
     * Here is the code to set to Windows L&F with Vsnet style extension.
     * <code><pre>
     * UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName()); // you need to catch the
     * exceptions on this call.
     * LookAndFeelFactory.installJideExtension(LookAndFeelFactory.VSNET_STYLE);
     * </pre></code>
     * There is a special system property "shading theme" you can use. If you turn it on using the code below, you will
     * see a gradient on dockable frame's title pane and rounded corner and gradient on the tabs of JideTabbedPane. So
     * if the L&F you are using uses gradient, you can set this property to true to match with your L&F. For example, if
     * you use Plastic3D L&F, turning this property on will look better.
     * <code><pre>
     * System.setProperty("shadingtheme", "true");
     * </pre></code>
     */
    public static final int VSNET_STYLE = 1;

    /**
     * A style that you can use with {@link #installJideExtension(int)} method. This style mimics the visual style of
     * Eclipse 2.x for the toolbars, menus and dockable windows.
     * <p/>
     * Eclipse style works for almost all L&Fs and on any operating systems, although it looks the best on Windows. For
     * any other operating systems we suggest you to use XERTO_STYLE or VSNET_STYLE.
     * <p/>
     * Here is the code to set to any L&F with Eclipse style extension.
     * <code><pre>
     * UIManager.setLookAndFeel(AnyLookAndFeel.class.getName()); // you need to catch the
     * exceptions
     * on this call.
     * LookAndFeelFactory.installJideExtension(LookAndFeelFactory.ECLIPSE_STYLE);
     * </pre></code>
     */
    public static final int ECLIPSE_STYLE = 2;

    /**
     * A style that you can use with {@link #installJideExtension(int)} method. This style mimics the visual style of
     * Microsoft Office2003 for the toolbars, menus and dockable windows.
     * <p/>
     * Office2003 style looks great on Windows XP when Windows or Windows XP L&F from Sun JDK is used. It replicated the
     * exact same style as Microsoft Office 2003, to give your end user a familiar visual style.
     * <p/>
     * Here is the code to set to Windows L&F with Office2003 style extension.
     * <code><pre>
     * UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName()); // you need to catch the
     * exceptions on this call.
     * LookAndFeelFactory.installJideExtension(LookAndFeelFactory.OFFICE2003_STYLE);
     * </pre></code>
     * It works either on any other Windows such asWindows 2000, Windows 98 etc. If you are on Windows XP, Office2003
     * style will change theme based on the theme setting in Windows Display Property. But if you are not on XP,
     * Office2003 style will use the default gray theme only. You can force to change it using {@link
     * Office2003Painter#setColorName(String)} method, but it won't look good as other non-JIDE components won't have
     * the matching theme.
     * <p/>
     * Office2003 style doesn't work on any operating systems other than Windows mainly because the design of Office2003
     * style is so centric to Windows that it doesn't look good on other operating systems.
     */
    public static final int OFFICE2003_STYLE = 3;

    /**
     * A style that you can use with {@link #installJideExtension(int)} method. This style is created by Xerto
     * (http://www.xerto.com) which is used in their Imagery product.
     * <p/>
     * Xerto style looks great on Windows XP when Windows XP L&F from Sun JDK is used.
     * <p/>
     * Here is the code to set to Windows L&F with Xerto style extension.
     * <code><pre>
     * UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName()); // you need to catch the
     * exceptions on this call.
     * LookAndFeelFactory.installJideExtension(LookAndFeelFactory.XERTO_STYLE);
     * </pre></code>
     * Although it looks the best on Windows, Xerto style also supports Linux or Solaris if you use any L&Fs based on
     * Metal L&F or Synth L&F. For example, we recommend you to use Xerto style as default if you use SyntheticaL&F, a
     * L&F based on Synth. To use it, you basically replace WindowsLookAndFeel to the L&F you want to use in
     * setLookAndFeel line above.
     */
    public static final int XERTO_STYLE = 4;

    /**
     * A style that you can use with {@link #installJideExtension(int)} method. This style is the same as XERTO_STYLE
     * except it doesn't have menu related UIDefaults. You can only use this style if you didn't use any component from
     * JIDE Action Framework. Please note, we only use menu extension for Xerto style when the underlying L&F is Windows
     * L&F. If you are using L&F such as Metal or other 3rd party L&F based on Metal, XERTO_STYLE_WITHOUT_MENU will be
     * used even you use XERTO_STYLE when calling to installJideExtension().
     * <p/>
     *
     * @see #XERTO_STYLE
     */
    public static final int XERTO_STYLE_WITHOUT_MENU = 6;

    /**
     * A style that you can use with {@link #installJideExtension(int)} method. This style mimics the visual style of
     * Eclipse 3.x for the toolbars, menus and dockable windows.
     * <p/>
     * Eclipse 3x style works for almost all L&Fs and on any operating systems, although it looks the best on Windows.
     * For any other OS's we suggest you to use XERTO_STYLE or VSNET_STYLE.
     * <code><pre>
     * UIManager.setLookAndFeel(AnyLookAndFeel.class.getName()); // you need to catch the
     * exceptions
     * on this call.
     * LookAndFeelFactory.installJideExtension(LookAndFeelFactory.ECLIPSE3X_STYLE);
     * </pre></code>
     */
    public static final int ECLIPSE3X_STYLE = 5;

    /**
     * A style that you can use with {@link #installJideExtension(int)} method. This style mimics the visual style of
     * Microsoft Office2007 for the toolbars, menus and dockable windows.
     * <p/>
     * Office2007 style looks great on Windows Vista when Windows L&F from Sun JDK is used. It replicated the exact same
     * style as Microsoft Office 2007, to give your end user a familiar visual style.
     * <p/>
     * Here is the code to set to Windows L&F with Office2007 style extension.
     * <code><pre>
     * UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName()); // you need to catch the
     * exceptions on this call.
     * LookAndFeelFactory.installJideExtension(LookAndFeelFactory.OFFICE2007_STYLE);
     * </pre></code>
     * <p/>
     * Office2007 style doesn't work on any operating systems other than Windows mainly because the design of Office2003
     * style is so centric to Windows that it doesn't look good on other operating systems.
     * <p/>
     * Because we use some painting code that is only available in JDK6, Office 2007 style only runs if you are using
     * JDK6 and above.
     */
    public static final int OFFICE2007_STYLE = 7;

    private static int _style = -1;
    private static int _defaultStyle = -1;
    private static LookAndFeel _lookAndFeel;

    /**
     * If installJideExtension is called, it will put an entry on UIDefaults table.
     * UIManagerLookup.getBoolean(JIDE_EXTENSION_INSTALLLED) will return true. You can also use {@link
     * #isJideExtensionInstalled()} to check the value instead of using UIManagerLookup.getBoolean(JIDE_EXTENSION_INSTALLLED).
     */
    public static final String JIDE_EXTENSION_INSTALLLED = "jidesoft.extendsionInstalled";

    /**
     * If installJideExtension is called, a JIDE style will be installed on UIDefaults table. If so,
     * UIManagerLookup.getInt(JIDE_STYLE_INSTALLED) will return you the style that is installed. For example, if the
     * value is 1, it means VSNET_STYLE is installed because 1 is the value of VSNET_STYLE.
     */
    public static final String JIDE_STYLE_INSTALLED = "jidesoft.extendsionStyle";

    /**
     * An interface to make the customization of UIDefaults easier. This customizer will be called after
     * installJideExtension() is called. So if you want to further customize UIDefault, you can use this customizer to
     * do it.
     */
    public static interface UIDefaultsCustomizer {
        void customize(UIDefaults defaults);
    }

    /**
     * An interface to make the initialization of UIDefaults easier. This initializer will be called before
     * installJideExtension() is called. So if you want to initialize UIDefault before installJideExtension is called,
     * you can use this initializer to do it.
     */
    public static interface UIDefaultsInitializer {
        void initialize(UIDefaults defaults);
    }

    private static List<UIDefaultsCustomizer> _uiDefaultsCustomizers = new Vector<UIDefaultsCustomizer>();
    private static List<UIDefaultsInitializer> _uiDefaultsInitializers = new Vector<UIDefaultsInitializer>();
    private static Map<String, String> _installedLookAndFeels = new HashMap<String, String>();

    public static final String LAF_INSTALLED = "installed";
    public static final String LAF_NOT_INSTALLED = "not installed";

    protected LookAndFeelFactory() {
    }

    /**
     * Gets the default style. If you never set default style before, it will return OFFICE2003_STYLE if you are on
     * Windows XP, L&F is instance of Windows L&F and XP theme is on. Otherwise, it will return VSNET_STYLE. If you set
     * default style before, it will return whatever style you set.
     *
     * @return the default style.
     */
    public static int getDefaultStyle() {
        if (_defaultStyle == -1) {
            String defaultStyle = SecurityUtils.getProperty("jide.defaultStyle", "-1");
            try {
                _defaultStyle = Integer.parseInt(defaultStyle);
            }
            catch (NumberFormatException e) {
                // ignore
            }
            if (_defaultStyle == -1) {
                int suggestedStyle;
                try {
                    if (SystemInfo.isWindowsVistaAbove() && UIManager.getLookAndFeel() instanceof WindowsLookAndFeel && SystemInfo.isJdk6Above()) {
                        suggestedStyle = OFFICE2007_STYLE;
                    }
                    else if (XPUtils.isXPStyleOn() && UIManager.getLookAndFeel() instanceof WindowsLookAndFeel) {
                        suggestedStyle = OFFICE2003_STYLE;
                    }
                    else {
                        suggestedStyle = VSNET_STYLE;
                    }
                }
                catch (UnsupportedOperationException e) {
                    suggestedStyle = VSNET_STYLE;
                }
                return suggestedStyle;
            }
        }
        return _defaultStyle;
    }

    /**
     * Sets the default style. If you call this method to set a default style, {@link #installJideExtension()} will use
     * it as the default style.
     *
     * @param defaultStyle the default style.
     */
    public static void setDefaultStyle(int defaultStyle) {
        _defaultStyle = defaultStyle;
    }

    /**
     * Adds additional UIDefaults JIDE needed to UIDefault table. You must call this method every time switching look
     * and feel. And callupdateComponentTreeUI() in corresponding DockingManager or DockableBarManager after this call.
     * <pre><code>
     *  try {
     *      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
     *  }
     *  catch (ClassNotFoundException e) {
     *     e.printStackTrace();
     *  }
     *  catch (InstantiationException e) {
     *     e.printStackTrace();
     *  }
     *  catch (IllegalAccessException e) {
     *      e.printStackTrace();
     *  }
     *  catch (UnsupportedLookAndFeelException e) {
     *      e.printStackTrace();
     *  }
     * <p/>
     *  // to additional UIDefault for JIDE components
     *  LookAndFeelFactory.installJideExtension(); // use default style VSNET_STYLE. You can change
     * to a different style
     * using setDefaultStyle(int style) and then call this method. Or simply call
     * installJideExtension(style).
     * <p/>
     *  // call updateComponentTreeUI
     *  frame.getDockableBarManager().updateComponentTreeUI();
     *  frame.getDockingManager().updateComponentTreeUI();
     * </code></pre>
     */
    public static void installJideExtension() {
        installJideExtension(getDefaultStyle());
    }

    /**
     * Add additional UIDefaults JIDE needed to UIDefaults table. You must call this method every time switching look
     * and feel. And call updateComponentTreeUI() in corresponding DockingManager or DockableBarManager after this
     * call.
     * <pre><code>
     *  try {
     *      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
     *  }
     *  catch (ClassNotFoundException e) {
     *     e.printStackTrace();
     *  }
     *  catch (InstantiationException e) {
     *     e.printStackTrace();
     *  }
     *  catch (IllegalAccessException e) {
     *      e.printStackTrace();
     *  }
     *  catch (UnsupportedLookAndFeelException e) {
     *      e.printStackTrace();
     *  }
     * <p/>
     *  // to add additional UIDefault for JIDE components
     *  LookAndFeelFactory.installJideExtension(LookAndFeelFactory.OFFICE2003_STYLE);
     * <p/>
     *  // call updateComponentTreeUI
     *  frame.getDockableBarManager().updateComponentTreeUI();
     *  frame.getDockingManager().updateComponentTreeUI();
     * </code></pre>
     *
     * @param style the style of the extension.
     */
    public static void installJideExtension(int style) {
        installJideExtension(UIManager.getLookAndFeelDefaults(), UIManager.getLookAndFeel(), style);
    }

    /**
     * Checks if JIDE extension is installed. Please note, UIManager.setLookAndFeel() method will overwrite the whole
     * UIDefaults table. So even you called {@link #installJideExtension()} method before, UIManager.setLookAndFeel()
     * method make isJideExtensionInstalled returning false.
     *
     * @return true if installed.
     */
    public static boolean isJideExtensionInstalled() {
        return UIDefaultsLookup.getBoolean(JIDE_EXTENSION_INSTALLLED);
    }

    /**
     * Installs the UIDefault needed by JIDE component to the uiDefaults table passed in.
     *
     * @param uiDefaults the UIDefault tables where JIDE UIDefaults will be installed.
     * @param lnf        the LookAndFeel. This may have an effect on which set of JIDE UIDefaults we will install.
     * @param style      the style of the JIDE UIDefaults.
     */
    public static void installJideExtension(UIDefaults uiDefaults, LookAndFeel lnf, int style) {
        if (isJideExtensionInstalled() && _style == style && _lookAndFeel == lnf) {
            return;
        }

        _style = style;
        uiDefaults.put(JIDE_STYLE_INSTALLED, _style);

        _lookAndFeel = lnf;
        UIDefaultsInitializer[] initializers = getUIDefaultsInitializers();
        for (UIDefaultsInitializer initializer : initializers) {
            if (initializer != null) {
                initializer.initialize(uiDefaults);
            }
        }

        // For Alloy
/*        if (lnf.getClass().getName().equals(ALLOY_LNF) && isAlloyLnfInstalled()) {
            Object progressBarUI = uiDefaults.get("ProgressBarUI");
            VsnetMetalUtils.initClassDefaults(uiDefaults);
            VsnetMetalUtils.initComponentDefaults(uiDefaults);
            uiDefaults.put("ProgressBarUI", progressBarUI);
            uiDefaults.put("DockableFrameUI", "com.jidesoft.plaf.vsnet.VsnetDockableFrameUI");
            uiDefaults.put("DockableFrameTitlePane.hideIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 0, titleButtonSize, titleButtonSize));
            uiDefaults.put("DockableFrameTitlePane.unfloatIcon", IconsFactory.getIcon(null, titleButtonImage, 0, titleButtonSize, titleButtonSize, titleButtonSize));
            uiDefaults.put("DockableFrameTitlePane.floatIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 2 * titleButtonSize, titleButtonSize, titleButtonSize));
            uiDefaults.put("DockableFrameTitlePane.autohideIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 3 * titleButtonSize, titleButtonSize, titleButtonSize));
            uiDefaults.put("DockableFrameTitlePane.stopAutohideIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 4 * titleButtonSize, titleButtonSize, titleButtonSize));
            uiDefaults.put("DockableFrameTitlePane.hideAutohideIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 5 * titleButtonSize, titleButtonSize, titleButtonSize));
            uiDefaults.put("DockableFrameTitlePane.maximizeIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 6 * titleButtonSize, titleButtonSize, titleButtonSize));
            uiDefaults.put("DockableFrameTitlePane.restoreIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 7 * titleButtonSize, titleButtonSize, titleButtonSize));
            uiDefaults.put("DockableFrameTitlePane.buttonGap", new Integer(4)); // gap between buttons
        }
        else */
        if ((lnf.getClass().getName().equals(ALLOY_LNF) && isAlloyLnfInstalled())
                || (lnf.getClass().getName().equals(PLASTIC3D_LNF) && isPlastic3DLnfInstalled())
                || (lnf.getClass().getName().equals(PLASTIC3D_LNF_1_3) && isPlastic3D13LnfInstalled())
                || (lnf.getClass().getName().equals(PLASTICXP_LNF) && isPlasticXPLnfInstalled())
                || (lnf.getClass().getName().equals(PGS_LNF) && isPgsLnfInstalled())
                || (lnf.getClass().getName().equals(TONIC_LNF) && isTonicLnfInstalled())) {
            switch (style) {
                case OFFICE2007_STYLE:
                    VsnetWindowsUtils.initComponentDefaults(uiDefaults);
                    Office2003WindowsUtils.initComponentDefaults(uiDefaults);
                    Office2007WindowsUtils.initComponentDefaults(uiDefaults);
                    Office2007WindowsUtils.initClassDefaults(uiDefaults, false);
                    break;
                case OFFICE2003_STYLE:
                    VsnetWindowsUtils.initComponentDefaults(uiDefaults);
                    Office2003WindowsUtils.initComponentDefaults(uiDefaults);
                    Office2003WindowsUtils.initClassDefaults(uiDefaults, false);
                    break;
                case VSNET_STYLE:
                case VSNET_STYLE_WITHOUT_MENU:
                    VsnetMetalUtils.initComponentDefaults(uiDefaults);
                    VsnetMetalUtils.initClassDefaults(uiDefaults);

                    Painter gripperPainter = new Painter() {
                        public void paint(JComponent c, Graphics g, Rectangle rect, int orientation, int state) {
                            Office2003Painter.getInstance().paintGripper(c, g, rect, orientation, state);
                        }
                    };

                    // set all grippers to Office2003 style gripper
                    uiDefaults.put("Gripper.painter", gripperPainter);
                    uiDefaults.put("JideTabbedPane.gripperPainter", gripperPainter);
                    uiDefaults.put("JideTabbedPane.defaultTabShape", JideTabbedPane.SHAPE_OFFICE2003);
                    uiDefaults.put("JideTabbedPane.selectedTabTextForeground", UIDefaultsLookup.getColor("controlText"));
                    uiDefaults.put("JideTabbedPane.unselectedTabTextForeground", UIDefaultsLookup.getColor("controlText"));
                    uiDefaults.put("JideTabbedPane.foreground", UIDefaultsLookup.getColor("controlText"));
                    uiDefaults.put("JideTabbedPane.light", UIDefaultsLookup.getColor("control"));
                    uiDefaults.put("JideSplitPaneDivider.gripperPainter", gripperPainter);

                    int products = LookAndFeelFactory.getProductsUsed();
                    if ((products & PRODUCT_DOCK) != 0) {
                        ImageIcon titleButtonImage = IconsFactory.getImageIcon(VsnetWindowsUtils.class, "icons/title_buttons_windows.gif"); // 10 x 10 x 8
                        final int titleButtonSize = 10;

                        uiDefaults.put("DockableFrameUI", "com.jidesoft.plaf.vsnet.VsnetDockableFrameUI");
                        uiDefaults.put("DockableFrameTitlePane.hideIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 0, titleButtonSize, titleButtonSize));
                        uiDefaults.put("DockableFrameTitlePane.unfloatIcon", IconsFactory.getIcon(null, titleButtonImage, 0, titleButtonSize, titleButtonSize, titleButtonSize));
                        uiDefaults.put("DockableFrameTitlePane.floatIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 2 * titleButtonSize, titleButtonSize, titleButtonSize));
                        uiDefaults.put("DockableFrameTitlePane.autohideIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 3 * titleButtonSize, titleButtonSize, titleButtonSize));
                        uiDefaults.put("DockableFrameTitlePane.stopAutohideIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 4 * titleButtonSize, titleButtonSize, titleButtonSize));
                        uiDefaults.put("DockableFrameTitlePane.hideAutohideIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 5 * titleButtonSize, titleButtonSize, titleButtonSize));
                        uiDefaults.put("DockableFrameTitlePane.maximizeIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 6 * titleButtonSize, titleButtonSize, titleButtonSize));
                        uiDefaults.put("DockableFrameTitlePane.restoreIcon", IconsFactory.getIcon(null, titleButtonImage, 0, 7 * titleButtonSize, titleButtonSize, titleButtonSize));
                        uiDefaults.put("DockableFrameTitlePane.buttonGap", 4); // gap between buttons
                        uiDefaults.put("DockableFrame.titleBorder", new BorderUIResource(BorderFactory.createEmptyBorder(1, 0, 2, 0)));
                        uiDefaults.put("DockableFrame.border", new BorderUIResource(BorderFactory.createEmptyBorder(2, 0, 0, 0)));
                        uiDefaults.put("DockableFrameTitlePane.gripperPainter", gripperPainter);
                    }
                    break;
                case ECLIPSE_STYLE:
                    EclipseMetalUtils.initComponentDefaults(uiDefaults);
                    EclipseMetalUtils.initClassDefaults(uiDefaults);
                    break;
                case ECLIPSE3X_STYLE:
                    Eclipse3xMetalUtils.initComponentDefaults(uiDefaults);
                    Eclipse3xMetalUtils.initClassDefaults(uiDefaults);
                    break;
                case XERTO_STYLE:
                case XERTO_STYLE_WITHOUT_MENU:
                    XertoMetalUtils.initComponentDefaults(uiDefaults);
                    XertoMetalUtils.initClassDefaults(uiDefaults);
                    break;
            }
            uiDefaults.put("Theme.painter", BasicPainter.getInstance());
        }
        else if (lnf.getClass().getName().equals(MetalLookAndFeel.class.getName())) {
            switch (style) {
                case OFFICE2007_STYLE:
                case OFFICE2003_STYLE:
                case VSNET_STYLE:
                    VsnetMetalUtils.initComponentDefaults(uiDefaults);
                    VsnetMetalUtils.initClassDefaultsWithMenu(uiDefaults);
                    break;
                case ECLIPSE_STYLE:
                    EclipseMetalUtils.initComponentDefaults(uiDefaults);
                    EclipseMetalUtils.initClassDefaults(uiDefaults);
                    break;
                case ECLIPSE3X_STYLE:
                    Eclipse3xMetalUtils.initComponentDefaults(uiDefaults);
                    Eclipse3xMetalUtils.initClassDefaults(uiDefaults);
                    break;
                case VSNET_STYLE_WITHOUT_MENU:
                    VsnetMetalUtils.initComponentDefaults(uiDefaults);
                    VsnetMetalUtils.initClassDefaults(uiDefaults);
                    break;
                case XERTO_STYLE:
                case XERTO_STYLE_WITHOUT_MENU:
                    XertoMetalUtils.initComponentDefaults(uiDefaults);
                    XertoMetalUtils.initClassDefaults(uiDefaults);
                    break;
                default:
            }
        }
        else if (lnf instanceof MetalLookAndFeel) {
            switch (style) {
                case OFFICE2007_STYLE:
                case OFFICE2003_STYLE:
                case VSNET_STYLE:
                case VSNET_STYLE_WITHOUT_MENU:
                    VsnetMetalUtils.initComponentDefaults(uiDefaults);
                    VsnetMetalUtils.initClassDefaults(uiDefaults);
                    break;
                case ECLIPSE_STYLE:
                    EclipseMetalUtils.initClassDefaults(uiDefaults);
                    EclipseMetalUtils.initComponentDefaults(uiDefaults);
                    break;
                case ECLIPSE3X_STYLE:
                    Eclipse3xMetalUtils.initClassDefaults(uiDefaults);
                    Eclipse3xMetalUtils.initComponentDefaults(uiDefaults);
                    break;
                case XERTO_STYLE:
                case XERTO_STYLE_WITHOUT_MENU:
                    XertoMetalUtils.initComponentDefaults(uiDefaults);
                    XertoMetalUtils.initClassDefaults(uiDefaults);
                    break;
            }
        }
        else if (lnf instanceof WindowsLookAndFeel) {
            switch (style) {
                case OFFICE2007_STYLE:
                    VsnetWindowsUtils.initComponentDefaultsWithMenu(uiDefaults);
                    VsnetWindowsUtils.initClassDefaultsWithMenu(uiDefaults);
                    Office2003WindowsUtils.initComponentDefaults(uiDefaults);
                    Office2007WindowsUtils.initComponentDefaults(uiDefaults);
                    Office2007WindowsUtils.initClassDefaults(uiDefaults);
                    break;
                case OFFICE2003_STYLE:
                    VsnetWindowsUtils.initComponentDefaultsWithMenu(uiDefaults);
                    VsnetWindowsUtils.initClassDefaultsWithMenu(uiDefaults);
                    Office2003WindowsUtils.initClassDefaults(uiDefaults);
                    Office2003WindowsUtils.initComponentDefaults(uiDefaults);
                    break;
                case ECLIPSE_STYLE:
                    EclipseWindowsUtils.initClassDefaultsWithMenu(uiDefaults);
                    EclipseWindowsUtils.initComponentDefaultsWithMenu(uiDefaults);
                    break;
                case ECLIPSE3X_STYLE:
                    Eclipse3xWindowsUtils.initClassDefaultsWithMenu(uiDefaults);
                    Eclipse3xWindowsUtils.initComponentDefaultsWithMenu(uiDefaults);
                    break;
                case VSNET_STYLE:
                    VsnetWindowsUtils.initComponentDefaultsWithMenu(uiDefaults);
                    VsnetWindowsUtils.initClassDefaultsWithMenu(uiDefaults);
                    break;
                case VSNET_STYLE_WITHOUT_MENU:
                    VsnetWindowsUtils.initComponentDefaults(uiDefaults);
                    VsnetWindowsUtils.initClassDefaults(uiDefaults);
                    break;
                case XERTO_STYLE:
                    XertoWindowsUtils.initComponentDefaultsWithMenu(uiDefaults);
                    XertoWindowsUtils.initClassDefaultsWithMenu(uiDefaults);
                    break;
                case XERTO_STYLE_WITHOUT_MENU:
                    XertoWindowsUtils.initComponentDefaults(uiDefaults);
                    XertoWindowsUtils.initClassDefaults(uiDefaults);
                    break;
            }
        }
        // For Mac only
        else if (((isLnfInUse(AQUA_LNF) || isLnfInUse(AQUA_LNF_6)) && isAquaLnfInstalled())
                || (isLnfInUse(QUAQUA_LNF) && isQuaquaLnfInstalled())) {
            // use reflection since we don't deliver source code of AquaJideUtils as most users don't compile it on Mac OS X
            try {
                Class<?> aquaJideUtils = getUIManagerClassLoader().loadClass("com.jidesoft.plaf.aqua.AquaJideUtils");
                aquaJideUtils.getMethod("initComponentDefaults", UIDefaults.class).invoke(null, uiDefaults);
                aquaJideUtils.getMethod("initClassDefaults", UIDefaults.class).invoke(null, uiDefaults);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                JideSwingUtilities.throwInvocationTargetException(e);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            // built in initializer
            if (isGTKLnfInstalled() && isLnfInUse(GTK_LNF)) {
                new GTKInitializer().initialize(uiDefaults);
            }
            else if (isSyntheticaLnfInstalled()
                    && (lnf.getClass().getName().startsWith(SYNTHETICA_LNF_PREFIX) || isLnfInUse(SYNTHETICA_LNF))) {
                new SyntheticaInitializer().initialize(uiDefaults);
            }
            else if (isNimbusLnfInstalled() && lnf.getClass().getName().indexOf(NIMBUS_LNF_NAME) != -1) {
                new NimbusInitializer().initialize(uiDefaults);
            }

            switch (style) {
                case OFFICE2007_STYLE:
                    if (SystemInfo.isWindows()) {
                        VsnetWindowsUtils.initComponentDefaultsWithMenu(uiDefaults);
                        Office2003WindowsUtils.initComponentDefaults(uiDefaults);
                        Office2007WindowsUtils.initComponentDefaults(uiDefaults);
                        Office2007WindowsUtils.initClassDefaults(uiDefaults);
                    }
                    else {
                        VsnetMetalUtils.initComponentDefaults(uiDefaults);
                        VsnetMetalUtils.initClassDefaults(uiDefaults);
                    }
                    break;
                case OFFICE2003_STYLE:
                    if (SystemInfo.isWindows()) {
                        VsnetWindowsUtils.initComponentDefaultsWithMenu(uiDefaults);
                        Office2003WindowsUtils.initComponentDefaults(uiDefaults);
                        Office2003WindowsUtils.initClassDefaults(uiDefaults);
                    }
                    else {
                        VsnetMetalUtils.initComponentDefaults(uiDefaults);
                        VsnetMetalUtils.initClassDefaults(uiDefaults);
                    }
                    break;
                case ECLIPSE_STYLE:
                    if (SystemInfo.isWindows()) {
                        EclipseWindowsUtils.initClassDefaultsWithMenu(uiDefaults);
                        EclipseWindowsUtils.initComponentDefaultsWithMenu(uiDefaults);
                    }
                    else {
                        EclipseMetalUtils.initClassDefaults(uiDefaults);
                        EclipseMetalUtils.initComponentDefaults(uiDefaults);
                    }
                    break;
                case ECLIPSE3X_STYLE:
                    if (SystemInfo.isWindows()) {
                        Eclipse3xWindowsUtils.initClassDefaultsWithMenu(uiDefaults);
                        Eclipse3xWindowsUtils.initComponentDefaultsWithMenu(uiDefaults);
                    }
                    else {
                        Eclipse3xMetalUtils.initClassDefaults(uiDefaults);
                        Eclipse3xMetalUtils.initComponentDefaults(uiDefaults);
                    }
                    break;
                case VSNET_STYLE:
                    if (SystemInfo.isWindows()) {
                        VsnetWindowsUtils.initClassDefaultsWithMenu(uiDefaults);
                        VsnetWindowsUtils.initComponentDefaultsWithMenu(uiDefaults);
                    }
                    else {
                        VsnetMetalUtils.initComponentDefaults(uiDefaults);
                        VsnetMetalUtils.initClassDefaults(uiDefaults);
                    }
                    break;
                case VSNET_STYLE_WITHOUT_MENU:
                    if (SystemInfo.isWindows()) {
                        VsnetWindowsUtils.initClassDefaults(uiDefaults);
                        VsnetWindowsUtils.initComponentDefaults(uiDefaults);
                    }
                    else {
                        VsnetMetalUtils.initComponentDefaults(uiDefaults);
                        VsnetMetalUtils.initClassDefaults(uiDefaults);
                    }
                    break;
                case XERTO_STYLE:
                    if (SystemInfo.isWindows()) {
                        XertoWindowsUtils.initClassDefaultsWithMenu(uiDefaults);
                        XertoWindowsUtils.initComponentDefaultsWithMenu(uiDefaults);
                    }
                    else {
                        XertoMetalUtils.initComponentDefaults(uiDefaults);
                        XertoMetalUtils.initClassDefaults(uiDefaults);
                    }
                    break;
                case XERTO_STYLE_WITHOUT_MENU:
                    if (SystemInfo.isWindows()) {
                        XertoWindowsUtils.initClassDefaults(uiDefaults);
                        XertoWindowsUtils.initComponentDefaults(uiDefaults);
                    }
                    else {
                        XertoMetalUtils.initComponentDefaults(uiDefaults);
                        XertoMetalUtils.initClassDefaults(uiDefaults);
                    }
                    break;
            }

            // built in customizer
            if (lnf.getClass().getName().startsWith(SYNTHETICA_LNF_PREFIX) || isLnfInUse(SYNTHETICA_LNF)) {
                new SyntheticaCustomizer().customize(uiDefaults);
            }
        }

        uiDefaults.put(JIDE_EXTENSION_INSTALLLED, Boolean.TRUE);

        UIDefaultsCustomizer[] customizers = getUIDefaultsCustomizers();
        for (UIDefaultsCustomizer customizer : customizers) {
            if (customizer != null) {
                customizer.customize(uiDefaults);
            }
        }
    }

    /**
     * Returns whether or not the L&F is in classpath.
     *
     * @param lnfName the L&F name.
     * @return <tt>true</tt> if the L&F is in classpath, <tt>false</tt> otherwise
     */
    public static boolean isLnfInstalled(String lnfName) {
        String installed = _installedLookAndFeels.get(lnfName);
        if (installed != null) {
            return LAF_INSTALLED.equals(installed);
        }
        return loadLnfClass(lnfName) != null;
    }

    public static ClassLoader getUIManagerClassLoader() {
        Object cl = UIManager.get("ClassLoader");
        if (cl instanceof ClassLoader) {
            return (ClassLoader) cl;
        }
        ClassLoader classLoader = LookAndFeelFactory.class.getClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        return classLoader;
    }

    /**
     * Checks if the L&F is the L&F or a subclass of the L&F.
     *
     * @param lnfName the L&F name.
     * @return true or false.
     */
    public static boolean isLnfInUse(String lnfName) {
        return !(_installedLookAndFeels.containsKey(lnfName)
                && (_installedLookAndFeels.get(lnfName) == null || _installedLookAndFeels.get(lnfName).equals(LAF_NOT_INSTALLED)))
                && isAssignableFrom(lnfName, UIManager.getLookAndFeel().getClass());
    }

    /**
     * Tells the LookAndFeelFactory whether a L&F is installed. We will try to instantiate the class when {@link
     * #isLnfInstalled(String)} is called to determine if the class is in the class path. However you can call this
     * method to tell if the L&F is available without us instantiating the class.
     *
     * @param lnfName   the L&F name.
     * @param installed true or false.
     */
    public static void setLnfInstalled(String lnfName, boolean installed) {
        _installedLookAndFeels.put(lnfName, installed ? LAF_INSTALLED : LAF_NOT_INSTALLED);
    }

    private static Class loadLnfClass(String lnfName) {
        try {
            Class clazz = getUIManagerClassLoader().loadClass(lnfName);
            Map<String, String> map = new HashMap<String, String>(_installedLookAndFeels);
            map.put(lnfName, LAF_INSTALLED);
            _installedLookAndFeels = map;
            return clazz;
        }
        catch (ClassNotFoundException e) {
            Map<String, String> map = new HashMap<String, String>(_installedLookAndFeels);
            map.put(lnfName, LAF_NOT_INSTALLED);
            _installedLookAndFeels = map;
            return null;
        }
    }

    private static boolean isAssignableFrom(String lnfName, Class cls) {
        if (lnfName.equals(cls.getName())) {
            return true;
        }
        Class cl = loadLnfClass(lnfName);
        return cl != null && cl.isAssignableFrom(cls);
    }

    /**
     * Returns whether or not the Aqua L&F is in classpath.
     *
     * @return <tt>true</tt> if aqua L&F is in classpath, <tt>false</tt> otherwise
     */
    public static boolean isAquaLnfInstalled() {
        return isLnfInstalled(AQUA_LNF) || isLnfInstalled(AQUA_LNF_6);
    }


    /**
     * Returns whether or not the Quaqua L&F is in classpath.
     *
     * @return <tt>true</tt> if Quaqua L&F is in classpath, <tt>false</tt> otherwise
     */
    public static boolean isQuaquaLnfInstalled() {
        return isLnfInstalled(QUAQUA_LNF);
    }

    /**
     * Returns whether alloy L&F is in classpath
     *
     * @return <tt>true</tt> alloy L&F is in classpath, <tt>false</tt> otherwise
     */
    public static boolean isAlloyLnfInstalled() {
        return isLnfInstalled(ALLOY_LNF);
    }

    /**
     * Returns whether GTK L&F is in classpath
     *
     * @return <tt>true</tt> GTK L&F is in classpath, <tt>false</tt> otherwise
     */
    public static boolean isGTKLnfInstalled() {
        return isLnfInstalled(GTK_LNF);
    }

    /**
     * Returns whether Plastic3D L&F is in classpath
     *
     * @return <tt>true</tt> Plastic3D L&F is in classpath, <tt>false</tt> otherwise
     */
    public static boolean isPlastic3DLnfInstalled() {
        return isLnfInstalled(PLASTIC3D_LNF);
    }

    /**
     * Returns whether Plastic3D L&F is in classpath
     *
     * @return <tt>true</tt> Plastic3D L&F is in classpath, <tt>false</tt> otherwise
     */
    public static boolean isPlastic3D13LnfInstalled() {
        return isLnfInstalled(PLASTIC3D_LNF_1_3);
    }

    /**
     * Returns whether PlasticXP L&F is in classpath
     *
     * @return <tt>true</tt> Plastic3D L&F is in classpath, <tt>false</tt> otherwise
     */
    public static boolean isPlasticXPLnfInstalled() {
        return isLnfInstalled(PLASTICXP_LNF);
    }

    /**
     * Returns whether Tonic L&F is in classpath
     *
     * @return <tt>true</tt> Tonic L&F is in classpath, <tt>false</tt> otherwise
     */
    public static boolean isTonicLnfInstalled() {
        return isLnfInstalled(TONIC_LNF);
    }

    /**
     * Returns whether A03 L&F is in classpath
     *
     * @return <tt>true</tt> A03 L&F is in classpath, <tt>false</tt> otherwise
     */
    public static boolean isA03LnfInstalled() {
        return isLnfInstalled(A03_LNF);
    }

    /**
     * Returns whether or not the Pgs L&F is in classpath.
     *
     * @return <tt>true</tt> if pgs L&F is in classpath, <tt>false</tt> otherwise
     */
    public static boolean isPgsLnfInstalled() {
        return isLnfInstalled(PGS_LNF);
    }

    /*
     * Returns whether or not the Substance L&F is in classpath.
     *
     * @return <tt>true</tt> if Substance L&F is in classpath, <tt>false</tt> otherwise
     */
//    public static boolean isSubstanceLnfInstalled() {
//        return isLnfInstalled(SUBSTANCE_LNF);
//    }

    /**
     * Returns whether or not the Synthetica L&F is in classpath.
     *
     * @return <tt>true</tt> if Synthetica L&F is in classpath, <tt>false</tt> otherwise
     */
    public static boolean isSyntheticaLnfInstalled() {
        return isLnfInstalled(SYNTHETICA_LNF);
    }

    /**
     * Returns whether or not the Nimbus L&F is in classpath.
     *
     * @return <tt>true</tt> if Nimbus L&F is in classpath, <tt>false</tt> otherwise
     */
    public static boolean isNimbusLnfInstalled() {
        UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo info : infos) {
            if (info.getClassName().indexOf(NIMBUS_LNF_NAME) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Install the default L&F. In this method, we will look at system property "swing.defaultlaf" first. If the value
     * is set and it's not an instance of Synth L&F, we will use it. Otherwise, we will use Metal L&F is OS is Linux or
     * UNIX and use UIManager.getSystemLookAndFeelClassName() for other OS. In addition, we will add JIDE extension to
     * it.
     */
    public static void installDefaultLookAndFeelAndExtension() {
        installDefaultLookAndFeel();
        // to add additional UIDefault for JIDE components
        LookAndFeelFactory.installJideExtension();
    }

    /**
     * Install the default L&F. In this method, we will look at system property "swing.defaultlaf" first. If the value
     * is set and it's not an instance of Synth L&F, we will use it. Otherwise, we will use Metal L&F is OS is Linux or
     * UNIX and use UIManager.getSystemLookAndFeelClassName() for other OS.
     */
    public static void installDefaultLookAndFeel() {
        try {
            String lnfName = SecurityUtils.getProperty("swing.defaultlaf", null);
            if (lnfName == null) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            else {
                UIManager.setLookAndFeel(lnfName);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets current L&F.
     *
     * @return the current L&F.
     */
    public static LookAndFeel getLookAndFeel() {
        return _lookAndFeel;
    }

    /**
     * Gets current style.
     *
     * @return the current style.
     */
    public static int getStyle() {
        return _style;
    }

    /**
     * Gets all UIDefaults customizers.
     *
     * @return an array of UIDefaults customizers.
     */
    public static UIDefaultsCustomizer[] getUIDefaultsCustomizers() {
        return _uiDefaultsCustomizers.toArray(new UIDefaultsCustomizer[_uiDefaultsCustomizers.size()]);
    }

    /**
     * Adds your own UIDefaults customizer. You need to add it before installJideExtension() is called but the actual
     * customize() code will be called after installJideExtension() is called.
     * <code><pre>
     * For example, we use "JideButton.font" as the UIDefault for the JideButton font. If you want
     * to use another font, you can do
     * LookAndFeelFactory.addUIDefaultsCustomizer(new LookAndFeelFactory.UIDefaultsCustomizer() {
     *     public void customize(UIDefaults defaults) {
     *         defaults.put("JideButton.font", whateverFont);
     *     }
     * });
     * LookAndFeelFactory.installJideExtension();
     * </pre></code>
     *
     * @param uiDefaultsCustomizer the UIDefaultsCustomizer
     */
    public static void addUIDefaultsCustomizer(UIDefaultsCustomizer uiDefaultsCustomizer) {
        if (!_uiDefaultsCustomizers.contains(uiDefaultsCustomizer)) {
            _uiDefaultsCustomizers.add(uiDefaultsCustomizer);
        }
    }

    /**
     * Removes an existing UIDefaults customizer you added before.
     *
     * @param uiDefaultsCustomizer the UIDefaultsCustomizer
     */
    public static void removeUIDefaultsCustomizer(UIDefaultsCustomizer uiDefaultsCustomizer) {
        _uiDefaultsCustomizers.remove(uiDefaultsCustomizer);
    }

    /**
     * Gets all UIDefaults initializers.
     *
     * @return an array of UIDefaults initializers.
     */
    public static UIDefaultsInitializer[] getUIDefaultsInitializers() {
        return _uiDefaultsInitializers.toArray(new UIDefaultsInitializer[_uiDefaultsInitializers.size()]);
    }

    /**
     * Adds your own UIDefaults initializer. This initializer will be called before installJideExtension() is called.
     * <p/>
     * Here is how you use it. For example, we use the color of UIDefault "activeCaption" to get the active title color
     * which we will use for active title bar color in JIDE components. If the L&F you are using doesn't set this
     * UIDefault, we might throw NPE later in the code. To avoid this, you call
     * <code><pre>
     * LookAndFeelFactory.addUIDefaultsInitializer(new LookAndFeelFactory.UIDefaultsInitializer() {
     *     public void initialize(UIDefaults defaults) {
     *         defaults.put("activeCaption", whateverColor);
     *     }
     * });
     * UIManager.setLookAndFeel(...); // set whatever L&F
     * LookAndFeelFactory.installJideExtension(); // install the UIDefaults needed by the JIDE
     * components
     * </pre></code>
     *
     * @param uiDefaultsInitializer the UIDefaultsInitializer.
     */
    public static void addUIDefaultsInitializer(UIDefaultsInitializer uiDefaultsInitializer) {
        if (!_uiDefaultsInitializers.contains(uiDefaultsInitializer)) {
            _uiDefaultsInitializers.add(uiDefaultsInitializer);
        }
    }

    /**
     * Removes an existing UIDefaults initializer you added before.
     *
     * @param uiDefaultsInitializer the UIDefaultsInitializer
     */
    public static void removeUIDefaultsInitializer(UIDefaultsInitializer uiDefaultsInitializer) {
        _uiDefaultsInitializers.remove(uiDefaultsInitializer);
    }

    public static class GTKInitializer implements UIDefaultsInitializer {
        public void initialize(UIDefaults defaults) {
            ImageIcon rightImageIcon = IconsFactory.createMaskImage(new JLabel(), JideIconsFactory.getImageIcon(JideIconsFactory.Arrow.RIGHT), Color.BLACK, Color.GRAY);
            ImageIcon downImageIcon = IconsFactory.createMaskImage(new JLabel(), JideIconsFactory.getImageIcon(JideIconsFactory.Arrow.DOWN), Color.BLACK, Color.GRAY);
            Object[] uiDefaults = {
                    "activeCaption", defaults.getColor("textHighlight"),
                    "activeCaptionText", defaults.getColor("textHighlightText"),
                    "inactiveCaptionBorder", defaults.getColor("controlShadowtextHighlightText"),
                    "CategorizedTable.categoryCollapsedIcon", rightImageIcon,
                    "CategorizedTable.categoryExpandedIcon", downImageIcon,
                    "CategorizedTable.collapsedIcon", rightImageIcon,
                    "CategorizedTable.expandedIcon", downImageIcon,
            };
            putDefaults(defaults, uiDefaults);
        }
    }

    public static class SyntheticaInitializer implements UIDefaultsInitializer {
        public void initialize(UIDefaults defaults) {
            Object[] uiDefaults = {
                    "Label.font", UIDefaultsLookup.getFont("Button.font"),
                    "ToolBar.font", UIDefaultsLookup.getFont("Button.font"),
                    "MenuItem.acceleratorFont", UIDefaultsLookup.getFont("Button.font"),
                    "ComboBox.disabledForeground", defaults.get("Synthetica.comboBox.disabled.textColor"),
                    "ComboBox.disabledBackground", defaults.get("Synthetica.comboBox.disabled.backgroundColor"),
                    "Slider.focusInsets", new InsetsUIResource(0, 0, 0, 0),
            };
            putDefaults(defaults, uiDefaults);
        }
    }

    //TODO: Miss SubstanceCustomizer here

    public static class SyntheticaCustomizer implements UIDefaultsCustomizer {
        @SuppressWarnings({"ConstantConditions"})
        public void customize(UIDefaults defaults) {
            try {
                Class syntheticaClass = Class.forName(SYNTHETICA_LNF);
                Class syntheticaFrameBorder = Class.forName("com.jidesoft.plaf.synthetica.SyntheticaFrameBorder");
                Color toolbarBackground = new JToolBar().getBackground();
                int products = LookAndFeelFactory.getProductsUsed();
                {
                    Object[] uiDefaults = {
                            "JideTabbedPaneUI", "com.jidesoft.plaf.synthetica.SyntheticaJideTabbedPaneUI",
                            "JideSplitPane.dividerSize", 6,
                            "JideTabbedPane.tabAreaBackground", UIManager.getColor("control"),
                            "JideTabbedPane.background", UIManager.getColor("control"),
                            "JideTabbedPane.defaultTabShape", JideTabbedPane.SHAPE_ROUNDED_VSNET,
                            "JideTabbedPane.defaultTabShape", JideTabbedPane.SHAPE_ROUNDED_VSNET,
                            "JideTabbedPane.contentBorderInsets", new InsetsUIResource(2, 2, 2, 2),
                            "JideButton.foreground", UIDefaultsLookup.getColor("Button.foreground"),
                            "JideSplitButton.foreground", UIDefaultsLookup.getColor("Button.foreground"),
                            "Icon.floating", Boolean.FALSE,
                            "ContentContainer.background", toolbarBackground,
                    };
                    overwriteDefaults(defaults, uiDefaults);
                }

                if ((products & PRODUCT_COMPONENTS) != 0) {
                    Object[] uiDefaults = {
                            "CollapsiblePane.background", UIDefaultsLookup.getColor("TaskPane.borderColor"),
                            "CollapsiblePane.emphasizedBackground", UIDefaultsLookup.getColor("TaskPane.borderColor"),
                            "CollapsiblePane.foreground", UIDefaultsLookup.getColor("TaskPane.titleForeground"),
                            "CollapsiblePane.emphasizedForeground", UIDefaultsLookup.getColor("TaskPane.specialTitleForeground"),
                            "StatusBarItem.border", new BorderUIResource(BorderFactory.createEmptyBorder(2, 2, 2, 2)),
                            "StatusBar.childrenOpaque", false,

                            "OutlookTabbedPane.buttonStyle", JideButton.TOOLBAR_STYLE,
                            "FloorTabbedPane.buttonStyle", JideButton.TOOLBAR_STYLE,
                    };
                    overwriteDefaults(defaults, uiDefaults);
                }

                if ((products & PRODUCT_GRIDS) != 0) {
                    Object[] uiDefaults = {
                            "NestedTableHeaderUI", "com.jidesoft.plaf.synthetica.SyntheticaNestedTableHeaderUI",
                            "EditableTableHeaderUI", "com.jidesoft.plaf.synthetica.SyntheticaEditableTableHeaderUI",
                    };
                    overwriteDefaults(defaults, uiDefaults);
                }

                if ((products & PRODUCT_ACTION) != 0) {
                    Object[] uiDefaults = {
                            "CommandBar.background", toolbarBackground,
                            "CommandBar.border", new BorderUIResource(BorderFactory.createEmptyBorder()),
                            "CommandBar.borderVert", new BorderUIResource(BorderFactory.createEmptyBorder()),
                            "CommandBar.borderFloating", syntheticaFrameBorder.newInstance(),
                            "CommandBar.titleBarBackground", UIDefaultsLookup.getColor("InternalFrame.activeTitleBackground"),
                            "CommandBar.titleBarForeground", UIDefaultsLookup.getColor("InternalFrame.activeTitleForeground"),
                            "CommandBarContainer.verticalGap", 0,
                    };
                    overwriteDefaults(defaults, uiDefaults);
                }

                if ((products & PRODUCT_DOCK) != 0) {
                    Object[] uiDefaults = {
                            "Workspace.background", UIManager.getColor("control"),

                            "DockableFrame.inactiveTitleForeground", UIDefaultsLookup.getColor("Synthetica.docking.titlebar.color"),
                            "DockableFrame.activeTitleForeground", UIDefaultsLookup.getColor("Synthetica.docking.titlebar.color.selected"),
                            "DockableFrame.titleBorder", UIDefaultsLookup.getColor("Synthetica.docking.border.color"),
                            "FrameContainer.contentBorderInsets", new InsetsUIResource(2, 2, 2, 2),

                            "DockableFrameTitlePane.hideIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.close")),
                            "DockableFrameTitlePane.hideRolloverIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.close.hover")),
                            "DockableFrameTitlePane.hideActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.close")),
                            "DockableFrameTitlePane.hideRolloverActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.close.hover")),

                            "DockableFrameTitlePane.floatIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.undock")),
                            "DockableFrameTitlePane.floatRolloverIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.undock.hover")),
                            "DockableFrameTitlePane.floatActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.undock")),
                            "DockableFrameTitlePane.floatRolloverActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.undock.hover")),

                            "DockableFrameTitlePane.unfloatIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.dock")),
                            "DockableFrameTitlePane.unfloatRolloverIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.dock.hover")),
                            "DockableFrameTitlePane.unfloatActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.dock")),
                            "DockableFrameTitlePane.unfloatRolloverActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.dock.hover")),

                            "DockableFrameTitlePane.autohideIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.iconify")),
                            "DockableFrameTitlePane.autohideRolloverIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.iconify.hover")),
                            "DockableFrameTitlePane.autohideActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.iconify")),
                            "DockableFrameTitlePane.autohideRolloverActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.iconify.hover")),

                            "DockableFrameTitlePane.stopAutohideIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.restore")),
                            "DockableFrameTitlePane.stopAutohideRolloverIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.restore.hover")),
                            "DockableFrameTitlePane.stopAutohideActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.restore")),
                            "DockableFrameTitlePane.stopAutohideRolloverActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.restore.hover")),

                            "DockableFrameTitlePane.hideAutohideIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.iconify")),
                            "DockableFrameTitlePane.hideAutohideRolloverIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.iconify.hover")),
                            "DockableFrameTitlePane.hideAutohideActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.iconify")),
                            "DockableFrameTitlePane.hideAutohideRolloverActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.iconify.hover")),

                            "DockableFrameTitlePane.maximizeIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.maximize")),
                            "DockableFrameTitlePane.maximizeRolloverIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.maximize.hover")),
                            "DockableFrameTitlePane.maximizeActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.maximize")),
                            "DockableFrameTitlePane.maximizeRolloverActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.maximize.hover")),

                            "DockableFrameTitlePane.restoreIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.restore")),
                            "DockableFrameTitlePane.restoreRolloverIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.restore.hover")),
                            "DockableFrameTitlePane.restoreActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.restore")),
                            "DockableFrameTitlePane.restoreRolloverActiveIcon", loadSyntheticaIcon(syntheticaClass, ("Synthetica.docking.titlebar.active.restore.hover")),

                            "DockableFrameTitlePane.use3dButtons", Boolean.FALSE,
                            "DockableFrameTitlePane.contentFilledButtons", Boolean.FALSE,
                            "DockableFrameTitlePane.buttonGap", 0,
                    };
                    overwriteDefaults(defaults, uiDefaults);
                }
                Class<?> painterClass = Class.forName("com.jidesoft.plaf.synthetica.SyntheticaJidePainter");
                Method getInstanceMethod = painterClass.getMethod("getInstance");
                Object painter = getInstanceMethod.invoke(null);
                UIDefaultsLookup.put(UIManager.getDefaults(), "Theme.painter", painter);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Icon loadSyntheticaIcon(Class syntheticaClass, String key) {
        try {
            Method method = syntheticaClass.getMethod("loadIcon", String.class);
            return (Icon) method.invoke(null, key);
        }
        catch (Exception e) {
            return IconsFactory.getImageIcon(syntheticaClass, UIDefaultsLookup.getString(key));
        }
    }

    public static class NimbusInitializer implements UIDefaultsInitializer {
        public void initialize(UIDefaults defaults) {
            Object marginBorder = new SwingLazyValue(
                    "javax.swing.plaf.basic.BasicBorders$MarginBorder");

            Object[] uiDefaults = {
                    "textHighlight", new ColorUIResource(197, 218, 233),
                    "controlText", new ColorUIResource(Color.BLACK),
                    "activeCaptionText", new ColorUIResource(Color.BLACK),
                    "MenuItem.acceleratorFont", new FontUIResource("Arial", Font.PLAIN, 12),
                    "ComboBox.background", new ColorUIResource(Color.WHITE),
                    "ComboBox.disabledForeground", new ColorUIResource(Color.DARK_GRAY),
                    "ComboBox.disabledBackground", new ColorUIResource(Color.GRAY),

                    "activeCaption", new ColorUIResource(197, 218, 233),
                    "inactiveCaption", new ColorUIResource(Color.DARK_GRAY),
                    "control", new ColorUIResource(220, 223, 228),
                    "controlLtHighlight", new ColorUIResource(Color.WHITE),
                    "controlHighlight", new ColorUIResource(Color.LIGHT_GRAY),
                    "controlShadow", new ColorUIResource(133, 137, 144),
                    "controlDkShadow", new ColorUIResource(Color.BLACK),
                    "MenuItem.background", new ColorUIResource(237, 239, 242),
                    "SplitPane.background", new ColorUIResource(220, 223, 228),
                    "Tree.hash", new ColorUIResource(Color.GRAY),

                    "TextField.foreground", new ColorUIResource(Color.BLACK),
                    "TextField.inactiveForeground", new ColorUIResource(Color.BLACK),
                    "TextField.selectionForeground", new ColorUIResource(Color.WHITE),
                    "TextField.selectionBackground", new ColorUIResource(197, 218, 233),
                    "Table.gridColor", new ColorUIResource(Color.BLACK),
                    "TextField.background", new ColorUIResource(Color.WHITE),

                    "Table.selectionBackground", defaults.getColor("Tree.selectionBackground"),
                    "Table.selectionForeground", defaults.getColor("Tree.selectionForeground"),

                    "Menu.border", marginBorder,
                    "MenuItem.border", marginBorder,
                    "CheckBoxMenuItem.border", marginBorder,
                    "RadioButtonMenuItem.border", marginBorder,
            };
            putDefaults(defaults, uiDefaults);
        }
    }

    @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
    public static void verifyDefaults(UIDefaults table, Object[] keyValueList) {
        for (int i = 0, max = keyValueList.length; i < max; i += 2) {
            Object value = keyValueList[i + 1];
            if (value == null) {
                System.out.println("The value for " + keyValueList[i] + " is null");
            }
            else {
                Object oldValue = table.get(keyValueList[i]);
                if (oldValue != null) {
                    System.out.println("The value for " + keyValueList[i] + " exists which is " + oldValue);
                }
            }
        }
    }

    /**
     * Puts a list of UIDefault to the UIDefaults table. The keyValueList is an array with a key and value in pair. If
     * the value is null, this method will remove the key from the table. If the table already has a value for the key,
     * the new value will be ignored. This is the difference from {@link #putDefaults(javax.swing.UIDefaults,Object[])}
     * method. You should use this method in {@link UIDefaultsInitializer} so that it fills in the UIDefault value only
     * when it is missing.
     *
     * @param table         the ui defaults table
     * @param keyValueArray the key value array. It is in the format of a key followed by a value.
     */
    public static void putDefaults(UIDefaults table, Object[] keyValueArray) {
        for (int i = 0, max = keyValueArray.length; i < max; i += 2) {
            Object value = keyValueArray[i + 1];
            if (value == null) {
                table.remove(keyValueArray[i]);
            }
            else {
                if (table.get(keyValueArray[i]) == null) {
                    table.put(keyValueArray[i], value);
                }
            }
        }
    }

    /**
     * Puts a list of UIDefault to the UIDefaults table. The keyValueList is an array with a key and value in pair. If
     * the value is null, this method will remove the key from the table. Otherwise, it will put the new value in even
     * if the table already has a value for the key. This is the difference from {@link
     * #putDefaults(javax.swing.UIDefaults,Object[])} method. You should use this method in {@link UIDefaultsCustomizer}
     * because you always want to override the existing value using the new value.
     *
     * @param table         the ui defaults table
     * @param keyValueArray the key value array. It is in the format of a key followed by a value.
     */
    public static void overwriteDefaults(UIDefaults table, Object[] keyValueArray) {
        for (int i = 0, max = keyValueArray.length; i < max; i += 2) {
            Object value = keyValueArray[i + 1];
            if (value == null) {
                table.remove(keyValueArray[i]);
            }
            else {
                table.put(keyValueArray[i], value);
            }
        }
    }

    private static int _productsUsed = -1;

    public static int getProductsUsed() {
        if (_productsUsed == -1) {
            _productsUsed = 0;
            try {
                Class.forName("com.jidesoft.docking.Product");
                _productsUsed |= PRODUCT_DOCK;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.action.Product");
                _productsUsed |= PRODUCT_ACTION;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.document.Product");
                _productsUsed |= PRODUCT_COMPONENTS;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.grid.Product");
                _productsUsed |= PRODUCT_GRIDS;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.wizard.Product");
                _productsUsed |= PRODUCT_DIALOGS;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.pivot.Product");
                _productsUsed |= PRODUCT_PIVOT;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.shortcut.Product");
                _productsUsed |= PRODUCT_SHORTCUT;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.editor.Product");
                _productsUsed |= PRODUCT_CODE_EDITOR;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.rss.Product");
                _productsUsed |= PRODUCT_FEEDREADER;
            }
            catch (Throwable e) {
                //
            }
        }
        return _productsUsed;
    }

    /**
     * Sets the products you will use. This is needed so that LookAndFeelFactory knows what UIDefault to initialize. For
     * example, if you use only JIDE Docking Framework and JIDE Grids, you should call
     * <code>setProductUsed(ProductNames.PRODUCT_DOCK | ProductNames.PRODUCT_GRIDS)</code> so that we don't initialize
     * UIDefaults needed by any other products. If you use this class as part of JIDE Common Layer open source project,
     * you should call <code>setProductUsed(ProductNames.PRODUCT_COMMON)</code>. If you want to use all JIDE products,
     * you should call <code>setProductUsed(ProductNames.PRODUCT_ALL)</code>
     *
     * @param productsUsed a bit-wise OR of product values defined in {@link com.jidesoft.utils.ProductNames}.
     */
    public static void setProductsUsed(int productsUsed) {
        _productsUsed = productsUsed;
    }

    /**
     * Checks if the current L&F uses decorated frames.
     *
     * @return true if the current L&F uses decorated frames. Otherwise false.
     */
    public static boolean isCurrentLnfDecorated() {
        return !isLnfInUse(SYNTHETICA_LNF);
    }

    public static void main(String[] args) {
//        LookAndFeelFactory.setLnfInstalled(AQUA_LNF, false);
        System.out.println(LookAndFeelFactory.isLnfInstalled(AQUA_LNF));
    }
}
