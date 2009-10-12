/*
 * @(#)FirefoxSearchBar.java 10/11/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.event.SearchableEvent;
import com.jidesoft.swing.event.SearchableListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

/**
 * <code>SearchableBar</code> is a convenient component to enable searching feature for components. As long as the
 * component support <code>Searchable</code> feature, it can work with <code>SearchableBar</code>.
 * <p/>
 * Different from <code>Searchable</code> feature which uses a small popup window to allow user typing in the searching
 * text, <code>SearchableBar</code> provides a full-size panel. Although they both pretty provide the same set of
 * features, they should be used in different cases to achieve the most desirable result.
 * <p/>
 * First of all, <code>SearchableBar</code> is a lot bigger than <code>Searchable</code>'s popup and need more space on
 * the screen. The component that installs <code>SearchableBar</code> should be large enough. In comparison,
 * <code>Searchable</code> can be installed on components of any size as it's a floating popup.
 * <p/>
 * Secondly, <code>SearchableBar</code> can be set visible all the time or can be set visible by a keystroke and stay
 * visible unless user explicitly hides it. If your user is not computer savvy, <code>SearchableBar</code> is more
 * appropriate because user can see searching feature very easily. <code>SearchableBar</code> can also be a better
 * replacement the traditional "Find" or "Search" dialog because <code>SearchableBar</code> doesn't block user input
 * like modal dialog. In comparison, <code>Searchable</code>'s popup is very transient. Mouse clicks outside the popup
 * will hide the popup. For computer savvy it is very helpful but it could be hard for non-computer savvy to
 * "understand" it. A good example is IntelliJ IDEA heavily uses Searchable popup because the users are all Java
 * developers. Firefox, on the other hand, uses SearchableBar because the users are just regular computer users.
 * <p/>
 * Although appearance wise, these two are very different, they both based on {@link Searchable} interface. So as
 * developer, both are almost the same. <code>SearchableBar</code> based on <code>Searchable</code>. So if you have an
 * interface of <code>Searchable</code>, all you need is to call
 * <code><pre>
 * SearchableBar.install(searchable, KeyStroke.getKeyStroke(KeyEvent.VK_F,
 * KeyEvent.CTRL_DOWN_MASK),
 * new SearchableBar.Installer() {
 *     public void openSearchBar(SearchableBar searchableBar) {
 *        // add code to show search bar
 *     }
 * <p/>
 *     public void closeSearchBar(SearchableBar searchableBar) {
 *        // add code to close search bar
 * }
 * });
 * </pre></code>
 * Or if you want fully control the SearchableBar, you can create one using one of its constructors and add to wherever
 * you want.
 * <p/>
 * There are a few options you can set on <code>SearchableBar</code>. You can set compact or full mode. Compact mode
 * will only use icon for buttons v.s. full mode will use both icon and text for buttons. All buttons on the
 * <code>SearchableBar</code> can be shown/hidden by using {@link #setVisibleButtons(int)} method. You can also set the
 * text field background for mismatch by using {@link #setMismatchForeground(java.awt.Color)}.
 * <p/>
 */
public class SearchableBar extends JToolBar implements SearchableProvider {
    private Searchable _searchable;

    private JLabel _statusLabel;
    private JTextField _textField;

    protected AbstractButton _closeButton;
    protected AbstractButton _findPrevButton;
    protected AbstractButton _findNextButton;
    protected AbstractButton _highlightsButton;

    private AbstractButton _matchCaseCheckBox;
    private AbstractButton _repeatCheckBox;

    public static final int SHOW_CLOSE = 0x1;
    public static final int SHOW_NAVIGATION = 0x2;
    public static final int SHOW_HIGHLIGHTS = 0x4;
    public static final int SHOW_MATCHCASE = 0x8;
    public static final int SHOW_REPEATS = 0x10;
    public static final int SHOW_STATUS = 0x20;
    public static final int SHOW_ALL = 0xFFFFFFFF;

    private int _visibleButtons = ~SHOW_REPEATS; // default is show all but repeats
    private boolean _compact;

    private JidePopup _messagePopup;
    private MouseMotionListener _mouseMotionListener;
    private KeyListener _keyListener;

    /**
     * Creates a searchable bar.
     *
     * @param searchable the searchable
     */
    public SearchableBar(Searchable searchable) {
        this(searchable, "", false);
    }

