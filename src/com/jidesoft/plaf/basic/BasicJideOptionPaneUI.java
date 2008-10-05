/*
 * @(#)BasicJideOptionPaneUI.java 3/27/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.basic;

import com.jidesoft.dialog.ButtonNames;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.ButtonResources;
import com.jidesoft.dialog.JideOptionPane;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.NullPanel;
import com.jidesoft.swing.PaintPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class BasicJideOptionPaneUI extends BasicOptionPaneUI {
    private Container _detailsArea;
    private Container _buttonArea;
    private Container _bannerArea;
    private Component _titleComponent;

    private ThemePainter _painter;

    // keep track of detail component width.
    private int _detailsPreferredWidth = -1;

    /**
     * Creates a new BasicOptionPaneUI instance.
     */
    public static ComponentUI createUI(JComponent x) {
        return new BasicJideOptionPaneUI();
    }

    @Override
    protected LayoutManager createLayoutManager() {
        return new JideBoxLayout(optionPane, JideBoxLayout.Y_AXIS);
    }

    // use a static to keep track of detail area visibility during the session
    private static boolean _detailsVisible = false;

    /**
     * Is details area visible initially.
     *
     * @return true if details area is visible.
     */
    public static boolean isDetailsVisible() {
        return _detailsVisible;
    }

    /**
     * Sets if details area is visible initially.
     *
     * @param detailsVisible
     */
    public static void setDetailsVisible(boolean detailsVisible) {
        _detailsVisible = detailsVisible;
    }

    @Override
    protected void installComponents() {
        if (UIDefaultsLookup.get("OptionPane.showBanner") == null || UIDefaultsLookup.getBoolean("OptionPane.showBanner")) {
            optionPane.add(_bannerArea = createBannerArea(), JideBoxLayout.FIX);
        }

        Container messageArea = createMessageArea();
        LookAndFeel.installBorder((JComponent) messageArea, "OptionPane.border");
        optionPane.add(messageArea);

        Container separator = createSeparator();
        if (separator != null) {
            optionPane.add(separator);
        }

        optionPane.add(_buttonArea = createButtonArea(), JideBoxLayout.FIX);
        optionPane.applyComponentOrientation(optionPane.getComponentOrientation());

        if (shouldDetailsButtonVisible()) {
            updateDetailsComponent();
        }

        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (JideOptionPane.DETAILS_PROPERTY.equals(evt.getPropertyName())) {
                    updateDetailsComponent();
                    if (_buttonArea instanceof ButtonPanel) {
                        Component detailsButton = ((ButtonPanel) _buttonArea).getButtonByName(ButtonNames.DETAILS);
                        if (detailsButton != null) {
                            detailsButton.setVisible(evt.getNewValue() != null);
                        }
                    }
                }
                else if (JideOptionPane.TITLE_PROPERTY.equals(evt.getPropertyName())) {
                    updateTitleComponent(_bannerArea);
                }
            }
        });
    }

    protected void updateDetailsComponent() {
        if (_detailsArea != null) {
            optionPane.remove(_detailsArea);
            _detailsArea = null;
        }
        _detailsArea = createDetailsComponent();
        if (_detailsArea != null) {
            optionPane.add(_detailsArea, JideBoxLayout.VARY);
            _detailsArea.setVisible(isDetailsVisible());
        }
    }

    @Override
    protected Container createMessageArea() {
        JPanel top = new JPanel();
        Border topBorder = (Border) UIDefaultsLookup.get("OptionPane.messageAreaBorder");
        if (topBorder != null) {
            top.setBorder(topBorder);
        }
        top.setLayout(new BorderLayout());

        /* Fill the body. */
        Container body = new JPanel(new GridBagLayout());
        Container realBody = new JPanel(new BorderLayout());

        body.setName("OptionPane.body");
        realBody.setName("OptionPane.realBody");

        if (getIcon() != null) {
            JPanel sep = new JPanel();
            sep.setName("OptionPane.separator");
            sep.setPreferredSize(new Dimension(15, 1));
            realBody.add(sep, BorderLayout.BEFORE_LINE_BEGINS);
        }
        realBody.add(body, BorderLayout.CENTER);

        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = cons.gridy = 0;
        cons.gridwidth = GridBagConstraints.REMAINDER;
        cons.gridheight = 1;
        int anchor = UIDefaultsLookup.getInt("OptionPane.messageAnchor");
        cons.anchor = anchor == 0 ? GridBagConstraints.CENTER : anchor;
        cons.insets = new Insets(0, 0, 3, 0);

        Object message = getMessage();
//        if (message instanceof String
//                && !((String) message).toLowerCase().startsWith("<html>")) {
//            addMessageComponents(body, cons, null, getMaxCharactersPerLineCount(), false);
//        }
//        else {
        addMessageComponents(body, cons, message, getMaxCharactersPerLineCount(), false);
//        }
        top.add(realBody, BorderLayout.CENTER);

        return top;
    }

    @Override
    protected Container createSeparator() {
        return new JSeparator();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        optionPane.setBorder(BorderFactory.createEmptyBorder());
        _painter = (ThemePainter) UIDefaultsLookup.get("Theme.painter");
    }

    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
        _painter = null;
    }

    protected Container createDetailsComponent() {
        if (!(optionPane instanceof JideOptionPane)) {
            return null;
        }
        JideOptionPane jideOptionPane = (JideOptionPane) optionPane;
        Object details = jideOptionPane.getDetails();
        if (details instanceof Container) {
            _detailsPreferredWidth = ((Container) details).getPreferredSize().width;
            return (Container) details;
        }
        else if (details instanceof Component) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add((Component) details);
            _detailsPreferredWidth = panel.getPreferredSize().width;
            return panel;
        }
        else if (details instanceof String) {
            JTextArea area = new JTextArea((String) details);
            area.setEditable(false);
            area.setRows(20);
            area.setColumns(60);
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(area));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 6, 10, 6));
            _detailsPreferredWidth = panel.getPreferredSize().width;
            return panel;
        }
        else {
            return null;
        }
    }

    @Override
    protected Container createButtonArea() {
        int orientation = UIDefaultsLookup.getInt("OptionPane.buttonOrientation");
        orientation = orientation == 0 ? SwingConstants.CENTER : orientation;
        ButtonPanel buttonPanel = new ButtonPanel(orientation);
        Border border = (Border) UIDefaultsLookup.get("OptionPane.buttonAreaBorder");
        buttonPanel.setName("OptionPane.buttonArea");
        if (border != null) {
            buttonPanel.setBorder(border);
        }
        boolean sameSize = UIDefaultsLookup.getBoolean("OptionPane.sameSizeButtons");
        buttonPanel.setSizeConstraint(sameSize ? ButtonPanel.SAME_SIZE : ButtonPanel.NO_LESS_THAN);
        int padding = UIDefaultsLookup.getInt("OptionPane.buttonPadding");
        padding = padding == 0 ? 6 : padding;
        buttonPanel.setButtonGap(padding);
        addButtonComponents(buttonPanel, getButtons(), getInitialValueIndex());
        return buttonPanel;
    }

    @Override
    protected void addButtonComponents(Container container, Object[] buttons,
                                       int initialIndex) {
        if (buttons != null && buttons.length > 0) {
            int numButtons = buttons.length;
            for (int counter = 0; counter < numButtons; counter++) {
                Object button = buttons[counter];
                Component newComponent;

                if (button instanceof Component) {
                    newComponent = (Component) button;
                    container.add(newComponent, ButtonPanel.OTHER_BUTTON);
                    hasCustomComponents = true;
                }
                else {
                    JButton aButton;

                    if (button instanceof ButtonFactory) {
                        aButton = ((ButtonFactory) button).createButton();
                    }
                    else if (button instanceof Icon)
                        aButton = new JButton((Icon) button);
                    else
                        aButton = new JButton(button.toString());

                    aButton.setMultiClickThreshhold(UIDefaultsLookup.getInt("OptionPane.buttonClickThreshhold"));
                    configureButton(aButton);

                    if (ButtonNames.YES.equals(aButton.getName())
                            || ButtonNames.NO.equals(aButton.getName())
                            || ButtonNames.OK.equals(aButton.getName())
                            || ButtonNames.CLOSE.equals(aButton.getName())
                            || ButtonNames.FINISH.equals(aButton.getName())) {
                        container.add(aButton, ButtonPanel.AFFIRMATIVE_BUTTON);
                    }
                    else if (ButtonNames.CANCEL.equals(aButton.getName())) {
                        container.add(aButton, ButtonPanel.CANCEL_BUTTON);
                    }
                    else if (ButtonNames.HELP.equals(aButton.getName())) {
                        container.add(aButton, ButtonPanel.HELP_BUTTON);
                    }
                    else {
                        container.add(aButton, ButtonPanel.OTHER_BUTTON);
                    }

                    if (ButtonNames.DETAILS.equals(aButton.getName())) {
                        aButton.addActionListener(new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                                JButton defaultButton = (JButton) e.getSource();
                                Container top = defaultButton.getTopLevelAncestor();
                                final ResourceBundle resourceBundle = ButtonResources.getResourceBundle(optionPane.getLocale());
                                if (_detailsArea.isVisible()) {
                                    setDetailsVisible(false);
                                    _detailsArea.setVisible(false);
                                    defaultButton.setText(resourceBundle.getString("Button.showDetails"));
                                    defaultButton.setMnemonic(resourceBundle.getString("Button.showDetails.mnemonic").charAt(0));
                                }
                                else {
                                    setDetailsVisible(true);
                                    _detailsArea.setVisible(true);
                                    defaultButton.setText(resourceBundle.getString("Button.hideDetails"));
                                    defaultButton.setMnemonic(resourceBundle.getString("Button.hideDetails.mnemonic").charAt(0));
                                }
                                if (top instanceof Window) {
                                    ((Window) top).pack();
                                }
                            }
                        });
                        aButton.setVisible(shouldDetailsButtonVisible());
                    }
                    else {
                        ActionListener buttonListener = createButtonActionListener(counter);
                        if (buttonListener != null) {
                            aButton.addActionListener(buttonListener);
                        }
                    }
                    newComponent = aButton;
                }

                if (counter == initialIndex) {
                    initialFocusComponent = newComponent;
                    if (initialFocusComponent instanceof JButton) {
                        JButton defaultB = (JButton) initialFocusComponent;
                        defaultB.addAncestorListener(new AncestorListener() {
                            public void ancestorAdded(AncestorEvent e) {
                                JButton defaultButton = (JButton) e.getComponent();
                                JRootPane root = SwingUtilities.getRootPane(defaultButton);
                                if (root != null) {
                                    root.setDefaultButton(defaultButton);
                                }
                            }

                            public void ancestorRemoved(AncestorEvent event) {
                            }

                            public void ancestorMoved(AncestorEvent event) {
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * Returns the buttons to display from the JOptionPane the receiver is providing the look and feel for. If the
     * JOptionPane has options set, they will be provided, otherwise if the optionType is YES_NO_OPTION, yesNoOptions is
     * returned, if the type is YES_NO_CANCEL_OPTION yesNoCancelOptions is returned, otherwise defaultButtons are
     * returned.
     */
    @Override
    protected Object[] getButtons() {
        if (optionPane != null) {
            Object[] suppliedOptions = optionPane.getOptions();

            if (suppliedOptions == null) {
                Object[] defaultOptions;
                int type = optionPane.getOptionType();
                Locale l = optionPane.getLocale();
                if (type == JOptionPane.YES_NO_OPTION) {
                    defaultOptions = new ButtonFactory[2];
                    defaultOptions[0] = new ButtonFactory(
                            ButtonNames.YES,
                            UIDefaultsLookup.getString("OptionPane.yesButtonText", l),
                            getMnemonic("OptionPane.yesButtonMnemonic", l),
                            (Icon) UIDefaultsLookup.get("OptionPane.yesIcon"));
                    defaultOptions[1] = new ButtonFactory(
                            ButtonNames.NO,
                            UIDefaultsLookup.getString("OptionPane.noButtonText", l),
                            getMnemonic("OptionPane.noButtonMnemonic", l),
                            (Icon) UIDefaultsLookup.get("OptionPane.noIcon"));
                }
                else if (type == JOptionPane.YES_NO_CANCEL_OPTION) {
                    defaultOptions = new ButtonFactory[3];
                    defaultOptions[0] = new ButtonFactory(
                            ButtonNames.YES,
                            UIDefaultsLookup.getString("OptionPane.yesButtonText", l),
                            getMnemonic("OptionPane.yesButtonMnemonic", l),
                            (Icon) UIDefaultsLookup.get("OptionPane.yesIcon"));
                    defaultOptions[1] = new ButtonFactory(
                            ButtonNames.NO,
                            UIDefaultsLookup.getString("OptionPane.noButtonText", l),
                            getMnemonic("OptionPane.noButtonMnemonic", l),
                            (Icon) UIDefaultsLookup.get("OptionPane.noIcon"));
                    defaultOptions[2] = new ButtonFactory(
                            ButtonNames.CANCEL,
                            UIDefaultsLookup.getString("OptionPane.cancelButtonText", l),
                            getMnemonic("OptionPane.cancelButtonMnemonic", l),
                            (Icon) UIDefaultsLookup.get("OptionPane.cancelIcon"));
                }
                else if (type == JOptionPane.OK_CANCEL_OPTION) {
                    defaultOptions = new ButtonFactory[2];
                    defaultOptions[0] = new ButtonFactory(
                            ButtonNames.OK,
                            UIDefaultsLookup.getString("OptionPane.okButtonText", l),
                            getMnemonic("OptionPane.okButtonMnemonic", l),
                            (Icon) UIDefaultsLookup.get("OptionPane.okIcon"));
                    defaultOptions[1] = new ButtonFactory(
                            ButtonNames.CANCEL,
                            UIDefaultsLookup.getString("OptionPane.cancelButtonText", l),
                            getMnemonic("OptionPane.cancelButtonMnemonic", l),
                            (Icon) UIDefaultsLookup.get("OptionPane.cancelIcon"));
                }
                else if (type == JideOptionPane.CLOSE_OPTION) {
                    defaultOptions = new ButtonFactory[1];
                    final ResourceBundle resourceBundle = ButtonResources.getResourceBundle(optionPane.getLocale());
                    defaultOptions[0] = new ButtonFactory(
                            ButtonNames.CLOSE,
                            resourceBundle.getString("Button.close"),
                            resourceBundle.getString("Button.close.mnemonic").charAt(0),
                            null);
                }
                else {
                    defaultOptions = new ButtonFactory[1];
                    defaultOptions[0] = new ButtonFactory(
                            ButtonNames.OK,
                            UIDefaultsLookup.getString("OptionPane.okButtonText", l),
                            getMnemonic("OptionPane.okButtonMnemonic", l),
                            (Icon) UIDefaultsLookup.get("OptionPane.okIcon"));
                }

                return addDetailsButton(defaultOptions, true);

            }
            return addDetailsButton(suppliedOptions, true);
        }
        return null;
    }

    protected Object[] addDetailsButton(Object[] options, boolean showDetails) {
        if (showDetails) {
            Object[] newOptions = new Object[options.length + 1];
            for (int i = 0; i < options.length; i++) {
                newOptions[i] = options[i];
            }
            final ResourceBundle resourceBundle = ButtonResources.getResourceBundle(optionPane.getLocale());
            newOptions[newOptions.length - 1] = new ButtonFactory(
                    ButtonNames.DETAILS,
                    resourceBundle.getString("Button.showDetails"),
                    resourceBundle.getString("Button.showDetails.mnemonic").charAt(0), null);
            return newOptions;
        }
        else {
            return options;
        }

    }

    private boolean shouldDetailsButtonVisible() {
        return optionPane instanceof JideOptionPane && ((JideOptionPane) optionPane).getDetails() != null;
    }

    /**
     * Configures any necessary colors/fonts for the specified button used representing the button portion of the
     * OptionPane.
     */
    protected void configureButton(JButton button) {
        Font buttonFont = (Font) UIDefaultsLookup.get("OptionPane.buttonFont");
        if (buttonFont != null) {
            button.setFont(buttonFont);
        }
    }

    protected int getMnemonic(String key, Locale l) {
        String value = (String) UIDefaultsLookup.get(key, l);

        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException nfe) {
        }
        return 0;
    }

    /**
     * This class is used to create the default buttons. This indirection is used so that addButtonComponents can tell
     * which Buttons were created by us vs subclasses or from the JOptionPane itself.
     */
    protected static class ButtonFactory {
        private String name;
        private String text;
        private int mnemonic;
        private Icon icon;

        ButtonFactory(String name, String text, int mnemonic, Icon icon) {
            this.name = name;
            this.text = text;
            this.mnemonic = mnemonic;
            this.icon = icon;
        }

        JButton createButton() {
            JButton button = new JButton(text);
            if (name != null) {
                button.setName(name);
            }
            if (icon != null) {
                button.setIcon(icon);
            }
            if (mnemonic != 0) {
                button.setMnemonic(mnemonic);
            }
            return button;
        }
    }

    @Override
    protected void addIcon(Container top) {
        Icon sideIcon = getIcon();
        if (sideIcon != null) {
            JLabel iconLabel = new JLabel(sideIcon);
            iconLabel.setName("OptionPane.iconLabel");
            top.add(iconLabel, BorderLayout.BEFORE_LINE_BEGINS);
        }
    }

    protected Container createBannerArea() {
        PaintPanel bannerPanel = new PaintPanel() {
            @Override
            public Dimension getPreferredSize() {
                Dimension preferredSize = super.getPreferredSize();
                if (preferredSize.width < _detailsPreferredWidth) {
                    preferredSize.width = _detailsPreferredWidth;
                }
                return preferredSize;
            }
        };
        customizeBannerArea(bannerPanel);
        bannerPanel.setLayout(new BorderLayout(10, 10));
        addIcon(bannerPanel);
        updateTitleComponent(bannerPanel);
        bannerPanel.add(new JLabel(UIDefaultsLookup.getIcon("OptionPane.bannerIcon")), BorderLayout.AFTER_LINE_ENDS);
        return bannerPanel;
    }

    protected void customizeBannerArea(PaintPanel bannerPanel) {
        Paint paint = (Paint) UIDefaultsLookup.get("OptionPane.bannerBackgroundPaint");

        if (paint != null) {
            bannerPanel.setBackgroundPaint(paint);
        }
        else {
            Color dk = UIDefaultsLookup.getColor("OptionPane.bannerBackgroundDk");
            Color lt = UIDefaultsLookup.getColor("OptionPane.bannerBackgroundLt");
            boolean direction = UIDefaultsLookup.get("OptionPane.bannerBackgroundDirection") == null
                    || UIDefaultsLookup.getBoolean("OptionPane.bannerBackgroundDirection");
            if (dk == null && lt != null) {
                dk = lt;
                bannerPanel.setGradientPaint(
                        dk != null ? dk : getPainter().getOptionPaneBannerLt(),
                        lt != null ? lt : getPainter().getOptionPaneBannerDk(),
                        direction);
            }
            else if (dk != null && lt == null) {
                lt = dk;
                bannerPanel.setGradientPaint(
                        dk != null ? dk : getPainter().getOptionPaneBannerLt(),
                        lt != null ? lt : getPainter().getOptionPaneBannerDk(),
                        direction);
            }
            else if (dk != null && lt != null) {
                bannerPanel.setGradientPaint(
                        dk != null ? dk : getPainter().getOptionPaneBannerLt(),
                        lt != null ? lt : getPainter().getOptionPaneBannerDk(),
                        direction);
            }
        }

        Border border = UIDefaultsLookup.getBorder("OptionPane.bannerBorder");
        bannerPanel.setBorder(border != null ? border : BorderFactory.createEmptyBorder(0, 10, 0, 0));
    }

    private void updateTitleComponent(Container bannerArea) {
        if (bannerArea == null) {
            return;
        }

        if (_titleComponent != null) {
            bannerArea.remove(_titleComponent);
            _titleComponent = null;
        }

        Object title = optionPane instanceof JideOptionPane ? ((JideOptionPane) optionPane).getTitle() : null;
        if (title instanceof String) {
            if (((String) title).startsWith("<html>") || ((String) title).startsWith("<HTML>")) {
                JLabel titleLabel = new JLabel(((String) title));
                _titleComponent = titleLabel;
            }
            else {
                String[] titles = fitInWidth((String) title, UIDefaultsLookup.getInt("OptionPane.bannerMaxCharsPerLine"));
                JPanel titlePanel = new NullPanel();
                titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
                titlePanel.setOpaque(false);
                titlePanel.add(Box.createGlue());
                for (String s : titles) {
                    JLabel label = new JLabel(s);
                    label.setFont(label.getFont().deriveFont(UIDefaultsLookup.getInt("OptionPane.bannerFontStyle"), UIDefaultsLookup.getInt("OptionPane.bannerFontSize")));
                    Color color = UIDefaultsLookup.getColor("OptionPane.bannerForeground");
                    label.setForeground(color != null ? color : getPainter().getOptionPaneBannerForeground());
                    titlePanel.add(label);
                }
                titlePanel.add(Box.createGlue());
                _titleComponent = titlePanel;
            }
        }
        else if (title instanceof Component) {
            _titleComponent = (Component) title;
        }
        if (_titleComponent != null) {
            bannerArea.add(_titleComponent, BorderLayout.CENTER);
        }
    }

    // static methods to split a string into multiple lines.

    /**
     * 1. Do not break a word/number. If the character is letter/digit, break at the most recent non- one; 2. Expand
     * "\n" to a blank line 3. Expand "\t" to four " " 4. Trim leading empty spaces
     *
     * @param str
     * @param width
     * @return An array of Strings with length of "width"
     */
    private static String[] fitInWidth(String str, int width) {
        if (str == null) str = "";

        String BLANK_STR = blankString(width, (byte) 32);
        str = replaceOccurrences(str, "\n", BLANK_STR);
        str = replaceOccurrences(str, "\t", "    ");

        ArrayList strArray = new ArrayList();
        str = str.trim();
        while (str.length() > width) {
            int breakPos = width;
            if (Character.isLetterOrDigit(str.charAt(width))) {
                breakPos--;
                char breakChar = str.charAt(breakPos);
                while (Character.isLetterOrDigit(breakChar) && breakPos > 0) {
                    breakPos--;
                    breakChar = str.charAt(breakPos);
                }
                if (breakPos == 0 && Character.isLetterOrDigit(breakChar)) {
                    breakPos = width;
                }
                else {
                    breakPos++;
                }
            }
            String subStr = str.substring(0, breakPos);
            if (subStr.length() < width) {
                subStr = subStr + blankString(width - subStr.length(), (byte) 32);
            }
            strArray.add(subStr);
            str = str.substring(breakPos).trim();
        }
        if (str.length() < width) {
            str = str + blankString(width - str.length(), (byte) 32);
        }
        strArray.add(str);
        return (String[]) strArray.toArray(new String[1]);
    }

    private static String blankString(int width, byte b) {
        byte[] bytes = new byte[width];
        Arrays.fill(bytes, b);
        return new String(bytes);
    }

    /**
     * Replaces all occurrences of <code>target</code> in <code>string</code> with <code>dest</code>.
     *
     * @param string string in which to target occurrences
     * @param target string to target
     * @param dest   replacement string
     * @return <code>string</code> with <code>dest</code> substituted for <code>target</code>
     */
    private static String replaceOccurrences(String string, String target, String dest) {
        StringBuffer b = new StringBuffer(string);
        int lastIndex = 0;
        while (true) {
            int index = indexOf(b, target, lastIndex);
            if (index < 0) {
                break;
            }
            b.replace(index, index + target.length(), dest);
            lastIndex = index + dest.length();
        }
        return b.toString();
    }

    /**
     * to do searches in character arrays. The source is the character array being searched, and the target is the
     * string being searched for.
     *
     * @param source       the characters being searched.
     * @param sourceOffset offset of the source string.
     * @param sourceCount  count of the source string.
     * @param target       the characters being searched for.
     * @param targetOffset offset of the target string.
     * @param targetCount  count of the target string.
     * @param fromIndex    the index to begin searching from.
     * @return If the string argument occurs as a substring within this object, then the index of the first character of
     *         the first such substring is returned; if it does not occur as a substring, -1 is returned.
     */
    private static int indexOf(char[] source, int sourceOffset, int sourceCount,
                               char[] target, int targetOffset, int targetCount,
                               int fromIndex) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        char first = target[targetOffset];
        int pos = sourceOffset + fromIndex;
        int max = sourceOffset + (sourceCount - targetCount);

        searchForNextChar:
        while (true) {
            // find first character

            // not first char increment position
            while (pos <= max && source[pos] != first) {
                pos++;
            }

            // at end of char buffer didn't find
            if (pos > max) {
                return -1;
            }

            // found first character - see if the rest of matches up
            int secondPos = pos + 1;
            int end = secondPos + targetCount - 1;
            int targetPos = targetOffset + 1;
            while (secondPos < end) {
                if (source[secondPos++] != target[targetPos++]) {
                    pos++;
                    // string doesn't match exit out of this loop and go back and
                    // continue looking at the next char
                    continue searchForNextChar;
                }
            }
            // Found an entire whole string.
            return pos - sourceOffset;
        }
    }

    /**
     * Returns the index within this string of the first occurrence of the specified substring, starting at the
     * specified index.  If the string argument occurs as a substring within this object, then the index of the first
     * character of the first such substring is returned; if it does not occur as a substring, -1 is returned.  This
     * replaces the functionality for java 1.4 StringBuffer.indexOf.
     *
     * @param buf       - buffer to search for str
     * @param findStr   - string to located
     * @param fromIndex - index to search
     * @return If the string argument occurs as a substring within this object, then the index of the first character of
     *         the first such substring is returned; if it does not occur as a substring, -1 is returned.
     */
    private static int indexOf(StringBuffer buf, String findStr, int fromIndex) {
        // function in Java's StringBuffer version 1.4 is synchronized
        // get character buffer from string buffer
        synchronized (buf) {
            int bufLen = buf.length();
            char[] charArray = new char[bufLen];
            buf.getChars(0, bufLen, charArray, 0);

            // send into char indexOf function
            return indexOf(charArray, 0, bufLen,
                    findStr.toCharArray(), 0, findStr.length(), fromIndex);
        }
    }

    public ThemePainter getPainter() {
        return _painter;
    }
}