    /**
     * Creates a searchable bar in compact mode or full mode.
     *
     * @param searchable the searchable
     * @param compact the flag indicating compact mode or full mode
     */
    public SearchableBar(Searchable searchable, boolean compact) {
        this(searchable, "", compact);
    }

    /**
     * Creates a searchable bar with initial searching text and in compact mode or full mode.
     *
     * @param searchable the searchable
     * @param initialText the initial text
     * @param compact the flag indicating compact mode or full mode
     */
    public SearchableBar(Searchable searchable, String initialText, boolean compact) {
        setFloatable(false);
        setRollover(true);
        _searchable = searchable;
        _searchable.addSearchableListener(new SearchableListener() {
            public void searchableEventFired(SearchableEvent e) {
                if (e.getID() == SearchableEvent.SEARCHABLE_MODEL_CHANGE && _searchable.getSearchingText() != null && _searchable.getSearchingText().length() != 0) {
                    highlightAllOrNext();
                }
            }
        });
        _searchable.setSearchableProvider(this);
        _compact = compact;
        initComponents(initialText);
    }

    private void initComponents(String initialText) {
        AbstractAction closeAction = new AbstractAction() {
            private static final long serialVersionUID = -2245391247321137224L;

            public void actionPerformed(ActionEvent e) {
                if (getInstaller() != null) {
                    getInstaller().closeSearchBar(SearchableBar.this);
                }
            }
        };

        AbstractAction findNextAction = new AbstractAction() {
            private static final long serialVersionUID = -5263488798121831276L;

            public void actionPerformed(ActionEvent e) {
                _highlightsButton.setSelected(false);
                String text = getSearchingText();
                int cursor = _searchable.getSelectedIndex();
                _searchable.setCursor(cursor);
                int found = _searchable.findNext(text);
                if (found != -1 && _searchable.isRepeats() && found <= cursor) {
                    select(found, text, false);
                    setStatus(getResourceString("SearchableBar.reachedBottomRepeat"), getImageIcon(SearchableBarIconsFactory.Buttons.REPEAT));
                }
                else if (!_searchable.isRepeats() && found == -1) {
                    setStatus(getResourceString("SearchableBar.reachedBottom"), getImageIcon(SearchableBarIconsFactory.Buttons.ERROR));
                }
                else if (found != -1) {
                    select(found, text, false);
                    clearStatus();
                }
            }
        };

        AbstractAction findPrevAction = new AbstractAction() {
            private static final long serialVersionUID = -2534332227053620232L;

            public void actionPerformed(ActionEvent e) {
                _highlightsButton.setSelected(false);
                String text = getSearchingText();
                int cursor = _searchable.getSelectedIndex();
                _searchable.setCursor(cursor);
                int found = _searchable.findPrevious(text);
                if (found != -1 && _searchable.isRepeats() && found >= cursor) {
                    select(found, text, false);
                    setStatus(getResourceString("SearchableBar.reachedTopRepeat"), getImageIcon(SearchableBarIconsFactory.Buttons.REPEAT));
                }
                else if (!_searchable.isRepeats() && found == -1) {
                    setStatus(getResourceString("SearchableBar.reachedTop"), getImageIcon(SearchableBarIconsFactory.Buttons.ERROR));
                }
                else if (found != -1) {
                    select(found, text, false);
                    clearStatus();
                }
            }
        };

        _mouseMotionListener = new MouseMotionListener() {
            public void mouseMoved(MouseEvent e) {
                hideMessage();
            }

            public void mouseDragged(MouseEvent e) {

            }
        };
        _keyListener = new KeyListener() {
            public void keyTyped(KeyEvent e) {
                hideMessage();
            }

            public void keyPressed(KeyEvent e) {

            }

            public void keyReleased(KeyEvent e) {

            }
        };

        _closeButton = createCloseButton(closeAction);
        _findNextButton = createFindNextButton(findNextAction);
        _findPrevButton = createFindPrevButton(findPrevAction);
        _highlightsButton = createHighlightButton();
        _matchCaseCheckBox = createMatchCaseButton();
        _repeatCheckBox = createRepeatsButton();

        _statusLabel = new JLabel();

        //setup text field
        _textField = createTextField();
        _textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                _textField.selectAll();
            }
        });
        _textField.setColumns(13);
        _textField.getDocument().addDocumentListener(new DocumentListener() {
            private Timer timer = new Timer(_searchable.getSearchingDelay(), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    highlightAllOrNext();
                }
            });

            public void insertUpdate(DocumentEvent e) {
                startTimer();
            }

            public void removeUpdate(DocumentEvent e) {
                startTimer();
            }

            public void changedUpdate(DocumentEvent e) {
                startTimer();
            }

            void startTimer() {
                if (_searchable.getSearchingDelay() > 0) {
                    if (timer.isRunning()) {
                        timer.restart();
                    }
                    else {
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
                else {
                    highlightAllOrNext();
                }
            }
        });
        _textField.setText(initialText);

        _textField.registerKeyboardAction(findNextAction, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_FOCUSED);
        _textField.registerKeyboardAction(findNextAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
        _textField.registerKeyboardAction(findPrevAction, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), JComponent.WHEN_FOCUSED);
        _textField.registerKeyboardAction(closeAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED);

        installComponents();

        int found = _searchable.findFromCursor(getSearchingText());
        if (initialText.length() != 0 && found == -1) {
            select(found, initialText, false);
        }
    }

    /**
     * Creates the text field where user types the text to be searched.
     *
     * @return a text field.
     */
    protected JTextField createTextField() {
        return new JTextField();
    }

    /**
     * Gets the underlying Searchable object.
     *
     * @return the Searchable object.
     */
    public Searchable getSearchable() {
        return _searchable;
    }

    /**
     * Creates the close button. Subclass can override it to create your own close button.
     *
     * @param closeAction the close action
     * @return the close button.
     */
    protected AbstractButton createCloseButton(AbstractAction closeAction) {
        AbstractButton button = new JButton(getImageIcon(SearchableBarIconsFactory.Buttons.CLOSE));
        button.addActionListener(closeAction);
        button.setRolloverEnabled(true);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setOpaque(false);
        button.setRequestFocusEnabled(false);
        button.setFocusable(false);
        button.setRolloverIcon(getImageIcon(SearchableBarIconsFactory.Buttons.CLOSE_ROLLOVER));
        return button;
    }

    /**
     * Creates the find next button. Subclass can override it to create your own find next button.
     *
     * @param findNextAction the find next action
     * @return the find next button.
     */
    protected AbstractButton createFindNextButton(AbstractAction findNextAction) {
        AbstractButton button = new JButton(_compact ? "" : getResourceString("SearchableBar.findNext"),
                getImageIcon(SearchableBarIconsFactory.Buttons.NEXT));
        button.setToolTipText(getResourceString("SearchableBar.findNext.tooltip"));
        button.setMnemonic(getResourceString("SearchableBar.findNext.mnemonic").charAt(0));
        button.setRolloverIcon(getImageIcon(SearchableBarIconsFactory.Buttons.NEXT_ROLLOVER));
        button.setDisabledIcon(getImageIcon(SearchableBarIconsFactory.Buttons.NEXT_DISABLED));
        button.setRequestFocusEnabled(false);
        button.setFocusable(false);
        button.addActionListener(findNextAction);
        button.setEnabled(false);
        return button;
    }

    /**
     * Creates the find prev button. Subclass can override it to create your own find prev button.
     *
     * @param findPrevAction the find previous action
     * @return the find prev button.
     */
    protected AbstractButton createFindPrevButton(AbstractAction findPrevAction) {
        AbstractButton button = new JButton(_compact ? "" : getResourceString("SearchableBar.findPrevious"),
                getImageIcon(SearchableBarIconsFactory.Buttons.PREVIOUS));
        button.setToolTipText(getResourceString("SearchableBar.findPrevious.tooltip"));
        button.setMnemonic(getResourceString("SearchableBar.findPrevious.mnemonic").charAt(0));
        button.setRolloverIcon(getImageIcon(SearchableBarIconsFactory.Buttons.PREVIOUS_ROLLOVER));
        button.setDisabledIcon(getImageIcon(SearchableBarIconsFactory.Buttons.PREVIOUS_DISABLED));
        button.setRequestFocusEnabled(false);
        button.setFocusable(false);
        button.addActionListener(findPrevAction);
        button.setEnabled(false);
        return button;
    }

    /**
     * Creates the highlight button.
     *
     * @return the highlight button.
     */
    protected AbstractButton createHighlightButton() {
        AbstractButton button = new JToggleButton(_compact ? "" : getResourceString("SearchableBar.highlights"),
                getImageIcon(SearchableBarIconsFactory.Buttons.HIGHLIGHTS));
        button.setToolTipText(getResourceString("SearchableBar.highlights.tooltip"));
        button.setMnemonic(getResourceString("SearchableBar.highlights.mnemonic").charAt(0));
        button.setSelectedIcon(getImageIcon(SearchableBarIconsFactory.Buttons.HIGHLIGHTS_SELECTED));
        button.setDisabledIcon(getImageIcon(SearchableBarIconsFactory.Buttons.HIGHLIGHTS_DISABLED));
        button.setRolloverIcon(getImageIcon(SearchableBarIconsFactory.Buttons.HIGHLIGHTS_ROLLOVER));
        button.setRolloverSelectedIcon(getImageIcon(SearchableBarIconsFactory.Buttons.HIGHLIGHTS_ROLLOVER_SELECTED));
        button.setRequestFocusEnabled(false);
        button.setFocusable(false);

        AbstractAction highlightAllAction = new AbstractAction() {
            private static final long serialVersionUID = 5170786863522331175L;

            public void actionPerformed(ActionEvent e) {
                highlightAllOrNext();
            }
        };

        button.addActionListener(highlightAllAction);
        button.setEnabled(false);
        return button;
    }

    /**
     * Creates the repeat button. By default it will return a JCheckBox. Subclass class can override it to return your
     * own button or customize the button created by default as long as it can set underlying Searchable's repeats
     * property.
     *
     * @return the repeat button.
     */
    protected AbstractButton createRepeatsButton() {
        AbstractButton button = new JCheckBox(getResourceString("SearchableBar.repeats"));
        button.setMnemonic(getResourceString("SearchableBar.repeats.mnemonic").charAt(0));
        button.setRequestFocusEnabled(false);
        button.setFocusable(false);
        button.setSelected(getSearchable().isRepeats());
        button.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getSource() instanceof AbstractButton) {
                    getSearchable().setRepeats(((AbstractButton) e.getSource()).isSelected());
                }
            }
        });
        return button;
    }

    /**
     * Creates the match case button. By default it will return a JCheckBox. Subclass class can override it to return
     * your own button or customize the button created by default as long as it can set underlying Searchable's
     * caseSensitive property.
     *
     * @return the match case button.
     */
    protected AbstractButton createMatchCaseButton() {
        JCheckBox checkBox = new JCheckBox(getResourceString("SearchableBar.matchCase"));
        checkBox.setMnemonic(getResourceString("SearchableBar.matchCase.mnemonic").charAt(0));
        checkBox.setRequestFocusEnabled(false);
        checkBox.setFocusable(false);
        checkBox.setSelected(getSearchable().isCaseSensitive());
        checkBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getSource() instanceof AbstractButton) {
                    getSearchable().setCaseSensitive(((AbstractButton) e.getSource()).isSelected());
                    highlightAllOrNext();
                }
            }
        });
        return checkBox;
    }

    /**
     * Adds the buttons to the SearchableBar. Subclass can override this method to rearrange the layout of those
     * buttons.
     */
    protected void installComponents() {
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(new JideBoxLayout(this, JideBoxLayout.X_AXIS));
        add(Box.createHorizontalStrut(4), JideBoxLayout.FIX);
        if ((_visibleButtons & SHOW_CLOSE) != 0) {
            add(_closeButton);
            add(Box.createHorizontalStrut(10));
        }
        // setup the label
        JLabel label = new JLabel(getResourceString("SearchableBar.find"));
        label.setDisplayedMnemonic(getResourceString("SearchableBar.find.mnemonic").charAt(0));
        label.setLabelFor(_textField);
        add(label);
        add(Box.createHorizontalStrut(2), JideBoxLayout.FIX);
        add(JideSwingUtilities.createCenterPanel(_textField), JideBoxLayout.FIX);
        add(Box.createHorizontalStrut(2), JideBoxLayout.FIX);
        if ((_visibleButtons & SHOW_NAVIGATION) != 0) {
            add(_findNextButton);
            add(_findPrevButton);
        }
        if ((_visibleButtons & SHOW_HIGHLIGHTS) != 0) {
            add(_highlightsButton);
        }
        if ((_visibleButtons & SHOW_MATCHCASE) != 0) {
            add(_matchCaseCheckBox);
        }
        if ((_visibleButtons & SHOW_REPEATS) != 0) {
            add(_repeatCheckBox);
        }
        if ((_visibleButtons & SHOW_STATUS) != 0) {
            add(Box.createHorizontalStrut(24));
            add(_statusLabel, JideBoxLayout.VARY);
        }
        add(Box.createHorizontalStrut(6), JideBoxLayout.FIX);
    }

    /**
     * Get if the SearchableBar is highlighting all matches.
     * <p/>
     * Even you set it to true, after the customer press previous or next button, this flag will be cleared.
     *
     * @return true if all matches are highlighted. Otherwise false.
     */
    public boolean isHighlightAll() {
        return _highlightsButton.isSelected();
    }

    /**
     * Set if the SearchableBar will highlight all matches.
     *
     * @see #isHighlightAll()  
     * @param highlightAll the flag
     */
    public void setHighlightAll(boolean highlightAll) {
        _highlightsButton.setSelected(highlightAll);
    }

    private void highlightAllOrNext() {
        if (_highlightsButton.isSelected()) {
            highlighAll();
        }
        else {
            highlightNext();
        }
    }

    private void highlighAll() {
        String text = getSearchingText();
        if (text == null || text.length() == 0) {
            _findNextButton.setEnabled(false);
            _findPrevButton.setEnabled(false);
            _highlightsButton.setEnabled(false);
            select(-1, "", false);
            clearStatus();
            return;
        }
        boolean old = _searchable.isRepeats();
        _searchable.setRepeats(false);
        int index = _searchable.findFirst(text);
        if (index != -1) {
            _searchable.setSelectedIndex(index, false); // clear side effect of ctrl-a will select all items
            _searchable.setCursor(index); // as setSelectedIndex is used directly, we have to manually set the cursor value.
            _findNextButton.setEnabled(true);
            _findPrevButton.setEnabled(true);
            _highlightsButton.setEnabled(true);
            clearStatus();
        }
        else {
            select(-1, text, false);
            _findNextButton.setEnabled(false);
            _findPrevButton.setEnabled(false);
            _highlightsButton.setEnabled(false);
            setStatus(getResourceString("SearchableBar.notFound"), getImageIcon(SearchableBarIconsFactory.Buttons.ERROR));
        }

        int firstIndex = -1;

        while (index != -1) {
            int newIndex = _searchable.findNext(text);
            if (index == newIndex) {
                index = -1;
            }
            else {
                index = newIndex;
            }
            if (index != -1) {
                if (firstIndex == -1) {
                    firstIndex = index;
                }
                select(index, text, true);
            }
        }
        // now select the first one
        if (firstIndex != -1) {
            select(firstIndex, text, true);
        }

        _searchable.setRepeats(old);
        _searchable.setCursor(0);
    }

    private void highlightNext() {
        String text = getSearchingText();
        if (text == null || text.length() == 0) {
            _findNextButton.setEnabled(false);
            _findPrevButton.setEnabled(false);
            _highlightsButton.setEnabled(false);
            select(-1, "", false);
            clearStatus();
            return;
        }
        int found = _searchable.findFromCursor(text);
        if (found == -1) {
            select(-1, "", false);
            _findNextButton.setEnabled(false);
            _findPrevButton.setEnabled(false);
            _highlightsButton.setEnabled(false);
            setStatus(getResourceString("SearchableBar.notFound"), getImageIcon(SearchableBarIconsFactory.Buttons.ERROR));
        }
        else {
            select(found, text, false);
            _findNextButton.setEnabled(true);
            _findPrevButton.setEnabled(true);
            _highlightsButton.setEnabled(true);
            clearStatus();
        }
    }

    private void clearStatus() {
        _statusLabel.setIcon(null);
        _statusLabel.setText("");
        _textField.setBackground(UIDefaultsLookup.getColor("TextField.background"));
        hideMessage();
    }

    private void setStatus(String message, Icon icon) {
        _statusLabel.setIcon(icon);
        _statusLabel.setText(message);
        _statusLabel.setToolTipText(message);
        if (!_statusLabel.isShowing() || _statusLabel.getWidth() < 25) {
            showMessage(message);
        }
    }

    /**
     * Makes the search field having focus.
     */
    public void focusSearchField() {
        if (_textField != null) {
            _textField.requestFocus();
        }
    }

    protected void select(int index, String searchingText, boolean incremental) {
        if (index != -1) {
            _searchable.setSelectedIndex(index, incremental);
            _searchable.setCursor(index, incremental);
            _textField.setBackground(UIDefaultsLookup.getColor("TextField.background"));
        }
        else {
            _searchable.setSelectedIndex(-1, false);
            _textField.setBackground(getMismatchBackground());
        }
        _searchable.firePropertyChangeEvent(searchingText);
        if (index != -1) {
            Object element = _searchable.getElementAt(index);
            _searchable.fireSearchableEvent(new SearchableEvent(_searchable, SearchableEvent.SEARCHABLE_MATCH, searchingText, element, _searchable.convertElementToString(element)));
        }
        else {
            _searchable.fireSearchableEvent(new SearchableEvent(_searchable, SearchableEvent.SEARCHABLE_NOMATCH, searchingText));
        }
    }

    /**
     * Gets the searching text.
     *
     * @return the searching text.
     */
    public String getSearchingText() {
        return _textField != null ? _textField.getText() : "";
    }

    /**
     * Sets the searching text.
     *
     * @param searchingText the new searching text.
     */
    public void setSearchingText(String searchingText) {
        if (_textField != null) {
            _textField.setText(searchingText);
        }
    }

    /**
     * Returns false.
     *
     * @return false.
     */
    public boolean isPassive() {
        return false;
    }

    final private static Color DEFAULT_MISMATCH_BACKGROUND = new Color(255, 85, 85);
    private Color _mismatchBackground;

    /**
     * Sets the background for mismatch.
     *
     * @param mismatchBackground the mismatch background
     */
    public void setMismatchForeground(Color mismatchBackground) {
        _mismatchBackground = mismatchBackground;
    }

    /**
     * Gets the background color when the searching text doesn't match with any of the elements in the component.
     *
     * @return the foreground color for mismatch. If you never call {@link #setMismatchForeground(java.awt.Color)}. red
     *         color will be used.
     */
    public Color getMismatchBackground() {
        if (_mismatchBackground == null) {
            return DEFAULT_MISMATCH_BACKGROUND;
        }
        else {
            return _mismatchBackground;
        }
    }

    private Installer _installer;

    /**
     * The installer for SearchableBar.
     */
    public interface Installer {
        /**
         * Called to show the SearchableBar so that user can see it.
         * <p/>
         * For example, if you want to add a SearchableBar to the south of a JTextArea, you should add JTextArea to the
         * CENTER of a BorderLayout panel. In this method, you add the SearchableBar to the SOUTH of the same
         * BorderLayout panel.
         *
         * @param searchableBar the searchable bar
         */
        public void openSearchBar(SearchableBar searchableBar);

        /**
         * Called to hide the SearchableBar.
         *
         * @param searchableBar the searchable bar
         */
        public void closeSearchBar(SearchableBar searchableBar);
    }

    public Installer getInstaller() {
        return _installer;
    }

    /**
     * Sets the installer. Installer is responsible for the installation and uninstallation of SearchableBar.
     *
     * @param installer the installer
     */
    public void setInstaller(Installer installer) {
        _installer = installer;
    }

    /**
     * Installs a SearchableBar on a component. This is just a convenient method for you, you can install it in your own
     * code. See below for the actual code we used in this method.
     * <p/>
     * <code><pre>
     * final SearchableBar searchableBar = new SearchableBar(searchable);
     * searchableBar.setInstaller(installer);
     * ((JComponent) searchable.getComponent()).registerKeyboardAction(new AbstractAction() {
     *     public void actionPerformed(ActionEvent e) {
     *         searchableBar.getInstaller().openSearchBar(searchableBar);
     *         searchableBar.focusSearchField();
     *     }
     * }, keyStroke, JComponent.WHEN_FOCUSED);
     * return searchableBar;
     * </pre></code>
     *
     * @param searchable the searchable
     * @param keyStroke the key stroke
     * @param installer the installer
     * @return the SearchableBar that is created.
     */
    public static SearchableBar install(Searchable searchable, KeyStroke keyStroke, Installer installer) {
        final SearchableBar searchableBar = new SearchableBar(searchable);
        searchableBar.setInstaller(installer);
        ((JComponent) searchable.getComponent()).registerKeyboardAction(new AbstractAction() {
            private static final long serialVersionUID = 8328919754409621715L;

            public void actionPerformed(ActionEvent e) {
                searchableBar.getInstaller().openSearchBar(searchableBar);
                searchableBar.focusSearchField();
            }
        }, keyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        return searchableBar;
    }

    @Override
    public void processKeyEvent(KeyEvent e) {
    }

    public int getVisibleButtons() {
        return _visibleButtons;
    }

    /**
     * Sets visible buttons on <code>SearchableBar</code>.
     *
     * @param visibleButtons bit-wise all of several constants. Valid constants are <ul> <li> {@link #SHOW_CLOSE} - the
     *                       close button <li> {@link #SHOW_NAVIGATION} - the find next and find previous buttons <li>
     *                       {@link #SHOW_HIGHLIGHTS} - highlights all button <li> {@link #SHOW_MATCHCASE} - match case
     *                       button <li> {@link #SHOW_REPEATS} - repeats button <li> {@link #SHOW_STATUS} - status area
     *                       <li> {@link #SHOW_ALL} - all buttons </ul> For example, if you want to show only close and
     *                       highlights all button, call <code>setVisibleButtons(SearchableBar.SHOW_CLOSE |
     *                       SearchableBar.SHOW_HIGHLIGHTS)</code>.
     */
    public void setVisibleButtons(int visibleButtons) {
        _visibleButtons = visibleButtons;
        removeAll();
        installComponents();
        revalidate();
        repaint();
    }

    /**
     * Checks if <code>SearchableBar</code> is in compact mode.
     *
     * @return true if in compact. Otherwise, false.
     */
    public boolean isCompact() {
        return _compact;
    }

    /**
     * Sets the <code>SearchableBar</code> to compact or full mode. In compact mode will only use icon for buttons v.s.
     * full mode will use both icon and text for buttons.
     *
     * @param compact the flag
     */
    public void setCompact(boolean compact) {
        _compact = compact;
        _findNextButton.setText(_compact ? "" : getResourceString("SearchableBar.findNext"));
        _highlightsButton.setText(_compact ? "" : getResourceString("SearchableBar.highlights"));
        _findPrevButton.setText(_compact ? "" : getResourceString("SearchableBar.findPrevious"));
    }

    /**
     * Gets the icons from SearchableBarIconsFactory. Subclass can override this method if they want to provide their
     * own icon.
     *
     * @param name the icon name
     * @return the icon of the specified name.
     */
    protected ImageIcon getImageIcon(String name) {
        return SearchableBarIconsFactory.getImageIcon(name);
    }

    /**
     * Gets the localized string from resource bundle. Subclass can override it to provide its own string. Available
     * keys are defined in swing.properties that begin with "SearchableBar.".
     *
     * @param key the resource key
     * @return the localized string.
     */
    protected String getResourceString(String key) {
        return Resource.getResourceBundle(Locale.getDefault()).getString(key);
    }

    private void showMessage(String message) {
        hideMessage();

        _messagePopup = com.jidesoft.popup.JidePopupFactory.getSharedInstance().createPopup();
        JLabel label = new JLabel(message);
        label.setOpaque(true);
        label.setFont(UIDefaultsLookup.getFont("Label.font").deriveFont(Font.BOLD, 11));
        label.setBackground(new Color(253, 254, 226));
        label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        label.setForeground(UIDefaultsLookup.getColor("ToolTip.foreground"));

        _messagePopup.getContentPane().setLayout(new BorderLayout());
        _messagePopup.getContentPane().add(label);
        _messagePopup.setOwner(_textField);

        _messagePopup.setDefaultMoveOperation(JidePopup.HIDE_ON_MOVED);
        _messagePopup.setTransient(true);
        _messagePopup.showPopup();

        addMouseMotionListener(_mouseMotionListener);
        _textField.addKeyListener(_keyListener);
    }

    private void hideMessage() {
        if (_messagePopup != null) {
            _messagePopup.hidePopupImmediately();
            _messagePopup = null;
        }
        if (_mouseMotionListener != null) {
            removeMouseMotionListener(_mouseMotionListener);
        }
        if (_keyListener != null) {
            _textField.removeKeyListener(_keyListener);
        }
    }
}
